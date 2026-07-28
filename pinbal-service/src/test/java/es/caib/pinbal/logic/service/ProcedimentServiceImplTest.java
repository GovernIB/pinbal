/**
 *
 */
package es.caib.pinbal.logic.service;

import es.caib.pinbal.logic.helper.CacheHelper;
import es.caib.pinbal.logic.helper.DtoMappingHelper;
import es.caib.pinbal.logic.helper.PaginacioHelper;
import es.caib.pinbal.logic.helper.UsuariHelper;
import es.caib.pinbal.logic.intf.dto.*;
import es.caib.pinbal.logic.intf.service.exception.*;
import es.caib.pinbal.persist.entity.*;
import es.caib.pinbal.persist.repository.*;
import ma.glasnost.orika.MapperFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.*;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class ProcedimentServiceImplTest {

    @Mock private ProcedimentRepository procedimentRepository;
    @Mock private EntitatRepository entitatRepository;
    @Mock private EntitatServeiRepository entitatServeiRepository;
    @Mock private EntitatUsuariRepository entitatUsuariRepository;
    @Mock private ProcedimentServeiRepository procedimentServeiRepository;
    @Mock private OrganGestorRepository organGestorRepository;
    @Mock private ServeiRepository serveiRepository;
    @Mock private ServeiConfigRepository serveiConfigRepository;
    @Mock private DtoMappingHelper dtoMappingHelper;
    @Mock private UsuariHelper usuariHelper;
    @Mock private PaginacioHelper paginacioHelper;
    @Mock private CacheHelper cacheHelper;
    @Mock private MutableAclService aclService;
    @Mock private MapperFacade mapperFacade;

    @InjectMocks
    private ProcedimentServiceImpl procedimentService;

    private Entitat entitatMock;
    private Procediment procedimentMock;
    private Authentication auth;
    private SecurityContext securityContext;

    @BeforeEach
    public void setUp() {
        when(dtoMappingHelper.getMapperFacade()).thenReturn(mapperFacade);
        entitatMock = mock(Entitat.class);
        when(entitatMock.getCodi()).thenReturn("ENT01");
        procedimentMock = mock(Procediment.class);
        when(procedimentMock.getEntitat()).thenReturn(entitatMock);

        auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("usuari1");
        when(auth.getAuthorities()).thenReturn(Collections.emptyList());
        securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
    }

    /** Estuba l'aclService per simular que no hi ha cap ACL creada per a l'objecte consultat. */
    private void stubAclNotFound() {
        when(aclService.readAclById(any())).thenThrow(mock(NotFoundException.class));
    }

    /** Estuba l'aclService per simular una resposta de permisos concedits (o no) via filterGrantedAll/isGrantedAll. */
    private void stubAclGranted(boolean granted) {
        MutableAcl aclMock = mock(MutableAcl.class);
        when(aclMock.isGranted(anyList(), anyList(), eq(false))).thenReturn(granted);
        when(aclMock.getEntries()).thenReturn(Collections.emptyList());
        when(aclService.readAclById(any())).thenReturn(aclMock);
    }

    /** Estuba l'aclService retornant un MutableAcl amb les entrades indicades (per getAclSids/revocar). */
    private MutableAcl stubMutableAclAmbEntrades(List<AccessControlEntry> entrades) {
        MutableAcl aclMock = mock(MutableAcl.class);
        when(aclMock.getEntries()).thenReturn(entrades);
        when(aclService.readAclById(any())).thenReturn(aclMock);
        return aclMock;
    }

	private Authentication stubAuthUsuari(String codi) {
		Authentication auth = mock(Authentication.class);
		when(auth.getName()).thenReturn(codi);
		doReturn(Collections.emptyList()).when(auth).getAuthorities();
		return auth;
	}

    // ==================== create ====================

    @Test
    public void create_entitatNoExisteix_llancaException() {
        ProcedimentDto dto = new ProcedimentDto();
        dto.setEntitatId(99L);
        when(entitatRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntitatNotFoundException.class, () -> procedimentService.create(dto));
    }

    @Test
    public void create_senseCodiSiaOrigen_creaProcediment() throws EntitatNotFoundException {
        ProcedimentDto dto = new ProcedimentDto();
        dto.setEntitatId(1L);
        dto.setCodi("PROC01");
        dto.setNom("Procediment de prova");
        dto.setDepartament("Informatica");
        dto.setOrganGestor(OrganGestorDto.builder().id(5L).codi("OG01").build());
        dto.setCodiSia("SIA01");

        OrganGestor organGestor = mock(OrganGestor.class);
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitatMock));
        when(organGestorRepository.getOne(5L)).thenReturn(organGestor);
        when(procedimentRepository.findByEntitatAndCodiSiaOrigen(eq(entitatMock), anyString())).thenReturn(Collections.emptyList());
        when(procedimentRepository.save(any(Procediment.class))).thenAnswer(inv -> inv.getArgument(0));
        ProcedimentDto respostaDto = new ProcedimentDto();
        when(mapperFacade.map(any(Procediment.class), eq(ProcedimentDto.class))).thenReturn(respostaDto);

        ProcedimentDto result = procedimentService.create(dto);

        assertNotNull(result);
        verify(cacheHelper).evictProcedimentsPerEntitat("ENT01");
        verify(procedimentRepository, never()).findByEntitatAndCodiSia(any(), any());
    }

    @Test
    public void create_ambCodiSiaOrigenIClonarPermisos_clonaPermisosServeis() throws EntitatNotFoundException {
        ProcedimentDto dto = new ProcedimentDto();
        dto.setEntitatId(1L);
        dto.setCodi("PROC01");
        dto.setNom("Procediment fill");
        dto.setDepartament("Informatica");
        dto.setOrganGestor(OrganGestorDto.builder().id(5L).build());
        dto.setCodiSia("SIA01");
        dto.setCodiSiaOrigen("SIA00");
        dto.setClonarPermisosOrigen(true);

        OrganGestor organGestor = mock(OrganGestor.class);
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitatMock));
        when(organGestorRepository.getOne(5L)).thenReturn(organGestor);
        when(procedimentRepository.findByEntitatAndCodiSiaOrigen(eq(entitatMock), anyString())).thenReturn(Collections.emptyList());
        when(procedimentRepository.save(any(Procediment.class))).thenAnswer(inv -> {
            Procediment p = inv.getArgument(0);
            p.configurarIdPerTest(200L);
            return p;
        });

        Procediment procedimentOrigen = mock(Procediment.class);
        when(procedimentOrigen.getId()).thenReturn(50L);
        when(procedimentRepository.findByEntitatAndCodiSia(entitatMock, "SIA00")).thenReturn(procedimentOrigen);

        ProcedimentServei serveiOrigen = mock(ProcedimentServei.class);
        Servei serveiScsp = mock(Servei.class);
        when(serveiOrigen.getServei()).thenReturn("SV01");
        when(serveiOrigen.getId()).thenReturn(100L);
        when(procedimentServeiRepository.findActiusByProcedimentId(50L)).thenReturn(Collections.singletonList(serveiOrigen));
        when(procedimentServeiRepository.save(any(ProcedimentServei.class))).thenAnswer(inv -> {
            ProcedimentServei p = inv.getArgument(0);
            p.configurarIdPerTest(300L);
            return p;
        });

        AccessControlEntry ace = mock(AccessControlEntry.class);
        when(ace.getSid()).thenReturn(new PrincipalSid("usuari1"));
        when(ace.getPermission()).thenReturn(BasePermission.READ);
        MutableAcl aclOrigen = stubMutableAclAmbEntrades(Collections.singletonList(ace));
        MutableAcl aclDesti = mock(MutableAcl.class);
        when(aclService.createAcl(any())).thenReturn(aclDesti);

        ProcedimentDto respostaDto = new ProcedimentDto();
        when(mapperFacade.map(any(Procediment.class), eq(ProcedimentDto.class))).thenReturn(respostaDto);

        ProcedimentDto result = procedimentService.create(dto);

        assertNotNull(result);
        verify(procedimentServeiRepository).save(any(ProcedimentServei.class));
        verify(aclService).updateAcl(any());
    }

    @Test
    public void create_ambFillsPeresenActualitzaOrigenAntic() throws EntitatNotFoundException {
        ProcedimentDto dto = new ProcedimentDto();
        dto.setEntitatId(1L);
        dto.setCodi("PROC01");
        dto.setNom("Procediment amb fills");
        dto.setOrganGestor(OrganGestorDto.builder().id(5L).build());
        dto.setCodiSia("SIA01");
        dto.setCodiSiaFills(Arrays.asList("SIA02", ""));

        OrganGestor organGestor = mock(OrganGestor.class);
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitatMock));
        when(organGestorRepository.getOne(5L)).thenReturn(organGestor);

        Procediment fillAntic = mock(Procediment.class);
        when(procedimentRepository.findByEntitatAndCodiSiaOrigen(eq(entitatMock), anyString())).thenReturn(Collections.singletonList(fillAntic));

        Procediment fillNou = mock(Procediment.class);
        when(procedimentRepository.findByEntitatAndCodiSia(entitatMock, "SIA02")).thenReturn(fillNou);
        when(procedimentRepository.save(any(Procediment.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapperFacade.map(any(Procediment.class), eq(ProcedimentDto.class))).thenReturn(new ProcedimentDto());

        procedimentService.create(dto);

        verify(fillAntic).setCodiSiaOrigen(null);
        verify(fillNou).updateCodiSiaOrigen("SIA01");
    }

    // ==================== delete ====================

    @Test
    public void delete_noExisteix_llancaException() {
        when(procedimentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ProcedimentNotFoundException.class, () -> procedimentService.delete(99L));
    }

    @Test
    public void delete_existeix_esborraIRetornaDto() throws ProcedimentNotFoundException {
        when(procedimentRepository.findById(1L)).thenReturn(Optional.of(procedimentMock));
        ProcedimentDto dto = new ProcedimentDto();
        when(mapperFacade.map(procedimentMock, ProcedimentDto.class)).thenReturn(dto);

        ProcedimentDto result = procedimentService.delete(1L);

        assertNotNull(result);
        verify(procedimentRepository).delete(procedimentMock);
        verify(cacheHelper).evictProcedimentsPerEntitat("ENT01");
    }

    // ==================== findAmbEntitat ====================

    @Test
    public void findAmbEntitat_noExisteix_llancaException() {
        when(entitatRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntitatNotFoundException.class, () -> procedimentService.findAmbEntitat(99L));
    }

    @Test
    public void findAmbEntitat_existeix_retornaLlista() throws EntitatNotFoundException {
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitatMock));
        when(procedimentRepository.findByEntitatOrderByNomAsc(entitatMock)).thenReturn(Collections.singletonList(procedimentMock));
        ProcedimentDto dto = new ProcedimentDto();
        when(mapperFacade.mapAsList(anyList(), eq(ProcedimentDto.class))).thenReturn(Collections.singletonList(dto));

        List<ProcedimentDto> result = procedimentService.findAmbEntitat(1L);

        assertEquals(1, result.size());
    }

    @Test
    public void findAmbEntitatAmbFiltre_noExisteix_llancaException() {
        when(entitatRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntitatNotFoundException.class, () -> procedimentService.findAmbEntitat(99L, "filtre"));
    }

    @Test
    public void findAmbEntitatAmbFiltre_existeix_retornaLlista() throws EntitatNotFoundException {
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitatMock));
        when(procedimentRepository.findByEntitatAndFiltreOrderByNomAsc(eq(entitatMock), anyBoolean(), any())).thenReturn(Collections.singletonList(procedimentMock));
        ProcedimentDto dto = new ProcedimentDto();
        when(mapperFacade.mapAsList(anyList(), eq(ProcedimentDto.class))).thenReturn(Collections.singletonList(dto));

        List<ProcedimentDto> result = procedimentService.findAmbEntitat(1L, "filtre");

        assertEquals(1, result.size());
    }

    // ==================== findAmbEntitatPerOrigen ====================

    @Test
    public void findAmbEntitatPerOrigen_noExisteix_llancaException() {
        when(entitatRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntitatNotFoundException.class, () -> procedimentService.findAmbEntitatPerOrigen(99L));
    }

    @Test
    public void findAmbEntitatPerOrigen_existeix_retornaCodiValors() throws EntitatNotFoundException {
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitatMock));
        when(procedimentMock.getCodiSia()).thenReturn("SIA001");
        when(procedimentMock.getCodi()).thenReturn("PROC01");
        when(procedimentMock.getNom()).thenReturn("Procediment Test");
        when(procedimentRepository.findByEntitatIdPerOrigen(1L)).thenReturn(Collections.singletonList(procedimentMock));

        List<CodiValor> result = procedimentService.findAmbEntitatPerOrigen(1L);

        assertEquals(1, result.size());
        assertEquals("SIA001", result.get(0).getCodi());
    }

    @Test
    public void findAmbEntitatPerOrigen_llistaNula_retornaLlistaVuida() throws EntitatNotFoundException {
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitatMock));
        when(procedimentRepository.findByEntitatIdPerOrigen(1L)).thenReturn(null);

        List<CodiValor> result = procedimentService.findAmbEntitatPerOrigen(1L);

        assertTrue(result.isEmpty());
    }

    // ==================== findAmbEntitatPerFills ====================

    @Test
    public void findAmbEntitatPerFills_noExisteix_llancaException() {
        when(entitatRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntitatNotFoundException.class, () -> procedimentService.findAmbEntitatPerFills(99L, "SIA01"));
    }

    @Test
    public void findAmbEntitatPerFills_existeix_retornaCodiValors() throws EntitatNotFoundException {
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitatMock));
        when(procedimentMock.getCodiSia()).thenReturn("SIA002");
        when(procedimentMock.getCodi()).thenReturn("PROC02");
        when(procedimentMock.getNom()).thenReturn("Procediment fill");
        when(procedimentRepository.findByEntitatIdPerFills(1L, "SIA01")).thenReturn(Collections.singletonList(procedimentMock));

        List<CodiValor> result = procedimentService.findAmbEntitatPerFills(1L, "SIA01");

        assertEquals(1, result.size());
        assertEquals("SIA002", result.get(0).getCodi());
    }

    @Test
    public void findAmbEntitatPerFills_llistaNula_retornaLlistaVuida() throws EntitatNotFoundException {
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitatMock));
        when(procedimentRepository.findByEntitatIdPerFills(1L, "SIA01")).thenReturn(null);

        List<CodiValor> result = procedimentService.findAmbEntitatPerFills(1L, "SIA01");

        assertTrue(result.isEmpty());
    }

    // ==================== findCodiSiaFills ====================

    @Test
    public void findCodiSiaFills_entitatNoExisteix_llancaException() {
        when(entitatRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntitatNotFoundException.class, () -> procedimentService.findCodiSiaFills(99L, "SIA01"));
    }

    @Test
    public void findCodiSiaFills_existeix_retornaLlista() throws EntitatNotFoundException {
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitatMock));
        when(procedimentRepository.findCodiSiaByEntitatAndCodiSiaOrigen(1L, "SIA01")).thenReturn(Collections.singletonList("SIA02"));

        List<String> result = procedimentService.findCodiSiaFills(1L, "SIA01");

        assertEquals(1, result.size());
        assertEquals("SIA02", result.get(0));
    }

    // ==================== findAmbFiltrePaginat ====================

    @Test
    public void findAmbFiltrePaginat_entitatNoExisteix_llancaException() {
        when(entitatRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntitatNotFoundException.class, () -> procedimentService.findAmbFiltrePaginat(
                99L, null, null, null, null, null, null, new PaginacioAmbOrdreDto()));
    }

    @Test
    public void findAmbFiltrePaginat_ambOrganGestorIActiuFiltrats_retornaPagina() throws EntitatNotFoundException {
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitatMock));
        OrganGestor organGestor = mock(OrganGestor.class);
        when(organGestorRepository.findById(5L)).thenReturn(Optional.of(organGestor));
        Pageable pageable = PageRequest.of(0, 10);
        when(paginacioHelper.toSpringDataPageable(any(), any())).thenReturn(pageable);
        Page<Procediment> page = new PageImpl<>(Collections.singletonList(procedimentMock), pageable, 1);
        when(procedimentRepository.findByFiltre(
                eq(entitatMock), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(),
                eq(false), eq(organGestor), anyBoolean(), any(), anyBoolean(), anyBoolean(), eq(pageable)))
                .thenReturn(page);
        Page<ProcedimentDto> pageDto = new PageImpl<>(Collections.singletonList(new ProcedimentDto()));
        when(dtoMappingHelper.pageEntities2pageDto(page, ProcedimentDto.class, pageable)).thenReturn(pageDto);

        Page<ProcedimentDto> result = procedimentService.findAmbFiltrePaginat(
                1L, "COD", "NOM", "DEP", 5L, "SIA", FiltreActiuEnumDto.ACTIU, new PaginacioAmbOrdreDto());

        assertEquals(1, result.getTotalElements());
    }

    @Test
    public void findAmbFiltrePaginat_senseOrganGestorAmbInactiu_retornaPagina() throws EntitatNotFoundException {
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitatMock));
        Pageable pageable = PageRequest.of(0, 10);
        when(paginacioHelper.toSpringDataPageable(any(), any())).thenReturn(pageable);
        Page<Procediment> page = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(procedimentRepository.findByFiltre(
                eq(entitatMock), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(),
                eq(true), isNull(), anyBoolean(), any(), anyBoolean(), eq(false), eq(pageable)))
                .thenReturn(page);
        Page<ProcedimentDto> pageDto = new PageImpl<>(Collections.emptyList());
        when(dtoMappingHelper.pageEntities2pageDto(page, ProcedimentDto.class, pageable)).thenReturn(pageDto);

        Page<ProcedimentDto> result = procedimentService.findAmbFiltrePaginat(
                1L, null, null, null, null, null, FiltreActiuEnumDto.INACTIU, new PaginacioAmbOrdreDto());

        assertEquals(0, result.getTotalElements());
    }

    // ==================== findAmbEntitatICodi ====================

    @Test
    public void findAmbEntitatICodi_entitatNoExisteix_llancaException() {
        when(entitatRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntitatNotFoundException.class, () -> procedimentService.findAmbEntitatICodi(99L, "PROC01"));
    }

    @Test
    public void findAmbEntitatICodi_existeix_retornaDto() throws EntitatNotFoundException {
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitatMock));
        when(procedimentRepository.findByEntitatAndCodi(entitatMock, "PROC01")).thenReturn(procedimentMock);
        ProcedimentDto dto = new ProcedimentDto();
        when(mapperFacade.map(procedimentMock, ProcedimentDto.class)).thenReturn(dto);

        ProcedimentDto result = procedimentService.findAmbEntitatICodi(1L, "PROC01");

        assertNotNull(result);
    }

    // ==================== findById ====================

    @Test
    public void findById_existeix_retornaDto() throws ProcedimentNotFoundException {
        when(procedimentRepository.findById(1L)).thenReturn(Optional.of(procedimentMock));
        ProcedimentDto dto = new ProcedimentDto();
        when(mapperFacade.map(procedimentMock, ProcedimentDto.class)).thenReturn(dto);

        ProcedimentDto result = procedimentService.findById(1L);

        assertNotNull(result);
    }

    @Test
    public void findById_noExisteix_retornaNull() {
        when(procedimentRepository.findById(99L)).thenReturn(Optional.empty());
        ProcedimentDto result = procedimentService.findById(99L);
        assertNull(result);
    }

    // ==================== update ====================

    @Test
    public void update_noExisteix_llancaException() {
        ProcedimentDto dto = new ProcedimentDto();
        dto.setId(99L);
        when(procedimentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ProcedimentNotFoundException.class, () -> procedimentService.update(dto));
    }

    @Test
    public void update_existeix_actualitzaIRetornaDto() throws ProcedimentNotFoundException {
        ProcedimentDto dto = new ProcedimentDto();
        dto.setId(1L);
        dto.setCodi("PROC01-U");
        dto.setNom("Nom actualitzat");
        dto.setOrganGestor(OrganGestorDto.builder().id(7L).build());
        dto.setCodiSia("SIA-U");
        OrganGestor organGestor = mock(OrganGestor.class);
        when(organGestorRepository.getOne(7L)).thenReturn(organGestor);
        when(procedimentRepository.findById(1L)).thenReturn(Optional.of(procedimentMock));
        when(procedimentRepository.findByEntitatAndCodiSiaOrigen(eq(entitatMock), any())).thenReturn(Collections.emptyList());
        ProcedimentDto respostaDto = new ProcedimentDto();
        when(mapperFacade.map(procedimentMock, ProcedimentDto.class)).thenReturn(respostaDto);

        ProcedimentDto result = procedimentService.update(dto);

        assertNotNull(result);
        verify(procedimentMock).update("PROC01-U", "Nom actualitzat", null, organGestor, "SIA-U", null, null);
    }

    // ==================== updateActiu ====================

    @Test
    public void updateActiu_noExisteix_llancaException() {
        when(procedimentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ProcedimentNotFoundException.class, () -> procedimentService.updateActiu(99L, true));
    }

    @Test
    public void updateActiu_existeix_actualitzaIRetornaDto() throws ProcedimentNotFoundException {
        when(procedimentRepository.findById(1L)).thenReturn(Optional.of(procedimentMock));
        ProcedimentDto dto = new ProcedimentDto();
        when(mapperFacade.map(procedimentMock, ProcedimentDto.class)).thenReturn(dto);

        ProcedimentDto result = procedimentService.updateActiu(1L, false);

        assertNotNull(result);
        verify(procedimentMock).updateActiu(false);
        verify(cacheHelper).evictProcedimentsPerEntitat("ENT01");
    }

    // ==================== serveiEnable ====================

    @Test
    public void serveiEnable_procedimentNoExisteix_llancaException() {
        when(procedimentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ProcedimentNotFoundException.class, () -> procedimentService.serveiEnable(99L, "SV01"));
    }

    @Test
    public void serveiEnable_serveiNoActiuPerEntitat_llancaException() {
        when(procedimentMock.getId()).thenReturn(1L);
        when(procedimentRepository.findById(1L)).thenReturn(Optional.of(procedimentMock));
        when(entitatMock.getId()).thenReturn(10L);
        when(entitatServeiRepository.findByEntitatIdAndServei(10L, "SV01")).thenReturn(null);
        assertThrows(ServeiNotFoundException.class, () -> procedimentService.serveiEnable(1L, "SV01"));
    }

    @Test
    public void serveiEnable_procedimentServeiExistent_actualitzaActiu() throws Exception {
        when(procedimentMock.getId()).thenReturn(1L);
        when(procedimentRepository.findById(1L)).thenReturn(Optional.of(procedimentMock));
        when(entitatMock.getId()).thenReturn(10L);
        EntitatServei entitatServei = mock(EntitatServei.class);
        when(entitatServeiRepository.findByEntitatIdAndServei(10L, "SV01")).thenReturn(entitatServei);
        ProcedimentServei procedimentServei = mock(ProcedimentServei.class);
        when(procedimentServei.getId()).thenReturn(50L);
        when(procedimentServeiRepository.findByProcedimentIdAndServei(1L, "SV01")).thenReturn(procedimentServei);
        when(procedimentMock.getCodi()).thenReturn("PROC01");

        procedimentService.serveiEnable(1L, "SV01");

        verify(procedimentServei).updateActiu(true);
        verify(procedimentServeiRepository, never()).save(any());
        verify(cacheHelper).evictServeisProcediment("PROC01");
    }

    @Test
    public void serveiEnable_procedimentServeiInexistent_enCreaUnNou() throws Exception {
        when(procedimentMock.getId()).thenReturn(1L);
        when(procedimentRepository.findById(1L)).thenReturn(Optional.of(procedimentMock));
        when(entitatMock.getId()).thenReturn(10L);
        EntitatServei entitatServei = mock(EntitatServei.class);
        when(entitatServeiRepository.findByEntitatIdAndServei(10L, "SV01")).thenReturn(entitatServei);
        when(procedimentServeiRepository.findByProcedimentIdAndServei(1L, "SV01")).thenReturn(null);
        when(procedimentMock.getCodi()).thenReturn("PROC01");

        procedimentService.serveiEnable(1L, "SV01");

        verify(procedimentServeiRepository).save(any(ProcedimentServei.class));
        verify(cacheHelper).evictServeisProcediment("PROC01");
    }

    // ==================== putProcedimentCodi ====================

    @Test
    public void putProcedimentCodi_procedimentNoExisteix_llancaException() {
        when(procedimentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ProcedimentNotFoundException.class, () -> procedimentService.putProcedimentCodi(99L, "SV01", "COD"));
    }

    @Test
    public void putProcedimentCodi_serveiNoActiuPerEntitat_llancaException() {
        when(procedimentRepository.findById(1L)).thenReturn(Optional.of(procedimentMock));
        when(entitatMock.getId()).thenReturn(10L);
        when(entitatServeiRepository.findByEntitatIdAndServei(10L, "SV01")).thenReturn(null);
        assertThrows(ServeiNotFoundException.class, () -> procedimentService.putProcedimentCodi(1L, "SV01", "COD"));
    }

    @Test
    public void putProcedimentCodi_procedimentServeiExistent_actualitzaCodi() throws Exception {
        when(procedimentRepository.findById(1L)).thenReturn(Optional.of(procedimentMock));
        when(entitatMock.getId()).thenReturn(10L);
        EntitatServei entitatServei = mock(EntitatServei.class);
        when(entitatServeiRepository.findByEntitatIdAndServei(10L, "SV01")).thenReturn(entitatServei);
        ProcedimentServei procedimentServei = mock(ProcedimentServei.class);
        when(procedimentServei.getId()).thenReturn(50L);
        when(procedimentServeiRepository.findByProcedimentIdAndServei(1L, "SV01")).thenReturn(procedimentServei);

        boolean result = procedimentService.putProcedimentCodi(1L, "SV01", "COD01");

        assertTrue(result);
        verify(procedimentServei).updateProcedimentCodi("COD01");
        verify(procedimentServeiRepository, never()).save(any());
    }

    @Test
    public void putProcedimentCodi_procedimentServeiInexistent_enCreaUnNouAmbCodi() throws Exception {
        when(procedimentRepository.findById(1L)).thenReturn(Optional.of(procedimentMock));
        when(entitatMock.getId()).thenReturn(10L);
        EntitatServei entitatServei = mock(EntitatServei.class);
        when(entitatServeiRepository.findByEntitatIdAndServei(10L, "SV01")).thenReturn(entitatServei);
        when(procedimentServeiRepository.findByProcedimentIdAndServei(1L, "SV01")).thenReturn(null);

        boolean result = procedimentService.putProcedimentCodi(1L, "SV01", "COD01");

        assertTrue(result);
        verify(procedimentServeiRepository).save(any(ProcedimentServei.class));
    }

    // ==================== serveiDisable ====================

    @Test
    public void serveiDisable_procedimentNoExisteix_llancaException() {
        when(procedimentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ProcedimentNotFoundException.class, () -> procedimentService.serveiDisable(99L, "SV01"));
    }

    @Test
    public void serveiDisable_procedimentServeiNoExisteix_llancaException() {
        when(procedimentRepository.findById(1L)).thenReturn(Optional.of(procedimentMock));
        when(procedimentServeiRepository.findByProcedimentIdAndServei(1L, "SV01")).thenReturn(null);
        assertThrows(ProcedimentServeiNotFoundException.class, () -> procedimentService.serveiDisable(1L, "SV01"));
    }

    @Test
    public void serveiDisable_existeix_desactivaIRevocaPermisos() throws Exception {
        when(procedimentRepository.findById(1L)).thenReturn(Optional.of(procedimentMock));
        ProcedimentServei procedimentServei = mock(ProcedimentServei.class);
        when(procedimentServei.getId()).thenReturn(50L);
        when(procedimentServeiRepository.findByProcedimentIdAndServei(1L, "SV01")).thenReturn(procedimentServei);
        when(procedimentMock.getCodi()).thenReturn("PROC01");
        stubAclNotFound();

        procedimentService.serveiDisable(1L, "SV01");

        verify(procedimentServei).updateActiu(false);
        verify(cacheHelper).evictServeisProcediment("PROC01");
    }

    // ==================== serveiPermisAllow ====================

    @Test
    public void serveiPermisAllow_procedimentNoExisteix_llancaException() {
        when(procedimentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ProcedimentNotFoundException.class, () -> procedimentService.serveiPermisAllow(99L, "SV01", "user1"));
    }

    @Test
    public void serveiPermisAllow_procedimentServeiNoExisteix_llancaException() {
        when(procedimentRepository.findById(1L)).thenReturn(Optional.of(procedimentMock));
        when(procedimentServeiRepository.findByProcedimentIdAndServei(1L, "SV01")).thenReturn(null);
        assertThrows(ProcedimentServeiNotFoundException.class, () -> procedimentService.serveiPermisAllow(1L, "SV01", "user1"));
    }

    @Test
    public void serveiPermisAllow_entitatUsuariNoExisteix_llancaException() {
        when(procedimentRepository.findById(1L)).thenReturn(Optional.of(procedimentMock));
        ProcedimentServei procedimentServei = mock(ProcedimentServei.class);
        when(procedimentServei.getId()).thenReturn(50L);
        when(procedimentServeiRepository.findByProcedimentIdAndServei(1L, "SV01")).thenReturn(procedimentServei);
        when(entitatMock.getId()).thenReturn(10L);
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(10L, "user1")).thenReturn(null);
        assertThrows(EntitatUsuariNotFoundException.class, () -> procedimentService.serveiPermisAllow(1L, "SV01", "user1"));
    }

    @Test
    public void serveiPermisAllow_totCorrecte_assignaPermisIBuidaCache() throws Exception {
        when(procedimentRepository.findById(1L)).thenReturn(Optional.of(procedimentMock));
        ProcedimentServei procedimentServei = mock(ProcedimentServei.class);
        when(procedimentServei.getId()).thenReturn(50L);
        when(procedimentServeiRepository.findByProcedimentIdAndServei(1L, "SV01")).thenReturn(procedimentServei);
        when(entitatMock.getId()).thenReturn(10L);
        EntitatUsuari entitatUsuari = mock(EntitatUsuari.class);
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(10L, "user1")).thenReturn(entitatUsuari);
        stubAclNotFound();
        MutableAcl novaAcl = mock(MutableAcl.class);
        when(aclService.createAcl(any())).thenReturn(novaAcl);
        when(novaAcl.getEntries()).thenReturn(Collections.emptyList());

        procedimentService.serveiPermisAllow(1L, "SV01", "user1");

        verify(aclService).updateAcl(novaAcl);
        verify(cacheHelper).evictPermisosPerDelegat("user1");
    }

    // ==================== serveiPermisDeny ====================

    @Test
    public void serveiPermisDeny_procedimentNoExisteix_llancaException() {
        when(procedimentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ProcedimentNotFoundException.class, () -> procedimentService.serveiPermisDeny(99L, "SV01", "user1"));
    }

    @Test
    public void serveiPermisDeny_procedimentServeiNoExisteix_llancaException() {
        when(procedimentRepository.findById(1L)).thenReturn(Optional.of(procedimentMock));
        when(procedimentServeiRepository.findByProcedimentIdAndServei(1L, "SV01")).thenReturn(null);
        assertThrows(ProcedimentServeiNotFoundException.class, () -> procedimentService.serveiPermisDeny(1L, "SV01", "user1"));
    }

    @Test
    public void serveiPermisDeny_entitatUsuariNoExisteix_llancaException() {
        when(procedimentRepository.findById(1L)).thenReturn(Optional.of(procedimentMock));
        ProcedimentServei procedimentServei = mock(ProcedimentServei.class);
        when(procedimentServei.getId()).thenReturn(50L);
        when(procedimentServeiRepository.findByProcedimentIdAndServei(1L, "SV01")).thenReturn(procedimentServei);
        when(entitatMock.getId()).thenReturn(10L);
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(10L, "user1")).thenReturn(null);
        assertThrows(EntitatUsuariNotFoundException.class, () -> procedimentService.serveiPermisDeny(1L, "SV01", "user1"));
    }

    @Test
    public void serveiPermisDeny_totCorrecte_revocaPermisIBuidaCache() throws Exception {
        when(procedimentRepository.findById(1L)).thenReturn(Optional.of(procedimentMock));
        ProcedimentServei procedimentServei = mock(ProcedimentServei.class);
        when(procedimentServei.getId()).thenReturn(50L);
        when(procedimentServeiRepository.findByProcedimentIdAndServei(1L, "SV01")).thenReturn(procedimentServei);
        when(entitatMock.getId()).thenReturn(10L);
        EntitatUsuari entitatUsuari = mock(EntitatUsuari.class);
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(10L, "user1")).thenReturn(entitatUsuari);
        AccessControlEntry ace = mock(AccessControlEntry.class);
        when(ace.getSid()).thenReturn(new PrincipalSid("user1"));
        when(ace.getPermission()).thenReturn(BasePermission.READ);
        MutableAcl acl = stubMutableAclAmbEntrades(new ArrayList<>(Collections.singletonList(ace)));

        procedimentService.serveiPermisDeny(1L, "SV01", "user1");

        verify(acl).deleteAce(0);
        verify(cacheHelper).evictPermisosPerDelegat("user1");
    }

    // ==================== serveiPermisDenyAll ====================

    @Test
    public void serveiPermisDenyAll_entitatUsuariNoExisteix_llancaException() {
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(1L, "user1")).thenReturn(null);
        assertThrows(EntitatUsuariNotFoundException.class, () -> procedimentService.serveiPermisDenyAll("user1", 1L));
    }

    @Test
    public void serveiPermisDenyAll_capPermisConcedit_noFaRes() throws Exception {
        EntitatUsuari entitatUsuari = mock(EntitatUsuari.class);
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(1L, "user1")).thenReturn(entitatUsuari);
        ProcedimentServei ps = mock(ProcedimentServei.class);
		when(ps.getId()).thenReturn(1L);
        when(procedimentServeiRepository.findByEntitatId(1L)).thenReturn(new ArrayList<>(Collections.singletonList(ps)));
        stubAclGranted(false);
		when(usuariHelper.generarUsuariAutenticat(anyString(), anyBoolean()))
			.thenAnswer(invocation -> stubAuthUsuari(invocation.getArgument(0)));

        procedimentService.serveiPermisDenyAll("user1", 1L);

        verify(cacheHelper, never()).evictPermisosPerDelegat(anyString());
    }

    @Test
    public void serveiPermisDenyAll_ambPermisosConcedits_elsRevocaIBuidaCache() throws Exception {
        EntitatUsuari entitatUsuari = mock(EntitatUsuari.class);
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(1L, "user1")).thenReturn(entitatUsuari);
        ProcedimentServei ps = mock(ProcedimentServei.class);
        when(ps.getId()).thenReturn(50L);
        when(procedimentServeiRepository.findByEntitatId(1L)).thenReturn(new ArrayList<>(Collections.singletonList(ps)));
        stubAclGranted(true);
        Authentication authUsuari1 = stubAuthUsuari("user1");
        when(usuariHelper.generarUsuariAutenticat("user1", false)).thenReturn(authUsuari1);

        procedimentService.serveiPermisDenyAll("user1", 1L);

        verify(cacheHelper).evictPermisosPerDelegat("user1");
    }

    // ==================== serveiPermisAllowSelected ====================

    @Test
    public void serveiPermisAllowSelected_entitatUsuariNoExisteix_llancaException() {
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(1L, "user1")).thenReturn(null);
        assertThrows(EntitatUsuariNotFoundException.class,
                () -> procedimentService.serveiPermisAllowSelected("user1", Collections.emptyList(), 1L));
    }

    @Test
    public void serveiPermisAllowSelected_procedimentServeiNoExisteix_llancaException() {
        EntitatUsuari entitatUsuari = mock(EntitatUsuari.class);
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(1L, "user1")).thenReturn(entitatUsuari);
        ProcedimentServeiSimpleDto seleccio = ProcedimentServeiSimpleDto.builder().procedimentCodi("PROC01").serveiCodi("SV01").build();
        when(procedimentServeiRepository.findByEntitatIdProcedimentCodiAndServeiCodi(1L, "PROC01", "SV01")).thenReturn(null);
        assertThrows(ProcedimentServeiNotFoundException.class,
                () -> procedimentService.serveiPermisAllowSelected("user1", Collections.singletonList(seleccio), 1L));
    }

    @Test
    public void serveiPermisAllowSelected_totCorrecte_assignaPermisos() throws Exception {
        EntitatUsuari entitatUsuari = mock(EntitatUsuari.class);
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(1L, "user1")).thenReturn(entitatUsuari);
        ProcedimentServeiSimpleDto seleccio = ProcedimentServeiSimpleDto.builder().procedimentCodi("PROC01").serveiCodi("SV01").build();
        ProcedimentServei procedimentServei = mock(ProcedimentServei.class);
        when(procedimentServei.getId()).thenReturn(50L);
        when(procedimentServeiRepository.findByEntitatIdProcedimentCodiAndServeiCodi(1L, "PROC01", "SV01")).thenReturn(procedimentServei);
        stubAclNotFound();
        MutableAcl novaAcl = mock(MutableAcl.class);
        when(aclService.createAcl(any())).thenReturn(novaAcl);
        when(novaAcl.getEntries()).thenReturn(Collections.emptyList());

        procedimentService.serveiPermisAllowSelected("user1", Collections.singletonList(seleccio), 1L);

        verify(cacheHelper).evictPermisosPerDelegat("user1");
    }

    // ==================== serveiPermisDenySelected ====================

    @Test
    public void serveiPermisDenySelected_entitatUsuariNoExisteix_llancaException() {
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(1L, "user1")).thenReturn(null);
        assertThrows(EntitatUsuariNotFoundException.class,
                () -> procedimentService.serveiPermisDenySelected("user1", Collections.emptyList(), 1L));
    }

    @Test
    public void serveiPermisDenySelected_ambSeleccioCoincident_revocaPermis() throws Exception {
        EntitatUsuari entitatUsuari = mock(EntitatUsuari.class);
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(1L, "user1")).thenReturn(entitatUsuari);
        ProcedimentServei ps = mock(ProcedimentServei.class);
        when(ps.getId()).thenReturn(50L);
        Procediment proc = mock(Procediment.class);
        when(proc.getCodi()).thenReturn("PROC01");
        when(ps.getProcediment()).thenReturn(proc);
        Servei servei = mock(Servei.class);
        when(servei.getCodi()).thenReturn("SV01");
        when(ps.getServeiScsp()).thenReturn(servei);
        when(procedimentServeiRepository.findByEntitatId(1L)).thenReturn(new ArrayList<>(Collections.singletonList(ps)));
        stubAclGranted(true);
        Authentication authUsuari1 = stubAuthUsuari("user1");
        when(usuariHelper.generarUsuariAutenticat("user1", false)).thenReturn(authUsuari1);

        ProcedimentServeiSimpleDto seleccio = ProcedimentServeiSimpleDto.builder().procedimentCodi("PROC01").serveiCodi("SV01").build();

        procedimentService.serveiPermisDenySelected("user1", Collections.singletonList(seleccio), 1L);

        verify(cacheHelper).evictPermisosPerDelegat("user1");
    }

    @Test
    public void serveiPermisDenySelected_senseSeleccioCoincident_noRevoca() throws Exception {
        EntitatUsuari entitatUsuari = mock(EntitatUsuari.class);
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(1L, "user1")).thenReturn(entitatUsuari);
        ProcedimentServei ps = mock(ProcedimentServei.class);
        when(ps.getId()).thenReturn(50L);
        Procediment proc = mock(Procediment.class);
        when(proc.getCodi()).thenReturn("PROC01");
        when(ps.getProcediment()).thenReturn(proc);
        Servei servei = mock(Servei.class);
        when(servei.getCodi()).thenReturn("SV01");
        when(ps.getServeiScsp()).thenReturn(servei);
        when(procedimentServeiRepository.findByEntitatId(1L)).thenReturn(new ArrayList<>(Collections.singletonList(ps)));
        stubAclGranted(true);
        Authentication authUsuari1 = stubAuthUsuari("user1");
        when(usuariHelper.generarUsuariAutenticat("user1", false)).thenReturn(authUsuari1);

        ProcedimentServeiSimpleDto seleccioDiferent = ProcedimentServeiSimpleDto.builder().procedimentCodi("ALTRE").serveiCodi("SV99").build();

        procedimentService.serveiPermisDenySelected("user1", Collections.singletonList(seleccioDiferent), 1L);

        // La cache es buida sempre que la llista de serveis amb permís no sigui buida,
        // independentment de si la selecció ha coincidit; el que NO s'ha de fer és revocar l'ACL.
        verify(aclService, never()).updateAcl(any());
        verify(cacheHelper).evictPermisosPerDelegat("user1");
    }

    // ==================== serveiDisponibles ====================

    @Test
    public void serveiDisponibles_entitatUsuariNoExisteix_llancaException() {
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(1L, "user1")).thenReturn(null);
        assertThrows(EntitatUsuariNotFoundException.class,
                () -> procedimentService.serveiDisponibles("user1", null, 1L));
    }

    @Test
    public void serveiDisponibles_llistaBuida_retornaLlistaVuida() throws EntitatUsuariNotFoundException {
        EntitatUsuari entitatUsuari = mock(EntitatUsuari.class);
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(1L, "user1")).thenReturn(entitatUsuari);
        when(procedimentServeiRepository.findByEntitatId(1L)).thenReturn(Collections.emptyList());

        List<ProcedimentServeiNomDto> result = procedimentService.serveiDisponibles("user1", null, 1L);

        assertTrue(result.isEmpty());
    }

    @Test
    public void serveiDisponibles_ambProcedimentIdIPermisNoConcedit_retornaDisponible() throws EntitatUsuariNotFoundException {
        EntitatUsuari entitatUsuari = mock(EntitatUsuari.class);
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(1L, "user1")).thenReturn(entitatUsuari);
        ProcedimentServei ps = mock(ProcedimentServei.class);
        when(ps.getId()).thenReturn(50L);
        Procediment proc = mock(Procediment.class);
        when(proc.getCodi()).thenReturn("PROC01");
        when(proc.getNom()).thenReturn("Procediment U");
        when(ps.getProcediment()).thenReturn(proc);
        Servei servei = mock(Servei.class);
        when(servei.getCodi()).thenReturn("SV01");
        when(servei.getDescripcio()).thenReturn("Servei U");
        when(ps.getServeiScsp()).thenReturn(servei);
        when(procedimentServeiRepository.findByEntitatIdAndProcedimentId(1L, 2L)).thenReturn(new ArrayList<>(Collections.singletonList(ps)));
        stubAclGranted(false);
        Authentication authUsuari1 = stubAuthUsuari("user1");
        when(usuariHelper.generarUsuariAutenticat("user1", false)).thenReturn(authUsuari1);

        List<ProcedimentServeiNomDto> result = procedimentService.serveiDisponibles("user1", 2L, 1L);

        assertEquals(1, result.size());
        assertEquals("PROC01", result.get(0).getProcedimentCodi());
        assertEquals("SV01", result.get(0).getServeiCodi());
    }

    // ==================== findUsuarisAmbPermisPerServei (llista) ====================

    @Test
    public void findUsuarisAmbPermisPerServeiLlista_procedimentNoExisteix_llancaException() {
        when(procedimentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ProcedimentNotFoundException.class,
                () -> procedimentService.findUsuarisAmbPermisPerServei(99L, "SV01"));
    }

    @Test
    public void findUsuarisAmbPermisPerServeiLlista_procedimentServeiNoExisteix_llancaException() {
        when(procedimentRepository.findById(1L)).thenReturn(Optional.of(procedimentMock));
        when(procedimentServeiRepository.findByProcedimentIdAndServei(1L, "SV01")).thenReturn(null);
        assertThrows(ProcedimentServeiNotFoundException.class,
                () -> procedimentService.findUsuarisAmbPermisPerServei(1L, "SV01"));
    }

    @Test
    public void findUsuarisAmbPermisPerServeiLlista_sensePermisos_retornaLlistaVuida() throws Exception {
        when(procedimentRepository.findById(1L)).thenReturn(Optional.of(procedimentMock));
        ProcedimentServei procedimentServei = mock(ProcedimentServei.class);
        when(procedimentServei.getId()).thenReturn(50L);
        when(procedimentServeiRepository.findByProcedimentIdAndServei(1L, "SV01")).thenReturn(procedimentServei);
        stubAclNotFound();

        List<EntitatUsuariDto> result = procedimentService.findUsuarisAmbPermisPerServei(1L, "SV01");

        assertTrue(result.isEmpty());
    }

    @Test
    public void findUsuarisAmbPermisPerServeiLlista_ambPermisos_retornaUsuaris() throws Exception {
        when(procedimentRepository.findById(1L)).thenReturn(Optional.of(procedimentMock));
        ProcedimentServei procedimentServei = mock(ProcedimentServei.class);
        when(procedimentServei.getId()).thenReturn(50L);
        when(procedimentServeiRepository.findByProcedimentIdAndServei(1L, "SV01")).thenReturn(procedimentServei);
        AccessControlEntry ace = mock(AccessControlEntry.class);
        when(ace.getSid()).thenReturn(new PrincipalSid("user1"));
        stubMutableAclAmbEntrades(Collections.singletonList(ace));
        when(entitatMock.getId()).thenReturn(10L);
        EntitatUsuari eu = mock(EntitatUsuari.class);
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodis(eq(10L), any(), any(), any(), any(), any()))
                .thenReturn(Collections.singletonList(eu));
		EntitatUsuariDto euDto = new EntitatUsuariDto(
			new UsuariDto(),
			"Departament de Test",
			false,
			false,
			true,
			false,
			false,
			true
		);
        when(dtoMappingHelper.convertirList(anyList(), eq(EntitatUsuariDto.class))).thenReturn(Collections.singletonList(euDto));

        List<EntitatUsuariDto> result = procedimentService.findUsuarisAmbPermisPerServei(1L, "SV01");

        assertEquals(1, result.size());
    }

    // ==================== findUsuarisAmbPermisPerServei (pagina) ====================

    @Test
    public void findUsuarisAmbPermisPerServeiPagina_procedimentNoExisteix_llancaException() {
        when(procedimentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ProcedimentNotFoundException.class,
                () -> procedimentService.findUsuarisAmbPermisPerServei(99L, "SV01", null, null, null, PageRequest.of(0, 10)));
    }

    @Test
    public void findUsuarisAmbPermisPerServeiPagina_procedimentServeiNoExisteix_llancaException() {
        when(procedimentRepository.findById(1L)).thenReturn(Optional.of(procedimentMock));
        when(procedimentServeiRepository.findByProcedimentIdAndServei(1L, "SV01")).thenReturn(null);
        assertThrows(ProcedimentServeiNotFoundException.class,
                () -> procedimentService.findUsuarisAmbPermisPerServei(1L, "SV01", null, null, null, PageRequest.of(0, 10)));
    }

    @Test
    public void findUsuarisAmbPermisPerServeiPagina_sensePermisos_retornaPaginaVuida() throws Exception {
        when(procedimentRepository.findById(1L)).thenReturn(Optional.of(procedimentMock));
        ProcedimentServei procedimentServei = mock(ProcedimentServei.class);
        when(procedimentServei.getId()).thenReturn(50L);
        when(procedimentServeiRepository.findByProcedimentIdAndServei(1L, "SV01")).thenReturn(procedimentServei);
        stubAclNotFound();
        Pageable pageable = PageRequest.of(0, 10);

        Page<EntitatUsuariDto> result = procedimentService.findUsuarisAmbPermisPerServei(1L, "SV01", null, null, null, pageable);

        assertEquals(0, result.getTotalElements());
    }

    @Test
    public void findUsuarisAmbPermisPerServeiPagina_ambPermisos_retornaPagina() throws Exception {
        when(procedimentRepository.findById(1L)).thenReturn(Optional.of(procedimentMock));
        ProcedimentServei procedimentServei = mock(ProcedimentServei.class);
        when(procedimentServei.getId()).thenReturn(50L);
        when(procedimentServeiRepository.findByProcedimentIdAndServei(1L, "SV01")).thenReturn(procedimentServei);
        AccessControlEntry ace = mock(AccessControlEntry.class);
        when(ace.getSid()).thenReturn(new PrincipalSid("user1"));
        stubMutableAclAmbEntrades(Collections.singletonList(ace));
        when(entitatMock.getId()).thenReturn(10L);
        Pageable pageable = PageRequest.of(0, 10);
        EntitatUsuari eu = mock(EntitatUsuari.class);
        Page<EntitatUsuari> pageEntitats = new PageImpl<>(Collections.singletonList(eu), pageable, 1);
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodis(
                eq(10L), any(), any(), any(), any(), any(),
                anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), eq(pageable)))
                .thenReturn(pageEntitats);
		EntitatUsuariDto euDto = new EntitatUsuariDto(
			new UsuariDto(),
			"Departament de Test",
			false,
			false,
			true,
			false,
			false,
			true
		);
        Page<EntitatUsuariDto> pageDto = new PageImpl<>(Collections.singletonList(euDto));
        when(dtoMappingHelper.pageEntities2pageDto(pageEntitats, EntitatUsuariDto.class, pageable)).thenReturn(pageDto);

        Page<EntitatUsuariDto> result = procedimentService.findUsuarisAmbPermisPerServei(1L, "SV01", "COD", "NIF", "NOM", pageable);

        assertEquals(1, result.getTotalElements());
    }

    // ==================== findAmbEntitatPerDelegat ====================

    @Test
    public void findAmbEntitatPerDelegat_entitatNoExisteix_llancaException() {
        when(entitatRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntitatNotFoundException.class, () -> procedimentService.findAmbEntitatPerDelegat(99L));
    }

    @Test
    public void findAmbEntitatPerDelegat_ambPermisConcedit_retornaProcedimentsOrdenats() throws Exception {
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitatMock));
        ProcedimentServei ps = mock(ProcedimentServei.class);
        when(ps.getId()).thenReturn(50L);
        Procediment proc = mock(Procediment.class);
        when(ps.getProcediment()).thenReturn(proc);
        when(procedimentServeiRepository.findActiusByEntitatId(1L)).thenReturn(new ArrayList<>(Collections.singletonList(ps)));
        stubAclGranted(true);

        ProcedimentDto dto1 = new ProcedimentDto();
        dto1.setNom("B Procediment");
        when(mapperFacade.mapAsList(anyCollection(), eq(ProcedimentDto.class))).thenReturn(Collections.singletonList(dto1));

        List<ProcedimentDto> result = procedimentService.findAmbEntitatPerDelegat(1L);

        assertEquals(1, result.size());
    }

    // ==================== findActiusAmbEntitatIServeiCodi ====================

    @Test
    public void findActiusAmbEntitatIServeiCodi_entitatNoExisteix_llancaException() {
        when(entitatRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntitatNotFoundException.class, () -> procedimentService.findActiusAmbEntitatIServeiCodi(99L, "SV01"));
    }

    @Test
    public void findActiusAmbEntitatIServeiCodi_ambPermisConcedit_retornaProcediments() throws Exception {
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitatMock));
        ProcedimentServei ps = mock(ProcedimentServei.class);
        when(ps.getId()).thenReturn(50L);
        Procediment proc = mock(Procediment.class);
        when(ps.getProcediment()).thenReturn(proc);
        when(procedimentServeiRepository.findActiusByEntitatIdAndServei(1L, "SV01")).thenReturn(new ArrayList<>(Collections.singletonList(ps)));
        stubAclGranted(true);
        when(mapperFacade.mapAsList(anyList(), eq(ProcedimentDto.class))).thenReturn(Collections.singletonList(new ProcedimentDto()));

        List<ProcedimentDto> result = procedimentService.findActiusAmbEntitatIServeiCodi(1L, "SV01");

        assertEquals(1, result.size());
    }

    // ==================== findAmbServeiCodi ====================

    @Test
    public void findAmbServeiCodi_retornaLlista() {
        when(procedimentRepository.findAllByServei("SV01")).thenReturn(Collections.singletonList(procedimentMock));
        when(mapperFacade.mapAsList(anyList(), eq(ProcedimentDto.class))).thenReturn(Collections.singletonList(new ProcedimentDto()));

        List<ProcedimentDto> result = procedimentService.findAmbServeiCodi("SV01");

        assertEquals(1, result.size());
    }

    // ==================== informeProcedimentsAgrupatsEntitatDepartament ====================

    @Test
    public void informeProcedimentsAgrupatsEntitatDepartament_retornaInforme() {
        when(procedimentRepository.findAllOrderByEntitatAndDepartament()).thenReturn(Collections.singletonList(procedimentMock));
        InformeProcedimentDto informeDto = new InformeProcedimentDto();
        when(mapperFacade.map(procedimentMock, InformeProcedimentDto.class)).thenReturn(informeDto);

        List<InformeProcedimentDto> result = procedimentService.informeProcedimentsAgrupatsEntitatDepartament();

        assertEquals(1, result.size());
    }

    // ==================== findAll ====================

    @Test
    public void findAll_retornaLlista() {
        when(procedimentRepository.findAll()).thenReturn(Collections.singletonList(procedimentMock));
        when(mapperFacade.mapAsList(anyList(), eq(ProcedimentDto.class))).thenReturn(Collections.singletonList(new ProcedimentDto()));

        List<ProcedimentDto> result = procedimentService.findAll();

        assertEquals(1, result.size());
    }

    // ==================== serveisDisponiblesPerProcediment ====================

    @Test
    public void serveisDisponiblesPerProcediment_procedimentNoExisteix_llancaException() {
        when(procedimentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ProcedimentNotFoundException.class, () -> procedimentService.serveisDisponiblesPerProcediment(99L));
    }

    @Test
    public void serveisDisponiblesPerProcediment_llistaNula_retornaLlistaVuida() throws ProcedimentNotFoundException {
        when(procedimentRepository.findById(1L)).thenReturn(Optional.of(procedimentMock));
        when(serveiRepository.findActiuNotInProcediment(1L)).thenReturn(null);

        List<ServeiDto> result = procedimentService.serveisDisponiblesPerProcediment(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    public void serveisDisponiblesPerProcediment_ambServeisActiusIInactius_retornaLlista() throws ProcedimentNotFoundException {
        when(procedimentRepository.findById(1L)).thenReturn(Optional.of(procedimentMock));
        Servei serveiActiu = mock(Servei.class);
        when(serveiActiu.getId()).thenReturn(1L);
        when(serveiActiu.getCodi()).thenReturn("SV01");
        when(serveiActiu.getDescripcio()).thenReturn("Servei actiu");
        Servei serveiInactiu = mock(Servei.class);
        when(serveiInactiu.getId()).thenReturn(2L);
        when(serveiInactiu.getCodi()).thenReturn("SV02");
        when(serveiInactiu.getDescripcio()).thenReturn("Servei inactiu");
        when(serveiRepository.findActiuNotInProcediment(1L)).thenReturn(Arrays.asList(serveiActiu, serveiInactiu));
        when(serveiConfigRepository.findByActiuFalse()).thenReturn(Collections.singletonList("SV02"));
        when(procedimentMock.getEntitat()).thenReturn(entitatMock);
        when(entitatMock.getServeis()).thenReturn(Arrays.asList("SV01", "SV02"));

        List<ServeiDto> result = procedimentService.serveisDisponiblesPerProcediment(1L);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(s -> s.getCodi().equals("SV01") && s.getActiu()));
        assertTrue(result.stream().anyMatch(s -> s.getCodi().equals("SV02") && !s.getActiu()));
    }

    // ==================== migrarProcedimentServei ====================

    @Test
    public void migrarProcedimentServei_procedimentNoExisteix_llancaException() {
        when(procedimentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ProcedimentNotFoundException.class,
                () -> procedimentService.migrarProcedimentServei(99L, "SV01", "SV02"));
    }

    @Test
    public void migrarProcedimentServei_origenNoExisteix_llancaException() {
        when(procedimentRepository.findById(1L)).thenReturn(Optional.of(procedimentMock));
        when(procedimentServeiRepository.findByProcedimentIdAndServei(1L, "SV01")).thenReturn(null);
        assertThrows(ProcedimentServeiNotFoundException.class,
                () -> procedimentService.migrarProcedimentServei(1L, "SV01", "SV02"));
    }

    @Test
    public void migrarProcedimentServei_destiInexistent_elCreaIMigraSenseEsborrarOrigen() throws Exception {
        when(procedimentRepository.findById(1L)).thenReturn(Optional.of(procedimentMock));
        ProcedimentServei origen = mock(ProcedimentServei.class);
        when(origen.getId()).thenReturn(10L);
        when(procedimentServeiRepository.findByProcedimentIdAndServei(1L, "SV01")).thenReturn(origen);
        when(procedimentServeiRepository.findByProcedimentIdAndServei(1L, "SV02")).thenReturn(null);
        ProcedimentServei destiCreat = mock(ProcedimentServei.class);
        when(destiCreat.getId()).thenReturn(20L);
        when(procedimentServeiRepository.save(any(ProcedimentServei.class))).thenReturn(destiCreat);
        stubAclNotFound();
        when(procedimentServeiRepository.hasConsultes(origen)).thenReturn(true);
        when(procedimentServeiRepository.hasHistoricConsultes(origen)).thenReturn(false);
        when(procedimentMock.getCodi()).thenReturn("PROC01");

        procedimentService.migrarProcedimentServei(1L, "SV01", "SV02");

        verify(destiCreat).updateActiu(true);
        verify(origen).updateActiu(false);
        verify(procedimentServeiRepository, never()).delete(origen);
        verify(cacheHelper).evictPermisosPerDelegat();
        verify(cacheHelper).evictServeisProcediment("PROC01");
    }

    @Test
    public void migrarProcedimentServei_destiExistentSenseConsultesOrigen_esborraOrigen() throws Exception {
        when(procedimentRepository.findById(1L)).thenReturn(Optional.of(procedimentMock));
        ProcedimentServei origen = mock(ProcedimentServei.class);
        when(origen.getId()).thenReturn(10L);
        ProcedimentServei desti = mock(ProcedimentServei.class);
        when(desti.getId()).thenReturn(20L);
        when(procedimentServeiRepository.findByProcedimentIdAndServei(1L, "SV01")).thenReturn(origen);
        when(procedimentServeiRepository.findByProcedimentIdAndServei(1L, "SV02")).thenReturn(desti);
        stubAclNotFound();
        when(procedimentServeiRepository.hasConsultes(origen)).thenReturn(false);
        when(procedimentServeiRepository.hasHistoricConsultes(origen)).thenReturn(false);
        when(procedimentMock.getCodi()).thenReturn("PROC01");

        procedimentService.migrarProcedimentServei(1L, "SV01", "SV02");

        verify(procedimentServeiRepository, never()).save(any());
        verify(desti).updateActiu(true);
        verify(procedimentServeiRepository).delete(origen);
    }

    @Test
    public void migrarProcedimentServei_errorAlProcessar_esPropagaExcepcio() {
        when(procedimentRepository.findById(1L)).thenReturn(Optional.of(procedimentMock));
        ProcedimentServei origen = mock(ProcedimentServei.class);
        when(origen.getId()).thenReturn(10L);
        when(procedimentServeiRepository.findByProcedimentIdAndServei(1L, "SV01")).thenReturn(origen);
        when(procedimentServeiRepository.findByProcedimentIdAndServei(1L, "SV02")).thenReturn(null);
        when(procedimentServeiRepository.save(any(ProcedimentServei.class))).thenThrow(new RuntimeException("Error BD"));

        assertThrows(RuntimeException.class, () -> procedimentService.migrarProcedimentServei(1L, "SV01", "SV02"));
    }
}
