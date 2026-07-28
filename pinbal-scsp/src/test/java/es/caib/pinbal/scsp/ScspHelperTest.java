package es.caib.pinbal.scsp;

import es.scsp.common.dao.ClavePrivadaDao;
import es.scsp.common.dao.ClavePublicaDao;
import es.scsp.common.dao.EmisorCertificadoDao;
import es.scsp.common.dao.OrganismoCesionarioDao;
import es.scsp.common.dao.ParametroConfiguracionDao;
import es.scsp.common.dao.PeticionRespuestaDao;
import es.scsp.common.dao.PinbalDao;
import es.scsp.common.dao.ServicioDao;
import es.scsp.common.dao.ServicioOrganismoCesionarioDao;
import es.scsp.bean.common.peticion.Emisor;
import es.scsp.common.domain.core.ClavePrivada;
import es.scsp.common.domain.core.ClavePublica;
import es.scsp.common.domain.core.EmisorCertificado;
import es.scsp.common.domain.core.OrganismoCesionario;
import es.scsp.common.domain.core.ParametroConfiguracion;
import es.scsp.common.domain.core.PeticionRespuesta;
import es.scsp.common.domain.core.Servicio;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * ScspHelper resol totes les seves dependències (DAOs, ClienteUnico) via
 * {@code applicationContext.getBean(...)}, la qual cosa permet testejar la seva lògica sense cap
 * sessió Hibernate real: només cal simular l'ApplicationContext amb Mockito i que retorni DAOs
 * igualment simulats.
 */
public class ScspHelperTest {

    private ApplicationContext applicationContext;
    private ScspHelper scspHelper;

    private ServicioDao servicioDao;
    private EmisorCertificadoDao emisorCertificadoDao;
    private ClavePublicaDao clavePublicaDao;
    private ClavePrivadaDao clavePrivadaDao;
    private PeticionRespuestaDao peticionRespuestaDao;
    private OrganismoCesionarioDao organismoCesionarioDao;
    private ServicioOrganismoCesionarioDao servicioOrganismoCesionarioDao;
    private PinbalDao pinbalDao;
    private ParametroConfiguracionDao parametroConfiguracionDao;

    @Before
    public void configurar() {
        applicationContext = mock(ApplicationContext.class);
        MessageSource messageSource = mock(MessageSource.class);
        scspHelper = new ScspHelper(applicationContext, messageSource);

        servicioDao = mock(ServicioDao.class);
        emisorCertificadoDao = mock(EmisorCertificadoDao.class);
        clavePublicaDao = mock(ClavePublicaDao.class);
        clavePrivadaDao = mock(ClavePrivadaDao.class);
        peticionRespuestaDao = mock(PeticionRespuestaDao.class);
        organismoCesionarioDao = mock(OrganismoCesionarioDao.class);
        servicioOrganismoCesionarioDao = mock(ServicioOrganismoCesionarioDao.class);
        pinbalDao = mock(PinbalDao.class);
        parametroConfiguracionDao = mock(ParametroConfiguracionDao.class);

        when(applicationContext.getBean("servicioDao")).thenReturn(servicioDao);
        when(applicationContext.getBean("emisorCertificadoDao")).thenReturn(emisorCertificadoDao);
        when(applicationContext.getBean("clavePublicaDao")).thenReturn(clavePublicaDao);
        when(applicationContext.getBean("clavePrivadaDao")).thenReturn(clavePrivadaDao);
        when(applicationContext.getBean("peticionRespuestaDao")).thenReturn(peticionRespuestaDao);
        when(applicationContext.getBean("organismoCesionarioDao")).thenReturn(organismoCesionarioDao);
        when(applicationContext.getBean("servicioOrganismoCesionarioDao")).thenReturn(servicioOrganismoCesionarioDao);
        when(applicationContext.getBean("pinbalDao")).thenReturn(pinbalDao);
        when(applicationContext.getBean("parametroConfiguracionDao")).thenReturn(parametroConfiguracionDao);
    }

    @SuppressWarnings("unchecked")
    private <T> T invoke(String methodName, Class<?>[] paramTypes, Object... args) throws Exception {
        Method method = ScspHelper.class.getDeclaredMethod(methodName, paramTypes);
        method.setAccessible(true);
        return (T) method.invoke(scspHelper, args);
    }

