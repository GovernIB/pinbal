package es.caib.pinbal.logic.helper;

import es.caib.pinbal.logic.intf.dto.CarregaDto;
import es.caib.pinbal.persist.repository.ConsultaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PeticioScspEstadistiquesHelperTest {

    @Mock private ConsultaRepository consultaRepository;

    @InjectMocks
    private PeticioScspEstadistiquesHelper estadistiquesHelper;

    @Test
    public void consultaEstadistiques_repositoriVuid_retornaLlistaVuida() {
        when(consultaRepository.findCarrega(any(Date.class))).thenReturn(Collections.emptyList());

        List<CarregaDto> result = estadistiquesHelper.consultaEstadistiques();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void consultaEstadistiques_segonaCrida_noTornaAinicialitzar() {
        when(consultaRepository.findCarrega(any(Date.class))).thenReturn(Collections.emptyList());

        estadistiquesHelper.consultaEstadistiques();
        estadistiquesHelper.consultaEstadistiques();

        // findCarrega is called 5 times on first init, but not again on second call
        verify(consultaRepository, times(5)).findCarrega(any(Date.class));
    }

    @Test
    public void actualitzarEstadistiquesPeticio_solicitudsNull_noFaRes() {
        when(consultaRepository.findCarrega(any(Date.class))).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() ->
                estadistiquesHelper.actualitzarEstadistiquesPeticio(1L, null, false));
    }

    @Test
    public void actualitzarEstadistiquesPeticio_solicitudsVuides_noFaRes() {
        when(consultaRepository.findCarrega(any(Date.class))).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() ->
                estadistiquesHelper.actualitzarEstadistiquesPeticio(1L, Collections.emptyList(), false));
    }
}
