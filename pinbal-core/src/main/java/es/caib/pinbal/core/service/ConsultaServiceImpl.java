/**
 * 
 */
package es.caib.pinbal.core.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Element;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.codec.Base64;

import es.caib.pinbal.core.dto.ArxiuDto;
import es.caib.pinbal.core.dto.ConsultaDto;
import es.caib.pinbal.core.dto.ConsultaDto.Consentiment;
import es.caib.pinbal.core.dto.ConsultaDto.DocumentTipus;
import es.caib.pinbal.core.dto.ConsultaFiltreDto;
import es.caib.pinbal.core.dto.EmisorDto;
import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.EstadisticaDto;
import es.caib.pinbal.core.dto.EstadistiquesFiltreDto;
import es.caib.pinbal.core.dto.EstadistiquesFiltreDto.EstadistiquesAgrupacioDto;
import es.caib.pinbal.core.dto.InformeGeneralEstatDto;
import es.caib.pinbal.core.dto.PaginaLlistatDto;
import es.caib.pinbal.core.dto.PaginacioAmbOrdreDto;
import es.caib.pinbal.core.dto.ProcedimentDto;
import es.caib.pinbal.core.dto.RecobrimentSolicitudDto;
import es.caib.pinbal.core.helper.ConversioTipusDocumentHelper;
import es.caib.pinbal.core.helper.DtoMappingHelper;
import es.caib.pinbal.core.helper.JustificantPlantillaHelper;
import es.caib.pinbal.core.helper.PaginacioHelper;
import es.caib.pinbal.core.helper.PermisosHelper;
import es.caib.pinbal.core.helper.PluginHelper;
import es.caib.pinbal.core.helper.PropertiesHelper;
import es.caib.pinbal.core.helper.ServeiHelper;
import es.caib.pinbal.core.helper.TransaccioHelper;
import es.caib.pinbal.core.helper.UsuariHelper;
import es.caib.pinbal.core.model.Consulta;
import es.caib.pinbal.core.model.Consulta.EstatTipus;
import es.caib.pinbal.core.model.Consulta.JustificantEstat;
import es.caib.pinbal.core.model.Entitat;
import es.caib.pinbal.core.model.EntitatUsuari;
import es.caib.pinbal.core.model.Procediment;
import es.caib.pinbal.core.model.ProcedimentServei;
import es.caib.pinbal.core.model.ServeiCamp;
import es.caib.pinbal.core.model.ServeiCamp.ServeiCampTipus;
import es.caib.pinbal.core.model.ServeiConfig;
import es.caib.pinbal.core.model.ServeiConfig.JustificantTipus;
import es.caib.pinbal.core.repository.ConsultaRepository;
import es.caib.pinbal.core.repository.EntitatRepository;
import es.caib.pinbal.core.repository.EntitatUsuariRepository;
import es.caib.pinbal.core.repository.ProcedimentRepository;
import es.caib.pinbal.core.repository.ProcedimentServeiRepository;
import es.caib.pinbal.core.repository.ServeiCampRepository;
import es.caib.pinbal.core.repository.ServeiConfigRepository;
import es.caib.pinbal.core.repository.ServeiJustificantCampRepository;
import es.caib.pinbal.core.repository.UsuariRepository;
import es.caib.pinbal.core.service.exception.AccesExternException;
import es.caib.pinbal.core.service.exception.ConsultaNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.JustificantGeneracioException;
import es.caib.pinbal.core.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentServeiNotFoundException;
import es.caib.pinbal.core.service.exception.ScspException;
import es.caib.pinbal.core.service.exception.ServeiNotAllowedException;
import es.caib.pinbal.core.service.exception.ValidacioDadesPeticioException;
import es.caib.pinbal.scsp.Resposta;
import es.caib.pinbal.scsp.ResultatEnviamentPeticio;
import es.caib.pinbal.scsp.ScspHelper;
import es.caib.pinbal.scsp.Solicitud;
import es.scsp.bean.common.Respuesta;
import es.scsp.common.domain.EmisorCertificado;
import es.scsp.common.domain.Servicio;

