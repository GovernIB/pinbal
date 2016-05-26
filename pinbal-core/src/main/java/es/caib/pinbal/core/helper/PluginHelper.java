/**
 * 
 */
package es.caib.pinbal.core.helper;

import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.pinbal.plugins.CustodiaPlugin;
import es.caib.pinbal.plugins.DadesUsuari;
import es.caib.pinbal.plugins.DadesUsuariPlugin;
import es.caib.pinbal.plugins.SignaturaPlugin;
import es.caib.pinbal.plugins.SistemaExternException;

/**
 * Helper per a interactuar amb sistemes externs.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PluginHelper {

	private DadesUsuariPlugin dadesUsuariPlugin;
	private SignaturaPlugin signaturaPlugin;
	private CustodiaPlugin custodiaPlugin;



	public DadesUsuari dadesUsuariConsultarAmbUsuariCodi(
			String usuariCodi) throws SistemaExternException {
		try {
			return getDadesUsuariPlugin().consultarAmbUsuariCodi(usuariCodi);
		} catch (SistemaExternException ex) {
			LOGGER.error("Error en el plugin de dades d'usuari", ex);
			throw ex;
		} catch (Exception ex) {
			throw new SistemaExternException(
					"Error al crear la instància del plugin de dades d'usuari",
					ex);
		}
	}

	public DadesUsuari dadesUsuariConsultarAmbUsuariNif(
			String usuariNif) throws SistemaExternException {
		try {
			return getDadesUsuariPlugin().consultarAmbUsuariNif(usuariNif);
		} catch (SistemaExternException ex) {
			LOGGER.error("Error en el plugin de dades d'usuari", ex);
			throw ex;
		} catch (Exception ex) {
			throw new SistemaExternException(
					"Error al crear la instància del plugin de dades d'usuari",
					ex);
		}
	}

	public void signaturaSignarEstamparPdf(
			InputStream contentStream,
			OutputStream signedStream,
			String url) throws SistemaExternException {
		try {
			getSignaturaPlugin().signarEstamparPdf(
					contentStream,
					signedStream,
					url);
		} catch (SistemaExternException ex) {
			LOGGER.error("Error en el plugin de signatura", ex);
			throw ex;
		} catch (Exception ex) {
			throw new SistemaExternException(
					"Error al signar i estampar un document PDF",
					ex);
		}
	}

	public String custodiaObtenirUrlVerificacioDocument(
			String documentId,
			String solicitudId) throws SistemaExternException {
		try {
			return getCustodiaPlugin().obtenirUrlVerificacioDocument(
					documentId + "#" + solicitudId);
		} catch (SistemaExternException ex) {
			LOGGER.error("Error en el plugin de custodia", ex);
			throw ex;
		} catch (Exception ex) {
			throw new SistemaExternException(
					"Error al crear la instància del plugin de custòdia",
					ex);
		}
	}

	public void custodiaEnviarPdfSignat(
			String documentId,
			String arxiuNom,
			byte[] arxiuContingut,
			String documentTipus) throws SistemaExternException {
		try {
			getCustodiaPlugin().enviarPdfSignat(
					documentId,
					arxiuNom,
					arxiuContingut,
					documentTipus);
		} catch (SistemaExternException ex) {
			LOGGER.error("Error en el plugin de custodia", ex);
			throw ex;
		} catch (Exception ex) {
			throw new SistemaExternException(
					"Error al crear la instància del plugin de custòdia",
					ex);
		}
	}

	public byte[] custodiaObtenirDocument(
			String documentId) throws SistemaExternException {
		try {
			return getCustodiaPlugin().obtenirDocument(documentId);
		} catch (SistemaExternException ex) {
			LOGGER.error("Error en el plugin de custodia", ex);
			throw ex;
		} catch (Exception ex) {
			throw new SistemaExternException(
					"Error al crear la instància del plugin de custòdia",
					ex);
		}
	}



	private DadesUsuariPlugin getDadesUsuariPlugin() throws Exception {
		if (dadesUsuariPlugin == null) {
			String pluginClass = getPropertyPluginDadesUsuari();
			if (pluginClass != null && pluginClass.length() > 0) {
				Class<?> clazz = Class.forName(pluginClass);
				dadesUsuariPlugin = (DadesUsuariPlugin)clazz.newInstance();
			} else {
				throw new SistemaExternException(
						"La classe del plugin de dades d'usuari no està configurada");
			}
		}
		return dadesUsuariPlugin;
	}
	private SignaturaPlugin getSignaturaPlugin() throws Exception {
		if (signaturaPlugin == null) {
			String pluginClass = getPropertyPluginSignatura();
			if (pluginClass != null && pluginClass.length() > 0) {
				Class<?> clazz = Class.forName(pluginClass);
				signaturaPlugin = (SignaturaPlugin)clazz.newInstance();
			} else {
				throw new SistemaExternException(
						"La classe del plugin de signatura no està configurada");
			}
		}
		return signaturaPlugin;
	}
	private CustodiaPlugin getCustodiaPlugin() throws Exception {
		if (custodiaPlugin == null) {
			String pluginClass = getPropertyPluginCustodia();
			if (pluginClass != null && pluginClass.length() > 0) {
				Class<?> clazz = Class.forName(pluginClass);
				custodiaPlugin = (CustodiaPlugin)clazz.newInstance();
			} else {
				throw new SistemaExternException(
						"La classe del plugin de custòdia no està configurada");
			}
		}
		return custodiaPlugin;
	}

	private String getPropertyPluginDadesUsuari() {
		return PropertiesHelper.getProperties().getProperty("es.caib.pinbal.plugin.dades.usuari.class");
	}
	private String getPropertyPluginSignatura() {
		return PropertiesHelper.getProperties().getProperty("es.caib.pinbal.plugin.signatura.class");
	}
	private String getPropertyPluginCustodia() {
		return PropertiesHelper.getProperties().getProperty("es.caib.pinbal.plugin.custodia.class");
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(PluginHelper.class);

}
