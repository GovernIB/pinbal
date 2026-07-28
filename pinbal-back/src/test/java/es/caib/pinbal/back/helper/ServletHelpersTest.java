package es.caib.pinbal.back.helper;

import es.caib.pinbal.logic.intf.dto.AvisDto;
import es.caib.pinbal.logic.intf.dto.EntitatDto;
import es.caib.pinbal.logic.intf.dto.ServeiDto;
import es.caib.pinbal.logic.intf.dto.UsuariDto;
import es.caib.pinbal.logic.intf.service.AvisService;
import es.caib.pinbal.logic.intf.service.ConsultaService;
import es.caib.pinbal.logic.intf.service.UsuariService;
import org.junit.jupiter.api.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests dels helpers simples que operen sobre {@link HttpServletRequest}/{@link HttpSession}
 * (marcatge d'ajax/modal/nodeco, alertes, dades d'usuari i avisos de sessió).
 */
public class ServletHelpersTest {

    // ------------------------- RequestHelper -------------------------

    @Test
    public void isErrorAmbAtributEsTrue() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute("javax.servlet.error.request_uri")).thenReturn("/error");

        assertTrue(RequestHelper.isError(request));
    }

    @Test
    public void isErrorSenseAtributEsFalse() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        assertFalse(RequestHelper.isError(request));
    }

    // ------------------------- ServeiHelper -------------------------

    @Test
    public void getServeisAmbAtributElRetorna() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        List<ServeiDto> serveis = List.of(new ServeiDto());
        when(request.getAttribute(ServeiHelper.REQUEST_ATTRIBUTE_SERVEIS)).thenReturn(serveis);

        assertSame(serveis, ServeiHelper.getServeis(request));
    }

    @Test
    public void getServeisSenseAtributRetornaNull() {
        assertNull(ServeiHelper.getServeis(mock(HttpServletRequest.class)));
    }

    // ------------------------- RequestSessionHelper -------------------------

    @Test
    public void objecteSessioRoundTrip() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("clau")).thenReturn("valor");

        assertEquals("valor", RequestSessionHelper.obtenirObjecteSessio(request, "clau"));
        assertTrue(RequestSessionHelper.existeixObjecteSessio(request, "clau"));

        RequestSessionHelper.actualitzarObjecteSessio(request, "clau", "nouValor");
        verify(session).setAttribute("clau", "nouValor");

        RequestSessionHelper.esborrarObjecteSessio(request, "clau");
        verify(session).removeAttribute("clau");
    }

    // ------------------------- AvisHelper -------------------------

    @Test
    public void findAvisosAmbAvisosJaCarregatsNoConsultaElServei() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        AvisService avisService = mock(AvisService.class);
        List<AvisDto> avisos = List.of(new AvisDto());
        when(request.getAttribute("AvisHelper.findAvisos")).thenReturn(avisos);

        AvisHelper.findAvisos(request, avisService);

        verify(avisService, never()).findActive();
    }

    @Test
    public void findAvisosSenseAvisosCarregatsElsConsulta() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        AvisService avisService = mock(AvisService.class);
        List<AvisDto> avisos = List.of(new AvisDto());
        when(avisService.findActive()).thenReturn(avisos);

        AvisHelper.findAvisos(request, avisService);

        verify(request).setAttribute("AvisHelper.findAvisos", avisos);
    }

    @Test
    public void findAvisosAmbErrorNoConsultaElServei() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        AvisService avisService = mock(AvisService.class);
        when(request.getAttribute("javax.servlet.error.request_uri")).thenReturn("/error");

        AvisHelper.findAvisos(request, avisService);

        verify(avisService, never()).findActive();
    }

    @Test
    public void getAvisosRetornaElsGuardats() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        List<AvisDto> avisos = List.of(new AvisDto());
        when(request.getAttribute("AvisHelper.findAvisos")).thenReturn(avisos);

        assertSame(avisos, AvisHelper.getAvisos(request));
    }

    // ------------------------- PeticionsMultiplesPendentsHelper -------------------------

    @Test
    public void countPendentsAmbValorJaCalculatElRetorna() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute("PeticionsMultiplesPendentsHelper.pendents")).thenReturn(5);

        assertEquals(5, PeticionsMultiplesPendentsHelper.countPendents(request, mock(ConsultaService.class)));
    }

    @Test
    public void countPendentsSenseEntitatActualRetornaNull() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

        assertNull(PeticionsMultiplesPendentsHelper.countPendents(request, mock(ConsultaService.class)));
    }

    @Test
    public void countPendentsAmbServeiNullRetornaNull() throws Exception {
        assertNull(PeticionsMultiplesPendentsHelper.countPendents(mock(HttpServletRequest.class)));
    }

    // ------------------------- AlertHelper -------------------------

    @Test
    public void alertHelperGuardaIRecuperaErrors() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

        AlertHelper.error(request, "error1");

        verify(session).setAttribute(eq(AlertHelper.SESSION_ATTRIBUTE_ERROR), any());
    }

    @Test
    public void alertHelperGetErrorsAmbDeleteEsborraLAtribut() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        List<String> errors = List.of("error1");
        when(session.getAttribute(AlertHelper.SESSION_ATTRIBUTE_ERROR)).thenReturn(errors);

        AlertHelper helper = new AlertHelper();
        assertSame(errors, helper.getErrors(request, true));
        verify(session).removeAttribute(AlertHelper.SESSION_ATTRIBUTE_ERROR);
    }

    @Test
    public void alertHelperWarningSuccessInfo() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

        AlertHelper.warning(request, "w");
        AlertHelper.success(request, "s");
        AlertHelper.info(request, "i");

        AlertHelper helper = new AlertHelper();
        helper.getWarnings(request, false);
        helper.getSuccesses(request, false);
        helper.getInfos(request, false);

        verify(session, times(3)).setAttribute(anyString(), any());
    }

    // ------------------------- MissatgesHelper -------------------------

    @Test
    public void missatgesHelperGuardaIRecuperaErrors() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        List<String> errors = new java.util.ArrayList<>(List.of("error1"));
        when(session.getAttribute(MissatgesHelper.SESSION_ATTRIBUTE_ERROR)).thenReturn(errors);

        MissatgesHelper.error(request, "error1");
        MissatgesHelper helper = new MissatgesHelper();
        assertSame(errors, helper.getErrors(request, false));
    }

    @Test
    public void missatgesHelperWarningSuccessInfo() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

        MissatgesHelper.warning(request, "w");
        MissatgesHelper.success(request, "s");
        MissatgesHelper.info(request, "i");

        MissatgesHelper helper = new MissatgesHelper();
        helper.getWarnings(request, true);
        helper.getSuccesses(request, true);
        helper.getInfos(request, true);

        verify(session, times(3)).removeAttribute(anyString());
    }

    @Test
    public void missatgesHelperManifestAtributsMapGetterSetter() {
        java.util.Map<String, Object> mapa = Collections.singletonMap("clau", "valor");

        MissatgesHelper.setManifestAtributsMap(mapa);

        assertSame(mapa, MissatgesHelper.getManifestAtributsMap());
    }

    // ------------------------- NodecoHelper -------------------------

    @Test
    public void isNodecoAmbAtributEsTrue() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute("NodecoHelper.Nodeco")).thenReturn(Boolean.TRUE);

        assertTrue(NodecoHelper.isNodeco(request));
    }

    @Test
    public void comprovarNodecoInterceptorAmbPathNodecoForward() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        when(request.getServletPath()).thenReturn("/nodeco/pagina");
        when(request.getSession()).thenReturn(session);
        when(request.getRequestDispatcher("/pagina")).thenReturn(dispatcher);

        boolean continuar = NodecoHelper.comprovarNodecoInterceptor(request, response);

        assertFalse(continuar);
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void comprovarNodecoInterceptorSensePathNodecoContinua() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getServletPath()).thenReturn("/pagina");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("NodecoHelper.RequestPathsMap")).thenReturn(new HashSet<String>(List.of("/pagina")));

        boolean continuar = NodecoHelper.comprovarNodecoInterceptor(request, response);

        assertTrue(continuar);
        verify(request).setAttribute(eq("NodecoHelper.Nodeco"), any());
    }

    // ------------------------- ModalHelper -------------------------

    @Test
    public void isModalAmbAtributEsTrue() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute("ModalHelper.Modal")).thenReturn(Boolean.TRUE);

        assertTrue(ModalHelper.isModal(request));
    }

    @Test
    public void isRequestPathModalAmbAccioTancarEsFalse() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getServletPath()).thenReturn(ModalHelper.ACCIO_MODAL_TANCAR);

        assertFalse(ModalHelper.isRequestPathModal(request));
    }

    @Test
    public void comprovarModalInterceptorAmbPathModalForward() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        when(request.getServletPath()).thenReturn("/modal/pagina");
        when(request.getSession()).thenReturn(session);
        when(request.getRequestDispatcher("/pagina")).thenReturn(dispatcher);

        boolean continuar = ModalHelper.comprovarModalInterceptor(request, response);

        assertFalse(continuar);
        verify(dispatcher).forward(request, response);
    }

    // ------------------------- AjaxHelper -------------------------

    @Test
    public void isAjaxAmbAtributEsTrue() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute("AjaxHelper.Ajax")).thenReturn(Boolean.TRUE);

        assertTrue(AjaxHelper.isAjax(request));
    }

    @Test
    public void comprovarAjaxInterceptorAmbPathAjaxForward() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        when(request.getServletPath()).thenReturn("/ajax/pagina");
        when(request.getSession()).thenReturn(session);
        when(request.getRequestDispatcher("/pagina")).thenReturn(dispatcher);

        boolean continuar = AjaxHelper.comprovarAjaxInterceptor(request, response);

        assertFalse(continuar);
    }

    @Test
    public void comprovarAjaxInterceptorAmbAccioOkNoForward() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getServletPath()).thenReturn(AjaxHelper.ACCIO_AJAX_OK);
        when(request.getSession()).thenReturn(session);

        assertTrue(AjaxHelper.comprovarAjaxInterceptor(request, response));
    }

    @Test
    public void generarAjaxFormOkSenseObjecte() {
        AjaxHelper.AjaxFormResponse resposta = AjaxHelper.generarAjaxFormOk();

        assertTrue(resposta.isEstatOk());
        assertFalse(resposta.isEstatError());
        assertFalse(resposta.isErrorsGlobals());
        assertFalse(resposta.isErrorsCamps());
    }

    @Test
    public void generarAjaxFormOkAmbObjecte() {
        Object objecte = new Object();

        AjaxHelper.AjaxFormResponse resposta = AjaxHelper.generarAjaxFormOk(objecte);

        assertSame(objecte, resposta.getObjecte());
        assertTrue(resposta.isEstatOk());
    }

    @Test
    public void generarAjaxFormErrorsAmbErrorsGlobalsICamps() {
        org.springframework.validation.BindingResult bindingResult = mock(org.springframework.validation.BindingResult.class);
        org.springframework.validation.ObjectError objectError = new org.springframework.validation.ObjectError(
                "obj", new String[]{"codi.error"}, null, "missatge per defecte");
        org.springframework.validation.FieldError fieldError = new org.springframework.validation.FieldError(
                "obj", "camp", null, false, new String[]{"codi.camp.error"}, null, "missatge per defecte");
        when(bindingResult.getGlobalErrors()).thenReturn(List.of(objectError));
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        org.springframework.context.MessageSource messageSource = mock(org.springframework.context.MessageSource.class);
        when(messageSource.getMessage(any(), any(), any())).thenReturn("missatge");
        MessageHelper.INSTANCE.setMessageSource(messageSource);

        AjaxHelper.AjaxFormResponse resposta = AjaxHelper.generarAjaxFormErrors(new Object(), bindingResult);

        assertTrue(resposta.isEstatError());
        assertTrue(resposta.isErrorsGlobals());
        assertTrue(resposta.isErrorsCamps());
        assertEquals("obj", resposta.getErrorsGlobals().get(0).getCamp());
        assertEquals("camp", resposta.getErrorsCamps().get(0).getCamp());
    }

    // ------------------------- UsuariHelper -------------------------

    @Test
    public void getDadesUsuariActualSenseUserPrincipalRetornaNull() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);

        assertNull(UsuariHelper.getDadesUsuariActual(request));
    }

    @Test
    public void getDadesUsuariActualAmbDadesGuardadesLesRetorna() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        Principal principal = mock(Principal.class);
        when(request.getUserPrincipal()).thenReturn(principal);
        when(request.getSession()).thenReturn(session);
        UsuariDto dades = new UsuariDto();
        when(session.getAttribute("UsuariHelper.dades.usuari.actual")).thenReturn(dades);

        assertSame(dades, UsuariHelper.getDadesUsuariActual(request));
    }

    @Test
    public void getDadesUsuariActualConsultaElServeiSiNoHiHaDades() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        Principal principal = mock(Principal.class);
        when(request.getUserPrincipal()).thenReturn(principal);
        when(request.getSession()).thenReturn(session);
        UsuariService usuariService = mock(UsuariService.class);
        UsuariDto dades = new UsuariDto();
        when(usuariService.getDades()).thenReturn(dades);

        assertSame(dades, UsuariHelper.getDadesUsuariActual(request, usuariService));
        verify(session).setAttribute("UsuariHelper.dades.usuari.actual", dades);
    }

    @Test
    public void inicialitzarUsuariActualPrimeraVegadaExecutaInicialitzacio() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        Principal principal = mock(Principal.class);
        when(request.getUserPrincipal()).thenReturn(principal);
        when(request.getSession()).thenReturn(session);
        UsuariService usuariService = mock(UsuariService.class);

        UsuariHelper.inicialitzarUsuariActual(request, usuariService);

        verify(usuariService).inicialitzarUsuariActual();
        verify(session).setAttribute(eq("UsuariHelper.usuari.creacio.exec"), any());
    }

    @Test
    public void inicialitzarUsuariActualJaExecutatNoTornaAInicialitzar() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("UsuariHelper.usuari.creacio.exec")).thenReturn(Boolean.TRUE);
        UsuariService usuariService = mock(UsuariService.class);

        UsuariHelper.inicialitzarUsuariActual(request, usuariService);

        verify(usuariService, never()).inicialitzarUsuariActual();
    }

    @Test
    public void resetUsuariActualEsborraLAtributDeSessio() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

        UsuariHelper.resetUsuariActual(request);

        verify(session).removeAttribute("UsuariHelper.dades.usuari.actual");
    }
}
