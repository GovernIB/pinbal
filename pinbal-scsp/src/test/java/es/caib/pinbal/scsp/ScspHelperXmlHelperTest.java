package es.caib.pinbal.scsp;

import es.caib.pinbal.scsp.XmlHelper.DadesEspecifiquesNode;
import es.scsp.bean.common.peticion.Peticion;
import es.scsp.common.dao.EmisorCertificadoDao;
import es.scsp.common.dao.PeticionRespuestaDao;
import es.scsp.common.dao.ServicioDao;
import es.scsp.common.dao.TipoMensajeDao;
import es.scsp.common.dao.TokenDao;
import es.scsp.common.dao.TransmisionDao;
import es.scsp.common.domain.core.PeticionRespuesta;
import es.scsp.common.domain.core.Servicio;
import es.scsp.common.domain.core.TipoMensaje;
import es.scsp.common.domain.core.Token;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests dels mètodes de {@code ScspHelper} que deleguen en {@link XmlHelper}
 * ({@code crearPeticion}, {@code generarArbreDadesEspecifiques},
 * {@code copiarDadesEspecifiquesRecobriment}, {@code getDadesEspecifiques*}, ...). Com que
 * {@code getXmlHelper()} només instancia un {@code XmlHelper} nou si el camp és {@code null},
 * s'hi injecta un mock via reflexió abans d'invocar cap mètode, evitant necessitar esquemes XSD
 * reals ni el client Axis2.
 */
public class ScspHelperXmlHelperTest {

    private ApplicationContext applicationContext;
    private ScspHelper scspHelper;
    private XmlHelper xmlHelper;

    private ServicioDao servicioDao;
    private PeticionRespuestaDao peticionRespuestaDao;
    private TokenDao tokenDao;
    private TipoMensajeDao tipoMensajeDao;
    private TransmisionDao transmisionDao;
    private EmisorCertificadoDao emisorCertificadoDao;

    @Before
    public void configurar() throws Exception {
        applicationContext = mock(ApplicationContext.class);
        MessageSource messageSource = mock(MessageSource.class);
        scspHelper = new ScspHelper(applicationContext, messageSource);

        xmlHelper = mock(XmlHelper.class);
        setField("xmlHelper", xmlHelper);

        servicioDao = mock(ServicioDao.class);
        peticionRespuestaDao = mock(PeticionRespuestaDao.class);
        tokenDao = mock(TokenDao.class);
        tipoMensajeDao = mock(TipoMensajeDao.class);
        transmisionDao = mock(TransmisionDao.class);
        emisorCertificadoDao = mock(EmisorCertificadoDao.class);

        when(applicationContext.getBean("servicioDao")).thenReturn(servicioDao);
        when(applicationContext.getBean("peticionRespuestaDao")).thenReturn(peticionRespuestaDao);
        when(applicationContext.getBean("tokenDao")).thenReturn(tokenDao);
        when(applicationContext.getBean("tipoMensajeDao")).thenReturn(tipoMensajeDao);
        when(applicationContext.getBean("transmisionDao")).thenReturn(transmisionDao);
        when(applicationContext.getBean("emisorCertificadoDao")).thenReturn(emisorCertificadoDao);

        es.scsp.common.domain.core.EmisorCertificado emisorTrobat = new es.scsp.common.domain.core.EmisorCertificado();
        emisorTrobat.setNombre("Emissor de prova");
        when(emisorCertificadoDao.selectByCif("B00000000")).thenReturn(emisorTrobat);
    }

    private void setField(String name, Object value) throws Exception {
        Field field = ScspHelper.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(scspHelper, value);
    }

    private Servicio serviAmbEmisor() {
        Servicio servei = new Servicio();
        es.scsp.common.domain.core.EmisorCertificado emisor = new es.scsp.common.domain.core.EmisorCertificado();
        emisor.setCif("B00000000");
        emisor.setNombre("Emissor de prova");
        servei.setEmisor(emisor);
        return servei;
    }

    private Solicitud solicitud(String serveiCodi) {
        Solicitud s = new Solicitud();
        s.setServeiCodi(serveiCodi);
        s.setSolicitantIdentificacio("B00000000");
        s.setSolicitantNom("Solicitant de prova");
        s.setFinalitat("Finalitat de prova");
        s.setConsentiment(Consentiment.Si);
        s.setUnitatTramitadora("Unitat");
        s.setUnitatTramitadoraCodi("U1");
        s.setExpedientId("EXP1");
        s.setTitularDocumentTipus(DocumentTipus.NIF);
        s.setTitularDocument("87654321X");
        s.setTitularNom("Nom titular");
        return s;
    }

