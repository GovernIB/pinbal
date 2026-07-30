package es.caib.pinbal.back.controller;

import es.caib.pinbal.back.command.AvisCommand;
import es.caib.pinbal.logic.intf.dto.AvisDto;
import es.caib.pinbal.logic.intf.service.AvisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AvisControllerTest {

    private static final String SESSION_SELECTED_IDS = "avisSelectedIds";

    private AvisController controller;
    private AvisService avisService;
    private HttpServletRequest request;
    private HttpSession session;

    @BeforeEach
    public void configurar() {
        controller = new AvisController();
        avisService = mock(AvisService.class);
        ControllerTestSupport.setField(controller, "avisService", avisService);
        controller.setMessageSource(ControllerTestSupport.mockMessageSourceEcoDeLaClau());
        request = ControllerTestSupport.mockRequest();
        session = request.getSession();
    }

    // ------------------------- get / datatable -------------------------

    @Test
    public void getMostraLlistat() {
        assertEquals("avisList", controller.get());
    }

    @Test
    public void datatableDelegaAlServei() throws Exception {
        ControllerTestSupport.mockDatatableParams(request, "assumpte", "missatge", "dataInici", "dataFinal", "actiu");
        when(avisService.findPaginat(org.mockito.ArgumentMatchers.any()))
                .thenReturn(new PageImpl<>(List.of(new AvisDto())));

        var resposta = controller.datatable(request);

        assertEquals(1, resposta.getRecordsFiltered());
    }

    // ------------------------- get(avisId) / getNew -------------------------

    @Test
    public void getNewDelegaAGetAmbIdNull() {
        Model model = new ExtendedModelMap();
        assertEquals("avisForm", controller.getNew(model));
        assertTrue(model.containsAttribute("avisCommand"));
    }

    @Test
    public void getAmbIdNullCreaCommandNouAmbDataInici() {
        Model model = new ExtendedModelMap();
        assertEquals("avisForm", controller.get(null, model));
        AvisCommand command = (AvisCommand) model.getAttribute("avisCommand");
        assertTrue(command.getDataInici() != null);
    }

    @Test
    public void getAmbIdInexistentCreaCommandNou() {
        when(avisService.findById(1L)).thenReturn(null);

        Model model = new ExtendedModelMap();
        assertEquals("avisForm", controller.get(1L, model));
        assertTrue(model.containsAttribute("avisCommand"));
    }

    @Test
    public void getAmbIdExistentCarregaLesDades() {
        AvisDto avis = new AvisDto();
        avis.setId(1L);
        avis.setAssumpte("Assumpte");
        when(avisService.findById(1L)).thenReturn(avis);

        Model model = new ExtendedModelMap();
        assertEquals("avisForm", controller.get(1L, model));
        AvisCommand command = (AvisCommand) model.getAttribute("avisCommand");
        assertEquals("Assumpte", command.getAssumpte());
    }

    // ------------------------- save -------------------------

    @Test
    public void saveAmbErrorsTornaAlFormulari() {
        AvisCommand command = new AvisCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");
        bindingResult.reject("error");

        assertEquals("avisForm", controller.save(request, command, bindingResult));
    }

    @Test
    public void saveAmbIdActualitza() {
        AvisCommand command = new AvisCommand();
        command.setId(1L);
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        controller.save(request, command, bindingResult);

        verify(avisService).update(org.mockito.ArgumentMatchers.any());
    }

    @Test
    public void saveSenseIdCrea() {
        AvisCommand command = new AvisCommand();
        BindingResult bindingResult = new BeanPropertyBindingResult(command, "command");

        controller.save(request, command, bindingResult);

        verify(avisService).create(org.mockito.ArgumentMatchers.any());
    }

    // ------------------------- enable / disable / delete -------------------------

    @Test
    public void enableDelegaAlServei() {
        controller.enable(request, 1L);

        verify(avisService).updateActiva(1L, true);
    }

    @Test
    public void disableDelegaAlServei() {
        controller.disable(request, 1L);

        verify(avisService).updateActiva(1L, false);
    }

    @Test
    public void deleteDelegaAlServei() {
        controller.delete(request, 1L);

        verify(avisService).delete(1L);
    }

    // ------------------------- selection: list / add / remove / clear / selectAll -------------------------

    @Test
    public void selectionListSenseSeleccioPreviaRetornaBuit() {
        Set<Long> resposta = controller.selectionList(request);

        assertTrue(resposta.isEmpty());
        verify(session).setAttribute(org.mockito.ArgumentMatchers.eq(SESSION_SELECTED_IDS), org.mockito.ArgumentMatchers.any());
    }

    @Test
    public void selectionListAmbSeleccioPreviaRetornaElsIds() {
        when(session.getAttribute(SESSION_SELECTED_IDS)).thenReturn(new HashSet<>(Set.of(1L, 2L)));

        Set<Long> resposta = controller.selectionList(request);

        assertEquals(Set.of(1L, 2L), resposta);
    }

    @Test
    public void selectionAddAfegeixIdsValids() {
        Set<Long> existent = new HashSet<>();
        when(session.getAttribute(SESSION_SELECTED_IDS)).thenReturn(existent);
        when(request.getParameter("ids")).thenReturn("1, 2,abc,3");

        assertEquals("OK", controller.selectionAdd(request));

        assertEquals(Set.of(1L, 2L, 3L), existent);
    }

    @Test
    public void selectionAddSenseIdsNoModificaElConjunt() {
        Set<Long> existent = new HashSet<>();
        when(session.getAttribute(SESSION_SELECTED_IDS)).thenReturn(existent);
        when(request.getParameter("ids")).thenReturn(null);

        assertEquals("OK", controller.selectionAdd(request));

        assertTrue(existent.isEmpty());
    }

    @Test
    public void selectionRemoveEsborraIdsValids() {
        Set<Long> existent = new HashSet<>(Set.of(1L, 2L, 3L));
        when(session.getAttribute(SESSION_SELECTED_IDS)).thenReturn(existent);
        when(request.getParameter("ids")).thenReturn("1,abc,3");

        assertEquals("OK", controller.selectionRemove(request));

        assertEquals(Set.of(2L), existent);
    }

    @Test
    public void selectionClearBuidaElConjunt() {
        Set<Long> existent = new HashSet<>(Set.of(1L, 2L));
        when(session.getAttribute(SESSION_SELECTED_IDS)).thenReturn(existent);

        assertEquals("OK", controller.selectionClear(request));

        assertTrue(existent.isEmpty());
    }

    @Test
    public void selectionSelectAllAfegeixTotsElsIds() {
        Set<Long> existent = new HashSet<>();
        when(session.getAttribute(SESSION_SELECTED_IDS)).thenReturn(existent);
        when(avisService.findAllIds()).thenReturn(List.of(1L, 2L, 3L));

        assertEquals("OK", controller.selectionSelectAll(request));

        assertEquals(Set.of(1L, 2L, 3L), existent);
    }

    // ------------------------- selected: enable / disable / delete -------------------------

    @Test
    public void enableSelectedActivaCadaAvisSeleccionat() {
        Set<Long> existent = new HashSet<>(Set.of(1L, 2L));
        when(session.getAttribute(SESSION_SELECTED_IDS)).thenReturn(existent);

        Map<String, Object> resposta = controller.enableSelected(request);

        assertEquals("enable", resposta.get("action"));
        assertEquals(2, resposta.get("processed"));
        verify(avisService).updateActiva(1L, true);
        verify(avisService).updateActiva(2L, true);
    }

    @Test
    public void disableSelectedDesactivaCadaAvisSeleccionat() {
        Set<Long> existent = new HashSet<>(Set.of(1L, 2L));
        when(session.getAttribute(SESSION_SELECTED_IDS)).thenReturn(existent);

        Map<String, Object> resposta = controller.disableSelected(request);

        assertEquals("disable", resposta.get("action"));
        assertEquals(2, resposta.get("processed"));
        verify(avisService).updateActiva(1L, false);
        verify(avisService).updateActiva(2L, false);
    }

    @Test
    public void deleteSelectedEsborraCadaAvisIBuidaLaSeleccio() {
        Set<Long> existent = new HashSet<>(Set.of(1L, 2L));
        when(session.getAttribute(SESSION_SELECTED_IDS)).thenReturn(existent);

        Map<String, Object> resposta = controller.deleteSelected(request);

        assertEquals("delete", resposta.get("action"));
        assertEquals(2, resposta.get("processed"));
        verify(avisService).delete(1L);
        verify(avisService).delete(2L);
        assertTrue(existent.isEmpty());
    }
}
