/**
 * 
 */
package es.caib.pinbal.core.helper;

import es.caib.pinbal.client.helper.ClasseTramitHelper;
import es.caib.pinbal.client.recobriment.v2.ConfirmacioPeticio;
import es.caib.pinbal.client.recobriment.v2.DadesComunes;
import es.caib.pinbal.client.recobriment.v2.DadesComunesResposta;
import es.caib.pinbal.client.recobriment.v2.Emisor;
import es.caib.pinbal.client.recobriment.v2.EstatEnum;
import es.caib.pinbal.client.recobriment.v2.Funcionari;
import es.caib.pinbal.client.recobriment.v2.PeticioAsincrona;
import es.caib.pinbal.client.recobriment.v2.PeticioConfirmacioAsincrona;
import es.caib.pinbal.client.recobriment.v2.PeticioResposta;
import es.caib.pinbal.client.recobriment.v2.PeticioRespostaAsincrona;
import es.caib.pinbal.client.recobriment.v2.PeticioRespostaSincrona;
import es.caib.pinbal.client.recobriment.v2.PeticioSincrona;
import es.caib.pinbal.client.recobriment.v2.Solicitant;
import es.caib.pinbal.client.recobriment.v2.SolicitudSimple;
import es.caib.pinbal.core.model.Consulta;
import es.caib.pinbal.core.model.Entitat;
import es.caib.pinbal.core.model.ServeiCamp;
import es.caib.pinbal.core.model.ServeiConfig;
import es.caib.pinbal.core.repository.ConsultaRepository;
import es.caib.pinbal.core.repository.EntitatRepository;
import es.caib.pinbal.core.repository.ProcedimentRepository;
import es.caib.pinbal.core.repository.ServeiCampRepository;
import es.caib.pinbal.core.repository.ServeiConfigRepository;
import es.caib.pinbal.core.service.ServeiService;
import es.caib.pinbal.core.service.exception.ConsultaScspGeneracioException;
import es.caib.pinbal.plugins.DadesUsuari;
import es.caib.pinbal.plugins.SistemaExternException;
import es.caib.pinbal.scsp.ScspHelper;
import es.caib.pinbal.scsp.XmlHelper;
import es.scsp.bean.common.Atributos;
import es.scsp.bean.common.ConfirmacionPeticion;
import es.scsp.bean.common.Consentimiento;
import es.scsp.bean.common.DatosGenericos;
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
import es.scsp.common.domain.core.Servicio;
import es.scsp.common.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Mètodes comuns per a les consultes al servei de recobriment fetes
 * des del serveis web SOAP i REST.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class RecobrimentV2Helper implements ApplicationContextAware, MessageSourceAware {

	private ApplicationContext applicationContext;
	private MessageSource messageSource;
	private ScspHelper scspHelper;

    @Autowired
    private ServeiConfigRepository serveiConfigRepository;
    @Autowired
    private PluginHelper pluginHelper;
    @Autowired
    private ProcedimentRepository procedimentRepository;
    @Autowired
    private EntitatRepository entitatRepository;
    @Autowired
    private ServeiCampRepository serveiCampRepository;
    @Autowired
    private ConsultaRepository consultaRepository;

	private XmlHelper xmlHelper;

	private static final long MILISEGONS_PER_HORA = 3600000L;

	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public void setScspHelper(ScspHelper scspHelper) {
		this.scspHelper = scspHelper;
	}

	private ScspHelper getScspHelper() {
		if (scspHelper == null) {
			scspHelper = new ScspHelper(
					applicationContext,
					messageSource);
		}
		return scspHelper;
	}


	public void validateDadesComunes(DadesComunes dadesComunes, String serveiCodi, BindException errors) {
		if (dadesComunes == null) {
			errors.rejectValue("dadesComunes", "rec.val.err.dadesComunes", "No s'ha trobat l'element dadesComunes");
			return;
		}

		// ServeiCodi
		if (!serveiCodi.equals(dadesComunes.getServeiCodi())) {
			errors.rejectValue("dadesComunes.serveiCodi", "rec.val.err.serveiCodi.mismatch", "El servei informat a la petició " + serveiCodi + " no coindideix amb el de la solicitud " + dadesComunes.getServeiCodi());
		}
		// Validem cada camp de "dadesComunes"
		validateCamp(dadesComunes.getServeiCodi(), "dadesComunes.serveiCodi", errors);
		ServeiConfig serveiConfig = serveiConfigRepository.findByServei(dadesComunes.getServeiCodi());
		if (serveiConfig == null) {
			errors.rejectValue("dadesComunes.serveiCodi", "rec.val.err.serveiCodi.notfound", "No s'ha trobat el servei amb codi " + dadesComunes.getServeiCodi());
		}

		validateCamp(dadesComunes.getEntitatCif(), "dadesComunes.entitatCif", errors);
		Entitat entitat = entitatRepository.findByCif(dadesComunes.getEntitatCif());
		if (entitat == null) {
			errors.rejectValue("dadesComunes.entitatCif", "rec.val.err.entitatCif.notfound", "No s'ha trobat l'entitat amb el CIF " + dadesComunes.getEntitatCif());
		}

		validateCamp(dadesComunes.getProcedimentCodi(), "dadesComunes.procedimentCodi", errors);
		es.caib.pinbal.core.model.Procediment procediment = procedimentRepository.findByEntitatAndCodi(entitat, dadesComunes.getProcedimentCodi());
		if (procediment == null) {
			errors.rejectValue("dadesComunes.procedimentCodi", "rec.val.err.procedimentCodi.notfound", "No s'ha trobat el procediment amb el codi " + dadesComunes.getProcedimentCodi());
		}

		// Velidam que l'entitat del procediment és l'enitat amb el cif informat a la petició
		if (procediment != null && entitat != null && procediment.getEntitat() != null && !procediment.getEntitat().getCodi().equals(entitat.getCodi())) {
			errors.rejectValue("dadesComunes.procedimentCodi", "rec.val.procedimentCodi.entitat.differs", "L'entitat del procediment no té el CIF indicant al camp dadesComunes.entitatCif");
		}

		validateFuncionari(dadesComunes.getFuncionari(), errors);

		validateCamp(dadesComunes.getDepartament(), "dadesComunes.departament", 250, errors);
		validateCamp(dadesComunes.getFinalitat(), "dadesComunes.finalitat", 250, errors);

		if (dadesComunes.getConsentiment() == null) {
			errors.rejectValue("dadesComunes.consentiment", "rec.val.err.consentiment", "No s'ha trobat l'element dadesComunes.consentiment");
		}
	}

	public void validateDadesSolicitud(SolicitudSimple solicitud, String serveiCodi, BindException errors, ServeiService serveiService) {
		if (solicitud == null) {
			errors.rejectValue("solicitud", "rec.val.err.solicitud", "No s'ha trobat l'element solicitud");
			return;
		}

//		String solicitudId = solicitud.getId() != null ? solicitud.getId().trim() : null;
		es.caib.pinbal.client.recobriment.v2.Titular titular = solicitud.getTitular();
		String expedientId = solicitud.getExpedient() != null ? solicitud.getExpedient().trim() : null;
		Map<String, String> dadesEspedifiques = solicitud.getDadesEspecifiques();

//		validateCampLength(solicitudId, "solicitud.id", 64, errors);

		validateTitular(titular, serveiCodi, errors);

		validateCampLength(expedientId, "solicitud.expedient", 25, errors);

		validateDadesEspecifiques(dadesEspedifiques, serveiCodi, errors, serveiService);
	}

	public Map<String, List<String>> validateDadesSolicituds(List<SolicitudSimple> solicituds, String serveiCodi, ServeiService serveiService) {

		Map<String, List<String>> errorsSolicituds = new HashMap<>();

		Servicio servicio = getScspHelper().getServicio(serveiCodi);
		int maxSolicituds = servicio.getMaxSolicitudesPeticion();
		int numPeticions = solicituds != null ? solicituds.size() : 0;

		if (numPeticions == 0) {
			log.error("Consulta asíncrona via recobriment sense sol·licituds", "La consulta ha de tenir al manco una sol·licitud");
			errorsSolicituds.put("GLOBAL", Collections.singletonList("La consulta excedeix el màxim de sol·licituds permeses pel servei"));
		}

		if (maxSolicituds > 0 && numPeticions > maxSolicituds) {
			log.error("Error al processar dades de la petició múltiple via recobriment", "La consulta excedeix el màxim de sol·licituds permeses pel servei");
			errorsSolicituds.put("GLOBAL", Collections.singletonList("La consulta excedeix el màxim de sol·licituds permeses pel servei"));
		}

		if (numPeticions == 0)
			return errorsSolicituds;

		int index = 1;
		for (SolicitudSimple solicitud : solicituds) {
			BindException errorsSolicitud = new BindException(solicitud, "solicitud " + index);
			validateDadesSolicitud(solicitud, serveiCodi, errorsSolicitud, serveiService);
			Map<String, List<String>> errorsCamps = new HashMap<>();
			String solIndex = String.format("%06d", index);

			for (FieldError fieldError : errorsSolicitud.getFieldErrors()) {
				String campNom = solIndex + ":" + fieldError.getField(); // Nom del camp
				String campErrorMsg = fieldError.getDefaultMessage(); // Missatge d'error

				// Si el camp no existeix al mapa, inicialitzar una nova llista
				if (!errorsCamps.containsKey(campNom)) {
					errorsCamps.put(campNom, new ArrayList<String>());
				}

				// Afegir el missatge d'error a la llista del camp
				errorsCamps.get(campNom).add(campErrorMsg);
			}
			errorsSolicituds.putAll(errorsCamps);
		}

		return errorsSolicituds;
	}

	public Peticion toPeticion(PeticioSincrona peticio) throws ConsultaScspGeneracioException {
		if (peticio == null)
			return null;

		String timeStamp = DateUtils.parseISO8601(new Date());
		DadesComunes dadesComunes = peticio.getDadesComunes();
		SolicitudSimple solicitud = peticio.getSolicitud();

		String serveiCodi = dadesComunes.getServeiCodi();
		ServeiConfig serveiConfig = serveiConfigRepository.findByServei(serveiCodi);

		String idPeticion = getScspHelper().generarIdPeticion(serveiCodi);
		String idSolicitud = getIdSolicitud(idPeticion, 1, 1);

		// Crear objecte Peticion
		Peticion peticion = new Peticion();
		peticion.setAtributos(crearAtributos(idPeticion, timeStamp, serveiCodi, 1));

		// Crear Solicitudes i SolicitudTransmision
		Solicitudes solicitudes = new Solicitudes();
		SolicitudTransmision solicitudTransmision = new SolicitudTransmision();
//		solicitudTransmision.setId(solicitud.getId());
		solicitudTransmision.setDatosGenericos(crearDatosGenericos(dadesComunes, solicitud, serveiConfig, serveiCodi, idPeticion, idSolicitud, timeStamp));

		// Dades específiques (si n'hi ha)
		if (solicitud.getDadesEspecifiques() != null && !solicitud.getDadesEspecifiques().isEmpty()) {
			Servicio servicio = getScspHelper().getServicio(serveiCodi);
			List<String> pathInicialitzablesByServei = serveiCampRepository.findPathInicialitzablesByServei(serveiCodi);
			solicitudTransmision.setDatosEspecificos(processarDadesEspecifiques(solicitud.getDadesEspecifiques(), serveiCodi, serveiConfig, servicio, pathInicialitzablesByServei));
		}

		// Afegir SolicitudTransmision a Solicitudes
		ArrayList<SolicitudTransmision> solicitudesTransmision = new ArrayList<>();
		solicitudesTransmision.add(solicitudTransmision);
		solicitudes.setSolicitudTransmision(solicitudesTransmision);
		peticion.setSolicitudes(solicitudes);

		return peticion;

	}

	public Peticion toPeticion(PeticioAsincrona peticio) throws ConsultaScspGeneracioException {
		if (peticio == null)
			return null;

		String timeStamp = DateUtils.parseISO8601(new Date());
		DadesComunes dadesComunes = peticio.getDadesComunes();
		List<SolicitudSimple> solicituds = peticio.getSolicituds();

		String serveiCodi = dadesComunes.getServeiCodi();
		ServeiConfig serveiConfig = serveiConfigRepository.findByServei(serveiCodi);
		Servicio servicio = getScspHelper().getServicio(serveiCodi);
		List<String> pathInicialitzablesByServei = serveiCampRepository.findPathInicialitzablesByServei(serveiCodi);

		String idPeticion = getScspHelper().generarIdPeticion(serveiCodi);
		int numSolicituds = solicituds.size();
		int index = 1;


		// Crear objecte Peticion
		Peticion peticion = new Peticion();
		peticion.setAtributos(crearAtributos(idPeticion, timeStamp, serveiCodi, numSolicituds));

		// Crear Solicitudes i SolicitudTransmision
		Solicitudes solicitudes = new Solicitudes();
		ArrayList<SolicitudTransmision> solicitudesTransmision = new ArrayList<>();

		for (SolicitudSimple solicitud : solicituds) {
			String idSolicitud = getIdSolicitud(idPeticion, numSolicituds, index++);

			SolicitudTransmision solicitudTransmision = new SolicitudTransmision();
//			solicitudTransmision.setId(solicitud.getId());
			solicitudTransmision.setDatosGenericos(crearDatosGenericos(dadesComunes, solicitud, serveiConfig, serveiCodi, idPeticion, idSolicitud, timeStamp));

			// Dades específiques (si n'hi ha)
			if (solicitud.getDadesEspecifiques() != null && !solicitud.getDadesEspecifiques().isEmpty()) {
				solicitudTransmision.setDatosEspecificos(processarDadesEspecifiques(solicitud.getDadesEspecifiques(), serveiCodi, serveiConfig, servicio, pathInicialitzablesByServei));
			}

			// Afegir SolicitudTransmision a Solicitudes
			solicitudesTransmision.add(solicitudTransmision);
		}
		solicitudes.setSolicitudTransmision(solicitudesTransmision);
		peticion.setSolicitudes(solicitudes);

		return peticion;
	}


	// Validació genèrica de camps: nul·litat i mida
	private void validateCamp(String valor, String campNom, int maxLongitud, BindException errors) {
		valor = valor != null ? valor.trim() : null;
		if (valor == null || valor.isEmpty()) {
			errors.rejectValue(campNom, "rec.val.err." + campNom, "No s'ha trobat l'element " + campNom);
		} else if (valor.length() > maxLongitud) {
			errors.rejectValue(campNom, "rec.val.err." + campNom + ".length", "Camp massa llarg. L'element " + campNom + " no pot superar els " + maxLongitud + " caràcters.");
		}
	}

	// Validació genèrica de camps: nul·litat
	private void validateCamp(String valor, String campNom, BindException errors) {
		valor = valor != null ? valor.trim() : null;
		if (valor == null || valor.isEmpty()) {
			errors.rejectValue(campNom, "rec.val.err." + campNom, "No s'ha trobat l'element " + campNom);
		}
	}

	// Validar la longitud d'un camp
	private void validateCampLength(String valor, String campNom, int maxLongitud, BindException errors) {
		if (valor != null && valor.length() > maxLongitud) {
			errors.rejectValue(campNom, "rec.val.err." + campNom + ".length", "Camp massa llarg. L'element " + campNom + " no pot superar els " + maxLongitud + " caràcters.");
		}
	}

	// Validació del funcionari (personalitzat segons regles complexes)
	private void validateFuncionari(Funcionari funcionari, BindException errors) {
		if (funcionari == null) {
			errors.rejectValue("dadesComunes.funcionari", "rec.val.err.funcionari", "No s'ha trobat l'element dadesComunes.funcionari");
			return;
		}

		// És obligatori informar o bé el codi o el nif.
		// No fa falta informar els dos, però si s'informen els dos es validarà que el codi i el nif corresponguin al mateix usuari.
		// Tampoc fa falta informar el nom del funcionari. Però si s'informa es validarà que el nom corresponguin al de l'usuari.
//		String funcionariCodi = funcionari.getCodi() != null ? funcionari.getCodi().trim() : null;
		String funcionariNif = funcionari.getNif() != null ? funcionari.getNif().trim() : null;
		String funcionariNom = funcionari.getNom() != null ? funcionari.getNom().trim() : null;

//		// Validació bàsica: codi o nif han d'estar informats
//		if ((funcionariCodi == null || funcionariCodi.isEmpty()) && (funcionariNif == null || funcionariNif.isEmpty())) {
//			errors.rejectValue("dadesComunes.funcionari", "rec.val.err.funcionari.missing", "És obligatori informar o bé el codi o el nif del funcionari.");
//			return;
//		}

//		DadesUsuari funcionariDades = consultarFuncionari(funcionariCodi, funcionariNif, errors);

		// Validem longitud per a codi i NIF
		validateCamp(funcionariNom, "dadesComunes.funcionari.nom", 122, errors);
		validateCamp(funcionariNif, "dadesComunes.funcionari.nif", 10, errors);

//		if (funcionariDades != null) {
//			if (funcionariNif != null && !funcionariNif.equalsIgnoreCase(funcionariDades.getNif())) {
//				errors.rejectValue("dadesComunes.funcionari.nif", "rec.val.err.funcionari.nif.mismatch", "El NIF del funcionari no coincideix amb el NIF del funcionari amb codi " + funcionariCodi);
//			}
//			if (funcionariNom != null && !funcionariNom.equalsIgnoreCase(funcionariDades.getNom())) {
//				errors.rejectValue("dadesComunes.funcionari.nom", "rec.val.err.funcionari.nom.mismatch", "El nom indicat no coincideix amb el nom del funcionari amb codi " + funcionariDades.getCodi());
//			}
//		}
//
//		validateCampLength(funcionariNom, "dadesComunes.funcionari.nom", 122, errors);
	}

	// Mètode que consulta el funcionari a partir del codi o nif indicat
	private DadesUsuari consultarFuncionari(String codi, String nif, BindException errors) {
		try {
			if (codi != null && !codi.isEmpty()) {
				DadesUsuari funcionari = pluginHelper.dadesUsuariConsultarAmbUsuariCodi(codi);
				if (funcionari == null) {
					errors.rejectValue("dadesComunes.funcionari.codi", "rec.val.err.funcionari.codi.notfound", "No s'ha trobat el funcionari amb el codi " + codi);
				}
				return funcionari;
			} else if (nif != null && !nif.isEmpty()) {
				DadesUsuari funcionari = pluginHelper.dadesUsuariConsultarAmbUsuariNif(nif);
				if (funcionari == null) {
					errors.rejectValue("dadesComunes.funcionari.nif", "rec.val.err.funcionari.nif.notfound", "No s'ha trobat el funcionari amb el nif " + nif);
				}
				return funcionari;
			}
		} catch (SistemaExternException e) {
			String identificador = codi != null ? codi : nif;
			log.error("No s'han pogut obtenir les dades del funcionari amb identificador: " + identificador, e);
		}
		return null;
	}

	private void validateTitular(es.caib.pinbal.client.recobriment.v2.Titular titular, String serveiCodi, BindException errors) {
		ServeiConfig servei = serveiConfigRepository.findByServei(serveiCodi);
		if (titular == null) {
			if (servei != null && servei.isDocumentObligatori())
				errors.rejectValue("solicitud.titular", "rec.val.err.titular", "No s'ha trobat l'element solicitud.titular");
			return;
		}

		es.caib.pinbal.client.recobriment.v2.Titular.DocumentTipus titularDocTipus = titular.getDocumentTipus();
		String titularDocNum = titular.getDocumentNumero() != null ? titular.getDocumentNumero().trim() : null;
		String titularNom = titular.getNom() != null ? titular.getNom().trim() : null;
		String titularLlinatge1 = titular.getLlinatge1() != null ? titular.getLlinatge1().trim() : null;
		String titularLlinatge2 = titular.getLlinatge2() != null ? titular.getLlinatge2().trim() : null;
		String titularNomComplet = titular.getNomComplet() != null ? titular.getNomComplet().trim() : null;

		if (servei != null && servei.isDocumentObligatori() || (titularDocNum != null && !titularDocNum.isEmpty())) {
			if (titularDocTipus == null) {
				errors.rejectValue("dadesComunes.titular.documentTipus", "rec.val.err.titular.documentTipus", "No s'ha trobat l'element dadesComunes.titular.documentTipus");
			}
		}
		if (servei != null && servei.isDocumentObligatori())
			validateCamp(titular.getDocumentNumero(), "solicitud.titular.documentNumero", errors);

		validateCampLength(titularDocNum, "solicitud.titular.documentNumero", 14, errors);

		if (titularDocTipus != null && titularDocNum != null && !titularDocNum.isEmpty() && servei != null && servei.isComprovarDocument()) {
			if (es.caib.pinbal.client.recobriment.v2.Titular.DocumentTipus.NIF.equals(titularDocTipus)) {
				if (!DocumentIdentitatHelper.validacioNif(titularDocNum))
					errors.rejectValue("solicitud.titular.documentNumero", "rec.val.err.titular.nif", "El valor de l'element dadesComunes.titular.documentTipus no és un NIF vàlid");
			} if (es.caib.pinbal.client.recobriment.v2.Titular.DocumentTipus.DNI.equals(titularDocTipus)) {
				if (!DocumentIdentitatHelper.validacioDni(titularDocNum))
					errors.rejectValue("solicitud.titular.documentNumero", "rec.val.err.titular.dni", "El valor de l'element dadesComunes.titular.documentTipus no és un DNI vàlid");
			} if (es.caib.pinbal.client.recobriment.v2.Titular.DocumentTipus.CIF.equals(titularDocTipus)) {
				if (!DocumentIdentitatHelper.validacioCif(titularDocNum))
					errors.rejectValue("solicitud.titular.documentNumero", "rec.val.err.titular.cif", "El valor de l'element dadesComunes.titular.documentTipus no és un CIF vàlid");
			} if (es.caib.pinbal.client.recobriment.v2.Titular.DocumentTipus.NIE.equals(titularDocTipus)) {
				if (!DocumentIdentitatHelper.validacioNie(titularDocNum))
					errors.rejectValue("solicitud.titular.documentNumero", "rec.val.err.titular.nie", "El valor de l'element dadesComunes.titular.documentTipus no és un NIE vàlid");
			} if (es.caib.pinbal.client.recobriment.v2.Titular.DocumentTipus.Pasaporte.equals(titularDocTipus)) {
				if (!DocumentIdentitatHelper.validacioPass(titularDocNum))
					errors.rejectValue("solicitud.titular.documentNumero", "rec.val.err.titular.pass", "El valor de l'element dadesComunes.titular.documentTipus no és un Passaport vàlid");
			}

		}

		validateCampLength(titularNom, "solicitud.titular.nom", 40, errors);
		validateCampLength(titularLlinatge1, "solicitud.titular.llinatge1", 40, errors);
		validateCampLength(titularLlinatge2, "solicitud.titular.llinatge2", 40, errors);
		validateCampLength(titularNomComplet, "solicitud.titular.nomComplet", 122, errors);

	}

	private void validateDadesEspecifiques(Map<String, String> dadesEspedifiques, String serveiCodi, BindException errors, ServeiService serveiService) {

		Map<String, Object> dades = new HashMap<>();

		DadesConsultaSimpleValidator validatorDadesEspecifiques = new DadesConsultaSimpleValidator(serveiService, serveiCodi);
		BindException errorsDadesEspecifiques = new BindException(new SolicitudSimple(), "solicitud");
		validatorDadesEspecifiques.validate(dadesEspedifiques, errorsDadesEspecifiques);

		if (!errorsDadesEspecifiques.hasErrors())
			return;

		for (FieldError fieldError : errorsDadesEspecifiques.getFieldErrors()) {
			String campNom = ("peticio".equals(errors.getObjectName()) ? "solicitud." : "") + fieldError.getField(); // Nom del camp
			String campErrorCodi = fieldError.getCode();
			String campErrorMsg = fieldError.getDefaultMessage(); // Missatge d'error
			errors.rejectValue(campNom, campErrorCodi, campErrorMsg);
		}
	}

	private static Atributos crearAtributos(String idPeticion, String timeStamp, String serveiCodi, int numSolicituds) {
		Atributos atributos = new Atributos();
		atributos.setIdPeticion(idPeticion);
		atributos.setNumElementos(String.valueOf(numSolicituds));
		atributos.setTimeStamp(timeStamp);
		atributos.setCodigoCertificado(serveiCodi);
		return atributos;
	}

	// Crear DatosGenericos
	private DatosGenericos crearDatosGenericos(DadesComunes dadesComunes, SolicitudSimple solicitud, ServeiConfig serveiConfig, String serveiCodi, String idPeticion, String idSolicitud, String timeStamp) {
		DatosGenericos datosGenericos = new DatosGenericos();

		// Configurar Emisor
		datosGenericos.setEmisor(getScspHelper().getEmisor(serveiCodi));

		// Configurar Solicitant
		datosGenericos.setSolicitante(crearSolicitante(dadesComunes, solicitud, serveiConfig));

		// Configurar Titular (si existeix)
		if (solicitud.getTitular() != null) {
			datosGenericos.setTitular(crearTitular(solicitud.getTitular()));
		}

		// Configurar Transmissió
		Transmision transmision = new Transmision();
		transmision.setCodigoCertificado(serveiCodi);
		transmision.setIdSolicitud(idSolicitud);
		transmision.setFechaGeneracion(timeStamp);
		datosGenericos.setTransmision(transmision);

		return datosGenericos;
	}

	// Crear Solicitante
	private Solicitante crearSolicitante(DadesComunes dadesComunes, SolicitudSimple solicitud, ServeiConfig serveiConfig) {
		Solicitante solicitante = new Solicitante();

		// Entitat
		Entitat entitat = entitatRepository.findByCif(dadesComunes.getEntitatCif());

		// Procediment
		es.caib.pinbal.core.model.Procediment procediment = procedimentRepository.findByEntitatAndCodi(entitat, dadesComunes.getProcedimentCodi());
		Procedimiento procedimiento = new Procedimiento();
		procedimiento.setCodProcedimiento(procediment.getCodi());
		procedimiento.setNombreProcedimiento(procediment.getNom());
		solicitante.setProcedimiento(procedimiento);

		// Funcionari
		solicitante.setFuncionario(crearFuncionario(dadesComunes.getFuncionari()));

		// Unitat tramitadora
		solicitante.setUnidadTramitadora(dadesComunes.getDepartament());
		solicitante.setCodigoUnidadTramitadora(determinarUnitatTramitadora(serveiConfig, procediment));

		// Solicitant
		solicitante.setIdentificadorSolicitante(entitat.getCif());
		solicitante.setNombreSolicitante(entitat.getNom());
		solicitante.setIdExpediente(solicitud.getExpedient());
		solicitante.setFinalidad(dadesComunes.getFinalitat());
		solicitante.setConsentimiento(Consentimiento.valueOf(dadesComunes.getConsentiment().name()));

		return solicitante;
	}

	private Funcionario crearFuncionario(Funcionari funcionari) {
		if (funcionari == null) {
			return null;
		}

		Funcionario funcionario = new Funcionario();
		funcionario.setNombreCompletoFuncionario(funcionari.getNom());
		funcionario.setNifFuncionario(funcionari.getNif());
		return funcionario;
	}

	private String determinarUnitatTramitadora(ServeiConfig serveiConfig, es.caib.pinbal.core.model.Procediment procediment) {
		return serveiConfig.isPinbalUnitatDir3FromEntitat()
				? procediment.getEntitat().getUnitatArrel()
				: serveiConfig.getPinbalUnitatDir3() != null && !serveiConfig.getPinbalUnitatDir3().isEmpty()
				? serveiConfig.getPinbalUnitatDir3()
				: procediment.getOrganGestor() != null
				? procediment.getOrganGestor().getCodi()
				: null;
	}

	private Titular crearTitular(es. caib. pinbal. client. recobriment. v2.Titular titularModel) {
		Titular titular = new Titular();
		if (titularModel.getDocumentTipus() != null) {
			titular.setTipoDocumentacion(TipoDocumentacion.valueOf(titularModel.getDocumentTipus().name()));
		}
		titular.setDocumentacion(titularModel.getDocumentNumero());
		titular.setNombreCompleto(titularModel.getNomComplet());
		titular.setNombre(titularModel.getNom());
		titular.setApellido1(titularModel.getLlinatge1());
		titular.setApellido2(titularModel.getLlinatge2());
		return titular;
	}

	private String getIdSolicitud(
			String idPeticion,
			int numSolicitudes,
			int index) {
		if (numSolicitudes == 1) {
			return idPeticion;
		} else {
			return String.format("%06d", index);
		}
	}

	// Processar dades específiques
	private Element processarDadesEspecifiques(Map<String, String> dadesEspecifiques,
											   String serveiCodi,
											   ServeiConfig serveiConfig,
											   Servicio servicio,
											   List<String> pathInicialitzablesByServei) throws ConsultaScspGeneracioException {
		Map<String, Object> dadesEspecifiquesConverted = new HashMap<>();
		for (Map.Entry<String, String> entry : dadesEspecifiques.entrySet()) {
			String valor = entry.getValue();
			ServeiCamp camp = serveiCampRepository.findByServeiAndPath(serveiCodi, entry.getKey());
			if (camp != null && ServeiCamp.ServeiCampTipus.DATA.equals(camp.getTipus()) && camp.getDataFormat() != null && !camp.getDataFormat().isEmpty()) {
				valor = getDateFormat(valor, camp.getDataFormat());
			}
			dadesEspecifiquesConverted.put(entry.getKey(), valor);
		}
		return getXmlHelper().crearDadesEspecifiques(
				servicio,
				dadesEspecifiquesConverted,
				serveiConfig.isActivaGestioXsd(),
				serveiConfig.isIniDadesEspecifiques(),
				pathInicialitzablesByServei,
				serveiConfig.isAddDadesEspecifiques()
		);
	}

//	public String dadesEspecifiquesToJson(Element dadesEspecifiques) {
//		if (dadesEspecifiques == null) {
//			return null;
//		}
//		try {
//			Map<String, String> dadesEspecifiquesMap = convertirXMLAMap(dadesEspecifiques, "DatosEspecificos");
//			ObjectMapper objectMapper = new ObjectMapper();
//			return objectMapper.writeValueAsString(dadesEspecifiquesMap);
//		} catch (Exception e) {
//			log.error("Error converting dadesEspecifiques to JSON", e);
//			return null;
//		}
//	}

	public static final String FORMAT_DEFAULT = "dd/MM/yyyy";
	public static final String FORMAT2 = "dd-MM-yyyy";
	public static final String FORMAT3 = "dd/MM/yy";
	public static final String FORMAT4 = "dd-MM-yy";
	public static final String FORMAT5 = "yyyy/MM/dd";
	public static final String FORMAT6 = "yyyy-MM-dd";

	private String getDateFormat(Object dateObj, String format) {

		if (dateObj == null) {
			return null;
		}

		SimpleDateFormat sdf = new SimpleDateFormat(format != null ? format: FORMAT_DEFAULT);
		Date dataDate = null;

		if (dateObj instanceof Date) {
			dataDate = (Date) dateObj;
		} else if (dateObj instanceof String) {
			// Validar format data
			String dataText = (String) dateObj;
			try {
				if (format != null && !format.isEmpty()) {
					dataDate = parseDate(dataText, FORMAT_DEFAULT, FORMAT2, FORMAT3, FORMAT4, FORMAT5, FORMAT6, format);
				} else {
					dataDate = parseDate(dataText, FORMAT_DEFAULT, FORMAT2, FORMAT3, FORMAT4, FORMAT5, FORMAT6);
				}
			} catch (Exception ex) {
				return dataText;
			}
		}

		return dataDate != null ? sdf.format(dataDate) : null;
	}

	private Date parseDate(String dateString, String... formatStrings) {
		for (String formatString : formatStrings) {
			try {
				return new SimpleDateFormat(formatString).parse(dateString);
			} catch (ParseException e) {
			}
		}
		return null;
	}

	private XmlHelper getXmlHelper() {
		if (xmlHelper == null) {
			xmlHelper = new XmlHelper();
		}
		return xmlHelper;
	}

	public PeticioRespostaSincrona toRespostaSincrona(Respuesta scspRespuesta) throws Exception {
		if (scspRespuesta == null)
			return PeticioRespostaSincrona.builder().error(true).missatge("No s'ha pobut recuperar la resposta.").estat(EstatEnum.ERROR).build();

		TransmisionDatos transmisionDatos = null;
		if (scspRespuesta.getTransmisiones() != null && scspRespuesta.getTransmisiones().getTransmisionDatos() != null && !scspRespuesta.getTransmisiones().getTransmisionDatos().isEmpty()) {
			transmisionDatos = scspRespuesta.getTransmisiones().getTransmisionDatos().get(0);
		}
		String msg = scspRespuesta.getAtributos() != null && scspRespuesta.getAtributos().getEstado() != null
				? scspRespuesta.getAtributos().getEstado().getLiteralError()
				: null;
		Consulta consulta = scspRespuesta.getAtributos() != null && scspRespuesta.getAtributos().getIdPeticion() != null
				? getConsultaSimpleBypeticioId(scspRespuesta.getAtributos().getIdPeticion())
				: null;
		EstatEnum estat = consulta != null && consulta.getEstat() != null
				? EstatEnum.valorAsEnum(consulta.getEstat().name())
				: EstatEnum.ERROR;
		boolean error = consulta != null && consulta.getEstat() != null
				? EstatEnum.ERROR.equals(estat)
				: scspRespuesta.getAtributos() == null
					|| scspRespuesta.getAtributos().getEstado() == null
					|| scspRespuesta.getAtributos().getEstado().getCodigoEstado() == null
					|| !scspRespuesta.getAtributos().getEstado().getCodigoEstado().startsWith("00");

		return PeticioRespostaSincrona.builder()
			.error(error)
			.missatge(msg)
			.estat(estat)
			.dadesComunes(toDadesComunesResposta(scspRespuesta))
			.resposta(toResposta(transmisionDatos))
			.build();
	}

	public PeticioConfirmacioAsincrona toConfirmacio(ConfirmacionPeticion resposta) throws Exception {
		if (resposta == null)
			return null;

		String msg = resposta.getAtributos() != null && resposta.getAtributos().getEstado() != null
				? resposta.getAtributos().getEstado().getLiteralError()
				: null;
		Consulta consulta = resposta.getAtributos() != null && resposta.getAtributos().getIdPeticion() != null
				? getConsultaBypeticioId(resposta.getAtributos().getIdPeticion())
				: null;
		EstatEnum estat = consulta != null && consulta.getEstat() != null
				? EstatEnum.valorAsEnum(consulta.getEstat().name())
				: EstatEnum.ERROR;
//		boolean error = consulta != null && consulta.getEstat() != null
//				? EstatEnum.ERROR.equals(estat)
//				: msg != null && !msg.trim().isEmpty();
		boolean error = consulta != null && consulta.getEstat() != null
				? EstatEnum.ERROR.equals(estat)
				: resposta.getAtributos() == null
					|| resposta.getAtributos().getEstado() == null
					|| resposta.getAtributos().getEstado().getCodigoEstado() == null
					|| !resposta.getAtributos().getEstado().getCodigoEstado().startsWith("00");

		return PeticioConfirmacioAsincrona.builder()
				.error(error)
				.missatge(msg)
				.estat(estat)
				.confirmacioPeticio(toConfirmacioPeticio(resposta))
				.build();
	}

	public PeticioRespostaAsincrona toRespostaAsincrona(Respuesta scspRespuesta) throws Exception {

		Consulta consulta = scspRespuesta.getAtributos() != null && scspRespuesta.getAtributos().getIdPeticion() != null
				? getConsultaBypeticioId(scspRespuesta.getAtributos().getIdPeticion())
				: null;
		EstatEnum estat = consulta != null && consulta.getEstat() != null
				? EstatEnum.valorAsEnum(consulta.getEstat().name())
				: EstatEnum.ERROR;
		boolean error = consulta != null && consulta.getEstat() != null
				? EstatEnum.ERROR.equals(estat)
				: scspRespuesta.getAtributos() == null
					|| scspRespuesta.getAtributos().getEstado() == null
					|| scspRespuesta.getAtributos().getEstado().getCodigoEstado() == null
					|| !scspRespuesta.getAtributos().getEstado().getCodigoEstado().startsWith("00");
		String msg = scspRespuesta.getAtributos() != null && scspRespuesta.getAtributos().getEstado() != null
				? scspRespuesta.getAtributos().getEstado().getLiteralError()
				: "La resposta SCSP no ha retornat l'estat de la petició.";
		Date dataEstimadaResposta = EstatEnum.PROCESSANT.equals(estat)
					&& scspRespuesta.getAtributos() != null
					&& scspRespuesta.getAtributos().getEstado() != null
					&& scspRespuesta.getAtributos().getEstado().getTiempoEstimadoRespuesta() != null
				? calcularDataEstimada(scspRespuesta.getAtributos().getEstado().getTiempoEstimadoRespuesta())
				: null;

		return PeticioRespostaAsincrona.builder()
				.error(error)
				.missatge(msg)
				.estat(estat)
				.dataEstimadaResposta(dataEstimadaResposta)
				.dadesComunes(toDadesComunesResposta(scspRespuesta))
				.respostes(toRespostes(scspRespuesta))
				.build();
	}

	public DadesComunesResposta toDadesComunesResposta(Respuesta respuesta) {
		if (respuesta == null) return null;

		DadesComunesResposta dadesComunes = new DadesComunesResposta();
		if (respuesta.getAtributos() != null) {
			dadesComunes.setServeiCodi(respuesta.getAtributos().getCodigoCertificado());
			dadesComunes.setIdPeticio(respuesta.getAtributos().getIdPeticion());
		}
		if (respuesta.getTransmisiones() != null && respuesta.getTransmisiones().getTransmisionDatos() != null && !respuesta.getTransmisiones().getTransmisionDatos().isEmpty()) {
			TransmisionDatos transmisionDatos = respuesta.getTransmisiones().getTransmisionDatos().get(0);
			if (transmisionDatos.getDatosGenericos() != null) {
				if (transmisionDatos.getDatosGenericos().getEmisor() != null) {
					Emisor emisor = Emisor.builder()
							.nif(transmisionDatos.getDatosGenericos().getEmisor().getNifEmisor())
							.nom(transmisionDatos.getDatosGenericos().getEmisor().getNombreEmisor())
							.build();
					dadesComunes.setEmisor(emisor);
				}
				Solicitante solicitante = transmisionDatos.getDatosGenericos().getSolicitante();
				if (solicitante != null) {
					Funcionari funcionari = null;
					if (solicitante.getFuncionario() != null) {
						funcionari = Funcionari.builder()
								.nom(solicitante.getFuncionario().getNombreCompletoFuncionario())
								.nif(solicitante.getFuncionario().getNifFuncionario())
								.build();
					}
					Solicitant solicitant = Solicitant.builder()
							.identificador(solicitante.getIdentificadorSolicitante())
							.nom(solicitante.getNombreSolicitante())
							.unitatTramitadora(solicitante.getUnidadTramitadora())
							.codiUnitatTramitadora(solicitante.getCodigoUnidadTramitadora())
							.procedimentCodi(solicitante.getProcedimiento() != null ? solicitante.getProcedimiento().getCodProcedimiento() : null)
							.procedimentNom(solicitante.getProcedimiento() != null ? solicitante.getProcedimiento().getNombreProcedimiento() : null)
							.automatitzat(solicitante.getProcedimiento() != null ? "S".equalsIgnoreCase(solicitante.getProcedimiento().getAutomatizado()) : null)
							.classeTramit(solicitante.getProcedimiento() != null ? ClasseTramitHelper.getClasseTramitById(solicitante.getProcedimiento().getClaseTramite()) : null)
							.finalitat(solicitante.getFinalidad())
							.consentiment(solicitante.getConsentimiento() != null ? DadesComunes.Consentiment.valueOf(solicitante.getConsentimiento().name()) : null)
							.funcionari(funcionari)
							.build();
					dadesComunes.setSolicitant(solicitant);
				}
			}
		}
		return dadesComunes;
	}

	// Obtenim la consulta per el seu id de petició.
	// Aquesta consulta pot ser simple o múltiple
	public Consulta getConsultaBypeticioId(String idPeticio) throws Exception {
		// Obtenim la consulta múltiple
		Consulta consulta = consultaRepository.findByScspPeticionIdAndMultipleIsTrue(idPeticio);
		if (consulta != null)
			return consulta;

		// Si no hem obtingut una consulta múltiple, llavors ha de ser una consulta simple
		List<Consulta> consultes = consultaRepository.findByScspPeticionId(idPeticio);

		if (consultes == null || consultes.isEmpty())
			return null;

		// Si hi ha més d'una consulta amb el matiex idPeticio, i cap és múltiple, és que hi ha un problema
		if (consultes.size() > 1)
			throw new RuntimeException("S'han trobat múltiples consultes simples amb el mateix id de petició");

		// És una consulta simple
		return consultes.get(0);
	}

	// Obtenim la consulta simple per el seu id de petició.
	public Consulta getConsultaSimpleBypeticioId(String idPeticio) throws Exception {
		// Obtenim totes les consultes amb idPeticio.
		// Si és una consulta simple, només ni hauria d'haver una
		List<Consulta> consultes = consultaRepository.findByScspPeticionId(idPeticio);

		if (consultes == null || consultes.isEmpty())
			return null;

		// Si hi ha més d'una consulta amb el matiex idPeticio, és qeu és múltiple o hi ha un problema
		if (consultes.size() > 1) {
			throw new RuntimeException("S'han trobat múltiples consultes amb el mateix id de petició");
		}

		return consultes.get(0);
	}

	private List<PeticioResposta> toRespostes(Respuesta scspRespuesta) {
		if (scspRespuesta.getTransmisiones() == null
				|| scspRespuesta.getTransmisiones().getTransmisionDatos() == null
				|| scspRespuesta.getTransmisiones().getTransmisionDatos().isEmpty())
			return null;

		List<PeticioResposta> peticioRespostes = new ArrayList<>();
		ArrayList<TransmisionDatos> transmisionesDatos = scspRespuesta.getTransmisiones().getTransmisionDatos();
		for (TransmisionDatos transmisionDatos : transmisionesDatos) {
			peticioRespostes.add(toResposta(transmisionDatos));
		}
		return peticioRespostes;
	}

	public PeticioResposta toResposta(TransmisionDatos transmisionDatos) {
		if (transmisionDatos == null) return null;

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		format.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));

		PeticioResposta peticioResposta = new PeticioResposta();
		if (transmisionDatos.getDatosGenericos() != null) {
			Solicitante solicitante = transmisionDatos.getDatosGenericos().getSolicitante();
			peticioResposta.setExpedient(solicitante != null ? solicitante.getIdExpediente() : null);

			Titular titular = transmisionDatos.getDatosGenericos().getTitular();
			if (titular != null) {
				peticioResposta.setTitular(
						es.caib.pinbal.client.recobriment.v2.Titular.builder()
								.documentTipus(titular.getTipoDocumentacion() != null ? es.caib.pinbal.client.recobriment.v2.Titular.DocumentTipus.valueOf(titular.getTipoDocumentacion().name()) : null)
								.documentNumero(titular.getDocumentacion())
								.nomComplet(titular.getNombreCompleto())
								.nom(titular.getNombre())
								.llinatge1(titular.getApellido1())
								.llinatge2(titular.getApellido2())
								.build()
				);
			}

			Transmision transmision = transmisionDatos.getDatosGenericos().getTransmision();
			if (transmision != null) {
				peticioResposta.setIdSolicitud(transmision.getIdSolicitud());
				peticioResposta.setIdTransmissio(transmision.getIdTransmision());
				if (transmision.getFechaGeneracion() != null) {
					try {
						Date dataResposta = format.parse(transmision.getFechaGeneracion());
						peticioResposta.setDataResposta(dataResposta);
					} catch (ParseException e) {
						log.error("Error al convertir la data de resposta", e);
					}
				}

				if (transmisionDatos.getDatosEspecificos() != null && transmisionDatos.getDatosEspecificos() instanceof Node) {
					Map<String, String> dadesEspecifiques = convertirXMLAMap((Node) transmisionDatos.getDatosEspecificos(), "DatosEspecificos");
					peticioResposta.setDadesEspecifiques(dadesEspecifiques);
				}
			}
		}
		return peticioResposta;
	}
	private ConfirmacioPeticio toConfirmacioPeticio(ConfirmacionPeticion resposta) {
		if (resposta == null || resposta.getAtributos() == null) {
			return null;
		}

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		format.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));

		int numPeticions = 0;
		Date dataResposta = null;
		Date dataEstimadaResposta = null;
		try {
			numPeticions = Integer.parseInt(resposta.getAtributos().getNumElementos());
		} catch (Exception e) {
			log.error("Error al convertir el número de peticions", e);
		}
		try {
			dataResposta = format.parse(resposta.getAtributos().getTimeStamp());
		} catch (Exception e) {
			log.error("Error al convertir la data de resposta", e);
		}
		if (resposta.getAtributos().getEstado() != null && resposta.getAtributos().getEstado().getTiempoEstimadoRespuesta() != null) {
			dataEstimadaResposta = calcularDataEstimada(resposta.getAtributos().getEstado().getTiempoEstimadoRespuesta());
		}

		return ConfirmacioPeticio.builder()
				.serveiCodi(resposta.getAtributos().getCodigoCertificado())
				.idPeticio(resposta.getAtributos().getIdPeticion())
				.numSolicituds(numPeticions)
				.dataEnviamentPeticio(dataResposta)
				.dataEstimadaResposta(dataEstimadaResposta)
				.build();
	}

	private Date calcularDataEstimada(Integer horesEstimades) {
		if (horesEstimades == null) {
			return null;
		}
		long milisegonsAfegits = horesEstimades * MILISEGONS_PER_HORA;
		return new Date(System.currentTimeMillis() + milisegonsAfegits);
	}


	private Map<String, String> convertirXMLAMap(Node node, String currentPath) {
		Map<String, String> map = new LinkedHashMap<>();

		if (node.getNodeType() == Node.ELEMENT_NODE && node.hasChildNodes() && node.getFirstChild().getNodeType()==Node.TEXT_NODE)
			map.put(currentPath, node.getTextContent().trim());

		if (node.hasAttributes()) {
			NamedNodeMap attributes = node.getAttributes();
			for (int i = 0; i < attributes.getLength(); i++) {
				Node attribute = attributes.item(i);
				map.put(currentPath + "/@" + attribute.getLocalName(), attribute.getNodeValue());
			}
		}

		NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node child = childNodes.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				String nodeName = child.getLocalName(); // Utilitza getLocalName() per obtenir el nom sense namespace
				map.putAll(convertirXMLAMap(child, currentPath + "/" + nodeName));
			}
		}
		return map;
	}
}
