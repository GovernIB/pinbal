package es.caib.pinbal.logic.service;

import es.caib.pinbal.logic.helper.ConfigHelper;
import es.caib.pinbal.logic.helper.RecobrimentHelper;
import es.caib.pinbal.logic.intf.service.ConsultaService;
import es.caib.pinbal.logic.intf.service.HistoricConsultaService;
import es.caib.pinbal.persist.repository.ConsultaRepository;
import es.caib.pinbal.persist.repository.HistoricConsultaRepository;
import es.scsp.bean.common.peticion.Atributos;
import es.scsp.bean.common.peticion.Consentimiento;
import es.scsp.bean.common.peticion.DatosGenericos;
import es.scsp.bean.common.peticion.Emisor;
import es.scsp.bean.common.peticion.Funcionario;
import es.scsp.bean.common.peticion.Peticion;
import es.scsp.bean.common.peticion.Procedimiento;
import es.scsp.bean.common.peticion.Solicitante;
import es.scsp.bean.common.peticion.SolicitudTransmision;
import es.scsp.bean.common.peticion.Solicitudes;
import es.scsp.bean.common.peticion.TipoDocumentacion;
import es.scsp.bean.common.peticion.Titular;
import es.scsp.bean.common.peticion.Transmision;
import es.scsp.common.exceptions.ScspException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class RecobrimentHelperTest {

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
    RecobrimentHelper recobrimentHelper;

    private Peticion peticio;

    @BeforeEach
    public void setUp() {
        peticio = new Peticion();

        // Funcionari
        Funcionario funcionari = new Funcionario();
        funcionari.setNombreCompletoFuncionario("Nom Complet Funcionari");
        funcionari.setNifFuncionario("57610215E");
        funcionari.setSeudonimoEmpleadoPublico("Funcionari");

        // Procediment
        Procedimiento procediment = new Procedimiento();
        procediment.setNombreProcedimiento("Procediment");
        procediment.setCodProcedimiento("COD_PROC");
        procediment.setAutomatizado("No");
        procediment.setClaseTramite(0);

        // Solicitant
        Solicitante solicitant = new Solicitante();
        solicitant.setIdentificadorSolicitante("32549495X");
        solicitant.setNombreSolicitante("Nom Sol.licitant");
        solicitant.setFinalidad("Finalitat");
        solicitant.setConsentimiento(Consentimiento.SI);
        solicitant.setFuncionario(funcionari);
        solicitant.setUnidadTramitadora("Unitat tramitadora");
        solicitant.setCodigoUnidadTramitadora("UT");
        solicitant.setProcedimiento(procediment);

        // Atributs
        Atributos atributs = new Atributos();
        atributs.setIdPeticion("000000001");
        atributs.setNumElementos(1);
        atributs.setTimeStamp(String.valueOf(System.currentTimeMillis()));
        atributs.setCodigoCertificado("COD_CER");
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

        SolicitudTransmision solicitudTransmisio = new SolicitudTransmision();
        solicitudTransmisio.setDatosGenericos(datosGenericos);
        solicitudTransmisio.setDatosEspecificos(null);

        solicitudsTransmissio.add(solicitudTransmisio);
        solicituds.getSolicitudTransmision().addAll(solicitudsTransmissio);
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
    private static final String MSG_ERROR_UNITAT_NULL = "No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.unidadTramitadora (solicitudIndex=0)";
    private static final String MSG_ERROR_UNITAT_EMPTY = "L'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.unidadTramitadora (solicitudIndex=0) no pot ser buit";
    private static final String MSG_ERROR_UNITAT_SIZE = "Camp massa llarg. L'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.unidadTramitadora (solicitudIndex=0) no pot superar els 250 caràcters";
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
    private static final String MSG_ERROR_TIT_NOM_SIZE = "Camp massa llarg. L'element peticion.solicitudes.solicitudTransmision.datosGenericos.titular.nombre (solicitudIndex=0) no pot superar els 40 caràcters";
    private static final String MSG_ERROR_TIT_LLIN1_SIZE = "Camp massa llarg. L'element peticion.solicitudes.solicitudTransmision.datosGenericos.titular.apellido1 (solicitudIndex=0) no pot superar els 40 caràcters";
    private static final String MSG_ERROR_TIT_LLIN2_SIZE = "Camp massa llarg. L'element peticion.solicitudes.solicitudTransmision.datosGenericos.titular.apellido2 (solicitudIndex=0) no pot superar els 40 caràcters";
    private static final String MSG_ERROR_TIT_COMP_SIZE = "Camp massa llarg. L'element peticion.solicitudes.solicitudTransmision.datosGenericos.titular.nombreCompleto (solicitudIndex=0) no pot superar els 122 caràcters";
    private static final String MSG_ERROR_SOL_TRANS_TYPE = "L'element peticion.solicitudes.solicitudTransmision.datosEspecificos (solicitudIndex=0) no és del tipus org.w3c.dom.Element";

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorPeticioNull() {
        ScspException ex = assertThrows(ScspException.class, () ->
                recobrimentHelper.validarIObtenirSolicituds(null, 1));
        assertTrue(ex.getMessage().contains(MSG_ERROR_PETICIO_NULL));
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorAtributsNull() {
        peticio.setAtributos(null);
        ScspException ex = assertThrows(ScspException.class, () ->
                recobrimentHelper.validarIObtenirSolicituds(peticio, 1));
        assertTrue(ex.getMessage().contains(MSG_ERROR_ATRIBUTS_NULL));
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorCodiCertificatNull() {
        peticio.getAtributos().setCodigoCertificado(null);
        ScspException ex = assertThrows(ScspException.class, () ->
                recobrimentHelper.validarIObtenirSolicituds(peticio, 1));
        assertTrue(ex.getMessage().contains(MSG_ERROR_COD_CER_NULL));
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorSolicitudsNull() {
        peticio.setSolicitudes(null);
        ScspException ex = assertThrows(ScspException.class, () ->
                recobrimentHelper.validarIObtenirSolicituds(peticio, 1));
        assertTrue(ex.getMessage().contains(MSG_ERROR_SOLICITUD_NULL));
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorSolicitudsTransmissioNull() {
        peticio.setSolicitudes(new Solicitudes() {
            @Override
            public java.util.List<SolicitudTransmision> getSolicitudTransmision() {
                return null;
            }
        });
        ScspException ex = assertThrows(ScspException.class, () ->
                recobrimentHelper.validarIObtenirSolicituds(peticio, 1));
        assertTrue(ex.getMessage().contains(MSG_ERROR_SOL_TRANS_NULL));
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorNombreMàximSolicituds() {
        peticio.getSolicitudes().getSolicitudTransmision().add(new SolicitudTransmision());
        ScspException ex = assertThrows(ScspException.class, () ->
                recobrimentHelper.validarIObtenirSolicituds(peticio, 1));
        assertTrue(ex.getMessage().contains(MSG_ERROR_MAX_SOLICITUDS));
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorDadesGeneriquesNull() {
        peticio.getSolicitudes().getSolicitudTransmision().get(0).setDatosGenericos(null);
        ScspException ex = assertThrows(ScspException.class, () ->
                recobrimentHelper.validarIObtenirSolicituds(peticio, 1));
        assertTrue(ex.getMessage().contains(MSG_ERROR_GENERIQUES_NULL));
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorDadesTransmissioNull() {
        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().setTransmision(null);
        ScspException ex = assertThrows(ScspException.class, () ->
                recobrimentHelper.validarIObtenirSolicituds(peticio, 10));
        assertTrue(ex.getMessage().contains(MSG_ERROR_TRANS_NULL));
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorIdSolicitudNull() {
        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getTransmision().setIdSolicitud(null);
        ScspException ex = assertThrows(ScspException.class, () ->
                recobrimentHelper.validarIObtenirSolicituds(peticio, 10));
        assertTrue(ex.getMessage().contains(MSG_ERROR_ID_SOL_NULL));
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorIdSolicitudBuit() {
        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getTransmision().setIdSolicitud("");
        ScspException ex = assertThrows(ScspException.class, () ->
                recobrimentHelper.validarIObtenirSolicituds(peticio, 10));
        assertTrue(ex.getMessage().contains(MSG_ERROR_ID_SOL_EMPTY));
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorIdSolicitudSize() {
        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getTransmision().setIdSolicitud("11111111111111111111111111111111111111111111111111111111111111111111111111111111");
        ScspException ex = assertThrows(ScspException.class, () ->
                recobrimentHelper.validarIObtenirSolicituds(peticio, 10));
        assertTrue(ex.getMessage().contains(MSG_ERROR_ID_SOL_SIZE));
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorSolicitantNull() {
        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().setSolicitante(null);
        ScspException ex = assertThrows(ScspException.class, () ->
                recobrimentHelper.validarIObtenirSolicituds(peticio, 1));
        assertTrue(ex.getMessage().contains(MSG_ERROR_SOLICITANT_NULL));
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorIdSolicitantNull() {
        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getSolicitante().setIdentificadorSolicitante(null);
        ScspException ex = assertThrows(ScspException.class, () ->
                recobrimentHelper.validarIObtenirSolicituds(peticio, 1));
        assertTrue(ex.getMessage().contains(MSG_ERROR_ID_SOLIC_NULL));
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorFinalitatNull() {
        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getSolicitante().setFinalidad(null);
        ScspException ex = assertThrows(ScspException.class, () ->
                recobrimentHelper.validarIObtenirSolicituds(peticio, 1));
        assertTrue(ex.getMessage().contains(MSG_ERROR_FINALITAT_NULL));
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorFinalitatBuit() {
        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getSolicitante().setFinalidad("");
        ScspException ex = assertThrows(ScspException.class, () ->
                recobrimentHelper.validarIObtenirSolicituds(peticio, 1));
        assertTrue(ex.getMessage().contains(MSG_ERROR_FINALITAT_EMPTY));
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorFinalitatSize() {
        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getSolicitante().setFinalidad("11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111");
        ScspException ex = assertThrows(ScspException.class, () ->
                recobrimentHelper.validarIObtenirSolicituds(peticio, 1));
        assertTrue(ex.getMessage().contains(MSG_ERROR_FINALITAT_SIZE));
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorConsentimentNull() {
        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getSolicitante().setConsentimiento(null);
        ScspException ex = assertThrows(ScspException.class, () ->
                recobrimentHelper.validarIObtenirSolicituds(peticio, 1));
        assertTrue(ex.getMessage().contains(MSG_ERROR_CONSENT_NULL));
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorUnitatNull() {
        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getSolicitante().setUnidadTramitadora(null);
        ScspException ex = assertThrows(ScspException.class, () ->
                recobrimentHelper.validarIObtenirSolicituds(peticio, 1));
        assertTrue(ex.getMessage().contains(MSG_ERROR_UNITAT_NULL));
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorUnitatBuid() {
        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getSolicitante().setUnidadTramitadora("");
        ScspException ex = assertThrows(ScspException.class, () ->
                recobrimentHelper.validarIObtenirSolicituds(peticio, 1));
        assertTrue(ex.getMessage().contains(MSG_ERROR_UNITAT_EMPTY));
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorUnitatSize() {
        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getSolicitante().setUnidadTramitadora("11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111");
        ScspException ex = assertThrows(ScspException.class, () ->
                recobrimentHelper.validarIObtenirSolicituds(peticio, 1));
        assertTrue(ex.getMessage().contains(MSG_ERROR_UNITAT_SIZE));
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorIdExpedientSize() {
        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getSolicitante().setIdExpediente("11111111111111111111111111111111111111111111111111111111111111111111111111111111");
        ScspException ex = assertThrows(ScspException.class, () ->
                recobrimentHelper.validarIObtenirSolicituds(peticio, 1));
        assertTrue(ex.getMessage().contains(MSG_ERROR_ID_EXP_SIZE));
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorProcedimentNull() {
        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getSolicitante().setProcedimiento(null);
        ScspException ex = assertThrows(ScspException.class, () ->
                recobrimentHelper.validarIObtenirSolicituds(peticio, 1));
        assertTrue(ex.getMessage().contains(MSG_ERROR_PROC_NULL));
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorCodiProcedimentNull() {
        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getSolicitante().getProcedimiento().setCodProcedimiento(null);
        ScspException ex = assertThrows(ScspException.class, () ->
                recobrimentHelper.validarIObtenirSolicituds(peticio, 1));
        assertTrue(ex.getMessage().contains(MSG_ERROR_COD_PROC_NULL));
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorCodiProcedimentBuit() {
        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getSolicitante().getProcedimiento().setCodProcedimiento("");
        ScspException ex = assertThrows(ScspException.class, () ->
                recobrimentHelper.validarIObtenirSolicituds(peticio, 1));
        assertTrue(ex.getMessage().contains(MSG_ERROR_COD_PROC_EMPTY));
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorFuncionariNull() {
        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getSolicitante().setFuncionario(null);
        ScspException ex = assertThrows(ScspException.class, () ->
                recobrimentHelper.validarIObtenirSolicituds(peticio, 1));
        assertTrue(ex.getMessage().contains(MSG_ERROR_FUNC_NULL));
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorNifFuncionariNull() {
        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getSolicitante().getFuncionario().setNifFuncionario(null);
        ScspException ex = assertThrows(ScspException.class, () ->
                recobrimentHelper.validarIObtenirSolicituds(peticio, 1));
        assertTrue(ex.getMessage().contains(MSG_ERROR_NIF_FUNC_NULL));
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorNifFuncionariBuit() {
        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getSolicitante().getFuncionario().setNifFuncionario("");
        ScspException ex = assertThrows(ScspException.class, () ->
                recobrimentHelper.validarIObtenirSolicituds(peticio, 1));
        assertTrue(ex.getMessage().contains(MSG_ERROR_NIF_FUNC_EMPTY));
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorNitFuncionariSize() {
        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getSolicitante().getFuncionario().setNifFuncionario("11111111111111111111111111111111111111111111111111111111111111111111111111111111");
        ScspException ex = assertThrows(ScspException.class, () ->
                recobrimentHelper.validarIObtenirSolicituds(peticio, 1));
        assertTrue(ex.getMessage().contains(MSG_ERROR_NIF_FUNC_SIZE));
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorNomCompletFuncionariNull() {
        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getSolicitante().getFuncionario().setNombreCompletoFuncionario(null);
        ScspException ex = assertThrows(ScspException.class, () ->
                recobrimentHelper.validarIObtenirSolicituds(peticio, 1));
        assertTrue(ex.getMessage().contains(MSG_ERROR_NOM_FUNC_NULL));
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorNomCompletFuncionariBuit() {
        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getSolicitante().getFuncionario().setNombreCompletoFuncionario("");
        ScspException ex = assertThrows(ScspException.class, () ->
                recobrimentHelper.validarIObtenirSolicituds(peticio, 1));
        assertTrue(ex.getMessage().contains(MSG_ERROR_NOM_FUNC_EMPTY));
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorNomCompletFuncionariSize() {
        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getSolicitante().getFuncionario().setNombreCompletoFuncionario("1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111");
        ScspException ex = assertThrows(ScspException.class, () ->
                recobrimentHelper.validarIObtenirSolicituds(peticio, 1));
        assertTrue(ex.getMessage().contains(MSG_ERROR_NOM_FUNC_SIZE));
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorDocumentTitularSize() {
        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getTitular().setDocumentacion("11111111111111111111111111111111111111111111111111111111111111111111111111111111");
        ScspException ex = assertThrows(ScspException.class, () ->
                recobrimentHelper.validarIObtenirSolicituds(peticio, 1));
        assertTrue(ex.getMessage().contains(MSG_ERROR_DOCUM_SIZE));
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorTipusDocumentTitularNull() {
        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getTitular().setTipoDocumentacion(null);
        ScspException ex = assertThrows(ScspException.class, () ->
                recobrimentHelper.validarIObtenirSolicituds(peticio, 1));
        assertTrue(ex.getMessage().contains(MSG_ERROR_TIP_DOC_NULL));
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorNomTitularSize() {
        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getTitular().setNombre("11111111111111111111111111111111111111111111111111111111111111111111111111111111");
        ScspException ex = assertThrows(ScspException.class, () ->
                recobrimentHelper.validarIObtenirSolicituds(peticio, 1));
        assertTrue(ex.getMessage().contains(MSG_ERROR_TIT_NOM_SIZE));
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorLlinatge1titularSize() {
        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getTitular().setApellido1("11111111111111111111111111111111111111111111111111111111111111111111111111111111");
        ScspException ex = assertThrows(ScspException.class, () ->
                recobrimentHelper.validarIObtenirSolicituds(peticio, 1));
        assertTrue(ex.getMessage().contains(MSG_ERROR_TIT_LLIN1_SIZE));
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorLlinatge2TitularSize() {
        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getTitular().setApellido2("11111111111111111111111111111111111111111111111111111111111111111111111111111111");
        ScspException ex = assertThrows(ScspException.class, () ->
                recobrimentHelper.validarIObtenirSolicituds(peticio, 1));
        assertTrue(ex.getMessage().contains(MSG_ERROR_TIT_LLIN2_SIZE));
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorNomCompletTitularSize() {
        peticio.getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos().getTitular().setNombreCompleto("1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111");
        ScspException ex = assertThrows(ScspException.class, () ->
                recobrimentHelper.validarIObtenirSolicituds(peticio, 1));
        assertTrue(ex.getMessage().contains(MSG_ERROR_TIT_COMP_SIZE));
    }

    @Test
    public void whenValidarIObtenirSolicitudsThenErrorDatosEspecificsTipus() {
        peticio.getSolicitudes().getSolicitudTransmision().get(0).setDatosEspecificos("aaa");
        ScspException ex = assertThrows(ScspException.class, () ->
                recobrimentHelper.validarIObtenirSolicituds(peticio, 1));
        assertTrue(ex.getMessage().contains(MSG_ERROR_SOL_TRANS_TYPE));
    }
}
