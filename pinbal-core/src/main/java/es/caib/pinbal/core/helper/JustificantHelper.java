/**
 * 
 */
package es.caib.pinbal.core.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import es.caib.pinbal.core.model.HistoricConsulta;
import es.caib.pinbal.core.model.IConsulta;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.stereotype.Component;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.codec.Base64;

import es.caib.pinbal.core.dto.FitxerDto;
import es.caib.pinbal.core.dto.IntegracioAccioTipusEnumDto;
import es.caib.pinbal.core.model.Consulta;
import es.caib.pinbal.core.model.Consulta.JustificantEstat;
import es.caib.pinbal.core.model.Procediment;
import es.caib.pinbal.core.model.ServeiConfig;
import es.caib.pinbal.core.model.ServeiConfig.JustificantTipus;
import es.caib.pinbal.core.model.ServeiJustificantCamp;
import es.caib.pinbal.core.repository.ServeiConfigRepository;
import es.caib.pinbal.core.repository.ServeiJustificantCampRepository;
import es.caib.pinbal.core.service.ServeiServiceImpl;
import es.caib.pinbal.plugins.FirmaServidorPlugin.TipusFirma;
import es.caib.pinbal.scsp.JustificantArbreHelper.ElementArbre;
import es.caib.pinbal.scsp.ResultatEnviamentPeticio;
import es.caib.pinbal.scsp.ScspHelper;
import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.ContingutOrigen;
import es.caib.plugins.arxiu.api.DocumentEstatElaboracio;
import freemarker.core.Environment;
import freemarker.core.NonStringException;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateModelException;
import net.sf.jooreports.templates.DocumentTemplate;
import net.sf.jooreports.templates.DocumentTemplateException;
import net.sf.jooreports.templates.DocumentTemplateFactory;

