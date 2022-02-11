/**
 * 
 */
package es.caib.pinbal.core.helper;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Element;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.pinbal.core.dto.ConsultaDto;
import es.caib.pinbal.core.dto.ConsultaDto.Consentiment;
import es.caib.pinbal.core.dto.ConsultaDto.DocumentTipus;
import es.caib.pinbal.core.dto.IntegracioAccioTipusEnumDto;
import es.caib.pinbal.core.model.Consulta;
import es.caib.pinbal.core.model.Consulta.EstatTipus;
import es.caib.pinbal.core.model.Entitat;
import es.caib.pinbal.core.model.Procediment;
import es.caib.pinbal.core.model.ProcedimentServei;
import es.caib.pinbal.core.model.ServeiCamp;
import es.caib.pinbal.core.model.ServeiCamp.ServeiCampTipus;
import es.caib.pinbal.core.model.ServeiConfig;
import es.caib.pinbal.core.repository.ConsultaRepository;
import es.caib.pinbal.core.repository.ServeiCampRepository;
import es.caib.pinbal.core.repository.ServeiConfigRepository;
import es.caib.pinbal.core.service.exception.ConsultaScspComunicacioException;
import es.caib.pinbal.core.service.exception.ConsultaScspGeneracioException;
import es.caib.pinbal.scsp.ResultatEnviamentPeticio;
import es.caib.pinbal.scsp.ScspHelper;
import es.caib.pinbal.scsp.Solicitud;
import lombok.extern.slf4j.Slf4j;

