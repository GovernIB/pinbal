/**
 * 
 */
package es.caib.pinbal.api.interna.controller;

import es.caib.pinbal.api.interna.api.RecobrimentApi;
import es.caib.pinbal.client.recobriment.model.ScspConfirmacionPeticion;
import es.caib.pinbal.client.recobriment.model.ScspJustificante;
import es.caib.pinbal.client.recobriment.model.ScspPeticion;
import es.caib.pinbal.client.recobriment.model.ScspRespuesta;
import es.caib.pinbal.logic.intf.service.RecobrimentService;
import es.caib.pinbal.logic.intf.service.exception.RecobrimentScspException;
import es.caib.pinbal.logic.intf.service.exception.RecobrimentScspValidationException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

/**
 * Controlador pel servei REST de recobriment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/recobriment")
public class RecobrimentRestController implements RecobrimentApi {

	private final RecobrimentService recobrimentService;

	@Override
	@GetMapping(value= "/test", produces = "application/json")
	// IMPORTANT: Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI definida a la interfície RecobrimentApi.
	public ResponseEntity<String> test() {
		return new ResponseEntity<String>("Test successful", HttpStatus.OK);
	}


	@Override
	@PostMapping(value= "/peticionSincrona", produces = "application/json")
	// IMPORTANT: Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI definida a la interfície RecobrimentApi.
	public ResponseEntity<ScspRespuesta> peticionSincrona(
			HttpServletRequest request,
			@RequestBody @Valid final ScspPeticion peticion) throws RecobrimentScspException {
		ScspRespuesta respuesta = recobrimentService.peticionSincrona(peticion);
		return new ResponseEntity<ScspRespuesta>(respuesta, HttpStatus.OK);
	}

	@Override
	@PostMapping(value= "/peticionAsincrona", produces = "application/json")
	// IMPORTANT: Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI definida a la interfície RecobrimentApi.
	public ResponseEntity<ScspConfirmacionPeticion> peticionAsincrona(
			HttpServletRequest request,
			@RequestBody @Valid final ScspPeticion peticion) throws RecobrimentScspException {
		ScspConfirmacionPeticion respuesta = recobrimentService.peticionAsincrona(peticion);
		return new ResponseEntity<ScspConfirmacionPeticion>(respuesta, HttpStatus.OK);
	}

	@GetMapping(value= "/getRespuesta", produces = "application/json")
	@Override
	// IMPORTANT: Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI definida a la interfície RecobrimentApi.
	public ResponseEntity<ScspRespuesta> getRespuesta(
			HttpServletRequest request,
			@RequestParam final String idPeticion) throws RecobrimentScspException {
		ScspRespuesta respuesta = recobrimentService.getRespuesta(idPeticion);
		return new ResponseEntity<ScspRespuesta>(respuesta, HttpStatus.OK);
	}

	@GetMapping(value= "/getJustificante", produces = "application/json")
	@Override
	// IMPORTANT: Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI definida a la interfície RecobrimentApi.
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

	@GetMapping(value= "/getJustificanteImprimible", produces = "application/json")
	@Override
	// IMPORTANT: Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI definida a la interfície RecobrimentApi.
	public void getJustificanteImprimible(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam final String idPeticion,
			@RequestParam final String idSolicitud) throws RecobrimentScspException, IOException {
		ScspJustificante justificante = recobrimentService.getJustificanteImprimible(idPeticion, idSolicitud);
		writeFileToResponse(
				justificante.getNom(),
				justificante.getContentType(),
				justificante.getContingut(),
				response);
	}

	@GetMapping(value= "/getJustificanteCsv", produces = "application/json")
	@Override
	// IMPORTANT: Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI definida a la interfície RecobrimentApi.
	public ResponseEntity<String> getJustificanteCsv(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam final String idPeticion,
			@RequestParam final String idSolicitud) throws RecobrimentScspException, IOException {
		String justificanteCsv = recobrimentService.getJustificanteCsv(idPeticion, idSolicitud);
		return new ResponseEntity<String>("", HttpStatus.OK);
	}

	@GetMapping(value= "/getJustificanteUuId", produces = "application/json")
	@Override
	// IMPORTANT: Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI definida a la interfície RecobrimentApi.
	public ResponseEntity<String> getJustificanteUuid(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam final String idPeticion,
			@RequestParam final String idSolicitud) throws RecobrimentScspException, IOException {
		String justificanteUuid = recobrimentService.getJustificanteUuid(idPeticion, idSolicitud);
		return new ResponseEntity<String>("", HttpStatus.OK);
	}

	@Override
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

	private void writeFileToResponse(
			String fileName,
			String contentType,
			byte[] fileContent,
			HttpServletResponse response) throws IOException {
		response.setHeader("Pragma", "");
		response.setHeader("Expires", "");
		response.setHeader("Cache-Control", "");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		if (contentType != null) {
			response.setContentType(contentType);
		} else {
			response.setContentType(new MimetypesFileTypeMap().getContentType(fileName));
		}
		response.getOutputStream().write(fileContent);
	}

}
