/**
 * 
 */
package es.caib.pinbal.api.externa.controller;

import es.caib.pinbal.api.externa.command.EstadistiquesFiltreCommand;
import es.caib.pinbal.client.comu.Departament;
import es.caib.pinbal.client.comu.Entitat;
import es.caib.pinbal.client.comu.Procediment;
import es.caib.pinbal.client.comu.Servei;
import es.caib.pinbal.client.comu.Servei.ConsultesOkError;
import es.caib.pinbal.client.comu.TotalAcumulat;
import es.caib.pinbal.core.dto.CarregaDto;
import es.caib.pinbal.core.dto.ConsultaDto.EstatTipus;
import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.EstadisticaDto;
import es.caib.pinbal.core.dto.EstadistiquesFiltreDto;
import es.caib.pinbal.core.dto.EstadistiquesFiltreDto.EstadistiquesAgrupacioDto;
import es.caib.pinbal.core.dto.ProcedimentDto;
import es.caib.pinbal.core.service.ConsultaService;
import es.caib.pinbal.core.service.EntitatService;
import es.caib.pinbal.core.service.HistoricConsultaService;
import es.caib.pinbal.core.service.ProcedimentService;
import es.caib.pinbal.core.service.ServeiService;
import es.caib.pinbal.core.service.UsuariService;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Controlador pel servei REST de consulta d'informes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/stats")
public class ExplotacioStatsRestController {

	@Autowired
	private EntitatService entitatService;
	@Autowired
	private ProcedimentService procedimentService;
	@Autowired
	private ServeiService serveiService;
	@Autowired
	private UsuariService usuariService;
	@Autowired
	private ConsultaService consultaService;
	@Autowired
	private HistoricConsultaService historicConsultaService;


	@RequestMapping(
			value= "/consultes",
			method = RequestMethod.GET,
			produces = "application/json")
	public ResponseEntity<List<Procediment>> consultes(
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
		List<Procediment> estadisticaProcediments = new ArrayList<Procediment>();
		Procediment procedimentActual = null;
		Long procedimentActualId = null;
		for (EstadisticaDto estadistica: estadistiques) {
			if (procedimentActual == null || !procedimentActualId.equals(estadistica.getProcedimentId())) {
				procedimentActualId = estadistica.getProcedimentId();
				procedimentActual = new Procediment();
				procedimentActual.setCodi(estadistica.getProcedimentCodi());
				procedimentActual.setNom(estadistica.getProcedimentNom());
				estadisticaProcediments.add(procedimentActual);
			}
			Servei servei = new Servei();
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
				procedimentActual.setServeis(new ArrayList<Servei>());
			}
			procedimentActual.getServeis().add(servei);
		}
		return new ResponseEntity<List<Procediment>>(estadisticaProcediments, HttpStatus.OK);
	}

	@RequestMapping(
			value= "/carrega",
			method = RequestMethod.GET,
			produces = "application/json")
	public ResponseEntity<List<Entitat>> carrega(
			HttpServletRequest request) {
		// Informe de carrega
		List<CarregaDto> carregues = consultaService.findEstadistiquesCarrega();
		Entitat entitatActual = null;
		Long entitatActualId = null;
		Departament departamentActual = null;
		Procediment procedimentActual = null;
		List<Entitat> entitats = new ArrayList<Entitat>();
		for (CarregaDto carrega: carregues) {
			if (entitatActual == null || !entitatActualId.equals(carrega.getEntitatId())) {
				entitatActualId = carrega.getEntitatId();
				entitatActual = new Entitat();
				entitatActual.setCodi(carrega.getEntitatCodi());
				entitatActual.setNom(carrega.getEntitatNom());
				entitatActual.setNif(carrega.getEntitatCif());
				entitats.add(entitatActual);
			}
			if (carrega.getDepartamentNom() != null) {
				if (departamentActual == null || departamentActual.getNom() != carrega.getDepartamentNom()) {
					departamentActual = new Departament();
					//departamentActual.setCodi(informeProcediment.getDepartamentCodi());
					departamentActual.setNom(carrega.getDepartamentNom());
					if (entitatActual.getDepartaments() == null) {
						entitatActual.setDepartaments(new ArrayList<Departament>());
					}
					entitatActual.getDepartaments().add(departamentActual);
				}
				if (procedimentActual == null || procedimentActual.getCodi() != carrega.getProcedimentCodi()) {
					procedimentActual = new Procediment();
					procedimentActual.setCodi(carrega.getProcedimentCodi());
					procedimentActual.setNom(carrega.getProcedimentNom());
					if (departamentActual.getProcediments() == null) {
						departamentActual.setProcediments(new ArrayList<Procediment>());
					}
					departamentActual.getProcediments().add(procedimentActual);
				}
				Servei servei = new Servei();
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
					procedimentActual.setServeis(new ArrayList<Servei>());
				}
				procedimentActual.getServeis().add(servei);
			}
		}
		return new ResponseEntity<List<Entitat>>(entitats, HttpStatus.OK);
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
