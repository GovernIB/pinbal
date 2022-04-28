/**
 * 
 */
package es.caib.pinbal.core.service;

import es.caib.pinbal.core.helper.ConfigHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementació dels mètodes per obtenir els properties.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class PropertyServiceImpl implements PropertyService {

	@Autowired
	ConfigHelper configHelper;

	@Override
	public String get(String key) {
		return configHelper.getConfig(key);
//		return PropertiesHelper.getProperties().getProperty(key);
	}

	@Override
	public String get(String key, String defaultValue) {
		return configHelper.getConfig(key, defaultValue);
	}

}
