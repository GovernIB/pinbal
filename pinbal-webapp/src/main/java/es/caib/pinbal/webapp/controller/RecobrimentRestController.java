/**
 * 
 */
package es.caib.pinbal.webapp.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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
	public ResponseEntity<ScspRespuesta> peticionSincrona(
			HttpServletRequest request,
			@RequestBody @Valid final ScspPeticion peticion) throws RecobrimentScspException {
		ScspRespuesta respuesta = recobrimentService.peticionSincrona(peticion);
		return new ResponseEntity<ScspRespuesta>(respuesta, HttpStatus.OK);
	}

	@RequestMapping(
			value= "/peticionAsincrona",
			method = RequestMethod.POST,
			produces = "application/json")
	public ResponseEntity<ScspConfirmacionPeticion> peticionAsincrona(
			HttpServletRequest request,
			@RequestBody @Valid final ScspPeticion peticion) throws RecobrimentScspException {
		ScspConfirmacionPeticion respuesta = recobrimentService.peticionAsincrona(peticion);
		return new ResponseEntity<ScspConfirmacionPeticion>(respuesta, HttpStatus.OK);
	}

	@RequestMapping(
			value= "/getRespuesta",
			method = RequestMethod.GET,
			produces = "application/json")
	public ResponseEntity<ScspRespuesta> getRespuesta(
			HttpServletRequest request,
			@RequestParam final String idPeticion) throws RecobrimentScspException {
		ScspRespuesta respuesta = recobrimentService.getRespuesta(idPeticion);
		return new ResponseEntity<ScspRespuesta>(respuesta, HttpStatus.OK);
	}

	@RequestMapping(
			value= "/getJustificante",
			method = RequestMethod.GET,
			produces = "application/json")
	public void getJustificante(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam final String idPeticion,
			@RequestParam final String idSolicitud) throws RecobrimentScspException, IOException {
		ScspJustificante justificante = recobrimentService.getJustificante(idPeticion, idSolicitud);
		writeFileToResponse(
				justificante.getNom(),
				justificante.getContentType(),
				justificante.getContingut(),
				response);
	}

	@ExceptionHandler(RecobrimentScspValidationException.class)
	public ResponseEntity<ErrorResponse> handleError(
			HttpServletRequest request,
			HttpServletResponse response,
			RecobrimentScspValidationException ex) {
		return new ResponseEntity<ErrorResponse>(
				new ErrorResponse(ex.getMessage()),
				HttpStatus.BAD_REQUEST);
	}

	public class ErrorResponse {
		private String message;
		public ErrorResponse(String message) {
			super();
			this.message = message;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
	}

}
