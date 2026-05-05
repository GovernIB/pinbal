package es.caib.pinbal.api.interna.api;

import es.caib.pinbal.client.comu.EntitatInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "API interna", description = "Operacions generals de la API interna: consulta d'entitats disponibles.")
public interface ApiApi {

    @Operation(
            summary = "Llistar entitats",
            description = "Retorna la llista d'entitats disponibles a la plataforma.",
            operationId = "getEntitatsApi")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Llista d'entitats obtinguda correctament",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = EntitatInfo.class)))),
            @ApiResponse(responseCode = "401", description = "No autenticat", content = @Content),
            @ApiResponse(responseCode = "403", description = "Accés denegat", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error intern del servidor", content = @Content)
    })
    ResponseEntity<List<EntitatInfo>> test();
}
