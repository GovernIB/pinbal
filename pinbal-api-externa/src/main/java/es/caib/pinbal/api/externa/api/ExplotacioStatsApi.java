package es.caib.pinbal.api.externa.api;

import es.caib.pinbal.client.comu.EntitatEstadistiques;
import es.caib.pinbal.client.comu.ErrorResponse;
import es.caib.pinbal.client.comu.ProcedimentEstadistiques;
import es.caib.pinbal.logic.intf.dto.ConsultaDto.EstatTipus;
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
		name = "Estadistiques",
		description = "Estadistiques d'explotacio de consultes i carrega de PINBAL.")
public interface ExplotacioStatsApi {

	String ESTADISTIQUES_CONSULTES_EXAMPLE = "[{"
			+ "\"codi\":\"PROC001\","
			+ "\"nom\":\"Ajut d'exemple\","
			+ "\"consultesWeb\":{\"ok\":82,\"error\":1},"
			+ "\"consultesRecobriment\":{\"ok\":42,\"error\":2},"
			+ "\"consultesTotal\":{\"ok\":124,\"error\":3},"
			+ "\"serveis\":[{"
			+ "\"codi\":\"SVCDATOS\","
			+ "\"nom\":\"Consulta de dades d'identitat\","
			+ "\"consultesWeb\":{\"ok\":82,\"error\":1},"
			+ "\"consultesRecobriment\":{\"ok\":42,\"error\":2},"
			+ "\"consultesTotal\":{\"ok\":124,\"error\":3}"
			+ "}]"
			+ "}]";

	String ESTADISTIQUES_CARREGA_EXAMPLE = "[{"
			+ "\"codi\":\"A04003003\","
			+ "\"nom\":\"Conselleria d'Exemple\","
			+ "\"nif\":\"S0711001H\","
			+ "\"departaments\":[{"
			+ "\"nom\":\"Departament de Serveis Generals\","
			+ "\"procediments\":[{"
			+ "\"codi\":\"PROC001\","
			+ "\"nom\":\"Ajut d'exemple\","
			+ "\"serveis\":[{"
			+ "\"codi\":\"SVCDATOS\","
			+ "\"nom\":\"Consulta de dades d'identitat\","
			+ "\"totalWeb\":{\"any\":6200,\"mes\":480,\"dia\":28,\"hora\":6,\"minut\":1},"
			+ "\"totalRecobriment\":{\"any\":4100,\"mes\":360,\"dia\":19,\"hora\":4,\"minut\":0}"
			+ "}]"
			+ "}]"
			+ "}]"
			+ "}]";

	String ERROR_AUTH_EXAMPLE = "{"
			+ "\"errorCode\":\"UNAUTHORIZED\","
			+ "\"errorMessage\":\"Autenticacio requerida\""
			+ "}";

	String ERROR_FORBIDDEN_EXAMPLE = "{"
			+ "\"errorCode\":\"FORBIDDEN\","
			+ "\"errorMessage\":\"L'usuari no te el rol PBL_REPORT\""
			+ "}";

	String ERROR_VALIDACIO_EXAMPLE = "{"
			+ "\"errorCode\":\"VALIDATION_ERROR\","
			+ "\"errorMessage\":\"El parametre estat ha de ser Pendent, Processant, Tramitada o Error\""
			+ "}";

	String ERROR_NO_TROBAT_EXAMPLE = "{"
			+ "\"errorCode\":\"RESOURCE_NOT_FOUND\","
			+ "\"errorMessage\":\"Entitat o procediment no trobat\""
			+ "}";

