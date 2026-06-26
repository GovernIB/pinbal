package es.caib.pinbal.logic.service;

import es.caib.pinbal.logic.helper.CacheHelper;
import es.caib.pinbal.logic.helper.DtoMappingHelper;
import es.caib.pinbal.logic.helper.PluginHelper;
import es.caib.pinbal.logic.helper.UsuariHelper;
import es.caib.pinbal.logic.intf.dto.EntitatUsuariDto;
import es.caib.pinbal.logic.intf.dto.UsuariDto;
import es.caib.pinbal.logic.intf.service.exception.EntitatNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.NotFoundException;
import es.caib.pinbal.persist.entity.EntitatUsuari;
import es.caib.pinbal.persist.entity.Usuari;
import es.caib.pinbal.persist.repository.AvisRepository;
import es.caib.pinbal.persist.repository.ConfigRepository;
import es.caib.pinbal.persist.repository.ConsultaRepository;
import es.caib.pinbal.persist.repository.EntitatRepository;
import es.caib.pinbal.persist.repository.EntitatServeiRepository;
import es.caib.pinbal.persist.repository.EntitatUsuariRepository;
import es.caib.pinbal.persist.repository.HistoricConsultaRepository;
import es.caib.pinbal.persist.repository.IntegracioAccioParamRepository;
import es.caib.pinbal.persist.repository.IntegracioAccioRepository;
import es.caib.pinbal.persist.repository.OrganGestorRepository;
import es.caib.pinbal.persist.repository.ProcedimentRepository;
import es.caib.pinbal.persist.repository.ProcedimentServeiRepository;
import es.caib.pinbal.persist.repository.ServeiBusRepository;
import es.caib.pinbal.persist.repository.ServeiCampGrupRepository;
import es.caib.pinbal.persist.repository.ServeiCampRepository;
import es.caib.pinbal.persist.repository.ServeiConfigRepository;
import es.caib.pinbal.persist.repository.ServeiJustificantCampRepository;
import es.caib.pinbal.persist.repository.ServeiReglaRepository;
import es.caib.pinbal.persist.repository.UsuariRepository;
import es.caib.pinbal.persist.repository.explotacio.ExplotConsultaDimensioRepository;
import es.caib.pinbal.persist.repository.llistat.LlistatConsultaRepository;
import es.caib.pinbal.persist.repository.llistat.LlistatHistoricConsultaRepository;
import ma.glasnost.orika.MapperFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class UsuariServiceImplTest {

    @Mock private EntitatUsuariRepository entitatUsuariRepository;
    @Mock private AvisRepository avisRepository;
    @Mock private ConfigRepository configRepository;
    @Mock private ConsultaRepository consultaRepository;
    @Mock private EntitatRepository entitatRepository;
    @Mock private EntitatServeiRepository entitatServeiRepository;
    @Mock private ExplotConsultaDimensioRepository explotConsultaDimensioRepository;
    @Mock private HistoricConsultaRepository historicConsultaRepository;
    @Mock private IntegracioAccioRepository integracioAccioRepository;
    @Mock private IntegracioAccioParamRepository integracioAccioParamRepository;
    @Mock private LlistatConsultaRepository llistatConsultaRepository;
    @Mock private LlistatHistoricConsultaRepository llistatHistoricConsultaRepository;
    @Mock private OrganGestorRepository organGestorRepository;
    @Mock private ProcedimentServeiRepository procedimentServeiRepository;
    @Mock private ProcedimentRepository procedimentRepository;
    @Mock private ServeiBusRepository serveiBusRepository;
    @Mock private ServeiCampRepository serveiCampRepository;
    @Mock private ServeiConfigRepository serveiConfigRepository;
    @Mock private ServeiJustificantCampRepository serveiJustificantCampRepository;
    @Mock private ServeiReglaRepository serveiReglaRepository;
    @Mock private ServeiCampGrupRepository serveiCampGrupRepository;
    @Mock private UsuariRepository usuariRepository;
    @Mock private CacheHelper cacheHelper;
    @Mock private DtoMappingHelper dtoMappingHelper;
    @Mock private PluginHelper pluginHelper;
    @Mock private UsuariHelper usuariHelper;
    @Mock private MutableAclService aclService;
    @Mock private AclCache aclCache;
    @Mock private MapperFacade mapperFacade;

    @InjectMocks
    private UsuariServiceImpl usuariService;

    private Authentication auth;
    private SecurityContext securityContext;

    @BeforeEach
    public void setUp() {
        when(dtoMappingHelper.getMapperFacade()).thenReturn(mapperFacade);
        auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("usuari1");
        securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void getDadesPerCodi_usuariExisteix_retornaDto() {
        Usuari usuari = mock(Usuari.class);
        UsuariDto dto = new UsuariDto();
        when(usuariRepository.findById("user1")).thenReturn(Optional.of(usuari));
        when(mapperFacade.map(usuari, UsuariDto.class)).thenReturn(dto);

        UsuariDto result = usuariService.getDades("user1");

        assertNotNull(result);
    }

    @Test
    public void getDadesPerCodi_usuariNoExisteix_retornaNull() {
        when(usuariRepository.findById("inexistent")).thenReturn(Optional.empty());
        when(mapperFacade.map(isNull(), eq(UsuariDto.class))).thenReturn(null);

        UsuariDto result = usuariService.getDades("inexistent");

        assertNull(result);
    }

    @Test
    public void getDades_contextActual_retornaDto() {
        Usuari usuari = mock(Usuari.class);
        UsuariDto dto = new UsuariDto();
        when(usuariRepository.findById("usuari1")).thenReturn(Optional.of(usuari));
        when(mapperFacade.map(usuari, UsuariDto.class)).thenReturn(dto);

        UsuariDto result = usuariService.getDades();

        assertNotNull(result);
    }

    @Test
    public void getIdiomaUsuariActual_usuariExisteix_retornaIdioma() {
        Usuari usuari = mock(Usuari.class);
        when(usuari.getIdioma()).thenReturn("ca");
        when(usuariRepository.findById("usuari1")).thenReturn(Optional.of(usuari));

        String idioma = usuariService.getIdiomaUsuariActual();

        assertEquals("ca", idioma);
    }

    @Test
    public void getIdiomaUsuariActual_usuariNoExisteix_retornaNull() {
        when(usuariRepository.findById("usuari1")).thenReturn(Optional.empty());

        String idioma = usuariService.getIdiomaUsuariActual();

        assertNull(idioma);
    }

    @Test
    public void getNumElementsPaginaDefecte_usuariAmbConfiguracio_retornaConfiguracio() {
        Usuari usuari = mock(Usuari.class);
        when(usuari.getNumElementsPagina()).thenReturn(25);
        when(usuariRepository.findById("usuari1")).thenReturn(Optional.of(usuari));

        Integer result = usuariService.getNumElementsPaginaDefecte();

        assertEquals(25, result);
    }

    @Test
    public void getNumElementsPaginaDefecte_usuariSenseConfiguracio_retornaDefault() {
        Usuari usuari = mock(Usuari.class);
        when(usuari.getNumElementsPagina()).thenReturn(null);
        when(usuariRepository.findById("usuari1")).thenReturn(Optional.of(usuari));

        Integer result = usuariService.getNumElementsPaginaDefecte();

        assertEquals(10, result);
    }

    @Test
    public void findLikeCodiONom_retornaLlista() {
        Usuari usuari = mock(Usuari.class);
        UsuariDto dto = new UsuariDto();
        when(usuariRepository.findByCodiOrNom("text")).thenReturn(Collections.singletonList(usuari));
        when(mapperFacade.mapAsList(anyList(), eq(UsuariDto.class))).thenReturn(Collections.singletonList(dto));

        List<UsuariDto> result = usuariService.findLikeCodiONom("text");

        assertEquals(1, result.size());
    }

    @Test
    public void findLikeCodiONomONif_retornaLlista() {
        Usuari usuari = mock(Usuari.class);
        UsuariDto dto = new UsuariDto();
        when(usuariRepository.findByCodiOrNomOrNif("text")).thenReturn(Collections.singletonList(usuari));
        when(mapperFacade.mapAsList(anyList(), eq(UsuariDto.class))).thenReturn(Collections.singletonList(dto));

        List<UsuariDto> result = usuariService.findLikeCodiONomONif("text");

        assertEquals(1, result.size());
    }

    @Test
    public void getEntitatUsuari_existeix_retornaDto() {
        EntitatUsuari entitatUsuari = mock(EntitatUsuari.class);
        EntitatUsuariDto dto = mock(EntitatUsuariDto.class);
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(1L, "user1")).thenReturn(entitatUsuari);
        when(dtoMappingHelper.convertir(entitatUsuari, EntitatUsuariDto.class)).thenReturn(dto);

        EntitatUsuariDto result = usuariService.getEntitatUsuari(1L, "user1");

        assertNotNull(result);
    }

    @Test
    public void getEntitatUsuari_noExisteix_llancaNotFoundException() {
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(1L, "inexistent")).thenReturn(null);

        assertThrows(NotFoundException.class, () -> usuariService.getEntitatUsuari(1L, "inexistent"));
    }

    @Test
    public void inicialitzarUsuariActual_cridaUsurariHelper() {
        usuariService.inicialitzarUsuariActual();
        verify(usuariHelper).init("usuari1");
    }
}
