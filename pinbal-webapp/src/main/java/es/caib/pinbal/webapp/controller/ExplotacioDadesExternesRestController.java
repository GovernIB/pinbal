/**
 * 
 */
package es.caib.pinbal.webapp.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import es.caib.pinbal.client.dadesobertes.DadesObertesRespostaConsulta;
import es.caib.pinbal.core.service.ConsultaService;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentNotFoundException;

/**
 * Controlador pel servei REST de consulta de dades obertes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/api/externa")
public class ExplotacioDadesExternesRestController extends BaseController {

	@Autowired
	private ConsultaService consultaService;

	@RequestMapping(
			value= "/opendata",
			method = RequestMethod.GET,
			produces = "application/json")
	public ResponseEntity<List<DadesObertesRespostaConsulta>> opendata(
			HttpServletRequest request,
			@RequestParam final String entitatCodi,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final Date dataInici,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final Date dataFi,
			@RequestParam(required = false) final String procedimentCodi,
			@RequestParam(required = false) final String serveiCodi) throws EntitatNotFoundException, ProcedimentNotFoundException {
		// Informe de procediments agrupats per entitat i departament
		List<DadesObertesRespostaConsulta> entitats = consultaService.findByFiltrePerOpenData(
				entitatCodi,
				dataInici,
				dataFi,
				procedimentCodi,
				serveiCodi);
		return new ResponseEntity<List<DadesObertesRespostaConsulta>>(entitats, HttpStatus.OK);
	}

}
