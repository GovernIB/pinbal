/**
 * 
 */
package es.scsp.common.utils;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import es.caib.pinbal.scsp.PropertiesHelper;

/**
 * PlaceholderConfigurer per a que les els beans de l'application
 * context de SCSP agafin els properties de la configuració de PINBAL.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ScspPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

	/*@Autowired
	private ParametroConfiguracionDao paramDao;*/

	public ScspPropertyPlaceholderConfigurer() {
	}

	/*public String getProperty(String property) {
		ParametroConfiguracion param = paramDao.select(property);
		if (param != null) {
			System.out.println(">>> getProperty (property=" + property + "): " + param.getValor());
			return param.getValor();
		} else {
			System.out.println(">>> getProperty (property=" + property + "): null");
			return null;
		}
	}*/
	public String getProperty(String property) {
		String propVal = PropertiesHelper.getProperties().getProperty(property);
		if (propVal == null) {
			String prefix = "es.caib.pinbal.scsp.";
			return PropertiesHelper.getProperties().getProperty(prefix + property);
		}
		return propVal;
	}

}
