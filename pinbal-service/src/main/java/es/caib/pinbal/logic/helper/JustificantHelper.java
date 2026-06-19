/**
 * 
 */
package es.caib.pinbal.logic.helper;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import es.caib.pinbal.logic.intf.dto.FitxerDto;
import es.caib.pinbal.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.pinbal.logic.intf.dto.JustificantEstat;
import es.caib.pinbal.logic.intf.service.exception.JustificantGeneracioException;
import es.caib.pinbal.logic.service.ServeiServiceImpl;
import es.caib.pinbal.persist.entity.Consulta;
import es.caib.pinbal.persist.entity.HistoricConsulta;
import es.caib.pinbal.persist.entity.IConsulta;
import es.caib.pinbal.persist.entity.Procediment;
import es.caib.pinbal.persist.entity.ProcedimentServei;
import es.caib.pinbal.persist.entity.ServeiConfig;
import es.caib.pinbal.persist.entity.ServeiJustificantCamp;
import es.caib.pinbal.persist.repository.ConsultaRepository;
import es.caib.pinbal.persist.repository.HistoricConsultaRepository;
import es.caib.pinbal.persist.repository.ServeiConfigRepository;
import es.caib.pinbal.persist.repository.ServeiJustificantCampRepository;
import es.caib.pinbal.plugin.firmaservidor.FirmaServidorPlugin.TipusFirma;
import es.caib.pinbal.plugin.firmaservidor.SignaturaResposta;
import es.caib.pinbal.scsp.JustificantArbreHelper.ElementArbre;
import es.caib.pinbal.scsp.ResultatEnviamentPeticio;
import es.caib.pinbal.scsp.ScspHelper;
import es.caib.pluginsib.arxiu.api.ContingutArxiu;
import es.caib.pluginsib.arxiu.api.ContingutOrigen;
import es.caib.pluginsib.arxiu.api.DocumentEstatElaboracio;
import es.scsp.common.exceptions.ScspException;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import fr.opensagres.xdocreport.template.freemarker.FreemarkerTemplateEngine;
import freemarker.core.Environment;
import freemarker.core.NonStringException;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateModelException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.stereotype.Component;

