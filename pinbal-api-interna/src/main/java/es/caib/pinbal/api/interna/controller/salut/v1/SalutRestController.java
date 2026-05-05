package es.caib.pinbal.api.interna.controller.salut.v1;

import es.caib.comanda.model.server.monitoring.AppInfo;
import es.caib.comanda.model.server.monitoring.SalutInfo;
import es.caib.comanda.ms.salut.helper.MonitorHelper;
import es.caib.pinbal.api.interna.api.SalutApi;
import es.caib.pinbal.api.interna.controller.PinbalHalRestController;
import es.caib.pinbal.logic.intf.service.SalutService;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/salut/v1")
public class SalutRestController extends PinbalHalRestController implements SalutApi {

    private final ServletContext servletContext;
    private final SalutService salutService;

    private ManifestInfo manifestInfo;

    /**
     * Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI
     * definida a la interfície SalutApi.
     */
    @Override
    @GetMapping(value = "/info", produces = MediaType.APPLICATION_JSON_VALUE)
    public AppInfo appInfo(HttpServletRequest request) throws IOException {
        ManifestInfo manifestInfo = getManifestInfo();
        return new AppInfo()
                .codi("PBL")
                .nom("Pinbal")
                .data(toOffsetDateTime(manifestInfo.getBuildDate()))
                .versio(manifestInfo.getVersion())
                .revisio(manifestInfo.getBuildScmRevision())
                .jdkVersion(resolveJdkVersion(manifestInfo))
                .versioJboss(MonitorHelper.getApplicationServerInfo())
                .integracions(salutService.getIntegracions())
                .subsistemes(salutService.getSubsistemes())
                .contexts(salutService.getContexts(getBaseUrl(request)));
    }

    /**
     * Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI
     * definida a la interfície SalutApi.
     */
    @Override
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
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

    private OffsetDateTime toOffsetDateTime(Date date) {
        return date != null ? OffsetDateTime.ofInstant(date.toInstant(), java.time.ZoneOffset.UTC) : null;
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
