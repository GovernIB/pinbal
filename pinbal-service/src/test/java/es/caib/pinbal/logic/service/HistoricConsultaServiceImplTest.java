package es.caib.pinbal.logic.service;

import es.caib.pinbal.logic.helper.ConfigHelper;
import es.caib.pinbal.logic.helper.DtoMappingHelper;
import es.caib.pinbal.logic.helper.PeticioScspEstadistiquesHelper;
import es.caib.pinbal.logic.helper.PluginHelper;
import es.caib.pinbal.logic.intf.service.exception.ConsultaNotFoundException;
import es.caib.pinbal.persist.entity.HistoricConsulta;
import es.caib.pinbal.persist.repository.EntitatRepository;
import es.caib.pinbal.persist.repository.EntitatUsuariRepository;
import es.caib.pinbal.persist.repository.HistoricConsultaRepository;
import es.caib.pinbal.persist.repository.ProcedimentRepository;
import es.caib.pinbal.persist.repository.ProcedimentServeiRepository;
import es.caib.pinbal.persist.repository.TokenRepository;
import es.caib.pinbal.persist.repository.UsuariRepository;
import es.caib.pinbal.persist.repository.dadesobertes.DadesObertesHistoricConsultaRepository;
import es.caib.pinbal.persist.repository.llistat.LlistatHistoricConsultaRepository;
import es.caib.pinbal.logic.helper.mock.JustificantHelperFactory;
import es.caib.pinbal.scsp.ScspHelper;
import ma.glasnost.orika.MapperFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class HistoricConsultaServiceImplTest {

    @Mock private HistoricConsultaRepository historicConsultaRepository;
    @Mock private ProcedimentRepository procedimentRepository;
    @Mock private ProcedimentServeiRepository procedimentServeiRepository;
    @Mock private EntitatRepository entitatRepository;
    @Mock private UsuariRepository usuariRepository;
    @Mock private EntitatUsuariRepository entitatUsuariRepository;
    @Mock private TokenRepository tokenRepository;
    @Mock private JustificantHelperFactory justificantHelperFactory;
    @Mock private DtoMappingHelper dtoMappingHelper;
    @Mock private PluginHelper pluginHelper;
    @Mock private PeticioScspEstadistiquesHelper peticioScspEstadistiquesHelper;
    @Mock private ConfigHelper configHelper;
    @Mock private MutableAclService aclService;
    @Mock private PlatformTransactionManager transactionManager;
    @Mock private DadesObertesHistoricConsultaRepository dadesObertesHistoricConsultaRepository;
    @Mock private LlistatHistoricConsultaRepository llistatHistoricConsultaRepository;
    @Mock private MapperFacade mapperFacade;
    @Mock private ScspHelper scspHelper;

    @InjectMocks
    private HistoricConsultaServiceImpl historicConsultaService;

    @BeforeEach
    public void setUp() {
        when(dtoMappingHelper.getMapperFacade()).thenReturn(mapperFacade);
        ReflectionTestUtils.setField(historicConsultaService, "scspHelper", scspHelper);
    }

    @Test
    public void obtenirArxiuInfo_consultaNoTrobada_retornaDetallBuid() {
        when(historicConsultaRepository.findById(99L)).thenReturn(Optional.empty());
        // Per al path de l'excepció (consulta == null llança ConsultaNotFoundException però
        // el try/catch intern retorna un ArxiuDetallDto buid)
        var result = historicConsultaService.obtenirArxiuInfo(99L);
        assertNotNull(result);
    }

    @Test
    public void obtenirJustificant_consultaNoTrobada_llancaException() {
        when(historicConsultaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ConsultaNotFoundException.class,
                () -> historicConsultaService.obtenirJustificant(99L, true));
    }

    @Test
    public void obtenirJustificantPerPeticio_consultaNoTrobada_llancaException() {
        when(historicConsultaRepository.findByScspPeticionIdAndScspSolicitudId("PET001", "SOL001"))
                .thenReturn(null);
        assertThrows(ConsultaNotFoundException.class,
                () -> historicConsultaService.obtenirJustificant("PET001", "SOL001", false, false));
    }

    @Test
    public void reintentarGeneracioJustificant_consultaNoTrobada_llancaException() {
        when(historicConsultaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ConsultaNotFoundException.class,
                () -> historicConsultaService.reintentarGeneracioJustificant(99L, false));
    }

    @Test
    public void obtenirJustificantMultipleConcatenat_consultaNoTrobada_llancaException() {
        when(configHelper.getConfig(anyString(), any())).thenReturn("false");
        when(historicConsultaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ConsultaNotFoundException.class,
                () -> historicConsultaService.obtenirJustificantMultipleConcatenat(99L));
    }
}