	@Operation(
			summary = "Estadistiques de consultes",
			description = "Retorna estadistiques de consultes agrupades per procediment i servei, amb totals correctes i erronis per canal web, recobriment i total.",
			operationId = "obtenirEstadistiquesConsultes",
			tags = {"Estadistiques"})
	@ApiResponses({
			@ApiResponse(
					responseCode = "200",
					description = "Estadistiques de consultes retornades correctament.",
					content = @Content(
							mediaType = "application/json",
							array = @ArraySchema(schema = @Schema(implementation = ProcedimentEstadistiques.class)),
							examples = @ExampleObject(
									name = "Estadistiques de consultes",
									value = ESTADISTIQUES_CONSULTES_EXAMPLE))),
			@ApiResponse(
					responseCode = "400",
					description = "Parametres obligatoris absents o amb format incorrecte.",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(name = "Error de validacio", value = ERROR_VALIDACIO_EXAMPLE))),
			@ApiResponse(
					responseCode = "401",
					description = "No autenticat.",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(name = "No autenticat", value = ERROR_AUTH_EXAMPLE))),
			@ApiResponse(
					responseCode = "403",
					description = "L'usuari autenticat no disposa del rol PBL_REPORT.",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(name = "Sense permisos", value = ERROR_FORBIDDEN_EXAMPLE))),
			@ApiResponse(
					responseCode = "404",
					description = "L'entitat o el procediment indicat no existeix.",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(name = "Recurs no trobat", value = ERROR_NO_TROBAT_EXAMPLE))),
			@ApiResponse(
					responseCode = "500",
					description = "Error intern en obtenir les estadistiques de consultes.",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(
									name = "Error intern",
									value = "{\"errorCode\":\"INTERNAL_SERVER_ERROR\",\"errorMessage\":\"Error intern del servidor\"}")))
	})
	ResponseEntity<List<ProcedimentEstadistiques>> consultes(
			@Parameter(hidden = true) HttpServletRequest request,
			@Parameter(
					name = "entitatCodi",
					description = "Codi de l'entitat sobre la qual es calculen les estadistiques.",
					required = true,
					example = "A04003003",
					schema = @Schema(type = "string", maxLength = 64))
			String entitatCodi,
			@Parameter(
					name = "procedimentCodi",
					description = "Codi del procediment pel qual es vol filtrar.",
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
					name = "estat",
					description = "Estat de les consultes a incloure en el filtre.",
					required = false,
					example = "Tramitada",
					schema = @Schema(implementation = EstatTipus.class, allowableValues = {"Pendent", "Processant", "Tramitada", "Error"}))
			EstatTipus estat,
			@Parameter(
					name = "dataInici",
					description = "Data i hora d'inici del periode estadistic, interpretada segons la configuracio de conversio de dates de l'aplicacio.",
					required = false,
					example = "2026-01-01T00:00:00",
					schema = @Schema(type = "string", format = "date-time"))
			Date dataInici,
			@Parameter(
					name = "dataFi",
					description = "Data i hora de fi del periode estadistic, interpretada segons la configuracio de conversio de dates de l'aplicacio.",
					required = false,
					example = "2026-01-31T23:59:59",
					schema = @Schema(type = "string", format = "date-time"))
			Date dataFi) throws EntitatNotFoundException, ProcedimentNotFoundException;

	@Operation(
			summary = "Estadistiques de carrega",
			description = "Retorna estadistiques acumulades de carrega agrupades per entitat, departament, procediment i servei.",
			operationId = "obtenirEstadistiquesCarrega",
			tags = {"Estadistiques"})
	@ApiResponses({
			@ApiResponse(
					responseCode = "200",
					description = "Estadistiques de carrega retornades correctament.",
					content = @Content(
							mediaType = "application/json",
							array = @ArraySchema(schema = @Schema(implementation = EntitatEstadistiques.class)),
							examples = @ExampleObject(
									name = "Estadistiques de carrega",
									value = ESTADISTIQUES_CARREGA_EXAMPLE))),
			@ApiResponse(
					responseCode = "401",
					description = "No autenticat.",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(name = "No autenticat", value = ERROR_AUTH_EXAMPLE))),
			@ApiResponse(
					responseCode = "403",
					description = "L'usuari autenticat no disposa del rol PBL_REPORT.",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(name = "Sense permisos", value = ERROR_FORBIDDEN_EXAMPLE))),
			@ApiResponse(
					responseCode = "500",
					description = "Error intern en obtenir les estadistiques de carrega.",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(
									name = "Error intern",
									value = "{\"errorCode\":\"INTERNAL_SERVER_ERROR\",\"errorMessage\":\"Error intern del servidor\"}")))
	})
	ResponseEntity<List<EntitatEstadistiques>> carrega(
			@Parameter(hidden = true) HttpServletRequest request);

}