    private Servicio servicio(String codi, String descripcio) {
        Servicio servicio = new Servicio();
        servicio.setCodCertificado(codi);
        servicio.setDescripcion(descripcio);
        return servicio;
    }

    // ------------------------- consultes simples -------------------------

    @Test
    public void getServicioDescripcionAmbServeiExistent() {
        when(servicioDao.select("SERV1")).thenReturn(servicio("SERV1", "Descripció servei 1"));
        assertEquals("Descripció servei 1", scspHelper.getServicioDescripcion("SERV1"));
    }

    @Test
    public void getServicioDescripcionAmbServeiInexistentRetornaNull() {
        when(servicioDao.select("NO-EXISTEIX")).thenReturn(null);
        assertNull(scspHelper.getServicioDescripcion("NO-EXISTEIX"));
    }

    @Test
    public void getEmisorNombreAmbEmissorExistent() {
        EmisorCertificado emisor = new EmisorCertificado();
        emisor.setNombre("Emissor 1");
        when(emisorCertificadoDao.selectByCif("B00000000")).thenReturn(emisor);

        assertEquals("Emissor 1", scspHelper.getEmisorNombre("B00000000"));
    }

    @Test
    public void getEmisorNombreAmbEmissorInexistentRetornaNull() {
        when(emisorCertificadoDao.selectByCif("NO-EXISTEIX")).thenReturn(null);
        assertNull(scspHelper.getEmisorNombre("NO-EXISTEIX"));
    }

    @Test
    public void getClavePrivadaNombreIGetClavePrivadaNumeroSerie() {
        ClavePrivada clave = new ClavePrivada();
        clave.setNombre("Clau privada 1");
        clave.setNumeroSerie("SERIE-1");
        when(clavePrivadaDao.selectByAlias("alias1")).thenReturn(clave);

        assertEquals("Clau privada 1", scspHelper.getClavePrivadaNombre("alias1"));
        assertEquals("SERIE-1", scspHelper.getClavePrivadaNumeroSerie("alias1"));
    }

    @Test
    public void getClavePrivadaNombreAmbClauInexistentRetornaNull() {
        when(clavePrivadaDao.selectByAlias("no-existeix")).thenReturn(null);
        assertNull(scspHelper.getClavePrivadaNombre("no-existeix"));
        assertNull(scspHelper.getClavePrivadaNumeroSerie("no-existeix"));
    }

    @Test
    public void getClavePublicaNombreIGetClavePublicaNumeroSerie() {
        ClavePublica clave = new ClavePublica();
        clave.setNombre("Clau pública 1");
        clave.setNumeroSerie("SERIE-2");
        when(clavePublicaDao.selectByAlias("alias2")).thenReturn(clave);

        assertEquals("Clau pública 1", scspHelper.getClavePublicaNombre("alias2"));
        assertEquals("SERIE-2", scspHelper.getClavePublicaNumeroSerie("alias2"));
    }

    @Test
    public void getClavePublicaNombreAmbClauInexistentRetornaNull() {
        when(clavePublicaDao.selectByAlias("no-existeix")).thenReturn(null);
        assertNull(scspHelper.getClavePublicaNombre("no-existeix"));
        assertNull(scspHelper.getClavePublicaNumeroSerie("no-existeix"));
    }

    @Test
    public void getTerPeticionAmbPeticioExistent() throws Exception {
        PeticionRespuesta pr = new PeticionRespuesta();
        Date ter = new Date();
        pr.setTer(ter);
        when(peticionRespuestaDao.select("PET1")).thenReturn(pr);

        assertEquals(ter, scspHelper.getTerPeticion("PET1"));
    }

    @Test
    public void getTerPeticionAmbPeticioInexistentRetornaNull() throws Exception {
        when(peticionRespuestaDao.select("NO-EXISTEIX")).thenReturn(null);
        assertNull(scspHelper.getTerPeticion("NO-EXISTEIX"));
    }

    @Test
    public void servicioHasConsultesAmbConsultesRetornaCert() {
        Servicio servei = servicio("SERV1", "d");
        when(servicioDao.select("SERV1")).thenReturn(servei);
        when(peticionRespuestaDao.count(servei)).thenReturn(3L);

        assertTrue(scspHelper.servicioHasConsultes("SERV1"));
    }

