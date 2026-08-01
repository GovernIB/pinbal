package es.scsp.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

/**
 * PlaceholderConfigurer per a que els beans de l'application context de SCSP
 * agafin els properties de la configuració de PINBAL (des de JNDI datasource +
 * taula core_parametro_configuracion).
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class ScspPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer implements EnvironmentAware {

	private static final String SQL_UPDATE = "UPDATE core_parametro_configuracion set valor = ? WHERE nombre = ?";
	private static final String SQL_INSERT = "INSERT INTO core_parametro_configuracion (nombre, valor, descripcion) values (?, ?, null)";
	private static final String SQL_SELECT = "SELECT valor FROM core_parametro_configuracion WHERE nombre = ?";

	private DataSource dataSource;
	private Properties dataBaseProperties = new Properties();
	private Environment environment;

	public ScspPropertyPlaceholderConfigurer() {}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

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

		if (dataSource == null)
			return null;

		try (Connection connection = dataSource.getConnection();
			 PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT)) {
			preparedStatement.setString(1, property);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					valor = resultSet.getString("valor");
				}
			}
		} catch (Exception ex) {
			log.debug("No ha estat possible obtenir la propietat {} de la base de dades.", property);
		}

		return valor;
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		try {
			log.info("Obtenint dataSource per a carregar les propietats de la BBDD...");
			String datasourceJndi = environment != null
					? environment.getProperty("spring.datasource.jndi-name")
					: null;
			if (datasourceJndi == null || datasourceJndi.isBlank()) {
				log.warn("spring.datasource.jndi-name no definit; no es carregaran propietats SCSP de la BBDD.");
			} else {
				dataSource = new JndiDataSourceLookup().getDataSource(datasourceJndi);
				Properties props = new Properties(System.getProperties());
				loadProperties(props);
				moveToDatabase("es.caib.pinbal.scsp.almacenamiento.ficheros", props);
				moveToDatabase("es.caib.pinbal.scsp.keystoreFile", props);
				moveToDatabase("es.caib.pinbal.scsp.keystorePass", props);
				loadDatabaseProperties(dataBaseProperties);
				log.info("... Datasource carregat correctament.");
			}
		} catch (Exception ex) {
			log.warn("... No ha estat possible carregar el Datasource: {}", ex.getMessage());
		}
		super.postProcessBeanFactory(beanFactory);
	}

	private void loadDatabaseProperties(Properties dataBaseProperties) {
		try (Connection connection = dataSource.getConnection();
			 PreparedStatement selectPreparedStatement = connection.prepareStatement("SELECT nombre, valor FROM core_parametro_configuracion")) {

			ResultSet resultSet = selectPreparedStatement.executeQuery();
			while (resultSet.next()) {
				dataBaseProperties.put(resultSet.getString("nombre"), resultSet.getString("valor"));
			}
			resultSet.close();
		} catch (Exception ex) {
			log.debug("... No ha estat possible carregar les propietats de la BBDD.");
		}
	}

	private void moveToDatabase(String property, Properties props) {
		// environment.getProperty(...) resol tant System.getProperties() com System.getenv() (aquesta darrera
		// és l'única via real per la qual arriben aquestes propietats quan es desplega amb docker-compose: les
		// variables d'entorn amb noms amb punts no es tradueixen mai a System properties -D de la JVM). Sense
		// això, la BBDD es queda amb el placeholder '.' sembrat per initial_data_scsp.sql i
		// ScspCryptoFactoryBean falla en arrencar.
		String propValue = environment != null ? environment.getProperty(property) : null;
		if (propValue == null) {
			propValue = super.resolvePlaceholder(property, props);
		}

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
