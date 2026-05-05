/**
 * 
 */
package es.caib.pinbal.api.externa.controller;

import es.caib.pinbal.api.externa.api.ExplotacioStatsApi;
import es.caib.pinbal.api.externa.command.EstadistiquesFiltreCommand;
import es.caib.pinbal.client.comu.*;
import es.caib.pinbal.client.comu.ServeiEstadistiques.ConsultesOkError;
import es.caib.pinbal.logic.intf.dto.*;
import es.caib.pinbal.logic.intf.dto.ConsultaDto.EstatTipus;
import es.caib.pinbal.logic.intf.dto.EstadistiquesFiltreDto.EstadistiquesAgrupacioDto;
import es.caib.pinbal.logic.intf.service.*;
import es.caib.pinbal.logic.intf.service.exception.EntitatNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.ProcedimentNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Controlador pel servei REST de consulta d'informes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/stats")
public class ExplotacioStatsRestController implements ExplotacioStatsApi {

	private final EntitatService entitatService;
	private final ProcedimentService procedimentService;
	private final ServeiService serveiService;
	private final UsuariService usuariService;
	private final ConsultaService consultaService;
	private final HistoricConsultaService historicConsultaService;


	@Override
	@PreAuthorize("hasRole('PBL_REPORT')")
	@GetMapping(value= "/consultes", produces = "application/json")
	// IMPORTANT: Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI definida a la interfície ExplotacioStatsApi.
	public ResponseEntity<List<ProcedimentEstadistiques>> consultes(
			HttpServletRequest request,
			@RequestParam final String entitatCodi,
			@RequestParam(required = false) final String procedimentCodi,
			@RequestParam(required = false) final String serveiCodi,
			@RequestParam(required = false) final EstatTipus estat,
			@RequestParam(required = false) final Date dataInici,
			@RequestParam(required = false) final Date dataFi) throws EntitatNotFoundException, ProcedimentNotFoundException {
		// Estadística de consultes
		List<EstadisticaDto> estadistiques = consultaService.findEstadistiquesByFiltre(
				getEstadistiquesFiltre(
						entitatCodi,
						procedimentCodi,
						serveiCodi,
						estat,
						null,
						dataInici,
						dataFi));
		List<ProcedimentEstadistiques> estadisticaProcediments = new ArrayList<ProcedimentEstadistiques>();
		ProcedimentEstadistiques procedimentActual = null;
		Long procedimentActualId = null;
		for (EstadisticaDto estadistica: estadistiques) {
			if (procedimentActual == null || !procedimentActualId.equals(estadistica.getProcedimentId())) {
				procedimentActualId = estadistica.getProcedimentId();
				procedimentActual = new ProcedimentEstadistiques();
				procedimentActual.setCodi(estadistica.getProcedimentCodi());
				procedimentActual.setNom(estadistica.getProcedimentNom());
				estadisticaProcediments.add(procedimentActual);
			}
			ServeiEstadistiques servei = new ServeiEstadistiques();
			servei.setCodi(estadistica.getServeiCodi());
			servei.setNom(estadistica.getServeiNom());
			servei.setConsultesWeb(new ConsultesOkError(
					estadistica.getNumWebUIOk(),
					estadistica.getNumWebUIError()));
			servei.setConsultesRecobriment(new ConsultesOkError(
					estadistica.getNumRecobrimentOk(),
					estadistica.getNumRecobrimentError()));
			servei.setConsultesTotal(new ConsultesOkError(
					estadistica.getNumWebUIOk() + estadistica.getNumRecobrimentOk(),
					estadistica.getNumWebUIError() + estadistica.getNumRecobrimentError()));
			if (estadistica.isConteSumatori()) {
				procedimentActual.setConsultesWeb(new ConsultesOkError(
						estadistica.getSumatoriNumWebUIOk(),
						estadistica.getSumatoriNumWebUIError()));
				procedimentActual.setConsultesRecobriment(new ConsultesOkError(
						estadistica.getSumatoriNumRecobrimentOk(),
						estadistica.getSumatoriNumRecobrimentError()));
				procedimentActual.setConsultesTotal(new ConsultesOkError(
						estadistica.getSumatoriNumWebUIOk() + estadistica.getSumatoriNumRecobrimentOk(),
						estadistica.getSumatoriNumWebUIError() + estadistica.getSumatoriNumRecobrimentError()));
			}
			if (procedimentActual.getServeis() == null) {
				procedimentActual.setServeis(new ArrayList<ServeiEstadistiques>());
			}
			procedimentActual.getServeis().add(servei);
		}
		return new ResponseEntity<List<ProcedimentEstadistiques>>(estadisticaProcediments, HttpStatus.OK);
	}

