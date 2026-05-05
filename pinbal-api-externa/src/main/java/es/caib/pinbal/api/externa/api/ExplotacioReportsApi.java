package es.caib.pinbal.api.externa.api;

import es.caib.pinbal.client.comu.EntitatEstadistiques;
import es.caib.pinbal.client.comu.ErrorResponse;
import es.caib.pinbal.client.comu.ServeiEstadistiques;
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
		name = "Informes",
		description = "Informes d'explotacio de PINBAL per a procediments, usuaris, serveis i estat general.")
public interface ExplotacioReportsApi {

	String INFORME_PROCEDIMENTS_EXAMPLE = "[{"
			+ "\"codi\":\"A04003003\","
			+ "\"nom\":\"Conselleria d'Exemple\","
			+ "\"nif\":\"S0711001H\","
			+ "\"departaments\":[{"
			+ "\"nom\":\"Departament de Serveis Generals\","
			+ "\"procediments\":[{"
			+ "\"codi\":\"PROC001\","
			+ "\"nom\":\"Ajut d'exemple\","
			+ "\"actiu\":true"
			+ "}]"
			+ "}]"
			+ "}]";

	String INFORME_USUARIS_EXAMPLE = "[{"
			+ "\"codi\":\"A04003003\","
			+ "\"nom\":\"Conselleria d'Exemple\","
			+ "\"nif\":\"S0711001H\","
			+ "\"departaments\":[{"
			+ "\"nom\":\"Departament de Serveis Generals\","
			+ "\"usuaris\":[{"
			+ "\"codi\":\"jgarcia\","
			+ "\"nom\":\"Joan Garcia\","
			+ "\"nif\":\"43123456Z\""
			+ "}]"
			+ "}]"
			+ "}]";

	String INFORME_SERVEIS_EXAMPLE = "[{"
			+ "\"codi\":\"SVCDATOS\","
			+ "\"nom\":\"Consulta de dades d'identitat\","
			+ "\"emisor\":\"Direccio General d'Exemple\""
			+ "}]";

	String INFORME_GENERAL_EXAMPLE = "[{"
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
			+ "\"emisor\":\"Direccio General d'Exemple\","
			+ "\"usuarisAmbPermisos\":12,"
			+ "\"consultesOk\":124,"
			+ "\"consultesError\":3"
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
			+ "\"errorMessage\":\"Els parametres de data no tenen un format valid\""
			+ "}";

	@Operation(
			summary = "Informe de procediments",
			description = "Retorna els procediments agrupats per entitat i departament per a usuaris amb permisos d'informe.",
			operationId = "obtenirInformeProcediments",
			tags = {"Informes"})
	@ApiResponses({
			@ApiResponse(
					responseCode = "200",
					description = "Informe de procediments generat correctament.",
					content = @Content(
							mediaType = "application/json",
							array = @ArraySchema(schema = @Schema(implementation = EntitatEstadistiques.class)),
							examples = @ExampleObject(
									name = "Informe de procediments",
									value = INFORME_PROCEDIMENTS_EXAMPLE))),
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
					description = "Error intern en generar l'informe de procediments.",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(
									name = "Error intern",
									value = "{\"errorCode\":\"INTERNAL_SERVER_ERROR\",\"errorMessage\":\"Error intern del servidor\"}")))
	})
	ResponseEntity<List<EntitatEstadistiques>> procediments(
			@Parameter(hidden = true) HttpServletRequest request);

	@Operation(
			summary = "Informe d'usuaris",
			description = "Retorna els usuaris agrupats per entitat i departament per a usuaris amb permisos d'informe.",
			operationId = "obtenirInformeUsuaris",
			tags = {"Informes"})
	@ApiResponses({
			@ApiResponse(
					responseCode = "200",
					description = "Informe d'usuaris generat correctament.",
					content = @Content(
							mediaType = "application/json",
							array = @ArraySchema(schema = @Schema(implementation = EntitatEstadistiques.class)),
							examples = @ExampleObject(
									name = "Informe d'usuaris",
									value = INFORME_USUARIS_EXAMPLE))),
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
					description = "Error intern en generar l'informe d'usuaris.",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(
									name = "Error intern",
									value = "{\"errorCode\":\"INTERNAL_SERVER_ERROR\",\"errorMessage\":\"Error intern del servidor\"}")))
	})
	ResponseEntity<List<EntitatEstadistiques>> usuaris(
			@Parameter(hidden = true) HttpServletRequest request);

	@Operation(
			summary = "Informe de serveis",
			description = "Retorna la llista de serveis actius amb el seu codi, nom i emissor.",
			operationId = "obtenirInformeServeis",
			tags = {"Informes"})
	@ApiResponses({
			@ApiResponse(
					responseCode = "200",
					description = "Informe de serveis generat correctament.",
					content = @Content(
							mediaType = "application/json",
							array = @ArraySchema(schema = @Schema(implementation = ServeiEstadistiques.class)),
							examples = @ExampleObject(
									name = "Informe de serveis",
									value = INFORME_SERVEIS_EXAMPLE))),
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
					description = "Error intern en generar l'informe de serveis.",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(
									name = "Error intern",
									value = "{\"errorCode\":\"INTERNAL_SERVER_ERROR\",\"errorMessage\":\"Error intern del servidor\"}")))
	})
	ResponseEntity<List<ServeiEstadistiques>> serveis(
			@Parameter(hidden = true) HttpServletRequest request);

	@Operation(
			summary = "Informe general d'estat",
			description = "Retorna l'estat general de consultes agrupat per entitat, departament, procediment i servei dins del periode indicat.",
			operationId = "obtenirInformeGeneralEstat",
			tags = {"Informes"})
	@ApiResponses({
			@ApiResponse(
					responseCode = "200",
					description = "Informe general d'estat generat correctament.",
					content = @Content(
							mediaType = "application/json",
							array = @ArraySchema(schema = @Schema(implementation = EntitatEstadistiques.class)),
							examples = @ExampleObject(
									name = "Informe general",
									value = INFORME_GENERAL_EXAMPLE))),
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
					responseCode = "500",
					description = "Error intern en generar l'informe general.",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(
									name = "Error intern",
									value = "{\"errorCode\":\"INTERNAL_SERVER_ERROR\",\"errorMessage\":\"Error intern del servidor\"}")))
	})
	ResponseEntity<List<EntitatEstadistiques>> general(
			@Parameter(hidden = true) HttpServletRequest request,
			@Parameter(
					name = "historic",
					description = "Indica si s'ha d'utilitzar la informacio historica de consultes.",
					required = false,
					example = "false",
					schema = @Schema(type = "boolean", defaultValue = "false"))
			boolean historic,
			@Parameter(
					name = "dataInici",
					description = "Data i hora d'inici del periode d'informe en format ISO date-time.",
					required = true,
					example = "2026-01-01T00:00:00",
					schema = @Schema(type = "string", format = "date-time"))
			Date dataInici,
			@Parameter(
					name = "dataFi",
					description = "Data i hora de fi del periode d'informe en format ISO date-time.",
					required = true,
					example = "2026-01-31T23:59:59",
					schema = @Schema(type = "string", format = "date-time"))
			Date dataFi);

}
