package es.scsp.common.interceptors;

import es.scsp.bean.common.confirmacion.ConfirmacionPeticion;
import es.scsp.bean.common.peticion.Estado;
import es.scsp.bean.common.peticion.Peticion;
import es.scsp.bean.common.peticion.SolicitudTransmision;
import es.scsp.bean.common.respuesta.Funcionario;
import es.scsp.bean.common.respuesta.Respuesta;
import es.scsp.bean.common.respuesta.TransmisionDatos;
import es.scsp.bean.common.solicitud.SolicitudRespuesta;
import es.scsp.common.core.ServiceManager;
import es.scsp.common.dao.PeticionRespuestaDao;
import es.scsp.common.dao.ServicioDao;
import es.scsp.common.dao.TipoMensajeDao;
import es.scsp.common.dao.TokenDao;
import es.scsp.common.dao.TransmisionDao;
import es.scsp.common.domain.core.PeticionRespuesta;
import es.scsp.common.domain.core.Servicio;
import es.scsp.common.domain.core.TipoMensaje;
import es.scsp.common.domain.core.Token;
import es.scsp.common.domain.core.Transmision;
import es.scsp.common.exceptions.ScspClientInterceptorException;
import es.scsp.common.exceptions.ScspException;
import es.scsp.common.utils.DateUtils;
import es.scsp.common.utils.XMLUtils;
import es.scsp.common.utils.xpath.XpathManager;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.SecretKey;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jaxen.JaxenException;
import org.springframework.context.ApplicationContext;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapMessage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Override de AlmacenarMensajeAbstract per corregir un bug de la llibreria vendor.
 *
 * El codi original (5.0.11), a processTransmisiones(), serialitza l'element de la
 * transmissió cridant Element.toString() i el guarda a core_transmision.xml_transmision.
 * A la versió anterior (4.26.1, la que corre en producció) el mateix codi funcionava
 * perquè els nodes eren org.apache.axiom.om.OMElement (Axis2/AXIOM), la implementació
 * del qual sí serialitza a XML dins toString(). En migrar de Axis2/AXIOM a Spring-WS
 * amb DOM estàndard (org.w3c.dom.Element), el codi es va deixar igual, però
 * Element.toString() (JDK/Xerces) no serialitza res: sempre retorna
 * "[NodeName: null]" (per espec DOM, getNodeValue() d'un element és null). Això fa
 * que xml_transmision quedi sempre amb el literal "[TransmisionDatos: null]" per a
 * qualsevol consulta real (no mock), independentment del contingut de la resposta.
 *
 * Fix: usar XMLUtils.nodeToString(...), la mateixa utilitat que aquesta classe ja fa
 * servir a manageRespuesta()/manageConfirmacion() per serialitzar altres nodes DOM.
 *
 * Resta del fitxer: còpia fidel de la classe original (descompilada del bytecode de
 * scsp-core-5.0.11.jar, no hi ha jar de fonts disponible), sense altres canvis de
 * comportament.
 */
public abstract class AlmacenarMensajeAbstract extends ScspClientHandler {
	private static Log LOG = LogFactory.getLog(AlmacenarMensajeAbstract.class);
	private static final String XPathSolicitudTransmision = "//*[local-name()='SolicitudTransmision']";
	private static final String XPathTransmisionDatos = "//*[local-name()='TransmisionDatos']";
	protected static final String ALGORITMO_AES128 = "http://www.w3.org/2001/04/xmlenc#aes128-cbc";
	protected static final String ALGORITMO_AES256 = "http://www.w3.org/2001/04/xmlenc#aes256-cbc";
	protected static final String ALGORITMO_AES192 = "http://www.w3.org/2001/04/xmlenc#aes192-cbc";
	protected static final String ALGORITMO_TDES = "http://www.w3.org/2001/04/xmlenc#DESede";
	protected static final String ALGORITMO_TDES_2 = "http://www.w3.org/2001/04/xmlenc#tripledes-cbc";
	protected static final String NAME_AES128 = "AES128";
	protected static final String NAME_AES256 = "AES256";
	protected static final String NAME_AES192 = "AES192";
	protected static final String NAME_TDES = "DESede";

	public AlmacenarMensajeAbstract(ApplicationContext context) {
		super(context);
	}

	protected PeticionRespuesta generateRespuesta(Peticion peticion) {
		Validate.notNull(peticion, "No se ha definido el objeto peticion.");
		Validate.notNull(peticion.getAtributos(), "La peticion no contiene atributos.");
		Validate.notEmpty(peticion.getAtributos().getCodigoCertificado(), "No se ha definido el tipo de certificado.");
		Estado estado = peticion.getAtributos().getEstado();
		PeticionRespuesta tmp = new PeticionRespuesta();
		ServicioDao daoServicio = (ServicioDao) this.getBean("servicioDao");
		Servicio servicio = daoServicio.select(peticion.getAtributos().getCodigoCertificado());
		Validate.notNull(servicio, "No se ha encontrado en la base de datos ningun certificado con ese código.");
		tmp.setServicio(servicio);
		tmp.setFechaPeticion(new Timestamp(System.currentTimeMillis()));
		tmp.setFechaRespuesta(new Timestamp(System.currentTimeMillis()));
		tmp.setIdPeticion(peticion.getAtributos().getIdPeticion());
		if (estado != null) {
			tmp.setEstado(estado.getCodigoEstado());
			tmp.setEstadosecundario(estado.getCodigoEstadoSecundario());
			long tiempoRespuesta = (long) (36000 * estado.getTiempoEstimadoRespuesta()) + System.currentTimeMillis();
			tmp.setTer(new Timestamp(tiempoRespuesta));
		} else {
			tmp.setEstado("0001");
			tmp.setTer((Date) null);
		}

		tmp.setFechaPeticion(new Timestamp(System.currentTimeMillis()));
		tmp.setNumeroEnvios(new Integer(0));
		return tmp;
	}

	protected String getKeyEncodedSender(MessageContext messageCtx) {
		String secretKeyEncoded = null;
		SecretKey sKey = (SecretKey) messageCtx.getProperty("SCC_SYMMETRIC_KEY");
		if (sKey != null) {
			secretKeyEncoded = new String(Base64.getEncoder().encode(sKey.getEncoded()));
		}

		return secretKeyEncoded;
	}

	protected String getKeyEncodedReceiver(MessageContext messageCtx) {
		return (String) messageCtx.getProperty("encryptionSymetricKey");
	}

	protected String getAlgorithmSender(Element message, MessageContext messageCtx) {
		if (message != null) {
			String xpath = String.format("//*[local-name()='%s']", "EncryptedData") + "/" + String.format("*[local-name()='%s']", "EncryptionMethod");
			Element encrytionMethod = this.evalXPathExpElement(xpath, message);
			if (encrytionMethod != null) {
				String algorithm = encrytionMethod.getAttribute("Algorithm");
				if (algorithm == null) {
					SecretKey sKey = (SecretKey) messageCtx.getProperty("SCC_SYMMETRIC_KEY");
					if (sKey != null) {
						return sKey.getAlgorithm();
					}
				}

				if (algorithm.equals("http://www.w3.org/2001/04/xmlenc#aes128-cbc")) {
					return "AES128";
				}

				if (algorithm.equals("http://www.w3.org/2001/04/xmlenc#aes192-cbc")) {
					return "AES192";
				}

				if (algorithm.equals("http://www.w3.org/2001/04/xmlenc#aes256-cbc")) {
					return "AES256";
				}

				if (algorithm.equals("http://www.w3.org/2001/04/xmlenc#DESede") || algorithm.equals("http://www.w3.org/2001/04/xmlenc#tripledes-cbc")) {
					return "DESede";
				}
			}
		}

		return null;
	}

	protected boolean manageRespuesta(MessageContext messageCtx, Object objectRequest) throws ScspException {
		PeticionRespuestaDao dbPeticion = (PeticionRespuestaDao) this.getBean("peticionRespuestaDao");
		Respuesta respuesta = (Respuesta) objectRequest;
		PeticionRespuesta entity = dbPeticion.select(respuesta.getAtributos().getIdPeticion());
		if (entity == null) {
			String msg = "La petición con identificador " + respuesta.getAtributos().getIdPeticion() + ", no existe";
			String idpeticion = respuesta.getAtributos().getIdPeticion();
			String codCertificado = respuesta.getAtributos().getCodigoCertificado();
			String[] args = new String[]{msg};
			throw ScspException.getScspException((Throwable) null, "0244", args, idpeticion, codCertificado);
		} else {
			String codigoEstado = "";
			int ter = 0;
			boolean codigoEstadoAsignado = false;
			if (respuesta.getAtributos() != null && respuesta.getAtributos().getEstado() != null) {
				codigoEstado = respuesta.getAtributos().getEstado().getCodigoEstado();
				if (codigoEstado != null && !"".equals(codigoEstado)) {
					codigoEstadoAsignado = true;
					if (codigoEstado.length() > 4) {
						LOG.warn("El codigo de respuesta excede los cuatro caracteres permitidos: " + codigoEstado + ".");
						codigoEstado = codigoEstado.substring(0, 4);
					}
				} else {
					codigoEstado = "0003";
				}

				if (respuesta.getAtributos().getEstado().getTiempoEstimadoRespuesta() != null) {
					ter = respuesta.getAtributos().getEstado().getTiempoEstimadoRespuesta();
				}

				entity.setError(respuesta.getAtributos().getEstado().getLiteralError());
				entity.setEstadosecundario(respuesta.getAtributos().getEstado().getCodigoEstadoSecundario());
				entity.setErrorsecundario(respuesta.getAtributos().getEstado().getLiteralErrorSecundario());
			} else {
				LOG.warn("La respuesta no contiene el nodo atributos.");
				codigoEstado = "0003";
				ter = 24;
			}

			entity.setEstado(codigoEstado);
			entity.setFechaRespuesta(Calendar.getInstance().getTime());
			if (respuesta.getTransmisiones() != null && respuesta.getTransmisiones().getTransmisionDatos() != null && respuesta.getTransmisiones().getTransmisionDatos().size() > 0) {
				this.processTransmisiones(messageCtx, entity, respuesta.getTransmisiones().getTransmisionDatos());
			} else {
				if (!codigoEstadoAsignado) {
					entity.setEstado("0002");
				}

				if (ter > 0) {
					long tiempoRespuesta = (long) (ter * 3600000) + System.currentTimeMillis();
					entity.setTer(new Timestamp(tiempoRespuesta));
				} else {
					entity.setTer((Date) null);
				}
			}

			dbPeticion.save(entity);
			TokenDao daoToken = (TokenDao) this.getBean("tokenDao");
			TipoMensajeDao daoTipoMensaje = (TipoMensajeDao) this.getBean("tipoMensajeDao");
			TipoMensaje tipopRespuesta = daoTipoMensaje.select(3);
			Token xmlRespuesta = daoToken.select(tipopRespuesta, entity);
			String b64EncodedKey;
			if (this.isEmisor(messageCtx)) {
				b64EncodedKey = this.getKeyEncodedSender(messageCtx);
			} else {
				b64EncodedKey = this.getKeyEncodedReceiver(messageCtx);
			}

			String algoritmo;
			if (this.isEmisor(messageCtx)) {
				algoritmo = this.getAlgorithmSender(this.getBodyContentResponse(messageCtx), messageCtx);
			} else {
				algoritmo = this.getAlgorithmReceiver(messageCtx);
			}

			if (xmlRespuesta == null) {
				xmlRespuesta = new Token();
				xmlRespuesta.setTipoMensaje(tipopRespuesta);
				xmlRespuesta.setPeticion(entity);
			}

			if (b64EncodedKey != null) {
				xmlRespuesta.setModoEncriptacion("transportKey");
			}

			xmlRespuesta.setAlgoritmoEncriptacion(algoritmo);
			xmlRespuesta.setClave(b64EncodedKey);
			Document response = ((SoapMessage) messageCtx.getResponse()).getDocument();
			if (this.isEmisor(messageCtx)) {
				xmlRespuesta.setDatos(XMLUtils.nodeToString(response));
			} else {
				xmlRespuesta.setDatos(XMLUtils.nodeToString((Element) messageCtx.getProperty("MESSAGE_PRE_SECURITY")));
			}

			daoToken.save(xmlRespuesta);
			return true;
		}
	}

	protected boolean manageConfirmacion(MessageContext messageCtx, Object objectRequest) throws ScspException {
		ConfirmacionPeticion confirmacionPeticion = (ConfirmacionPeticion) objectRequest;
		PeticionRespuestaDao dbPeticion = (PeticionRespuestaDao) this.getBean("peticionRespuestaDao");
		String idpeticion = confirmacionPeticion.getAtributos().getIdPeticion();
		PeticionRespuesta peticion = dbPeticion.select(idpeticion);
		String codigoEstado = null;
		if (peticion == null) {
			String msg = "La petición con identificador " + idpeticion + ", no existe";
			String codcertificado = confirmacionPeticion.getAtributos().getCodigoCertificado();
			String[] args = new String[]{msg};
			throw ScspException.getScspException((Throwable) null, "0244", args, idpeticion, codcertificado);
		} else {
			if (confirmacionPeticion.getAtributos() != null && confirmacionPeticion.getAtributos().getEstado() != null) {
				int ter = 24;
				if (confirmacionPeticion.getAtributos().getEstado().getTiempoEstimadoRespuesta() != null) {
					ter = confirmacionPeticion.getAtributos().getEstado().getTiempoEstimadoRespuesta();
				}

				long tiempoRespuesta = (long) (ter * 3600000) + System.currentTimeMillis();
				peticion.setTer(new Timestamp(tiempoRespuesta));
				codigoEstado = confirmacionPeticion.getAtributos().getEstado().getCodigoEstado();
				peticion.setEstado(confirmacionPeticion.getAtributos().getEstado().getCodigoEstado());
				peticion.setEstadosecundario(confirmacionPeticion.getAtributos().getEstado().getCodigoEstadoSecundario());
			} else {
				LOG.warn("La confirmacion de peticion no contiene el nodo atributos.");
				long tiempoRespuesta = 86400000L + System.currentTimeMillis();
				peticion.setTer(new Timestamp(tiempoRespuesta));
				codigoEstado = "0002";
			}

			peticion.setEstado(codigoEstado);
			TokenDao daoToken = (TokenDao) this.getBean("tokenDao");
			TipoMensajeDao daoTipoMensaje = (TipoMensajeDao) this.getBean("tipoMensajeDao");
			TipoMensaje tipopConfirmacion = daoTipoMensaje.select(1);
			Token xmlConfirmacion = daoToken.select(tipopConfirmacion, peticion);
			if (xmlConfirmacion == null) {
				xmlConfirmacion = new Token();
				xmlConfirmacion.setTipoMensaje(tipopConfirmacion);
				xmlConfirmacion.setPeticion(peticion);
			}

			Document response = ((SoapMessage) messageCtx.getResponse()).getDocument();
			xmlConfirmacion.setDatos(XMLUtils.nodeToString(response));
			dbPeticion.save(peticion);
			daoToken.save(xmlConfirmacion);
			return true;
		}
	}

	protected String getAlgorithmReceiver(MessageContext messageCtx) {
		return this.getAlgorithmSender((Element) messageCtx.getProperty("MESSAGE_PRE_SECURITY"), messageCtx);
	}

	protected void processTransmisiones(MessageContext messageCtx, PeticionRespuesta peticionRespuesta, List<TransmisionDatos> list) throws ScspException {
		TransmisionDao dbTransmisiones = (TransmisionDao) this.getBean("transmisionDao");

		try {
			Map<String, Element> transmisiones = null;
			Document response = ((SoapMessage) messageCtx.getResponse()).getDocument();
			if (this.isEmisor(messageCtx)) {
				transmisiones = this.extractNodesRespuesta((Element) messageCtx.getProperty("MESSAGE_PRE_SECURITY"));
			} else {
				transmisiones = this.extractNodesRespuesta(response.getDocumentElement());
			}

			this.checkNumeroTransmisiones(peticionRespuesta, list);

			for (TransmisionDatos transmisionDatos : list) {
				if (transmisionDatos.getDatosGenericos() == null || transmisionDatos.getDatosGenericos().getTransmision() == null || transmisionDatos.getDatosGenericos().getTransmision().getIdSolicitud() == null) {
					String msg = "Una de las transmisiones no esta correctamente formada, posee a null o los datos genericos o la transmision o el id solicitud";
					String codCertificado = this.getCodigoCertificado(peticionRespuesta.getServicio());
					String idpeticion = peticionRespuesta.getIdPeticion();
					LOG.error(msg);
					String[] args = new String[]{" " + idpeticion + " ." + msg};
					throw ScspException.getScspException((Throwable) null, "0214", args, idpeticion, codCertificado);
				}

				String idSolicitud = transmisionDatos.getDatosGenericos().getTransmision().getIdSolicitud();
				Transmision transmision = dbTransmisiones.select(peticionRespuesta, idSolicitud);
				if (transmision == null) {
					String primkey = "idpeticion = " + peticionRespuesta.getIdPeticion() + " , idsolicitud = " + idSolicitud;
					LOG.error("No se ha encontrado en la tabla de transmisiones un registro con la primary key " + primkey);
				} else {
					if (transmisionDatos.getDatosGenericos().getTitular() != null) {
						if (this.isFilled(transmisionDatos.getDatosGenericos().getTitular().getDocumentacion())) {
							transmision.setDocTitular(transmisionDatos.getDatosGenericos().getTitular().getDocumentacion());
						}

						if (this.isFilled(transmisionDatos.getDatosGenericos().getTitular().getNombre())) {
							transmision.setNombreTitular(transmisionDatos.getDatosGenericos().getTitular().getNombre());
						}

						if (this.isFilled(transmisionDatos.getDatosGenericos().getTitular().getNombreCompleto())) {
							transmision.setNombreCompletoTitular(transmisionDatos.getDatosGenericos().getTitular().getNombreCompleto());
						}

						if (this.isFilled(transmisionDatos.getDatosGenericos().getTitular().getApellido1())) {
							transmision.setApellido1Titular(transmisionDatos.getDatosGenericos().getTitular().getApellido1());
						}

						if (this.isFilled(transmisionDatos.getDatosGenericos().getTitular().getApellido2())) {
							transmision.setApellido2Titular(transmisionDatos.getDatosGenericos().getTitular().getApellido2());
						}
					}

					if (transmisionDatos.getDatosGenericos().getSolicitante() != null) {
						Funcionario funcionario = transmisionDatos.getDatosGenericos().getSolicitante().getFuncionario();
						if (funcionario != null) {
							if (this.isFilled(funcionario.getNifFuncionario())) {
								transmision.setDocFuncionario(funcionario.getNifFuncionario());
							}

							if (this.isFilled(funcionario.getNombreCompletoFuncionario())) {
								transmision.setNombreFuncionario(funcionario.getNombreCompletoFuncionario());
							}

							if (this.isFilled(funcionario.getSeudonimoEmpleadoPublico())) {
								transmision.setSeudonimoFuncionario(funcionario.getSeudonimoEmpleadoPublico());
							}
						}

						transmision.setFinalidad(transmisionDatos.getDatosGenericos().getSolicitante().getFinalidad());
						transmision.setConsentimiento(transmisionDatos.getDatosGenericos().getSolicitante().getConsentimiento().toString());
						if (this.isFilled(transmisionDatos.getDatosGenericos().getSolicitante().getIdExpediente())) {
							transmision.setExpediente(transmisionDatos.getDatosGenericos().getSolicitante().getIdExpediente());
						}

						transmision.setIdSolicitante(transmisionDatos.getDatosGenericos().getSolicitante().getIdentificadorSolicitante());
						transmision.setNombreSolicitante(transmisionDatos.getDatosGenericos().getSolicitante().getNombreSolicitante());
						if (transmisionDatos.getDatosGenericos().getSolicitante().getProcedimiento() != null) {
							if (this.isFilled(transmisionDatos.getDatosGenericos().getSolicitante().getProcedimiento().getNombreProcedimiento())) {
								transmision.setNombreProcedimiento(transmisionDatos.getDatosGenericos().getSolicitante().getProcedimiento().getNombreProcedimiento());
							}

							if (this.isFilled(transmisionDatos.getDatosGenericos().getSolicitante().getProcedimiento().getCodProcedimiento())) {
								transmision.setCodigoProcedimiento(transmisionDatos.getDatosGenericos().getSolicitante().getProcedimiento().getCodProcedimiento());
							}

							String automatizado = transmisionDatos.getDatosGenericos().getSolicitante().getProcedimiento().getAutomatizado();
							if (automatizado != null) {
								transmision.setAutomatizado((short) ("S".equals(automatizado) ? 1 : 0));
							}

							Short claseTramite = transmisionDatos.getDatosGenericos().getSolicitante().getProcedimiento().getClaseTramite() == null ? null : transmisionDatos.getDatosGenericos().getSolicitante().getProcedimiento().getClaseTramite().shortValue();
							transmision.setClaseTramite(claseTramite);
						}

						if (this.isFilled(transmisionDatos.getDatosGenericos().getSolicitante().getUnidadTramitadora())) {
							transmision.setUnidadTramitadora(transmisionDatos.getDatosGenericos().getSolicitante().getUnidadTramitadora());
						}

						if (this.isFilled(transmisionDatos.getDatosGenericos().getSolicitante().getCodigoUnidadTramitadora())) {
							transmision.setCodigoUnidadTramitadora(transmisionDatos.getDatosGenericos().getSolicitante().getCodigoUnidadTramitadora());
						}
					}

					Timestamp fechaGeneracion = null;

					try {
						if (transmisionDatos.getDatosGenericos().getTransmision().getFechaGeneracion() != null) {
							LOG.debug("Se recibe una fecha de generacion en la solicitud de transmision, Se procede a verificar formato");
							fechaGeneracion = DateUtils.parseTimestamp(transmisionDatos.getDatosGenericos().getTransmision().getFechaGeneracion());
						}
					} catch (Exception var14) {
						LOG.error("La fecha de generacion de la transmision no esta en un formato conocido yyyy-MM-dd'T'HH:mm:ss.SSSZ, yyyy-MM-dd HH:mm:ss.SSS ó yyyy-MM-dd  ó dd/MM/yyyy");
					}

					transmision.setFechaGeneracion(fechaGeneracion);
					transmision.setPeticion(peticionRespuesta);
					transmision.setIdSolicitud(transmisionDatos.getDatosGenericos().getTransmision().getIdSolicitud());
					String idtransmision = transmisionDatos.getDatosGenericos().getTransmision().getIdTransmision();
					idtransmision = idtransmision != null && "".equals(idtransmision) ? null : idtransmision;
					transmision.setIdTransmision(idtransmision);
					if (transmisionDatos.getDatosGenericos() != null) {
						this.setInfoErrorTransmision(peticionRespuesta.getServicio(), transmision, transmisionDatos.getDatosEspecificos());
					}

					boolean almacenarPeticiones = this.readParameter("almacenamiento.transmisiones", messageCtx).equals("1");
					if (almacenarPeticiones) {
						transmision.setXmlTransmision(XMLUtils.nodeToString((Element) transmisiones.get(transmision.getIdSolicitud() + transmision.getIdTransmision())));
					}

					dbTransmisiones.save(transmision);
					peticionRespuesta.setDescompuesta("S");
				}
			}
		} catch (ScspException se) {
			throw se;
		} catch (Exception e) {
			peticionRespuesta.setDescompuesta("E");
			LOG.error("Se ha producido un error en el procesamiento de las transmisiones", e);
		}

	}

	protected String getCodigoCertificado(Servicio servicio) {
		ServicioDao servicioDao = (ServicioDao) this.getBean("servicioDao");
		String codCertificado = "";
		if (servicio != null) {
			Servicio s = servicioDao.select(servicio.getId());
			codCertificado = s != null ? s.getCodCertificado() : "";
		}

		return codCertificado;
	}

	private void setInfoErrorTransmision(Servicio servicio, Transmision transmision, Object datosEspecificos) throws ScspException {
		ServiceManager manager = (ServiceManager) this.getBean("serviceManager");
		servicio = manager.selectServicio(servicio.getId());
		if (servicio.getXpathCodigoError() != null && !servicio.getXpathCodigoError().trim().isEmpty()) {
			String code = this.evalXPathExp(servicio.getXpathCodigoError().trim(), (Element) datosEspecificos);
			if (code == null) {
				LOG.error(String.format("Revise la configuración establecida para recuperar el codigo de error de la transmisión en función al xpath %s.No se ha podido recuperar.Se ignorará el valor", servicio.getXpathCodigoError()));
			}

			transmision.setEstado(code);
		}

		if (servicio.getXpathCodigoErrorSecundario() != null && !servicio.getXpathCodigoErrorSecundario().trim().isEmpty()) {
			String code = this.evalXPathExp(servicio.getXpathCodigoErrorSecundario().trim(), (Element) datosEspecificos);
			if (code == null) {
				LOG.error(String.format("Revise la configuración establecida para recuperar el codigo de error secundario de la transmisión en función al xpath %s.No se ha podido recuperar.Se ignorará el valor", servicio.getXpathCodigoErrorSecundario()));
			}

			transmision.setEstadoSecundario(code);
		}

		if (servicio.getXpathLiteralError() != null && !servicio.getXpathLiteralError().trim().isEmpty()) {
			String error = this.evalXPathExp(servicio.getXpathLiteralError().trim(), (Element) datosEspecificos);
			if (error == null) {
				LOG.error(String.format("Revise la configuración establecida para recuperar el literal de error de la transmisión en función al xpath %s.No se ha podido recuperar.Se ignorará el valor", servicio.getXpathLiteralError()));
			}

			transmision.setError(error);
		}

	}

	private void checkNumeroTransmisiones(PeticionRespuesta peticion, List<TransmisionDatos> list) throws ScspException {
		if (list != null && list.size() > 0) {
			TransmisionDao dbTransmisiones = (TransmisionDao) this.getBean("transmisionDao");
			List<Transmision> solicitudes = dbTransmisiones.select(peticion);
			String codCertificado = this.getCodigoCertificado(peticion.getServicio());
			if (solicitudes == null) {
				String idpeticion = peticion.getIdPeticion();
				String msg = "No existen solicitudes de transmision para la peticion con id " + idpeticion;
				LOG.error(msg);
				String[] args = new String[]{" " + idpeticion + " ." + msg};
				throw ScspException.getScspException((Throwable) null, "0209", args, idpeticion, codCertificado);
			}

			if (solicitudes.size() != list.size()) {
				String msg = "El numero de transmisiones no coincide con el número de solicitudes de transmision para la petición con id " + peticion.getIdPeticion();
				LOG.error(msg);
				String idpeticion = peticion.getIdPeticion();
				String[] args = new String[]{" " + idpeticion + " ." + msg};
				throw ScspException.getScspException((Throwable) null, "0237", args, idpeticion, codCertificado);
			}
		}

	}

	private boolean isFilled(String text) {
		return text != null && !"".equals(text);
	}

	protected void processSolicitudesTransmisiones(MessageContext messageCtx, PeticionRespuesta peticionRespuesta, List<SolicitudTransmision> list) throws ScspClientInterceptorException {
		TransmisionDao dbTransmisiones = (TransmisionDao) this.getBean("transmisionDao");

		try {
			for (SolicitudTransmision transmisionDatos : list) {
				Transmision transmision = new Transmision();
				if (transmisionDatos.getDatosGenericos().getTitular() != null) {
					transmision.setDocTitular(transmisionDatos.getDatosGenericos().getTitular().getDocumentacion());
					transmision.setNombreTitular(transmisionDatos.getDatosGenericos().getTitular().getNombre());
					transmision.setNombreCompletoTitular(transmisionDatos.getDatosGenericos().getTitular().getNombreCompleto());
					transmision.setApellido1Titular(transmisionDatos.getDatosGenericos().getTitular().getApellido1());
					transmision.setApellido2Titular(transmisionDatos.getDatosGenericos().getTitular().getApellido2());
				}

				if (transmisionDatos.getDatosGenericos().getSolicitante() != null) {
					es.scsp.bean.common.peticion.Funcionario funcionario = transmisionDatos.getDatosGenericos().getSolicitante().getFuncionario();
					if (funcionario != null) {
						transmision.setDocFuncionario(funcionario.getNifFuncionario());
						transmision.setNombreFuncionario(funcionario.getNombreCompletoFuncionario());
						transmision.setSeudonimoFuncionario(funcionario.getSeudonimoEmpleadoPublico());
					}

					transmision.setFinalidad(transmisionDatos.getDatosGenericos().getSolicitante().getFinalidad());
					transmision.setConsentimiento(transmisionDatos.getDatosGenericos().getSolicitante().getConsentimiento().toString());
					transmision.setExpediente(transmisionDatos.getDatosGenericos().getSolicitante().getIdExpediente());
					transmision.setIdSolicitante(transmisionDatos.getDatosGenericos().getSolicitante().getIdentificadorSolicitante());
					transmision.setNombreSolicitante(transmisionDatos.getDatosGenericos().getSolicitante().getNombreSolicitante());
					if (transmisionDatos.getDatosGenericos().getSolicitante().getProcedimiento() != null) {
						transmision.setNombreProcedimiento(transmisionDatos.getDatosGenericos().getSolicitante().getProcedimiento().getNombreProcedimiento());
						transmision.setCodigoProcedimiento(transmisionDatos.getDatosGenericos().getSolicitante().getProcedimiento().getCodProcedimiento());
						String automatizado = transmisionDatos.getDatosGenericos().getSolicitante().getProcedimiento().getAutomatizado();
						if (automatizado != null) {
							transmision.setAutomatizado((short) ("S".equals(automatizado) ? 1 : 0));
						}

						if (transmisionDatos.getDatosGenericos().getSolicitante().getProcedimiento().getClaseTramite() != null) {
							transmision.setClaseTramite(transmisionDatos.getDatosGenericos().getSolicitante().getProcedimiento().getClaseTramite().shortValue());
						}
					}

					transmision.setUnidadTramitadora(transmisionDatos.getDatosGenericos().getSolicitante().getUnidadTramitadora());
					transmision.setCodigoUnidadTramitadora(transmisionDatos.getDatosGenericos().getSolicitante().getCodigoUnidadTramitadora());
				}

				Timestamp fechaGeneracion = null;

				try {
					if (transmisionDatos.getDatosGenericos().getTransmision().getFechaGeneracion() != null) {
						fechaGeneracion = DateUtils.parseTimestamp(transmisionDatos.getDatosGenericos().getTransmision().getFechaGeneracion());
					}
				} catch (Exception var10) {
					LOG.error("La fecha de generacion de la transmision no esta en un formato conocido yyyy-MM-dd'T'HH:mm:ss.SSSZ, yyyy-MM-dd HH:mm:ss.SSS ó yyyy-MM-dd  ó dd/MM/yyyy");
				}

				transmision.setFechaGeneracion(fechaGeneracion);
				transmision.setPeticion(peticionRespuesta);
				transmision.setIdSolicitud(transmisionDatos.getDatosGenericos().getTransmision().getIdSolicitud());
				transmision.setIdTransmision(transmisionDatos.getDatosGenericos().getTransmision().getIdTransmision());
				dbTransmisiones.save(transmision);
				peticionRespuesta.setDescompuesta("N");
			}

		} catch (Exception e) {
			LOG.error("Error registrando la solicitud de transmision", e);
			String msg = "Error al actualizar el objeto en base de datos. Error al tramitar las solicitudes de transmsion";
			LOG.error(msg);
			String codCertificado = peticionRespuesta.getServicio() == null ? "" : peticionRespuesta.getServicio().getCodCertificado();
			String idpeticion = peticionRespuesta.getIdPeticion();
			String[] args = new String[]{" " + idpeticion + " ." + msg};
			throw ScspClientInterceptorException.getScspException((Throwable) null, "0501", args, idpeticion, codCertificado);
		}
	}

	protected Map<String, Element> extractNodesRespuesta(Element node) throws JaxenException {
		Map<String, Element> result = new HashMap<String, Element>();
		if (node != null) {
			XpathManager manager = new XpathManager();
			String pathDatosGenericos = manager.constructXpath("//*[local-name()='TransmisionDatos']", new String[]{"DatosGenericos"});

			for (Node dato : manager.evalXpathListNode(pathDatosGenericos, node)) {
				String root = String.format("*[local-name()='%s']", "Transmision");
				String idSolicitud = manager.evalXPathExp(manager.constructXpath(root, new String[]{"IdSolicitud"}), (Element) dato);
				String idTransmision = manager.evalXPathExp(manager.constructXpath(root, new String[]{"IdTransmision"}), (Element) dato);
				result.put(idSolicitud + idTransmision, (Element) dato.getParentNode());
			}
		}

		if (result.isEmpty()) {
			LOG.warn("No se han encontrado transmisiones en la respuesta.");
		}

		return result;
	}

	protected PeticionRespuesta generateRespuesta(SolicitudRespuesta solicitud) {
		Validate.notNull(solicitud, "No se ha definido el objeto Solicitud de Respuesta.");
		Validate.notNull(solicitud.getAtributos(), "La solicitud no contiene atributos.");
		Validate.notEmpty(solicitud.getAtributos().getCodigoCertificado(), "No se ha definido el tipo de certificado.");
		es.scsp.bean.common.solicitud.Estado estado = solicitud.getAtributos().getEstado();
		PeticionRespuesta tmp = new PeticionRespuesta();
		ServicioDao daoServicio = (ServicioDao) this.getBean("servicioDao");
		Servicio servicio = daoServicio.select(solicitud.getAtributos().getCodigoCertificado());
		Validate.notNull(servicio, "No se ha encontrado en la base de datos ningun certificado con ese código.");
		tmp.setServicio(servicio);
		tmp.setFechaPeticion(new Timestamp(System.currentTimeMillis()));
		tmp.setFechaRespuesta(new Timestamp(System.currentTimeMillis()));
		tmp.setIdPeticion(solicitud.getAtributos().getIdPeticion());
		if (estado != null) {
			tmp.setEstado(estado.getCodigoEstado());
			tmp.setEstadosecundario(estado.getCodigoEstadoSecundario());
			long tiempoRespuesta = (long) (36000 * estado.getTiempoEstimadoRespuesta()) + System.currentTimeMillis();
			tmp.setTer(new Timestamp(tiempoRespuesta));
		} else {
			tmp.setEstado("0001");
			tmp.setTer((Date) null);
		}

		tmp.setFechaPeticion(new Timestamp(System.currentTimeMillis()));
		tmp.setNumeroEnvios(new Integer(0));
		return tmp;
	}

	protected List<Map<String, String>> extractTransmisionesPeticion(Element node) {
		return null;
	}

	protected List<Map<String, String>> extractTransmisionesRespuesta(Element node) {
		return null;
	}
}
