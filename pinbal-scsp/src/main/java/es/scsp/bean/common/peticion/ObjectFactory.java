package es.scsp.bean.common.peticion;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * Override de es.scsp.bean.common.peticion.ObjectFactory de scsp-beans.
 *
 * Elimina createDatosEspecificos perque tant peticion.ObjectFactory com
 * respuesta.ObjectFactory el declaren per al mateix QName
 * {http://intermediacion.redsara.es/scsp/esquemas/datosespecificos}DatosEspecificos,
 * cosa que provoca IllegalAnnotationsException en el JAXBContext de CXF.
 * La definicio del element queda unicament a respuesta.ObjectFactory.
 */
@XmlRegistry
public class ObjectFactory {

    private static final String NS = "http://intermediacion.redsara.es/scsp/esquemas/V3/peticion";

    private static final QName _Apellido1_QNAME = new QName(NS, "Apellido1");
    private static final QName _Apellido2_QNAME = new QName(NS, "Apellido2");
    private static final QName _IdPeticion_QNAME = new QName(NS, "IdPeticion");
    private static final QName _NumElementos_QNAME = new QName(NS, "NumElementos");
    private static final QName _TimeStamp_QNAME = new QName(NS, "TimeStamp");
    private static final QName _CodigoEstado_QNAME = new QName(NS, "CodigoEstado");
    private static final QName _CodigoEstadoSecundario_QNAME = new QName(NS, "CodigoEstadoSecundario");
    private static final QName _LiteralError_QNAME = new QName(NS, "LiteralError");
    private static final QName _TiempoEstimadoRespuesta_QNAME = new QName(NS, "TiempoEstimadoRespuesta");
    private static final QName _CodigoCertificado_QNAME = new QName(NS, "CodigoCertificado");
    private static final QName _Consentimiento_QNAME = new QName(NS, "Consentimiento");
    private static final QName _NifEmisor_QNAME = new QName(NS, "NifEmisor");
    private static final QName _NombreEmisor_QNAME = new QName(NS, "NombreEmisor");
    private static final QName _IdentificadorSolicitante_QNAME = new QName(NS, "IdentificadorSolicitante");
    private static final QName _NombreSolicitante_QNAME = new QName(NS, "NombreSolicitante");
    private static final QName _UnidadTramitadora_QNAME = new QName(NS, "UnidadTramitadora");
    private static final QName _CodigoUnidadTramitadora_QNAME = new QName(NS, "CodigoUnidadTramitadora");
    private static final QName _CodProcedimiento_QNAME = new QName(NS, "CodProcedimiento");
    private static final QName _NombreProcedimiento_QNAME = new QName(NS, "NombreProcedimiento");
    private static final QName _Automatizado_QNAME = new QName(NS, "Automatizado");
    private static final QName _ClaseTramite_QNAME = new QName(NS, "ClaseTramite");
    private static final QName _Finalidad_QNAME = new QName(NS, "Finalidad");
    private static final QName _NombreCompletoFuncionario_QNAME = new QName(NS, "NombreCompletoFuncionario");
    private static final QName _NifFuncionario_QNAME = new QName(NS, "NifFuncionario");
    private static final QName _SeudonimoEmpleadoPublico_QNAME = new QName(NS, "SeudonimoEmpleadoPublico");
    private static final QName _IdExpediente_QNAME = new QName(NS, "IdExpediente");
    private static final QName _TipoDocumentacion_QNAME = new QName(NS, "TipoDocumentacion");
    private static final QName _Documentacion_QNAME = new QName(NS, "Documentacion");
    private static final QName _NombreCompleto_QNAME = new QName(NS, "NombreCompleto");
    private static final QName _Nombre_QNAME = new QName(NS, "Nombre");
    private static final QName _IdSolicitud_QNAME = new QName(NS, "IdSolicitud");
    private static final QName _IdTransmision_QNAME = new QName(NS, "IdTransmision");
    private static final QName _FechaGeneracion_QNAME = new QName(NS, "FechaGeneracion");

    public Atributos createAtributos() { return new Atributos(); }
    public Estado createEstado() { return new Estado(); }
    public DatosGenericos createDatosGenericos() { return new DatosGenericos(); }
    public Emisor createEmisor() { return new Emisor(); }
    public Solicitante createSolicitante() { return new Solicitante(); }
    public Procedimiento createProcedimiento() { return new Procedimiento(); }
    public Funcionario createFuncionario() { return new Funcionario(); }
    public Titular createTitular() { return new Titular(); }
    public Transmision createTransmision() { return new Transmision(); }
    public Peticion createPeticion() { return new Peticion(); }
    public Solicitudes createSolicitudes() { return new Solicitudes(); }
    public SolicitudTransmision createSolicitudTransmision() { return new SolicitudTransmision(); }

    @XmlElementDecl(namespace = NS, name = "Apellido1")
    public JAXBElement<String> createApellido1(String value) {
        return new JAXBElement<>(_Apellido1_QNAME, String.class, null, value);
    }
    @XmlElementDecl(namespace = NS, name = "Apellido2")
    public JAXBElement<String> createApellido2(String value) {
        return new JAXBElement<>(_Apellido2_QNAME, String.class, null, value);
    }
    @XmlElementDecl(namespace = NS, name = "IdPeticion")
    public JAXBElement<String> createIdPeticion(String value) {
        return new JAXBElement<>(_IdPeticion_QNAME, String.class, null, value);
    }
    @XmlElementDecl(namespace = NS, name = "NumElementos")
    public JAXBElement<Integer> createNumElementos(Integer value) {
        return new JAXBElement<>(_NumElementos_QNAME, Integer.class, null, value);
    }
    @XmlElementDecl(namespace = NS, name = "TimeStamp")
    public JAXBElement<String> createTimeStamp(String value) {
        return new JAXBElement<>(_TimeStamp_QNAME, String.class, null, value);
    }
    @XmlElementDecl(namespace = NS, name = "CodigoEstado")
    public JAXBElement<String> createCodigoEstado(String value) {
        return new JAXBElement<>(_CodigoEstado_QNAME, String.class, null, value);
    }
    @XmlElementDecl(namespace = NS, name = "CodigoEstadoSecundario")
    public JAXBElement<String> createCodigoEstadoSecundario(String value) {
        return new JAXBElement<>(_CodigoEstadoSecundario_QNAME, String.class, null, value);
    }
    @XmlElementDecl(namespace = NS, name = "LiteralError")
    public JAXBElement<String> createLiteralError(String value) {
        return new JAXBElement<>(_LiteralError_QNAME, String.class, null, value);
    }
    @XmlElementDecl(namespace = NS, name = "TiempoEstimadoRespuesta")
    public JAXBElement<Integer> createTiempoEstimadoRespuesta(Integer value) {
        return new JAXBElement<>(_TiempoEstimadoRespuesta_QNAME, Integer.class, null, value);
    }
    @XmlElementDecl(namespace = NS, name = "CodigoCertificado")
    public JAXBElement<String> createCodigoCertificado(String value) {
        return new JAXBElement<>(_CodigoCertificado_QNAME, String.class, null, value);
    }
    @XmlElementDecl(namespace = NS, name = "Consentimiento")
    public JAXBElement<Consentimiento> createConsentimiento(Consentimiento value) {
        return new JAXBElement<>(_Consentimiento_QNAME, Consentimiento.class, null, value);
    }
    @XmlElementDecl(namespace = NS, name = "NifEmisor")
    public JAXBElement<String> createNifEmisor(String value) {
        return new JAXBElement<>(_NifEmisor_QNAME, String.class, null, value);
    }
    @XmlElementDecl(namespace = NS, name = "NombreEmisor")
    public JAXBElement<String> createNombreEmisor(String value) {
        return new JAXBElement<>(_NombreEmisor_QNAME, String.class, null, value);
    }
    @XmlElementDecl(namespace = NS, name = "IdentificadorSolicitante")
    public JAXBElement<String> createIdentificadorSolicitante(String value) {
        return new JAXBElement<>(_IdentificadorSolicitante_QNAME, String.class, null, value);
    }
    @XmlElementDecl(namespace = NS, name = "NombreSolicitante")
    public JAXBElement<String> createNombreSolicitante(String value) {
        return new JAXBElement<>(_NombreSolicitante_QNAME, String.class, null, value);
    }
    @XmlElementDecl(namespace = NS, name = "UnidadTramitadora")
    public JAXBElement<String> createUnidadTramitadora(String value) {
        return new JAXBElement<>(_UnidadTramitadora_QNAME, String.class, null, value);
    }
    @XmlElementDecl(namespace = NS, name = "CodigoUnidadTramitadora")
    public JAXBElement<String> createCodigoUnidadTramitadora(String value) {
        return new JAXBElement<>(_CodigoUnidadTramitadora_QNAME, String.class, null, value);
    }
    @XmlElementDecl(namespace = NS, name = "CodProcedimiento")
    public JAXBElement<String> createCodProcedimiento(String value) {
        return new JAXBElement<>(_CodProcedimiento_QNAME, String.class, null, value);
    }
    @XmlElementDecl(namespace = NS, name = "NombreProcedimiento")
    public JAXBElement<String> createNombreProcedimiento(String value) {
        return new JAXBElement<>(_NombreProcedimiento_QNAME, String.class, null, value);
    }
    @XmlElementDecl(namespace = NS, name = "Automatizado")
    public JAXBElement<String> createAutomatizado(String value) {
        return new JAXBElement<>(_Automatizado_QNAME, String.class, null, value);
    }
    @XmlElementDecl(namespace = NS, name = "ClaseTramite")
    public JAXBElement<Integer> createClaseTramite(Integer value) {
        return new JAXBElement<>(_ClaseTramite_QNAME, Integer.class, null, value);
    }
    @XmlElementDecl(namespace = NS, name = "Finalidad")
    public JAXBElement<String> createFinalidad(String value) {
        return new JAXBElement<>(_Finalidad_QNAME, String.class, null, value);
    }
    @XmlElementDecl(namespace = NS, name = "NombreCompletoFuncionario")
    public JAXBElement<String> createNombreCompletoFuncionario(String value) {
        return new JAXBElement<>(_NombreCompletoFuncionario_QNAME, String.class, null, value);
    }
    @XmlElementDecl(namespace = NS, name = "NifFuncionario")
    public JAXBElement<String> createNifFuncionario(String value) {
        return new JAXBElement<>(_NifFuncionario_QNAME, String.class, null, value);
    }
    @XmlElementDecl(namespace = NS, name = "SeudonimoEmpleadoPublico")
    public JAXBElement<String> createSeudonimoEmpleadoPublico(String value) {
        return new JAXBElement<>(_SeudonimoEmpleadoPublico_QNAME, String.class, null, value);
    }
    @XmlElementDecl(namespace = NS, name = "IdExpediente")
    public JAXBElement<String> createIdExpediente(String value) {
        return new JAXBElement<>(_IdExpediente_QNAME, String.class, null, value);
    }
    @XmlElementDecl(namespace = NS, name = "TipoDocumentacion")
    public JAXBElement<TipoDocumentacion> createTipoDocumentacion(TipoDocumentacion value) {
        return new JAXBElement<>(_TipoDocumentacion_QNAME, TipoDocumentacion.class, null, value);
    }
    @XmlElementDecl(namespace = NS, name = "Documentacion")
    public JAXBElement<String> createDocumentacion(String value) {
        return new JAXBElement<>(_Documentacion_QNAME, String.class, null, value);
    }
    @XmlElementDecl(namespace = NS, name = "NombreCompleto")
    public JAXBElement<String> createNombreCompleto(String value) {
        return new JAXBElement<>(_NombreCompleto_QNAME, String.class, null, value);
    }
    @XmlElementDecl(namespace = NS, name = "Nombre")
    public JAXBElement<String> createNombre(String value) {
        return new JAXBElement<>(_Nombre_QNAME, String.class, null, value);
    }
    @XmlElementDecl(namespace = NS, name = "IdSolicitud")
    public JAXBElement<String> createIdSolicitud(String value) {
        return new JAXBElement<>(_IdSolicitud_QNAME, String.class, null, value);
    }
    @XmlElementDecl(namespace = NS, name = "IdTransmision")
    public JAXBElement<String> createIdTransmision(String value) {
        return new JAXBElement<>(_IdTransmision_QNAME, String.class, null, value);
    }
    @XmlElementDecl(namespace = NS, name = "FechaGeneracion")
    public JAXBElement<String> createFechaGeneracion(String value) {
        return new JAXBElement<>(_FechaGeneracion_QNAME, String.class, null, value);
    }
}
