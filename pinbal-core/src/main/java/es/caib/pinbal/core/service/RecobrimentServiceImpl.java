/**
 * 
 */
package es.caib.pinbal.core.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

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
import es.caib.pinbal.core.dto.JustificantDto;
import es.caib.pinbal.core.helper.RecobrimentHelper;
import es.caib.pinbal.core.service.exception.RecobrimentScspException;
import es.caib.pinbal.core.service.exception.RecobrimentScspValidationException;
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

/**
 * Implementació dels mètodes per a fer peticions al recobriment SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class RecobrimentServiceImpl implements RecobrimentService {

	@Autowired
	private RecobrimentHelper recobrimentHelper;

	@Override
	public ScspRespuesta peticionSincrona(
			ScspPeticion peticion) throws RecobrimentScspException {
		try {
			return toScspRespuesta(
					recobrimentHelper.peticionSincrona(toPeticion(peticion)));
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
					recobrimentHelper.peticionAsincrona(
							toPeticion(peticion)));
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
			JustificantDto justificant = recobrimentHelper.getJustificante(idPeticion, idSolicitud);
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

	private Peticion toPeticion(ScspPeticion scspPeticion) throws ParserConfigurationException, SAXException, IOException {
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
				for (ScspSolicitud solicitud: scspPeticion.getSolicitudes()) {
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
							titular.setDocumentacion(
									solicitud.getDatosGenericos().getTitular().getDocumentacion());
							titular.setNombreCompleto(
									solicitud.getDatosGenericos().getTitular().getNombreCompleto());
							titular.setNombre(
									solicitud.getDatosGenericos().getTitular().getNombre());
							titular.setApellido1(
									solicitud.getDatosGenericos().getTitular().getApellido1());
							titular.setApellido2(
									solicitud.getDatosGenericos().getTitular().getApellido2());
							datosGenericos.setTitular(titular);
						}
						if (solicitud.getDatosGenericos().getTransmision() != null) {
							Transmision transmision = new Transmision();
							transmision.setCodigoCertificado(
									solicitud.getDatosGenericos().getTransmision().getCodigoCertificado());
							transmision.setIdSolicitud(
									solicitud.getDatosGenericos().getTransmision().getIdSolicitud());
							transmision.setIdTransmision(
									solicitud.getDatosGenericos().getTransmision().getIdTransmision());
							transmision.setFechaGeneracion(
									solicitud.getDatosGenericos().getTransmision().getFechaGeneracion());
							datosGenericos.setTransmision(transmision);
						}
						solicitudTransmision.setDatosGenericos(datosGenericos);
					}
					if (solicitud.getDatosEspecificos() != null) {
						solicitudTransmision.setDatosEspecificos(
								stringToElement(solicitud.getDatosEspecificos()));
					}
					solicitudesTransmision.add(solicitudTransmision);
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
				for (TransmisionDatos transmisionDatos: respuesta.getTransmisiones().getTransmisionDatos()) {
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
									nodeToString((Node)transmisionDatos.getDatosEspecificos()));
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

}
