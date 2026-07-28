package es.caib.pinbal.scsp;

import es.caib.pinbal.logic.intf.service.exception.ConsultaScspComunicacioException;
import es.caib.pinbal.logic.intf.service.exception.ConsultaScspGeneracioException;
import es.caib.pinbal.scsp.JustificantArbreHelper.ElementArbre;
import es.caib.pinbal.scsp.mock.ClienteUnicoFactory;
import es.scsp.bean.common.confirmacion.ConfirmacionPeticion;
import es.scsp.bean.common.respuesta.DatosGenericos;
import es.scsp.bean.common.respuesta.Respuesta;
import es.scsp.bean.common.respuesta.Transmision;
import es.scsp.bean.common.respuesta.TransmisionDatos;
import es.scsp.bean.common.respuesta.Transmisiones;
import es.scsp.client.ClienteUnico;
import es.scsp.common.dao.ErrorDao;
import es.scsp.common.dao.PeticionRespuestaDao;
import es.scsp.common.dao.ServicioDao;
import es.scsp.common.dao.TipoMensajeDao;
import es.scsp.common.dao.TokenDao;
import es.scsp.common.dao.TransmisionDao;
import es.scsp.common.domain.core.CodigoError;
import es.scsp.common.domain.core.PeticionRespuesta;
import es.scsp.common.domain.core.Servicio;
import es.scsp.common.domain.core.TipoMensaje;
import es.scsp.common.domain.core.Token;
import es.scsp.common.exceptions.ScspException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests dels mètodes de {@code ScspHelper} que deleguen en {@link ClienteUnico}
 * ({@code enviarPeticionSincrona/Asincrona}, {@code generaJustificanteTransmision},
 * {@code generarArbreJustificant}, {@code recuperarRespuestaScsp}) i dels que combinen DAOs sense
 * necessitar-lo ({@code isPeticionEnviada}, {@code recuperarResposta},
 * {@code recuperarResultatEnviamentPeticio}). El {@code ClienteUnico} s'obté via
 * {@code ClienteUnicoFactory}, que es mockeja igual que la resta de beans (per nom/classe a
 * {@code applicationContext.getBean(...)}); {@code xmlHelper} s'injecta per reflexió com a la
 * resta de tests d'aquesta classe per evitar processar XSD reals.
 */
public class ScspHelperClienteUnicoTest {

    private ApplicationContext applicationContext;
    private ScspHelper scspHelper;
    private XmlHelper xmlHelper;
    private JustificantArbreHelper justificantArbreHelper;
    private ClienteUnico clienteUnico;

    private ServicioDao servicioDao;
    private PeticionRespuestaDao peticionRespuestaDao;
    private TokenDao tokenDao;
    private TipoMensajeDao tipoMensajeDao;
    private TransmisionDao transmisionDao;
    private ErrorDao errorDao;
    private es.scsp.common.dao.EmisorCertificadoDao emisorCertificadoDao;

