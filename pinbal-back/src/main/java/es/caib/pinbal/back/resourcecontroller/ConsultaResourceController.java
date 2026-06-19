package es.caib.pinbal.back.resourcecontroller;

import es.caib.pinbal.back.base.controller.BaseReadonlyResourceController;
import es.caib.pinbal.back.helper.RolHelper;
import es.caib.pinbal.logic.intf.base.config.BaseConfig;
import es.caib.pinbal.logic.intf.dto.JustificantDto;
import es.caib.pinbal.logic.intf.model.ConsultaResource;
import es.caib.pinbal.logic.intf.service.ConsultaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Servei REST de consulta de consultes SCSP recents.
 * <p>
 * Una mateixa pantalla serveix per a administrador i delegat: l'àmbit de dades
 * es resol al servei segons el rol de l'usuari autenticat.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@RestController
@RequestMapping(BaseConfig.API_PATH + "/consultes")
public class ConsultaResourceController extends BaseReadonlyResourceController<ConsultaResource, Long> {

    @Autowired
    private ConsultaService consultaService;

    /**
     * Descàrrega del justificant d'una consulta. Genera/recupera el justificant
     * mitjançant la lògica de domini existent, amb el rol adequat de l'usuari.
     */
    @GetMapping("/{id}/justificant")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> justificant(
            @PathVariable Long id,
            HttpServletRequest request) {
        boolean isAdmin = RolHelper.isRolActualAdministrador(request);
        try {
            JustificantDto justificant = consultaService.obtenirJustificant(id, isAdmin);
            if (justificant != null && !justificant.isError() && justificant.getContingut() != null) {
                String filename = justificant.getNom() != null ? justificant.getNom() : ("justificant_" + id + ".pdf");
                MediaType contentType = justificant.getContentType() != null
                        ? MediaType.parseMediaType(justificant.getContentType())
                        : MediaType.APPLICATION_PDF;
                return ResponseEntity.ok()
                        .contentType(contentType)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                        .body(justificant.getContingut());
            }
        } catch (Exception ex) {
            log.error("Error obtenint el justificant de la consulta (id=" + id + ")", ex);
        }
        return ResponseEntity.noContent().build();
    }

}
