package es.caib.pinbal.core.helper;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.pinbal.core.dto.IntegracioAccioTipusEnumDto;
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

	@Autowired
	private IntegracioHelper integracioHelper;

	private UnitatsOrganitzativesPlugin unitatsOrganitzativesPlugin;
	private static final String PROPERTY_PLUGIN_CLASS = "es.caib.pinbal.plugin.unitats.organitzatives.class";

	public Map<String, NodeDir3> getOrganigramaOrganGestor(String codiDir3) throws Exception {
		String accioDescripcio = "Consulta de l'arbre d'òrgans gestors per entitat";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("codiDir3Entitat", codiDir3);
		long t0 = System.currentTimeMillis();
		Map<String, NodeDir3> organigrama = null;
		try {
			organigrama = getUnitatsOrganitzativesPlugin().organigrama(codiDir3);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ORGANS,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
		} catch (SistemaExternException ex) {
			String errorDescripcio = "Error al consultar l'arbre d'òrgans gestors per entitat";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ORGANS,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(errorDescripcio, ex);
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
