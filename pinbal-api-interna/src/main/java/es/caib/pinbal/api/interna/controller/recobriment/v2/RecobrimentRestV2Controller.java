/**
 * 
 */
package es.caib.pinbal.api.interna.controller.recobriment.v2;

import es.caib.pinbal.api.interna.controller.PinbalHalRestController;
import es.caib.pinbal.api.interna.openapi.interficies.recobriment.v2.RecobrimentRestV2Intf;
import es.caib.pinbal.client.procediments.Procediment;
import es.caib.pinbal.client.recobriment.model.ScspJustificante;
import es.caib.pinbal.client.recobriment.model.ScspRespuesta;
import es.caib.pinbal.client.recobriment.v2.DadaEspecifica;
import es.caib.pinbal.client.recobriment.v2.Entitat;
import es.caib.pinbal.client.recobriment.v2.PeticioAsincrona;
import es.caib.pinbal.client.recobriment.v2.PeticioRespostaAsincrona;
import es.caib.pinbal.client.recobriment.v2.PeticioRespostaSincrona;
import es.caib.pinbal.client.recobriment.v2.PeticioSincrona;
import es.caib.pinbal.client.recobriment.v2.ValorEnum;
import es.caib.pinbal.client.serveis.Servei;
import es.caib.pinbal.core.dto.apiresponse.ServiceExecutionException;
import es.caib.pinbal.core.service.RecobrimentService;
import es.caib.pinbal.core.service.exception.AccessDenegatException;
import es.caib.pinbal.core.service.exception.ConsultaNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.core.service.exception.ResourceNotFoundException;
import es.caib.pinbal.core.service.exception.ServeiNotFoundException;
import lombok.extern.slf4j.Slf4j;
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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Controlador pel servei REST de recobriment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
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

	@RequestMapping(value = "/entitats/{entitatCodi}/serveis", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
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

	@RequestMapping(value = "/procediments/{procedimentCodi}/serveis", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
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
			@PathVariable("serveiCodi") String serveiCodi,
			@PathVariable("campCodi") String campCodi,
			@PathVariable("enumCodi") String enumCodi,
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

	@RequestMapping(value= "/serveis/{serveiCodi}/peticioSincrona", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PeticioRespostaSincrona> peticioSincrona(@RequestBody PeticioSincrona peticio) {
		try {
			Map<String, List<String>> errors = recobrimentService.validatePeticio(peticio);
			if (!errors.isEmpty()) {
				PeticioRespostaSincrona respuesta = PeticioRespostaSincrona.builder()
						.error(true)
						.errorsValidacio(errors)
						.messageError("S'han produït errors en la validació de les dades de la petició.")
						.build();
				return new ResponseEntity<>(respuesta, HttpStatus.OK);
			}
			PeticioRespostaSincrona respuesta = recobrimentService.peticionSincrona(peticio);
			return new ResponseEntity<>(respuesta, HttpStatus.OK);
		} catch (Exception ex) {
			throw new ServiceExecutionException(ex.getMessage(), ex);
		}
	}

	@RequestMapping(value= "/serveis/{serveiCodi}/peticioAsincrona", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PeticioRespostaAsincrona> peticioAsincrona(@RequestBody PeticioAsincrona peticio) {
		try{
			Map<String, List<String>> errors = recobrimentService.validatePeticio(peticio);
			if (!errors.isEmpty()) {
				PeticioRespostaAsincrona respuesta = PeticioRespostaAsincrona.builder()
						.error(true)
						.errorsValidacio(errors)
						.messageError("S'han produït errors en la validació de les dades de la petició.")
						.build();
				return new ResponseEntity<>(respuesta, HttpStatus.OK);
			}
			PeticioRespostaAsincrona respuesta = recobrimentService.peticionAsincrona(peticio);
			return new ResponseEntity<>(respuesta, HttpStatus.OK);
		} catch (Exception ex) {
			throw new ServiceExecutionException(ex.getMessage(), ex);
		}
	}

	// Obtenció de respostes
	// /////////////////////////////////////////////////////////////

	@RequestMapping(value= "/consultes/{idPeticio}/resposta", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PeticioRespostaSincrona> getResposta(@PathVariable("idPeticio") String idPeticio) {

		PeticioRespostaSincrona resposta = null;
		try {

			ScspRespuesta scspRespuesta = recobrimentService.getResposta(idPeticio);
			if (scspRespuesta == null) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			resposta = PeticioRespostaSincrona.builder()
					.error(!scspRespuesta.getAtributos().getEstado().getCodigoEstado().startsWith("00"))
					.messageError(scspRespuesta.getAtributos().getEstado().getLiteralError())
					.resposta(scspRespuesta)
					.build();
			return new ResponseEntity<>(resposta, HttpStatus.OK);
		} catch (ConsultaNotFoundException ce) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (Exception ex) {
			throw new ServiceExecutionException(ex.getMessage(), ex);
		}

	}

	@RequestMapping(value= "/consultes/{idPeticio}/solicitud/{idSolicitud}/justificant", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ScspJustificante> getJustificant(
			@PathVariable("idPeticio") String idPeticio,
			@PathVariable("idSolicitud") String idSolicitud) throws Exception {

		ScspJustificante justificant = null;
		try {
			justificant = recobrimentService.getJustificant(idPeticio, idSolicitud);
			if (justificant == null) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<>(justificant, HttpStatus.OK);
		} catch (ConsultaNotFoundException ce) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (Exception ex) {
			throw new ServiceExecutionException(ex.getMessage(), ex);
		}
	}

	@RequestMapping(value= "/consultes/{idPeticio}/solicitud/{idSolicitud}/justificantImprimible", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ScspJustificante> getJustificantImprimible(
			@PathVariable("idPeticio") String idPeticio,
			@PathVariable("idSolicitud") String idSolicitud) throws Exception {

		ScspJustificante justificant = null;
		try {
			justificant = recobrimentService.getJustificantImprimible(idPeticio, idSolicitud);
			if (justificant == null) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<>(justificant, HttpStatus.OK);
		} catch (ConsultaNotFoundException ce) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (Exception ex) {
			throw new ServiceExecutionException(ex.getMessage(), ex);
		}
	}

	@RequestMapping(value= "/consultes/{idPeticio}/solicitud/{idSolicitud}/justificantCsv", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getJustificantCsv(
			@PathVariable("idPeticio") String idPeticio,
			@PathVariable("idSolicitud") String idSolicitud) throws Exception {
		String justificanteCsv = null;
		try {
			justificanteCsv = recobrimentService.getJustificantCsv(idPeticio, idSolicitud);
			if (justificanteCsv == null) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<>(justificanteCsv, HttpStatus.OK);
		} catch (ConsultaNotFoundException ce) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (Exception ex) {
			throw new ServiceExecutionException(ex.getMessage(), ex);
		}
	}

	@RequestMapping(value= "/consultes/{idPeticio}/solicitud/{idSolicitud}/justificantUuid", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getJustificantUuid(
			@PathVariable("idPeticio") String idPeticio,
			@PathVariable("idSolicitud") String idSolicitud) throws Exception {
		String justificantUuid = null;
		try {
			justificantUuid = recobrimentService.getJustificantUuid(idPeticio, idSolicitud);
			if (justificantUuid == null) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<>(justificantUuid, HttpStatus.OK);
		} catch (ConsultaNotFoundException ce) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (Exception ex) {
			throw new ServiceExecutionException(ex.getMessage(), ex);
		}
	}

}
