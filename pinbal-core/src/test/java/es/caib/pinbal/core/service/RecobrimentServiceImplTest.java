package es.caib.pinbal.core.service;

import es.caib.pinbal.client.procediments.Procediment;
import es.caib.pinbal.client.recobriment.v2.DadaEspecifica;
import es.caib.pinbal.client.recobriment.v2.ValorEnum;
import es.caib.pinbal.client.serveis.Servei;
import es.caib.pinbal.core.dto.dadesexternes.Municipi;
import es.caib.pinbal.core.dto.dadesexternes.Pais;
import es.caib.pinbal.core.dto.dadesexternes.Provincia;
import es.caib.pinbal.core.model.Entitat;
import es.caib.pinbal.core.model.OrganGestor;
import es.caib.pinbal.core.model.ServeiCamp;
import es.caib.pinbal.core.model.ServeiConfig;
import es.caib.pinbal.core.repository.EntitatRepository;
import es.caib.pinbal.core.repository.ProcedimentRepository;
import es.caib.pinbal.core.repository.ServeiCampRepository;
import es.caib.pinbal.core.repository.ServeiConfigRepository;
import es.caib.pinbal.core.repository.ServeiRepository;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.core.service.exception.ServeiCampNotFoundException;
import es.caib.pinbal.core.service.exception.ServeiNotFoundException;
import es.caib.pinbal.scsp.ScspHelper;
import es.caib.pinbal.scsp.XmlHelper;
import es.caib.pinbal.scsp.tree.Node;
import es.caib.pinbal.scsp.tree.Tree;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private ScspHelper scspHelper;

    @Mock
    private DadesExternesService dadesExternesService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
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
        Mockito.when(entitatRepository.findByCodi(entitatCodi)).thenReturn(mockEntitat);
        Mockito.when(procedimentRepository.findByEntitatOrderByNomAsc(mockEntitat)).thenReturn(mockProcediments);

        // Call the method
        List<Procediment> procediments = recobrimentServiceImpl.getProcediments(entitatCodi);

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
        Mockito.when(entitatRepository.findByCodi(entitatCodi)).thenReturn(null);

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
        Mockito.when(entitatRepository.findByCodi(entitatCodi)).thenReturn(mockEntitat);
        Mockito.when(procedimentRepository.findByEntitatOrderByNomAsc(mockEntitat)).thenReturn(new ArrayList<es.caib.pinbal.core.model.Procediment>());

        // Call the method
        List<Procediment> procediments = recobrimentServiceImpl.getProcediments(entitatCodi);

        // Verify results
        Assert.assertNotNull(procediments);
        Assert.assertTrue(procediments.isEmpty());
    }

    // TESTS getServeis
    // /////////////////////////////////////////////////////////

    @Test
    public void testGetServeis_Success() {
        // Mock data
        Servei servei1 = new Servei("S001", "Servei 1", true);
        Servei servei2 = new Servei("S002", "Servei 2", false);
        List<Servei> mockServeis = new ArrayList<>();
        mockServeis.add(servei1);
        mockServeis.add(servei2);

        // Mock behavior
        Mockito.when(serveiRepository.findAllServeisClient()).thenReturn(mockServeis);

        // Call the method
        List<Servei> serveis = recobrimentServiceImpl.getServeis();

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
        Mockito.when(serveiRepository.findAllServeisClient()).thenReturn(new ArrayList<Servei>());

        // Call the method
        List<Servei> serveis = recobrimentServiceImpl.getServeis();

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
        Servei servei1 = new Servei("SERV001", "Servei 1", true);
        Servei servei2 = new Servei("SERV002", "Servei 2", false);
        List<Servei> mockServeis = new ArrayList<>();
        mockServeis.add(servei1);
        mockServeis.add(servei2);

        // Mock behavior
        Mockito.when(entitatRepository.findByCodi(entitatCodi)).thenReturn(new Entitat());
        Mockito.when(serveiRepository.findServeisClientByEntitatCodi(entitatCodi)).thenReturn(mockServeis);

        // Call the method
        List<Servei> serveis = recobrimentServiceImpl.getServeisByEntitat(entitatCodi);

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
        Mockito.when(entitatRepository.findByCodi(entitatCodi)).thenReturn(new Entitat());
        Mockito.when(serveiRepository.findServeisClientByEntitatCodi(entitatCodi)).thenReturn(new ArrayList<Servei>());

        // Call the method
        List<Servei> serveis = recobrimentServiceImpl.getServeisByEntitat(entitatCodi);

        // Verify results
        Assert.assertNotNull(serveis);
        Assert.assertTrue(serveis.isEmpty());
    }

    @Test(expected = EntitatNotFoundException.class)
    public void testGetServeisByEntitat_EntitatNotFoundException() throws EntitatNotFoundException {
        // Mock data
        String entitatCodi = "ENT003";

        // Mock behavior
        Mockito.when(entitatRepository.findByCodi(entitatCodi)).thenReturn(null);

        // Call the method
        recobrimentServiceImpl.getServeisByEntitat(entitatCodi);
    }

    // TESTS getServeisByProcediment
    // /////////////////////////////////////////////////////////

    @Test
    public void testGetServeisByProcediment_Success() throws ProcedimentNotFoundException {
        // Mock data
        String procedimentCodi = "PROC001";
        Servei servei1 = new Servei("SERV001", "Servei 1", true);
        Servei servei2 = new Servei("SERV002", "Servei 2", false);
        List<Servei> mockServeis = new ArrayList<>();
        mockServeis.add(servei1);
        mockServeis.add(servei2);

        // Mock behavior
        Mockito.when(procedimentRepository.findByCodi(procedimentCodi)).thenReturn(new es.caib.pinbal.core.model.Procediment());
        Mockito.when(serveiRepository.findServeisClientByProcedimentCodi(procedimentCodi)).thenReturn(mockServeis);

        // Call method
        List<Servei> serveis = recobrimentServiceImpl.getServeisByProcediment(procedimentCodi);

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
        Mockito.when(procedimentRepository.findByCodi(procedimentCodi)).thenReturn(new es.caib.pinbal.core.model.Procediment());
        Mockito.when(serveiRepository.findServeisClientByProcedimentCodi(procedimentCodi)).thenReturn(new ArrayList<Servei>());

        // Call method
        List<Servei> serveis = recobrimentServiceImpl.getServeisByProcediment(procedimentCodi);

        // Verify results
        Assert.assertNotNull(serveis);
        Assert.assertTrue(serveis.isEmpty());
    }

    @Test(expected = ProcedimentNotFoundException.class)
    public void testGetServeisByProcediment_ProcedimentNotFoundException() throws ProcedimentNotFoundException {
        // Mock data
        String procedimentCodi = "PROC003";

        // Mock behavior
        Mockito.when(procedimentRepository.findByCodi(procedimentCodi)).thenReturn(null);

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
        Mockito.when(serveiConfigRepository.findByServei(serveiCodi)).thenReturn(mockServeiConfig);
        Mockito.when(serveiCampRepository.findByServeiOrderByGrupOrdreAsc(serveiCodi)).thenReturn(mockServeiCamps);

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
        Mockito.when(serveiConfigRepository.findByServei(serveiCodi)).thenReturn(mockServeiConfig);
        Mockito.when(serveiCampRepository.findByServeiOrderByGrupOrdreAsc(serveiCodi)).thenReturn(new ArrayList<ServeiCamp>());

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
        Mockito.when(serveiConfigRepository.findByServei(serveiCodi)).thenReturn(null);

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
        Mockito.when(serveiConfigRepository.findByServei(serveiCodi)).thenReturn(mockServeiConfig);

        ServeiCamp mockServeiCamp = Mockito.mock(ServeiCamp.class);
        Mockito.when(serveiCampRepository.findByPath(campCodi)).thenReturn(mockServeiCamp);

        Tree<XmlHelper.DadesEspecifiquesNode> arbre = getDadesEspecifiquesNodeTree();
        Mockito.when(scspHelper.generarArbreDadesEspecifiques(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(arbre);
        Mockito.when(mockServeiCamp.getEnumDescripcions()).thenReturn(new String[] {"Etiqueta A", "Etiqueta B", "Etiqueta C"});

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
        Mockito.when(serveiConfigRepository.findByServei(serveiCodi)).thenReturn(mockServeiConfig);

        ServeiCamp mockServeiCamp = Mockito.mock(ServeiCamp.class);
        Mockito.when(serveiCampRepository.findByPath(campCodi)).thenReturn(mockServeiCamp);

        Tree<XmlHelper.DadesEspecifiquesNode> arbre = getDadesEspecifiquesNodeTree();
        Mockito.when(scspHelper.generarArbreDadesEspecifiques(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(arbre);
        Mockito.when(mockServeiCamp.getEnumDescripcions()).thenReturn(new String[] {"Etiqueta A", "Etiqueta B", "Etiqueta C"});

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

        Mockito.when(serveiConfigRepository.findByServei(serveiCodi)).thenReturn(null);

        recobrimentServiceImpl.getValorsEnumByServei(serveiCodi, "CAMP001", "ENUM1", "Test");
    }

    @Test(expected = ServeiCampNotFoundException.class)
    public void testGetValorsEnumByServei_CampNotFound() throws Exception {
        String serveiCodi = "SERV001";
        String campCodi = "INVALID";

        ServeiConfig mockServeiConfig = Mockito.mock(ServeiConfig.class);
        Mockito.when(serveiConfigRepository.findByServei(serveiCodi)).thenReturn(mockServeiConfig);

        Mockito.when(serveiCampRepository.findByPath(campCodi)).thenReturn(null);

        recobrimentServiceImpl.getValorsEnumByServei(serveiCodi, campCodi, "ENUM1", "Test");
    }

    @Test
    public void testGetValorsEnumByServei_PaisEnumeration() throws Exception {
        String serveiCodi = "SERV001";
        String campPath = "DatosEspecificos/pais";
        String enumCodi = "PAIS";

        ServeiConfig mockServeiConfig = Mockito.mock(ServeiConfig.class);
        Mockito.when(serveiConfigRepository.findByServei(serveiCodi)).thenReturn(mockServeiConfig);

        ServeiCamp mockServeiCamp = Mockito.mock(ServeiCamp.class);
        Mockito.when(serveiCampRepository.findByPath(campPath)).thenReturn(mockServeiCamp);

        List<Pais> mockPaisos = new ArrayList<>();
        mockPaisos.add(Pais.builder().codi_numeric("01").nom("Andorra").alpha2("AN").build());
        mockPaisos.add(Pais.builder().codi_numeric("73").nom("España").alpha2("ES").build());
        Mockito.when(dadesExternesService.findPaisos()).thenReturn(mockPaisos);

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
        Mockito.when(serveiConfigRepository.findByServei(serveiCodi)).thenReturn(mockServeiConfig);

        ServeiCamp mockServeiCamp = Mockito.mock(ServeiCamp.class);
        Mockito.when(serveiCampRepository.findByPath(campPath)).thenReturn(mockServeiCamp);

        List<Provincia> mockProvincies = new ArrayList<>();
        mockProvincies.add(Provincia.builder().codi("07").nom("Illes Balears").build());
        mockProvincies.add(Provincia.builder().codi("08").nom("Barcelona").build());
        Mockito.when(dadesExternesService.findProvincies()).thenReturn(mockProvincies);

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
        Mockito.when(serveiConfigRepository.findByServei(serveiCodi)).thenReturn(mockServeiConfig);

        ServeiCamp mockServeiCamp = Mockito.mock(ServeiCamp.class);
        Mockito.when(serveiCampRepository.findByPath(campPath)).thenReturn(mockServeiCamp);

        List<Municipi> mockMunicipis = new ArrayList<>();
        mockMunicipis.add(Municipi.builder().codi("030").nom("Manacor").build());
        mockMunicipis.add(Municipi.builder().codi("040").nom("Palma de Mallorca").build());
        Mockito.when(dadesExternesService.findMunicipisPerProvincia(Mockito.anyString())).thenReturn(mockMunicipis);

        List<ValorEnum> result = recobrimentServiceImpl.getValorsEnumByServei(serveiCodi, campPath, enumCodi, filtre);

        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());
        Assert.assertEquals("030", result.get(0).getCodi());
        Assert.assertEquals("Manacor", result.get(0).getValor());

    }

    @Test(expected = RuntimeException.class)
    public void testGetValorsEnumByServei_MunicipisFiltreValueValidation() throws Exception {
        String serveiCodi = "SERV001";
        String campPath = "DatosEspecificos/minicipi";
        String enumCodi = "MUNICIPI_3";
        String filtre = "60";

        ServeiConfig mockServeiConfig = Mockito.mock(ServeiConfig.class);
        Mockito.when(serveiConfigRepository.findByServei(serveiCodi)).thenReturn(mockServeiConfig);

        ServeiCamp mockServeiCamp = Mockito.mock(ServeiCamp.class);
        Mockito.when(serveiCampRepository.findByPath(campPath)).thenReturn(mockServeiCamp);

        recobrimentServiceImpl.getValorsEnumByServei(serveiCodi, campPath, enumCodi, filtre);
    }

    @Test(expected = RuntimeException.class)
    public void testGetValorsEnumByServei_MunicipisFiltreValidation() throws Exception {
        String serveiCodi = "SERV001";
        String campPath = "DatosEspecificos/minicipi";
        String enumCodi = "MUNICIPI_3";
        String filtre = "";

        ServeiConfig mockServeiConfig = Mockito.mock(ServeiConfig.class);
        Mockito.when(serveiConfigRepository.findByServei(serveiCodi)).thenReturn(mockServeiConfig);

        ServeiCamp mockServeiCamp = Mockito.mock(ServeiCamp.class);
        Mockito.when(serveiCampRepository.findByPath(campPath)).thenReturn(mockServeiCamp);

        recobrimentServiceImpl.getValorsEnumByServei(serveiCodi, campPath, enumCodi, filtre);
    }
}