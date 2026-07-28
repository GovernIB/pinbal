package es.caib.pinbal.logic.service;

import es.caib.pinbal.client.recobriment.model.ScspAtributos;
import es.caib.pinbal.client.recobriment.model.ScspConfirmacionPeticion;
import es.caib.pinbal.client.recobriment.model.ScspDatosGenericos;
import es.caib.pinbal.client.recobriment.model.ScspEmisor;
import es.caib.pinbal.client.recobriment.model.ScspEstado;
import es.caib.pinbal.client.recobriment.model.ScspFuncionario;
import es.caib.pinbal.client.recobriment.model.ScspJustificante;
import es.caib.pinbal.client.recobriment.model.ScspPeticion;
import es.caib.pinbal.client.recobriment.model.ScspProcedimiento;
import es.caib.pinbal.client.recobriment.model.ScspRespuesta;
import es.caib.pinbal.client.recobriment.model.ScspSolicitante;
import es.caib.pinbal.client.recobriment.model.ScspSolicitante.ScspConsentimiento;
import es.caib.pinbal.client.recobriment.model.ScspSolicitud;
import es.caib.pinbal.client.recobriment.model.ScspTitular;
import es.caib.pinbal.client.recobriment.model.ScspTitular.ScspTipoDocumentacion;
import es.caib.pinbal.client.recobriment.model.ScspTransmision;
import es.caib.pinbal.client.recobriment.v2.DadaTipusEnum;
import es.caib.pinbal.client.recobriment.v2.PeticioRespostaAsincrona;
import es.caib.pluginsib.arxiu.api.Document;
import es.caib.pluginsib.arxiu.api.DocumentMetadades;
import es.caib.pinbal.logic.helper.PluginHelper;
import es.caib.pinbal.logic.helper.RecobrimentHelper;
import es.caib.pinbal.logic.helper.RecobrimentV2Helper;
import es.caib.pinbal.logic.intf.dto.ArbreDto;
import es.caib.pinbal.logic.intf.dto.DadaEspecificaDto;
import es.caib.pinbal.logic.intf.dto.EstatTipus;
import es.caib.pinbal.logic.intf.dto.JustificantDto;
import es.caib.pinbal.logic.intf.dto.NodeDto;
import es.caib.pinbal.logic.intf.service.DadesExternesService;
import es.caib.pinbal.logic.intf.service.ServeiService;
import es.caib.pinbal.logic.intf.service.exception.AccessDenegatException;
import es.caib.pinbal.logic.intf.service.exception.ConsultaNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.RecobrimentScspException;
import es.caib.pinbal.logic.intf.service.exception.RecobrimentScspValidationException;
import es.caib.pinbal.logic.intf.service.exception.ServeiNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.ServeiRespostaNotFoundException;
import es.caib.pinbal.persist.entity.Consulta;
import es.caib.pinbal.persist.entity.ServeiConfig;
import es.caib.pinbal.persist.entity.Usuari;
import es.caib.pinbal.persist.repository.ConsultaRepository;
import es.caib.pinbal.persist.repository.EntitatRepository;
import es.caib.pinbal.persist.repository.HistoricConsultaRepository;
import es.caib.pinbal.persist.repository.ProcedimentRepository;
import es.caib.pinbal.persist.repository.ServeiCampRepository;
import es.caib.pinbal.persist.repository.ServeiConfigRepository;
import es.caib.pinbal.persist.repository.ServeiRepository;
import es.scsp.bean.common.confirmacion.ConfirmacionPeticion;
import es.scsp.bean.common.peticion.Peticion;
import es.scsp.bean.common.respuesta.Atributos;
import es.scsp.bean.common.respuesta.Consentimiento;
import es.scsp.bean.common.respuesta.DatosGenericos;
import es.scsp.bean.common.respuesta.Emisor;
import es.scsp.bean.common.respuesta.Estado;
import es.scsp.bean.common.respuesta.Funcionario;
import es.scsp.bean.common.respuesta.Procedimiento;
import es.scsp.bean.common.respuesta.Respuesta;
import es.scsp.bean.common.respuesta.Solicitante;
import es.scsp.bean.common.respuesta.Titular;
import es.scsp.bean.common.respuesta.Transmision;
import es.scsp.bean.common.respuesta.TransmisionDatos;
import es.scsp.bean.common.respuesta.Transmisiones;
import es.scsp.bean.common.respuesta.TipoDocumentacion;
import es.scsp.common.exceptions.ScspException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Tests complementaris de {@link RecobrimentServiceImplTest} centrats en els mètodes
 * i branques que aquell fitxer no exercitava: l'API antiga basada en {@code ScspPeticion}
 * (peticionSincrona/peticionAsincrona/getRespuesta/getJustificante* "Spanish spelling"),
 * getDadesEspecifiquesByServeiResposta, i les branques no cobertes de la resta de mètodes v2.
 */
@ExtendWith(MockitoExtension.class)
public class RecobrimentServiceImplTopUpTest {

    @InjectMocks
    private RecobrimentServiceImpl recobrimentServiceImpl;

    @Mock
    private ConsultaRepository consultaRepository;

    @Mock
    private HistoricConsultaRepository historicConsultaRepository;

    @Mock
    private EntitatRepository entitatRepository;

    @Mock
    private ProcedimentRepository procedimentRepository;

    @Mock
    private ServeiCampRepository serveiCampRepository;

    @Mock
    private ServeiRepository serveiRepository;

    @Mock
    private ServeiConfigRepository serveiConfigRepository;

    @Mock
    private RecobrimentHelper recobrimentHelper;

    @Mock
    private RecobrimentV2Helper recobrimentV2Helper;

    @Mock
    private PluginHelper pluginHelper;

    @Mock
    private ServeiService serveiService;

