package es.caib.pinbal.api.externa.api;

import es.caib.pinbal.client.comu.ErrorResponse;
import es.caib.pinbal.client.dadesobertes.DadesObertesResposta;
import es.caib.pinbal.logic.intf.service.exception.EntitatNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.ProcedimentNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Tag(
		name = "Dades obertes v2",
		description = "Consulta publica paginada de dades obertes de PINBAL.")
public interface ExplotacioDadesObertesV2Api {

	String DADES_OBERTES_V2_RESPONSE_EXAMPLE = "{"
			+ "\"totalElements\":125,"
			+ "\"paginaActual\":0,"
			+ "\"totalPagines\":3,"
			+ "\"properaPagina\":\"https://se.caib.es/pinbalapi/externa/v2/opendata?pagina=1&mida=50\","
			+ "\"dades\":[{"
			+ "\"entitatCodi\":\"A04003003\","
			+ "\"entitatNom\":\"Conselleria d'Exemple\","
			+ "\"entitatNif\":\"S0711001H\","
			+ "\"entitatTipus\":\"CAIB\","
			+ "\"departamentCodi\":\"DEP001\","
			+ "\"departamentNom\":\"Departament de Serveis Generals\","
			+ "\"procedimentCodi\":\"PROC001\","
			+ "\"procedimentNom\":\"Ajut d'exemple\","
			+ "\"serveiCodi\":\"SVCDATOS\","
			+ "\"serveiNom\":\"Consulta de dades d'identitat\","
			+ "\"emissor\":\"Direccio General d'Exemple\","
			+ "\"emissorNif\":\"S2800001A\","
			+ "\"consentiment\":\"Llei\","
			+ "\"finalitat\":\"Tramitacio administrativa\","
			+ "\"titularTipusDoc\":\"NIF\","
			+ "\"solicitudId\":\"PINBAL-2026-000001\","
			+ "\"data\":\"2026-01-15T09:30:00Z\","
			+ "\"tipus\":\"WEB\","
			+ "\"resultat\":\"OK\""
			+ "}]"
			+ "}";

	String ERROR_VALIDACIO_EXAMPLE = "{"
			+ "\"errorCode\":\"VALIDATION_ERROR\","
			+ "\"errorMessage\":\"El parametre dataInici no te un format valid. Usa yyyy-MM-dd, per exemple 2026-01-01\""
			+ "}";

	String ERROR_NO_TROBAT_EXAMPLE = "{"
			+ "\"errorCode\":\"RESOURCE_NOT_FOUND\","
			+ "\"errorMessage\":\"Entitat o procediment no trobat\""
			+ "}";

	@Operation(
			summary = "Consulta dades obertes paginades",
			description = "Retorna una resposta paginada de consultes de dades obertes. Si s'indica historic=true s'utilitzen les dades historiques; en cas contrari es consulten les dades operatives.",
			operationId = "consultarDadesObertesV2",
			tags = {"Dades obertes v2"})
	@ApiResponses({
			@ApiResponse(
					responseCode = "200",
					description = "Pagina de registres de dades obertes retornada correctament.",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = DadesObertesResposta.class),
							examples = @ExampleObject(
									name = "Resposta paginada",
									value = DADES_OBERTES_V2_RESPONSE_EXAMPLE))),
			@ApiResponse(
					responseCode = "400",
					description = "Parametres obligatoris absents o amb format incorrecte.",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(
									name = "Error de validacio",
									value = ERROR_VALIDACIO_EXAMPLE))),
			@ApiResponse(
					responseCode = "404",
					description = "L'entitat o el procediment indicat no existeix.",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(
									name = "Recurs no trobat",
									value = ERROR_NO_TROBAT_EXAMPLE))),
			@ApiResponse(
					responseCode = "500",
					description = "Error intern en consultar les dades obertes paginades.",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(
									name = "Error intern",
									value = "{\"errorCode\":\"INTERNAL_SERVER_ERROR\",\"errorMessage\":\"Error intern del servidor\"}")))
	})
	ResponseEntity<DadesObertesResposta> opendata(
			@Parameter(hidden = true) HttpServletRequest request,
			@Parameter(
					name = "historic",
					description = "Indica si s'ha d'utilitzar la informacio historica de consultes.",
					required = false,
					example = "false",
					schema = @Schema(type = "boolean", defaultValue = "false"))
			boolean historic,
			@Parameter(
					name = "entitatCodi",
					description = "Codi de l'entitat sobre la qual es vol filtrar la consulta.",
					required = false,
					example = "A04003003",
					schema = @Schema(type = "string", maxLength = 64))
			String entitatCodi,
			@Parameter(
					name = "dataInici",
					description = "Data inicial de la consulta en format yyyy-MM-dd.",
					required = true,
					example = "2026-01-01",
					schema = @Schema(type = "string", format = "date"))
			Date dataInici,
			@Parameter(
					name = "dataFi",
					description = "Data final de la consulta en format yyyy-MM-dd. Si no s'informa, el servei aplica el comportament per defecte.",
					required = false,
					example = "2026-01-31",
					schema = @Schema(type = "string", format = "date"))
			Date dataFi,
			@Parameter(
					name = "procedimentCodi",
					description = "Codi del procediment administratiu pel qual es vol filtrar.",
					required = false,
					example = "PROC001",
					schema = @Schema(type = "string", maxLength = 64))
			String procedimentCodi,
			@Parameter(
					name = "serveiCodi",
					description = "Codi del servei d'interoperabilitat pel qual es vol filtrar.",
					required = false,
					example = "SVCDATOS",
					schema = @Schema(type = "string", maxLength = 64))
			String serveiCodi,
			@Parameter(
					name = "pagina",
					description = "Numero de pagina a mostrar. La paginacio comenca a 0; si no s'informa es retorna la primera pagina.",
					required = false,
					example = "0",
					schema = @Schema(type = "integer", minimum = "0", defaultValue = "0"))
			Integer pagina,
			@Parameter(
					name = "mida",
					description = "Mida de la pagina a retornar. Si no s'informa el servei aplica el valor per defecte.",
					required = false,
					example = "50",
					schema = @Schema(type = "integer", minimum = "1", defaultValue = "50"))
			Integer mida) throws EntitatNotFoundException, ProcedimentNotFoundException;

}
