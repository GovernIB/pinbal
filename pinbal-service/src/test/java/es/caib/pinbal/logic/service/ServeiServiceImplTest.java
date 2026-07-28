package es.caib.pinbal.logic.service;

import es.caib.pinbal.logic.helper.CacheHelper;
import es.caib.pinbal.logic.helper.DtoMappingHelper;
import es.caib.pinbal.logic.helper.ServeiHelper;
import es.caib.pinbal.logic.helper.ServeiXsdHelper;
import es.caib.pinbal.logic.helper.UsuariHelper;
import es.caib.pinbal.logic.intf.dto.ArbreDto;
import es.caib.pinbal.logic.intf.dto.ClauPrivadaDto;
import es.caib.pinbal.logic.intf.dto.ClauPublicaDto;
import es.caib.pinbal.logic.intf.dto.DadaEspecificaDto;
import es.caib.pinbal.logic.intf.dto.EmisorDto;
import es.caib.pinbal.logic.intf.dto.EntitatDto;
import es.caib.pinbal.logic.intf.dto.ProcedimentDto;
import es.caib.pinbal.logic.intf.dto.ServeiBusDto;
import es.caib.pinbal.logic.intf.dto.ServeiCampDto;
import es.caib.pinbal.logic.intf.dto.ServeiCampDto.ServeiCampDtoTipus;
import es.caib.pinbal.logic.intf.dto.ServeiCampGrupDto;
import es.caib.pinbal.logic.intf.dto.ServeiDto;
import es.caib.pinbal.logic.intf.dto.ServeiDto.EntitatTipusDto;
import es.caib.pinbal.logic.intf.dto.ServeiDto.JustificantTipusDto;
import es.caib.pinbal.logic.intf.dto.ServeiJustificantCampDto;
import es.caib.pinbal.logic.intf.dto.regles.ServeiReglaDto;
import es.caib.pinbal.logic.intf.dto.ServeiXsdDto;
import es.caib.pinbal.logic.intf.dto.XsdTipusEnumDto;
import es.caib.pinbal.logic.intf.dto.regles.AccioEnum;
import es.caib.pinbal.logic.intf.dto.regles.ModificatEnum;
import es.caib.pinbal.logic.intf.service.ServeiService;
import es.caib.pinbal.logic.intf.service.exception.EntitatNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.NotFoundException;
import es.caib.pinbal.logic.intf.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.ScspException;
import es.caib.pinbal.logic.intf.service.exception.ServeiAmbConsultesException;
import es.caib.pinbal.logic.intf.service.exception.ServeiBusNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.ServeiCampGrupNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.ServeiCampNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.ServeiNotFoundException;
import es.caib.pinbal.logic.regles.ReglaHelper;
import es.caib.pinbal.persist.entity.ClauPrivada;
import es.caib.pinbal.persist.entity.Entitat;
import es.caib.pinbal.persist.entity.EntitatServei;
import es.caib.pinbal.persist.entity.EntitatUsuari;
import es.caib.pinbal.persist.entity.Procediment;
import es.caib.pinbal.persist.entity.ProcedimentServei;
import es.caib.pinbal.persist.entity.Servei;
import es.caib.pinbal.persist.entity.ServeiBus;
import es.caib.pinbal.persist.entity.ServeiCamp;
import es.caib.pinbal.persist.entity.ServeiCampGrup;
import es.caib.pinbal.persist.entity.ServeiConfig;
import es.caib.pinbal.persist.entity.ServeiJustificantCamp;
import es.caib.pinbal.persist.entity.ServeiRegla;
import es.caib.pinbal.persist.entity.ServeiXsd;
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
import es.caib.pinbal.scsp.XmlHelper.DadesEspecifiquesNode;
import es.caib.pinbal.scsp.tree.Node;
import es.caib.pinbal.scsp.tree.Tree;
import es.scsp.common.domain.core.ClavePrivada;
import es.scsp.common.domain.core.ClavePublica;
import es.scsp.common.domain.core.EmisorCertificado;
import es.scsp.common.domain.core.Servicio;
import ma.glasnost.orika.MapperFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
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
    @Mock private ApplicationContext applicationContext;
    @Mock private MessageSource messageSource;
    @Mock private ServeiService self;

    @InjectMocks
    private ServeiServiceImpl serveiService;

    private Authentication auth;
    private SecurityContext securityContext;

    @BeforeEach
    public void setUp() {
        when(dtoMappingHelper.getMapperFacade()).thenReturn(mapperFacade);
        // Per defecte mapperFacade.map(...) instancia el tipus destí amb el constructor buit,
        // perquè els tests que no els interessa el contingut del DTO no obtinguin null.
        when(mapperFacade.map(any(), any())).thenAnswer(invocation -> {
            Object source = invocation.getArgument(0);
            Class<?> destClass = invocation.getArgument(1);
            if (source == null || destClass == null) {
                return null;
            }
            try {
                return destClass.getDeclaredConstructor().newInstance();
            } catch (ReflectiveOperationException e) {
                return null;
            }
        });
        ReflectionTestUtils.setField(serveiService, "scspHelper", scspHelper);

        auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("usuari1");
        doReturn(Collections.emptyList()).when(auth).getAuthorities();
        securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
    }

    private Servei stubServeiEntitat(String codi, Long id) {
        Servei servei = mock(Servei.class);
        when(servei.getId()).thenReturn(id);
        when(servei.getCodi()).thenReturn(codi);
        when(serveiRepository.findByCode(codi)).thenReturn(Collections.singletonList(servei));
        return servei;
    }

    private Servicio stubServicioByCode(String codi, Long id) {
        Servicio servicio = new Servicio();
        servicio.setCodCertificado(codi);
        servicio.setDescripcion("Descripcio " + codi);
        stubServeiEntitat(codi, id);
        when(scspHelper.getServicioById(id)).thenReturn(servicio);
        return servicio;
    }

    private void stubServeiNotTrobat(String codi) {
        when(serveiRepository.findByCode(codi)).thenReturn(Collections.emptyList());
    }

    private MutableAcl stubAclNotFound() {
        org.springframework.security.acls.model.NotFoundException nfe =
                mock(org.springframework.security.acls.model.NotFoundException.class);
        when(aclService.readAclById(any())).thenThrow(nfe);
        return null;
    }

    private MutableAcl stubMutableAcl(List<AccessControlEntry> entrades) {
        MutableAcl aclMock = mock(MutableAcl.class);
        when(aclMock.getEntries()).thenReturn(entrades);
        when(aclService.readAclById(any())).thenReturn(aclMock);
        return aclMock;
    }

    // ==================== findById / saveActiu (existents) ====================

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
        Entitat entitat = mock(Entitat.class);
        when(entitat.getCodi()).thenReturn("ENT01");
        when(es.getEntitat()).thenReturn(entitat);

        ProcedimentServei ps = mock(ProcedimentServei.class);
        Procediment proc = mock(Procediment.class);
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
    public void evictCachesPerServei_cridaEvictsCorrectament() {
        when(entitatServeiRepository.findByServei("SV001")).thenReturn(Collections.emptyList());
        when(procedimentServeiRepository.findByServei("SV001")).thenReturn(Collections.emptyList());

        serveiService.evictCachesPerServei("SV001");

        verify(cacheHelper).evictServeis();
    }

    // ==================== scspActualitzarDescripcio / scspDescripcio ====================

    @Test
    public void scspActualitzarDescripcio_servicioTrobat_actualitza() {
        Servicio servicio = new Servicio();
        servicio.setCodCertificado("SV001");
        when(scspHelper.getServicio("SV001")).thenReturn(servicio);

        serveiService.scspActualitzarDescripcio("SV001", "Nova descripció");

        assertEquals("Nova descripció", servicio.getDescripcion());
        verify(scspHelper).saveServicio(servicio);
    }

    @Test
    public void scspActualitzarDescripcio_servicioNoTrobat_noFaRes() {
        when(scspHelper.getServicio("SV001")).thenReturn(null);

        serveiService.scspActualitzarDescripcio("SV001", "Nova descripció");

        verify(scspHelper, never()).saveServicio(any());
    }

    @Test
    public void scspDescripcio_servicioTrobat_retornaDescripcio() {
        Servicio servicio = new Servicio();
        servicio.setDescripcion("Desc SV001");
        when(scspHelper.getServicio("SV001")).thenReturn(servicio);

        assertEquals("Desc SV001", serveiService.scspDescripcio("SV001"));
    }

    @Test
    public void scspDescripcio_servicioNoTrobat_retornaNull() {
        when(scspHelper.getServicio("SV001")).thenReturn(null);

        assertNull(serveiService.scspDescripcio("SV001"));
    }

    // ==================== save ====================

    @Test
    public void save_serveiConfigNoExisteix_creaNouSenseRol() throws ServeiNotFoundException {
        ServeiDto dto = new ServeiDto();
        dto.setCodi("SV001");
        dto.setDescripcio("Servei Ibsalut");
        when(scspHelper.getServicio("SV001")).thenReturn(null);
        when(serveiConfigRepository.findByServei("SV001")).thenReturn(null);

        ServeiDto result = serveiService.save(dto);

        assertNotNull(result);
        verify(serveiConfigRepository).save(any(ServeiConfig.class));
        verify(cacheHelper).evictServeis();
    }

    @Test
    public void save_serveiConfigExisteix_actualitzaSenseCanviRol() throws ServeiNotFoundException {
        ServeiDto dto = new ServeiDto();
        dto.setCodi("SV001");
        dto.setPinbalEntitatTipus(EntitatTipusDto.AJUNTAMENT);
        dto.setPinbalJustificantTipus(JustificantTipusDto.GENERAT);

        ServeiConfig config = mock(ServeiConfig.class);
        when(config.getId()).thenReturn(10L);
        when(config.getRoleName()).thenReturn(null);
        when(config.isUseCertificatEntitat()).thenReturn(false);
        when(serveiConfigRepository.findByServei("SV001")).thenReturn(config);

        ServeiDto result = serveiService.save(dto);

        assertNotNull(result);
        verify(config).update(any(), any(), any(), any(), any(), any(),
                anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(),
                anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(),
                any(), anyBoolean(), anyBoolean(), anyBoolean(), any(), any(),
                anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean());
        verify(config, never()).updateFitxerAjuda(any(), any(), any());
    }

    @Test
    public void save_serveiConfigExisteix_ambFitxerAjuda_actualitzaFitxer() throws ServeiNotFoundException {
        ServeiDto dto = new ServeiDto();
        dto.setCodi("SV001");
        dto.setFitxerAjudaNom("ajuda.pdf");
        dto.setFitxerAjudaMimeType("application/pdf");
        dto.setFitxerAjudaContingut(new byte[] {1, 2, 3});

        ServeiConfig config = mock(ServeiConfig.class);
        when(config.getId()).thenReturn(11L);
        when(config.getRoleName()).thenReturn(null);
        when(serveiConfigRepository.findByServei("SV001")).thenReturn(config);

        serveiService.save(dto);

        verify(config).updateFitxerAjuda("ajuda.pdf", "application/pdf", new byte[] {1, 2, 3});
    }

    @Test
    public void save_useCertificatEntitatCanviat_actualitzaSocsCorrectament() throws ServeiNotFoundException {
        ServeiDto dto = new ServeiDto();
        dto.setCodi("SV001");
        dto.setUseCertificatEntitat(true);

        ServeiConfig config = mock(ServeiConfig.class);
        when(config.getId()).thenReturn(12L);
        when(config.getServei()).thenReturn("SV001");
        when(config.getRoleName()).thenReturn(null);
        when(config.isUseCertificatEntitat()).thenReturn(false);
        when(serveiConfigRepository.findByServei("SV001")).thenReturn(config);
        when(serveiConfigRepository.findById(12L)).thenReturn(Optional.of(config));

        EntitatServei entitatServei = mock(EntitatServei.class);
        Entitat entitat = mock(Entitat.class);
        when(entitat.getCif()).thenReturn("Q0001");
        when(entitatServei.getEntitat()).thenReturn(entitat);
        when(entitatServeiRepository.findByServei("SV001")).thenReturn(Collections.singletonList(entitatServei));

        ClauPrivada clauPrivada = mock(ClauPrivada.class);
        when(clauPrivada.getAlies()).thenReturn("aliesFirma");
        when(clauPrivadaRepository.findTopByOrganismeCifAndPerEntitatTrueOrderByDataAltaDesc("Q0001")).thenReturn(clauPrivada);

        serveiService.save(dto);

        verify(scspHelper).actualitzarServeiOrganismoCesionario("Q0001", "SV001", "aliesFirma");
    }

    @Test
    public void save_useCertificatEntitatCanviat_scspHelperLlancaExcepcio_esCapturaINoPropaga() throws ServeiNotFoundException {
        ServeiDto dto = new ServeiDto();
        dto.setCodi("SV001");
        dto.setUseCertificatEntitat(false);

        ServeiConfig config = mock(ServeiConfig.class);
        when(config.getId()).thenReturn(13L);
        when(config.getServei()).thenReturn("SV001");
        when(config.getRoleName()).thenReturn(null);
        when(config.isUseCertificatEntitat()).thenReturn(true);
        when(serveiConfigRepository.findByServei("SV001")).thenReturn(config);
        when(serveiConfigRepository.findById(13L)).thenReturn(Optional.of(config));

        EntitatServei entitatServei = mock(EntitatServei.class);
        Entitat entitat = mock(Entitat.class);
        when(entitat.getCif()).thenReturn("Q0002");
        when(entitatServei.getEntitat()).thenReturn(entitat);
        when(entitatServeiRepository.findByServei("SV001")).thenReturn(Collections.singletonList(entitatServei));
        doThrow(new RuntimeException("error scsp")).when(scspHelper)
                .actualitzarServeiOrganismoCesionario(anyString(), anyString(), any());

        assertDoesNotThrow(() -> serveiService.save(dto));
    }

    @Test
    public void save_useCertificatEntitatCanviat_serveiConfigNoTrobat_llancaExcepcio() throws ServeiNotFoundException {
        ServeiDto dto = new ServeiDto();
        dto.setCodi("SV001");
        dto.setUseCertificatEntitat(true);

        ServeiConfig config = mock(ServeiConfig.class);
        when(config.getId()).thenReturn(14L);
        when(config.getRoleName()).thenReturn(null);
        when(config.isUseCertificatEntitat()).thenReturn(false);
        when(serveiConfigRepository.findByServei("SV001")).thenReturn(config);
        when(serveiConfigRepository.findById(14L)).thenReturn(Optional.empty());

        assertThrows(ServeiNotFoundException.class, () -> serveiService.save(dto));
    }

    // ==================== delete ====================

    @Test
    public void delete_ambConsultes_llancaExcepcio() {
        stubServicioByCode("SV001", 1L);
        when(scspHelper.servicioHasConsultes("SV001")).thenReturn(true);

        assertThrows(ServeiAmbConsultesException.class, () -> serveiService.delete("SV001"));
    }

    @Test
    public void delete_serveiNoTrobat_llancaExcepcio() {
        stubServeiNotTrobat("SV001");

        assertThrows(ServeiNotFoundException.class, () -> serveiService.delete("SV001"));
    }

    @Test
    public void delete_ok_esborraAssociacionsIConfig() throws Exception {
        stubServicioByCode("SV001", 1L);
        when(scspHelper.servicioHasConsultes("SV001")).thenReturn(false);

        ProcedimentServei ps = mock(ProcedimentServei.class);
        when(ps.getId()).thenReturn(200L);
        Procediment procediment = mock(Procediment.class);
        when(procediment.getCodi()).thenReturn("PROC01");
        when(procediment.getServeis()).thenReturn(new ArrayList<>());
        when(ps.getProcediment()).thenReturn(procediment);
        when(procedimentServeiRepository.findByServei("SV001")).thenReturn(Collections.singletonList(ps));

        EntitatServei es = mock(EntitatServei.class);
        when(es.getId()).thenReturn(300L);
        Entitat entitat = mock(Entitat.class);
        when(entitat.getCodi()).thenReturn("ENT01");
        when(entitat.getCif()).thenReturn("Q9999");
        when(entitat.getServeis()).thenReturn(new ArrayList<>());
        when(es.getEntitat()).thenReturn(entitat);
        when(entitatServeiRepository.findByServei("SV001")).thenReturn(Collections.singletonList(es));
        when(entitatServeiRepository.findByEntitat(entitat)).thenReturn(Collections.emptyList());

        ServeiConfig config = mock(ServeiConfig.class);
        when(serveiConfigRepository.findByServei("SV001")).thenReturn(config);

        stubAclNotFound();

        ServeiDto result = serveiService.delete("SV001");

        assertNotNull(result);
        verify(procedimentServeiRepository).deleteAll(Collections.singletonList(ps));
        verify(entitatServeiRepository).deleteById(300L);
        verify(serveiConfigRepository).delete(config);
        verify(cacheHelper).evictServeis();
        verify(cacheHelper).evictDadesEspecifiques("SV001");
    }

    @Test
    public void delete_serveiConfigNoExisteix_noEsborraConfig() throws Exception {
        stubServicioByCode("SV001", 1L);
        when(scspHelper.servicioHasConsultes("SV001")).thenReturn(false);
        when(procedimentServeiRepository.findByServei("SV001")).thenReturn(Collections.emptyList());
        when(entitatServeiRepository.findByServei("SV001")).thenReturn(Collections.emptyList());
        when(serveiConfigRepository.findByServei("SV001")).thenReturn(null);

        serveiService.delete("SV001");

        verify(serveiConfigRepository, never()).delete(any());
    }

    // ==================== findAmbCodiPerAdminORepresentant / findAmbCodiPerDelegat ====================

    @Test
    public void findAmbCodiPerAdminORepresentant_retornaDto() throws ServeiNotFoundException {
        stubServicioByCode("SV001", 1L);

        ServeiDto result = serveiService.findAmbCodiPerAdminORepresentant("SV001");

        assertEquals("SV001", result.getCodi());
    }

    @Test
    public void findAmbCodiPerDelegat_senseRestriccio_retornaDto() throws ServeiNotFoundException {
        stubServicioByCode("SV001", 1L);
        when(serveiConfigRepository.findByServei("SV001")).thenReturn(null);

        ServeiDto result = serveiService.findAmbCodiPerDelegat(1L, "SV001");

        assertEquals("SV001", result.getCodi());
    }

    @Test
    public void findAmbCodiPerDelegat_ambRolIPermisConcedit_retornaDto() throws ServeiNotFoundException {
        stubServicioByCode("SV001", 1L);
        ServeiConfig config = mock(ServeiConfig.class);
        when(config.getId()).thenReturn(20L);
        when(config.getRoleName()).thenReturn("ROLE_TEST");
        when(serveiConfigRepository.findByServei("SV001")).thenReturn(config);

        MutableAcl aclMock = mock(MutableAcl.class);
        when(aclMock.isGranted(anyList(), anyList(), eq(false))).thenReturn(true);
        when(aclService.readAclById(any())).thenReturn(aclMock);

        ServeiDto result = serveiService.findAmbCodiPerDelegat(1L, "SV001");

        assertEquals("SV001", result.getCodi());
    }

    @Test
    public void findAmbCodiPerDelegat_ambRolIPermisDenegat_llancaExcepcio() {
        stubServicioByCode("SV001", 1L);
        ServeiConfig config = mock(ServeiConfig.class);
        when(config.getId()).thenReturn(21L);
        when(config.getRoleName()).thenReturn("ROLE_TEST");
        when(serveiConfigRepository.findByServei("SV001")).thenReturn(config);
        stubAclNotFound();

        assertThrows(ServeiNotFoundException.class, () -> serveiService.findAmbCodiPerDelegat(1L, "SV001"));
    }

    // ==================== findActius ====================

    @Test
    public void findActius_senseFiltre_retornaServeisConfigurats() {
        Servicio s1 = new Servicio();
        s1.setCodCertificado("SV001");
        s1.setDescripcion("D1");
        Servicio s2 = new Servicio();
        s2.setCodCertificado("SV002");
        s2.setDescripcion("D2");
        when(scspHelper.findServicioAll()).thenReturn(Arrays.asList(s1, s2));
        when(serveiConfigRepository.findAllCodis()).thenReturn(Collections.singletonList("SV001"));

        List<ServeiDto> result = serveiService.findActius();

        assertEquals(1, result.size());
        assertEquals("SV001", result.get(0).getCodi());
    }

    @Test
    public void findActius_ambFiltreBuit_retornaTots() {
        Servicio s1 = new Servicio();
        s1.setCodCertificado("SV001");
        s1.setDescripcion("Descripcio 1");
        when(scspHelper.findServicioAll()).thenReturn(Collections.singletonList(s1));

        List<ServeiDto> result = serveiService.findActius("");

        assertEquals(1, result.size());
    }

    @Test
    public void findActius_ambFiltreQueCoincideix_retornaCoincident() {
        Servicio s1 = new Servicio();
        s1.setCodCertificado("SV001");
        s1.setDescripcion("Padro habitants");
        Servicio s2 = new Servicio();
        s2.setCodCertificado("SV002");
        s2.setDescripcion("Renda");
        when(scspHelper.findServicioAll()).thenReturn(Arrays.asList(s1, s2));

        List<ServeiDto> result = serveiService.findActius("padro");

        assertEquals(1, result.size());
        assertEquals("SV001", result.get(0).getCodi());
    }

    @Test
    public void findActius_ambFiltreSenseCoincidencia_retornaBuit() {
        Servicio s1 = new Servicio();
        s1.setCodCertificado("SV001");
        s1.setDescripcion("Renda");
        when(scspHelper.findServicioAll()).thenReturn(Collections.singletonList(s1));

        List<ServeiDto> result = serveiService.findActius("inexistent");

        assertTrue(result.isEmpty());
    }

    // ==================== findAmbFiltrePaginat (1r overload) ====================

    @Test
    public void findAmbFiltrePaginat_simple_serveiActiu() {
        Pageable pageable = PageRequest.of(0, 10);
        Servei servei = mock(Servei.class);
        Page<Servei> pageEntities = new PageImpl<>(Collections.singletonList(servei), pageable, 1);
        when(serveiRepository.findByFiltre(anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(),
                anyBoolean(), any(), anyBoolean(), any(), eq(pageable))).thenReturn(pageEntities);

        ServeiDto dto = new ServeiDto();
        dto.setCodi("SV001");
        Page<ServeiDto> pageDtos = new PageImpl<>(Collections.singletonList(dto), pageable, 1);
        when(dtoMappingHelper.pageEntities2pageDto(pageEntities, ServeiDto.class, pageable)).thenReturn(pageDtos);

        ServeiConfig config = mock(ServeiConfig.class);
        when(config.isActiu()).thenReturn(true);
        when(serveiConfigRepository.findByServei("SV001")).thenReturn(config);

        Page<ServeiDto> result = serveiService.findAmbFiltrePaginat("SV001", null, null, true, null, pageable);

        assertTrue(result.getContent().get(0).getActiu());
    }

    @Test
    public void findAmbFiltrePaginat_simple_serveiSenseConfig_esInactiu() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Servei> pageEntities = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(serveiRepository.findByFiltre(anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(),
                anyBoolean(), any(), anyBoolean(), any(), eq(pageable))).thenReturn(pageEntities);

        ServeiDto dto = new ServeiDto();
        dto.setCodi("SV002");
        Page<ServeiDto> pageDtos = new PageImpl<>(Collections.singletonList(dto), pageable, 1);
        when(dtoMappingHelper.pageEntities2pageDto(pageEntities, ServeiDto.class, pageable)).thenReturn(pageDtos);
        when(serveiConfigRepository.findByServei("SV002")).thenReturn(null);

        Page<ServeiDto> result = serveiService.findAmbFiltrePaginat(null, null, "3", null, "1.0", pageable);

        assertFalse(result.getContent().get(0).getActiu());
    }

    // ==================== findAmbFiltrePaginat (2n overload) ====================

    @Test
    public void findAmbFiltrePaginat_ambEntitatIProcediment_serveisEntitatBuit_retornaPaginaBuida() {
        EntitatDto entitatDto = new EntitatDto();
        entitatDto.setId(1L);
        ProcedimentDto procedimentDto = new ProcedimentDto();
        procedimentDto.setId(2L);
        Pageable pageable = PageRequest.of(0, 10);
        when(entitatServeiRepository.findServeisByEntitatId(1L)).thenReturn(Collections.emptyList());

        Page<ServeiDto> result = serveiService.findAmbFiltrePaginat(
                null, null, null, null, entitatDto, procedimentDto, pageable);

        assertEquals(0, result.getTotalElements());
    }

    @Test
    public void findAmbFiltrePaginat_ambEntitatIProcediment_ple_calculaPermisosIActiu() {
        EntitatDto entitatDto = new EntitatDto();
        entitatDto.setId(1L);
        entitatDto.setCodi("ENT01");
        ProcedimentDto procedimentDto = new ProcedimentDto();
        procedimentDto.setId(2L);
        Pageable pageable = PageRequest.of(0, 10);

        when(entitatServeiRepository.findServeisByEntitatId(1L)).thenReturn(Collections.singletonList("SV001"));

        Entitat entitatEntity = mock(Entitat.class);
        Procediment procedimentEntity = mock(Procediment.class);
        when(entitatRepository.findByCodi("ENT01")).thenReturn(entitatEntity);
        when(procedimentRepository.findById(2L)).thenReturn(Optional.of(procedimentEntity));
        when(procedimentServeiRepository.findServeisProcedimenActiustServeisIds(entitatEntity, procedimentEntity))
                .thenReturn(Collections.singletonList("SV001"));

        Servei serveiEntity = mock(Servei.class);
        Page<Servei> pageEntities = new PageImpl<>(Collections.singletonList(serveiEntity), pageable, 1);
        when(serveiRepository.findByFiltre(anyList(), any(), anyBoolean(), any(), anyBoolean(), any(),
                anyBoolean(), any(), anyBoolean(), any(), eq(pageable))).thenReturn(pageEntities);

        ProcedimentServei ps = mock(ProcedimentServei.class);
        when(ps.getId()).thenReturn(100L);
        when(ps.getServei()).thenReturn("SV001");
        Procediment procDetall = mock(Procediment.class);
        when(procDetall.getCodi()).thenReturn("PROC01");
        when(ps.getProcediment()).thenReturn(procDetall);
        when(procedimentServeiRepository.findServeisProcediment(entitatEntity, procedimentEntity))
                .thenReturn(Collections.singletonList(ps));

        ServeiConfig config = mock(ServeiConfig.class);
        when(config.getServei()).thenReturn("SV001");
        when(config.isActiu()).thenReturn(true);
        when(serveiConfigRepository.findByServeiIn(Collections.singletonList("SV001")))
                .thenReturn(Collections.singletonList(config));

        ServeiDto dto = new ServeiDto();
        dto.setCodi("SV001");
        Page<ServeiDto> pageDtos = new PageImpl<>(Collections.singletonList(dto), pageable, 1);
        when(dtoMappingHelper.pageEntities2pageDto(pageEntities, ServeiDto.class, pageable)).thenReturn(pageDtos);

        Acl aclCommon = mock(Acl.class);
        Sid sidCommon = mock(Sid.class);
        AccessControlEntry ace1 = mock(AccessControlEntry.class);
        when(ace1.getAcl()).thenReturn(aclCommon);
        when(ace1.getSid()).thenReturn(sidCommon);
        when(ace1.getPermission()).thenReturn(BasePermission.READ);
        AccessControlEntry ace2 = mock(AccessControlEntry.class);
        when(ace2.getAcl()).thenReturn(aclCommon);
        when(ace2.getSid()).thenReturn(sidCommon);
        when(ace2.getPermission()).thenReturn(BasePermission.READ);
        stubMutableAcl(Arrays.asList(ace1, ace2));

        Page<ServeiDto> result = serveiService.findAmbFiltrePaginat(
                null, null, null, null, entitatDto, procedimentDto, pageable);

        ServeiDto resultat = result.getContent().get(0);
        assertTrue(resultat.getActiu());
        assertEquals("PROC01", resultat.getProcedimentCodi());
        assertEquals(1, resultat.getUsuarisAmbPermis());
    }

    // ==================== findAmbEntitat ====================

    @Test
    public void findAmbEntitat_entitatNoTrobada_llancaExcepcio() {
        when(entitatRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntitatNotFoundException.class, () -> serveiService.findAmbEntitat(99L));
    }

    @Test
    public void findAmbEntitat_ok_filtraServeisDeEntitat() throws EntitatNotFoundException {
        Entitat entitat = mock(Entitat.class);
        when(entitat.getServeis()).thenReturn(Collections.singletonList("SV001"));
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitat));

        Servicio s1 = new Servicio();
        s1.setCodCertificado("SV001");
        s1.setDescripcion("D1");
        Servicio s2 = new Servicio();
        s2.setCodCertificado("SV002");
        s2.setDescripcion("D2");
        when(scspHelper.findServicioAll()).thenReturn(Arrays.asList(s1, s2));

        List<ServeiDto> result = serveiService.findAmbEntitat(1L);

        assertEquals(1, result.size());
        assertEquals("SV001", result.get(0).getCodi());
    }

    @Test
    public void findAmbEntitatAmbFiltre_entitatNoTrobada_llancaExcepcio() {
        when(entitatRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntitatNotFoundException.class, () -> serveiService.findAmbEntitat(99L, "x"));
    }

    @Test
    public void findAmbEntitatAmbFiltre_ok_filtraPerText() throws EntitatNotFoundException {
        Entitat entitat = mock(Entitat.class);
        when(entitat.getServeis()).thenReturn(Collections.singletonList("SV001"));
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitat));

        Servicio s1 = new Servicio();
        s1.setCodCertificado("SV001");
        s1.setDescripcion("Padro habitants");
        when(scspHelper.findServicioAll()).thenReturn(Collections.singletonList(s1));

        List<ServeiDto> result = serveiService.findAmbEntitat(1L, "padro");

        assertEquals(1, result.size());
    }

    // ==================== findAmbEntitatIProcediment / findAmbProcediment ====================

    @Test
    public void findAmbEntitatIProcediment_entitatNoTrobada_llancaExcepcio() {
        when(entitatRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntitatNotFoundException.class, () -> serveiService.findAmbEntitatIProcediment(99L, 1L));
    }

    @Test
    public void findAmbEntitatIProcediment_procedimentNoTrobat_llancaExcepcio() {
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(mock(Entitat.class)));
        when(procedimentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProcedimentNotFoundException.class, () -> serveiService.findAmbEntitatIProcediment(1L, 99L));
    }

    @Test
    public void findAmbEntitatIProcediment_ok() throws Exception {
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(mock(Entitat.class)));
        when(procedimentRepository.findById(2L)).thenReturn(Optional.of(mock(Procediment.class)));
        ProcedimentServei ps = mock(ProcedimentServei.class);
        when(ps.getServei()).thenReturn("SV001");
        when(procedimentServeiRepository.findByEntitatIdAndProcedimentId(1L, 2L))
                .thenReturn(Collections.singletonList(ps));

        Servicio s1 = new Servicio();
        s1.setCodCertificado("SV001");
        s1.setDescripcion("D1");
        when(scspHelper.findServicioAll()).thenReturn(Collections.singletonList(s1));

        List<ServeiDto> result = serveiService.findAmbEntitatIProcediment(1L, 2L);

        assertEquals(1, result.size());
    }

    @Test
    public void findAmbEntitatIProcedimentAmbFiltre_ok() throws Exception {
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(mock(Entitat.class)));
        when(procedimentRepository.findById(2L)).thenReturn(Optional.of(mock(Procediment.class)));
        ProcedimentServei ps = mock(ProcedimentServei.class);
        when(ps.getServei()).thenReturn("SV001");
        when(procedimentServeiRepository.findByEntitatIdAndProcedimentId(1L, 2L))
                .thenReturn(Collections.singletonList(ps));

        Servicio s1 = new Servicio();
        s1.setCodCertificado("SV001");
        s1.setDescripcion("Padro habitants");
        when(scspHelper.findServicioAll()).thenReturn(Collections.singletonList(s1));

        List<ServeiDto> result = serveiService.findAmbEntitatIProcediment(1L, 2L, "padro");

        assertEquals(1, result.size());
    }

    @Test
    public void findAmbEntitatIProcedimentAmbFiltre_entitatNoTrobada_llancaExcepcio() {
        when(entitatRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntitatNotFoundException.class,
                () -> serveiService.findAmbEntitatIProcediment(99L, 1L, "x"));
    }

    @Test
    public void findAmbEntitatIProcedimentAmbFiltre_procedimentNoTrobat_llancaExcepcio() {
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(mock(Entitat.class)));
        when(procedimentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProcedimentNotFoundException.class,
                () -> serveiService.findAmbEntitatIProcediment(1L, 99L, "x"));
    }

    @Test
    public void findAmbProcediment_procedimentNoTrobat_llancaExcepcio() {
        when(procedimentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProcedimentNotFoundException.class, () -> serveiService.findAmbProcediment(99L));
    }

    @Test
    public void findAmbProcediment_ok() throws ProcedimentNotFoundException {
        when(procedimentRepository.findById(2L)).thenReturn(Optional.of(mock(Procediment.class)));
        ProcedimentServei ps = mock(ProcedimentServei.class);
        when(ps.getServei()).thenReturn("SV001");
        when(procedimentServeiRepository.findByProcedimentId(2L)).thenReturn(Collections.singletonList(ps));

        Servicio s1 = new Servicio();
        s1.setCodCertificado("SV001");
        s1.setDescripcion("D1");
        when(scspHelper.findServicioAll()).thenReturn(Collections.singletonList(s1));

        List<ServeiDto> result = serveiService.findAmbProcediment(2L);

        assertEquals(1, result.size());
    }

    // ==================== findAmbEntitatNotInProcediment ====================

    @Test
    public void findAmbEntitatNotInProcediment_entitatNoTrobada_llancaExcepcio() {
        when(entitatRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntitatNotFoundException.class,
                () -> serveiService.findAmbEntitatNotInProcediment(99L, 1L));
    }

    @Test
    public void findAmbEntitatNotInProcediment_procedimentNoTrobat_llancaExcepcio() {
        Entitat entitat = mock(Entitat.class);
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitat));
        when(procedimentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProcedimentNotFoundException.class,
                () -> serveiService.findAmbEntitatNotInProcediment(1L, 99L));
    }

    @Test
    public void findAmbEntitatNotInProcediment_ok_excluSeveisJaAssociats() throws Exception {
        Entitat entitat = mock(Entitat.class);
        when(entitat.getId()).thenReturn(1L);
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitat));
        when(procedimentRepository.findById(2L)).thenReturn(Optional.of(mock(Procediment.class)));

        ProcedimentServei psAssociat = mock(ProcedimentServei.class);
        when(psAssociat.getServei()).thenReturn("SV001");
        when(procedimentServeiRepository.findByEntitatIdAndProcedimentId(1L, 2L))
                .thenReturn(Collections.singletonList(psAssociat));

        when(entitatServeiRepository.findServeisByEntitatId(1L))
                .thenReturn(new ArrayList<>(Arrays.asList("SV001", "SV002")));

        Servicio s1 = new Servicio();
        s1.setCodCertificado("SV001");
        s1.setDescripcion("D1");
        Servicio s2 = new Servicio();
        s2.setCodCertificado("SV002");
        s2.setDescripcion("D2");
        when(scspHelper.findServicioAll()).thenReturn(Arrays.asList(s1, s2));

        List<ServeiDto> result = serveiService.findAmbEntitatNotInProcediment(1L, 2L);

        assertEquals(1, result.size());
        assertEquals("SV002", result.get(0).getCodi());
    }

    // ==================== findPermesosAmbEntitatIUsuari / countPermesosAmbEntitatIUsuari ====================

    @Test
    public void findPermesosAmbEntitatIUsuari_entitatNoTrobada_llancaExcepcio() {
        when(entitatRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntitatNotFoundException.class,
                () -> serveiService.findPermesosAmbEntitatIUsuari(99L, "usuari1"));
    }

    @Test
    public void findPermesosAmbEntitatIUsuari_ok_retornaPermesos() throws EntitatNotFoundException {
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(mock(Entitat.class)));

        ProcedimentServei ps = mock(ProcedimentServei.class);
        when(ps.getId()).thenReturn(50L);
        when(ps.getServei()).thenReturn("SV001");
        Procediment proc = mock(Procediment.class);
        when(ps.getProcediment()).thenReturn(proc);
        when(procedimentServeiRepository.findByEntitatId(1L)).thenReturn(new ArrayList<>(Collections.singletonList(ps)));

        when(usuariHelper.generarUsuariAutenticat("usuari1", false)).thenReturn(auth);
        MutableAcl aclMock = mock(MutableAcl.class);
        when(aclMock.isGranted(anyList(), anyList(), eq(false))).thenReturn(true);
        when(aclMock.getEntries()).thenReturn(Collections.emptyList());
        when(aclService.readAclById(any())).thenReturn(aclMock);

        Servicio servicio = new Servicio();
        servicio.setCodCertificado("SV001");
        when(scspHelper.getServicio("SV001")).thenReturn(servicio);

        List<es.caib.pinbal.logic.intf.dto.ProcedimentServeiDto> result =
                serveiService.findPermesosAmbEntitatIUsuari(1L, "usuari1");

        assertEquals(1, result.size());
    }

    @Test
    public void countPermesosAmbEntitatIUsuari_llistaBuida_retornaZero() {
        when(procedimentServeiRepository.findByEntitatId(1L)).thenReturn(Collections.emptyList());

        assertEquals(0, serveiService.countPermesosAmbEntitatIUsuari(1L, "usuari1"));
    }

    @Test
    public void countPermesosAmbEntitatIUsuari_ambPermisConcedit_retornaCount() {
        ProcedimentServei ps = mock(ProcedimentServei.class);
        when(ps.getId()).thenReturn(51L);
        when(procedimentServeiRepository.findByEntitatId(1L)).thenReturn(new ArrayList<>(Collections.singletonList(ps)));

        when(usuariHelper.generarUsuariAutenticat("usuari1", false)).thenReturn(auth);
        MutableAcl aclMock = mock(MutableAcl.class);
        when(aclMock.isGranted(anyList(), anyList(), eq(false))).thenReturn(true);
        when(aclService.readAclById(any())).thenReturn(aclMock);

        assertEquals(1, serveiService.countPermesosAmbEntitatIUsuari(1L, "usuari1"));
    }

    // ==================== findPermesosAmbProcedimentPerDelegat / getServeiPermesosPerDelegat ====================

    @Test
    public void findPermesosAmbProcedimentPerDelegat_delegaASelf() throws Exception {
        ReflectionTestUtils.setField(serveiService, "self", self);
        when(self.getServeiPermesosPerDelegat(eq(1L), eq(2L), any())).thenReturn(Collections.emptyList());

        List<ServeiDto> result = serveiService.findPermesosAmbProcedimentPerDelegat(1L, 2L);

        assertTrue(result.isEmpty());
        verify(self).getServeiPermesosPerDelegat(1L, 2L, auth);
    }

    @Test
    public void getServeiPermesosPerDelegat_entitatNoExisteix_llancaException() {
        when(entitatRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntitatNotFoundException.class,
                () -> serveiService.getServeiPermesosPerDelegat(99L, 1L, null));
    }

    @Test
    public void getServeiPermesosPerDelegat_procedimentNoExisteix_llancaException() {
        Entitat entitat = mock(Entitat.class);
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitat));
        when(procedimentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProcedimentNotFoundException.class,
                () -> serveiService.getServeiPermesosPerDelegat(1L, 99L, null));
    }

    @Test
    public void getServeiPermesosPerDelegat_delegatActiu_retornaServeisPermesos() throws Exception {
        Entitat entitat = mock(Entitat.class);
        when(entitat.getId()).thenReturn(1L);
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitat));
        Procediment procediment = mock(Procediment.class);
        when(procediment.getCodi()).thenReturn("PROC01");
        when(procedimentRepository.findById(2L)).thenReturn(Optional.of(procediment));

        EntitatUsuari entitatUsuari = mock(EntitatUsuari.class);
        when(entitatUsuari.isDelegat()).thenReturn(true);
        when(entitatUsuari.isActiu()).thenReturn(true);
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(1L, "usuari1")).thenReturn(entitatUsuari);

        when(serveiHelper.findServeisPermesosPerUsuari(1L, "PROC01", auth))
                .thenReturn(Collections.singletonList("SV001"));

        Servicio servicio = new Servicio();
        servicio.setCodCertificado("SV001");
        when(scspHelper.getServicio("SV001")).thenReturn(servicio);

        List<ServeiDto> result = serveiService.getServeiPermesosPerDelegat(1L, 2L, auth);

        assertEquals(1, result.size());
    }

    @Test
    public void getServeiPermesosPerDelegat_noDelegat_retornaBuit() throws Exception {
        Entitat entitat = mock(Entitat.class);
        when(entitat.getId()).thenReturn(1L);
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitat));
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(1L, "usuari1")).thenReturn(null);

        List<ServeiDto> result = serveiService.getServeiPermesosPerDelegat(1L, null, auth);

        assertTrue(result.isEmpty());
    }

    // ==================== findEmisorAll / findClauPublicaAll / findClauPrivadaAll ====================

    @Test
    public void findEmisorAll_retornaLlista() {
        EmisorCertificado emisor = new EmisorCertificado();
        emisor.setId(1L);
        emisor.setNombre("Emisor1");
        emisor.setCif("Q1111");
        when(scspHelper.findEmisorCertificadoAll()).thenReturn(Collections.singletonList(emisor));

        List<EmisorDto> result = serveiService.findEmisorAll();

        assertEquals(1, result.size());
        assertEquals("Emisor1", result.get(0).getNom());
    }

    @Test
    public void findClauPublicaAll_ignoraNulls() {
        ClavePublica cp = new ClavePublica();
        cp.setAlias("alies1");
        cp.setNombre("Clau1");
        cp.setNumeroSerie("123");
        when(scspHelper.findClavePublicaAll()).thenReturn(Arrays.asList(cp, null));

        List<ClauPublicaDto> result = serveiService.findClauPublicaAll();

        assertEquals(1, result.size());
    }

    @Test
    public void findClauPrivadaAll_ignoraNulls() {
        ClavePrivada cp = new ClavePrivada();
        cp.setAlias("alies1");
        cp.setNombre("Clau1");
        cp.setNumeroSerie("123");
        when(scspHelper.findClavePrivadaAll()).thenReturn(Arrays.asList(null, cp));

        List<ClauPrivadaDto> result = serveiService.findClauPrivadaAll();

        assertEquals(1, result.size());
    }

    // ==================== generarArbreDadesEspecifiques ====================

    private Tree<DadesEspecifiquesNode> arbreSimple(String nom, boolean enumerat) {
        DadesEspecifiquesNode dades = new DadesEspecifiquesNode();
        dades.setNom(nom);
        if (enumerat) {
            dades.addEnumValue("A");
            dades.addEnumValue("B");
        }
        Node<DadesEspecifiquesNode> root = new Node<>(dades);
        Tree<DadesEspecifiquesNode> tree = new Tree<>();
        tree.setRootElement(root);
        return tree;
    }

    @Test
    public void generarArbreDadesEspecifiques_serveiNoTrobat_llancaExcepcio() {
        stubServeiNotTrobat("SV001");

        assertThrows(ServeiNotFoundException.class, () -> serveiService.generarArbreDadesEspecifiques("SV001"));
    }

    @Test
    public void generarArbreDadesEspecifiques_ok_generaArbre() throws Exception {
        stubServicioByCode("SV001", 1L);
        when(serveiConfigRepository.findByServei("SV001")).thenReturn(null);
        when(scspHelper.generarArbreDadesEspecifiques("SV001", false)).thenReturn(arbreSimple("camp1", false));

        ArbreDto<DadaEspecificaDto> arbre = serveiService.generarArbreDadesEspecifiques("SV001");

        assertNotNull(arbre.getArrel());
        assertEquals("camp1", arbre.getArrel().getDades().getNom());
    }

    @Test
    public void generarArbreDadesEspecifiques_excepcio_llancaScspException() throws Exception {
        stubServicioByCode("SV001", 1L);
        when(serveiConfigRepository.findByServei("SV001")).thenReturn(null);
        when(scspHelper.generarArbreDadesEspecifiques("SV001", false)).thenThrow(new RuntimeException("boom"));

        assertThrows(ScspException.class, () -> serveiService.generarArbreDadesEspecifiques("SV001"));
    }

    // ==================== createServeiCamp ====================

    @Test
    public void createServeiCamp_serveiNoTrobat_llancaExcepcio() {
        stubServeiNotTrobat("SV001");

        assertThrows(ServeiNotFoundException.class, () -> serveiService.createServeiCamp("SV001", "camp1"));
    }

    @Test
    public void createServeiCamp_campJaExisteix_noLoTornaACrear() throws ServeiNotFoundException {
        stubServicioByCode("SV001", 1L);
        ServeiCamp existent = ServeiCamp.getBuilder("SV001", "camp1", 0, 6).build();
        when(serveiCampRepository.findByServeiAndGrupOrderByOrdreAsc("SV001", true, null))
                .thenReturn(Collections.singletonList(existent));
        when(serveiCampRepository.save(existent)).thenReturn(existent);

        ServeiCampDto dto = new ServeiCampDto();
        when(mapperFacade.map(existent, ServeiCampDto.class)).thenReturn(dto);

        ServeiCampDto result = serveiService.createServeiCamp("SV001", "camp1");

        assertNotNull(result);
        verify(serveiCampRepository, never()).save(argThat(c -> c != existent));
    }

    @Test
    public void createServeiCamp_nouCampNoEnum_esCrea() throws Exception {
        stubServicioByCode("SV001", 1L);
        when(serveiCampRepository.findByServeiAndGrupOrderByOrdreAsc("SV001", true, null))
                .thenReturn(Collections.emptyList());
        when(serveiConfigRepository.findByServei("SV001")).thenReturn(null);
        when(scspHelper.generarArbreDadesEspecifiques("SV001", false)).thenReturn(arbreSimple("altre", false));
        when(serveiCampRepository.save(any(ServeiCamp.class))).thenAnswer(inv -> inv.getArgument(0));

        ServeiCampDto result = serveiService.createServeiCamp("SV001", "camp1");

        assertNotNull(result);
        verify(cacheHelper).evictDadesEspecifiques("SV001");
        verify(cacheHelper, never()).evictEnumeratsPerServei(any());
    }

    @Test
    public void createServeiCamp_nouCampEnum_esCreaAmbTipusEnum() throws Exception {
        stubServicioByCode("SV001", 1L);
        when(serveiCampRepository.findByServeiAndGrupOrderByOrdreAsc("SV001", true, null))
                .thenReturn(Collections.emptyList());
        when(serveiConfigRepository.findByServei("SV001")).thenReturn(null);
        when(scspHelper.generarArbreDadesEspecifiques("SV001", false)).thenReturn(arbreSimple("camp1", true));
        when(serveiCampRepository.save(any(ServeiCamp.class))).thenAnswer(inv -> inv.getArgument(0));

        ServeiCampDto result = serveiService.createServeiCamp("SV001", "camp1");

        assertNotNull(result);
        verify(cacheHelper).evictEnumeratsPerServei("SV001");
    }

    @Test
    public void createServeiCamp_errorGenerantArbreEnum_esCapturaIContinua() throws Exception {
        stubServicioByCode("SV001", 1L);
        when(serveiCampRepository.findByServeiAndGrupOrderByOrdreAsc("SV001", true, null))
                .thenReturn(Collections.emptyList());
        when(serveiConfigRepository.findByServei("SV001")).thenReturn(null);
        when(scspHelper.generarArbreDadesEspecifiques("SV001", false))
                .thenReturn(arbreSimple("camp1", true))
                .thenThrow(new RuntimeException("boom"));
        when(serveiCampRepository.save(any(ServeiCamp.class))).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> serveiService.createServeiCamp("SV001", "camp1"));
    }

    // ==================== updateServeiCamp ====================

    @Test
    public void updateServeiCamp_noTrobat_llancaExcepcio() {
        when(serveiCampRepository.findById(99L)).thenReturn(Optional.empty());

        ServeiCampDto dto = new ServeiCampDto();
        dto.setId(99L);

        assertThrows(ServeiCampNotFoundException.class, () -> serveiService.updateServeiCamp(dto));
    }

    @Test
    public void updateServeiCamp_ok_ambCampPareIValidacio() throws ServeiCampNotFoundException {
        ServeiCamp campExistent = ServeiCamp.getBuilder("SV001", "camp1", 0, 6).build();
        when(serveiCampRepository.findById(1L)).thenReturn(Optional.of(campExistent));
        ServeiCamp campPare = ServeiCamp.getBuilder("SV001", "pare", 0, 6).build();
        when(serveiCampRepository.findById(2L)).thenReturn(Optional.of(campPare));
        ServeiCamp campValidacio = ServeiCamp.getBuilder("SV001", "val2", 0, 6).build();
        when(serveiCampRepository.findById(3L)).thenReturn(Optional.of(campValidacio));

        ServeiCampDto dto = new ServeiCampDto();
        dto.setId(1L);
        dto.setTipus(ServeiCampDtoTipus.TEXT);
        dto.setMida(10);
        ServeiCampDto pareDto = new ServeiCampDto();
        pareDto.setId(2L);
        dto.setCampPare(pareDto);
        ServeiCampDto validacioDto = new ServeiCampDto();
        validacioDto.setId(3L);
        dto.setValidacioDataCmpCamp2(validacioDto);

        ServeiCampDto result = serveiService.updateServeiCamp(dto);

        assertNotNull(result);
        verify(cacheHelper).evictDadesEspecifiques("SV001");
        verify(cacheHelper, never()).evictEnumeratsPerServei(any());
    }

    @Test
    public void updateServeiCamp_ambTipusEnum_evictaEnumerats() throws ServeiCampNotFoundException {
        ServeiCamp campExistent = ServeiCamp.getBuilder("SV001", "camp1", 0, 6).build();
        when(serveiCampRepository.findById(1L)).thenReturn(Optional.of(campExistent));

        ServeiCampDto dto = new ServeiCampDto();
        dto.setId(1L);
        dto.setTipus(ServeiCampDtoTipus.ENUM);
        dto.setMida(10);

        serveiService.updateServeiCamp(dto);

        verify(cacheHelper).evictEnumeratsPerServei("SV001");
    }

    // ==================== deleteServeiCamp ====================

    @Test
    public void deleteServeiCamp_noExisteix_llancaException() {
        when(serveiCampRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ServeiCampNotFoundException.class, () -> serveiService.deleteServeiCamp(99L));
    }

    @Test
    public void deleteServeiCamp_ok_reordenaIDeslligaFills() throws ServeiCampNotFoundException {
        ServeiCamp perEsborrar = ServeiCamp.getBuilder("SV001", "camp1", 0, 6).build();
        when(serveiCampRepository.findById(1L)).thenReturn(Optional.of(perEsborrar));
        ServeiCamp altre = ServeiCamp.getBuilder("SV001", "camp2", 1, 6).build();
        when(serveiCampRepository.findByServeiAndGrupOrderByOrdreAsc("SV001", true, null))
                .thenReturn(new ArrayList<>(Collections.singletonList(altre)));

        ServeiCampDto result = serveiService.deleteServeiCamp(1L);

        assertNotNull(result);
        verify(serveiCampRepository).delete(perEsborrar);
        verify(cacheHelper).evictDadesEspecifiques("SV001");
    }

    // ==================== moveServeiCamp ====================

    @Test
    public void moveServeiCamp_noTrobat_llancaExcepcio() {
        when(serveiCampRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ServeiCampNotFoundException.class, () -> serveiService.moveServeiCamp("SV001", 99L, 0));
    }

    @Test
    public void moveServeiCamp_indexOrigenForaDeRang_llancaExcepcio() {
        ServeiCamp camp = ServeiCamp.getBuilder("SV001", "camp1", 5, 6).build();
        when(serveiCampRepository.findById(1L)).thenReturn(Optional.of(camp));
        when(serveiCampRepository.findByServeiAndGrupOrderByOrdreAsc("SV001", true, null))
                .thenReturn(Collections.emptyList());

        assertThrows(ServeiCampNotFoundException.class, () -> serveiService.moveServeiCamp("SV001", 1L, 0));
    }

    @Test
    public void moveServeiCamp_ok_reordena() throws ServeiCampNotFoundException {
        ServeiCamp camp0 = ServeiCamp.getBuilder("SV001", "camp0", 0, 6).build();
        ServeiCamp camp1 = ServeiCamp.getBuilder("SV001", "camp1", 1, 6).build();
        when(serveiCampRepository.findById(1L)).thenReturn(Optional.of(camp0));
        when(serveiCampRepository.findByServeiAndGrupOrderByOrdreAsc("SV001", true, null))
                .thenReturn(new ArrayList<>(Arrays.asList(camp0, camp1)));

        assertDoesNotThrow(() -> serveiService.moveServeiCamp("SV001", 1L, 1));
    }

    // ==================== agrupaServeiCamp ====================

    @Test
    public void agrupaServeiCamp_campNoTrobat_llancaExcepcio() {
        when(serveiCampRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ServeiCampNotFoundException.class, () -> serveiService.agrupaServeiCamp(99L, 1L));
    }

    @Test
    public void agrupaServeiCamp_grupNoTrobat_llancaExcepcio() {
        ServeiCamp camp = ServeiCamp.getBuilder("SV001", "camp1", 0, 6).build();
        when(serveiCampRepository.findById(1L)).thenReturn(Optional.of(camp));
        when(serveiCampGrupRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ServeiCampGrupNotFoundException.class, () -> serveiService.agrupaServeiCamp(1L, 99L));
    }

    @Test
    public void agrupaServeiCamp_ok_ambGrupDestiNull() throws Exception {
        ServeiCamp camp = ServeiCamp.getBuilder("SV001", "camp1", 0, 6).build();
        when(serveiCampRepository.findById(1L)).thenReturn(Optional.of(camp));
        when(serveiCampRepository.findByServeiAndGrupOrderByOrdreAsc(eq("SV001"), anyBoolean(), any()))
                .thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> serveiService.agrupaServeiCamp(1L, null));
        verify(cacheHelper).evictDadesEspecifiques("SV001");
    }

    @Test
    public void agrupaServeiCamp_ok_ambGrupDesti() throws Exception {
        ServeiCamp camp = ServeiCamp.getBuilder("SV001", "camp1", 0, 6).build();
        when(serveiCampRepository.findById(1L)).thenReturn(Optional.of(camp));
        ServeiCampGrup grup = ServeiCampGrup.getBuilder("SV001", null, "Grup1", null, 0).build();
        when(serveiCampGrupRepository.findById(5L)).thenReturn(Optional.of(grup));
        when(serveiCampRepository.findByServeiAndGrupOrderByOrdreAsc(eq("SV001"), anyBoolean(), any()))
                .thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> serveiService.agrupaServeiCamp(1L, 5L));
    }

    // ==================== findServeiCamps ====================

    @Test
    public void findServeiCamps_serveiNoTrobat_llancaExcepcio() {
        stubServeiNotTrobat("SV001");

        assertThrows(ServeiNotFoundException.class, () -> serveiService.findServeiCamps("SV001"));
    }

    @Test
    public void findServeiCamps_ok() throws ServeiNotFoundException {
        stubServicioByCode("SV001", 1L);
        when(serveiCampRepository.findByServeiOrderByGrupOrdreAsc("SV001")).thenReturn(Collections.emptyList());
        when(mapperFacade.mapAsList(Collections.emptyList(), ServeiCampDto.class)).thenReturn(Collections.emptyList());

        List<ServeiCampDto> result = serveiService.findServeiCamps("SV001");

        assertNotNull(result);
    }

    // ==================== marcarArrelResposta / desmarcarArrelResposta / getArrelRespostaPath ====================

    @Test
    public void marcarArrelResposta_cfgExisteix_actualitza() {
        ServeiConfig cfg = mock(ServeiConfig.class);
        when(serveiConfigRepository.findByServei("SV001")).thenReturn(cfg);

        serveiService.marcarArrelResposta("SV001", "path1");

        verify(cfg).setArrelRespostaPath("path1");
        verify(serveiConfigRepository).save(cfg);
    }

    @Test
    public void marcarArrelResposta_cfgNoExisteix_noFaRes() {
        when(serveiConfigRepository.findByServei("SV001")).thenReturn(null);

        serveiService.marcarArrelResposta("SV001", "path1");

        verify(serveiConfigRepository, never()).save(any());
    }

    @Test
    public void desmarcarArrelResposta_cfgExisteix_netejaPath() {
        ServeiConfig cfg = mock(ServeiConfig.class);
        when(serveiConfigRepository.findByServei("SV001")).thenReturn(cfg);

        serveiService.desmarcarArrelResposta("SV001");

        verify(cfg).setArrelRespostaPath(null);
        verify(serveiConfigRepository).save(cfg);
    }

    @Test
    public void desmarcarArrelResposta_cfgNoExisteix_noFaRes() {
        when(serveiConfigRepository.findByServei("SV001")).thenReturn(null);

        serveiService.desmarcarArrelResposta("SV001");

        verify(serveiConfigRepository, never()).save(any());
    }

    @Test
    public void getArrelRespostaPath_cfgExisteix_retornaPath() {
        ServeiConfig cfg = mock(ServeiConfig.class);
        when(cfg.getArrelRespostaPath()).thenReturn("path1");
        when(serveiConfigRepository.findByServei("SV001")).thenReturn(cfg);

        assertEquals("path1", serveiService.getArrelRespostaPath("SV001"));
    }

    @Test
    public void getArrelRespostaPath_cfgNoExisteix_retornaNull() {
        when(serveiConfigRepository.findByServei("SV001")).thenReturn(null);

        assertNull(serveiService.getArrelRespostaPath("SV001"));
    }

    // ==================== createServeiCampGrup / updateServeiCampGrup / deleteServeiCampGrup / moveServeiCampGrup ====================

    @Test
    public void createServeiCampGrup_serveiNoTrobat_llancaExcepcio() {
        when(scspHelper.getServicio("SV001")).thenReturn(null);

        ServeiCampGrupDto dto = new ServeiCampGrupDto();
        dto.setServei("SV001");
        dto.setNom("Grup1");

        assertThrows(ServeiNotFoundException.class, () -> serveiService.createServeiCampGrup(dto));
    }

    @Test
    public void createServeiCampGrup_ok_sensePare() throws ServeiNotFoundException {
        Servicio servicio = new Servicio();
        servicio.setCodCertificado("SV001");
        when(scspHelper.getServicio("SV001")).thenReturn(servicio);
        when(serveiCampGrupRepository.countByServeiAndPareOrderByOrdreAsc("SV001", true, null)).thenReturn(0);
        when(serveiCampGrupRepository.save(any(ServeiCampGrup.class))).thenAnswer(inv -> inv.getArgument(0));

        ServeiCampGrupDto dto = new ServeiCampGrupDto();
        dto.setServei("SV001");
        dto.setNom("Grup1");

        ServeiCampGrupDto result = serveiService.createServeiCampGrup(dto);

        assertNotNull(result);
    }

    @Test
    public void createServeiCampGrup_ok_ambPare() throws ServeiNotFoundException {
        Servicio servicio = new Servicio();
        servicio.setCodCertificado("SV001");
        when(scspHelper.getServicio("SV001")).thenReturn(servicio);
        ServeiCampGrup pare = ServeiCampGrup.getBuilder("SV001", null, "Pare", null, 0).build();
        when(serveiCampGrupRepository.findById(7L)).thenReturn(Optional.of(pare));
        when(serveiCampGrupRepository.countByServeiAndPareOrderByOrdreAsc(eq("SV001"), anyBoolean(), any())).thenReturn(1);
        when(serveiCampGrupRepository.save(any(ServeiCampGrup.class))).thenAnswer(inv -> inv.getArgument(0));

        ServeiCampGrupDto dto = new ServeiCampGrupDto();
        dto.setServei("SV001");
        dto.setNom("Fill1");
        dto.setPareId(7L);

        ServeiCampGrupDto result = serveiService.createServeiCampGrup(dto);

        assertNotNull(result);
    }

    @Test
    public void updateServeiCampGrup_noTrobat_llancaExcepcio() {
        when(serveiCampGrupRepository.findById(99L)).thenReturn(Optional.empty());

        ServeiCampGrupDto dto = new ServeiCampGrupDto();
        dto.setId(99L);

        assertThrows(ServeiCampGrupNotFoundException.class, () -> serveiService.updateServeiCampGrup(dto));
    }

    @Test
    public void updateServeiCampGrup_ok() throws ServeiCampGrupNotFoundException {
        ServeiCampGrup grup = ServeiCampGrup.getBuilder("SV001", null, "GrupOriginal", null, 0).build();
        when(serveiCampGrupRepository.findById(1L)).thenReturn(Optional.of(grup));

        ServeiCampGrupDto dto = new ServeiCampGrupDto();
        dto.setId(1L);
        dto.setNom("NouNom");
        dto.setAjuda("Ajuda");

        ServeiCampGrupDto result = serveiService.updateServeiCampGrup(dto);

        assertNotNull(result);
        assertEquals("NouNom", grup.getNom());
        verify(cacheHelper).evictDadesEspecifiques("SV001");
    }

    @Test
    public void deleteServeiCampGrup_noExisteix_llancaException() {
        when(serveiCampGrupRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ServeiCampGrupNotFoundException.class, () -> serveiService.deleteServeiCampGrup(99L));
    }

    @Test
    public void deleteServeiCampGrup_ok_reassignaCampsIEsborraSubgrups() throws ServeiCampGrupNotFoundException {
        ServeiCampGrup perEsborrar = ServeiCampGrup.getBuilder("SV001", null, "Grup1", null, 0).build();
        when(serveiCampGrupRepository.findById(1L)).thenReturn(Optional.of(perEsborrar));
        when(serveiCampGrupRepository.findByServeiAndPareOrderByOrdreAsc("SV001", false, perEsborrar))
                .thenReturn(Collections.emptyList());
        when(serveiCampRepository.countByServeiAndGrupOrderByOrdreAsc("SV001", true, null)).thenReturn(0);

        ServeiCampGrupDto result = serveiService.deleteServeiCampGrup(1L);

        assertNotNull(result);
        verify(serveiCampGrupRepository).deleteById(perEsborrar.getId());
        verify(cacheHelper).evictDadesEspecifiques("SV001");
    }

    @Test
    public void moveServeiCampGrup_noTrobat_llancaExcepcio() {
        when(serveiCampGrupRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ServeiCampGrupNotFoundException.class, () -> serveiService.moveServeiCampGrup(99L, true));
    }

    @Test
    public void moveServeiCampGrup_amunt_ok() throws ServeiCampGrupNotFoundException {
        ServeiCampGrup g0 = ServeiCampGrup.getBuilder("SV001", null, "G0", null, 0).build();
        ServeiCampGrup g1 = ServeiCampGrup.getBuilder("SV001", null, "G1", null, 1).build();
        when(serveiCampGrupRepository.findById(2L)).thenReturn(Optional.of(g1));
        when(serveiCampGrupRepository.findByServeiAndPareOrderByOrdreAsc("SV001", true, null))
                .thenReturn(new ArrayList<>(Arrays.asList(g0, g1)));

        serveiService.moveServeiCampGrup(2L, true);

        assertEquals(0, g1.getOrdre());
        assertEquals(1, g0.getOrdre());
    }

    @Test
    public void moveServeiCampGrup_avall_ok() throws ServeiCampGrupNotFoundException {
        ServeiCampGrup g0 = ServeiCampGrup.getBuilder("SV001", null, "G0", null, 0).build();
        ServeiCampGrup g1 = ServeiCampGrup.getBuilder("SV001", null, "G1", null, 1).build();
        when(serveiCampGrupRepository.findById(1L)).thenReturn(Optional.of(g0));
        when(serveiCampGrupRepository.findByServeiAndPareOrderByOrdreAsc("SV001", true, null))
                .thenReturn(new ArrayList<>(Arrays.asList(g0, g1)));

        serveiService.moveServeiCampGrup(1L, false);

        assertEquals(1, g0.getOrdre());
        assertEquals(0, g1.getOrdre());
    }

    @Test
    public void moveServeiCampGrup_extremSuperior_noFaRes() throws ServeiCampGrupNotFoundException {
        ServeiCampGrup g0 = ServeiCampGrup.getBuilder("SV001", null, "G0", null, 0).build();
        when(serveiCampGrupRepository.findById(1L)).thenReturn(Optional.of(g0));
        when(serveiCampGrupRepository.findByServeiAndPareOrderByOrdreAsc("SV001", true, null))
                .thenReturn(new ArrayList<>(Collections.singletonList(g0)));

        serveiService.moveServeiCampGrup(1L, true);

        assertEquals(0, g0.getOrdre());
    }

    // ==================== findServeiCampGrups / findServeiCampGrupsAndSubgrups / serveiCampGrupFindByNom ====================

    @Test
    public void findServeiCampGrups_serveiNoTrobat_llancaExcepcio() {
        stubServeiNotTrobat("SV001");

        assertThrows(ServeiNotFoundException.class, () -> serveiService.findServeiCampGrups("SV001"));
    }

    @Test
    public void findServeiCampGrups_ok() throws ServeiNotFoundException {
        stubServicioByCode("SV001", 1L);
        when(serveiCampGrupRepository.findByServeiAndPareIsNullOrderByOrdreAsc("SV001")).thenReturn(Collections.emptyList());
        when(mapperFacade.mapAsList(Collections.emptyList(), ServeiCampGrupDto.class)).thenReturn(Collections.emptyList());

        assertNotNull(serveiService.findServeiCampGrups("SV001"));
    }

    @Test
    public void findServeiCampGrupsAndSubgrups_serveiNoTrobat_llancaExcepcio() {
        stubServeiNotTrobat("SV001");

        assertThrows(ServeiNotFoundException.class, () -> serveiService.findServeiCampGrupsAndSubgrups("SV001"));
    }

    @Test
    public void findServeiCampGrupsAndSubgrups_ok() throws ServeiNotFoundException {
        stubServicioByCode("SV001", 1L);
        when(serveiCampGrupRepository.findByServei("SV001")).thenReturn(Collections.emptyList());
        when(mapperFacade.mapAsList(Collections.emptyList(), ServeiCampGrupDto.class)).thenReturn(Collections.emptyList());

        assertNotNull(serveiService.findServeiCampGrupsAndSubgrups("SV001"));
    }

    @Test
    public void serveiCampGrupFindByNom_retornaDto() {
        ServeiCampGrup grup = ServeiCampGrup.getBuilder("SV001", null, "Grup1", null, 0).build();
        when(serveiCampGrupRepository.findByNom("SV001", "Grup1")).thenReturn(grup);
        ServeiCampGrupDto dto = new ServeiCampGrupDto();
        when(mapperFacade.map(grup, ServeiCampGrupDto.class)).thenReturn(dto);

        assertSame(dto, serveiService.serveiCampGrupFindByNom("SV001", "Grup1"));
    }

    // ==================== createServeiBus / updateServeiBus / deleteServeiBus / findServeiBusById / findServeisBus ====================

    @Test
    public void createServeiBus_serveiNoTrobat_llancaExcepcio() {
        when(scspHelper.getServicio("SV001")).thenReturn(null);

        ServeiBusDto dto = new ServeiBusDto();
        dto.setServei("SV001");
        EntitatDto entitatDto = new EntitatDto();
        entitatDto.setId(1L);
        dto.setEntitat(entitatDto);

        assertThrows(ServeiNotFoundException.class, () -> serveiService.createServeiBus(dto));
    }

    @Test
    public void createServeiBus_entitatNoTrobada_llancaExcepcio() {
        Servicio servicio = new Servicio();
        servicio.setCodCertificado("SV001");
        when(scspHelper.getServicio("SV001")).thenReturn(servicio);
        when(entitatRepository.findById(1L)).thenReturn(Optional.empty());

        ServeiBusDto dto = new ServeiBusDto();
        dto.setServei("SV001");
        EntitatDto entitatDto = new EntitatDto();
        entitatDto.setId(1L);
        dto.setEntitat(entitatDto);

        assertThrows(EntitatNotFoundException.class, () -> serveiService.createServeiBus(dto));
    }

    @Test
    public void createServeiBus_ok() throws Exception {
        Servicio servicio = new Servicio();
        servicio.setCodCertificado("SV001");
        when(scspHelper.getServicio("SV001")).thenReturn(servicio);
        Entitat entitat = mock(Entitat.class);
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitat));
        when(serveiBusRepository.save(any(ServeiBus.class))).thenAnswer(inv -> inv.getArgument(0));

        ServeiBusDto dto = new ServeiBusDto();
        dto.setServei("SV001");
        dto.setUrlDesti("http://desti");
        EntitatDto entitatDto = new EntitatDto();
        entitatDto.setId(1L);
        dto.setEntitat(entitatDto);

        ServeiBusDto result = serveiService.createServeiBus(dto);

        assertNotNull(result);
    }

    @Test
    public void updateServeiBus_busNoTrobat_llancaExcepcio() {
        when(serveiBusRepository.findById(1L)).thenReturn(Optional.empty());

        ServeiBusDto dto = new ServeiBusDto();
        dto.setId(1L);

        assertThrows(ServeiBusNotFoundException.class, () -> serveiService.updateServeiBus(dto));
    }

    @Test
    public void updateServeiBus_entitatNoTrobada_llancaExcepcio() {
        ServeiBus bus = ServeiBus.getBuilder("SV001", "http://old", mock(Entitat.class)).build();
        when(serveiBusRepository.findById(1L)).thenReturn(Optional.of(bus));
        when(entitatRepository.findById(2L)).thenReturn(Optional.empty());

        ServeiBusDto dto = new ServeiBusDto();
        dto.setId(1L);
        EntitatDto entitatDto = new EntitatDto();
        entitatDto.setId(2L);
        dto.setEntitat(entitatDto);

        assertThrows(EntitatNotFoundException.class, () -> serveiService.updateServeiBus(dto));
    }

    @Test
    public void updateServeiBus_ok() throws Exception {
        ServeiBus bus = ServeiBus.getBuilder("SV001", "http://old", mock(Entitat.class)).build();
        when(serveiBusRepository.findById(1L)).thenReturn(Optional.of(bus));
        Entitat entitatNova = mock(Entitat.class);
        when(entitatRepository.findById(2L)).thenReturn(Optional.of(entitatNova));

        ServeiBusDto dto = new ServeiBusDto();
        dto.setId(1L);
        dto.setUrlDesti("http://nou");
        EntitatDto entitatDto = new EntitatDto();
        entitatDto.setId(2L);
        dto.setEntitat(entitatDto);

        ServeiBusDto result = serveiService.updateServeiBus(dto);

        assertNotNull(result);
        assertEquals("http://nou", bus.getUrlDesti());
    }

    @Test
    public void deleteServeiBus_noTrobat_llancaExcepcio() {
        when(serveiBusRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ServeiBusNotFoundException.class, () -> serveiService.deleteServeiBus(1L));
    }

    @Test
    public void deleteServeiBus_ok() throws ServeiBusNotFoundException {
        ServeiBus bus = ServeiBus.getBuilder("SV001", "http://old", mock(Entitat.class)).build();
        when(serveiBusRepository.findById(1L)).thenReturn(Optional.of(bus));

        ServeiBusDto result = serveiService.deleteServeiBus(1L);

        assertNotNull(result);
        verify(serveiBusRepository).delete(bus);
    }

    @Test
    public void findServeiBusById_noTrobat_llancaExcepcio() {
        when(serveiBusRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ServeiBusNotFoundException.class, () -> serveiService.findServeiBusById(1L));
    }

    @Test
    public void findServeiBusById_ok() throws ServeiBusNotFoundException {
        ServeiBus bus = ServeiBus.getBuilder("SV001", "http://old", mock(Entitat.class)).build();
        when(serveiBusRepository.findById(1L)).thenReturn(Optional.of(bus));

        assertNotNull(serveiService.findServeiBusById(1L));
    }

    @Test
    public void findServeisBus_serveiNoTrobat_llancaExcepcio() {
        stubServeiNotTrobat("SV001");

        assertThrows(ServeiNotFoundException.class, () -> serveiService.findServeisBus("SV001"));
    }

    @Test
    public void findServeisBus_ok() throws ServeiNotFoundException {
        stubServicioByCode("SV001", 1L);
        when(serveiBusRepository.findByServeiOrderByIdAsc("SV001")).thenReturn(Collections.emptyList());
        when(mapperFacade.mapAsList(Collections.emptyList(), ServeiBusDto.class)).thenReturn(Collections.emptyList());

        assertNotNull(serveiService.findServeisBus("SV001"));
    }

    // ==================== addServeiJustificantCamp / findServeiJustificantCamps ====================

    @Test
    public void addServeiJustificantCamp_serveiNoTrobat_llancaExcepcio() {
        when(scspHelper.getServicio("SV001")).thenReturn(null);

        ServeiJustificantCampDto dto = new ServeiJustificantCampDto();
        dto.setServei("SV001");
        dto.setXpath("/x");

        assertThrows(ServeiNotFoundException.class, () -> serveiService.addServeiJustificantCamp(dto));
    }

    @Test
    public void addServeiJustificantCamp_existentAmbTraduccio_actualitza() throws ServeiNotFoundException {
        Servicio servicio = new Servicio();
        servicio.setCodCertificado("SV001");
        when(scspHelper.getServicio("SV001")).thenReturn(servicio);
        ServeiJustificantCamp existent = mock(ServeiJustificantCamp.class);
        when(serveiJustificantCampRepository.findByServeiAndXpathAndLocaleIdiomaAndLocaleRegio(
                "SV001", "/x", "ca", "ES")).thenReturn(existent);

        ServeiJustificantCampDto dto = new ServeiJustificantCampDto();
        dto.setServei("SV001");
        dto.setXpath("/x");
        dto.setTraduccio("Traduccio nova");
        dto.setDocument(true);

        serveiService.addServeiJustificantCamp(dto);

        verify(existent).update("Traduccio nova", true);
    }

    @Test
    public void addServeiJustificantCamp_existentSenseTraduccio_esborra() throws ServeiNotFoundException {
        Servicio servicio = new Servicio();
        servicio.setCodCertificado("SV001");
        when(scspHelper.getServicio("SV001")).thenReturn(servicio);
        ServeiJustificantCamp existent = mock(ServeiJustificantCamp.class);
        when(serveiJustificantCampRepository.findByServeiAndXpathAndLocaleIdiomaAndLocaleRegio(
                "SV001", "/x", "ca", "ES")).thenReturn(existent);

        ServeiJustificantCampDto dto = new ServeiJustificantCampDto();
        dto.setServei("SV001");
        dto.setXpath("/x");
        dto.setTraduccio("");

        serveiService.addServeiJustificantCamp(dto);

        verify(serveiJustificantCampRepository).delete(existent);
    }

    @Test
    public void addServeiJustificantCamp_nouAmbTraduccio_esCrea() throws ServeiNotFoundException {
        Servicio servicio = new Servicio();
        servicio.setCodCertificado("SV001");
        when(scspHelper.getServicio("SV001")).thenReturn(servicio);
        when(serveiJustificantCampRepository.findByServeiAndXpathAndLocaleIdiomaAndLocaleRegio(
                "SV001", "/x", "ca", "ES")).thenReturn(null);

        ServeiJustificantCampDto dto = new ServeiJustificantCampDto();
        dto.setServei("SV001");
        dto.setXpath("/x");
        dto.setTraduccio("Traduccio nova");
        dto.setDocument(false);

        serveiService.addServeiJustificantCamp(dto);

        verify(serveiJustificantCampRepository).save(any(ServeiJustificantCamp.class));
    }

    @Test
    public void addServeiJustificantCamp_nouSenseTraduccio_noFaRes() throws ServeiNotFoundException {
        Servicio servicio = new Servicio();
        servicio.setCodCertificado("SV001");
        when(scspHelper.getServicio("SV001")).thenReturn(servicio);
        when(serveiJustificantCampRepository.findByServeiAndXpathAndLocaleIdiomaAndLocaleRegio(
                "SV001", "/x", "ca", "ES")).thenReturn(null);

        ServeiJustificantCampDto dto = new ServeiJustificantCampDto();
        dto.setServei("SV001");
        dto.setXpath("/x");

        serveiService.addServeiJustificantCamp(dto);

        verify(serveiJustificantCampRepository, never()).save(any());
    }

    @Test
    public void findServeiJustificantCamps_serveiNoTrobat_llancaExcepcio() {
        stubServeiNotTrobat("SV001");

        assertThrows(ServeiNotFoundException.class, () -> serveiService.findServeiJustificantCamps("SV001"));
    }

    @Test
    public void findServeiJustificantCamps_ok() throws ServeiNotFoundException {
        stubServicioByCode("SV001", 1L);
        when(serveiJustificantCampRepository.findByServeiAndLocaleIdiomaAndLocaleRegio("SV001", "ca", "ES"))
                .thenReturn(Collections.emptyList());
        when(mapperFacade.mapAsList(Collections.emptyList(), ServeiJustificantCampDto.class))
                .thenReturn(Collections.emptyList());

        assertNotNull(serveiService.findServeiJustificantCamps("SV001"));
    }

    // ==================== xsdFindByServei / getRolsConfigurats ====================

    @Test
    public void xsdFindByServei_serveiNoTrobat_llancaExcepcio() {
        stubServeiNotTrobat("SV001");

        assertThrows(ServeiNotFoundException.class, () -> serveiService.xsdFindByServei("SV001"));
    }

    @Test
    public void xsdFindByServei_ok() throws Exception {
        stubServicioByCode("SV001", 1L);
        when(serveiXsdRepository.findByServei("SV001")).thenReturn(Collections.emptyList());
        when(mapperFacade.mapAsList(Collections.emptyList(), ServeiXsdDto.class)).thenReturn(Collections.emptyList());

        assertNotNull(serveiService.xsdFindByServei("SV001"));
    }

    @Test
    public void getRolsConfigurats_filtraNomsBuits() {
        ServeiConfig ambRol = mock(ServeiConfig.class);
        when(ambRol.getRoleName()).thenReturn("ROLE_A");
        ServeiConfig senseRol = mock(ServeiConfig.class);
        when(senseRol.getRoleName()).thenReturn(null);
        when(serveiConfigRepository.findAll()).thenReturn(Arrays.asList(ambRol, senseRol));

        List<String> result = serveiService.getRolsConfigurats();

        assertEquals(Collections.singletonList("ROLE_A"), result);
    }

    // ==================== setApplicationContext / setMessageSource / onApplicationEvent ====================

    @Test
    public void setApplicationContext_estableixCamp() {
        serveiService.setApplicationContext(applicationContext);

        assertSame(applicationContext, ReflectionTestUtils.getField(serveiService, "applicationContext"));
    }

    @Test
    public void setMessageSource_estableixCamp() {
        serveiService.setMessageSource(messageSource);

        assertSame(messageSource, ReflectionTestUtils.getField(serveiService, "messageSource"));
    }

    @Test
    public void onApplicationEvent_obteSelfCorrectament() {
        ReflectionTestUtils.setField(serveiService, "applicationContext", applicationContext);
        when(applicationContext.getBean(ServeiService.class)).thenReturn(self);

        assertDoesNotThrow(() -> serveiService.onApplicationEvent(mock(ContextRefreshedEvent.class)));

        assertSame(self, ReflectionTestUtils.getField(serveiService, "self"));
    }

    @Test
    public void onApplicationEvent_errorObtenintSelf_esCapturaError() {
        ReflectionTestUtils.setField(serveiService, "applicationContext", applicationContext);
        when(applicationContext.getBean(ServeiService.class)).thenThrow(new RuntimeException("no bean"));

        assertDoesNotThrow(() -> serveiService.onApplicationEvent(mock(ContextRefreshedEvent.class)));
    }

    // ==================== xsdDelete / xsdDescarregar / xsdCreate ====================

    @Test
    public void xsdDelete_existeix_esborraFitxerIRegistre() throws Exception {
        ServeiXsd xsd = ServeiXsd.builder()
                .servei("SV001")
                .tipus(XsdTipusEnumDto.PETICIO)
                .nomArxiu("peticio.xsd")
                .path("/tmp/inexistent-dir")
                .build();
        when(serveiXsdRepository.findByServeiAndTipus("SV001", XsdTipusEnumDto.PETICIO)).thenReturn(xsd);

        serveiService.xsdDelete("SV001", XsdTipusEnumDto.PETICIO);

        verify(serveiXsdRepository).delete(xsd);
        verify(cacheHelper).evictEnumeratsPerServei("SV001");
    }

    @Test
    public void xsdDelete_noExisteix_noFaRes() throws Exception {
        when(serveiXsdRepository.findByServeiAndTipus("SV001", XsdTipusEnumDto.PETICIO)).thenReturn(null);

        serveiService.xsdDelete("SV001", XsdTipusEnumDto.PETICIO);

        verify(serveiXsdRepository, never()).delete(any());
    }

    @Test
    public void xsdDescarregar_ok_llegeixFitxerDeDisc() throws Exception {
        String scratchDir = "/tmp/claude-1000/-home-sionandreu-IdeaProjects-pinbal2/b16316c0-8c5d-4c3e-8359-8b25f3072b5f/scratchpad";
        new File(scratchDir).mkdirs();
        File fitxer = new File(scratchDir, "descarregar.xsd");
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(fitxer)) {
            fos.write("<xsd/>".getBytes());
        }
        ServeiXsd xsd = ServeiXsd.builder()
                .servei("SV001")
                .tipus(XsdTipusEnumDto.PETICIO)
                .nomArxiu("descarregar.xsd")
                .path(scratchDir)
                .build();
        when(serveiXsdRepository.findByServeiAndTipus("SV001", XsdTipusEnumDto.PETICIO)).thenReturn(xsd);

        es.caib.pinbal.logic.intf.dto.FitxerDto result = serveiService.xsdDescarregar("SV001", XsdTipusEnumDto.PETICIO);

        assertEquals("descarregar.xsd", result.getNom());
        assertTrue(result.getContingut().length > 0);
        fitxer.delete();
    }

    @Test
    public void xsdCreate_nouFitxer_esCreaAlDisc() throws Exception {
        String scratchDir = "/tmp/claude-1000/-home-sionandreu-IdeaProjects-pinbal2/b16316c0-8c5d-4c3e-8359-8b25f3072b5f/scratchpad/xsdcreate";
        when(serveiXsdRepository.findByServeiAndTipus("SV001", XsdTipusEnumDto.PETICIO)).thenReturn(null);
        when(serveiXsdHelper.getXsdTipusNom(XsdTipusEnumDto.PETICIO)).thenReturn("peticio.xsd");
        when(serveiXsdHelper.getPathPerServei("SV001")).thenReturn(scratchDir);

        ServeiXsdDto dto = new ServeiXsdDto();
        dto.setTipus(XsdTipusEnumDto.PETICIO);
        dto.setNomArxiu("peticio.xsd");

        serveiService.xsdCreate("SV001", dto, "<xsd/>".getBytes());

        verify(serveiXsdRepository).save(any(ServeiXsd.class));
        verify(cacheHelper).evictEnumeratsPerServei("SV001");
        new File(scratchDir, "peticio.xsd").delete();
    }

    @Test
    public void xsdCreate_fitxerExistent_esSubstitueix() throws Exception {
        String scratchDir = "/tmp/claude-1000/-home-sionandreu-IdeaProjects-pinbal2/b16316c0-8c5d-4c3e-8359-8b25f3072b5f/scratchpad/xsdcreate2";
        new File(scratchDir).mkdirs();
        ServeiXsd existent = mock(ServeiXsd.class);
        when(existent.getPath()).thenReturn(scratchDir);
        when(existent.getNomArxiu()).thenReturn("existent.xsd");
        when(serveiXsdRepository.findByServeiAndTipus("SV001", XsdTipusEnumDto.RESPOSTA)).thenReturn(existent);
        when(serveiXsdHelper.getXsdTipusNom(XsdTipusEnumDto.RESPOSTA)).thenReturn("resposta.xsd");
        when(serveiXsdHelper.getPathPerServei("SV001")).thenReturn(scratchDir);

        ServeiXsdDto dto = new ServeiXsdDto();
        dto.setTipus(XsdTipusEnumDto.RESPOSTA);
        dto.setNomArxiu("resposta.xsd");

        serveiService.xsdCreate("SV001", dto, "<xsd/>".getBytes());

        verify(existent).updateServeiXsd();
        verify(serveiXsdRepository).save(existent);
        new File(scratchDir, "resposta.xsd").delete();
    }

    // ==================== updateVersio / findAll ====================

    @Test
    public void updateVersio_ok_incrementaVersio() {
        ServeiConfig cfg = mock(ServeiConfig.class);
        when(cfg.getVersion()).thenReturn(3L);
        when(serveiConfigRepository.findByServei("SV001")).thenReturn(cfg);

        serveiService.updateVersio("SV001");

        verify(cfg).setVersion(4L);
        verify(serveiConfigRepository).save(cfg);
    }

    @Test
    public void updateVersio_serveiConfigNoExisteix_esCapturaError() {
        when(serveiConfigRepository.findByServei("SV001")).thenReturn(null);

        assertDoesNotThrow(() -> serveiService.updateVersio("SV001"));
    }

    @Test
    public void findAll_retornaTots() {
        Servicio s1 = new Servicio();
        s1.setCodCertificado("SV001");
        s1.setDescripcion("D1");
        when(scspHelper.findServicioAll()).thenReturn(Collections.singletonList(s1));

        assertEquals(1, serveiService.findAll().size());
    }

    // ==================== serveiRegla* ====================

    @Test
    public void serveiReglaFindByNom_retornaDto() {
        ServeiRegla regla = mock(ServeiRegla.class);
        when(serveiReglaRepository.findByNom(1L, "regla1")).thenReturn(regla);
        ServeiReglaDto dto = new ServeiReglaDto();
        when(mapperFacade.map(regla, ServeiReglaDto.class)).thenReturn(dto);

        assertSame(dto, serveiService.serveiReglaFindByNom(1L, "regla1"));
    }

    @Test
    public void serveiReglaFindById_retornaDto() {
        ServeiRegla regla = mock(ServeiRegla.class);
        when(serveiReglaRepository.findById(1L)).thenReturn(Optional.of(regla));
        ServeiReglaDto dto = new ServeiReglaDto();
        when(mapperFacade.map(regla, ServeiReglaDto.class)).thenReturn(dto);

        assertSame(dto, serveiService.serveiReglaFindById(1L));
    }

    @Test
    public void serveiReglaCreate_serveiNoTrobat_llancaExcepcio() {
        when(serveiRepository.findByCode("SV001")).thenReturn(Collections.emptyList());

        assertThrows(ServeiNotFoundException.class,
                () -> serveiService.serveiReglaCreate("SV001", new ServeiReglaDto()));
    }

    @Test
    public void serveiReglaCreate_ok_ambSeguentOrdreNull() throws ServeiNotFoundException {
        Servei servei = mock(Servei.class);
        when(servei.getId()).thenReturn(1L);
        when(serveiRepository.findByCode("SV001")).thenReturn(Collections.singletonList(servei));
        when(serveiReglaRepository.getSeguentOrdre(1L)).thenReturn(null);
        when(serveiReglaRepository.save(any(ServeiRegla.class))).thenAnswer(inv -> inv.getArgument(0));

        ServeiReglaDto dto = new ServeiReglaDto();
        dto.setNom("regla1");
        dto.setModificat(ModificatEnum.CAMPS);
        dto.setAccio(AccioEnum.MOSTRAR);
        dto.setModificatValor(new LinkedHashSet<>(Collections.singletonList("v1")));
        dto.setAfectatValor(new LinkedHashSet<>(Collections.singletonList("v2")));

        ServeiReglaDto result = serveiService.serveiReglaCreate("SV001", dto);

        assertNotNull(result);
        verify(serveiReglaRepository).save(argThat(r -> r.getOrdre() == 0));
    }

    @Test
    public void serveiReglaCreate_ok_ambSeguentOrdreExistent() throws ServeiNotFoundException {
        Servei servei = mock(Servei.class);
        when(servei.getId()).thenReturn(1L);
        when(serveiRepository.findByCode("SV001")).thenReturn(Collections.singletonList(servei));
        when(serveiReglaRepository.getSeguentOrdre(1L)).thenReturn(4);
        when(serveiReglaRepository.save(any(ServeiRegla.class))).thenAnswer(inv -> inv.getArgument(0));

        ServeiReglaDto dto = new ServeiReglaDto();
        dto.setNom("regla2");
        dto.setModificat(ModificatEnum.GRUPS);
        dto.setAccio(AccioEnum.OCULTAR);

        serveiService.serveiReglaCreate("SV001", dto);

        verify(serveiReglaRepository).save(argThat(r -> r.getOrdre() == 5));
    }

    @Test
    public void serveiReglaUpdate_serveiNoTrobat_llancaExcepcio() {
        when(serveiRepository.findByCode("SV001")).thenReturn(Collections.emptyList());

        assertThrows(ServeiNotFoundException.class,
                () -> serveiService.serveiReglaUpdate("SV001", new ServeiReglaDto()));
    }

    @Test
    public void serveiReglaUpdate_reglaNoTrobada_llancaExcepcio() {
        stubServeiEntitat("SV001", 1L);
        when(serveiReglaRepository.findById(9L)).thenReturn(Optional.empty());

        ServeiReglaDto dto = new ServeiReglaDto();
        dto.setId(9L);

        assertThrows(NotFoundException.class, () -> serveiService.serveiReglaUpdate("SV001", dto));
    }

    @Test
    public void serveiReglaUpdate_ok() throws ServeiNotFoundException {
        stubServeiEntitat("SV001", 1L);
        ServeiRegla regla = mock(ServeiRegla.class);
        when(serveiReglaRepository.findById(9L)).thenReturn(Optional.of(regla));
        when(serveiReglaRepository.save(regla)).thenReturn(regla);

        ServeiReglaDto dto = new ServeiReglaDto();
        dto.setId(9L);
        dto.setNom("nouNom");
        dto.setModificat(ModificatEnum.CAMPS);
        dto.setAccio(AccioEnum.EDITAR);

        serveiService.serveiReglaUpdate("SV001", dto);

        verify(regla).setNom("nouNom");
        verify(serveiReglaRepository).save(regla);
    }

    @Test
    public void serveiReglaDelete_serveiNoTrobat_llancaExcepcio() {
        when(serveiRepository.findByCode("SV001")).thenReturn(Collections.emptyList());

        assertThrows(ServeiNotFoundException.class, () -> serveiService.serveiReglaDelete("SV001", 1L));
    }

    @Test
    public void serveiReglaDelete_reglaNoTrobada_llancaExcepcio() {
        stubServeiEntitat("SV001", 1L);
        when(serveiReglaRepository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> serveiService.serveiReglaDelete("SV001", 9L));
    }

    @Test
    public void serveiReglaDelete_ok() throws ServeiNotFoundException {
        stubServeiEntitat("SV001", 1L);
        ServeiRegla regla = mock(ServeiRegla.class);
        when(serveiReglaRepository.findById(9L)).thenReturn(Optional.of(regla));

        serveiService.serveiReglaDelete("SV001", 9L);

        verify(serveiReglaRepository).delete(regla);
    }

    @Test
    public void serveiReglaMoure_reglaNoTrobada_llancaExcepcio() {
        when(serveiReglaRepository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> serveiService.serveiReglaMoure(9L, 0));
    }

    @Test
    public void serveiReglaMoure_ok_reordena() {
        ServeiRegla regla = mock(ServeiRegla.class);
        Servei servei = mock(Servei.class);
        when(regla.getServei()).thenReturn(servei);
        when(serveiReglaRepository.findById(1L)).thenReturn(Optional.of(regla));
        ServeiRegla regla2 = mock(ServeiRegla.class);
        List<ServeiRegla> regles = new ArrayList<>(Arrays.asList(regla, regla2));
        when(serveiReglaRepository.findByServeiOrderByOrdreAsc(servei)).thenReturn(regles);

        serveiService.serveiReglaMoure(1L, 1);

        verify(serveiReglaRepository, times(2)).save(any(ServeiRegla.class));
    }

    @Test
    public void serveiReglaMoure_mateixaPosicio_noReordena() {
        ServeiRegla regla = mock(ServeiRegla.class);
        Servei servei = mock(Servei.class);
        when(regla.getServei()).thenReturn(servei);
        when(serveiReglaRepository.findById(1L)).thenReturn(Optional.of(regla));
        List<ServeiRegla> regles = new ArrayList<>(Collections.singletonList(regla));
        when(serveiReglaRepository.findByServeiOrderByOrdreAsc(servei)).thenReturn(regles);

        serveiService.serveiReglaMoure(1L, 0);

        verify(serveiReglaRepository, never()).save(any());
    }

    @Test
    public void serveiReglesFindAll_serveiNoTrobat_llancaExcepcio() {
        when(serveiRepository.findByCode("SV001")).thenReturn(Collections.emptyList());

        assertThrows(ServeiNotFoundException.class, () -> serveiService.serveiReglesFindAll("SV001"));
    }

    @Test
    public void serveiReglesFindAll_ok() throws ServeiNotFoundException {
        Servei servei = stubServeiEntitat("SV001", 1L);
        when(serveiReglaRepository.findByServeiOrderByOrdreAsc(servei)).thenReturn(Collections.emptyList());
        when(mapperFacade.mapAsList(Collections.emptyList(), ServeiReglaDto.class)).thenReturn(Collections.emptyList());

        assertNotNull(serveiService.serveiReglesFindAll("SV001"));
    }

    @Test
    public void findCampIdsByReglesServei_serveiNoTrobat_llancaExcepcio() {
        when(serveiRepository.findByCode("SV001")).thenReturn(Collections.emptyList());

        assertThrows(ServeiNotFoundException.class, () -> serveiService.findCampIdsByReglesServei("SV001"));
    }

    @Test
    public void findCampIdsByReglesServei_ok() throws ServeiNotFoundException {
        stubServeiEntitat("SV001", 1L);
        when(serveiReglaRepository.findCampsRegles("SV001")).thenReturn(Collections.singletonList(5L));

        assertEquals(Collections.singletonList(5L), serveiService.findCampIdsByReglesServei("SV001"));
    }

    @Test
    public void findGrupIdsByReglesServei_ok() throws ServeiNotFoundException {
        when(serveiReglaRepository.findGrupsRegles("SV001")).thenReturn(Collections.singletonList(7L));

        assertEquals(Collections.singletonList(7L), serveiService.findGrupIdsByReglesServei("SV001"));
    }

    @Test
    public void getCampsByserveiRegla_serveiNoTrobat_llancaExcepcio() {
        when(serveiRepository.findByCode("SV001")).thenReturn(Collections.emptyList());

        assertThrows(ServeiNotFoundException.class,
                () -> serveiService.getCampsByserveiRegla("SV001", null));
    }

    @Test
    public void getCampsByserveiRegla_ok_ambCampsModificats() throws ServeiNotFoundException {
        Servei servei = stubServeiEntitat("SV001", 1L);
        when(reglaHelper.getCampFormProperties(eq(servei), anySet())).thenReturn(Collections.emptyList());

        assertNotNull(serveiService.getCampsByserveiRegla("SV001", new String[] {"camp1"}));
    }

    @Test
    public void getGrupsByserveiRegla_serveiNoTrobat_llancaExcepcio() {
        when(serveiRepository.findByCode("SV001")).thenReturn(Collections.emptyList());

        assertThrows(ServeiNotFoundException.class,
                () -> serveiService.getGrupsByserveiRegla("SV001", null));
    }

    @Test
    public void getGrupsByserveiRegla_ok_senseGrupsModificats() throws ServeiNotFoundException {
        Servei servei = stubServeiEntitat("SV001", 1L);
        when(reglaHelper.getGrupFormProperties(eq(servei), anySet())).thenReturn(Collections.emptyList());

        assertNotNull(serveiService.getGrupsByserveiRegla("SV001", null));
    }

    // ==================== getServeis / getServeiDtoByCodi ====================

    @Test
    public void getServeis_retornaLlistaDeDtos() {
        Servei servei = mock(Servei.class);
        when(servei.getId()).thenReturn(1L);
        when(servei.getCodi()).thenReturn("SV001");
        when(servei.getDescripcio()).thenReturn("Descripcio");
        when(serveiRepository.findByCodiAndDescripcioLikeText("text")).thenReturn(Collections.singletonList(servei));

        List<ServeiDto> result = serveiService.getServeis("text");

        assertEquals(1, result.size());
        assertEquals("SV001", result.get(0).getCodi());
    }

    @Test
    public void getServeiDtoByCodi_serveiNoTrobat_llancaExcepcio() {
        stubServeiNotTrobat("SV001");

        assertThrows(ServeiNotFoundException.class, () -> serveiService.getServeiDtoByCodi("SV001"));
    }

    @Test
    public void getServeiDtoByCodi_ok() throws ServeiNotFoundException {
        stubServicioByCode("SV001", 1L);

        ServeiDto result = serveiService.getServeiDtoByCodi("SV001");

        assertEquals("SV001", result.getCodi());
    }
}
