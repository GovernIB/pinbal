package es.caib.pinbal.api.interna.controller.estadistica.v1;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import es.caib.comanda.ms.estadistica.model.EstadistiquesInfo;
import es.caib.comanda.ms.estadistica.model.RegistresEstadistics;
import es.caib.pinbal.api.config.ApiVersion;
import es.caib.pinbal.api.interna.controller.PinbalHalRestController;
import es.caib.pinbal.core.service.EstadisticaService;
import es.caib.pinbal.core.service.exception.InvalidDateFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@Api(value = "API Serveis v1", description = "Operacions relacionades amb Estadístiques")
public class EstadisticaRestController extends PinbalHalRestController {

    @Autowired
    private EstadisticaService estadisticaService;

    private static SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    @ApiVersion("1")
    @ApiOperation(value = "Obtén informació de les estadístiques de l'aplicació",
            response = EstadistiquesInfo.class,
            notes = "Aquest mètode retorna informació detallada de les estadístiques de l'aplicació.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Informació obtinguda amb èxit"),
            @ApiResponse(code = 404, message = "Informació no trobada")
    })
    @RequestMapping(value = "/estadistiques/info", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EstadistiquesInfo estadistiquesInfo(HttpServletRequest request) throws IOException {

        return estadisticaService.getEstadistiquesInfo();
    }


    @ApiVersion("1")
    @ApiOperation(value = "Obté informació estadística del dia anterior",
            response = RegistresEstadistics.class,
            notes = "Aquest mètode retorna informació estadística del dia anterior.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Informació obtinguda amb èxit"),
            @ApiResponse(code = 404, message = "Informació no trobada")
    })
    @RequestMapping(value = "/estadistiques", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RegistresEstadistics estadistiques(HttpServletRequest request) throws IOException {

        return estadisticaService.consultaUltimesEstadistiques();
    }

    @ApiVersion("1")
    @ApiOperation(value = "Obté informació estadística del dia indicat",
            response = RegistresEstadistics.class,
            notes = "Aquest mètode retorna informació estadística del dia indicat a través del paràmetre data.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Informació obtinguda amb èxit"),
            @ApiResponse(code = 404, message = "Informació no trobada")
    })
    @RequestMapping(value = "/estadistiques/of/{data}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RegistresEstadistics estadistiques(
            HttpServletRequest request,
            @ApiParam(value = "Data de la que es volen obtenir les dades estadístiques. El format ha de ser dd-MM-yyyy", required = true)
            @PathVariable String data) throws Exception {

        return estadisticaService.consultaEstadistiques(toDate(data));
    }

    @ApiVersion("1")
    @ApiOperation(value = "Obté informació estadística del dies indicats",
            response = RegistresEstadistics.class,
            responseContainer = "List",
            notes = "Aquest mètode retorna informació estadística del dies indicats, des de la data d'inici (paràmetre dataInici) fins a la data de fi (paràmetre dataFi), ambdós inclosos.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Informació obtinguda amb èxit"),
            @ApiResponse(code = 404, message = "Informació no trobada")
    })
    @RequestMapping(value = "/estadistiques/from/{dataInici}/to/{dataFi}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<RegistresEstadistics> estadistiques(
            HttpServletRequest request,
            @ApiParam(value = "Data inicial a partir de la qual volen obtenir les dades estadístiques. El format ha de ser dd-MM-yyyy", required = true)
            @PathVariable String dataInici,
            @ApiParam(value = "Data final fins a la qual volen obtenir les dades estadístiques. El format ha de ser dd-MM-yyyy", required = true)
            @PathVariable String dataFi) throws Exception {

        return estadisticaService.consultaEstadistiques(toDate(dataInici), toDate(dataFi));
    }

    @ApiIgnore
    @RequestMapping(value = "/generarEstadistiques/from/{dataInici}/to/{dataFi}", method = RequestMethod.GET)
    @ResponseBody
    public String generarEstadistiques(HttpServletRequest request, @PathVariable String dataInici, @PathVariable String dataFi) throws Exception {

        return estadisticaService.generarEstadistiques(toDate(dataInici), toDate(dataFi));
    }

    private Date toDate(String data) {
        try {
            return sdf.parse(data);
        } catch (ParseException e) {
            throw new InvalidDateFormatException("El format de la data ha de ser dd-MM-yyyy");
        }
    }
    
}
