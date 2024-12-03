package es.caib.pinbal.core.service;

import es.caib.pinbal.client.comu.OptionalField;
import es.caib.pinbal.client.procediments.ClaseTramite;
import es.caib.pinbal.client.procediments.Procediment;
import es.caib.pinbal.client.procediments.ProcedimentPatch;
import es.caib.pinbal.client.serveis.Servei;
import es.caib.pinbal.client.usuaris.FiltreUsuaris;
import es.caib.pinbal.client.usuaris.PermisosServei;
import es.caib.pinbal.client.usuaris.ProcedimentServei;
import es.caib.pinbal.client.usuaris.UsuariEntitat;
import es.caib.pinbal.core.dto.EmisorDto;
import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.EntitatUsuariDto;
import es.caib.pinbal.core.dto.FiltreActiuEnumDto;
import es.caib.pinbal.core.dto.OrganGestorDto;
import es.caib.pinbal.core.dto.PaginacioAmbOrdreDto;
import es.caib.pinbal.core.dto.ProcedimentClaseTramiteEnumDto;
import es.caib.pinbal.core.dto.ProcedimentDto;
import es.caib.pinbal.core.dto.ProcedimentServeiDto;
import es.caib.pinbal.core.dto.ProcedimentServeiSimpleDto;
import es.caib.pinbal.core.dto.ServeiDto;
import es.caib.pinbal.core.dto.UsuariDto;
import es.caib.pinbal.core.model.Entitat;
import es.caib.pinbal.core.model.OrganGestor;
import es.caib.pinbal.core.model.Usuari;
import es.caib.pinbal.core.repository.EntitatRepository;
import es.caib.pinbal.core.repository.OrganGestorRepository;
import es.caib.pinbal.core.repository.ProcedimentRepository;
import es.caib.pinbal.core.repository.UsuariRepository;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatUsuariNotFoundException;
import es.caib.pinbal.core.service.exception.MultiplesUsuarisExternsException;
import es.caib.pinbal.core.service.exception.OrganNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentServeiNotFoundException;
import es.caib.pinbal.core.service.exception.ServeiNotFoundException;
import es.caib.pinbal.core.service.exception.UsuariExternNotFoundException;
import es.caib.pinbal.core.service.exception.UsuariNotFoundException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

//@ActiveProfiles("test")
@RunWith(MockitoJUnitRunner.class)
public class GestioRestServiceImplTest {

    @InjectMocks
    private GestioRestServiceImpl gestioRestServiceImpl = new GestioRestServiceImpl();

    @Mock
    private ProcedimentRepository procedimentRepository;
    @Mock
    private EntitatRepository entitatRepository;
    @Mock
    private OrganGestorRepository organGestorRepository;
    @Mock
    private UsuariRepository usuariRepository;

    @Mock
    private ProcedimentService procedimentService;
    @Mock
    private ServeiService serveiService;
    @Mock
    private UsuariService usuariService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    // CREATE Procediment
    // ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testCreate_Success () throws EntitatNotFoundException, OrganNotFoundException {
        Procediment procediment = Procediment.builder()
                .codi("codi")
                .nom("nom")
                .entitatCodi("entitatCodi")
                .departament("departament")
                .entitatCodi("entitatCodi")
                .organGestorDir3("organCodi")
                .codiSia("sia")
                .valorCampAutomatizado(true)
                .valorCampClaseTramite(ClaseTramite.SANCIONADOR)
                .build();

        Entitat entitat = new Entitat();
        entitat.fillIdForTesting(1L);
        entitat.setCodi("entitatCodi");

        final OrganGestor organGestor = new OrganGestor();
        organGestor.fillIdForTesting(1L);
        organGestor.setCodi("organCodi");

        ProcedimentDto procedimentDto = ProcedimentDto.builder()
                .id(1L)
                .codi("codi")
                .nom("nom")
                .entitatId(1L)
                .departament("departament")
                .organGestor(OrganGestorDto.builder().id(1L).build())
                .codiSia("sia")
                .valorCampAutomatizado(true)
                .valorCampClaseTramite(ProcedimentClaseTramiteEnumDto.SANCIONADOR)
                .actiu(true)
                .build();

