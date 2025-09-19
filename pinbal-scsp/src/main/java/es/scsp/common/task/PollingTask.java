/**
 * 
 */
package es.scsp.common.task;

import es.scsp.bean.common.solicitud.Atributos;
import es.scsp.bean.common.solicitud.Solicitante;
import es.scsp.bean.common.solicitud.SolicitudRespuesta;
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
    /* MOD PBL */ //	private static final String MSG_POLLING_INIT = "Iniciando servicio de polling.";
    /* MOD PBL */ //	private static final String MSG_POLLING_DISABLED = "El modulo de polling esta desactivado por configuracion. No se realiza el servicio de polling.";
    /* MOD PBL */ //	private static final String MSG_INIT_PROCESS = "Iniciando ciclo de procesamiento de solicitudes pendientes.";
    /* MOD PBL */ //	private static final String MSG_ERROR_GET_PETICIONES_PENDIENTES = "Ha ocurrido un error mientras se recuperaba de base de datos la lista de peticiones asincronas pendientes de ser consultadas.";
    /* MOD PBL */ //	private static final String MSG_NOHAY_PETICIONES_PENDIENTES = "No se han encontrado peticiones en base de datos pendientes de ser consultadas.";
    /* MOD PBL */ //	private static final String MSG_PROCESANDO_IDPETICION = "Iniciando consulta asincrona de la peticion %s.";
    /* MOD PBL */ //	private static final String MSG_FIN_PROCESADO_PETICION = "Finalizada la consulta asincrona de la peticion %s.";
    /* MOD PBL */ //	private static final String MSG_ERR_PROCESANDO_PETICION = "Ha ocurrido en un error durante la consulta asincrona de la peticion %s.";
    /* MOD PBL */ //	private static final String MSG_CREAR_PETICION_ASINCRONA = "Ha ocurrido un error mientras se creaba la peticion pendiente a partir de los datos de base de datos.";
    /* MOD PBL */ //	private static final String MSG_FIN_PROCESAMIENTO = "Finalizado servicio de polling.";
    /* MOD PBL */ //	private static final String MSG_END_CYCLE = "Finalizado ciclo de procesamiento de peticiones.";

	@Autowired
	private PeticionRespuestaDao peticionRespuestaDao;
	@Autowired
	private ServicioDao servicioDao;
	@Autowired
	private ParametroConfiguracionDao parametroConfiguracionDao;
    /* MOD PBL */ @Autowired
    /* MOD PBL */ private TransmisionDao transmisionDao;
    /* MOD PBL */ @Autowired
    /* MOD PBL */ private ServeiDao serveiDao;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	@Autowired
	private ClienteUnico clienteUnico;

    /* MOD PBL */ private final TransactionTemplate transactionTemplate;
    /* MOD PBL */ @Autowired
    /* MOD PBL */ public PollingTask(PlatformTransactionManager transactionManager) {
    /* MOD PBL */ 	transactionTemplate = new TransactionTemplate(transactionManager);
    /* MOD PBL */ }

	public void run() {
		log.debug("Iniciando servicio de polling.");
        if (!this.isEnabled()) {
            log.debug("El modulo de polling esta desactivado por configuracion. No se realiza el servicio de polling.");
        } else {
			while (true) {
				log.debug("Iniciando ciclo de procesamiento de solicitudes pendientes.");

				List<PeticionRespuesta> peticiones;
				try {
					/* MOD PBL */ peticiones = transactionTemplate.execute(new TransactionCallback<List<PeticionRespuesta>>() {
					/* MOD PBL */ 	public List<PeticionRespuesta> doInTransaction(TransactionStatus status) {
					/* MOD PBL */ 		return peticionRespuestaDao.selectByEstado(
					/* MOD PBL */ 				"0002",
					/* MOD PBL */ 				Calendar.getInstance().getTime());
					/* MOD PBL */ 	}
					/* MOD PBL */ });
				} catch (Exception ex) {
                    throw new RuntimeException("Ha ocurrido un error mientras se recuperaba de base de datos la lista de peticiones asincronas pendientes de ser consultadas.", ex);
				}

                if (peticiones.isEmpty()) {
                    log.debug("No se han encontrado peticiones en base de datos pendientes de ser consultadas.");
                    log.debug("Finalizado servicio de polling.");
                    return;
                }

                for (final PeticionRespuesta peticionRespuesta: peticiones) {
                    final String idPeticion = peticionRespuesta.getIdPeticion();
                    log.debug(String.format("Iniciando consulta asincrona de la peticion %s.", idPeticion));
                    final SolicitudRespuesta solicitud = new SolicitudRespuesta();

                    try {
                        /* MOD PBL */ Servicio servicio = getServicio(peticionRespuesta);
                        String codigoCertificado = servicio.getCodCertificado();
                        int numTransmissions = peticionRespuesta.getNumeroTransmisiones().intValue();
                        solicitud.setAtributos(new Atributos());
                        solicitud.getAtributos().setCodigoCertificado(codigoCertificado);
                        solicitud.getAtributos().setIdPeticion(peticionRespuesta.getIdPeticion());
                        solicitud.getAtributos().setNumElementos(numTransmissions);
                        solicitud.getAtributos().setTimeStamp(dateFormat.format(new Date()));

                        /* MOD PBL */ if (serveiHasToSendSolicitant(codigoCertificado)) {
                        /* MOD PBL */ 	Transmision transmision = getTransmision(peticionRespuesta);
                        /* MOD PBL */
                        /* MOD PBL */ 	if (transmision != null) {
                        /* MOD PBL */ 		solicitud.getAtributos().setSolicitante(getSolicitante(transmision));
                        /* MOD PBL */     }
                        /* MOD PBL */ }

                        log.debug(String.format("Finalizada la consulta asincrona de la peticion %s.", idPeticion));
                    } catch (Exception e) {
                        log.debug(String.format("Ha ocurrido un error mientras se creaba la peticion pendiente a partir de los datos de base de datos.", idPeticion));
                        this.handleErrorPeticion(peticionRespuesta, e);
                    }

                    /* MOD PBL */ transactionTemplate.execute(new TransactionCallback<Object>() {
                    /* MOD PBL */ 	public Object doInTransaction(TransactionStatus status) {
                            try {
                                clienteUnico.realizaSolicitudRespuesta(solicitud);
                            } catch (Exception ex) {
                                log.error(String.format("Ha ocurrido en un error durante la consulta asincrona de la peticion %s.", idPeticion), ex);
                                /* MOD PBL */ // this.handleErrorPeticion(peticionRespuesta, ex);
                            }
                            return null;
                    /* MOD PBL */ 	}
                    /* MOD PBL */ });
                }

				log.debug("Finalizado ciclo de procesamiento de peticiones.");
			}
		}
	}

