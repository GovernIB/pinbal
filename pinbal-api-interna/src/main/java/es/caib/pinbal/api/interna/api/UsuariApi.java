package es.caib.pinbal.api.interna.api;

import es.caib.pinbal.client.recobriment.v2.Entitat;
import es.caib.pinbal.client.usuaris.PermisosServei;
import es.caib.pinbal.client.usuaris.UsuariEntitat;
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

import javax.validation.Valid;
import java.util.List;

@Tag(name = "Usuaris interns", description = "Gestió d'usuaris, permisos i consulta d'entitats disponibles per a l'usuari autenticat.")
public interface UsuariApi {

    @Operation(summary = "Obtenir entitats de l'usuari", description = "Retorna les entitats on l'usuari actual té permisos.", operationId = "getUsuariEntitatsIntern")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Correcte"), @ApiResponse(responseCode = "204", description = "Sense contingut", content = @Content)})
    ResponseEntity<List<Entitat>> getEntitats();

    @Operation(summary = "Crear o actualitzar usuari", description = "Crea o actualitza un usuari dins una entitat.", operationId = "createOrUpdateUserIntern")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Creat/actualitzat"), @ApiResponse(responseCode = "400", description = "Entrada invàlida", content = @Content)})
    ResponseEntity<Void> createOrUpdateUser(@io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content) UsuariEntitat usuariEntitat,
                                            @Parameter(hidden = true) BindingResult bindingResult);

    @Operation(summary = "Llistar usuaris", description = "Consulta paginada d'usuaris per entitat amb filtre JSON opcional.", operationId = "getUsuarisIntern")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Correcte"), @ApiResponse(responseCode = "204", description = "Sense contingut", content = @Content)})
    ResponseEntity<PagedModel<EntityModel<UsuariEntitat>>> getUsuaris(String entitatCodi, String filtreUsuarisString, @Parameter(hidden = true) Pageable pageable);

    @Operation(summary = "Obtenir usuari", description = "Retorna les dades d'un usuari per codi i entitat.", operationId = "getUsuariIntern")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Correcte"), @ApiResponse(responseCode = "204", description = "Sense contingut", content = @Content)})
    ResponseEntity<EntityModel<UsuariEntitat>> getUsuari(String usuariCodi, String entitatCodi);

    @Operation(summary = "Atorgar permisos", description = "Atorga permisos seleccionats de procediment/servei a un usuari.", operationId = "grantUsuariPermissionsIntern")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Correcte"), @ApiResponse(responseCode = "400", description = "Entrada invàlida", content = @Content)})
    void grantPermissions(String usuariCodi,
                          @Valid @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content) PermisosServei permisosServei,
                          @Parameter(hidden = true) BindingResult bindingResult);

    @Operation(summary = "Obtenir permisos d'usuari", description = "Retorna permisos de l'usuari en una entitat.", operationId = "getUserPermissionsIntern")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Correcte"), @ApiResponse(responseCode = "204", description = "Sense contingut", content = @Content)})
    ResponseEntity<EntityModel<PermisosServei>> getUserPermissions(String usuariCodi, String entitatCodi);
}