        when(entitatRepository.findByCodi("entitatCodi")).thenReturn(entitat);
        when(entitatRepository.findOne(1L)).thenReturn(entitat);
        when(organGestorRepository.findByCodiAndEntitat("organCodi", entitat)).thenReturn(organGestor);
        when(procedimentService.create(any(ProcedimentDto.class))).thenAnswer(new Answer<ProcedimentDto>() {
            @Override
            public ProcedimentDto answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                ProcedimentDto procediment = (ProcedimentDto) args[0];
                if (new Long(1L).equals(procediment.getOrganGestor().getId()))
                    procediment.getOrganGestor().setCodi("organCodi");
                return procediment;
            }
        });

        Procediment createdProcediment = gestioRestServiceImpl.create(procediment);

        Assert.assertEquals("codi", createdProcediment.getCodi());
        Assert.assertEquals("nom", createdProcediment.getNom());
        Assert.assertEquals("entitatCodi", createdProcediment.getEntitatCodi());
        Assert.assertEquals("organCodi", createdProcediment.getOrganGestorDir3());
    }

    @Test(expected = EntitatNotFoundException.class)
    public void testCreate_EntitatNotFoundException () throws EntitatNotFoundException, OrganNotFoundException {
        Procediment procediment = Procediment.builder()
                .codi("codi")
                .nom("nom")
                .entitatCodi("entitatCodi")
                .organGestorDir3("organCodi")
                .build();

        when(entitatRepository.findByCodi("entitatCodi")).thenReturn(null);

        gestioRestServiceImpl.create(procediment);
    }

    @Test(expected = OrganNotFoundException.class)
    public void testCreate_OrganNotFoundException () throws EntitatNotFoundException, OrganNotFoundException {
        Procediment procediment = Procediment.builder()
                .codi("codi")
                .nom("nom")
                .entitatCodi("entitatCodi")
                .organGestorDir3("organCodi")
                .build();

        Entitat entitat = new Entitat();
//        entitat.setId(1L);
        entitat.setCodi("entitatCodi");

        when(entitatRepository.findByCodi("entitatCodi")).thenReturn(entitat);
        when(organGestorRepository.findByCodiAndEntitat("organCodi", entitat)).thenReturn(null);

        gestioRestServiceImpl.create(procediment);
    }


    // UPDATE Procediment
    // ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testUpdate_Success() throws EntitatNotFoundException, ProcedimentNotFoundException, OrganNotFoundException {
        Procediment procediment = Procediment.builder()
                .id(1L)
                .codi("codi_updated")
                .nom("nom_updated")
                .entitatCodi("entitatCodi")
                .organGestorDir3("organCodi")
                .codiSia("sia_updated")
                .valorCampAutomatizado(true)
                .valorCampClaseTramite(ClaseTramite.PREMIOS)
                .build();

        Entitat entitat = new Entitat();
        entitat.fillIdForTesting(1L);
        entitat.setCodi("entitatCodi");

        final OrganGestor organGestor = new OrganGestor();
        organGestor.fillIdForTesting(1L);
        organGestor.setCodi("organCodi");

        ProcedimentDto updatedProcedimentDto = ProcedimentDto.builder()
                .id(1L)
                .codi("codi_updated")
                .nom("nom_updated")
                .entitatId(1L)
                .departament("departament_updated")
                .organGestor(OrganGestorDto.builder().id(1L).build())
                .codiSia("sia_updated")
                .valorCampAutomatizado(true)
                .valorCampClaseTramite(ProcedimentClaseTramiteEnumDto.PREMIOS)
                .actiu(true)
                .build();

        when(entitatRepository.findByCodi("entitatCodi")).thenReturn(entitat);
        when(entitatRepository.findOne(1L)).thenReturn(entitat);
        when(organGestorRepository.findByCodiAndEntitat("organCodi", entitat)).thenReturn(organGestor);
        when(procedimentService.update(any(ProcedimentDto.class))).thenAnswer(new Answer<ProcedimentDto>() {
            @Override
            public ProcedimentDto answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                ProcedimentDto procediment = (ProcedimentDto) args[0];
                if (new Long(1L).equals(procediment.getOrganGestor().getId()))
                    procediment.getOrganGestor().setCodi("organCodi");
                return procediment;
            }
        });

        Procediment updatedProcediment = gestioRestServiceImpl.update(procediment);

        Assert.assertEquals("codi_updated", updatedProcediment.getCodi());
        Assert.assertEquals("nom_updated", updatedProcediment.getNom());
        Assert.assertEquals("entitatCodi", updatedProcediment.getEntitatCodi());
        Assert.assertEquals("organCodi", updatedProcediment.getOrganGestorDir3());
    }

    @Test(expected = EntitatNotFoundException.class)
    public void testUpdate_EntitatNotFoundException() throws EntitatNotFoundException, ProcedimentNotFoundException, OrganNotFoundException {
        Procediment procediment = Procediment.builder()
                .id(1L)
                .codi("codi")
                .nom("nom")
                .entitatCodi("entitatCodiNotExist")
                .organGestorDir3("organCodi")
                .build();

        when(entitatRepository.findByCodi("entitatCodiNotExist")).thenReturn(null);

        gestioRestServiceImpl.update(procediment);
    }

    @Test(expected = ProcedimentNotFoundException.class)
    public void testUpdate_ProcedimentNotFoundException() throws EntitatNotFoundException, ProcedimentNotFoundException, OrganNotFoundException {
        Procediment procediment = Procediment.builder()
                .id(1L)
                .codi("codi")
                .nom("nom")
                .entitatCodi("entitatCodi")
                .organGestorDir3("organCodi")
                .build();

        Entitat entitat = new Entitat();
        entitat.fillIdForTesting(1L);
        entitat.setCodi("entitatCodi");

        final OrganGestor organGestor = new OrganGestor();
        organGestor.fillIdForTesting(1L);
        organGestor.setCodi("organCodi");

        when(entitatRepository.findByCodi("entitatCodi")).thenReturn(entitat);
        when(entitatRepository.findOne(1L)).thenReturn(entitat);
        when(organGestorRepository.findByCodiAndEntitat("organCodi", entitat)).thenReturn(organGestor);
        when(procedimentService.update(any(ProcedimentDto.class))).thenThrow(new ProcedimentNotFoundException("1"));

        gestioRestServiceImpl.update(procediment);
    }

    @Test(expected = OrganNotFoundException.class)
    public void testUpdate_OrganNotFoundException() throws EntitatNotFoundException, ProcedimentNotFoundException, OrganNotFoundException {
        Procediment procediment = Procediment.builder()
                .id(1L)
                .codi("codi")
                .nom("nom")
                .entitatCodi("entitatCodi")
                .organGestorDir3("organCodiNotExist")
                .build();

        Entitat entitat = new Entitat();
        entitat.fillIdForTesting(1L);
        entitat.setCodi("entitatCodi");

        when(entitatRepository.findByCodi("entitatCodi")).thenReturn(entitat);
        when(entitatRepository.findOne(1L)).thenReturn(entitat);
        when(organGestorRepository.findByCodiAndEntitat("organCodiNotExist", entitat)).thenReturn(null);

        gestioRestServiceImpl.update(procediment);
    }



    // UPDATE Parcial Procediment
    // ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testUpdateParcial_Success() throws EntitatNotFoundException, ProcedimentNotFoundException, OrganNotFoundException {
        Long procedimentId = 1L;

        Entitat entitat = new Entitat();
        entitat.setCodi("entitatCodi");

        OrganGestor organGestor = new OrganGestor();
        organGestor.setCodi("organCodi");

        es.caib.pinbal.core.model.Procediment procediment = new es.caib.pinbal.core.model.Procediment();
        procediment.setCodi("originalCodi");
        procediment.setNom("originalNom");
        procediment.setDepartament("originalDepartament");
        procediment.setEntitat(entitat);
        procediment.setOrganGestor(organGestor);
        procediment.setCodiSia("originalCodiSia");
        procediment.setValorCampAutomatizado(true);
        procediment.setActiu(true);

        ProcedimentPatch procedimentPatch = ProcedimentPatch.builder()
                .codi(new OptionalField<String>("newCodi"))
                .nom(new OptionalField<>("newNom"))
                .departament(new OptionalField<>("newDepartament"))
                .codiSia(new OptionalField<>("newCodiSia"))
                .valorCampAutomatizado(new OptionalField<>(false))
                .actiu(new OptionalField<>(false))
                .build();

        when(procedimentRepository.findOne(procedimentId)).thenReturn(procediment);
        when(procedimentRepository.save(any(es.caib.pinbal.core.model.Procediment.class))).thenAnswer(new Answer<es.caib.pinbal.core.model.Procediment>() {
            @Override
            public es.caib.pinbal.core.model.Procediment answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (es.caib.pinbal.core.model.Procediment) args[0];
            }
        });
        when(entitatRepository.findByCodi("entitatCodi")).thenReturn(entitat);
        when(organGestorRepository.findByCodiAndEntitat("organCodi", procediment.getEntitat())).thenReturn(organGestor);

        Procediment updatedProcediment = gestioRestServiceImpl.updateParcial(procedimentId, procedimentPatch);

        Assert.assertEquals("newCodi", updatedProcediment.getCodi());
        Assert.assertEquals("newNom", updatedProcediment.getNom());
        Assert.assertEquals("newDepartament", updatedProcediment.getDepartament());
        Assert.assertEquals("newCodiSia", updatedProcediment.getCodiSia());
        Assert.assertFalse(updatedProcediment.getValorCampAutomatizado());
        Assert.assertFalse(updatedProcediment.isActiu());
    }

    @Test(expected = ProcedimentNotFoundException.class)
    public void testUpdateParcial_ProcedimentNotFound() throws EntitatNotFoundException, ProcedimentNotFoundException, OrganNotFoundException {
        Long procedimentId = 1L;
        ProcedimentPatch procedimentPatch = ProcedimentPatch.builder().build();

        when(procedimentRepository.findOne(procedimentId)).thenReturn(null);

        gestioRestServiceImpl.updateParcial(procedimentId, procedimentPatch);
    }

    @Test(expected = OrganNotFoundException.class)
    public void testUpdateParcial_OrganNotFound() throws EntitatNotFoundException, ProcedimentNotFoundException, OrganNotFoundException {
        Long procedimentId = 1L;
        Procediment procediment = Procediment.builder()
                .id(procedimentId)
                .codi("originalCodi")
                .nom("originalNom")
                .departament("originalDepartament")
                .entitatCodi("entitatCodi")
                .organGestorDir3("organCodi")
                .codiSia("originalCodiSia")
                .valorCampAutomatizado(true)
                .actiu(true)
                .build();

        ProcedimentPatch procedimentPatch = ProcedimentPatch.builder()
                .organGestorDir3(OptionalField.of("nonExistentOrganCodi"))
                .build();

        Entitat entitat = new Entitat();
        entitat.setCodi("entitatCodi");

        OrganGestor organGestor = new OrganGestor();
        organGestor.setCodi("organCodi");

        es.caib.pinbal.core.model.Procediment procedimentEntity = new es.caib.pinbal.core.model.Procediment();
        procedimentEntity.setCodi("originalCodi");
        procedimentEntity.setNom("originalNom");
        procedimentEntity.setDepartament("originalDepartament");
        procedimentEntity.setEntitat(entitat);
        procedimentEntity.setOrganGestor(organGestor);
        procedimentEntity.setCodiSia("originalCodiSia");
        procedimentEntity.setValorCampAutomatizado(true);
        procedimentEntity.setActiu(true);

        when(procedimentRepository.findOne(procedimentId)).thenReturn(procedimentEntity);
        when(entitatRepository.findByCodi("entitatCodi")).thenReturn(entitat);
        when(organGestorRepository.findByCodiAndEntitat("nonExistentOrganCodi", procedimentEntity.getEntitat())).thenReturn(null);

        gestioRestServiceImpl.updateParcial(procedimentId, procedimentPatch);
    }


    // ENABLE servei
    // ///////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void testServeiEnable_Success() throws ProcedimentNotFoundException, ServeiNotFoundException {
        Long procedimentId = 1L;
        String serveiCodi = "serveiCodi";

        // Mocking the service call
        Mockito.doNothing().when(procedimentService).serveiEnable(procedimentId, serveiCodi);

        // Method call
        gestioRestServiceImpl.serveiEnable(procedimentId, serveiCodi);

        // Verifications
        Mockito.verify(procedimentService, Mockito.times(1)).serveiEnable(procedimentId, serveiCodi);
    }

    @Test(expected = ProcedimentNotFoundException.class)
    public void testServeiEnable_ProcedimentNotFoundException() throws ProcedimentNotFoundException, ServeiNotFoundException {
        Long procedimentId = 1L;
        String serveiCodi = "serveiCodi";

        // Mocking the service call to throw ProcedimentNotFoundException
        doThrow(new ProcedimentNotFoundException(procedimentId.toString())).when(procedimentService).serveiEnable(procedimentId, serveiCodi);

        // Method call
        gestioRestServiceImpl.serveiEnable(procedimentId, serveiCodi);
    }

    @Test(expected = ServeiNotFoundException.class)
    public void testServeiEnable_ServeiNotFoundException() throws ProcedimentNotFoundException, ServeiNotFoundException {
        Long procedimentId = 1L;
        String serveiCodi = "serveiCodi";

        // Mocking the service call to throw ServeiNotFoundException
        doThrow(new ServeiNotFoundException(serveiCodi)).when(procedimentService).serveiEnable(procedimentId, serveiCodi);

        // Method call
        gestioRestServiceImpl.serveiEnable(procedimentId, serveiCodi);
    }


    // FIND Procediments Paginat
    // ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testFindProcedimentsPaginat_Success() throws EntitatNotFoundException, OrganNotFoundException {
        String entitatCodi = "entitatCodi";
        String codi = "codi";
        String nom = "nom";
        String organGestor = "organGestor";
        Pageable pageable = new PageRequest(0, 10);

        PaginacioAmbOrdreDto paginacio = new PaginacioAmbOrdreDto();
        paginacio.setPaginaNum(pageable.getPageNumber());
        paginacio.setPaginaTamany(pageable.getPageSize());

        Entitat entitat = new Entitat();
        entitat.fillIdForTesting(1L);
        entitat.setCodi(entitatCodi);

        OrganGestor organ = new OrganGestor();
        organ.fillIdForTesting(1L);
        organ.setCodi(organGestor);

        ProcedimentDto procedimentDto = ProcedimentDto.builder()
                .id(1L)
                .codi("codi")
                .nom("nom")
                .entitatId(1L)
                .departament("departament")
                .organGestor(OrganGestorDto.builder().id(1L).build())
                .codiSia("sia")
                .valorCampAutomatizado(true)
                .valorCampClaseTramite(ProcedimentClaseTramiteEnumDto.SANCIONADOR)
                .actiu(true)
                .build();
        Page<ProcedimentDto> procedimentDtoPage = new PageImpl<>(Collections.singletonList(procedimentDto), pageable, 1);

        when(entitatRepository.findByCodi(entitatCodi)).thenReturn(entitat);
        when(entitatRepository.findOne(1L)).thenReturn(entitat);
        when(organGestorRepository.findByCodiAndEntitat(organGestor, entitat)).thenReturn(organ);
        when(procedimentService.findAmbFiltrePaginat(anyLong(), anyString(), anyString(), (String) isNull(), anyLong(), (String) isNull(), any(FiltreActiuEnumDto.class), any(PaginacioAmbOrdreDto.class))).thenReturn(procedimentDtoPage);

        Page<Procediment> page = gestioRestServiceImpl.findProcedimentsPaginat(entitatCodi, codi, nom, organGestor, pageable);

        Assert.assertNotNull(page);
        Assert.assertEquals(1, page.getContent().size());
        Assert.assertEquals("codi", page.getContent().get(0).getCodi());
    }

    @Test(expected = EntitatNotFoundException.class)
    public void testFindProcedimentsPaginat_EntitatNotFoundException() throws EntitatNotFoundException, OrganNotFoundException {
        String entitatCodi = "nonExistentEntitatCodi";
        Pageable pageable = new PageRequest(0, 10);

        when(entitatRepository.findByCodi(entitatCodi)).thenReturn(null);

        gestioRestServiceImpl.findProcedimentsPaginat(entitatCodi, null, null, null, pageable);
    }

    @Test(expected = OrganNotFoundException.class)
    public void testFindProcedimentsPaginat_OrganNotFoundException() throws EntitatNotFoundException, OrganNotFoundException {
        String entitatCodi = "entitatCodi";
        String organGestor = "nonExistentOrganCodi";
        Pageable pageable = new PageRequest(0, 10);

        Entitat entitat = new Entitat();
        entitat.fillIdForTesting(1L);
        entitat.setCodi(entitatCodi);

        when(entitatRepository.findByCodi(entitatCodi)).thenReturn(entitat);
        when(organGestorRepository.findByCodiAndEntitat(organGestor, entitat)).thenReturn(null);

        gestioRestServiceImpl.findProcedimentsPaginat(entitatCodi, null, null, organGestor, pageable);
    }


    // GET Procediment
    // ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testGetProcedimentById_Success() throws ProcedimentNotFoundException {
        Long procedimentId = 1L;
        ProcedimentDto procedimentDto = ProcedimentDto.builder()
                .id(procedimentId)
                .codi("codi")
                .nom("nom")
                .entitatId(1L)
                .departament("departament")
                .organGestor(OrganGestorDto.builder().id(1L).codi("organCodi").build())
                .codiSia("sia")
                .valorCampAutomatizado(true)
                .valorCampClaseTramite(ProcedimentClaseTramiteEnumDto.SANCIONADOR)
                .actiu(true)
                .build();

        Procediment expectedProcediment = Procediment.builder()
                .id(procedimentId)
                .codi("codi")
                .nom("nom")
                .departament("departament")
                .entitatCodi("entitatCodi")
                .organGestorDir3("organCodi")
                .codiSia("sia")
                .valorCampAutomatizado(true)
                .valorCampClaseTramite(ClaseTramite.SANCIONADOR)
                .actiu(true)
                .build();

        Entitat entitat = new Entitat();
        entitat.fillIdForTesting(1L);
        entitat.setCodi("entitatCodi");

        when(procedimentService.findById(procedimentId)).thenReturn(procedimentDto);
        when(entitatRepository.findOne(1L)).thenReturn(entitat);

        Procediment procediment = gestioRestServiceImpl.getProcedimentById(procedimentId);

        Assert.assertEquals(expectedProcediment.getId(), procediment.getId());
        Assert.assertEquals(expectedProcediment.getCodi(), procediment.getCodi());
        Assert.assertEquals(expectedProcediment.getNom(), procediment.getNom());
        Assert.assertEquals(expectedProcediment.getDepartament(), procediment.getDepartament());
        Assert.assertEquals(expectedProcediment.getEntitatCodi(), procediment.getEntitatCodi());
        Assert.assertEquals(expectedProcediment.getOrganGestorDir3(), procediment.getOrganGestorDir3());
        Assert.assertEquals(expectedProcediment.getCodiSia(), procediment.getCodiSia());
        Assert.assertEquals(expectedProcediment.getValorCampAutomatizado(), procediment.getValorCampAutomatizado());
        Assert.assertEquals(expectedProcediment.getValorCampClaseTramite(), procediment.getValorCampClaseTramite());
        Assert.assertEquals(expectedProcediment.isActiu(), procediment.isActiu());
    }

    @Test
    public void testGetProcedimentById_NotFound() {
        Long procedimentId = 1L;
        when(procedimentService.findById(procedimentId)).thenReturn(null);
        Procediment procediment = gestioRestServiceImpl.getProcedimentById(procedimentId);

        Assert.assertNull(procediment);
    }


    // GET Procediment by codi
    // ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testGetProcedimentAmbEntitatICodi_Success() throws EntitatNotFoundException {
        String entitatCodi = "entitatCodi";
        String procedimentCodi = "procedimentCodi";

        Entitat entitat = new Entitat();
        entitat.fillIdForTesting(1L);
        entitat.setCodi(entitatCodi);

        ProcedimentDto procedimentDto = ProcedimentDto.builder()
                .id(1L)
                .codi(procedimentCodi)
                .nom("nom")
                .entitatId(1L)
                .departament("departament")
                .organGestor(OrganGestorDto.builder().id(1L).codi("organCodi").build())
                .codiSia("sia")
                .valorCampAutomatizado(true)
                .valorCampClaseTramite(ProcedimentClaseTramiteEnumDto.SANCIONADOR)
                .actiu(true)
                .build();

        Procediment expectedProcediment = Procediment.builder()
                .id(1L)
                .codi(procedimentCodi)
                .nom("nom")
                .departament("departament")
                .entitatCodi(entitatCodi)
                .organGestorDir3("organCodi")
                .codiSia("sia")
                .valorCampAutomatizado(true)
                .valorCampClaseTramite(ClaseTramite.SANCIONADOR)
                .actiu(true)
                .build();

        when(entitatRepository.findByCodi(entitatCodi)).thenReturn(entitat);
        when(entitatRepository.findOne(1L)).thenReturn(entitat);
        when(procedimentService.findAmbEntitatICodi(1L, procedimentCodi)).thenReturn(procedimentDto);

        Procediment procediment = gestioRestServiceImpl.getProcedimentAmbEntitatICodi(entitatCodi, procedimentCodi);

        Assert.assertEquals(expectedProcediment.getId(), procediment.getId());
        Assert.assertEquals(expectedProcediment.getCodi(), procediment.getCodi());
        Assert.assertEquals(expectedProcediment.getNom(), procediment.getNom());
        Assert.assertEquals(expectedProcediment.getDepartament(), procediment.getDepartament());
        Assert.assertEquals(expectedProcediment.getEntitatCodi(), procediment.getEntitatCodi());
        Assert.assertEquals(expectedProcediment.getOrganGestorDir3(), procediment.getOrganGestorDir3());
        Assert.assertEquals(expectedProcediment.getCodiSia(), procediment.getCodiSia());
        Assert.assertEquals(expectedProcediment.getValorCampAutomatizado(), procediment.getValorCampAutomatizado());
        Assert.assertEquals(expectedProcediment.getValorCampClaseTramite(), procediment.getValorCampClaseTramite());
        Assert.assertEquals(expectedProcediment.isActiu(), procediment.isActiu());
    }

    @Test(expected = EntitatNotFoundException.class)
    public void testGetProcedimentAmbEntitatICodi_EntitatNotFoundException() throws EntitatNotFoundException {
        String entitatCodi = "entitatCodi";
        String procedimentCodi = "procedimentCodi";

        when(entitatRepository.findByCodi(entitatCodi)).thenReturn(null);

        gestioRestServiceImpl.getProcedimentAmbEntitatICodi(entitatCodi, procedimentCodi);
    }


    // FIND Serveis by Procediment
    // ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testFindServeisByProcedimentPaginat_Success() throws ProcedimentNotFoundException {
        Long procedimentId = 1L;
        ServeiDto serveiDto = ServeiDto.builder().codi("serveiCodi").descripcio("serveiDescripcio").scspEmisor(EmisorDto.builder().nom("emisor").build()).actiu(true).build();
        List<ServeiDto> serveis = new ArrayList<ServeiDto>();
        serveis.add(serveiDto);
        Page<ServeiDto> serveiDtoPage = new PageImpl<>(serveis, new PageRequest(0, 10), 1);
        es.caib.pinbal.core.model.Procediment procediment = new es.caib.pinbal.core.model.Procediment();
        procediment.setEntitat(new Entitat());
        procediment.getEntitat().fillIdForTesting(1L);
        procediment.getEntitat().setCodi("entitatCodi");

        Pageable pageable = new PageRequest(0, 10);

        when(procedimentRepository.getOne(procedimentId)).thenReturn(procediment);
        when(serveiService.findAmbFiltrePaginat((String)isNull(), (String)isNull(), (String)isNull(), anyBoolean(), any(EntitatDto.class), any(ProcedimentDto.class), any(Pageable.class))).thenReturn(serveiDtoPage);

        Page<Servei> serveiPage = gestioRestServiceImpl.findServeisByProcedimentPaginat(procedimentId, pageable);

        Assert.assertEquals(1, serveiPage.getTotalElements());
        Assert.assertEquals("serveiCodi", serveiPage.getContent().get(0).getCodi());
        Assert.assertEquals("serveiDescripcio", serveiPage.getContent().get(0).getDescripcio());
    }

    @Test(expected = ProcedimentNotFoundException.class)
    public void testFindServeisByProcedimentPaginat_ProcedimentNotFoundException() throws ProcedimentNotFoundException {
        Long procedimentId = 1L;
        when(procedimentRepository.getOne(procedimentId)).thenReturn(null);
        gestioRestServiceImpl.findServeisByProcedimentPaginat(procedimentId, new PageRequest(0, 10));
    }


    // FIND Serveis Paginat
    // ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testFindServeisPaginat_Success () {
        String codi = "serveiCodi";
        String descripcio = "serveiDescripcio";
        ServeiDto serveiDto = ServeiDto.builder().codi(codi).descripcio(descripcio).actiu(true).build();
        List<ServeiDto> serveis = new ArrayList<>();
        serveis.add(serveiDto);
        Page<ServeiDto> serveiDtoPage = new PageImpl<>(serveis, new PageRequest(0, 10), serveis.size());

        when(serveiService.findAmbFiltrePaginat(eq(codi), eq(descripcio), (String) isNull(), eq(true), (String) isNull(), any(Pageable.class)))
                .thenReturn(serveiDtoPage);

        Pageable pageable = new PageRequest(0, 10);
        Page<Servei> serveiPage = gestioRestServiceImpl.findServeisPaginat(codi, descripcio, pageable);

        Assert.assertNotNull(serveiPage);
        Assert.assertEquals(1, serveiPage.getTotalElements());
        Assert.assertEquals(codi, serveiPage.getContent().get(0).getCodi());
        Assert.assertEquals(descripcio, serveiPage.getContent().get(0).getDescripcio());
    }

    @Test
    public void testFindServeisPaginat_NoResults () {
        String codi = "serveiCodi";
        String descripcio = "serveiDescripcio";
        List<ServeiDto> serveis = new ArrayList<>();
        Page<ServeiDto> serveiDtoPage = new PageImpl<>(serveis, new PageRequest(0, 10), serveis.size());

        when(serveiService.findAmbFiltrePaginat(eq(codi), eq(descripcio), (String) isNull(), eq(true), (String) isNull(), any(Pageable.class)))
                .thenReturn(serveiDtoPage);

        Pageable pageable = new PageRequest(0, 10);
        Page<Servei> serveiPage = gestioRestServiceImpl.findServeisPaginat(codi, descripcio, pageable);

        Assert.assertNotNull(serveiPage);
        Assert.assertEquals(0, serveiPage.getTotalElements());
    }


    // FIND Servei by codi
    // ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testGetServeiByCodi_Success() throws ServeiNotFoundException {
        String serveiCodi = "serveiCodi";
        ServeiDto serveiDto = ServeiDto.builder()
                .codi(serveiCodi)
                .descripcio("serveiDescripcio")
                .scspEmisor(EmisorDto.builder().nom("emisor").build())
                .actiu(true)
                .build();

        when(serveiService.findAmbCodiPerAdminORepresentant(serveiCodi)).thenReturn(serveiDto);

        Servei servei = gestioRestServiceImpl.getServeiByCodi(serveiCodi);

        Assert.assertNotNull(servei);
        Assert.assertEquals(serveiCodi, servei.getCodi());
        Assert.assertEquals("serveiDescripcio", servei.getDescripcio());
    }

    @Test
    public void testGetServeiByCodi_ServeiNotFoundException() throws ServeiNotFoundException {
        String serveiCodi = "serveiCodi";

        doThrow(new ServeiNotFoundException()).when(serveiService).findAmbCodiPerAdminORepresentant(serveiCodi);

        Servei servei = gestioRestServiceImpl.getServeiByCodi(serveiCodi);

        Assert.assertNull(servei);
    }


    // CREATE or UPDATE Usuari
    // ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testCreateOrUpdateUsuari_Success() throws Exception {
        UsuariEntitat usuariEntitat = UsuariEntitat.builder()
                .entitatCodi("entitatCodi")
                .codi("usuariCodi")
                .departament("dept")
                .representant(true)
                .delegat(false)
                .auditor(true)
                .aplicacio(false)
                .actiu(true)
                .build();

        Entitat entitat = new Entitat();
        entitat.fillIdForTesting(1L);
        entitat.setCodi("entitatCodi");

        UsuariDto usuariDto = UsuariDto.builder()
                .codi("usuariCodi")
                .build();

        when(entitatRepository.findByCodi("entitatCodi")).thenReturn(entitat);
        when(usuariService.getUsuarisExterns("usuariCodi")).thenReturn(Arrays.asList(new UsuariDto()));

        gestioRestServiceImpl.createOrUpdateUsuari(usuariEntitat);
    }

    @Test(expected = EntitatNotFoundException.class)
    public void testCreateOrUpdateUsuari_EntitatNotFoundException() throws Exception {
        UsuariEntitat usuariEntitat = UsuariEntitat.builder()
                .entitatCodi("entitatCodi")
                .build();

        when(entitatRepository.findByCodi("entitatCodi")).thenReturn(null);

        gestioRestServiceImpl.createOrUpdateUsuari(usuariEntitat);
    }

    @Test(expected = UsuariExternNotFoundException.class)
    public void testCreateOrUpdateUsuari_UsuariExternNotFoundException() throws Exception {
        UsuariEntitat usuariEntitat = UsuariEntitat.builder()
                .entitatCodi("entitatCodi")
                .codi("usuariCodi")
                .build();

        Entitat entitat = new Entitat();
        entitat.fillIdForTesting(1L);
        entitat.setCodi("entitatCodi");

        when(entitatRepository.findByCodi("entitatCodi")).thenReturn(entitat);
        when(usuariService.getUsuarisExterns("usuariCodi")).thenReturn(new ArrayList<UsuariDto>());

        gestioRestServiceImpl.createOrUpdateUsuari(usuariEntitat);
    }

    @Test(expected = MultiplesUsuarisExternsException.class)
    public void testCreateOrUpdateUsuari_MultiplesUsuarisExternsException() throws Exception {
        UsuariEntitat usuariEntitat = UsuariEntitat.builder()
                .entitatCodi("entitatCodi")
                .codi("usuariCodi")
                .build();

        Entitat entitat = new Entitat();
        entitat.fillIdForTesting(1L);
        entitat.setCodi("entitatCodi");

        when(entitatRepository.findByCodi("entitatCodi")).thenReturn(entitat);
        when(usuariService.getUsuarisExterns("usuariCodi")).thenReturn(Arrays.asList(new UsuariDto(), new UsuariDto()));

        gestioRestServiceImpl.createOrUpdateUsuari(usuariEntitat);
    }


    // GET Usuari amb entitat i codi
    // ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testGetUsuariAmbEntitatICodi_Success() throws EntitatNotFoundException {
        String entitatCodi = "entitatCodi";
        String usuariCodi = "usuariCodi";

        Entitat entitat = new Entitat();
        entitat.configurarIdPerTest(1L);
        entitat.setCodi(entitatCodi);

        EntitatUsuariDto entitatUsuariDto = new EntitatUsuariDto(
                UsuariDto.builder().nom("nomUsuari").codi(usuariCodi).entitatId(1L).build(),
                "departament",
                true,
                true,
                true,
                true,
                true,
                true);

        when(entitatRepository.findByCodi(entitatCodi)).thenReturn(entitat);
        when(usuariService.getEntitatUsuari(entitat.getId(), usuariCodi)).thenReturn(entitatUsuariDto);

        UsuariEntitat result = gestioRestServiceImpl.getUsuariAmbEntitatICodi(entitatCodi, usuariCodi);

        Assert.assertNotNull(result);
        Assert.assertEquals(entitatCodi, result.getEntitatCodi());
        Assert.assertEquals(usuariCodi, result.getCodi());
    }

    @Test(expected = EntitatNotFoundException.class)
    public void testGetUsuariAmbEntitatICodi_EntitatNotFoundException() throws EntitatNotFoundException {
        String entitatCodi = "entitatCodi";
        String usuariCodi = "usuariCodi";

        when(entitatRepository.findByCodi(entitatCodi)).thenReturn(null);

        gestioRestServiceImpl.getUsuariAmbEntitatICodi(entitatCodi, usuariCodi);
    }


    // GRANT Permis a Serveis
    // ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testServeiGrantPermis_Success() throws EntitatNotFoundException, EntitatUsuariNotFoundException, ProcedimentServeiNotFoundException {
        PermisosServei permisosServei = PermisosServei.builder()
                .usuariCodi("usuariCodi")
                .entitatCodi("entitatCodi")
                .procedimentServei(new ArrayList<ProcedimentServei>())
                .build();

        Entitat entitat = new Entitat();
        entitat.configurarIdPerTest(1L);
        entitat.setCodi("entitatCodi");

        when(entitatRepository.findByCodi("entitatCodi")).thenReturn(entitat);

        gestioRestServiceImpl.serveiGrantPermis(permisosServei);
    }

    @Test(expected = EntitatNotFoundException.class)
    public void testServeiGrantPermis_EntitatNotFoundException() throws EntitatNotFoundException, EntitatUsuariNotFoundException, ProcedimentServeiNotFoundException {
        PermisosServei permisosServei = PermisosServei.builder()
                .usuariCodi("usuariCodi")
                .entitatCodi("entitatCodi")
                .procedimentServei(new ArrayList<ProcedimentServei>())
                .build();

        when(entitatRepository.findByCodi("entitatCodi")).thenReturn(null);

        gestioRestServiceImpl.serveiGrantPermis(permisosServei);
    }

    @Test(expected = EntitatUsuariNotFoundException.class)
    public void testServeiGrantPermis_EntitatUsuariNotFoundException() throws EntitatNotFoundException, EntitatUsuariNotFoundException, ProcedimentServeiNotFoundException {
        PermisosServei permisosServei = PermisosServei.builder()
                .usuariCodi("usuariCodi")
                .entitatCodi("entitatCodi")
                .procedimentServei(Arrays.asList(ProcedimentServei.builder().procedimentCodi("procedimentCodi").serveiCodi("serveiCodi").build()))
                .build();

        Entitat entitat = new Entitat();
        entitat.configurarIdPerTest(1L);
        entitat.setCodi("entitatCodi");

        List<ProcedimentServeiSimpleDto> procedimentServeiSimpleDtos = new ArrayList<>();
        procedimentServeiSimpleDtos.add(ProcedimentServeiSimpleDto.builder()
                .procedimentCodi("procedimentCodi")
                .serveiCodi("serveiCodi")
                .build());

        when(entitatRepository.findByCodi("entitatCodi")).thenReturn(entitat);
        doThrow(new EntitatUsuariNotFoundException("entitatCodi", "usuariCodi")).when(procedimentService)
                .serveiPermisAllowSelected(eq("usuariCodi"), eq(procedimentServeiSimpleDtos), eq(entitat.getId()));

        gestioRestServiceImpl.serveiGrantPermis(permisosServei);
    }

    @Test(expected = ProcedimentServeiNotFoundException.class)
    public void testServeiGrantPermis_ProcedimentServeiNotFoundException() throws EntitatNotFoundException, EntitatUsuariNotFoundException, ProcedimentServeiNotFoundException {
        PermisosServei permisosServei = PermisosServei.builder()
                .usuariCodi("usuariCodi")
                .entitatCodi("entitatCodi")
                .procedimentServei(Arrays.asList(ProcedimentServei.builder().procedimentCodi("procedimentCodi").serveiCodi("serveiCodi").build()))
                .build();

        Entitat entitat = new Entitat();
        entitat.configurarIdPerTest(1L);
        entitat.setCodi("entitatCodi");

        List<ProcedimentServeiSimpleDto> procedimentServeiSimpleDtos = new ArrayList<>();
        procedimentServeiSimpleDtos.add(ProcedimentServeiSimpleDto.builder()
                .procedimentCodi("procedimentCodi")
                .serveiCodi("serveiCodi")
                .build());

        when(entitatRepository.findByCodi("entitatCodi")).thenReturn(entitat);
        doThrow(new ProcedimentServeiNotFoundException("procedimentCodi", "serveiCodi")).when(procedimentService)
                .serveiPermisAllowSelected(eq("usuariCodi"), eq(procedimentServeiSimpleDtos), eq(entitat.getId()));

        gestioRestServiceImpl.serveiGrantPermis(permisosServei);
    }


    // GRANT Permisos
    // ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testPermisosPerUsuariEntitat_Success() throws EntitatNotFoundException, UsuariNotFoundException {
        String entitatCodi = "entitatCodi";
        String usuariCodi = "usuariCodi";

        Entitat entitat = new Entitat();
        entitat.configurarIdPerTest(1L);
        entitat.setCodi(entitatCodi);

        Usuari usuari = Usuari.getBuilderInicialitzat(usuariCodi, "usuariNom", "usuariNif").build();

        List<ProcedimentServeiDto> procedimentServeiDtos = new ArrayList<>();

        when(entitatRepository.findByCodi(entitatCodi)).thenReturn(entitat);
        when(usuariRepository.findByCodi(usuariCodi)).thenReturn(usuari);
        when(serveiService.findPermesosAmbEntitatIUsuari(entitat.getId(), usuariCodi)).thenReturn(procedimentServeiDtos);

        PermisosServei result = gestioRestServiceImpl.permisosPerUsuariEntitat(entitatCodi, usuariCodi);

        Assert.assertNotNull(result);
        Assert.assertEquals(entitatCodi, result.getEntitatCodi());
        Assert.assertEquals(usuariCodi, result.getUsuariCodi());
    }

    @Test(expected = EntitatNotFoundException.class)
    public void testPermisosPerUsuariEntitat_EntitatNotFoundException() throws EntitatNotFoundException, UsuariNotFoundException {
        String entitatCodi = "entitatCodi";
        String usuariCodi = "usuariCodi";

        when(entitatRepository.findByCodi(entitatCodi)).thenReturn(null);

        gestioRestServiceImpl.permisosPerUsuariEntitat(entitatCodi, usuariCodi);
    }

    @Test(expected = UsuariNotFoundException.class)
    public void testPermisosPerUsuariEntitat_UsuariNotFoundException() throws EntitatNotFoundException, UsuariNotFoundException {
        String entitatCodi = "entitatCodi";
        String usuariCodi = "usuariCodi";

        Entitat entitat = new Entitat();
        entitat.configurarIdPerTest(1L);
        entitat.setCodi(entitatCodi);

        when(entitatRepository.findByCodi(entitatCodi)).thenReturn(entitat);
        when(usuariRepository.findByCodi(usuariCodi)).thenReturn(null);

        gestioRestServiceImpl.permisosPerUsuariEntitat(entitatCodi, usuariCodi);
    }


    // FIND Usuaris Paginat
    // ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testFindUsuarisPaginat_Success() throws EntitatNotFoundException {
        String entitatCodi = "entitatCodi";
        FiltreUsuaris filtreUsuaris = FiltreUsuaris.builder().build();
        Pageable pageable = new PageRequest(0, 10);

        Entitat entitat = new Entitat();
        entitat.configurarIdPerTest(1L);
        entitat.setCodi(entitatCodi);

        EntitatUsuariDto entitatUsuariDto = new EntitatUsuariDto(UsuariDto.builder().build(), "", true, true, true, true, true, true);
        Page<EntitatUsuariDto> page = new PageImpl<>(Arrays.asList(entitatUsuariDto), pageable, 1);

        when(entitatRepository.findByCodi(entitatCodi)).thenReturn(entitat);
        when(usuariService.findAmbFiltrePaginat(any(Long.class), (Boolean) any(), (Boolean) any(), (Boolean) any(), (Boolean) any(), (String) any(), (String) any(), (String) any(), (String) any(), any(Pageable.class))).thenReturn(page);

        Page<UsuariEntitat> result = gestioRestServiceImpl.findUsuarisPaginat(entitatCodi, filtreUsuaris, pageable);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.getTotalElements());
    }

    @Test(expected = EntitatNotFoundException.class)
    public void testFindUsuarisPaginat_EntitatNotFoundException() throws EntitatNotFoundException {
        String entitatCodi = "entitatCodi";
        FiltreUsuaris filtreUsuaris = FiltreUsuaris.builder().build();
        Pageable pageable = new PageRequest(0, 10);

        when(entitatRepository.findByCodi(entitatCodi)).thenReturn(null);

        gestioRestServiceImpl.findUsuarisPaginat(entitatCodi, filtreUsuaris, pageable);
    }

}