/* MOD PBL */ 	private static Solicitante getSolicitante(Transmision transmision) {
/* MOD PBL */ 		Solicitante solicitante = new Solicitante();
/* MOD PBL */ 		solicitante.setIdentificadorSolicitante(transmision.getIdSolicitante());
/* MOD PBL */ 		solicitante.setNombreSolicitante(transmision.getNombreSolicitante());
/* MOD PBL */ 		return solicitante;
/* MOD PBL */ 	}

/* MOD PBL */ 	private Transmision getTransmision(final PeticionRespuesta peticionRespuesta) {
/* MOD PBL */ 		Transmision transmision = transactionTemplate.execute(new TransactionCallback<Transmision>() {
/* MOD PBL */ 			public Transmision doInTransaction(TransactionStatus status) {
/* MOD PBL */ 				List<Transmision> transmisions = null;
/* MOD PBL */ 				try {
/* MOD PBL */ 					transmisions = transmisionDao.select(peticionRespuesta);
/* MOD PBL */ 				} catch (ScspException e) {
/* MOD PBL */ 					throw new RuntimeException(e);
/* MOD PBL */ 				}
/* MOD PBL */ 				return transmisions == null || transmisions.isEmpty() ? null : transmisions.get(0);
/* MOD PBL */ 			}
/* MOD PBL */ 		});
/* MOD PBL */ 		return transmision;
/* MOD PBL */ 	}

/* MOD PBL */ 	private Servicio getServicio(PeticionRespuesta peticionRespuesta) {
/* MOD PBL */ 		final long servicioId = peticionRespuesta.getServicio().getId();
/* MOD PBL */ 		Servicio servicio = transactionTemplate.execute(new TransactionCallback<Servicio>() {
/* MOD PBL */ 			public Servicio doInTransaction(TransactionStatus status) {
/* MOD PBL */ 				return servicioDao.select(
/* MOD PBL */ 						servicioId);
/* MOD PBL */ 			}
/* MOD PBL */ 		});
/* MOD PBL */ 		return servicio;
/* MOD PBL */ 	}

/* MOD PBL */ 	private boolean serveiHasToSendSolicitant(final String codigoCertificado) {
/* MOD PBL */ 		return transactionTemplate.execute(new TransactionCallback<Boolean>() {
/* MOD PBL */ 			public Boolean doInTransaction(TransactionStatus status) {
/* MOD PBL */ 				return serveiDao.serveiHasToSendSolicitant(codigoCertificado);
/* MOD PBL */ 			}
/* MOD PBL */ 		});
/* MOD PBL */ 	}

	private boolean isEnabled() {
        /* MOD PBL */ ParametroConfiguracion enabled = transactionTemplate.execute(new TransactionCallback<ParametroConfiguracion>() {
        /* MOD PBL */ 	public ParametroConfiguracion doInTransaction(TransactionStatus status) {
				return parametroConfiguracionDao.select("polling.enabled");
        /* MOD PBL */ 	}
        /* MOD PBL */ });
		if (enabled == null) {
			log.warn("No existe en la tabla de configuracion global el parametro polling.enabled");
			return false;
		}
		return enabled.getValor().equals("true");
	}

    private void handleErrorPeticion(PeticionRespuesta peticionRespuesta, Exception e) {
        log.error("=========================================================");
        log.error("Error al procesar la peticion asincrona");
        log.error("IdPeticion: " + peticionRespuesta.getIdPeticion());
        log.error("ID Certificado: " + peticionRespuesta.getServicio().getId());
        log.error("Mensaje: " + e.getMessage());
        log.error("StackTrace", e);
        log.error("=========================================================");

        try {
            PeticionRespuesta peticionRespuestaaux = this.peticionRespuestaDao.select(peticionRespuesta.getIdPeticion());
            if (e instanceof ScspException) {
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
