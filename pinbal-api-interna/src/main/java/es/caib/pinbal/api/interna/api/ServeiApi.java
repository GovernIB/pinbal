package es.caib.pinbal.api.interna.api;

import es.caib.pinbal.client.serveis.Servei;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;

@Tag(name = "Serveis interns", description = "Operacions internes de consulta de serveis amb suport de paginació i consulta per codi.")
public interface ServeiApi {

    @Operation(
            summary = "Llistar serveis",
            description = "Retorna una pàgina de serveis filtrant per codi o descripció.",
            operationId = "getServeisInterns")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serveis obtinguts correctament",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PagedModel.class),
                            examples = @ExampleObject(value = "{\"_embedded\":{\"serveiList\":[{\"codi\":\"SVCDATOS\",\"descripcio\":\"Servei d'exemple\"}]},\"page\":{\"size\":10,\"totalElements\":1,\"totalPages\":1,\"number\":0}}"))),
            @ApiResponse(responseCode = "204", description = "No hi ha serveis per als filtres indicats", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticat", content = @Content),
            @ApiResponse(responseCode = "403", description = "Accés denegat", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error intern", content = @Content)
    })
    ResponseEntity<PagedModel<EntityModel<Servei>>> getServeis(
            @Parameter(description = "Part del codi del servei per filtrar", required = false, example = "SVC") String codi,
            @Parameter(description = "Part de la descripció del servei per filtrar", required = false, example = "identitat") String descripcio,
            @Parameter(hidden = true) Pageable pageable);

    @Operation(
            summary = "Obtenir servei per codi",
            description = "Retorna un servei concret identificat pel seu codi.",
            operationId = "getServeiIntern")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Servei obtingut correctament",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EntityModel.class),
                            examples = @ExampleObject(value = "{\"codi\":\"SVCDATOS\",\"descripcio\":\"Servei d'exemple\"}"))),
            @ApiResponse(responseCode = "404", description = "Servei no trobat", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticat", content = @Content),
            @ApiResponse(responseCode = "403", description = "Accés denegat", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error intern", content = @Content)
    })
    ResponseEntity<EntityModel<Servei>> getServei(
            @Parameter(description = "Codi del servei", required = true, example = "SVCDATOS") String serveiCodi);
}
