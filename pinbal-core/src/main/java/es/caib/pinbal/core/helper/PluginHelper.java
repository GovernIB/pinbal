/**
 * 
 */
package es.caib.pinbal.core.helper;

import es.caib.pinbal.core.dto.FitxerDto;
import es.caib.pinbal.core.dto.IntegracioAccioTipusEnumDto;
import es.caib.pinbal.plugin.PropertiesHelper;
import es.caib.pinbal.plugin.unitat.NodeDir3;
import es.caib.pinbal.plugin.unitat.UnitatOrganitzativa;
import es.caib.pinbal.plugin.unitat.UnitatsOrganitzativesPlugin;
import es.caib.pinbal.plugins.CustodiaPlugin;
import es.caib.pinbal.plugins.DadesUsuari;
import es.caib.pinbal.plugins.DadesUsuariPlugin;
import es.caib.pinbal.plugins.FirmaServidorPlugin;
import es.caib.pinbal.plugins.FirmaServidorPlugin.TipusFirma;
import es.caib.pinbal.plugins.SignaturaPlugin;
import es.caib.pinbal.plugins.SistemaExternException;
import es.caib.plugins.arxiu.api.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Helper per a interactuar amb sistemes externs.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class PluginHelper {

	private static final String PROPERTY_PLUGIN_UNITATS_CLASS = "es.caib.pinbal.plugin.unitats.organitzatives.class";
	private static final String PROPERTY_PLUGIN_USUARIS_CLASS = "es.caib.pinbal.plugin.dades.usuari.class";
	private static final String PROPERTY_PLUGIN_SIGNATURA_CLASS = "es.caib.pinbal.plugin.signatura.class";
	private static final String PROPERTY_PLUGIN_CUSTODIA_CLASS = "es.caib.pinbal.plugin.custodia.class";
	private static final String PROPERTY_PLUGIN_FIRMA_SERVIDOR_CLASS = "es.caib.pinbal.plugin.firmaservidor.class";
	private static final String PROPERTY_PLUGIN_ARXIU_CLASS = "es.caib.pinbal.plugin.arxiu.class";


	private DadesUsuariPlugin dadesUsuariPlugin;
	private SignaturaPlugin signaturaPlugin;
	private CustodiaPlugin custodiaPlugin;
	private FirmaServidorPlugin firmaServidorPlugin;
	private IArxiuPlugin arxiuPlugin;
	private UnitatsOrganitzativesPlugin unitatsOrganitzativesPlugin;

	@Autowired
	private IntegracioHelper integracioHelper;
	@Autowired
	private ConfigHelper configHelper;

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
	
	public List<DadesUsuari> dadesUsuariFindAmbGrup(
			String grupCodi) throws SistemaExternException {
		String accioDescripcio = "Consulta d'usuaris d'un grup";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("grup", grupCodi);
		long t0 = System.currentTimeMillis();
		try {
			List<DadesUsuari> dadesUsuari = getDadesUsuariPlugin().findAmbGrup(
					grupCodi);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_USUARIS,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return dadesUsuari;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades d'usuari";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_USUARIS,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					errorDescripcio,
					ex);
		}
	}

	public void signaturaIbkeySignarEstamparPdf(
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
			String documentId) throws SistemaExternException {
		try {
			return getCustodiaPlugin().obtenirUrlVerificacioDocument(documentId);
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

	public boolean isPluginFirmaServidorActiu() {
		String propertyPlugin = getPropertyPluginFirmaServidor();
		return propertyPlugin != null && !propertyPlugin.isEmpty();
	}

	public byte[] firmaServidorFirmar(
			FitxerDto fitxer,
			TipusFirma tipusFirma,
			String motiu,
			String idioma) throws SistemaExternException {
		
		String accioDescripcio = "Firma en servidor d'un fitxer";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("nom", fitxer.getNom());
		long t0 = System.currentTimeMillis();
		try {
			byte[] firmaContingut = getFirmaServidorPlugin().firmar(
					fitxer.getNom(),
					motiu,
					fitxer.getContingut(),
					tipusFirma,
					idioma);
			
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_FIRMASERV,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			
			return firmaContingut;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de firma en servidor: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_FIRMASERV,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			
			throw new SistemaExternException(
					errorDescripcio,
					ex);
		}
	}

	public boolean isPluginArxiuActiu() {
		String propertyPlugin = getPropertyPluginArxiu();
		return propertyPlugin != null && !propertyPlugin.isEmpty();
	}

	public String arxiuExpedientCrear(
			String titol,
			String interessatDocumentNum,
			String codiDir3,
			String codiClassificacio,
			String codiProcediment,
			String serieDocumental) throws SistemaExternException {
		String accioDescripcio = "Creació d'un expedient";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("titol", titol);
		accioParams.put("interessatDocumentNum", interessatDocumentNum);
		accioParams.put("codiDir3", codiDir3);
		accioParams.put("codiClassificacio", codiClassificacio);
		accioParams.put("codiProcediment", codiProcediment);
		accioParams.put("serieDocumental", serieDocumental);
		long t0 = System.currentTimeMillis();
		try {
			String classificacio;
			if (codiClassificacio != null) {
				classificacio = codiClassificacio;
			} else {
				classificacio = codiDir3 + "_PRO_PBL" + StringUtils.leftPad(codiProcediment, 27, "0");
			}
			ContingutArxiu expedientCreat = getArxiuPlugin().expedientCrear(
					toArxiuExpedient(
							null,
							titol,
							null,
							Arrays.asList(codiDir3),
							new Date(),
							classificacio,
							ExpedientEstat.OBERT,
							Arrays.asList(interessatDocumentNum),
							serieDocumental));
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_FIRMASERV,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			
			return expedientCreat.getIdentificador();
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de firma en servidor: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_FIRMASERV,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			
			throw new SistemaExternException(
					errorDescripcio,
					ex);
		}
	}

	public Expedient arxiuExpedientConsultar(
			String uuid) throws SistemaExternException {
		String accioDescripcio = "Consulta d'un expedient per uuid";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("uuid", uuid);
		long t0 = System.currentTimeMillis();
		try {
			Expedient arxiuExpedient = getArxiuPlugin().expedientDetalls(
					uuid,
					null);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return arxiuExpedient;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					errorDescripcio,
					ex);
		}
	}

	public ContingutArxiu arxiuExpedientCercarAmbNom(
			String nom) throws SistemaExternException {
		String accioDescripcio = "Consulta d'un expedient per nom";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("nom", nom);
		long t0 = System.currentTimeMillis();
		try {
			ConsultaFiltre filtre = new ConsultaFiltre();
			filtre.setMetadada("name");
			filtre.setOperacio(ConsultaOperacio.IGUAL);
			filtre.setValorOperacio1(nom);
			ConsultaResultat resultat = getArxiuPlugin().expedientConsulta(Arrays.asList(filtre), 0, 1);
			
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			if (resultat.getNumRetornat() > 0) {
				return resultat.getResultats().get(0);
			} else {
				return null;
			}
		} catch (Exception ex) {
			
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					errorDescripcio,
					ex);
		}
	}

	public void arxiuExpedientTancar(
			String uuid) throws SistemaExternException {
		String accioDescripcio = "Tancament d'un expedient";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("uuid", uuid);
		long t0 = System.currentTimeMillis();
		
		try {
			getArxiuPlugin().expedientTancar(uuid);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					errorDescripcio,
					ex);
		}
	}

	public void arxiuExpedientEsborrar(
			String uuid) throws SistemaExternException {
		String accioDescripcio = "Eliminació d'un expedient";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("uuid", uuid);
		long t0 = System.currentTimeMillis();
		try {
			getArxiuPlugin().expedientEsborrar(uuid);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					errorDescripcio,
					ex);
		}
	}

	public String arxiuDocumentGuardarFirmaPades(
			String expedientUuid,
			String titol,
			String codiDir3,
			String serieDocumental,
			FitxerDto fitxerPdfFirmat,
			ContingutOrigen ntiOrigen,
			DocumentEstatElaboracio ntiEstatElaboracio,
			DocumentTipus ntiTipusDocumental) throws SistemaExternException {
		
		
		String accioDescripcio = "Creació d'un document";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("expedientUuid", expedientUuid);
		accioParams.put("titol", titol);
		accioParams.put("codiDir3", codiDir3);
		accioParams.put("fitxerNom", fitxerPdfFirmat.getNom());
		accioParams.put("ntiOrigen", ntiOrigen.toString());
		accioParams.put("ntiEstatElaboracio", ntiEstatElaboracio.toString());
		accioParams.put("ntiTipusDocumental", ntiTipusDocumental.toString());
		long t0 = System.currentTimeMillis();
		
		try {
			ArxiuFirma firma = new ArxiuFirma();
			firma.setNom(fitxerPdfFirmat.getNom());
			firma.setContingut(fitxerPdfFirmat.getContingut());
			firma.setContentType("application/pdf");
			firma.setTipus(FirmaTipus.PADES);
			firma.setPerfil(FirmaPerfil.EPES);
			ContingutArxiu documentModificat = getArxiuPlugin().documentCrear(
					toArxiuDocument(
							null,
							titol,
							null,
							Arrays.asList(firma),
							false,
							null,
							ntiOrigen,
							Arrays.asList(codiDir3),
							new Date(),
							ntiEstatElaboracio,
							ntiTipusDocumental,
							DocumentEstat.DEFINITIU,
							serieDocumental),
					expedientUuid);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			
			return documentModificat.getIdentificador();
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					errorDescripcio,
					ex);
		}
	}

	public Document arxiuDocumentConsultar(
			String arxiuUuid,
			String versio,
			boolean ambContingut,
			boolean ambVersioImprimible) throws SistemaExternException {
		
		String accioDescripcio = "Consulta d'un document";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("nodeId", arxiuUuid);
		accioParams.put("arxiuUuidCalculat", arxiuUuid);
		accioParams.put("versio", versio);
		accioParams.put("ambContingut", new Boolean(ambContingut).toString());
		long t0 = System.currentTimeMillis();
		try {
			Document documentDetalls = getArxiuPlugin().documentDetalls(
					arxiuUuid,
					versio,
					ambContingut);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			
			if (ambVersioImprimible && documentDetalls.getFirmes() != null && !documentDetalls.getFirmes().isEmpty()) {
				documentDetalls.setContingut(getArxiuPlugin().documentImprimible(documentDetalls.getIdentificador()));
			}
			return documentDetalls;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			
			throw new SistemaExternException(
					errorDescripcio,
					ex);
		}
	}

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

	public List<UnitatOrganitzativa> getOrganigramaAmbPare(String codiDir3) throws Exception {
		String accioDescripcio = "Consulta dels òrgans gestors per entitat";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("codiDir3Entitat", codiDir3);
		long t0 = System.currentTimeMillis();
		List<UnitatOrganitzativa> organs = null;
		try {
			organs = getUnitatsOrganitzativesPlugin().findAmbPare(codiDir3);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ORGANS,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
		} catch (SistemaExternException ex) {
			String errorDescripcio = "Error al consultar els òrgans gestors per entitat";
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
		return organs;
	}

	private Expedient toArxiuExpedient(
			String identificador,
			String nom,
			String ntiIdentificador,
			List<String> ntiOrgans,
			Date ntiDataObertura,
			String ntiClassificacio,
			ExpedientEstat ntiEstat,
			List<String> ntiInteressats,
			String serieDocumental) {
		Expedient expedient = new Expedient();
		expedient.setNom(nom);
		expedient.setIdentificador(identificador);
		ExpedientMetadades metadades = new ExpedientMetadades();
		metadades.setIdentificador(ntiIdentificador);
		metadades.setDataObertura(ntiDataObertura);
		metadades.setClassificacio(ntiClassificacio);
		metadades.setEstat(ntiEstat);
		metadades.setOrgans(ntiOrgans);
		metadades.setInteressats(ntiInteressats);
		metadades.setSerieDocumental(serieDocumental);
		expedient.setMetadades(metadades);
		return expedient;
	}

	/*
	 * - Si fitxer != null aleshores suposarem que el document no està firmat.
	 * - Si fitxer == null aleshores suposarem que el document està firmat i
	 *   les firmes es troben a dins la variable firmes.
	 */
	private Document toArxiuDocument(
			String documentUuid,
			String nom,
			FitxerDto fitxer,
			List<ArxiuFirma> firmes,
			boolean firmaSeparada,
			String ntiIdentificador,
			ContingutOrigen ntiOrigen,
			List<String> ntiOrgans,
			Date ntiDataCaptura,
			DocumentEstatElaboracio ntiEstatElaboracio,
			DocumentTipus ntiTipusDocumental,
			DocumentEstat estat,
			String serieDocumental) {
		Document document = new Document();
		document.setNom(nom);
		document.setIdentificador(documentUuid);
		DocumentMetadades metadades = new DocumentMetadades();
		String fitxerExtensio = null;
		document.setMetadades(metadades);
		document.setEstat(estat);
		if (fitxer != null) {
			DocumentContingut contingut = new DocumentContingut();
			contingut.setArxiuNom(fitxer.getNom());
			contingut.setContingut(fitxer.getContingut());
			contingut.setTipusMime(fitxer.getContentType());
			document.setContingut(contingut);
			fitxerExtensio = fitxer.getExtensio();
		}
		if (firmes != null && !firmes.isEmpty() && !firmaSeparada) {
			// Firma attached
			Firma firma = new Firma();
			ArxiuFirma primeraFirma = firmes.get(0);
			firma.setFitxerNom(primeraFirma.getNom());
			firma.setContingut(primeraFirma.getContingut());
			firma.setTipusMime(primeraFirma.getContentType());
			firma.setTipus(primeraFirma.getTipus());
			firma.setPerfil(primeraFirma.getPerfil());
			document.setFirmes(Arrays.asList(firma));
			fitxerExtensio = primeraFirma.getExtensio();
		} else if (firmes != null && !firmes.isEmpty() && firmaSeparada) {
			// Firma detached
			document.setFirmes(new ArrayList<Firma>());
			for (ArxiuFirma arxiuFirma: firmes) {
				Firma firma = new Firma();
				firma.setFitxerNom(arxiuFirma.getNom());
				firma.setContingut(arxiuFirma.getContingut());
				firma.setTipusMime(arxiuFirma.getContentType());
				firma.setTipus(arxiuFirma.getTipus());
				firma.setPerfil(arxiuFirma.getPerfil());
				document.getFirmes().add(firma);
			}
			fitxerExtensio = fitxer.getExtensio();
		}
		configurarDocumentMetadades(
				ntiOrigen,
				ntiIdentificador,
				ntiDataCaptura,
				ntiEstatElaboracio,
				ntiTipusDocumental,
				fitxerExtensio,
				ntiOrgans,
				serieDocumental,
				metadades);
		return document;
	}

	private void configurarDocumentMetadades(
			ContingutOrigen ntiOrigen,
			String ntiIdentificador,
			Date ntiDataCaptura,
			DocumentEstatElaboracio ntiEstatElaboracio,
			DocumentTipus ntiTipusDocumental,
			String fitxerExtensio,
			List<String> ntiOrgans,
			String serieDocumental,
			DocumentMetadades metadades){
		metadades.setIdentificador(ntiIdentificador);
		metadades.setOrigen(ntiOrigen);
		metadades.setDataCaptura(ntiDataCaptura);
		metadades.setEstatElaboracio(ntiEstatElaboracio);
		metadades.setTipusDocumental(ntiTipusDocumental);
		DocumentExtensio extensio = null;
		if (fitxerExtensio != null) {
			String extensioAmbPunt = (fitxerExtensio.startsWith(".")) ? fitxerExtensio.toLowerCase(): "." + fitxerExtensio.toLowerCase();
			extensio = DocumentExtensio.toEnum(extensioAmbPunt);
		}
		if (extensio != null) {
			metadades.setExtensio(extensio);
			DocumentFormat format = null;
			switch (extensio) {
			case AVI:
				format = DocumentFormat.AVI;
				break;
			case CSS:
				format = DocumentFormat.CSS;
				break;
			case CSV:
				format = DocumentFormat.CSV;
				break;
			case DOCX:
				format = DocumentFormat.SOXML;
				break;
			case GML:
				format = DocumentFormat.GML;
				break;
			case GZ:
				format = DocumentFormat.GZIP;
				break;
			case HTM:
				format = DocumentFormat.XHTML; // HTML o XHTML!!!
				break;
			case HTML:
				format = DocumentFormat.XHTML; // HTML o XHTML!!!
				break;
			case JPEG:
				format = DocumentFormat.JPEG;
				break;
			case JPG:
				format = DocumentFormat.JPEG;
				break;
			case MHT:
				format = DocumentFormat.MHTML;
				break;
			case MHTML:
				format = DocumentFormat.MHTML;
				break;
			case MP3:
				format = DocumentFormat.MP3;
				break;
			case MP4:
				format = DocumentFormat.MP4V; // MP4A o MP4V!!!
				break;
			case MPEG:
				format = DocumentFormat.MP4V; // MP4A o MP4V!!!
				break;
			case ODG:
				format = DocumentFormat.OASIS12;
				break;
			case ODP:
				format = DocumentFormat.OASIS12;
				break;
			case ODS:
				format = DocumentFormat.OASIS12;
				break;
			case ODT:
				format = DocumentFormat.OASIS12;
				break;
			case OGA:
				format = DocumentFormat.OGG;
				break;
			case OGG:
				format = DocumentFormat.OGG;
				break;
			case PDF:
				format = DocumentFormat.PDF; // PDF o PDFA!!!
				break;
			case PNG:
				format = DocumentFormat.PNG;
				break;
			case PPTX:
				format = DocumentFormat.SOXML;
				break;
			case RTF:
				format = DocumentFormat.RTF;
				break;
			case SVG:
				format = DocumentFormat.SVG;
				break;
			case TIFF:
				format = DocumentFormat.TIFF;
				break;
			case TXT:
				format = DocumentFormat.TXT;
				break;
			case WEBM:
				format = DocumentFormat.WEBM;
				break;
			case XLSX:
				format = DocumentFormat.SOXML;
				break;
			case ZIP:
				format = DocumentFormat.ZIP;
				break;
			case CSIG:
				format = DocumentFormat.CSIG;
				break;
			case XSIG:
				format = DocumentFormat.XSIG;
				break;
			case XML:
				format = DocumentFormat.XML;
				break;
			}
			metadades.setFormat(format);
		}
		metadades.setOrgans(ntiOrgans);
		metadades.setSerieDocumental(serieDocumental);
	}

	private DadesUsuariPlugin getDadesUsuariPlugin() throws Exception {
		loadPluginProperties("USUARIS");
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
		loadPluginProperties("SIGNATURA");
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
		loadPluginProperties("CUSTODIA");
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
	private FirmaServidorPlugin getFirmaServidorPlugin() throws Exception {
		loadPluginProperties("FIRMA_SERVIDOR");
		if (firmaServidorPlugin == null) {
			String pluginClass = getPropertyPluginFirmaServidor();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					firmaServidorPlugin = (FirmaServidorPlugin)clazz.newInstance();
				} catch (Exception ex) {
					throw new SistemaExternException(
							"Error al crear la instància del plugin de firma en servidor",
							ex);
				}
			} else {
				throw new SistemaExternException(
						"No està configurada la classe per al plugin de firma en servidor");
			}
		}
		return firmaServidorPlugin;
	}
	private IArxiuPlugin getArxiuPlugin() throws SistemaExternException {
		loadPluginProperties("ARXIU");
		if (arxiuPlugin == null) {
			String pluginClass = getPropertyPluginArxiu();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					arxiuPlugin = (IArxiuPlugin)clazz.getDeclaredConstructor(
							String.class,
							Properties.class).newInstance(
							"es.caib.pinbal.",
							PropertiesHelper.getProperties().findAll());
				} catch (Exception ex) {
					throw new SistemaExternException(
							"Error al crear la instància del plugin d'arxiu digital",
							ex);
				}
			} else {
				throw new SistemaExternException(
						"No està configurada la classe per al plugin d'arxiu digital");
			}
		}
		return arxiuPlugin;
	}

	private UnitatsOrganitzativesPlugin getUnitatsOrganitzativesPlugin() throws Exception {
		loadPluginProperties("UNITATS");
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

	private final static Map<String, Boolean> propertiesLoaded = new HashMap<>();
	private synchronized void loadPluginProperties(String codeProperties) {
		if (!propertiesLoaded.containsKey(codeProperties) || !propertiesLoaded.get(codeProperties)) {
			propertiesLoaded.put(codeProperties, true);
			Map<String, String> pluginProps = configHelper.getGroupProperties(codeProperties);
			for (Map.Entry<String, String> entry : pluginProps.entrySet() ) {
				String value = entry.getValue() == null ? "" : entry.getValue();
				PropertiesHelper.getProperties().setProperty(entry.getKey(), value);
			}
		}
	}

	public void reloadProperties(String codeProperties) {
		if (propertiesLoaded.containsKey(codeProperties))
			propertiesLoaded.put(codeProperties, false);
	}
	public void resetPlugins() {
		dadesUsuariPlugin = null;
		signaturaPlugin = null;
		custodiaPlugin = null;
		firmaServidorPlugin = null;
		arxiuPlugin = null;
		unitatsOrganitzativesPlugin = null;
	}

	private String getPropertyPluginDadesUsuari() {
		return configHelper.getConfig(PROPERTY_PLUGIN_USUARIS_CLASS);
	}
	private String getPropertyPluginSignatura() {
		return configHelper.getConfig(PROPERTY_PLUGIN_SIGNATURA_CLASS);
	}
	private String getPropertyPluginCustodia() {
		return configHelper.getConfig(PROPERTY_PLUGIN_CUSTODIA_CLASS);
	}
	private String getPropertyPluginFirmaServidor() {
		return configHelper.getConfig(PROPERTY_PLUGIN_FIRMA_SERVIDOR_CLASS);
	}
	private String getPropertyPluginArxiu() {
		return configHelper.getConfig(PROPERTY_PLUGIN_ARXIU_CLASS);
	}
	private String getPropertyPluginUnitatsOrganitzatives() {
		return configHelper.getConfig(PROPERTY_PLUGIN_UNITATS_CLASS);
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(PluginHelper.class);

	@SuppressWarnings("serial")
	private static class ArxiuFirma extends FitxerDto {
		private FirmaTipus tipus;
		private FirmaPerfil perfil;
		public FirmaTipus getTipus() {
			return tipus;
		}
		public void setTipus(FirmaTipus tipus) {
			this.tipus = tipus;
		}
		public FirmaPerfil getPerfil() {
			return perfil;
		}
		public void setPerfil(FirmaPerfil perfil) {
			this.perfil = perfil;
		}
	}

}