/**
 * Helper per a controlar el ritme al qual s'envien les consultes SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class PeticioScspHelper {

	@Autowired
	private ConsultaRepository consultaRepository;
	@Autowired
	private ServeiConfigRepository serveiConfigRepository;
	@Autowired
	private ServeiCampRepository serveiCampRepository;
	@Autowired
	private PeticioScspEstadistiquesHelper peticionsScspEstadistiquesHelper;
	@Autowired
	private IntegracioHelper integracioHelper;

	/* Emmagatzema el comptador de peticions fetes dins linterval actual per a cada servei */
	private Map<String, Integer> consultaServeiCount = new HashMap<String, Integer>();
	/* Emmagatzema l'inici de l'interval actual per a cada servei */
	private Map<String, Date> consultaIntervalStart = new HashMap<String, Date>();

	/* Retorna true si s'ha de continuar l'enviament de la consulta, fals en cas contrari. */
	public boolean isEnviarConsultaServei(Consulta consulta, boolean auto) {
		String serveiCodi = consulta.getProcedimentServei().getServei();
		ServeiConfig serveiConfig = serveiConfigRepository.findByServei(serveiCodi);

		if (serveiConfig.getMaxPeticionsMinut() != null) {
			Integer count = consultaServeiCount.get(serveiCodi);
			if (count == null) {
				count = 0;
			}
			
			
			boolean mateixMinut = isMateixMinut(serveiCodi);

			if (mateixMinut) {
				boolean isMaxPeticionsEnviades = count >= serveiConfig.getMaxPeticionsMinut();
				if (isMaxPeticionsEnviades) {
					return false;
				} else {
					if (!auto && existOlderFromAutoPendingToSend(consulta.getProcedimentServei())) {
						return false;
					} else {
						consultaServeiCount.put(serveiCodi, ++count);
						return true;
					}
				}

			} else {
				if (!auto && existOlderFromAutoPendingToSend(consulta.getProcedimentServei())) {
					return false;
				} else {
					consultaIntervalStart.put(serveiCodi, new Date());
					consultaServeiCount.put(serveiCodi, new Integer(1));
					return true;
				}
				

			}
		}
		return true;
	}
	
	private boolean existOlderFromAutoPendingToSend(ProcedimentServei procedimentServei) {
		List<Consulta> pendents = consultaRepository.findByEstatAndMultipleAndProcedimentServeiAndConsentimentNotNullOrderByIdAsc(EstatTipus.Pendent, false, procedimentServei);
		return pendents.size() > 1;
	}

	private boolean isMateixMinut(String serveiCodi) {

		Calendar now = Calendar.getInstance();
		Date intervalStart = consultaIntervalStart.get(serveiCodi);
		Calendar intervalStartCal = Calendar.getInstance();
		intervalStartCal.setTime(intervalStart != null ? intervalStart : new Date(0));

		long millisBetweenDates = now.getTime().getTime() - intervalStartCal.getTime().getTime();
		
		log.trace(String.valueOf(Math.round(((float) millisBetweenDates) / 1000)) + " seconds passed from last reset");
		
		boolean mateixMinut = millisBetweenDates < 60 * 1000 && intervalStartCal.get(Calendar.MINUTE) == now.get(Calendar.MINUTE);

		return mateixMinut;
	}
	

	public ResultatEnviamentPeticio enviarPeticioScsp(
			Consulta consulta,
			List<Solicitud> solicituds,
			boolean sincrona,
			boolean recobriment,
			ScspHelper scspHelper) throws ConsultaScspGeneracioException, ConsultaScspComunicacioException {
		peticionsScspEstadistiquesHelper.actualitzarEstadistiquesPeticio(
				consulta.getProcedimentServei().getProcediment().getEntitat().getId(),
				solicituds,
				recobriment);
		boolean gestioXsdActiva = isGestioXsdActiva(consulta.getProcedimentServei().getServei());
		if (sincrona) {
			return scspHelper.enviarPeticionSincrona(
					consulta.getScspPeticionId(),
					solicituds,
					gestioXsdActiva);
		} else {
			return scspHelper.enviarPeticionAsincrona(
					consulta.getScspPeticionId(),
					solicituds,
					gestioXsdActiva);
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void enviarPeticioScspPendent(
			Long consultaId,
			ScspHelper scspHelper) {
		Consulta consulta = consultaRepository.getOne(consultaId);
		try {
			long t0 = System.currentTimeMillis();
			consulta.updateEstat(EstatTipus.Processant);
			ResultatEnviamentPeticio resultat = enviarPeticioScsp(
					consulta,
					Arrays.asList(convertirEnSolicitud(consulta)),
					true,
					consulta.isRecobriment(),
					scspHelper);
			Map<String, String> accioParams = new HashMap<String, String>();
			accioParams.put("codi", consulta.getProcedimentServei().getServei());
			updateEstatConsulta(consulta, resultat, accioParams);
			if (resultat.getIdsSolicituds() != null && resultat.getIdsSolicituds().length > 0) {
				consulta.updateScspSolicitudId(resultat.getIdsSolicituds()[0]);
			}
			String accioDescripcio = "Enviament de la consulta pendent al servei SCSP";
			/*if (resultat.isError()) {
				integracioHelper.addAccioError(
						IntegracioHelper.INTCODI_SERVEIS_SCSP,
						accioDescripcio,
						accioParams,
						IntegracioAccioTipusEnumDto.ENVIAMENT,
						System.currentTimeMillis() - t0,
						"[" + resultat.getErrorCodi() + "] " + resultat.getErrorDescripcio(),
						(Throwable)null);
			} else {
				integracioHelper.addAccioOk(
						IntegracioHelper.INTCODI_SERVEIS_SCSP,
						accioDescripcio,
						accioParams,
						IntegracioAccioTipusEnumDto.ENVIAMENT,
						System.currentTimeMillis() - t0);
			}*/
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_SERVEIS_SCSP,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
		} catch (ConsultaScspGeneracioException ex) {
			consulta.updateEstatError("Error al generar la petició SCSP: " + ex.getMessage());
		} catch (ConsultaScspComunicacioException ex) {
			consulta.updateEstatError("Error en la comunicació SCSP: " + ex.getMessage());
		}
	}

	public void processarIEmmagatzemarDadesEspecifiques(
			Consulta consulta,
			Map<String, Object> dadesEspecifiques) throws ConsultaScspGeneracioException {
		ProcedimentServei procedimentServei = consulta.getProcedimentServei();
		processarDadesEspecifiques(procedimentServei.getServei(), dadesEspecifiques);
		// Emmagatzema les dades específiques a l'entitat de base de dades
		try {
			consulta.updateDadesEspecifiques(
					dadesEspecifiquesToJson(dadesEspecifiques));
		} catch (IOException ex) {
			throw new ConsultaScspGeneracioException(
					"No s'han pogut convertir les dades específiques des de format JSON: " + ex.getMessage(),
					ex);
		}
	}

	public Solicitud convertirEnSolicitud(Consulta consulta) throws ConsultaScspGeneracioException {
		ProcedimentServei procedimentServei = consulta.getProcedimentServei();
		Entitat entitat = procedimentServei.getProcediment().getEntitat();
		Solicitud solicitud = new Solicitud();
		solicitud.setServeiCodi(procedimentServei.getServei());
		Procediment procediment = procedimentServei.getProcediment();
		solicitud.setProcedimentCodi(
				(procedimentServei.getProcedimentCodi() != null && !("".equalsIgnoreCase(procedimentServei.getProcedimentCodi())) ? 
						procedimentServei.getProcedimentCodi() : 
						procediment.getCodi()));
		solicitud.setProcedimentNom(procediment.getNom());
		solicitud.setProcedimentValorCampAutomatizado(procediment.getValorCampAutomatizado());
		if (procediment.getValorCampClaseTramite() != null) {
			solicitud.setProcedimentValorCampClaseTramite(procediment.getValorCampClaseTramite().getShortValue());
		}
		solicitud.setSolicitantIdentificacio(entitat.getCif());
		solicitud.setSolicitantNom(entitat.getNom());
		solicitud.setFuncionariNom(consulta.getFuncionariNom());
		solicitud.setFuncionariNif(consulta.getFuncionariDocumentNum());
		if (consulta.getTitularDocumentTipus() != null) {
			solicitud.setTitularDocumentTipus(
					es.caib.pinbal.scsp.DocumentTipus.valueOf(consulta.getTitularDocumentTipus().toString()));
		}
		solicitud.setTitularDocument(consulta.getTitularDocumentNum());
		solicitud.setTitularNom(consulta.getTitularNom());
		solicitud.setTitularLlinatge1(consulta.getTitularLlinatge1());
		solicitud.setTitularLlinatge2(consulta.getTitularLlinatge2());
		solicitud.setTitularNomComplet(consulta.getTitularNomComplet());
		solicitud.setFinalitat(consulta.getFinalitat());
		solicitud.setConsentiment(
				es.caib.pinbal.scsp.Consentiment.valueOf(
						consulta.getConsentiment().toString()));
		setUnitatTramitadoraSolicitud(solicitud, procediment, consulta.getDepartamentNom());
		solicitud.setExpedientId(consulta.getExpedientId());
		String dadesEspecifiques = consulta.getDadesEspecifiques();
		if (dadesEspecifiques != null && !dadesEspecifiques.trim().isEmpty()) {
			try {
				solicitud.setDadesEspecifiquesMap(dadesEspecifiquesFromJson(consulta.getDadesEspecifiques()));
			} catch (IOException ex) {
				throw new ConsultaScspGeneracioException(
						"No s'han pogut convertir les dades específiques des de format JSON: " + ex.getMessage(),
						ex);
			}
		}
		return solicitud;
	}

	public Solicitud convertirEnSolicitud(
			Entitat entitat,
			Procediment procediment,
			String serveiCodi,
			String funcionariNom,
			String funcionariNif,
			DocumentTipus titularDocumentTipus,
			String titularDocumentNum,
			String titularNom,
			String titularLlinatge1,
			String titularLlinatge2,
			String titularNomComplet,
			String finalitat,
			Consentiment consentiment,
			String departamentNom,
			String unitatTramitadoraCodi,
			String expedientId,
			Element dadesEspecifiques,
			ProcedimentServei procedimentServei,
			ScspHelper scspHelper) throws ConsultaScspGeneracioException {
		Solicitud solicitud = new Solicitud();
		solicitud.setServeiCodi(serveiCodi);
		solicitud.setProcedimentCodi(
				(procedimentServei.getProcedimentCodi() != null && !("".equalsIgnoreCase(procedimentServei.getProcedimentCodi())) ? 
						procedimentServei.getProcedimentCodi() : 
						procediment.getCodi()));
		solicitud.setProcedimentNom(procediment.getNom());
		solicitud.setProcedimentValorCampAutomatizado(procediment.getValorCampAutomatizado());
		if (procediment.getValorCampClaseTramite() != null) {
			solicitud.setProcedimentValorCampClaseTramite(procediment.getValorCampClaseTramite().getShortValue());
		}
		solicitud.setSolicitantIdentificacio(entitat.getCif());
		solicitud.setSolicitantNom(entitat.getNom());
		solicitud.setFuncionariNom(funcionariNom);
		solicitud.setFuncionariNif(funcionariNif);
		if (titularDocumentTipus != null) {
			solicitud.setTitularDocumentTipus(
					es.caib.pinbal.scsp.DocumentTipus.valueOf(titularDocumentTipus.toString()));
		}
		solicitud.setTitularDocument(titularDocumentNum);
		solicitud.setTitularNom(titularNom);
		solicitud.setTitularLlinatge1(titularLlinatge1);
		solicitud.setTitularLlinatge2(titularLlinatge2);
		solicitud.setTitularNomComplet(titularNomComplet);
		solicitud.setFinalitat(finalitat);
		solicitud.setConsentiment(
				es.caib.pinbal.scsp.Consentiment.valueOf(consentiment.toString()));
		setUnitatTramitadoraSolicitud(solicitud, procediment, departamentNom);
		solicitud.setUnitatTramitadoraCodi(unitatTramitadoraCodi);
		solicitud.setExpedientId(expedientId);
		solicitud.setDadesEspecifiquesElement(
				scspHelper.copiarDadesEspecifiquesRecobriment(
						serveiCodi,
						dadesEspecifiques,
						isGestioXsdActiva(serveiCodi)));
		return solicitud;
	}

	public List<Solicitud> convertirEnMultiplesSolicituds(
			ConsultaDto consulta,
			ProcedimentServei procedimentServei) throws ConsultaScspGeneracioException {
		try {
			List<ServeiCamp> serveiCamps = serveiCampRepository.findByServeiOrderByGrupOrdreAsc(
					consulta.getServeiCodi());
			List<Solicitud> solicituds = new ArrayList<Solicitud>();
			for (String[] dades: consulta.getDadesPeticioMultiple()) {
				Solicitud solicitud = new Solicitud();
				solicitud.setServeiCodi(consulta.getServeiCodi());
				Procediment procediment = procedimentServei.getProcediment();
				solicitud.setProcedimentCodi(procediment.getCodi());
				solicitud.setProcedimentNom(procediment.getNom());
				solicitud.setProcedimentValorCampAutomatizado(procediment.getValorCampAutomatizado());
				if (procediment.getValorCampClaseTramite() != null) {
					solicitud.setProcedimentValorCampClaseTramite(procediment.getValorCampClaseTramite().getShortValue());
				}
				solicitud.setSolicitantIdentificacio(consulta.getEntitatCif());
				solicitud.setSolicitantNom(consulta.getEntitatNom());
				solicitud.setFuncionariNom(consulta.getFuncionariNom());
				solicitud.setFuncionariNif(consulta.getFuncionariNif());
				solicitud.setFinalitat(consulta.getFinalitat());
				solicitud.setConsentiment(
						es.caib.pinbal.scsp.Consentiment.valueOf(
								consulta.getConsentiment().toString()));
				setUnitatTramitadoraSolicitud(solicitud, procediment, consulta.getDepartamentNom());
				String titularDocumentTipus = getValorCampPeticioMultiple(
						"DatosGenericos/Titular/TipoDocumentacion",
						consulta.getCampsPeticioMultiple(),
						dades);
				if (titularDocumentTipus != null) {
					solicitud.setTitularDocumentTipus(
							es.caib.pinbal.scsp.DocumentTipus.valueOf(
									titularDocumentTipus));
				}
				solicitud.setTitularDocument(
						getValorCampPeticioMultiple(
								"DatosGenericos/Titular/Documentacion",
								consulta.getCampsPeticioMultiple(),
								dades));
				solicitud.setTitularNom(
						getValorCampPeticioMultiple(
								"DatosGenericos/Titular/Nombre",
								consulta.getCampsPeticioMultiple(),
								dades));
				solicitud.setTitularLlinatge1(
						getValorCampPeticioMultiple(
								"DatosGenericos/Titular/Apellido1",
								consulta.getCampsPeticioMultiple(),
								dades));
				solicitud.setTitularLlinatge2(
						getValorCampPeticioMultiple(
								"DatosGenericos/Titular/Apellido2",
								consulta.getCampsPeticioMultiple(),
								dades));
				solicitud.setTitularNomComplet(
						getValorCampPeticioMultiple(
								"DatosGenericos/Titular/NombreCompleto",
								consulta.getCampsPeticioMultiple(),
								dades));
				solicitud.setExpedientId(
						getValorCampPeticioMultiple(
								"DatosGenericos",
								consulta.getCampsPeticioMultiple(),
								dades));
				Map<String, Object> dadesEspecifiques = getDadesEspecifiquesPeticioMultiple(
						serveiCamps,
						consulta.getCampsPeticioMultiple(),
						dades);
				processarDadesEspecifiques(
						consulta.getServeiCodi(),
						dadesEspecifiques);
				solicitud.setDadesEspecifiquesMap(dadesEspecifiques);
				solicituds.add(solicitud);
			}
			return solicituds;
		} catch (Exception ex) {
			if (ex instanceof ConsultaScspGeneracioException) {
				throw ex;
			} else {
				throw new ConsultaScspGeneracioException(ex);
			}
		}
	}

	public void updateEstatConsulta(
			Consulta consulta,
			ResultatEnviamentPeticio resultat,
			Map<String, String> accioParams) {
		String error = null;
		if (resultat.isError()) {
			error = "[" + resultat.getErrorCodi() + "] " + resultat.getErrorDescripcio();
			updateEstatConsultaError(consulta, error);
		} else if ("0001".equals(resultat.getEstatCodi())) {
			consulta.updateEstat(EstatTipus.Pendent);
		} else if ("0002".equals(resultat.getEstatCodi())) {
			consulta.updateEstat(EstatTipus.Processant);
		} else if ("0003".equals(resultat.getEstatCodi())) {
			consulta.updateEstat(EstatTipus.Tramitada);
		} else if ("0004".equals(resultat.getEstatCodi())) {
			consulta.updateEstat(EstatTipus.Processant);
		}
		if (accioParams != null) {
			accioParams.put("idPeticion", consulta.getScspPeticionId());
			accioParams.put("idSolicitud", consulta.getScspSolicitudId());
			accioParams.put("estat", "[" + resultat.getEstatCodi() + "] " + resultat.getEstatDescripcio());
			if (error != null) {
				accioParams.put("error", error);
			}
		}
	}

	public void updateEstatConsultaError(
			Consulta consulta,
			String error) {
		consulta.updateEstat(EstatTipus.Error);
		consulta.updateEstatError(error);
	}

	public boolean isGestioXsdActiva(String serveiCodi) {
		ServeiConfig serveiConfig = serveiConfigRepository.findByServei(serveiCodi);
		return serveiConfig != null && serveiConfig.isActivaGestioXsd();
	}

	private String dadesEspecifiquesToJson(Map<String, Object> dadesEspecifiques) throws IOException {
		return new ObjectMapper().writeValueAsString(dadesEspecifiques);
	}

	private Map<String, Object> dadesEspecifiquesFromJson(String json) throws IOException {
		return new ObjectMapper().readValue(json, new TypeReference<HashMap<String, String>>() {});
	}

	private void processarDadesEspecifiques(
			String serveiCodi,
			Map<String, Object> dadesEspecifiques) throws ConsultaScspGeneracioException {
		// Modifica les dades específiques depenent dels camps definits al formulari del servei
		try {
			SimpleDateFormat sdfComu = new SimpleDateFormat("dd/MM/yyyy");
			List<ServeiCamp> camps = serveiCampRepository.findByServeiOrderByGrupOrdreAsc(serveiCodi);
			// Conversió de format
			for (String path: dadesEspecifiques.keySet()) {
				for (ServeiCamp camp: camps) {
					if (camp.getPath().equals(path)) {
						if (camp.getTipus().equals(ServeiCampTipus.DATA)) {
							String str = (String)dadesEspecifiques.get(path);
							if (str != null && !str.isEmpty()) {
								Date data = sdfComu.parse(str);
								SimpleDateFormat sdfCamp = null;
								if (camp.getDataFormat() != null && !camp.getDataFormat().isEmpty())
									sdfCamp = new SimpleDateFormat(camp.getDataFormat());
								else
									sdfCamp = new SimpleDateFormat("ddMMyyyy");
								dadesEspecifiques.put(path, sdfCamp.format(data));
							} else {
								dadesEspecifiques.put(path, null);
							}
						} else if (camp.getTipus().equals(ServeiCampTipus.BOOLEA)) {
							String str = (String)dadesEspecifiques.get(path);
							if (str != null && !str.isEmpty()) {
								boolean valor =
										"true".equalsIgnoreCase(str) ||
										"on".equalsIgnoreCase(str) ||
										"yes".equalsIgnoreCase(str) ||
										"si".equalsIgnoreCase(str);
								dadesEspecifiques.put(path, valor ? "true" : "false");
							} else {
								dadesEspecifiques.put(path, null);
							}
						}
						break;
					}
				}
			}
			// Control de camps de tipus document
			for (ServeiCamp camp: camps) {
				if (camp.getTipus().equals(ServeiCampTipus.DOC_IDENT)) {
					// Elimina el tipus de document de les dades específiques
					// si no s'especifica el número de document
					String valor = (String)dadesEspecifiques.get(camp.getPath());
					if (valor == null || valor.isEmpty()) {
						ServeiCamp campTipusDocument = camp.getCampPare();
						if (campTipusDocument != null)
							dadesEspecifiques.remove(campTipusDocument.getPath());
					}
				}
			}
		} catch (ParseException ex) {
			throw new ConsultaScspGeneracioException(ex);
		}
	}

	private Map<String, Object> getDadesEspecifiquesPeticioMultiple(
			List<ServeiCamp> serveiCamps,
			String[] camps,
			String[] dades) {
		Map<String, Object> dadesEspecifiques = new HashMap<String, Object>();
		for (ServeiCamp serveiCamp: serveiCamps) {
			for (int i = 0; i < camps.length; i++) {
				if (camps[i].equals(serveiCamp.getPath())) {
					dadesEspecifiques.put(
							camps[i],
							dades[i]);
					break;
				}
			}
		}
		return dadesEspecifiques;
		
	}

	private String getValorCampPeticioMultiple(
			String path,
			String[] camps,
			String[] valors) {
		String valor = null;
		for (int i = 0; i < camps.length; i++) {
			if (path.equals(camps[i])) {
				valor = valors[i];
				break;
			}
		}
		return valor;
	}

	private void setUnitatTramitadoraSolicitud(Solicitud solicitud, Procediment procediment, String defaultUnitatTramitadora) {
		ServeiConfig serveiConfig = serveiConfigRepository.findByServei(solicitud.getServeiCodi());
		if (serveiConfig.isPinbalUnitatDir3FromEntitat()) {
			solicitud.setUnitatTramitadoraCodi(procediment.getEntitat().getUnitatArrel());
		} else if(serveiConfig.getPinbalUnitatDir3() != null && !serveiConfig.getPinbalUnitatDir3().isEmpty()) {
			solicitud.setUnitatTramitadoraCodi(serveiConfig.getPinbalUnitatDir3());
		} else if (procediment.getOrganGestor() != null) {
			solicitud.setUnitatTramitadoraCodi(procediment.getOrganGestor().getCodi());
		}
		solicitud.setUnitatTramitadora(defaultUnitatTramitadora);
	}

}