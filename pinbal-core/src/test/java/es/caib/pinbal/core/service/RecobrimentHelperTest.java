package es.caib.pinbal.core.service;

import es.caib.pinbal.core.helper.ConfigHelper;
import es.caib.pinbal.core.helper.RecobrimentHelper;
import es.caib.pinbal.core.repository.ConsultaRepository;
import es.caib.pinbal.core.repository.HistoricConsultaRepository;
import es.scsp.bean.common.*;
import es.scsp.common.exceptions.ScspException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Date;

@RunWith(MockitoJUnitRunner.class)
public class RecobrimentHelperTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Mock
    private ConsultaRepository consultaRepository;
    @Mock
    private HistoricConsultaRepository historicConsultaRepository;

    @Mock
    private ConsultaService consultaService;
    @Mock
    private HistoricConsultaService historicConsultaService;
    @Mock
    private ConfigHelper configHelper;

    @InjectMocks
    RecobrimentHelper recobrimentHelper = new RecobrimentHelper();

    private Peticion peticio;

    @Before
    public void setUp() {
//        MockitoAnnotations.initMocks(this);
        peticio = new Peticion();

        // Funcionari
        Funcionario funcionari = new Funcionario();
        funcionari.setNombreCompletoFuncionario("Nom Complet Funcionari");
        funcionari.setNifFuncionario("57610215E");
        funcionari.setSeudonimo("Funcionari");

        // Procediment
        Procedimiento procediment = new Procedimiento();
        procediment.setNombreProcedimiento("Procediment");
        procediment.setCodProcedimiento("COD_PROC");
        procediment.setAutomatizado("No");
        procediment.setClaseTramite((short)0);

        // Solicitant
        Solicitante solicitant = new Solicitante();
        solicitant.setIdentificadorSolicitante("32549495X");
        solicitant.setNombreSolicitante("Nom Sol.licitant");
        solicitant.setFinalidad("Finalitat");
        solicitant.setConsentimiento(Consentimiento.Si);
        solicitant.setFuncionario(funcionari);
        solicitant.setUnidadTramitadora("Unitat tramitadora");
        solicitant.setCodigoUnidadTramitadora("UT");
        solicitant.setProcedimiento(procediment);

        // Atributs
        Atributos atributs = new Atributos();
        atributs.setIdPeticion("000000001");
        atributs.setNumElementos("1");
        atributs.setTimeStamp(String.valueOf(System.currentTimeMillis()));
        atributs.setCodigoCertificado("COD_CER");
//        atributs.setEstado();
        atributs.setSolicitante(solicitant);
        peticio.setAtributos(atributs);


        Solicitudes solicituds = new Solicitudes();
        ArrayList<SolicitudTransmision> solicitudsTransmissio = new ArrayList<>();

        // Emisor
        Emisor emisor = new Emisor();
        emisor.setNifEmisor("87284566A");
        emisor.setNombreEmisor("Nom emisor");

        // Titular
        Titular titular = new Titular();
        titular.setTipoDocumentacion(TipoDocumentacion.DNI);
        titular.setDocumentacion("95394317T");
        titular.setNombre("Nom Complet Titular");
        titular.setNombre("Nom");
        titular.setApellido1("Complet");
        titular.setApellido2("Titular");

        // Transmissio
        Transmision transmissio = new Transmision();
        transmissio.setCodigoCertificado("COD_CER");
        transmissio.setIdSolicitud("000000001");
        transmissio.setIdTransmision("000001");
        transmissio.setFechaGeneracion(new Date().toString());

        // Datos Genericos
        DatosGenericos datosGenericos = new DatosGenericos();
        datosGenericos.setEmisor(emisor);
        datosGenericos.setSolicitante(solicitant);
        datosGenericos.setTitular(titular);
        datosGenericos.setTransmision(transmissio);

        // Datos específicos
        Object datosEspecificos = null;

        SolicitudTransmision solicitudTransmisio = new SolicitudTransmision();
        solicitudTransmisio.setDatosGenericos(datosGenericos);
        solicitudTransmisio.setDatosEspecificos(datosEspecificos);
        solicitudTransmisio.setId("000001");

        solicitudsTransmissio.add(solicitudTransmisio);
        solicituds.setSolicitudTransmision(solicitudsTransmissio);
        peticio.setSolicitudes(solicituds);
    }

    private static final String MSG_ERROR_PETICIO_NULL = "No s'ha trobat l'element peticion";
    private static final String MSG_ERROR_ATRIBUTS_NULL = "No s'ha trobat l'element peticion.atributos";
    private static final String MSG_ERROR_COD_CER_NULL = "No s'ha trobat l'element peticion.atributos.codigoCertificado";
    private static final String MSG_ERROR_SOLICITUD_NULL = "No s'ha trobat l'element peticion.solicitudes";
    private static final String MSG_ERROR_SOL_TRANS_NULL = "No s'ha trobat cap element peticion.solicitudes.solicitudTransmision";
    private static final String MSG_ERROR_MAX_SOLICITUDS = "S'ha excedit el màxim nombre de sol·licituds permeses en la petició (màxim=1)";
    private static final String MSG_ERROR_GENERIQUES_NULL = "No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos (solicitudIndex=0)";
    private static final String MSG_ERROR_TRANS_NULL = "No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.transmision (solicitudIndex=0)";
    private static final String MSG_ERROR_ID_SOL_NULL = "No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.transmision.idSolicitud (solicitudIndex=0)";
    private static final String MSG_ERROR_ID_SOL_EMPTY = "L'element peticion.solicitudes.solicitudTransmision.datosGenericos.transmision.idSolicitud (solicitudIndex=0) no pot ser buit";
    private static final String MSG_ERROR_ID_SOL_SIZE = "Camp massa llarg. L'element peticion.solicitudes.solicitudTransmision.datosGenericos.transmision.idSolicitud (solicitudIndex=0) no pot superar els 64 caràcters";
    private static final String MSG_ERROR_SOLICITANT_NULL = "No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante (solicitudIndex=0)";
    private static final String MSG_ERROR_ID_SOLIC_NULL = "No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.identificadorSolicitante (solicitudIndex=0)";
    private static final String MSG_ERROR_FINALITAT_NULL = "No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.finalidad (solicitudIndex=0)";
    private static final String MSG_ERROR_FINALITAT_EMPTY = "L'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.finalidad (solicitudIndex=0) no pot ser buit";
    private static final String MSG_ERROR_FINALITAT_SIZE = "Camp massa llarg. L'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.finalidad (solicitudIndex=0) no pot superar els 250 caràcters";
    private static final String MSG_ERROR_CONSENT_NULL = "No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.consentimiento (solicitudIndex=0)";
    private static final String MSG_ERROR_CONSENT_ENUM = "Valor incorrecte. Els valors possibles de l'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.consentimiento (solicitudIndex=0) son: [Si | Llei]";
    private static final String MSG_ERROR_UNITAT_NULL = "No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.unidadTramitadora (solicitudIndex=0)";
    private static final String MSG_ERROR_UNITAT_EMPTY = "L'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.unidadTramitadora (solicitudIndex=0) no pot ser buit";
    private static final String MSG_ERROR_UNITAT_SIZE = "Camp massa llarg. L'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.unidadTramitadora (solicitudIndex=0) no pot superar els 64 caràcters";
    private static final String MSG_ERROR_ID_EXP_SIZE = "Camp massa llarg. L'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.idExpediente (solicitudIndex=0) no pot superar els 25 caràcters";
    private static final String MSG_ERROR_PROC_NULL = "No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.procedimiento (solicitudIndex=0)";
    private static final String MSG_ERROR_COD_PROC_NULL = "No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.procedimiento.codProcedimiento (solicitudIndex=0)";
    private static final String MSG_ERROR_COD_PROC_EMPTY = "L'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.procedimiento.codProcedimiento (solicitudIndex=0) no pot ser buit";
    private static final String MSG_ERROR_FUNC_NULL = "No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.funcionario (solicitudIndex=0)";
    private static final String MSG_ERROR_NIF_FUNC_NULL = "No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.funcionario.nifFuncionario (solicitudIndex=0)";
    private static final String MSG_ERROR_NIF_FUNC_EMPTY = "L'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.funcionario.nifFuncionario (solicitudIndex=0) no pot ser buit";
    private static final String MSG_ERROR_NIF_FUNC_SIZE = "Camp massa llarg. L'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.funcionario.nifFuncionario (solicitudIndex=0) no pot superar els 10 caràcters";
    private static final String MSG_ERROR_NOM_FUNC_NULL = "No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.funcionario.nombreCompletoFuncionario (solicitudIndex=0)";
    private static final String MSG_ERROR_NOM_FUNC_EMPTY = "L'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.funcionario.nombreCompletoFuncionario (solicitudIndex=0) no pot ser buit";
    private static final String MSG_ERROR_NOM_FUNC_SIZE = "Camp massa llarg. L'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.funcionario.nombreCompletoFuncionario (solicitudIndex=0) no pot superar els 122 caràcters";
    private static final String MSG_ERROR_DOCUM_SIZE = "Camp massa llarg. L'element peticion.solicitudes.solicitudTransmision.datosGenericos.titular.documentacion (solicitudIndex=0) no pot superar els 14 caràcters";
    private static final String MSG_ERROR_TIP_DOC_NULL = "No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.titular.tipoDocumentacion (solicitudIndex=0)";
    private static final String MSG_ERROR_TIP_DOC_ENUM = "Valor incorrecte. Els valors possibles de l'element peticion.solicitudes.solicitudTransmision.datosGenericos.titular.tipoDocumentacion (solicitudIndex=0) son: [CIF | DNI | NIF | NIE | Pasaporte | NumeroIdentificacion | Otros]";
    private static final String MSG_ERROR_TIT_NOM_SIZE = "Camp massa llarg. L'element peticion.solicitudes.solicitudTransmision.datosGenericos.titular.nombre (solicitudIndex=0) no pot superar els 40 caràcters";
    private static final String MSG_ERROR_TIT_LLIN1_SIZE = "Camp massa llarg. L'element peticion.solicitudes.solicitudTransmision.datosGenericos.titular.apellido1 (solicitudIndex=0) no pot superar els 40 caràcters";
    private static final String MSG_ERROR_TIT_LLIN2_SIZE = "Camp massa llarg. L'element peticion.solicitudes.solicitudTransmision.datosGenericos.titular.apellido2 (solicitudIndex=0) no pot superar els 40 caràcters";
    private static final String MSG_ERROR_TIT_COMP_SIZE = "Camp massa llarg. L'element peticion.solicitudes.solicitudTransmision.datosGenericos.titular.nombreCompleto (solicitudIndex=0) no pot superar els 122 caràcters";
    private static final String MSG_ERROR_SOL_TRANS_TYPE = "L'element peticion.solicitudes.solicitudTransmision.datosEspecificos (solicitudIndex=0) no és del tipus org.w3c.dom.Element";


    @Test
    public void whenValidarIObtenirSolicitudsThenErrorPeticioNull() throws ScspException {
        exceptionRule.expect(ScspException.class);
        exceptionRule.expectMessage(MSG_ERROR_PETICIO_NULL);

        recobrimentHelper.validarIObtenirSolicituds(null, 1);
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorAtributsNull() throws ScspException {
        exceptionRule.expect(ScspException.class);
        exceptionRule.expectMessage(MSG_ERROR_ATRIBUTS_NULL);

        peticio.setAtributos(null);
        recobrimentHelper.validarIObtenirSolicituds(peticio, 1);
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorCodiCertificatNull() throws ScspException {
        exceptionRule.expect(ScspException.class);
        exceptionRule.expectMessage(MSG_ERROR_COD_CER_NULL);

        peticio.getAtributos().setCodigoCertificado(null);
        recobrimentHelper.validarIObtenirSolicituds(peticio, 1);
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorSolicitudsNull() throws ScspException {
        exceptionRule.expect(ScspException.class);
        exceptionRule.expectMessage(MSG_ERROR_SOLICITUD_NULL);

        peticio.setSolicitudes(null);
        recobrimentHelper.validarIObtenirSolicituds(peticio, 1);
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorSolicitudsTransmissioNull() throws ScspException {
        exceptionRule.expect(ScspException.class);
        exceptionRule.expectMessage(MSG_ERROR_SOL_TRANS_NULL);

        peticio.getSolicitudes().setSolicitudTransmision(null);
        recobrimentHelper.validarIObtenirSolicituds(peticio, 1);
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorNombreMàximSolicituds() throws ScspException {
        exceptionRule.expect(ScspException.class);
        exceptionRule.expectMessage(MSG_ERROR_MAX_SOLICITUDS);

        peticio.getSolicitudes().getSolicitudTransmision().add(new SolicitudTransmision());
        recobrimentHelper.validarIObtenirSolicituds(peticio, 1);
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorDadesGeneriquesNull() throws ScspException {
        exceptionRule.expect(ScspException.class);
        exceptionRule.expectMessage(MSG_ERROR_GENERIQUES_NULL);

        peticio.getSolicitudes().getSolicitudTransmision().get(0).setDatosGenericos(null);
        recobrimentHelper.validarIObtenirSolicituds(peticio, 1);
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorDadesTransmissioNull() throws ScspException {
        exceptionRule.expect(ScspException.class);
        exceptionRule.expectMessage(MSG_ERROR_TRANS_NULL);

        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().setTransmision(null);
        recobrimentHelper.validarIObtenirSolicituds(peticio, 10);
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorIdSolicitudNull() throws ScspException {
        exceptionRule.expect(ScspException.class);
        exceptionRule.expectMessage(MSG_ERROR_ID_SOL_NULL);

        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getTransmision().setIdSolicitud(null);
        recobrimentHelper.validarIObtenirSolicituds(peticio, 10);
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorIdSolicitudBuit() throws ScspException {
        exceptionRule.expect(ScspException.class);
        exceptionRule.expectMessage(MSG_ERROR_ID_SOL_EMPTY);

        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getTransmision().setIdSolicitud("");
        recobrimentHelper.validarIObtenirSolicituds(peticio, 10);
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorIdSolicitudSize() throws ScspException {
        exceptionRule.expect(ScspException.class);
        exceptionRule.expectMessage(MSG_ERROR_ID_SOL_SIZE);

        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getTransmision().setIdSolicitud("11111111111111111111111111111111111111111111111111111111111111111111111111111111");
        recobrimentHelper.validarIObtenirSolicituds(peticio, 10);
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorSolicitantNull() throws ScspException {
        exceptionRule.expect(ScspException.class);
        exceptionRule.expectMessage(MSG_ERROR_SOLICITANT_NULL);

        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().setSolicitante(null);
        recobrimentHelper.validarIObtenirSolicituds(peticio, 1);
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorIdSolicitantNull() throws ScspException {
        exceptionRule.expect(ScspException.class);
        exceptionRule.expectMessage(MSG_ERROR_ID_SOLIC_NULL);

        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getSolicitante().setIdentificadorSolicitante(null);
        recobrimentHelper.validarIObtenirSolicituds(peticio, 1);
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorFinalitatNull() throws ScspException {
        exceptionRule.expect(ScspException.class);
        exceptionRule.expectMessage(MSG_ERROR_FINALITAT_NULL);

        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getSolicitante().setFinalidad(null);
        recobrimentHelper.validarIObtenirSolicituds(peticio, 1);
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorFinalitatBuit() throws ScspException {
        exceptionRule.expect(ScspException.class);
        exceptionRule.expectMessage(MSG_ERROR_FINALITAT_EMPTY);

        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getSolicitante().setFinalidad("");
        recobrimentHelper.validarIObtenirSolicituds(peticio, 1);
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorFinalitatSize() throws ScspException {
        exceptionRule.expect(ScspException.class);
        exceptionRule.expectMessage(MSG_ERROR_FINALITAT_SIZE);

        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getSolicitante().setFinalidad("11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111");
        recobrimentHelper.validarIObtenirSolicituds(peticio, 1);
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorConsentimentNull() throws ScspException {
        exceptionRule.expect(ScspException.class);
        exceptionRule.expectMessage(MSG_ERROR_CONSENT_NULL);

        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getSolicitante().setConsentimiento(null);
        recobrimentHelper.validarIObtenirSolicituds(peticio, 1);
    }

//    @Test
//    private void whenValidarIObtenirSolicitudsThenErrorConsentimentValor() throws ScspException {
//        exceptionRule.expect(ScspException.class);
//        exceptionRule.expectMessage(MSG_ERROR_CONSENT_ENUM);
//
//        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getSolicitante().setConsentimiento(Consentimiento.valueOf("Altre"));
//        recobrimentHelper.validarIObtenirSolicituds(peticio, 1);
//    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorUnitatNull() throws ScspException {
        exceptionRule.expect(ScspException.class);
        exceptionRule.expectMessage(MSG_ERROR_UNITAT_NULL);

        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getSolicitante().setUnidadTramitadora(null);
        recobrimentHelper.validarIObtenirSolicituds(peticio, 1);
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorUnitatBuid() throws ScspException {
        exceptionRule.expect(ScspException.class);
        exceptionRule.expectMessage(MSG_ERROR_UNITAT_EMPTY);

        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getSolicitante().setUnidadTramitadora("");
        recobrimentHelper.validarIObtenirSolicituds(peticio, 1);
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorUnitatSize() throws ScspException {
        exceptionRule.expect(ScspException.class);
        exceptionRule.expectMessage(MSG_ERROR_UNITAT_SIZE);

        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getSolicitante().setUnidadTramitadora("11111111111111111111111111111111111111111111111111111111111111111111111111111111");
        recobrimentHelper.validarIObtenirSolicituds(peticio, 1);
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorIdExpedientSize() throws ScspException {
        exceptionRule.expect(ScspException.class);
        exceptionRule.expectMessage(MSG_ERROR_ID_EXP_SIZE);

        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getSolicitante().setIdExpediente("11111111111111111111111111111111111111111111111111111111111111111111111111111111");
        recobrimentHelper.validarIObtenirSolicituds(peticio, 1);
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorProcedimentNull() throws ScspException {
        exceptionRule.expect(ScspException.class);
        exceptionRule.expectMessage(MSG_ERROR_PROC_NULL);

        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getSolicitante().setProcedimiento(null);
        recobrimentHelper.validarIObtenirSolicituds(peticio, 1);
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorCodiProcedimentNull() throws ScspException {
        exceptionRule.expect(ScspException.class);
        exceptionRule.expectMessage(MSG_ERROR_COD_PROC_NULL);

        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getSolicitante().getProcedimiento().setCodProcedimiento(null);
        recobrimentHelper.validarIObtenirSolicituds(peticio, 1);
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorCodiProcedimentBuit() throws ScspException {
        exceptionRule.expect(ScspException.class);
        exceptionRule.expectMessage(MSG_ERROR_COD_PROC_EMPTY);

        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getSolicitante().getProcedimiento().setCodProcedimiento("");
        recobrimentHelper.validarIObtenirSolicituds(peticio, 1);
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorFuncionariNull() throws ScspException {
        exceptionRule.expect(ScspException.class);
        exceptionRule.expectMessage(MSG_ERROR_FUNC_NULL);

        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getSolicitante().setFuncionario(null);
        recobrimentHelper.validarIObtenirSolicituds(peticio, 1);
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorNifFuncionariNull() throws ScspException {
        exceptionRule.expect(ScspException.class);
        exceptionRule.expectMessage(MSG_ERROR_NIF_FUNC_NULL);

        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getSolicitante().getFuncionario().setNifFuncionario(null);
        recobrimentHelper.validarIObtenirSolicituds(peticio, 1);
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorNifFuncionariBuit() throws ScspException {
        exceptionRule.expect(ScspException.class);
        exceptionRule.expectMessage(MSG_ERROR_NIF_FUNC_EMPTY);

        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getSolicitante().getFuncionario().setNifFuncionario("");
        recobrimentHelper.validarIObtenirSolicituds(peticio, 1);
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorNitFuncionariSize() throws ScspException {
        exceptionRule.expect(ScspException.class);
        exceptionRule.expectMessage(MSG_ERROR_NIF_FUNC_SIZE);

        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getSolicitante().getFuncionario().setNifFuncionario("11111111111111111111111111111111111111111111111111111111111111111111111111111111");
        recobrimentHelper.validarIObtenirSolicituds(peticio, 1);
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorNomCompletFuncionariNull() throws ScspException {
        exceptionRule.expect(ScspException.class);
        exceptionRule.expectMessage(MSG_ERROR_NOM_FUNC_NULL);

        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getSolicitante().getFuncionario().setNombreCompletoFuncionario(null);
        recobrimentHelper.validarIObtenirSolicituds(peticio, 1);
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorNomCompletFuncionariBuit() throws ScspException {
        exceptionRule.expect(ScspException.class);
        exceptionRule.expectMessage(MSG_ERROR_NOM_FUNC_EMPTY);

        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getSolicitante().getFuncionario().setNombreCompletoFuncionario("");
        recobrimentHelper.validarIObtenirSolicituds(peticio, 1);
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorNomCompletFuncionariSize() throws ScspException {
        exceptionRule.expect(ScspException.class);
        exceptionRule.expectMessage(MSG_ERROR_NOM_FUNC_SIZE);

        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getSolicitante().getFuncionario().setNombreCompletoFuncionario("1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111");
        recobrimentHelper.validarIObtenirSolicituds(peticio, 1);
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorDocumentTitularSize() throws ScspException {
        exceptionRule.expect(ScspException.class);
        exceptionRule.expectMessage(MSG_ERROR_DOCUM_SIZE);

        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getTitular().setDocumentacion("11111111111111111111111111111111111111111111111111111111111111111111111111111111");
        recobrimentHelper.validarIObtenirSolicituds(peticio, 1);
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorTipusDocumentTitularNull() throws ScspException {
        exceptionRule.expect(ScspException.class);
        exceptionRule.expectMessage(MSG_ERROR_TIP_DOC_NULL);

        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getTitular().setTipoDocumentacion(null);
        recobrimentHelper.validarIObtenirSolicituds(peticio, 1);
    }

//    @Test
//    private void whenValidarIObtenirSolicitudsThenErrorTipusDocumentTitularValor() throws ScspException {
//        exceptionRule.expect(ScspException.class);
//        exceptionRule.expectMessage(MSG_ERROR_TIP_DOC_ENUM);
//
//        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getTitular().setTipoDocumentacion(TipoDocumentacion.valueOf("Altre"));
//        recobrimentHelper.validarIObtenirSolicituds(peticio, 1);
//    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorNomTitularSize() throws ScspException {
        exceptionRule.expect(ScspException.class);
        exceptionRule.expectMessage(MSG_ERROR_TIT_NOM_SIZE);

        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getTitular().setNombre("11111111111111111111111111111111111111111111111111111111111111111111111111111111");
        recobrimentHelper.validarIObtenirSolicituds(peticio, 1);
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorLlinatge1titularSize() throws ScspException {
        exceptionRule.expect(ScspException.class);
        exceptionRule.expectMessage(MSG_ERROR_TIT_LLIN1_SIZE);

        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getTitular().setApellido1("11111111111111111111111111111111111111111111111111111111111111111111111111111111");
        recobrimentHelper.validarIObtenirSolicituds(peticio, 1);
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorLlinatge2TitularSize() throws ScspException {
        exceptionRule.expect(ScspException.class);
        exceptionRule.expectMessage(MSG_ERROR_TIT_LLIN2_SIZE);

        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getTitular().setApellido2("11111111111111111111111111111111111111111111111111111111111111111111111111111111");
        recobrimentHelper.validarIObtenirSolicituds(peticio, 1);
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorNomCompletTitularSize() throws ScspException {
        exceptionRule.expect(ScspException.class);
        exceptionRule.expectMessage(MSG_ERROR_TIT_COMP_SIZE);

        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getTitular().setNombreCompleto("1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111");
        recobrimentHelper.validarIObtenirSolicituds(peticio, 1);
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorDatosEspecificsTipus() throws ScspException {
        exceptionRule.expect(ScspException.class);
        exceptionRule.expectMessage(MSG_ERROR_SOL_TRANS_TYPE);

        peticio.getSolicitudes().getSolicitudTransmision().get(0).setDatosEspecificos("aaa");
        recobrimentHelper.validarIObtenirSolicituds(peticio, 1);
    }

}
