/**
 * 
 */
package es.caib.pinbal.api.interna.controller.recobriment.v2;

import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import es.caib.pinbal.api.interna.controller.PinbalHalRestController;
import es.caib.pinbal.api.interna.openapi.interficies.recobriment.v2.RecobrimentRestV2Intf;
import es.caib.pinbal.client.procediments.Procediment;
import es.caib.pinbal.client.recobriment.model.ScspConfirmacionPeticion;
import es.caib.pinbal.client.recobriment.model.ScspJustificante;
import es.caib.pinbal.client.recobriment.model.ScspPeticion;
import es.caib.pinbal.client.recobriment.model.ScspRespuesta;
import es.caib.pinbal.client.recobriment.v2.DadaEspecifica;
import es.caib.pinbal.client.recobriment.v2.Entitat;
import es.caib.pinbal.client.recobriment.v2.PeticioAsincrona;
import es.caib.pinbal.client.recobriment.v2.PeticioSincrona;
import es.caib.pinbal.client.recobriment.v2.ValorEnum;
import es.caib.pinbal.client.serveis.Servei;
import es.caib.pinbal.core.dto.apiresponse.ServiceExecutionException;
import es.caib.pinbal.core.service.RecobrimentService;
import es.caib.pinbal.core.service.exception.AccessDenegatException;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.core.service.exception.RecobrimentScspException;
import es.caib.pinbal.core.service.exception.ResourceNotFoundException;
import es.caib.pinbal.core.service.exception.ServeiNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Controlador pel servei REST de recobriment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/recobriment/v2")
public class RecobrimentRestV2Controller extends PinbalHalRestController implements RecobrimentRestV2Intf {


	@Autowired
	private RecobrimentService recobrimentService;

