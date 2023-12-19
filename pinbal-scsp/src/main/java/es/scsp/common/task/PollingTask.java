/**
 * 
 */
package es.scsp.common.task;

import es.scsp.bean.common.Atributos;
import es.scsp.bean.common.Solicitante;
import es.scsp.bean.common.SolicitudRespuesta;
import es.scsp.client.ClienteUnico;
import es.scsp.common.dao.ParametroConfiguracionDao;
import es.scsp.common.dao.PeticionRespuestaDao;
import es.scsp.common.dao.ServeiDao;
import es.scsp.common.dao.ServicioDao;
import es.scsp.common.dao.TransmisionDao;
import es.scsp.common.domain.core.ParametroConfiguracion;
import es.scsp.common.domain.core.PeticionRespuesta;
import es.scsp.common.domain.core.Servicio;
import es.scsp.common.domain.core.Transmision;
import es.scsp.common.exceptions.ScspException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

/**
 * Modificació del PollingTask de les llibreries SCSP per a
 * realitzar les operacions a dins transaccions i evitar els
 * errors "No Hibernate Session bound to thread, and configuration
 * does not allow creation of non-transactional one here".
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component("pollingTimerTask")
	public class PollingTask extends TimerTask {
	private static final Log log = LogFactory.getLog(PollingTask.class);
	/*private static final String MSG_POLLING_INIT = "Iniciando servicio de polling.";
	private static final String MSG_POLLING_DISABLED = "El modulo de polling esta desactivado por configuracion. No se realiza el servicio de polling.";
	private static final String MSG_INIT_PROCESS = "Iniciando ciclo de procesamiento de solicitudes pendientes.";
	private static final String MSG_ERROR_GET_PETICIONES_PENDIENTES = "Ha ocurrido un error mientras se recuperaba de base de datos la lista de peticiones asincronas pendientes de ser consultadas.";
	private static final String MSG_NOHAY_PETICIONES_PENDIENTES = "No se han encontrado peticiones en base de datos pendientes de ser consultadas.";
	private static final String MSG_PROCESANDO_IDPETICION = "Iniciando consulta asincrona de la peticion %s.";
	private static final String MSG_FIN_PROCESADO_PETICION = "Finalizada la consulta asincrona de la peticion %s.";
	private static final String MSG_ERR_PROCESANDO_PETICION = "Ha ocurrido en un error durante la consulta asincrona de la peticion %s.";
	private static final String MSG_CREAR_PETICION_ASINCRONA = "Ha ocurrido un error mientras se creaba la peticion pendiente a partir de los datos de base de datos.";
	private static final String MSG_FIN_PROCESAMIENTO = "Finalizado servicio de polling.";
	private static final String MSG_END_CYCLE = "Finalizado ciclo de procesamiento de peticiones.";*/

	@Autowired
	private PeticionRespuestaDao peticionRespuestaDao;
	@Autowired
	private ServicioDao servicioDao;
	@Autowired
	private ParametroConfiguracionDao parametroConfiguracionDao;
	@Autowired
	private TransmisionDao transmisionDao;
	@Autowired
	private ServeiDao serveiDao;
	@Autowired
	private SimpleDateFormat dateFormat;
	@Autowired
	private ClienteUnico clienteUnico;

	private final TransactionTemplate transactionTemplate;
	@Autowired
	public PollingTask(PlatformTransactionManager transactionManager) {
		transactionTemplate = new TransactionTemplate(transactionManager);
	}

	public void run() {
		log.debug("Iniciando servicio de polling.");
		if (!isEnabled()) {
			log.debug("El modulo de polling esta desactivado por configuracion. No se realiza el servicio de polling.");
		} else {
			while (true) {
				log.debug("Iniciando ciclo de procesamiento de solicitudes pendientes.");
				List<PeticionRespuesta> peticiones;
				try {
					peticiones = transactionTemplate.execute(new TransactionCallback<List<PeticionRespuesta>>() {
						public List<PeticionRespuesta> doInTransaction(TransactionStatus status) {
							return peticionRespuestaDao.selectByEstado(
									"0002",
									Calendar.getInstance().getTime());
						}
					});
				} catch (Exception ex) {
					throw new RuntimeException(
							"Ha ocurrido un error mientras se recuperaba de base de datos la lista de peticiones asincronas pendientes de ser consultadas.",
							ex);
				}
				if (peticiones.isEmpty()) {
					log.debug("No se han encontrado peticiones en base de datos pendientes de ser consultadas.");
					break;
				} else {
					for (final PeticionRespuesta peticionRespuesta: peticiones) {
						final String idPeticion = peticionRespuesta.getIdPeticion();
						log.debug(String.format(
								"Iniciando consulta asincrona de la peticion %s.",
								new Object[] { idPeticion }));
						final SolicitudRespuesta solicitud = new SolicitudRespuesta();
						try {
							Servicio servicio = getServicio(peticionRespuesta);
							String codigoCertificado = servicio.getCodCertificado();
							int numTransmissions = peticionRespuesta.getNumeroTransmisiones().intValue();
							solicitud.setAtributos(new Atributos());
							solicitud.getAtributos().setCodigoCertificado(codigoCertificado);
							solicitud.getAtributos().setIdPeticion(peticionRespuesta.getIdPeticion());
							solicitud.getAtributos().setNumElementos(String.valueOf(numTransmissions));
							solicitud.getAtributos().setTimeStamp(dateFormat.format(new Date()));

							if (serveiHasToSendSolicitant(codigoCertificado)) {
								Transmision transmision = getTransmision(peticionRespuesta);

								if (transmision != null) {
									solicitud.getAtributos().setSolicitante(getSolicitante(transmision));
                                }
							}

							log.debug(String.format("Finalizada la consulta asincrona de la peticion %s.", new Object[] { idPeticion }));
						} catch (Exception e) {
							log.debug(
									String.format("Ha ocurrido un error mientras se creaba la peticion pendiente a partir de los datos de base de datos.",
											new Object[] { idPeticion }));
							handleErrorPeticion(peticionRespuesta, e);
						}
						transactionTemplate.execute(new TransactionCallback<Object>() {
							public Object doInTransaction(TransactionStatus status) {
								try {
									clienteUnico.realizaSolicitudRespuesta(solicitud);
								} catch (Exception ex) {
									log.error(
											String.format(
													"Ha ocurrido en un error durante la consulta asincrona de la peticion %s.",
													new Object[] { idPeticion }), ex);
								}
								return null;
							}
						});
					}
				}
				log.debug("Finalizado ciclo de procesamiento de peticiones.");
			}
			log.debug("Finalizado servicio de polling.");
		}
	}

	private static Solicitante getSolicitante(Transmision transmision) {
		Solicitante solicitante = new Solicitante();
		solicitante.setIdentificadorSolicitante(transmision.getIdSolicitante());
		solicitante.setNombreSolicitante(transmision.getNombreSolicitante());
//		solicitante.setFinalidad(transmision.getFinalidad());
//		solicitante.setConsentimiento(Consentimiento.valueOf(transmision.getConsentimiento()));
//		solicitante.setUnidadTramitadora(transmision.getUnidadTramitadora());
//		solicitante.setCodigoUnidadTramitadora(transmision.getCodigoUnidadTramitadora());
//		solicitante.setIdExpediente(transmision.getExpediente());
//
//		Procedimiento procedimiento = new Procedimiento();
//		procedimiento.setNombreProcedimiento(transmision.getNombreProcedimiento());
//		procedimiento.setCodProcedimiento(transmision.getCodigoProcedimiento());
//		if (transmision.getAutomatizado() != null) {
//			procedimiento.setAutomatizado(transmision.getAutomatizado() == 0 ? "N" : "S");
//		}
//		procedimiento.setClaseTramite(transmision.getClaseTramite());
//		solicitante.setProcedimiento(procedimiento);
//
//		Funcionario funcionario = new Funcionario();
//		funcionario.setNombreCompletoFuncionario(transmision.getNombreFuncionario());
//		funcionario.setNifFuncionario(transmision.getDocFuncionario());
//		funcionario.setSeudonimo(transmision.getSeudonimoFuncionario());
//		solicitante.setFuncionario(funcionario);
		return solicitante;
	}

	private Transmision getTransmision(final PeticionRespuesta peticionRespuesta) {
		Transmision transmision = transactionTemplate.execute(new TransactionCallback<Transmision>() {
			public Transmision doInTransaction(TransactionStatus status) {
				List<Transmision> transmisions = null;
				try {
					transmisions = transmisionDao.select(peticionRespuesta);
				} catch (ScspException e) {
					throw new RuntimeException(e);
				}
				return transmisions == null || transmisions.isEmpty() ? null : transmisions.get(0);
			}
		});
		return transmision;
	}

	private Servicio getServicio(PeticionRespuesta peticionRespuesta) {
		final long servicioId = peticionRespuesta.getServicio().getId();
		Servicio servicio = transactionTemplate.execute(new TransactionCallback<Servicio>() {
			public Servicio doInTransaction(TransactionStatus status) {
				return servicioDao.select(
						servicioId);
			}
		});
		return servicio;
	}

	private boolean serveiHasToSendSolicitant(final String codigoCertificado) {
		return transactionTemplate.execute(new TransactionCallback<Boolean>() {
			public Boolean doInTransaction(TransactionStatus status) {
				return serveiDao.serveiHasToSendSolicitant(codigoCertificado);
			}
		});
	}

	private boolean isEnabled() {
		ParametroConfiguracion enabled = transactionTemplate.execute(new TransactionCallback<ParametroConfiguracion>() {
			public ParametroConfiguracion doInTransaction(TransactionStatus status) {
				return parametroConfiguracionDao.select("polling.enabled");
			}
		});
		if (enabled == null) {
			log.warn("No existe en la tabla de configuracion global el parametro polling.enabled");
			return false;
		}
		return enabled.getValor().equals("true");
	}

	private void handleErrorPeticion(PeticionRespuesta peticionRespuesta,
			Exception e) {
		log.error("=========================================================");
		log.error("Error al procesar la peticion asincrona");
		log.error("IdPeticion: " + peticionRespuesta.getIdPeticion());
		log.error("ID Certificado: " + peticionRespuesta.getServicio().getId());
		log.error("Mensaje: " + e.getMessage());
		log.error("StackTrace", e);
		log.error("=========================================================");
		try {
			PeticionRespuesta peticionRespuestaaux = this.peticionRespuestaDao.select(peticionRespuesta.getIdPeticion());
			if ((e instanceof ScspException)) {
				ScspException se = (ScspException)e;
				if (se.getScspCode().equals("0002")) {
					log.warn("La excepción recibida posee estado 0002, implica un error en comunicaciones y reseteo posterior de estado.");
				} else {
					peticionRespuestaaux.setError(se.getMessage());
				}
				peticionRespuestaaux.setEstado(se.getScspCode());
			} else {
				peticionRespuestaaux.setError("0502");
			}
			this.peticionRespuestaDao.save(peticionRespuestaaux);
		} catch (ScspException ex) {
			log.error("=========================================================");
			log.error("Error al actualizar en base de datos el estado de error de la peticion: ");
			log.error(ex);
			log.error("=========================================================");
		}
	}

}
