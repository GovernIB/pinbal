package es.caib.pinbal.logic.service;

import es.caib.pinbal.client.procediments.ProcedimentBasic;
import es.caib.pinbal.client.recobriment.model.ScspJustificante;
import es.caib.pinbal.client.recobriment.v2.DadaEspecifica;
import es.caib.pinbal.client.recobriment.v2.DadesComunes;
import es.caib.pinbal.client.recobriment.v2.PeticioAsincrona;
import es.caib.pinbal.client.recobriment.v2.PeticioConfirmacioAsincrona;
import es.caib.pinbal.client.recobriment.v2.PeticioRespostaAsincrona;
import es.caib.pinbal.client.recobriment.v2.PeticioRespostaSincrona;
import es.caib.pinbal.client.recobriment.v2.PeticioSincrona;
import es.caib.pinbal.client.recobriment.v2.SolicitudSimple;
import es.caib.pinbal.client.recobriment.v2.ValorEnum;
import es.caib.pinbal.client.serveis.ServeiBasic;
import es.caib.pinbal.logic.helper.RecobrimentHelper;
import es.caib.pinbal.logic.helper.RecobrimentV2Helper;
import es.caib.pinbal.logic.intf.dto.EstatTipus;
import es.caib.pinbal.logic.intf.dto.IdiomaEnumDto;
import es.caib.pinbal.logic.intf.dto.JustificantDto;
import es.caib.pinbal.logic.intf.service.DadesExternesService;
import es.caib.pinbal.logic.intf.service.ServeiService;
import es.caib.pinbal.logic.intf.service.exception.ConsultaNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.ConsultaScspGeneracioException;
import es.caib.pinbal.logic.intf.service.exception.EntitatNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.RecobrimentScspException;
import es.caib.pinbal.logic.intf.service.exception.ServeiCampNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.ServeiNotFoundException;
import es.caib.pinbal.persist.entity.Consulta;
import es.caib.pinbal.persist.entity.Entitat;
import es.caib.pinbal.persist.entity.OrganGestor;
import es.caib.pinbal.persist.entity.Procediment;
import es.caib.pinbal.persist.entity.ServeiCamp;
import es.caib.pinbal.persist.entity.ServeiConfig;
import es.caib.pinbal.persist.entity.Usuari;
import es.caib.pinbal.persist.repository.ConsultaRepository;
import es.caib.pinbal.persist.repository.EntitatRepository;
import es.caib.pinbal.persist.repository.HistoricConsultaRepository;
import es.caib.pinbal.persist.repository.ProcedimentRepository;
import es.caib.pinbal.persist.repository.ServeiCampRepository;
import es.caib.pinbal.persist.repository.ServeiConfigRepository;
import es.caib.pinbal.persist.repository.ServeiRepository;
import es.caib.pinbal.plugin.dadescomuns.Municipi;
import es.caib.pinbal.plugin.dadescomuns.Pais;
import es.caib.pinbal.plugin.dadescomuns.Provincia;
import es.caib.pinbal.scsp.ScspHelper;
import es.caib.pinbal.scsp.XmlHelper;
import es.caib.pinbal.scsp.tree.Node;
import es.caib.pinbal.scsp.tree.Tree;
import es.scsp.bean.common.confirmacion.ConfirmacionPeticion;
import es.scsp.bean.common.peticion.Peticion;
import es.scsp.bean.common.respuesta.Estado;
import es.scsp.bean.common.respuesta.Respuesta;
import es.scsp.common.exceptions.ScspException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecobrimentServiceImplTest {

    @InjectMocks
    private RecobrimentServiceImpl recobrimentServiceImpl;

    @Mock
    private EntitatRepository entitatRepository;

    @Mock
    private ProcedimentRepository procedimentRepository;

    @Mock
    private ServeiRepository serveiRepository;

    @Mock
    private ServeiConfigRepository serveiConfigRepository;

    @Mock
    private ServeiCampRepository serveiCampRepository;

    @Mock
    private ConsultaRepository consultaRepository;

    @Mock
    private HistoricConsultaRepository historicConsultaRepository;

    @Mock
    private ScspHelper scspHelper;

    @Mock
    private RecobrimentHelper recobrimentHelper;

    @Mock
    private RecobrimentV2Helper recobrimentV2Helper;

    @Mock
    private DadesExternesService dadesExternesService;

    @BeforeEach
    public void setUp() {
        // El SecurityContextHolder és estàtic (thread-local). Alguns tests (p.ex.
        // testGetEntitats_*) hi deixen un mock de SecurityContext que persisteix entre
        // tests dins la mateixa classe. Si no es neteja, mockAuthentication() crida
        // setAuthentication() sobre aquest mock (que és un no-op) i getConsulta() acaba
        // veient l'usuari "testUser" en lloc de "username", llançant AccessDenegatException.
        SecurityContextHolder.clearContext();
        ReflectionTestUtils.setField(recobrimentServiceImpl, "scspHelper", scspHelper);
    }

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }


    // TESTS getEntitats
    // /////////////////////////////////////////////////////////

    @Test
    public void testGetEntitats_Success() {
        // Mock data
        String username = "testUser";
        Entitat mockEntitat1 = new Entitat();
        mockEntitat1.setCodi("ENT001");
        mockEntitat1.setNom("Entity 1");
        mockEntitat1.setCif("CIF001");

        Entitat mockEntitat2 = new Entitat();
        mockEntitat2.setCodi("ENT002");
        mockEntitat2.setNom("Entity 2");
        mockEntitat2.setCif("CIF002");

        List<Entitat> mockEntitats = new ArrayList<>();
        mockEntitats.add(mockEntitat1);
        mockEntitats.add(mockEntitat2);

        // Simular autenticació
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(username);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Establir el SecurityContext simulat a SecurityContextHolder
        SecurityContextHolder.setContext(securityContext);

        // Mock behavior
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn(username);
        when(entitatRepository.findActivesPerUsuari(username)).thenReturn(mockEntitats);

        // Call method
        List<es.caib.pinbal.client.recobriment.v2.Entitat> entitats = recobrimentServiceImpl.getEntitats();

        // Verify results
        Assertions.assertNotNull(entitats);
        Assertions.assertEquals(2, entitats.size());
        Assertions.assertEquals("ENT001", entitats.get(0).getCodi());
        Assertions.assertEquals("Entity 1", entitats.get(0).getNom());
        Assertions.assertEquals("ENT002", entitats.get(1).getCodi());
        Assertions.assertEquals("Entity 2", entitats.get(1).getNom());
    }

    @Test
    public void testGetEntitats_EmptyList() {
        // Mock data
        String username = "testUser";
        List<Entitat> mockEntitats = new ArrayList<>();

        // Simular autenticació
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(username);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Establir el SecurityContext simulat a SecurityContextHolder
        SecurityContextHolder.setContext(securityContext);

        // Mock behavior
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn(username);

        // Call method
        List<es.caib.pinbal.client.recobriment.v2.Entitat> entitats = recobrimentServiceImpl.getEntitats();

        // Verify results
        Assertions.assertNotNull(entitats);
        Assertions.assertTrue(entitats.isEmpty());
    }


    // TESTS getProcediments
    // /////////////////////////////////////////////////////////

    @Test
    public void testGetProcediments_Success() throws EntitatNotFoundException {
        // Mock data
        String entitatCodi = "ABC123";
        Entitat mockEntitat = new Entitat();
        mockEntitat.setCodi(entitatCodi);

        String organCodi = "A03000000";
        OrganGestor mockOrganGestor = new OrganGestor();
        mockOrganGestor.setCodi(organCodi);

        Procediment procediment1 = new Procediment();
        procediment1.setCodi("PRO123");
        procediment1.setNom("Procediment 123");
        procediment1.setActiu(true);
        procediment1.setOrganGestor(mockOrganGestor);

        Procediment procediment2 = new Procediment();
        procediment2.setCodi("PRO456");
        procediment2.setNom("Procediment 456");
        procediment2.setActiu(false);
        procediment2.setOrganGestor(mockOrganGestor);

        List<Procediment> mockProcediments = new ArrayList<>();
        mockProcediments.add(procediment1);
        mockProcediments.add(procediment2);

        // Mock behavior
        when(entitatRepository.findByCodi(entitatCodi)).thenReturn(mockEntitat);
        when(procedimentRepository.findByEntitatOrderByNomAsc(mockEntitat)).thenReturn(mockProcediments);

        // Call the method
        List<ProcedimentBasic> procediments = recobrimentServiceImpl.getProcediments(entitatCodi);

        // Verify results
        Assertions.assertNotNull(procediments);
        Assertions.assertEquals(2, procediments.size());

        Assertions.assertEquals("PRO123", procediments.get(0).getCodi());
        Assertions.assertEquals("Procediment 123", procediments.get(0).getNom());
        Assertions.assertTrue(procediments.get(0).isActiu());

        Assertions.assertEquals("PRO456", procediments.get(1).getCodi());
        Assertions.assertEquals("Procediment 456", procediments.get(1).getNom());
        Assertions.assertFalse(procediments.get(1).isActiu());
    }

    @Test
    public void testGetProcediments_EntitatNotFound() throws EntitatNotFoundException {
        // Mock data
        String entitatCodi = "XYZ789";

        // Mock behavior
        when(entitatRepository.findByCodi(entitatCodi)).thenReturn(null);

        // Call the method
        assertThrows(EntitatNotFoundException.class, () ->
                recobrimentServiceImpl.getProcediments(entitatCodi)
        );
    }

    @Test
    public void testGetProcediments_NoProcedimentsFound() throws EntitatNotFoundException {
        // Mock data
        String entitatCodi = "DEF456";
        Entitat mockEntitat = new Entitat();
        mockEntitat.setCodi(entitatCodi);

        // Mock behavior
        when(entitatRepository.findByCodi(entitatCodi)).thenReturn(mockEntitat);
        when(procedimentRepository.findByEntitatOrderByNomAsc(mockEntitat)).thenReturn(new ArrayList<Procediment>());

        // Call the method
        List<ProcedimentBasic> procediments = recobrimentServiceImpl.getProcediments(entitatCodi);

        // Verify results
        Assertions.assertNotNull(procediments);
        Assertions.assertTrue(procediments.isEmpty());
    }

    // TESTS getServeis
    // /////////////////////////////////////////////////////////

    @Test
    public void testGetServeis_Success() {
        // Mock data
        ServeiBasic servei1 = new ServeiBasic("S001", "Servei 1", true);
        ServeiBasic servei2 = new ServeiBasic("S002", "Servei 2", false);
        List<ServeiBasic> mockServeis = new ArrayList<>();
        mockServeis.add(servei1);
        mockServeis.add(servei2);

        // Mock behavior
        when(serveiRepository.findAllServeisClient()).thenReturn(mockServeis);

        // Call the method
        List<ServeiBasic> serveis = recobrimentServiceImpl.getServeis();

        // Verify results
        Assertions.assertNotNull(serveis);
        Assertions.assertEquals(2, serveis.size());
        Assertions.assertEquals("S001", serveis.get(0).getCodi());
        Assertions.assertEquals("Servei 1", serveis.get(0).getDescripcio());
        Assertions.assertTrue(serveis.get(0).getActiu());

        Assertions.assertEquals("S002", serveis.get(1).getCodi());
        Assertions.assertEquals("Servei 2", serveis.get(1).getDescripcio());
        Assertions.assertFalse(serveis.get(1).getActiu());
    }

    @Test
    public void testGetServeis_NoServeisFound() {
        // Mock behavior
        when(serveiRepository.findAllServeisClient()).thenReturn(new ArrayList<ServeiBasic>());

        // Call the method
        List<ServeiBasic> serveis = recobrimentServiceImpl.getServeis();

        // Verify results
        Assertions.assertNotNull(serveis);
        Assertions.assertTrue(serveis.isEmpty());
    }

    // TESTS getServeisByEntitat
    // /////////////////////////////////////////////////////////

    @Test
    public void testGetServeisByEntitat_Success() throws EntitatNotFoundException {
        // Mock data
        String entitatCodi = "ENT001";
        ServeiBasic servei1 = new ServeiBasic("SERV001", "Servei 1", true);
        ServeiBasic servei2 = new ServeiBasic("SERV002", "Servei 2", false);
        List<ServeiBasic> mockServeis = new ArrayList<>();
        mockServeis.add(servei1);
        mockServeis.add(servei2);

        // Mock behavior
        when(entitatRepository.findByCodi(entitatCodi)).thenReturn(new Entitat());
        when(serveiRepository.findServeisClientByEntitatCodi(entitatCodi)).thenReturn(mockServeis);

        // Call the method
        List<ServeiBasic> serveis = recobrimentServiceImpl.getServeisByEntitat(entitatCodi);

        // Verify results
        Assertions.assertNotNull(serveis);
        Assertions.assertEquals(2, serveis.size());
        Assertions.assertEquals("SERV001", serveis.get(0).getCodi());
        Assertions.assertEquals("Servei 1", serveis.get(0).getDescripcio());
        Assertions.assertTrue(serveis.get(0).getActiu());
        Assertions.assertEquals("SERV002", serveis.get(1).getCodi());
        Assertions.assertEquals("Servei 2", serveis.get(1).getDescripcio());
        Assertions.assertFalse(serveis.get(1).getActiu());
    }

    @Test
    public void testGetServeisByEntitat_NoServeisFound() throws EntitatNotFoundException {
        // Mock data
        String entitatCodi = "ENT002";

        // Mock behavior
        when(entitatRepository.findByCodi(entitatCodi)).thenReturn(new Entitat());
        when(serveiRepository.findServeisClientByEntitatCodi(entitatCodi)).thenReturn(new ArrayList<ServeiBasic>());

        // Call the method
        List<ServeiBasic> serveis = recobrimentServiceImpl.getServeisByEntitat(entitatCodi);

        // Verify results
        Assertions.assertNotNull(serveis);
        Assertions.assertTrue(serveis.isEmpty());
    }

    @Test
    public void testGetServeisByEntitat_EntitatNotFoundException() throws EntitatNotFoundException {
        // Mock data
        String entitatCodi = "ENT003";

        // Mock behavior
        when(entitatRepository.findByCodi(entitatCodi)).thenReturn(null);

        // Call the method
        assertThrows(EntitatNotFoundException.class, () ->
                recobrimentServiceImpl.getServeisByEntitat(entitatCodi)
        );

    }

    // TESTS getServeisByProcediment
    // /////////////////////////////////////////////////////////

    @Test
    public void testGetServeisByProcediment_Success() throws ProcedimentNotFoundException {
        // Mock data
        String entitatCodi = "ENT001";
        String procedimentCodi = "PROC001";
        ServeiBasic servei1 = new ServeiBasic("SERV001", "Servei 1", true);
        ServeiBasic servei2 = new ServeiBasic("SERV002", "Servei 2", false);
        List<ServeiBasic> mockServeis = new ArrayList<>();
        mockServeis.add(servei1);
        mockServeis.add(servei2);

        // Mock behavior
        when(procedimentRepository.findByEntitatCodiAndCodi(entitatCodi, procedimentCodi)).thenReturn(new Procediment());
        when(serveiRepository.findServeisClientByProcedimentCodi(procedimentCodi)).thenReturn(mockServeis);

        // Call method
        List<ServeiBasic> serveis = recobrimentServiceImpl.getServeisByProcediment(entitatCodi, procedimentCodi);

        // Verify results
        Assertions.assertNotNull(serveis);
        Assertions.assertEquals(2, serveis.size());
        Assertions.assertEquals("SERV001", serveis.get(0).getCodi());
        Assertions.assertEquals("Servei 1", serveis.get(0).getDescripcio());
        Assertions.assertTrue(serveis.get(0).getActiu());
        Assertions.assertEquals("SERV002", serveis.get(1).getCodi());
        Assertions.assertEquals("Servei 2", serveis.get(1).getDescripcio());
        Assertions.assertFalse(serveis.get(1).getActiu());
    }

    @Test
    public void testGetServeisByProcediment_NoServeisFound() throws ProcedimentNotFoundException {
        // Mock data
        String entitatCodi = "ENT001";
        String procedimentCodi = "PROC002";

        // Mock behavior
        when(procedimentRepository.findByEntitatCodiAndCodi(entitatCodi, procedimentCodi)).thenReturn(new Procediment());
        when(serveiRepository.findServeisClientByProcedimentCodi(procedimentCodi)).thenReturn(new ArrayList<ServeiBasic>());

        // Call method
        List<ServeiBasic> serveis = recobrimentServiceImpl.getServeisByProcediment(entitatCodi, procedimentCodi);

        // Verify results
        Assertions.assertNotNull(serveis);
        Assertions.assertTrue(serveis.isEmpty());
    }

    @Test
    public void testGetServeisByProcediment_ProcedimentNotFoundException() throws ProcedimentNotFoundException {
        // Mock data
        String entitatCodi = "ENT001";
        String procedimentCodi = "PROC003";

        // Mock behavior
        when(procedimentRepository.findByEntitatCodiAndCodi(entitatCodi, procedimentCodi)).thenReturn(null);

        // Call method
        assertThrows(ProcedimentNotFoundException.class, () ->
                recobrimentServiceImpl.getServeisByProcediment(entitatCodi, procedimentCodi)
        );
    }

    // TESTS getDadesEspecifiques
    // /////////////////////////////////////////////////////////

    @Test
    public void testGetDadesEspecifiquesByServei_Success() throws ServeiNotFoundException {
        // Mock data
        String serveiCodi = "SERV001";

        ServeiCamp serveiCamp1 = ServeiCamp.getBuilder(serveiCodi, "path/path1", ServeiCamp.ServeiCampTipus.TEXT, "Etiqueta 1", null, 1, 6).build();
        serveiCamp1.setObligatori(true);

        ServeiCamp serveiCamp2 = ServeiCamp.getBuilder(serveiCodi, "path/path2", ServeiCamp.ServeiCampTipus.TEXT, "Etiqueta 2", null, 2, 6).build();
        serveiCamp2.setObligatori(false);

        List<ServeiCamp> mockServeiCamps = new ArrayList<>();
        mockServeiCamps.add(serveiCamp1);
        mockServeiCamps.add(serveiCamp2);

        ServeiConfig mockServeiConfig = new ServeiConfig();
        when(serveiConfigRepository.findByServei(serveiCodi)).thenReturn(mockServeiConfig);
        when(serveiCampRepository.findByServeiOrderByGrupOrdreAsc(serveiCodi)).thenReturn(mockServeiCamps);

        // Call the method
        List<DadaEspecifica> dadesEspecifiques = recobrimentServiceImpl.getDadesEspecifiquesByServei(serveiCodi);

        // Verify results
        Assertions.assertNotNull(dadesEspecifiques);
        Assertions.assertEquals(2, dadesEspecifiques.size());
        Assertions.assertEquals("path/path1", dadesEspecifiques.get(0).getCodi());
        Assertions.assertEquals("Etiqueta 1", dadesEspecifiques.get(0).getEtiqueta());
        Assertions.assertTrue(dadesEspecifiques.get(0).isObligatori());
        Assertions.assertEquals("path/path2", dadesEspecifiques.get(1).getCodi());
        Assertions.assertEquals("Etiqueta 2", dadesEspecifiques.get(1).getEtiqueta());
        Assertions.assertFalse(dadesEspecifiques.get(1).isObligatori());
    }

    @Test
    public void testGetDadesEspecifiquesByServei_NoDataFound() throws ServeiNotFoundException {
        // Mock data
        String serveiCodi = "SERV002";

        ServeiConfig mockServeiConfig = new ServeiConfig();
        when(serveiConfigRepository.findByServei(serveiCodi)).thenReturn(mockServeiConfig);
        when(serveiCampRepository.findByServeiOrderByGrupOrdreAsc(serveiCodi)).thenReturn(new ArrayList<ServeiCamp>());

        // Call the method
        List<DadaEspecifica> dadesEspecifiques = recobrimentServiceImpl.getDadesEspecifiquesByServei(serveiCodi);

        // Verify results
        Assertions.assertNotNull(dadesEspecifiques);
        Assertions.assertTrue(dadesEspecifiques.isEmpty());
    }

    @Test
    public void testGetDadesEspecifiquesByServei_ServeiNotFoundException() throws ServeiNotFoundException {
        // Mock data
        String serveiCodi = "SERV003";

        // Mock behavior
        when(serveiConfigRepository.findByServei(serveiCodi)).thenReturn(null);

        // Call the method
        assertThrows(ServeiNotFoundException.class, () ->
                recobrimentServiceImpl.getDadesEspecifiquesByServei(serveiCodi)
        );
    }

    // TESTS getValorsEnum
    // /////////////////////////////////////////////////////////

    @Test
    public void testGetValorsEnumByServei_Success() throws Exception {
        String serveiCodi = "SERV001";
        String campCodi = "DatosEspecificos/enumerat";
        String enumCodi = "enumerat";
        String filtre = null;

        ServeiConfig mockServeiConfig = mock(ServeiConfig.class);
        when(serveiConfigRepository.findByServei(serveiCodi)).thenReturn(mockServeiConfig);

        ServeiCamp mockServeiCamp = mock(ServeiCamp.class);
        when(serveiCampRepository.findByServeiAndPath(serveiCodi, campCodi)).thenReturn(mockServeiCamp);

        Tree<XmlHelper.DadesEspecifiquesNode> arbre = getDadesEspecifiquesNodeTree();
        when(scspHelper.generarArbreDadesEspecifiques(anyString(), Mockito.anyBoolean())).thenReturn(arbre);
        when(mockServeiCamp.getEnumDescripcions()).thenReturn(new String[] {"Etiqueta A", "Etiqueta B", "Etiqueta C"});

        List<ValorEnum> result = recobrimentServiceImpl.getValorsEnumByServei(serveiCodi, campCodi, enumCodi, filtre);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals("ENUM001", result.get(0).getCodi());
        Assertions.assertEquals("Etiqueta A", result.get(0).getValor());
    }

    @Test
    public void testGetValorsEnumByServeiAmbFiltre_Success() throws Exception {
        String serveiCodi = "SERV001";
        String campCodi = "DatosEspecificos/enumerat";
        String enumCodi = "enumerat";
        String filtre = "B";

        ServeiConfig mockServeiConfig = mock(ServeiConfig.class);
        when(serveiConfigRepository.findByServei(serveiCodi)).thenReturn(mockServeiConfig);

        ServeiCamp mockServeiCamp = mock(ServeiCamp.class);
        when(serveiCampRepository.findByServeiAndPath(serveiCodi, campCodi)).thenReturn(mockServeiCamp);

        Tree<XmlHelper.DadesEspecifiquesNode> arbre = getDadesEspecifiquesNodeTree();
        when(scspHelper.generarArbreDadesEspecifiques(anyString(), Mockito.anyBoolean())).thenReturn(arbre);
        when(mockServeiCamp.getEnumDescripcions()).thenReturn(new String[] {"Etiqueta A", "Etiqueta B", "Etiqueta C"});

        List<ValorEnum> result = recobrimentServiceImpl.getValorsEnumByServei(serveiCodi, campCodi, enumCodi, filtre);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("ENUM002", result.get(0).getCodi());
        Assertions.assertEquals("Etiqueta B", result.get(0).getValor());
    }

    private static Tree<XmlHelper.DadesEspecifiquesNode> getDadesEspecifiquesNodeTree() {
        Tree<XmlHelper.DadesEspecifiquesNode> arbre = new Tree<>();
        XmlHelper.DadesEspecifiquesNode dadesArrel = new XmlHelper.DadesEspecifiquesNode();
        dadesArrel.setPath("/DatosEspecificos");
        dadesArrel.setNom("DatosEspecificos");
        dadesArrel.setComplex(true);
        dadesArrel.setGroupMin(1);
        dadesArrel.setGroupMax(1);
        Node<XmlHelper.DadesEspecifiquesNode> arrel = new Node<>(dadesArrel);

        XmlHelper.DadesEspecifiquesNode dadesNode = new XmlHelper.DadesEspecifiquesNode();
        dadesNode.setPath("/DatosEspecificos/enumerat");
        dadesNode.setNom("enumerat");
        dadesNode.setComplex(false);
        dadesNode.setGroupMin(1);
        dadesNode.setGroupMax(1);
        dadesNode.setEnumValues(Arrays.asList("ENUM001", "ENUM002", "ENUM003"));
        Node<XmlHelper.DadesEspecifiquesNode> node = new Node<>(dadesNode);
        arrel.addChild(node);

        arbre.setRootElement(arrel);
        return arbre;
    }

    @Test
    public void testGetValorsEnumByServei_ServesNotFound() throws Exception {
        String serveiCodi = "INVALID";

        when(serveiConfigRepository.findByServei(serveiCodi)).thenReturn(null);

        assertThrows(ServeiNotFoundException.class, () ->
                recobrimentServiceImpl.getValorsEnumByServei(serveiCodi, "CAMP001", "ENUM1", "Test")
        );
    }

    @Test
    public void testGetValorsEnumByServei_CampNotFound() throws Exception {
        String serveiCodi = "SERV001";
        String campCodi = "INVALID";

        ServeiConfig mockServeiConfig = mock(ServeiConfig.class);
        when(serveiConfigRepository.findByServei(serveiCodi)).thenReturn(mockServeiConfig);

        when(serveiCampRepository.findByServeiAndPath(serveiCodi, campCodi)).thenReturn(null);

        assertThrows(ServeiCampNotFoundException.class, () ->
                recobrimentServiceImpl.getValorsEnumByServei(serveiCodi, campCodi, "ENUM1", "Test")
        );
    }

    @Test
    public void testGetValorsEnumByServei_PaisEnumeration() throws Exception {
        String serveiCodi = "SERV001";
        String campPath = "DatosEspecificos/pais";
        String enumCodi = "PAIS";

        ServeiConfig mockServeiConfig = mock(ServeiConfig.class);
        when(serveiConfigRepository.findByServei(serveiCodi)).thenReturn(mockServeiConfig);

        ServeiCamp mockServeiCamp = mock(ServeiCamp.class);
        when(serveiCampRepository.findByServeiAndPath(serveiCodi, campPath)).thenReturn(mockServeiCamp);

        List<Pais> mockPaisos = new ArrayList<>();
        mockPaisos.add(Pais.builder().codi_numeric("01").nom("Andorra").alpha2("AN").build());
        mockPaisos.add(Pais.builder().codi_numeric("73").nom("España").alpha2("ES").build());
        when(dadesExternesService.findPaisos(IdiomaEnumDto.CA)).thenReturn(mockPaisos);

        List<ValorEnum> result = recobrimentServiceImpl.getValorsEnumByServei(serveiCodi, campPath, enumCodi, null);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("73", result.get(1).getCodi());
        Assertions.assertEquals("España", result.get(1).getValor());
    }

    @Test
    public void testGetValorsEnumByServei_ProvinciaEnumeration() throws Exception {
        String serveiCodi = "SERV001";
        String campPath = "DatosEspecificos/provincia";
        String enumCodi = "PROVINCIA";

        ServeiConfig mockServeiConfig = mock(ServeiConfig.class);
        when(serveiConfigRepository.findByServei(serveiCodi)).thenReturn(mockServeiConfig);

        ServeiCamp mockServeiCamp = mock(ServeiCamp.class);
        when(serveiCampRepository.findByServeiAndPath(serveiCodi, campPath)).thenReturn(mockServeiCamp);

        List<Provincia> mockProvincies = new ArrayList<>();
        mockProvincies.add(Provincia.builder().codi("07").nom("Illes Balears").build());
        mockProvincies.add(Provincia.builder().codi("08").nom("Barcelona").build());
        when(dadesExternesService.findProvincies(any(IdiomaEnumDto.class))).thenReturn(mockProvincies);

        List<ValorEnum> result = recobrimentServiceImpl.getValorsEnumByServei(serveiCodi, campPath, enumCodi, null);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("07", result.get(0).getCodi());
        Assertions.assertEquals("Illes Balears", result.get(0).getValor());
    }

    @Test
    public void testGetValorsEnumByServei_MunicipisEnumeration() throws Exception {
        String serveiCodi = "SERV001";
        String campPath = "DatosEspecificos/minicipi";
        String enumCodi = "MUNICIPI_3";
        String filtre = "07";

        ServeiConfig mockServeiConfig = mock(ServeiConfig.class);
        when(serveiConfigRepository.findByServei(serveiCodi)).thenReturn(mockServeiConfig);

        ServeiCamp mockServeiCamp = mock(ServeiCamp.class);
        when(serveiCampRepository.findByServeiAndPath(serveiCodi, campPath)).thenReturn(mockServeiCamp);

        List<Municipi> mockMunicipis = new ArrayList<>();
        mockMunicipis.add(Municipi.builder().codi("07033").nom("Manacor").build());
        mockMunicipis.add(Municipi.builder().codi("07040").nom("Palma de Mallorca").build());
        when(dadesExternesService.findMunicipisPerProvincia(anyString())).thenReturn(mockMunicipis);

        List<ValorEnum> result = recobrimentServiceImpl.getValorsEnumByServei(serveiCodi, campPath, enumCodi, filtre);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("033", result.get(0).getCodi());
        Assertions.assertEquals("Manacor", result.get(0).getValor());

    }

    @Test
    public void testGetValorsEnumByServei_MunicipisFiltreValueValidation() throws Exception {
        String serveiCodi = "SERV001";
        String campPath = "DatosEspecificos/minicipi";
        String enumCodi = "MUNICIPI_3";
        String filtre = "60";

        ServeiConfig mockServeiConfig = mock(ServeiConfig.class);
        when(serveiConfigRepository.findByServei(serveiCodi)).thenReturn(mockServeiConfig);

        ServeiCamp mockServeiCamp = mock(ServeiCamp.class);
        when(serveiCampRepository.findByServeiAndPath(serveiCodi, campPath)).thenReturn(mockServeiCamp);

        assertThrows(RuntimeException.class, () ->
                recobrimentServiceImpl.getValorsEnumByServei(serveiCodi, campPath, enumCodi, filtre)
        );
    }

    @Test
    public void testGetValorsEnumByServei_MunicipisFiltreValidation() throws Exception {
        String serveiCodi = "SERV001";
        String campPath = "DatosEspecificos/minicipi";
        String enumCodi = "MUNICIPI_3";
        String filtre = "";

        ServeiConfig mockServeiConfig = mock(ServeiConfig.class);
        when(serveiConfigRepository.findByServei(serveiCodi)).thenReturn(mockServeiConfig);

        ServeiCamp mockServeiCamp = mock(ServeiCamp.class);
        when(serveiCampRepository.findByServeiAndPath(serveiCodi, campPath)).thenReturn(mockServeiCamp);

        assertThrows(RuntimeException.class, () ->
                recobrimentServiceImpl.getValorsEnumByServei(serveiCodi, campPath, enumCodi, filtre)
        );
    }

    // TESTS validatePeticio Sincrona
    // /////////////////////////////////////////////////////////

    @Test
    public void testValidatePeticioSincrona_Success() {
        // Mock valid PeticioSincrona object
        PeticioSincrona peticio = PeticioSincrona.builder().build();
        Map<String, List<String>> mockErrors = new HashMap<>(); // No errors

        // Mock behavior
        doNothing().when(recobrimentV2Helper).validateDadesComunes(nullable(DadesComunes.class), anyString(), any(BindException.class));
        doNothing().when(recobrimentV2Helper).validateDadesSolicitud(nullable(SolicitudSimple.class), anyString(), any(BindException.class), nullable(ServeiService.class));

        // Call the method
        Map<String, List<String>> result = recobrimentServiceImpl.validatePeticio("SERVEI_CODI", peticio);

        // Validate results
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void testValidatePeticioSincrona_Errors() {
        // Mock invalid PeticioSincrona object
        PeticioSincrona peticio = PeticioSincrona.builder().build();

        // Mock validation errors
        doNothing().when(recobrimentV2Helper).validateDadesComunes(nullable(DadesComunes.class), anyString(), any(BindException.class));
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                BindException bindException = (BindException) invocation.getArguments()[2];
                bindException.addError(new FieldError("peticio", "field1", "Field1 is invalid"));
                return null;
            }
        }).when(recobrimentV2Helper).validateDadesSolicitud(nullable(SolicitudSimple.class), anyString(), any(BindException.class), nullable(ServeiService.class));

        // Call the method
        Map<String, List<String>> result = recobrimentServiceImpl.validatePeticio("SERVEI_CODI", peticio);

        // Verify results
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1, result.size());
        Assertions.assertTrue(result.containsKey("field1"));
        Assertions.assertEquals("Field1 is invalid", result.get("field1").get(0));
    }


    // TESTS validatePeticio Asincrona
    // /////////////////////////////////////////////////////////

    @Test
    public void testValidatePeticioAsincrona_Success() {
        // Mock valid PeticioAsincrona object
        PeticioAsincrona peticio = PeticioAsincrona.builder().build();
        Map<String, List<String>> mockErrors = new HashMap<>(); // No errors

        // Mock behavior
        doNothing().when(recobrimentV2Helper).validateDadesComunes(nullable(DadesComunes.class), anyString(), any(BindException.class));
        when(recobrimentV2Helper.validateDadesSolicituds(nullable(List.class), anyString(), nullable(ServeiService.class))).thenReturn(mockErrors);

        // Call the method
        Map<String, List<String>> result = recobrimentServiceImpl.validatePeticio("SERVEI_CODI", peticio);

        // Validate results
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void testValidatePeticioAsincrona_Errors() {
        // Mock invalid PeticioAsincrona object
        PeticioAsincrona peticio = PeticioAsincrona.builder().build();
        Map<String, List<String>> mockErrors = new HashMap<>();
        List<String> mockErrorList = new ArrayList<>();
        mockErrorList.add("Field2 is invalid");
        mockErrors.put("field2", mockErrorList);

        // Mock validation errors
        doNothing().when(recobrimentV2Helper).validateDadesComunes(nullable(DadesComunes.class), anyString(), any(BindException.class));
        when(recobrimentV2Helper.validateDadesSolicituds(nullable(List.class), anyString(), nullable(ServeiService.class))).thenReturn(mockErrors);

        // Call the method
        Map<String, List<String>> result = recobrimentServiceImpl.validatePeticio("SERVEI_CODI", peticio);

        // Verify results
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1, result.size());
        Assertions.assertTrue(result.containsKey("field2"));
        Assertions.assertEquals("Field2 is invalid", result.get("field2").get(0));
    }


    // TESTS peticioSincrona
    // /////////////////////////////////////////////////////////

    @Test
    public void testPeticionSincrona_Success() throws Exception {
        // Mocking
        PeticioSincrona mockPeticio = PeticioSincrona.builder().build();
        Respuesta mockRespuesta = new Respuesta();
        es.scsp.bean.common.respuesta.Atributos atributos = new es.scsp.bean.common.respuesta.Atributos();
        Estado estado = new Estado();
        estado.setCodigoEstado("00");
        estado.setLiteralError(null);
        atributos.setEstado(estado);
        mockRespuesta.setAtributos(atributos);

        when(recobrimentV2Helper.toPeticion(any(PeticioSincrona.class))).thenReturn(new Peticion());
        when(recobrimentHelper.peticionSincrona(any(Peticion.class), anyBoolean())).thenReturn(mockRespuesta);
        when(recobrimentV2Helper.toRespostaSincrona(any(Respuesta.class))).thenCallRealMethod();
//        Mockito.when(recobrimentServiceImpl.toScspRespuesta(Mockito.any())).thenReturn(mockRespuesta);

        // Call the method
        PeticioRespostaSincrona response = recobrimentServiceImpl.peticionSincrona(mockPeticio);

        // Assertions
        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.isError());
        Assertions.assertNull(response.getMissatge());
