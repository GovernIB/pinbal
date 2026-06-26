package es.caib.pinbal.logic.helper;

import es.caib.pinbal.logic.intf.dto.IntegracioAccioDto;
import es.caib.pinbal.logic.intf.dto.IntegracioAccioEstatEnumDto;
import es.caib.pinbal.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.pinbal.logic.intf.dto.IntegracioDto;
import es.caib.pinbal.logic.intf.service.IntegracioAccioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IntegracioHelperTest {

    @Mock
    private UsuariHelper usuariHelper;

    @Mock
    private IntegracioAccioService integracioAccioService;

    @InjectMocks
    private IntegracioHelper integracioHelper;

    @Test
    public void findAll_retornaSetIntegracions() {
        List<IntegracioDto> integracions = integracioHelper.findAll();
        assertEquals(7, integracions.size());
    }

    @Test
    public void findAll_totesAmNomDefinit() {
        List<IntegracioDto> integracions = integracioHelper.findAll();
        integracions.forEach(i -> assertNotNull(i.getNom()));
    }

    @Test
    public void findAll_conteCodiArxiu() {
        List<IntegracioDto> integracions = integracioHelper.findAll();
        assertTrue(integracions.stream().anyMatch(i -> IntegracioHelper.INTCODI_ARXIU.equals(i.getCodi())));
    }

    @Test
    public void findAll_conteCodiDadesComuns() {
        List<IntegracioDto> integracions = integracioHelper.findAll();
        assertTrue(integracions.stream().anyMatch(i -> IntegracioHelper.INTCODI_DADES_COMUNS.equals(i.getCodi())));
    }

    @Test
    public void addAccioOk_cridaAlServei() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("codi", "EXP001");

        integracioHelper.addAccioOk(
                "PET001",
                IntegracioHelper.INTCODI_ARXIU,
                "Descripció",
                params,
                IntegracioAccioTipusEnumDto.ENVIAMENT,
                100L);

        ArgumentCaptor<IntegracioAccioDto> captor = ArgumentCaptor.forClass(IntegracioAccioDto.class);
        verify(integracioAccioService).create(captor.capture());
        IntegracioAccioDto accio = captor.getValue();
        assertEquals(IntegracioHelper.INTCODI_ARXIU, accio.getCodi());
        assertEquals(IntegracioAccioEstatEnumDto.OK, accio.getEstat());
        assertEquals("PET001", accio.getIdPeticio());
        assertEquals(1, accio.getParametres().size());
        assertEquals("codi", accio.getParametres().get(0).getNom());
    }

    @Test
    public void addAccioOk_sensePeticio_usaDefaultId() throws Exception {
        integracioHelper.addAccioOk(
                IntegracioHelper.INTCODI_ORGANS,
                "Desc",
                null,
                IntegracioAccioTipusEnumDto.RECEPCIO,
                50L);

        ArgumentCaptor<IntegracioAccioDto> captor = ArgumentCaptor.forClass(IntegracioAccioDto.class);
        verify(integracioAccioService).create(captor.capture());
        assertEquals("--", captor.getValue().getIdPeticio());
    }

    @Test
    public void addAccioError_cridaAlServeiAmbEstatError() throws Exception {
        integracioHelper.addAccioError(
                "PET002",
                IntegracioHelper.INTCODI_USUARIS,
                "Error desc",
                null,
                IntegracioAccioTipusEnumDto.ENVIAMENT,
                200L,
                "Error concret",
                new RuntimeException("exc"));

        ArgumentCaptor<IntegracioAccioDto> captor = ArgumentCaptor.forClass(IntegracioAccioDto.class);
        verify(integracioAccioService).create(captor.capture());
        IntegracioAccioDto accio = captor.getValue();
        assertEquals(IntegracioAccioEstatEnumDto.ERROR, accio.getEstat());
        assertNotNull(accio.getExcepcioMessage());
        assertNotNull(accio.getExcepcioStacktrace());
    }

    @Test
    public void addAccioError_senseTrowable_noIncouExcepcio() throws Exception {
        integracioHelper.addAccioError(
                "PET003",
                IntegracioHelper.INTCODI_FIRMASERV,
                "Error desc",
                null,
                IntegracioAccioTipusEnumDto.ENVIAMENT,
                0L,
                "Desc error");

        ArgumentCaptor<IntegracioAccioDto> captor = ArgumentCaptor.forClass(IntegracioAccioDto.class);
        verify(integracioAccioService).create(captor.capture());
        assertNull(captor.getValue().getExcepcioMessage());
    }

    @Test
    public void addAccioOk_excepcioPersistencia_noPropagarExcepcio() throws Exception {
        when(integracioAccioService.create(any())).thenThrow(new RuntimeException("DB error"));

        assertDoesNotThrow(() -> integracioHelper.addAccioOk(
                IntegracioHelper.INTCODI_SERVEIS_SCSP,
                "Desc",
                null,
                IntegracioAccioTipusEnumDto.ENVIAMENT,
                10L));
    }

    @Test
    public void addAccioError_excepcioPersistencia_noPropagarExcepcio() throws Exception {
        when(integracioAccioService.create(any())).thenThrow(new RuntimeException("DB error"));

        assertDoesNotThrow(() -> integracioHelper.addAccioError(
                IntegracioHelper.INTCODI_EXPLOTACIO,
                "Desc",
                null,
                IntegracioAccioTipusEnumDto.ENVIAMENT,
                10L,
                "error",
                null));
    }
}
