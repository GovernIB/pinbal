package es.caib.pinbal.scsp.mock;

import es.scsp.bean.common.peticion.Atributos;
import es.scsp.bean.common.peticion.Peticion;
import es.scsp.bean.common.peticion.Solicitudes;
import es.scsp.bean.common.peticion.SolicitudTransmision;
import es.scsp.bean.common.respuesta.Respuesta;
import es.scsp.bean.common.confirmacion.ConfirmacionPeticion;
import es.scsp.common.exceptions.ScspException;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ClienteUnicoMockTest {

    private final ClienteUnicoMock mock = new ClienteUnicoMock();

    private Peticion peticio(String idPeticion, String codigoCertificado, int numSolicituds) {
        Peticion peticio = new Peticion();
        Atributos atributos = new Atributos();
        atributos.setIdPeticion(idPeticion);
        atributos.setCodigoCertificado(codigoCertificado);
        peticio.setAtributos(atributos);

        Solicitudes solicitudes = new Solicitudes();
        for (int i = 0; i < numSolicituds; i++) {
            solicitudes.getSolicitudTransmision().add(new SolicitudTransmision());
        }
        peticio.setSolicitudes(solicitudes);
        return peticio;
    }

    @Test
    public void getIDPeticionGeneraUnIdAmbPrefixMock() throws Exception {
        String id = mock.getIDPeticion("SERV1");
        assertTrue(id.startsWith("MOCK"));
    }

    @Test
    public void realizaPeticionSincronaRetornaUnaRespostaAmbLIdPeticio() throws Exception {
        Respuesta resposta = mock.realizaPeticionSincrona(peticio("PET1", "SERV1", 1));

        assertNotNull(resposta);
        assertEquals("PET1", resposta.getAtributos().getIdPeticion());
        assertEquals(1, resposta.getAtributos().getNumElementos());
        assertEquals("0003", resposta.getAtributos().getEstado().getCodigoEstado());
        assertEquals(1, resposta.getTransmisiones().getTransmisionDatos().size());
    }

    @Test
    public void realizaPeticionAsincronaRetornaConfirmacioAmbEstatOk() throws Exception {
        ConfirmacionPeticion confirmacio = mock.realizaPeticionAsincrona(peticio("PET2", "SERV1", 2));

        assertNotNull(confirmacio);
        assertEquals("PET2", confirmacio.getAtributos().getIdPeticion());
        assertEquals("0000", confirmacio.getAtributos().getEstado().getCodigoEstado());
    }

    @Test
    public void recuperaRespuestaOmpleDadesGeneriquesMock() throws Exception {
        Respuesta resposta = mock.recuperaRespuesta("PET3");

        assertEquals("PET3", resposta.getAtributos().getIdPeticion());
        es.scsp.bean.common.respuesta.DatosGenericos dades =
                resposta.getTransmisiones().getTransmisionDatos().get(0).getDatosGenericos();
        assertEquals("PET300001", dades.getTransmision().getIdSolicitud());
        assertEquals("B07167448", dades.getEmisor().getNifEmisor());
        assertEquals("12345678Z", dades.getTitular().getDocumentacion());
    }

    @Test
    public void generaJustificanteTransmisionRetornaUnPdfMock() throws Exception {
        ByteArrayOutputStream baos = mock.generaJustificanteTransmision("TRANS1", "PET1");

        assertNotNull(baos);
        assertTrue(baos.size() > 0);
        assertTrue(baos.toString().startsWith("%PDF-1.4"));
    }

    @Test
    public void simularErrorLlancaScspExceptionAmbElCodiIndicat() {
        try {
            mock.simularError("0001", "error de prova");
            fail("Hauria d'haver llançat ScspException");
        } catch (ScspException e) {
            // esperat
        }
    }
}
