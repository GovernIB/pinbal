/**
 * 
 */
package es.caib.pinbal.core.ws;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

import es.caib.pinbal.core.dto.ConsultaDto;
import es.caib.pinbal.core.dto.ConsultaDto.Consentiment;
import es.caib.pinbal.core.dto.ConsultaDto.DocumentTipus;
import es.caib.pinbal.core.dto.RecobrimentSolicitudDto;
import es.caib.pinbal.core.dto.RespostaAtributsDto;
import es.caib.pinbal.core.service.ConsultaService;
import es.caib.pinbal.core.service.exception.ConsultaNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentServeiNotFoundException;
import es.caib.pinbal.core.service.exception.ServeiNotAllowedException;
import es.caib.pinbal.scsp.ScspHelper;
import es.scsp.bean.common.Atributos;
import es.scsp.bean.common.ConfirmacionPeticion;
import es.scsp.bean.common.DatosGenericos;
import es.scsp.bean.common.Estado;
import es.scsp.bean.common.Peticion;
import es.scsp.bean.common.Respuesta;
import es.scsp.bean.common.Solicitante;
import es.scsp.bean.common.SolicitudTransmision;
import es.scsp.bean.common.Titular;
import es.scsp.bean.common.Transmision;
import es.scsp.common.exceptions.ScspException;

