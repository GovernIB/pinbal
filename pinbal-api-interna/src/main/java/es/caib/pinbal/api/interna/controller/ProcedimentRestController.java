package es.caib.pinbal.api.interna.controller;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import es.caib.pinbal.api.config.ApiVersion;
import es.caib.pinbal.client.comu.Create;
import es.caib.pinbal.client.comu.Update;
import es.caib.pinbal.client.procediments.Procediment;
import es.caib.pinbal.client.procediments.ProcedimentPatch;
import es.caib.pinbal.client.serveis.Servei;
import es.caib.pinbal.core.dto.apiresponse.ServiceExecutionException;
import es.caib.pinbal.core.service.GestioRestService;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.InvalidInputException;
import es.caib.pinbal.core.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.core.service.exception.ResourceNotFoundException;
import es.caib.pinbal.core.service.exception.ServeiNotFoundException;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
//@RequestMapping("/entitats/{entitatCodi}")
@Api(value = "API Procediments v1", description = "Operacions relacionades amb Procediments")
public class ProcedimentRestController extends PinbalHalRestController {

    @Autowired
    private GestioRestService gestioRestService;

    /**
     * Crea un nou procediment.
     * @param procediment Dades del procediment a crear.
     * @return Procédiment creat amb enllaç HATEOAS.
     */
    @ApiVersion("1")
    @PreAuthorize("hasRole('PBL_WS')")
    @RequestMapping(value = "/procediments", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Crear un nou procediment", response = Procediment.class)
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "Authorization", value = "Basic auth credentials", required = true, dataType = "string", paramType = "header")
//    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Procediment creat amb èxit"),
            @ApiResponse(code = 400, message = "Entrada invàlida")
    })
    public @ResponseBody ResponseEntity<Resource<Procediment>> createProcediment(
            @ApiParam(value = "Dades del procediment a crear", required = true) @Validated(Create.class) @RequestBody Procediment procediment,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InvalidInputException(bindingResult);
        }
        try {
            Procediment createdProcediment = gestioRestService.create(procediment);
            Resource<Procediment> procedimentResource = new Resource<>(
                    createdProcediment,
                    ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(ProcedimentRestController.class).getProcediment(createdProcediment.getId())).withSelfRel());
            return new ResponseEntity<>(procedimentResource, HttpStatus.CREATED);
        } catch (EntitatNotFoundException e) {
            throw new ResourceNotFoundException(e.getDefaultMessage());
        } catch (Exception e) {
            throw new ServiceExecutionException(e.getMessage(), e);
        }
    }

    /**
     * Modifica un procediment.
     * @param 
     * @param procediment Dades del procediment a crear.
     * @return Procédiment creat amb enllaç HATEOAS.
     */
    @ApiVersion("1")
    @PreAuthorize("hasRole('PBL_WS')")
    @RequestMapping(value = "/procediments/{procedimentId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Modifica un procediment pel seu ID", response = Procediment.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Procediment modificat amb èxit"),
            @ApiResponse(code = 404, message = "Procediment no trobat"),
            @ApiResponse(code = 400, message = "Entrada invàlida")
    })
    public @ResponseBody Resource<Procediment> updateProcediment(
            @ApiParam(value = "ID del procediment", required = true) @PathVariable("procedimentId") Long procedimentId,
            @ApiParam(value = "Dades del procediment a crear", required = true) @Validated(Update.class) @RequestBody Procediment procediment,
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
            return new Resource<>(
                    createdProcediment,
                    ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(ProcedimentRestController.class).getProcediment(createdProcediment.getId())).withSelfRel());
        } catch (ProcedimentNotFoundException e) {
            throw new ResourceNotFoundException("Procediment no trobat");
        } catch (Exception e) {
            throw new ServiceExecutionException(e.getMessage(), e);
        }
    }

    @ApiVersion("1")
    @PreAuthorize("hasRole('PBL_WS')")
    @RequestMapping(value = "/procediments/{procedimentId}", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Modifica parcialment un procediment pel seu ID", response = Procediment.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Procediment modificat amb èxit"),
            @ApiResponse(code = 404, message = "Procediment no trobat"),
            @ApiResponse(code = 400, message = "Entrada invàlida")
    })
    public @ResponseBody Resource<Procediment> patchProcediment(
            @ApiParam(value = "ID del procediment", required = true) @PathVariable("procedimentId") Long procedimentId,
            @ApiParam(value = "Dades del procediment a modificar", required = true) @RequestBody ProcedimentPatch procedimentPatch) {
        try {
            Procediment updatedProcediment = gestioRestService.updateParcial(procedimentId, procedimentPatch);
            return new Resource<>(
                    updatedProcediment,
                    ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(ProcedimentRestController.class).getProcediment(updatedProcediment.getId())).withSelfRel());
        } catch (ProcedimentNotFoundException e) {
            throw new ResourceNotFoundException(e.getDefaultMessage());
        } catch (Exception e) {
            throw new ServiceExecutionException(e.getMessage(), e);
        }
    }

    /**
     * Habilita un servei per a un procediment.
     * @param procedimentId ID del procediment.
     * @param serveiCodi Codi del servei.
     */
    @ApiVersion("1")
    @PreAuthorize("hasRole('PBL_WS')")
    @RequestMapping(value = "/procediments/{procedimentId}/serveis/{serveiCodi}/enable", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Habilita un servei per a un procediment")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Servei habilitat amb èxit"),
            @ApiResponse(code = 404, message = "Procediment o Servei no trobat")
    })
    public @ResponseBody void enableServeiToProcediment(
            @ApiParam(value = "ID del procediment", required = true) @PathVariable("procedimentId") Long procedimentId,
            @ApiParam(value = "Codi del servei", required = true) @PathVariable("serveiCodi") String serveiCodi) {
        try {
            gestioRestService.serveiEnable(procedimentId, serveiCodi);
        } catch (ProcedimentNotFoundException | ServeiNotFoundException e) {
            throw new ResourceNotFoundException(e.getMessage(), e);
        } catch (Exception e) {
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
    @ApiVersion("1")
    @PreAuthorize("hasRole('PBL_WS')")
    @RequestMapping(value = "/procediments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Obtén procediments amb filtratge i paginació",
            response = PagedResources.class,
            notes = "Els paràmetres de pàgina inclouen: " +
                    "page (número de la pàgina, comença per 0), " +
                    "size (mida de la pàgina), " +
                    "sort (ordre, e.g., sort=field1,asc&sort=field2,desc)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Procediments obtinguts amb èxit"),
            @ApiResponse(code = 204, message = "No s'han trobat procediments"),
            @ApiResponse(code = 404, message = "Entitat no trobada")
    })
    public @ResponseBody ResponseEntity<PagedResources<Resource<Procediment>>> getProcediments(
            @ApiParam(value = "Codi de l'entitat", required = true) @RequestParam("entitatCodi") String entitatCodi,
            @ApiParam(value = "Part del codi del procediment. Per filtrar (opcional)") @RequestParam(required = false) String codi,
            @ApiParam(value = "Part del nom del procediment. Per filtrar (opcional)") @RequestParam(required = false) String nom,
            @ApiParam(value = "Codi Dir3 de l'òrgan gestor del procediment. Per filtrar (opcional)") @RequestParam(required = false) String organGestor,
            @PageableDefault(size = 10) Pageable pageable) {
        try {
            Page<Procediment> procedimentsPage = gestioRestService.findProcedimentsPaginat(entitatCodi, codi, nom, organGestor, pageable);

            if (procedimentsPage == null || procedimentsPage.getContent().isEmpty()) {
                return new ResponseEntity<PagedResources<Resource<Procediment>>>(HttpStatus.NO_CONTENT);
            }

            List<Resource<Procediment>> procedimentResources = new ArrayList<>();
            for (Procediment procediment : procedimentsPage.getContent()) {
                Resource<Procediment> resource = new Resource<>(procediment);
                Link selfLink = ControllerLinkBuilder.linkTo(
                        ControllerLinkBuilder.methodOn(this.getClass()).getProcediment(procediment.getId())
                ).withSelfRel();
                resource.add(selfLink);
                procedimentResources.add(resource);
            }

            Link link = ControllerLinkBuilder.linkTo(
                    ControllerLinkBuilder.methodOn(this.getClass()).getProcediments(entitatCodi, codi, nom, organGestor, pageable)
            ).withSelfRel();

            PagedResources.PageMetadata metadata = new PagedResources.PageMetadata(
                    procedimentsPage.getSize(), procedimentsPage.getNumber(), procedimentsPage.getTotalElements(), procedimentsPage.getTotalPages()
            );

            PagedResources<Resource<Procediment>> pagedResources = new PagedResources<>(procedimentResources, metadata, link);
            return new ResponseEntity<PagedResources<Resource<Procediment>>>(pagedResources, HttpStatus.OK);

        } catch (EntitatNotFoundException e) {
            throw new ResourceNotFoundException(e.getDefaultMessage(), e);
        } catch (Exception e) {
            throw new ServiceExecutionException(e.getMessage(), e);
        }
    }

    /**
     * Recupera un procediment pel seu ID.
     * @param procedimentId ID del procediment.
     * @return Dades del procediment.
     */
    @ApiVersion("1")
    @PreAuthorize("hasRole('PBL_WS')")
    @RequestMapping(value = "/procediments/{procedimentId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Obtén un procediment pel seu ID",
            response = Procediment.class,
            notes = "Aquest mètode retorna els detalls d'un procediment específic identificat pel seu ID.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Procediment obtingut amb èxit"),
            @ApiResponse(code = 204, message = "No s'ha trobat el procediment"),
            @ApiResponse(code = 404, message = "Procediment no trobat")
    })
    public @ResponseBody ResponseEntity<Resource<Procediment>> getProcediment(
            @ApiParam(value = "ID del procediment", required = true) @PathVariable("procedimentId") Long procedimentId) {
        try {
            Procediment procediment = gestioRestService.getProcedimentById(procedimentId);
            if (procediment == null) {
                return new ResponseEntity<Resource<Procediment>>(HttpStatus.NOT_FOUND);
            }

            Resource<Procediment> resource = new Resource<Procediment>(procediment);
            Link selfLink = ControllerLinkBuilder.linkTo(
                    ControllerLinkBuilder.methodOn(this.getClass()).getProcediment(procedimentId)
            ).withSelfRel();
            resource.add(selfLink);
            return new ResponseEntity<Resource<Procediment>>(resource, HttpStatus.OK);
        } catch (Exception e) {
            throw new ServiceExecutionException(e.getMessage(), e);
        }
    }

    /**
     * Recupera un procediment pel seu Codi.
     * @param procedimentCodi Codi del procediment.
     * @param entitatCodi Codi de l'entitat.
     * @return Dades del procediment.
     */
    @ApiVersion("1")
    @PreAuthorize("hasRole('PBL_WS')")
    @RequestMapping(value = "/procediments/byCodi/{procedimentCodi}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Obtén un procediment pel seu ID",
            response = Procediment.class,
            notes = "Aquest mètode retorna els detalls d'un procediment específic identificat pel seu ID.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Procediment obtingut amb èxit"),
            @ApiResponse(code = 204, message = "No s'ha trobat el procediment"),
            @ApiResponse(code = 404, message = "Procediment no trobat")
    })
    public @ResponseBody ResponseEntity<Resource<Procediment>> getProcediment(
            @ApiParam(value = "Codi del procediment", required = true) @PathVariable("procedimentCodi") String procedimentCodi,
            @ApiParam(value = "Codi de l'entitat", required = true) @RequestParam("entitatCodi") String entitatCodi) {
        try {
            Procediment procediment = gestioRestService.getProcedimentAmbEntitatICodi(entitatCodi, procedimentCodi);
            if (procediment == null) {
                return new ResponseEntity<Resource<Procediment>>(HttpStatus.NOT_FOUND);
            }

            Resource<Procediment> resource = new Resource<Procediment>(procediment);
            Link selfLink = ControllerLinkBuilder.linkTo(
                    ControllerLinkBuilder.methodOn(this.getClass()).getProcediment(procedimentCodi, entitatCodi)
            ).withSelfRel();
            resource.add(selfLink);
            return new ResponseEntity<Resource<Procediment>>(resource, HttpStatus.OK);
        } catch (EntitatNotFoundException e) {
            throw new ResourceNotFoundException(e.getDefaultMessage(), e);
        } catch (Exception e) {
            throw new ServiceExecutionException(e.getMessage(), e);
        }
    }


    /**
     * Recupera procediments amb filtratge i paginació.
     * @param procedimentId ID del procediment.
     * @param pageable Informació de paginació.
     * @return Pàgina de procediments.
     */
    @ApiVersion("1")
    @PreAuthorize("hasRole('PBL_WS')")
    @RequestMapping(value = "/procediments/{procedimentId}/serveis", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Obtén els serveis d'un procediment amb filtratge i paginació",
            response = PagedResources.class,
            notes = "Els paràmetres de pàgina inclouen: " +
                    "page (número de la pàgina, comença per 0), " +
                    "size (mida de la pàgina), " +
                    "sort (ordre, e.g., sort=field1,asc&sort=field2,desc)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Serveis obtinguts amb èxit"),
            @ApiResponse(code = 204, message = "No s'han trobat serveis"),
            @ApiResponse(code = 404, message = "Entitat no trobada"),
            @ApiResponse(code = 404, message = "Procediment no trobat"),
    })
    public @ResponseBody ResponseEntity<PagedResources<Resource<Servei>>> getProcedimentServeis(
            @ApiParam(value = "ID del procediment", required = true) @PathVariable("procedimentId") Long procedimentId,
            @PageableDefault(size = 10) Pageable pageable) {
        try {
            return getPagedProcedimentServeis(procedimentId, pageable);

        } catch (ProcedimentNotFoundException e) {
            throw new ResourceNotFoundException(e.getDefaultMessage(), e);
        } catch (Exception e) {
            throw new ServiceExecutionException(e.getMessage(), e);
        }
    }

    /**
     * Recupera procediments amb filtratge i paginació.
     * @param procedimentCodi Codi del procediment.
     * @param pageable Informació de paginació.
     * @return Pàgina de procediments.
     */
    @ApiVersion("1")
    @PreAuthorize("hasRole('PBL_WS')")
    @RequestMapping(value = "/procediments/byCodi/{procedimentCodi}/serveis", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Obtén els serveis d'un procediment amb filtratge i paginació",
            response = PagedResources.class,
            notes = "Els paràmetres de pàgina inclouen: " +
                    "page (número de la pàgina, comença per 0), " +
                    "size (mida de la pàgina), " +
                    "sort (ordre, e.g., sort=field1,asc&sort=field2,desc)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Serveis obtinguts amb èxit"),
            @ApiResponse(code = 204, message = "No s'han trobat serveis"),
            @ApiResponse(code = 404, message = "Entitat no trobada"),
            @ApiResponse(code = 404, message = "Procediment no trobat"),
    })
    public @ResponseBody ResponseEntity<PagedResources<Resource<Servei>>> getProcedimentServeisByCodi(
            @ApiParam(value = "Codi del procediment", required = true) @PathVariable("procedimentCodi") String procedimentCodi,
            @ApiParam(value = "Codi de l'entitat", required = true) @RequestParam("entitatCodi") String entitatCodi,
            @PageableDefault(size = 10) Pageable pageable) {
        try {
            Procediment procediment = gestioRestService.getProcedimentAmbEntitatICodi(entitatCodi, procedimentCodi);
            if (procediment == null) {
                throw new ProcedimentNotFoundException(procedimentCodi);
            }

            return getPagedProcedimentServeis(procediment.getId(), pageable);

        } catch (ProcedimentNotFoundException e) {
            throw new ResourceNotFoundException(e.getDefaultMessage(), e);
        } catch (Exception e) {
            throw new ServiceExecutionException(e.getMessage(), e);
        }
    }

    private ResponseEntity<PagedResources<Resource<Servei>>> getPagedProcedimentServeis(Long procedimentId, Pageable pageable) throws ProcedimentNotFoundException {
        Page<Servei> serveisPage = gestioRestService.findServeisByProcedimentPaginat(procedimentId, pageable);
        if (serveisPage == null || serveisPage.getContent().isEmpty()) {
            return new ResponseEntity<PagedResources<Resource<Servei>>>(HttpStatus.NO_CONTENT);
        }

        List<Resource<Servei>> serveiResources = new ArrayList<>();
        for (Servei servei : serveisPage.getContent()) {
            Resource<Servei> resource = new Resource<>(servei);
            Link selfLink = ControllerLinkBuilder.linkTo(
                    ControllerLinkBuilder.methodOn(ServeiRestController.class).getServei(servei.getCodi())
            ).withSelfRel();
            resource.add(selfLink);
            serveiResources.add(resource);
        }

        Link link = ControllerLinkBuilder.linkTo(
                ControllerLinkBuilder.methodOn(this.getClass()).getProcedimentServeis(procedimentId, pageable)
        ).withSelfRel();

        PagedResources.PageMetadata metadata = new PagedResources.PageMetadata(
                serveisPage.getSize(), serveisPage.getNumber(), serveisPage.getTotalElements(), serveisPage.getTotalPages()
        );

        PagedResources<Resource<Servei>> pagedResources = new PagedResources<>(serveiResources, metadata, link);
        return new ResponseEntity<PagedResources<Resource<Servei>>>(pagedResources, HttpStatus.OK);
    }

}