    @Mock
    private DadesExternesService dadesExternesService;

    @BeforeEach
    public void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void mockAuthentication(String username) {
        Authentication authentication = mock(Authentication.class);
        lenient().when(authentication.getName()).thenReturn(username);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private Consulta mockConsultaAmbCreador(String creatorUsername) {
        Consulta consulta = mock(Consulta.class);
        Usuari usuari = mock(Usuari.class);
        lenient().when(consulta.getCreatedBy()).thenReturn(Optional.of(usuari));
        lenient().when(usuari.getCodi()).thenReturn(creatorUsername);
        return consulta;
    }

    private Element buildElement(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document document = builder.parse(new java.io.ByteArrayInputStream(xml.getBytes()));
        return document.getDocumentElement();
    }

    // ==========================================================================
    // TESTS API ANTIGA: peticionSincrona(ScspPeticion)
    // ==========================================================================

    @Test
    public void testOldPeticionSincrona_Success_FullMapping() throws Exception {
        ScspPeticion peticio = new ScspPeticion();
        ScspAtributos atributos = new ScspAtributos();
        atributos.setIdPeticion("PET-1");
        atributos.setNumElementos("2");
        atributos.setTimeStamp("2024-01-01");
        atributos.setCodigoCertificado("CERT-1");
        ScspEstado estadoIn = new ScspEstado();
        estadoIn.setCodigoEstado("00");
        estadoIn.setLiteralError(null);
        estadoIn.setCodigoEstadoSecundario("SEC");
        estadoIn.setTiempoEstimadoRespuesta(10);
        atributos.setEstado(estadoIn);
        peticio.setAtributos(atributos);

        ScspSolicitud s1 = new ScspSolicitud();
        ScspDatosGenericos dg1 = new ScspDatosGenericos();
        ScspEmisor emisor1 = new ScspEmisor();
        emisor1.setNifEmisor("NIFE1");
        emisor1.setNombreEmisor("Emissor 1");
        dg1.setEmisor(emisor1);
        ScspSolicitante sol1 = new ScspSolicitante();
        ScspProcedimiento proc1 = new ScspProcedimiento();
        proc1.setCodProcedimiento("PR1");
        proc1.setNombreProcedimiento("Procediment 1");
        sol1.setProcedimiento(proc1);
        ScspFuncionario func1 = new ScspFuncionario();
        func1.setNombreCompletoFuncionario("Func Complet");
        func1.setNifFuncionario("NIFF1");
        sol1.setFuncionario(func1);
        sol1.setUnidadTramitadora("UNITAT1");
        sol1.setCodigoUnidadTramitadora("COD1");
        sol1.setIdentificadorSolicitante("IDSOL1");
        sol1.setNombreSolicitante("Sol·licitant 1");
        sol1.setIdExpediente("EXP1");
        sol1.setFinalidad("Finalitat1");
        sol1.setConsentimiento(ScspConsentimiento.Si);
        dg1.setSolicitante(sol1);
        ScspTitular tit1 = new ScspTitular();
        tit1.setTipoDocumentacion(ScspTipoDocumentacion.NIF);
        tit1.setDocumentacion("12345678A");
        tit1.setNombreCompleto("Nom Complet 1");
        tit1.setNombre("Nom1");
        tit1.setApellido1("Ap1");
        tit1.setApellido2("Ap2");
        dg1.setTitular(tit1);
        ScspTransmision trans1 = new ScspTransmision();
        trans1.setCodigoCertificado("CERT-1");
        trans1.setIdSolicitud("IDSOLICITUD1");
        trans1.setIdTransmision("IDTRANS1");
        trans1.setFechaGeneracion("2024-01-01");
        dg1.setTransmision(trans1);
        s1.setDatosGenericos(dg1);
        s1.setDatosEspecificos("<root><child>v</child></root>");

        ScspSolicitud s2 = new ScspSolicitud();
        ScspDatosGenericos dg2 = new ScspDatosGenericos();
        ScspSolicitante sol2 = new ScspSolicitante();
        sol2.setConsentimiento(ScspConsentimiento.Ley);
        dg2.setSolicitante(sol2);
        ScspTitular tit2 = new ScspTitular();
        tit2.setTipoDocumentacion(ScspTipoDocumentacion.CIF);
        dg2.setTitular(tit2);
        s2.setDatosGenericos(dg2);
        // sense datosEspecificos -> no crida stringToElement

        List<ScspSolicitud> solicituds = new ArrayList<>();
        solicituds.add(s1);
        solicituds.add(s2);
        peticio.setSolicitudes(solicituds);

        // Respuesta simulada (per exercitar toScspRespuesta)
        Respuesta respuesta = new Respuesta();
        Atributos atrOut = new Atributos();
        atrOut.setIdPeticion("PET-1");
        atrOut.setNumElementos(2);
        atrOut.setTimeStamp("2024-01-02");
        atrOut.setCodigoCertificado("CERT-1");
        Estado estadoOut = new Estado();
        estadoOut.setCodigoEstado("00");
        estadoOut.setLiteralError(null);
        estadoOut.setLiteralErrorSecundario("SECOUT");
        estadoOut.setTiempoEstimadoRespuesta(5);
        atrOut.setEstado(estadoOut);
        respuesta.setAtributos(atrOut);

        Transmisiones transmisionesOut = new Transmisiones();
        TransmisionDatos t1 = new TransmisionDatos();
        DatosGenericos dgOut1 = new DatosGenericos();
        Emisor emisorOut1 = new Emisor();
        emisorOut1.setNifEmisor("NIFE-OUT1");
        emisorOut1.setNombreEmisor("Emissor Out 1");
        dgOut1.setEmisor(emisorOut1);
        Solicitante solOut1 = new Solicitante();
        Procedimiento procOut1 = new Procedimiento();
        procOut1.setCodProcedimiento("PROUT1");
        procOut1.setNombreProcedimiento("Procediment Out 1");
        solOut1.setProcedimiento(procOut1);
        Funcionario funcOut1 = new Funcionario();
        funcOut1.setNombreCompletoFuncionario("Func Out Complet");
        funcOut1.setNifFuncionario("NIFFOUT1");
        solOut1.setFuncionario(funcOut1);
        solOut1.setUnidadTramitadora("UNITATOUT1");
        solOut1.setCodigoUnidadTramitadora("CODOUT1");
        solOut1.setIdentificadorSolicitante("IDSOLOUT1");
        solOut1.setNombreSolicitante("Sol·licitant Out 1");
        solOut1.setIdExpediente("EXPOUT1");
        solOut1.setFinalidad("FinalitatOut1");
        solOut1.setConsentimiento(Consentimiento.SI);
        dgOut1.setSolicitante(solOut1);
        Titular titOut1 = new Titular();
        titOut1.setTipoDocumentacion(TipoDocumentacion.CIF);
        titOut1.setDocumentacion("DOCOUT1");
        titOut1.setNombreCompleto("Nom Complet Out 1");
        titOut1.setNombre("NomOut1");
        titOut1.setApellido1("ApOut1");
        titOut1.setApellido2("ApOut2");
        dgOut1.setTitular(titOut1);
        Transmision transOut1 = new Transmision();
        transOut1.setCodigoCertificado("CERT-OUT1");
        transOut1.setIdSolicitud("IDSOLICITUDOUT1");
        transOut1.setIdTransmision("IDTRANSOUT1");
        transOut1.setFechaGeneracion("2024-01-03");
        dgOut1.setTransmision(transOut1);
        t1.setDatosGenericos(dgOut1);
        t1.setDatosEspecificos(buildElement("<resposta><valor>1</valor></resposta>"));

        TransmisionDatos t2 = new TransmisionDatos();
        DatosGenericos dgOut2 = new DatosGenericos();
        Solicitante solOut2 = new Solicitante();
        solOut2.setConsentimiento(Consentimiento.LEY);
        dgOut2.setSolicitante(solOut2);
        Titular titOut2 = new Titular();
        titOut2.setTipoDocumentacion(TipoDocumentacion.DNI);
        dgOut2.setTitular(titOut2);
        t2.setDatosGenericos(dgOut2);
        t2.setDatosEspecificos("no és un node");

        transmisionesOut.getTransmisionDatos().add(t1);
        transmisionesOut.getTransmisionDatos().add(t2);
        respuesta.setTransmisiones(transmisionesOut);

        when(recobrimentHelper.peticionSincrona(any(Peticion.class), eq(false))).thenReturn(respuesta);

        ScspRespuesta result = recobrimentServiceImpl.peticionSincrona(peticio);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("PET-1", result.getAtributos().getIdPeticion());
        Assertions.assertEquals("2", result.getAtributos().getNumElementos());
        Assertions.assertEquals("00", result.getAtributos().getEstado().getCodigoEstado());
        Assertions.assertEquals("SECOUT", result.getAtributos().getEstado().getLiteralErrorSec());

        Assertions.assertEquals(2, result.getTransmisiones().size());
        // Nota: toScspRespuesta() compara transmisionDatos.*.getTipoDocumentacion()/getConsentimiento()
        // (tipus es.scsp.bean.common.respuesta.*) contra les constants es.scsp.bean.common.peticion.*
        // (l'únic import present a la classe), per la qual cosa aquestes comparacions mai coincideixen
        // i els camps mapejats queden sempre a null. Es documenta aquí el comportament real observat.
        Assertions.assertNull(result.getTransmisiones().get(0).getDatosGenericos().getTitular().getTipoDocumentacion());
        Assertions.assertTrue(result.getTransmisiones().get(0).getDatosEspecificos().contains("valor"));
        Assertions.assertNull(result.getTransmisiones().get(0).getDatosGenericos().getSolicitante().getConsentimiento());

        Assertions.assertNull(result.getTransmisiones().get(1).getDatosGenericos().getTitular().getTipoDocumentacion());
        Assertions.assertNull(result.getTransmisiones().get(1).getDatosGenericos().getSolicitante().getConsentimiento());
        Assertions.assertNull(result.getTransmisiones().get(1).getDatosEspecificos());

        verify(recobrimentHelper, times(1)).peticionSincrona(any(Peticion.class), eq(false));
    }

    @Test
    public void testOldPeticionSincrona_ValidationException() throws Exception {
        ScspPeticion peticio = new ScspPeticion();
        when(recobrimentHelper.peticionSincrona(any(), anyBoolean()))
                .thenThrow(new ScspException("Error de validació", RecobrimentHelper.ERROR_CODE_SCSP_VALIDATION));

        RecobrimentScspException ex = assertThrows(RecobrimentScspValidationException.class, () ->
                recobrimentServiceImpl.peticionSincrona(peticio));
        Assertions.assertTrue(ex.getMessage().contains("Error de validació"));
    }

    @Test
    public void testOldPeticionSincrona_OtherScspException() throws Exception {
        ScspPeticion peticio = new ScspPeticion();
        when(recobrimentHelper.peticionSincrona(any(), anyBoolean()))
                .thenThrow(new ScspException("Altre error", "9999"));

        RecobrimentScspException ex = assertThrows(RecobrimentScspException.class, () ->
                recobrimentServiceImpl.peticionSincrona(peticio));
        Assertions.assertFalse(ex instanceof RecobrimentScspValidationException);
        Assertions.assertTrue(ex.getMessage().contains("Altre error"));
    }

    @Test
    public void testOldPeticionSincrona_InvalidXmlDatosEspecificos_ThrowsRecobrimentScspException() {
        ScspPeticion peticio = new ScspPeticion();
        ScspSolicitud s1 = new ScspSolicitud();
        s1.setDatosEspecificos("<no-tancat");
        List<ScspSolicitud> solicituds = new ArrayList<>();
        solicituds.add(s1);
        peticio.setSolicitudes(solicituds);

        assertThrows(RecobrimentScspException.class, () ->
                recobrimentServiceImpl.peticionSincrona(peticio));
    }

    @Test
    public void testOldPeticionSincrona_NullPeticion_ReturnsNull() throws Exception {
        when(recobrimentHelper.peticionSincrona(nullable(Peticion.class), eq(false))).thenReturn(null);

        ScspRespuesta result = recobrimentServiceImpl.peticionSincrona((ScspPeticion) null);

        Assertions.assertNull(result);
    }

    // ==========================================================================
    // TESTS API ANTIGA: peticionAsincrona(ScspPeticion)
    // ==========================================================================

    @Test
    public void testOldPeticionAsincrona_Success_MultipleSyntheticTransmision() throws Exception {
        ScspPeticion peticio = new ScspPeticion();
        ScspAtributos atributos = new ScspAtributos();
        atributos.setIdPeticion("PET-ASYNC-1");
        atributos.setNumElementos("1");
        atributos.setCodigoCertificado("CERT-ASYNC");
        peticio.setAtributos(atributos);

        ScspSolicitud s1 = new ScspSolicitud();
        ScspDatosGenericos dg1 = new ScspDatosGenericos();
        // Sense transmision -> ha de generar-se sintèticament perquè multiple=true
        s1.setDatosGenericos(dg1);
        s1.setDatosEspecificos("<a><b>1</b></a>");
        List<ScspSolicitud> solicituds = new ArrayList<>();
        solicituds.add(s1);
        peticio.setSolicitudes(solicituds);

        ConfirmacionPeticion confirmacio = new ConfirmacionPeticion();
        es.scsp.bean.common.confirmacion.Atributos atrOut = new es.scsp.bean.common.confirmacion.Atributos();
        atrOut.setIdPeticion("PET-ASYNC-1");
        atrOut.setNumElementos(1);
        atrOut.setTimeStamp("2024-01-05");
        atrOut.setCodigoCertificado("CERT-ASYNC");
        es.scsp.bean.common.confirmacion.Estado estadoOut = new es.scsp.bean.common.confirmacion.Estado();
        estadoOut.setCodigoEstado("00");
        estadoOut.setLiteralError(null);
        estadoOut.setCodigoEstadoSecundario("SECASYNC");
        estadoOut.setTiempoEstimadoRespuesta(20);
        atrOut.setEstado(estadoOut);
        confirmacio.setAtributos(atrOut);

        when(recobrimentHelper.peticionAsincrona(any(Peticion.class), eq(false))).thenReturn(confirmacio);

        ScspConfirmacionPeticion result = recobrimentServiceImpl.peticionAsincrona(peticio);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("PET-ASYNC-1", result.getAtributos().getIdPeticion());
        Assertions.assertEquals("1", result.getAtributos().getNumElementos());
        Assertions.assertEquals("CERT-ASYNC", result.getAtributos().getCodigoCertificado());
        Assertions.assertEquals("00", result.getAtributos().getEstado().getCodigoEstado());
        Assertions.assertEquals("SECASYNC", result.getAtributos().getEstado().getLiteralErrorSec());

        verify(recobrimentHelper, times(1)).peticionAsincrona(any(Peticion.class), eq(false));
    }

    @Test
    public void testOldPeticionAsincrona_ScspException() throws Exception {
        ScspPeticion peticio = new ScspPeticion();
        when(recobrimentHelper.peticionAsincrona(any(), anyBoolean()))
                .thenThrow(new ScspException("Error async", "1111"));

        assertThrows(RecobrimentScspException.class, () ->
                recobrimentServiceImpl.peticionAsincrona(peticio));
    }

    // ==========================================================================
    // TESTS API ANTIGA: getRespuesta(String) -> ScspRespuesta
    // ==========================================================================

    @Test
    public void testOldGetRespuesta_Success() throws Exception {
        String idPeticion = "IDPET-OLD";
        Respuesta respuesta = new Respuesta();
        when(recobrimentHelper.getRespuesta(idPeticion)).thenReturn(respuesta);

        ScspRespuesta result = recobrimentServiceImpl.getRespuesta(idPeticion);

        Assertions.assertNotNull(result);
        verify(recobrimentHelper, times(1)).getRespuesta(idPeticion);
    }

    @Test
    public void testOldGetRespuesta_ScspException() throws Exception {
        String idPeticion = "IDPET-OLD-2";
        when(recobrimentHelper.getRespuesta(idPeticion)).thenThrow(new ScspException("Error resposta", "code"));

        assertThrows(RecobrimentScspException.class, () ->
                recobrimentServiceImpl.getRespuesta(idPeticion));
    }

    // ==========================================================================
    // TESTS API ANTIGA: getJustificante / getJustificanteImprimible / getJustificanteCsv / getJustificanteUuid
    // ==========================================================================

    @Test
    public void testOldGetJustificante_Success() throws Exception {
        String idPeticion = "P1", idSolicitud = "S1";
        JustificantDto dto = mock(JustificantDto.class);
        when(dto.getNom()).thenReturn("nom.pdf");
        when(dto.getContentType()).thenReturn("application/pdf");
        when(dto.getContingut()).thenReturn(new byte[]{1, 2});
        when(recobrimentHelper.getJustificante(idPeticion, idSolicitud, false, true)).thenReturn(dto);

        ScspJustificante result = recobrimentServiceImpl.getJustificante(idPeticion, idSolicitud);

        Assertions.assertEquals("nom.pdf", result.getNom());
        Assertions.assertEquals("application/pdf", result.getContentType());
        Assertions.assertArrayEquals(new byte[]{1, 2}, result.getContingut());
    }

    @Test
    public void testOldGetJustificante_ScspException() throws Exception {
        String idPeticion = "P1", idSolicitud = "S1";
        when(recobrimentHelper.getJustificante(idPeticion, idSolicitud, false, true))
                .thenThrow(new ScspException("Error justificant", "code"));

        assertThrows(RecobrimentScspException.class, () ->
                recobrimentServiceImpl.getJustificante(idPeticion, idSolicitud));
    }

    @Test
    public void testOldGetJustificanteImprimible_Success() throws Exception {
        String idPeticion = "P2", idSolicitud = "S2";
        JustificantDto dto = mock(JustificantDto.class);
        when(dto.getNom()).thenReturn("imprimible.pdf");
        when(dto.getContentType()).thenReturn("application/pdf");
        when(dto.getContingut()).thenReturn(new byte[]{3, 4});
        when(recobrimentHelper.getJustificante(idPeticion, idSolicitud, true, true)).thenReturn(dto);

        ScspJustificante result = recobrimentServiceImpl.getJustificanteImprimible(idPeticion, idSolicitud);

        Assertions.assertEquals("imprimible.pdf", result.getNom());
        Assertions.assertArrayEquals(new byte[]{3, 4}, result.getContingut());
    }

    @Test
    public void testOldGetJustificanteImprimible_ScspException() throws Exception {
        String idPeticion = "P2", idSolicitud = "S2";
        when(recobrimentHelper.getJustificante(idPeticion, idSolicitud, true, true))
                .thenThrow(new ScspException("Error imprimible", "code"));

        assertThrows(RecobrimentScspException.class, () ->
                recobrimentServiceImpl.getJustificanteImprimible(idPeticion, idSolicitud));
    }

    @Test
    public void testOldGetJustificanteCsv_Success() throws Exception {
        String idPeticion = "P3", idSolicitud = "S3";
        JustificantDto dto = mock(JustificantDto.class);
        when(dto.getArxiuCsv()).thenReturn("CSV-OLD");
        when(recobrimentHelper.getJustificante(idPeticion, idSolicitud, true, false)).thenReturn(dto);

        String result = recobrimentServiceImpl.getJustificanteCsv(idPeticion, idSolicitud);

        Assertions.assertEquals("CSV-OLD", result);
    }

    @Test
    public void testOldGetJustificanteCsv_ScspException() throws Exception {
        String idPeticion = "P3", idSolicitud = "S3";
        when(recobrimentHelper.getJustificante(idPeticion, idSolicitud, true, false))
                .thenThrow(new ScspException("Error csv", "code"));

        assertThrows(RecobrimentScspException.class, () ->
                recobrimentServiceImpl.getJustificanteCsv(idPeticion, idSolicitud));
    }

    @Test
    public void testOldGetJustificanteUuid_Success() throws Exception {
        String idPeticion = "P4", idSolicitud = "S4";
        JustificantDto dto = mock(JustificantDto.class);
        when(dto.getArxiuUuid()).thenReturn("UUID-OLD");
        when(recobrimentHelper.getJustificante(idPeticion, idSolicitud, false, false)).thenReturn(dto);

        String result = recobrimentServiceImpl.getJustificanteUuid(idPeticion, idSolicitud);

        Assertions.assertEquals("UUID-OLD", result);
    }

    @Test
    public void testOldGetJustificanteUuid_ScspException() throws Exception {
        String idPeticion = "P4", idSolicitud = "S4";
        when(recobrimentHelper.getJustificante(idPeticion, idSolicitud, false, false))
                .thenThrow(new ScspException("Error uuid", "code"));

        assertThrows(RecobrimentScspException.class, () ->
                recobrimentServiceImpl.getJustificanteUuid(idPeticion, idSolicitud));
    }

    // ==========================================================================
    // TESTS getDadesEspecifiquesByServeiResposta
    // ==========================================================================

    @Test
    public void testGetDadesEspecifiquesByServeiResposta_ServeiNotFound() {
        String serveiCodi = "SRV-NF";
        when(serveiConfigRepository.findByServei(serveiCodi)).thenReturn(null);

        assertThrows(ServeiNotFoundException.class, () ->
                recobrimentServiceImpl.getDadesEspecifiquesByServeiResposta(serveiCodi));
    }

    @Test
    public void testGetDadesEspecifiquesByServeiResposta_ArrelRespostaPathNull() {
        String serveiCodi = "SRV-NULL";
        ServeiConfig serveiConfig = new ServeiConfig();
        when(serveiConfigRepository.findByServei(serveiCodi)).thenReturn(serveiConfig);

        assertThrows(ServeiRespostaNotFoundException.class, () ->
                recobrimentServiceImpl.getDadesEspecifiquesByServeiResposta(serveiCodi));
    }

    @Test
    public void testGetDadesEspecifiquesByServeiResposta_ArrelRespostaPathEmpty() {
        String serveiCodi = "SRV-EMPTY";
        ServeiConfig serveiConfig = new ServeiConfig();
        serveiConfig.setArrelRespostaPath("");
        when(serveiConfigRepository.findByServei(serveiCodi)).thenReturn(serveiConfig);

        assertThrows(ServeiRespostaNotFoundException.class, () ->
                recobrimentServiceImpl.getDadesEspecifiquesByServeiResposta(serveiCodi));
    }

    @Test
    public void testGetDadesEspecifiquesByServeiResposta_NodeNotFound() throws Exception {
        String serveiCodi = "SRV-NODE-NF";
        ServeiConfig serveiConfig = new ServeiConfig();
        serveiConfig.setArrelRespostaPath("No/Existeix");
        when(serveiConfigRepository.findByServei(serveiCodi)).thenReturn(serveiConfig);

        DadaEspecificaDto arrelDades = new DadaEspecificaDto();
        arrelDades.setPath(new String[0]);
        arrelDades.setNom("Root");
        NodeDto<DadaEspecificaDto> arrelNode = new NodeDto<>(arrelDades);
        ArbreDto<DadaEspecificaDto> arbre = new ArbreDto<>();
        arbre.setArrel(arrelNode);

        when(serveiService.generarArbreDadesEspecifiques(serveiCodi)).thenReturn(arbre);

        assertThrows(ServeiRespostaNotFoundException.class, () ->
                recobrimentServiceImpl.getDadesEspecifiquesByServeiResposta(serveiCodi));
    }

    private NodeDto<DadaEspecificaDto> node(String[] path, String nom, String tipus) {
        DadaEspecificaDto dades = new DadaEspecificaDto();
        dades.setPath(path);
        dades.setNom(nom);
        dades.setTipus(tipus);
        return new NodeDto<>(dades);
    }

    @Test
    public void testGetDadesEspecifiquesByServeiResposta_Success() throws Exception {
        String serveiCodi = "SRV-OK";
        ServeiConfig serveiConfig = new ServeiConfig();
        serveiConfig.setArrelRespostaPath("Root/Mid");
        when(serveiConfigRepository.findByServei(serveiCodi)).thenReturn(serveiConfig);

        NodeDto<DadaEspecificaDto> root = node(new String[0], "Root", null);
        NodeDto<DadaEspecificaDto> mid = node(new String[]{"Root"}, "Mid", "Complex()");
        root.addFill(mid);

        mid.addFill(node(new String[]{"Root", "Mid"}, "CampDouble", "Double(2)"));
        mid.addFill(node(new String[]{"Root", "Mid"}, "CampLong", "Long(0)"));
        mid.addFill(node(new String[]{"Root", "Mid"}, "CampDate", "Date()"));
        mid.addFill(node(new String[]{"Root", "Mid"}, "CampBoolean", "Boolean()"));
        mid.addFill(node(new String[]{"Root", "Mid"}, "CampDocIdentitat", "DocIdentitat()"));
        mid.addFill(node(new String[]{"Root", "Mid"}, "CampFile", "File()"));
        mid.addFill(node(new String[]{"Root", "Mid"}, "CampEnum", "Enum(A,B)"));
        mid.addFill(node(new String[]{"Root", "Mid"}, "CampComplex", "Complex()"));
        mid.addFill(node(new String[]{"Root", "Mid"}, "CampAltre", "Altre(9)"));
        mid.addFill(node(new String[]{"Root", "Mid"}, "CampSenseTipus", null));

        ArbreDto<DadaEspecificaDto> arbre = new ArbreDto<>();
        arbre.setArrel(root);
        when(serveiService.generarArbreDadesEspecifiques(serveiCodi)).thenReturn(arbre);

        List<es.caib.pinbal.client.recobriment.v2.DadaEspecificaBasic> result =
                recobrimentServiceImpl.getDadesEspecifiquesByServeiResposta(serveiCodi);

        Assertions.assertNotNull(result);
        // "Mid" + 10 fills
        Assertions.assertEquals(11, result.size());

        es.caib.pinbal.client.recobriment.v2.DadaEspecificaBasic double_ = trobaPerNom(result, "CampDouble");
        Assertions.assertEquals(DadaTipusEnum.NUMERIC, double_.getTipus());
        Assertions.assertEquals("Decimal", double_.getFormat());

        es.caib.pinbal.client.recobriment.v2.DadaEspecificaBasic long_ = trobaPerNom(result, "CampLong");
        Assertions.assertEquals(DadaTipusEnum.NUMERIC, long_.getTipus());
        Assertions.assertEquals("Integer", long_.getFormat());

        es.caib.pinbal.client.recobriment.v2.DadaEspecificaBasic date_ = trobaPerNom(result, "CampDate");
        Assertions.assertEquals(DadaTipusEnum.DATE, date_.getTipus());

        es.caib.pinbal.client.recobriment.v2.DadaEspecificaBasic bool_ = trobaPerNom(result, "CampBoolean");
        Assertions.assertEquals(DadaTipusEnum.BOOLEAN, bool_.getTipus());

        es.caib.pinbal.client.recobriment.v2.DadaEspecificaBasic docId_ = trobaPerNom(result, "CampDocIdentitat");
        Assertions.assertEquals(DadaTipusEnum.DOC_IDENTITAT, docId_.getTipus());

        es.caib.pinbal.client.recobriment.v2.DadaEspecificaBasic file_ = trobaPerNom(result, "CampFile");
        Assertions.assertEquals(DadaTipusEnum.FILE, file_.getTipus());

        es.caib.pinbal.client.recobriment.v2.DadaEspecificaBasic enum_ = trobaPerNom(result, "CampEnum");
        Assertions.assertEquals(DadaTipusEnum.ENUM, enum_.getTipus());
        Assertions.assertEquals("[A,B]", enum_.getFormat());

        es.caib.pinbal.client.recobriment.v2.DadaEspecificaBasic complex_ = trobaPerNom(result, "CampComplex");
        Assertions.assertEquals(DadaTipusEnum.COMPLEX, complex_.getTipus());

        es.caib.pinbal.client.recobriment.v2.DadaEspecificaBasic altre_ = trobaPerNom(result, "CampAltre");
        Assertions.assertEquals(DadaTipusEnum.TEXT, altre_.getTipus());
        Assertions.assertEquals("MaxSize(9)", altre_.getFormat());

        es.caib.pinbal.client.recobriment.v2.DadaEspecificaBasic senseTipus_ = trobaPerNom(result, "CampSenseTipus");
        Assertions.assertEquals(DadaTipusEnum.TEXT, senseTipus_.getTipus());
        Assertions.assertNull(senseTipus_.getFormat());
    }

    private es.caib.pinbal.client.recobriment.v2.DadaEspecificaBasic trobaPerNom(
            List<es.caib.pinbal.client.recobriment.v2.DadaEspecificaBasic> llista, String nom) {
        for (es.caib.pinbal.client.recobriment.v2.DadaEspecificaBasic d : llista) {
            if (d.getNom().equals(nom)) {
                return d;
            }
        }
        Assertions.fail("No s'ha trobat cap dada amb nom " + nom);
        return null;
    }

    // ==========================================================================
    // TESTS getResposta (v2) — branques encara no cobertes
    // ==========================================================================

    @Test
    public void testGetRespostaV2_ProcessantMultiple() throws Exception {
        String idPeticion = "IDPET-PROC";
        Consulta consulta = mock(Consulta.class);
        when(consulta.getEstat()).thenReturn(EstatTipus.Processant);
        when(consulta.isMultiple()).thenReturn(true);
        when(recobrimentV2Helper.getConsultaBypeticioId(idPeticion)).thenReturn(consulta);

        es.caib.pinbal.client.recobriment.v2.PeticioRespostaAsincrona expected =
                PeticioRespostaAsincrona.builder().build();
        when(recobrimentV2Helper.toRespostaAsincrona(consulta)).thenReturn(expected);

        es.caib.pinbal.client.recobriment.v2.PeticioRespostaAsincrona result =
                recobrimentServiceImpl.getResposta(idPeticion);

        Assertions.assertSame(expected, result);
        verify(recobrimentHelper, never()).getRespuesta(anyString());
    }

    @Test
    public void testGetRespostaV2_EstatError_RecuperacioCorrecta() throws Exception {
        String idPeticion = "IDPET-ERR-OK";
        Consulta consulta = mock(Consulta.class);
        when(consulta.getEstat()).thenReturn(EstatTipus.Error);
        when(recobrimentV2Helper.getConsultaBypeticioId(idPeticion)).thenReturn(consulta);

        Respuesta respuesta = new Respuesta();
        when(recobrimentHelper.getRespuesta(idPeticion)).thenReturn(respuesta);

        es.caib.pinbal.client.recobriment.v2.PeticioRespostaAsincrona expected =
                PeticioRespostaAsincrona.builder().build();
        when(recobrimentV2Helper.toRespostaAsincrona(respuesta)).thenReturn(expected);

        es.caib.pinbal.client.recobriment.v2.PeticioRespostaAsincrona result =
                recobrimentServiceImpl.getResposta(idPeticion);

        Assertions.assertSame(expected, result);
    }

    @Test
    public void testGetRespostaV2_EstatError_RecuperacioFalla() throws Exception {
        String idPeticion = "IDPET-ERR-FAIL";
        Consulta consulta = mock(Consulta.class);
        when(consulta.getEstat()).thenReturn(EstatTipus.Error);
        when(recobrimentV2Helper.getConsultaBypeticioId(idPeticion)).thenReturn(consulta);

        when(recobrimentHelper.getRespuesta(idPeticion)).thenThrow(new ScspException("no disponible", "code"));

        es.caib.pinbal.client.recobriment.v2.PeticioRespostaAsincrona expected =
                PeticioRespostaAsincrona.builder().build();
        when(recobrimentV2Helper.toRespostaAsincrona(consulta)).thenReturn(expected);

        es.caib.pinbal.client.recobriment.v2.PeticioRespostaAsincrona result =
                recobrimentServiceImpl.getResposta(idPeticion);

        Assertions.assertSame(expected, result);
    }

    // ==========================================================================
    // TESTS getJustificant / getJustificantImprimible (v2) — AccessDenegatException
    // ==========================================================================

    @Test
    public void testGetJustificantV2_AccessDenegat() {
        String idPeticion = "IDP", idSolicitud = "IDS";
        Consulta consulta = mockConsultaAmbCreador("usuariCreador");
        mockAuthentication("usuariDiferent");
        when(consultaRepository.findByScspPeticionIdAndScspSolicitudId(idPeticion, idSolicitud)).thenReturn(consulta);

        assertThrows(AccessDenegatException.class, () ->
                recobrimentServiceImpl.getJustificant(idPeticion, idSolicitud));

        verifyNoInteractions(recobrimentHelper);
    }

    @Test
    public void testGetJustificantImprimibleV2_AccessDenegat() {
        String idPeticion = "IDP2", idSolicitud = "IDS2";
        Consulta consulta = mockConsultaAmbCreador("usuariCreador2");
        mockAuthentication("usuariDiferent2");
        when(consultaRepository.findByScspPeticionIdAndScspSolicitudId(idPeticion, idSolicitud)).thenReturn(consulta);

        assertThrows(AccessDenegatException.class, () ->
                recobrimentServiceImpl.getJustificantImprimible(idPeticion, idSolicitud));

        verifyNoInteractions(recobrimentHelper);
    }

    // ==========================================================================
    // TESTS getJustificantCsv (v2)
    // ==========================================================================

    @Test
    public void testGetJustificantCsvV2_ConsultaNotFound() {
        String idPeticion = "CSV-NF", idSolicitud = "S";
        when(consultaRepository.findByScspPeticionIdAndScspSolicitudId(idPeticion, idSolicitud)).thenReturn(null);
        when(historicConsultaRepository.findByScspPeticionIdAndScspSolicitudId(idPeticion, idSolicitud)).thenReturn(null);

        assertThrows(ConsultaNotFoundException.class, () ->
                recobrimentServiceImpl.getJustificantCsv(idPeticion, idSolicitud));
    }

    @Test
    public void testGetJustificantCsvV2_ArxiuUuidPresent_Success() throws Exception {
        String idPeticion = "CSV-ARX", idSolicitud = "S";
        Consulta consulta = mock(Consulta.class);
        when(consulta.getArxiuDocumentUuid()).thenReturn("UUID-ARX");
        when(consultaRepository.findByScspPeticionIdAndScspSolicitudId(idPeticion, idSolicitud)).thenReturn(consulta);

        Document document = new Document();
        DocumentMetadades metadades = new DocumentMetadades();
        metadades.setCsv("CSV-DE-ARXIU");
        document.setMetadades(metadades);
        when(pluginHelper.arxiuDocumentConsultar(idPeticion, "UUID-ARX", null, false, false)).thenReturn(document);

        String result = recobrimentServiceImpl.getJustificantCsv(idPeticion, idSolicitud);

        Assertions.assertEquals("CSV-DE-ARXIU", result);
        verifyNoInteractions(recobrimentHelper);
    }

    @Test
    public void testGetJustificantCsvV2_ArxiuUuidPresent_MetadadesNull() throws Exception {
        String idPeticion = "CSV-ARX2", idSolicitud = "S";
        Consulta consulta = mock(Consulta.class);
        when(consulta.getArxiuDocumentUuid()).thenReturn("UUID-ARX2");
        when(consultaRepository.findByScspPeticionIdAndScspSolicitudId(idPeticion, idSolicitud)).thenReturn(consulta);

        Document document = new Document();
        when(pluginHelper.arxiuDocumentConsultar(idPeticion, "UUID-ARX2", null, false, false)).thenReturn(document);

        String result = recobrimentServiceImpl.getJustificantCsv(idPeticion, idSolicitud);

        Assertions.assertNull(result);
    }

    @Test
    public void testGetJustificantCsvV2_ArxiuThrows_RecobrimentScspException() throws Exception {
        String idPeticion = "CSV-ARX3", idSolicitud = "S";
        Consulta consulta = mock(Consulta.class);
        when(consulta.getArxiuDocumentUuid()).thenReturn("UUID-ARX3");
        when(consultaRepository.findByScspPeticionIdAndScspSolicitudId(idPeticion, idSolicitud)).thenReturn(consulta);

        when(pluginHelper.arxiuDocumentConsultar(idPeticion, "UUID-ARX3", null, false, false))
                .thenThrow(new RuntimeException("error arxiu"));

        assertThrows(RecobrimentScspException.class, () ->
                recobrimentServiceImpl.getJustificantCsv(idPeticion, idSolicitud));
    }

    @Test
    public void testGetJustificantCsvV2_Fallback_ScspException() throws Exception {
        String idPeticion = "CSV-FB", idSolicitud = "S";
        Consulta consulta = mock(Consulta.class);
        when(consulta.getArxiuDocumentUuid()).thenReturn(null);
        when(consultaRepository.findByScspPeticionIdAndScspSolicitudId(idPeticion, idSolicitud)).thenReturn(consulta);

        when(recobrimentHelper.getJustificante(idPeticion, idSolicitud, true, false))
                .thenThrow(new ScspException("Error csv v2", "code"));

        assertThrows(RecobrimentScspException.class, () ->
                recobrimentServiceImpl.getJustificantCsv(idPeticion, idSolicitud));
    }

    // ==========================================================================
    // TESTS getJustificantUuid (v2)
    // ==========================================================================

    @Test
    public void testGetJustificantUuidV2_ConsultaNotFound() {
        String idPeticion = "UUID-NF", idSolicitud = "S";
        when(consultaRepository.findByScspPeticionIdAndScspSolicitudId(idPeticion, idSolicitud)).thenReturn(null);
        when(historicConsultaRepository.findByScspPeticionIdAndScspSolicitudId(idPeticion, idSolicitud)).thenReturn(null);

        assertThrows(ConsultaNotFoundException.class, () ->
                recobrimentServiceImpl.getJustificantUuid(idPeticion, idSolicitud));
    }

    @Test
    public void testGetJustificantUuidV2_ArxiuUuidPresent() throws Exception {
        String idPeticion = "UUID-ARX", idSolicitud = "S";
        Consulta consulta = mock(Consulta.class);
        when(consulta.getArxiuDocumentUuid()).thenReturn("UUID-DIRECTE");
        when(consultaRepository.findByScspPeticionIdAndScspSolicitudId(idPeticion, idSolicitud)).thenReturn(consulta);

        String result = recobrimentServiceImpl.getJustificantUuid(idPeticion, idSolicitud);

        Assertions.assertEquals("UUID-DIRECTE", result);
        verifyNoInteractions(recobrimentHelper);
    }

    @Test
    public void testGetJustificantUuidV2_Fallback_ScspException() throws Exception {
        String idPeticion = "UUID-FB", idSolicitud = "S";
        Consulta consulta = mock(Consulta.class);
        when(consulta.getArxiuDocumentUuid()).thenReturn(null);
        when(consultaRepository.findByScspPeticionIdAndScspSolicitudId(idPeticion, idSolicitud)).thenReturn(consulta);

        when(recobrimentHelper.getJustificante(idPeticion, idSolicitud, true, false))
                .thenThrow(new ScspException("Error uuid v2", "code"));

        assertThrows(RecobrimentScspException.class, () ->
                recobrimentServiceImpl.getJustificantUuid(idPeticion, idSolicitud));
    }

}