/**
 * Implementació dels mètodes per al recobriment de les peticions
 * SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
@WebService(
		name = "Recobriment",
		serviceName = "RecobrimentService",
		portName = "RecobrimentServicePort",
		targetNamespace = "http://www.caib.es/pinbal/ws/recobriment")
public class RecobrimentImpl implements Recobriment {

	@Autowired
	private ConsultaService consultaService;
	@Autowired
	private ScspHelper scspHelper;



	@Override
	public Respuesta peticionSincrona(
			Peticion peticion) throws ScspException {
		LOGGER.debug("Processant petició síncrona al servei web del recobriment");
		List<RecobrimentSolicitudDto> solicituds = validarIObtenirSolicituds(
				peticion,
				1);
		String codigoCertificado = peticion.getAtributos().getCodigoCertificado();
		try {
			ConsultaDto consulta = novaConsultaRecobriment(
					codigoCertificado,
					solicituds.get(0));
			if (consulta.getError() != null) {
				throw getErrorValidacio(
						consulta.getRespostaEstadoCodigo(),
						consulta.getRespostaEstadoError());
			}
			return scspHelper.recuperarRespuestaScsp(consulta.getScspPeticionId());
		} catch (EntitatNotFoundException ex) {
			throw getErrorValidacio(
					"0227",
					"No s'ha trobat l'entitat especificada");
		} catch (ProcedimentNotFoundException ex) {
			throw getErrorValidacio(
					"0227",
					"No s'ha trobat el procediment especificat");
		} catch (ProcedimentServeiNotFoundException ex) {
			throw getErrorValidacio(
					"0227",
					"No s'ha trobat el procediment-servei especificat");
		} catch (ConsultaNotFoundException ex) {
			throw getErrorValidacio(
					"0227",
					"No s'ha trobat la consulta una vegada creada");
		} catch (ServeiNotAllowedException ex) {
			throw getErrorValidacio(
					"0227",
					"L'usuari no te permisos per a realitzar aquesta consulta");
		} catch (ScspException ex) {
			throw ex;
		} catch (Exception ex) {
			LOGGER.error("Error en la consulta SCSP", ex);
			throw getErrorValidacio(
					"0227",
					"Error en la consulta SCSP: " + ExceptionUtils.getStackTrace(ex));
		}
	}

	@Override
	public ConfirmacionPeticion peticionAsincrona(
			Peticion peticion) throws ScspException {
		LOGGER.debug("Processant petició asíncrona al servei web del recobriment");
		List<RecobrimentSolicitudDto> solicituds = validarIObtenirSolicituds(
				peticion,
				-1);
		String codigoCertificado = peticion.getAtributos().getCodigoCertificado();
		try {
			ConsultaDto consulta = consultaService.novaConsultaRecobrimentMultiple(
					codigoCertificado,
					solicituds);
			if (consulta.getError() != null) {
				throw getErrorValidacio(
						consulta.getRespostaEstadoCodigo(),
						consulta.getRespostaEstadoError());
			}
			RespostaAtributsDto atributs = consulta.getRespostaAtributs();
			ConfirmacionPeticion confirmacionPeticion = new ConfirmacionPeticion();
			Atributos atributos = new Atributos();
			atributos.setCodigoCertificado(codigoCertificado);
			Estado estado = new Estado();
			estado.setCodigoEstado(atributs.getEstatCodi());
			estado.setCodigoEstadoSecundario(atributs.getEstatCodiSecundari());
			estado.setLiteralError(atributs.getEstatErrorLiteral());
			estado.setTiempoEstimadoRespuesta(atributs.getEstatTempsEstimatResposta());
			atributos.setEstado(estado);
			atributos.setIdPeticion(atributs.getPeticioId());
			atributos.setNumElementos(atributs.getNumElements());
			atributos.setTimeStamp(atributs.getTimestamp());
			confirmacionPeticion.setAtributos(atributos);
			return confirmacionPeticion;
		} catch (EntitatNotFoundException ex) {
			throw getErrorValidacio(
					"0227",
					"No s'ha trobat l'entitat especificada");
		} catch (ProcedimentNotFoundException ex) {
			throw getErrorValidacio(
					"0227",
					"No s'ha trobat el procediment especificat");
		} catch (ProcedimentServeiNotFoundException ex) {
			throw getErrorValidacio(
					"0227",
					"No s'ha trobat el procediment-servei especificat");
		} catch (ServeiNotAllowedException ex) {
			throw getErrorValidacio(
					"0227",
					"L'usuari no te permisos per a realitzar aquesta consulta");
		} catch (ScspException ex) {
			throw ex;
		} catch (Exception ex) {
			LOGGER.error("Error en la consulta SCSP", ex);
			throw getErrorValidacio(
					"0227",
					"Error en la consulta SCSP: " + ExceptionUtils.getStackTrace(ex));
		}
	}

	@Override
	public Respuesta getRespuesta(
			String idpeticion) throws ScspException {
		throw getErrorValidacio(
				"0227",
				"Mètode no implementat");
	}



	private List<RecobrimentSolicitudDto> validarIObtenirSolicituds(
			Peticion peticion,
			int maxSolicituds) throws ScspException {
		if (peticion == null)
			throw getErrorValidacio(
					"0226",
					"No s'ha trobat l'element peticion");
		if (peticion.getAtributos() == null)
			throw getErrorValidacio(
					"0226",
					"No s'ha trobat l'element peticion.atributos");
		if (peticion.getAtributos().getCodigoCertificado() == null)
			throw getErrorValidacio(
					"0226",
					"No s'ha trobat l'element peticion.atributos.codigoCertificado");
		if (peticion.getSolicitudes() == null)
			throw getErrorValidacio(
					"0226",
					"No s'ha trobat l'element peticion.solicitudes");
		if (peticion.getSolicitudes().getSolicitudTransmision() == null)
			throw getErrorValidacio(
					"0226",
					"No s'ha trobat cap element peticion.solicitudes.solicitudTransmision");
		if (maxSolicituds != -1) {
			int numSolicitudes = peticion.getSolicitudes().getSolicitudTransmision().size();
			if (numSolicitudes > maxSolicituds)
				throw getErrorValidacio(
						"0226",
						"S'ha excedit el màxim nombre de sol·licituds permeses en la petició (màxim=" + maxSolicituds + ")");
		}
		List<RecobrimentSolicitudDto> solicituds = new ArrayList<RecobrimentSolicitudDto>();
		int index = 0;
		for (SolicitudTransmision st: peticion.getSolicitudes().getSolicitudTransmision()) {
			RecobrimentSolicitudDto solicitud = new RecobrimentSolicitudDto();
			DatosGenericos datosGenericos = st.getDatosGenericos();
			if (datosGenericos == null)
				throw getErrorValidacio(
						"0226",
						"No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos (solicitudIndex=" + index + ")");
			if (maxSolicituds != 1) {
				Transmision transmision = datosGenericos.getTransmision();
				if (transmision == null)
					throw getErrorValidacio(
							"0226",
							"No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.transmision (solicitudIndex=" + index + ")");
				if (transmision.getIdSolicitud() == null)
					throw getErrorValidacio(
							"0226",
							"No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.transmision.idSolicitud (solicitudIndex=" + index + ")");
				solicitud.setId(transmision.getIdSolicitud());
			} else {
				solicitud.setId("000001");
			}
			if (datosGenericos.getSolicitante() == null)
				throw getErrorValidacio(
						"0226",
						"No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante (solicitudIndex=" + index + ")");
			Solicitante solicitante = datosGenericos.getSolicitante();
			if (solicitante.getIdentificadorSolicitante() == null)
				throw getErrorValidacio(
						"0226",
						"No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.identificadorSolicitante (solicitudIndex=" + index + ")");
			solicitud.setEntitatCif(solicitante.getIdentificadorSolicitante());
			if (solicitante.getFinalidad() == null)
				throw getErrorValidacio(
						"0226",
						"No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.finalidad (solicitudIndex=" + index + ")");
			solicitud.setFinalitat(solicitante.getFinalidad());
			if (solicitante.getConsentimiento() == null)
				throw getErrorValidacio(
						"0226",
						"No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.consentimiento (solicitudIndex=" + index + ")");
			switch (solicitante.getConsentimiento()) {
			case Si:
				solicitud.setConsentiment(Consentiment.Si);
				break;
			case Ley:
				solicitud.setConsentiment(Consentiment.Llei);
				break;
			}
			if (solicitante.getUnidadTramitadora() == null)
				throw getErrorValidacio(
						"0226",
						"No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.unidadTramitadora (solicitudIndex=" + index + ")");
			solicitud.setDepartamentNom(solicitante.getUnidadTramitadora());
			solicitud.setExpedientId(solicitante.getIdExpediente());
			if (solicitante.getProcedimiento() == null)
				throw getErrorValidacio(
						"0226",
						"No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.procedimiento (solicitudIndex=" + index + ")");
			if (solicitante.getProcedimiento().getCodProcedimiento() == null)
				throw getErrorValidacio(
						"0226",
						"No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.procedimiento.codProcedimiento (solicitudIndex=" + index + ")");
			solicitud.setProcedimentCodi(solicitante.getProcedimiento().getCodProcedimiento());
			if (solicitante.getFuncionario() == null)
				throw getErrorValidacio(
						"0226",
						"No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.funcionario (solicitudIndex=" + index + ")");
			if (solicitante.getFuncionario().getNifFuncionario() == null)
				throw getErrorValidacio(
						"0226",
						"No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.funcionario.nifFuncionario (solicitudIndex=" + index + ")");
			solicitud.setFuncionariNif(solicitante.getFuncionario().getNifFuncionario());
			if (solicitante.getFuncionario().getNombreCompletoFuncionario() == null)
				throw getErrorValidacio(
						"0226",
						"No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.funcionario.nombreCompletoFuncionario (solicitudIndex=" + index + ")");
			solicitud.setFuncionariNom(solicitante.getFuncionario().getNombreCompletoFuncionario());
			if (datosGenericos.getTitular() != null) {
				Titular titular = datosGenericos.getTitular();
				if (titular.getDocumentacion() != null) {
					solicitud.setTitularDocumentNum(titular.getDocumentacion());
					if (titular.getTipoDocumentacion() == null)
						throw getErrorValidacio(
								"0226",
								"No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.titular.tipoDocumentacion (solicitudIndex=" + index + ")");
					switch (titular.getTipoDocumentacion()) {
					case CIF:
						solicitud.setTitularDocumentTipus(DocumentTipus.CIF);
						break;
					case DNI:
						solicitud.setTitularDocumentTipus(DocumentTipus.DNI);
						break;
					case NIE:
						solicitud.setTitularDocumentTipus(DocumentTipus.NIE);
						break;
					case NIF:
						solicitud.setTitularDocumentTipus(DocumentTipus.NIF);
						break;
					case Pasaporte:
						solicitud.setTitularDocumentTipus(DocumentTipus.Passaport);
						break;
					}
				}
				solicitud.setTitularNom(titular.getNombre());
				solicitud.setTitularLlinatge1(titular.getApellido1());
				solicitud.setTitularLlinatge2(titular.getApellido2());
				solicitud.setTitularNomComplet(titular.getNombreCompleto());
			}
			solicitud.setDadesEspecifiques(
					(Element)st.getDatosEspecificos());
			solicituds.add(solicitud);
			index++;
		}
		return solicituds;
	}

	private ConsultaDto novaConsultaRecobriment(
			String serveiCodi,
			RecobrimentSolicitudDto solicitud) throws EntitatNotFoundException, ProcedimentNotFoundException, ProcedimentServeiNotFoundException, ConsultaNotFoundException, ServeiNotAllowedException, es.caib.pinbal.core.service.exception.ScspException {
		if (consultaService.isOptimitzarTransaccionsNovaConsulta()) {
			ConsultaDto consultaInit = consultaService.novaConsultaRecobrimentInit(
					serveiCodi,
					solicitud);
			consultaService.novaConsultaRecobrimentEnviament(
					consultaInit.getId(),
					solicitud);
			return consultaService.novaConsultaEstat(
					consultaInit.getId());
		} else {
			return consultaService.novaConsultaRecobriment(
					serveiCodi,
					solicitud);
		}
	}

	private ScspException getErrorValidacio(
			String codi,
			String missatge) {
		return new ScspException(missatge, codi);
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(RecobrimentImpl.class);

}