import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Helper per a generar el justificant.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class JustificantHelper implements MessageSourceAware {

	private static final String PLANTILLA_ODT_RESOURCE = "/es/caib/pinbal/logic/template/justificant.odt";
	private static final String JUSTIFICANT_EXTENSIO_SORTIDA = "pdf";

	private final ServeiJustificantCampRepository serveiJustificantCampRepository;
	private final ServeiConfigRepository serveiConfigRepository;
	private final ConversioTipusDocumentHelper conversioTipusDocumentHelper;
	private final PluginHelper pluginHelper;
	private final IntegracioHelper integracioHelper;
	private final ConfigHelper configHelper;
	private final ConsultaRepository consultaRepository;
	private final HistoricConsultaRepository historicConsultaRepository;

	private MessageSource messageSource;
    private final ConcurrentMap<String, Object> expedientLocks = new ConcurrentHashMap<String, Object>();

	public void generarCustodiarJustificantPendent(
			IConsulta consulta,
			ScspHelper scspHelper) {
		log.debug("[JUSTIFICANT] Generant justificant pendent (consultaPeticioId=" + consulta.getScspPeticionId() + ", consultaSolicitudId=" + consulta.getScspSolicitudId() + ")");
        long startTime = System.currentTimeMillis();
        try {
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
						if (consulta.isAplicacioGuardaJustificantArxiu()) {
							// El justificant no es desa ni a l'arxiu ni a custòdia: es genera i
							// firma sota demanda quan es consulta, per tant no cal generar-lo ni
							// firmar-lo ara (estalviem la generació del PDF i la crida de firma
							// de servidor).
							log.debug("[JUSTIFICANT] El justificant es generarà sota demanda; no es desa ni a arxiu ni a custòdia");
						} else {
							// Genera el justificant emprant la plantilla
							log.debug("[JUSTIFICANT] Generam justificant amprant plantilla");
							FitxerDto arxiuJustificantGenerat = generar(
									consulta,
									scspHelper);
							log.debug("[JUSTIFICANT] Inici del procés de signatura i custodia del justificant de la consulta");
							if (!pluginHelper.isPluginArxiuActiu()) {
								throw new JustificantGeneracioException("Plugin arxiu no activat");
							}

							if (consulta.getArxiuExpedientUuid() == null || consulta.getArxiuDocumentUuid() == null) {
								log.debug("[JUSTIFICANT] Es desarà el justificant a l'arxiu");
								// Signa el justificant amb firma de servidor
								FitxerDto justificantFitxer = new FitxerDto();
								justificantFitxer.setNom(arxiuJustificantGenerat.getNom());
								justificantFitxer.setContentType("application/pdf");
								justificantFitxer.setContingut(arxiuJustificantGenerat.getContingut());
								SignaturaResposta justificantFirmat = pluginHelper.firmaServidorFirmar(
										justificantFitxer,
										TipusFirma.PADES,
										"Firma justificant PINBAL",
										"ca",
										consulta.getScspPeticionId());
								log.debug("Firmat justificant de la consulta (consultaPeticioId=" + consulta.getScspPeticionId() + ", consultaSolicitudId=" + consulta.getScspSolicitudId() + ")");
								// Guarda el justificant a dins l'expedient
								Procediment procediment = consulta.getProcedimentServei().getProcediment();
								String serieDocumental = getJustificantSerieDocumental();
								if (consulta.getArxiuExpedientUuid() == null) {
									arxiuExpedientUuid = obtenirOCrearExpedientArxiu(consulta, procediment, serieDocumental);
									propagarArxiuExpedientUuidCompartit(consulta, arxiuExpedientUuid);
								}
								if (arxiuExpedientUuid != null) {
									justificantFitxer.setContingut(justificantFirmat.getContingut());
									arxiuDocumentUuid = pluginHelper.arxiuDocumentGuardarFirmaPades(
											consulta.getScspPeticionId(),
											arxiuExpedientUuid,
											consulta.getScspSolicitudId(),
											procediment.getOrganGestor().getCodi(),
											serieDocumental,
											justificantFitxer,
											ContingutOrigen.ADMINISTRACIO,
											DocumentEstatElaboracio.ORIGINAL,
											es.caib.pluginsib.arxiu.api.DocumentTipus.CERTIFICAT);
									log.info(
											"Guardat justificant a l'arxiu relacionat amb la consulta (" +
													"id=" + consulta.getId() + ", " +
													"scspPeticionId=" + consulta.getScspPeticionId() + ", " +
													"scspSolicitudId=" + consulta.getScspSolicitudId() + ", " +
													"arxiuExpedientUuid=" + arxiuExpedientUuid + ", " +
													"arxiuDocumentUuid=" + arxiuDocumentUuid + ")");
								}
							}
						}
                        justificantEstat = JustificantEstat.OK;
                        custodiat = true;
                        SubsistemaMetricHelper.addSuccessOperation("JUS", System.currentTimeMillis() - startTime);
                    } catch (Exception ex) {
                        log.error("La generació del justificant ha produït errors (" +
                                        "id=" + consulta.getScspPeticionId() + ", " +
                                        "scspPeticionId=" + consulta.getScspPeticionId() + ", " +
                                        "scspSolicitudId=" + consulta.getScspSolicitudId() + ")",
                                ex);
                        justificantEstat = JustificantEstat.ERROR;
                        justificantError = ExceptionUtils.getStackTrace(ex);
                        SubsistemaMetricHelper.addErrorOperation("JUS");
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
        } catch (Exception e) {
            SubsistemaMetricHelper.addErrorOperation("JUS");
            throw e;
        }
	}

    private String obtenirOCrearExpedientArxiu(
            IConsulta consulta,
            Procediment procediment,
            String serieDocumental) throws Exception {
        String scspPeticionId = consulta.getScspPeticionId();
        Object expedientLock = getExpedientLock(scspPeticionId);
        try {
            synchronized (expedientLock) {
                ContingutArxiu expedientExistent = pluginHelper.arxiuExpedientCercarAmbNom(scspPeticionId);
                if (expedientExistent != null) {
                    return expedientExistent.getIdentificador();
                }
                try {
                    String arxiuExpedientUuid = pluginHelper.arxiuExpedientCrear(
                            scspPeticionId,
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
                    return arxiuExpedientUuid;
                } catch (Exception ex) {
                    if (isDuplicateExpedientException(ex)) {
                        ContingutArxiu expedientCreatConcurrentment = pluginHelper.arxiuExpedientCercarAmbNom(scspPeticionId);
                        if (expedientCreatConcurrentment != null) {
                            log.warn(
                                    "Detectada creació concurrent de l'expedient a l'arxiu; es reutilitza l'existent (" +
                                            "id=" + consulta.getId() + ", " +
                                            "scspPeticionId=" + consulta.getScspPeticionId() + ", " +
                                            "scspSolicitudId=" + consulta.getScspSolicitudId() + ", " +
                                            "arxiuExpedientUuid=" + expedientCreatConcurrentment.getIdentificador() + ")",
                                    ex);
                            return expedientCreatConcurrentment.getIdentificador();
                        }
                    }
                    throw ex;
                }
            }
        } finally {
            releaseExpedientLock(scspPeticionId, expedientLock);
        }
    }

    private boolean isDuplicateExpedientException(Throwable ex) {
        String stackTrace = ExceptionUtils.getStackTrace(ex);
        return stackTrace != null &&
                (stackTrace.contains("Duplicate child name not allowed") || stackTrace.contains("ORA-00001"));
    }

    private void propagarArxiuExpedientUuidCompartit(
            IConsulta consulta,
            String arxiuExpedientUuid) {
        if (arxiuExpedientUuid == null) {
            return;
        }
        // Les consultes recuperades aquí ja són gestionades (managed) per la transacció actual,
        // de manera que només cal mutar-les: el flush de la transacció persistirà els canvis.
        // NO s'ha de cridar save()/saveAndFlush() (= em.merge()): el merge d'una Consulta gestionada
        // amb la col·lecció 'fills' (cascade=ALL) fa un CollectionType.replace de tot el graf
        // pare-fills i, quan pare i fills coexisteixen al mateix context, acaba compartint la
        // col·lecció 'fills' entre dues entitats (error "Found shared references to a collection").
        if (consulta instanceof Consulta) {
            List<Consulta> consultes = consultaRepository.findByScspPeticionId(consulta.getScspPeticionId());
            for (Consulta consultaRelacionada : consultes) {
                if (consultaRelacionada.getArxiuExpedientUuid() == null) {
                    consultaRelacionada.updateArxiuExpedientUuid(arxiuExpedientUuid);
                }
            }
        } else if (consulta instanceof HistoricConsulta) {
            List<HistoricConsulta> consultes = historicConsultaRepository.findByScspPeticionId(consulta.getScspPeticionId());
            for (HistoricConsulta consultaRelacionada : consultes) {
                if (consultaRelacionada.getArxiuExpedientUuid() == null) {
                    consultaRelacionada.updateArxiuExpedientUuid(arxiuExpedientUuid);
                }
            }
        }
    }

    private Object getExpedientLock(String scspPeticionId) {
        Object existingLock = expedientLocks.get(scspPeticionId);
        if (existingLock != null) {
            return existingLock;
        }
        Object newLock = new Object();
        Object racedLock = expedientLocks.putIfAbsent(scspPeticionId, newLock);
        return racedLock != null ? racedLock : newLock;
    }

    private void releaseExpedientLock(String scspPeticionId, Object expedientLock) {
        expedientLocks.remove(scspPeticionId, expedientLock);
    }

	public FitxerDto descarregarFitxerGenerat(
			IConsulta consulta,
			ScspHelper scspHelper,
			boolean versioImprimible) throws Exception {
		String serveiCodi = consulta.getProcedimentServei().getServei();
		String peticionId = consulta.getScspPeticionId();
		String solicitudId = consulta.getScspSolicitudId();
		ServeiConfig serveiConfig = serveiConfigRepository.findByServei(serveiCodi);
		FitxerDto fitxerDto = new FitxerDto();
		if (serveiConfig.getJustificantTipus() != null && ServeiConfig.JustificantTipus.ADJUNT_PDF_BASE64.equals(serveiConfig.getJustificantTipus())) {
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
				fitxerDto.setContingut(Base64.getDecoder().decode(justificantB64));
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
				// El justificant ja s'ha generat i desat a l'arxiu: es recupera de l'arxiu.
				es.caib.pluginsib.arxiu.api.Document documentArxiu = pluginHelper.arxiuDocumentConsultar(
						consulta.getScspPeticionId(),
						consulta.getArxiuDocumentUuid(),
						null,
						true,
						versioImprimible);
				fitxerDto.setContingut(documentArxiu.getContingut().getContingut());
			} else if (consulta.isAplicacioGuardaJustificantArxiu()) {
				// El justificant no s'ha desat a l'arxiu. Es genera i firma sota
				// demanda (l'aplicació que el consulta l'espera firmat).
				FitxerDto justificantFirmat = generarIFirmarJustificant(consulta, scspHelper);
				fitxerDto.setNom(justificantFirmat.getNom());
				fitxerDto.setContingut(justificantFirmat.getContingut());
				if (!consulta.isRecobriment()) {
					// CORRECCIÓ d'una errada pròpia: el canvi de valor per defecte
					// introduït a #357 va deixar consultes que NO són de recobriment
					// amb aplicacioGuardaJustificantArxiu=true, de manera que el
					// justificant no es va arribar a desar a l'arxiu. En consultar-les,
					// a més de generar-lo i firmar-lo, el desem a l'arxiu i
					// actualitzem la consulta perquè quedi persistit i les properes
					// consultes funcionin amb normalitat (recuperació de les consultes
					// afectades).
					desarJustificantArxiu(consulta, justificantFirmat);
				}
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

	/**
	 * Genera el justificant a partir de la plantilla i el firma amb firma de
	 * servidor. No el desa enlloc.
	 */
	private FitxerDto generarIFirmarJustificant(
			IConsulta consulta,
			ScspHelper scspHelper) throws Exception {
		FitxerDto justificantGenerat = generar(consulta, scspHelper);
		FitxerDto justificantFitxer = new FitxerDto();
		justificantFitxer.setNom(justificantGenerat.getNom());
		justificantFitxer.setContentType("application/pdf");
		justificantFitxer.setContingut(justificantGenerat.getContingut());
		SignaturaResposta justificantFirmat = pluginHelper.firmaServidorFirmar(
				justificantFitxer,
				TipusFirma.PADES,
				"Firma justificant PINBAL",
				"ca",
				consulta.getScspPeticionId());
		justificantFitxer.setContingut(justificantFirmat.getContingut());
		return justificantFitxer;
	}

	/**
	 * Desa a l'arxiu un justificant ja generat i firmat, creant l'expedient si
	 * cal, i actualitza els identificadors d'arxiu de la consulta.
	 */
	private void desarJustificantArxiu(
			IConsulta consulta,
			FitxerDto justificantFirmat) throws Exception {
		Procediment procediment = consulta.getProcedimentServei().getProcediment();
		String serieDocumental = getJustificantSerieDocumental();
		String arxiuExpedientUuid = consulta.getArxiuExpedientUuid();
		if (arxiuExpedientUuid == null) {
			arxiuExpedientUuid = obtenirOCrearExpedientArxiu(consulta, procediment, serieDocumental);
			propagarArxiuExpedientUuidCompartit(consulta, arxiuExpedientUuid);
		}
		String arxiuDocumentUuid = pluginHelper.arxiuDocumentGuardarFirmaPades(
				consulta.getScspPeticionId(),
				arxiuExpedientUuid,
				consulta.getScspSolicitudId(),
				procediment.getOrganGestor().getCodi(),
				serieDocumental,
				justificantFirmat,
				ContingutOrigen.ADMINISTRACIO,
				DocumentEstatElaboracio.ORIGINAL,
				es.caib.pluginsib.arxiu.api.DocumentTipus.CERTIFICAT);
		consulta.updateArxiuExpedientUuid(arxiuExpedientUuid);
		consulta.updateArxiuDocumentUuid(arxiuDocumentUuid);
		log.info(
				"Guardat justificant a l'arxiu en consultar-lo (correcció #357) (" +
						"id=" + consulta.getId() + ", " +
						"scspPeticionId=" + consulta.getScspPeticionId() + ", " +
						"scspSolicitudId=" + consulta.getScspSolicitudId() + ", " +
						"arxiuExpedientUuid=" + arxiuExpedientUuid + ", " +
						"arxiuDocumentUuid=" + arxiuDocumentUuid + ")");
	}

	public FitxerDto generar(
			IConsulta consulta,
			ScspHelper scspHelper) throws IOException, XDocReportException, ParserConfigurationException, DocumentException, ScspException {
		log.debug("[JUSTIFICANT] Es generarà el justificant de la consulta (id=" + consulta.getId() + ", consultaPeticioId=" + consulta.getScspPeticionId() + ", consultaSolicitudId=" + consulta.getScspSolicitudId() + ")");
		String arxiuNom = getNomArxiuGenerat(
				consulta.getScspPeticionId(),
				consulta.getScspSolicitudId());
		String arxiuExtensio = getExtensioArxiu(arxiuNom);
		ProcedimentServei procedimentServei = consulta.getProcedimentServei();
		String serveiCodi = procedimentServei.getServei();
		String extensioSortida = getExtensioSortida();
		boolean convertir = !extensioSortida.equalsIgnoreCase(arxiuExtensio);
		ByteArrayOutputStream baosGeneracio = new ByteArrayOutputStream();
		log.debug("Generant el justificant per a la consulta a partir de la plantilla");
		
		String accioDescripcio = "Generant el justificant per a la consulta";
		Map<String, String> accioParams = new HashMap<>();
		accioParams.put("consultaPeticioId", consulta.getScspPeticionId());
		accioParams.put("consultaSolicitudId", consulta.getScspSolicitudId());
		accioParams.put("procediment", procedimentServei.getProcediment() != null ? procedimentServei.getProcediment().getCodi() + " - " + procedimentServei.getProcediment().getNom() : "");
		accioParams.put("servei", procedimentServei.getServeiScsp() != null ? procedimentServei.getServeiScsp().getCodi() + " - " + procedimentServei.getServeiScsp().getDescripcio() : "");
		long t0 = System.currentTimeMillis();
		String idPeticio = consulta.getScspPeticionId();
		List<NodeInfo> nodesTipusDocument = null;
		try {
			nodesTipusDocument = generarAmbPlantillaFreemarker(
					scspHelper.generarArbreJustificant(idPeticio, consulta.getScspSolicitudId(), null),
					"[" + serveiCodi + "] " + scspHelper.getServicioDescripcion(serveiCodi),
					serveiJustificantCampRepository.findByServeiAndLocaleIdiomaAndLocaleRegio(
							serveiCodi,
							ServeiServiceImpl.DEFAULT_TRADUCCIO_LOCALE.getLanguage(),
							ServeiServiceImpl.DEFAULT_TRADUCCIO_LOCALE.getCountry()),
					null,
					baosGeneracio);
			integracioHelper.addAccioOk(
					idPeticio,
					IntegracioHelper.INTCODI_SERVEIS_SCSP,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			
		} catch (Exception ex) {

			log.error("[JUSTIFICANT] S'ha produït un error al generar el justificant.", ex);
			String errorDescripcio = "Error generant el justificant per a la consulta";
			
			integracioHelper.addAccioError(
					idPeticio,
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
			boolean mergeDocuments = nodesTipusDocument != null && !nodesTipusDocument.isEmpty();
			if ("pdf".equalsIgnoreCase(extensioSortida) && mergeDocuments) {
				log.info("[JUSTIFICANT] Generant justificant amb documents adjunts");
				baosConversio = mergePdfFiles(baosConversio.toByteArray(), nodesTipusDocument);
			}
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

	private ByteArrayOutputStream mergePdfFiles(byte[] justifiant, List<NodeInfo> nodesTipusDocument) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Document document = new Document();
		try {
			PdfCopy copy = new PdfCopy(document, outputStream);
			document.open();
			// El primer és el justificant
			PdfReader justificantReader = new PdfReader(justifiant);
			for (int page = 1; page <= justificantReader.getNumberOfPages(); page++) {
				copy.addPage(copy.getImportedPage(justificantReader, page));
			}
			copy.freeReader(justificantReader);

			for (NodeInfo nodeTipusDocument : nodesTipusDocument) {
				PdfReader reader = new PdfReader(getPdfAmbTitol(nodeTipusDocument));
				int n = reader.getNumberOfPages();
				for (int page = 1; page <= n; page++) {
					copy.addPage(copy.getImportedPage(reader, page));
				}
				copy.freeReader(reader);
				reader.close();
			}
			document.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return outputStream;
	}

	private byte[] getPdfAmbTitol(NodeInfo nodeTipusDocument) {
		byte[] documentResposta = org.apache.commons.codec.binary.Base64.decodeBase64(nodeTipusDocument.getDescripcio());

		// Afegim el títol a la primera pàgina
		ByteArrayOutputStream adjuntOutputStream = new ByteArrayOutputStream();
		PdfReader reader = null;
		PdfStamper stamper = null;

		try {
			reader = new PdfReader(documentResposta);
			stamper = new PdfStamper(reader, adjuntOutputStream);

			PdfContentByte over = stamper.getOverContent(1);

			Font f = new Font(Font.HELVETICA, 11, Font.BOLD, new Color(195, 0, 69));
			Phrase p = new Phrase(nodeTipusDocument.getTitol(), f);
			over.saveState();
			PdfGState gs1 = new PdfGState();
			gs1.setFillOpacity(0.5f);
			over.setGState(gs1);
			ColumnText.showTextAligned(over, Element.ALIGN_CENTER, p, 297, 800, 0);
			over.restoreState();
			stamper.close();
			reader.close();

		} catch (Exception ex) {
			log.error("Error afegint titol al PDF", ex);
			return documentResposta;
		}

		return adjuntOutputStream.toByteArray();
	}

	public List<NodeInfo> generarAmbPlantillaFreemarker(
			ElementArbre arbre,
			String serveiDescripcio,
			List<ServeiJustificantCamp> traduccions,
			Locale locale,
			OutputStream out) throws IOException, XDocReportException {
		InputStream plantilla = getClass().getResourceAsStream(PLANTILLA_ODT_RESOURCE);
		if (plantilla == null) {
			throw new FileNotFoundException("No s'ha trobat la plantilla del justificant: " + PLANTILLA_ODT_RESOURCE);
		}
		try (InputStream plantillaStream = plantilla) {
			IXDocReport report = XDocReportRegistry.getRegistry().loadReport(
					plantillaStream,
					TemplateEngineKind.Freemarker);
			configurarFreemarker(report, locale);

			Map<String, Object> model = generarModel(arbre, serveiDescripcio, traduccions, locale);
			IContext context = report.createContext();
			context.putMap(model);
			report.process(context, out);

			return (List<NodeInfo>) model.get("nodesTipusDocument");
		}
	}

	private void configurarFreemarker(
			IXDocReport report,
			Locale locale) {
		FreemarkerTemplateEngine templateEngine = (FreemarkerTemplateEngine) report.getTemplateEngine();
		Configuration configuration = templateEngine.getFreemarkerConfiguration();
		configuration.setTagSyntax(Configuration.SQUARE_BRACKET_TAG_SYNTAX);
		configuration.setDefaultEncoding("UTF-8");
		configuration.setOutputEncoding("UTF-8");
		configuration.setLocale((locale != null) ? locale : new Locale("ca", "ES"));
		configuration.setTemplateExceptionHandler(new TemplateExceptionHandler() {
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

	private Map<String, Object> generarModel(ElementArbre arbre, String serveiDescripcio, List<ServeiJustificantCamp> traduccions, Locale locale) {

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("text_titol_capsalera", messageSource.getMessage("justificant.plantilla.titol.capsalera", null, locale));
		model.put("text_titol_servei", serveiDescripcio);
		model.put("text_titol_limitacio", messageSource.getMessage("justificant.plantilla.titol.limitacio", null, locale));
		model.put("text_legal", messageSource.getMessage("justificant.plantilla.text.legal", null, locale));
		model.put("text_data_eldia", messageSource.getMessage("justificant.plantilla.data.eldia", null, locale));
		Date ara = new Date();
		model.put("text_data_data", formatDate(ara, locale));
		List<NodeInfo> nodes = new ArrayList<>();
		List<NodeInfo> nodesTipusDocument = new ArrayList<>();

		convertirArbreEnLlista(arbre, 0, nodes);
		if (traduccions != null) {
			for (NodeInfo node: nodes) {
				for (ServeiJustificantCamp traduccio: traduccions) {
					if (traduccio.getXpath().equals(node.getXpathDadaEspecifica())) {
						node.setTitol(traduccio.getTraduccio());
						if (traduccio.isDocument()) {
							nodesTipusDocument.add(node);
						}
						break;
					}
				}
			}
		}
		nodes.removeAll(nodesTipusDocument);
		model.put("nodes", nodes);
		model.put("nodesTipusDocument", nodesTipusDocument);
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

	private String formatDate(Date date, Locale locale) {

		return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(date);
//		if (locale == null || locale.getLanguage().equalsIgnoreCase("ca")) {
//			String data = DateFormat.getDateInstance(DateFormat.LONG, new Locale("ca", "ES")).format(date);
//			Calendar cal = Calendar.getInstance();
//			cal.setTime(date);
//			int month = cal.get(Calendar.MONTH);
//			if (month == Calendar.APRIL || month == Calendar.AUGUST || month == Calendar.OCTOBER) {
//				return data.replaceFirst("/ ", "d'").replace("/", "de");
//			} else {
//				return data.replace("/", "de");
//			}
//		} else {
//			return DateFormat.getDateInstance(DateFormat.LONG, locale).format(date);
//		}
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
		return configHelper.getConfigAsBoolean("es.caib.pinbal.justificant.convertir.pdfa", false);
	}
	private boolean isSignarICustodiarJustificant() {
		return configHelper.getConfigAsBoolean("es.caib.pinbal.justificant.signar.i.custodiar", false);
	}
	private String getJustificantSerieDocumental() {
//		return configHelper.getConfig("es.caib.pinbal.justificant.serie.documental");
		return configHelper.getConfig("es.caib.pinbal.plugin.arxiu.serie.documental");
	}

	public static class NodeInfo {
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
