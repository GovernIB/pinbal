package es.caib.pinbal.api.interna.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import es.caib.pinbal.api.config.ApiVersion;
import es.caib.pinbal.client.comu.Create;
import es.caib.pinbal.client.usuaris.FiltreUsuaris;
import es.caib.pinbal.client.usuaris.PermisosServei;
import es.caib.pinbal.client.usuaris.ProcedimentServei;
import es.caib.pinbal.client.usuaris.UsuariEntitat;
import es.caib.pinbal.core.dto.apiresponse.ServiceExecutionException;
import es.caib.pinbal.core.service.GestioRestService;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatUsuariNotFoundException;
import es.caib.pinbal.core.service.exception.InvalidInputException;
import es.caib.pinbal.core.service.exception.MultiplesUsuarisExternsException;
import es.caib.pinbal.core.service.exception.NotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentServeiNotFoundException;
import es.caib.pinbal.core.service.exception.ResourceNotFoundException;
import es.caib.pinbal.core.service.exception.UsuariExternNotFoundException;
import es.caib.pinbal.core.service.exception.UsuariNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Controller
//@RequestMapping("/usuaris")
@Api(value = "API Usuaris", description = "Operacions relacionades amb Usuaris i permisos")
public class UsuariRestController extends PinbalHalRestController {

    @Autowired
    private GestioRestService gestioRestService;

