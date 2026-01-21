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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;

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

            // Serialitzar les dades específiques si existeixen
            if (st.getDatosEspecificos() != null) {
                try {
                    trans.setXmlTransmision(serializarXml(st));
                } catch (Exception e) {
                    LOGGER.warn("{} Error serialitzant dades específiques: {}", MOCK_PREFIX, e.getMessage());
                    trans.setXmlTransmision(generarXmlMockResposta(idSolicitud));
                }
            } else {
                trans.setXmlTransmision(generarXmlMockResposta(idSolicitud));
            }

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
                trans.setXmlTransmision(generarXmlMockResposta(trans.getIdSolicitud()));
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

            // Nota: Les dades específiques estan a transDBItem.getXmlTransmision()
            // però no les parsejem aquí perquè es fan servir directament des de BBDD

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
}
