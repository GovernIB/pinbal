package es.caib.pinbal.api.interna.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.pinbal.api.interna.api.UsuariApi;
import es.caib.pinbal.client.comu.Create;
import es.caib.pinbal.client.recobriment.v2.Entitat;
import es.caib.pinbal.client.usuaris.FiltreUsuaris;
import es.caib.pinbal.client.usuaris.PermisosServei;
import es.caib.pinbal.client.usuaris.ProcedimentServei;
import es.caib.pinbal.client.usuaris.UsuariEntitat;
import es.caib.pinbal.logic.intf.dto.apiresponse.ServiceExecutionException;
import es.caib.pinbal.logic.intf.service.GestioRestService;
import es.caib.pinbal.logic.intf.service.RecobrimentService;
import es.caib.pinbal.logic.intf.service.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/usuaris")
public class UsuariRestController extends PinbalHalRestController implements UsuariApi {

    private final GestioRestService gestioRestService;
    private final RecobrimentService recobrimentService;


    /**
     * @return llista d'entitats a les que l'usuari autenticat té permís
     */
    // TODO: Revisar si s'utilitza en algun lloc. Canviat de /usuari/entitats a /usuaris/actual/entitats
    @Override
    @GetMapping(value = "/usuaris/actual/entitats", produces = MediaType.APPLICATION_JSON_VALUE)
    // IMPORTANT: Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI definida a la interfície UsuariApi.
    public ResponseEntity<List<Entitat>> getEntitats() {
        try {
            List<Entitat> entitats = recobrimentService.getEntitats();

            if (entitats == null || entitats.isEmpty() ) {
                return new ResponseEntity<List<Entitat>>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<List<Entitat>>(entitats, HttpStatus.OK);
        } catch (AccessDeniedException | AccessDenegatException ade) {
            throw new AccessDenegatException(Arrays.asList("PBL_WS"));
        } catch (Exception ex) {
            log.error("Error obtenint les entitats de l'usuari actual", ex);
            throw new ServiceExecutionException(ex.getMessage(), ex);
        }
    }

    /**
     * Crea o actualitza un usuari.
     * @param usuariEntitat Dades de l'usuari.
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    // IMPORTANT: Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI definida a la interfície UsuariApi.
    public ResponseEntity<Void> createOrUpdateUser(
            @Validated(Create.class) @RequestBody UsuariEntitat usuariEntitat, BindingResult bindingResult) {
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
        } catch (AccessDeniedException ade) {
            throw new AccessDenegatException(Arrays.asList("PBL_WS", "PBL_REPRES"));
        } catch (Exception e) {
            log.error("Error creant o actualitzant usuari", e);
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
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    // IMPORTANT: Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI definida a la interfície UsuariApi.
    public ResponseEntity<PagedModel<EntityModel<UsuariEntitat>>> getUsuaris(
            @RequestParam("entitatCodi") String entitatCodi,
            @RequestParam(value = "filtreUsuaris", required = false) String filtreUsuarisString,
            @PageableDefault(size = 10) Pageable pageable) {
        try {
            FiltreUsuaris filtreUsuaris = new FiltreUsuaris();
            // Converteix el filtreUsuarisString a l'objecte FiltreUsuaris
            if (filtreUsuarisString != null && !filtreUsuarisString.isEmpty()) {
                filtreUsuaris = new ObjectMapper().readValue(filtreUsuarisString, FiltreUsuaris.class);
            }

            Page<UsuariEntitat> usuarisPage = gestioRestService.findUsuarisPaginat(entitatCodi, filtreUsuaris, pageable);
            if (usuarisPage == null || usuarisPage.getContent().isEmpty()) {
                return new ResponseEntity<PagedModel<EntityModel<UsuariEntitat>>>(HttpStatus.NO_CONTENT);
            }

            List<EntityModel<UsuariEntitat>> usuariResources = new ArrayList<>();
            for (UsuariEntitat usuari : usuarisPage.getContent()) {
                EntityModel<UsuariEntitat> resource = EntityModel.of(usuari);
                Link selfLink = WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(this.getClass()).getUsuari(usuari.getCodi(), entitatCodi)
                ).withSelfRel();
                resource.add(selfLink);
                usuariResources.add(resource);
            }

            String filtre = filtreUsuarisString == null ? "" : URLEncoder.encode(filtreUsuarisString, "UTF-8");
            Link link = WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(this.getClass()).getUsuaris(entitatCodi, filtre, pageable)
            ).withSelfRel();

            PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(
                    usuarisPage.getSize(), usuarisPage.getNumber(), usuarisPage.getTotalElements(), usuarisPage.getTotalPages()
            );

            PagedModel<EntityModel<UsuariEntitat>> pagedResources = PagedModel.of(usuariResources, metadata, link);
            return new ResponseEntity<PagedModel<EntityModel<UsuariEntitat>>>(pagedResources, HttpStatus.OK);

        } catch (EntitatNotFoundException e) {
            throw new ResourceNotFoundException(e.getDefaultMessage());
        } catch (AccessDeniedException ade) {
            throw new AccessDenegatException(Arrays.asList("PBL_WS", "PBL_REPRES"));
        } catch (Exception e) {
            log.error("Error obtenint usuaris", e);
            throw new ServiceExecutionException(e.getMessage(), e);
        }
    }

    /**
     * Recupera un usuari pel seu Codi.
     * @param usuariCodi Codi de l'usuari'.
     * @param entitatCodi Codi de l'entitat.
     * @return Dades del procediment.
     */
    @GetMapping(value = "/{usuariCodi}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    // IMPORTANT: Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI definida a la interfície UsuariApi.
    public ResponseEntity<EntityModel<UsuariEntitat>> getUsuari(
            @PathVariable String usuariCodi,
            @RequestParam("entitatCodi") String entitatCodi) {
        try {
            UsuariEntitat usuari = gestioRestService.getUsuariAmbEntitatICodi(entitatCodi, usuariCodi);
            if (usuari == null) {
                return new ResponseEntity<EntityModel<UsuariEntitat>>(HttpStatus.NO_CONTENT);
            }

            EntityModel<UsuariEntitat> resource = EntityModel.of(usuari);
            Link selfLink = WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(this.getClass()).getUsuari(usuariCodi, entitatCodi)
            ).withSelfRel();
            resource.add(selfLink);
            return new ResponseEntity<EntityModel<UsuariEntitat>>(resource, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<EntityModel<UsuariEntitat>>(HttpStatus.NO_CONTENT);
        } catch (EntitatNotFoundException e) {
            throw new ResourceNotFoundException(e.getDefaultMessage());
        } catch (AccessDeniedException ade) {
            throw new AccessDenegatException(Arrays.asList("PBL_WS", "PBL_REPRES"));
        } catch (Exception e) {
            if ("EJBException".equals(e.getClass().getSimpleName())) {
                Throwable cause = e.getCause();
                if (cause != null && cause instanceof EntitatNotFoundException) {
                    throw new ResourceNotFoundException(((EntitatNotFoundException)e).getDefaultMessage());
                } else if (cause != null && cause instanceof NotFoundException) {
                    return new ResponseEntity<EntityModel<UsuariEntitat>>(HttpStatus.NO_CONTENT);
                }
            }
            log.error("Error obtenint usuari. codi: " + usuariCodi + ", entitat: " + entitatCodi, e);
            throw new ServiceExecutionException(e.getMessage(), e);
        }
    }


    /**
     * Atorga permisos seleccionats a un usuari per a procediments i serveis.
     * @param usuariCodi  Codi de l'usuari.
     * @param permisosServei Dades dels permisos a atorgar.
     */
    @PostMapping(value = "/{usuariCodi}/permisos", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    // IMPORTANT: Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI definida a la interfície UsuariApi.
    public void grantPermissions(
            @PathVariable String usuariCodi,
            @Valid @RequestBody PermisosServei permisosServei,
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
        } catch (AccessDeniedException ade) {
            throw new AccessDenegatException(Arrays.asList("PBL_WS", "PBL_REPRES"));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (AccessDenegatException e) {
            throw e;
        } catch (InvalidInputException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error assignant permisos a l'usuari " + usuariCodi, e);
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
    @GetMapping(value = "/{usuariCodi}/permisos", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    // IMPORTANT: Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI definida a la interfície UsuariApi.
    public ResponseEntity<EntityModel<PermisosServei>> getUserPermissions(
            @PathVariable String usuariCodi,
            @RequestParam("entitatCodi") String entitatCodi) {

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

            EntityModel<PermisosServei> resource = EntityModel.of(permisos);
            Link selfLink = WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(this.getClass()).getUserPermissions(usuariCodi, entitatCodi)
            ).withSelfRel();
            resource.add(selfLink);

            for (ProcedimentServei procedimentServei : permisos.getProcedimentServei()) {
                Link usuariLink = WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(this.getClass()).getUsuari(usuariCodi, entitatCodi)
                ).withRel("usuari");
                resource.add(usuariLink);

                Link procedimentLink = WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(ProcedimentRestController.class).getProcediment(procedimentServei.getProcedimentCodi(), entitatCodi)
                ).withRel("procediment");
                resource.add(procedimentLink);

                Link serveiLink = WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(ServeiRestController.class).getServei(procedimentServei.getServeiCodi())
                ).withRel("servei");
                resource.add(serveiLink);
            }

            return new ResponseEntity<>(resource, HttpStatus.OK);
        } catch (EntitatNotFoundException e) {
            throw new ResourceNotFoundException(e.getDefaultMessage());
        } catch (UsuariNotFoundException e) {
            throw new ResourceNotFoundException(e.getDefaultMessage());
        } catch (AccessDeniedException ade) {
            throw new AccessDenegatException(Arrays.asList("PBL_WS", "PBL_REPRES"));
        } catch (Exception e) {
            log.error("Error obtenint permisos de l'usuari " + usuariCodi, e);
            throw new ServiceExecutionException(e.getMessage(), e);
        }
    }

}
