/**
 * 
 */
package es.caib.pinbal.logic.service;

import es.caib.pinbal.logic.helper.ConfigHelper;
import es.caib.pinbal.logic.intf.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementació dels mètodes per obtenir els properties.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@RequiredArgsConstructor
@Service
public class PropertyServiceImpl implements PropertyService {

	private final ConfigHelper configHelper;

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
