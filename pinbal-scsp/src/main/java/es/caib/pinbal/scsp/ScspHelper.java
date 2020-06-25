/**
 * 
 */
package es.caib.pinbal.scsp;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import es.caib.pinbal.scsp.JustificantArbreHelper.ElementArbre;
import es.caib.pinbal.scsp.XmlHelper.DadesEspecifiquesNode;
import es.caib.pinbal.scsp.tree.Tree;
import es.scsp.bean.common.Atributos;
import es.scsp.bean.common.Consentimiento;
import es.scsp.bean.common.DatosGenericos;
import es.scsp.bean.common.Emisor;
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
import es.scsp.client.ClienteUnico;
import es.scsp.common.dao.ClavePrivadaDao;
import es.scsp.common.dao.ClavePublicaDao;
import es.scsp.common.dao.EmisorCertificadoDao;
import es.scsp.common.dao.OrganismoCesionarioDao;
import es.scsp.common.dao.ParametroConfiguracionDao;
import es.scsp.common.dao.PeticionRespuestaDao;
import es.scsp.common.dao.PinbalDao;
import es.scsp.common.dao.ServicioDao;
import es.scsp.common.dao.ServicioOrganismoCesionarioDao;
import es.scsp.common.dao.TipoMensajeDao;
import es.scsp.common.dao.TokenDao;
import es.scsp.common.dao.TransmisionDao;
import es.scsp.common.domain.core.ClavePrivada;
import es.scsp.common.domain.core.ClavePublica;
import es.scsp.common.domain.core.EmisorCertificado;
import es.scsp.common.domain.core.OrganismoCesionario;
import es.scsp.common.domain.core.ParametroConfiguracion;
import es.scsp.common.domain.core.PeticionRespuesta;
import es.scsp.common.domain.core.Servicio;
import es.scsp.common.domain.core.TipoMensaje;
import es.scsp.common.domain.core.Token;
import es.scsp.common.domain.req.ServicioOrganismoCesionario;
import es.scsp.common.exceptions.ScspException;
import es.scsp.common.utils.DateUtils;
import es.scsp.common.utils.StaticContextSupport;

