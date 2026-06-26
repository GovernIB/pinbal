package es.caib.pinbal.logic.service;

import es.caib.comanda.model.server.monitoring.EstadistiquesInfo;
import es.caib.comanda.model.server.monitoring.RegistresEstadistics;
import es.caib.pinbal.logic.intf.service.ConsultaService;
import es.caib.pinbal.persist.entity.explotacio.ExplotConsultaFets;
import es.caib.pinbal.persist.entity.explotacio.ExplotTempsEntity;
import es.caib.pinbal.persist.repository.EntitatRepository;
import es.caib.pinbal.persist.repository.ProcedimentRepository;
import es.caib.pinbal.persist.repository.ServeiConfigRepository;
import es.caib.pinbal.persist.repository.UsuariRepository;
import es.caib.pinbal.persist.repository.explotacio.ExplotConsultaFetsRepository;
import es.caib.pinbal.persist.repository.explotacio.ExplotTempsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class EstadisticaServiceImplTest {

    @Mock private UsuariRepository usuariRepository;
    @Mock private EntitatRepository entitatRepository;
    @Mock private ProcedimentRepository procedimentRepository;
    @Mock private ServeiConfigRepository serveiConfigRepository;
    @Mock private ExplotTempsRepository explotTempsRepository;
    @Mock private ExplotConsultaFetsRepository explotConsultaFetsRepository;
    @Mock private ConsultaService consultaService;

    @InjectMocks
    private EstadisticaServiceImpl estadisticaService;

    @BeforeEach
    public void setUp() {
        when(entitatRepository.findAllCodis()).thenReturn(Arrays.asList("ENT001", "ENT002"));
        when(procedimentRepository.findAllCodis()).thenReturn(Arrays.asList("PRC001"));
        when(serveiConfigRepository.findAllCodis()).thenReturn(Arrays.asList("SV001"));
        when(usuariRepository.findAllCodis()).thenReturn(Arrays.asList("USU001"));
    }

    @Test
    public void getEstadistiquesInfo_retornaInfoAmbDimensionsIIndicadors() {
        EstadistiquesInfo result = estadisticaService.getEstadistiquesInfo();

        assertNotNull(result);
        assertEquals("PBL", result.getCodi());
        assertEquals(6, result.getDimensions().size());
        assertEquals(4, result.getIndicadors().size());
    }

    @Test
    public void getEstadistiquesInfo_dimensionsAmbNomIDescripcio() {
        EstadistiquesInfo result = estadisticaService.getEstadistiquesInfo();

        result.getDimensions().forEach(d -> {
            assertNotNull(d.getCodi());
            assertNotNull(d.getNom());
            assertNotNull(d.getDescripcio());
        });
    }

    @Test
    public void consultaEstadistiques_dataFutura_retornaLlistaBuilda() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date dataFutura = cal.getTime();

        RegistresEstadistics result = estadisticaService.consultaEstadistiques(dataFutura);

        assertNotNull(result);
        assertNotNull(result.getFets());
        assertTrue(result.getFets().isEmpty());
    }

    @Test
    public void consultaEstadistiques_senseFets_retornaRegistresBuids() {
        Date dataAhir = dataAhir();
        ExplotTempsEntity temps = new ExplotTempsEntity();
        when(explotTempsRepository.findFirstByData(any())).thenReturn(temps);
        when(explotConsultaFetsRepository.findByTemps(temps)).thenReturn(Collections.emptyList());

        RegistresEstadistics result = estadisticaService.consultaEstadistiques(dataAhir);

        assertNotNull(result);
    }

    @Test
    public void consultaEstadistiques_ambFets_retornaRegistresAmbDades() {
        Date dataAhir = dataAhir();
        ExplotTempsEntity temps = new ExplotTempsEntity();
        when(explotTempsRepository.findFirstByData(any())).thenReturn(temps);

        ExplotConsultaFets fets = ExplotConsultaFets.builder()
                .entitatId(1L)
                .entitatCodi("ENT001")
                .procedimentId(1L)
                .procedimentCodi("PRC001")
                .serveiCodi("SV001")
                .usuariCodi("USU001")
                .webOk(5L)
                .build();
        when(explotConsultaFetsRepository.findByTemps(temps)).thenReturn(Collections.singletonList(fets));

        RegistresEstadistics result = estadisticaService.consultaEstadistiques(dataAhir);

        assertNotNull(result);
        assertNotNull(result.getFets());
    }

    @Test
    public void consultaEstadistiques_rangeDataes_buidesRetornaLluidaBuida() {
        List<RegistresEstadistics> result = estadisticaService.consultaEstadistiques(null, null);
        assertTrue(result.isEmpty());
    }

    @Test
    public void consultaEstadistiques_dataIniciPosteriorAFi_intercanvia() {
        Date dataInici = dataAhir();
        Date dataFi = new Date();
        // dataInici < dataFi after swap; should execute without error
        when(explotTempsRepository.findFirstByData(any())).thenReturn(new ExplotTempsEntity());
        when(explotConsultaFetsRepository.findByTemps(any())).thenReturn(Collections.emptyList());

        // intercanvi no llança excepció
        assertDoesNotThrow(() -> estadisticaService.consultaEstadistiques(dataFi, dataInici));
    }

    @Test
    public void generarEstadistiques_datesNull_retornaMissatgeError() {
        String result = estadisticaService.generarEstadistiques(null, null);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void generarEstadistiques_rango_cridaConsultaService() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -3);
        Date dataInici = cal.getTime();
        Date dataFi = new Date();

        estadisticaService.generarEstadistiques(dataInici, dataFi);

        verify(consultaService, atLeastOnce()).generarDadesExplotacio(any());
    }

    @Test
    public void consultaUltimesEstadistiques_delegaAConsultaEstadistiquesNull() {
        when(explotTempsRepository.findFirstByData(any())).thenReturn(new ExplotTempsEntity());
        when(explotConsultaFetsRepository.findByTemps(any())).thenReturn(Collections.emptyList());

        RegistresEstadistics result = estadisticaService.consultaUltimesEstadistiques();

        assertNotNull(result);
    }

    private Date dataAhir() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}
