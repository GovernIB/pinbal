package es.caib.pinbal.api.interna.controller;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import es.caib.pinbal.api.config.ApiVersion;
import es.caib.pinbal.client.serveis.Servei;
import es.caib.pinbal.core.dto.apiresponse.ServiceExecutionException;
import es.caib.pinbal.core.service.GestioRestService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
//@RequestMapping("/entitats/{entitatCodi}")
@Api(value = "API Serveis v1", description = "Operacions relacionades amb Serveis")
public class ServeiRestController extends PinbalHalRestController {

    @Autowired
    private GestioRestService gestioRestService;


    /**
     * Recupera serveis amb filtratge i paginació.
     * @param codi Part del codi del servei. Per filtrar (opcional).
     * @param descripcio Part de la descripcio del servei. Per filtrar (opcional).
     * @param pageable Informació de paginació.
     * @return Pàgina de procediments.
     */
    @ApiVersion("1")
    @PreAuthorize("hasRole('PBL_WS')")
    @RequestMapping(value = "/serveis", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Obtén tots els serveis amb paginació",
            response = PagedResources.class,
            notes = "Els paràmetres de pàgina inclouen: " +
                    "page (número de la pàgina, comença per 0), " +
                    "size (mida de la pàgina), " +
                    "sort (ordre, e.g., sort=field1,asc&sort=field2,desc)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Serveis obtinguts amb èxit"),
            @ApiResponse(code = 204, message = "No s'han trobat serveis")
    })
    public @ResponseBody ResponseEntity<PagedResources<Resource<Servei>>> getServeis(
            @ApiParam(value = "Part del codi del servei. Per filtrar (opcional)") @RequestParam(required = false) String codi,
            @ApiParam(value = "Part de la descripcio del servei. Per filtrar (opcional)") @RequestParam(required = false) String descripcio,
            @PageableDefault(size = 10) Pageable pageable) {
        try {
            Page<Servei> serveisPage = gestioRestService.findServeisPaginat(codi, descripcio, pageable);

            if (serveisPage == null || serveisPage.getContent().isEmpty()) {
                return new ResponseEntity<PagedResources<Resource<Servei>>>(HttpStatus.NO_CONTENT);
            }

            List<Resource<Servei>> serveiResources = new ArrayList<>();
            for (Servei servei : serveisPage.getContent()) {
                Resource<Servei> resource = new Resource<>(servei);
                Link selfLink = ControllerLinkBuilder.linkTo(
                        ControllerLinkBuilder.methodOn(this.getClass()).getServei(servei.getCodi())
                ).withSelfRel();
                resource.add(selfLink);
                serveiResources.add(resource);
            }

            Link link = ControllerLinkBuilder.linkTo(
                    ControllerLinkBuilder.methodOn(this.getClass()).getServeis(codi, descripcio, pageable)
            ).withSelfRel();

            PagedResources.PageMetadata metadata = new PagedResources.PageMetadata(
                    serveisPage.getSize(), serveisPage.getNumber(), serveisPage.getTotalElements(), serveisPage.getTotalPages()
            );

            PagedResources<Resource<Servei>> pagedResources = new PagedResources<>(serveiResources, metadata, link);
            return new ResponseEntity<PagedResources<Resource<Servei>>>(pagedResources, HttpStatus.OK);

        } catch (Exception e) {
            throw new ServiceExecutionException(e.getMessage(), e);
        }
    }

    /**
     * Recupera un servei pel seu Codi.
     * @param serveiCodi Codi del servei.
     * @return Dades del servei.
     */
    @ApiVersion("1")
    @PreAuthorize("hasRole('PBL_WS')")
    @RequestMapping(value = "/serveis/{serveiCodi}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Obtén un servei pel seu codi",
            response = Servei.class,
            notes = "Aquest mètode retorna els detalls d'un servei específic identificat pel seu codi.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Servei obtingut amb èxit"),
            @ApiResponse(code = 404, message = "Servei no trobat")
    })
    public @ResponseBody ResponseEntity<Resource<Servei>> getServei(
            @ApiParam(value = "Codi del servei", required = true) @PathVariable("serveiCodi") String serveiCodi) {
        try {
            Servei servei = gestioRestService.getServeiByCodi(serveiCodi);
            if (servei == null) {
                return new ResponseEntity<Resource<Servei>>(HttpStatus.NOT_FOUND);
            }

            Resource<Servei> resource = new Resource<Servei>(servei);
            Link selfLink = ControllerLinkBuilder.linkTo(
                    ControllerLinkBuilder.methodOn(this.getClass()).getServei(serveiCodi)
            ).withSelfRel();
            resource.add(selfLink);
            return new ResponseEntity<Resource<Servei>>(resource, HttpStatus.OK);
        } catch (Exception e) {
            throw new ServiceExecutionException(e.getMessage(), e);
        }
    }
    
}