    private Element domElement(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        Document doc = factory.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        return doc.getDocumentElement();
    }

    // ------------------------- crearPeticion / crearSolicitudTransmision -------------------------

    @Test
    public void crearPeticionConstrueixLaPeticioAmbUnaSolicitud() throws Exception {
        when(servicioDao.select("SERV1")).thenReturn(serviAmbEmisor());
        when(xmlHelper.hasCodigoUnidadTramitadora(any(), anyBoolean())).thenReturn(true);

        Peticion peticio = scspHelper.crearPeticion(
                "PET1", List.of(solicitud("SERV1")), false, false, null, false);

        assertEquals("PET1", peticio.getAtributos().getIdPeticion());
        assertEquals("SERV1", peticio.getAtributos().getCodigoCertificado());
        assertEquals(1, peticio.getAtributos().getNumElementos());
        assertEquals(1, peticio.getSolicitudes().getSolicitudTransmision().size());

        es.scsp.bean.common.peticion.Solicitante solicitant =
                peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getSolicitante();
        assertEquals("U1", solicitant.getCodigoUnidadTramitadora());
        assertEquals(es.scsp.bean.common.peticion.Consentimiento.SI, solicitant.getConsentimiento());
    }

    @Test
    public void crearPeticionSenseCodigoUnidadTramitadoraElDeixaNull() throws Exception {
        when(servicioDao.select("SERV1")).thenReturn(serviAmbEmisor());
        when(xmlHelper.hasCodigoUnidadTramitadora(any(), anyBoolean())).thenReturn(false);

        Peticion peticio = scspHelper.crearPeticion(
                "PET1", List.of(solicitud("SERV1")), false, false, null, false);

        es.scsp.bean.common.peticion.Solicitante solicitant =
                peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getSolicitante();
        assertNull(solicitant.getCodigoUnidadTramitadora());
    }

    @Test
    public void crearPeticionAmbDadesEspecifiquesMapCridaXmlHelper() throws Exception {
        when(servicioDao.select("SERV1")).thenReturn(serviAmbEmisor());
        when(xmlHelper.hasCodigoUnidadTramitadora(any(), anyBoolean())).thenReturn(false);
        Element dadesEspecifiques = domElement("<DatosEspecificos/>");
        Map<String, Object> mapa = new HashMap<>();
        mapa.put("camp1", "valor1");
        when(xmlHelper.crearDadesEspecifiques(any(), eq(mapa), anyBoolean(), anyBoolean(), any(), anyBoolean()))
                .thenReturn(dadesEspecifiques);

        Solicitud sol = solicitud("SERV1");
        sol.setDadesEspecifiquesMap(mapa);

        Peticion peticio = scspHelper.crearPeticion("PET1", List.of(sol), false, false, null, false);

        Object datosEspecificos = peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosEspecificos();
        assertSame(dadesEspecifiques, datosEspecificos);
    }

    @Test
    public void crearPeticionAmbMultiplesSolicitudsFormategaElIdAmbSisDigits() throws Exception {
        when(servicioDao.select("SERV1")).thenReturn(serviAmbEmisor());
        when(xmlHelper.hasCodigoUnidadTramitadora(any(), anyBoolean())).thenReturn(false);

        Peticion peticio = scspHelper.crearPeticion(
                "PET1", List.of(solicitud("SERV1"), solicitud("SERV1")), false, false, null, false);

        String idSol1 = peticio.getSolicitudes().getSolicitudTransmision().get(0)
                .getDatosGenericos().getTransmision().getIdSolicitud();
        String idSol2 = peticio.getSolicitudes().getSolicitudTransmision().get(1)
                .getDatosGenericos().getTransmision().getIdSolicitud();
        assertEquals("000001", idSol1);
        assertEquals("000002", idSol2);
    }

    // ------------------------- generaPeticioXml -------------------------

    @Test
    public void generaPeticioXmlDelegaEnXmlHelper() throws Exception {
        Peticion peticio = new Peticion();
        when(xmlHelper.generatePeticioXml(peticio)).thenReturn("<xml/>");

        assertEquals("<xml/>", scspHelper.generaPeticioXml(peticio));
    }

