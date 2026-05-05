package es.caib.pinbal.api.interna.api;

import es.caib.comanda.model.server.monitoring.FitxerContingut;
import es.caib.comanda.model.server.monitoring.FitxerInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Logs", description = "Operacions relacionades amb fitxers de log del servidor de l'aplicació.")
public interface LogApi {

    @Operation(
            summary = "Llistar fitxers de log",
            description = "Retorna una llista amb tots els fitxers que es troben dins la carpeta de logs del servidor de l'aplicació.",
            operationId = "getFitxersLog")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Llista de fitxers obtinguda correctament",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = FitxerInfo.class)))),
            @ApiResponse(responseCode = "204", description = "No hi ha fitxers de log disponibles", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error intern del servidor", content = @Content),
            @ApiResponse(responseCode = "501", description = "No implementat a COMANDA. Aquest endpoint l'ha d'exposar l'APP.", content = @Content)
    })
    ResponseEntity<List<FitxerInfo>> getFitxers();

    @Operation(
            summary = "Obtenir contingut d'un fitxer de log",
            description = "Retorna el contingut i detalls del fitxer de log que es troba dins la carpeta de logs del servidor, i que té el nom indicat.",
            operationId = "getFitxerLogByNom")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contingut del fitxer obtingut correctament",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FitxerContingut.class))),
            @ApiResponse(responseCode = "404", description = "Fitxer no trobat", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error intern del servidor", content = @Content),
            @ApiResponse(responseCode = "501", description = "No implementat a COMANDA. Aquest endpoint l'ha d'exposar l'APP.", content = @Content)
    })
    ResponseEntity<FitxerContingut> getFitxerByNom(
            @Parameter(description = "Nom del fitxer de log", required = true, example = "pinbal.log")
            String nomFitxer);

    @Operation(
            summary = "Descarregar fitxer de log complet",
            description = "Descarrega el fitxer de log complet que es troba dins la carpeta de logs del servidor, i que té el nom indicat.",
            operationId = "descarregarFitxerLog")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fitxer descarregat correctament",
                    content = @Content(mediaType = "application/octet-stream",
                            schema = @Schema(type = "string", format = "binary"))),
            @ApiResponse(responseCode = "404", description = "Fitxer no trobat", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error intern del servidor", content = @Content),
            @ApiResponse(responseCode = "501", description = "No implementat a COMANDA. Aquest endpoint l'ha d'exposar l'APP.", content = @Content)
    })
    ResponseEntity<byte[]> descarregarFitxerDirecte(
            @Parameter(description = "Nom del fitxer de log", required = true, example = "pinbal.log")
            String nomFitxer);

    @Operation(
            summary = "Obtenir les darreres línies d'un fitxer de log",
            description = "Retorna les darreres línies del fitxer de log indicat per nom. Concretament es retorna el número de línies indicat al paràmetre nLinies.",
            operationId = "getFitxerLogLinies")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Línies del fitxer obtingudes correctament",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(type = "string")))),
            @ApiResponse(responseCode = "404", description = "Fitxer no trobat", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error intern del servidor", content = @Content),
            @ApiResponse(responseCode = "501", description = "No implementat a COMANDA. Aquest endpoint l'ha d'exposar l'APP.", content = @Content)
    })
    ResponseEntity<List<String>> getFitxerLinies(
            @Parameter(description = "Nom del fitxer de log", required = true, example = "pinbal.log")
            String nomFitxer,
            @Parameter(description = "Número de línies a recuperar del fitxer", required = true, example = "100")
            Long nLinies);
}
