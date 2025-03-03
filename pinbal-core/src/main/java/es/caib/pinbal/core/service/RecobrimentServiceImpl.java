/**
 *
 */
package es.caib.pinbal.core.service;

import es.caib.pinbal.client.procediments.Procediment;
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
import es.caib.pinbal.client.recobriment.model.ScspTransmisionDatos;
import es.caib.pinbal.client.recobriment.v2.DadaEspecifica;
import es.caib.pinbal.client.recobriment.v2.DadaTipusEnum;
import es.caib.pinbal.client.recobriment.v2.PeticioAsincrona;
import es.caib.pinbal.client.recobriment.v2.PeticioRespostaAsincrona;
import es.caib.pinbal.client.recobriment.v2.PeticioRespostaSincrona;
import es.caib.pinbal.client.recobriment.v2.PeticioSincrona;
import es.caib.pinbal.client.recobriment.v2.Validacio;
import es.caib.pinbal.client.recobriment.v2.ValorEnum;
import es.caib.pinbal.client.serveis.Servei;
import es.caib.pinbal.core.dto.DadaEspecificaDto;
import es.caib.pinbal.core.dto.IdiomaEnumDto;
import es.caib.pinbal.core.dto.JustificantDto;
import es.caib.pinbal.core.dto.ServeiCampDto;
import es.caib.pinbal.core.dto.dadesexternes.Municipi;
import es.caib.pinbal.core.dto.dadesexternes.Pais;
import es.caib.pinbal.core.dto.dadesexternes.Provincia;
import es.caib.pinbal.core.helper.RecobrimentHelper;
import es.caib.pinbal.core.helper.RecobrimentV2Helper;
import es.caib.pinbal.core.model.Consulta;
import es.caib.pinbal.core.model.Entitat;
import es.caib.pinbal.core.model.ServeiCamp;
import es.caib.pinbal.core.model.ServeiCamp.ServeiCampTipus;
import es.caib.pinbal.core.model.ServeiCampGrup;
import es.caib.pinbal.core.model.ServeiConfig;
import es.caib.pinbal.core.repository.ConsultaRepository;
import es.caib.pinbal.core.repository.EntitatRepository;
import es.caib.pinbal.core.repository.ProcedimentRepository;
import es.caib.pinbal.core.repository.ServeiCampRepository;
import es.caib.pinbal.core.repository.ServeiConfigRepository;
import es.caib.pinbal.core.repository.ServeiRepository;
import es.caib.pinbal.core.service.exception.ConsultaNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.core.service.exception.RecobrimentScspException;
import es.caib.pinbal.core.service.exception.RecobrimentScspValidationException;
import es.caib.pinbal.core.service.exception.ServeiCampNotFoundException;
import es.caib.pinbal.core.service.exception.ServeiNotFoundException;
import es.caib.pinbal.scsp.ScspHelper;
import es.caib.pinbal.scsp.XmlHelper;
import es.caib.pinbal.scsp.tree.Tree;
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
import es.scsp.bean.common.Solicitudes;
import es.scsp.bean.common.TipoDocumentacion;
import es.scsp.bean.common.Titular;
import es.scsp.bean.common.Transmision;
import es.scsp.bean.common.TransmisionDatos;
import es.scsp.common.exceptions.ScspException;
import es.scsp.common.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementació dels mètodes per a fer peticions al recobriment SCSP.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class RecobrimentServiceImpl implements RecobrimentService, ApplicationContextAware, MessageSourceAware {

    @Autowired
    private DadesExternesService dadesExternesService;
    @Autowired
    private ServeiService serveiService;
    @Autowired
    private RecobrimentHelper recobrimentHelper;
    @Autowired
    private RecobrimentV2Helper recobrimentV2Helper;
    @Autowired
    private EntitatRepository entitatRepository;
    @Autowired
    private ProcedimentRepository procedimentRepository;
    @Autowired
    private ServeiConfigRepository serveiConfigRepository;
    @Autowired
    private ServeiRepository serveiRepository;
    @Autowired
    private ServeiCampRepository serveiCampRepository;

	private ApplicationContext applicationContext;
	private MessageSource messageSource;
	private ScspHelper scspHelper;
    @Autowired
    private ConsultaRepository consultaRepository;

    @Override
	public void setApplicationContext(
			ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}


    @Override
    public ScspRespuesta peticionSincrona(
            ScspPeticion peticion) throws RecobrimentScspException {
        try {
            return toScspRespuesta(
                    recobrimentHelper.peticionSincrona(toPeticion(peticion, false)));
        } catch (ScspException ex) {
            if (RecobrimentHelper.ERROR_CODE_SCSP_VALIDATION.equals(ex.getScspCode())) {
                throw new RecobrimentScspValidationException(
                        ex.getMessage(),
                        ex);
            } else {
                throw new RecobrimentScspException(
                        ex.getMessage(),
                        ex);
            }
        } catch (TransformerException ex) {
            throw new RecobrimentScspException(
                    ex.getMessage(),
                    ex);
        } catch (ParserConfigurationException ex) {
            throw new RecobrimentScspException(
                    ex.getMessage(),
                    ex);
        } catch (SAXException ex) {
            throw new RecobrimentScspException(
                    ex.getMessage(),
                    ex);
        } catch (IOException ex) {
            throw new RecobrimentScspException(
                    ex.getMessage(),
                    ex);
        }
    }

    @Override
    public ScspConfirmacionPeticion peticionAsincrona(
            ScspPeticion peticion) throws RecobrimentScspException {
        try {
            return toScspConfirmacionPeticion(
                    recobrimentHelper.peticionAsincrona(toPeticion(peticion, true)));
        } catch (ScspException ex) {
            throw new RecobrimentScspException(
                    ex.getMessage(),
                    ex);
        } catch (ParserConfigurationException ex) {
            throw new RecobrimentScspException(
                    ex.getMessage(),
                    ex);
        } catch (SAXException ex) {
            throw new RecobrimentScspException(
                    ex.getMessage(),
                    ex);
        } catch (IOException ex) {
            throw new RecobrimentScspException(
                    ex.getMessage(),
                    ex);
        }
    }

    @Override
    public ScspRespuesta getRespuesta(
            String idPeticion) throws RecobrimentScspException {
        try {
            return toScspRespuesta(
                    recobrimentHelper.getRespuesta(idPeticion));
        } catch (TransformerException ex) {
            throw new RecobrimentScspException(
                    ex.getMessage(),
                    ex);
        } catch (ScspException ex) {
            throw new RecobrimentScspException(
                    ex.getMessage(),
                    ex);
        }
    }

    @Override
    public ScspJustificante getJustificante(
            String idPeticion,
            String idSolicitud) throws RecobrimentScspException {
        try {
            JustificantDto justificant = recobrimentHelper.getJustificante(idPeticion, idSolicitud, false, true);
            ScspJustificante justificante = new ScspJustificante();
            justificante.setNom(justificant.getNom());
            justificante.setContentType(justificant.getContentType());
            justificante.setContingut(justificant.getContingut());
            return justificante;
        } catch (ScspException ex) {
            throw new RecobrimentScspException(
                    ex.getMessage(),
                    ex);
        }
    }

    @Override
    public ScspJustificante getJustificanteImprimible(String idPeticion, String idSolicitud) throws RecobrimentScspException {
        try {
            JustificantDto justificant = recobrimentHelper.getJustificante(idPeticion, idSolicitud, true, true);
            ScspJustificante justificante = new ScspJustificante();
            justificante.setNom(justificant.getNom());
            justificante.setContentType(justificant.getContentType());
            justificante.setContingut(justificant.getContingut());
            return justificante;
        } catch (ScspException ex) {
            throw new RecobrimentScspException(
                    ex.getMessage(),
                    ex);
        }
    }

    @Override
    public String getJustificanteCsv(String idPeticion, String idSolicitud) throws RecobrimentScspException {
        try {
            JustificantDto justificant = recobrimentHelper.getJustificante(idPeticion, idSolicitud, true, false);
            return justificant.getArxiuCsv();
        } catch (ScspException ex) {
            throw new RecobrimentScspException(
                    ex.getMessage(),
                    ex);
        }
    }

    @Override
    public String getJustificanteUuid(String idPeticion, String idSolicitud) throws RecobrimentScspException {
        try {
            JustificantDto justificant = recobrimentHelper.getJustificante(idPeticion, idSolicitud, false, false);
            return justificant.getArxiuUuid();
        } catch (ScspException ex) {
            throw new RecobrimentScspException(
                    ex.getMessage(),
                    ex);
        }
    }

    private Peticion toPeticion(ScspPeticion scspPeticion, boolean multiple) throws ParserConfigurationException, SAXException, IOException {
        if (scspPeticion != null) {
            Peticion peticion = new Peticion();
            if (scspPeticion.getAtributos() != null) {
                Atributos atributos = new Atributos();
                atributos.setIdPeticion(scspPeticion.getAtributos().getIdPeticion());
                atributos.setNumElementos(scspPeticion.getAtributos().getNumElementos());
                atributos.setTimeStamp(scspPeticion.getAtributos().getTimeStamp());
                atributos.setCodigoCertificado(scspPeticion.getAtributos().getCodigoCertificado());
                if (scspPeticion.getAtributos().getEstado() != null) {
                    Estado estado = new Estado();
                    estado.setCodigoEstado(scspPeticion.getAtributos().getEstado().getCodigoEstado());
                    estado.setLiteralError(scspPeticion.getAtributos().getEstado().getLiteralError());
                    estado.setLiteralErrorSec(scspPeticion.getAtributos().getEstado().getLiteralErrorSec());
                    estado.setTiempoEstimadoRespuesta(scspPeticion.getAtributos().getEstado().getTiempoEstimadoRespuesta());
                    atributos.setEstado(estado);
                }
                peticion.setAtributos(atributos);
            }
            if (scspPeticion.getSolicitudes() != null) {
                Solicitudes solicitudes = new Solicitudes();
                ArrayList<SolicitudTransmision> solicitudesTransmision = new ArrayList<SolicitudTransmision>();
                int index = 1;
                String timeStamp = DateUtils.parseISO8601(new Date());
                for (ScspSolicitud solicitud : scspPeticion.getSolicitudes()) {
                    SolicitudTransmision solicitudTransmision = new SolicitudTransmision();
                    solicitudTransmision.setId(solicitud.getId());
                    if (solicitud.getDatosGenericos() != null) {
                        DatosGenericos datosGenericos = new DatosGenericos();
                        if (solicitud.getDatosGenericos().getEmisor() != null) {
                            Emisor emisor = new Emisor();
                            emisor.setNifEmisor(solicitud.getDatosGenericos().getEmisor().getNifEmisor());
                            emisor.setNombreEmisor(solicitud.getDatosGenericos().getEmisor().getNombreEmisor());
                            datosGenericos.setEmisor(emisor);
                        }
                        if (solicitud.getDatosGenericos().getSolicitante() != null) {
                            Solicitante solicitante = new Solicitante();
                            if (solicitud.getDatosGenericos().getSolicitante().getProcedimiento() != null) {
                                Procedimiento procedimiento = new Procedimiento();
                                procedimiento.setCodProcedimiento(
                                        solicitud.getDatosGenericos().getSolicitante().getProcedimiento().getCodProcedimiento());
                                procedimiento.setNombreProcedimiento(
                                        solicitud.getDatosGenericos().getSolicitante().getProcedimiento().getNombreProcedimiento());
                                solicitante.setProcedimiento(procedimiento);
                            }
                            if (solicitud.getDatosGenericos().getSolicitante().getFuncionario() != null) {
                                Funcionario funcionario = new Funcionario();
                                funcionario.setNombreCompletoFuncionario(
                                        solicitud.getDatosGenericos().getSolicitante().getFuncionario().getNombreCompletoFuncionario());
                                funcionario.setNifFuncionario(
                                        solicitud.getDatosGenericos().getSolicitante().getFuncionario().getNifFuncionario());
                                funcionario.setSeudonimo(
                                        solicitud.getDatosGenericos().getSolicitante().getFuncionario().getSeudonimo());
                                solicitante.setFuncionario(funcionario);
                            }
                            solicitante.setUnidadTramitadora(
                                    solicitud.getDatosGenericos().getSolicitante().getUnidadTramitadora());
                            solicitante.setCodigoUnidadTramitadora(
                                    solicitud.getDatosGenericos().getSolicitante().getCodigoUnidadTramitadora());
                            solicitante.setIdentificadorSolicitante(
                                    solicitud.getDatosGenericos().getSolicitante().getIdentificadorSolicitante());
                            solicitante.setNombreSolicitante(
                                    solicitud.getDatosGenericos().getSolicitante().getNombreSolicitante());
                            solicitante.setIdExpediente(
                                    solicitud.getDatosGenericos().getSolicitante().getIdExpediente());
                            solicitante.setFinalidad(
                                    solicitud.getDatosGenericos().getSolicitante().getFinalidad());
                            if (ScspConsentimiento.Si.equals(solicitud.getDatosGenericos().getSolicitante().getConsentimiento())) {
                                solicitante.setConsentimiento(Consentimiento.Si);
                            }
                            if (ScspConsentimiento.Ley.equals(solicitud.getDatosGenericos().getSolicitante().getConsentimiento())) {
                                solicitante.setConsentimiento(Consentimiento.Ley);
                            }
                            datosGenericos.setSolicitante(solicitante);
                        }
                        if (solicitud.getDatosGenericos().getTitular() != null) {
                            Titular titular = new Titular();
                            if (ScspTipoDocumentacion.CIF.equals(solicitud.getDatosGenericos().getTitular().getTipoDocumentacion())) {
                                titular.setTipoDocumentacion(TipoDocumentacion.CIF);
                            }
                            if (ScspTipoDocumentacion.CSV.equals(solicitud.getDatosGenericos().getTitular().getTipoDocumentacion())) {
                                titular.setTipoDocumentacion(TipoDocumentacion.CSV);
                            }
                            if (ScspTipoDocumentacion.DNI.equals(solicitud.getDatosGenericos().getTitular().getTipoDocumentacion())) {
                                titular.setTipoDocumentacion(TipoDocumentacion.DNI);
                            }
                            if (ScspTipoDocumentacion.NIE.equals(solicitud.getDatosGenericos().getTitular().getTipoDocumentacion())) {
                                titular.setTipoDocumentacion(TipoDocumentacion.NIE);
                            }
                            if (ScspTipoDocumentacion.NIF.equals(solicitud.getDatosGenericos().getTitular().getTipoDocumentacion())) {
                                titular.setTipoDocumentacion(TipoDocumentacion.NIF);
                            }
                            if (ScspTipoDocumentacion.Pasaporte.equals(solicitud.getDatosGenericos().getTitular().getTipoDocumentacion())) {
                                titular.setTipoDocumentacion(TipoDocumentacion.Pasaporte);
                            }
                            if (ScspTipoDocumentacion.NumeroIdentificacion.equals(solicitud.getDatosGenericos().getTitular().getTipoDocumentacion())) {
                                titular.setTipoDocumentacion(TipoDocumentacion.NumeroIdentificacion);
                            }
                            if (ScspTipoDocumentacion.Otros.equals(solicitud.getDatosGenericos().getTitular().getTipoDocumentacion())) {
                                titular.setTipoDocumentacion(TipoDocumentacion.Otros);
                            }
                            titular.setDocumentacion(solicitud.getDatosGenericos().getTitular().getDocumentacion());
                            titular.setNombreCompleto(solicitud.getDatosGenericos().getTitular().getNombreCompleto());
                            titular.setNombre(solicitud.getDatosGenericos().getTitular().getNombre());
                            titular.setApellido1(solicitud.getDatosGenericos().getTitular().getApellido1());
                            titular.setApellido2(solicitud.getDatosGenericos().getTitular().getApellido2());
                            datosGenericos.setTitular(titular);
                        }
                        if (solicitud.getDatosGenericos().getTransmision() != null) {
                            Transmision transmision = new Transmision();
                            transmision.setCodigoCertificado(solicitud.getDatosGenericos().getTransmision().getCodigoCertificado());
                            transmision.setIdSolicitud(solicitud.getDatosGenericos().getTransmision().getIdSolicitud());
                            transmision.setIdTransmision(solicitud.getDatosGenericos().getTransmision().getIdTransmision());
                            transmision.setFechaGeneracion(solicitud.getDatosGenericos().getTransmision().getFechaGeneracion());
                            datosGenericos.setTransmision(transmision);
                        } else if (multiple) {
                            Transmision transmision = new Transmision();
                            transmision.setCodigoCertificado(scspPeticion.getAtributos().getCodigoCertificado());
                            transmision.setIdSolicitud(String.format("%06d", index));
//							transmision.setIdTransmision(solicitud.getDatosGenericos().getTransmision().getIdTransmision());
                            transmision.setFechaGeneracion(timeStamp);
                            datosGenericos.setTransmision(transmision);
                        }
                        solicitudTransmision.setDatosGenericos(datosGenericos);
                    }
                    if (solicitud.getDatosEspecificos() != null) {
                        solicitudTransmision.setDatosEspecificos(
                                stringToElement(solicitud.getDatosEspecificos()));
                    }
                    solicitudesTransmision.add(solicitudTransmision);
                    index++;
                }
                solicitudes.setSolicitudTransmision(solicitudesTransmision);
                peticion.setSolicitudes(solicitudes);
            }
            return peticion;
        } else {
            return null;
        }
    }

    private ScspRespuesta toScspRespuesta(Respuesta respuesta) throws TransformerException {
        if (respuesta != null) {
            ScspRespuesta scspRespuesta = new ScspRespuesta();
            if (respuesta.getAtributos() != null) {
                ScspAtributos atributos = new ScspAtributos();
                atributos.setIdPeticion(respuesta.getAtributos().getIdPeticion());
                atributos.setNumElementos(respuesta.getAtributos().getNumElementos());
                atributos.setTimeStamp(respuesta.getAtributos().getTimeStamp());
                atributos.setCodigoCertificado(respuesta.getAtributos().getCodigoCertificado());
                if (respuesta.getAtributos().getEstado() != null) {
                    ScspEstado estado = new ScspEstado();
                    estado.setCodigoEstado(respuesta.getAtributos().getEstado().getCodigoEstado());
                    estado.setLiteralError(respuesta.getAtributos().getEstado().getLiteralError());
                    estado.setLiteralErrorSec(respuesta.getAtributos().getEstado().getLiteralErrorSec());
                    estado.setTiempoEstimadoRespuesta(respuesta.getAtributos().getEstado().getTiempoEstimadoRespuesta());
                    atributos.setEstado(estado);
                }
                scspRespuesta.setAtributos(atributos);
            }
            if (respuesta.getTransmisiones() != null && respuesta.getTransmisiones().getTransmisionDatos() != null) {
                List<ScspTransmisionDatos> transmisiones = new ArrayList<ScspTransmisionDatos>();
                for (TransmisionDatos transmisionDatos : respuesta.getTransmisiones().getTransmisionDatos()) {
                    ScspTransmisionDatos scspTransmisionDatos = new ScspTransmisionDatos();
                    if (transmisionDatos.getDatosGenericos() != null) {
                        ScspDatosGenericos datosGenericos = new ScspDatosGenericos();
                        if (transmisionDatos.getDatosGenericos().getEmisor() != null) {
                            ScspEmisor emisor = new ScspEmisor();
                            emisor.setNifEmisor(transmisionDatos.getDatosGenericos().getEmisor().getNifEmisor());
                            emisor.setNombreEmisor(transmisionDatos.getDatosGenericos().getEmisor().getNombreEmisor());
                            datosGenericos.setEmisor(emisor);
                        }
                        if (transmisionDatos.getDatosGenericos().getSolicitante() != null) {
                            ScspSolicitante solicitante = new ScspSolicitante();
                            if (transmisionDatos.getDatosGenericos().getSolicitante().getProcedimiento() != null) {
                                ScspProcedimiento procedimiento = new ScspProcedimiento();
                                procedimiento.setCodProcedimiento(
                                        transmisionDatos.getDatosGenericos().getSolicitante().getProcedimiento().getCodProcedimiento());
                                procedimiento.setNombreProcedimiento(
                                        transmisionDatos.getDatosGenericos().getSolicitante().getProcedimiento().getNombreProcedimiento());
                                solicitante.setProcedimiento(procedimiento);
                            }
                            if (transmisionDatos.getDatosGenericos().getSolicitante().getFuncionario() != null) {
                                ScspFuncionario funcionario = new ScspFuncionario();
                                funcionario.setNombreCompletoFuncionario(
                                        transmisionDatos.getDatosGenericos().getSolicitante().getFuncionario().getNombreCompletoFuncionario());
                                funcionario.setNifFuncionario(
                                        transmisionDatos.getDatosGenericos().getSolicitante().getFuncionario().getNifFuncionario());
                                funcionario.setSeudonimo(
                                        transmisionDatos.getDatosGenericos().getSolicitante().getFuncionario().getSeudonimo());
                                solicitante.setFuncionario(funcionario);
                            }
                            solicitante.setUnidadTramitadora(
                                    transmisionDatos.getDatosGenericos().getSolicitante().getUnidadTramitadora());
                            solicitante.setCodigoUnidadTramitadora(
                                    transmisionDatos.getDatosGenericos().getSolicitante().getCodigoUnidadTramitadora());
                            solicitante.setIdentificadorSolicitante(
                                    transmisionDatos.getDatosGenericos().getSolicitante().getIdentificadorSolicitante());
                            solicitante.setNombreSolicitante(
                                    transmisionDatos.getDatosGenericos().getSolicitante().getNombreSolicitante());
                            solicitante.setIdExpediente(
                                    transmisionDatos.getDatosGenericos().getSolicitante().getIdExpediente());
                            solicitante.setFinalidad(
                                    transmisionDatos.getDatosGenericos().getSolicitante().getFinalidad());
                            if (Consentimiento.Si.equals(transmisionDatos.getDatosGenericos().getSolicitante().getConsentimiento())) {
                                solicitante.setConsentimiento(ScspConsentimiento.Si);
                            }
                            if (Consentimiento.Ley.equals(transmisionDatos.getDatosGenericos().getSolicitante().getConsentimiento())) {
                                solicitante.setConsentimiento(ScspConsentimiento.Ley);
                            }
                            datosGenericos.setSolicitante(solicitante);
                        }
                        if (transmisionDatos.getDatosGenericos().getTitular() != null) {
                            ScspTitular titular = new ScspTitular();
                            if (TipoDocumentacion.CIF.equals(transmisionDatos.getDatosGenericos().getTitular().getTipoDocumentacion())) {
                                titular.setTipoDocumentacion(ScspTipoDocumentacion.CIF);
                            }
                            if (TipoDocumentacion.CSV.equals(transmisionDatos.getDatosGenericos().getTitular().getTipoDocumentacion())) {
                                titular.setTipoDocumentacion(ScspTipoDocumentacion.CSV);
                            }
                            if (TipoDocumentacion.DNI.equals(transmisionDatos.getDatosGenericos().getTitular().getTipoDocumentacion())) {
                                titular.setTipoDocumentacion(ScspTipoDocumentacion.DNI);
                            }
                            if (TipoDocumentacion.NIE.equals(transmisionDatos.getDatosGenericos().getTitular().getTipoDocumentacion())) {
                                titular.setTipoDocumentacion(ScspTipoDocumentacion.NIE);
                            }
                            if (TipoDocumentacion.NIF.equals(transmisionDatos.getDatosGenericos().getTitular().getTipoDocumentacion())) {
                                titular.setTipoDocumentacion(ScspTipoDocumentacion.NIF);
                            }
                            if (TipoDocumentacion.Pasaporte.equals(transmisionDatos.getDatosGenericos().getTitular().getTipoDocumentacion())) {
                                titular.setTipoDocumentacion(ScspTipoDocumentacion.Pasaporte);
                            }
                            if (TipoDocumentacion.NumeroIdentificacion.equals(transmisionDatos.getDatosGenericos().getTitular().getTipoDocumentacion())) {
                                titular.setTipoDocumentacion(ScspTipoDocumentacion.NumeroIdentificacion);
                            }
                            if (TipoDocumentacion.Otros.equals(transmisionDatos.getDatosGenericos().getTitular().getTipoDocumentacion())) {
                                titular.setTipoDocumentacion(ScspTipoDocumentacion.Otros);
                            }
                            titular.setDocumentacion(
                                    transmisionDatos.getDatosGenericos().getTitular().getDocumentacion());
                            titular.setNombreCompleto(
                                    transmisionDatos.getDatosGenericos().getTitular().getNombreCompleto());
                            titular.setNombre(
                                    transmisionDatos.getDatosGenericos().getTitular().getNombre());
                            titular.setApellido1(
                                    transmisionDatos.getDatosGenericos().getTitular().getApellido1());
                            titular.setApellido2(
                                    transmisionDatos.getDatosGenericos().getTitular().getApellido2());
                            datosGenericos.setTitular(titular);
                        }
                        if (transmisionDatos.getDatosGenericos().getTransmision() != null) {
                            ScspTransmision transmision = new ScspTransmision();
                            transmision.setCodigoCertificado(
                                    transmisionDatos.getDatosGenericos().getTransmision().getCodigoCertificado());
                            transmision.setIdSolicitud(
                                    transmisionDatos.getDatosGenericos().getTransmision().getIdSolicitud());
                            transmision.setIdTransmision(
                                    transmisionDatos.getDatosGenericos().getTransmision().getIdTransmision());
                            transmision.setFechaGeneracion(
                                    transmisionDatos.getDatosGenericos().getTransmision().getFechaGeneracion());
                            datosGenericos.setTransmision(transmision);
                        }
                        scspTransmisionDatos.setDatosGenericos(datosGenericos);
                    }
                    if (transmisionDatos.getDatosEspecificos() != null) {
                        if (transmisionDatos.getDatosEspecificos() instanceof Node) {
                            scspTransmisionDatos.setDatosEspecificos(
                                    nodeToString((Node) transmisionDatos.getDatosEspecificos()));
                        }
                    }
                    transmisiones.add(scspTransmisionDatos);
                }
                scspRespuesta.setTransmisiones(transmisiones);
            }
            return scspRespuesta;
        } else {
            return null;
        }
    }

    private ScspConfirmacionPeticion toScspConfirmacionPeticion(ConfirmacionPeticion confirmacionPeticion) {
        if (confirmacionPeticion != null) {
            ScspConfirmacionPeticion scspConfirmacionPeticion = new ScspConfirmacionPeticion();
            if (confirmacionPeticion.getAtributos() != null) {
                ScspAtributos atributos = new ScspAtributos();
                atributos.setIdPeticion(confirmacionPeticion.getAtributos().getIdPeticion());
                atributos.setNumElementos(confirmacionPeticion.getAtributos().getNumElementos());
                atributos.setTimeStamp(confirmacionPeticion.getAtributos().getTimeStamp());
                atributos.setCodigoCertificado(confirmacionPeticion.getAtributos().getCodigoCertificado());
                if (confirmacionPeticion.getAtributos().getEstado() != null) {
                    ScspEstado estado = new ScspEstado();
                    estado.setCodigoEstado(confirmacionPeticion.getAtributos().getEstado().getCodigoEstado());
                    estado.setLiteralError(confirmacionPeticion.getAtributos().getEstado().getLiteralError());
                    estado.setLiteralErrorSec(confirmacionPeticion.getAtributos().getEstado().getLiteralErrorSec());
                    estado.setTiempoEstimadoRespuesta(confirmacionPeticion.getAtributos().getEstado().getTiempoEstimadoRespuesta());
                    atributos.setEstado(estado);
                }
                scspConfirmacionPeticion.setAtributos(atributos);
            }
            return scspConfirmacionPeticion;
        } else {
            return null;
        }
    }

    private String nodeToString(Node node) throws TransformerException {
        if (node == null)
            return null;
        StringWriter writer = new StringWriter();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.transform(new DOMSource(node), new StreamResult(writer));
        return writer.toString();
    }

    private Element stringToElement(String xml) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new ByteArrayInputStream(xml.getBytes()));
        return document.getDocumentElement();
    }


    // V2
    // /////////////////////////////////////////////////////////////

    @Override
    @Transactional(readOnly = true)
    public List<es.caib.pinbal.client.recobriment.v2.Entitat> getEntitats() {
        log.debug("Cercant entitats");

        List<Entitat> entitats = entitatRepository.findActivesPerUsuari(SecurityContextHolder.getContext().getAuthentication().getName());
        return toEntitats(entitats);
    }

    @Override
    @Cacheable(value = "procediments", key = "#entitatCodi")
    @Transactional(readOnly = true)
    public List<Procediment> getProcediments(String entitatCodi) throws EntitatNotFoundException {
        log.debug("Cercant els procediments de l'entitat (codi=" + entitatCodi + ")");
        Entitat entitat = entitatRepository.findByCodi(entitatCodi);
        if (entitat == null)
            throw new EntitatNotFoundException();

        List<es.caib.pinbal.core.model.Procediment> procediments = procedimentRepository.findByEntitatOrderByNomAsc(entitat);
        return procediments != null ? toProcediments(procediments) : null;
    }

    @Override
    @Cacheable(value = "serveis")
    @Transactional(readOnly = true)
    public List<Servei> getServeis() {
        log.debug("Cercant tots els serveis");

        return serveiRepository.findAllServeisClient();
    }

    @Override
    @Cacheable(value = "serveisEntitat", key = "#entitatCodi")
    @Transactional(readOnly = true)
    public List<Servei> getServeisByEntitat(String entitatCodi) throws EntitatNotFoundException {
        log.debug("Cercant els serveis per a l'entitat (codi=" + entitatCodi + ")");
        Entitat entitat = entitatRepository.findByCodi(entitatCodi);
        if (entitat == null)
            throw new EntitatNotFoundException();

        return serveiRepository.findServeisClientByEntitatCodi(entitatCodi);
    }

    @Override
    @Cacheable(value = "serveisProcediment", key = "#procedimentCodi")
    @Transactional(readOnly = true)
    public List<Servei> getServeisByProcediment(String procedimentCodi) throws ProcedimentNotFoundException {
        log.debug("Cercant els serveis actius per al procediment (codi=" + procedimentCodi + ")");
        es.caib.pinbal.core.model.Procediment procediment = procedimentRepository.findByCodi(procedimentCodi);
        if (procediment == null)
            throw new ProcedimentNotFoundException();

        return serveiRepository.findServeisClientByProcedimentCodi(procedimentCodi);
    }

    @Override
    @Cacheable(value = "dadesEspecifiques", key = "#serveiCodi")
    @Transactional(readOnly = true)
    public List<DadaEspecifica> getDadesEspecifiquesByServei(String serveiCodi) throws ServeiNotFoundException {
        log.debug("Cercant les dades especifiques del servei (codi=" + serveiCodi + ")");
        ServeiConfig serveiConfig = serveiConfigRepository.findByServei(serveiCodi);
        if (serveiConfig == null)
            throw new ServeiNotFoundException(serveiCodi);

        List<ServeiCamp> serveiCamps = serveiCampRepository.findByServeiOrderByGrupOrdreAsc(serveiCodi);
        return toDadesEspecifiques(serveiCamps);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "enumerats", key = "#serveiCodi + ':' + #campPath + ':' + (#enumCodi == null ? 'null' : #enumCodi) + ':' + (#filtre == null ? 'null' : #filtre)")
    public List<ValorEnum> getValorsEnumByServei(String serveiCodi, String campPath, String enumCodi, String filtre) throws Exception {
        log.debug("Cercant els valors de l'enumerat (serveicodi={}, codi={}, filtre={})", serveiCodi, enumCodi, filtre);

        ServeiConfig serveiConfig = serveiConfigRepository.findByServei(serveiCodi);
        if (serveiConfig == null)
            throw new ServeiNotFoundException(serveiCodi);

        ServeiCamp serveiCamp = serveiCampRepository.findByPath(campPath);
        if (serveiCamp == null)
            throw new ServeiCampNotFoundException(campPath);

        IdiomaEnumDto idioma = IdiomaEnumDto.CA;
        List<ValorEnum> enumerat = new ArrayList<>();
        switch (enumCodi) {
            case "PAIS":
                idioma = getIdioma(filtre);
                enumerat.addAll(obtenirPaisos(idioma));
                break;
            case "PROVINCIA":
                idioma = getIdioma(filtre);
                enumerat.addAll(obtenirProvincies(idioma));
                break;
            case "MUNICIPI_3":
            case "MUNICIPI_5":
                enumerat.addAll(obtenirMunicipis(filtre, enumCodi));
                break;
            default:
                enumerat.addAll(obtenirEnumerat(
                        serveiCodi,
                        campPath,
                        filtre,
                        serveiConfig,
                        serveiCamp));
        }

        return enumerat;
    }

    private static IdiomaEnumDto getIdioma(String filtre) {
        IdiomaEnumDto idioma = IdiomaEnumDto.CA;
        if (filtre != null && "ES".equalsIgnoreCase(filtre))
            idioma = IdiomaEnumDto.ES;
        return idioma;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, List<String>> validatePeticio(String serveiCodi, PeticioSincrona peticio) {

        BindException errors = new BindException(peticio, "peticio");
        recobrimentV2Helper.validateDadesComunes(peticio.getDadesComunes(), serveiCodi, errors);
        recobrimentV2Helper.validateDadesSolicitud(peticio.getSolicitud(), serveiCodi, errors, serveiService);

        Map<String, List<String>> errorsCamps = errorsToMap(errors);

        return errorsCamps;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, List<String>> validatePeticio(String serveiCodi, PeticioAsincrona peticio) {

        BindException errors = new BindException(peticio, "peticio");
        recobrimentV2Helper.validateDadesComunes(peticio.getDadesComunes(), serveiCodi, errors);

        Map<String, List<String>> errorsSolicituds = recobrimentV2Helper.validateDadesSolicituds(peticio.getSolicituds(), serveiCodi, serveiService);
        Map<String, List<String>> errorsDadesComunes = errorsToMap(errors);
        errorsDadesComunes.putAll(errorsSolicituds);

        return errorsDadesComunes;
    }

    private static Map<String, List<String>> errorsToMap(BindException errors) {
        Map<String, List<String>> errorsDadesComunes = new HashMap<>();
        for (FieldError fieldError : errors.getFieldErrors()) {
            String campNom = fieldError.getField(); // Nom del camp
            String campErrorMsg = fieldError.getDefaultMessage(); // Missatge d'error

            // Si el camp no existeix al mapa, inicialitzar una nova llista
            if (!errorsDadesComunes.containsKey(campNom)) {
                errorsDadesComunes.put(campNom, new ArrayList<String>());
            }

            // Afegir el missatge d'error a la llista del camp
            errorsDadesComunes.get(campNom).add(campErrorMsg);
        }
        return errorsDadesComunes;
    }

    @Override
//    @Transactional
    public PeticioRespostaSincrona peticionSincrona(PeticioSincrona peticio) {
        PeticioRespostaSincrona respuesta = null;
        try {
            Peticion peticion = recobrimentV2Helper.toPeticion(peticio);
            ScspRespuesta scspRespuesta = toScspRespuesta(recobrimentHelper.peticionSincrona(peticion));
            respuesta = PeticioRespostaSincrona.builder()
                    .error(!scspRespuesta.getAtributos().getEstado().getCodigoEstado().startsWith("00"))
                    .messageError(scspRespuesta.getAtributos().getEstado().getLiteralError())
                    .resposta(scspRespuesta)
                    .build();
        } catch (Exception e) {
            log.error("Error al realitzar la petició sincrona amb solicitudId= " + (peticio.getSolicitud() != null ? peticio.getSolicitud().getId() : ""), e);
            respuesta = PeticioRespostaSincrona.builder()
                    .error(true)
                    .messageError(e.getMessage())
                    .build();
        }
        return respuesta;
    }

    @Override
//    @Transactional
    public PeticioRespostaAsincrona peticionAsincrona(PeticioAsincrona peticio) {
        PeticioRespostaAsincrona respuesta = null;
        try {
            Peticion peticion = recobrimentV2Helper.toPeticion(peticio);
            ScspConfirmacionPeticion scspConfirmacionPeticion = toScspConfirmacionPeticion(recobrimentHelper.peticionAsincrona(peticion));
            String errorMsg = scspConfirmacionPeticion.getAtributos().getEstado() != null
                    ? scspConfirmacionPeticion.getAtributos().getEstado().getLiteralError()
                    : null;
            respuesta = PeticioRespostaAsincrona.builder()
                    .error(errorMsg != null && !errorMsg.trim().isEmpty())
                    .messageError(errorMsg)
                    .resposta(scspConfirmacionPeticion)
                    .build();
        } catch (Exception e) {
            log.error("Error al realitzar la petició asincrona al servei= " + peticio.getDadesComunes().getServeiCodi(), e);
            respuesta = PeticioRespostaAsincrona.builder()
                    .error(true)
                    .messageError(e.getMessage())
                    .build();
        }
        return respuesta;
    }

    private List<ValorEnum> obtenirPaisos(IdiomaEnumDto idioma) {
        List<Pais> paisos = dadesExternesService.findPaisos(idioma);
        if (paisos == null)
            return new ArrayList<>();

        List<ValorEnum> paisosCodiValor = new ArrayList<>();
        for (Pais pais : paisos) {
            paisosCodiValor.add(ValorEnum.builder()
                    .codi(pais.getCodi_numeric())
                    .valor(pais.getNom())
                    .build());
        }
        return paisosCodiValor;
    }

    private List<ValorEnum> obtenirProvincies(IdiomaEnumDto idioma) {
        List<Provincia> provincias = dadesExternesService.findProvincies(idioma);
        if (provincias == null)
            return new ArrayList<>();

        List<ValorEnum> provinciasCodiValor = new ArrayList<>();
        for (Provincia provincia : provincias) {
            provinciasCodiValor.add(ValorEnum.builder()
                    .codi(provincia.getCodi())
                    .valor(provincia.getNom())
                    .build());
        }
        return provinciasCodiValor;
    }

    private List<ValorEnum> obtenirMunicipis(String filtre, String enumCodi) {
        if (filtre == null || filtre.isEmpty())
            throw new RuntimeException("No s'ha informat el codi de la província en el camp filte.");
        if (!filtre.matches("\\d+") || Integer.parseInt(filtre) >= 52) {
            throw new RuntimeException("El valor del codi de la província informada al camp filtre ha de ser un valor enter inferior a 52.");
        }
        filtre = String.format("%02d", Integer.parseInt(filtre));
        List<Municipi> municipis = dadesExternesService.findMunicipisPerProvincia(filtre);
        if (municipis == null)
            return new ArrayList<>();

        List<ValorEnum> municipisCodiValor = new ArrayList<>();
        for (Municipi municipi : municipis) {
            municipisCodiValor.add(ValorEnum.builder()
                    .codi("MUNICIPI_5".equals(enumCodi) ? municipi.getCodi() : municipi.getCodi() != null ? municipi.getCodi().substring(2) : null)
                    .valor(municipi.getNom())
                    .build());
        }
        return municipisCodiValor;
    }

    private List<ValorEnum> obtenirEnumerat(String serveiCodi, String campPath, String filtre, ServeiConfig serveiConfig, ServeiCamp serveiCamp) throws Exception {
        Tree<XmlHelper.DadesEspecifiquesNode> arbreDdadesEspecifiques = getScspHelper().generarArbreDadesEspecifiques(
                serveiCodi,
                serveiConfig.isActivaGestioXsd());
        XmlHelper.DadesEspecifiquesNode nodeEnum = trobarNodeAmbPath(
                arbreDdadesEspecifiques.getRootElement(),
//                enumCodi,
                campPath,
                new ArrayList<String>());
        if (nodeEnum == null)
            throw new RuntimeException("El camp " + campPath + "no s'ha torbat en l'XSD del servei " + serveiCodi);
        if (!nodeEnum.isEnum())
            throw new RuntimeException("El camp " + campPath + "no és un camp de tipus enumerat");

        List<String> enumValues = nodeEnum.getEnumValues();
        String[] enumDescripcions = serveiCamp.getEnumDescripcions();

        List<ValorEnum> enumerat = new ArrayList<>();
        boolean filtrar = filtre != null && !filtre.isEmpty();
        if (filtrar)
            filtre = filtre.toLowerCase();
        int index = 0;
        for (String enumVal: enumValues) {
            if (!filtrar || enumDescripcions[index].toLowerCase().contains(filtre)) {
                enumerat.add(ValorEnum.builder()
                        .codi(enumVal)
                        .valor(enumDescripcions[index])
                        .build());
            }
            index++;
        }
        return enumerat;
    }

    private XmlHelper.DadesEspecifiquesNode trobarNodeAmbPath(
            es.caib.pinbal.scsp.tree.Node<XmlHelper.DadesEspecifiquesNode> source,
            String pathToFind,
            List<String> currentPath) {
        if (source.getData() != null) {
            DadaEspecificaDto dada = new DadaEspecificaDto();
            dada.setPath(currentPath.toArray(new String[currentPath.size()]));
            dada.setNom(source.getData().getNom());
            if (dada.getPathAmbSeparadorDefault().equals(pathToFind))
                return source.getData();
        }
        if (source.getNumberOfChildren() > 0) {
            for (es.caib.pinbal.scsp.tree.Node<XmlHelper.DadesEspecifiquesNode> child: source.getChildren()) {
                currentPath.add(source.getData().getNom());
                XmlHelper.DadesEspecifiquesNode trobat = trobarNodeAmbPath(child, pathToFind, currentPath);
                if (trobat != null)
                    return trobat;
                currentPath.remove(currentPath.size() - 1);
            }
        }
        return null;
    }


    @Override
    public ScspRespuesta getResposta(String idPeticion) throws RecobrimentScspException, ConsultaNotFoundException {
        try {
            Consulta consulta = consultaRepository.findByScspPeticionId(idPeticion);
            if (consulta == null) {
                throw new ConsultaNotFoundException();
            }
            return toScspRespuesta(recobrimentHelper.getRespuesta(idPeticion));
        } catch (TransformerException ex) {
            throw new RecobrimentScspException(ex.getMessage(), ex);
        } catch (ScspException ex) {
            throw new RecobrimentScspException(ex.getMessage(), ex);
        }
    }

    @Override
    public ScspJustificante getJustificant(
            String idPeticion,
            String idSolicitud) throws RecobrimentScspException, ConsultaNotFoundException {
        try {
            Consulta consulta = consultaRepository.findByScspPeticionIdAndScspSolicitudId(idPeticion, idSolicitud);
            if (consulta == null) {
                throw new ConsultaNotFoundException();
            }
            JustificantDto justificant = recobrimentHelper.getJustificante(idPeticion, idSolicitud, false, true);
            ScspJustificante justificante = new ScspJustificante();
            justificante.setNom(justificant.getNom());
            justificante.setContentType(justificant.getContentType());
            justificante.setContingut(justificant.getContingut());
            return justificante;
        } catch (ScspException ex) {
            throw new RecobrimentScspException(
                    ex.getMessage(),
                    ex);
        }
    }

    @Override
    public ScspJustificante getJustificantImprimible(String idPeticion, String idSolicitud) throws RecobrimentScspException, ConsultaNotFoundException {
        try {
            Consulta consulta = consultaRepository.findByScspPeticionIdAndScspSolicitudId(idPeticion, idSolicitud);
            if (consulta == null) {
                throw new ConsultaNotFoundException();
            }
            JustificantDto justificant = recobrimentHelper.getJustificante(idPeticion, idSolicitud, true, true);
            ScspJustificante justificante = new ScspJustificante();
            justificante.setNom(justificant.getNom());
            justificante.setContentType(justificant.getContentType());
            justificante.setContingut(justificant.getContingut());
            return justificante;
        } catch (ScspException ex) {
            throw new RecobrimentScspException(
                    ex.getMessage(),
                    ex);
        }
    }

    @Override
    public String getJustificantCsv(String idPeticion, String idSolicitud) throws RecobrimentScspException, ConsultaNotFoundException {
        try {
            Consulta consulta = consultaRepository.findByScspPeticionIdAndScspSolicitudId(idPeticion, idSolicitud);
            if (consulta == null) {
                throw new ConsultaNotFoundException();
            }
            JustificantDto justificant = recobrimentHelper.getJustificante(idPeticion, idSolicitud, true, false);
            return justificant.getArxiuCsv();
        } catch (ScspException ex) {
            throw new RecobrimentScspException(
                    ex.getMessage(),
                    ex);
        }
    }

    @Override
    public String getJustificantUuid(String idPeticion, String idSolicitud) throws RecobrimentScspException, ConsultaNotFoundException {
        try {
            Consulta consulta = consultaRepository.findByScspPeticionIdAndScspSolicitudId(idPeticion, idSolicitud);
            if (consulta == null) {
                throw new ConsultaNotFoundException();
            }
            JustificantDto justificant = recobrimentHelper.getJustificante(idPeticion, idSolicitud, true, false);
            return justificant.getArxiuUuid();
        } catch (ScspException ex) {
            throw new RecobrimentScspException(
                    ex.getMessage(),
                    ex);
        }
    }


    // Conversors
    // ////////////////////////////////////////////////////

    private es.caib.pinbal.client.recobriment.v2.Entitat toEntitat(Entitat entitatEntity) {
        if (entitatEntity == null) return null;

        return es.caib.pinbal.client.recobriment.v2.Entitat.builder()
                .codi(entitatEntity.getCodi())
                .nom(entitatEntity.getNom())
                .cif(entitatEntity.getCif())
                .build();
    }

    private List<es.caib.pinbal.client.recobriment.v2.Entitat> toEntitats(List<Entitat> entitatEntities) {
        if (entitatEntities == null) return new ArrayList<>();

        List<es.caib.pinbal.client.recobriment.v2.Entitat> entitatList = new ArrayList<>();
        for (Entitat entity : entitatEntities) {
            entitatList.add(toEntitat(entity));
        }

        return entitatList;
    }

    private Procediment toProcediment(es.caib.pinbal.core.model.Procediment procedimentEntity) {
        if (procedimentEntity == null) return null;

        return Procediment.builder()
                .codi(procedimentEntity.getCodi())
                .nom(procedimentEntity.getNom())
                .organGestorDir3(procedimentEntity.getOrganGestor().getCodi())
                .actiu(procedimentEntity.isActiu())
                .build();
    }

    private List<Procediment> toProcediments(List<es.caib.pinbal.core.model.Procediment> procedimentEntities) {
        if (procedimentEntities == null) return new ArrayList<>();

        List<Procediment> procedimentsList = new ArrayList<>();
        for (es.caib.pinbal.core.model.Procediment entity : procedimentEntities) {
            procedimentsList.add(toProcediment(entity));
        }

        return procedimentsList;
    }

    private DadaEspecifica toDadaEspecifica(ServeiCamp serveiCamp) {
        if (serveiCamp == null) return null;

        return DadaEspecifica.builder()
                .codi(serveiCamp.getPath())
                .nom(obtenirUltimaPartDelPath(serveiCamp.getPath()))
                .tipus(toTipus(serveiCamp.getTipus()))
                .format(serveiCamp.getDataFormat())
                .etiqueta(serveiCamp.getEtiqueta())
                .comentari(serveiCamp.getComentari())
                .valorDefecte(serveiCamp.getValorPerDefecte())
                .obligatori(serveiCamp.isObligatori())
                .modificable(serveiCamp.isModificable())
                .campCondicionant(serveiCamp.getCampPare() != null ? serveiCamp.getCampPare().getPath() : null)
                .valorCondicionant(serveiCamp.getValorPare())
                .validacio(obtenirValidacio(serveiCamp))
                .grup(obtenirNomGrup(serveiCamp.getGrup()))
                .build();
    }

    private List<DadaEspecifica> toDadesEspecifiques(List<ServeiCamp> serveiCamps) {
        if (serveiCamps == null) return new ArrayList<>();

        List<DadaEspecifica> dadesEspecifiques = new ArrayList<>();
        for (ServeiCamp serveiCamp : serveiCamps) {
            dadesEspecifiques.add(toDadaEspecifica(serveiCamp));
        }

        return dadesEspecifiques;
    }

    private DadaTipusEnum toTipus(ServeiCampTipus tipus) {
        if (tipus == null) return null;

        switch (tipus) {
            case TEXT:
                return DadaTipusEnum.TEXT;
            case NUMERIC:
                return DadaTipusEnum.NUMERIC;
            case DATA:
                return DadaTipusEnum.DATE;
            case BOOLEA:
                return DadaTipusEnum.BOOLEAN;
            case DOC_IDENT:
                return DadaTipusEnum.DOC_IDENTITAT;
            case ADJUNT_BINARI:
            case ADJUNT_XML:
                return DadaTipusEnum.FILE;
            default:
                return DadaTipusEnum.ENUM;
        }
    }

    private String obtenirUltimaPartDelPath(String path) {
        if (path == null || path.isEmpty()) {
            return ""; // Retornem una cadena buida en cas d'entrada nul·la o buida
        }
        int lastSeparatorIndex = path.lastIndexOf('/');
        return lastSeparatorIndex == -1 ? path : path.substring(lastSeparatorIndex + 1);
    }

    public String obtenirNomGrup(ServeiCampGrup grup) {
        if (grup == null) return null;

        if (grup.getPare() == null) {
            return grup.getNom(); // Si no té pare, simplement retornem el nom del grup actual
        }
        return obtenirNomGrup(grup.getPare()) + "/" + grup.getNom(); // Recursivament afegim el nom del pare
    }

    private Validacio obtenirValidacio(ServeiCamp serveiCamp) {
        if (serveiCamp == null) return null;

        ServeiCampTipus tipus = serveiCamp.getTipus();

        if (ServeiCampTipus.TEXT.equals(tipus)) {
            return obtenirValidacioText(serveiCamp);
        }
        if (ServeiCampTipus.NUMERIC.equals(tipus)) {
            return obtenirValidacioNumeric(serveiCamp);
        }
        if (ServeiCampTipus.DATA.equals(tipus)) {
            return obtenirValidacioData(serveiCamp);
        }

        return null;
    }

    private Validacio obtenirValidacioText(ServeiCamp serveiCamp) {
        String validacioRegexp = serveiCamp.getValidacioRegexp();
        if (validacioRegexp != null && !validacioRegexp.isEmpty()) {
            return Validacio.builder()
                    .tipus(Validacio.ValidacioTipus.EXPRESSIO_REGULAR)
                    .regexp(validacioRegexp)
                    .build();
        }
        return null;
    }

    private Validacio obtenirValidacioNumeric(ServeiCamp serveiCamp) {
        if (serveiCamp.getValidacioMin() != null && serveiCamp.getValidacioMax() != null) {
            return Validacio.builder()
                    .tipus(Validacio.ValidacioTipus.NUM_RANG)
                    .min(serveiCamp.getValidacioMin())
                    .max(serveiCamp.getValidacioMax())
                    .build();
        }
        if (serveiCamp.getValidacioMin() != null) {
            return Validacio.builder()
                    .tipus(Validacio.ValidacioTipus.NUM_MINIM)
                    .min(serveiCamp.getValidacioMin())
                    .build();
        }
        if (serveiCamp.getValidacioMax() != null) {
            return Validacio.builder()
                    .tipus(Validacio.ValidacioTipus.NUM_MAXIM)
                    .max(serveiCamp.getValidacioMax())
                    .build();
        }
        return null;
    }

    private Validacio obtenirValidacioData(ServeiCamp serveiCamp) {
        if (serveiCamp.getValidacioDataCmpCamp2() != null && serveiCamp.getValidacioDataCmpOperacio() != null) {
            String diferencia = serveiCamp.getValidacioDataCmpNombre() != null
                    ? serveiCamp.getValidacioDataCmpNombre() + " " +
                    (serveiCamp.getValidacioDataCmpTipus() != null
                            ? serveiCamp.getValidacioDataCmpTipus().name()
                            : ServeiCampDto.ServeiCampDtoValidacioDataTipus.DIES.name())
                    : null;

            return Validacio.builder()
                    .tipus(diferencia != null ? Validacio.ValidacioTipus.DATA_DIFERENCIA : Validacio.ValidacioTipus.DATA_COMPARACIO)
                    .campComparacio(serveiCamp.getValidacioDataCmpCamp2().getPath())
                    .operacio(Validacio.ValidacioOperacio.valueOf(serveiCamp.getValidacioDataCmpOperacio().name()))
                    .diferencia(diferencia)
                    .build();
        }
        return null;
    }


	private ScspHelper getScspHelper() {
		if (scspHelper == null) {
			scspHelper = new ScspHelper(applicationContext, messageSource);
		}
		return scspHelper;
	}

}
