package es.caib.pinbal.api.interna.openapi.interficies.recobriment.v2;

import es.caib.pinbal.client.procediments.ProcedimentBasic;
import es.caib.pinbal.client.recobriment.model.ScspJustificante;
import es.caib.pinbal.client.recobriment.v2.DadaEspecifica;
import es.caib.pinbal.client.recobriment.v2.DadaEspecificaBasic;
import es.caib.pinbal.client.recobriment.v2.Entitat;
import es.caib.pinbal.client.recobriment.v2.PeticioAsincrona;
import es.caib.pinbal.client.recobriment.v2.PeticioConfirmacioAsincrona;
import es.caib.pinbal.client.recobriment.v2.PeticioRespostaAsincrona;
import es.caib.pinbal.client.recobriment.v2.PeticioRespostaSincrona;
import es.caib.pinbal.client.recobriment.v2.PeticioSincrona;
import es.caib.pinbal.client.recobriment.v2.ValorEnum;
import es.caib.pinbal.client.serveis.ServeiBasic;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Tag(name = "Recobriment SCSP v2", description = "Operacions de recobriment SCSP versió 2: entitats, procediments, serveis, dades específiques i realització de consultes síncrones i asíncrones.")
public interface RecobrimentRestV2Intf {

    /**
     * Descripció compartida pel paràmetre opcional que activa la informació de
     * permisos als mètodes d'obtenció de serveis. Per defecte és 'false' per a
     * mantenir la resposta idèntica a la de les versions anteriors de l'API.
     */
    String PARAM_AMB_PERMISOS = "Si és 'true', afegeix a cada servei els camps 'permis' (indica si l'usuari autenticat té permís " +
            "sobre el servei) i 'documentsTipusPermesos' (tipus de document identificatiu del titular admesos pel servei). " +
            "Per defecte és 'false' i aquests camps no s'inclouen a la resposta.";

    // Obtencio d'entitats
    // /////////////////////////////////////////////////////////////

