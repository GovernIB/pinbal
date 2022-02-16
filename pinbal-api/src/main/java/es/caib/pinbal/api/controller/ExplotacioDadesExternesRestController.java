/**
 * 
 */
package es.caib.pinbal.api.controller;

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

import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

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
@RequestMapping("/externa")
public class ExplotacioDadesExternesRestController {

	@Autowired
	private ConsultaService consultaService;

	@RequestMapping(
			value= "/opendata",
			method = RequestMethod.GET,
			produces = "application/json")
	@ApiOperation(
			value = "Informe de procediments agrupats per entitat i departament", 
			notes = "Retorna una llista amb les entitats i l'estatus") //, response=ArrayList.class)
	public ResponseEntity<List<DadesObertesRespostaConsulta>> opendata(
			HttpServletRequest request,
			@ApiParam(name="entitatCodi", value="Codi de l'entitat", required=false) 
			@RequestParam(required = false) final String entitatCodi,
			@ApiParam(name="dataInici", value="Data d'inici") 
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final Date dataInici,
			@ApiParam(name="dataFi", value="Data de fi", required=false) 
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final Date dataFi,
			@ApiParam(name="procedimentCodi", value="Codi del procediment", required=false) 
			@RequestParam(required = false) final String procedimentCodi,
			@ApiParam(name="serveiCodi", value="Codi del servei", required=false) 
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
