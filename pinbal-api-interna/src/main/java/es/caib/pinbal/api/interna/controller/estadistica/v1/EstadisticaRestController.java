package es.caib.pinbal.api.interna.controller.estadistica.v1;

import es.caib.comanda.model.server.monitoring.EstadistiquesInfo;
import es.caib.comanda.model.server.monitoring.RegistresEstadistics;
import es.caib.pinbal.api.interna.api.EstadisticaApi;
import es.caib.pinbal.api.interna.controller.PinbalHalRestController;
import es.caib.pinbal.logic.intf.service.EstadisticaService;
import es.caib.pinbal.logic.intf.service.exception.InvalidDateFormatException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/estadistiques/v1")
public class EstadisticaRestController extends PinbalHalRestController implements EstadisticaApi {

    private final EstadisticaService estadisticaService;

    private static SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    /**
     * Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI
     * definida a la interfície EstadisticaApi.
     */
    @Override
    @GetMapping(value = "/info", produces = MediaType.APPLICATION_JSON_VALUE)
    public EstadistiquesInfo estadistiquesInfo(HttpServletRequest request) throws IOException {
        return estadisticaService.getEstadistiquesInfo();
    }

    /**
     * Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI
     * definida a la interfície EstadisticaApi.
     */
    @Override
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public RegistresEstadistics estadistiques(HttpServletRequest request) throws IOException {
        return estadisticaService.consultaUltimesEstadistiques();
    }

    /**
     * Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI
     * definida a la interfície EstadisticaApi.
     */
    @Override
    @GetMapping(value = "/of/{data}", produces = MediaType.APPLICATION_JSON_VALUE)
    public RegistresEstadistics estadistiques(
            HttpServletRequest request,
            @PathVariable String data) throws Exception {
        return estadisticaService.consultaEstadistiques(toDate(data));
    }

    /**
     * Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI
     * definida a la interfície EstadisticaApi.
     */
    @Override
    @GetMapping(value = "/from/{dataInici}/to/{dataFi}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RegistresEstadistics> estadistiques(
            HttpServletRequest request,
            @PathVariable String dataInici,
            @PathVariable String dataFi) throws Exception {
        return estadisticaService.consultaEstadistiques(toDate(dataInici), toDate(dataFi));
    }

    /**
     * Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI
     * definida a la interfície EstadisticaApi.
     */
    @Override
    @GetMapping(value = "/generar/from/{dataInici}/to/{dataFi}")
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