/**
 * Implementació dels mètodes per a gestionar les consultes al SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ConsultaServiceImpl implements ConsultaService {

	@Resource
	private ConsultaRepository consultaRepository;
	@Resource
	private ProcedimentRepository procedimentRepository;
	@Resource
	private ProcedimentServeiRepository procedimentServeiRepository;
	@Resource
	private EntitatRepository entitatRepository;
	@Resource
	private UsuariRepository usuariRepository;
	@Resource
	private ServeiConfigRepository serveiConfigRepository;
	@Resource
	private ServeiCampRepository serveiCampRepository;
	@Resource
	private EntitatUsuariRepository entitatUsuariRepository;
	@Resource
	private ServeiJustificantCampRepository serveiJustificantCampRepository;

	@Resource
	private JustificantPlantillaHelper justificantPlantillaHelper;
	@Resource
	private ConversioTipusDocumentHelper conversioTipusDocumentHelper;
	@Resource
	private DtoMappingHelper dtoMappingHelper;
	@Resource
	private TransaccioHelper transaccioHelper;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private UsuariHelper usuariHelper;
	@Resource
	private ServeiHelper serveiHelper;
	@Resource
	private ScspHelper scspHelper;

	@Resource
	private MutableAclService aclService;



	@Transactional(rollbackFor = {ProcedimentServeiNotFoundException.class, ServeiNotAllowedException.class, ScspException.class})
	@Override
	public ConsultaDto novaConsulta(
			ConsultaDto consulta) throws ProcedimentServeiNotFoundException, ServeiNotAllowedException, ScspException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		LOGGER.debug("Executant consulta del servei (codi=" + consulta.getServeiCodi() + "): " + consulta);
		copiarPropertiesToDb();
		ProcedimentServei procedimentServei = procedimentServeiRepository.findByProcedimentIdAndServei(
				consulta.getProcedimentId(),
				consulta.getServeiCodi());
		if (procedimentServei == null || !procedimentServei.isActiu()) {
			LOGGER.debug("No s'ha trobat el servei (codi=" + consulta.getServeiCodi() + ") del procediment (id=" + consulta.getProcedimentId() + ")");
			throw new ProcedimentServeiNotFoundException();
		}
		if (!isServeiPermesPerUsuari(
				procedimentServei.getProcediment().getEntitat(),
				procedimentServei.getProcediment(),
				consulta.getServeiCodi())) {
			LOGGER.debug("L'usuari no te accés al servei (codi=" + auth.getName() + ")");
			throw new ServeiNotAllowedException();
		}
		String idPeticion = null;
		try {
			idPeticion = scspHelper.generarIdPeticion(consulta.getServeiCodi());
		} catch (Exception ex) {
			// Error al generar l'identificador
			LOGGER.error("Error al generar identificador per a consulta (servei=" + consulta.getServeiCodi() + ", usuari=" + auth.getName() + ")", ex);
			throw new ScspException(ex.getMessage(), ex);
		}
		try {
			String titularDocumentTipus = null;
			if (consulta.getTitularDocumentTipus() != null)
				titularDocumentTipus = consulta.getTitularDocumentTipus().toString();
			Consulta c = Consulta.getBuilder(
					idPeticion,
					consulta.getFuncionariNom(),
					consulta.getFuncionariNif(),
					titularDocumentTipus,
					consulta.getTitularDocumentNum(),
					consulta.getTitularNom(),
					consulta.getTitularLlinatge1(),
					consulta.getTitularLlinatge2(),
					consulta.getTitularNomComplet(),
					consulta.getDepartamentNom(),
					procedimentServei,
					false,
					false,
					null).build();
			c.updateEstat(EstatTipus.Processant);
			processarDadesEspecifiquesSegonsCamps(
					consulta.getServeiCodi(),
					consulta.getDadesEspecifiques());
			List<Solicitud> solicituds = new ArrayList<Solicitud>();
			solicituds.add(convertirEnSolicitud(consulta));
			ResultatEnviamentPeticio resultat = scspHelper.enviarPeticionSincrona(
					idPeticion,
					solicituds);
			updateEstatConsulta(c, resultat, true);
			c.updateScspSolicitudId(resultat.getIdsSolicituds()[0]);
			if (EstatTipus.Tramitada.equals(c.getEstat())) {
				generarCustodiarJustificant(c);
			}
			Consulta saved = consultaRepository.save(c);
			return dtoMappingHelper.getMapperFacade().map(
					saved,
					ConsultaDto.class);
		} catch (Exception ex) {
			// Error al realitzar la petició (no s'arriba a enviar)
			LOGGER.error("Excepció al realitzar consulta SCSP (id=" + idPeticion + ", servei=" + consulta.getServeiCodi() + ", usuari=" + auth.getName() + ")", ex);
			throw new ScspException(ex.getMessage(), ex);
		}
		// Generació del justificant
	}

	@Transactional(rollbackFor = {ProcedimentServeiNotFoundException.class, ServeiNotAllowedException.class, ScspException.class})
	@Override
	public ConsultaDto novaConsultaInit(
			ConsultaDto consulta) throws ProcedimentServeiNotFoundException, ServeiNotAllowedException, ScspException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		LOGGER.debug("Executant consulta del servei (init) (codi=" + consulta.getServeiCodi() + "): " + consulta);
		copiarPropertiesToDb();
		ProcedimentServei procedimentServei = procedimentServeiRepository.findByProcedimentIdAndServei(
				consulta.getProcedimentId(),
				consulta.getServeiCodi());
		if (procedimentServei == null || !procedimentServei.isActiu()) {
			LOGGER.debug("No s'ha trobat el servei (codi=" + consulta.getServeiCodi() + ") del procediment (id=" + consulta.getProcedimentId() + ")");
			throw new ProcedimentServeiNotFoundException();
		}
		if (!isServeiPermesPerUsuari(
				procedimentServei.getProcediment().getEntitat(),
				procedimentServei.getProcediment(),
				consulta.getServeiCodi())) {
			LOGGER.debug("L'usuari no te accés al servei (codi=" + auth.getName() + ")");
			throw new ServeiNotAllowedException();
		}
		String idPeticion = null;
		try {
			idPeticion = scspHelper.generarIdPeticion(consulta.getServeiCodi());
		} catch (Exception ex) {
			// Error al generar l'identificador
			LOGGER.error("Error al generar identificador per a consulta (servei=" + consulta.getServeiCodi() + ", usuari=" + auth.getName() + ")", ex);
			throw new ScspException(ex.getMessage(), ex);
		}
		String titularDocumentTipus = null;
		if (consulta.getTitularDocumentTipus() != null)
			titularDocumentTipus = consulta.getTitularDocumentTipus().toString();
		Consulta c = Consulta.getBuilder(
				idPeticion,
				consulta.getFuncionariNom(),
				consulta.getFuncionariNif(),
				titularDocumentTipus,
				consulta.getTitularDocumentNum(),
				consulta.getTitularNom(),
				consulta.getTitularLlinatge1(),
				consulta.getTitularLlinatge2(),
				consulta.getTitularNomComplet(),
				consulta.getDepartamentNom(),
				procedimentServei,
				false,
				false,
				null).build();
		c.updateEstat(EstatTipus.Processant);
		Consulta saved = consultaRepository.save(c);
		return dtoMappingHelper.getMapperFacade().map(
				saved,
				ConsultaDto.class);
	}

	@Transactional
	@Override
	public void novaConsultaEnviament(
			Long consultaId,
			ConsultaDto consulta) throws ConsultaNotFoundException, ScspException {
		LOGGER.debug("Executant consulta del servei (enviament) (consultaId=" + consultaId + ")");
		try {
			processarDadesEspecifiquesSegonsCamps(
					consulta.getServeiCodi(),
					consulta.getDadesEspecifiques());
		} catch (Exception ex) {
			LOGGER.error("Error al processar dades específiques de la consulta (consultaId=" + consultaId + ")", ex);
			throw new ScspException(ex.getMessage(), ex);
		}
		Consulta c = consultaRepository.findOne(consultaId);
		if (c == null) {
			LOGGER.debug("No s'ha trobat la consulta (id=" + consultaId + ")");
			throw new ConsultaNotFoundException();
		}
		List<Solicitud> solicituds = new ArrayList<Solicitud>();
		solicituds.add(convertirEnSolicitud(consulta));
		ResultatEnviamentPeticio resultat = scspHelper.enviarPeticionSincrona(
				c.getScspPeticionId(),
				solicituds);
		if (resultat.isError()) {
			String error = "[" + resultat.getErrorCodi() + "] " + resultat.getErrorDescripcio();
			transaccioHelper.updateErrorConsulta(consultaId, error);
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			LOGGER.error("Error retornat per la consulta SCSP (id=" + c.getScspPeticionId() + ", servei=" + consulta.getServeiCodi() + ", usuari=" + auth.getName() + "): " + error);
			throw new ScspException(error);
		}
	}

	@Transactional(rollbackFor = {ScspException.class})
	@Override
	public ConsultaDto novaConsultaEstat(
			Long consultaId) throws ScspException {
		LOGGER.debug("Executant consulta del servei (estat) (consultaId=" + consultaId + ")");
		Consulta consulta = consultaRepository.findOne(consultaId);
		try {
			ResultatEnviamentPeticio resultat = scspHelper.recuperarResultatEnviamentPeticio(
					consulta.getScspPeticionId());
			updateEstatConsulta(consulta, resultat, true);
			consulta.updateScspSolicitudId(resultat.getIdsSolicituds()[0]);
			if (EstatTipus.Tramitada.equals(consulta.getEstat())) {
				generarCustodiarJustificant(consulta);
			}
			return dtoMappingHelper.getMapperFacade().map(
					consulta,
					ConsultaDto.class);
		} catch (Exception ex) {
			LOGGER.error("Error al obtenir l'estat de la petició corresponent a la consulta (consultaId=" + consultaId + ")", ex);
			throw new ScspException(ex.getMessage(), ex);
		}
	}

	@Transactional(rollbackFor = {ProcedimentServeiNotFoundException.class, ServeiNotAllowedException.class, ScspException.class})
	@Override
	public ConsultaDto novaConsultaMultiple(
			ConsultaDto consulta) throws ValidacioDadesPeticioException, ProcedimentServeiNotFoundException, ServeiNotAllowedException, ScspException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		LOGGER.debug("Executant consulta múltiple del servei (codi=" + consulta.getServeiCodi() + "): " + consulta);
		copiarPropertiesToDb();
		ProcedimentServei procedimentServei = procedimentServeiRepository.findByProcedimentIdAndServei(
				consulta.getProcedimentId(),
				consulta.getServeiCodi());
		if (procedimentServei == null || !procedimentServei.isActiu()) {
			LOGGER.debug("No s'ha trobat el servei (codi=" + consulta.getServeiCodi() + ") del procediment (id=" + consulta.getProcedimentId() + ")");
			throw new ProcedimentServeiNotFoundException();
		}
		if (!isServeiPermesPerUsuari(
				procedimentServei.getProcediment().getEntitat(),
				procedimentServei.getProcediment(),
				consulta.getServeiCodi())) {
			LOGGER.debug("L'usuari no te accés al servei (codi=" + auth.getName() + ")");
			throw new ServeiNotAllowedException();
		}
		String idPeticion = null;
		try {
			idPeticion = scspHelper.generarIdPeticion(consulta.getServeiCodi());
		} catch (Exception ex) {
			// Error al generar l'identificador
			LOGGER.error("Error al generar identificador per a consulta (servei=" + consulta.getServeiCodi() + ", usuari=" + auth.getName() + ")", ex);
			throw new ScspException(ex.getMessage(), ex);
		}
		try {
			Consulta c = Consulta.getBuilder(
					idPeticion,
					consulta.getFuncionariNom(),
					consulta.getFuncionariNif(),
					null,
					null,
					null,
					null,
					null,
					null,
					consulta.getDepartamentNom(),
					procedimentServei,
					false,
					true,
					null).build();
			c.updateEstat(EstatTipus.Pendent);
			List<Solicitud> solicituds = convertirEnMultiplesSolicituds(consulta);
			ResultatEnviamentPeticio resultat = scspHelper.enviarPeticionAsincrona(
					idPeticion,
					solicituds);
			updateEstatConsulta(c, resultat, true);
			Consulta saved = consultaRepository.save(c);
			int solicitudIndex = 0;
			for (Solicitud solicitud: solicituds) {
				String titularDocumentTipus = null;
				if (solicitud.getTitularDocumentTipus() != null)
					titularDocumentTipus = solicitud.getTitularDocumentTipus().toString();
				Consulta cs = Consulta.getBuilder(
						idPeticion,
						consulta.getFuncionariNom(),
						consulta.getFuncionariNif(),
						titularDocumentTipus,
						solicitud.getTitularDocument(),
						solicitud.getTitularNom(),
						solicitud.getTitularLlinatge1(),
						solicitud.getTitularLlinatge2(),
						solicitud.getTitularNomComplet(),
						consulta.getDepartamentNom(),
						procedimentServei,
						false,
						false,
						c).build();
				cs.updateScspSolicitudId(resultat.getIdsSolicituds()[solicitudIndex++]);
				updateEstatConsulta(cs, resultat, true);
				consultaRepository.save(cs);
			}
			return dtoMappingHelper.getMapperFacade().map(
					saved,
					ConsultaDto.class);
		} catch (Exception ex) {
			// Error al realitzar la petició (no s'arriba a enviar)
			LOGGER.error("Excepció al realitzar consulta SCSP (id=" + idPeticion + ", servei=" + consulta.getServeiCodi() + ", usuari=" + auth.getName() + ")", ex);
			throw new ScspException(ex.getMessage(), ex);
		}
	}

	@Transactional(rollbackFor = {EntitatNotFoundException.class, ProcedimentNotFoundException.class, ProcedimentNotFoundException.class, AccesExternException.class, ServeiNotAllowedException.class, ScspException.class})
	@Override
	public ConsultaDto novaConsultaRecobriment(
			String serveiCodi,
			RecobrimentSolicitudDto solicitud) throws EntitatNotFoundException, ProcedimentNotFoundException, ProcedimentServeiNotFoundException, ServeiNotAllowedException, ScspException {
		LOGGER.debug("Executant consulta del servei via recobriment (" +
				"entitatCif=" + solicitud.getEntitatCif() + ", " +
				"procedimentCodi=" + solicitud.getProcedimentCodi() + ", " +
				"serveiCodi=" + serveiCodi + ")");
		copiarPropertiesToDb();
		String entitatCif = solicitud.getEntitatCif();
		String procedimentCodi = solicitud.getProcedimentCodi();
		Entitat entitat = entitatRepository.findByCif(entitatCif);
		if (entitat == null) {
			LOGGER.debug("No s'ha trobat l'entitat (entitatCif=" + entitatCif + ")");
			throw new EntitatNotFoundException();
		}
		Procediment procediment = procedimentRepository.findByEntitatAndCodi(entitat, procedimentCodi);
		if (procediment == null) {
			LOGGER.debug("No s'ha trobat el procediment (entitatCif=" + entitatCif + ", procedimentCodi=" + procedimentCodi + ")");
			throw new ProcedimentNotFoundException();
		}
		ProcedimentServei procedimentServei = procedimentServeiRepository.findByProcedimentIdAndServei(
				procediment.getId(),
				serveiCodi);
		if (procedimentServei == null || !procedimentServei.isActiu()) {
			LOGGER.debug("No s'ha trobat el servei (serveiCodi=" + serveiCodi + ") del procediment (id=" + procediment.getId() + ")");
			throw new ProcedimentServeiNotFoundException();
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		usuariHelper.init(auth.getName());
		if (!isServeiPermesPerUsuari(
				entitat,
				procediment,
				serveiCodi)) {
			LOGGER.debug("L'usuari no te accés al servei (usuari=" + auth.getName() + ")");
			throw new ServeiNotAllowedException();
		}
		String idPeticion = null;
		try {
			idPeticion = scspHelper.generarIdPeticion(serveiCodi);
		} catch (Exception ex) {
			// Error al generar l'identificador
			LOGGER.error("Error al generar identificador per a consulta (servei=" + serveiCodi + ")", ex);
			throw new ScspException(ex.getMessage(), ex);
		}
		try {
			Consulta c = Consulta.getBuilder(
					idPeticion,
					solicitud.getFuncionariNom(),
					solicitud.getFuncionariNif(),
					(solicitud.getTitularDocumentTipus() != null) ? solicitud.getTitularDocumentTipus().toString() : null,
					solicitud.getTitularDocumentNum(),
					solicitud.getTitularNom(),
					solicitud.getTitularLlinatge1(),
					solicitud.getTitularLlinatge2(),
					solicitud.getTitularNomComplet(),
					solicitud.getDepartamentNom(),
					procedimentServei,
					true,
					false,
					null).build();
			c.updateEstat(EstatTipus.Pendent);
			Solicitud solicitudEnviar = convertirEnSolicitud(
					entitat,
					procediment,
					serveiCodi,
					solicitud.getFuncionariNom(),
					solicitud.getFuncionariNif(),
					solicitud.getTitularDocumentTipus(),
					solicitud.getTitularDocumentNum(),
					solicitud.getTitularNom(),
					solicitud.getTitularLlinatge1(),
					solicitud.getTitularLlinatge2(),
					solicitud.getTitularNomComplet(),
					solicitud.getFinalitat(),
					solicitud.getConsentiment(),
					solicitud.getDepartamentNom(),
					solicitud.getExpedientId(),
					scspHelper.copiarDadesEspecifiquesRecobriment(
							serveiCodi,
							solicitud.getDadesEspecifiques()));
			List<Solicitud> solicituds = new ArrayList<Solicitud>();
			solicituds.add(solicitudEnviar);
			ResultatEnviamentPeticio resultat = scspHelper.enviarPeticionSincrona(
					idPeticion,
					solicituds);
			c.updateEstat(EstatTipus.Processant);
			c.updateScspSolicitudId(resultat.getIdsSolicituds()[0]);
			if (resultat.isError()) {
				c.updateEstat(EstatTipus.Error);
				c.updateEstatError("[" + resultat.getErrorCodi() + "] " + resultat.getErrorDescripcio());
				LOGGER.error("Error retornat per la consulta SCSP (id=" + idPeticion + ", servei=" + serveiCodi + ", usuari=" + auth.getName() + "): " + c.getError());
			} else {
				c.updateEstat(EstatTipus.Tramitada);
			}
			Consulta saved = consultaRepository.save(c);
			ConsultaDto resposta = dtoMappingHelper.getMapperFacade().map(
					saved,
					ConsultaDto.class);
			if (resultat.isError()) {
				// Si la petició ha produit un error ompl els camps per permetre
				// al recobriment generar el missatge amb informació de l'error.
				resposta.setRespostaEstadoCodigo(resultat.getErrorCodi());
				resposta.setRespostaEstadoError(resultat.getErrorDescripcio());
			} else {
				// Si no hi ha error genera el justificant
				generarCustodiarJustificant(saved);
			}
			return resposta;
		} catch (Exception ex) {
			// Error al realitzar la petició (no s'arriba a enviar)
			LOGGER.error("Excepció al realitzar consulta SCSP (id=" + idPeticion + ", servei=" + serveiCodi + ", usuari=" + auth.getName() + ")", ex);
			throw new ScspException(ex.getMessage(), ex);
		}
	}

	@Transactional(rollbackFor = {EntitatNotFoundException.class, ProcedimentNotFoundException.class, ProcedimentNotFoundException.class, AccesExternException.class, ServeiNotAllowedException.class, ScspException.class})
	@Override
	public ConsultaDto novaConsultaRecobrimentInit(
			String serveiCodi,
			RecobrimentSolicitudDto solicitud) throws EntitatNotFoundException, ProcedimentNotFoundException, ProcedimentServeiNotFoundException, ServeiNotAllowedException, ScspException {
		LOGGER.debug("Executant consulta del servei via recobriment (init) (" +
				"entitatCif=" + solicitud.getEntitatCif() + ", " +
				"procedimentCodi=" + solicitud.getProcedimentCodi() + ", " +
				"serveiCodi=" + serveiCodi + ")");
		copiarPropertiesToDb();
		Entitat entitat = entitatRepository.findByCif(solicitud.getEntitatCif());
		if (entitat == null) {
			LOGGER.debug("No s'ha trobat l'entitat (entitatCif=" + solicitud.getEntitatCif() + ")");
			throw new EntitatNotFoundException();
		}
		Procediment procediment = procedimentRepository.findByEntitatAndCodi(entitat, solicitud.getProcedimentCodi());
		if (procediment == null) {
			LOGGER.debug("No s'ha trobat el procediment (entitatCif=" + solicitud.getEntitatCif() + ", procedimentCodi=" + solicitud.getProcedimentCodi() + ")");
			throw new ProcedimentNotFoundException();
		}
		ProcedimentServei procedimentServei = procedimentServeiRepository.findByProcedimentIdAndServei(
				procediment.getId(),
				serveiCodi);
		if (procedimentServei == null || !procedimentServei.isActiu()) {
			LOGGER.debug("No s'ha trobat el servei (serveiCodi=" + serveiCodi + ") del procediment (id=" + procediment.getId() + ")");
			throw new ProcedimentServeiNotFoundException();
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		usuariHelper.init(auth.getName());
		if (!isServeiPermesPerUsuari(
				entitat,
				procediment,
				serveiCodi)) {
			LOGGER.debug("L'usuari no te accés al servei (usuari=" + auth.getName() + ")");
			throw new ServeiNotAllowedException();
		}
		String idPeticion = null;
		try {
			idPeticion = scspHelper.generarIdPeticion(serveiCodi);
		} catch (Exception ex) {
			LOGGER.error("Error al generar identificador per a consulta (servei=" + serveiCodi + ")", ex);
			throw new ScspException(ex.getMessage(), ex);
		}
		Consulta c = Consulta.getBuilder(
				idPeticion,
				solicitud.getFuncionariNom(),
				solicitud.getFuncionariNif(),
				(solicitud.getTitularDocumentTipus() != null) ? solicitud.getTitularDocumentTipus().toString() : null,
				solicitud.getTitularDocumentNum(),
				solicitud.getTitularNom(),
				solicitud.getTitularLlinatge1(),
				solicitud.getTitularLlinatge2(),
				solicitud.getTitularNomComplet(),
				solicitud.getDepartamentNom(),
				procedimentServei,
				true,
				false,
				null).build();
		c.updateEstat(EstatTipus.Processant);
		Consulta saved = consultaRepository.save(c);
		ConsultaDto resposta = dtoMappingHelper.getMapperFacade().map(
				saved,
				ConsultaDto.class);
		return resposta;
	}
	@Transactional
	@Override
	public void novaConsultaRecobrimentEnviament(
			Long consultaId,
			RecobrimentSolicitudDto solicitud) throws ConsultaNotFoundException, ScspException {
		LOGGER.debug("Executant consulta del servei via recobriment (SCSP) (" +
				"consultaId=" + consultaId + ")");
		Consulta consulta = consultaRepository.findOne(consultaId);
		if (consulta == null) {
			LOGGER.debug("No s'ha trobat la consulta (id=" + consultaId + ")");
			throw new ConsultaNotFoundException();
		}
		ProcedimentServei procedimentServei = consulta.getProcedimentServei();
		Element copiaDadesEspecifiques = null;
		try {
			copiaDadesEspecifiques = scspHelper.copiarDadesEspecifiquesRecobriment(
					procedimentServei.getServei(),
					solicitud.getDadesEspecifiques());
		} catch (Exception ex) {
			LOGGER.error("Error al copiar les dades específiques de la consulta (consultaId=" + consultaId + ")", ex);
			throw new ScspException(ex.getMessage(), ex);
		}
		Solicitud solicitudEnviar = convertirEnSolicitud(
				procedimentServei.getProcediment().getEntitat(),
				procedimentServei.getProcediment(),
				procedimentServei.getServei(),
				consulta.getFuncionariNom(),
				consulta.getFuncionariDocumentNum(),
				DocumentTipus.valueOf(consulta.getTitularDocumentTipus()),
				consulta.getTitularDocumentNum(),
				consulta.getTitularNom(),
				consulta.getTitularLlinatge1(),
				consulta.getTitularLlinatge2(),
				consulta.getTitularNomComplet(),
				solicitud.getFinalitat(),
				solicitud.getConsentiment(),
				solicitud.getDepartamentNom(),
				solicitud.getExpedientId(),
				copiaDadesEspecifiques);
		List<Solicitud> solicituds = new ArrayList<Solicitud>();
		solicituds.add(solicitudEnviar);
		ResultatEnviamentPeticio resultat = scspHelper.enviarPeticionSincrona(
				consulta.getScspPeticionId(),
				solicituds);
		if (resultat.isError()) {
			String error = "[" + resultat.getErrorCodi() + "] " + resultat.getErrorDescripcio();
			transaccioHelper.updateErrorConsulta(consultaId, error);
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			LOGGER.error("Error retornat per la consulta SCSP (id=" + consulta.getScspPeticionId() + ", servei=" + procedimentServei.getServei() + ", usuari=" + auth.getName() + "): " + error);
			throw new ScspException(error);
		}
	}
	@Transactional(rollbackFor = {EntitatNotFoundException.class, ProcedimentNotFoundException.class, ProcedimentNotFoundException.class, AccesExternException.class, ServeiNotAllowedException.class, ScspException.class})
	@Override
	public ConsultaDto novaConsultaRecobrimentEstat(
			Long consultaId) throws ScspException {
		LOGGER.debug("Executant consulta del servei via recobriment (SCSP) (" +
				"consultaId=" + consultaId + ")");
		Consulta consulta = consultaRepository.findOne(consultaId);
		try {
			ResultatEnviamentPeticio resultat = scspHelper.recuperarResultatEnviamentPeticio(consulta.getScspPeticionId());
			updateEstatConsulta(consulta, resultat, true);
			consulta.updateScspSolicitudId(resultat.getIdsSolicituds()[0]);
			if (EstatTipus.Tramitada.equals(consulta.getEstat())) {
				generarCustodiarJustificant(consulta);
			}
			return dtoMappingHelper.getMapperFacade().map(
					consulta,
					ConsultaDto.class);
		} catch (Exception ex) {
			LOGGER.error("Error al obtenir l'estat de la petició corresponent a la consulta (consultaId=" + consultaId + ")", ex);
			throw new ScspException(ex.getMessage(), ex);
		}
	}

	@Transactional(rollbackFor = {EntitatNotFoundException.class, ProcedimentNotFoundException.class, ProcedimentServeiNotFoundException.class, ServeiNotAllowedException.class, ScspException.class})
	@Override
	public ConsultaDto novaConsultaRecobrimentMultiple(
			String serveiCodi,
			List<RecobrimentSolicitudDto> solicituds) throws EntitatNotFoundException, ProcedimentNotFoundException, ProcedimentServeiNotFoundException, ServeiNotAllowedException, ScspException {
		LOGGER.debug("Executant consulta del servei multiple via recobriment (" +
				"serveiCodi=" + serveiCodi + ", " +
				"solicituds=" + ((solicituds != null) ? solicituds.size() : "") + ")");
		copiarPropertiesToDb();
		RecobrimentSolicitudDto primeraSolicitud = solicituds.get(0);
		String entitatCif = primeraSolicitud.getEntitatCif();
		String procedimentCodi = primeraSolicitud.getProcedimentCodi();
		String funcionariNom = primeraSolicitud.getFuncionariNom();
		String funcionariNif = primeraSolicitud.getFuncionariNif();
		String departamentNom = primeraSolicitud.getDepartamentNom();
		Entitat entitat = entitatRepository.findByCif(entitatCif);
		if (entitat == null) {
			LOGGER.debug("No s'ha trobat l'entitat (entitatCif=" + entitatCif + ")");
			throw new EntitatNotFoundException();
		}
		Procediment procediment = procedimentRepository.findByEntitatAndCodi(entitat, procedimentCodi);
		if (procediment == null) {
			LOGGER.debug("No s'ha trobat el procediment (entitatCif=" + entitatCif + ", procedimentCodi=" + procedimentCodi + ")");
			throw new ProcedimentNotFoundException();
		}
		ProcedimentServei procedimentServei = procedimentServeiRepository.findByProcedimentIdAndServei(
				procediment.getId(),
				serveiCodi);
		if (procedimentServei == null || !procedimentServei.isActiu()) {
			LOGGER.debug("No s'ha trobat el servei (serveiCodi=" + serveiCodi + ") del procediment (id=" + procediment.getId() + ")");
			throw new ProcedimentServeiNotFoundException();
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		usuariHelper.init(auth.getName());
		if (!isServeiPermesPerUsuari(
				entitat,
				procediment,
				serveiCodi)) {
			LOGGER.debug("L'usuari no te accés al servei (usuari=" + auth.getName() + ")");
			throw new ServeiNotAllowedException();
		}
		String idPeticion = null;
		try {
			idPeticion = scspHelper.generarIdPeticion(serveiCodi);
		} catch (Exception ex) {
			// Error al generar l'identificador
			LOGGER.error("Error al generar identificador per a consulta (servei=" + serveiCodi + ")", ex);
			throw new ScspException(ex.getMessage(), ex);
		}
		try {
			Consulta c = Consulta.getBuilder(
					idPeticion,
					funcionariNom,
					funcionariNif,
					null,
					null,
					null,
					null,
					null,
					null,
					departamentNom,
					procedimentServei,
					true,
					true,
					null).build();
			c.updateEstat(EstatTipus.Pendent);
			List<Solicitud> solicitudsEnviar = new ArrayList<Solicitud>();
			for (RecobrimentSolicitudDto solicitud: solicituds) {
				Solicitud solicitudEnviar = convertirEnSolicitud(
						entitat,
						procediment,
						serveiCodi,
						funcionariNom,
						funcionariNif,
						solicitud.getTitularDocumentTipus(),
						solicitud.getTitularDocumentNum(),
						solicitud.getTitularNom(),
						solicitud.getTitularLlinatge1(),
						solicitud.getTitularLlinatge2(),
						solicitud.getTitularNomComplet(),
						solicitud.getFinalitat(),
						solicitud.getConsentiment(),
						departamentNom,
						solicitud.getExpedientId(),
						scspHelper.copiarDadesEspecifiquesRecobriment(
								serveiCodi,
								solicitud.getDadesEspecifiques()));
				solicitudsEnviar.add(solicitudEnviar);
			}
			ResultatEnviamentPeticio resultat = scspHelper.enviarPeticionSincrona(
					idPeticion,
					solicitudsEnviar);
			updateEstatConsulta(c, resultat, true);
			Consulta saved = consultaRepository.save(c);
			int solicitudIndex = 0;
			for (RecobrimentSolicitudDto solicitud: solicituds) {
				String titularDocumentTipus = null;
				if (solicitud.getTitularDocumentTipus() != null)
					titularDocumentTipus = solicitud.getTitularDocumentTipus().toString();
				Consulta cs = Consulta.getBuilder(
						idPeticion,
						funcionariNom,
						funcionariNif,
						titularDocumentTipus,
						solicitud.getTitularDocumentNum(),
						solicitud.getTitularNom(),
						solicitud.getTitularLlinatge1(),
						solicitud.getTitularLlinatge2(),
						solicitud.getTitularNomComplet(),
						departamentNom,
						procedimentServei,
						true,
						false,
						c).build();
				cs.updateScspSolicitudId(resultat.getIdsSolicituds()[solicitudIndex++]);
				updateEstatConsulta(cs, resultat, true);
				consultaRepository.save(cs);
			}
			ConsultaDto resposta = dtoMappingHelper.getMapperFacade().map(
					saved,
					ConsultaDto.class);
			// Si la petició ha produit un error ompl els camps per permetre
			// al recobriment generar el missatge amb informació de l'error.
			if (resultat.isError()) {
				resposta.setRespostaEstadoCodigo(resultat.getErrorCodi());
				resposta.setRespostaEstadoError(resultat.getErrorDescripcio());
			}
			return resposta;
		} catch (Exception ex) {
			// Error al realitzar la petició (no s'arriba a enviar)
			LOGGER.error("Excepció al realitzar consulta SCSP (id=" + idPeticion + ", servei=" + serveiCodi + ", usuari=" + auth.getName() + ")", ex);
			throw new ScspException(ex.getMessage(), ex);
		}
	}

	@Transactional(rollbackFor = {ConsultaNotFoundException.class, JustificantGeneracioException.class})
	@Override
	public ArxiuDto obtenirJustificant(
			Long id) throws ConsultaNotFoundException, JustificantGeneracioException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		LOGGER.debug("Generant justificant per a la consulta (id=" + id + ")");
		copiarPropertiesToDb();
		Consulta consulta = consultaRepository.findOne(id);
		if (consulta == null) {
			LOGGER.error("No s'ha trobat la consulta (id=" + id + ")");
			throw new ConsultaNotFoundException();
		}
		if (!auth.getName().equals(consulta.getCreatedBy().getCodi())) {
			LOGGER.error("La consulta (id=" + id + ") no pertany a aquest usuari");
			throw new ConsultaNotFoundException();
		}
		ResultatEnviamentPeticio resultat;
		try {
			resultat = scspHelper.recuperarResultatEnviamentPeticio(consulta.getScspPeticionId());
		} catch (Exception ex) {
			LOGGER.error("No s'ha pogut recuperar l'estat de la consulta (id=" + id + ")", ex);
			throw new JustificantGeneracioException();
		}
		if (resultat.isError()) {
			LOGGER.error("La consulta (id=" + id + ") conté errors");
			throw new ConsultaNotFoundException();
		}
		try {
			return obtenirJustificantConsulta(consulta);
		} catch (Exception ex) {
			LOGGER.error("Error al obtenir el justificant de la consulta (id=" + id + ")", ex);
			throw new JustificantGeneracioException(ex.getMessage(), ex);
		}
	}

	@Transactional(rollbackFor = {ConsultaNotFoundException.class, JustificantGeneracioException.class})
	@Override
	public ArxiuDto obtenirJustificantMultipleConcatenat(
			Long id) throws ConsultaNotFoundException, JustificantGeneracioException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		LOGGER.debug("Generant justificant concatenat per a la consulta múltiple (id=" + id + ")");
		copiarPropertiesToDb();
		Consulta consulta = consultaRepository.findOne(id);
		if (consulta == null) {
			LOGGER.debug("No s'ha trobat la consulta (id=" + id + ")");
			throw new ConsultaNotFoundException();
		}
		if (!auth.getName().equals(consulta.getCreatedBy().getCodi())) {
			LOGGER.debug("La consulta (id=" + id + ") no pertany a aquest usuari");
			throw new ConsultaNotFoundException();
		}
		if (!consulta.isMultiple()) {
			LOGGER.debug("La consulta (id=" + id + ") no és múltiple");
			throw new ConsultaNotFoundException();
		}
		if (!getExtensioSortida().equalsIgnoreCase("pdf")) {
			LOGGER.debug("L'extensió de sortida del justificant no és PDF");
			throw new JustificantGeneracioException();
		}
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Document pdfConcatenat = new Document();
			PdfCopy copy = new PdfCopy(pdfConcatenat, baos);
			pdfConcatenat.open();
			for (Consulta solicitud: consulta.getFills()) {
				PdfReader pdfReader = new PdfReader(
						generarJustificantAmbPlantilla(solicitud));
				for (int pagina = 1; pagina <= pdfReader.getNumberOfPages(); pagina++) {
					copy.addPage(
							copy.getImportedPage(pdfReader, pagina));
				}
			}
			pdfConcatenat.close();
			ArxiuDto arxiu = new ArxiuDto();
			arxiu.setNom(conversioTipusDocumentHelper.nomArxiuConvertit(
					justificantPlantillaHelper.getNomArxiuGenerat(
							consulta.getScspPeticionId(),
							consulta.getScspSolicitudId()),
					getExtensioSortida()));
			arxiu.setContingut(baos.toByteArray());
			return arxiu;
		} catch (Exception ex) {
			LOGGER.error("Error al generar justificant concatenat per a la consulta múltiple (id=" + id + ")", ex);
			throw new JustificantGeneracioException(ex.getMessage(), ex);
		}
	}

	@Transactional(rollbackFor = {ConsultaNotFoundException.class, JustificantGeneracioException.class})
	@Override
	public ArxiuDto obtenirJustificantMultipleZip(
			Long id) throws ConsultaNotFoundException, JustificantGeneracioException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		LOGGER.debug("Generant justificant ZIP per a la consulta múltiple (id=" + id + ")");
		copiarPropertiesToDb();
		Consulta consulta = consultaRepository.findOne(id);
		if (consulta == null) {
			LOGGER.debug("No s'ha trobat la consulta (id=" + id + ")");
			throw new ConsultaNotFoundException();
		}
		if (!auth.getName().equals(consulta.getCreatedBy().getCodi())) {
			LOGGER.debug("La consulta (id=" + id + ") no pertany a aquest usuari");
			throw new ConsultaNotFoundException();
		}
		if (!consulta.isMultiple()) {
			LOGGER.debug("La consulta (id=" + id + ") no és múltiple");
			throw new ConsultaNotFoundException();
		}
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ZipOutputStream zos = new ZipOutputStream(baos);
			for (Consulta solicitud: consulta.getFills()) {
				String justificantNom = conversioTipusDocumentHelper.nomArxiuConvertit(
						justificantPlantillaHelper.getNomArxiuGenerat(
								solicitud.getScspPeticionId(),
								solicitud.getScspSolicitudId()),
						getExtensioSortida());
				ZipEntry zipEntry = new ZipEntry(justificantNom);
				zos.putNextEntry(zipEntry);
				zos.write(
						generarJustificantAmbPlantilla(solicitud));
				zos.closeEntry();
			}
			zos.close();
			ArxiuDto arxiu = new ArxiuDto();
			arxiu.setNom(conversioTipusDocumentHelper.nomArxiuConvertit(
					justificantPlantillaHelper.getNomArxiuGenerat(
							consulta.getScspPeticionId(),
							consulta.getScspSolicitudId()),
					"zip"));
			arxiu.setContingut(baos.toByteArray());
			return arxiu;
		} catch (Exception ex) {
			LOGGER.error("Error al generar justificant ZIP per a la consulta múltiple (id=" + id + ")", ex);
			throw new JustificantGeneracioException(ex.getMessage(), ex);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public void reintentarGeneracioJustificant(
			Long id) throws ConsultaNotFoundException, JustificantGeneracioException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		LOGGER.debug("Reintentant generació del justificant per a la consulta (id=" + id + ")");
		copiarPropertiesToDb();
		Consulta consulta = consultaRepository.findOne(id);
		if (consulta == null) {
			LOGGER.error("No s'ha trobat la consulta (id=" + id + ")");
			throw new ConsultaNotFoundException();
		}
		if (!auth.getName().equals(consulta.getCreatedBy().getCodi())) {
			LOGGER.error("La consulta (id=" + id + ") no pertany a aquest usuari");
			throw new ConsultaNotFoundException();
		}
		ResultatEnviamentPeticio resultat;
		try {
			resultat = scspHelper.recuperarResultatEnviamentPeticio(consulta.getScspPeticionId());
		} catch (Exception ex) {
			LOGGER.error("No s'ha pogut recuperar l'estat de la consulta (id=" + id + ")", ex);
			throw new JustificantGeneracioException();
		}
		if (resultat.isError()) {
			LOGGER.error("La consulta (id=" + id + ") conté errors");
			throw new ConsultaNotFoundException();
		}
		if (EstatTipus.Tramitada.equals(consulta.getEstat())) {
			try {
				generarCustodiarJustificant(consulta);
			} catch (Exception ex) {
				LOGGER.error("Error al generar el justificant de la consulta (id=" + id + ")", ex);
				throw new JustificantGeneracioException(ex.getMessage(), ex);
			}
		} else {
			LOGGER.error("La consulta no està en estat TRAMITADA (id=" + id + ")");
			throw new JustificantGeneracioException();
		}
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaLlistatDto<ConsultaDto> findSimplesByFiltrePaginatPerDelegat(
			Long entitatId,
			ConsultaFiltreDto filtre,
			PaginacioAmbOrdreDto paginacioAmbOrdre) throws EntitatNotFoundException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		LOGGER.debug("Cercant les consultes de delegat simples per a l'entitat (id=" + entitatId + ") i l'usuari (codi=" + auth.getName() + ")");
		Entitat entitat = entitatRepository.findOne(entitatId);
		if (entitat == null) {
			LOGGER.debug("No s'ha trobat l'entitat (id=" + entitatId + ")");
			throw new EntitatNotFoundException();
		}
		copiarPropertiesToDb();
		return findByEntitatIUsuariFiltrePaginat(
				entitat,
				auth.getName(),
				filtre,
				paginacioAmbOrdre,
				false,
				true);
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaLlistatDto<ConsultaDto> findMultiplesByFiltrePaginatPerDelegat(
			Long entitatId,
			ConsultaFiltreDto filtre,
			PaginacioAmbOrdreDto paginacioAmbOrdre) throws EntitatNotFoundException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		LOGGER.debug("Cercant les consultes de delegat múltiples per a l'entitat (id=" + entitatId + ") i l'usuari (codi=" + auth.getName() + ")");
		Entitat entitat = entitatRepository.findOne(entitatId);
		if (entitat == null) {
			LOGGER.debug("No s'ha trobat l'entitat (id=" + entitatId + ")");
			throw new EntitatNotFoundException();
		}
		copiarPropertiesToDb();
		return findByEntitatIUsuariFiltrePaginat(
				entitat,
				auth.getName(),
				filtre,
				paginacioAmbOrdre,
				true,
				true);
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaLlistatDto<ConsultaDto> findByFiltrePaginatPerAuditor(
			Long entitatId,
			ConsultaFiltreDto filtre,
			PaginacioAmbOrdreDto paginacioAmbOrdre) throws EntitatNotFoundException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		LOGGER.debug("Cercant les consultes d'auditor per a l'entitat (id=" + entitatId + ")");
		Entitat entitat = entitatRepository.findOne(entitatId);
		if (entitat == null) {
			LOGGER.debug("No s'ha trobat l'entitat (id=" + entitatId + ")");
			throw new EntitatNotFoundException();
		}
		EntitatUsuari entitatUsuari = entitatUsuariRepository.findByEntitatIdAndUsuariCodi(
				entitat.getId(),
				auth.getName());
		if (entitatUsuari == null || !entitatUsuari.isAuditor()) {
			LOGGER.debug("Aquest usuari no té permisos per auditar l'entitat (id=" + entitatId + ", usuariCodi=" + auth.getName() + ")");
			throw new EntitatNotFoundException();
		}
		copiarPropertiesToDb();
		return findByEntitatIUsuariFiltrePaginat(
				entitat,
				null,
				filtre,
				paginacioAmbOrdre,
				false,
				false);
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaLlistatDto<ConsultaDto> findByFiltrePaginatPerSuperauditor(
			Long entitatId,
			ConsultaFiltreDto filtre,
			PaginacioAmbOrdreDto paginacioAmbOrdre) throws EntitatNotFoundException {
		LOGGER.debug("Cercant les consultes de superauditor per a l'entitat (id=" + entitatId + ")");
		Entitat entitat = entitatRepository.findOne(entitatId);
		if (entitat == null) {
			LOGGER.debug("No s'ha trobat l'entitat (id=" + entitatId + ")");
			throw new EntitatNotFoundException();
		}
		copiarPropertiesToDb();
		return findByEntitatIUsuariFiltrePaginat(
				entitat,
				null,
				filtre,
				paginacioAmbOrdre,
				false,
				false);
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaLlistatDto<ConsultaDto> findByFiltrePaginatPerAdmin(
			Long entitatId,
			ConsultaFiltreDto filtre,
			PaginacioAmbOrdreDto paginacioAmbOrdre) throws EntitatNotFoundException {
		LOGGER.debug("Cercant les consultes de administrador per a l'entitat (id=" + entitatId + ")");
		Entitat entitat = entitatRepository.findOne(entitatId);
		if (entitat == null) {
			LOGGER.debug("No s'ha trobat l'entitat (id=" + entitatId + ")");
			throw new EntitatNotFoundException();
		}
		copiarPropertiesToDb();
		return findByEntitatIUsuariFiltrePaginat(
				entitat,
				null,
				filtre,
				paginacioAmbOrdre,
				false,
				false);
	}

	@Transactional(readOnly = true)
	@Override
	public ConsultaDto findOneDelegat(Long id) throws ConsultaNotFoundException, ScspException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		LOGGER.debug("Cercant les dades de la consulta (id=" + id + ")");
		Consulta consulta = consultaRepository.findOne(id);
		if (consulta == null) {
			LOGGER.debug("No s'ha trobat la consulta (id=" + id + ")");
			throw new ConsultaNotFoundException();
		}
		if (!auth.getName().equals(consulta.getCreatedBy().getCodi())) {
			LOGGER.debug("La consulta (id=" + id + ") no pertany a aquest usuari");
			throw new ConsultaNotFoundException();
		}
		return toConsultaDto(
				null,
				consulta);
	}

	@Transactional(readOnly = true)
	@Override
	public ConsultaDto findOneAuditor(Long id) throws ConsultaNotFoundException, ScspException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		LOGGER.debug("Cercant les dades de la consulta (id=" + id + ")");
		Consulta consulta = consultaRepository.findOne(id);
		if (consulta == null) {
			LOGGER.debug("No s'ha trobat la consulta (id=" + id + ")");
			throw new ConsultaNotFoundException();
		}
		Entitat entitat = consulta.getProcedimentServei().getProcediment().getEntitat();
		EntitatUsuari entitatUsuari = entitatUsuariRepository.findByEntitatIdAndUsuariCodi(
				entitat.getId(),
				auth.getName());
		if (entitatUsuari == null || !entitatUsuari.isAuditor()) {
			LOGGER.debug("Aquest usuari no té permisos per auditar la consulta (id=" + id + ", usuariCodi=" + auth.getName() + ")");
			throw new ConsultaNotFoundException();
		}
		return toConsultaDto(
				null,
				consulta);
	}

	@Transactional(readOnly = true)
	@Override
	public ConsultaDto findOneSuperauditor(Long id) throws ConsultaNotFoundException, ScspException {
		LOGGER.debug("Cercant les dades de la consulta (id=" + id + ")");
		Consulta consulta = consultaRepository.findOne(id);
		if (consulta == null) {
			LOGGER.debug("No s'ha trobat la consulta (id=" + id + ")");
			throw new ConsultaNotFoundException();
		}
		return toConsultaDto(
				null,
				consulta);
	}

	@Transactional(readOnly = true)
	@Override
	public ConsultaDto findOneAdmin(Long id) throws ConsultaNotFoundException, ScspException {
		LOGGER.debug("Cercant les dades de la consulta (id=" + id + ")");
		Consulta consulta = consultaRepository.findOne(id);
		if (consulta == null) {
			LOGGER.debug("No s'ha trobat la consulta (id=" + id + ")");
			throw new ConsultaNotFoundException();
		}
		return toConsultaDto(
				null,
				consulta);
	}

	@Transactional(readOnly = true)
	@Override
	public List<ConsultaDto> findAmbPare(
			Long pareId) throws ConsultaNotFoundException, ScspException {
		LOGGER.debug("Cercant les consultes amb pare (pareId=" + pareId + ")");
		Consulta pare = consultaRepository.findOne(pareId);
		if (pare == null) {
			LOGGER.debug("No s'ha trobat la consulta (id=" + pareId + ")");
			throw new ConsultaNotFoundException();
		}
		List<ConsultaDto> resposta = new ArrayList<ConsultaDto>();
		List<Consulta> filles = consultaRepository.findByPareOrderByScspSolicitudIdAsc(pare);
		for (Consulta filla: filles) {
			resposta.add(
					toConsultaDto(
							null,
							filla));
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public long countConsultesMultiplesProcessant(
			Long entitatId) throws EntitatNotFoundException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		LOGGER.debug("Contant consultes múltiples pendents (entitatId=" + entitatId + ")");
		return consultaRepository.countByEstatAndCreatedByAndMultipleTrue(
				EstatTipus.Processant,
				usuariRepository.findOne(auth.getName()));
	}

	@Transactional(readOnly = true)
	@Override
	public List<EstadisticaDto> findEstadistiquesByFiltre(EstadistiquesFiltreDto filtre) throws EntitatNotFoundException {
		LOGGER.debug("Consultant estadístiques per a l'entitat (id=" + filtre.getEntitatId() + ")");
		if (filtre.getEntitatId() != null) {
			Entitat entitat = entitatRepository.findOne(filtre.getEntitatId());
			if (entitat == null) {
				LOGGER.debug("No s'ha trobat l'entitat (id=" + filtre.getEntitatId() + ")");
				throw new EntitatNotFoundException();
			}
		}
		List<EstadisticaDto> resposta = new ArrayList<EstadisticaDto>();
		List<Object[]> resultats;
		if (EstadistiquesAgrupacioDto.PROCEDIMENT_SERVEI.equals(filtre.getAgrupacio())) {
			resultats = consultaRepository.countByProcedimentServei(
					filtre.getEntitatId() == null,
					filtre.getEntitatId(),
					filtre.getUsuariCodi() == null,
					(filtre.getUsuariCodi() != null) ? usuariRepository.findOne(filtre.getUsuariCodi()) : null,
					filtre.getProcedimentId() == null,
					filtre.getProcedimentId(),
					filtre.getServeiCodi() == null || filtre.getServeiCodi().isEmpty(),
					filtre.getServeiCodi(),
					filtre.getEstat() == null,
					(filtre.getEstat() != null) ? Consulta.EstatTipus.valueOf(filtre.getEstat().toString()) : null,
					filtre.getDataInici() == null,
					filtre.getDataInici(),
					filtre.getDataFi() == null,
					configurarDataFiPerFiltre(filtre.getDataFi()),
					EstatTipus.Tramitada,
					EstatTipus.Error);
		} else {
			resultats = consultaRepository.countByServeiProcediment(
					filtre.getEntitatId() == null,
					filtre.getEntitatId(),
					filtre.getUsuariCodi() == null,
					(filtre.getUsuariCodi() != null) ? usuariRepository.findOne(filtre.getUsuariCodi()) : null,
					filtre.getProcedimentId() == null,
					filtre.getProcedimentId(),
					filtre.getServeiCodi() == null || filtre.getServeiCodi().isEmpty(),
					filtre.getServeiCodi(),
					filtre.getEstat() == null,
					(filtre.getEstat() != null) ? Consulta.EstatTipus.valueOf(filtre.getEstat().toString()) : null,
					filtre.getDataInici() == null,
					filtre.getDataInici(),
					filtre.getDataFi() == null,
					configurarDataFiPerFiltre(filtre.getDataFi()),
					EstatTipus.Tramitada,
					EstatTipus.Error);
		}
		for (Object[] resultat: resultats) {
			Long procedimentServeiId = (Long)resultat[0];
			Long numRecobrimentOk = (Long)resultat[1];
			Long numRecobrimentError = (Long)resultat[2];
			Long numWebUIOk = (Long)resultat[4];
			Long numWebUIError = (Long)resultat[5];
			ProcedimentServei procedimentServei = procedimentServeiRepository.findOne(procedimentServeiId);
			EstadisticaDto dto = new EstadisticaDto();
			dto.setProcediment(
					dtoMappingHelper.getMapperFacade().map(
							procedimentServei.getProcediment(),
							ProcedimentDto.class));
			dto.setServeiCodi(procedimentServei.getServei());
			dto.setServeiNom(
					scspHelper.getServicioDescripcion(
							procedimentServei.getServei()));
			dto.setNumRecobrimentOk(numRecobrimentOk);
			dto.setNumRecobrimentError(numRecobrimentError);
			dto.setNumWebUIOk(numWebUIOk);
			dto.setNumWebUIError(numWebUIError);
			resposta.add(dto);
		}
		EstadisticaDto estadisticaActual = null;
		for (EstadisticaDto estadistica: resposta) {
			boolean coincideixProcediment = estadisticaActual != null && estadisticaActual.getProcediment().getId().equals(estadistica.getProcediment().getId());
			boolean coincideixServei = estadisticaActual != null && estadisticaActual.getServeiCodi().equals(estadistica.getServeiCodi());
			if (	estadisticaActual == null || 
					(EstadistiquesAgrupacioDto.PROCEDIMENT_SERVEI.equals(filtre.getAgrupacio()) && !coincideixProcediment) ||
					(EstadistiquesAgrupacioDto.SERVEI_PROCEDIMENT.equals(filtre.getAgrupacio()) && !coincideixServei)) {
				estadisticaActual = estadistica;
				estadisticaActual.setConteSumatori(true);
			}
			estadisticaActual.setSumatoriNumRegistres(
					estadisticaActual.getSumatoriNumRegistres() + 1);
			estadisticaActual.setSumatoriNumRecobrimentOk(
					estadisticaActual.getSumatoriNumRecobrimentOk() + estadistica.getNumRecobrimentOk());
			estadisticaActual.setSumatoriNumRecobrimentError(
					estadisticaActual.getSumatoriNumRecobrimentError() + estadistica.getNumRecobrimentError());
			estadisticaActual.setSumatoriNumWebUIOk(
					estadisticaActual.getSumatoriNumWebUIOk() + estadistica.getNumWebUIOk());
			estadisticaActual.setSumatoriNumWebUIError(
					estadisticaActual.getSumatoriNumWebUIError() + estadistica.getNumWebUIError());
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public Map<EntitatDto, List<EstadisticaDto>> findEstadistiquesGlobalsByFiltre(
			EstadistiquesFiltreDto filtre) {
		LOGGER.debug("Consultant estadístiques globals");
		Map<EntitatDto, List<EstadisticaDto>> resposta = new HashMap<EntitatDto, List<EstadisticaDto>>();
		List<Object[]> resultats = consultaRepository.countByEntitat(
				filtre.getUsuariCodi() == null,
				(filtre.getUsuariCodi() != null) ? usuariRepository.findOne(filtre.getUsuariCodi()) : null,
				filtre.getProcedimentId() == null,
				filtre.getProcedimentId(),
				filtre.getServeiCodi() == null || filtre.getServeiCodi().isEmpty(),
				filtre.getServeiCodi(),
				filtre.getEstat() == null,
				(filtre.getEstat() != null) ? Consulta.EstatTipus.valueOf(filtre.getEstat().toString()) : null,
				filtre.getDataInici() == null,
				filtre.getDataInici(),
				filtre.getDataFi() == null,
				configurarDataFiPerFiltre(filtre.getDataFi()));
		// Omple les estadístiques per cada entitat
		for (Object[] resultat: resultats) {
			Long entitatId = (Long)resultat[0];
			Long numConsultes = (Long)resultat[1];
			if (numConsultes > 0) {
				filtre.setEntitatId(entitatId);
				try {
					resposta.put(
							dtoMappingHelper.getMapperFacade().map(
									entitatRepository.findOne(entitatId),
									EntitatDto.class),
							findEstadistiquesByFiltre(filtre));
				} catch (EntitatNotFoundException ex) {
					// És impossible però ho traurem pel logger
					LOGGER.error("No s'ha trobat l'entitat (entitatId=" + entitatId + ")", ex);
				}
			}
		}
		// Omple l'estadística global
		filtre.setEntitatId(null);
		try {
			resposta.put(
					null,
					findEstadistiquesByFiltre(filtre));
		} catch (EntitatNotFoundException ex) {
			// És impossible però ho traurem pel logger
			LOGGER.error("No s'ha trobat l'entitat (entitatId=null)", ex);
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public List<Long> auditoriaGenerarAuditor(
			Long entitatId,
			Date dataInici,
			Date dataFi,
			int numConsultes) throws EntitatNotFoundException {
		LOGGER.debug("Generant auditoria per auditor (" +
				"entitatId=" + entitatId + "," +
				"dataInici=" + dataInici + "," +
				"dataFi=" + dataFi + "," +
				"numConsultes=" + numConsultes + ")");
		Entitat entitat = entitatRepository.findOne(entitatId);
		if (entitat == null) {
			LOGGER.debug("No s'ha trobat l'entitat (id=" + entitatId + ")");
			throw new EntitatNotFoundException();
		}
		List<Consulta> consultes = consultaRepository.findByEntitatAndDataIniciAndDataFi(
				entitat,
				dataInici,
				dataFi);
		List<Long> resposta = new ArrayList<Long>();
		if (consultes.size() > numConsultes) {
			int[] indexos = getRandomIndexesFromList(
					consultes,
					numConsultes);
			for (int index: indexos) {
				resposta.add(consultes.get(index).getId());
			}
		} else {
			for (Consulta consulta: consultes)
				resposta.add(consulta.getId());
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public List<ConsultaDto> auditoriaConsultarAuditor(
			Long entitatId,
			List<Long> consultaIds) throws EntitatNotFoundException, ScspException {
		LOGGER.debug("Consultant auditoria per auditor (" +
				"entitatId=" + entitatId + "," +
				"consultaIds=" + consultaIds + ")");
		Entitat entitat = entitatRepository.findOne(entitatId);
		if (entitat == null) {
			LOGGER.debug("No s'ha trobat l'entitat (id=" + entitatId + ")");
			throw new EntitatNotFoundException();
		}
		List<Consulta> consultes = consultaRepository.findByEntitatAndIds(
				entitat,
				consultaIds);
		List<ConsultaDto> resposta = new ArrayList<ConsultaDto>();
		for (Consulta consulta: consultes)
			resposta.add(toConsultaDto(entitatId, consulta));
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public List<Long> auditoriaGenerarSuperauditor(
			Date dataInici,
			Date dataFi,
			int numEntitats,
			int numConsultes) {
		LOGGER.debug("Generant auditoria per superauditor (" +
				"dataInici=" + dataInici + "," +
				"dataFi=" + dataFi + "," +
				"numConsultes=" + numConsultes + "," +
				"numEntitats=" + numEntitats + ")");
		List<Entitat> entitats = entitatRepository.findAll();
		// Filtra les entitats sense cap consulta
		List<Object[]> entitatCounts = consultaRepository.countByEntitat(
				true,
				null,
				true,
				null,
				true,
				null,
				true,
				null,
				false,
				dataInici,
				false,
				dataFi);
		Iterator<Entitat> itEntitats = entitats.iterator();
		while (itEntitats.hasNext()) {
			Entitat entitat = itEntitats.next();
			boolean trobada = false;
			for (Object[] count: entitatCounts) {
				Long entitatId = (Long)count[0];
				if (entitat.getId().equals(entitatId)) {
					trobada = true;
					break;
				}
			}
			if (!trobada)
				itEntitats.remove();
		}
		// Selecciona numEntitats d'entre les disponibles
		List<Long> seleccioEntitats = new ArrayList<Long>();
		if (entitats.size() > numEntitats) {
			int[] indexos = getRandomIndexesFromList(
					entitats,
					numEntitats);
			for (int index: indexos) {
				seleccioEntitats.add(entitats.get(index).getId());
			}
			itEntitats = entitats.iterator();
			while (itEntitats.hasNext()) {
				Entitat entitat = itEntitats.next();
				if (!seleccioEntitats.contains(entitat.getId()))
					itEntitats.remove();
			}
		}
		// Genera l'auditoria per a cada entitat
		List<Long> resposta = new ArrayList<Long>();
		for (Entitat entitat: entitats) {
			try {
				List<Long> auditoria = auditoriaGenerarAuditor(
						entitat.getId(),
						dataInici,
						dataFi,
						numConsultes);
				resposta.addAll(auditoria);
			} catch (EntitatNotFoundException e) {
				LOGGER.error("No s'ha trobat l'entitat (id=" + entitat.getId() + ")");
			}
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public Map<EntitatDto, List<ConsultaDto>> auditoriaConsultarSuperauditor(
			List<Long> consultaIds) throws ScspException {
		LOGGER.debug("Consultant auditoria per superauditor (" +
				"consultaIds=" + consultaIds + ")");
		List<Object[]> consultes = consultaRepository.findByIds(
				consultaIds);
		Map<EntitatDto, List<ConsultaDto>> resposta = new HashMap<EntitatDto, List<ConsultaDto>>();
		List<ConsultaDto> consultesEntitat = null;
		EntitatDto entitatActual = null;
		for (Object[] consulta: consultes) {
			Consulta c = (Consulta)consulta[0];
			Long entitatId = (Long)consulta[1];
			if (entitatActual == null || !entitatActual.getId().equals(entitatId)) {
				entitatActual = dtoMappingHelper.getMapperFacade().map(
						entitatRepository.findOne(entitatId),
						EntitatDto.class);
				consultesEntitat = new ArrayList<ConsultaDto>();
				resposta.put(entitatActual, consultesEntitat);
			}
			consultesEntitat.add(toConsultaDto(entitatId, c));
		}
		return resposta;
	}

	@Transactional
	@Override
	public void revisarEstatPeticionsMultiplesPendents() {
		LOGGER.debug("Revisant estats peticions múltiples pendents");
		long t0 = System.currentTimeMillis();
		List<Consulta> pendents = consultaRepository.findByEstatAndMultipleTrue(EstatTipus.Processant);
		for (Consulta pendent: pendents) {
			try {
				ResultatEnviamentPeticio resultat = scspHelper.recuperarResultatEnviamentPeticio(pendent.getScspPeticionId());
				updateEstatConsulta(pendent, resultat, false);
				for (Consulta filla: pendent.getFills()) {
					updateEstatConsulta(filla, resultat, false);
				}
			} catch (Exception ex) {
				LOGGER.error("No s'ha pogut obtenir l'estat de la consulta SCSP (peticionId=" + pendent.getScspPeticionId() + ")", ex);
			}
		}
		LOGGER.debug("Finalitzada revisió estats peticions múltiples pendents (" + (System.currentTimeMillis() - t0) + "ms)");
	}

	@Override
	public boolean isOptimitzarTransaccionsNovaConsulta() {
		LOGGER.debug("Consultant optimització transaccions en nova consulta");
		return PropertiesHelper.getProperties().getAsBoolean("es.caib.pinbal.optimitzar.transaccions.nova.consulta");
	}

	@Transactional
	public Respuesta recuperarRespuestaScsp(String peticionId) throws es.scsp.common.exceptions.ScspException {
		LOGGER.debug("Recuperant resposta SCSP per recobriment(peticionId=" + peticionId + ")");
		return scspHelper.recuperarRespuestaScsp(peticionId);
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<InformeGeneralEstatDto> informeGeneralEstat(Date dataInici, Date dataFi) {
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(dataInici);
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND,0);
		dataInici = cal.getTime();
		
		cal.setTime(dataFi);
		cal.set(Calendar.HOUR_OF_DAY,23);
		cal.set(Calendar.MINUTE,59);
		cal.set(Calendar.SECOND,59);
		cal.set(Calendar.MILLISECOND,999);
		dataFi = cal.getTime();
		
		LOGGER.debug("Obtenint informe general d'estats");
		List<InformeGeneralEstatDto> resposta = new ArrayList<InformeGeneralEstatDto>();
		
		List<ProcedimentServei> serveis = procedimentServeiRepository.findAll(
				new Sort(Sort.Direction.ASC, "procediment.entitat.nom", "procediment.codi", "servei"));
		List<Object[]> consultes = consultaRepository.countGroupByProcedimentServeiEstat(dataInici, dataFi);
		
		for (ProcedimentServei servei : serveis) {
			resposta.add(toInformeGeneralEstatDto(servei, consultes));
		}
		
		return resposta;
	}

	private InformeGeneralEstatDto toInformeGeneralEstatDto(ProcedimentServei servei, List<Object[]> consultes) {
		InformeGeneralEstatDto dto = new InformeGeneralEstatDto();
		
		Servicio servicio = scspHelper.getServicio(servei.getServei());
	
		dto.setEntitatNom(servei.getProcediment().getEntitat().getNom());
		dto.setEntitatCif(servei.getProcediment().getEntitat().getCif());
		dto.setDepartament(servei.getProcediment().getDepartament());
		dto.setProcedimentCodi(servei.getProcediment().getCodi());
		dto.setProcedimentNom(servei.getProcediment().getNom());
		dto.setServeiCodi(servicio.getCodCertificado());
		dto.setServeiNom(servicio.getDescripcion());
		if (servicio.getEmisor() != null) {
			EmisorDto emisor = new EmisorDto();
			EmisorCertificado emisorCertificado = servicio.getEmisor();
			emisor.setCif(emisorCertificado.getCif());
			emisor.setNom(scspHelper.getEmisorNombre(emisorCertificado.getCif()));
			dto.setServeiEmisor(emisor);
		}
		
		// Obtenim els usuaris que tenen permís sobre el procediment-servei
		List<AccessControlEntry> aces = PermisosHelper.getAclSids(
				ProcedimentServei.class,
				servei.getId(),
				aclService);
		Set<String> usuaris = new HashSet<String>();
		if (aces != null)
			for (AccessControlEntry ace: aces) {
				if (ace.getSid() instanceof PrincipalSid)
					usuaris.add(((PrincipalSid)ace.getSid()).getPrincipal());
			}
		dto.setServeiUsuaris(usuaris.size());
		
		// Calculam les peticions correctes i les errònees
		// consulta[0] --> Codi entitat
		// consulta[1] --> Codi procediemnt
		// consulta[2] --> Codi servei
		// consulta[3] --> Estat (Pendent, Processant, Tramitada o Error)
		// consulta[4] --> Número de peticions
		Long peticionsCorrectes = 0L;
		Long peticionsErronees = 0L;
		
		boolean etrobat = false;
		boolean strobat = false;
		
		Long entitatId = servei.getProcediment().getEntitat().getId();
		Long procedimentId = servei.getProcediment().getId();
		
		for (int i = 0; i < consultes.size(); i++) {
			Long entId = (Long)consultes.get(i)[0];
			Long procId = (Long)consultes.get(i)[1];
			if (entId.equals(entitatId) && procId.equals(procedimentId)) {
				etrobat = true;
				String serveiCodi = (String)consultes.get(i)[2];
				if (servei.getServei().equals(serveiCodi)) {
					strobat = true;
					EstatTipus estat = (EstatTipus)consultes.get(i)[3];
					Long count = (Long)consultes.get(i)[4];
					if (estat.equals(EstatTipus.Error))
						peticionsErronees += count;
					else
						peticionsCorrectes += count;
				} else if (strobat) {
					// Les consultes estan ordenades per servei. Per tant, al trobar un nou servei, ja no cal continuar
					strobat = false;
					break;
				}
			} else if(etrobat) {
				// Les consultes estan ordenades per entitat i procediment. Per tant, al trobar una nova entitat-procediment, ja no cal continuar
				etrobat = false;
				break;
			}
		}
		dto.setPeticionsCorrectes(peticionsCorrectes.intValue());
		dto.setPeticionsErronees(peticionsErronees.intValue());
		
		return dto;
	}


	private ConsultaDto toConsultaDto(
			Long entitatId,
			Consulta consulta) throws ScspException {
		ConsultaDto resposta = dtoMappingHelper.getMapperFacade().map(
				consulta,
				ConsultaDto.class);
		resposta.setEntitatId(entitatId);
		//resposta.setFuncionariNom(consulta.getCreatedBy().getNom());
		//resposta.setFuncionariNif(consulta.getCreatedBy().getNif());
		resposta.setFuncionariNom(consulta.getFuncionariNom());
		resposta.setFuncionariNif(consulta.getFuncionariDocumentNum());
		try {
			Resposta rpt = scspHelper.recuperarResposta(
					consulta.getScspPeticionId(),
					consulta.getScspSolicitudId(),
					consulta.isMultiple());
			if (rpt != null) {
				resposta.setFinalitat(rpt.getFinalitat());
				if (rpt.getConsentiment() != null)
					resposta.setConsentiment(ConsultaDto.Consentiment.valueOf(rpt.getConsentiment().name()));
				resposta.setExpedientId(rpt.getExpedientId());
				if (rpt.getRespostaXml() != null) {
					resposta.setHiHaResposta(true);
					resposta.setRespostaData(rpt.getRespostaData());
					resposta.setRespostaXml(rpt.getRespostaXml());
				}
				if (rpt.getPeticioXml() != null) {
					resposta.setHiHaPeticio(true);
					resposta.setPeticioXml(rpt.getPeticioXml());
				}
				if (!consulta.isMultiple()) {
					resposta.setDadesEspecifiques(
							scspHelper.getDadesEspecifiquesPeticio(
									consulta.getScspPeticionId(),
									consulta.getScspSolicitudId()));
				}
			}
		} catch (Exception ex) {
			LOGGER.error("No s'han pogut consultar les dades de la petició (peticionId=" + consulta.getScspPeticionId() + ", solicitudId=" + consulta.getScspSolicitudId() + ")", ex);
			throw new ScspException("No s'han pogut consultar les dades de la petició (peticionId=" + consulta.getScspPeticionId() + ", solicitudId=" + consulta.getScspSolicitudId() + ")", ex);
		}
		return resposta;
	}

	@SuppressWarnings("unchecked")
	private PaginaLlistatDto<ConsultaDto> findByEntitatIUsuariFiltrePaginat(
			Entitat entitat,
			String usuariCodi,
			ConsultaFiltreDto filtre,
			PaginacioAmbOrdreDto paginacioAmbOrdre,
			boolean multiple,
			boolean nomesSensePare) throws EntitatNotFoundException {
		copiarPropertiesToDb();
		Map<String, String> mapeigPropietats = new HashMap<String, String>();
		mapeigPropietats.put("scspPeticionSolicitudId", "scspPeticionId");
		mapeigPropietats.put("creacioData", "createdDate");
		mapeigPropietats.put("procedimentNom", "procedimentServei.procediment.nom");
		mapeigPropietats.put("serveiDescripcio", "procedimentServei.servei");
		mapeigPropietats.put("titularNomSencer", "titularNom");
		mapeigPropietats.put("titularDocumentAmbTipus", "titularDocumentNum");
		mapeigPropietats.put("funcionariNomAmbDocument", "funcionariNom");
		Pageable pageable = PaginacioHelper.toSpringDataPageable(
				paginacioAmbOrdre,
				mapeigPropietats);
		Page<Consulta> paginaConsultes;
		if (filtre == null) {
			paginaConsultes = consultaRepository.findByProcedimentServeiProcedimentEntitatIdAndCreatedBy(
					entitat.getId(),
					usuariCodi == null,
					(usuariCodi != null) ? usuariRepository.findOne(usuariCodi) : null,
					multiple,
					nomesSensePare,
					pageable);
		} else {
			paginaConsultes = consultaRepository.findByCreatedByAndFiltrePaginat(
					entitat.getId(),
					usuariCodi == null,
					(usuariCodi != null) ? usuariRepository.findOne(usuariCodi) : null,
					filtre.getProcedimentId() == null,
					filtre.getProcedimentId(),
					filtre.getServeiCodi() == null || filtre.getServeiCodi().isEmpty(),
					filtre.getServeiCodi(),
					filtre.getEstat() == null,
					(filtre.getEstat() != null) ? Consulta.EstatTipus.valueOf(filtre.getEstat().toString()) : null,
					filtre.getDataInici() == null,
					filtre.getDataInici(),
					filtre.getDataFi() == null,
					configurarDataFiPerFiltre(filtre.getDataFi()),
					filtre.getTitularNom() == null || filtre.getTitularNom().isEmpty(),
					filtre.getTitularNom(),
					filtre.getTitularDocument() == null || filtre.getTitularDocument().isEmpty(),
					filtre.getTitularDocument(),
					filtre.getFuncionariNom() == null || filtre.getFuncionariNom().isEmpty(),
					filtre.getFuncionariNom(),
					filtre.getFuncionariDocument() == null || filtre.getFuncionariDocument().isEmpty(),
					filtre.getFuncionariDocument(),
					multiple,
					nomesSensePare,
					pageable);
		}
		PaginaLlistatDto<ConsultaDto> resposta = PaginacioHelper.toPaginaLlistatDto(
				paginaConsultes,
				dtoMappingHelper,
				ConsultaDto.class);
		for (ConsultaDto consulta: resposta) {
			consulta.setServeiDescripcio(
					scspHelper.getServicioDescripcion(
							consulta.getServeiCodi()));
			try {
				consulta.setHiHaPeticio(
						scspHelper.isPeticionEnviada(
								consulta.getScspPeticionId()));
			} catch (es.scsp.common.exceptions.ScspException ex) {
				LOGGER.error("No s'han pogut consultar l'enviament de la petició (id=" + consulta.getScspPeticionId() + ")", ex);
				consulta.setHiHaPeticio(false);
			}
			try {
				consulta.setTerData(scspHelper.getTerPeticion(
									consulta.getScspPeticionId()));
			} catch (es.scsp.common.exceptions.ScspException ex) {
				LOGGER.error("No s'han pogut consultar el TER de la petició (id=" + consulta.getScspPeticionId() + ")", ex);
			}
		}
		return resposta;
	}

	private ArxiuDto obtenirJustificantConsulta(
			Consulta consulta) throws Exception {
		if (JustificantEstat.PENDENT.equals(consulta.getJustificantEstat())) {
			LOGGER.error("L'estat del justificant de la consulta és PENDENT (" +
					"id=" + consulta.getId() + ")");
			throw new JustificantGeneracioException("L'estat del justificant de la consulta és PENDENT");
		}
		if (JustificantEstat.ERROR.equals(consulta.getJustificantEstat())) {
			LOGGER.error("L'estat del justificant de la consulta és ERROR (" +
					"id=" + consulta.getId() + ")");
			throw new JustificantGeneracioException("L'estat del justificant de la consulta és ERROR");
		}
		if (JustificantEstat.NO_DISPONIBLE.equals(consulta.getJustificantEstat())) {
			LOGGER.error("L'estat del justificant de la consulta és NO_DISPONIBLE (" +
					"id=" + consulta.getId() + ")");
			throw new JustificantGeneracioException("L'estat del justificant de la consulta és NO_DISPONIBLE");
		}
		String serveiCodi = consulta.getProcedimentServei().getServei();
		String peticionId = consulta.getScspPeticionId();
		String solicitudId = consulta.getScspSolicitudId();
		ServeiConfig serveiConfig = serveiConfigRepository.findByServei(serveiCodi);
		ArxiuDto arxiuDto = new ArxiuDto();
		if (serveiConfig.getJustificantTipus() != null && JustificantTipus.ADJUNT_PDF_BASE64.equals(serveiConfig.getJustificantTipus())) {
			LOGGER.debug("El justificant de la consulta (id=" + consulta.getId() + ") està inclòs a dins la resposta");
			Map<String, String> dadesEspecifiques = scspHelper.getDadesEspecifiquesResposta(
					peticionId,
					solicitudId);
			String justificantB64 = dadesEspecifiques.get(serveiConfig.getJustificantXpath());
			arxiuDto.setNom("PBL_" + peticionId + ".pdf");
			if (justificantB64 != null) {
				arxiuDto.setContingut(Base64.decode(justificantB64));
				return arxiuDto;
			} else {
				// Si no hi ha justificant a dins les dades específiques continuarà i
				// es retornarà el justificant genèric.
			}
		}
		arxiuDto.setNom(
				conversioTipusDocumentHelper.nomArxiuConvertit(
						justificantPlantillaHelper.getNomArxiuGenerat(
								consulta.getScspPeticionId(),
								consulta.getScspSolicitudId()),
						getExtensioSortida()));
		if (JustificantEstat.OK.equals(consulta.getJustificantEstat())) {
			arxiuDto.setContingut(
					pluginHelper.custodiaObtenirDocument(peticionId));
		} else if (JustificantEstat.OK_NO_CUSTODIA.equals(consulta.getJustificantEstat())) {
			arxiuDto.setContingut(
					generarJustificantAmbPlantilla(consulta));
		}
		return arxiuDto;
		/*if (consulta.isCustodiat()) {
			LOGGER.debug("El justificant per a la consulta (id=" + consulta.getId() + ") ja està custodiat");
			arxiuDto.setContingut(
					pluginHelper.custodiaObtenirDocument(peticionId));
			return arxiuDto;
		} else {
			// Genera el justificant emprant la plantilla
			byte[] justificant = generarJustificantAmbPlantilla(consulta);
			// Només signa i custòdia el document si és un PDF i si està
			// activat per paràmetre.
			if (!deshabilitarSignaturaICustodia && isSignarICustodiarJustificant()) {
				if ("pdf".equalsIgnoreCase(getExtensioSortida())) {
					LOGGER.debug("Inici del procés de signatura i custodia del justificant de la consulta (id=" + consulta.getId() + ")");
					String documentTipus = null;
					if (serveiConfig != null)
						documentTipus = serveiConfig.getCustodiaCodi();
					// Obté la URL de comprovació de signatura
					LOGGER.debug("Sol·licitud de URL per a la custòdia del justificant de la consulta (id=" + consulta.getId() + ")");
					String url = pluginHelper.custodiaObtenirUrlVerificacioDocument(peticionId);
					LOGGER.debug("Obtinguda URL per a la custòdia del justificant de la consulta (id=" + consulta.getId() + ", url=" + url + ")");
					// Signa el justificant
					LOGGER.debug("Signatura del justificant de la consulta (id=" + consulta.getId() + ")");
					ByteArrayOutputStream signedStream = new ByteArrayOutputStream();
					pluginHelper.signaturaSignarEstamparPdf(
							new ByteArrayInputStream(justificant),
							signedStream,
							url);
					// Envia el justificant a custòdia
					LOGGER.debug("Custodia del justificant de la consulta (id=" + consulta.getId() + ")");
					byte[] arxiuSignat = signedStream.toByteArray();
					pluginHelper.custodiaEnviarPdfSignat(
							peticionId,
							arxiuDto.getNom(),
							arxiuSignat,
							documentTipus);
					consulta.updateCustodiat(true);
					arxiuDto.setContingut(arxiuSignat);
				} else {
					throw new Exception("Només es poden signar i custodiar arxius PDF.");
				}
			} else {
				LOGGER.debug("Signatura i custòdia desactivada per a la consulta (id=" + consulta.getId() + ")");
				arxiuDto.setContingut(justificant);
			}
			return arxiuDto;
		}*/
	}

	private void generarCustodiarJustificant(
			Consulta consulta) throws Exception {
		String serveiCodi = consulta.getProcedimentServei().getServei();
		String peticionId = consulta.getScspPeticionId();
		ServeiConfig serveiConfig = serveiConfigRepository.findByServei(serveiCodi);
		String arxiuNom = conversioTipusDocumentHelper.nomArxiuConvertit(
				justificantPlantillaHelper.getNomArxiuGenerat(
						consulta.getScspPeticionId(),
						consulta.getScspSolicitudId()),
						getExtensioSortida());
		// Només signa i custòdia si està activat per paràmetre i 
		// si encara no està custodiat
		if (isSignarICustodiarJustificant() && !consulta.isCustodiat()) {
			/*// Genera el certificat SCSP original
			ByteArrayOutputStream baosGeneracio = scspHelper.generaJustificanteTransmision(
					documentId);*/
			// Genera el justificant emprant la plantilla
			byte[] justificant = generarJustificantAmbPlantilla(consulta);
			// Només signa i custòdia el document si és un PDF
			if ("pdf".equalsIgnoreCase(getExtensioSortida())) {
				boolean custodiat = false;
				JustificantEstat justificantEstat = JustificantEstat.PENDENT;
				String custodiaUrl = consulta.getCustodiaUrl();
				String custodiaError = null;
				try {
					LOGGER.debug("Inici del procés de signatura i custodia del justificant de la consulta (id=" + consulta.getId() + ")");
					String documentTipus = null;
					if (serveiConfig != null)
						documentTipus = serveiConfig.getCustodiaCodi();
					if (custodiaUrl == null || custodiaUrl.isEmpty()) {
						// Obté la URL de comprovació de signatura
						LOGGER.debug("Sol·licitud de URL per a la custòdia del justificant de la consulta (id=" + consulta.getId() + ")");
						custodiaUrl = pluginHelper.custodiaObtenirUrlVerificacioDocument(peticionId);
						LOGGER.debug("Obtinguda URL per a la custòdia del justificant de la consulta (id=" + consulta.getId() + ", custodiaUrl=" + custodiaUrl + ")");
					}
					// Signa el justificant
					LOGGER.debug("Signatura del justificant de la consulta (id=" + consulta.getId() + ")");
					ByteArrayOutputStream signedStream = new ByteArrayOutputStream();
					pluginHelper.signaturaSignarEstamparPdf(
							new ByteArrayInputStream(justificant),
							signedStream,
							custodiaUrl);
					// Envia el justificant a custòdia
					LOGGER.debug("Custodia del justificant de la consulta (id=" + consulta.getId() + ")");
					byte[] arxiuSignat = signedStream.toByteArray();
					pluginHelper.custodiaEnviarPdfSignat(
							peticionId,
							arxiuNom,
							arxiuSignat,
							documentTipus);
					justificantEstat = JustificantEstat.OK;
					custodiat = true;
				} catch (Exception ex) {
					justificantEstat = JustificantEstat.ERROR;
					StringWriter sw = new StringWriter();
					ex.printStackTrace(new PrintWriter(sw));
					custodiaError =  sw.toString();
				} finally {
					consulta.updateJustificantEstat(
							justificantEstat,
							custodiat,
							custodiaUrl,
							custodiaError);
				}
			} else {
				throw new Exception("Només es poden signar i custodiar arxius PDF.");
			}
		} else {
			consulta.updateJustificantEstat(
					JustificantEstat.OK_NO_CUSTODIA,
					false,
					null,
					null);
		}
	}

	private byte[] generarJustificantAmbPlantilla(
			Consulta consulta) throws Exception {
		String arxiuNom = justificantPlantillaHelper.getNomArxiuGenerat(
				consulta.getScspPeticionId(),
				consulta.getScspSolicitudId());
		String arxiuExtensio = justificantPlantillaHelper.getExtensioArxiuGenerat(
				consulta.getScspPeticionId(),
				consulta.getScspSolicitudId());
		String serveiCodi = consulta.getProcedimentServei().getServei();
		boolean convertir = !getExtensioSortida().equalsIgnoreCase(arxiuExtensio);
		ByteArrayOutputStream baosGeneracio = new ByteArrayOutputStream();
		LOGGER.debug("Generant el justificant per a la consulta (id=" + consulta.getId() + ") a partir de la plantilla");
		justificantPlantillaHelper.generar(
				scspHelper.generarArbreJustificant(
						consulta.getScspPeticionId(),
						consulta.getScspSolicitudId(),
						null),
				"[" + serveiCodi + "] " + scspHelper.getServicioDescripcion(serveiCodi),
				serveiJustificantCampRepository.findByServeiAndLocaleIdiomaAndLocaleRegio(
						serveiCodi,
						ServeiServiceImpl.DEFAULT_TRADUCCIO_LOCALE.getLanguage(),
						ServeiServiceImpl.DEFAULT_TRADUCCIO_LOCALE.getCountry()),
				null,
				baosGeneracio);
		// Converteix el document si és necessari
		if (convertir) {
			LOGGER.debug("Convertint el justificant per a la consulta (" +
					"id=" + consulta.getId() + "," +
					"extensio=" + getExtensioSortida() + ")");
			ByteArrayOutputStream baosConversio = new ByteArrayOutputStream();
			String extensioSortida = getExtensioSortida();
			conversioTipusDocumentHelper.convertir(
					arxiuNom,
					new ByteArrayInputStream(baosGeneracio.toByteArray()),
					extensioSortida,
					baosConversio);
			boolean convertirPdfa = isConvertirPdfaJustificant() && "pdf".equalsIgnoreCase(getExtensioSortida());
			if (convertirPdfa) {
				ByteArrayOutputStream baosPdfa = new ByteArrayOutputStream();
				conversioTipusDocumentHelper.convertirPdfToPdfa(
						new ByteArrayInputStream(baosConversio.toByteArray()),
						baosPdfa);
				return baosPdfa.toByteArray();
			} else {
				return baosConversio.toByteArray();
			}
		} else {
			return baosGeneracio.toByteArray();
		}
	}

	private Solicitud convertirEnSolicitud(
			ConsultaDto consulta) {
		Solicitud solicitud = new Solicitud();
		solicitud.setServeiCodi(consulta.getServeiCodi());
		Procediment procediment = procedimentRepository.findOne(consulta.getProcedimentId());
		solicitud.setProcedimentCodi(procediment.getCodi());
		solicitud.setProcedimentNom(procediment.getNom());
		solicitud.setSolicitantIdentificacio(consulta.getEntitatCif());
		solicitud.setSolicitantNom(consulta.getEntitatNom());
		solicitud.setFuncionariNom(consulta.getFuncionariNom());
		solicitud.setFuncionariNif(consulta.getFuncionariNif());
		if (consulta.getTitularDocumentTipus() != null) {
			solicitud.setTitularDocumentTipus(
					es.caib.pinbal.scsp.DocumentTipus.valueOf(
							consulta.getTitularDocumentTipus().toString()));
		}
		solicitud.setTitularDocument(consulta.getTitularDocumentNum());
		solicitud.setTitularNom(consulta.getTitularNom());
		solicitud.setTitularLlinatge1(consulta.getTitularLlinatge1());
		solicitud.setTitularLlinatge2(consulta.getTitularLlinatge2());
		solicitud.setTitularNomComplet(consulta.getTitularNomComplet());
		solicitud.setFinalitat(consulta.getFinalitat());
		solicitud.setConsentiment(
				es.caib.pinbal.scsp.Consentiment.valueOf(
						consulta.getConsentiment().toString()));
		solicitud.setUnitatTramitadora(consulta.getDepartamentNom());
		solicitud.setExpedientId(consulta.getExpedientId());
		solicitud.setDadesEspecifiquesMap(consulta.getDadesEspecifiques());
		return solicitud;
	}
	private Solicitud convertirEnSolicitud(
			Entitat entitat,
			Procediment procediment,
			String serveiCodi,
			String funcionariNom,
			String funcionariNif,
			DocumentTipus titularDocumentTipus,
			String titularDocumentNum,
			String titularNom,
			String titularLlinatge1,
			String titularLlinatge2,
			String titularNomComplet,
			String finalitat,
			Consentiment consentiment,
			String departamentNom,
			String expedientId,
			Element dadesEspecifiques) {
		Solicitud solicitud = new Solicitud();
		solicitud.setServeiCodi(serveiCodi);
		solicitud.setProcedimentCodi(procediment.getCodi());
		solicitud.setProcedimentNom(procediment.getNom());
		solicitud.setSolicitantIdentificacio(entitat.getCif());
		solicitud.setSolicitantNom(entitat.getNom());
		solicitud.setFuncionariNom(funcionariNom);
		solicitud.setFuncionariNif(funcionariNif);
		solicitud.setTitularDocumentTipus(
				es.caib.pinbal.scsp.DocumentTipus.valueOf(titularDocumentTipus.toString()));
		solicitud.setTitularDocument(titularDocumentNum);
		solicitud.setTitularNom(titularNom);
		solicitud.setTitularLlinatge1(titularLlinatge1);
		solicitud.setTitularLlinatge2(titularLlinatge2);
		solicitud.setTitularNomComplet(titularNomComplet);
		solicitud.setFinalitat(finalitat);
		solicitud.setConsentiment(
				es.caib.pinbal.scsp.Consentiment.valueOf(consentiment.toString()));
		solicitud.setUnitatTramitadora(departamentNom);
		solicitud.setExpedientId(expedientId);
		solicitud.setDadesEspecifiquesElement(dadesEspecifiques);
		return solicitud;
	}
	private List<Solicitud> convertirEnMultiplesSolicituds(
			ConsultaDto consulta) throws ParseException {
		List<ServeiCamp> serveiCamps = serveiCampRepository.findByServeiOrderByGrupOrdreAsc(
				consulta.getServeiCodi());
		List<Solicitud> solicituds = new ArrayList<Solicitud>();
		for (String[] dades: consulta.getDadesPeticioMultiple()) {
			Solicitud solicitud = new Solicitud();
			solicitud.setServeiCodi(consulta.getServeiCodi());
			Procediment procediment = procedimentRepository.findOne(consulta.getProcedimentId());
			solicitud.setProcedimentCodi(procediment.getCodi());
			solicitud.setProcedimentNom(procediment.getNom());
			solicitud.setSolicitantIdentificacio(consulta.getEntitatCif());
			solicitud.setSolicitantNom(consulta.getEntitatNom());
			solicitud.setFuncionariNom(consulta.getFuncionariNom());
			solicitud.setFuncionariNif(consulta.getFuncionariNif());
			solicitud.setFinalitat(consulta.getFinalitat());
			solicitud.setConsentiment(
					es.caib.pinbal.scsp.Consentiment.valueOf(
							consulta.getConsentiment().toString()));
			solicitud.setUnitatTramitadora(consulta.getDepartamentNom());
			//
			String titularDocumentTipus = getValorCampPeticioMultiple(
					"DatosGenericos/Titular/TipoDocumentacion",
					consulta.getCampsPeticioMultiple(),
					dades);
			if (titularDocumentTipus != null) {
				solicitud.setTitularDocumentTipus(
						es.caib.pinbal.scsp.DocumentTipus.valueOf(
								titularDocumentTipus));
			}
			solicitud.setTitularDocument(
					getValorCampPeticioMultiple(
							"DatosGenericos/Titular/Documentacion",
							consulta.getCampsPeticioMultiple(),
							dades));
			solicitud.setTitularNom(
					getValorCampPeticioMultiple(
							"DatosGenericos/Titular/Nombre",
							consulta.getCampsPeticioMultiple(),
							dades));
			solicitud.setTitularLlinatge1(
					getValorCampPeticioMultiple(
							"DatosGenericos/Titular/Apellido1",
							consulta.getCampsPeticioMultiple(),
							dades));
			solicitud.setTitularLlinatge2(
					getValorCampPeticioMultiple(
							"DatosGenericos/Titular/Apellido2",
							consulta.getCampsPeticioMultiple(),
							dades));
			solicitud.setTitularNomComplet(
					getValorCampPeticioMultiple(
							"DatosGenericos/Titular/NombreCompleto",
							consulta.getCampsPeticioMultiple(),
							dades));
			solicitud.setExpedientId(
					getValorCampPeticioMultiple(
							"DatosGenericos",
							consulta.getCampsPeticioMultiple(),
							dades));
			Map<String, String> dadesEspecifiques = getDadesEspecifiquesPeticioMultiple(
					serveiCamps,
					consulta.getCampsPeticioMultiple(),
					dades);
			processarDadesEspecifiquesSegonsCamps(
					consulta.getServeiCodi(),
					dadesEspecifiques);
			solicitud.setDadesEspecifiquesMap(dadesEspecifiques);
			solicituds.add(solicitud);
		}
		return solicituds;
	}

	private void processarDadesEspecifiquesSegonsCamps(
			String serveiCodi,
			Map<String, String> dadesEspecifiques) throws ParseException {
		SimpleDateFormat sdfComu = new SimpleDateFormat("dd/MM/yyyy");
		List<ServeiCamp> camps = serveiCampRepository.findByServeiOrderByGrupOrdreAsc(serveiCodi);
		// Conversió de format
		for (String path: dadesEspecifiques.keySet()) {
			for (ServeiCamp camp: camps) {
				if (camp.getPath().equals(path)) {
					if (camp.getTipus().equals(ServeiCampTipus.DATA)) {
						String str = dadesEspecifiques.get(path);
						if (str != null && !str.isEmpty()) {
							Date data = sdfComu.parse(str);
							SimpleDateFormat sdfCamp = null;
							if (camp.getDataFormat() != null && !camp.getDataFormat().isEmpty())
								sdfCamp = new SimpleDateFormat(camp.getDataFormat());
							else
								sdfCamp = new SimpleDateFormat("ddMMyyyy");
							dadesEspecifiques.put(path, sdfCamp.format(data));
						} else {
							dadesEspecifiques.put(path, null);
						}
					} else if (camp.getTipus().equals(ServeiCampTipus.BOOLEA)) {
						String str = dadesEspecifiques.get(path);
						if (str != null && !str.isEmpty()) {
							boolean valor =
									"true".equalsIgnoreCase(str) ||
									"on".equalsIgnoreCase(str) ||
									"yes".equalsIgnoreCase(str) ||
									"si".equalsIgnoreCase(str);
							dadesEspecifiques.put(path, valor ? "true" : "false");
						} else {
							dadesEspecifiques.put(path, null);
						}
					}
					break;
				}
			}
		}
		// Control de camps de tipus document
		for (ServeiCamp camp: camps) {
			if (camp.getTipus().equals(ServeiCampTipus.DOC_IDENT)) {
				// Elimina el tipus de document de les dades específiques
				// si no s'especifica el número de document
				String valor = dadesEspecifiques.get(camp.getPath());
				if (valor == null || valor.isEmpty()) {
					ServeiCamp campTipusDocument = camp.getCampPare();
					if (campTipusDocument != null)
						dadesEspecifiques.remove(campTipusDocument.getPath());
				}
			}
		}
	}

	private Map<String, String> getDadesEspecifiquesPeticioMultiple(
			List<ServeiCamp> serveiCamps,
			String[] camps,
			String[] dades) {
		Map<String, String> dadesEspecifiques = new HashMap<String, String>();
		for (ServeiCamp serveiCamp: serveiCamps) {
			for (int i = 0; i < camps.length; i++) {
				if (camps[i].equals(serveiCamp.getPath())) {
					dadesEspecifiques.put(
							camps[i],
							dades[i]);
					break;
				}
			}
		}
		return dadesEspecifiques;
		
	}

	private String getValorCampPeticioMultiple(
			String path,
			String[] camps,
			String[] valors) {
		String valor = null;
		for (int i = 0; i < camps.length; i++) {
			if (path.equals(camps[i])) {
				valor = valors[i];
				break;
			}
		}
		return valor;
	}

	private void updateEstatConsulta(
			Consulta consulta,
			ResultatEnviamentPeticio resultat,
			boolean logError) {
		if (resultat.isError()) {
			consulta.updateEstat(EstatTipus.Error);
			consulta.updateEstatError("[" + resultat.getErrorCodi() + "] " + resultat.getErrorDescripcio());
			if (logError) {
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				LOGGER.error("Error retornat per la consulta SCSP (id=" + consulta.getScspPeticionId() + ", servei=" + consulta.getProcedimentServei().getServei() + ", usuari=" + auth.getName() + "): " + consulta.getError());
			}
		} else if ("0001".equals(resultat.getEstatCodi())) {
			consulta.updateEstat(EstatTipus.Pendent);
		} else if ("0002".equals(resultat.getEstatCodi())) {
			consulta.updateEstat(EstatTipus.Processant);
		} else if ("0003".equals(resultat.getEstatCodi())) {
			consulta.updateEstat(EstatTipus.Tramitada);
		} else if ("0004".equals(resultat.getEstatCodi())) {
			consulta.updateEstat(EstatTipus.Processant);
		}
	}

	private Date configurarDataFiPerFiltre(Date dataFi) {
		if (dataFi == null)
			return null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(dataFi);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		return cal.getTime();
	}

	private boolean isServeiPermesPerUsuari(
			Entitat entitat,
			Procediment procediment,
			String serveiCodi) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		List<String> permesos = serveiHelper.findServeisPermesosPerUsuari(
				entitat.getId(),
				procediment.getCodi(),
				auth);
		boolean trobat = false;
		for (String servei: permesos) {
			if (servei.equals(serveiCodi)) {
				trobat = true;
				break;
			}
		}
		return trobat;
	}

	private int[] getRandomIndexesFromList(
			List<?> list,
			int numIndexes) {
		int[] indexes = new int[numIndexes];
		for (int i = 0; i < numIndexes; i++) {
			int randomNum;
			boolean existeix;
			do {
				randomNum = new Random().nextInt(list.size());
				existeix = false;
				for (int j = 0; j < i; j++) {
					if (indexes[j] == randomNum)
						existeix = true;
				}
			} while (existeix);
			indexes[i] = randomNum;
		}
		Arrays.sort(indexes);
		return indexes;
	}

	private boolean propertiesCopiades = false;
	private void copiarPropertiesToDb() {
		if (!propertiesCopiades) {
			scspHelper.copiarPropertiesToDb(
					PropertiesHelper.getProperties());
			propertiesCopiades = true;
		}
	}

	private String getExtensioSortida() {
		return PropertiesHelper.getProperties().getProperty("es.caib.pinbal.justificant.extensio.sortida");
	}
	private boolean isSignarICustodiarJustificant() {
		return PropertiesHelper.getProperties().getAsBoolean("es.caib.pinbal.justificant.signar.i.custodiar");
	}
	private boolean isConvertirPdfaJustificant() {
		return PropertiesHelper.getProperties().getAsBoolean("es.caib.pinbal.justificant.convertir.pdfa");
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ConsultaServiceImpl.class);

}
