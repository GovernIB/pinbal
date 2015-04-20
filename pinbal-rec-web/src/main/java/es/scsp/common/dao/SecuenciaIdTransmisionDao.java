package es.scsp.common.dao;

import java.sql.CallableStatement;
import java.sql.Connection;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.scsp.common.exceptions.ScspException;

@Component
public class SecuenciaIdTransmisionDao extends BaseDao implements Runnable {
	private static final Log log = LogFactory
			.getLog(SecuenciaIdTransmisionDao.class);

	@Autowired
	private DataSource datasource;

	public static void main(String[] args) {
		SecuenciaIdTransmisionDao sc = new SecuenciaIdTransmisionDao();
		for (int i = 0; i < 3000; i++) {
			Thread miHilo = new Thread(sc);
			miHilo.start();
			if ((i != 1000) && (i != 2000))
				continue;
			try {
				Thread.sleep(100L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void run() {
		try {
			SecuenciaIdTransmisionDao sc = new SecuenciaIdTransmisionDao();
			sc.next("ss");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String next(String prefijo) throws ScspException {
		Connection conn = null;
		CallableStatement cs = null;
		String result = "";
		try {
			conn = datasource.getConnection();
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
				if (conn != null)
					conn.close();
				if (cs != null)
					cs.close();
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
		return result;
	}
}