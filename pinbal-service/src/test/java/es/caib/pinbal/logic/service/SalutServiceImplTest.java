package es.caib.pinbal.logic.service;

import es.caib.comanda.model.server.monitoring.ContextInfo;
import es.caib.comanda.model.server.monitoring.EstatSalutEnum;
import es.caib.comanda.model.server.monitoring.IntegracioInfo;
import es.caib.comanda.model.server.monitoring.SalutInfo;
import es.caib.comanda.model.server.monitoring.SubsistemaInfo;
import es.caib.pinbal.logic.helper.ConfigHelper;
import es.caib.pinbal.logic.helper.IntegracioHelper;
import es.caib.pinbal.logic.helper.SubsistemaMetricHelper;
import es.caib.pinbal.logic.intf.dto.IntegracioDto;
import es.caib.pinbal.persist.entity.Avis;
import es.caib.pinbal.persist.entity.Servei;
import es.caib.pinbal.persist.repository.AvisRepository;
import es.caib.pinbal.persist.repository.ServeiRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class SalutServiceImplTest {

    @Mock
    private AvisRepository avisRepository;

    @Mock
    private ServeiRepository serveiRepository;

    @Mock
    private ConfigHelper configHelper;

    @Mock
    private IntegracioHelper integracioHelper;

    @InjectMocks
    private SalutServiceImpl salutService;

    @BeforeEach
    public void setUp() {
        when(avisRepository.findActive(any())).thenReturn(Collections.emptyList());
    }

    @Test
    public void getIntegracions_combinaIntegracionsIServeis() {
        IntegracioDto integracio = new IntegracioDto();
        integracio.setCodi("ARXIU");
        integracio.setNom("Arxiu digital");
        when(integracioHelper.findAll()).thenReturn(Collections.singletonList(integracio));

        Servei servei = new Servei();
        servei.setCodi("SV001");
        servei.setDescripcio("Servei 001");
        when(serveiRepository.findActius()).thenReturn(Collections.singletonList(servei));

        List<IntegracioInfo> result = salutService.getIntegracions();

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(i -> "ARXIU".equals(i.getCodi())));
        assertTrue(result.stream().anyMatch(i -> "SV001".equals(i.getCodi())));
    }

    @Test
    public void getSubsistemes_retornaTotsElsSubsistemes() {
        List<SubsistemaInfo> result = salutService.getSubsistemes();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        result.forEach(s -> {
            assertNotNull(s.getCodi());
            assertNotNull(s.getNom());
        });
    }

    @Test
    public void getContexts_retornaTresContextos() {
        List<ContextInfo> result = salutService.getContexts("https://pinbal.caib.es");
        assertEquals(3, result.size());
        assertTrue(result.stream().anyMatch(c -> "BACK".equals(c.getCodi())));
        assertTrue(result.stream().anyMatch(c -> "INT".equals(c.getCodi())));
        assertTrue(result.stream().anyMatch(c -> "EXT".equals(c.getCodi())));
    }

    @Test
    public void getContexts_pathsInclouenBaseUrl() {
        String baseUrl = "https://pinbal.caib.es";
        List<ContextInfo> result = salutService.getContexts(baseUrl);
        result.forEach(c -> assertTrue(c.getPath().startsWith(baseUrl)));
    }

    @Test
    public void getContexts_backContextTeManuals() {
        List<ContextInfo> result = salutService.getContexts("https://pinbal.caib.es");
        ContextInfo back = result.stream().filter(c -> "BACK".equals(c.getCodi())).findFirst().orElseThrow();
        assertNotNull(back.getManuals());
        assertFalse(back.getManuals().isEmpty());
    }

    @Test
    public void checkSalut_retornaSalutInfo() {
        try (MockedStatic<SubsistemaMetricHelper> mocked = mockStatic(SubsistemaMetricHelper.class)) {
            SubsistemaMetricHelper.SubsistemesInfo subsistemesInfo =
                    mock(SubsistemaMetricHelper.SubsistemesInfo.class);
            when(subsistemesInfo.getSubsistemesSalut()).thenReturn(Collections.emptyList());
            when(subsistemesInfo.getEstatGlobal()).thenReturn(EstatSalutEnum.UNKNOWN);
            mocked.when(SubsistemaMetricHelper::getSubsistemesInfo).thenReturn(subsistemesInfo);
            mocked.when(SubsistemaMetricHelper::getIntegracionsSalut).thenReturn(Collections.emptyList());

            SalutInfo result = salutService.checkSalut("1.0.0");

            assertNotNull(result);
            assertEquals("PBL", result.getCodi());
            assertEquals("1.0.0", result.getVersio());
            assertNotNull(result.getData());
        }
    }

    @Test
    public void checkSalut_bdOk_estatGlobalUp() {
        try (MockedStatic<SubsistemaMetricHelper> mocked = mockStatic(SubsistemaMetricHelper.class)) {
            SubsistemaMetricHelper.SubsistemesInfo subsistemesInfo =
                    mock(SubsistemaMetricHelper.SubsistemesInfo.class);
            when(subsistemesInfo.getSubsistemesSalut()).thenReturn(Collections.emptyList());
            when(subsistemesInfo.getEstatGlobal()).thenReturn(EstatSalutEnum.UP);
            mocked.when(SubsistemaMetricHelper::getSubsistemesInfo).thenReturn(subsistemesInfo);
            mocked.when(SubsistemaMetricHelper::getIntegracionsSalut).thenReturn(Collections.emptyList());
            when(serveiRepository.count()).thenReturn(5L);

            SalutInfo result = salutService.checkSalut("1.0.0");

            assertEquals(EstatSalutEnum.UP, result.getEstatGlobal().getEstat());
        }
    }
}
