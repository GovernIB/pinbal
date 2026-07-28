package es.caib.pinbal.logic.service;

import es.caib.pinbal.logic.helper.CacheHelper;
import es.caib.pinbal.logic.helper.DtoMappingHelper;
import es.caib.pinbal.logic.helper.PluginHelper;
import es.caib.pinbal.logic.helper.UsuariHelper;
import es.caib.pinbal.logic.intf.dto.EntitatUsuariDto;
import es.caib.pinbal.logic.intf.dto.InformeUsuariDto;
import es.caib.pinbal.logic.intf.dto.NumElementsPaginaEnum;
import es.caib.pinbal.logic.intf.dto.UsuariDto;
import es.caib.pinbal.logic.intf.service.exception.EntitatNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.EntitatUsuariNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.EntitatUsuariProtegitException;
import es.caib.pinbal.logic.intf.service.exception.NotFoundException;
import es.caib.pinbal.logic.intf.service.exception.UsuariExternNotFoundException;
import es.caib.pinbal.persist.entity.Entitat;
import es.caib.pinbal.persist.entity.EntitatUsuari;
import es.caib.pinbal.persist.entity.Usuari;
import es.caib.pinbal.plugin.SistemaExternException;
import es.caib.pinbal.plugin.usuari.DadesUsuari;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
        when(auth.getAuthorities()).thenReturn(Collections.emptyList());
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

    // ---- findAmbFiltrePaginat ----

    @Test
    public void findAmbFiltrePaginat_idEntitatNull_delegaARepositori() {
        Pageable pageable = PageRequest.of(0, 10);
        when(entitatRepository.findById(isNull())).thenReturn(Optional.empty());
        Page<EntitatUsuari> pagina = new PageImpl<>(Collections.emptyList());
        when(entitatUsuariRepository.findByFiltre(
                anyBoolean(), isNull(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(),
                anyBoolean(), isNull(), anyBoolean(), isNull(), anyBoolean(), isNull(), anyBoolean(), isNull(),
                eq(pageable))).thenReturn(pagina);
        Page<EntitatUsuariDto> paginaDto = new PageImpl<>(Collections.emptyList());
        when(dtoMappingHelper.pageEntities2pageDto(pagina, EntitatUsuariDto.class, pageable)).thenReturn(paginaDto);

        Page<EntitatUsuariDto> result = usuariService.findAmbFiltrePaginat(
                null, null, null, null, null, null, null, null, null, pageable);

        assertNotNull(result);
    }

    @Test
    public void findAmbFiltrePaginat_ambEntitatIFiltres_delegaARepositori() {
        Pageable pageable = PageRequest.of(0, 5);
        Entitat entitat = mock(Entitat.class);
        when(entitatRepository.findById(7L)).thenReturn(Optional.of(entitat));
        Page<EntitatUsuari> pagina = new PageImpl<>(Collections.emptyList());
        when(entitatUsuariRepository.findByFiltre(
                eq(false), eq(entitat), eq(true), eq(true), eq(true), eq(true),
                eq(false), eq("USR"), eq(false), eq("Nom"), eq(false), eq("Nif"), eq(false), eq("Dept"),
                eq(pageable))).thenReturn(pagina);
        Page<EntitatUsuariDto> paginaDto = new PageImpl<>(Collections.emptyList());
        when(dtoMappingHelper.pageEntities2pageDto(pagina, EntitatUsuariDto.class, pageable)).thenReturn(paginaDto);

        Page<EntitatUsuariDto> result = usuariService.findAmbFiltrePaginat(
                7L, true, true, true, true, "USR", "Nom", "Nif", "Dept", pageable);

        assertNotNull(result);
    }

    // ---- getUsuariActual ----

    @Test
    public void getUsuariActual_usuariNoExisteix_retornaNull() {
        when(usuariRepository.findById("usuari1")).thenReturn(Optional.empty());

        assertNull(usuariService.getUsuariActual());
    }

    @Test
    public void getUsuariActual_ambEmailExistent_noConsultaPlugin() throws Exception {
        Usuari usuari = mock(Usuari.class);
        when(usuari.getEmail()).thenReturn("existent@test.com");
        when(usuari.getEntitats()).thenReturn(Collections.emptySet());
        when(usuariRepository.findById("usuari1")).thenReturn(Optional.of(usuari));
        when(dtoMappingHelper.convertir(usuari, UsuariDto.class)).thenReturn(new UsuariDto());

        UsuariDto result = usuariService.getUsuariActual();

        assertNotNull(result);
        verify(pluginHelper, never()).dadesUsuariConsultarAmbUsuariCodi(anyString());
    }

    @Test
    public void getUsuariActual_ambEmailBuit_actualitzaEmailAmbPlugin() throws Exception {
        Usuari usuari = mock(Usuari.class);
        when(usuari.getEmail()).thenReturn("");
        when(usuari.getEntitats()).thenReturn(Collections.emptySet());
        when(usuariRepository.findById("usuari1")).thenReturn(Optional.of(usuari));
        DadesUsuari dadesUsuari = DadesUsuari.builder().email("nou@test.com").build();
        when(pluginHelper.dadesUsuariConsultarAmbUsuariCodi("usuari1")).thenReturn(dadesUsuari);
        when(dtoMappingHelper.convertir(usuari, UsuariDto.class)).thenReturn(new UsuariDto());

        UsuariDto result = usuariService.getUsuariActual();

        assertNotNull(result);
        verify(usuari).updateEmail("nou@test.com");
    }

    @Test
    public void getUsuariActual_pluginLlencaExcepcio_noPropagaError() throws Exception {
        Usuari usuari = mock(Usuari.class);
        when(usuari.getEmail()).thenReturn(null);
        when(usuari.getEntitats()).thenReturn(Collections.emptySet());
        when(usuariRepository.findById("usuari1")).thenReturn(Optional.of(usuari));
        when(pluginHelper.dadesUsuariConsultarAmbUsuariCodi("usuari1")).thenThrow(new SistemaExternException("error"));
        when(dtoMappingHelper.convertir(usuari, UsuariDto.class)).thenReturn(new UsuariDto());

        UsuariDto result = usuariService.getUsuariActual();

        assertNotNull(result);
        verify(usuari, never()).updateEmail(anyString());
    }

    @Test
    public void getUsuariActual_multiplesEntitatsActives_marcaHasMultiplesEntitats() {
        Usuari usuari = mock(Usuari.class);
        when(usuari.getEmail()).thenReturn("test@test.com");
        EntitatUsuari eu1 = mock(EntitatUsuari.class);
        Entitat entitat1 = mock(Entitat.class);
        when(entitat1.isActiva()).thenReturn(true);
        when(eu1.getEntitat()).thenReturn(entitat1);
        EntitatUsuari eu2 = mock(EntitatUsuari.class);
        Entitat entitat2 = mock(Entitat.class);
        when(entitat2.isActiva()).thenReturn(true);
        when(eu2.getEntitat()).thenReturn(entitat2);
        Set<EntitatUsuari> entitats = new LinkedHashSet<>(Arrays.asList(eu1, eu2));
        when(usuari.getEntitats()).thenReturn(entitats);
        when(usuariRepository.findById("usuari1")).thenReturn(Optional.of(usuari));
        UsuariDto dto = new UsuariDto();
        when(dtoMappingHelper.convertir(usuari, UsuariDto.class)).thenReturn(dto);

        UsuariDto result = usuariService.getUsuariActual();

        assertTrue(result.isHasMultiplesEntitats());
    }

    @Test
    public void getUsuariActual_mapaTotsElsRols() {
        Usuari usuari = mock(Usuari.class);
        when(usuari.getEmail()).thenReturn("test@test.com");
        when(usuari.getEntitats()).thenReturn(Collections.emptySet());
        when(usuariRepository.findById("usuari1")).thenReturn(Optional.of(usuari));
        UsuariDto dto = new UsuariDto();
        when(dtoMappingHelper.convertir(usuari, UsuariDto.class)).thenReturn(dto);
        List<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_ADMIN"),
                new SimpleGrantedAuthority("ROLE_REPRES"),
                new SimpleGrantedAuthority("ROLE_DELEG"),
                new SimpleGrantedAuthority("ROLE_AUDIT"),
                new SimpleGrantedAuthority("ROLE_SUPERAUD"),
                new SimpleGrantedAuthority("ROLE_WS"),
                new SimpleGrantedAuthority("ROLE_REPORT"),
                new SimpleGrantedAuthority("ROLE_ALTRE"));
        doReturn(authorities).when(auth).getAuthorities();

        UsuariDto result = usuariService.getUsuariActual();

        assertArrayEquals(
                new String[]{"PBL_ADMIN", "PBL_REPRES", "PBL_DELEG", "PBL_AUDIT", "PBL_SUPERAUD", "PBL_WS", "PBL_REPORT", "ROLE_ALTRE"},
                result.getRols());
    }

    // ---- updateUsuariActual ----

    @Test
    public void updateUsuariActual_usuariNoExisteix_retornaNull() {
        UsuariDto dto = UsuariDto.builder().codi("inexistent").build();
        when(usuariRepository.findById("inexistent")).thenReturn(Optional.empty());

        UsuariDto result = usuariService.updateUsuariActual(dto, true);

        assertNull(result);
    }

    @Test
    public void updateUsuariActual_updateEntitatTrue_actualitzaAmbEntitat() {
        UsuariDto dto = UsuariDto.builder()
                .codi("usuari1")
                .idioma("ca")
                .procedimentId(1L)
                .serveiCodi("SV1")
                .entitatId(2L)
                .departament("Dept")
                .finalitat("Fin")
                .numElementsPagina(NumElementsPaginaEnum.VINT)
                .build();
        Usuari usuari = mock(Usuari.class);
        when(usuari.getEntitats()).thenReturn(Collections.emptySet());
        when(usuariRepository.findById("usuari1")).thenReturn(Optional.of(usuari));
        when(dtoMappingHelper.convertir(usuari, UsuariDto.class)).thenReturn(new UsuariDto());

        UsuariDto result = usuariService.updateUsuariActual(dto, true);

        assertNotNull(result);
        verify(usuari).updateValorsPerDefecte("ca", 1L, "SV1", 2L, "Dept", "Fin", 20);
    }

    @Test
    public void updateUsuariActual_updateEntitatFalse_actualitzaSenseEntitat() {
        UsuariDto dto = UsuariDto.builder()
                .codi("usuari1")
                .idioma("es")
                .procedimentId(3L)
                .serveiCodi("SV2")
                .departament("Dept2")
                .finalitat("Fin2")
                .build();
        Usuari usuari = mock(Usuari.class);
        when(usuari.getEntitats()).thenReturn(Collections.emptySet());
        when(usuariRepository.findById("usuari1")).thenReturn(Optional.of(usuari));
        when(dtoMappingHelper.convertir(usuari, UsuariDto.class)).thenReturn(new UsuariDto());

        UsuariDto result = usuariService.updateUsuariActual(dto, false);

        assertNotNull(result);
        verify(usuari).updateValorsPerDefecte("es", 3L, "SV2", "Dept2", "Fin2", null);
    }

    // ---- findEntitatUsuariById ----

    @Test
    public void findEntitatUsuariById_existeix_retornaDtoAmbEntitatId() {
        EntitatUsuari entitatUsuari = mock(EntitatUsuari.class);
        Entitat entitat = mock(Entitat.class);
        when(entitat.getId()).thenReturn(9L);
        when(entitatUsuari.getEntitat()).thenReturn(entitat);
        when(entitatUsuariRepository.findById(1L)).thenReturn(Optional.of(entitatUsuari));
        EntitatUsuariDto dto = new EntitatUsuariDto(new UsuariDto(), "Departament", false, false, false, false, false, true);
        when(dtoMappingHelper.convertir(entitatUsuari, EntitatUsuariDto.class)).thenReturn(dto);

        EntitatUsuariDto result = usuariService.findEntitatUsuariById(1L);

        assertNotNull(result);
        assertEquals(9L, result.getEntitatId());
    }

    @Test
    public void findEntitatUsuariById_noExisteix_retornaNull() {
        when(entitatUsuariRepository.findById(2L)).thenReturn(Optional.empty());

        assertNull(usuariService.findEntitatUsuariById(2L));
    }

    // ---- getUsuariExtern / getUsuarisExterns ----

    @Test
    public void getUsuariExtern_retornaDtoAmbDadesDelPlugin() throws Exception {
        DadesUsuari dadesUsuari = DadesUsuari.builder().codi("EXT1").nom("Extern").nif("99999999Z").email("extern@test.com").build();
        when(pluginHelper.dadesUsuariConsultarAmbUsuariCodi("EXT1")).thenReturn(dadesUsuari);

        UsuariDto result = usuariService.getUsuariExtern("EXT1");

        assertEquals("EXT1", result.getCodi());
        assertEquals("Extern", result.getNom());
        assertEquals("99999999Z", result.getNif());
        assertEquals("extern@test.com", result.getEmail());
    }

    @Test
    public void getUsuariExtern_pluginLlencaExcepcio_propaga() throws Exception {
        when(pluginHelper.dadesUsuariConsultarAmbUsuariCodi("EXT2")).thenThrow(new SistemaExternException("error"));

        assertThrows(SistemaExternException.class, () -> usuariService.getUsuariExtern("EXT2"));
    }

    @Test
    public void getUsuarisExterns_retornaLlistaBuida_quanPluginRetornaNull() throws Exception {
        when(pluginHelper.dadesUsuariLikeCodiNomOrNif("text")).thenReturn(null);

        List<UsuariDto> result = usuariService.getUsuarisExterns("text");

        assertTrue(result.isEmpty());
    }

    @Test
    public void getUsuarisExterns_retornaLlistaAmbDades() throws Exception {
        DadesUsuari d1 = DadesUsuari.builder().codi("E1").nom("U1").nif("111").email("e1@test.com").build();
        DadesUsuari d2 = DadesUsuari.builder().codi("E2").nom("U2").nif("222").email("e2@test.com").build();
        when(pluginHelper.dadesUsuariLikeCodiNomOrNif("text")).thenReturn(Arrays.asList(d1, d2));

        List<UsuariDto> result = usuariService.getUsuarisExterns("text");

        assertEquals(2, result.size());
        assertEquals("E1", result.get(0).getCodi());
    }

    // ---- getUsuariEntitat / getUsuarisEntitat ----

    @Test
    public void getUsuariEntitat_existeix_retornaDto() {
        EntitatUsuari entitatUsuari = mock(EntitatUsuari.class);
        Usuari usuari = mock(Usuari.class);
        when(usuari.getCodi()).thenReturn("USR20");
        when(usuari.getNom()).thenReturn("Usuari Vint");
        when(usuari.getNif()).thenReturn("55555555E");
        when(usuari.getEmail()).thenReturn("usr20@test.com");
        when(entitatUsuari.getUsuari()).thenReturn(usuari);
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(10L, "USR20")).thenReturn(entitatUsuari);

        UsuariDto result = usuariService.getUsuariEntitat(10L, "USR20");

        assertEquals("USR20", result.getCodi());
        assertEquals(10L, result.getEntitatId());
    }

    @Test
    public void getUsuariEntitat_noExisteix_llancaException() {
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(10L, "inexistent")).thenReturn(null);

        assertThrows(NotFoundException.class, () -> usuariService.getUsuariEntitat(10L, "inexistent"));
    }

    @Test
    public void getUsuarisEntitat_retornaLlista() {
        Usuari u1 = mock(Usuari.class);
        when(u1.getCodi()).thenReturn("U1");
        when(entitatUsuariRepository.findByEntitatIdAndUsuariLikeText(10L, "text")).thenReturn(Collections.singletonList(u1));

        List<UsuariDto> result = usuariService.getUsuarisEntitat(10L, "text");

        assertEquals(1, result.size());
        assertEquals("U1", result.get(0).getCodi());
    }

    @Test
    public void getUsuarisEntitat_retornaLlistaBuida_quanNull() {
        when(entitatUsuariRepository.findByEntitatIdAndUsuariLikeText(10L, "text")).thenReturn(null);

        List<UsuariDto> result = usuariService.getUsuarisEntitat(10L, "text");

        assertTrue(result.isEmpty());
    }

    // ---- updateUsuariCodi(codiAntic, codiNou) ----

    @Test
    public void updateUsuariCodi_usuariAnticNoExisteix_llancaNotFoundException() {
        when(usuariRepository.findByCodi("antic")).thenReturn(null);

        assertThrows(NotFoundException.class, () -> usuariService.updateUsuariCodi("antic", "nou"));
    }

    @Test
    public void updateUsuariCodi_usuariNouJaExisteix_actualitzaReferenciesIRetornaTotal() {
        Usuari usuariAntic = mock(Usuari.class);
        Usuari usuariNou = mock(Usuari.class);
        when(usuariRepository.findByCodi("antic")).thenReturn(usuariAntic);
        when(usuariRepository.findByCodi("nou")).thenReturn(usuariNou);
        when(avisRepository.updateUsuariAuditoria("antic", "nou")).thenReturn(5);
        when(usuariRepository.updateUsuariPermis("antic", "nou")).thenReturn(3);
        when(llistatConsultaRepository.updateUsuariCodi("antic", "nou")).thenReturn(2);

        Long result = usuariService.updateUsuariCodi("antic", "nou");

        assertEquals(10L, result);
        verify(usuariRepository).delete(usuariAntic);
        verify(aclCache).clearCache();
    }

    @Test
    public void updateUsuariCodi_usuariNouNoExisteix_clonaUsuari() {
        Usuari usuariAntic = mock(Usuari.class);
        when(usuariAntic.getNom()).thenReturn("Antic Nom");
        when(usuariAntic.getNif()).thenReturn("12345678A");
        when(usuariAntic.getEmail()).thenReturn("antic@email.com");
        when(usuariAntic.getIdioma()).thenReturn("ca");
        when(usuariAntic.getProcedimentId()).thenReturn(1L);
        when(usuariAntic.getServeiCodi()).thenReturn("SV1");
        when(usuariAntic.getEntitatId()).thenReturn(2L);
        when(usuariAntic.getDepartament()).thenReturn("Departament");
        when(usuariAntic.getFinalitat()).thenReturn("Finalitat");
        when(usuariAntic.getNumElementsPagina()).thenReturn(20);
        when(usuariRepository.findByCodi("antic")).thenReturn(usuariAntic);
        when(usuariRepository.findByCodi("nou")).thenReturn(null);
        when(usuariRepository.saveAndFlush(any(Usuari.class))).thenAnswer(inv -> inv.getArgument(0));

        Long result = usuariService.updateUsuariCodi("antic", "nou");

        assertNotNull(result);
        verify(usuariRepository).saveAndFlush(any(Usuari.class));
        verify(usuariRepository).delete(usuariAntic);
    }

    // ---- updateUsuariCodi(codiAntic, codiNou, nom, nif, email, idioma) ----

    @Test
    public void updateUsuariCodiComplet_usuariAnticNoExisteix_llancaNotFoundException() {
        when(usuariRepository.findByCodi("antic")).thenReturn(null);

        assertThrows(NotFoundException.class, () ->
                usuariService.updateUsuariCodi("antic", "nou", "Nom", "NIF", "email@test.com", "ca"));
    }

    @Test
    public void updateUsuariCodiComplet_senseCodiNou_actualitzaEnLloc() {
        Usuari usuariAntic = mock(Usuari.class);
        when(usuariRepository.findByCodi("antic")).thenReturn(usuariAntic);

        usuariService.updateUsuariCodi("antic", "", "Nom Nou", "87654321B", "nou@email.com", "es");

        verify(usuariAntic).update("Nom Nou", "87654321B");
        verify(usuariAntic).updateEmail("nou@email.com");
        verify(usuariAntic).updateIdioma("es");
        verify(usuariRepository).saveAndFlush(usuariAntic);
        verify(aclCache, never()).clearCache();
    }

    @Test
    public void updateUsuariCodiComplet_ambCodiNouIUsuariNouExistent_actualitzaReferencies() {
        Usuari usuariAntic = mock(Usuari.class);
        Usuari usuariNou = mock(Usuari.class);
        when(usuariRepository.findByCodi("antic")).thenReturn(usuariAntic);
        when(usuariRepository.findByCodi("nou")).thenReturn(usuariNou);

        usuariService.updateUsuariCodi("antic", "nou", "Nom Nou", "87654321B", "nou@email.com", "es");

        verify(usuariRepository).delete(usuariAntic);
        verify(aclCache).clearCache();
        verify(usuariRepository, never()).saveAndFlush(any(Usuari.class));
    }

    @Test
    public void updateUsuariCodiComplet_ambCodiNouIUsuariNouInexistent_clonaUsuari() {
        Usuari usuariAntic = mock(Usuari.class);
        when(usuariAntic.getProcedimentId()).thenReturn(1L);
        when(usuariAntic.getServeiCodi()).thenReturn("SV1");
        when(usuariAntic.getEntitatId()).thenReturn(2L);
        when(usuariAntic.getDepartament()).thenReturn("Dept");
        when(usuariAntic.getFinalitat()).thenReturn("Fin");
        when(usuariAntic.getNumElementsPagina()).thenReturn(10);
        when(usuariRepository.findByCodi("antic")).thenReturn(usuariAntic);
        when(usuariRepository.findByCodi("nou")).thenReturn(null);
        when(usuariRepository.saveAndFlush(any(Usuari.class))).thenAnswer(inv -> inv.getArgument(0));

        usuariService.updateUsuariCodi("antic", "nou", "Nom Nou", "87654321B", "nou@email.com", "es");

        verify(usuariRepository).saveAndFlush(any(Usuari.class));
        verify(usuariRepository).delete(usuariAntic);
        verify(aclCache).clearCache();
    }

    // ---- actualitzarDadesAdmin ----

    @Test
    public void actualitzarDadesAdmin_entitatNoExisteix_llancaEntitatNotFoundException() {
        when(entitatRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntitatNotFoundException.class, () ->
                usuariService.actualitzarDadesAdmin(1L, "USR1", null, "Dept", true, false, false, false, false, true));
    }

    @Test
    public void actualitzarDadesAdmin_perCodiUsuariInicialitzatSenseEntitatUsuari_creaRegistreNou() throws Exception {
        Entitat entitat = mock(Entitat.class);
        when(entitat.getId()).thenReturn(1L);
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitat));
        Usuari usuari = mock(Usuari.class);
        when(usuari.isInicialitzat()).thenReturn(true);
        when(usuari.getCodi()).thenReturn("USR1");
        when(usuariRepository.findByCodi("USR1")).thenReturn(usuari);
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(1L, "USR1")).thenReturn(null);

        usuariService.actualitzarDadesAdmin(1L, "USR1", "12345678A", "Departament TIC", true, false, true, false, false, true);

        verify(entitatUsuariRepository).save(any(EntitatUsuari.class));
        verify(cacheHelper).evictPermisosPerDelegat("USR1");
    }

    @Test
    public void actualitzarDadesAdmin_perNifUsuariNoExisteixDadesTrobades_creaUsuariNouIEntitatUsuari() throws Exception {
        Entitat entitat = mock(Entitat.class);
        when(entitat.getId()).thenReturn(1L);
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitat));
        when(usuariRepository.findByNif("87654321B")).thenReturn(null);
        DadesUsuari dadesUsuari = DadesUsuari.builder().codi("USRNIF").nom("Joan").nif("87654321B").build();
        when(pluginHelper.dadesUsuariConsultarAmbUsuariNif("87654321B")).thenReturn(dadesUsuari);
        when(usuariRepository.save(any(Usuari.class))).thenAnswer(inv -> inv.getArgument(0));
        when(entitatUsuariRepository.findByEntitatIdAndUsuariNif(1L, "87654321B")).thenReturn(null);

        usuariService.actualitzarDadesAdmin(1L, null, "87654321B", "Departament", true, false, false, false, false, true);

        verify(usuariRepository).save(any(Usuari.class));
        verify(entitatUsuariRepository).save(any(EntitatUsuari.class));
        verify(cacheHelper).evictPermisosPerDelegat("USRNIF");
    }

    @Test
    public void actualitzarDadesAdmin_perNifDadesNoTrobades_llancaUsuariExternNotFoundException() throws Exception {
        Entitat entitat = mock(Entitat.class);
        when(entitat.getId()).thenReturn(1L);
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitat));
        when(usuariRepository.findByNif("87654321B")).thenReturn(null);
        when(pluginHelper.dadesUsuariConsultarAmbUsuariNif("87654321B")).thenThrow(new SistemaExternException("no trobat"));

        assertThrows(UsuariExternNotFoundException.class, () ->
                usuariService.actualitzarDadesAdmin(1L, null, "87654321B", "Departament", true, false, false, false, false, true));
    }

    @Test
    public void actualitzarDadesAdmin_perCodiDadesNoTrobades_llancaUsuariExternNotFoundException() throws Exception {
        Entitat entitat = mock(Entitat.class);
        when(entitat.getId()).thenReturn(1L);
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitat));
        when(usuariRepository.findByCodi("USRX")).thenReturn(null);
        when(pluginHelper.dadesUsuariConsultarAmbUsuariCodi("USRX")).thenThrow(new SistemaExternException("no trobat"));

        assertThrows(UsuariExternNotFoundException.class, () ->
                usuariService.actualitzarDadesAdmin(1L, "USRX", null, "Departament", true, false, false, false, false, true));
    }

    @Test
    public void actualitzarDadesAdmin_usuariNoInicialitzatIDadesTrobades_actualitzaAmbMoure() throws Exception {
        Entitat entitat = mock(Entitat.class);
        when(entitat.getId()).thenReturn(1L);
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitat));
        Usuari usuariNoInicialitzat = mock(Usuari.class);
        when(usuariNoInicialitzat.isInicialitzat()).thenReturn(false);
        when(usuariRepository.findByCodi("USR2")).thenReturn(usuariNoInicialitzat);
        DadesUsuari dadesUsuari = DadesUsuari.builder().codi("USR2").nom("Maria").nif("11122233C").build();
        when(pluginHelper.dadesUsuariConsultarAmbUsuariCodi("USR2")).thenReturn(dadesUsuari);
        Usuari usuariMogut = mock(Usuari.class);
        when(usuariMogut.getCodi()).thenReturn("USR2");
        when(usuariHelper.moure(usuariNoInicialitzat, dadesUsuari, usuariRepository, procedimentServeiRepository, aclService))
                .thenReturn(usuariMogut);
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(1L, "USR2")).thenReturn(null);

        usuariService.actualitzarDadesAdmin(1L, "USR2", null, "Departament", true, false, false, false, false, true);

        verify(usuariHelper).moure(usuariNoInicialitzat, dadesUsuari, usuariRepository, procedimentServeiRepository, aclService);
        verify(entitatUsuariRepository).save(any(EntitatUsuari.class));
        verify(cacheHelper).evictPermisosPerDelegat("USR2");
    }

    @Test
    public void actualitzarDadesAdmin_entitatUsuariExistent_afegirTrue_combinaFlagsAmbOr() throws Exception {
        Entitat entitat = mock(Entitat.class);
        when(entitat.getId()).thenReturn(1L);
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitat));
        Usuari usuari = mock(Usuari.class);
        when(usuari.isInicialitzat()).thenReturn(true);
        when(usuari.getCodi()).thenReturn("USR3");
        when(usuariRepository.findByCodi("USR3")).thenReturn(usuari);
        EntitatUsuari entitatUsuari = mock(EntitatUsuari.class);
        when(entitatUsuari.isRepresentant()).thenReturn(true);
        when(entitatUsuari.isDelegat()).thenReturn(false);
        when(entitatUsuari.isAuditor()).thenReturn(false);
        when(entitatUsuari.isAplicacio()).thenReturn(false);
        when(entitatUsuari.isActiu()).thenReturn(true);
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(1L, "USR3")).thenReturn(entitatUsuari);

        usuariService.actualitzarDadesAdmin(1L, "USR3", null, "Departament2", false, true, true, true, true, true);

        verify(entitatUsuari).update(eq("Departament2"), eq(true), eq(true), eq(true), eq(true), eq(true));
    }

    @Test
    public void actualitzarDadesAdmin_entitatUsuariExistent_afegirFalse_substitueixFlags() throws Exception {
        Entitat entitat = mock(Entitat.class);
        when(entitat.getId()).thenReturn(1L);
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitat));
        Usuari usuari = mock(Usuari.class);
        when(usuari.isInicialitzat()).thenReturn(true);
        when(usuari.getCodi()).thenReturn("USR4");
        when(usuariRepository.findByCodi("USR4")).thenReturn(usuari);
        EntitatUsuari entitatUsuari = mock(EntitatUsuari.class);
        when(entitatUsuari.isActiu()).thenReturn(true);
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(1L, "USR4")).thenReturn(entitatUsuari);

        usuariService.actualitzarDadesAdmin(1L, "USR4", null, "Departament3", true, false, true, false, false, false);

        verify(entitatUsuari).update(eq("Departament3"), eq(true), eq(false), eq(true), eq(false), eq(false));
    }

    // ---- actualitzarDadesRepresentant ----

    @Test
    public void actualitzarDadesRepresentant_entitatNoExisteix_llancaException() {
        when(entitatRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntitatNotFoundException.class, () ->
                usuariService.actualitzarDadesRepresentant(2L, "USR5", null, "Dept", true, false, false, false, true));
    }

    @Test
    public void actualitzarDadesRepresentant_usuariPrincipal_llancaEntitatUsuariProtegitException() throws Exception {
        Entitat entitat = mock(Entitat.class);
        when(entitat.getId()).thenReturn(2L);
        when(entitatRepository.findById(2L)).thenReturn(Optional.of(entitat));
        EntitatUsuari entitatUsuari = mock(EntitatUsuari.class);
        when(entitatUsuari.isPrincipal()).thenReturn(true);
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(2L, "USR5")).thenReturn(entitatUsuari);

        assertThrows(EntitatUsuariProtegitException.class, () ->
                usuariService.actualitzarDadesRepresentant(2L, "USR5", null, "Dept", true, false, false, false, true));
    }

    @Test
    public void actualitzarDadesRepresentant_perNifUsuariPrincipal_llancaException() throws Exception {
        Entitat entitat = mock(Entitat.class);
        when(entitat.getId()).thenReturn(2L);
        when(entitatRepository.findById(2L)).thenReturn(Optional.of(entitat));
        EntitatUsuari entitatUsuari = mock(EntitatUsuari.class);
        when(entitatUsuari.isPrincipal()).thenReturn(true);
        when(entitatUsuariRepository.findByEntitatIdAndUsuariNif(2L, "22233344D")).thenReturn(entitatUsuari);

        assertThrows(EntitatUsuariProtegitException.class, () ->
                usuariService.actualitzarDadesRepresentant(2L, null, "22233344D", "Dept", true, false, false, false, true));
    }

    @Test
    public void actualitzarDadesRepresentant_usuariNoPrincipal_actualitzaCorrectament() throws Exception {
        Entitat entitat = mock(Entitat.class);
        when(entitat.getId()).thenReturn(2L);
        when(entitatRepository.findById(2L)).thenReturn(Optional.of(entitat));
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(2L, "USR6")).thenReturn(null);
        Usuari usuari = mock(Usuari.class);
        when(usuari.isInicialitzat()).thenReturn(true);
        when(usuari.getCodi()).thenReturn("USR6");
        when(usuariRepository.findByCodi("USR6")).thenReturn(usuari);

        usuariService.actualitzarDadesRepresentant(2L, "USR6", null, "Dept", true, false, true, false, true);

        verify(entitatUsuariRepository).save(any(EntitatUsuari.class));
    }

    // ---- actualitzarDadesAuditor ----

    @Test
    public void actualitzarDadesAuditor_entitatNoExisteix_llancaException() {
        when(entitatRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(EntitatNotFoundException.class, () ->
                usuariService.actualitzarDadesAuditor(3L, "USR7", null, true, false));
    }

    @Test
    public void actualitzarDadesAuditor_usuariPrincipal_llancaEntitatUsuariProtegitException() throws Exception {
        Entitat entitat = mock(Entitat.class);
        when(entitat.getId()).thenReturn(3L);
        when(entitatRepository.findById(3L)).thenReturn(Optional.of(entitat));
        EntitatUsuari entitatUsuari = mock(EntitatUsuari.class);
        when(entitatUsuari.isPrincipal()).thenReturn(true);
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(3L, "USR7")).thenReturn(entitatUsuari);

        assertThrows(EntitatUsuariProtegitException.class, () ->
                usuariService.actualitzarDadesAuditor(3L, "USR7", null, true, false));
    }

    @Test
    public void actualitzarDadesAuditor_usuariNoPrincipal_actualitzaCorrectament() throws Exception {
        Entitat entitat = mock(Entitat.class);
        when(entitat.getId()).thenReturn(3L);
        when(entitatRepository.findById(3L)).thenReturn(Optional.of(entitat));
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(3L, "USR8")).thenReturn(null);
        Usuari usuari = mock(Usuari.class);
        when(usuari.isInicialitzat()).thenReturn(true);
        when(usuari.getCodi()).thenReturn("USR8");
        when(usuariRepository.findByCodi("USR8")).thenReturn(usuari);

        usuariService.actualitzarDadesAuditor(3L, "USR8", null, true, true);

        verify(entitatUsuariRepository).save(any(EntitatUsuari.class));
    }

    // ---- establirPrincipal ----

    @Test
    public void establirPrincipal_entitatNoExisteix_llancaException() {
        when(entitatRepository.findById(4L)).thenReturn(Optional.empty());

        assertThrows(EntitatNotFoundException.class, () -> usuariService.establirPrincipal(4L, "USR9"));
    }

    @Test
    public void establirPrincipal_entitatUsuariNoExisteix_llancaException() throws Exception {
        Entitat entitat = mock(Entitat.class);
        when(entitatRepository.findById(4L)).thenReturn(Optional.of(entitat));
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(4L, "USR9")).thenReturn(null);

        assertThrows(EntitatUsuariNotFoundException.class, () -> usuariService.establirPrincipal(4L, "USR9"));
    }

    @Test
    public void establirPrincipal_canviaEstatPrincipal() throws Exception {
        Entitat entitat = mock(Entitat.class);
        when(entitatRepository.findById(4L)).thenReturn(Optional.of(entitat));
        EntitatUsuari entitatUsuari = mock(EntitatUsuari.class);
        when(entitatUsuari.canviPrincipal()).thenReturn(true);
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(4L, "USR9")).thenReturn(entitatUsuari);

        boolean result = usuariService.establirPrincipal(4L, "USR9");

        assertTrue(result);
    }

    // ---- canviActiu ----

    @Test
    public void canviActiu_entitatNoExisteix_llancaException() {
        when(entitatRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(EntitatNotFoundException.class, () -> usuariService.canviActiu(5L, "USR10"));
    }

    @Test
    public void canviActiu_entitatUsuariNoExisteix_llancaException() throws Exception {
        Entitat entitat = mock(Entitat.class);
        when(entitatRepository.findById(5L)).thenReturn(Optional.of(entitat));
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(5L, "USR10")).thenReturn(null);

        assertThrows(EntitatUsuariNotFoundException.class, () -> usuariService.canviActiu(5L, "USR10"));
    }

    @Test
    public void canviActiu_canviaEstatActiu() throws Exception {
        Entitat entitat = mock(Entitat.class);
        when(entitatRepository.findById(5L)).thenReturn(Optional.of(entitat));
        EntitatUsuari entitatUsuari = mock(EntitatUsuari.class);
        when(entitatUsuari.canviActiu()).thenReturn(false);
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(5L, "USR10")).thenReturn(entitatUsuari);

        boolean result = usuariService.canviActiu(5L, "USR10");

        assertFalse(result);
    }

    // ---- informeUsuarisAgrupatsEntitatDepartament ----

    @Test
    public void informeUsuarisAgrupatsEntitatDepartament_retornaLlistaInformes() {
        EntitatUsuari eu1 = mock(EntitatUsuari.class);
        Entitat entitat1 = mock(Entitat.class);
        when(eu1.getEntitat()).thenReturn(entitat1);
        when(eu1.getDepartament()).thenReturn("Informatica");
        EntitatUsuari eu2 = mock(EntitatUsuari.class);
        when(eu2.getEntitat()).thenReturn(entitat1);
        when(eu2.getDepartament()).thenReturn("Informatica");
        when(entitatUsuariRepository.findAllOrderByEntitatAndDepartament()).thenReturn(Arrays.asList(eu1, eu2));
        InformeUsuariDto dto1 = new InformeUsuariDto();
        InformeUsuariDto dto2 = new InformeUsuariDto();
        when(mapperFacade.map(eu1, InformeUsuariDto.class)).thenReturn(dto1);
        when(mapperFacade.map(eu2, InformeUsuariDto.class)).thenReturn(dto2);

        List<InformeUsuariDto> result = usuariService.informeUsuarisAgrupatsEntitatDepartament();

        assertEquals(2, result.size());
    }
}
