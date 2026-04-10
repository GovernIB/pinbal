package es.caib.pinbal.scsp.mock;

import es.scsp.bean.common.Atributos;
import es.scsp.bean.common.ConfirmacionPeticion;
import es.scsp.bean.common.Consentimiento;
import es.scsp.bean.common.DatosGenericos;
import es.scsp.bean.common.Emisor;
import es.scsp.bean.common.Estado;
import es.scsp.bean.common.Funcionario;
import es.scsp.bean.common.Peticion;
import es.scsp.bean.common.Procedimiento;
import es.scsp.bean.common.Respuesta;
import es.scsp.bean.common.Solicitante;
import es.scsp.bean.common.SolicitudTransmision;
import es.scsp.bean.common.Titular;
import es.scsp.bean.common.TransmisionDatos;
import es.scsp.bean.common.Transmisiones;
import es.scsp.client.ClienteUnico;
import es.scsp.common.dao.PeticionRespuestaDao;
import es.scsp.common.dao.ServicioDao;
import es.scsp.common.dao.TipoMensajeDao;
import es.scsp.common.dao.TokenDao;
import es.scsp.common.dao.TransmisionDao;
import es.scsp.common.domain.core.PeticionRespuesta;
import es.scsp.common.domain.core.Servicio;
import es.scsp.common.domain.core.TipoMensaje;
import es.scsp.common.domain.core.Token;
import es.scsp.common.exceptions.ScspException;
import es.scsp.common.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Mock de ClienteUnico amb persistència a BBDD.
 *
 * Aquest mock simula el comportament del ClienteUnico però guarda
 * tota la informació a les taules SCSP (core_peticion_respuesta,
 * core_transmision, core_token) igual que ho fa el client real.
 *
 * Això permet:
 * - Desenvolupar sense certificats ni connexió a serveis reals
 * - L'aplicació pot recuperar i visualitzar la informació de les consultes
 * - Proves completes de tot el flux sense dependències externes
 *
 * Per activar-lo:
 * - docker run -e SPRING_PROFILES_ACTIVE=mock-scsp-db ...
 * - mvn spring-boot:run -Dspring-boot.run.profiles=mock-scsp-db
 *
 * @author Pinbal Development Team
 */