	// Obtencio de entitats
	// /////////////////////////////////////////////////////////////
	@RequestMapping(value = "/entitats", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Entitat>> getEntitats() {
		try {
			List<Entitat> entitats = recobrimentService.getEntitats();

			if (entitats == null || entitats.isEmpty() ) {
				return new ResponseEntity<List<Entitat>>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<List<Entitat>>(entitats, HttpStatus.OK);
		} catch (AccessDeniedException | AccessDenegatException ade) {
			throw new AccessDenegatException(Arrays.asList("PBL_WS"));
		} catch (Exception ex) {
			throw new ServiceExecutionException(ex.getMessage(), ex);
		}
	}

	// Obtencio de procediments
	// /////////////////////////////////////////////////////////////
	@RequestMapping(value = "/entitats/{entitatCodi}/procediments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Procediment>> getProcediments(@PathVariable("entitatCodi") String entitatCodi) {
		try {
			List<Procediment> procediments = recobrimentService.getProcediments(entitatCodi);

			if (procediments == null || procediments.isEmpty() ) {
				return new ResponseEntity<List<Procediment>>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<List<Procediment>>(procediments, HttpStatus.OK);
		} catch (EntitatNotFoundException e) {
			throw new ResourceNotFoundException(e.getDefaultMessage(), e);
		} catch (AccessDeniedException | AccessDenegatException ade) {
			throw new AccessDenegatException(Arrays.asList("PBL_WS"));
		} catch (Exception ex) {
			throw new ServiceExecutionException(ex.getMessage(), ex);
		}
	}

	// Obtenció de serveis
	// /////////////////////////////////////////////////////////////
	@RequestMapping(value = "/serveis", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Servei>> getServeis() {
		try {
			List<Servei> serveis = recobrimentService.getServeis();

			if (serveis == null || serveis.isEmpty() ) {
				return new ResponseEntity<List<Servei>>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<List<Servei>>(serveis, HttpStatus.OK);
		} catch (AccessDeniedException | AccessDenegatException ade) {
			throw new AccessDenegatException(Arrays.asList("PBL_WS"));
		} catch (Exception ex) {
			throw new ServiceExecutionException(ex.getMessage(), ex);
		}
	}

	@RequestMapping(value = "/entitat/{entitatCodi}/serveis", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Servei>> getServeisPerEntitat(@PathVariable("entitatCodi") String entitatCodi) {
		try {
			List<Servei> serveis = recobrimentService.getServeisByEntitat(entitatCodi);

			if (serveis == null || serveis.isEmpty() ) {
				return new ResponseEntity<List<Servei>>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<List<Servei>>(serveis, HttpStatus.OK);
		} catch (EntitatNotFoundException e) {
			throw new ResourceNotFoundException(e.getDefaultMessage(), e);
		} catch (AccessDeniedException | AccessDenegatException ade) {
			throw new AccessDenegatException(Arrays.asList("PBL_WS"));
		} catch (Exception ex) {
			throw new ServiceExecutionException(ex.getMessage(), ex);
		}
	}

	@RequestMapping(value = "/procediment/{procedimentCodi}/serveis", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Servei>> getServeisPerProcediment(@PathVariable("procedimentCodi") String procedimentCodi) {
		try {
			List<Servei> serveis = recobrimentService.getServeisByProcediment(procedimentCodi);

			if (serveis == null || serveis.isEmpty() ) {
				return new ResponseEntity<List<Servei>>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<List<Servei>>(serveis, HttpStatus.OK);
		} catch (ProcedimentNotFoundException e) {
			throw new ResourceNotFoundException(e.getDefaultMessage(), e);
		} catch (AccessDeniedException | AccessDenegatException ade) {
			throw new AccessDenegatException(Arrays.asList("PBL_WS"));
		} catch (Exception ex) {
			throw new ServiceExecutionException(ex.getMessage(), ex);
		}
	}

	// Obtenció de dades específiques
	// /////////////////////////////////////////////////////////////

	@RequestMapping(value = "/serveis/{serveiCodi}/dadesEspecifiques", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DadaEspecifica>> getDadesEspecifiques(@PathVariable("serveiCodi") String serveiCodi) {
		try {
			List<DadaEspecifica> dadesEspecifiques = recobrimentService.getDadesEspecifiquesByServei(serveiCodi);

			if (dadesEspecifiques == null || dadesEspecifiques.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<>(dadesEspecifiques, HttpStatus.OK);
		} catch (ServeiNotFoundException e) {
			throw new ResourceNotFoundException(e.getDefaultMessage(), e);
		} catch (AccessDeniedException | AccessDenegatException ade) {
			throw new AccessDenegatException(Arrays.asList("PBL_WS"));
		} catch (Exception ex) {
			throw new ServiceExecutionException(ex.getMessage(), ex);
		}
	}

	@RequestMapping(value = "/serveis/{serveiCodi}/camps/{campCodi}/enumerat/{enumCodi}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ValorEnum>> getValorsEnum(
			@PathVariable("serveiCodi")String serveiCodi,
			@PathVariable("campCodi")String campCodi,
			@PathVariable("enumCodi")String enumCodi,
			@RequestParam(required = false)String filtre) {
		try {
			// Decodificació del camp si conté caràcters especials
			campCodi = java.net.URLDecoder.decode(campCodi, "UTF-8");

			List<ValorEnum> valorsEnum = recobrimentService.getValorsEnumByServei(serveiCodi, campCodi, enumCodi, filtre);

			if (valorsEnum == null || valorsEnum.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<>(valorsEnum, HttpStatus.OK);
		} catch (ServeiNotFoundException e) {
			throw new ResourceNotFoundException(e.getDefaultMessage(), e);
		} catch (AccessDeniedException | AccessDenegatException ade) {
			throw new AccessDenegatException(Arrays.asList("PBL_WS"));
		} catch (Exception ex) {
			throw new ServiceExecutionException(ex.getMessage(), ex);
		}
	}

	// Realització de consultes
	// /////////////////////////////////////////////////////////////

	public ResponseEntity<ScspRespuesta> peticioSincrona(PeticioSincrona peticio) {
		return null;
	}
	public ResponseEntity<ScspConfirmacionPeticion> peticioAsincrona(PeticioAsincrona peticio) {
		return null;
	}
	public ResponseEntity<ScspRespuesta> getRespuesta(String idPeticio) {
		return null;
	}
	public ResponseEntity<ScspJustificante> getJustificant(String idPeticio, String idSolicitud) {
		return null;
	}
	public ResponseEntity<ScspJustificante> getJustificanteImprimible(String idPeticio, String idSolicitud) {
		return null;
	}


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

//	@RequestMapping(
//			value= "/peticionAsincrona",
//			method = RequestMethod.POST,
//			produces = "application/json")
//	@ApiOperation(
//			value = "Informe de petició asíncrona de tipus SCSP",
//			notes = "Retorna una entitat de tipus ScspConfirmacionPeticion i l'estatus") //, response=ArrayList.class)
//	public ResponseEntity<ScspConfirmacionPeticion> peticionAsincrona(
//			HttpServletRequest request,
//			@ApiParam(name="peticion", value="Petició de tipus SCSP")
//			@RequestBody @Valid final ScspPeticion peticion) throws RecobrimentScspException {
//		ScspConfirmacionPeticion respuesta = recobrimentService.peticionAsincrona(peticion);
//		return new ResponseEntity<ScspConfirmacionPeticion>(respuesta, HttpStatus.OK);
//	}
//
//	@RequestMapping(
//			value= "/getRespuesta",
//			method = RequestMethod.GET,
//			produces = "application/json")
//	@ApiOperation(
//			value = "Informe de petició de tipus SCSP",
//			notes = "Retorna una entitat de tipus ScspRespuesta i l'estatus") //, response=ArrayList.class)
//	public ResponseEntity<ScspRespuesta> getRespuesta(
//			HttpServletRequest request,
//			@ApiParam(name="idPeticion", value="Id de petició")
//			@RequestParam final String idPeticion) throws RecobrimentScspException {
//		ScspRespuesta respuesta = recobrimentService.getRespuesta(idPeticion);
//		return new ResponseEntity<ScspRespuesta>(respuesta, HttpStatus.OK);
//	}
//
//	@RequestMapping(
//			value= "/getJustificante",
//			method = RequestMethod.GET,
//			produces = "application/json")
//	@ApiOperation(
//			value = "Justificant de petició de tipus SCSP",
//			notes = "Obté un justificant de la petició, de tipus ScspJustificante") //, response=ArrayList.class)
//	public void getJustificante(
//			HttpServletRequest request,
//			HttpServletResponse response,
//			@ApiParam(name="idPeticion", value="Id de petició")
//			@RequestParam final String idPeticion,
//			@ApiParam(name="idSolicitud", value="Id de sol·licitud")
//			@RequestParam final String idSolicitud) throws RecobrimentScspException, IOException {
//		ScspJustificante justificante = recobrimentService.getJustificante(idPeticion, idSolicitud);
//		writeFileToResponse(
//				justificante.getNom(),
//				justificante.getContentType(),
//				justificante.getContingut(),
//				response);
//	}
//
//	@RequestMapping(
//			value= "/getJustificanteImprimible",
//			method = RequestMethod.GET,
//			produces = "application/json")
//	@ApiOperation(
//			value = "Versió imprimible del justificant de petició de tipus SCSP",
//			notes = "Obté la versió imprimible del justificant de la petició, de tipus ScspJustificante") //, response=ArrayList.class)
//	public void getJustificanteImprimible(
//			HttpServletRequest request,
//			HttpServletResponse response,
//			@ApiParam(name="idPeticion", value="Id de petició")
//			@RequestParam final String idPeticion,
//			@ApiParam(name="idSolicitud", value="Id de sol·licitud")
//			@RequestParam final String idSolicitud) throws RecobrimentScspException, IOException {
//		ScspJustificante justificante = recobrimentService.getJustificanteImprimible(idPeticion, idSolicitud);
//		writeFileToResponse(
//				justificante.getNom(),
//				justificante.getContentType(),
//				justificante.getContingut(),
//				response);
//	}
//
//	@RequestMapping(
//			value= "/getJustificanteCsv",
//			method = RequestMethod.GET,
//			produces = "application/json")
//	@ApiOperation(
//			value = "CSV del justificant de petició de tipus SCSP",
//			notes = "Obté el CSV del justificant de la petició, de tipus ScspJustificante") //, response=ArrayList.class)
//	public ResponseEntity<String> getJustificanteCsv(
//			HttpServletRequest request,
//			HttpServletResponse response,
//			@ApiParam(name="idPeticion", value="Id de petició")
//			@RequestParam final String idPeticion,
//			@ApiParam(name="idSolicitud", value="Id de sol·licitud")
//			@RequestParam final String idSolicitud) throws RecobrimentScspException, IOException {
//		String justificanteCsv = recobrimentService.getJustificanteCsv(idPeticion, idSolicitud);
//		return new ResponseEntity<String>("", HttpStatus.OK);
//	}
//
//	@RequestMapping(
//			value= "/getJustificanteUuId",
//			method = RequestMethod.GET,
//			produces = "application/json")
//	@ApiOperation(
//			value = "Uuid del justificant de petició de tipus SCSP",
//			notes = "Obté l'Uuid del justificant de la petició, de tipus ScspJustificante") //, response=ArrayList.class)
//	public ResponseEntity<String> getJustificanteUuid(
//			HttpServletRequest request,
//			HttpServletResponse response,
//			@ApiParam(name="idPeticion", value="Id de petició")
//			@RequestParam final String idPeticion,
//			@ApiParam(name="idSolicitud", value="Id de sol·licitud")
//			@RequestParam final String idSolicitud) throws RecobrimentScspException, IOException {
//		String justificanteUuid = recobrimentService.getJustificanteUuid(idPeticion, idSolicitud);
//		return new ResponseEntity<String>("", HttpStatus.OK);
//	}
//
//	@ExceptionHandler(Exception.class)
//	public ResponseEntity<ErrorResponse> handleError(
//			HttpServletRequest request,
//			HttpServletResponse response,
//			Exception ex) {
//		if (ex instanceof RecobrimentScspValidationException) {
//			return new ResponseEntity<ErrorResponse>(
//					new ErrorResponse(ex.getMessage()),
//					HttpStatus.BAD_REQUEST);
//		} else {
//			return new ResponseEntity<ErrorResponse>(
//					new ErrorResponse(ex.getMessage(), ExceptionUtils.getStackTrace(ex)),
//					HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}

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
