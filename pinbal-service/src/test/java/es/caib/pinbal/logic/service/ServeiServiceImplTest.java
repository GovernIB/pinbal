package es.caib.pinbal.logic.service;

import es.caib.pinbal.logic.helper.CacheHelper;
import es.caib.pinbal.logic.helper.DtoMappingHelper;
import es.caib.pinbal.logic.helper.ServeiHelper;
import es.caib.pinbal.logic.helper.ServeiXsdHelper;
import es.caib.pinbal.logic.helper.UsuariHelper;
import es.caib.pinbal.logic.intf.dto.ServeiDto;
import es.caib.pinbal.logic.intf.service.exception.EntitatNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.ServeiCampGrupNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.ServeiCampNotFoundException;
import es.caib.pinbal.logic.regles.ReglaHelper;
import es.caib.pinbal.persist.entity.EntitatServei;
import es.caib.pinbal.persist.entity.ProcedimentServei;
import es.caib.pinbal.persist.entity.ServeiCamp;
import es.caib.pinbal.persist.entity.ServeiCampGrup;
import es.caib.pinbal.persist.entity.ServeiConfig;
import es.caib.pinbal.persist.entity.Servei;
import es.caib.pinbal.persist.repository.ClauPrivadaRepository;
import es.caib.pinbal.persist.repository.EntitatRepository;
import es.caib.pinbal.persist.repository.EntitatServeiRepository;
import es.caib.pinbal.persist.repository.EntitatUsuariRepository;
import es.caib.pinbal.persist.repository.ProcedimentRepository;
import es.caib.pinbal.persist.repository.ProcedimentServeiRepository;
import es.caib.pinbal.persist.repository.ServeiBusRepository;
import es.caib.pinbal.persist.repository.ServeiCampGrupRepository;
import es.caib.pinbal.persist.repository.ServeiCampRepository;
import es.caib.pinbal.persist.repository.ServeiConfigRepository;
import es.caib.pinbal.persist.repository.ServeiJustificantCampRepository;
import es.caib.pinbal.persist.repository.ServeiReglaRepository;
import es.caib.pinbal.persist.repository.ServeiRepository;
import es.caib.pinbal.persist.repository.ServeiXsdRepository;
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

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class ServeiServiceImplTest {

    @Mock private EntitatRepository entitatRepository;
    @Mock private EntitatUsuariRepository entitatUsuariRepository;
    @Mock private ProcedimentRepository procedimentRepository;
    @Mock private ProcedimentServeiRepository procedimentServeiRepository;
    @Mock private ServeiRepository serveiRepository;
    @Mock private ServeiCampRepository serveiCampRepository;
    @Mock private ServeiCampGrupRepository serveiCampGrupRepository;
    @Mock private ServeiConfigRepository serveiConfigRepository;
    @Mock private ServeiBusRepository serveiBusRepository;
    @Mock private ServeiJustificantCampRepository serveiJustificantCampRepository;
    @Mock private EntitatServeiRepository entitatServeiRepository;
    @Mock private ServeiReglaRepository serveiReglaRepository;
    @Mock private ServeiXsdRepository serveiXsdRepository;
    @Mock private ClauPrivadaRepository clauPrivadaRepository;
    @Mock private ServeiHelper serveiHelper;
    @Mock private DtoMappingHelper dtoMappingHelper;
    @Mock private UsuariHelper usuariHelper;
    @Mock private ServeiXsdHelper serveiXsdHelper;
    @Mock private ReglaHelper reglaHelper;
    @Mock private CacheHelper cacheHelper;
    @Mock private MutableAclService aclService;
    @Mock private MapperFacade mapperFacade;
    @Mock private ScspHelper scspHelper;

    @InjectMocks
    private ServeiServiceImpl serveiService;

    @BeforeEach
    public void setUp() {
        when(dtoMappingHelper.getMapperFacade()).thenReturn(mapperFacade);
        ReflectionTestUtils.setField(serveiService, "scspHelper", scspHelper);
    }

    @Test
    public void findById_existeix_retornaDto() {
        Servei servei = mock(Servei.class);
        ServeiDto dto = new ServeiDto();
        when(serveiRepository.findById(1L)).thenReturn(Optional.of(servei));
        when(mapperFacade.map(servei, ServeiDto.class)).thenReturn(dto);

        ServeiDto result = serveiService.findById(1L);

        assertNotNull(result);
    }

    @Test
    public void findById_noExisteix_retornaNull() {
        when(serveiRepository.findById(99L)).thenReturn(Optional.empty());
        when(mapperFacade.map(isNull(), eq(ServeiDto.class))).thenReturn(null);

        ServeiDto result = serveiService.findById(99L);

        assertNull(result);
    }

    @Test
    public void saveActiu_serveiConfigNoExisteix_creaServeiConfig() {
        when(serveiConfigRepository.findByServei("SV001")).thenReturn(null);
        when(entitatServeiRepository.findByServei("SV001")).thenReturn(Collections.emptyList());
        when(procedimentServeiRepository.findByServei("SV001")).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> serveiService.saveActiu("SV001", true));

        verify(serveiConfigRepository).save(any(ServeiConfig.class));
        verify(cacheHelper).evictServeis();
    }

    @Test
    public void saveActiu_serveiConfigExisteix_actualitzaActiu() {
        ServeiConfig config = mock(ServeiConfig.class);
        when(serveiConfigRepository.findByServei("SV001")).thenReturn(config);
        when(entitatServeiRepository.findByServei("SV001")).thenReturn(Collections.emptyList());
        when(procedimentServeiRepository.findByServei("SV001")).thenReturn(Collections.emptyList());

        serveiService.saveActiu("SV001", false);

        verify(config).updateActiu(false);
        verify(cacheHelper).evictServeis();
    }

    @Test
    public void saveActiu_invalidaCache_perEntitatIPerProcediment() {
        EntitatServei es = mock(EntitatServei.class);
        es.caib.pinbal.persist.entity.Entitat entitat = mock(es.caib.pinbal.persist.entity.Entitat.class);
        when(entitat.getCodi()).thenReturn("ENT01");
        when(es.getEntitat()).thenReturn(entitat);

        ProcedimentServei ps = mock(ProcedimentServei.class);
        es.caib.pinbal.persist.entity.Procediment proc = mock(es.caib.pinbal.persist.entity.Procediment.class);
        when(proc.getCodi()).thenReturn("PROC01");
        when(ps.getProcediment()).thenReturn(proc);

        when(serveiConfigRepository.findByServei("SV001")).thenReturn(mock(ServeiConfig.class));
        when(entitatServeiRepository.findByServei("SV001")).thenReturn(Collections.singletonList(es));
        when(procedimentServeiRepository.findByServei("SV001")).thenReturn(Collections.singletonList(ps));

        serveiService.saveActiu("SV001", true);

        verify(cacheHelper).evictServeisEntitat("ENT01");
        verify(cacheHelper).evictServeisProcediment("PROC01");
    }

    @Test
    public void getServeiPermesosPerDelegat_entitatNoExisteix_llancaException() {
        when(entitatRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntitatNotFoundException.class,
                () -> serveiService.getServeiPermesosPerDelegat(99L, 1L, null));
    }

    @Test
    public void getServeiPermesosPerDelegat_procedimentNoExisteix_llancaException() {
        es.caib.pinbal.persist.entity.Entitat entitat = mock(es.caib.pinbal.persist.entity.Entitat.class);
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitat));
        when(procedimentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProcedimentNotFoundException.class,
                () -> serveiService.getServeiPermesosPerDelegat(1L, 99L, null));
    }

    @Test
    public void deleteServeiCamp_noExisteix_llancaException() {
        when(serveiCampRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ServeiCampNotFoundException.class,
                () -> serveiService.deleteServeiCamp(99L));
    }

    @Test
    public void deleteServeiCampGrup_noExisteix_llancaException() {
        when(serveiCampGrupRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ServeiCampGrupNotFoundException.class,
                () -> serveiService.deleteServeiCampGrup(99L));
    }
}
