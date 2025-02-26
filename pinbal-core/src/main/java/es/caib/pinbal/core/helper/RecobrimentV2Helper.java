/**
 * 
 */
package es.caib.pinbal.core.helper;

import es.caib.pinbal.client.recobriment.v2.DadesComunes;
import es.caib.pinbal.client.recobriment.v2.Funcionari;
import es.caib.pinbal.client.recobriment.v2.PeticioAsincrona;
import es.caib.pinbal.client.recobriment.v2.PeticioSincrona;
import es.caib.pinbal.client.recobriment.v2.SolicitudSimple;
import es.caib.pinbal.core.model.Entitat;
import es.caib.pinbal.core.model.ServeiConfig;
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
import es.scsp.bean.common.Consentimiento;
import es.scsp.bean.common.DatosGenericos;
import es.scsp.bean.common.Funcionario;
import es.scsp.bean.common.Peticion;
import es.scsp.bean.common.Procedimiento;
import es.scsp.bean.common.Solicitante;
import es.scsp.bean.common.SolicitudTransmision;
import es.scsp.bean.common.Solicitudes;
import es.scsp.bean.common.TipoDocumentacion;
import es.scsp.bean.common.Titular;
import es.scsp.bean.common.Transmision;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	private XmlHelper xmlHelper;

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
		es.caib.pinbal.core.model.Procediment procediment = procedimentRepository.findByCodi(dadesComunes.getProcedimentCodi());
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

		String solicitudId = solicitud.getId() != null ? solicitud.getId().trim() : null;
		es.caib.pinbal.client.recobriment.v2.Titular titular = solicitud.getTitular();
		String expedientId = solicitud.getExpedient() != null ? solicitud.getExpedient().trim() : null;
		Map<String, String> dadesEspedifiques = solicitud.getDadesEspecifiques();

		validateCampLength(solicitudId, "solicitud.id", 64, errors);

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
		solicitudTransmision.setId(solicitud.getId());
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
			solicitudTransmision.setId(solicitud.getId());
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
		String funcionariCodi = funcionari.getCodi() != null ? funcionari.getCodi().trim() : null;
		String funcionariNif = funcionari.getNif() != null ? funcionari.getNif().trim() : null;
		String funcionariNom = funcionari.getNom() != null ? funcionari.getNom().trim() : null;

		// Validació bàsica: codi o nif han d'estar informats
		if ((funcionariCodi == null || funcionariCodi.isEmpty()) && (funcionariNif == null || funcionariNif.isEmpty())) {
			errors.rejectValue("dadesComunes.funcionari", "rec.val.err.funcionari.missing", "És obligatori informar o bé el codi o el nif del funcionari.");
			return;
		}

		DadesUsuari funcionariDades = consultarFuncionari(funcionariCodi, funcionariNif, errors);

		// Validem longitud per a codi i NIF
		validateCampLength(funcionariCodi, "dadesComunes.funcionari.codi", 16, errors);
		validateCampLength(funcionariNif, "dadesComunes.funcionari.nif", 10, errors);

		if (funcionariDades != null) {
			if (funcionariNif != null && !funcionariNif.equalsIgnoreCase(funcionariDades.getNif())) {
				errors.rejectValue("dadesComunes.funcionari.nif", "rec.val.err.funcionari.nif.mismatch", "El NIF del funcionari no coincideix amb el NIF del funcionari amb codi " + funcionariCodi);
			}
			if (funcionariNom != null && !funcionariNom.equalsIgnoreCase(funcionariDades.getNom())) {
				errors.rejectValue("dadesComunes.funcionari.nom", "rec.val.err.funcionari.nom.mismatch", "El nom indicat no coincideix amb el nom del funcionari amb codi " + funcionariDades.getCodi());
			}
		}

		validateCampLength(funcionariNom, "dadesComunes.funcionari.nom", 122, errors);
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

		// Procediment
		es.caib.pinbal.core.model.Procediment procediment = procedimentRepository.findByCodi(dadesComunes.getProcedimentCodi());
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
		Entitat entitat = entitatRepository.findByCif(dadesComunes.getEntitatCif());
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

		String funcionariCodi = funcionari.getCodi() != null ? funcionari.getCodi().trim() : null;
		String funcionariNif = funcionari.getNif() != null ? funcionari.getNif().trim() : null;

		DadesUsuari funcionariDades = null;
		try {
			funcionariDades = funcionariCodi != null && !funcionariCodi.isEmpty()
					? pluginHelper.dadesUsuariConsultarAmbUsuariCodi(funcionariCodi)
					: pluginHelper.dadesUsuariConsultarAmbUsuariNif(funcionariNif);
		} catch (SistemaExternException e) {
			// Gestionar errors, si escau
		}

		Funcionario funcionario = new Funcionario();
		funcionario.setNombreCompletoFuncionario(funcionariDades != null ? funcionariDades.getNom() : funcionari.getNom());
		funcionario.setNifFuncionario(funcionariDades != null ? funcionariDades.getNif() : funcionariNif);
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
			dadesEspecifiquesConverted.put(entry.getKey(), entry.getValue());
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

	private XmlHelper getXmlHelper() {
		if (xmlHelper == null) {
			xmlHelper = new XmlHelper();
		}
		return xmlHelper;
	}

}