    @Test
    public void servicioHasConsultesSenseConsultesRetornaFals() {
        Servicio servei = servicio("SERV1", "d");
        when(servicioDao.select("SERV1")).thenReturn(servei);
        when(peticionRespuestaDao.count(servei)).thenReturn(0L);

        assertFalse(scspHelper.servicioHasConsultes("SERV1"));
    }

    // ------------------------- llistes -------------------------

    @Test
    public void findServicioAllRetornaLaLlistaOrdenadaPerCodi() {
        List<Servicio> servicios = new ArrayList<>();
        servicios.add(servicio("B", "servei B"));
        servicios.add(servicio("A", "servei A"));
        when(servicioDao.select()).thenReturn(servicios);

        List<Servicio> resultat = scspHelper.findServicioAll();

        assertEquals("A", resultat.get(0).getCodCertificado());
        assertEquals("B", resultat.get(1).getCodCertificado());
    }

    @Test
    public void findServicioByCodeDelegaEnElDao() {
        Servicio servei = servicio("SERV1", "d");
        when(servicioDao.select(42L)).thenReturn(servei);
        assertSame(servei, scspHelper.findServicioByCode(42L));
    }

    @Test
    public void findEmisorCertificadoAllDelegaEnElDao() {
        List<EmisorCertificado> llista = List.of(new EmisorCertificado());
        when(emisorCertificadoDao.select()).thenReturn(llista);
        assertSame(llista, scspHelper.findEmisorCertificadoAll());
    }

    @Test
    public void findClavePublicaAllDelegaEnElDao() {
        List<ClavePublica> llista = List.of(new ClavePublica());
        when(clavePublicaDao.select()).thenReturn(llista);
        assertSame(llista, scspHelper.findClavePublicaAll());
    }

    @Test
    public void findClavePrivadaAllDelegaEnElDao() {
        List<ClavePrivada> llista = List.of(new ClavePrivada());
        when(clavePrivadaDao.select()).thenReturn(llista);
        assertSame(llista, scspHelper.findClavePrivadaAll());
    }

    // ------------------------- CRUD servei -------------------------

    @Test
    public void saveServicioDelegaEnElDao() {
        Servicio servei = servicio("SERV1", "d");
        scspHelper.saveServicio(servei);
        verify(servicioDao).save(servei);
    }

    @Test
    public void getServicioDelegaEnElDao() {
        Servicio servei = servicio("SERV1", "d");
        when(servicioDao.select("SERV1")).thenReturn(servei);
        assertSame(servei, scspHelper.getServicio("SERV1"));
    }

    @Test
    public void getServicioByIdDelegaEnElDao() {
        Servicio servei = servicio("SERV1", "d");
        when(servicioDao.select(7L)).thenReturn(servei);
        assertSame(servei, scspHelper.getServicioById(7L));
    }

    @Test
    public void deleteServicioSeleccionaIEsborraAmbElPinbalDao() {
        Servicio servei = servicio("SERV1", "d");
        when(servicioDao.select("SERV1")).thenReturn(servei);

        scspHelper.deleteServicio("SERV1");

        verify(pinbalDao).delete(servei);
    }

    // ------------------------- generarSolicitudId -------------------------

    @Test
    public void generarSolicitudIdFormatejaLIndexAmbZerosDavant() {
        assertEquals("PET100001", scspHelper.generarSolicitudId("PET1", 1));
        assertEquals("PET100042", scspHelper.generarSolicitudId("PET1", 42));
    }

    // ------------------------- organismo cesionario -------------------------

    @Test
    public void organismoCesionarioSaveAmbOrganismeNouElCrea() {
        when(organismoCesionarioDao.getAll()).thenReturn(new ArrayList<>());
        Date alta = new Date();
        Date baixa = new Date();

        scspHelper.organismoCesionarioSave("B123", "Organisme 1", alta, baixa, true);

        org.mockito.ArgumentCaptor<OrganismoCesionario> captor = org.mockito.ArgumentCaptor.forClass(OrganismoCesionario.class);
        verify(organismoCesionarioDao).save(captor.capture());
        OrganismoCesionario guardat = captor.getValue();
        assertEquals("B123", guardat.getCif());
        assertEquals("Organisme 1", guardat.getNombre());
        assertEquals(alta, guardat.getFechaAlta());
        assertEquals(baixa, guardat.getFechaBaja());
        assertTrue(guardat.isBloqueado());
    }

