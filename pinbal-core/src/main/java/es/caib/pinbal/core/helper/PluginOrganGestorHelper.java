package es.caib.pinbal.core.helper;

import java.util.Map;

import org.springframework.stereotype.Component;

import es.caib.pinbal.plugin.unitat.NodeDir3;
import es.caib.pinbal.plugin.unitat.UnitatsOrganitzativesPlugin;
import es.caib.pinbal.plugins.SistemaExternException;

/**
 * Helper per a accedir al plugin de organs gestors
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class PluginOrganGestorHelper {

	private UnitatsOrganitzativesPlugin unitatsOrganitzativesPlugin;
	private static final String PROPERTY_PLUGIN_CLASS = "es.caib.pinbal.plugin.unitats.organitzatives.class";
	
	public Map<String, NodeDir3> getOrganigramaOrganGestor(String codiDir3) throws Exception {
		Map<String, NodeDir3> organigrama = null;
		try {
			organigrama = getUnitatsOrganitzativesPlugin().organigrama(codiDir3);
		} catch (SistemaExternException ex) {
			throw new SistemaExternException("Error al obtenir l'organigrama per entitat");
		}
		return organigrama;
	}

	private UnitatsOrganitzativesPlugin getUnitatsOrganitzativesPlugin() throws Exception {
		if (unitatsOrganitzativesPlugin == null) {
			String pluginClass = getPropertyPluginUnitatsOrganitzatives();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					unitatsOrganitzativesPlugin = (UnitatsOrganitzativesPlugin)clazz.newInstance();
				} catch (Exception ex) {
					throw new SistemaExternException(
							"Error al crear la instància del plugin d'unitats organitzatives");
				}
			} else {
				throw new SistemaExternException(
						"No està configurada la classe per al plugin d'unitats organitzatives");
			}
		}
		return unitatsOrganitzativesPlugin;
	}

	private String getPropertyPluginUnitatsOrganitzatives() {
		return PropertiesHelper.getProperties().getProperty(PROPERTY_PLUGIN_CLASS);
	}

}
