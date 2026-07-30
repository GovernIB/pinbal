package es.scsp.common.dao;

import es.caib.pinbal.scsp.SecuenciaH2Helper;
import es.scsp.common.exceptions.ScspException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.jdbc.ReturningWork;
import org.springframework.stereotype.Component;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Override local (pinbal-scsp) de es.scsp.common.dao.SecuenciaIdTransmisionDao.
 *
 * Mateix problema i mateixa solucio que SecuenciaIdPeticionDao: la versio
 * original crida sempre el procediment emmagatzemat d'Oracle
 * GETSECUENCIAIDTRANSMISION, absent a H2. Sota H2 fa servir una taula
 * comptador propia (pbl_e2e_seq_id_transmisio, sembrada per Liquibase,
 * dbms: h2); contra Oracle el comportament es identic a l'original.
 *
 * La seguretat davant transaccions ja actives (igual que a
 * SecuenciaIdPeticionDao) la proporciona de forma transparent
 * NestedTransactionSessionFactory, aplicat a BaseDao.setSessionFactory():
 * aquest metode pot cridar session.beginTransaction()/commit()/rollback()
 * com si sempre fos ell qui obre la transaccio, sense comprovar-ne l'estat
 * previ.
 */
@Component
public class SecuenciaIdTransmisionDao extends BaseDao {

    private static final Log LOG = LogFactory.getLog(SecuenciaIdTransmisionDao.class);

    private static final String TAULA_H2 = "pbl_e2e_seq_id_transmisio";

    public String next(final String prefijo) throws ScspException {
        Session session = getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            String resultat = session.doReturningWork(new ReturningWork<String>() {
                @Override
                public String execute(Connection connection) throws SQLException {
                    if (SecuenciaH2Helper.esH2(connection)) {
                        return Integer.toString(SecuenciaH2Helper.seguentValor(connection, TAULA_H2, prefijo));
                    }
                    return nextOracle(connection, prefijo);
                }
            });
            tx.commit();
            return resultat;
        } catch (Exception ex) {
            LOG.error("Error generando idtransmision", ex);
            try {
                if (tx.isActive()) {
                    tx.rollback();
                }
            } catch (Exception rollbackEx) {
                LOG.error("Error fent rollback de la transaccio despres d'un error generant l'idtransmision", rollbackEx);
            }
            throw ScspException.getScspException(ex, "0201", null);
        }
    }

    private String nextOracle(Connection connection, String prefijo) throws SQLException {
        CallableStatement cs = connection.prepareCall("{ call GETSECUENCIAIDTRANSMISION(?,?)}");
        try {
            cs.setString(1, prefijo);
            cs.registerOutParameter(2, Types.INTEGER);
            cs.execute();
            return Integer.toString(cs.getInt(2));
        } finally {
            cs.close();
        }
    }
}
