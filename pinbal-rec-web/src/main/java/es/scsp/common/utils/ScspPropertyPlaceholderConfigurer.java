package es.scsp.common.utils;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

public class ScspPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
	private static final Log log = LogFactory.getLog(ScspPropertyPlaceholderConfigurer.class);
	private Properties dataBaseProperties = new Properties();
	private String tableName;
	private String keyColumnName;
	private String valueColumnName;

	public void setDataBaseProperties(Properties dataBaseProperties) {
		this.dataBaseProperties = dataBaseProperties;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setKeyColumnName(String keyColumnName) {
		this.keyColumnName = keyColumnName;
	}

	public void setValueColumnName(String valueColumnName) {
		this.valueColumnName = valueColumnName;
	}

	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactory) throws BeansException {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			log.debug("Obteniendo configuracion de base de datos a traves del fichero /scsp-service.properties");

			InputStream in = getClass().getResourceAsStream(
					"/scsp-service.properties");
			if (in == null) {
				throw new RuntimeException(String.format(
						"No se encuentra el fichero de propiedades %s.",
						new Object[] { "/scsp-service.properties" }));
			}
			log.debug("Fichero obtenido desde "
					+ getClass().getResource("/scsp-service.properties")
							.getPath());
			DataSource datasource = beanFactory.getBean(DataSource.class);
			conn = datasource.getConnection();
			st = conn.createStatement();
			rs = st.executeQuery(String.format(
					"select %s, %s from %s", new Object[] { this.keyColumnName,
							this.valueColumnName, this.tableName }));
			while (rs.next()) {
				String key = rs.getString(this.keyColumnName);
				String value = rs.getString(this.valueColumnName);
				log.debug("Setting property " + key + "=" + value);
				this.dataBaseProperties.put(key, value);
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			try {
				if (conn != null)
					conn.close();
				if (st != null)
					st.close();
				if (rs != null)
					rs.close();
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
		super.postProcessBeanFactory(beanFactory);
	}

	protected String resolvePlaceholder(String placeholder, Properties props,
			int systemPropertiesMode) {
		String dataBaseValue = this.dataBaseProperties.getProperty(placeholder);
		if (dataBaseValue != null) {
			return dataBaseValue;
		}
		return super.resolvePlaceholder(placeholder, props,
				systemPropertiesMode);
	}
}