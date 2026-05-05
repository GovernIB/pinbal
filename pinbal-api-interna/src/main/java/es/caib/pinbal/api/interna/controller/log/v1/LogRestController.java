package es.caib.pinbal.api.interna.controller.log.v1;

import es.caib.comanda.model.server.monitoring.FitxerContingut;
import es.caib.comanda.model.server.monitoring.FitxerInfo;
import es.caib.comanda.ms.exception.ComandaApiException;
import es.caib.comanda.ms.log.helper.LogFileStream;
import es.caib.pinbal.api.interna.api.LogApi;
import es.caib.pinbal.api.interna.controller.PinbalHalRestController;
import es.caib.pinbal.logic.intf.service.SalutService;
import es.caib.pinbal.logic.intf.service.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/logs/v1")
public class LogRestController extends PinbalHalRestController implements LogApi {

    private final SalutService salutService;

    /**
     * Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI
     * definida a la interfície LogApi.
     */
    @Override
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<FitxerInfo>> getFitxers() {
        List<FitxerInfo> logs = salutService.getFitxersLog();
        if (logs == null) {
            return new ResponseEntity<List<FitxerInfo>>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<FitxerInfo>>(logs, HttpStatus.OK);
    }

    /**
     * Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI
     * definida a la interfície LogApi.
     */
    @Override
    @GetMapping(value = "/{nomFitxer:.+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FitxerContingut> getFitxerByNom(@PathVariable("nomFitxer") String nomFitxer) {
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

    /**
     * Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI
     * definida a la interfície LogApi.
     */
    @Override
    @GetMapping(
            value = "/{nomFitxer:.+}/directe",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public ResponseEntity<byte[]> descarregarFitxerDirecte(@PathVariable("nomFitxer") String nomFitxer) {
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

    /**
     * Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI
     * definida a la interfície LogApi.
     */
    @Override
    @GetMapping(value = "/{nomFitxer:.+}/linies/{nLinies}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getFitxerLinies(
            @PathVariable("nomFitxer") String nomFitxer,
            @PathVariable("nLinies") Long nLinies) {
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
