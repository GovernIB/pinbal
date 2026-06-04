package es.caib.pinbal.back.interceptor;

import es.caib.pinbal.logic.intf.dto.EntitatDto;
import es.caib.pinbal.logic.intf.dto.ServeiDto;
import es.caib.pinbal.logic.intf.service.AvisService;
import es.caib.pinbal.logic.intf.service.ConsultaService;
import es.caib.pinbal.logic.intf.service.EntitatService;
import es.caib.pinbal.logic.intf.service.ServeiService;
import es.caib.pinbal.logic.intf.service.UsuariService;
import es.caib.pinbal.logic.intf.service.exception.EntitatNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.back.helper.EntitatHelper;
import es.caib.pinbal.back.helper.PeticionsMultiplesPendentsHelper;
import es.caib.pinbal.back.helper.RolHelper;
import es.caib.pinbal.back.helper.ServeiHelper;
import es.caib.pinbal.back.helper.UsuariHelper;
import es.caib.pinbal.back.helper.AjaxHelper;
import es.caib.pinbal.back.helper.AvisHelper;
import es.caib.pinbal.back.helper.MissatgesHelper;
import es.caib.pinbal.back.helper.ModalHelper;
import es.caib.pinbal.back.helper.NodecoHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class PinbalInterceptor implements AsyncHandlerInterceptor {

    public static final String REQUEST_ATTRIBUTE_MANIFEST_ATRIBUTES = "manifestAtributes";
    public static final String REQUEST_ATTRIBUTE_LOCALE = "requestLocale";

    private final UsuariService usuariService;
    private final ConsultaService consultaService;
    private final EntitatService entitatService;
    private final ServeiService serveiService;
    private final AvisService avisService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        request.setAttribute(REQUEST_ATTRIBUTE_MANIFEST_ATRIBUTES, MissatgesHelper.getManifestAtributsMap());
        request.setAttribute(REQUEST_ATTRIBUTE_LOCALE, RequestContextUtils.getLocale(request).getLanguage());

        HttpServletRequest wrappedRequest = new SecurityContextHolderAwareRequestWrapper(request, "");
        // Tipus accés: Es comprova si s'està fent una petició noDeco, modal o ajax.
        // En aquest cas es canvia la url, es redirigeix a la nova url, i per tant NO ha de continuar executat l'interceptor
        var continuarExecucio = ModalHelper.comprovarModalInterceptor(wrappedRequest, response) &&
                NodecoHelper.comprovarNodecoInterceptor(wrappedRequest, response) &&
                AjaxHelper.comprovarAjaxInterceptor(wrappedRequest, response);

        if (!continuarExecucio)
            return false;

        // Comprova si s'està canviant el rol.
        RolHelper.processarCanviRols(request);
        // Si el rol actual és delegat, s'obté el número de peticions múltiples pendents
        if (RolHelper.isRolActualDelegat(request)) {
            PeticionsMultiplesPendentsHelper.countPendents(request, consultaService);
        }

        if (!wrappedRequest.getServletPath().startsWith("/api")) {
            // Es comprova que l'usuari s'hagi desat a la BBDD, i l'assigna com a usuari actual
            UsuariHelper.inicialitzarUsuariActual(request, usuariService);
            UsuariHelper.getDadesUsuariActual(request, usuariService);
            // Recuperam l'idioma de l'usuari actual
            UsuariHelper.processarLocale(request, response, usuariService);

            // Obtenim les entitats accessibles per l'usuari actual
            EntitatHelper.getEntitats(request, entitatService, true);
            // Comprova si s'està canviant de entitat.
            EntitatHelper.processarCanviEntitats(request, entitatService);

            obtenirServeis(request);
        }

        if (!wrappedRequest.getRequestURI().startsWith(wrappedRequest.getContextPath() + "/api/recobriment")) {
            AvisHelper.findAvisos(
                    request,
                    avisService);
        }

        return true;
    }


    // Mètodes a executar per l'interceptor
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Recuperar els serveis accessibles per l'usuari actual'
    private void obtenirServeis(HttpServletRequest request) throws EntitatNotFoundException, ProcedimentNotFoundException {
        EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
        List<ServeiDto> serveis = null;
        if (request.getUserPrincipal() != null && RolHelper.isRolActualDelegat(request)) { // Si hi ha algún usuari autenticat
            String usuari = request.getUserPrincipal().getName();
            log.debug("Consulta del llistat de serveis pel delegat (usuari=" + usuari + ")");
            serveis = serveiService.findPermesosAmbProcedimentPerDelegat(entitatActual.getId(), null);
        }
        // Ordenar els serveis per descripció
        if (serveis != null) {
            Collections.sort(serveis, new Comparator<ServeiDto>() {
                @Override
                public int compare(ServeiDto o1, ServeiDto o2) {
                    String d1 = o1 != null && o1.getDescripcio() != null ? o1.getDescripcio() : "";
                    String d2 = o2 != null && o2.getDescripcio() != null ? o2.getDescripcio() : "";
                    return d1.compareToIgnoreCase(d2);
                }
            });
        }
        request.setAttribute(
                ServeiHelper.REQUEST_ATTRIBUTE_SERVEIS,
                serveis);
    }

}