    // ------------------------- generarArbreDadesEspecifiques -------------------------

    @Test
    public void generarArbreDadesEspecifiquesDelegaEnXmlHelper() throws Exception {
        Servicio servei = new Servicio();
        when(servicioDao.select("SERV1")).thenReturn(servei);
        es.caib.pinbal.scsp.tree.Tree<DadesEspecifiquesNode> arbre = new es.caib.pinbal.scsp.tree.Tree<>();
        when(xmlHelper.getArbrePerDadesEspecifiques(servei, true)).thenReturn(arbre);

        assertSame(arbre, scspHelper.generarArbreDadesEspecifiques("SERV1", true));
    }

    // ------------------------- copiarDadesEspecifiquesRecobriment -------------------------

    @Test
    public void copiarDadesEspecifiquesRecobrimentAmbDadesNullRetornaNull() throws Exception {
        assertNull(scspHelper.copiarDadesEspecifiquesRecobriment("SERV1", null, false, false, null));
    }

    @Test
    public void copiarDadesEspecifiquesRecobrimentDelegaEnXmlHelper() throws Exception {
        Servicio servei = new Servicio();
        when(servicioDao.select("SERV1")).thenReturn(servei);
        Element dadesRebudes = domElement("<DatosEspecificos><Camp>1</Camp></DatosEspecificos>");
        Element resultat = domElement("<DatosEspecificos/>");
        when(xmlHelper.copiarDadesEspecifiquesRecobriment(eq(servei), eq(dadesRebudes), anyBoolean(), anyBoolean(), any()))
                .thenReturn(resultat);

        assertSame(resultat, scspHelper.copiarDadesEspecifiquesRecobriment("SERV1", dadesRebudes, false, false, null));
    }

    // ------------------------- getDadesEspecifiquesPeticio / Resposta -------------------------

    @Test
    public void getDadesEspecifiquesPeticioAmbTokenExistentDelegaEnXmlHelper() throws Exception {
        PeticionRespuesta pr = new PeticionRespuesta();
        when(peticionRespuestaDao.select("PET1")).thenReturn(pr);
        TipoMensaje tm = new TipoMensaje();
        when(tipoMensajeDao.select(TipoMensaje.PETICION)).thenReturn(tm);
        Token token = new Token();
        token.setDatos("<xml-peticio/>");
        when(tokenDao.select(tm, pr)).thenReturn(token);
        when(xmlHelper.getXmlSolicitudTransmision("<xml-peticio/>", "SOL1")).thenReturn("<fragment/>");
        Map<String, Object> resultatMapa = Collections.singletonMap("clau", "valor");
        when(xmlHelper.getDadesEspecifiquesXml("<fragment/>")).thenReturn(resultatMapa);

        Map<String, Object> resultat = scspHelper.getDadesEspecifiquesPeticio("PET1", "SOL1");

        assertSame(resultatMapa, resultat);
    }

    @Test
    public void getDadesEspecifiquesPeticioSenseTokenRetornaNull() throws Exception {
        when(peticionRespuestaDao.select("PET1")).thenReturn(new PeticionRespuesta());
        when(tokenDao.select(any(), any())).thenReturn(null);
        when(xmlHelper.getDadesEspecifiquesXml(null)).thenReturn(null);

        assertNull(scspHelper.getDadesEspecifiquesPeticio("PET1", "SOL1"));
    }

    @Test
    public void getDadesEspecifiquesRespostaDelegaEnLaTransmissio() throws Exception {
        PeticionRespuesta pr = new PeticionRespuesta();
        when(peticionRespuestaDao.select("PET1")).thenReturn(pr);
        es.scsp.common.domain.core.Transmision transmissio = new es.scsp.common.domain.core.Transmision();
        transmissio.setXmlTransmision("<xml-transmissio/>");
        when(transmisionDao.select(pr, "SOL1")).thenReturn(transmissio);
        Map<String, Object> resultatMapa = Collections.singletonMap("clau", "valor");
        when(xmlHelper.getDadesEspecifiquesXml("<xml-transmissio/>")).thenReturn(resultatMapa);

        Map<String, Object> resultat = scspHelper.getDadesEspecifiquesResposta("PET1", "SOL1");

        assertSame(resultatMapa, resultat);
    }
}
