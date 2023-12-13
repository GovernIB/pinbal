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

}
