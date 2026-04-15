package es.caib.pinbal.api.interna.controller.salut.v1;

import com.wordnik.swagger.annotations.*;
import es.caib.comanda.ms.salut.helper.MonitorHelper;
import es.caib.comanda.ms.salut.model.AppInfo;
import es.caib.comanda.ms.salut.model.SalutInfo;
import es.caib.pinbal.api.config.ApiVersion;
import es.caib.pinbal.api.interna.controller.PinbalHalRestController;
import es.caib.pinbal.core.service.SalutService;
import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

@Controller
@RequestMapping("/salut/v1")
@Api(value = "/salut/v1", description = "Operacions relacionades amb Salut")
public class SalutRestController extends PinbalHalRestController {

    @Autowired
    private ServletContext servletContext;
    @Autowired
    private SalutService salutService;

    private ManifestInfo manifestInfo;


    @ApiVersion("1")
    @ApiOperation(value = "Obté informació de l'aplicació",
            response = AppInfo.class,
            notes = "Aquest mètode retorna informació detallada de l'aplicació.",
            authorizations = @Authorization(value = "basicAuth"),
            tags = "salut")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Informació obtinguda amb èxit"),
            @ApiResponse(code = 404, message = "Informació no trobada")
    })
    @RequestMapping(value = "/info", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppInfo appInfo(HttpServletRequest request) throws IOException {
        ManifestInfo manifestInfo = getManifestInfo();
        return AppInfo.builder()
                .codi("PBL")
                .nom("Pinbal")
                .data(manifestInfo.getBuildDate())
                .versio(manifestInfo.getVersion())
                .revisio(manifestInfo.getBuildScmRevision())
                .jdkVersion(resolveJdkVersion(manifestInfo))
                .versioJboss(MonitorHelper.getApplicationServerInfo())
                .integracions(salutService.getIntegracions())
                .subsistemes(salutService.getSubsistemes())
                .contexts(salutService.getContexts(getBaseUrl(request)))
                .build();
    }

    @ApiVersion("1")
    @ApiOperation(value = "Obté informació de salut de l'aplicació",
            response = SalutInfo.class,
            notes = "Aquest mètode retorna informació detallada de salut de l'aplicació.",
            tags = "salut")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Informació obtinguda amb èxit"),
            @ApiResponse(code = 404, message = "Informació no trobada")
    })
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public SalutInfo health(HttpServletRequest request,
                            @RequestParam(required = false) String dataPeriode,
                            @RequestParam(required = false) String dataTotal) throws IOException {

        ManifestInfo manifestInfo = getManifestInfo();
        return salutService.checkSalut(manifestInfo.getVersion());
    }

    private ManifestInfo getManifestInfo() throws IOException {

        if (manifestInfo == null) {
            manifestInfo = buildManifestInfo();
        }

        return manifestInfo;
    }

    private ManifestInfo buildManifestInfo() throws IOException {

        ManifestInfo manifestInfo = ManifestInfo.builder().build();
        Manifest manifest = new Manifest(servletContext.getResourceAsStream("/" + JarFile.MANIFEST_NAME));
        Attributes manifestAtributs = manifest.getMainAttributes();
        Map<String, Object> manifestAtributsMap = new HashMap<>();
        for (Object key: new HashMap<>(manifestAtributs).keySet()) {
            manifestAtributsMap.put(key.toString(), manifestAtributs.get(key));
        }
        if (!manifestAtributsMap.isEmpty()) {
            Object version = manifestAtributsMap.get("Implementation-Version");
            Object buildDate = manifestAtributsMap.get("Build-Timestamp");
            Object buildJDK = manifestAtributsMap.get("Build-Jdk-Spec");
            Object buildScmBranch = manifestAtributsMap.get("Implementation-SCM-Branch");
            Object buildScmRevision = manifestAtributsMap.get("Implementation-SCM-Revision");
            manifestInfo = ManifestInfo.builder()
                    .version(version != null ? version.toString() : null)
                    .buildDate(buildDate != null ? getDate(buildDate.toString()) : null)
                    .buildJDK(buildJDK != null ? buildJDK.toString() : null)
                    .buildScmBranch(buildScmBranch != null ? buildScmBranch.toString() : null)
                    .buildScmRevision(buildScmRevision != null ? buildScmRevision.toString() : null)
                    .build();
        }
        return manifestInfo;
    }

    public static Date getDate(String isoDate) {

        try {
            // Parsejem ISO-8601 simple amb javax.xml.bind DatatypeConverter.
            // Admet formats ISO comuns com "2023-07-21T10:15:30Z" o amb offset.
            java.util.Calendar cal = DatatypeConverter.parseDateTime(isoDate);
            return cal.getTime();
        } catch (IllegalArgumentException e) {
            System.out.println("El format de la data és incorrecte: " + e.getMessage());
            return null;
        }
    }

    public String getBaseUrl(HttpServletRequest request) {
        return ServletUriComponentsBuilder
                .fromRequestUri(request)
                .replacePath(null) // elimina el context path "/pinbalapi/..."
                .build()
                .toUriString();
    }

    private String resolveJdkVersion(ManifestInfo manifestInfo) {
        MonitorHelper.SystemInfo systemInfo = MonitorHelper.getSystemInfo();

        if (systemInfo != null) {
            String jdkVersion = systemInfo.getJdkVersion();
            if (jdkVersion != null && !jdkVersion.trim().isEmpty()) {
                return jdkVersion;
            }
        }

        return manifestInfo.getBuildJDK();
    }

    @Builder
    @Getter
    public static class ManifestInfo {

        private final String version;
        private final Date buildDate;
        private final String buildJDK;
        private final String buildScmBranch;
        private final String buildScmRevision;
    }
    
}
