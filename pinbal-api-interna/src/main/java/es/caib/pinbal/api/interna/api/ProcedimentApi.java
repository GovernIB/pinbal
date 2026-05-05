package es.caib.pinbal.api.interna.api;

import es.caib.pinbal.client.procediments.Procediment;
import es.caib.pinbal.client.procediments.ProcedimentPatch;
import es.caib.pinbal.client.serveis.Servei;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

@Tag(name = "Procediments interns", description = "Gestió interna de procediments administratius i serveis vinculats.")
public interface ProcedimentApi {

    @Operation(summary = "Crear procediment", description = "Crea un procediment nou.", operationId = "createProcedimentIntern")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Creat"), @ApiResponse(responseCode = "400", description = "Entrada invàlida", content = @Content)})
    ResponseEntity<EntityModel<Procediment>> createProcediment(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Dades del procediment", content = @Content) Procediment procediment,
            @Parameter(hidden = true) BindingResult bindingResult);

    @Operation(summary = "Modificar procediment", description = "Modifica un procediment existent.", operationId = "updateProcedimentIntern")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Correcte"), @ApiResponse(responseCode = "404", description = "No trobat", content = @Content)})
    EntityModel<Procediment> updateProcediment(@Parameter(required = true) Long procedimentId,
                                            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Dades de modificació", content = @Content) Procediment procediment,
                                            @Parameter(hidden = true) BindingResult bindingResult);

    @Operation(summary = "Modificar parcialment procediment", description = "Aplica un patch parcial.", operationId = "patchProcedimentIntern")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Correcte"), @ApiResponse(responseCode = "404", description = "No trobat", content = @Content)})
    EntityModel<Procediment> patchProcediment(@Parameter(required = true) Long procedimentId,
                                           @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Patch del procediment", content = @Content) ProcedimentPatch procedimentPatch);

    @Operation(summary = "Habilitar servei per procediment", description = "Activa un servei dins un procediment.", operationId = "enableServeiToProcedimentIntern")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Correcte"), @ApiResponse(responseCode = "404", description = "No trobat", content = @Content)})
    void enableServeiToProcediment(@Parameter(required = true) Long procedimentId, @Parameter(required = true) String serveiCodi);

    @Operation(summary = "Llistar procediments", description = "Consulta paginada de procediments.", operationId = "getProcedimentsIntern")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Correcte"), @ApiResponse(responseCode = "204", description = "Sense contingut", content = @Content)})
    ResponseEntity<PagedModel<EntityModel<Procediment>>> getProcediments(String entitatCodi, String codi, String nom, String organGestor, @Parameter(hidden = true) Pageable pageable);

    @Operation(summary = "Obtenir procediment per id", description = "Retorna un procediment per identificador.", operationId = "getProcedimentByIdIntern")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Correcte"), @ApiResponse(responseCode = "404", description = "No trobat", content = @Content)})
    ResponseEntity<EntityModel<Procediment>> getProcediment(Long procedimentId);

    @Operation(summary = "Obtenir procediment per codi", description = "Retorna un procediment per codi i entitat.", operationId = "getProcedimentByCodiIntern")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Correcte"), @ApiResponse(responseCode = "404", description = "No trobat", content = @Content)})
    ResponseEntity<EntityModel<Procediment>> getProcediment(String procedimentCodi, String entitatCodi);

    @Operation(summary = "Llistar serveis del procediment", description = "Llista paginada de serveis d'un procediment per id.", operationId = "getProcedimentServeisIntern")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Correcte"), @ApiResponse(responseCode = "204", description = "Sense contingut", content = @Content)})
    ResponseEntity<PagedModel<EntityModel<Servei>>> getProcedimentServeis(Long procedimentId, @Parameter(hidden = true) Pageable pageable);

    @Operation(summary = "Llistar serveis del procediment per codi", description = "Llista paginada de serveis per codi de procediment i entitat.", operationId = "getProcedimentServeisByCodiIntern")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Correcte"), @ApiResponse(responseCode = "204", description = "Sense contingut", content = @Content)})
    ResponseEntity<PagedModel<EntityModel<Servei>>> getProcedimentServeisByCodi(String procedimentCodi, String entitatCodi, @Parameter(hidden = true) Pageable pageable);
}
