/**
 * 
 */
package es.scsp.common.utils;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import es.scsp.common.domain.core.ParametroConfiguracion;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

/**
 * PlaceholderConfigurer per a que les els beans de l'application
 * context de SCSP agafin els properties de la configuració de PINBAL.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class ScspPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

	private static final String SQL_UPDATE = "UPDATE core_parametro_configuracion set valor = ? WHERE nombre = ?";
	private static final String SQL_INSERT = "INSERT INTO core_parametro_configuracion (nombre, valor, descripcion) values (?, ?, null)";
	private static final String SQL_SELECT = "SELECT valor FROM core_parametro_configuracion WHERE nombre = ?";

	private DataSource dataSource;
	private Properties dataBaseProperties = new Properties();

	public ScspPropertyPlaceholderConfigurer() {}

	protected String resolvePlaceholder(String placeholder, Properties props, int systemPropertiesMode) {

		String dataBaseValue = getProperty(placeholder);
		if (dataBaseValue != null) {
			return dataBaseValue;
		} else {
			log.warn("********************************************************************************");
			log.warn("No se ha encontrado en base de datos la propiedad de configuración: " + placeholder);
			log.warn("********************************************************************************");
			return super.resolvePlaceholder(placeholder, props, systemPropertiesMode);
		}
	}

	public String getProperty(String property) {
		String valor = this.dataBaseProperties.getProperty(property);
		if (valor != null)
			return valor;

		try {
			Connection connection = dataSource.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT);
			preparedStatement.setString(1, property);
			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				valor = resultSet.getString("valor");
			}
			resultSet.close();
			preparedStatement.close();
			connection.close();

		} catch (Exception ex) {
			log.debug("No ha estat possible obtenir la propietat {} de la base de dades.", property);
		}

		return valor;
	}

	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		try {
			log.info("Obtenint dataSource per a carregar les propietats de la BBDD...");
			JndiDataSourceLookup lookup = new JndiDataSourceLookup();
			Properties props = new Properties(System.getProperties());
			loadProperties(props);
			String datasourceJndi = super.resolvePlaceholder("es.caib.pinbal.datasource.jndi", props);
			dataSource = lookup.getDataSource(datasourceJndi);

			if (dataSource != null) {
				// Carregam les propietats del fitxer a la BBDD
				moveToDatabase("es.caib.pinbal.scsp.almacenamiento.ficheros", props);
				moveToDatabase("es.caib.pinbal.scsp.keystoreFile", props);
				moveToDatabase("es.caib.pinbal.scsp.keystorePass", props);
				loadDatabaseProperties(dataBaseProperties);
			}
			log.debug("... Datasource carregat correctament.");
		} catch (Exception ex) {
			log.debug("... No ha estat possible carregar el Datasource.");
		}
		super.postProcessBeanFactory(beanFactory);
	}

	private void loadDatabaseProperties(Properties dataBaseProperties) {
		try (Connection connection = dataSource.getConnection();
			 PreparedStatement selectPreparedStatement = connection.prepareStatement("SELECT nombre, valor FROM core_parametro_configuracion")) {

			ResultSet resultSet = selectPreparedStatement.executeQuery();
			while(resultSet.next()) {
				dataBaseProperties.put(resultSet.getString("nombre"), resultSet.getString("valor"));
			}
			resultSet.close();
		} catch (Exception ex) {
			log.debug("... No ha estat possible carregar les propietats de la BBDD.");
		}

	}

	private void moveToDatabase(String property, Properties props) {

		String propValue = super.resolvePlaceholder(property, props);

		try (Connection connection = dataSource.getConnection();
			 PreparedStatement selectPreparedStatement = connection.prepareStatement(SQL_SELECT);
			 PreparedStatement insertPreparedStatement = connection.prepareStatement(SQL_INSERT);
			 PreparedStatement updatePreparedStatement = connection.prepareStatement(SQL_UPDATE)) {

			selectPreparedStatement.setString(1, property.substring(20));
			ResultSet resultSet = selectPreparedStatement.executeQuery();
			boolean propertyExists = resultSet.next();
			resultSet.close();

			if (propertyExists) {
				updatePreparedStatement.setString(1, propValue);
				updatePreparedStatement.setString(2, property.substring(20));
				updatePreparedStatement.executeUpdate();
			} else {
				insertPreparedStatement.setString(1, property.substring(20));
				insertPreparedStatement.setString(2, propValue);
				insertPreparedStatement.executeUpdate();
			}

		} catch (Exception ex) {
			log.debug("... No ha estat possible moure la propietat {} a la BBDD.", property);
		}
	}
}
