package es.caib.pinbal.api.externa.api;

import es.caib.pinbal.client.comu.ErrorResponse;
import es.caib.pinbal.client.dadesobertes.DadesObertesRespostaConsulta;
import es.caib.pinbal.logic.intf.service.exception.EntitatNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.ProcedimentNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Tag(
		name = "Dades obertes",
		description = "Consulta publica de dades obertes de PINBAL amb filtres d'entitat, procediment, servei i periode.")
public interface ExplotacioDadesObertesApi {

	String DADES_OBERTES_RESPONSE_EXAMPLE = "[{"
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
			+ "}]";

	String ERROR_VALIDACIO_EXAMPLE = "{"
			+ "\"errorCode\":\"VALIDATION_ERROR\","
			+ "\"errorMessage\":\"El parametre dataInici no te un format valid. Usa ISO date-time, per exemple 2026-01-01T00:00:00\""
			+ "}";

	String ERROR_NO_TROBAT_EXAMPLE = "{"
			+ "\"errorCode\":\"RESOURCE_NOT_FOUND\","
			+ "\"errorMessage\":\"Entitat o procediment no trobat\""
			+ "}";

	@Operation(
			summary = "Consulta dades obertes",
			description = "Retorna les consultes de dades obertes agrupades amb informacio d'entitat, departament, procediment i servei. Permet consultar dades actuals o historiques i filtrar per periode, entitat, procediment i servei.",
			operationId = "consultarDadesObertes",
			tags = {"Dades obertes"})
	@ApiResponses({
			@ApiResponse(
					responseCode = "200",
					description = "Llista de registres de dades obertes retornada correctament.",
					content = @Content(
							mediaType = "application/json",
							array = @ArraySchema(schema = @Schema(implementation = DadesObertesRespostaConsulta.class)),
							examples = @ExampleObject(
									name = "Resposta",
									value = DADES_OBERTES_RESPONSE_EXAMPLE))),
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
					description = "Error intern en consultar les dades obertes.",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(
									name = "Error intern",
									value = "{\"errorCode\":\"INTERNAL_SERVER_ERROR\",\"errorMessage\":\"Error intern del servidor\"}")))
	})
	ResponseEntity<List<DadesObertesRespostaConsulta>> opendata(
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
					description = "Data i hora d'inici del periode de consulta en format ISO date-time.",
					required = true,
					example = "2026-01-01T00:00:00",
					schema = @Schema(type = "string", format = "date-time"))
			Date dataInici,
			@Parameter(
					name = "dataFi",
					description = "Data i hora de fi del periode de consulta en format ISO date-time.",
					required = false,
					example = "2026-01-31T23:59:59",
					schema = @Schema(type = "string", format = "date-time"))
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
			String serveiCodi) throws EntitatNotFoundException, ProcedimentNotFoundException;

}
