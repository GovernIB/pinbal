package es.caib.pinbal.api.interna.controller.estadistica.v1;

import com.wordnik.swagger.annotations.Api;
import es.caib.pinbal.api.interna.controller.PinbalHalRestController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
//@RequestMapping("/entitats/{entitatCodi}")
@Api(value = "API Serveis v1", description = "Operacions relacionades amb Estadístiques")
public class EstadisticaRestController extends PinbalHalRestController {

//    @Autowired
//    private GestioRestService gestioRestService;
//
//
//    /**
//     * Recupera un servei pel seu Codi.
//     * @param serveiCodi Codi del servei.
//     * @return Dades del servei.
//     */
//    @ApiVersion("1")
//    @RequestMapping(value = "/serveis/{serveiCodi}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    @ApiOperation(value = "Obtén un servei pel seu codi",
//            response = Servei.class,
//            notes = "Aquest mètode retorna els detalls d'un servei específic identificat pel seu codi.")
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Servei obtingut amb èxit"),
//            @ApiResponse(code = 404, message = "Servei no trobat")
//    })
//    public @ResponseBody ResponseEntity<Resource<Servei>> getServei(
//            @ApiParam(value = "Codi del servei", required = true) @PathVariable("serveiCodi") String serveiCodi) {
//        try {
//            Servei servei = gestioRestService.getServeiByCodi(serveiCodi);
//            if (servei == null) {
//                return new ResponseEntity<Resource<Servei>>(HttpStatus.NOT_FOUND);
//            }
//
//            Resource<Servei> resource = new Resource<Servei>(servei);
//            Link selfLink = ControllerLinkBuilder.linkTo(
//                    ControllerLinkBuilder.methodOn(this.getClass()).getServei(serveiCodi)
//            ).withSelfRel();
//            resource.add(selfLink);
//            return new ResponseEntity<Resource<Servei>>(resource, HttpStatus.OK);
//        } catch (AccessDeniedException ade) {
//            throw new AccessDenegatException(Arrays.asList("PBL_WS", "PBL_REPRES"));
//        } catch (Exception e) {
//            log.error("Error obtenint el servei " + serveiCodi, e);
//            throw new ServiceExecutionException(e.getMessage(), e);
//        }
//    }
    
}