//        Assertions.assertNotNull(response.getResposta());
    }

    @Test
    public void testPeticionSincrona_ValidationError() throws Exception {
        // Mocking
        PeticioSincrona mockPeticio = PeticioSincrona.builder().build();
        Respuesta mockRespuesta = new Respuesta();
        es.scsp.bean.common.respuesta.Atributos atributos = new es.scsp.bean.common.respuesta.Atributos();
        Estado estado = new Estado();
        estado.setCodigoEstado("01");
        estado.setLiteralError("Validation failed");
        atributos.setEstado(estado);
        mockRespuesta.setAtributos(atributos);

        when(recobrimentV2Helper.toPeticion(any(PeticioSincrona.class))).thenReturn(new Peticion());
        when(recobrimentHelper.peticionSincrona(any(Peticion.class), anyBoolean())).thenReturn(mockRespuesta);
        when(recobrimentV2Helper.toRespostaSincrona(any(Respuesta.class))).thenCallRealMethod();
//        Mockito.when(recobrimentServiceImpl.toScspRespuesta(Mockito.any())).thenReturn(mockRespuesta);

        // Call the method
        PeticioRespostaSincrona response = recobrimentServiceImpl.peticionSincrona(mockPeticio);

        // Assertions
        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isError());
        Assertions.assertEquals("Validation failed", response.getMissatge());
    }

    @Test
    public void testPeticionSincrona_ExceptionThrown() throws ConsultaScspGeneracioException {
        // Mocking
        PeticioSincrona mockPeticio = PeticioSincrona.builder().build();
        when(recobrimentV2Helper.toPeticion(any(PeticioSincrona.class))).thenThrow(new RuntimeException("Unexpected error"));

        // Call the method
        PeticioRespostaSincrona response = recobrimentServiceImpl.peticionSincrona(mockPeticio);

        // Assertions
        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isError());
        Assertions.assertEquals("Unexpected error", response.getMissatge());
    }

    @Test
    public void testPeticionSincrona_AplicacioGuardaJustificantArxiu() throws Exception {
        PeticioSincrona mockPeticio = PeticioSincrona.builder().build();
        Respuesta mockRespuesta = new Respuesta();
        es.scsp.bean.common.respuesta.Atributos atributos = new es.scsp.bean.common.respuesta.Atributos();
        Estado estado = new Estado();
        estado.setCodigoEstado("00");
        atributos.setEstado(estado);
        mockRespuesta.setAtributos(atributos);

        when(recobrimentV2Helper.toPeticion(any(PeticioSincrona.class))).thenReturn(new Peticion());
        when(recobrimentHelper.peticionSincrona(any(Peticion.class), anyBoolean())).thenReturn(mockRespuesta);
        when(recobrimentV2Helper.toRespostaSincrona(any(Respuesta.class))).thenCallRealMethod();

        PeticioRespostaSincrona response = recobrimentServiceImpl.peticionSincrona(mockPeticio);

        Assertions.assertNotNull(response);
        verify(recobrimentHelper).peticionSincrona(any(Peticion.class), eq(false));
    }


    // TESTS peticioAsincrona
    // /////////////////////////////////////////////////////////

    @Test
    public void testPeticionAsincrona_Success() throws Exception {
        // Mocking
        PeticioAsincrona mockPeticio = PeticioAsincrona.builder().build();
        ConfirmacionPeticion mockConfirmacion = mock(ConfirmacionPeticion.class);
        es.scsp.bean.common.confirmacion.Atributos mockAtributos = mock(es.scsp.bean.common.confirmacion.Atributos.class);
        Consulta mockConsulta = mock(Consulta.class);
//        mockConfirmacion.setAtributos(new Atributos());
        when(recobrimentV2Helper.toPeticion(any(PeticioAsincrona.class))).thenReturn(new Peticion());
        when(recobrimentHelper.peticionAsincrona(any(Peticion.class), anyBoolean())).thenReturn(mockConfirmacion);
        when(recobrimentV2Helper.toConfirmacio(any(ConfirmacionPeticion.class))).thenCallRealMethod();
        when(recobrimentV2Helper.getConsultaBypeticioId("PETICION_ID")).thenReturn(mockConsulta);
        when(mockConfirmacion.getAtributos()).thenReturn(mockAtributos);
        when(mockAtributos.getIdPeticion()).thenReturn("PETICION_ID");
        when(mockConsulta.getEstat()).thenReturn(EstatTipus.Processant);

        // Call the method
        PeticioConfirmacioAsincrona response = recobrimentServiceImpl.peticionAsincrona(mockPeticio);

        // Assertions
        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.isError());
        Assertions.assertNull(response.getMissatge());
        Assertions.assertNotNull(response.getConfirmacioPeticio());
    }

    @Test
    public void testPeticionAsincrona_WithError() throws Exception {
        // Mocking
        PeticioAsincrona mockPeticio = PeticioAsincrona.builder().build();
        ConfirmacionPeticion confirmacionPeticion = new ConfirmacionPeticion();
        es.scsp.bean.common.confirmacion.Atributos atributos = new es.scsp.bean.common.confirmacion.Atributos();
        es.scsp.bean.common.confirmacion.Estado estado = new es.scsp.bean.common.confirmacion.Estado();
        estado.setCodigoEstado("99");
        estado.setLiteralError("Error occurred");
        atributos.setEstado(estado);
        confirmacionPeticion.setAtributos(atributos);

        when(recobrimentV2Helper.toPeticion(any(PeticioAsincrona.class))).thenReturn(new Peticion());
        when(recobrimentHelper.peticionAsincrona(any(Peticion.class), anyBoolean())).thenReturn(confirmacionPeticion);
        when(recobrimentV2Helper.toConfirmacio(any(ConfirmacionPeticion.class))).thenCallRealMethod();

        // Call the method
        PeticioConfirmacioAsincrona response = recobrimentServiceImpl.peticionAsincrona(mockPeticio);

        // Assertions
        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isError());
        Assertions.assertEquals("Error occurred", response.getMissatge());
    }

    @Test
    public void testPeticionAsincrona_ExceptionThrown() throws ConsultaScspGeneracioException {
        // Mocking
        PeticioAsincrona mockPeticio = PeticioAsincrona.builder().dadesComunes(DadesComunes.builder().serveiCodi("Servei01").build()).build();
        when(recobrimentV2Helper.toPeticion(any(PeticioAsincrona.class))).thenThrow(new RuntimeException("Unexpected exception"));

        // Call the method
        PeticioConfirmacioAsincrona response = recobrimentServiceImpl.peticionAsincrona(mockPeticio);

        // Assertions
        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isError());
        Assertions.assertEquals("Unexpected exception", response.getMissatge());
    }


    // TESTS getResposta
    // /////////////////////////////////////////////////////////

    @Test
    public void testGetResposta_Success() throws Exception {
        // Mocking
        String idPeticion = "12345";
        Respuesta mockRespuesta = new Respuesta();
        es.scsp.bean.common.respuesta.Atributos atributos = new es.scsp.bean.common.respuesta.Atributos();
        Estado estado = new Estado();
        estado.setCodigoEstado("003");
        atributos.setEstado(estado);
        mockRespuesta.setAtributos(atributos);
        when(recobrimentHelper.getRespuesta(anyString())).thenReturn(mockRespuesta);
        when(recobrimentV2Helper.getConsultaBypeticioId(idPeticion)).thenReturn(new Consulta());
        when(recobrimentV2Helper.toRespostaAsincrona(any(Respuesta.class))).thenCallRealMethod();

        // Call the method
        PeticioRespostaAsincrona response = recobrimentServiceImpl.getResposta(idPeticion);

        // Assertions
        Assertions.assertFalse(response.isError());
        Assertions.assertNotNull(response);
        Mockito.verify(recobrimentHelper, Mockito.times(1)).getRespuesta(Mockito.eq(idPeticion));
    }

    @Test
    public void testGetResposta_ConsultaNotFound() throws ScspException {
        // Mocking
        String idPeticion = "12345";
        when(recobrimentV2Helper.getConsultaBypeticioId(idPeticion)).thenReturn(null);

        // Call the method
        try {
            recobrimentServiceImpl.getResposta(idPeticion);
            Assertions.fail("Expected ConsultaNotFoundException was not thrown");
        } catch (ConsultaNotFoundException e) {
            // Expected exception
            Assertions.assertNotNull(e);
        } catch (Exception e) {
            Assertions.fail("Unexpected exception thrown: " + e.getMessage());
        }

        // Verify helper is not called
        Mockito.verify(recobrimentHelper, Mockito.never()).getRespuesta(anyString());
    }

    @Test
    public void testGetResposta_ThrowsException() throws Exception {
        // Mocking
        String idPeticion = "12345";
        when(recobrimentV2Helper.getConsultaBypeticioId(idPeticion)).thenReturn(new Consulta());
        when(recobrimentHelper.getRespuesta(anyString())).thenThrow(new ScspException("Unexpected error", "error code"));

        // Call the method
        try {
            recobrimentServiceImpl.getResposta(idPeticion);
            Assertions.fail("Expected RuntimeException was not thrown");
        } catch (RecobrimentScspException e) {
            Assertions.assertTrue(e.getMessage().contains("Unexpected error"));
        } catch (Exception e) {
            Assertions.fail("Unexpected exception thrown: " + e.getMessage());
        }

        // Verify helper is called once
        Mockito.verify(recobrimentHelper, Mockito.times(1)).getRespuesta(Mockito.eq(idPeticion));
    }


    // TESTS getJustificant
    // /////////////////////////////////////////////////////////

    private void mockAuthentication() {
        Authentication authentication = mock(Authentication.class);
        lenient().when(authentication.getName()).thenReturn("username");
        lenient().when(authentication.getCredentials()).thenReturn("password");
        Collection authorities = Collections.singletonList(new SimpleGrantedAuthority("PBL_ADMIN"));
        lenient().when(authentication.getAuthorities()).thenReturn(authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    private Consulta mockConsulta() {
        Consulta consulta = mock(Consulta.class);
        Usuari usuari = mock(Usuari.class);
        lenient().when(consulta.getCreatedBy()).thenReturn(Optional.of(usuari));
        lenient().when(usuari.getCodi()).thenReturn("username");
        return consulta;
    }

    @Test
    public void testGetJustificant_Success() throws RecobrimentScspException, ConsultaNotFoundException, ScspException {
        // Mocking
        String idPeticion = "12345";
        String idSolicitud = "54321";
        JustificantDto mockJustificantDto = mock(JustificantDto.class);
        when(mockJustificantDto.getNom()).thenReturn("sampleName");
        when(mockJustificantDto.getContentType()).thenReturn("application/pdf");
        when(mockJustificantDto.getContingut()).thenReturn(new byte[]{1, 2, 3});
        Consulta consulta = mockConsulta();
        mockAuthentication();
        when(consultaRepository.findByScspPeticionIdAndScspSolicitudId(idPeticion, idSolicitud)).thenReturn(consulta);
        when(recobrimentHelper.getJustificante(idPeticion, idSolicitud, false, true)).thenReturn(mockJustificantDto);

        // Call the method
        ScspJustificante result = recobrimentServiceImpl.getJustificant(idPeticion, idSolicitud);

        // Assertions
        Assertions.assertNotNull(result);
        Assertions.assertEquals("sampleName", result.getNom());
        Assertions.assertEquals("application/pdf", result.getContentType());
        Assertions.assertArrayEquals(new byte[]{1, 2, 3}, result.getContingut());
        Mockito.verify(recobrimentHelper, Mockito.times(1)).getJustificante(idPeticion, idSolicitud, false, true);
    }

    @Test
    public void testGetJustificant_ConsultaNotFound() {
        // Mocking
        String idPeticion = "12345";
        String idSolicitud = "54321";
        when(consultaRepository.findByScspPeticionIdAndScspSolicitudId(idPeticion, idSolicitud)).thenReturn(null);
        when(historicConsultaRepository.findByScspPeticionIdAndScspSolicitudId(idPeticion, idSolicitud)).thenReturn(null);

        // Call the method
        try {
            recobrimentServiceImpl.getJustificant(idPeticion, idSolicitud);
            Assertions.fail("Expected ConsultaNotFoundException was not thrown");
        } catch (ConsultaNotFoundException e) {
            // Expected Exception
            Assertions.assertNotNull(e);
        } catch (Exception e) {
            Assertions.fail("Unexpected exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testGetJustificant_ThrowsException() throws ScspException {
        // Mocking
        String idPeticion = "12345";
        String idSolicitud = "54321";
        Consulta consulta = mockConsulta();
        mockAuthentication();
        when(consultaRepository.findByScspPeticionIdAndScspSolicitudId(idPeticion, idSolicitud)).thenReturn(consulta);
        when(recobrimentHelper.getJustificante(anyString(), anyString(), Mockito.anyBoolean(), Mockito.anyBoolean())).thenThrow(new ScspException("Unexpected Error", "error code"));

        // Call the method
        try {
            recobrimentServiceImpl.getJustificant(idPeticion, idSolicitud);
            Assertions.fail("Expected RecobrimentScspException was not thrown");
        } catch (RecobrimentScspException e) {
            Assertions.assertTrue(e.getMessage().contains("Unexpected Error"));
        } catch (Exception e) {
            Assertions.fail("Unexpected exception thrown: " + e.getMessage());
        }
    }


    // TESTS getJustificantImprimible
    // /////////////////////////////////////////////////////////

    @Test
    public void testGetJustificantImprimible_Success() throws RecobrimentScspException, ConsultaNotFoundException, ScspException {
        // Mocking
        String idPeticion = "12345";
        String idSolicitud = "54321";
        JustificantDto mockJustificantDto = mock(JustificantDto.class);
        when(mockJustificantDto.getNom()).thenReturn("imprimibleName");
        when(mockJustificantDto.getContentType()).thenReturn("application/pdf");
        when(mockJustificantDto.getContingut()).thenReturn(new byte[]{4, 5, 6});
        Consulta consulta = mockConsulta();
        mockAuthentication();
        when(consultaRepository.findByScspPeticionIdAndScspSolicitudId(idPeticion, idSolicitud)).thenReturn(consulta);
        when(recobrimentHelper.getJustificante(idPeticion, idSolicitud, true, true)).thenReturn(mockJustificantDto);

        // Call the method
        ScspJustificante result = recobrimentServiceImpl.getJustificantImprimible(idPeticion, idSolicitud);

        // Assertions
        Assertions.assertNotNull(result);
        Assertions.assertEquals("imprimibleName", result.getNom());
        Assertions.assertEquals("application/pdf", result.getContentType());
        Assertions.assertArrayEquals(new byte[]{4, 5, 6}, result.getContingut());
        Mockito.verify(recobrimentHelper, Mockito.times(1)).getJustificante(idPeticion, idSolicitud, true, true);
    }

    @Test
    public void testGetJustificantImprimible_ConsultaNotFound() {
        // Mocking
        String idPeticion = "12345";
        String idSolicitud = "54321";
        when(consultaRepository.findByScspPeticionIdAndScspSolicitudId(idPeticion, idSolicitud)).thenReturn(null);

        // Call the method
        try {
            recobrimentServiceImpl.getJustificantImprimible(idPeticion, idSolicitud);
            Assertions.fail("Expected ConsultaNotFoundException was not thrown");
        } catch (ConsultaNotFoundException e) {
            // Expected Exception
            Assertions.assertNotNull(e);
        } catch (Exception e) {
            Assertions.fail("Unexpected exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testGetJustificantImprimible_ThrowsException() throws ScspException {
        // Mocking
        String idPeticion = "12345";
        String idSolicitud = "54321";
        Consulta consulta = mockConsulta();
        mockAuthentication();
        when(consultaRepository.findByScspPeticionIdAndScspSolicitudId(idPeticion, idSolicitud)).thenReturn(consulta);
        when(recobrimentHelper.getJustificante(anyString(), anyString(), Mockito.eq(true), Mockito.eq(true)))
                .thenThrow(new ScspException("Unexpected Error Imprimible", "error code"));

        // Call the method
        try {
            recobrimentServiceImpl.getJustificantImprimible(idPeticion, idSolicitud);
            Assertions.fail("Expected RecobrimentScspException was not thrown");
        } catch (RecobrimentScspException e) {
            Assertions.assertTrue(e.getMessage().contains("Unexpected Error Imprimible"));
        } catch (Exception e) {
            Assertions.fail("Unexpected exception thrown: " + e.getMessage());
        }
    }


    // TESTS getJustificantCsv
    // /////////////////////////////////////////////////////////

    @Test
    public void testGetJustificantCsv_Success() throws RecobrimentScspException, ConsultaNotFoundException, ScspException {
        // Mocking
        String idPeticion = "12345";
        String idSolicitud = "54321";
        JustificantDto mockJustificantDto = mock(JustificantDto.class);
        when(mockJustificantDto.getArxiuCsv()).thenReturn("CSV_CODE");
        Consulta consulta = mockConsulta();
        mockAuthentication();
        when(consultaRepository.findByScspPeticionIdAndScspSolicitudId(idPeticion, idSolicitud)).thenReturn(consulta);
        when(recobrimentHelper.getJustificante(idPeticion, idSolicitud, true, false)).thenReturn(mockJustificantDto);

        // Call the method
        String result = recobrimentServiceImpl.getJustificantCsv(idPeticion, idSolicitud);

        // Assertions
        Assertions.assertNotNull(result);
        Assertions.assertEquals("CSV_CODE", result);
        Mockito.verify(recobrimentHelper, Mockito.times(1)).getJustificante(idPeticion, idSolicitud, true, false);
    }

    @Test
    public void testGetJustificantCsv_ConsultaNotFound() {
        // Mocking
        String idPeticion = "12345";
        String idSolicitud = "54321";
        when(consultaRepository.findByScspPeticionIdAndScspSolicitudId(idPeticion, idSolicitud)).thenReturn(null);
        when(historicConsultaRepository.findByScspPeticionIdAndScspSolicitudId(idPeticion, idSolicitud)).thenReturn(null);

        // Call the method
        try {
            recobrimentServiceImpl.getJustificant(idPeticion, idSolicitud);
            Assertions.fail("Expected ConsultaNotFoundException was not thrown");
        } catch (ConsultaNotFoundException e) {
            // Expected Exception
            Assertions.assertNotNull(e);
        } catch (Exception e) {
            Assertions.fail("Unexpected exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testGetJustificantCsv_ThrowsException() throws ScspException {
        // Mocking
        String idPeticion = "12345";
        String idSolicitud = "54321";
        Consulta consulta = mockConsulta();
        mockAuthentication();
        when(consultaRepository.findByScspPeticionIdAndScspSolicitudId(idPeticion, idSolicitud)).thenReturn(consulta);
        when(recobrimentHelper.getJustificante(anyString(), anyString(), Mockito.anyBoolean(), Mockito.anyBoolean())).thenThrow(new ScspException("Unexpected Error", "error code"));

        // Call the method
        try {
            recobrimentServiceImpl.getJustificant(idPeticion, idSolicitud);
            Assertions.fail("Expected RecobrimentScspException was not thrown");
        } catch (RecobrimentScspException e) {
            Assertions.assertTrue(e.getMessage().contains("Unexpected Error"));
        } catch (Exception e) {
            Assertions.fail("Unexpected exception thrown: " + e.getMessage());
        }
    }


    // TESTS getJustificantUuid
    // /////////////////////////////////////////////////////////

    @Test
    public void testGetJustificantUuid_Success() throws RecobrimentScspException, ConsultaNotFoundException, ScspException {
        // Mocking
        String idPeticion = "12345";
        String idSolicitud = "54321";
        JustificantDto mockJustificantDto = mock(JustificantDto.class);
        when(mockJustificantDto.getArxiuUuid()).thenReturn("UUID_CODE");
        Consulta consulta = mockConsulta();
        mockAuthentication();
        when(consultaRepository.findByScspPeticionIdAndScspSolicitudId(idPeticion, idSolicitud)).thenReturn(consulta);
        when(recobrimentHelper.getJustificante(idPeticion, idSolicitud, true, false)).thenReturn(mockJustificantDto);

        // Call the method
        String result = recobrimentServiceImpl.getJustificantUuid(idPeticion, idSolicitud);

        // Assertions
        Assertions.assertNotNull(result);
        Assertions.assertEquals("UUID_CODE", result);
        Mockito.verify(recobrimentHelper, Mockito.times(1)).getJustificante(idPeticion, idSolicitud, true, false);
    }

    @Test
    public void testGetJustificantUuid_ConsultaNotFound() {
        // Mocking
        String idPeticion = "12345";
        String idSolicitud = "54321";
        when(consultaRepository.findByScspPeticionIdAndScspSolicitudId(idPeticion, idSolicitud)).thenReturn(null);
        when(historicConsultaRepository.findByScspPeticionIdAndScspSolicitudId(idPeticion, idSolicitud)).thenReturn(null);

        // Call the method
        try {
            recobrimentServiceImpl.getJustificant(idPeticion, idSolicitud);
            Assertions.fail("Expected ConsultaNotFoundException was not thrown");
        } catch (ConsultaNotFoundException e) {
            // Expected Exception
            Assertions.assertNotNull(e);
        } catch (Exception e) {
            Assertions.fail("Unexpected exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testGetJustificantUuid_ThrowsException() throws ScspException {
        // Mocking
        String idPeticion = "12345";
        String idSolicitud = "54321";
        Consulta consulta = mockConsulta();
        mockAuthentication();
        when(consultaRepository.findByScspPeticionIdAndScspSolicitudId(idPeticion, idSolicitud)).thenReturn(consulta);
        when(recobrimentHelper.getJustificante(anyString(), anyString(), Mockito.anyBoolean(), Mockito.anyBoolean())).thenThrow(new ScspException("Unexpected Error", "error code"));

        // Call the method
        try {
            recobrimentServiceImpl.getJustificant(idPeticion, idSolicitud);
            Assertions.fail("Expected RecobrimentScspException was not thrown");
        } catch (RecobrimentScspException e) {
            Assertions.assertTrue(e.getMessage().contains("Unexpected Error"));
        } catch (Exception e) {
            Assertions.fail("Unexpected exception thrown: " + e.getMessage());
        }
    }

}
