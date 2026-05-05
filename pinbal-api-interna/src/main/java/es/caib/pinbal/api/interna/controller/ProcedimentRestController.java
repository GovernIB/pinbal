package es.caib.pinbal.api.interna.controller;

import es.caib.pinbal.api.interna.api.ProcedimentApi;
import es.caib.pinbal.client.comu.Create;
import es.caib.pinbal.client.comu.Update;
import es.caib.pinbal.client.procediments.Procediment;
import es.caib.pinbal.client.procediments.ProcedimentPatch;
import es.caib.pinbal.client.serveis.Servei;
import es.caib.pinbal.logic.intf.dto.apiresponse.ServiceExecutionException;
import es.caib.pinbal.logic.intf.service.GestioRestService;
import es.caib.pinbal.logic.intf.service.exception.AccessDenegatException;
import es.caib.pinbal.logic.intf.service.exception.EntitatNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.InvalidInputException;
import es.caib.pinbal.logic.intf.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.ResourceNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.ServeiNotFoundException;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/procediments")
public class ProcedimentRestController extends PinbalHalRestController implements ProcedimentApi {

    private final GestioRestService gestioRestService;

    /**
     * Crea un nou procediment.
     * @param procediment Dades del procediment a crear.
     * @return Procédiment creat amb enllaç HATEOAS.
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    // IMPORTANT: Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI definida a la interfície ProcedimentApi.
    public ResponseEntity<EntityModel<Procediment>> createProcediment(
            @Validated(Create.class) @RequestBody Procediment procediment,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InvalidInputException(bindingResult);
        }
        try {
            Procediment createdProcediment = gestioRestService.create(procediment);
            EntityModel<Procediment> procedimentResource = EntityModel.of(
                    createdProcediment,
                    WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProcedimentRestController.class).getProcediment(createdProcediment.getId())).withSelfRel());
            return new ResponseEntity<>(procedimentResource, HttpStatus.CREATED);
        } catch (EntitatNotFoundException e) {
            throw new ResourceNotFoundException(e.getDefaultMessage());
        } catch (AccessDeniedException ade) {
            throw new AccessDenegatException(Arrays.asList("PBL_WS", "PBL_REPRES"));
        } catch (Exception e) {
            log.error("Error creant procediment", e);
            throw new ServiceExecutionException(e.getMessage(), e);
        }
    }

    /**
     * Modifica un procediment.
     * @param 
     * @param procediment Dades del procediment a crear.
     * @return Procédiment creat amb enllaç HATEOAS.
     */
    @PostMapping(value = "/{procedimentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    // IMPORTANT: Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI definida a la interfície ProcedimentApi.
    public EntityModel<Procediment> updateProcediment(
            @PathVariable Long procedimentId,
            @Validated(Update.class) @RequestBody Procediment procediment,
            BindingResult bindingResult) {
        if (procediment.getId() != null && !procedimentId.equals(procediment.getId())) {
            bindingResult.rejectValue("id", "procediment.id.invalid", "L'identificador del procediment no coincideix amb el procedimentId informat");
        }
        if (bindingResult.hasErrors()) {
            throw new InvalidInputException(bindingResult);
        }
        try {
            procediment.setId(procedimentId);
            Procediment createdProcediment = gestioRestService.update(procediment);
            return EntityModel.of(
                    createdProcediment,
                    WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProcedimentRestController.class).getProcediment(createdProcediment.getId())).withSelfRel());
        } catch (ProcedimentNotFoundException e) {
            throw new ResourceNotFoundException("Procediment no trobat");
        } catch (AccessDeniedException ade) {
            throw new AccessDenegatException(Arrays.asList("PBL_WS", "PBL_REPRES"));
        } catch (Exception e) {
            log.error("Error modificant procediment", e);
            throw new ServiceExecutionException(e.getMessage(), e);
        }
    }

    @PatchMapping(value = "/{procedimentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    // IMPORTANT: Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI definida a la interfície ProcedimentApi.
    public EntityModel<Procediment> patchProcediment(
            @PathVariable Long procedimentId,
            @RequestBody ProcedimentPatch procedimentPatch) {
        try {
            Procediment updatedProcediment = gestioRestService.updateParcial(procedimentId, procedimentPatch);
            return EntityModel.of(
                    updatedProcediment,
                    WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProcedimentRestController.class).getProcediment(updatedProcediment.getId())).withSelfRel());
        } catch (ProcedimentNotFoundException e) {
            throw new ResourceNotFoundException(e.getDefaultMessage());
        } catch (AccessDeniedException ade) {
            throw new AccessDenegatException(Arrays.asList("PBL_WS", "PBL_REPRES"));
        } catch (Exception e) {
            log.error("Error modificant (patch) procediment", e);
            throw new ServiceExecutionException(e.getMessage(), e);
        }
    }

    /**
     * Habilita un servei per a un procediment.
     * @param procedimentId ID del procediment.
     * @param serveiCodi Codi del servei.
     */
    @PostMapping(value = "/{procedimentId}/serveis/{serveiCodi}/enable", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    // IMPORTANT: Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI definida a la interfície ProcedimentApi.
    public void enableServeiToProcediment(
            @PathVariable Long procedimentId,
            @PathVariable String serveiCodi) {
        try {
            gestioRestService.serveiEnable(procedimentId, serveiCodi);
        } catch (ProcedimentNotFoundException e) {
            throw new ResourceNotFoundException(e.getDefaultMessage(), e);
        } catch (ServeiNotFoundException e) {
            throw new ResourceNotFoundException(e.getDefaultMessage(), e);
        } catch (AccessDeniedException ade) {
            throw new AccessDenegatException(Arrays.asList("PBL_WS", "PBL_REPRES"));
        } catch (Exception e) {
            log.error("Error habilitant servei " + serveiCodi + " en procediment " + procedimentId, e);
            throw new ServiceExecutionException(e.getMessage(), e);
        }
    }

    /**
     * Recupera procediments amb filtratge i paginació.
     * @param entitatCodi Codi de l'entitat.
     * @param codi Part del codi del procediment. Per filtrar (opcional).
     * @param nom Part del nom del procediment. Per filtrar (opcional).
     * @param organGestor Codi Dir3 de l'òrgan gestor del procediment. Per filtrar (opcional).
     * @param pageable Informació de paginació.
     * @return Pàgina de procediments.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    // IMPORTANT: Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI definida a la interfície ProcedimentApi.
    public ResponseEntity<PagedModel<EntityModel<Procediment>>> getProcediments(
            @RequestParam("entitatCodi") String entitatCodi,
            @RequestParam(required = false) String codi,
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String organGestor,
            @PageableDefault(size = 10) Pageable pageable) {
        try {
            Page<Procediment> procedimentsPage = gestioRestService.findProcedimentsPaginat(entitatCodi, codi, nom, organGestor, pageable);

            if (procedimentsPage == null || procedimentsPage.getContent().isEmpty()) {
                return new ResponseEntity<PagedModel<EntityModel<Procediment>>>(HttpStatus.NO_CONTENT);
            }

            List<EntityModel<Procediment>> procedimentResources = new ArrayList<>();
            for (Procediment procediment : procedimentsPage.getContent()) {
                EntityModel<Procediment> resource = EntityModel.of(procediment);
                Link selfLink = WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(this.getClass()).getProcediment(procediment.getId())
                ).withSelfRel();
                resource.add(selfLink);
                procedimentResources.add(resource);
            }

            Link link = WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(this.getClass()).getProcediments(entitatCodi, codi, nom, organGestor, pageable)
            ).withSelfRel();

            PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(
                    procedimentsPage.getSize(), procedimentsPage.getNumber(), procedimentsPage.getTotalElements(), procedimentsPage.getTotalPages()
            );

            PagedModel<EntityModel<Procediment>> pagedResources = PagedModel.of(procedimentResources, metadata, link);
            return new ResponseEntity<PagedModel<EntityModel<Procediment>>>(pagedResources, HttpStatus.OK);

        } catch (EntitatNotFoundException e) {
            throw new ResourceNotFoundException(e.getDefaultMessage(), e);
        } catch (AccessDeniedException ade) {
            throw new AccessDenegatException(Arrays.asList("PBL_WS", "PBL_REPRES"));
        } catch (Exception e) {
            log.error("Error obtenint procediments", e);
            throw new ServiceExecutionException(e.getMessage(), e);
        }
    }

    /**
     * Recupera un procediment pel seu ID.
     * @param procedimentId ID del procediment.
     * @return Dades del procediment.
     */
    @GetMapping(value = "/{procedimentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    // IMPORTANT: Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI definida a la interfície ProcedimentApi.
    public ResponseEntity<EntityModel<Procediment>> getProcediment(
            @PathVariable Long procedimentId) {
        try {
            Procediment procediment = gestioRestService.getProcedimentById(procedimentId);
            if (procediment == null) {
                return new ResponseEntity<EntityModel<Procediment>>(HttpStatus.NOT_FOUND);
            }

            EntityModel<Procediment> resource = EntityModel.of(procediment);
            Link selfLink = WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(this.getClass()).getProcediment(procedimentId)
            ).withSelfRel();
            resource.add(selfLink);
            return new ResponseEntity<EntityModel<Procediment>>(resource, HttpStatus.OK);
        } catch (AccessDeniedException ade) {
            throw new AccessDenegatException(Arrays.asList("PBL_WS", "PBL_REPRES"));
        } catch (Exception e) {
            log.error("Error obtenint procediment " + procedimentId, e);
            throw new ServiceExecutionException(e.getMessage(), e);
        }
    }

    /**
     * Recupera un procediment pel seu Codi.
     * @param procedimentCodi Codi del procediment.
     * @param entitatCodi Codi de l'entitat.
     * @return Dades del procediment.
     */
    @GetMapping(value = "/byCodi/{procedimentCodi}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    // IMPORTANT: Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI definida a la interfície ProcedimentApi.
    public ResponseEntity<EntityModel<Procediment>> getProcediment(
            @PathVariable String procedimentCodi,
            @RequestParam("entitatCodi") String entitatCodi) {
        try {
            Procediment procediment = gestioRestService.getProcedimentAmbEntitatICodi(entitatCodi, procedimentCodi);
            if (procediment == null) {
                return new ResponseEntity<EntityModel<Procediment>>(HttpStatus.NOT_FOUND);
            }

            EntityModel<Procediment> resource = EntityModel.of(procediment);
            Link selfLink = WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(this.getClass()).getProcediment(procedimentCodi, entitatCodi)
            ).withSelfRel();
            resource.add(selfLink);
            return new ResponseEntity<EntityModel<Procediment>>(resource, HttpStatus.OK);
        } catch (EntitatNotFoundException e) {
            throw new ResourceNotFoundException(e.getDefaultMessage(), e);
        } catch (AccessDeniedException ade) {
            throw new AccessDenegatException(Arrays.asList("PBL_WS", "PBL_REPRES"));
        } catch (Exception e) {
            log.error("Error obtenint procediment " + procedimentCodi, e);
            throw new ServiceExecutionException(e.getMessage(), e);
        }
    }


    /**
     * Recupera procediments amb filtratge i paginació.
     * @param procedimentId ID del procediment.
     * @param pageable Informació de paginació.
     * @return Pàgina de procediments.
     */
    @GetMapping(value = "/{procedimentId}/serveis", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    // IMPORTANT: Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI definida a la interfície ProcedimentApi.
    public ResponseEntity<PagedModel<EntityModel<Servei>>> getProcedimentServeis(
            @PathVariable Long procedimentId,
            @PageableDefault(size = 10) Pageable pageable) {
        try {
            return getPagedProcedimentServeis(procedimentId, pageable);

        } catch (ProcedimentNotFoundException e) {
            throw new ResourceNotFoundException(e.getDefaultMessage(), e);
        } catch (AccessDeniedException ade) {
            throw new AccessDenegatException(Arrays.asList("PBL_WS", "PBL_REPRES"));
        } catch (Exception e) {
            log.error("Error obtenint serveis del procediment " + procedimentId, e);
            throw new ServiceExecutionException(e.getMessage(), e);
        }
    }

    /**
     * Recupera procediments amb filtratge i paginació.
     * @param procedimentCodi Codi del procediment.
     * @param pageable Informació de paginació.
     * @return Pàgina de procediments.
     */
    @GetMapping(value = "/byCodi/{procedimentCodi}/serveis", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    // IMPORTANT: Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI definida a la interfície ProcedimentApi.
    public ResponseEntity<PagedModel<EntityModel<Servei>>> getProcedimentServeisByCodi(
            @PathVariable("procedimentCodi") String procedimentCodi,
            @RequestParam("entitatCodi") String entitatCodi,
            @PageableDefault(size = 10) Pageable pageable) {
        try {
            Procediment procediment = gestioRestService.getProcedimentAmbEntitatICodi(entitatCodi, procedimentCodi);
            if (procediment == null) {
                throw new ProcedimentNotFoundException(procedimentCodi);
            }

            return getPagedProcedimentServeis(procediment.getId(), pageable);

        } catch (ProcedimentNotFoundException e) {
            throw new ResourceNotFoundException(e.getDefaultMessage(), e);
        } catch (AccessDeniedException ade) {
            throw new AccessDenegatException(Arrays.asList("PBL_WS", "PBL_REPRES"));
        } catch (Exception e) {
            log.error("Error obtenint serveis del procediment " + procedimentCodi, e);
            throw new ServiceExecutionException(e.getMessage(), e);
        }
    }

    private ResponseEntity<PagedModel<EntityModel<Servei>>> getPagedProcedimentServeis(Long procedimentId, Pageable pageable) throws ProcedimentNotFoundException {
        Page<Servei> serveisPage = gestioRestService.findServeisByProcedimentPaginat(procedimentId, pageable);
        if (serveisPage == null || serveisPage.getContent().isEmpty()) {
            return new ResponseEntity<PagedModel<EntityModel<Servei>>>(HttpStatus.NO_CONTENT);
        }

        List<EntityModel<Servei>> serveiResources = new ArrayList<>();
        for (Servei servei : serveisPage.getContent()) {
            EntityModel<Servei> resource = EntityModel.of(servei);
            Link selfLink = WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(ServeiRestController.class).getServei(servei.getCodi())
            ).withSelfRel();
            resource.add(selfLink);
            serveiResources.add(resource);
        }

        Link link = WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(this.getClass()).getProcedimentServeis(procedimentId, pageable)
        ).withSelfRel();

        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(
                serveisPage.getSize(), serveisPage.getNumber(), serveisPage.getTotalElements(), serveisPage.getTotalPages()
        );

        PagedModel<EntityModel<Servei>> pagedResources = PagedModel.of(serveiResources, metadata, link);
        return new ResponseEntity<PagedModel<EntityModel<Servei>>>(pagedResources, HttpStatus.OK);
    }

}
