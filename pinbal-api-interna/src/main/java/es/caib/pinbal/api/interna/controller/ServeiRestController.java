package es.caib.pinbal.api.interna.controller;

import es.caib.pinbal.api.interna.api.ServeiApi;
import es.caib.pinbal.client.serveis.Servei;
import es.caib.pinbal.logic.intf.dto.apiresponse.ServiceExecutionException;
import es.caib.pinbal.logic.intf.service.GestioRestService;
import es.caib.pinbal.logic.intf.service.exception.AccessDenegatException;
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
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/serveis")
public class ServeiRestController extends PinbalHalRestController implements ServeiApi {

    private final GestioRestService gestioRestService;


    /**
     * Recupera serveis amb filtratge i paginació.
     * @param codi Part del codi del servei. Per filtrar (opcional).
     * @param descripcio Part de la descripcio del servei. Per filtrar (opcional).
     * @param pageable Informació de paginació.
     * @return Pàgina de procediments.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    // IMPORTANT: Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI definida a la interfície ServeiApi.
    public ResponseEntity<PagedModel<EntityModel<Servei>>> getServeis(
            @RequestParam(required = false) String codi,
            @RequestParam(required = false) String descripcio,
            @PageableDefault(size = 10) Pageable pageable) {
        try {
            Page<Servei> serveisPage = gestioRestService.findServeisPaginat(codi, descripcio, pageable);

            if (serveisPage == null || serveisPage.getContent().isEmpty()) {
                return new ResponseEntity<PagedModel<EntityModel<Servei>>>(HttpStatus.NO_CONTENT);
            }

            List<EntityModel<Servei>> serveiResources = new ArrayList<>();
            for (Servei servei : serveisPage.getContent()) {
                EntityModel<Servei> resource = EntityModel.of(servei);
                Link selfLink = WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(this.getClass()).getServei(servei.getCodi())
                ).withSelfRel();
                resource.add(selfLink);
                serveiResources.add(resource);
            }

            Link link = WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(this.getClass()).getServeis(codi, descripcio, pageable)
            ).withSelfRel();

            PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(
                    serveisPage.getSize(), serveisPage.getNumber(), serveisPage.getTotalElements(), serveisPage.getTotalPages()
            );

            PagedModel<EntityModel<Servei>> pagedResources = PagedModel.of(serveiResources, metadata, link);
            return new ResponseEntity<PagedModel<EntityModel<Servei>>>(pagedResources, HttpStatus.OK);

        } catch (AccessDeniedException ade) {
            throw new AccessDenegatException(Arrays.asList("PBL_WS", "PBL_REPRES"));
        } catch (Exception e) {
            log.error("Error obtenint serveis", e);
            throw new ServiceExecutionException(e.getMessage(), e);
        }
    }

    /**
     * Recupera un servei pel seu Codi.
     * @param serveiCodi Codi del servei.
     * @return Dades del servei.
     */
    @GetMapping(value = "/{serveiCodi}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    // IMPORTANT: Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI definida a la interfície ServeiApi.
    public ResponseEntity<EntityModel<Servei>> getServei(
            @PathVariable String serveiCodi) {
        try {
            Servei servei = gestioRestService.getServeiByCodi(serveiCodi);
            if (servei == null) {
                return new ResponseEntity<EntityModel<Servei>>(HttpStatus.NOT_FOUND);
            }

            EntityModel<Servei> resource = EntityModel.of(servei);
            Link selfLink = WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(this.getClass()).getServei(serveiCodi)
            ).withSelfRel();
            resource.add(selfLink);
            return new ResponseEntity<EntityModel<Servei>>(resource, HttpStatus.OK);
        } catch (AccessDeniedException ade) {
            throw new AccessDenegatException(Arrays.asList("PBL_WS", "PBL_REPRES"));
        } catch (Exception e) {
            log.error("Error obtenint el servei " + serveiCodi, e);
            throw new ServiceExecutionException(e.getMessage(), e);
        }
    }
    
}
