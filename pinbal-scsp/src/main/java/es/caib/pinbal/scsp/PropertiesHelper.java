/**
 *
 */
package es.caib.pinbal.scsp;

import es.caib.pinbal.logic.intf.base.config.BaseConfig;

import java.io.FileInputStream;
import java.io.IOException;
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

	private boolean llegirSystem = true;
	// Fitxer extern carregat com a últim recurs de getProperty(): el mateix que ja carrega Spring
	// via @PropertySource sobre BaseConfig.APP_PROPERTIES/APP_SYSTEM_PROPERTIES (vegeu
	// SystemPropertiesConfig i ConfigHelper, al mòdul pinbal-service). Sense aquest fallback, una
	// propietat definida només en aquest fitxer (no com a "-D" real ni com a variable d'entorn) és
	// invisible per a aquesta classe encara que ConfigHelper.getConfig() (Spring Environment) sí la
	// trobi: dues parts de l'aplicació que llegeixen la "mateixa" clau (p.ex.
	// "es.caib.pinbal.xsd.base.path") es comporten de manera diferent segons quina fan servir.
	private volatile Properties fitxerExternProperties;



	public static PropertiesHelper getProperties() {
		if (instance == null) {
			instance = new PropertiesHelper();
			String propertiesPath = System.getProperty(APPSERV_PROPS_PATH);
			if (propertiesPath != null) {
				instance.llegirSystem = false;
				logger.info("Llegint les propietats de l'aplicació del path: " + propertiesPath);
				try {
					if (propertiesPath.startsWith("classpath:")) {
						instance.load(
								PropertiesHelper.class.getClassLoader().getResourceAsStream(
										propertiesPath.substring("classpath:".length())));
					} else if (propertiesPath.startsWith("file://")) {
						FileInputStream fis = new FileInputStream(
								propertiesPath.substring("file://".length()));
						instance.load(fis);
					} else {
						FileInputStream fis = new FileInputStream(propertiesPath);
						instance.load(fis);
					}
				} catch (Exception ex) {
					logger.error("No s'han pogut llegir els properties", ex);
				}
			}
		}
		return instance;
	}

	public String getProperty(String key) {
		if (llegirSystem) {
			String value = System.getProperty(key);
			if (value == null) {
				value = System.getenv(key);
			}
			if (value == null) {
				value = getPropertyDelFitxerExtern(key);
			}
			return value;
		} else {
			return super.getProperty(key);
		}
	}

	// Nomes es cacheja quan s'ha arribat a carregar cap propietat real: si encara no hi ha cap
	// path configurat (p.ex. en un test, o abans que Spring hagi fixat les propietats de sistema
	// APP_PROPERTIES/APP_SYSTEM_PROPERTIES a l'arrencada) es torna a comprovar al proper accés en
	// lloc de quedar-se per sempre amb un resultat buit.
	private synchronized String getPropertyDelFitxerExtern(String key) {
		Properties props = fitxerExternProperties;
		if (props == null) {
			props = carregarFitxerExtern();
			if (!props.isEmpty()) {
				fitxerExternProperties = props;
			}
		}
		return props.getProperty(key);
	}

	private Properties carregarFitxerExtern() {
		Properties props = new Properties();
		for (String propertyKey: new String[] {BaseConfig.APP_PROPERTIES, BaseConfig.APP_SYSTEM_PROPERTIES}) {
			String path = System.getProperty(propertyKey);
			if (path == null) {
				continue;
			}
			try (FileInputStream fis = new FileInputStream(path)) {
				props.load(fis);
			} catch (IOException ex) {
				logger.warn("No s'ha pogut llegir el fitxer de propietats extern (" + propertyKey + "=" + path + ")", ex);
			}
		}
		return props;
	}

	public String getProperty(String key, String defaultValue) {
		String val = getProperty(key);
        return (val == null) ? defaultValue : val;
	}

	public boolean getAsBoolean(String key) {
		return new Boolean(getProperty(key)).booleanValue();
	}
	public int getAsInt(String key) {
		return new Integer(getProperty(key)).intValue();
	}
	public long getAsLong(String key) {
		return new Long(getProperty(key)).longValue();
	}
	public float getAsFloat(String key) {
		return new Float(getProperty(key)).floatValue();
	}
	public double getAsDouble(String key) {
		return new Double(getProperty(key)).doubleValue();
	}

	private static final Logger logger = LoggerFactory.getLogger(PropertiesHelper.class);
	private static final long serialVersionUID = 1L;

}
