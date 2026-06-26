package es.caib.pinbal.logic.helper;

import es.caib.pinbal.logic.intf.dto.EstatTipus;
import es.caib.pinbal.persist.entity.Consulta;
import es.caib.pinbal.persist.entity.ProcedimentServei;
import es.caib.pinbal.persist.entity.dadesobertes.DadesObertesConsulta;
import es.caib.pinbal.persist.entity.llistat.LlistatConsulta;
import es.caib.pinbal.persist.repository.ConsultaRepository;
import es.caib.pinbal.persist.repository.dadesobertes.DadesObertesConsultaRepository;
import es.caib.pinbal.persist.repository.llistat.LlistatConsultaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class ConsultaHelperTest {

    @Mock private LlistatConsultaRepository llistatConsultaRepository;
    @Mock private DadesObertesConsultaRepository dadesObertesConsultaRepository;
    @Mock private ConsultaRepository consultaRepository;
    @Mock private IntegracioHelper integracioHelper;

    @Spy
    @InjectMocks
    private ConsultaHelper consultaHelper;

    @Test
    public void propagaCanviConsulta_llistatNoExisteix_crida_propagaCreacio() {
        Consulta consulta = buildConsultaMock(1L, EstatTipus.Tramitada);
        when(llistatConsultaRepository.findById(1L)).thenReturn(Optional.empty());
        doNothing().when(consultaHelper).propagaCreacioConsulta(any(Consulta.class));

        assertDoesNotThrow(() -> consultaHelper.propagaCanviConsulta(consulta));

        verify(consultaHelper).propagaCreacioConsulta(consulta);
    }

    @Test
    public void propagaCanviConsulta_llistatExisteix_actualitza() {
        Consulta consulta = buildConsultaMock(2L, EstatTipus.Tramitada);
        when(consulta.getScspSolicitudId()).thenReturn("SOL001");
        when(consulta.getError()).thenReturn(null);
        when(consulta.getDataEsperadaResposta()).thenReturn(null);

        LlistatConsulta llistat = mock(LlistatConsulta.class);
        DadesObertesConsulta dadesObertes = mock(DadesObertesConsulta.class);

        when(llistatConsultaRepository.findById(2L)).thenReturn(Optional.of(llistat));
        when(dadesObertesConsultaRepository.findById(2L)).thenReturn(Optional.of(dadesObertes));

        consultaHelper.propagaCanviConsulta(consulta);

        verify(llistatConsultaRepository, atLeast(1)).save(any());
        verify(dadesObertesConsultaRepository).save(any());
    }

    @Test
    public void processarErrorConsulta_consultaNoTrobada_noLlancaException() {
        when(consultaRepository.getOne(99L)).thenReturn(null);
        assertDoesNotThrow(() ->
                consultaHelper.processarErrorConsulta(99L, "Error", 100L, new RuntimeException("test")));
    }

    @Test
    public void processarErrorConsulta_consultaExisteix_actualitzaEstatError() {
        Consulta consulta = buildConsultaMock(3L, EstatTipus.Processant);
        ProcedimentServei ps = mock(ProcedimentServei.class);
        when(ps.getProcediment()).thenReturn(null);
        when(ps.getServeiScsp()).thenReturn(null);
        when(consulta.getProcedimentServei()).thenReturn(ps);
        when(consulta.getScspPeticionId()).thenReturn("PET001");
        when(consulta.getScspSolicitudId()).thenReturn("SOL001");
        when(consultaRepository.getOne(3L)).thenReturn(consulta);

        LlistatConsulta llistat = mock(LlistatConsulta.class);
        DadesObertesConsulta dadesObertes = mock(DadesObertesConsulta.class);
        when(llistatConsultaRepository.findById(3L)).thenReturn(Optional.of(llistat));
        when(dadesObertesConsultaRepository.findById(3L)).thenReturn(Optional.of(dadesObertes));

        assertDoesNotThrow(() ->
                consultaHelper.processarErrorConsulta(3L, "Desc error", 200L, new RuntimeException("error")));

        verify(consulta).updateEstatError(anyString());
    }

    private Consulta buildConsultaMock(Long id, EstatTipus estat) {
        Consulta c = mock(Consulta.class);
        when(c.getId()).thenReturn(id);
        when(c.getEstat()).thenReturn(estat);
        return c;
    }
}
