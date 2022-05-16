/**
 * 
 */
package es.caib.pinbal.plugins.caib;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import es.caib.pinbal.plugin.PropertiesHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.pinbal.plugins.CustodiaPlugin;
import es.caib.pinbal.plugins.SistemaExternException;
import es.caib.pinbal.plugins.caib.CustodiaCaibHelper.CustodiaResponse;
import es.caib.signatura.cliente.custodia.CustodiaRequestBuilder;

/**
 * Implementació del plugin de custòdia emprant l'aplicació
 * de custòdia de la CAIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CustodiaPluginCaib implements CustodiaPlugin {

	private Map<String, String> cacheHash = new HashMap<String, String>();



	@Override
	public String obtenirUrlVerificacioDocument(String documentId) throws SistemaExternException {
		LOGGER.debug("Obtenint URL de verificació pel document (id=" + documentId + ")");
		String token = cacheHash.get(documentId);
		if (token == null) {
			try {
				byte[] response = CustodiaCaibHelper.getCustodiaClient(
						getPropertyUrl(),
						getPropertyUsername(),
						getPropertyPassword()).reservarDocumento(
								getPropertyUsername(),
								getPropertyPassword(),
								documentId);
				if (CustodiaCaibHelper.isXmlResponse(response)) {
					CustodiaResponse resposta = CustodiaCaibHelper.parseResponse(response);
					throw new SistemaExternException("Error al reservar document de custòdia: [" + resposta.getErrorCodi() + "] " + resposta.getErrorDescripcio());
				} else {
					token = new String(response);
					cacheHash.put(documentId, token);
				}
			} catch (Exception ex) {
				throw new SistemaExternException("Error al obtenir la URL del document (documentId=" + documentId + ")", ex);
			}
		}
		String url = getVerificacioBaseUrl() + token;
		LOGGER.debug("Obtinguda URL de verificació pel document (id=" + documentId + "): " + url);
		return url;
	}

	@Override
	public void enviarPdfSignat(
			String documentId,
			String arxiuNom,
			byte[] arxiuContingut,
			String documentTipus) throws SistemaExternException {
		LOGGER.debug("Enviant PDF signat a la custòdia (id=" + documentId + ", arxiuNom=" + arxiuNom + ", documentTipus=" + documentTipus + ")");
		try {
			CustodiaRequestBuilder custodiaRequestBuilder = new CustodiaRequestBuilder(
					getPropertyUsername(),
					getPropertyPassword());
			byte[] xml = custodiaRequestBuilder.buildXML(
					new ByteArrayInputStream(arxiuContingut),
					arxiuNom,
					documentId,
					(documentTipus == null || documentTipus.isEmpty()) ? getDocumentTipusPerDefecte() : documentTipus);
			byte[] response = CustodiaCaibHelper.getCustodiaClient(
					getPropertyUrl(),
					getPropertyUsername(),
					getPropertyPassword()).custodiarPDFFirmado_v2(xml);
			CustodiaResponse resposta = CustodiaCaibHelper.parseResponse(response);
			if (resposta.isError())
				throw new SistemaExternException("La petició de custodiar PDF signat ha retornat un error: [" + resposta.getErrorCodi() + "] " + resposta.getErrorDescripcio());
		} catch (Exception ex) {
			throw new SistemaExternException("Error al enviar PDF signat a custòdia", ex);
		}
	}

	@Override
	public byte[] obtenirDocument(String documentId) throws SistemaExternException {
		LOGGER.debug("Descarregant document custodiat (id=" + documentId + ")");
		try {
			byte[] response = CustodiaCaibHelper.getCustodiaClient(
					getPropertyUrl(),
					getPropertyUsername(),
					getPropertyPassword()).consultarDocumento_v2(
					getPropertyUsername(),
					getPropertyPassword(),
					documentId);
			if (CustodiaCaibHelper.isXmlResponse(response)) {
				CustodiaResponse resposta = CustodiaCaibHelper.parseResponse(response);
				throw new SistemaExternException("Error al llegir document de custòdia: [" + resposta.getErrorCodi() + "] " + resposta.getErrorDescripcio());
			} else {
				return response;
			}
		} catch (Exception ex) {
			throw new SistemaExternException("Error al llegir document de custòdia", ex);
		}
	}



	private String getPropertyUrl() {
		return PropertiesHelper.getProperties().getProperty("es.caib.pinbal.plugin.custodia.caib.url");
	}
	private String getPropertyUsername() {
		return PropertiesHelper.getProperties().getProperty("es.caib.pinbal.plugin.custodia.caib.username");
	}
	private String getPropertyPassword() {
		return PropertiesHelper.getProperties().getProperty("es.caib.pinbal.plugin.custodia.caib.password");
	}
	private String getDocumentTipusPerDefecte() {
		return PropertiesHelper.getProperties().getProperty("es.caib.pinbal.plugin.custodia.caib.tipus.document");
	}
	private String getVerificacioBaseUrl() {
		return PropertiesHelper.getProperties().getProperty("es.caib.pinbal.plugin.custodia.caib.verificacio.url.base");
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(CustodiaPluginCaib.class);

}
