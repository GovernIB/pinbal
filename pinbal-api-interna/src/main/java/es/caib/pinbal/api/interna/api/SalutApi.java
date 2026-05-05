package es.caib.pinbal.api.interna.api;

import es.caib.comanda.model.server.monitoring.AppInfo;
import es.caib.comanda.model.server.monitoring.SalutInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Tag(name = "Salut", description = "Operacions relacionades amb l'estat de salut i informació de l'aplicació.")
public interface SalutApi {

    @Operation(
            summary = "Obtenir informació de l'aplicació",
            description = "Retorna informació detallada de l'aplicació: versió, data de compilació, JDK, integracions i subsistemes.",
            operationId = "getAppInfo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informació obtinguda amb èxit",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppInfo.class))),
            @ApiResponse(responseCode = "404", description = "Informació no trobada", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error intern del servidor", content = @Content)
    })
    AppInfo appInfo(HttpServletRequest request) throws IOException;

    @Operation(
            summary = "Obtenir informació de salut de l'aplicació",
            description = "Retorna l'estat de salut de l'aplicació: bases de dades, serveis externs i altres subsistemes.",
            operationId = "getHealth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informació de salut obtinguda amb èxit",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SalutInfo.class))),
            @ApiResponse(responseCode = "404", description = "Informació no trobada", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error intern del servidor", content = @Content)
    })
    SalutInfo health(
            HttpServletRequest request,
            @Parameter(description = "Data de període per filtrar les estadístiques de salut (opcional)", required = false)
            String dataPeriode,
            @Parameter(description = "Data total per filtrar les estadístiques de salut (opcional)", required = false)
            String dataTotal) throws IOException;
}
