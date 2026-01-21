package es.caib.pinbal.scsp.mock;

import es.scsp.bean.common.Atributos;
import es.scsp.bean.common.ConfirmacionPeticion;
import es.scsp.bean.common.Consentimiento;
import es.scsp.bean.common.DatosGenericos;
import es.scsp.bean.common.Emisor;
import es.scsp.bean.common.Estado;
import es.scsp.bean.common.Peticion;
import es.scsp.bean.common.Respuesta;
import es.scsp.bean.common.Solicitante;
import es.scsp.bean.common.TipoDocumentacion;
import es.scsp.bean.common.Titular;
import es.scsp.bean.common.Transmision;
import es.scsp.bean.common.TransmisionDatos;
import es.scsp.bean.common.Transmisiones;
import es.scsp.client.ClienteUnico;
import es.scsp.common.exceptions.ScspException;
import es.scsp.common.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.Date;

/**
 * Mock de ClienteUnico per desenvolupament sense accés als serveis SCSP reals.
 *
 * Aquesta classe substitueix ClienteUnico quan s'activa el perfil "mock-scsp",
 * permetent desenvolupar i provar l'aplicació sense necessitat de certificats
 * ni connexió als serveis SCSP reals.
 *
 * Per activar-lo:
 * - Arrancar amb: mvn spring-boot:run -Dspring-boot.run.profiles=mock-scsp
 * - O configurar a IntelliJ: Run > Edit Configurations > Active profiles: mock-scsp
 *
 * @author Pinbal Development Team
 */
