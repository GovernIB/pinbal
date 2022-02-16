/**
 * 
 */
package es.caib.pinbal.webapp.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import es.caib.pinbal.client.recobriment.model.ScspConfirmacionPeticion;
import es.caib.pinbal.client.recobriment.model.ScspJustificante;
import es.caib.pinbal.client.recobriment.model.ScspPeticion;
import es.caib.pinbal.client.recobriment.model.ScspRespuesta;
import es.caib.pinbal.core.service.RecobrimentService;
import es.caib.pinbal.core.service.exception.RecobrimentScspException;
import es.caib.pinbal.core.service.exception.RecobrimentScspValidationException;

/**
 * Controlador pel servei REST de recobriment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/api/recobriment")
public class RecobrimentRestController extends BaseController {

	@Autowired
	private RecobrimentService recobrimentService;

	@RequestMapping(
			value= "/peticionSincrona",
			method = RequestMethod.POST,
			produces = "application/json")
	@ApiOperation(
			value = "Informe de petició síncrona de tipus SCSP", 
			notes = "Retorna una entitat de tipus ScspRespuesta i l'estatus") //, response=ArrayList.class)
	public ResponseEntity<ScspRespuesta> peticionSincrona(
			HttpServletRequest request,
			@ApiParam(name="peticion", value="Petició de tipus SCSP") 
			@RequestBody @Valid final ScspPeticion peticion) throws RecobrimentScspException {
		ScspRespuesta respuesta = recobrimentService.peticionSincrona(peticion);
		return new ResponseEntity<ScspRespuesta>(respuesta, HttpStatus.OK);
	}

	@RequestMapping(
			value= "/peticionAsincrona",
			method = RequestMethod.POST,
			produces = "application/json")
	@ApiOperation(
			value = "Informe de petició asíncrona de tipus SCSP", 
			notes = "Retorna una entitat de tipus ScspConfirmacionPeticion i l'estatus") //, response=ArrayList.class)
	public ResponseEntity<ScspConfirmacionPeticion> peticionAsincrona(
			HttpServletRequest request,
			@ApiParam(name="peticion", value="Petició de tipus SCSP") 
			@RequestBody @Valid final ScspPeticion peticion) throws RecobrimentScspException {
		ScspConfirmacionPeticion respuesta = recobrimentService.peticionAsincrona(peticion);
		return new ResponseEntity<ScspConfirmacionPeticion>(respuesta, HttpStatus.OK);
	}

	@RequestMapping(
			value= "/getRespuesta",
			method = RequestMethod.GET,
			produces = "application/json")
	@ApiOperation(
			value = "Informe de petició de tipus SCSP", 
			notes = "Retorna una entitat de tipus ScspRespuesta i l'estatus") //, response=ArrayList.class)
	public ResponseEntity<ScspRespuesta> getRespuesta(
			HttpServletRequest request,
			@ApiParam(name="idPeticion", value="Id de petició") 
			@RequestParam final String idPeticion) throws RecobrimentScspException {
		ScspRespuesta respuesta = recobrimentService.getRespuesta(idPeticion);
		return new ResponseEntity<ScspRespuesta>(respuesta, HttpStatus.OK);
	}

	@RequestMapping(
			value= "/getJustificante",
			method = RequestMethod.GET,
			produces = "application/json")
	@ApiOperation(
			value = "Justificant de petició de tipus SCSP", 
			notes = "Obté un justificant de la petició, de tipus ScspJustificante") //, response=ArrayList.class)
	public void getJustificante(
			HttpServletRequest request,
			HttpServletResponse response,
			@ApiParam(name="idPeticion", value="Id de petició") 
			@RequestParam final String idPeticion,
			@ApiParam(name="idSolicitud", value="Id de sol·licitud") 
			@RequestParam final String idSolicitud) throws RecobrimentScspException, IOException {
		ScspJustificante justificante = recobrimentService.getJustificante(idPeticion, idSolicitud);
		writeFileToResponse(
				justificante.getNom(),
				justificante.getContentType(),
				justificante.getContingut(),
				response);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleError(
			HttpServletRequest request,
			HttpServletResponse response,
			Exception ex) {
		if (ex instanceof RecobrimentScspValidationException) {
			return new ResponseEntity<ErrorResponse>(
					new ErrorResponse(ex.getMessage()),
					HttpStatus.BAD_REQUEST);
		} else {
			return new ResponseEntity<ErrorResponse>(
					new ErrorResponse(ex.getMessage(), ExceptionUtils.getStackTrace(ex)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public class ErrorResponse {
		private String message;
		private String trace;
		public ErrorResponse(String message) {
			super();
			this.message = message;
		}
		public ErrorResponse(String message, String trace) {
			super();
			this.message = message;
			this.trace = trace;
		}
		public String getMessage() {
			return message;
		}
		public String getTrace() {
			return trace;
		}
	}

}
