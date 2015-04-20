/**
 * 
 */
package es.scsp.common.dao;

import java.sql.CallableStatement;
import java.sql.Connection;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import es.scsp.common.exceptions.ScspException;

/**
 * Dao per a esborrar registres de les taules SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component("secuenciaIdPeticionDao")
public class SecuenciaIdPeticionPinbalDao extends SecuenciaIdPeticionDao implements ApplicationContextAware {

	private ApplicationContext applicationContext;


	@Override
	public String next(String prefijo) throws ScspException {
		DataSource dataSource = (DataSource)applicationContext.getBean("dataSource");
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
		} catch (Exception e) {
			log.error(e);
			throw ScspException.getScspException(e, "0502", null);
		}
		CallableStatement cs = null;
		String result = "";
		try {
			cs = conn.prepareCall("{ call GETSECUENCIAIDPETICION(?,?)}");
			cs.setString(1, prefijo);
			cs.registerOutParameter(2, 4);
			cs.execute();
			result = Integer.toString(cs.getInt(2));
			conn.close();
		} catch (Exception e) {
			log.error(e);
			throw ScspException.getScspException(e, "0201", null);
		} finally {
			try {
				cs.close();
			} catch (Exception e) {
			}
			try {
				conn.close();
			} catch (Exception e) {
			}
		}
		return result;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	private static final Log log = LogFactory.getLog(SecuenciaIdPeticionDao.class);

}
