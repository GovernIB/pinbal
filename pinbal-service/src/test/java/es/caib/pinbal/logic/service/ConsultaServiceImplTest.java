package es.caib.pinbal.logic.service;

import es.caib.pinbal.logic.helper.ArbreRespostaDocumentHelper;
import es.caib.pinbal.logic.helper.ConfigHelper;
import es.caib.pinbal.logic.helper.LoggerHelper;
import es.caib.pinbal.logic.helper.ConsultaHelper;
import es.caib.pinbal.logic.helper.DtoMappingHelper;
import es.caib.pinbal.logic.helper.EmailReportEstatHelper;
import es.caib.pinbal.logic.helper.ExcelHelper;
import es.caib.pinbal.logic.helper.IntegracioHelper;
import es.caib.pinbal.logic.helper.PeticioScspEstadistiquesHelper;
import es.caib.pinbal.logic.helper.PeticioScspHelper;
import es.caib.pinbal.logic.helper.PluginHelper;
import es.caib.pinbal.logic.helper.ServeiHelper;
import es.caib.pinbal.logic.helper.UsuariHelper;
import es.caib.pinbal.logic.helper.mock.JustificantHelperFactory;
import es.caib.pinbal.logic.intf.dto.ConsultaDto;
import es.caib.pinbal.logic.intf.dto.RecobrimentSolicitudDto;
import es.caib.pinbal.logic.intf.service.exception.ConsultaNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.EntitatNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.ProcedimentServeiNotFoundException;
import es.caib.pinbal.persist.entity.Consulta;
import es.caib.pinbal.persist.entity.ProcedimentServei;
import es.caib.pinbal.persist.repository.ConsultaRepository;
import es.caib.pinbal.persist.repository.EntitatRepository;
import es.caib.pinbal.persist.repository.EntitatUsuariRepository;
import es.caib.pinbal.persist.repository.ProcedimentRepository;
import es.caib.pinbal.persist.repository.ProcedimentServeiRepository;
import es.caib.pinbal.persist.repository.ServeiJustificantCampRepository;
import es.caib.pinbal.persist.repository.ServeiRepository;
import es.caib.pinbal.persist.repository.SuperConsultaRepository;
import es.caib.pinbal.persist.repository.TokenRepository;
import es.caib.pinbal.persist.repository.UsuariRepository;
import es.caib.pinbal.persist.repository.dadesobertes.DadesObertesConsultaRepository;
import es.caib.pinbal.persist.repository.explotacio.ExplotConsultaDimensioRepository;
import es.caib.pinbal.persist.repository.explotacio.ExplotConsultaFetsRepository;
import es.caib.pinbal.persist.repository.explotacio.ExplotTempsRepository;
import es.caib.pinbal.persist.repository.llistat.LlistatConsultaRepository;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class ConsultaServiceImplTest {

    @Mock private ConsultaRepository consultaRepository;
    @Mock private DadesObertesConsultaRepository dadesObertesConsultaRepository;
    @Mock private EntitatRepository entitatRepository;
    @Mock private EntitatUsuariRepository entitatUsuariRepository;
    @Mock private ExplotConsultaDimensioRepository explotConsultaDimensioRepository;
    @Mock private ExplotConsultaFetsRepository explotConsultaFetsRepository;
    @Mock private ExplotTempsRepository explotTempsRepository;
    @Mock private LlistatConsultaRepository llistatConsultaRepository;
    @Mock private ProcedimentRepository procedimentRepository;
    @Mock private ProcedimentServeiRepository procedimentServeiRepository;
    @Mock private ServeiJustificantCampRepository serveiJustificantCampRepository;
    @Mock private ServeiRepository serveiRepository;
    @Mock private SuperConsultaRepository superConsultaRepository;
    @Mock private TokenRepository tokenRepository;
    @Mock private UsuariRepository usuariRepository;
    @Mock private ConfigHelper configHelper;
    @Mock private ConsultaHelper consultaHelper;
    @Mock private DtoMappingHelper dtoMappingHelper;
    @Mock private EmailReportEstatHelper emailReportEstatHelper;
    @Mock private ExcelHelper excelHelper;
    @Mock private IntegracioHelper integracioHelper;
    @Mock private JustificantHelperFactory justificantHelperFactory;
    @Mock private PeticioScspEstadistiquesHelper peticioScspEstadistiquesHelper;
    @Mock private PeticioScspHelper peticioScspHelper;
    @Mock private PluginHelper pluginHelper;
    @Mock private ServeiHelper serveiHelper;
    @Mock private UsuariHelper usuariHelper;
    @Mock private MutableAclService aclService;
    @Mock private PlatformTransactionManager transactionManager;
    @Mock private MapperFacade mapperFacade;
    @Mock private ScspHelper scspHelper;
    @Mock private LoggerHelper loggerHelper;

    @InjectMocks
    private ConsultaServiceImpl consultaService;

    private Authentication auth;
    private SecurityContext securityContext;

    @BeforeEach
    public void setUp() {
        when(dtoMappingHelper.getMapperFacade()).thenReturn(mapperFacade);
        ReflectionTestUtils.setField(consultaService, "scspHelper", scspHelper);
        ReflectionTestUtils.setField(LoggerHelper.class, "INSTANCE", loggerHelper);
        // Skip copiarPropertiesToDb call on every test
        ReflectionTestUtils.setField(consultaService, "propertiesCopiades", true);
        auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("usuari1");
        when(auth.getAuthorities()).thenReturn(Collections.emptyList());
        securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void novaConsulta_procedimentServeiNull_llancaException() {
        ConsultaDto consulta = new ConsultaDto();
        consulta.setServeiCodi("SV001");
        consulta.setProcedimentId(1L);
        when(procedimentServeiRepository.findByProcedimentIdAndServei(1L, "SV001")).thenReturn(null);

        assertThrows(ProcedimentServeiNotFoundException.class, () -> consultaService.novaConsulta(consulta));
    }

    @Test
    public void novaConsulta_procedimentServeiNoActiu_llancaException() {
        ConsultaDto consulta = new ConsultaDto();
        consulta.setServeiCodi("SV001");
        consulta.setProcedimentId(1L);
        ProcedimentServei ps = mock(ProcedimentServei.class);
        when(ps.isActiu()).thenReturn(false);
        when(procedimentServeiRepository.findByProcedimentIdAndServei(1L, "SV001")).thenReturn(ps);

        assertThrows(ProcedimentServeiNotFoundException.class, () -> consultaService.novaConsulta(consulta));
    }

    @Test
    public void novaConsultaMultiple_procedimentServeiNull_llancaException() {
        ConsultaDto consulta = new ConsultaDto();
        consulta.setServeiCodi("SV001");
        consulta.setProcedimentId(1L);
        when(procedimentServeiRepository.findByProcedimentIdAndServei(1L, "SV001")).thenReturn(null);

        assertThrows(ProcedimentServeiNotFoundException.class, () -> consultaService.novaConsultaMultiple(consulta));
    }

    @Test
    public void obtenirJustificant_consultaNoTrobada_llancaException() {
        when(consultaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ConsultaNotFoundException.class,
                () -> consultaService.obtenirJustificant(99L, true));
    }

    @Test
    public void obtenirJustificantMultipleConcatenat_consultaNoTrobada_llancaException() {
        when(consultaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ConsultaNotFoundException.class,
                () -> consultaService.obtenirJustificantMultipleConcatenat(99L));
    }

    @Test
    public void novaConsultaRecobrimentInit_entitatNoExisteix_llancaException() {
        RecobrimentSolicitudDto solicitud = new RecobrimentSolicitudDto();
        solicitud.setEntitatCif("INEXISTENT");
        when(entitatRepository.findByCif("INEXISTENT")).thenReturn(null);

        assertThrows(EntitatNotFoundException.class,
                () -> consultaService.novaConsultaRecobrimentInit(null, solicitud));
    }

    @Test
    public void novaConsultaRecobriment_entitatNoExisteix_llancaException() {
        RecobrimentSolicitudDto solicitud = new RecobrimentSolicitudDto();
        solicitud.setEntitatCif("INEXISTENT");
        when(entitatRepository.findByCif("INEXISTENT")).thenReturn(null);

        assertThrows(EntitatNotFoundException.class,
                () -> consultaService.novaConsultaRecobriment(null, solicitud));
    }

    @Test
    public void novaConsultaEstat_consultaNoTrobada_llancaException() {
        when(consultaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ConsultaNotFoundException.class,
                () -> consultaService.novaConsultaEstat(99L));
    }
}
