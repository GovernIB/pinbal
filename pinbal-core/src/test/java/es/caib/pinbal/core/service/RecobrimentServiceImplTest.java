package es.caib.pinbal.core.service;

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
import es.caib.pinbal.core.dto.IdiomaEnumDto;
import es.caib.pinbal.core.dto.JustificantDto;
import es.caib.pinbal.core.dto.dadesexternes.Municipi;
import es.caib.pinbal.core.dto.dadesexternes.Pais;
import es.caib.pinbal.core.dto.dadesexternes.Provincia;
import es.caib.pinbal.core.helper.RecobrimentHelper;
import es.caib.pinbal.core.helper.RecobrimentV2Helper;
import es.caib.pinbal.core.model.Consulta;
import es.caib.pinbal.core.model.Entitat;
import es.caib.pinbal.core.model.OrganGestor;
import es.caib.pinbal.core.model.ServeiCamp;
import es.caib.pinbal.core.model.ServeiConfig;
import es.caib.pinbal.core.repository.ConsultaRepository;
import es.caib.pinbal.core.repository.EntitatRepository;
import es.caib.pinbal.core.repository.ProcedimentRepository;
import es.caib.pinbal.core.repository.ServeiCampRepository;
import es.caib.pinbal.core.repository.ServeiConfigRepository;
import es.caib.pinbal.core.repository.ServeiRepository;
import es.caib.pinbal.core.service.exception.ConsultaNotFoundException;
import es.caib.pinbal.core.service.exception.ConsultaScspGeneracioException;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.core.service.exception.RecobrimentScspException;
import es.caib.pinbal.core.service.exception.ServeiCampNotFoundException;
import es.caib.pinbal.core.service.exception.ServeiNotFoundException;
import es.caib.pinbal.scsp.ScspHelper;
import es.caib.pinbal.scsp.XmlHelper;
import es.caib.pinbal.scsp.tree.Node;
import es.caib.pinbal.scsp.tree.Tree;
import es.scsp.bean.common.Atributos;
import es.scsp.bean.common.ConfirmacionPeticion;
import es.scsp.bean.common.Estado;
import es.scsp.bean.common.Peticion;
import es.scsp.bean.common.Respuesta;
import es.scsp.common.exceptions.ScspException;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RecobrimentServiceImplTest {

    @InjectMocks
    private RecobrimentServiceImpl recobrimentServiceImpl = new RecobrimentServiceImpl();

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
    private ScspHelper scspHelper;

    @Mock
    private RecobrimentHelper recobrimentHelper;

    @Mock
    private RecobrimentV2Helper recobrimentV2Helper;

    @Mock
    private DadesExternesService dadesExternesService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
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
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn(username);

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Establir el SecurityContext simulat a SecurityContextHolder
        SecurityContextHolder.setContext(securityContext);

        // Mock behavior
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn(username);
        when(entitatRepository.findActivesPerUsuari(username)).thenReturn(mockEntitats);

        // Call method
        List<es.caib.pinbal.client.recobriment.v2.Entitat> entitats = recobrimentServiceImpl.getEntitats();

        // Verify results
        Assert.assertNotNull(entitats);
        Assert.assertEquals(2, entitats.size());
        Assert.assertEquals("ENT001", entitats.get(0).getCodi());
        Assert.assertEquals("Entity 1", entitats.get(0).getNom());
        Assert.assertEquals("ENT002", entitats.get(1).getCodi());
        Assert.assertEquals("Entity 2", entitats.get(1).getNom());
    }

    @Test
    public void testGetEntitats_EmptyList() {
        // Mock data
        String username = "testUser";
        List<Entitat> mockEntitats = new ArrayList<>();

        // Simular autenticació
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn(username);

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Establir el SecurityContext simulat a SecurityContextHolder
        SecurityContextHolder.setContext(securityContext);

        // Mock behavior
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn(username);
        when(entitatRepository.findActivesAmbUsuariCodi(username)).thenReturn(mockEntitats);

        // Call method
        List<es.caib.pinbal.client.recobriment.v2.Entitat> entitats = recobrimentServiceImpl.getEntitats();

        // Verify results
        Assert.assertNotNull(entitats);
        Assert.assertTrue(entitats.isEmpty());
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

        es.caib.pinbal.core.model.Procediment procediment1 = new es.caib.pinbal.core.model.Procediment();
        procediment1.setCodi("PRO123");
        procediment1.setNom("Procediment 123");
        procediment1.setActiu(true);
        procediment1.setOrganGestor(mockOrganGestor);

        es.caib.pinbal.core.model.Procediment procediment2 = new es.caib.pinbal.core.model.Procediment();
        procediment2.setCodi("PRO456");
        procediment2.setNom("Procediment 456");
        procediment2.setActiu(false);
        procediment2.setOrganGestor(mockOrganGestor);

        List<es.caib.pinbal.core.model.Procediment> mockProcediments = new ArrayList<>();
        mockProcediments.add(procediment1);
        mockProcediments.add(procediment2);

        // Mock behavior
        when(entitatRepository.findByCodi(entitatCodi)).thenReturn(mockEntitat);
        when(procedimentRepository.findByEntitatOrderByNomAsc(mockEntitat)).thenReturn(mockProcediments);

        // Call the method
        List<ProcedimentBasic> procediments = recobrimentServiceImpl.getProcediments(entitatCodi);

        // Verify results
        Assert.assertNotNull(procediments);
        Assert.assertEquals(2, procediments.size());

        Assert.assertEquals("PRO123", procediments.get(0).getCodi());
        Assert.assertEquals("Procediment 123", procediments.get(0).getNom());
        Assert.assertTrue(procediments.get(0).isActiu());

        Assert.assertEquals("PRO456", procediments.get(1).getCodi());
        Assert.assertEquals("Procediment 456", procediments.get(1).getNom());
        Assert.assertFalse(procediments.get(1).isActiu());
    }

    @Test(expected = EntitatNotFoundException.class)
    public void testGetProcediments_EntitatNotFound() throws EntitatNotFoundException {
        // Mock data
        String entitatCodi = "XYZ789";

        // Mock behavior
        when(entitatRepository.findByCodi(entitatCodi)).thenReturn(null);

        // Call the method
        recobrimentServiceImpl.getProcediments(entitatCodi);
    }

    @Test
    public void testGetProcediments_NoProcedimentsFound() throws EntitatNotFoundException {
        // Mock data
        String entitatCodi = "DEF456";
        Entitat mockEntitat = new Entitat();
        mockEntitat.setCodi(entitatCodi);

        // Mock behavior
        when(entitatRepository.findByCodi(entitatCodi)).thenReturn(mockEntitat);
        when(procedimentRepository.findByEntitatOrderByNomAsc(mockEntitat)).thenReturn(new ArrayList<es.caib.pinbal.core.model.Procediment>());

        // Call the method
        List<ProcedimentBasic> procediments = recobrimentServiceImpl.getProcediments(entitatCodi);

        // Verify results
        Assert.assertNotNull(procediments);
        Assert.assertTrue(procediments.isEmpty());
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
        Assert.assertNotNull(serveis);
        Assert.assertEquals(2, serveis.size());
        Assert.assertEquals("S001", serveis.get(0).getCodi());
        Assert.assertEquals("Servei 1", serveis.get(0).getDescripcio());
        Assert.assertTrue(serveis.get(0).getActiu());

        Assert.assertEquals("S002", serveis.get(1).getCodi());
        Assert.assertEquals("Servei 2", serveis.get(1).getDescripcio());
        Assert.assertFalse(serveis.get(1).getActiu());
    }

    @Test
    public void testGetServeis_NoServeisFound() {
        // Mock behavior
        when(serveiRepository.findAllServeisClient()).thenReturn(new ArrayList<ServeiBasic>());

        // Call the method
        List<ServeiBasic> serveis = recobrimentServiceImpl.getServeis();

        // Verify results
        Assert.assertNotNull(serveis);
        Assert.assertTrue(serveis.isEmpty());
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
        Assert.assertNotNull(serveis);
        Assert.assertEquals(2, serveis.size());
        Assert.assertEquals("SERV001", serveis.get(0).getCodi());
        Assert.assertEquals("Servei 1", serveis.get(0).getDescripcio());
        Assert.assertTrue(serveis.get(0).getActiu());
        Assert.assertEquals("SERV002", serveis.get(1).getCodi());
        Assert.assertEquals("Servei 2", serveis.get(1).getDescripcio());
        Assert.assertFalse(serveis.get(1).getActiu());
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
        Assert.assertNotNull(serveis);
        Assert.assertTrue(serveis.isEmpty());
    }

    @Test(expected = EntitatNotFoundException.class)
    public void testGetServeisByEntitat_EntitatNotFoundException() throws EntitatNotFoundException {
        // Mock data
        String entitatCodi = "ENT003";

        // Mock behavior
        when(entitatRepository.findByCodi(entitatCodi)).thenReturn(null);

        // Call the method
        recobrimentServiceImpl.getServeisByEntitat(entitatCodi);
    }

    // TESTS getServeisByProcediment
    // /////////////////////////////////////////////////////////

    @Test
    public void testGetServeisByProcediment_Success() throws ProcedimentNotFoundException {
        // Mock data
        String procedimentCodi = "PROC001";
        ServeiBasic servei1 = new ServeiBasic("SERV001", "Servei 1", true);
        ServeiBasic servei2 = new ServeiBasic("SERV002", "Servei 2", false);
        List<ServeiBasic> mockServeis = new ArrayList<>();
        mockServeis.add(servei1);
        mockServeis.add(servei2);

        // Mock behavior
        when(procedimentRepository.findByCodi(procedimentCodi)).thenReturn(new es.caib.pinbal.core.model.Procediment());
        when(serveiRepository.findServeisClientByProcedimentCodi(procedimentCodi)).thenReturn(mockServeis);

        // Call method
        List<ServeiBasic> serveis = recobrimentServiceImpl.getServeisByProcediment(procedimentCodi);

        // Verify results
        Assert.assertNotNull(serveis);
        Assert.assertEquals(2, serveis.size());
        Assert.assertEquals("SERV001", serveis.get(0).getCodi());
        Assert.assertEquals("Servei 1", serveis.get(0).getDescripcio());
        Assert.assertTrue(serveis.get(0).getActiu());
        Assert.assertEquals("SERV002", serveis.get(1).getCodi());
        Assert.assertEquals("Servei 2", serveis.get(1).getDescripcio());
        Assert.assertFalse(serveis.get(1).getActiu());
    }

    @Test
    public void testGetServeisByProcediment_NoServeisFound() throws ProcedimentNotFoundException {
        // Mock data
        String procedimentCodi = "PROC002";

        // Mock behavior
        when(procedimentRepository.findByCodi(procedimentCodi)).thenReturn(new es.caib.pinbal.core.model.Procediment());
        when(serveiRepository.findServeisClientByProcedimentCodi(procedimentCodi)).thenReturn(new ArrayList<ServeiBasic>());

        // Call method
        List<ServeiBasic> serveis = recobrimentServiceImpl.getServeisByProcediment(procedimentCodi);

        // Verify results
        Assert.assertNotNull(serveis);
        Assert.assertTrue(serveis.isEmpty());
    }

    @Test(expected = ProcedimentNotFoundException.class)
    public void testGetServeisByProcediment_ProcedimentNotFoundException() throws ProcedimentNotFoundException {
        // Mock data
        String procedimentCodi = "PROC003";

        // Mock behavior
        when(procedimentRepository.findByCodi(procedimentCodi)).thenReturn(null);

        // Call method
        recobrimentServiceImpl.getServeisByProcediment(procedimentCodi);
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
        Assert.assertNotNull(dadesEspecifiques);
        Assert.assertEquals(2, dadesEspecifiques.size());
        Assert.assertEquals("path/path1", dadesEspecifiques.get(0).getCodi());
        Assert.assertEquals("Etiqueta 1", dadesEspecifiques.get(0).getEtiqueta());
        Assert.assertTrue(dadesEspecifiques.get(0).isObligatori());
        Assert.assertEquals("path/path2", dadesEspecifiques.get(1).getCodi());
        Assert.assertEquals("Etiqueta 2", dadesEspecifiques.get(1).getEtiqueta());
        Assert.assertFalse(dadesEspecifiques.get(1).isObligatori());
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
        Assert.assertNotNull(dadesEspecifiques);
        Assert.assertTrue(dadesEspecifiques.isEmpty());
    }

    @Test(expected = ServeiNotFoundException.class)
    public void testGetDadesEspecifiquesByServei_ServeiNotFoundException() throws ServeiNotFoundException {
        // Mock data
        String serveiCodi = "SERV003";

        // Mock behavior
        when(serveiConfigRepository.findByServei(serveiCodi)).thenReturn(null);

        // Call the method
        recobrimentServiceImpl.getDadesEspecifiquesByServei(serveiCodi);
    }

    // TESTS getValorsEnum
    // /////////////////////////////////////////////////////////

    @Test
    public void testGetValorsEnumByServei_Success() throws Exception {
        String serveiCodi = "SERV001";
        String campCodi = "DatosEspecificos/enumerat";
        String enumCodi = "enumerat";
        String filtre = null;

        ServeiConfig mockServeiConfig = Mockito.mock(ServeiConfig.class);
        when(serveiConfigRepository.findByServei(serveiCodi)).thenReturn(mockServeiConfig);

        ServeiCamp mockServeiCamp = Mockito.mock(ServeiCamp.class);
        when(serveiCampRepository.findByPath(campCodi)).thenReturn(mockServeiCamp);

        Tree<XmlHelper.DadesEspecifiquesNode> arbre = getDadesEspecifiquesNodeTree();
        when(scspHelper.generarArbreDadesEspecifiques(anyString(), Mockito.anyBoolean())).thenReturn(arbre);
        when(mockServeiCamp.getEnumDescripcions()).thenReturn(new String[] {"Etiqueta A", "Etiqueta B", "Etiqueta C"});

        List<ValorEnum> result = recobrimentServiceImpl.getValorsEnumByServei(serveiCodi, campCodi, enumCodi, filtre);

        Assert.assertNotNull(result);
        Assert.assertEquals(3, result.size());
        Assert.assertEquals("ENUM001", result.get(0).getCodi());
        Assert.assertEquals("Etiqueta A", result.get(0).getValor());
    }

    @Test
    public void testGetValorsEnumByServeiAmbFiltre_Success() throws Exception {
        String serveiCodi = "SERV001";
        String campCodi = "DatosEspecificos/enumerat";
        String enumCodi = "enumerat";
        String filtre = "B";

        ServeiConfig mockServeiConfig = Mockito.mock(ServeiConfig.class);
        when(serveiConfigRepository.findByServei(serveiCodi)).thenReturn(mockServeiConfig);

        ServeiCamp mockServeiCamp = Mockito.mock(ServeiCamp.class);
        when(serveiCampRepository.findByPath(campCodi)).thenReturn(mockServeiCamp);

        Tree<XmlHelper.DadesEspecifiquesNode> arbre = getDadesEspecifiquesNodeTree();
        when(scspHelper.generarArbreDadesEspecifiques(anyString(), Mockito.anyBoolean())).thenReturn(arbre);
        when(mockServeiCamp.getEnumDescripcions()).thenReturn(new String[] {"Etiqueta A", "Etiqueta B", "Etiqueta C"});

        List<ValorEnum> result = recobrimentServiceImpl.getValorsEnumByServei(serveiCodi, campCodi, enumCodi, filtre);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("ENUM002", result.get(0).getCodi());
        Assert.assertEquals("Etiqueta B", result.get(0).getValor());
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

    @Test(expected = ServeiNotFoundException.class)
    public void testGetValorsEnumByServei_ServesNotFound() throws Exception {
        String serveiCodi = "INVALID";

        when(serveiConfigRepository.findByServei(serveiCodi)).thenReturn(null);

        recobrimentServiceImpl.getValorsEnumByServei(serveiCodi, "CAMP001", "ENUM1", "Test");
    }

    @Test(expected = ServeiCampNotFoundException.class)
    public void testGetValorsEnumByServei_CampNotFound() throws Exception {
        String serveiCodi = "SERV001";
        String campCodi = "INVALID";

        ServeiConfig mockServeiConfig = Mockito.mock(ServeiConfig.class);
        when(serveiConfigRepository.findByServei(serveiCodi)).thenReturn(mockServeiConfig);

        when(serveiCampRepository.findByPath(campCodi)).thenReturn(null);

        recobrimentServiceImpl.getValorsEnumByServei(serveiCodi, campCodi, "ENUM1", "Test");
    }

    @Test
    public void testGetValorsEnumByServei_PaisEnumeration() throws Exception {
        String serveiCodi = "SERV001";
        String campPath = "DatosEspecificos/pais";
        String enumCodi = "PAIS";

        ServeiConfig mockServeiConfig = Mockito.mock(ServeiConfig.class);
        when(serveiConfigRepository.findByServei(serveiCodi)).thenReturn(mockServeiConfig);

        ServeiCamp mockServeiCamp = Mockito.mock(ServeiCamp.class);
        when(serveiCampRepository.findByPath(campPath)).thenReturn(mockServeiCamp);

        List<Pais> mockPaisos = new ArrayList<>();
        mockPaisos.add(Pais.builder().codi_numeric("01").nom("Andorra").alpha2("AN").build());
        mockPaisos.add(Pais.builder().codi_numeric("73").nom("España").alpha2("ES").build());
        when(dadesExternesService.findPaisos(IdiomaEnumDto.CA)).thenReturn(mockPaisos);

        List<ValorEnum> result = recobrimentServiceImpl.getValorsEnumByServei(serveiCodi, campPath, enumCodi, null);

        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());
        Assert.assertEquals("73", result.get(1).getCodi());
        Assert.assertEquals("España", result.get(1).getValor());
    }

    @Test
    public void testGetValorsEnumByServei_ProvinciaEnumeration() throws Exception {
        String serveiCodi = "SERV001";
        String campPath = "DatosEspecificos/provincia";
        String enumCodi = "PROVINCIA";

        ServeiConfig mockServeiConfig = Mockito.mock(ServeiConfig.class);
        when(serveiConfigRepository.findByServei(serveiCodi)).thenReturn(mockServeiConfig);

        ServeiCamp mockServeiCamp = Mockito.mock(ServeiCamp.class);
        when(serveiCampRepository.findByPath(campPath)).thenReturn(mockServeiCamp);

        List<Provincia> mockProvincies = new ArrayList<>();
        mockProvincies.add(Provincia.builder().codi("07").nom("Illes Balears").build());
        mockProvincies.add(Provincia.builder().codi("08").nom("Barcelona").build());
        when(dadesExternesService.findProvincies(any(IdiomaEnumDto.class))).thenReturn(mockProvincies);

        List<ValorEnum> result = recobrimentServiceImpl.getValorsEnumByServei(serveiCodi, campPath, enumCodi, null);

        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());
        Assert.assertEquals("07", result.get(0).getCodi());
        Assert.assertEquals("Illes Balears", result.get(0).getValor());
    }

    @Test
    public void testGetValorsEnumByServei_MunicipisEnumeration() throws Exception {
        String serveiCodi = "SERV001";
        String campPath = "DatosEspecificos/minicipi";
        String enumCodi = "MUNICIPI_3";
        String filtre = "07";

        ServeiConfig mockServeiConfig = Mockito.mock(ServeiConfig.class);
        when(serveiConfigRepository.findByServei(serveiCodi)).thenReturn(mockServeiConfig);

        ServeiCamp mockServeiCamp = Mockito.mock(ServeiCamp.class);
        when(serveiCampRepository.findByPath(campPath)).thenReturn(mockServeiCamp);

        List<Municipi> mockMunicipis = new ArrayList<>();
        mockMunicipis.add(Municipi.builder().codi("07033").nom("Manacor").build());
        mockMunicipis.add(Municipi.builder().codi("07040").nom("Palma de Mallorca").build());
        when(dadesExternesService.findMunicipisPerProvincia(anyString())).thenReturn(mockMunicipis);

        List<ValorEnum> result = recobrimentServiceImpl.getValorsEnumByServei(serveiCodi, campPath, enumCodi, filtre);

        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());
        Assert.assertEquals("033", result.get(0).getCodi());
        Assert.assertEquals("Manacor", result.get(0).getValor());

    }

    @Test(expected = RuntimeException.class)
    public void testGetValorsEnumByServei_MunicipisFiltreValueValidation() throws Exception {
        String serveiCodi = "SERV001";
        String campPath = "DatosEspecificos/minicipi";
        String enumCodi = "MUNICIPI_3";
        String filtre = "60";

        ServeiConfig mockServeiConfig = Mockito.mock(ServeiConfig.class);
        when(serveiConfigRepository.findByServei(serveiCodi)).thenReturn(mockServeiConfig);

        ServeiCamp mockServeiCamp = Mockito.mock(ServeiCamp.class);
        when(serveiCampRepository.findByPath(campPath)).thenReturn(mockServeiCamp);

        recobrimentServiceImpl.getValorsEnumByServei(serveiCodi, campPath, enumCodi, filtre);
    }

    @Test(expected = RuntimeException.class)
    public void testGetValorsEnumByServei_MunicipisFiltreValidation() throws Exception {
        String serveiCodi = "SERV001";
        String campPath = "DatosEspecificos/minicipi";
        String enumCodi = "MUNICIPI_3";
        String filtre = "";

        ServeiConfig mockServeiConfig = Mockito.mock(ServeiConfig.class);
        when(serveiConfigRepository.findByServei(serveiCodi)).thenReturn(mockServeiConfig);

        ServeiCamp mockServeiCamp = Mockito.mock(ServeiCamp.class);
        when(serveiCampRepository.findByPath(campPath)).thenReturn(mockServeiCamp);

        recobrimentServiceImpl.getValorsEnumByServei(serveiCodi, campPath, enumCodi, filtre);
    }

    // TESTS validatePeticio Sincrona
    // /////////////////////////////////////////////////////////

    @Test
    public void testValidatePeticioSincrona_Success() {
        // Mock valid PeticioSincrona object
        PeticioSincrona peticio = PeticioSincrona.builder().build();
        Map<String, List<String>> mockErrors = new HashMap<>(); // No errors

        // Mock behavior
        doNothing().when(recobrimentV2Helper).validateDadesComunes(any(DadesComunes.class), anyString(), any(BindException.class));
        doNothing().when(recobrimentV2Helper).validateDadesSolicitud(any(SolicitudSimple.class), anyString(), any(BindException.class), any(ServeiService.class));

        // Call the method
        Map<String, List<String>> result = recobrimentServiceImpl.validatePeticio("SERVEI_CODI", peticio);

        // Validate results
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testValidatePeticioSincrona_Errors() {
        // Mock invalid PeticioSincrona object
        PeticioSincrona peticio = PeticioSincrona.builder().build();

        // Mock validation errors
        doNothing().when(recobrimentV2Helper).validateDadesComunes(any(DadesComunes.class), anyString(), any(BindException.class));
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                BindException bindException = (BindException) invocation.getArguments()[2];
                bindException.addError(new FieldError("peticio", "field1", "Field1 is invalid"));
                return null;
            }
        }).when(recobrimentV2Helper).validateDadesSolicitud(any(SolicitudSimple.class), anyString(), any(BindException.class), any(ServeiService.class));

        // Call the method
        Map<String, List<String>> result = recobrimentServiceImpl.validatePeticio("SERVEI_CODI", peticio);

        // Verify results
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(1, result.size());
        Assert.assertTrue(result.containsKey("field1"));
        Assert.assertEquals("Field1 is invalid", result.get("field1").get(0));
    }


    // TESTS validatePeticio Asincrona
    // /////////////////////////////////////////////////////////

    @Test
    public void testValidatePeticioAsincrona_Success() {
        // Mock valid PeticioAsincrona object
        PeticioAsincrona peticio = PeticioAsincrona.builder().build();
        Map<String, List<String>> mockErrors = new HashMap<>(); // No errors

        // Mock behavior
        doNothing().when(recobrimentV2Helper).validateDadesComunes(any(DadesComunes.class), anyString(), any(BindException.class));
        when(recobrimentV2Helper.validateDadesSolicituds(Mockito.<List<SolicitudSimple>>any(), anyString(), any(ServeiService.class))).thenReturn(mockErrors);

        // Call the method
        Map<String, List<String>> result = recobrimentServiceImpl.validatePeticio("SERVEI_CODI", peticio);

        // Validate results
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
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
        doNothing().when(recobrimentV2Helper).validateDadesComunes(any(DadesComunes.class), anyString(), any(BindException.class));
        when(recobrimentV2Helper.validateDadesSolicituds(Mockito.<List<SolicitudSimple>>any(), anyString(), any(ServeiService.class))).thenReturn(mockErrors);

        // Call the method
        Map<String, List<String>> result = recobrimentServiceImpl.validatePeticio("SERVEI_CODI", peticio);

        // Verify results
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(1, result.size());
        Assert.assertTrue(result.containsKey("field2"));
        Assert.assertEquals("Field2 is invalid", result.get("field2").get(0));
    }


    // TESTS peticioSincrona
    // /////////////////////////////////////////////////////////

    @Test
    public void testPeticionSincrona_Success() throws ConsultaScspGeneracioException, ScspException {
        // Mocking
        PeticioSincrona mockPeticio = PeticioSincrona.builder().build();
        Respuesta mockRespuesta = new Respuesta();
        Atributos atributos = new Atributos();
        Estado estado = new Estado();
        estado.setCodigoEstado("00");
        estado.setLiteralError(null);
        atributos.setEstado(estado);
        mockRespuesta.setAtributos(atributos);

        when(recobrimentV2Helper.toPeticion(any(PeticioSincrona.class))).thenReturn(new Peticion());
        when(recobrimentHelper.peticionSincrona(any(Peticion.class))).thenReturn(mockRespuesta);
        when(recobrimentV2Helper.toRespostaSincrona(any(Respuesta.class))).thenCallRealMethod();
//        Mockito.when(recobrimentServiceImpl.toScspRespuesta(Mockito.any())).thenReturn(mockRespuesta);

        // Call the method
        PeticioRespostaSincrona response = recobrimentServiceImpl.peticionSincrona(mockPeticio);

        // Assertions
        Assert.assertNotNull(response);
        Assert.assertFalse(response.isError());
        Assert.assertNull(response.getMessageError());
//        Assert.assertNotNull(response.getResposta());
    }

    @Test
    public void testPeticionSincrona_ValidationError() throws ConsultaScspGeneracioException, ScspException {
        // Mocking
        PeticioSincrona mockPeticio = PeticioSincrona.builder().build();
        Respuesta mockRespuesta = new Respuesta();
        Atributos atributos = new Atributos();
        Estado estado = new Estado();
        estado.setCodigoEstado("01");
        estado.setLiteralError("Validation failed");
        atributos.setEstado(estado);
        mockRespuesta.setAtributos(atributos);

        when(recobrimentV2Helper.toPeticion(any(PeticioSincrona.class))).thenReturn(new Peticion());
        when(recobrimentHelper.peticionSincrona(any(Peticion.class))).thenReturn(mockRespuesta);
        when(recobrimentV2Helper.toRespostaSincrona(any(Respuesta.class))).thenCallRealMethod();
//        Mockito.when(recobrimentServiceImpl.toScspRespuesta(Mockito.any())).thenReturn(mockRespuesta);

        // Call the method
        PeticioRespostaSincrona response = recobrimentServiceImpl.peticionSincrona(mockPeticio);

        // Assertions
        Assert.assertNotNull(response);
        Assert.assertTrue(response.isError());
        Assert.assertEquals("Validation failed", response.getMessageError());
    }

    @Test
    public void testPeticionSincrona_ExceptionThrown() throws ConsultaScspGeneracioException {
        // Mocking
        PeticioSincrona mockPeticio = PeticioSincrona.builder().build();
        when(recobrimentV2Helper.toPeticion(any(PeticioSincrona.class))).thenThrow(new RuntimeException("Unexpected error"));

        // Call the method
        PeticioRespostaSincrona response = recobrimentServiceImpl.peticionSincrona(mockPeticio);

        // Assertions
        Assert.assertNotNull(response);
        Assert.assertTrue(response.isError());
        Assert.assertEquals("Unexpected error", response.getMessageError());
    }


    // TESTS peticioAsincrona
    // /////////////////////////////////////////////////////////

    @Test
    public void testPeticionAsincrona_Success() throws ConsultaScspGeneracioException, ScspException {
        // Mocking
        PeticioAsincrona mockPeticio = PeticioAsincrona.builder().build();
        ConfirmacionPeticion mockConfirmacion = new ConfirmacionPeticion();
        mockConfirmacion.setAtributos(new Atributos());
        when(recobrimentV2Helper.toPeticion(any(PeticioAsincrona.class))).thenReturn(new Peticion());
        when(recobrimentHelper.peticionAsincrona(any(Peticion.class))).thenReturn(mockConfirmacion);
        when(recobrimentV2Helper.toConfirmacio(any(ConfirmacionPeticion.class))).thenCallRealMethod();

        // Call the method
        PeticioConfirmacioAsincrona response = recobrimentServiceImpl.peticionAsincrona(mockPeticio);

        // Assertions
        Assert.assertNotNull(response);
        Assert.assertFalse(response.isError());
        Assert.assertNull(response.getMessageError());
        Assert.assertNotNull(response.getConfirmacioPeticio());
    }

    @Test
    public void testPeticionAsincrona_WithError() throws ConsultaScspGeneracioException, ScspException {
        // Mocking
        PeticioAsincrona mockPeticio = PeticioAsincrona.builder().build();
        ConfirmacionPeticion confirmacionPeticion = new ConfirmacionPeticion();
        Atributos atributos = new Atributos();
        Estado estado = new Estado();
        estado.setCodigoEstado("99");
        estado.setLiteralError("Error occurred");
        atributos.setEstado(estado);
        confirmacionPeticion.setAtributos(atributos);

        when(recobrimentV2Helper.toPeticion(any(PeticioAsincrona.class))).thenReturn(new Peticion());
        when(recobrimentHelper.peticionAsincrona(any(Peticion.class))).thenReturn(confirmacionPeticion);
        when(recobrimentV2Helper.toConfirmacio(any(ConfirmacionPeticion.class))).thenCallRealMethod();

        // Call the method
        PeticioConfirmacioAsincrona response = recobrimentServiceImpl.peticionAsincrona(mockPeticio);

        // Assertions
        Assert.assertNotNull(response);
        Assert.assertTrue(response.isError());
        Assert.assertEquals("Error occurred", response.getMessageError());
    }

    @Test
    public void testPeticionAsincrona_ExceptionThrown() throws ConsultaScspGeneracioException {
        // Mocking
        PeticioAsincrona mockPeticio = PeticioAsincrona.builder().dadesComunes(DadesComunes.builder().serveiCodi("Servei01").build()).build();
        when(recobrimentV2Helper.toPeticion(any(PeticioAsincrona.class))).thenThrow(new RuntimeException("Unexpected exception"));

        // Call the method
        PeticioConfirmacioAsincrona response = recobrimentServiceImpl.peticionAsincrona(mockPeticio);

        // Assertions
        Assert.assertNotNull(response);
        Assert.assertTrue(response.isError());
        Assert.assertEquals("Unexpected exception", response.getMessageError());
    }


    // TESTS getResposta
    // /////////////////////////////////////////////////////////

    @Test
    public void testGetResposta_Success() throws RecobrimentScspException, ConsultaNotFoundException, ScspException {
        // Mocking
        String idPeticion = "12345";
        Respuesta mockRespuesta = new Respuesta();
        Atributos atributos = new Atributos();
        Estado estado = new Estado();
        estado.setCodigoEstado("003");
        atributos.setEstado(estado);
        mockRespuesta.setAtributos(atributos);
        when(recobrimentHelper.getRespuesta(anyString())).thenReturn(mockRespuesta);
        when(consultaRepository.findByScspPeticionId(idPeticion)).thenReturn(new Consulta());
        when(recobrimentV2Helper.toRespostaAsincrona(any(Respuesta.class))).thenCallRealMethod();

        // Call the method
        PeticioRespostaAsincrona response = recobrimentServiceImpl.getResposta(idPeticion);

        // Assertions
        Assert.assertFalse("S'ha produït un error", response.isError());
        Assert.assertNotNull(response);
        Mockito.verify(recobrimentHelper, Mockito.times(1)).getRespuesta(Mockito.eq(idPeticion));
    }

    @Test
    public void testGetResposta_ConsultaNotFound() throws ScspException {
        // Mocking
        String idPeticion = "12345";
        when(consultaRepository.findByScspPeticionId(idPeticion)).thenReturn(null);

        // Call the method
        try {
            recobrimentServiceImpl.getResposta(idPeticion);
            Assert.fail("Expected ConsultaNotFoundException was not thrown");
        } catch (ConsultaNotFoundException e) {
            // Expected exception
            Assert.assertNotNull(e);
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown: " + e.getMessage());
        }

        // Verify helper is not called
        Mockito.verify(recobrimentHelper, Mockito.never()).getRespuesta(anyString());
    }

    @Test
    public void testGetResposta_ThrowsException() throws ScspException {
        // Mocking
        String idPeticion = "12345";
        when(consultaRepository.findByScspPeticionId(idPeticion)).thenReturn(new Consulta());
        when(recobrimentHelper.getRespuesta(anyString())).thenThrow(new ScspException("Unexpected error", "error code"));

        // Call the method
        try {
            recobrimentServiceImpl.getResposta(idPeticion);
            Assert.fail("Expected RuntimeException was not thrown");
        } catch (RecobrimentScspException e) {
            Assert.assertTrue(e.getMessage().contains("Unexpected error"));
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown: " + e.getMessage());
        }

        // Verify helper is called once
        Mockito.verify(recobrimentHelper, Mockito.times(1)).getRespuesta(Mockito.eq(idPeticion));
    }


    // TESTS getJustificant
    // /////////////////////////////////////////////////////////

    @Test
    public void testGetJustificant_Success() throws RecobrimentScspException, ConsultaNotFoundException, ScspException {
        // Mocking
        String idPeticion = "12345";
        String idSolicitud = "54321";
        JustificantDto mockJustificantDto = Mockito.mock(JustificantDto.class);
        when(mockJustificantDto.getNom()).thenReturn("sampleName");
        when(mockJustificantDto.getContentType()).thenReturn("application/pdf");
        when(mockJustificantDto.getContingut()).thenReturn(new byte[]{1, 2, 3});
        when(consultaRepository.findByScspPeticionIdAndScspSolicitudId(idPeticion, idSolicitud)).thenReturn(new Consulta());
        when(recobrimentHelper.getJustificante(idPeticion, idSolicitud, false, true)).thenReturn(mockJustificantDto);

        // Call the method
        ScspJustificante result = recobrimentServiceImpl.getJustificant(idPeticion, idSolicitud);

        // Assertions
        Assert.assertNotNull(result);
        Assert.assertEquals("sampleName", result.getNom());
        Assert.assertEquals("application/pdf", result.getContentType());
        Assert.assertArrayEquals(new byte[]{1, 2, 3}, result.getContingut());
        Mockito.verify(recobrimentHelper, Mockito.times(1)).getJustificante(idPeticion, idSolicitud, false, true);
    }

    @Test
    public void testGetJustificant_ConsultaNotFound() {
        // Mocking
        String idPeticion = "12345";
        String idSolicitud = "54321";
        when(consultaRepository.findByScspPeticionIdAndScspSolicitudId(idPeticion, idSolicitud)).thenReturn(null);

        // Call the method
        try {
            recobrimentServiceImpl.getJustificant(idPeticion, idSolicitud);
            Assert.fail("Expected ConsultaNotFoundException was not thrown");
        } catch (ConsultaNotFoundException e) {
            // Expected Exception
            Assert.assertNotNull(e);
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testGetJustificant_ThrowsException() throws ScspException {
        // Mocking
        String idPeticion = "12345";
        String idSolicitud = "54321";
        when(consultaRepository.findByScspPeticionIdAndScspSolicitudId(idPeticion, idSolicitud)).thenReturn(new Consulta());
        when(recobrimentHelper.getJustificante(anyString(), anyString(), Mockito.anyBoolean(), Mockito.anyBoolean())).thenThrow(new ScspException("Unexpected Error", "error code"));

        // Call the method
        try {
            recobrimentServiceImpl.getJustificant(idPeticion, idSolicitud);
            Assert.fail("Expected RecobrimentScspException was not thrown");
        } catch (RecobrimentScspException e) {
            Assert.assertTrue(e.getMessage().contains("Unexpected Error"));
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown: " + e.getMessage());
        }
    }


    // TESTS getJustificantImprimible
    // /////////////////////////////////////////////////////////

    @Test
    public void testGetJustificantImprimible_Success() throws RecobrimentScspException, ConsultaNotFoundException, ScspException {
        // Mocking
        String idPeticion = "12345";
        String idSolicitud = "54321";
        JustificantDto mockJustificantDto = Mockito.mock(JustificantDto.class);
        when(mockJustificantDto.getNom()).thenReturn("imprimibleName");
        when(mockJustificantDto.getContentType()).thenReturn("application/pdf");
        when(mockJustificantDto.getContingut()).thenReturn(new byte[]{4, 5, 6});
        when(consultaRepository.findByScspPeticionIdAndScspSolicitudId(idPeticion, idSolicitud)).thenReturn(new Consulta());
        when(recobrimentHelper.getJustificante(idPeticion, idSolicitud, true, true)).thenReturn(mockJustificantDto);

        // Call the method
        ScspJustificante result = recobrimentServiceImpl.getJustificantImprimible(idPeticion, idSolicitud);

        // Assertions
        Assert.assertNotNull(result);
        Assert.assertEquals("imprimibleName", result.getNom());
        Assert.assertEquals("application/pdf", result.getContentType());
        Assert.assertArrayEquals(new byte[]{4, 5, 6}, result.getContingut());
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
            Assert.fail("Expected ConsultaNotFoundException was not thrown");
        } catch (ConsultaNotFoundException e) {
            // Expected Exception
            Assert.assertNotNull(e);
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testGetJustificantImprimible_ThrowsException() throws ScspException {
        // Mocking
        String idPeticion = "12345";
        String idSolicitud = "54321";
        when(consultaRepository.findByScspPeticionIdAndScspSolicitudId(idPeticion, idSolicitud)).thenReturn(new Consulta());
        when(recobrimentHelper.getJustificante(anyString(), anyString(), Mockito.eq(true), Mockito.eq(true)))
                .thenThrow(new ScspException("Unexpected Error Imprimible", "error code"));

        // Call the method
        try {
            recobrimentServiceImpl.getJustificantImprimible(idPeticion, idSolicitud);
            Assert.fail("Expected RecobrimentScspException was not thrown");
        } catch (RecobrimentScspException e) {
            Assert.assertTrue(e.getMessage().contains("Unexpected Error Imprimible"));
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown: " + e.getMessage());
        }
    }


    // TESTS getJustificantCsv
    // /////////////////////////////////////////////////////////

    @Test
    public void testGetJustificantCsv_Success() throws RecobrimentScspException, ConsultaNotFoundException, ScspException {
        // Mocking
        String idPeticion = "12345";
        String idSolicitud = "54321";
        JustificantDto mockJustificantDto = Mockito.mock(JustificantDto.class);
        when(mockJustificantDto.getArxiuCsv()).thenReturn("CSV_CODE");
        when(consultaRepository.findByScspPeticionIdAndScspSolicitudId(idPeticion, idSolicitud)).thenReturn(new Consulta());
        when(recobrimentHelper.getJustificante(idPeticion, idSolicitud, true, false)).thenReturn(mockJustificantDto);

        // Call the method
        String result = recobrimentServiceImpl.getJustificantCsv(idPeticion, idSolicitud);

        // Assertions
        Assert.assertNotNull(result);
        Assert.assertEquals("CSV_CODE", result);
        Mockito.verify(recobrimentHelper, Mockito.times(1)).getJustificante(idPeticion, idSolicitud, true, false);
    }

    @Test
    public void testGetJustificantCsv_ConsultaNotFound() {
        // Mocking
        String idPeticion = "12345";
        String idSolicitud = "54321";
        when(consultaRepository.findByScspPeticionIdAndScspSolicitudId(idPeticion, idSolicitud)).thenReturn(null);

        // Call the method
        try {
            recobrimentServiceImpl.getJustificant(idPeticion, idSolicitud);
            Assert.fail("Expected ConsultaNotFoundException was not thrown");
        } catch (ConsultaNotFoundException e) {
            // Expected Exception
            Assert.assertNotNull(e);
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testGetJustificantCsv_ThrowsException() throws ScspException {
        // Mocking
        String idPeticion = "12345";
        String idSolicitud = "54321";
        when(consultaRepository.findByScspPeticionIdAndScspSolicitudId(idPeticion, idSolicitud)).thenReturn(new Consulta());
        when(recobrimentHelper.getJustificante(anyString(), anyString(), Mockito.anyBoolean(), Mockito.anyBoolean())).thenThrow(new ScspException("Unexpected Error", "error code"));

        // Call the method
        try {
            recobrimentServiceImpl.getJustificant(idPeticion, idSolicitud);
            Assert.fail("Expected RecobrimentScspException was not thrown");
        } catch (RecobrimentScspException e) {
            Assert.assertTrue(e.getMessage().contains("Unexpected Error"));
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown: " + e.getMessage());
        }
    }


    // TESTS getJustificantUuid
    // /////////////////////////////////////////////////////////

    @Test
    public void testGetJustificantUuid_Success() throws RecobrimentScspException, ConsultaNotFoundException, ScspException {
        // Mocking
        String idPeticion = "12345";
        String idSolicitud = "54321";
        JustificantDto mockJustificantDto = Mockito.mock(JustificantDto.class);
        when(mockJustificantDto.getArxiuUuid()).thenReturn("UUID_CODE");
        when(consultaRepository.findByScspPeticionIdAndScspSolicitudId(idPeticion, idSolicitud)).thenReturn(new Consulta());
        when(recobrimentHelper.getJustificante(idPeticion, idSolicitud, true, false)).thenReturn(mockJustificantDto);

        // Call the method
        String result = recobrimentServiceImpl.getJustificantUuid(idPeticion, idSolicitud);

        // Assertions
        Assert.assertNotNull(result);
        Assert.assertEquals("UUID_CODE", result);
        Mockito.verify(recobrimentHelper, Mockito.times(1)).getJustificante(idPeticion, idSolicitud, true, false);
    }

    @Test
    public void testGetJustificantUuid_ConsultaNotFound() {
        // Mocking
        String idPeticion = "12345";
        String idSolicitud = "54321";
        when(consultaRepository.findByScspPeticionIdAndScspSolicitudId(idPeticion, idSolicitud)).thenReturn(null);

        // Call the method
        try {
            recobrimentServiceImpl.getJustificant(idPeticion, idSolicitud);
            Assert.fail("Expected ConsultaNotFoundException was not thrown");
        } catch (ConsultaNotFoundException e) {
            // Expected Exception
            Assert.assertNotNull(e);
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testGetJustificantUuid_ThrowsException() throws ScspException {
        // Mocking
        String idPeticion = "12345";
        String idSolicitud = "54321";
        when(consultaRepository.findByScspPeticionIdAndScspSolicitudId(idPeticion, idSolicitud)).thenReturn(new Consulta());
        when(recobrimentHelper.getJustificante(anyString(), anyString(), Mockito.anyBoolean(), Mockito.anyBoolean())).thenThrow(new ScspException("Unexpected Error", "error code"));

        // Call the method
        try {
            recobrimentServiceImpl.getJustificant(idPeticion, idSolicitud);
            Assert.fail("Expected RecobrimentScspException was not thrown");
        } catch (RecobrimentScspException e) {
            Assert.assertTrue(e.getMessage().contains("Unexpected Error"));
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown: " + e.getMessage());
        }
    }

}