    @Test
    public void organismoCesionarioSaveAmbOrganismeExistentLActualitza() {
        OrganismoCesionario existent = new OrganismoCesionario();
        existent.setCif("B123");
        when(organismoCesionarioDao.getAll()).thenReturn(new ArrayList<>(List.of(existent)));

        scspHelper.organismoCesionarioSave("B123", "Nou nom", new Date(), null, false);

        verify(organismoCesionarioDao).save(existent);
        assertEquals("Nou nom", existent.getNombre());
        assertFalse(existent.isBloqueado());
    }

    @Test
    public void organismoCesionarioUpdateAmbOrganismeInexistentEnCreaUnDeNou() {
        when(organismoCesionarioDao.getAll()).thenReturn(new ArrayList<>());

        scspHelper.organismoCesionarioUpdate("B999", "Organisme nou", true);

        org.mockito.ArgumentCaptor<OrganismoCesionario> captor = org.mockito.ArgumentCaptor.forClass(OrganismoCesionario.class);
        verify(organismoCesionarioDao).save(captor.capture());
        assertEquals("B999", captor.getValue().getCif());
        assertEquals("Organisme nou", captor.getValue().getNombre());
    }

    @Test
    public void organismoCesionarioDeleteEsborraLHistoricIL0rganisme() {
        OrganismoCesionario organisme = new OrganismoCesionario();
        organisme.setCif("B123");
        when(organismoCesionarioDao.getAll()).thenReturn(new ArrayList<>(List.of(organisme)));
        when(servicioOrganismoCesionarioDao.selectHistorico(organisme)).thenReturn(new ArrayList<>());

        scspHelper.organismoCesionarioDelete("B123");

        verify(pinbalDao).delete(organisme);
    }

    @Test
    public void organismoCesionarioDeleteAmbOrganismeInexistentNoEsborraRes() {
        when(organismoCesionarioDao.getAll()).thenReturn(new ArrayList<>());
        when(servicioOrganismoCesionarioDao.selectHistorico((OrganismoCesionario) null)).thenReturn(new ArrayList<>());

        scspHelper.organismoCesionarioDelete("NO-EXISTEIX");

        verify(pinbalDao, never()).delete(any());
    }

    @Test
    public void organismoCesionarioFindByCifDelegaEnLaCercaPerCif() {
        OrganismoCesionario organisme = new OrganismoCesionario();
        organisme.setCif("B123");
        when(organismoCesionarioDao.getAll()).thenReturn(new ArrayList<>(List.of(organisme)));

        assertSame(organisme, scspHelper.organismoCesionarioFindByCif("B123"));
    }

    @Test
    public void organismoCesionarioFindByCifSenseCoincidenciaRetornaNull() {
        when(organismoCesionarioDao.getAll()).thenReturn(new ArrayList<>());
        assertNull(scspHelper.organismoCesionarioFindByCif("NO-EXISTEIX"));
    }

    // ------------------------- nodeToString -------------------------

    @Test
    public void nodeToStringSerialitzaElNodeSenseDeclaracioXml() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc = factory.newDocumentBuilder().parse(
                new ByteArrayInputStream("<a><b>text</b></a>".getBytes(StandardCharsets.UTF_8)));

        String resultat = scspHelper.nodeToString(doc.getDocumentElement());

