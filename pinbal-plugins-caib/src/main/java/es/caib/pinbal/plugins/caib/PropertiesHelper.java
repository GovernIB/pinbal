/**
 * 
 */
package es.caib.pinbal.plugins.caib;

import java.io.FileInputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilitat per accedir a les entrades del fitxer de properties.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PropertiesHelper extends Properties {

	private static final String APPSERV_PROPS_PATH = "es.caib.pinbal.properties.path";

	private static PropertiesHelper instance = null;



	public static PropertiesHelper getProperties() {
		if (instance == null) {
			String basePath = System.getProperty(APPSERV_PROPS_PATH);
			if (basePath != null) {
				try {
					if (basePath.startsWith("classpath:")) {
						instance = new PropertiesHelper();
						instance.load(PropertiesHelper.class.getClassLoader().getResourceAsStream(
								basePath.substring("classpath:".length())));
					} else if (basePath.startsWith("file://")) {
						basePath = basePath.substring("file://".length());
						FileInputStream fis = new FileInputStream(basePath);
						instance = new PropertiesHelper();
						instance.load(fis);
					} else {
						FileInputStream fis = new FileInputStream(basePath);
						instance = new PropertiesHelper();
						instance.load(fis);
					}
				} catch (Exception ex) {
					LOGGER.error("No s'han pogut llegir els properties", ex);
				}
			} else {
				LOGGER.error("No s'han pogut llegir els properties: la propietat de sistema " + APPSERV_PROPS_PATH + " no està definida");
			}
		}
		return instance;
	}

	public boolean getAsBoolean(String key) {
		return "true".equalsIgnoreCase(getProperty("key")) ? true : false;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesHelper.class);
	private static final long serialVersionUID = 1L;

}
