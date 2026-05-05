/**
 * 
 */
package es.caib.pinbal.api.externa.controller;

import es.caib.pinbal.api.externa.api.ExplotacioDadesObertesApi;
import es.caib.pinbal.client.dadesobertes.DadesObertesRespostaConsulta;
import es.caib.pinbal.logic.intf.service.ConsultaService;
import es.caib.pinbal.logic.intf.service.HistoricConsultaService;
import es.caib.pinbal.logic.intf.service.exception.EntitatNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.ProcedimentNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * Controlador pel servei REST de consulta de dades obertes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/opendata")
public class ExplotacioDadesObertesRestController implements ExplotacioDadesObertesApi {

	private final ConsultaService consultaService;
	private final HistoricConsultaService historicConsultaService;

	// IMPORTANT: Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI definida a la interfície ExplotacioDadesObertesApi.
	@Override
	@GetMapping(produces = "application/json")
	public ResponseEntity<List<DadesObertesRespostaConsulta>> opendata(
			HttpServletRequest request,
			@RequestParam(value = "historic", required = false) boolean historic,
			@RequestParam(required = false) final String entitatCodi,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final Date dataInici,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final Date dataFi,
			@RequestParam(required = false) final String procedimentCodi,
			@RequestParam(required = false) final String serveiCodi) throws EntitatNotFoundException, ProcedimentNotFoundException {
		// Informe de procediments agrupats per entitat i departament
		List<DadesObertesRespostaConsulta> entitats;
		if (historic) {
			entitats = historicConsultaService.findByFiltrePerOpenData(
					entitatCodi,
					dataInici,
					dataFi,
					procedimentCodi,
					serveiCodi);
		} else {
			entitats = consultaService.findByFiltrePerOpenData(
					entitatCodi,
					dataInici,
					dataFi,
					procedimentCodi,
					serveiCodi);
		}
		return new ResponseEntity<List<DadesObertesRespostaConsulta>>(entitats, HttpStatus.OK);
	}

}
