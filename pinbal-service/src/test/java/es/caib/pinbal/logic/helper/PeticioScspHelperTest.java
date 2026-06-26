package es.caib.pinbal.logic.helper;

import es.caib.pinbal.persist.entity.Consulta;
import es.caib.pinbal.persist.entity.ProcedimentServei;
import es.caib.pinbal.persist.entity.ServeiConfig;
import es.caib.pinbal.persist.repository.ConsultaRepository;
import es.caib.pinbal.persist.repository.ProcedimentRepository;
import es.caib.pinbal.persist.repository.ServeiCampRepository;
import es.caib.pinbal.persist.repository.ServeiConfigRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PeticioScspHelperTest {

    @Mock private ConsultaRepository consultaRepository;
    @Mock private ServeiConfigRepository serveiConfigRepository;
    @Mock private ServeiCampRepository serveiCampRepository;
    @Mock private ProcedimentRepository procedimentRepository;
    @Mock private PeticioScspEstadistiquesHelper peticionsScspEstadistiquesHelper;
    @Mock private IntegracioHelper integracioHelper;
    @Mock private ConsultaHelper consultaHelper;

    @InjectMocks
    private PeticioScspHelper peticioScspHelper;

    @Test
    public void isEnviarConsultaServei_senseMaxPeticions_retornaTrue() {
        Consulta consulta = mock(Consulta.class);
        ProcedimentServei ps = mock(ProcedimentServei.class);
        when(ps.getServei()).thenReturn("SV001");
        when(consulta.getProcedimentServei()).thenReturn(ps);

        ServeiConfig serveiConfig = mock(ServeiConfig.class);
        when(serveiConfig.getMaxPeticionsMinut()).thenReturn(null);
        when(serveiConfigRepository.findByServei("SV001")).thenReturn(serveiConfig);

        boolean result = peticioScspHelper.isEnviarConsultaServei(consulta, false);

        assertTrue(result);
    }

    @Test
    public void isEnviarConsultaServei_ambMaxPeticionsNoCoberta_retornaTrue() {
        Consulta consulta = mock(Consulta.class);
        ProcedimentServei ps = mock(ProcedimentServei.class);
        when(ps.getServei()).thenReturn("SV002");
        when(consulta.getProcedimentServei()).thenReturn(ps);

        ServeiConfig serveiConfig = mock(ServeiConfig.class);
        when(serveiConfig.getMaxPeticionsMinut()).thenReturn(100);
        when(serveiConfigRepository.findByServei("SV002")).thenReturn(serveiConfig);

        // Auto=true to skip the existOlderFromAutoPendingToSend check
        boolean result = peticioScspHelper.isEnviarConsultaServei(consulta, true);

        assertTrue(result);
    }

    @Test
    public void isEnviarConsultaServei_maxPeticionsAssolides_retornaFalse() {
        Consulta consulta = mock(Consulta.class);
        ProcedimentServei ps = mock(ProcedimentServei.class);
        when(ps.getServei()).thenReturn("SV003");
        when(consulta.getProcedimentServei()).thenReturn(ps);

        ServeiConfig serveiConfig = mock(ServeiConfig.class);
        when(serveiConfig.getMaxPeticionsMinut()).thenReturn(1);
        when(serveiConfigRepository.findByServei("SV003")).thenReturn(serveiConfig);

        // First call → sets count to 1
        peticioScspHelper.isEnviarConsultaServei(consulta, true);
        // Second call in same minute → count (1) >= maxPeticionsMinut (1) → false
        boolean result = peticioScspHelper.isEnviarConsultaServei(consulta, true);

        assertFalse(result);
    }
}
