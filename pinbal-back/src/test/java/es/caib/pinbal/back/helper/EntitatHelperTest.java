package es.caib.pinbal.back.helper;

import es.caib.pinbal.logic.intf.dto.EntitatDto;
import es.caib.pinbal.logic.intf.dto.EntitatUsuariDto;
import es.caib.pinbal.logic.intf.dto.UsuariDto;
import es.caib.pinbal.logic.intf.service.EntitatService;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EntitatHelperTest {

    private HttpServletRequest request;
    private HttpSession session;
    private EntitatService entitatService;
    private Principal principal;

    private void configurar() {
        request = mock(HttpServletRequest.class);
        session = mock(HttpSession.class);
        entitatService = mock(EntitatService.class);
        principal = mock(Principal.class);
        when(request.getSession()).thenReturn(session);
        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("usuari1");
    }

    private EntitatUsuariDto usuariAmbRols(String codi, boolean representant, boolean delegat, boolean auditor) {
        UsuariDto usuari = new UsuariDto();
        usuari.setCodi(codi);
        return new EntitatUsuariDto(usuari, "Dept", false, representant, delegat, auditor, false, true);
    }

    // ------------------------- getEntitats -------------------------

    @Test
    public void getEntitatsSenseRefrescarNoConsultaElServei() {
        configurar();

        EntitatHelper.getEntitats(request, entitatService, false);

        verify(entitatService, never()).findActivesAmbUsuariCodi(any());
    }

    @Test
    public void getEntitatsAmbRefrescarIUsuariConsultaElServei() {
        configurar();
        EntitatDto entitat = new EntitatDto();
        entitat.setId(1L);
        entitat.setUsuaris(List.of());
        List<EntitatDto> entitats = List.of(entitat);
        when(entitatService.findActivesAmbUsuariCodi("usuari1")).thenReturn(entitats);
        when(entitatService.getEntitatIdPerDefecte("usuari1")).thenReturn(1L);

        List<EntitatDto> resultat = EntitatHelper.getEntitats(request, entitatService, true);

        assertSame(entitats, resultat);
        verify(session).setAttribute("EntitatHelper.entitats", entitats);
        verify(session).setAttribute("EntitatHelper.entitat.actual.index", 0);
    }

    @Test
    public void getEntitatsSenseUserPrincipalNoConsultaElServei() {
        request = mock(HttpServletRequest.class);
        when(request.getSession()).thenReturn(mock(HttpSession.class));

        EntitatHelper.getEntitats(request, mock(EntitatService.class), true);

        verify(request, never()).getRequestURI();
    }

    // ------------------------- processarCanviEntitats -------------------------

    @Test
    public void processarCanviEntitatsSenseParametreNoFaRes() {
        configurar();

        EntitatHelper.processarCanviEntitats(request, entitatService);

        verify(session, never()).setAttribute(org.mockito.ArgumentMatchers.eq("EntitatHelper.entitat.actual.index"), any());
    }

    @Test
    public void processarCanviEntitatsAmbIdInvalidNoFaRes() {
        configurar();
        when(request.getParameter("canviEntitat")).thenReturn("no-es-un-long");

        EntitatHelper.processarCanviEntitats(request, entitatService);

        verify(session, never()).setAttribute(org.mockito.ArgumentMatchers.eq("EntitatHelper.entitat.actual.index"), any());
    }

    @Test
    public void processarCanviEntitatsAmbIdTrobatCanviaEntitatActual() {
        configurar();
        when(request.getParameter("canviEntitat")).thenReturn("2");
        EntitatDto entitat1 = new EntitatDto();
        entitat1.setId(1L);
        EntitatDto entitat2 = new EntitatDto();
        entitat2.setId(2L);
        entitat2.setUsuaris(List.of());
        when(session.getAttribute("EntitatHelper.entitats")).thenReturn(List.of(entitat1, entitat2));

        EntitatHelper.processarCanviEntitats(request, entitatService);

        verify(session).setAttribute("EntitatHelper.entitat.actual.index", 1);
    }

    // ------------------------- getEntitatActual -------------------------

    @Test
    public void getEntitatActualSenseEntitatsRetornaNull() {
        configurar();

        assertNull(EntitatHelper.getEntitatActual(request));
    }

    @Test
    public void getEntitatActualSenseServeiRetornaDeLaSessio() {
        configurar();
        EntitatDto entitat = new EntitatDto();
        entitat.setId(5L);
        when(session.getAttribute("EntitatHelper.entitats")).thenReturn(List.of(entitat));
        when(session.getAttribute("EntitatHelper.entitat.actual.index")).thenReturn(0);

        assertSame(entitat, EntitatHelper.getEntitatActual(request, null));
    }

    @Test
    public void getEntitatActualAmbServeiElConsulta() {
        configurar();
        EntitatDto entitat = new EntitatDto();
        entitat.setId(5L);
        EntitatDto entitatActualitzada = new EntitatDto();
        entitatActualitzada.setId(5L);
        when(session.getAttribute("EntitatHelper.entitats")).thenReturn(List.of(entitat));
        when(session.getAttribute("EntitatHelper.entitat.actual.index")).thenReturn(0);
        when(entitatService.findById(5L)).thenReturn(entitatActualitzada);

        assertSame(entitatActualitzada, EntitatHelper.getEntitatActual(request, entitatService));
    }

    @Test
    public void getEntitatActualIndexRetornaElDeLaSessio() {
        configurar();
        when(session.getAttribute("EntitatHelper.entitat.actual.index")).thenReturn(3);

        assertEquals(3, EntitatHelper.getEntitatActualIndex(request));
    }

    @Test
    public void getRequestParameterCanviEntitatRetornaElNomDelParametre() {
        assertEquals("canviEntitat", EntitatHelper.getRequestParameterCanviEntitat());
    }

    // ------------------------- isXxxEntitatActual -------------------------

    @Test
    public void isRepresentantEntitatActualAmbAtributTrue() {
        configurar();
        when(session.getAttribute("EntitatHelper.entitat.actual.representant")).thenReturn(Boolean.TRUE);

        assertTrue(EntitatHelper.isRepresentantEntitatActual(request));
    }

    @Test
    public void isDelegatEntitatActualSenseAtributEsFalse() {
        configurar();

        assertFalse(EntitatHelper.isDelegatEntitatActual(request));
    }

    @Test
    public void isAuditorEntitatActualAmbAtributTrue() {
        configurar();
        when(session.getAttribute("EntitatHelper.entitat.actual.auditor")).thenReturn(Boolean.TRUE);

        assertTrue(EntitatHelper.isAuditorEntitatActual(request));
    }

    // ------------------------- canviEntitatActual (via getEntitats) comprova rols de l'usuari -------------------------

    @Test
    public void getEntitatsCalculaRolsDeLUsuariEnCanviarEntitat() {
        configurar();
        EntitatUsuariDto usuariRepresentant = usuariAmbRols("usuari1", true, false, false);
        EntitatDto entitat = new EntitatDto();
        entitat.setId(1L);
        entitat.setUsuaris(List.of(usuariRepresentant));
        List<EntitatDto> entitats = List.of(entitat);
        when(entitatService.findActivesAmbUsuariCodi("usuari1")).thenReturn(entitats);
        when(entitatService.getEntitatIdPerDefecte("usuari1")).thenReturn(null);
        when(entitatService.findById(1L)).thenReturn(entitat);
        // La primera lectura (comprovació inicial) ha de ser null perquè entri al bloc de refresc;
        // les lectures posteriors (dins canviEntitatActual -> getEntitatActual) han de retornar la llista ja carregada.
        when(session.getAttribute("EntitatHelper.entitats")).thenReturn(null).thenReturn(entitats);
        when(session.getAttribute("EntitatHelper.entitat.actual.index")).thenReturn(0);

        EntitatHelper.getEntitats(request, entitatService, true);

        verify(session).setAttribute("EntitatHelper.entitat.actual.representant", true);
        verify(session).setAttribute("EntitatHelper.entitat.actual.delegat", false);
        verify(session).setAttribute("EntitatHelper.entitat.actual.auditor", false);
    }
}
