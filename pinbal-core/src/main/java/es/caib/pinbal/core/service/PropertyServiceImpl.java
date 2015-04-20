/**
 * 
 */
package es.caib.pinbal.core.service;

import org.springframework.stereotype.Service;

import es.caib.pinbal.core.helper.PropertiesHelper;

/**
 * Implementació dels mètodes per obtenir els properties.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class PropertyServiceImpl implements PropertyService {

	@Override
	public String get(String key) {
		return PropertiesHelper.getProperties().getProperty(key);
	}

}
