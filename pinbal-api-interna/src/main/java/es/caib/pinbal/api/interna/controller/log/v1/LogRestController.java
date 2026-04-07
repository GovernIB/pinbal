package es.caib.pinbal.api.interna.controller.log.v1;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import com.wordnik.swagger.annotations.Authorization;
import es.caib.comanda.ms.exception.ComandaApiException;
import es.caib.comanda.ms.log.helper.LogFileStream;
import es.caib.comanda.ms.log.helper.LogHelper;
import es.caib.comanda.ms.log.model.FitxerContingut;
import es.caib.comanda.ms.log.model.FitxerInfo;
import es.caib.comanda.ms.salut.model.AppInfo;
import es.caib.comanda.ms.salut.model.SalutInfo;
import es.caib.pinbal.api.config.ApiVersion;
import es.caib.pinbal.api.config.Iso8601DateDeserializer;
import es.caib.pinbal.api.interna.controller.PinbalHalRestController;
import es.caib.pinbal.api.interna.controller.RecobrimentRestController;
import es.caib.pinbal.client.comu.ErrorResponse;
import es.caib.pinbal.client.recobriment.v2.Entitat;
import es.caib.pinbal.core.service.SalutService;
import es.caib.pinbal.core.service.exception.ResourceNotFoundException;
import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/logs/v1")
@Api(value = "API Serveis v1", description = "Operacions relacionades amb Logs")
public class LogRestController extends PinbalHalRestController {

    @Autowired
    private SalutService salutService;

    @ApiVersion("1")
    @ApiOperation(value = "Obtenir el llistat de fitxers de log disponibles",
            notes = "Retorna una llista amb tots els fitxers que es troben dins la carpeta de logs del servidor de l'aplicació",
            response = FitxerInfo.class,
            responseContainer = "List",
            authorizations = @Authorization(value = "basicAuth"),
            tags = "logs")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Llista de fitxers obtinguda correctament"),
            @ApiResponse(code = 500, message = "Error intern del servidor"),
            @ApiResponse(code = 501, message = "No implementat a COMANDA. Aquest endpoint l'ha d'exposar l'APP.")
    })
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<FitxerInfo>> getFitxers() {
        List<FitxerInfo> logs = salutService.getFitxersLog();
        if (logs == null) {
            return new ResponseEntity<List<FitxerInfo>>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<FitxerInfo>>(logs, HttpStatus.OK);
    }

    @ApiVersion("1")
    @ApiOperation(value = "Obtenir contingut complet d'un fitxer de log",
            notes = "Retorna el contingut i detalls del fitxer de log que es troba dins la carpeta de logs del servidor, i que té el nom indicat",
            response = FitxerContingut.class,
            authorizations = @Authorization(value = "basicAuth"),
            tags = "logs")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Contingut del fitxer obtingut correctament"),
            @ApiResponse(code = 404, message = "Fitxer no trobat"),
            @ApiResponse(code = 500, message = "Error intern del servidor"),
            @ApiResponse(code = 501, message = "No implementat a COMANDA. Aquest endpoint l'ha d'exposar l'APP.")
    })
    @RequestMapping(method = RequestMethod.GET, value = "/{nomFitxer}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<FitxerContingut> getFitxerByNom(@ApiParam(value = "Nom del fitxer", required = true) @PathVariable("nomFitxer") String nomFitxer) {
        FitxerContingut fitxer = null;
        try {
            fitxer = salutService.getFitxerLogByNom(nomFitxer);
        } catch (ComandaApiException e) {
            throw new ResourceNotFoundException(e.getMessage(), e);
        }

        if (fitxer == null) {
            throw new ResourceNotFoundException("Fitxer no trobat");
        }

        return new ResponseEntity<FitxerContingut>(fitxer, HttpStatus.OK);
    }

    @ApiVersion("1")
    @RequestMapping(method = RequestMethod.GET,
            value = "/{nomFitxer}/directe",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    @ResponseBody
    @ApiOperation(value = "Descarregar fitxer de log complet",
            notes = "Descarrega el fitxer de log complet que es troba dins la carpeta de logs del servidor, i que té el nom indicat",
            response = byte[].class,
            authorizations = @Authorization(value = "basicAuth"),
            tags = "logs")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Fitxer descarregat correctament"),
            @ApiResponse(code = 404, message = "Fitxer no trobat"),
            @ApiResponse(code = 500, message = "Error intern del servidor"),
            @ApiResponse(code = 501, message = "No implementat a COMANDA. Aquest endpoint l'ha d'exposar l'APP.")
    })
    public ResponseEntity<byte[]> descarregarFitxerDirecte(@ApiParam(value = "Nom del fitxer", required = true) @PathVariable("nomFitxer") String nomFitxer) {
        LogFileStream file;
        try {
            file = salutService.getFitxerLogStream(nomFitxer);
        } catch (ComandaApiException e) {
            throw new ResourceNotFoundException(e.getMessage(), e);
        }

        if (file == null) {
            throw new ResourceNotFoundException("Fitxer no trobat");
        }

        byte[] contingut;
        InputStream in = null;
        ByteArrayOutputStream out = null;
        try {
            in = file.getInputStream();
            out = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            contingut = out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error llegint el fitxer de log", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignored) {
                }
            }
        }

        MediaType mediaType;
        try {
            mediaType = (file.getContentType() != null && file.getContentType().trim().length() > 0)
                    ? MediaType.parseMediaType(file.getContentType())
                    : MediaType.APPLICATION_OCTET_STREAM;
        } catch (Exception e) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setContentLength(contingut.length);

        headers.set("Content-Disposition", "attachment; filename=\"" + file.getFileName() + "\"");

        return new ResponseEntity<byte[]>(contingut, headers, HttpStatus.OK);
    }

    @ApiVersion("1")
    @ApiOperation(value = "Obtenir les darreres línies d'un fitxer de log",
            notes = "Retorna les darreres linies del fitxer de log indicat per nom. Concretament es retorna el número de línies indicat al paràmetre nLinies.",
            response = String.class,
            responseContainer = "List",
            authorizations = @Authorization(value = "basicAuth"),
            tags = "logs")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Línies del fitxer obtingudes correctament"),
            @ApiResponse(code = 404, message = "Fitxer no trobat"),
            @ApiResponse(code = 500, message = "Error intern del servidor"),
            @ApiResponse(code = 501, message = "No implementat a COMANDA. Aquest endpoint l'ha d'exposar l'APP.")
    })
    @RequestMapping(method = RequestMethod.GET, value = "/{nomFitxer}/linies/{nLinies}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<String>> getFitxerLinies(
            @ApiParam(value = "Nom del fitxer", required = true) @PathVariable("nomFitxer") String nomFitxer,
            @ApiParam(value = "Número de línies a recuperar del fitxer", required = true) @PathVariable("nLinies") Long nLinies) {
        List<String> linies = null;
        try {
            linies = salutService.getFitxerLogLinies(nomFitxer, nLinies);
        } catch (ComandaApiException e) {
            throw new ResourceNotFoundException(e.getMessage(), e);
        }

        if (linies == null) {
            throw new ResourceNotFoundException("Fitxer no trobat");
        }

        return new ResponseEntity<List<String>>(linies, HttpStatus.OK);
    }

}