@Component
@Profile("mock-scsp")
public class ClienteUnicoMock extends ClienteUnico {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClienteUnicoMock.class);
    private static final String MOCK_PREFIX = "🔧 [MOCK SCSP]";

    @Override
    public String getIDPeticion(String codigoCertificado) throws ScspException {
        String idPeticion = "MOCK" + System.currentTimeMillis();
        LOGGER.info("{} Generant ID Petició: {} per servei: {}",
            MOCK_PREFIX, idPeticion, codigoCertificado);
        return idPeticion;
    }

    @Override
    public Respuesta realizaPeticionSincrona(Peticion peticion) throws ScspException {
        String idPeticion = peticion.getAtributos().getIdPeticion();
        String codigoCertificado = peticion.getAtributos().getCodigoCertificado();
        int numSolicitudes = peticion.getSolicitudes().getSolicitudTransmision().size();

        LOGGER.info("{} ============================================", MOCK_PREFIX);
        LOGGER.info("{} Enviant petició SÍNCRONA", MOCK_PREFIX);
        LOGGER.info("{} ID Petició: {}", MOCK_PREFIX, idPeticion);
        LOGGER.info("{} Servei: {}", MOCK_PREFIX, codigoCertificado);
        LOGGER.info("{} Número de sol·licituds: {}", MOCK_PREFIX, numSolicitudes);
        LOGGER.info("{} ============================================", MOCK_PREFIX);

        // Simular processament
        try {
            Thread.sleep(800); // Simular latència de xarxa
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        LOGGER.info("{} ✅ Petició síncrona processada correctament", MOCK_PREFIX);

        // Retornar resposta mock
        return recuperaRespuesta(idPeticion);
    }

    @Override
    public ConfirmacionPeticion realizaPeticionAsincrona(Peticion peticion) throws ScspException {
        String idPeticion = peticion.getAtributos().getIdPeticion();
        String codigoCertificado = peticion.getAtributos().getCodigoCertificado();
        int numSolicitudes = peticion.getSolicitudes().getSolicitudTransmision().size();

        LOGGER.info("{} ============================================", MOCK_PREFIX);
        LOGGER.info("{} Enviant petició ASÍNCRONA", MOCK_PREFIX);
        LOGGER.info("{} ID Petició: {}", MOCK_PREFIX, idPeticion);
        LOGGER.info("{} Servei: {}", MOCK_PREFIX, codigoCertificado);
        LOGGER.info("{} Número de sol·licituds: {}", MOCK_PREFIX, numSolicitudes);
        LOGGER.info("{} ============================================", MOCK_PREFIX);

        // Simular processament
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Crear confirmació de petició
        ConfirmacionPeticion confirmacion = new ConfirmacionPeticion();
        Atributos atributos = new Atributos();
        atributos.setIdPeticion(idPeticion);
        atributos.setTimeStamp(DateUtils.parseISO8601(new Date()));

        Estado estado = new Estado();
        estado.setCodigoEstado("0000");
        estado.setLiteralError("Mock: Petició acceptada correctament");
        atributos.setEstado(estado);

        confirmacion.setAtributos(atributos);

        LOGGER.info("{} ✅ Petició asíncrona confirmada amb estat: 0000", MOCK_PREFIX);

        return confirmacion;
    }

    @Override
    public Respuesta recuperaRespuesta(String idPeticion) throws ScspException {
        LOGGER.info("{} Recuperant resposta per petició: {}", MOCK_PREFIX, idPeticion);

        Respuesta respuesta = new Respuesta();
        Transmisiones transmisiones = new Transmisiones();
        transmisiones.setTransmisionDatos(new java.util.ArrayList<TransmisionDatos>());

        // Crear una transmissió mock bàsica
        TransmisionDatos transmisionDatos = new TransmisionDatos();

        // Configurar dades genèriques
        DatosGenericos datosGenericos = new DatosGenericos();

        Transmision transmision = new Transmision();
        transmision.setIdSolicitud(idPeticion + "00001");
        transmision.setCodigoCertificado("MOCK_SERVICE");
        transmision.setFechaGeneracion(DateUtils.parseISO8601(new Date()));
        datosGenericos.setTransmision(transmision);

        Emisor emisor = new Emisor();
        emisor.setNifEmisor("B07167448");
        emisor.setNombreEmisor("Mock Emisor");
        datosGenericos.setEmisor(emisor);

        Solicitante solicitante = new Solicitante();
        solicitante.setIdentificadorSolicitante("B07167448");
        solicitante.setNombreSolicitante("Mock Solicitant");
        solicitante.setConsentimiento(Consentimiento.Si);
        datosGenericos.setSolicitante(solicitante);

        Titular titular = new Titular();
        titular.setTipoDocumentacion(TipoDocumentacion.NIF);
        titular.setDocumentacion("12345678Z");
        titular.setNombreCompleto("Mock Titular");
        datosGenericos.setTitular(titular);

        transmisionDatos.setDatosGenericos(datosGenericos);

        // Afegir a les transmissions
        transmisiones.getTransmisionDatos().add(transmisionDatos);
        respuesta.setTransmisiones(transmisiones);

        // Atributs de la resposta
        Atributos atributos = new Atributos();
        atributos.setIdPeticion(idPeticion);
        atributos.setNumElementos("1");
        atributos.setTimeStamp(DateUtils.parseISO8601(new Date()));
        atributos.setEstado(new Estado());
        atributos.getEstado().setCodigoEstado("0003");
        atributos.getEstado().setLiteralError("OK");
        respuesta.setAtributos(atributos);

        LOGGER.info("{} ✅ Resposta recuperada amb {} transmissions",
            MOCK_PREFIX, transmisiones.getTransmisionDatos().size());

        return respuesta;
    }

    @Override
    public ByteArrayOutputStream generaJustificanteTransmision(
            String idTransmision,
            String idPeticion) throws ScspException {

        LOGGER.info("{} Generant justificant de transmissió", MOCK_PREFIX);
        LOGGER.info("{} - ID Transmissió: {}", MOCK_PREFIX, idTransmision);
        LOGGER.info("{} - ID Petició: {}", MOCK_PREFIX, idPeticion);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Generar un PDF mock molt simple
        String mockPdfContent = "%PDF-1.4\n" +
                "1 0 obj\n" +
                "<< /Type /Catalog /Pages 2 0 R >>\n" +
                "endobj\n" +
                "2 0 obj\n" +
                "<< /Type /Pages /Kids [3 0 R] /Count 1 >>\n" +
                "endobj\n" +
                "3 0 obj\n" +
                "<< /Type /Page /Parent 2 0 R /Resources 4 0 R /MediaBox [0 0 612 792] >>\n" +
                "endobj\n" +
                "4 0 obj\n" +
                "<< /Font << /F1 5 0 R >> >>\n" +
                "endobj\n" +
                "5 0 obj\n" +
                "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\n" +
                "endobj\n" +
                "xref\n" +
                "0 6\n" +
                "0000000000 65535 f\n" +
                "0000000009 00000 n\n" +
                "0000000058 00000 n\n" +
                "0000000115 00000 n\n" +
                "0000000214 00000 n\n" +
                "0000000263 00000 n\n" +
                "trailer\n" +
                "<< /Size 6 /Root 1 0 R >>\n" +
                "startxref\n" +
                "348\n" +
                "%%EOF\n";

        try {
            baos.write(mockPdfContent.getBytes());
        } catch (Exception e) {
            LOGGER.error("{} Error generant PDF mock", MOCK_PREFIX, e);
        }

        LOGGER.info("{} ✅ Justificant generat ({} bytes)",
            MOCK_PREFIX, baos.size());

        return baos;
    }

    /**
     * Mètode addicional per si es necessita simular errors
     */
    public void simularError(String codiError, String descripcio) throws ScspException {
        LOGGER.warn("{} ⚠️ Simulant error: {} - {}", MOCK_PREFIX, codiError, descripcio);
        throw new ScspException(codiError, descripcio);
    }
}
