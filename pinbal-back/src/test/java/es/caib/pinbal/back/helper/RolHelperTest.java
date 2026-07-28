package es.caib.pinbal.back.helper;

import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RolHelperTest {

    private HttpServletRequest request;
    private HttpSession session;

    private void configurar() {
        request = mock(HttpServletRequest.class);
        session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
    }

    @Test
    public void processarCanviRolsSenseParametreNoFaRes() {
        configurar();

        RolHelper.processarCanviRols(request);

        verify(session, org.mockito.Mockito.never()).setAttribute(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    public void processarCanviRolsAmbRolNoAutoritzatNoElGuarda() {
        configurar();
        when(request.getParameter("canviRol")).thenReturn("PBL_ADMIN");
        when(request.isUserInRole("PBL_ADMIN")).thenReturn(false);

        RolHelper.processarCanviRols(request);

        verify(session, org.mockito.Mockito.never()).setAttribute(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    public void processarCanviRolsAmbRolAutoritzatElGuarda() {
        configurar();
        when(request.getParameter("canviRol")).thenReturn("PBL_ADMIN");
        when(request.isUserInRole("PBL_ADMIN")).thenReturn(true);

        RolHelper.processarCanviRols(request);

        verify(session).setAttribute("RolHelper.rol.actual", "PBL_ADMIN");
    }

    @Test
    public void getRolActualAmbRolDeSessioValidElRetorna() {
        configurar();
        when(session.getAttribute("RolHelper.rol.actual")).thenReturn("PBL_ADMIN");
        when(request.isUserInRole("PBL_ADMIN")).thenReturn(true);

        assertEquals("PBL_ADMIN", RolHelper.getRolActual(request));
    }

    @Test
    public void getRolActualSenseRolCalculaSegonsPrioritatAdmin() {
        configurar();
        when(request.isUserInRole("PBL_ADMIN")).thenReturn(true);

        assertEquals("PBL_ADMIN", RolHelper.getRolActual(request));
        verify(session).setAttribute("RolHelper.rol.actual", "PBL_ADMIN");
    }

    @Test
    public void getRolActualSenseCapRolDisponibleRetornaNull() {
        configurar();

        assertNull(RolHelper.getRolActual(request));
    }

    @Test
    public void isRolActualAdministradorAmbAdmin() {
        configurar();
        when(request.isUserInRole("PBL_ADMIN")).thenReturn(true);

        assertTrue(RolHelper.isRolActualAdministrador(request));
        assertFalse(RolHelper.isRolActualRepresentant(request));
        assertFalse(RolHelper.isRolActualDelegat(request));
        assertFalse(RolHelper.isRolActualAuditor(request));
        assertFalse(RolHelper.isRolActualSuperauditor(request));
    }

    @Test
    public void isRolActualSuperauditorAmbSuperaudit() {
        configurar();
        when(request.isUserInRole("PBL_SUPERAUD")).thenReturn(true);

        assertTrue(RolHelper.isRolActualSuperauditor(request));
    }

    @Test
    public void getRolsUsuariActualNomesElsAutoritzats() {
        configurar();
        when(request.isUserInRole("PBL_ADMIN")).thenReturn(true);
        when(request.isUserInRole("PBL_SUPERAUD")).thenReturn(true);

        assertEquals(java.util.List.of("PBL_ADMIN", "PBL_SUPERAUD"), RolHelper.getRolsUsuariActual(request));
    }

    @Test
    public void esborrarRolActualEsborraLAtributDeSessio() {
        configurar();

        RolHelper.esborrarRolActual(request);

        verify(session).removeAttribute("RolHelper.rol.actual");
    }

    @Test
    public void getRequestParameterCanviRolRetornaElNomDelParametre() {
        assertEquals("canviRol", RolHelper.getRequestParameterCanviRol());
    }
}