        assertFalse(resultat.contains("<?xml"));
        assertTrue(resultat.contains("<a><b>text</b></a>"));
    }

    // ------------------------- getEmisor / getCifEmisor / getNombreEmisor -------------------------

    @Test
    public void getEmisorConstrueixLEmisorAmbCifINom() {
        Servicio servei = servicio("SERV1", "d");
        EmisorCertificado emisorCert = new EmisorCertificado();
        emisorCert.setCif("B00000000");
        servei.setEmisor(emisorCert);
        when(servicioDao.select("SERV1")).thenReturn(servei);

        EmisorCertificado emisorTrobat = new EmisorCertificado();
        emisorTrobat.setNombre("Emissor 1");
        when(emisorCertificadoDao.selectByCif("B00000000")).thenReturn(emisorTrobat);

        Emisor emisor = scspHelper.getEmisor("SERV1");

        assertEquals("B00000000", emisor.getNifEmisor());
        assertEquals("Emissor 1", emisor.getNombreEmisor());
    }

    @Test
    public void getCifEmisorIGetNombreEmisorDeleguenEnElsDaos() throws Exception {
        Servicio servei = servicio("SERV1", "d");
        EmisorCertificado emisorCert = new EmisorCertificado();
        emisorCert.setCif("B00000000");
        servei.setEmisor(emisorCert);
        when(servicioDao.select("SERV1")).thenReturn(servei);

        EmisorCertificado emisorTrobat = new EmisorCertificado();
        emisorTrobat.setNombre("Emissor 1");
        when(emisorCertificadoDao.selectByCif("B00000000")).thenReturn(emisorTrobat);

        assertEquals("B00000000", (String) invoke("getCifEmisor", new Class<?>[]{String.class}, "SERV1"));
        assertEquals("Emissor 1", (String) invoke("getNombreEmisor", new Class<?>[]{String.class}, "SERV1"));
    }

    // ------------------------- generarFinalidad -------------------------

    @Test
    public void generarFinalidadAmbFinalitatInformadaLaRetorna() throws Exception {
        Solicitud solicitud = new Solicitud();
        solicitud.setFinalitat("Finalitat concreta");
        solicitud.setProcedimentCodi("PROC1");

        assertEquals("Finalitat concreta", invoke("generarFinalidad", new Class<?>[]{Solicitud.class}, solicitud));
    }

    @Test
    public void generarFinalidadSenseFinalitatFaServirElCodiDeProcediment() throws Exception {
        Solicitud solicitud = new Solicitud();
        solicitud.setFinalitat("   ");
        solicitud.setProcedimentCodi("PROC1");

        assertEquals("PROC1", invoke("generarFinalidad", new Class<?>[]{Solicitud.class}, solicitud));
    }

    // ------------------------- copiarPropertiesToDb -------------------------

    @Test
    public void copiarPropertiesToDbIgnoraPropietatsSenseElPrefix() {
        Properties props = new Properties();
        props.setProperty("altre.prefix.clau", "valor");

        scspHelper.copiarPropertiesToDb(props);

        verify(parametroConfiguracionDao, never()).save(any());
    }

    @Test
    public void copiarPropertiesToDbGuardaLesPropietatsAmbElPrefixNoKeystore() {
        Properties props = new Properties();
        props.setProperty("es.caib.pinbal.scsp.prefijo.idpeticion", "ABC");

        scspHelper.copiarPropertiesToDb(props);

        org.mockito.ArgumentCaptor<ParametroConfiguracion> captor =
                org.mockito.ArgumentCaptor.forClass(ParametroConfiguracion.class);
        verify(parametroConfiguracionDao).save(captor.capture());
        assertEquals("prefijo.idpeticion", captor.getValue().getNombre());
        assertEquals("ABC", captor.getValue().getValor());
    }

    @Test
    public void copiarPropertiesToDbAmbClauKeystoreNoLaGuardaMaiADb() {
        Properties props = new Properties();
        props.setProperty("es.caib.pinbal.scsp.keystore.path", "/algun/path");
        when(parametroConfiguracionDao.select("keystore.path")).thenReturn(null);

        scspHelper.copiarPropertiesToDb(props);

        verify(parametroConfiguracionDao, never()).save(any());
    }

    @Test
    public void copiarPropertiesToDbAmbClauKeystoreJaExistentIIgualNoFaRes() {
        Properties props = new Properties();
        props.setProperty("es.caib.pinbal.scsp.keystore.path", "/algun/path");
        ParametroConfiguracion existent = new ParametroConfiguracion();
        existent.setValor("/algun/path");
        when(parametroConfiguracionDao.select("keystore.path")).thenReturn(existent);

        scspHelper.copiarPropertiesToDb(props);

        verify(parametroConfiguracionDao, never()).save(any());
    }

    @Test
    public void copiarPropertiesToDbIgnoraExcepcioEnGuardar() {
        Properties props = new Properties();
        props.setProperty("es.caib.pinbal.scsp.prefijo.idpeticion", "ABC");
        org.mockito.Mockito.doThrow(new RuntimeException("error bd")).when(parametroConfiguracionDao).save(any());

        // No hauria de propagar l'excepció (es captura internament).
        scspHelper.copiarPropertiesToDb(props);
    }
}