    /**
     * Crea o actualitza un usuari.
     * @param usuariEntitat Dades de l'usuari.
     */
    @ApiVersion("1")
    @PreAuthorize("hasRole('PBL_WS')")
    @RequestMapping(value = "/usuaris", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Crear o actualitzar un usuari")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Usuari creat/actualitzat amb èxit"),
            @ApiResponse(code = 400, message = "Entrada invàlida")
    })
    public @ResponseBody ResponseEntity<Void> createOrUpdateUser(
            @ApiParam(value = "Dades de l'usuari a crear/actualitzar", required = true) @Validated(Create.class) @RequestBody UsuariEntitat usuariEntitat, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InvalidInputException(bindingResult);
        }
        try {
            gestioRestService.createOrUpdateUsuari(usuariEntitat);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (EntitatNotFoundException e) {
            throw new ResourceNotFoundException(e.getDefaultMessage());
        } catch (UsuariExternNotFoundException e) {
            throw new ResourceNotFoundException(e.getDefaultMessage());
        } catch (MultiplesUsuarisExternsException e) {
            bindingResult.addError(new ObjectError("usuariEntitat", "Multiples usuaris coincideixen amb les dades aportades"));
            throw new InvalidInputException(bindingResult);
        } catch (Exception e) {
            throw new ServiceExecutionException(e.getMessage(), e);
        }
    }

    /**
     * Recupera usuaris amb filtratge i paginació.
     * @param entitatCodi Codi de l'entitat.
     * @param filtreUsuarisString filtre per filtrar els usuaris.
     * @param pageable Informació de paginació.
     * @return Pàgina d'usuaris.
     */
    @ApiVersion("1")
    @PreAuthorize("hasRole('PBL_WS')")
    @RequestMapping(value = "/usuaris", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Obtén usuaris amb filtratge i paginació",
            response = PagedResources.class,
            notes = "Els paràmetres de pàgina inclouen: " +
                    "page (número de la pàgina, comença per 0), " +
                    "size (mida de la pàgina), " +
                    "sort (ordre, e.g., sort=field1,asc&sort=field2,desc). " +
                    "El filtreUsuaris permet especificar criteris addicionals de filtratge." +
                    "Exemple de `filtreUsuaris`: " +
                    "`{\"codi\":\"12345\", \"nif\":\"12345678A\", \"nom\":\"NomUsuari\"}`")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Usuaris obtinguts amb èxit"),
            @ApiResponse(code = 204, message = "No s'han trobat usuaris"),
            @ApiResponse(code = 404, message = "Entitat no trobada")
    })
    public @ResponseBody ResponseEntity<PagedResources<Resource<UsuariEntitat>>> getUsuaris(
            @ApiParam(value = "Codi de l'entitat", required = true) @RequestParam("entitatCodi") String entitatCodi,
            @ApiParam(value = "Filtre per els usuaris. Exemple: {\"codi\":\"12345\", \"nif\":\"12345678A\", \"nom\":\"NomUsuari\"}", required = false) @RequestParam(value = "filtreUsuaris", required = false) String filtreUsuarisString,
            @PageableDefault(size = 10) Pageable pageable) {
        try {
            FiltreUsuaris filtreUsuaris = new FiltreUsuaris();
            // Converteix el filtreUsuarisString a l'objecte FiltreUsuaris
            if (filtreUsuarisString != null && !filtreUsuarisString.isEmpty()) {
                filtreUsuaris = new ObjectMapper().readValue(filtreUsuarisString, FiltreUsuaris.class);
            }

            Page<UsuariEntitat> usuarisPage = gestioRestService.findUsuarisPaginat(entitatCodi, filtreUsuaris, pageable);
            if (usuarisPage == null || usuarisPage.getContent().isEmpty()) {
                return new ResponseEntity<PagedResources<Resource<UsuariEntitat>>>(HttpStatus.NO_CONTENT);
            }

            List<Resource<UsuariEntitat>> usuariResources = new ArrayList<>();
            for (UsuariEntitat usuari : usuarisPage.getContent()) {
                Resource<UsuariEntitat> resource = new Resource<>(usuari);
                Link selfLink = ControllerLinkBuilder.linkTo(
                        ControllerLinkBuilder.methodOn(this.getClass()).getUsuari(usuari.getCodi(), entitatCodi)
                ).withSelfRel();
                resource.add(selfLink);
                usuariResources.add(resource);
            }

            String filtre = filtreUsuarisString == null ? "" : URLEncoder.encode(filtreUsuarisString, "UTF-8");
            Link link = ControllerLinkBuilder.linkTo(
                    ControllerLinkBuilder.methodOn(this.getClass()).getUsuaris(entitatCodi, filtre, pageable)
            ).withSelfRel();

            PagedResources.PageMetadata metadata = new PagedResources.PageMetadata(
                    usuarisPage.getSize(), usuarisPage.getNumber(), usuarisPage.getTotalElements(), usuarisPage.getTotalPages()
            );

            PagedResources<Resource<UsuariEntitat>> pagedResources = new PagedResources<>(usuariResources, metadata, link);
            return new ResponseEntity<PagedResources<Resource<UsuariEntitat>>>(pagedResources, HttpStatus.OK);

        } catch (EntitatNotFoundException e) {
            throw new ResourceNotFoundException(e.getDefaultMessage());
        } catch (Exception e) {
            throw new ServiceExecutionException(e.getMessage(), e);
        }
    }

    /**
     * Recupera un usuari pel seu Codi.
     * @param usuariCodi Codi de l'usuari'.
     * @param entitatCodi Codi de l'entitat.
     * @return Dades del procediment.
     */
    @ApiVersion("1")
    @PreAuthorize("hasRole('PBL_WS')")
    @RequestMapping(value = "/usuaris/{usuariCodi}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Obtén un usauri pel seu Codi",
            response = UsuariEntitat.class,
            notes = "Aquest mètode retorna els detalls d'un usuuari específic en una entitat identificat pel seu Codi.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Usuari obtingut amb èxit"),
            @ApiResponse(code = 204, message = "No s'ha trobat l'usuari"),
            @ApiResponse(code = 404, message = "Entitat no trobada")
    })
    public @ResponseBody ResponseEntity<Resource<UsuariEntitat>> getUsuari(
            @ApiParam(value = "Codi de l'usuari", required = true) @PathVariable("usuariCodi") String usuariCodi,
            @ApiParam(value = "Codi de l'entitat", required = true) @RequestParam("entitatCodi") String entitatCodi) {
        try {
            UsuariEntitat usuari = gestioRestService.getUsuariAmbEntitatICodi(entitatCodi, usuariCodi);
            if (usuari == null) {
                return new ResponseEntity<Resource<UsuariEntitat>>(HttpStatus.NO_CONTENT);
            }

            Resource<UsuariEntitat> resource = new Resource<UsuariEntitat>(usuari);
            Link selfLink = ControllerLinkBuilder.linkTo(
                    ControllerLinkBuilder.methodOn(this.getClass()).getUsuari(usuariCodi, entitatCodi)
            ).withSelfRel();
            resource.add(selfLink);
            return new ResponseEntity<Resource<UsuariEntitat>>(resource, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<Resource<UsuariEntitat>>(HttpStatus.NO_CONTENT);
        } catch (EntitatNotFoundException e) {
            throw new ResourceNotFoundException(e.getDefaultMessage());
        } catch (Exception e) {
            if ("EJBException".equals(e.getClass().getSimpleName())) {
                Throwable cause = e.getCause();
                if (cause != null && cause instanceof EntitatNotFoundException) {
                    throw new ResourceNotFoundException(((EntitatNotFoundException)e).getDefaultMessage());
                } else if (cause != null && cause instanceof NotFoundException) {
                    return new ResponseEntity<Resource<UsuariEntitat>>(HttpStatus.NO_CONTENT);
                }
            }
            throw new ServiceExecutionException(e.getMessage(), e);
        }
    }


    /**
     * Atorga permisos seleccionats a un usuari per a procediments i serveis.
     * @param usuariCodi  Codi de l'usuari.
     * @param permisosServei Dades dels permisos a atorgar.
     */
    @ApiVersion("1")
    @PreAuthorize("hasRole('PBL_WS')")
    @RequestMapping(value = "/usuaris/{usuariCodi}/permisos", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Atorgar permisos seleccionats a un usuari per a procediments i serveis")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Permisos atorgats amb èxit"),
            @ApiResponse(code = 400, message = "Entrada invàlida")
    })
    public @ResponseBody void grantPermissions(
            @ApiParam(value = "Codi de l'usuari al que atorgar els permisos", required = true) @PathVariable("usuariCodi") String usuariCodi,
            @ApiParam(value = "Dades dels permisos a atorgar", required = true) @Valid @RequestBody PermisosServei permisosServei,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InvalidInputException(bindingResult);
        }
        try {
            if (!usuariCodi.equals(permisosServei.getUsuariCodi())) {
                bindingResult.rejectValue("usuariCodi", "usuari.codi.invalid", "El codi d'usuari del permís no coincideix amb el de l'usuari al que atorgar el permís");
                throw new InvalidInputException(bindingResult);
            }
            gestioRestService.serveiGrantPermis(permisosServei);
        } catch (EntitatNotFoundException e) {
            throw new ResourceNotFoundException(e.getDefaultMessage());
        } catch (EntitatUsuariNotFoundException e) {
            throw new ResourceNotFoundException("Entitat-usuari no trobat: " + permisosServei.getEntitatCodi() + " - " + usuariCodi);
        } catch (ProcedimentServeiNotFoundException e) {
            throw new ResourceNotFoundException(e.getDefaultMessage());
        } catch (Exception e) {
            throw new ServiceExecutionException(e.getMessage(), e);
        }
    }

    /**
     * Recupera permisos associats a un usuari.
     *
     * @param usuariCodi  Codi de l'usuari.
     * @param entitatCodi Codi de l'entitat.
     * @return Llista de recursos HATOAS de tipus PermisDto.
     */
    @ApiVersion("1")
    @PreAuthorize("hasRole('PBL_WS')")
    @RequestMapping(value = "/usuaris/{usuariCodi}/permisos", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Obtén permisos associats a un usuari", response = PermisosServei.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Permisos obtinguts amb èxit"),
            @ApiResponse(code = 204, message = "No s'han trobat permisos"),
            @ApiResponse(code = 404, message = "Usuari o permisos no trobats")
    })
    public @ResponseBody ResponseEntity<Resource<PermisosServei>> getUserPermissions(
            @ApiParam(value = "Codi de l'usuari", required = true) @PathVariable("usuariCodi") String usuariCodi,
            @ApiParam(value = "ID de l'entitat", required = true) @RequestParam("entitatCodi") String entitatCodi) {

        if ("null".equals(usuariCodi) || usuariCodi == null || usuariCodi.isEmpty()) {
            BindingResult errors = new BeanPropertyBindingResult(this, "usuariCodi");
            errors.addError(new ObjectError("usuariCodi", "El codi d'usuari del permís no coincideix amb el de l'usuari al que atorgar el permís"));
            throw new InvalidInputException(errors);
        }
        try {
            PermisosServei permisos = gestioRestService.permisosPerUsuariEntitat(entitatCodi, usuariCodi);

            if (permisos == null || permisos.getProcedimentServei() == null || permisos.getProcedimentServei().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            Resource<PermisosServei> resource = new Resource<>(permisos);
            Link selfLink = ControllerLinkBuilder.linkTo(
                    ControllerLinkBuilder.methodOn(this.getClass()).getUserPermissions(usuariCodi, entitatCodi)
            ).withSelfRel();
            resource.add(selfLink);

            for (ProcedimentServei procedimentServei : permisos.getProcedimentServei()) {
                Link usuariLink = ControllerLinkBuilder.linkTo(
                        ControllerLinkBuilder.methodOn(this.getClass()).getUsuari(usuariCodi, entitatCodi)
                ).withRel("usuari");
                resource.add(usuariLink);

                Link procedimentLink = ControllerLinkBuilder.linkTo(
                        ControllerLinkBuilder.methodOn(ProcedimentRestController.class).getProcediment(procedimentServei.getProcedimentCodi(), entitatCodi)
                ).withRel("procediment");
                resource.add(procedimentLink);

                Link serveiLink = ControllerLinkBuilder.linkTo(
                        ControllerLinkBuilder.methodOn(ServeiRestController.class).getServei(procedimentServei.getServeiCodi())
                ).withRel("servei");
                resource.add(serveiLink);
            }

            return new ResponseEntity<>(resource, HttpStatus.OK);
        } catch (EntitatNotFoundException e) {
            throw new ResourceNotFoundException(e.getDefaultMessage());
        } catch (UsuariNotFoundException e) {
            throw new ResourceNotFoundException(e.getDefaultMessage());
        } catch (Exception e) {
            throw new ServiceExecutionException(e.getMessage(), e);
        }
    }

}
