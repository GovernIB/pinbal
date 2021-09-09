/**
 * 
 */
package es.caib.pinbal.core.helper;

import java.io.IOException;
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.pinbal.core.dto.IntegracioAccioTipusEnumDto;
import es.caib.pinbal.core.model.Consulta;
import es.caib.pinbal.core.model.Consulta.EstatTipus;
import es.caib.pinbal.core.model.Entitat;
import es.caib.pinbal.core.model.Procediment;
import es.caib.pinbal.core.model.ProcedimentServei;
import es.caib.pinbal.core.model.ServeiConfig;
import es.caib.pinbal.core.repository.ConsultaRepository;
import es.caib.pinbal.core.repository.ServeiConfigRepository;
import es.caib.pinbal.core.service.exception.ConsultaScspComunicacioException;
import es.caib.pinbal.core.service.exception.ConsultaScspGeneracioException;
import es.caib.pinbal.scsp.ResultatEnviamentPeticio;
import es.caib.pinbal.scsp.ScspHelper;
import es.caib.pinbal.scsp.Solicitud;

/**
 * Helper per a controlar el ritme al qual s'envien les consultes SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class PeticioScspHelper {

	@Autowired
	private ConsultaRepository consultaRepository;
	@Autowired
	private ServeiConfigRepository serveiConfigRepository;
	@Autowired
	private PeticioScspEstadistiquesHelper peticionsScspEstadistiquesHelper;
	@Autowired
	private IntegracioHelper integracioHelper;

	/* Emmagatzema el comptador de peticions fetes dins linterval actual per a cada servei */
	private Map<String, Integer> consultaServeiCount = new HashMap<String, Integer>();
	/* Emmagatzema l'inici de l'interval actual per a cada servei */
	private Map<String, Date> consultaIntervalStart = new HashMap<String, Date>();

	/* Retorna true si s'ha de continuar l'enviament de la consulta, fals en cas contrari. */
	public boolean isEnviarConsultaServei(Consulta consulta) {
		String serveiCodi = consulta.getProcedimentServei().getServei();
		ServeiConfig serveiConfig = serveiConfigRepository.findByServei(serveiCodi);
		if (serveiConfig.getMaxPeticionsMinut() != null) {
			Integer count = consultaServeiCount.get(serveiCodi);
			if (count == null) {
				count = 0;
			}
			boolean isMaxPeticionsEnviades = count >= serveiConfig.getMaxPeticionsMinut();
			Date intervalStart = consultaIntervalStart.get(serveiCodi);
			Calendar now = Calendar.getInstance();
			Calendar intervalStartCal = Calendar.getInstance();
			intervalStartCal.setTime(intervalStart != null ? intervalStart : new Date(0));
			long millisBetweenDates = now.getTime().getTime() - intervalStartCal.getTime().getTime();
			boolean mateixMinut = millisBetweenDates < 60 * 1000 && intervalStartCal.get(Calendar.MINUTE) == now.get(Calendar.MINUTE);
			if (mateixMinut) {
				consultaServeiCount.put(serveiCodi, count++);
			} else {
				consultaIntervalStart.put(serveiCodi, new Date());
				consultaServeiCount.put(serveiCodi, new Integer(1));
			}
			return !isMaxPeticionsEnviades;
		}
		return true;
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
			updateEstatConsulta(consulta, resultat);
			consulta.updateScspSolicitudId(resultat.getIdsSolicituds()[0]);
			String accioDescripcio = "Consulta del servei";
			Map<String, String> accioParams = new HashMap<String, String>();
			accioParams.put("codi", consulta.getProcedimentServei().getServei());
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_SERVEIS_SCSP,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
		} catch (IOException ex) {
			consulta.updateEstatError("Error al convertir les dades específiques des de format JSON: " + ex.getMessage());
		} catch (ConsultaScspGeneracioException ex) {
			consulta.updateEstatError("Error al generar la petició SCSP: " + ex.getMessage());
		} catch (ConsultaScspComunicacioException ex) {
			consulta.updateEstatError("Error en la comunicació SCSP: " + ex.getMessage());
		}
	}

	public Solicitud convertirEnSolicitud(Consulta consulta) throws IOException {
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
			solicitud.setDadesEspecifiquesMap(dadesEspecifiquesFromJson(consulta.getDadesEspecifiques()));
		}
		return solicitud;
	}

	public void setUnitatTramitadoraSolicitud(Solicitud solicitud, Procediment procediment, String defaultUnitatTramitadora) {
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

	public void updateEstatConsulta(
			Consulta consulta,
			ResultatEnviamentPeticio resultat) {
		if (resultat.isError()) {
			updateEstatConsultaError(
					consulta,
					"[" + resultat.getErrorCodi() + "] " + resultat.getErrorDescripcio());
		} else if ("0001".equals(resultat.getEstatCodi())) {
			consulta.updateEstat(EstatTipus.Pendent);
		} else if ("0002".equals(resultat.getEstatCodi())) {
			consulta.updateEstat(EstatTipus.Processant);
		} else if ("0003".equals(resultat.getEstatCodi())) {
			consulta.updateEstat(EstatTipus.Tramitada);
		} else if ("0004".equals(resultat.getEstatCodi())) {
			consulta.updateEstat(EstatTipus.Processant);
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

	private Map<String, Object> dadesEspecifiquesFromJson(String json) throws IOException {
		return new ObjectMapper().readValue(json, new TypeReference<HashMap<String, String>>() {});
	}

}