	@Override
	@PreAuthorize("hasRole('PBL_REPORT')")
	@GetMapping(value= "/carrega", produces = "application/json")
	// IMPORTANT: Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI definida a la interfície ExplotacioStatsApi.
	public ResponseEntity<List<EntitatEstadistiques>> carrega(
			HttpServletRequest request) {
		// Informe de carrega
		List<CarregaDto> carregues = consultaService.findEstadistiquesCarrega();
		EntitatEstadistiques entitatActual = null;
		Long entitatActualId = null;
		DepartamentEstadistiques departamentActual = null;
		ProcedimentEstadistiques procedimentActual = null;
		List<EntitatEstadistiques> entitats = new ArrayList<EntitatEstadistiques>();
		for (CarregaDto carrega: carregues) {
			if (entitatActual == null || !entitatActualId.equals(carrega.getEntitatId())) {
				entitatActualId = carrega.getEntitatId();
				entitatActual = new EntitatEstadistiques();
				entitatActual.setCodi(carrega.getEntitatCodi());
				entitatActual.setNom(carrega.getEntitatNom());
				entitatActual.setNif(carrega.getEntitatCif());
				entitats.add(entitatActual);
			}
			if (carrega.getDepartamentNom() != null) {
				if (departamentActual == null || departamentActual.getNom() != carrega.getDepartamentNom()) {
					departamentActual = new DepartamentEstadistiques();
					//departamentActual.setCodi(informeProcediment.getDepartamentCodi());
					departamentActual.setNom(carrega.getDepartamentNom());
					if (entitatActual.getDepartaments() == null) {
						entitatActual.setDepartaments(new ArrayList<DepartamentEstadistiques>());
					}
					entitatActual.getDepartaments().add(departamentActual);
				}
				if (procedimentActual == null || procedimentActual.getCodi() != carrega.getProcedimentCodi()) {
					procedimentActual = new ProcedimentEstadistiques();
					procedimentActual.setCodi(carrega.getProcedimentCodi());
					procedimentActual.setNom(carrega.getProcedimentNom());
					if (departamentActual.getProcediments() == null) {
						departamentActual.setProcediments(new ArrayList<ProcedimentEstadistiques>());
					}
					departamentActual.getProcediments().add(procedimentActual);
				}
				ServeiEstadistiques servei = new ServeiEstadistiques();
				servei.setCodi(carrega.getServeiCodi());
				servei.setNom(carrega.getServeiDescripcio());
				if (carrega.getDetailedWebCount() != null) {
					servei.setTotalWeb(new TotalAcumulat(
							carrega.getDetailedWebCount().getAny(),
							carrega.getDetailedWebCount().getMes(),
							carrega.getDetailedWebCount().getDia(),
							carrega.getDetailedWebCount().getHora(),
							carrega.getDetailedWebCount().getMinut()));
				}
				if (carrega.getDetailedRecobrimentCount() != null) {
					servei.setTotalRecobriment(new TotalAcumulat(
							carrega.getDetailedRecobrimentCount().getAny(),
							carrega.getDetailedRecobrimentCount().getMes(),
							carrega.getDetailedRecobrimentCount().getDia(),
							carrega.getDetailedRecobrimentCount().getHora(),
							carrega.getDetailedRecobrimentCount().getMinut()));
				}
				if (procedimentActual.getServeis() == null) {
					procedimentActual.setServeis(new ArrayList<ServeiEstadistiques>());
				}
				procedimentActual.getServeis().add(servei);
			}
		}
		return new ResponseEntity<List<EntitatEstadistiques>>(entitats, HttpStatus.OK);
	}

	private EstadistiquesFiltreDto getEstadistiquesFiltre(
			String entitatCodi,
			String procedimentCodi,
			String serveiCodi,
			EstatTipus estat,
			EstadistiquesAgrupacioDto agrupacio,
			Date dataInici,
			Date dataFi) throws EntitatNotFoundException, ProcedimentNotFoundException {
		EstadistiquesFiltreCommand filtre = new EstadistiquesFiltreCommand();
		EntitatDto entitat = entitatService.findByCodi(entitatCodi);
		if (entitat != null) {
			filtre.setEntitatId(entitat.getId());
		} else {
			throw new EntitatNotFoundException();
		}
		ProcedimentDto procediment = procedimentService.findAmbEntitatICodi(
				entitat.getId(),
				procedimentCodi);
		if (procediment != null) {
			filtre.setProcediment(procediment.getId());
		} else {
			throw new ProcedimentNotFoundException();
		}
		filtre.setServei(serveiCodi);
		filtre.setEstat(estat);
		filtre.setDataInici(dataInici);
		filtre.setDataFi(dataFi);
		filtre.setAgrupacio(agrupacio != null ? agrupacio : EstadistiquesAgrupacioDto.PROCEDIMENT_SERVEI);
		return EstadistiquesFiltreCommand.asDto(filtre);
	}

}