/**
 * Mètodes d'ajuda per invocar els serveis SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ScspHelper {

	private JustificantArbreHelper justificantArbrehelper;
	private XmlHelper xmlHelper;

	private ApplicationContext applicationContext;
	private MessageSource messageSource;

	@PersistenceContext
	private EntityManager entityManager;



	public ScspHelper(ApplicationContext applicationContext, MessageSource messageSource) {
		super();
		this.applicationContext = applicationContext;
		this.messageSource = messageSource;
	}

	public String generarIdPeticion(
			String serveiCodi) throws ScspException {
		LOGGER.debug("Generació id petició (serveiCodi=" + serveiCodi + ")");
		return getClienteUnico().getIDPeticion(serveiCodi);
	}

	public ResultatEnviamentPeticio enviarPeticionSincrona(
			String idPeticion,
			List<Solicitud> solicituds) {
		LOGGER.debug("Nova petició SCSP (peticionId=" + idPeticion + ")");
		Peticion peticion = null;
		try {
			peticion = crearPeticion(
					idPeticion,
					solicituds);
		} catch (Exception ex) {
			LOGGER.error("Error al generar nova petició SCSP síncrona (peticionId=" + idPeticion + ")", ex);
			ResultatEnviamentPeticio estat = new ResultatEnviamentPeticio();
			estat.setErrorGeneracio(true);
			estat.setEstatCodi("ERR");
			if (ex.getMessage() != null)
				estat.setEstatDescripcio(ex.getMessage());
			else
				estat.setEstatDescripcio(ex.getClass().getName());
			estat.setIdsSolicituds(
					getIdsSolicitudes(
							idPeticion,
							solicituds.size()));
			return estat;
		}
		try {
			getClienteUnico().realizaPeticionSincrona(peticion);
			return getResultatEnviamentPeticio(idPeticion);
		} catch (ScspException ex) {
			LOGGER.error("Error al enviar petició SCSP síncrona (peticionId=" + idPeticion + ")", ex);
			ResultatEnviamentPeticio estat = new ResultatEnviamentPeticio();
			estat.setEstatCodi(ex.getScspCode());
			estat.setErrorEnviament(true);
			estat.setEstatDescripcio(ex.getMessage());
			estat.setIdsSolicituds(
					getIdsSolicitudes(
							idPeticion,
							solicituds.size()));
			return estat;
		}
	}

	public ResultatEnviamentPeticio enviarPeticionAsincrona(
			String idPeticion,
			List<Solicitud> solicituds) {
		LOGGER.debug("Nova petició SCSP (peticionId=" + idPeticion + ")");
		Peticion peticion = null;
		try {
			peticion = crearPeticion(
					idPeticion,
					solicituds);
		} catch (Exception ex) {
			LOGGER.error("Error al generar nova petició SCSP síncrona (peticionId=" + idPeticion + ")", ex);
			ResultatEnviamentPeticio estat = new ResultatEnviamentPeticio();
			estat.setErrorGeneracio(true);
			estat.setEstatCodi("ERR");
			if (ex.getMessage() != null)
				estat.setEstatDescripcio(ex.getMessage());
			else
				estat.setEstatDescripcio(ex.getClass().getName());
			estat.setIdsSolicituds(
					getIdsSolicitudes(
							idPeticion,
							solicituds.size()));
			return estat;
		}
		try {
			getClienteUnico().realizaPeticionAsincrona(peticion);
			return getResultatEnviamentPeticio(idPeticion);
		} catch (ScspException ex) {
			LOGGER.error("Error al enviar petició SCSP asíncrona (peticionId=" + idPeticion + ")", ex);
			ResultatEnviamentPeticio estat = new ResultatEnviamentPeticio();
			estat.setEstatCodi(ex.getScspCode());
			estat.setErrorEnviament(true);
			estat.setEstatDescripcio(ex.getMessage());
			estat.setIdsSolicituds(
					getIdsSolicitudes(
							idPeticion,
							solicituds.size()));
			return estat;
		}
	}

	public ByteArrayOutputStream generaJustificanteTransmision(
			String idPeticion,
			String idSolicitud) throws ScspException {
		LOGGER.debug("Generant justificant de transmissió SCSP (idPeticion=" + idPeticion + ")");
		es.scsp.common.domain.core.Transmision transmision = getTransmision(
				idPeticion,
				idSolicitud);
		return getClienteUnico().generaJustificanteTransmision(
				transmision.getIdTransmision(),
				idPeticion);
	}

	public ElementArbre generarArbreJustificant(
			String idPeticion,
			String idSolicitud,
			Locale locale) throws ScspException, ParserConfigurationException {
		LOGGER.debug("Generant arbre pel justificant de transmissió SCSP (idPeticion=" + idPeticion + ", locale=" + locale + ")");
		Respuesta respuesta = getClienteUnico().recuperaRespuesta(idPeticion);
		TransmisionDatos transmisionDatos = null;
		for (TransmisionDatos ts: respuesta.getTransmisiones().getTransmisionDatos()) {
			String ids = ts.getDatosGenericos().getTransmision().getIdSolicitud();
			if (idSolicitud.equals(ids)) {
				transmisionDatos = ts;
				break;
			}
		}
		if (transmisionDatos != null) {
			ElementArbre arbre = getJustificantArbrehelper().generarArbre(
					transmisionDatos,
					idPeticion,
					locale);
			return arbre;
		} else {
			throw new ScspException("0234", "No s'ha pogut trobar la transmissió SCSP (idPeticion=" + idPeticion + ", idSolicitud=" + idSolicitud + ")");
		}
	}

	public Map<String, Object> getDadesEspecifiquesPeticio(
			String idPeticion,
			String idSolicitud) throws Exception {
		LOGGER.debug("Consulta map de dades específiques petició (" +
				"idPeticion=" + idPeticion + ", " +
				"idSolicitud=" + idSolicitud + ")");
		return getXmlHelper().getDadesEspecifiquesXml(
				getXmlSolicitudTransmision(
						idPeticion,
						idSolicitud));
	}
	public Map<String, Object> getDadesEspecifiquesResposta(
			String idPeticion,
			String idSolicitud) throws Exception {
		LOGGER.debug("Consulta map de dades específiques resposta (" +
				"idPeticion=" + idPeticion + ", " +
				"idSolicitud=" + idSolicitud + ")");
		return getXmlHelper().getDadesEspecifiquesXml(
				getXmlTransmisionDatos(
						idPeticion,
						idSolicitud));
	}

	public boolean isPeticionEnviada(
			String idPeticion) throws ScspException {
		LOGGER.debug("Comprovant si s'ha enviat peticio (idPeticion=" + idPeticion + ")");
		PeticionRespuesta peticionRespuesta = getPeticionRespuestaDao().select(
				idPeticion);
		if (peticionRespuesta != null) {
			Token token = getTokenDao().select(
					getTipoMensajeDao().select(TipoMensaje.PETICION),
					peticionRespuesta);
			return token != null;
		}
		return false;
	}

	public Resposta recuperarResposta(
			String idPeticion,
			String idSolicitud,
			boolean multiple) throws Exception {
		LOGGER.debug("Recuperant resposta SCSP (" +
				"idPeticion=" + idPeticion + "," +
				"idSolicitud=" + idSolicitud + "," +
				"multiple=" + multiple + ")");
		Resposta resposta = new Resposta();
		PeticionRespuesta peticionRespuesta = getPeticionRespuestaDao().select(
				idPeticion);
		if (peticionRespuesta != null) {
			resposta.setRespostaData(peticionRespuesta.getFechaRespuesta());
			es.scsp.common.domain.core.Transmision transmision = null;
			if (!multiple) {
				transmision = getTransmision(
						idPeticion,
						idSolicitud);
			} else {
				transmision = getPrimeraTransmision(idPeticion);
			}
			if (transmision != null) {
				resposta.setUnitatTramitadora(transmision.getUnidadTramitadora());
				resposta.setUnitatTramitadoraCodi(transmision.getCodigoUnidadTramitadora());
				String cons = transmision.getConsentimiento();
				if (cons.equals(Consentimiento.Si.toString()))
					resposta.setConsentiment(Consentiment.Si);
				else
					resposta.setConsentiment(Consentiment.Llei);
				resposta.setExpedientId(transmision.getExpediente());
				// Extreu l'expedient i la finalitat codificats a dins la finalidad SCSP
				String finalitat = transmision.getFinalidad();
				String[] finParts = finalitat.split("#::#");
				if (finParts.length > 1) {
					String finEid = null;
					String finFin = null;
					if (finParts.length == 2) {
						if (finalitat.contains("#::##::#")) {
							finFin = finParts[1];
						} else {
							finEid = finParts[1];
						}
					} else if (finParts.length == 3) {
						finEid = finParts[1];
						finFin = finParts[2];
					}
					if (resposta.getExpedientId() == null) {
						resposta.setExpedientId(finEid);
					}
					if (resposta.getFinalitat() == null) {
						resposta.setFinalitat(finFin);
					}
				}
				if (multiple) {
					resposta.setExpedientId(null);
					resposta.setPeticioXml(
							getXmlPeticion(idPeticion));
				} else {
					resposta.setPeticioXml(
							getXmlSolicitudTransmision(
									idPeticion,
									idSolicitud));
					resposta.setRespostaXml(
							getXmlTransmisionDatos(
									idPeticion,
									idSolicitud));
				}
				resposta.setResultatEnviament(
						getResultatEnviamentPeticio(idPeticion));
			}
		}
		return resposta;
	}
	public Respuesta recuperarRespuestaScsp(
			String idPeticion) throws ScspException {
		LOGGER.debug("Recuperant respuesta SCSP (idPeticion=" + idPeticion + ")");
		return getClienteUnico().recuperaRespuesta(idPeticion);
	}

	public ResultatEnviamentPeticio recuperarResultatEnviamentPeticio(
			String idPeticion) throws Exception {
		LOGGER.debug("Recuperant estat comunicació (peticionId=" + idPeticion + ")");
		return getResultatEnviamentPeticio(idPeticion);
	}

	public Tree<DadesEspecifiquesNode> generarArbreDadesEspecifiques(
			String codigoServicio) throws Exception {
		LOGGER.debug("Generant l'arbre de dades específiques pel servicio (codi=" + codigoServicio + ")");
		Servicio servicio = getServicio(codigoServicio);
		return getXmlHelper().getArbrePerDadesEspecifiques(servicio);
	}
	
	public Tree<DadesEspecifiquesNode> generarArbreDadesEspecifiques(
			String codigoServicio, boolean gestioXsdActiva) throws Exception {
		LOGGER.debug("Generant l'arbre de dades específiques pel servicio (codi=" + codigoServicio + ")");
		Servicio servicio = getServicio(codigoServicio);
		if(gestioXsdActiva) {
			return getXmlHelper().getArbrePerDadesEspecifiques(servicio, gestioXsdActiva);
		}else{
			return getXmlHelper().getArbrePerDadesEspecifiques(servicio);
		}
	}

	public Element copiarDadesEspecifiquesRecobriment(
			String codigoCertificado,
			Element dadesEspecifiques) throws Exception {
		LOGGER.debug("Copia dades específiques per recobriment (" +
				"codigoCertificado=" + codigoCertificado + ")");
		if (dadesEspecifiques != null)
			return getXmlHelper().copiarDadesEspecifiquesRecobriment(
					getServicioDao().select(codigoCertificado),
					dadesEspecifiques);
		else
			return null;
	}

	@Cacheable("serveiDescripcio")
	public String getServicioDescripcion(String codigoServicio) {
		LOGGER.debug("Consulta de la descripció del servei (" +
				"codigoServicio=" + codigoServicio + ")");
		Servicio servicio = getServicioDao().select(codigoServicio);
		if (servicio != null)
			return servicio.getDescripcion();
		else
			return null;
	}
	@Cacheable("emisorNombre")
	public String getEmisorNombre(String cif) {
		LOGGER.debug("Consulta del nom de l'emissor (" +
				"cif=" + cif + ")");
		EmisorCertificado emisor = getEmisorCertificadoDao().selectByCif(cif);
		if (emisor != null)
			return emisor.getNombre();
		else
			return null;
	}
	@Cacheable("clavePrivadaNombre")
	public String getClavePrivadaNombre(String alias) {
		LOGGER.debug("Consulta del nom de la clau privada (" +
				"alias=" + alias + ")");
		ClavePrivada clave = getClavePrivadaDao().selectByAlias(alias);
		if (clave != null)
			return clave.getNombre();
		else
			return null;
	}
	@Cacheable("clavePrivadaNumeroSerie")
	public String getClavePrivadaNumeroSerie(String alias) {
		LOGGER.debug("Consulta del núm. de sèrie de la clau privada (" +
				"alias=" + alias + ")");
		ClavePrivada clave = getClavePrivadaDao().selectByAlias(alias);
		if (clave != null)
			return clave.getNumeroSerie();
		else
			return null;
	}
	@Cacheable("clavePublicaNombre")
	public String getClavePublicaNombre(String alias) {
		LOGGER.debug("Consulta del nom de la clau pública (" +
				"alias=" + alias + ")");
		ClavePublica clave = getClavePublicaDao().selectByAlias(alias);
		if (clave != null)
			return clave.getNombre();
		else
			return null;
	}
	@Cacheable("clavePublicaNumeroSerie")
	public String getClavePublicaNumeroSerie(String alias) {
		LOGGER.debug("Consulta del núm. de sèrie de la clau pública (" +
				"alias=" + alias + ")");
		ClavePublica clave = getClavePublicaDao().selectByAlias(alias);
		if (clave != null)
			return clave.getNumeroSerie();
		else
			return null;
	}

	public Date getTerPeticion(
			String idPeticion) throws ScspException {
		LOGGER.debug("Consulta del TER de la petició (" +
				"idPeticion=" + idPeticion + ")");
		PeticionRespuesta peticionRespuesta = getPeticionRespuestaDao().select(idPeticion);
		if (peticionRespuesta != null)
			return peticionRespuesta.getTer();
		else
			return null;
	}

	public boolean servicioHasConsultes(String codigoServicio) {
		LOGGER.debug("Consulta si un servei té consultes enviades (" +
				"codigoServicio=" + codigoServicio + ")");
		Servicio servicio = getServicioDao().select(codigoServicio);
		return getPeticionRespuestaDao().count(servicio) > 0;
	}

	public List<Servicio> findServicioAll() {
		LOGGER.debug("Consulta de la llista de serveis");
		List<Servicio> servicios = getServicioDao().select();
		Collections.sort(
				servicios,
				new Comparator<Servicio>() {
					public int compare(Servicio o1, Servicio o2) {
						return o1.getCodCertificado().compareTo(o2.getCodCertificado());
					}
		});
		return servicios;
	}
	
	public Servicio findServicioByCode(long id) {
		LOGGER.debug("Consulta de la llista de serveis segons un codi");
		Servicio servicio = getServicioDao().select(id);
		return servicio;
	}
	
	public List<EmisorCertificado> findEmisorCertificadoAll() {
		LOGGER.debug("Consulta de la llista d'emissors");
		return getEmisorCertificadoDao().select();
	}
	public List<ClavePublica> findClavePublicaAll() {
		LOGGER.debug("Consulta de la llista de claus públiques");
		return getClavePublicaDao().select();
	}
	public List<ClavePrivada> findClavePrivadaAll() {
		LOGGER.debug("Consulta de la llista de claus privades");
		return getClavePrivadaDao().select();
	}

	public void saveServicio(Servicio servicio) {
		LOGGER.debug("Actualització de les dades del servei(" +
				"serveiCodi=" + servicio.getCodCertificado() + ")");
		getServicioDao().save(servicio);
	}
	public Servicio getServicio(String codigoServicio) {
		LOGGER.debug("Consulta de les dades del servei(" +
				"codigoServicio=" + codigoServicio + ")");
		Servicio servicio = getServicioDao().select(codigoServicio);
		return servicio;
	}
	public void deleteServicio(String codigoServicio) {
		LOGGER.debug("Eliminació del servei(" +
				"codigoServicio=" + codigoServicio + ")");
		Servicio servicio = getServicioDao().select(codigoServicio);
		getPinbalDao().delete(servicio);
	}

	public String generarSolicitudId(
			String peticionId,
			int index) {
		LOGGER.debug("Generació de id de sol·licitud (" +
				"peticionId=" + peticionId + ", " +
				"index=" + index + ")");
		return peticionId + String.format("%05d", index);
	}

	public void organismoCesionarioSave(
			String cif,
			String nombre,
			Date fechaAlta,
			Date fechaBaja,
			boolean bloqueado) {
		LOGGER.debug("Guardant organismo cesionario (" +
				"cif=" + cif + ", " +
				"nombre=" + nombre + ", " +
				"fechaAlta=" + fechaAlta + ", " +
				"fechaBaja=" + fechaBaja + ", " + 
				"bloqueado=" + bloqueado + ")");
		OrganismoCesionario organismoCesionario = getOrganismoCesionarioAmbCif(cif);
		if (organismoCesionario == null) {
			organismoCesionario = new OrganismoCesionario();
			organismoCesionario.setCif(cif);
		}
		organismoCesionario.setNombre(nombre);
		organismoCesionario.setFechaAlta(fechaAlta);
		organismoCesionario.setFechaBaja(fechaBaja);
		organismoCesionario.setBloqueado(bloqueado);
		getOrganismoCesionarioDao().save(organismoCesionario);
	}
	public void organismoCesionarioDelete(String cif) {
		LOGGER.debug("Esborrant organismo cesionario (" +
				"cif=" + cif + ")");
		OrganismoCesionario organismoCesionario = getOrganismoCesionarioAmbCif(cif);
		List<ServicioOrganismoCesionario> servicios = getServicioOrganismoCesionarioDao().selectHistorico(organismoCesionario);
		for (ServicioOrganismoCesionario servicio: servicios) {
			getPinbalDao().delete(servicio);	
		}
		if(organismoCesionario != null) {
			getPinbalDao().delete(organismoCesionario);	
		}
	}
	public OrganismoCesionario organismoCesionarioFindByCif(String cif) {
		LOGGER.debug("Consulta organismo cesionario (" +
				"cif=" + cif + ")");
		return getOrganismoCesionarioAmbCif(cif);
	}
	public void actualitzarServiciosActivosOrganismoCesionario(
			String cif,
			String[] activos) {
		LOGGER.debug("Guardant organismo cesionario (" +
				"cif=" + cif + ", " +
				"activos=" + Arrays.toString(activos) + ")");
		OrganismoCesionario organismoCesionario = getOrganismoCesionarioAmbCif(cif);
		List<ServicioOrganismoCesionario> servicios = getServicioOrganismoCesionarioDao().selectHistorico(
				organismoCesionario);
		// Actualitza el camp bloqueado dels serveis existents
		for (ServicioOrganismoCesionario servicio: servicios) {
			boolean trobat = false;
			for (String activo: activos) {
				if (activo != null && servicio != null && servicio.getServicio() != null && activo.equals(servicio.getServicio().getCodCertificado())) {
					trobat = true;
					break;
				}
			}
			servicio.setBloqueado(!trobat);
			if(!trobat) {
				getPinbalDao().delete(servicio);
			}
		}
		// Crea els servicios que no existeixen
		for (String activo: activos) {
			boolean trobat = false;
			for (ServicioOrganismoCesionario servicio: servicios) {
				if (activo != null && servicio != null && servicio.getServicio() != null && activo.equals(servicio.getServicio().getCodCertificado())) {
					trobat = true;
					break;
				}
			}
			if (!trobat) {
				Servicio servicio = getServicioDao().select(activo);
				if (servicio != null) {
					ServicioOrganismoCesionario servicioOrganismo = new ServicioOrganismoCesionario();
					servicioOrganismo.setOrganismo(organismoCesionario);
					servicioOrganismo.setServicio(servicio);
					servicioOrganismo.setClavePrivada(servicio.getClaveFirma());
					servicioOrganismo.setFechaAlta(new Date());
					servicioOrganismo.setBloqueado(false);
					getPinbalDao().save(servicioOrganismo);
				}
			}
		}
	}



	private static final String PROP_PREFIX = "es.caib.pinbal.scsp.";
	public void copiarPropertiesToDb(Properties props) {
		LOGGER.debug("Copiant les propietats cap a la taula core_parametro_configuracion");
		for (Object key: props.keySet()) {
			String prop = (String)key;
			if (prop.startsWith(PROP_PREFIX)) {
				String valor = props.getProperty(prop);
				String propNoPrefix = prop.substring(PROP_PREFIX.length());
				if (propNoPrefix.startsWith("keystore")) {
					ParametroConfiguracion paramDb = getParametroConfiguracionDao().select(propNoPrefix);
					boolean equal = false;
					if (paramDb != null)
						equal = ObjectUtils.equals(valor, paramDb.getValor());
					else
						equal = valor == null;
					if (!equal)
						LOGGER.error("El valor de la propietat " + propNoPrefix + " difereix entre el fitxer de properties i la base de dades");
				} else {
					ParametroConfiguracion param = new ParametroConfiguracion();
					param.setNombre(propNoPrefix);
					param.setValor(valor);
					try {
						getParametroConfiguracionDao().save(param);
					} catch (Exception ignored) {
					}
				}
			}
		}
	}

	public String nodeToString(Node node) throws Exception {
		LOGGER.debug("Node to string");
		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transformer = transFactory.newTransformer();
		StringWriter buffer = new StringWriter();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.transform(
				new DOMSource(node),
				new StreamResult(buffer));
		return buffer.toString();
	}



	private Peticion crearPeticion(
			String idPeticion,
			List<Solicitud> solicituds) throws Exception {
		String serveiCodi = solicituds.get(0).getServeiCodi();
		Peticion peticion = new Peticion();
		// Afegeix les sol·licituds
		Solicitudes solicitudes = new Solicitudes();
		solicitudes.setSolicitudTransmision(
				new ArrayList<SolicitudTransmision>());
		int indexSolicitud = 1;
		for (Solicitud solicitud: solicituds) {
			solicitudes.getSolicitudTransmision().add(
					crearSolicitudTransmision(
							idPeticion,
							solicituds.size(),
							solicitud,
							indexSolicitud++));
		}
		peticion.setSolicitudes(solicitudes);
		// Afegeix els atributs
		Atributos atributos = new Atributos();
		atributos.setCodigoCertificado(serveiCodi);
		atributos.setIdPeticion(idPeticion);
		String timeStamp = DateUtils.parseISO8601(new Date());
		atributos.setTimeStamp(timeStamp);
		atributos.setNumElementos(
				String.valueOf(
						peticion.getSolicitudes().getSolicitudTransmision().size()));
		peticion.setAtributos(atributos);
		return peticion;
	}

	private SolicitudTransmision crearSolicitudTransmision(
			String idPeticion,
			int numSolicitudes,
			Solicitud solicitud,
			int index) throws Exception {
		SolicitudTransmision st = new SolicitudTransmision();
		DatosGenericos datosGenericos = new DatosGenericos();
		Emisor beanEmisor = new Emisor();
		beanEmisor.setNifEmisor(getCifEmisor(solicitud.getServeiCodi()));
		beanEmisor.setNombreEmisor(getNombreEmisor(solicitud.getServeiCodi()));
		datosGenericos.setEmisor(beanEmisor);
		Solicitante solicitante = new Solicitante();
		datosGenericos.setSolicitante(solicitante);
		solicitante.setIdentificadorSolicitante(solicitud.getSolicitantIdentificacio());
		solicitante.setNombreSolicitante(solicitud.getSolicitantNom());
		solicitante.setFinalidad(generarFinalidad(solicitud));
		if (solicitud.getConsentiment().equals(Consentiment.Si))
			solicitante.setConsentimiento(Consentimiento.Si);
		else
			solicitante.setConsentimiento(Consentimiento.Ley);
		Funcionario funcionario = new Funcionario();
		funcionario.setNombreCompletoFuncionario(solicitud.getFuncionariNom());
		funcionario.setNifFuncionario(solicitud.getFuncionariNif());
		solicitante.setFuncionario(funcionario);
		Procedimiento procedimiento = new Procedimiento();
		procedimiento.setCodProcedimiento(solicitud.getProcedimentCodi());
		procedimiento.setNombreProcedimiento(solicitud.getProcedimentNom());
		solicitante.setProcedimiento(procedimiento);
		solicitante.setUnidadTramitadora(solicitud.getUnitatTramitadora());
		solicitante.setCodigoUnidadTramitadora(solicitud.getUnitatTramitadoraCodi());
		if (solicitud.getExpedientId() != null && solicitud.getExpedientId().length() > 0)
			solicitante.setIdExpediente(solicitud.getExpedientId());
		datosGenericos.setTitular(new Titular());
		if (solicitud.getTitularDocument() != null && solicitud.getTitularDocument().length() > 0) {
			if (solicitud.getTitularDocumentTipus() != null) {
				if (DocumentTipus.CIF.equals(solicitud.getTitularDocumentTipus()))
					datosGenericos.getTitular().setTipoDocumentacion(TipoDocumentacion.CIF);
				else if (DocumentTipus.NIF.equals(solicitud.getTitularDocumentTipus()))
					datosGenericos.getTitular().setTipoDocumentacion(TipoDocumentacion.NIF);
				else if (DocumentTipus.DNI.equals(solicitud.getTitularDocumentTipus()))
					datosGenericos.getTitular().setTipoDocumentacion(TipoDocumentacion.DNI);
				else if (DocumentTipus.Passaport.equals(solicitud.getTitularDocumentTipus()))
					datosGenericos.getTitular().setTipoDocumentacion(TipoDocumentacion.Pasaporte);
				else if (DocumentTipus.NIE.equals(solicitud.getTitularDocumentTipus()))
					datosGenericos.getTitular().setTipoDocumentacion(TipoDocumentacion.NIE);
			}
			datosGenericos.getTitular().setDocumentacion(solicitud.getTitularDocument());
		}
		if (solicitud.getTitularNom() != null && solicitud.getTitularNom().length() > 0)
			datosGenericos.getTitular().setNombre(solicitud.getTitularNom());
		if (solicitud.getTitularLlinatge1() != null && solicitud.getTitularLlinatge1().length() > 0)
			datosGenericos.getTitular().setApellido1(solicitud.getTitularLlinatge1());
		if (solicitud.getTitularLlinatge2() != null && solicitud.getTitularLlinatge2().length() > 0)
			datosGenericos.getTitular().setApellido2(solicitud.getTitularLlinatge2());
		if (solicitud.getTitularNomComplet() != null && solicitud.getTitularNomComplet().length() > 0)
			datosGenericos.getTitular().setNombreCompleto(solicitud.getTitularNomComplet());
		datosGenericos.setTransmision(new Transmision());
		datosGenericos.getTransmision().setCodigoCertificado(solicitud.getServeiCodi());
		String timeStamp = DateUtils.parseISO8601(new Date());
		datosGenericos.getTransmision().setFechaGeneracion(timeStamp);
		datosGenericos.getTransmision().setIdSolicitud(
				getIdSolicitud(
						idPeticion,
						numSolicitudes,
						index));
		st.setDatosGenericos(datosGenericos);
		if (solicitud.getDadesEspecifiquesElement() != null) {
			st.setDatosEspecificos(solicitud.getDadesEspecifiquesElement());
		}
		if (solicitud.getDadesEspecifiquesMap() != null && solicitud.getDadesEspecifiquesMap().size() > 0) {
			st.setDatosEspecificos(
					getXmlHelper().crearDadesEspecifiques(
							getServicioDao().select(solicitud.getServeiCodi()),
							solicitud.getDadesEspecifiquesMap()));
		}
		return st;
	}

	private String[] getIdsSolicitudes(
			String idPeticion,
			int numSolicitudes) {
		String[] ids = new String[numSolicitudes];
		for (int i = 0; i < numSolicitudes; i++) {
			ids[i] = getIdSolicitud(
					idPeticion,
					numSolicitudes,
					i);
		}
		return ids;
	}
	private String getIdSolicitud(
			String idPeticion,
			int numSolicitudes,
			int index) {
		if (numSolicitudes == 1) {
			// Si la petició només te una sol·licitud posam com a id de sol·licitud
			// el mateix id que la petició. Això és per evitar que alguns serveis
			// donin el següent error:
			// [0417] El identificador de peticion y el de solicitud no coinciden
			return idPeticion;
		} else {
			return String.format("%06d", index);
		}
	}

	private String getCifEmisor(String certificado) {
		configurarAccesScsp();
		ServicioDao servicioDao = (ServicioDao)applicationContext.getBean("servicioDao");
		Servicio servicio = servicioDao.select(certificado);
		return servicio.getEmisor().getCif();
	}
	private String getNombreEmisor(String certificado) {
		String cifEmisor = getCifEmisor(certificado);
		EmisorCertificadoDao emisorCertificadoDao = (EmisorCertificadoDao)applicationContext.getBean("emisorCertificadoDao");
		EmisorCertificado emisorCertificado = emisorCertificadoDao.selectByCif(cifEmisor);
		return emisorCertificado.getNombre();
	}
	private String generarFinalidad(Solicitud solicitud) {
		StringBuilder sb = new StringBuilder();
		sb.append(solicitud.getProcedimentCodi());
		sb.append("#::#");
		if (solicitud.getExpedientId() != null)
			sb.append(solicitud.getExpedientId());
		sb.append("#::#");
		if (solicitud.getFinalitat() != null)
			sb.append(solicitud.getFinalitat());
		return sb.toString();
	}

	private ResultatEnviamentPeticio getResultatEnviamentPeticio(
			String idPeticion) throws ScspException {
		ResultatEnviamentPeticio resultat = new ResultatEnviamentPeticio();
		PeticionRespuesta peticionRespuesta = getPeticionRespuestaDao().select(idPeticion);
		resultat.setEstatCodi(peticionRespuesta.getEstado());
		resultat.setErrorRecepcio(!peticionRespuesta.getEstado().startsWith("00"));
		resultat.setEstatDescripcio(peticionRespuesta.getError());
		List<es.scsp.common.domain.core.Transmision> transmisiones = getTransmisionDao().select(peticionRespuesta);
		if (transmisiones != null) {
			String[] idsSolicituds = new String[transmisiones.size()];
			int i = 0;
			for (es.scsp.common.domain.core.Transmision transmision: transmisiones) {
				idsSolicituds[i++] = transmision.getIdSolicitud();
			}
			resultat.setIdsSolicituds(idsSolicituds);
		}
		return resultat;
	}

	private es.scsp.common.domain.core.Transmision getTransmision(
			String idPeticion,
			String idSolicitud) throws ScspException {
		PeticionRespuesta peticionRespuesta = getPeticionRespuestaDao().select(idPeticion);
		es.scsp.common.domain.core.Transmision transmision = getTransmisionDao().select(
				peticionRespuesta,
				idSolicitud);
		return transmision;
	}

	private es.scsp.common.domain.core.Transmision getPrimeraTransmision(
			String idPeticion) throws ScspException {
		PeticionRespuesta peticionRespuesta = getPeticionRespuestaDao().select(idPeticion);
		List<es.scsp.common.domain.core.Transmision> transmisions = getTransmisionDao().select(peticionRespuesta);
		if (transmisions != null && !transmisions.isEmpty())
			return transmisions.get(0);
		else
			return null;
	}

	private String getXmlPeticion(
			String idPeticion) throws Exception {
		PeticionRespuesta peticionRespuesta = getPeticionRespuestaDao().select(idPeticion);
		Token token = getTokenDao().select(
				getTipoMensajeDao().select(TipoMensaje.PETICION),
				peticionRespuesta);
		if (token != null) {
			return getXmlHelper().getXmlPeticion(
					token.getDatos(),
					idPeticion);
		} else {
			return null;
		}
	}

	private String getXmlSolicitudTransmision(
			String idPeticion,
			String idSolicitud) throws Exception {
		PeticionRespuesta peticionRespuesta = getPeticionRespuestaDao().select(idPeticion);
		Token token = getTokenDao().select(
				getTipoMensajeDao().select(TipoMensaje.PETICION),
				peticionRespuesta);
		if (token != null) {
			return getXmlHelper().getXmlSolicitudTransmision(
					token.getDatos(),
					idSolicitud);
		} else {
			return null;
		}
	}

	private String getXmlTransmisionDatos(
			String idPeticion,
			String idSolicitud) throws ScspException {
		es.scsp.common.domain.core.Transmision transmision = getTransmision(
				idPeticion,
				idSolicitud);
		if (transmision != null)
			return transmision.getXmlTransmision();
		else
			return null;
	}

	private OrganismoCesionario getOrganismoCesionarioAmbCif(
			String cif) {
		List<OrganismoCesionario> organismos = getOrganismoCesionarioDao().getAll();
		for (OrganismoCesionario organismo: organismos) {
			if (cif.equals(organismo.getCif())) {
				return organismo;
			}
		}
		return null;
	}

	private ServicioDao getServicioDao() {
		configurarAccesScsp();
		return (ServicioDao)applicationContext.getBean("servicioDao");
	}
	private EmisorCertificadoDao getEmisorCertificadoDao() {
		configurarAccesScsp();
		return (EmisorCertificadoDao)applicationContext.getBean("emisorCertificadoDao");
	}
	private ClavePublicaDao getClavePublicaDao() {
		configurarAccesScsp();
		return (ClavePublicaDao)applicationContext.getBean("clavePublicaDao");
	}
	private ClavePrivadaDao getClavePrivadaDao() {
		configurarAccesScsp();
		return (ClavePrivadaDao)applicationContext.getBean("clavePrivadaDao");
	}
	private PeticionRespuestaDao getPeticionRespuestaDao() {
		configurarAccesScsp();
		return (PeticionRespuestaDao)applicationContext.getBean("peticionRespuestaDao");
	}
	private TransmisionDao getTransmisionDao() {
		configurarAccesScsp();
		return (TransmisionDao)applicationContext.getBean("transmisionDao");
	}
	private ParametroConfiguracionDao getParametroConfiguracionDao() {
		configurarAccesScsp();
		return (ParametroConfiguracionDao)applicationContext.getBean("parametroConfiguracionDao");
	}
	private TipoMensajeDao getTipoMensajeDao() {
		configurarAccesScsp();
		return (TipoMensajeDao)applicationContext.getBean("tipoMensajeDao");
	}
	private TokenDao getTokenDao() {
		configurarAccesScsp();
		return (TokenDao)applicationContext.getBean("tokenDao");
	}
	private OrganismoCesionarioDao getOrganismoCesionarioDao() {
		configurarAccesScsp();
		return (OrganismoCesionarioDao)applicationContext.getBean("organismoCesionarioDao");
	}
	private ServicioOrganismoCesionarioDao getServicioOrganismoCesionarioDao() {
		configurarAccesScsp();
		return (ServicioOrganismoCesionarioDao)applicationContext.getBean("servicioOrganismoCesionarioDao");
	}
	private PinbalDao getPinbalDao() {
		configurarAccesScsp();
		return (PinbalDao)applicationContext.getBean("pinbalDao");
	}
	private ClienteUnico getClienteUnico() {
		configurarAccesScsp();
		return (ClienteUnico)applicationContext.getBean("clienteUnico");
	}

	private JustificantArbreHelper getJustificantArbrehelper() {
		if (justificantArbrehelper == null) {
			justificantArbrehelper = new JustificantArbreHelper();
			justificantArbrehelper.setMessageSource(messageSource);
		}
		return justificantArbrehelper;
	}
	private XmlHelper getXmlHelper() {
		if (xmlHelper == null) {
			xmlHelper = new XmlHelper();
		}
		return xmlHelper;
	}

	private static boolean accesScspConfigurat = false;
	private synchronized void configurarAccesScsp() {
		try {
			if (!accesScspConfigurat) {
				// Configuració del StaticContextSupport
				StaticContextSupport.setContextInstance(applicationContext);
				accesScspConfigurat = true;
			}
		} catch (ScspException ex) {
			// StaticContextSupport ja estava configurat
			accesScspConfigurat = true;
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ScspHelper.class);

}