@Component
@Profile("mock-scsp-db")
public class ClienteUnicoMockPersistent extends ClienteUnico {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClienteUnicoMockPersistent.class);
    private static final String MOCK_PREFIX = "🔧 [MOCK SCSP DB]";
    static final String SERVEI_TSD_PRESTACIONS = "SVDINSSTSDPRESTACIONESWS01";
    private static final String MOCK_PDF_BASE64 =
            "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b2RpbmcgL0lkZW50aXR5LUgKL0Rlc2NlbmRhbnRGb250cyBbMTUgMCBSXQovVG9Vbmljb2RlIDE2IDAgUj4+CmVuZG9iagp4cmVmCjAgMTcKMDAwMDAwMDAwMCA2NTUzNSBmIAowMDAwMDAwMDE1IDAwMDAwIG4gCjAwMDAwMDA0NzUgMDAwMDAgbiAKMDAwMDAwMDE3NSAwMDAwMCBuIAowMDAwMDA2MjIzIDAwMDAwIG4gCjAwMDAwMDAyMTIgMDAwMDAgbiAKMDAwMDAwMDY5MiAwMDAwMCBuIAowMDAwMDAwOTYxIDAwMDAwIG4gCjAwMDAwMDA4MTQgMDAwMDAgbiAKMDAwMDAwMDc0NyAwMDAwMCBuIAowMDAwMDAwODgyIDAwMDAwIG4gCjAwMDAwMDA5MDYgMDAwMDAgbiAKMDAwMDAwMTA1MSAwMDAwMCBuIAowMDAwMDAxMjQ1IDAwMDAwIG4gCjAwMDAwMDUzNjMgMDAwMDAgbiAKMDAwMDAwNTYxNSAwMDAwMCBuIAowMDAwMDA1OTAzIDAwMDAwIG4gCnRyYWlsZXIKPDwvU2l6ZSAxNwovUm9vdCAxMiAwIFIKL0luZm8gMSAwIFI+PgpzdGFydHhyZWYKNjM3MQolJUVPRgo=";

    @Autowired
    private PeticionRespuestaDao peticionRespuestaDao;

    @Autowired
    private TransmisionDao transmisionDao;

    @Autowired
    private TokenDao tokenDao;

    @Autowired
    private TipoMensajeDao tipoMensajeDao;

    @Autowired
    private ServicioDao servicioDao;

    @Autowired
    private SessionFactory sessionFactory;

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
        LOGGER.info("{} Enviant petició SÍNCRONA amb persistència", MOCK_PREFIX);
        LOGGER.info("{} ID Petició: {}", MOCK_PREFIX, idPeticion);
        LOGGER.info("{} Servei: {}", MOCK_PREFIX, codigoCertificado);
        LOGGER.info("{} Número de sol·licituds: {}", MOCK_PREFIX, numSolicitudes);
        LOGGER.info("{} ============================================", MOCK_PREFIX);

        // Simular processament
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Guardar a BBDD
        guardarPeticionRespuesta(peticion, true);
        guardarTransmisiones(peticion);
        guardarToken(peticion, TipoMensaje.PETICION);

        LOGGER.info("{} ✅ Petició síncrona guardada a BBDD", MOCK_PREFIX);

        // Retornar la resposta
        return recuperaRespuesta(idPeticion);
    }

    @Override
    public ConfirmacionPeticion realizaPeticionAsincrona(Peticion peticion) throws ScspException {
        String idPeticion = peticion.getAtributos().getIdPeticion();
        String codigoCertificado = peticion.getAtributos().getCodigoCertificado();
        int numSolicitudes = peticion.getSolicitudes().getSolicitudTransmision().size();

        LOGGER.info("{} ============================================", MOCK_PREFIX);
        LOGGER.info("{} Enviant petició ASÍNCRONA amb persistència", MOCK_PREFIX);
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

        // Guardar a BBDD
        guardarPeticionRespuesta(peticion, false);
        guardarTransmisiones(peticion);
        guardarToken(peticion, TipoMensaje.PETICION);

        // Crear confirmació de petició
        ConfirmacionPeticion confirmacion = new ConfirmacionPeticion();
        Atributos atributos = new Atributos();
        atributos.setIdPeticion(idPeticion);
        atributos.setTimeStamp(DateUtils.parseISO8601(new Date()));

        Estado estado = new Estado();
        estado.setCodigoEstado("0000");
        estado.setLiteralError("Mock: Petició acceptada correctament amb persistència a BBDD");
        atributos.setEstado(estado);

        confirmacion.setAtributos(atributos);

        LOGGER.info("{} ✅ Petició asíncrona guardada a BBDD amb estat: 0000", MOCK_PREFIX);

        return confirmacion;
    }

    @Override
    public Respuesta recuperaRespuesta(String idPeticion) throws ScspException {
        LOGGER.info("{} Recuperant resposta de BBDD per petició: {}", MOCK_PREFIX, idPeticion);

        // Recuperar de BBDD la petició
        PeticionRespuesta peticionRespuesta = peticionRespuestaDao.select(idPeticion);
        if (peticionRespuesta == null) {
            throw new ScspException("0234", "No s'ha trobat la petició: " + idPeticion);
        }

        // Si encara no s'ha processat, simular la resposta i guardar-la
        if (peticionRespuesta.getFechaRespuesta() == null) {
            LOGGER.info("{} Simulant processament de resposta...", MOCK_PREFIX);
            simularRespuesta(peticionRespuesta);
        }

        // Construir la resposta a partir de les dades de BBDD
        Respuesta respuesta = construirRespuestaDesdeDB(peticionRespuesta);

        LOGGER.info("{} ✅ Resposta recuperada de BBDD amb {} transmissions",
            MOCK_PREFIX, respuesta.getTransmisiones().getTransmisionDatos().size());

        return respuesta;
    }

    @Override
    public ByteArrayOutputStream generaJustificanteTransmision(
            String idTransmision,
            String idPeticion) throws ScspException {

        LOGGER.info("{} Generant justificant de transmissió des de BBDD", MOCK_PREFIX);
        LOGGER.info("{} - ID Transmissió: {}", MOCK_PREFIX, idTransmision);
        LOGGER.info("{} - ID Petició: {}", MOCK_PREFIX, idPeticion);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Generar un PDF mock simple
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

        LOGGER.info("{} ✅ Justificant generat ({} bytes)", MOCK_PREFIX, baos.size());
        return baos;
    }

    /**
     * Guarda la petició-resposta a la taula core_peticion_respuesta
     */
    private void guardarPeticionRespuesta(Peticion peticion, boolean sincrona) throws ScspException {
        String idPeticion = peticion.getAtributos().getIdPeticion();
        String codigoCertificado = peticion.getAtributos().getCodigoCertificado();

        PeticionRespuesta pr = new PeticionRespuesta();
        pr.setIdPeticion(idPeticion);

        // Obtenir el servei
        Servicio servicio = servicioDao.select(codigoCertificado);
        pr.setServicio(servicio);

        pr.setFechaPeticion(new Date());
        pr.setNumeroEnvios(1);
        pr.setNumeroTransmisiones(peticion.getSolicitudes().getSolicitudTransmision().size());
        pr.setTransmisionSincrona(sincrona ? 1 : 0);

        // Calcular TER (Tiempo Estimado de Respuesta) - 24 hores
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, 24);
        pr.setTer(cal.getTime());

        if (sincrona) {
            // Per síncrones, la resposta és immediata amb estat Tramitada
            pr.setEstado("0003"); // Tramitada
            pr.setFechaRespuesta(new Date());
            pr.setError("Tramitada");
            LOGGER.info("{} Petició síncrona guardada amb estat 0003 (Tramitada)", MOCK_PREFIX);
        } else {
            // Per asíncrones, la resposta es recupera després
            pr.setEstado("0002"); // Pendent de resposta
            pr.setFechaUltimoSondeo(new Date());
        }

        peticionRespuestaDao.save(pr);
        LOGGER.debug("{} PeticionRespuesta guardada: {}", MOCK_PREFIX, idPeticion);
    }

    /**
     * Guarda les transmissions a la taula core_transmision
     */
    private void guardarTransmisiones(Peticion peticion) throws ScspException {
        String idPeticion = peticion.getAtributos().getIdPeticion();
        PeticionRespuesta pr = peticionRespuestaDao.select(idPeticion);

        int index = 1;
        for (SolicitudTransmision st : peticion.getSolicitudes().getSolicitudTransmision()) {
            es.scsp.common.domain.core.Transmision trans = new es.scsp.common.domain.core.Transmision();

            // Dades de la sol·licitud
            String idSolicitud = st.getDatosGenericos().getTransmision().getIdSolicitud();
            trans.setIdSolicitud(idSolicitud);
            trans.setIdTransmision("TRANS" + System.currentTimeMillis() + String.format("%03d", index++));
            trans.setPeticion(pr);

            // Dades del solicitant
            trans.setIdSolicitante(st.getDatosGenericos().getSolicitante().getIdentificadorSolicitante());
            trans.setNombreSolicitante(st.getDatosGenericos().getSolicitante().getNombreSolicitante());

            // Dades del titular
            if (st.getDatosGenericos().getTitular() != null) {
                trans.setDocTitular(st.getDatosGenericos().getTitular().getDocumentacion());
                trans.setNombreTitular(st.getDatosGenericos().getTitular().getNombre());
                trans.setApellido1Titular(st.getDatosGenericos().getTitular().getApellido1());
                trans.setApellido2Titular(st.getDatosGenericos().getTitular().getApellido2());
                trans.setNombreCompletoTitular(st.getDatosGenericos().getTitular().getNombreCompleto());
            }

            // Dades del funcionari
            if (st.getDatosGenericos().getSolicitante().getFuncionario() != null) {
                trans.setDocFuncionario(st.getDatosGenericos().getSolicitante().getFuncionario().getNifFuncionario());
                trans.setNombreFuncionario(st.getDatosGenericos().getSolicitante().getFuncionario().getNombreCompletoFuncionario());
            }

            // Dades del procediment
            trans.setCodigoProcedimiento(st.getDatosGenericos().getSolicitante().getProcedimiento().getCodProcedimiento());
            trans.setNombreProcedimiento(st.getDatosGenericos().getSolicitante().getProcedimiento().getNombreProcedimiento());
            trans.setUnidadTramitadora(st.getDatosGenericos().getSolicitante().getUnidadTramitadora());
            trans.setCodigoUnidadTramitadora(st.getDatosGenericos().getSolicitante().getCodigoUnidadTramitadora());

            // Altres camps
            trans.setFechaGeneracion(new Date());
            trans.setExpediente(st.getDatosGenericos().getSolicitante().getIdExpediente());
            trans.setFinalidad(st.getDatosGenericos().getSolicitante().getFinalidad());
            trans.setConsentimiento(st.getDatosGenericos().getSolicitante().getConsentimiento().toString());

            // Estat de la transmissió (0003 = Tramitada per síncrones)
            PeticionRespuesta prCheck = peticionRespuestaDao.select(idPeticion);
            if (prCheck != null && prCheck.getTransmisionSincrona() == 1) {
                trans.setEstado("0003"); // Tramitada
            } else {
                trans.setEstado("0000"); // OK per asíncrones
            }

            String serveiCodi = peticion.getAtributos().getCodigoCertificado();
            String xmlResposta = generarXmlRespostaPerServei(serveiCodi, idSolicitud);
            if (xmlResposta == null) {
                // Serialitzar les dades específiques si existeixen
                if (st.getDatosEspecificos() != null) {
                    try {
                        xmlResposta = serializarXml(st);
                    } catch (Exception e) {
                        LOGGER.warn("{} Error serialitzant dades específiques: {}", MOCK_PREFIX, e.getMessage());
                        xmlResposta = generarXmlMockResposta(idSolicitud);
                    }
                } else {
                    xmlResposta = generarXmlMockResposta(idSolicitud);
                }
            }
            trans.setXmlTransmision(injectarDocumentsConfigurats(xmlResposta, serveiCodi));

            transmisionDao.save(trans);
            LOGGER.debug("{} Transmision guardada: {}", MOCK_PREFIX, idSolicitud);
        }
    }

    /**
     * Guarda el token (XML de la petició) a la taula core_token
     */
    private void guardarToken(Peticion peticion, int tipoMensaje) throws ScspException {
        String idPeticion = peticion.getAtributos().getIdPeticion();
        PeticionRespuesta pr = peticionRespuestaDao.select(idPeticion);
        TipoMensaje tm = tipoMensajeDao.select(tipoMensaje);

        Token token = new Token();
        token.setPeticion(pr);
        token.setTipoMensaje(tm);

        // Serialitzar la petició a XML
        try {
            String xmlPeticion = serializarPeticionAXml(peticion);
            token.setDatos(xmlPeticion);
        } catch (Exception e) {
            LOGGER.warn("{} Error serialitzant petició a XML: {}", MOCK_PREFIX, e.getMessage());
            token.setDatos(generarXmlMockPeticion(idPeticion));
        }

        tokenDao.save(token);
        LOGGER.debug("{} Token guardat per petició: {}", MOCK_PREFIX, idPeticion);
    }

    /**
     * Simula el processament de la resposta per peticions asíncrones
     */
    private void simularRespuesta(PeticionRespuesta peticionRespuesta) throws ScspException {
        peticionRespuesta.setFechaRespuesta(new Date());
        peticionRespuesta.setEstado("0000");
        peticionRespuesta.setError("Mock: Consulta processada correctament");
        peticionRespuestaDao.save(peticionRespuesta);

        // Actualitzar les transmissions amb respostes mock
        java.util.List<es.scsp.common.domain.core.Transmision> transmisiones = transmisionDao.select(peticionRespuesta);
        for (es.scsp.common.domain.core.Transmision trans : transmisiones) {
            if (trans.getXmlTransmision() == null || trans.getXmlTransmision().isEmpty()) {
                String serveiCodi = peticionRespuesta.getServicio().getCodCertificado();
                String xmlResposta = generarXmlRespostaPerServei(serveiCodi, trans.getIdSolicitud());
                trans.setXmlTransmision(injectarDocumentsConfigurats(
                        xmlResposta != null ? xmlResposta : generarXmlMockResposta(trans.getIdSolicitud()),
                        serveiCodi));
                transmisionDao.save(trans);
            }
        }

        LOGGER.debug("{} Resposta simulada per petició: {}", MOCK_PREFIX, peticionRespuesta.getIdPeticion());
    }

    /**
     * Construeix l'objecte Respuesta a partir de les dades de BBDD
     */
    private Respuesta construirRespuestaDesdeDB(PeticionRespuesta peticionRespuesta) throws ScspException {
        Respuesta respuesta = new Respuesta();

        // Atributs
        Atributos atributos = new Atributos();
        atributos.setIdPeticion(peticionRespuesta.getIdPeticion());
        atributos.setTimeStamp(DateUtils.parseISO8601(peticionRespuesta.getFechaRespuesta()));
        atributos.setNumElementos(String.valueOf(peticionRespuesta.getNumeroTransmisiones()));

        Estado estado = new Estado();
        estado.setCodigoEstado(peticionRespuesta.getEstado());
        estado.setLiteralError(peticionRespuesta.getError());
        atributos.setEstado(estado);

        respuesta.setAtributos(atributos);

        // Transmissions
        Transmisiones transmisiones = new Transmisiones();
        transmisiones.setTransmisionDatos(new java.util.ArrayList<TransmisionDatos>());
        java.util.List<es.scsp.common.domain.core.Transmision> transDB = transmisionDao.select(peticionRespuesta);

        for (es.scsp.common.domain.core.Transmision transDBItem : transDB) {
            TransmisionDatos td = new TransmisionDatos();

            // Dades genèriques
            DatosGenericos dg = new DatosGenericos();

            es.scsp.bean.common.Transmision transmision = new es.scsp.bean.common.Transmision();
            transmision.setIdSolicitud(transDBItem.getIdSolicitud());
            transmision.setCodigoCertificado(peticionRespuesta.getServicio().getCodCertificado());
            transmision.setFechaGeneracion(DateUtils.parseISO8601(transDBItem.getFechaGeneracion()));
            dg.setTransmision(transmision);

            Solicitante solicitante = new Solicitante();
            solicitante.setIdentificadorSolicitante(transDBItem.getIdSolicitante());
            solicitante.setNombreSolicitante(transDBItem.getNombreSolicitante());
            solicitante.setUnidadTramitadora(transDBItem.getUnidadTramitadora());
            solicitante.setFinalidad(transDBItem.getFinalidad());

            if (transDBItem.getConsentimiento() != null && transDBItem.getConsentimiento().equals("Si")) {
                solicitante.setConsentimiento(Consentimiento.Si);
            } else {
                solicitante.setConsentimiento(Consentimiento.Ley);
            }

            Funcionario funcionario = new Funcionario();
            funcionario.setNifFuncionario(transDBItem.getDocFuncionario());
            funcionario.setNombreCompletoFuncionario(transDBItem.getNombreFuncionario());
            solicitante.setFuncionario(funcionario);

            Procedimiento procedimiento = new Procedimiento();
            procedimiento.setCodProcedimiento(transDBItem.getCodigoProcedimiento());
            procedimiento.setNombreProcedimiento(transDBItem.getNombreProcedimiento());
            solicitante.setProcedimiento(procedimiento);

            dg.setSolicitante(solicitante);

            Titular titular = new Titular();
            titular.setDocumentacion(transDBItem.getDocTitular());
            titular.setNombre(transDBItem.getNombreTitular());
            titular.setApellido1(transDBItem.getApellido1Titular());
            titular.setApellido2(transDBItem.getApellido2Titular());
            titular.setNombreCompleto(transDBItem.getNombreCompletoTitular());
            dg.setTitular(titular);

            Emisor emisor = new Emisor();
            emisor.setNifEmisor(peticionRespuesta.getServicio().getEmisor().getCif());
            emisor.setNombreEmisor(peticionRespuesta.getServicio().getEmisor().getNombre());
            dg.setEmisor(emisor);

            td.setDatosGenericos(dg);

            try {
                td.setDatosEspecificos(parseDatosEspecificos(transDBItem.getXmlTransmision()));
            } catch (Exception e) {
                LOGGER.warn("{} No s'han pogut parsejar les dades específiques de la transmissió {}: {}",
                        MOCK_PREFIX, transDBItem.getIdSolicitud(), e.getMessage());
            }

            transmisiones.getTransmisionDatos().add(td);
        }

        respuesta.setTransmisiones(transmisiones);

        return respuesta;
    }

    /**
     * Serialitza una SolicitudTransmision a XML
     */
    private String serializarXml(SolicitudTransmision st) throws Exception {
        if (st.getDatosEspecificos() == null) {
            return null;
        }

        Object datosEsp = st.getDatosEspecificos();
        if (datosEsp instanceof org.w3c.dom.Node) {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource((org.w3c.dom.Node) datosEsp), new StreamResult(writer));
            return writer.toString();
        }

        // Si no és un Node, retornar null
        return null;
    }

    private String injectarDocumentsConfigurats(String xmlResposta, String serveiCodi) {
        if (xmlResposta == null || serveiCodi == null) {
            return xmlResposta;
        }
        try {
            List<String> xpathsDocumentals = getXpathsDocumentals(serveiCodi);
            if (xpathsDocumentals == null || xpathsDocumentals.isEmpty()) {
                return xmlResposta;
            }
            return injectarDocumentsEnXmlResposta(xmlResposta, xpathsDocumentals, getMockPdfBytes());
        } catch (Exception e) {
            LOGGER.warn("{} No s'han pogut injectar documents mock pel servei {}: {}",
                    MOCK_PREFIX, serveiCodi, e.getMessage());
            return xmlResposta;
        }
    }

    List<String> getXpathsDocumentals(String serveiCodi) {
        Session session = sessionFactory.getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            @SuppressWarnings("unchecked")
            List<String> xpaths = session.createSQLQuery(
                            "select xpath from pbl_servei_justif_camp " +
                                    "where servei_id = :serveiCodi and document = 1")
                    .setString("serveiCodi", serveiCodi)
                    .list();
            tx.commit();
            return xpaths;
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        }
    }

    byte[] getMockPdfBytes() {
        return DatatypeConverter.parseBase64Binary(MOCK_PDF_BASE64);
    }

    String injectarDocumentsEnXmlResposta(String xmlResposta, List<String> xpathsDocumentals, byte[] contingutPdf) throws Exception {
        if (xmlResposta == null || xpathsDocumentals == null || xpathsDocumentals.isEmpty() || contingutPdf == null) {
            return xmlResposta;
        }

        Element datosEspecificos = parseDatosEspecificos(xmlResposta);
        if (datosEspecificos == null) {
            return xmlResposta;
        }

        String pdfBase64 = DatatypeConverter.printBase64Binary(contingutPdf);
        for (String xpathDocumental : xpathsDocumentals) {
            injectarDocument(datosEspecificos, xpathDocumental, pdfBase64);
        }

        return serialitzarNode(datosEspecificos);
    }

    Element parseDatosEspecificos(String xml) throws Exception {
        if (xml == null || xml.trim().isEmpty()) {
            return null;
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document document = builder.parse(new InputSource(new StringReader(xml)));

        Element root = document.getDocumentElement();
        if (root != null && "DatosEspecificos".equals(root.getLocalName() != null ? root.getLocalName() : root.getNodeName())) {
            return root;
        }

        NodeList nodes = document.getElementsByTagNameNS("*", "DatosEspecificos");
        if (nodes.getLength() > 0) {
            return (Element) nodes.item(0);
        }

        return null;
    }

    private void injectarDocument(Element datosEspecificos, String xpathDocumental, String pdfBase64) {
        if (xpathDocumental == null || xpathDocumental.trim().isEmpty()) {
            return;
        }

        String[] parts = xpathDocumental.split("/");
        if (parts.length == 0 || !"DatosEspecificos".equals(parts[0])) {
            return;
        }

        Element current = datosEspecificos;
        for (int i = 1; i < parts.length; i++) {
            String part = parts[i];
            if (part == null || part.isEmpty()) {
                continue;
            }
            Element child = getChildElement(current, part);
            if (child == null) {
                child = current.getOwnerDocument().createElement(part);
                current.appendChild(child);
            }
            current = child;
        }

        current.setTextContent(pdfBase64);
    }

    private Element getChildElement(Element parent, String nodeName) {
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child instanceof Element) {
                Element childElement = (Element) child;
                String localName = childElement.getLocalName() != null ? childElement.getLocalName() : childElement.getNodeName();
                if (nodeName.equals(localName)) {
                    return childElement;
                }
            }
        }
        return null;
    }

    private String serialitzarNode(Node node) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(node), new StreamResult(writer));
        return writer.toString();
    }

    /**
     * Serialitza una Peticion a XML
     */
    private String serializarPeticionAXml(Peticion peticion) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // Construir XML de la petició manualment
            StringBuilder xml = new StringBuilder();
            xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            xml.append("<Peticion xmlns=\"http://www.map.es/scsp/esquemas/V2/peticion\">\n");
            xml.append("  <Atributos>\n");
            xml.append("    <IdPeticion>").append(peticion.getAtributos().getIdPeticion()).append("</IdPeticion>\n");
            xml.append("    <NumElementos>").append(peticion.getAtributos().getNumElementos()).append("</NumElementos>\n");
            xml.append("    <TimeStamp>").append(peticion.getAtributos().getTimeStamp()).append("</TimeStamp>\n");
            xml.append("    <CodigoCertificado>").append(peticion.getAtributos().getCodigoCertificado()).append("</CodigoCertificado>\n");
            xml.append("  </Atributos>\n");
            xml.append("  <Solicitudes>\n");

            // Afegir les sol·licituds
            if (peticion.getSolicitudes() != null && peticion.getSolicitudes().getSolicitudTransmision() != null) {
                for (SolicitudTransmision st : peticion.getSolicitudes().getSolicitudTransmision()) {
                    xml.append("    <SolicitudTransmision>\n");
                    xml.append("      <DatosGenericos>\n");

                    // Solicitante
                    if (st.getDatosGenericos() != null && st.getDatosGenericos().getSolicitante() != null) {
                        Solicitante sol = st.getDatosGenericos().getSolicitante();
                        xml.append("        <Solicitante>\n");
                        xml.append("          <IdentificadorSolicitante>").append(sol.getIdentificadorSolicitante()).append("</IdentificadorSolicitante>\n");
                        xml.append("          <NombreSolicitante>").append(sol.getNombreSolicitante()).append("</NombreSolicitante>\n");
                        if (sol.getFinalidad() != null) {
                            xml.append("          <Finalidad>").append(sol.getFinalidad()).append("</Finalidad>\n");
                        }
                        xml.append("          <Consentimiento>").append(sol.getConsentimiento()).append("</Consentimiento>\n");
                        xml.append("        </Solicitante>\n");
                    }

                    // Titular
                    if (st.getDatosGenericos() != null && st.getDatosGenericos().getTitular() != null) {
                        Titular tit = st.getDatosGenericos().getTitular();
                        xml.append("        <Titular>\n");
                        if (tit.getTipoDocumentacion() != null) {
                            xml.append("          <TipoDocumentacion>").append(tit.getTipoDocumentacion()).append("</TipoDocumentacion>\n");
                        }
                        if (tit.getDocumentacion() != null) {
                            xml.append("          <Documentacion>").append(tit.getDocumentacion()).append("</Documentacion>\n");
                        }
                        xml.append("        </Titular>\n");
                    }

                    xml.append("      </DatosGenericos>\n");
                    xml.append("    </SolicitudTransmision>\n");
                }
            }

            xml.append("  </Solicitudes>\n");
            xml.append("</Peticion>");

            LOGGER.debug("{} XML de la petició serialitzat correctament", MOCK_PREFIX);
            return xml.toString();

        } catch (Exception e) {
            LOGGER.error("{} Error serialitzant petició a XML: {}", MOCK_PREFIX, e.getMessage(), e);
            return generarXmlMockPeticion(peticion.getAtributos().getIdPeticion());
        }
    }

    /**
     * Genera un XML mock per la petició
     */
    private String generarXmlMockPeticion(String idPeticion) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<Peticion>\n" +
                "  <Atributos>\n" +
                "    <IdPeticion>" + idPeticion + "</IdPeticion>\n" +
                "    <TimeStamp>" + DateUtils.parseISO8601(new Date()) + "</TimeStamp>\n" +
                "  </Atributos>\n" +
                "  <!-- Mock: Petició generada pel mock amb persistència -->\n" +
                "</Peticion>";
    }

    /**
     * Genera un XML mock per la resposta
     */
    private String generarXmlMockResposta(String idSolicitud) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<TransmisionDatos>\n" +
                "  <DatosGenericos>\n" +
                "    <Transmision>\n" +
                "      <IdSolicitud>" + idSolicitud + "</IdSolicitud>\n" +
                "    </Transmision>\n" +
                "  </DatosGenericos>\n" +
                "  <DatosEspecificos>\n" +
                "    <Respuesta>\n" +
                "      <Estado>0000</Estado>\n" +
                "      <Descripcion>Mock: Dades generades pel mock amb persistència</Descripcion>\n" +
                "      <FechaProceso>" + DateUtils.parseISO8601(new Date()) + "</FechaProceso>\n" +
                "    </Respuesta>\n" +
                "  </DatosEspecificos>\n" +
                "</TransmisionDatos>";
    }

    String generarXmlRespostaPerServei(String serveiCodi, String idSolicitud) {
        if (SERVEI_TSD_PRESTACIONS.equals(serveiCodi)) {
            return generarXmlMockRespostaPrestacionsTsd(idSolicitud);
        }
        return null;
    }

    private String generarXmlMockRespostaPrestacionsTsd(String idSolicitud) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<TransmisionDatos>\n" +
                "  <DatosGenericos>\n" +
                "    <Transmision>\n" +
                "      <IdSolicitud>" + idSolicitud + "</IdSolicitud>\n" +
                "    </Transmision>\n" +
                "  </DatosGenericos>\n" +
                "  <DatosEspecificos>\n" +
                "    <consultaPrestacionesResponse>\n" +
                "      <return>\n" +
                "        <codigosRespuesta>0001</codigosRespuesta>\n" +
                "        <literalRespuesta>Coincidencia del IPF y retorno de todos los datos del sistema.</literalRespuesta>\n" +
                "        <listadoIpf>\n" +
                "          <identificativo>\n" +
                "            <numeroDocumento>21043258Y</numeroDocumento>\n" +
                "          </identificativo>\n" +
                "          <listaPrestaciones>\n" +
                "            <prestacion>\n" +
                "              <clavePropiaEntidad>EA00211941800000000000000000000000000001</clavePropiaEntidad>\n" +
                "              <importePrestacion>3.871,16</importePrestacion>\n" +
                "              <literalPrestacion>INGRESO MINIMO VITAL</literalPrestacion>\n" +
                "              <listaComplementos>\n" +
                "                <complemento>\n" +
                "                  <datosComplemento>\n" +
                "                    <claveComplemento>203</claveComplemento>\n" +
                "                    <importeEfectivo>282,35</importeEfectivo>\n" +
                "                    <nombreComplemento>COMP. AYUDA PARA LA INFANCIA (IMV)</nombreComplemento>\n" +
                "                  </datosComplemento>\n" +
                "                </complemento>\n" +
                "              </listaComplementos>\n" +
                "            </prestacion>\n" +
                "            <prestacion>\n" +
                "              <clavePropiaEntidad>EA00211941800000000000000000000000000003</clavePropiaEntidad>\n" +
                "              <importePrestacion>800,50</importePrestacion>\n" +
                "              <literalPrestacion>INGRESO MINIMO VITAL</literalPrestacion>\n" +
                "            </prestacion>\n" +
                "            <prestacion>\n" +
                "              <clavePropiaEntidad>EA00211941800000000000000000000000005001</clavePropiaEntidad>\n" +
                "              <importePrestacion>3.871,16</importePrestacion>\n" +
                "              <literalPrestacion>INGRESO MINIMO VITAL</literalPrestacion>\n" +
                "            </prestacion>\n" +
                "          </listaPrestaciones>\n" +
                "        </listadoIpf>\n" +
                "      </return>\n" +
                "    </consultaPrestacionesResponse>\n" +
                "  </DatosEspecificos>\n" +
                "</TransmisionDatos>";
    }
}