    @Operation(
            summary = "Obtenir entitats",
            description = "Retorna la llista d'entitats a les que l'usuari autenticat té permís.",
            operationId = "getEntitatsV2")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entitats obtingudes amb èxit",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Entitat.class)))),
            @ApiResponse(responseCode = "204", description = "No s'han trobat entitats", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error intern del servidor", content = @Content)
    })
    ResponseEntity<List<Entitat>> getEntitats();

    // Obtencio de procediments
    // /////////////////////////////////////////////////////////////

    @Operation(
            summary = "Obtenir procediments d'una entitat",
            description = "Retorna una llista de procediments disponibles per l'entitat especificada.",
            operationId = "getProcedimentsV2")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Procediments obtinguts amb èxit",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ProcedimentBasic.class)))),
            @ApiResponse(responseCode = "204", description = "No s'han trobat procediments", content = @Content),
            @ApiResponse(responseCode = "404", description = "Entitat no trobada", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error intern del servidor", content = @Content)
    })
    ResponseEntity<List<ProcedimentBasic>> getProcediments(
            @Parameter(description = "Codi de l'entitat", required = true, example = "A04003003")
            String entitatCodi);

    // Obtenció de serveis
    // /////////////////////////////////////////////////////////////

    @Operation(
            summary = "Obtenir tots els serveis",
            description = "Retorna una llista de tots els serveis disponibles a Pinbal. Si s'indica 'ambPermisos=true', per a cada " +
                    "servei s'afegeix el camp 'permis', que indica si l'usuari autenticat hi té permís en alguna de les entitats a " +
                    "les que està vinculat, i el camp 'documentsTipusPermesos', amb els tipus de document identificatiu del titular " +
                    "admesos pel servei. Si no s'indica, aquests dos camps no s'inclouen a la resposta.",
            operationId = "getServeisV2")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serveis obtinguts amb èxit",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ServeiBasic.class)))),
            @ApiResponse(responseCode = "204", description = "No s'han trobat serveis", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error intern del servidor", content = @Content)
    })
    ResponseEntity<List<ServeiBasic>> getServeis(
            @Parameter(description = PARAM_AMB_PERMISOS, required = false, example = "true")
            boolean ambPermisos);

    @Operation(
            summary = "Obtenir serveis per entitat",
            description = "Retorna una llista dels serveis disponibles a Pinbal per a una entitat. Si s'indica 'ambPermisos=true', per " +
                    "a cada servei s'afegeix el camp 'permis', que indica si l'usuari autenticat hi té permís dins l'entitat, i el " +
                    "camp 'documentsTipusPermesos', amb els tipus de document identificatiu del titular admesos pel servei. Si no " +
                    "s'indica, aquests dos camps no s'inclouen a la resposta.",
            operationId = "getServeisPerEntitatV2")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serveis obtinguts amb èxit",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ServeiBasic.class)))),
            @ApiResponse(responseCode = "204", description = "No s'han trobat serveis", content = @Content),
            @ApiResponse(responseCode = "404", description = "Entitat no trobada", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error intern del servidor", content = @Content)
    })
    ResponseEntity<List<ServeiBasic>> getServeisPerEntitat(
            @Parameter(description = "Codi de l'entitat", required = true, example = "A04003003")
            String entitatCodi,
            @Parameter(description = PARAM_AMB_PERMISOS, required = false, example = "true")
            boolean ambPermisos);

    @Operation(
            summary = "Obtenir serveis per procediment",
            description = "Retorna una llista de serveis disponibles a Pinbal per a un procediment d'una entitat. Si s'indica " +
                    "'ambPermisos=true', per a cada servei s'afegeix el camp 'permis', que indica si l'usuari autenticat hi té permís " +
                    "dins el procediment, i el camp 'documentsTipusPermesos', amb els tipus de document identificatiu del titular " +
                    "admesos pel servei. Si no s'indica, aquests dos camps no s'inclouen a la resposta.",
            operationId = "getServeisPerProcedimentV2")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serveis obtinguts amb èxit",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ServeiBasic.class)))),
            @ApiResponse(responseCode = "204", description = "No s'han trobat serveis", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error intern del servidor", content = @Content)
    })
    ResponseEntity<List<ServeiBasic>> getServeisPerProcediment(
            @Parameter(description = "Codi de l'entitat", required = true, example = "A04003003")
            String entitatCodi,
            @Parameter(description = "Codi del procediment", required = true, example = "PROC001")
            String procedimentCodi,
            @Parameter(description = PARAM_AMB_PERMISOS, required = false, example = "true")
            boolean ambPermisos);

    // Obtenció de dades específiques
    // /////////////////////////////////////////////////////////////

    @Operation(
            summary = "Obtenir dades específiques d'un servei",
            description = "Retorna la llista de camps necessaris per emplenar l'apartat de dades específiques de la petició SCSP al servei web final.",
            operationId = "getDadesEspecifiquesV2")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dades específiques obtingudes amb èxit",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = DadaEspecifica.class)))),
            @ApiResponse(responseCode = "204", description = "No s'han trobat dades específiques pel servei", content = @Content),
            @ApiResponse(responseCode = "404", description = "Servei no trobat", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error intern del servidor", content = @Content)
    })
    ResponseEntity<List<DadaEspecifica>> getDadesEspecifiques(
            @Parameter(description = "Codi del servei", required = true, example = "SVCDATOS")
            String serveiCodi);

    @Operation(
            summary = "Obtenir dades específiques de resposta d'un servei",
            description = "Retorna la llista de camps que poden aparèixer en la resposta de la petició SCSP.",
            operationId = "getDadesEspecifiquesRespostaV2")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dades específiques de resposta obtingudes amb èxit",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = DadaEspecificaBasic.class)))),
            @ApiResponse(responseCode = "204", description = "No s'han trobat dades específiques pel servei", content = @Content),
            @ApiResponse(responseCode = "404", description = "Servei no trobat", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error intern del servidor", content = @Content)
    })
    ResponseEntity<List<DadaEspecificaBasic>> getDadesEspecifiquesResposta(
            @Parameter(description = "Codi del servei", required = true, example = "SVCDATOS")
            String serveiCodi);

    @Hidden
    ResponseEntity<List<ValorEnum>> getValorsEnum(
            String serveiCodi,
            String filtre,
            HttpServletRequest request);

    @Operation(
            summary = "Obtenir valors d'un camp enumerat",
            description = "Retorna la llista de valors d'un camp de tipus enumerat d'un servei. Quan l'enumerat és PAIS o PROVINCIA, es pot passar com a filtre 'CA' o 'ES' per indicar l'idioma.",
            operationId = "getValorsEnumV2")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Valors d'enumerats obtinguts amb èxit",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ValorEnum.class)))),
            @ApiResponse(responseCode = "204", description = "No s'han trobat valors d'enumerat", content = @Content),
            @ApiResponse(responseCode = "404", description = "Servei o enumerat no trobat", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error intern del servidor", content = @Content)
    })
    ResponseEntity<List<ValorEnum>> getValorsEnum(
            @Parameter(description = "Codi del servei", required = true, example = "SVCDATOS")
            String serveiCodi,
            @Parameter(description = "Codi del camp", required = true, example = "campNacionalitat")
            String campCodi,
            @Parameter(description = "Codi de l'enumerat", required = true, example = "PAIS")
            String enumCodi,
            @Parameter(description = "Filtre a aplicar en l'obtenció dels possibles valors de l'enumerat (opcional). Per a PAIS/PROVINCIA: 'CA' o 'ES' per indicar l'idioma.", required = false, example = "CA")
            String filtre);

    // Realització de consultes
    // /////////////////////////////////////////////////////////////

    @Operation(
            summary = "Realitzar consulta síncrona",
            description = "Realitza una consulta síncrona al servei indicat. Retorna informació dels possibles errors de validació o la resposta de la consulta si s'ha realitzat correctament.",
            operationId = "peticioSincronaV2")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realitzada. Pot retornar errors de validació, error en la consulta, o haver-se realitzat correctament.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PeticioRespostaSincrona.class))),
            @ApiResponse(responseCode = "404", description = "Mètode no trobat", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error intern del servidor", content = @Content)
    })
    ResponseEntity<PeticioRespostaSincrona> peticioSincrona(
            @Parameter(description = "Codi del servei", required = true, example = "SVCDATOS")
            String serveiCodi,
            @RequestBody(required = true, description = "Dades de la petició síncrona",
                    content = @Content(schema = @Schema(implementation = PeticioSincrona.class)))
            PeticioSincrona peticio);

    @Operation(
            summary = "Realitzar consulta asíncrona",
            description = "Realitza una consulta asíncrona al servei indicat. Retorna informació dels possibles errors de validació o la confirmació de la petició si s'ha enviat correctament.",
            operationId = "peticioAsincronaV2")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realitzada. Pot retornar errors de validació, error en la consulta, o haver-se realitzat correctament.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PeticioConfirmacioAsincrona.class))),
            @ApiResponse(responseCode = "404", description = "Mètode no trobat", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error intern del servidor", content = @Content)
    })
    ResponseEntity<PeticioConfirmacioAsincrona> peticioAsincrona(
            @Parameter(description = "Codi del servei", required = true, example = "SVCDATOS")
            String serveiCodi,
            @RequestBody(required = true, description = "Dades de la petició asíncrona",
                    content = @Content(schema = @Schema(implementation = PeticioAsincrona.class)))
            PeticioAsincrona peticio);

    // Obtenció de respostes
    // /////////////////////////////////////////////////////////////

    @Operation(
            summary = "Obtenir resultat d'una petició asíncrona",
            description = "Retorna informació de la resposta d'una petició asíncrona ja realitzada.",
            operationId = "getRespostaV2")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resposta obtinguda correctament",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PeticioRespostaAsincrona.class))),
            @ApiResponse(responseCode = "204", description = "No s'ha trobat la resposta", content = @Content),
            @ApiResponse(responseCode = "404", description = "Petició no trobada", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error intern del servidor", content = @Content)
    })
    ResponseEntity<PeticioRespostaAsincrona> getResposta(
            @Parameter(description = "Identificador de la petició", required = true, example = "PET-2025-0001")
            String idPeticio);

    @Operation(
            summary = "Obtenir justificant",
            description = "Obté el justificant de la petició.",
            operationId = "getJustificantV2")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Justificant obtingut correctament",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ScspJustificante.class))),
            @ApiResponse(responseCode = "204", description = "No s'ha trobat la consulta", content = @Content),
            @ApiResponse(responseCode = "404", description = "Justificant no trobat", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error intern del servidor", content = @Content)
    })
    ResponseEntity<ScspJustificante> getJustificant(
            @Parameter(description = "Identificador de la petició", required = true, example = "PET-2025-0001")
            String idPeticio,
            @Parameter(description = "Identificador de la sol·licitud", required = true, example = "SOL-2025-0001")
            String idSolicitud) throws Exception;

    @Operation(
            summary = "Obtenir justificant imprimible",
            description = "Obté la versió imprimible del justificant de la petició.",
            operationId = "getJustificantImprimibleV2")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Justificant imprimible obtingut correctament",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ScspJustificante.class))),
            @ApiResponse(responseCode = "204", description = "No s'ha trobat la consulta", content = @Content),
            @ApiResponse(responseCode = "404", description = "Justificant no trobat", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error intern del servidor", content = @Content)
    })
    ResponseEntity<ScspJustificante> getJustificantImprimible(
            @Parameter(description = "Identificador de la petició", required = true, example = "PET-2025-0001")
            String idPeticio,
            @Parameter(description = "Identificador de la sol·licitud", required = true, example = "SOL-2025-0001")
            String idSolicitud) throws Exception;

    @Operation(
            summary = "Obtenir CSV del justificant",
            description = "Obté el codi CSV del justificant de la petició.",
            operationId = "getJustificantCsvV2")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "CSV del justificant obtingut correctament",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "204", description = "No s'ha trobat la consulta", content = @Content),
            @ApiResponse(responseCode = "404", description = "Justificant no trobat", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error intern del servidor", content = @Content)
    })
    ResponseEntity<String> getJustificantCsv(
            @Parameter(description = "Identificador de la petició", required = true, example = "PET-2025-0001")
            String idPeticio,
            @Parameter(description = "Identificador de la sol·licitud", required = true, example = "SOL-2025-0001")
            String idSolicitud) throws Exception;

    @Operation(
            summary = "Obtenir UUID del justificant",
            description = "Obté el codi UUID del justificant de la petició.",
            operationId = "getJustificantUuidV2")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "UUID del justificant obtingut correctament",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "204", description = "No s'ha trobat la consulta", content = @Content),
            @ApiResponse(responseCode = "404", description = "Justificant no trobat", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error intern del servidor", content = @Content)
    })
    ResponseEntity<String> getJustificantUuid(
            @Parameter(description = "Identificador de la petició", required = true, example = "PET-2025-0001")
            String idPeticio,
            @Parameter(description = "Identificador de la sol·licitud", required = true, example = "SOL-2025-0001")
            String idSolicitud) throws Exception;

}
