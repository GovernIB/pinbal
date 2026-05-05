package es.caib.pinbal.api.interna.api;

import es.caib.comanda.model.server.monitoring.EstadistiquesInfo;
import es.caib.comanda.model.server.monitoring.RegistresEstadistics;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Tag(name = "Estadístiques", description = "Operacions relacionades amb estadístiques d'ús de la plataforma.")
public interface EstadisticaApi {

    @Operation(
            summary = "Obtenir informació d'estadístiques",
            description = "Retorna informació detallada de les estadístiques de l'aplicació.",
            operationId = "getEstadistiquesInfo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informació obtinguda amb èxit",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EstadistiquesInfo.class))),
            @ApiResponse(responseCode = "404", description = "Informació no trobada", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error intern del servidor", content = @Content)
    })
    EstadistiquesInfo estadistiquesInfo(HttpServletRequest request) throws IOException;

    @Operation(
            summary = "Obtenir estadístiques del dia anterior",
            description = "Retorna informació estadística del dia anterior.",
            operationId = "getEstadistiquesUltimes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informació obtinguda amb èxit",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegistresEstadistics.class))),
            @ApiResponse(responseCode = "404", description = "Informació no trobada", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error intern del servidor", content = @Content)
    })
    RegistresEstadistics estadistiques(HttpServletRequest request) throws IOException;

    @Operation(
            summary = "Obtenir estadístiques d'un dia",
            description = "Retorna informació estadística del dia indicat a través del paràmetre data.",
            operationId = "getEstadistiquesByData")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informació obtinguda amb èxit",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegistresEstadistics.class))),
            @ApiResponse(responseCode = "404", description = "Informació no trobada", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error intern del servidor", content = @Content)
    })
    RegistresEstadistics estadistiques(
            HttpServletRequest request,
            @Parameter(description = "Data de la qual es volen obtenir les dades estadístiques. El format ha de ser dd-MM-yyyy", required = true, example = "01-01-2025")
            String data) throws Exception;

    @Operation(
            summary = "Obtenir estadístiques d'un rang de dies",
            description = "Retorna informació estadística dels dies indicats, des de la data d'inici fins a la data de fi, ambdós inclosos.",
            operationId = "getEstadistiquesByRangDates")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informació obtinguda amb èxit",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = RegistresEstadistics.class)))),
            @ApiResponse(responseCode = "404", description = "Informació no trobada", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error intern del servidor", content = @Content)
    })
    List<RegistresEstadistics> estadistiques(
            HttpServletRequest request,
            @Parameter(description = "Data inicial a partir de la qual es volen obtenir les dades estadístiques. El format ha de ser dd-MM-yyyy", required = true, example = "01-01-2025")
            String dataInici,
            @Parameter(description = "Data final fins a la qual es volen obtenir les dades estadístiques. El format ha de ser dd-MM-yyyy", required = true, example = "31-01-2025")
            String dataFi) throws Exception;

    @Hidden
    String generarEstadistiques(HttpServletRequest request, String dataInici, String dataFi) throws Exception;
}