/**
 * Helper per a generar el justificant.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class JustificantHelper implements MessageSourceAware {

	private static final String PLANTILLA_ODT_RESOURCE = "/es/caib/pinbal/core/template/justificant.odt";
	private static final String JUSTIFICANT_EXTENSIO_SORTIDA = "pdf";

	@Autowired
	private ServeiJustificantCampRepository serveiJustificantCampRepository;
	@Autowired
	private ServeiConfigRepository serveiConfigRepository;
	@Autowired
	private ConversioTipusDocumentHelper conversioTipusDocumentHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private IntegracioHelper integracioHelper;
	@Autowired
	private ConfigHelper configHelper;

	private MessageSource messageSource;

	public void generarCustodiarJustificantPendent(
			IConsulta consulta,
			ScspHelper scspHelper) {
		log.debug("[JUSTIFICANT] Generant justificant pendent (consultaPeticioId=" + consulta.getScspPeticionId() + ", consultaSolicitudId=" + consulta.getScspSolicitudId() + ")");
		ResultatEnviamentPeticio resultat = getResultatEnviamentPeticio(consulta, scspHelper);
		if (resultat != null) {
			log.debug("[JUSTIFICANT] Obtinguda resposta de la petició");
			String serveiCodi = consulta.getProcedimentServei().getServei();
			ServeiConfig serveiConfig = serveiConfigRepository.findByServei(serveiCodi);
			String arxiuNom = conversioTipusDocumentHelper.nomArxiuConvertit(
					getNomArxiuGenerat(
							consulta.getScspPeticionId(),
							consulta.getScspSolicitudId()),
							JUSTIFICANT_EXTENSIO_SORTIDA);
			log.debug("[JUSTIFICANT] Nom arxiu: {}", arxiuNom);
			// Només signa i custòdia si està activat per paràmetre i 
			// si encara no està custodiat
			if (isSignarICustodiarJustificant() && !consulta.isCustodiat()) {
				log.debug("[JUSTIFICANT] Custodiant justificant");
				JustificantEstat justificantEstat = JustificantEstat.PENDENT;
				boolean custodiat = false;
				String custodiaId = null;
				String custodiaUrl = consulta.getCustodiaUrl();
				String justificantError = null;
				String arxiuExpedientUuid = consulta.getArxiuExpedientUuid();
				String arxiuDocumentUuid = consulta.getArxiuDocumentUuid();
				try {
					// Genera el justificant emprant la plantilla
					log.debug("[JUSTIFICANT] Generam justificant amprant plantilla");
					FitxerDto arxiuJustificantGenerat = generar(
							consulta,
							scspHelper);
					log.debug("[JUSTIFICANT] Inici del procés de signatura i custodia del justificant de la consulta");
					if (pluginHelper.isPluginArxiuActiu() && (consulta.getArxiuExpedientUuid() == null || consulta.getArxiuDocumentUuid() == null)) {
						log.debug("[JUSTIFICANT] Es desarà el justificant a l'arxiu");
						// Signa el justificant amb firma de servidor
						FitxerDto justificantFitxer = new FitxerDto();
						justificantFitxer.setNom(arxiuJustificantGenerat.getNom());
						justificantFitxer.setContentType("application/pdf");
						justificantFitxer.setContingut(arxiuJustificantGenerat.getContingut());
						byte[] justificantFirmat = pluginHelper.firmaServidorFirmar(
								justificantFitxer,
								TipusFirma.PADES,
								"Firma justificant PINBAL",
								"ca");
						log.debug("Firmat justificant de la consulta (consultaPeticioId=" + consulta.getScspPeticionId() + ", consultaSolicitudId=" + consulta.getScspSolicitudId() + ")");
						// Guarda el justificant a dins l'expedient
						Procediment procediment = consulta.getProcedimentServei().getProcediment();
						String serieDocumental = getJustificantSerieDocumental();
						if (consulta.getArxiuExpedientUuid() == null) {
							// Consulta a veure si l'expedient ja està creat
							ContingutArxiu expedientExistent = pluginHelper.arxiuExpedientCercarAmbNom(consulta.getScspPeticionId());
							if (expedientExistent != null) {
								arxiuExpedientUuid = expedientExistent.getIdentificador();
							} else {
								// Crea l'expedient
								arxiuExpedientUuid = pluginHelper.arxiuExpedientCrear(
										consulta.getScspPeticionId(),
										consulta.getTitularDocumentNum(),
										procediment.getOrganGestor().getCodi(),
										procediment.getCodiSia(),
										procediment.getCodi(),
										serieDocumental);
								log.info(
										"Creat nou expedient a l'arxiu relacionat amb la consulta (" +
										"id=" + consulta.getId() + ", " +
										"scspPeticionId=" + consulta.getScspPeticionId() + ", " +
										"scspSolicitudId=" + consulta.getScspSolicitudId() + ", " +
										"arxiuExpedientUuid=" + arxiuExpedientUuid + ")");
							}
						}
						justificantFitxer.setContingut(justificantFirmat);
						arxiuDocumentUuid = pluginHelper.arxiuDocumentGuardarFirmaPades(
								arxiuExpedientUuid,
								consulta.getScspSolicitudId(),
								procediment.getOrganGestor().getCodi(),
								serieDocumental,
								justificantFitxer,
								ContingutOrigen.ADMINISTRACIO,
								DocumentEstatElaboracio.ORIGINAL,
								es.caib.plugins.arxiu.api.DocumentTipus.CERTIFICAT);
						log.info(
								"Guardat justificant a l'arxiu relacionat amb la consulta (" +
								"id=" + consulta.getId() + ", " +
								"scspPeticionId=" + consulta.getScspPeticionId() + ", " +
								"scspSolicitudId=" + consulta.getScspSolicitudId() + ", " +
								"arxiuExpedientUuid=" + arxiuExpedientUuid + ", " +
								"arxiuDocumentUuid=" + arxiuDocumentUuid + ")");
					} else {
						log.debug("[JUSTIFICANT] Es desarà el justificant a custòdia");
						// Reserva l'id de custòdia i genera la URL
						String documentTipus = null;
						if (serveiConfig != null) {
							documentTipus = serveiConfig.getCustodiaCodi();
						}
						custodiaId = custodiaObtenirId(consulta);
						if (custodiaUrl == null || custodiaUrl.isEmpty()) {
							// Obté la URL de comprovació de signatura
							log.debug("[JUSTIFICANT] Sol·licitud de URL per a la custòdia del justificant de la consulta");
							custodiaUrl = pluginHelper.custodiaObtenirUrlVerificacioDocument(custodiaId);
							log.debug("[JUSTIFICANT] Obtinguda URL per a la custòdia del justificant de la consulta (custodiaUrl=" + custodiaUrl + ")");
						}
						byte[] justificantFirmat;
						if (pluginHelper.isPluginFirmaServidorActiu()) {
							// Signa el justificant amb firma de servidor
							FitxerDto justificantFitxer = new FitxerDto();
							justificantFitxer.setNom(arxiuJustificantGenerat.getNom());
							justificantFitxer.setContentType("application/pdf");
							justificantFitxer.setContingut(arxiuJustificantGenerat.getContingut());
							justificantFirmat = pluginHelper.firmaServidorFirmar(
									justificantFitxer,
									TipusFirma.PADES,
									"Firma justificant PINBAL",
									"ca");
							log.debug("Firmat justificant de la consulta (consultaPeticioId=" + consulta.getScspPeticionId() + ", consultaSolicitudId=" + consulta.getScspSolicitudId() + ")");
						} else {
							// Signa el justificant amb IBKey
							log.debug("Signatura amb IBKey del justificant de la consulta (id=" + consulta.getId() + ", consultaPeticioId=" + consulta.getScspPeticionId() + ", consultaSolicitudId=" + consulta.getScspSolicitudId() + ")");
							ByteArrayOutputStream signedStream = new ByteArrayOutputStream();
							pluginHelper.signaturaIbkeySignarEstamparPdf(
									new ByteArrayInputStream(arxiuJustificantGenerat.getContingut()),
									signedStream,
									custodiaUrl);
							justificantFirmat = signedStream.toByteArray();
						}
						// Envia el justificant a custòdia
						log.debug("Enviament a custòdia del justificant de la consulta (id=" + consulta.getId() + ", consultaPeticioId=" + consulta.getScspPeticionId() + ", consultaSolicitudId=" + consulta.getScspSolicitudId() + ")");
						pluginHelper.custodiaEnviarPdfSignat(
								custodiaId,
								arxiuNom,
								justificantFirmat,
								documentTipus);
					}
					justificantEstat = JustificantEstat.OK;
					custodiat = true;
				} catch (Exception ex) {
					log.error("La generació del justificant ha produït errors (" +
							"id=" + consulta.getScspPeticionId() + ", " +
							"scspPeticionId=" + consulta.getScspPeticionId() + ", " +
							"scspSolicitudId=" + consulta.getScspSolicitudId() + ")",
							ex);
					justificantEstat = JustificantEstat.ERROR;
					justificantError = ExceptionUtils.getStackTrace(ex);
				} finally {
					consulta.updateJustificantEstat(
							justificantEstat,
							custodiat,
							custodiaId,
							custodiaUrl,
							justificantError,
							arxiuExpedientUuid,
							arxiuDocumentUuid);
				}
			} else {
				consulta.updateJustificantEstat(
						JustificantEstat.OK_NO_CUSTODIA,
						false,
						null,
						null,
						null,
						null,
						null);
			}
		}
	}

	public FitxerDto descarregarFitxerGenerat(
			IConsulta consulta,
			ScspHelper scspHelper) throws Exception {
		String serveiCodi = consulta.getProcedimentServei().getServei();
		String peticionId = consulta.getScspPeticionId();
		String solicitudId = consulta.getScspSolicitudId();
		ServeiConfig serveiConfig = serveiConfigRepository.findByServei(serveiCodi);
		FitxerDto fitxerDto = new FitxerDto();
		if (serveiConfig.getJustificantTipus() != null && JustificantTipus.ADJUNT_PDF_BASE64.equals(serveiConfig.getJustificantTipus())) {
			log.debug("[JUSTIFICANT] El justificant de la consulta (id=" + consulta.getId() + ", consultaPeticioId=" + consulta.getScspPeticionId() + ", consultaSolicitudId=" + consulta.getScspSolicitudId() + ") està inclòs a dins la resposta");
			Map<String, Object> dadesEspecifiques = scspHelper.getDadesEspecifiquesResposta(
					peticionId,
					solicitudId);
			String justificantB64 = (String)dadesEspecifiques.get(serveiConfig.getJustificantXpath());
			if (justificantB64 != null) {
				fitxerDto.setNom(
						getNomArxiuJustificant(
								consulta.getScspPeticionId(),
								consulta.getScspSolicitudId()));
				fitxerDto.setContingut(Base64.decode(justificantB64));
				return fitxerDto;
			} else {
				// Si no hi ha justificant a dins les dades específiques continuarà i
				// es retornarà el justificant genèric.
			}
		}
		if (JustificantEstat.OK.equals(consulta.getJustificantEstat())) {
			log.debug("[JUSTIFICANT] El justificant de la consulta (id=" + consulta.getId() + ", consultaPeticioId=" + consulta.getScspPeticionId() + ", consultaSolicitudId=" + consulta.getScspSolicitudId() + ") està en estat OK");
			fitxerDto.setNom(
					getNomArxiuJustificant(
							consulta.getScspPeticionId(),
							consulta.getScspSolicitudId()));
			if (consulta.getArxiuDocumentUuid() != null) {
				es.caib.plugins.arxiu.api.Document documentArxiu = pluginHelper.arxiuDocumentConsultar(
						consulta.getArxiuDocumentUuid(),
						null,
						false,
						true);
				fitxerDto.setContingut(documentArxiu.getContingut().getContingut());
			} else {
				fitxerDto.setContingut(
						pluginHelper.custodiaObtenirDocument(custodiaObtenirId(consulta)));
			}
		} else if (JustificantEstat.OK_NO_CUSTODIA.equals(consulta.getJustificantEstat())) {
			log.debug("[JUSTIFICANT] El justificant de la consulta (id=" + consulta.getId() + ", consultaPeticioId=" + consulta.getScspPeticionId() + ", consultaSolicitudId=" + consulta.getScspSolicitudId() + ") està en estat OK_NO_CUSTODIA");
			FitxerDto arxiuGenerat = generar(consulta, scspHelper);
			fitxerDto.setNom(arxiuGenerat.getNom());
			fitxerDto.setContingut(arxiuGenerat.getContingut());
		}
		return fitxerDto;
	}

	public FitxerDto generar(
			IConsulta consulta,
			ScspHelper scspHelper) throws IOException, DocumentTemplateException, ParserConfigurationException, DocumentException {
		log.debug("[JUSTIFICANT] Es generarà el justificant de la consulta (id=" + consulta.getId() + ", consultaPeticioId=" + consulta.getScspPeticionId() + ", consultaSolicitudId=" + consulta.getScspSolicitudId() + ")");
		String arxiuNom = getNomArxiuGenerat(
				consulta.getScspPeticionId(),
				consulta.getScspSolicitudId());
		String arxiuExtensio = getExtensioArxiu(arxiuNom);
		String serveiCodi = consulta.getProcedimentServei().getServei();
		String extensioSortida = getExtensioSortida();
		boolean convertir = !extensioSortida.equalsIgnoreCase(arxiuExtensio);
		ByteArrayOutputStream baosGeneracio = new ByteArrayOutputStream();
		log.debug("Generant el justificant per a la consulta a partir de la plantilla");
		
		String accioDescripcio = "Generant el justificant per a la consulta";
		Map<String, String> accioParams = new HashMap<>();
		accioParams.put("consultaPeticioId", consulta.getScspPeticionId());
		accioParams.put("consultaSolicitudId", consulta.getScspSolicitudId());
		long t0 = System.currentTimeMillis();
		try {
		
			generarAmbPlantillaFreemarker(
					scspHelper.generarArbreJustificant(
							consulta.getScspPeticionId(),
							consulta.getScspSolicitudId(),
							null),
					"[" + serveiCodi + "] " + scspHelper.getServicioDescripcion(serveiCodi),
					serveiJustificantCampRepository.findByServeiAndLocaleIdiomaAndLocaleRegio(
							serveiCodi,
							ServeiServiceImpl.DEFAULT_TRADUCCIO_LOCALE.getLanguage(),
							ServeiServiceImpl.DEFAULT_TRADUCCIO_LOCALE.getCountry()),
					null,
					baosGeneracio);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_SERVEIS_SCSP,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			
		} catch (Exception ex) {

			log.error("[JUSTIFICANT] S'ha produït un error al generar el justificant.", ex);
			String errorDescripcio = "Error generant el justificant per a la consulta";
			
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_SERVEIS_SCSP,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			
			throw ex;
		}
		
		
		FitxerDto fitxerDto = new FitxerDto();
		fitxerDto.setNom(
				getNomArxiuJustificant(
								consulta.getScspPeticionId(),
								consulta.getScspSolicitudId()));
		// Converteix el document si és necessari
		if (convertir) {
			log.debug("[JUSTIFICANT] Convertint el justificant per a la consulta (" +
					"consultaPeticioId=" + consulta.getScspPeticionId() + ", " +
					"consultaSolicitudId=" + consulta.getScspSolicitudId() + ", " +
					"extensio=" + extensioSortida + ")");
			ByteArrayOutputStream baosConversio = new ByteArrayOutputStream();
			conversioTipusDocumentHelper.convertir(
					arxiuNom,
					new ByteArrayInputStream(baosGeneracio.toByteArray()),
					extensioSortida,
					baosConversio);
			boolean convertirPdfa = isConvertirPdfaJustificant() && "pdf".equalsIgnoreCase(extensioSortida);
			if (convertirPdfa) {
				log.debug("[JUSTIFICANT] Convertint justificant a PDFA");
				ByteArrayOutputStream baosPdfa = new ByteArrayOutputStream();
				conversioTipusDocumentHelper.convertirPdfToPdfa(
						new ByteArrayInputStream(baosConversio.toByteArray()),
						baosPdfa);
				fitxerDto.setContingut(baosPdfa.toByteArray());
			} else {
				fitxerDto.setContingut(baosConversio.toByteArray());
			}
		} else {
			fitxerDto.setContingut(baosGeneracio.toByteArray());
		}
		log.debug("[JUSTIFICANT] Justificant generat.");
		return fitxerDto;
	}

	public void generarAmbPlantillaFreemarker(
			ElementArbre arbre,
			String serveiDescripcio,
			List<ServeiJustificantCamp> traduccions,
			Locale locale,
			OutputStream out) throws IOException, DocumentTemplateException {
		DocumentTemplateFactory documentTemplateFactory = new DocumentTemplateFactory();
		documentTemplateFactory.getFreemarkerConfiguration().setTemplateExceptionHandler(new TemplateExceptionHandler() {
			public void handleTemplateException(TemplateException te, Environment env, Writer out) throws TemplateException {
				try {
					if (te instanceof TemplateModelException || te instanceof NonStringException) {
						out.write("[exception]");
					} else {
						out.write("[???]");
					}
					out.write("<office:annotation><dc:creator>Pinbal</dc:creator><dc:date>" + new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date()) + "</dc:date><text:p><![CDATA[" + te.getFTLInstructionStack() + "\n" + te.getMessage() + "]]></text:p></office:annotation>");
				} catch (IOException e) {
					throw new TemplateException("Failed to print error message. Cause: " + e, env);
				}
			}
		});
		documentTemplateFactory.getFreemarkerConfiguration().setLocale(
				(locale != null) ? locale : new Locale("ca", "ES"));
		DocumentTemplate template = documentTemplateFactory.getTemplate(
				getClass().getResourceAsStream(PLANTILLA_ODT_RESOURCE));
		template.createDocument(
				generarModel(
						arbre,
						serveiDescripcio,
						traduccions,
						locale),
				out);
	}

	/*public void imprimirAmbPlantillaProva(ElementArbre arbre) throws Exception {
		Configuration cfg = new Configuration();
		cfg.setTemplateLoader(
				new FileTemplateLoader(new File(PLANTILLA_PATH)));  
		cfg.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
		Template tpl = cfg.getTemplate("justificant.ftl");
		OutputStreamWriter output = new OutputStreamWriter(System.out);
		tpl.process(generarModel(arbre), output);
	}*/

	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}



	private ResultatEnviamentPeticio getResultatEnviamentPeticio(
			IConsulta consulta,
			ScspHelper scspHelper) {
		ResultatEnviamentPeticio resultat = null;
		try {
			resultat =  scspHelper.recuperarResultatEnviamentPeticio(consulta.getScspPeticionId());
		} catch (Exception ex) {
			log.error("No s'ha pogut recuperar la resposta SCSP associada a la consulta (" +
					"id=" + consulta.getScspPeticionId() + ", " +
					"scspPeticionId=" + consulta.getScspPeticionId() + ", " +
					"scspSolicitudId=" + consulta.getScspSolicitudId() + ")",
					ex);
			consulta.updateJustificantEstat(
					JustificantEstat.ERROR,
					false,
					null,
					null,
					"No s'ha pogut recuperar la resposta SCSP associada a la consulta: " + ExceptionUtils.getStackTrace(ex),
					null,
					null);
			return null;
		}
		if (resultat.isError()) {
			log.error("La resposta SCSP associada a la consulta és de tipus error (" +
					"id=" + consulta.getScspPeticionId() + ", " +
					"scspPeticionId=" + consulta.getScspPeticionId() + ", " +
					"scspSolicitudId=" + consulta.getScspSolicitudId() + "): " + resultat.getErrorDescripcio());
			consulta.updateJustificantEstat(
					JustificantEstat.ERROR,
					false,
					null,
					null,
					"La resposta SCSP associada a la consulta és de tipus error: " + resultat.getErrorDescripcio(),
					null,
					null);
			return null;
		}
		return resultat;
	}

	private Map<String, Object> generarModel(
			ElementArbre arbre,
			String serveiDescripcio,
			List<ServeiJustificantCamp> traduccions,
			Locale locale) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put(
				"text_titol_capsalera",
				messageSource.getMessage(
						"justificant.plantilla.titol.capsalera",
						null,
						locale));
		model.put(
				"text_titol_servei",
				serveiDescripcio);
		model.put(
				"text_titol_limitacio",
				messageSource.getMessage(
						"justificant.plantilla.titol.limitacio",
						null,
						locale));
		model.put(
				"text_legal",
				messageSource.getMessage(
						"justificant.plantilla.text.legal",
						null,
						locale));
		model.put(
				"text_data_eldia",
				messageSource.getMessage(
						"justificant.plantilla.data.eldia",
						null,
						locale));
		Date ara = new Date();
		model.put(
				"text_data_data",
				formatDate(ara, locale));
		model.put(
				"text_data_ales",
				messageSource.getMessage(
						"justificant.plantilla.data.ales",
						null,
						locale));
		model.put(
				"text_data_hora",
				(locale == null) ? 
						DateFormat.getTimeInstance(DateFormat.SHORT).format(ara) : 
						DateFormat.getTimeInstance(DateFormat.SHORT, locale).format(ara));
		List<NodeInfo> nodes = new ArrayList<NodeInfo>();
		convertirArbreEnLlista(arbre, 0, nodes);
		if (traduccions != null) {
			for (NodeInfo node: nodes) {
				for (ServeiJustificantCamp traduccio: traduccions) {
					if (traduccio.getXpath().equals(node.getXpathDadaEspecifica())) {
						node.setTitol(traduccio.getTraduccio());
						break;
					}
				}
			}
		}
		model.put("nodes", nodes);
		return model;
	}
	private void convertirArbreEnLlista(ElementArbre element, int nivell, List<NodeInfo> nodes) {
		if (nivell > 0) {
			nodes.add(
					new NodeInfo(
							nivell - 1,
							element.getTitol(),
							element.getDescripcio(),
							element.getXpathDatoEspecifico()));
		}
		if (element.teFills()) {
			for (ElementArbre fill: element.getFills())
				convertirArbreEnLlista(
						fill, nivell + 1, nodes);
		}
	}

	private String formatDate(
			Date date,
			Locale locale) {
		if (locale == null || locale.getLanguage().equalsIgnoreCase("ca")) {
			String data = DateFormat.getDateInstance(DateFormat.LONG, new Locale("ca", "ES")).format(date);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			int month = cal.get(Calendar.MONTH);
			if (month == Calendar.APRIL || month == Calendar.AUGUST || month == Calendar.OCTOBER) {
				return data.replaceFirst("/ ", "d'").replace("/", "de");
			} else {
				return data.replace("/", "de");
			}
		} else {
			return DateFormat.getDateInstance(DateFormat.LONG, locale).format(date);
		}
	}

	private String getNomArxiuGenerat(
			String peticionId,
			String solicitudId) {
		if (solicitudId != null)
			return "PBL_" + peticionId + "_" + solicitudId + ".odt";
		else
			return "PBL_" + peticionId + ".odt";
	}

	private String getExtensioArxiu(
			String arxiuNom) {
		if (arxiuNom.indexOf(".") == -1)
			return null;
		return arxiuNom.substring(arxiuNom.lastIndexOf(".") + 1);
	}

	private String custodiaObtenirId(
			IConsulta consulta) {
		if (consulta.getCustodiaId() != null) {
			return consulta.getCustodiaId();
		} else {
			if (consulta.isCustodiat()) {
				return consulta.getScspPeticionId();
			} else {
				return consulta.getScspPeticionId() + "#" + consulta.getScspSolicitudId();
			}
		}
	}

	private String getNomArxiuJustificant(
			String peticionId,
			String solicitudId) {
		return conversioTipusDocumentHelper.nomArxiuConvertit(
				getNomArxiuGenerat(peticionId, solicitudId),
				getExtensioSortida());
	}

	private String getExtensioSortida() {
		return configHelper.getConfig("es.caib.pinbal.justificant.extensio.sortida", "pdf");
	}
	private boolean isConvertirPdfaJustificant() {
		return configHelper.getAsBoolean("es.caib.pinbal.justificant.convertir.pdfa", false);
	}
	private boolean isSignarICustodiarJustificant() {
		return configHelper.getAsBoolean("es.caib.pinbal.justificant.signar.i.custodiar", false);
	}
	private String getJustificantSerieDocumental() {
		return configHelper.getConfig("es.caib.pinbal.justificant.serie.documental");
	}

	public class NodeInfo {
		private int nivell;
		private String xpathDadaEspecifica;
		private String titol;
		private String descripcio;
		public NodeInfo(int nivell, String titol, String descripcio, String xpathDadaEspecifica) {
			super();
			this.nivell = nivell;
			this.xpathDadaEspecifica = xpathDadaEspecifica;
			this.titol = titol;
			this.descripcio = descripcio;
		}
		public String getXpathDadaEspecifica() {
			return xpathDadaEspecifica;
		}
		public void setXpathDadaEspecifica(String xpathDadaEspecifica) {
			this.xpathDadaEspecifica = xpathDadaEspecifica;
		}
		public int getNivell() {
			return nivell;
		}
		public void setNivell(int nivell) {
			this.nivell = nivell;
		}
		public String getTitol() {
			return titol;
		}
		public void setTitol(String titol) {
			this.titol = titol;
		}
		public String getDescripcio() {
			return descripcio;
		}
		public void setDescripcio(String descripcio) {
			this.descripcio = descripcio;
		}
		public boolean isFinal() {
			return (descripcio != null);
		}
	}

}
