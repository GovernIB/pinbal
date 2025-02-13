package es.caib.pinbal.api.interna.openapi.interficies.recobriment.v2;

import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import es.caib.pinbal.api.config.ApiVersion;
import es.caib.pinbal.client.procediments.Procediment;
import es.caib.pinbal.client.recobriment.model.ScspJustificante;
import es.caib.pinbal.client.recobriment.v2.DadaEspecifica;
import es.caib.pinbal.client.recobriment.v2.Entitat;
import es.caib.pinbal.client.recobriment.v2.PeticioAsincrona;
import es.caib.pinbal.client.recobriment.v2.PeticioRespostaAsincrona;
import es.caib.pinbal.client.recobriment.v2.PeticioRespostaSincrona;
import es.caib.pinbal.client.recobriment.v2.PeticioSincrona;
import es.caib.pinbal.client.recobriment.v2.ValorEnum;
import es.caib.pinbal.client.serveis.Servei;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@ApiVersion("2")
public interface RecobrimentRestV2Intf {

    // Obtencio d'entitats
    // /////////////////////////////////////////////////////////////

    @ApiOperation(value = "Obtén les entitat",
            notes = "Aquesta operació retorna la llista d'entitats a les que l'usuari autenticat té permís.",
            response = Entitat.class,
            responseContainer = "List"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Entitats obtingudes amb èxit"),
            @ApiResponse(code = 204, message = "No s'han trobat entitats"),
            @ApiResponse(code = 500, message = "Error intern del servidor")
    })
    @RequestMapping(value = "/entitats", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<Entitat>> getEntitats();

    // Obtencio de procediments
    // /////////////////////////////////////////////////////////////

    @ApiOperation(value = "Obtén els procediments d'una entitat",
            notes = "Aquesta operació retorna una llista de procediments disponibles per l'entitat especificada a 'entitatCodi'.",
            response = Procediment.class,
            responseContainer = "List"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Procediments obtinguts amb èxit"),
            @ApiResponse(code = 204, message = "No s'han trobat procediments"),
            @ApiResponse(code = 404, message = "Entitat no trobada"),
            @ApiResponse(code = 500, message = "Error intern del servidor")
    })
    @RequestMapping(value = "/entitats/{entitatCodi}/procediments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<Procediment>> getProcediments(@ApiParam(value = "Codi de l'entitat") @PathVariable("entitatCodi") String entitatCodi);

    // Obtenció de serveis
    // /////////////////////////////////////////////////////////////

    @ApiOperation(value = "Obtén tots els serveis de Pinbal",
            notes = "Aquesta operació retorna una llista de serveis disponibles a Pinbal",
            response = Servei.class,
            responseContainer = "List"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Serveis obtinguts amb èxit"),
            @ApiResponse(code = 204, message = "No s'han trobat serveis"),
            @ApiResponse(code = 500, message = "Error intern del servidor")
    })
    @RequestMapping(value = "/serveis", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<Servei>> getServeis();

    @ApiOperation(value = "Obtén tots els serveis de Pinbal per entitat",
            notes = "Aquesta operació retorna una llista dels serveis disponibles a Pinbal d'una entitat",
            response = Servei.class,
            responseContainer = "List"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Serveis obtinguts amb èxit"),
            @ApiResponse(code = 204, message = "No s'han trobat serveis"),
            @ApiResponse(code = 404, message = "Entitat no trobada"),
            @ApiResponse(code = 500, message = "Error intern del servidor")
    })
    @RequestMapping(value = "/entitat/{entitatCodi}/serveis", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<Servei>> getServeisPerEntitat(@ApiParam(value = "Codi de l'entitat") @PathVariable("entitatCodi") String entitatCodi);

    @ApiOperation(value = "Obtén tots els serveis de Pinbal per procediment",
            notes = "Aquesta operació retorna una llista de serveis disponibles a Pinbal per un procediment",
            response = Servei.class,
            responseContainer = "List"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Serveis obtinguts amb èxit"),
            @ApiResponse(code = 204, message = "No s'han trobat serveis"),
            @ApiResponse(code = 500, message = "Error intern del servidor")
    })
    @RequestMapping(value = "/procediment/{procedimentCodi}/serveis", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<Servei>> getServeisPerProcediment(@ApiParam(value = "Codi del procediment") @PathVariable("procedimentCodi") String procedimentCodi);

    // Obtenció de dades específiques
    // /////////////////////////////////////////////////////////////

    @ApiOperation(value = "Obtén les dades específiques d'un servei",
            notes = "Aquesta operació retorna lista de camps que son necessaris per emplenar l’apartat de dades específiques de la petició SCSP al servei web final.",
            response = DadaEspecifica.class,
            responseContainer = "List"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Dades específiqeus obtingudes amb èxit"),
            @ApiResponse(code = 204, message = "No s'han trobat dades específiques pel servei"),
            @ApiResponse(code = 404, message = "Servei no trobat"),
            @ApiResponse(code = 500, message = "Error intern del servidor")
    })
    @RequestMapping(value = "/serveis/{serveiCodi}/dadesEspecifiques", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<DadaEspecifica>> getDadesEspecifiques(@ApiParam(value = "Codi del servei") @PathVariable("serveiCodi") String serveiCodi);

    @ApiOperation(value = "Obtén tots els serveis de Pinbal per procediment",
            notes = "llistes de valors, siguin de enumerats o de valors de dades externes. Se li passa el codi enum que s’obté de la cridada anterior quant el camp és de tipus enumerat. També se li passa opcionalment un filtre a aplicar que pot tenir varis comportaments segons el enumerat",
            response = ValorEnum.class,
            responseContainer = "List"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Valors d'enumerats obtinguts amb èxit"),
            @ApiResponse(code = 204, message = "No s'han trobat valors d'enumerat"),
            @ApiResponse(code = 404, message = "Servei o enumerat no trobat"),
            @ApiResponse(code = 500, message = "Error intern del servidor")
    })
    @RequestMapping(value = "/serveis/{serveiCodi}/camps/{campCodi}/enumerat/{enumCodi}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<ValorEnum>> getValorsEnum(
            @ApiParam(value = "Codi del servei") @PathVariable("serveiCodi") String serveiCodi,
            @ApiParam(value = "Codi del camp") @PathVariable("campCodi") String campCodi,
            @ApiParam(value = "Codi de l'enumerat") @PathVariable("enumCodi") String enumCodi,
            @ApiParam(value = "Filtre a aplicar en l’obtenció dels possibles valors de l’enumerat (opcional)") @RequestParam(required = false) String filtre);

    // Realització de consultes
    // /////////////////////////////////////////////////////////////

    ResponseEntity<PeticioRespostaSincrona> peticioSincrona(PeticioSincrona peticio);
    ResponseEntity<PeticioRespostaAsincrona> peticioAsincrona(PeticioAsincrona peticio);
    ResponseEntity<PeticioRespostaSincrona> getRespuesta(String idPeticio);
    ResponseEntity<ScspJustificante> getJustificant(String idPeticio, String idSolicitud) throws Exception;
    ResponseEntity<ScspJustificante> getJustificanteImprimible(String idPeticio, String idSolicitud) throws Exception;

}