    @Before
    public void configurar() throws Exception {
        applicationContext = mock(ApplicationContext.class);
        MessageSource messageSource = mock(MessageSource.class);
        scspHelper = new ScspHelper(applicationContext, messageSource);

        xmlHelper = mock(XmlHelper.class);
        setField("xmlHelper", xmlHelper);
        justificantArbreHelper = mock(JustificantArbreHelper.class);
        setField("justificantArbrehelper", justificantArbreHelper);

        servicioDao = mock(ServicioDao.class);
        peticionRespuestaDao = mock(PeticionRespuestaDao.class);
        tokenDao = mock(TokenDao.class);
        tipoMensajeDao = mock(TipoMensajeDao.class);
        transmisionDao = mock(TransmisionDao.class);
        errorDao = mock(ErrorDao.class);
        emisorCertificadoDao = mock(es.scsp.common.dao.EmisorCertificadoDao.class);

        when(applicationContext.getBean("servicioDao")).thenReturn(servicioDao);
        when(applicationContext.getBean("peticionRespuestaDao")).thenReturn(peticionRespuestaDao);
        when(applicationContext.getBean("tokenDao")).thenReturn(tokenDao);
        when(applicationContext.getBean("tipoMensajeDao")).thenReturn(tipoMensajeDao);
        when(applicationContext.getBean("transmisionDao")).thenReturn(transmisionDao);
        when(applicationContext.getBean("errorDao")).thenReturn(errorDao);
        when(applicationContext.getBean("emisorCertificadoDao")).thenReturn(emisorCertificadoDao);

        es.scsp.common.domain.core.EmisorCertificado emisorTrobat = new es.scsp.common.domain.core.EmisorCertificado();
        emisorTrobat.setNombre("Emissor de prova");
        when(emisorCertificadoDao.selectByCif("B00000000")).thenReturn(emisorTrobat);

        ClienteUnicoFactory clienteUnicoFactory = mock(ClienteUnicoFactory.class);
        clienteUnico = mock(ClienteUnico.class);
        when(applicationContext.getBean(ClienteUnicoFactory.class)).thenReturn(clienteUnicoFactory);
        when(clienteUnicoFactory.getClienteUnico()).thenReturn(clienteUnico);
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

    private void preparaCreacioPeticio() throws Exception {
        when(servicioDao.select("SERV1")).thenReturn(serviAmbEmisor());
        when(xmlHelper.hasCodigoUnidadTramitadora(any(), anyBoolean())).thenReturn(true);
    }

    // ------------------------- enviarPeticionSincrona -------------------------

    @Test
    public void enviarPeticionSincronaAmbExitRetornaElResultat() throws Exception {
        preparaCreacioPeticio();
        PeticionRespuesta pr = new PeticionRespuesta();
        pr.setEstado("0000");
        when(peticionRespuestaDao.select("PET1")).thenReturn(pr);
        when(transmisionDao.select(pr)).thenReturn(Collections.emptyList());

        ResultatEnviamentPeticio resultat = scspHelper.enviarPeticionSincrona(
                "PET1", List.of(solicitud("SERV1")), false, false, null, false);

        assertFalse(resultat.isErrorRecepcio());
        assertEquals("0000", resultat.getEstatCodi());
    }

    @Test
    public void enviarPeticionSincronaAmbErrorEnCrearPeticioLlancaGeneracioException() {
        // Sense preparar servicioDao -> getEmisor() de crearSolicitudTransmision provoca NPE.
        try {
            scspHelper.enviarPeticionSincrona(
                    "PET1", List.of(solicitud("SERV1")), false, false, null, false);
            fail("Hauria d'haver llançat ConsultaScspGeneracioException");
        } catch (ConsultaScspGeneracioException | ConsultaScspComunicacioException ex) {
            assertTrue(ex instanceof ConsultaScspGeneracioException);
        }
    }

    @Test
    public void enviarPeticionSincronaAmbErrorDeComunicacioLlancaComunicacioException() throws Exception {
        preparaCreacioPeticio();
        when(clienteUnico.realizaPeticionSincrona(any())).thenThrow(new ScspException("0500", "error scsp"));

        try {
            scspHelper.enviarPeticionSincrona(
                    "PET1", List.of(solicitud("SERV1")), false, false, null, false);
            fail("Hauria d'haver llançat ConsultaScspComunicacioException");
        } catch (ConsultaScspComunicacioException ex) {
            // esperat
        }
    }

    // ------------------------- enviarPeticionAsincrona -------------------------

    @Test
    public void enviarPeticionAsincronaAmbExitRetornaElResultatAmbConfirmacio() throws Exception {
        preparaCreacioPeticio();
        PeticionRespuesta pr = new PeticionRespuesta();
        pr.setEstado("0002");
        when(peticionRespuestaDao.select("PET1")).thenReturn(pr);
        when(transmisionDao.select(pr)).thenReturn(Collections.emptyList());
        ConfirmacionPeticion confirmacio = new ConfirmacionPeticion();
        when(clienteUnico.realizaPeticionAsincrona(any())).thenReturn(confirmacio);

        ResultatEnviamentPeticio resultat = scspHelper.enviarPeticionAsincrona(
                "PET1", List.of(solicitud("SERV1")), false, false, null, false);

        assertSame(confirmacio, resultat.getConfirmacionPeticion());
        assertEquals("0002", resultat.getEstatCodi());
    }

    @Test
    public void enviarPeticionAsincronaAmbErrorDeComunicacioLlancaComunicacioException() throws Exception {
        preparaCreacioPeticio();
        when(clienteUnico.realizaPeticionAsincrona(any())).thenThrow(new ScspException("0500", "error scsp"));

        try {
            scspHelper.enviarPeticionAsincrona(
                    "PET1", List.of(solicitud("SERV1")), false, false, null, false);
            fail("Hauria d'haver llançat ConsultaScspComunicacioException");
        } catch (ConsultaScspComunicacioException ex) {
            // esperat
        }
    }

    // ------------------------- generaJustificanteTransmision -------------------------

    @Test
    public void generaJustificanteTransmisionDelegaEnClienteUnico() throws Exception {
        PeticionRespuesta pr = new PeticionRespuesta();
        when(peticionRespuestaDao.select("PET1")).thenReturn(pr);
        es.scsp.common.domain.core.Transmision transmissio = new es.scsp.common.domain.core.Transmision();
        transmissio.setIdTransmision("TR1");
        when(transmisionDao.select(pr, "SOL1")).thenReturn(transmissio);
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        when(clienteUnico.generaJustificanteTransmision("TR1", "PET1")).thenReturn(baos);

        assertSame(baos, scspHelper.generaJustificanteTransmision("PET1", "SOL1"));
    }

    // ------------------------- generarArbreJustificant -------------------------

    private Respuesta respostaAmbTransmisio(String idSolicitud) {
        Respuesta resposta = new Respuesta();
        Transmisiones transmisiones = new Transmisiones();
        TransmisionDatos td = new TransmisionDatos();
        DatosGenericos dg = new DatosGenericos();
        Transmision transmissio = new Transmision();
        transmissio.setIdSolicitud(idSolicitud);
        dg.setTransmision(transmissio);
        td.setDatosGenericos(dg);
        transmisiones.getTransmisionDatos().add(td);
        resposta.setTransmisiones(transmisiones);
        return resposta;
    }

    @Test
    public void generarArbreJustificantAmbTransmissioTrobadaDelegaEnJustificantArbreHelper() throws Exception {
        Respuesta resposta = respostaAmbTransmisio("SOL1");
        when(clienteUnico.recuperaRespuesta("PET1")).thenReturn(resposta);
        ElementArbre arbre = new ElementArbre("/");
        when(justificantArbreHelper.generarArbre(any(TransmisionDatos.class), eq("PET1"), eq(Locale.ENGLISH)))
                .thenReturn(arbre);

        ElementArbre resultat = scspHelper.generarArbreJustificant("PET1", "SOL1", Locale.ENGLISH);

        assertSame(arbre, resultat);
    }

    @Test
    public void generarArbreJustificantSenseTransmissioTrobadaLlancaScspException() throws Exception {
        Respuesta resposta = respostaAmbTransmisio("ALTRA-SOL");
        when(clienteUnico.recuperaRespuesta("PET1")).thenReturn(resposta);

        try {
            scspHelper.generarArbreJustificant("PET1", "SOL1", Locale.ENGLISH);
            fail("Hauria d'haver llançat ScspException");
        } catch (ScspException ex) {
            assertEquals("0234", ex.getMessage());
            assertTrue(ex.getScspCode().contains("No s'ha pogut trobar"));
        }
    }

    // ------------------------- recuperarRespuestaScsp -------------------------

    @Test
    public void recuperarRespuestaScspDelegaEnClienteUnico() throws Exception {
        Respuesta resposta = new Respuesta();
        when(clienteUnico.recuperaRespuesta("PET1")).thenReturn(resposta);

        assertSame(resposta, scspHelper.recuperarRespuestaScsp("PET1"));
    }

    // ------------------------- isPeticionEnviada -------------------------

    @Test
    public void isPeticionEnviadaAmbTokenExistentRetornaTrue() throws Exception {
        PeticionRespuesta pr = new PeticionRespuesta();
        when(peticionRespuestaDao.select("PET1")).thenReturn(pr);
        TipoMensaje tm = new TipoMensaje();
        when(tipoMensajeDao.select(TipoMensaje.PETICION)).thenReturn(tm);
        when(tokenDao.select(tm, pr)).thenReturn(new Token());

        assertTrue(scspHelper.isPeticionEnviada("PET1"));
    }

    @Test
    public void isPeticionEnviadaSenseTokenRetornaFalse() throws Exception {
        PeticionRespuesta pr = new PeticionRespuesta();
        when(peticionRespuestaDao.select("PET1")).thenReturn(pr);
        when(tipoMensajeDao.select(TipoMensaje.PETICION)).thenReturn(new TipoMensaje());
        when(tokenDao.select(any(), any())).thenReturn(null);

        assertFalse(scspHelper.isPeticionEnviada("PET1"));
    }

    @Test
    public void isPeticionEnviadaSensePeticioRetornaFalse() throws Exception {
        when(peticionRespuestaDao.select("PET1")).thenReturn(null);

        assertFalse(scspHelper.isPeticionEnviada("PET1"));
    }

    // ------------------------- recuperarResultatEnviamentPeticio -------------------------

    @Test
    public void recuperarResultatEnviamentPeticioSensePeticioIndicaErrorRecepcio() throws Exception {
        when(peticionRespuestaDao.select("PET1")).thenReturn(null);

        ResultatEnviamentPeticio resultat = scspHelper.recuperarResultatEnviamentPeticio("PET1");

        assertTrue(resultat.isErrorRecepcio());
    }

    @Test
    public void recuperarResultatEnviamentPeticioAmbErrorBuscaDescripcioAErrorDao() throws Exception {
        PeticionRespuesta pr = new PeticionRespuesta();
        pr.setEstado("0500");
        pr.setError("ERR-DETALL");
        when(peticionRespuestaDao.select("PET1")).thenReturn(pr);
        CodigoError codigoError = new CodigoError();
        codigoError.setDescripcion("Descripció genèrica {0}{1}{2}{3}{4}");
        when(errorDao.select("0500")).thenReturn(codigoError);
        when(transmisionDao.select(pr)).thenReturn(Collections.emptyList());

        ResultatEnviamentPeticio resultat = scspHelper.recuperarResultatEnviamentPeticio("PET1");

        assertTrue(resultat.isErrorRecepcio());
        assertTrue(resultat.getEstatDescripcio().contains("Descripció genèrica"));
        assertTrue(resultat.getEstatDescripcio().contains("ERR-DETALL"));
    }

    @Test
    public void recuperarResultatEnviamentPeticioOrdenaIdsSolicitudsDeLesTransmissions() throws Exception {
        PeticionRespuesta pr = new PeticionRespuesta();
        pr.setEstado("0000");
        when(peticionRespuestaDao.select("PET1")).thenReturn(pr);
        es.scsp.common.domain.core.Transmision t1 = new es.scsp.common.domain.core.Transmision();
        t1.setIdSolicitud("000002");
        es.scsp.common.domain.core.Transmision t2 = new es.scsp.common.domain.core.Transmision();
        t2.setIdSolicitud("000001");
        List<es.scsp.common.domain.core.Transmision> transmisions = new ArrayList<>(List.of(t1, t2));
        when(transmisionDao.select(pr)).thenReturn(transmisions);

        ResultatEnviamentPeticio resultat = scspHelper.recuperarResultatEnviamentPeticio("PET1");

        assertEquals("000001", resultat.getIdsSolicituds()[0]);
        assertEquals("000002", resultat.getIdsSolicituds()[1]);
    }

    // ------------------------- recuperarResposta -------------------------

    @Test
    public void recuperarRespostaSensePeticioRetornaRespostaBuida() throws Exception {
        when(peticionRespuestaDao.select("PET1")).thenReturn(null);

        Resposta resposta = scspHelper.recuperarResposta("PET1", "SOL1", false);

        assertNull(resposta.getRespostaData());
        assertNull(resposta.getUnitatTramitadora());
    }

    @Test
    public void recuperarRespostaNoMultipleOmpleDadesDeLaTransmissio() throws Exception {
        PeticionRespuesta pr = new PeticionRespuesta();
        java.util.Date dataResposta = new java.util.Date(0L);
        pr.setEstado("0000");
        pr.setFechaRespuesta(dataResposta);
        when(peticionRespuestaDao.select("PET1")).thenReturn(pr);

        es.scsp.common.domain.core.Transmision transmissio = new es.scsp.common.domain.core.Transmision();
        transmissio.setUnidadTramitadora("Unitat");
        transmissio.setCodigoUnidadTramitadora("U1");
        transmissio.setConsentimiento("SI");
        transmissio.setExpediente("EXP1");
        transmissio.setFinalidad("Finalitat");
        transmissio.setXmlTransmision("<xml-transmissio/>");
        when(transmisionDao.select(pr, "SOL1")).thenReturn(transmissio);

        TipoMensaje tm = new TipoMensaje();
        when(tipoMensajeDao.select(TipoMensaje.PETICION)).thenReturn(tm);
        Token token = new Token();
        token.setDatos("<xml-peticio/>");
        when(tokenDao.select(tm, pr)).thenReturn(token);
        when(xmlHelper.getXmlSolicitudTransmision("<xml-peticio/>", "SOL1")).thenReturn("<sol-fragment/>");
        when(transmisionDao.select(pr)).thenReturn(Collections.singletonList(transmissio));

        Resposta resposta = scspHelper.recuperarResposta("PET1", "SOL1", false);

        assertEquals(dataResposta, resposta.getRespostaData());
        assertEquals("Unitat", resposta.getUnitatTramitadora());
        assertEquals("U1", resposta.getUnitatTramitadoraCodi());
        assertEquals(Consentiment.Si, resposta.getConsentiment());
        assertEquals("EXP1", resposta.getExpedientId());
        assertEquals("Finalitat", resposta.getFinalitat());
        assertEquals("<sol-fragment/>", resposta.getPeticioXml());
        assertEquals("<xml-transmissio/>", resposta.getRespostaXml());
        assertFalse(resposta.getResultatEnviament().isErrorRecepcio());
    }

    @Test
    public void recuperarRespostaAmbFinalitatCodificadaExtreuExpedientIFinalitat() throws Exception {
        PeticionRespuesta pr = new PeticionRespuesta();
        pr.setEstado("0000");
        when(peticionRespuestaDao.select("PET1")).thenReturn(pr);

        es.scsp.common.domain.core.Transmision transmissio = new es.scsp.common.domain.core.Transmision();
        transmissio.setConsentimiento("LEY");
        transmissio.setFinalidad("FIN-BASE#::#EXP-CODIFICAT#::#FIN-CODIFICADA");
        when(transmisionDao.select(pr, "SOL1")).thenReturn(transmissio);
        when(tipoMensajeDao.select(TipoMensaje.PETICION)).thenReturn(new TipoMensaje());
        when(tokenDao.select(any(), eq(pr))).thenReturn(null);
        when(transmisionDao.select(pr)).thenReturn(Collections.singletonList(transmissio));

        Resposta resposta = scspHelper.recuperarResposta("PET1", "SOL1", false);

        assertEquals(Consentiment.Llei, resposta.getConsentiment());
        assertEquals("EXP-CODIFICAT", resposta.getExpedientId());
        assertEquals("FIN-CODIFICADA", resposta.getFinalitat());
    }

    @Test
    public void recuperarRespostaMultipleAgafaLaPrimeraTransmissioIElXmlDeLaPeticio() throws Exception {
        PeticionRespuesta pr = new PeticionRespuesta();
        pr.setEstado("0000");
        when(peticionRespuestaDao.select("PET1")).thenReturn(pr);

        es.scsp.common.domain.core.Transmision transmissio = new es.scsp.common.domain.core.Transmision();
        transmissio.setConsentimiento("SI");
        transmissio.setExpediente("EXP1");
        when(transmisionDao.select(pr)).thenReturn(Collections.singletonList(transmissio));

        TipoMensaje tm = new TipoMensaje();
        when(tipoMensajeDao.select(TipoMensaje.PETICION)).thenReturn(tm);
        Token token = new Token();
        token.setDatos("<xml-peticio/>");
        when(tokenDao.select(tm, pr)).thenReturn(token);
        when(xmlHelper.getXmlPeticion("<xml-peticio/>", "PET1")).thenReturn("<peticio-completa/>");

        Resposta resposta = scspHelper.recuperarResposta("PET1", "SOL1", true);

        assertNull(resposta.getExpedientId());
        assertEquals("<peticio-completa/>", resposta.getPeticioXml());
        assertNull(resposta.getRespostaXml());
    }

    @Test
    public void recuperarRespostaSenseTransmissioNoOmpleDadesDeTransmissio() throws Exception {
        PeticionRespuesta pr = new PeticionRespuesta();
        pr.setEstado("0000");
        when(peticionRespuestaDao.select("PET1")).thenReturn(pr);
        when(transmisionDao.select(pr, "SOL1")).thenReturn(null);

        Resposta resposta = scspHelper.recuperarResposta("PET1", "SOL1", false);

        assertNull(resposta.getUnitatTramitadora());
        assertNull(resposta.getResultatEnviament());
    }
}
