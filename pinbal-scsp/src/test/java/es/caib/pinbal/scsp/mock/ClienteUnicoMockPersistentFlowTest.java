package es.caib.pinbal.scsp.mock;

import es.scsp.bean.common.confirmacion.ConfirmacionPeticion;
import es.scsp.bean.common.peticion.Atributos;
import es.scsp.bean.common.peticion.Consentimiento;
import es.scsp.bean.common.peticion.DatosGenericos;
import es.scsp.bean.common.peticion.Funcionario;
import es.scsp.bean.common.peticion.Peticion;
import es.scsp.bean.common.peticion.Procedimiento;
import es.scsp.bean.common.peticion.Solicitante;
import es.scsp.bean.common.peticion.Solicitudes;
import es.scsp.bean.common.peticion.SolicitudTransmision;
import es.scsp.bean.common.peticion.Titular;
import es.scsp.bean.common.peticion.Transmision;
import es.scsp.bean.common.respuesta.Respuesta;
import es.scsp.common.dao.PeticionRespuestaDao;
import es.scsp.common.dao.ServicioDao;
import es.scsp.common.dao.TipoMensajeDao;
import es.scsp.common.dao.TokenDao;
import es.scsp.common.dao.TransmisionDao;
import es.scsp.common.domain.core.EmisorCertificado;
import es.scsp.common.domain.core.PeticionRespuesta;
import es.scsp.common.domain.core.Servicio;
import es.scsp.common.domain.core.TipoMensaje;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testeja els fluxos complets (síncron/asíncron) de {@link ClienteUnicoMockPersistent}, que
 * persisteixen dades a través de diversos DAOs. S'injecten mocks de tots els DAOs via reflexió
 * (els camps {@code @Autowired} no tenen setter), i es fa servir un {@link AtomicReference} per
 * simular la persistència de {@code PeticionRespuesta} entre les diverses crides internes a
 * {@code save}/{@code select}, evitant necessitar una sessió Hibernate real.
 */
public class ClienteUnicoMockPersistentFlowTest {

    private ClienteUnicoMockPersistent mock;
    private ServicioDao servicioDao;
    private PeticionRespuestaDao peticionRespuestaDao;
    private TransmisionDao transmisionDao;
    private TipoMensajeDao tipoMensajeDao;
    private TokenDao tokenDao;

    private final AtomicReference<PeticionRespuesta> peticionRespuestaGuardada = new AtomicReference<>();
    private final List<es.scsp.common.domain.core.Transmision> transmisionsGuardades = new ArrayList<>();

    @Before
    public void configurar() throws Exception {
        mock = new ClienteUnicoMockPersistent();
        servicioDao = mock(ServicioDao.class);
        peticionRespuestaDao = mock(PeticionRespuestaDao.class);
        transmisionDao = mock(TransmisionDao.class);
        tipoMensajeDao = mock(TipoMensajeDao.class);
        tokenDao = mock(TokenDao.class);

        setField("servicioDao", servicioDao);
        setField("peticionRespuestaDao", peticionRespuestaDao);
        setField("transmisionDao", transmisionDao);
        setField("tipoMensajeDao", tipoMensajeDao);
        setField("tokenDao", tokenDao);

        Servicio servei = new Servicio();
        servei.setCodCertificado("SERV1");
        EmisorCertificado emisor = new EmisorCertificado();
        emisor.setCif("B00000000");
        emisor.setNombre("Emissor de prova");
        servei.setEmisor(emisor);
        when(servicioDao.select("SERV1")).thenReturn(servei);

        // Simula la persistència de PeticionRespuesta: cada save() actualitza la referència, i
        // cada select() retorna l'última versió guardada.
        org.mockito.Mockito.doAnswer(invocation -> {
            peticionRespuestaGuardada.set(invocation.getArgument(0));
            return null;
        }).when(peticionRespuestaDao).save(any());
        when(peticionRespuestaDao.select(anyString())).thenAnswer(invocation -> peticionRespuestaGuardada.get());

        org.mockito.Mockito.doAnswer(invocation -> {
            transmisionsGuardades.add(invocation.getArgument(0));
            return null;
        }).when(transmisionDao).save(any());
        when(transmisionDao.select(any(PeticionRespuesta.class))).thenAnswer(invocation -> transmisionsGuardades);

        TipoMensaje tipoMensaje = new TipoMensaje();
        tipoMensaje.setTipo(TipoMensaje.PETICION);
        when(tipoMensajeDao.select(TipoMensaje.PETICION)).thenReturn(tipoMensaje);
    }

    private void setField(String name, Object value) throws Exception {
        Field field = ClienteUnicoMockPersistent.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(mock, value);
    }

