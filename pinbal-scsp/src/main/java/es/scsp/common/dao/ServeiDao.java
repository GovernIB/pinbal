/**
 * 
 */
package es.scsp.common.dao;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import static org.hibernate.type.StandardBasicTypes.BOOLEAN;

/**
 * Dao per a obtenir els paràmetres de la configuració de PINBAL amb
 * prioritat a damunt els de la taula SCSP CORE_PARAMETRO_CONFIGURACION.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ServeiDao extends BaseDao {

	private static final String SERVICE_REQUEST_SENT_SQL = "SELECT enviar_solicitant FROM pbl_servei_config WHERE servei_id = :serveiCodi";
	private static final String SERVICE_USE_CERT_SQL = "SELECT use_cert_entitat FROM pbl_servei_config WHERE servei_id = :serveiCodi";
	private static final String SERVICE_FORMAT_NS_SCSP4_SQL = "SELECT format_ns_scsp4 FROM pbl_servei_config WHERE servei_id = :serveiCodi";
	private static final String SERVICE_CODE_PARAM = "serveiCodi";

	public boolean serveiHasToSendSolicitant(String serveiCodi) {
		Session session = getSessionFactory().getCurrentSession();
		Transaction tx = session.beginTransaction();
		SQLQuery sqlQuery = session.createSQLQuery(SERVICE_REQUEST_SENT_SQL);
		sqlQuery.setParameter(SERVICE_CODE_PARAM, serveiCodi);
		sqlQuery.addScalar("enviar_solicitant", BOOLEAN);
		Object enviarSolicitant = sqlQuery.uniqueResult();
		tx.commit();
		return Boolean.TRUE.equals(enviarSolicitant);
	}

    public boolean serveiUseCertificatEntitat(String serveiCodi) {
        Session session = getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        SQLQuery sqlQuery = session.createSQLQuery(SERVICE_USE_CERT_SQL);
        sqlQuery.setParameter(SERVICE_CODE_PARAM, serveiCodi);
        sqlQuery.addScalar("use_cert_entitat", BOOLEAN);
        Object certificatPerEntitat = sqlQuery.uniqueResult();
        tx.commit();
        return Boolean.TRUE.equals(certificatPerEntitat);
    }

    /**
     * Indica si les peticions del servei s'han de generar amb el format de namespaces de SCSP 4
     * (vegeu es.caib.pinbal.scsp.binding.ScspJaxb2Marshaller).
     */
    public boolean serveiFormatNamespacesScsp4(String serveiCodi) {
        Session session = getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        SQLQuery sqlQuery = session.createSQLQuery(SERVICE_FORMAT_NS_SCSP4_SQL);
        sqlQuery.setParameter(SERVICE_CODE_PARAM, serveiCodi);
        sqlQuery.addScalar("format_ns_scsp4", BOOLEAN);
        Object formatScsp4 = sqlQuery.uniqueResult();
        tx.commit();
        return Boolean.TRUE.equals(formatScsp4);
    }

}
