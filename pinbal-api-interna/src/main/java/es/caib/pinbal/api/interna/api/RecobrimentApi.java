package es.caib.pinbal.api.interna.api;

import es.caib.pinbal.api.interna.controller.RecobrimentRestController;
import es.caib.pinbal.client.recobriment.model.ScspConfirmacionPeticion;
import es.caib.pinbal.client.recobriment.model.ScspPeticion;
import es.caib.pinbal.client.recobriment.model.ScspRespuesta;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Tag(name = "Recobriment SCSP intern", description = "Operacions internes de passarel·la SCSP: peticions síncrones/asíncrones, consulta de resposta i obtenció de justificants.")
public interface RecobrimentApi {

    @Operation(summary = "Comprovar estat de l'API", description = "Endpoint de prova de disponibilitat.", operationId = "testRecobrimentIntern")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "API activa")})
    ResponseEntity<String> test();

    @Operation(summary = "Petició síncrona SCSP", description = "Envia una petició SCSP síncrona i retorna la resposta.", operationId = "peticionSincronaIntern")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Resposta SCSP obtinguda"), @ApiResponse(responseCode = "400", description = "Error de validació", content = @Content), @ApiResponse(responseCode = "500", description = "Error intern", content = @Content)})
    ResponseEntity<ScspRespuesta> peticionSincrona(@Parameter(hidden = true) HttpServletRequest request,
                                                   @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content) ScspPeticion peticion) throws Exception;

    @Operation(summary = "Petició asíncrona SCSP", description = "Envia una petició SCSP asíncrona i retorna la confirmació.", operationId = "peticionAsincronaIntern")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Confirmació obtinguda"), @ApiResponse(responseCode = "400", description = "Error de validació", content = @Content), @ApiResponse(responseCode = "500", description = "Error intern", content = @Content)})
    ResponseEntity<ScspConfirmacionPeticion> peticionAsincrona(@Parameter(hidden = true) HttpServletRequest request,
                                                               @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content) ScspPeticion peticion) throws Exception;

    @Operation(summary = "Consultar resposta SCSP", description = "Recupera la resposta associada a una petició.", operationId = "getRespuestaIntern")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Resposta obtinguda"), @ApiResponse(responseCode = "404", description = "No trobat", content = @Content)})
    ResponseEntity<ScspRespuesta> getRespuesta(@Parameter(hidden = true) HttpServletRequest request,
                                               @Parameter(description = "Identificador de la petició", required = true, example = "PET-2026-0001") String idPeticion) throws Exception;

    @Operation(summary = "Obtenir justificant", description = "Retorna el fitxer del justificant de la petició.", operationId = "getJustificanteIntern")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Justificant generat"), @ApiResponse(responseCode = "404", description = "No trobat", content = @Content)})
    void getJustificante(HttpServletRequest request, HttpServletResponse response, String idPeticion, String idSolicitud) throws Exception;

    @Operation(summary = "Obtenir justificant imprimible", description = "Retorna el fitxer imprimible del justificant.", operationId = "getJustificanteImprimibleIntern")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Justificant imprimible generat"), @ApiResponse(responseCode = "404", description = "No trobat", content = @Content)})
    void getJustificanteImprimible(HttpServletRequest request, HttpServletResponse response, String idPeticion, String idSolicitud) throws Exception;

    @Operation(summary = "Obtenir CSV del justificant", description = "Genera el CSV del justificant.", operationId = "getJustificanteCsvIntern")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "CSV generat"), @ApiResponse(responseCode = "404", description = "No trobat", content = @Content)})
    ResponseEntity<String> getJustificanteCsv(HttpServletRequest request, HttpServletResponse response, String idPeticion, String idSolicitud) throws Exception;

    @Operation(summary = "Obtenir UUID del justificant", description = "Genera l'UUID associat al justificant.", operationId = "getJustificanteUuidIntern")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "UUID generat"), @ApiResponse(responseCode = "404", description = "No trobat", content = @Content)})
    ResponseEntity<String> getJustificanteUuid(HttpServletRequest request, HttpServletResponse response, String idPeticion, String idSolicitud) throws Exception;

    ResponseEntity<RecobrimentRestController.ErrorResponse> handleError(HttpServletRequest request, HttpServletResponse response, Exception ex);
}