    private Peticion peticio(String idPeticion) {
        Peticion peticion = new Peticion();
        Atributos atributos = new Atributos();
        atributos.setIdPeticion(idPeticion);
        atributos.setCodigoCertificado("SERV1");
        peticion.setAtributos(atributos);

        SolicitudTransmision st = new SolicitudTransmision();
        DatosGenericos dadesGeneriques = new DatosGenericos();

        Solicitante solicitant = new Solicitante();
        solicitant.setIdentificadorSolicitante("B00000000");
        solicitant.setNombreSolicitante("Solicitant de prova");
        solicitant.setFinalidad("Finalitat de prova");
        solicitant.setConsentimiento(Consentimiento.SI);
        solicitant.setUnidadTramitadora("Unitat");
        solicitant.setCodigoUnidadTramitadora("U1");
        solicitant.setIdExpediente("EXP1");

        Funcionario funcionari = new Funcionario();
        funcionari.setNifFuncionario("12345678Z");
        funcionari.setNombreCompletoFuncionario("Funcionari de prova");
        solicitant.setFuncionario(funcionari);

        Procedimiento procediment = new Procedimiento();
        procediment.setCodProcedimiento("PROC1");
        procediment.setNombreProcedimiento("Procediment de prova");
        solicitant.setProcedimiento(procediment);

        dadesGeneriques.setSolicitante(solicitant);

        Titular titular = new Titular();
        titular.setDocumentacion("87654321X");
        titular.setNombre("Nom titular");
        titular.setApellido1("Llinatge1");
        titular.setApellido2("Llinatge2");
        titular.setNombreCompleto("Nom titular Llinatge1 Llinatge2");
        dadesGeneriques.setTitular(titular);

        Transmision transmissio = new Transmision();
        transmissio.setCodigoCertificado("SERV1");
        transmissio.setIdSolicitud(idPeticion);
        transmissio.setFechaGeneracion("2024-01-01T00:00:00");
        dadesGeneriques.setTransmision(transmissio);

        st.setDatosGenericos(dadesGeneriques);

        Solicitudes solicitudes = new Solicitudes();
        solicitudes.getSolicitudTransmision().add(st);
        peticion.setSolicitudes(solicitudes);

        return peticion;
    }

    @Test
    public void realizaPeticionSincronaGuardaIRecuperaLaRespostaAmbPersistencia() throws Exception {
        Respuesta resposta = mock.realizaPeticionSincrona(peticio("PET-SINC-1"));

        assertNotNull(resposta);
        assertEquals("PET-SINC-1", resposta.getAtributos().getIdPeticion());
        assertEquals("0003", resposta.getAtributos().getEstado().getCodigoEstado());
        assertEquals(1, resposta.getTransmisiones().getTransmisionDatos().size());

        es.scsp.bean.common.respuesta.DatosGenericos dadesResposta =
                resposta.getTransmisiones().getTransmisionDatos().get(0).getDatosGenericos();
        assertEquals("B00000000", dadesResposta.getSolicitante().getIdentificadorSolicitante());
        assertEquals("87654321X", dadesResposta.getTitular().getDocumentacion());
        assertEquals("B00000000", dadesResposta.getEmisor().getNifEmisor());

        verify(peticionRespuestaDao, org.mockito.Mockito.atLeastOnce()).save(any());
        verify(transmisionDao).save(any());
        verify(tokenDao).save(any());
    }

    @Test
    public void realizaPeticionAsincronaGuardaIRetornaConfirmacioPendent() throws Exception {
        ConfirmacionPeticion confirmacio = mock.realizaPeticionAsincrona(peticio("PET-ASINC-1"));

        assertNotNull(confirmacio);
        assertEquals("PET-ASINC-1", confirmacio.getAtributos().getIdPeticion());
        assertEquals("0000", confirmacio.getAtributos().getEstado().getCodigoEstado());

        // La petició s'ha guardat amb estat "0002" (pendent de resposta), ja que és asíncrona.
        assertEquals("0002", peticionRespuestaGuardada.get().getEstado());
        verify(transmisionDao).save(any());
        verify(tokenDao).save(any());
    }

    @Test
    public void recuperaRespuestaAmbPeticioAsincronaEncaraNoResoltaSimulaLaResposta() throws Exception {
        mock.realizaPeticionAsincrona(peticio("PET-ASINC-2"));
        // En aquest punt la petició encara no té fechaRespuesta (és asíncrona i no s'ha "consultat" encara).

        Respuesta resposta = mock.recuperaRespuesta("PET-ASINC-2");

        assertNotNull(resposta);
        assertEquals("0000", resposta.getAtributos().getEstado().getCodigoEstado());
        assertNotNull(peticionRespuestaGuardada.get().getFechaRespuesta());
    }
}
