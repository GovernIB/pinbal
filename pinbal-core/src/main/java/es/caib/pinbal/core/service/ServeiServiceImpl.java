/**
 * 
 */
package es.caib.pinbal.core.service;

import es.caib.pinbal.core.dto.ArbreDto;
import es.caib.pinbal.core.dto.ClauPrivadaDto;
import es.caib.pinbal.core.dto.ClauPublicaDto;
import es.caib.pinbal.core.dto.DadaEspecificaDto;
import es.caib.pinbal.core.dto.DadaEspecificaDto.TipusDadaComplexaEnum;
import es.caib.pinbal.core.dto.EmisorDto;
import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.FitxerDto;
import es.caib.pinbal.core.dto.NodeDto;
import es.caib.pinbal.core.dto.ProcedimentDto;
import es.caib.pinbal.core.dto.ProcedimentServeiDto;
import es.caib.pinbal.core.dto.ServeiBusDto;
import es.caib.pinbal.core.dto.ServeiCampDto;
import es.caib.pinbal.core.dto.ServeiCampDto.ServeiCampDtoTipus;
import es.caib.pinbal.core.dto.ServeiCampGrupDto;
import es.caib.pinbal.core.dto.ServeiDto;
import es.caib.pinbal.core.dto.ServeiDto.EntitatTipusDto;
import es.caib.pinbal.core.dto.ServeiDto.JustificantTipusDto;
import es.caib.pinbal.core.dto.ServeiJustificantCampDto;
import es.caib.pinbal.core.dto.ServeiXsdDto;
import es.caib.pinbal.core.dto.XsdTipusEnumDto;
import es.caib.pinbal.core.dto.regles.CampFormProperties;
import es.caib.pinbal.core.dto.regles.ServeiReglaDto;
import es.caib.pinbal.core.helper.DtoMappingHelper;
import es.caib.pinbal.core.helper.PermisosHelper;
import es.caib.pinbal.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.pinbal.core.helper.PluginHelper;
import es.caib.pinbal.core.helper.ServeiHelper;
import es.caib.pinbal.core.helper.ServeiXsdHelper;
import es.caib.pinbal.core.helper.UsuariHelper;
import es.caib.pinbal.core.model.Entitat;
import es.caib.pinbal.core.model.EntitatUsuari;
import es.caib.pinbal.core.model.Procediment;
import es.caib.pinbal.core.model.ProcedimentServei;
import es.caib.pinbal.core.model.Servei;
import es.caib.pinbal.core.model.ServeiBus;
import es.caib.pinbal.core.model.ServeiCamp;
import es.caib.pinbal.core.model.ServeiCamp.ServeiCampTipus;
import es.caib.pinbal.core.model.ServeiCampGrup;
import es.caib.pinbal.core.model.ServeiConfig;
import es.caib.pinbal.core.model.ServeiConfig.EntitatTipus;
import es.caib.pinbal.core.model.ServeiConfig.JustificantTipus;
import es.caib.pinbal.core.model.ServeiJustificantCamp;
import es.caib.pinbal.core.model.ServeiRegla;
import es.caib.pinbal.core.regles.ReglaHelper;
import es.caib.pinbal.core.repository.EntitatRepository;
import es.caib.pinbal.core.repository.EntitatServeiRepository;
import es.caib.pinbal.core.repository.EntitatUsuariRepository;
import es.caib.pinbal.core.repository.ProcedimentRepository;
import es.caib.pinbal.core.repository.ProcedimentServeiRepository;
import es.caib.pinbal.core.repository.ServeiBusRepository;
import es.caib.pinbal.core.repository.ServeiCampGrupRepository;
import es.caib.pinbal.core.repository.ServeiCampRepository;
import es.caib.pinbal.core.repository.ServeiConfigRepository;
import es.caib.pinbal.core.repository.ServeiJustificantCampRepository;
import es.caib.pinbal.core.repository.ServeiReglaRepository;
import es.caib.pinbal.core.repository.ServeiRepository;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.NotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.core.service.exception.ScspException;
import es.caib.pinbal.core.service.exception.ServeiAmbConsultesException;
import es.caib.pinbal.core.service.exception.ServeiBusNotFoundException;
import es.caib.pinbal.core.service.exception.ServeiCampGrupNotFoundException;
import es.caib.pinbal.core.service.exception.ServeiCampNotFoundException;
import es.caib.pinbal.core.service.exception.ServeiNotFoundException;
import es.caib.pinbal.scsp.ScspHelper;
import es.caib.pinbal.scsp.XmlHelper.DadesEspecifiquesNode;
import es.caib.pinbal.scsp.tree.Node;
import es.caib.pinbal.scsp.tree.Tree;
import es.scsp.common.domain.core.ClavePrivada;
import es.scsp.common.domain.core.ClavePublica;
import es.scsp.common.domain.core.EmisorCertificado;
import es.scsp.common.domain.core.Servicio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Implementació dels mètodes per a interactuar amb les funcionalitats SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class ServeiServiceImpl implements ServeiService, ApplicationContextAware, MessageSourceAware {

	public static final Locale DEFAULT_TRADUCCIO_LOCALE = new Locale("ca", "ES");

	@Resource
	private EntitatRepository entitatRepository;
	@Resource
	private EntitatUsuariRepository entitatUsuariRepository;
	@Resource
	private ProcedimentRepository procedimentRepository;
	@Resource
	private ProcedimentServeiRepository procedimentServeiRepository;
	@Resource
	private ServeiRepository serveiRepository;
	@Resource
	private ServeiCampRepository serveiCampRepository;
	@Resource
	private ServeiCampGrupRepository serveiCampGrupRepository;
	@Resource
	private ServeiConfigRepository serveiConfigRepository;
	@Resource
	private ServeiBusRepository serveiBusRepository;
	@Resource
	private ServeiJustificantCampRepository serveiJustificantCampRepository;
	@Resource
	private EntitatServeiRepository entitatServeiRepository;
	@Resource
	private ServeiReglaRepository serveiReglaRepository;

	@Resource
	private ServeiHelper serveiHelper;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private DtoMappingHelper dtoMappingHelper;
	@Resource
	private UsuariHelper usuariHelper;
	@Resource
	private ServeiXsdHelper serveiXsdHelper;
	@Resource
	private ReglaHelper reglaHelper;

	@Resource
	private MutableAclService aclService;

	private ApplicationContext applicationContext;
	private MessageSource messageSource;
	private ScspHelper scspHelper;

	@Transactional
	@Override
	public void saveActiu(String serveiCodi, boolean actiu) {
		ServeiConfig serveiConfig = serveiConfigRepository.findByServei(serveiCodi);
		if (serveiConfig == null) {
			// Si no està creat el crea
			serveiConfig = ServeiConfig.getBuilder(
					serveiCodi,
					null,
					null,
					null,
					null,
					JustificantTipus.GENERAT,
					null,
					null,
					false,
					null,
					null,
					null,
					new byte[0],
					false,
					true,
					true,
					actiu).build();
			serveiConfigRepository.save(serveiConfig);
		} else {
			serveiConfig.updateActiu(actiu);
		}
	}

	@Transactional
	@Override
	public ServeiDto save(ServeiDto servei) {
		log.debug("Guardant dades per al servicio SCSP (codi=" + servei.getCodi() + ")");
		getScspHelper().saveServicio(toServicioScsp(servei));
		ServeiConfig serveiConfig = serveiConfigRepository.findByServei(servei.getCodi());
		// Actualitza la configuració del servei
		String rolAntic = null;
		if (serveiConfig == null) {
			// Si no està creat el crea
			serveiConfig = ServeiConfig.getBuilder(
					servei.getCodi(),
					servei.getPinbalCustodiaCodi(),
					servei.getPinbalRoleName(),
					servei.getPinbalCondicioBusClass(),
					toEntitatTipus(servei.getPinbalEntitatTipus()),
					toJustificantTipus(servei.getPinbalJustificantTipus()),
					servei.getPinbalJustificantXpath(),
					servei.getAjuda(),
					servei.isActivaGestioXsd(),
					servei.getMaxPeticionsMinut(),
					servei.getFitxerAjudaNom(),
					servei.getFitxerAjudaMimeType(),
					servei.getFitxerAjudaContingut(),
					servei.isPinbalIniDadesExpecifiques(),
					servei.isPinbalAddDadesEspecifiques(),
					servei.isUseAutoClasse(),
					true).build();
			serveiConfig.setPinbalUnitatDir3FromEntitat(servei.isPinbalUnitatDir3FromEntitat());
			serveiConfigRepository.save(serveiConfig);
		} else {
			// Si ja està creat l'actualitza
			rolAntic = serveiConfig.getRoleName();
			serveiConfig.update(
					servei.getPinbalCustodiaCodi(),
					servei.getPinbalRoleName(),
					servei.getPinbalCondicioBusClass(),
					toEntitatTipus(servei.getPinbalEntitatTipus()),
					toJustificantTipus(servei.getPinbalJustificantTipus()),
					servei.getPinbalJustificantXpath(),
					servei.isPinbalPermesDocumentTipusDni(),
					servei.isPinbalPermesDocumentTipusNif(),
					servei.isPinbalPermesDocumentTipusCif(),
					servei.isPinbalPermesDocumentTipusNie(),
					servei.isPinbalPermesDocumentTipusPas(),
					servei.isPinbalActiuCampNom(),
					servei.isPinbalActiuCampLlinatge1(),
					servei.isPinbalActiuCampLlinatge2(),
					servei.isPinbalActiuCampNomComplet(),
					servei.isPinbalActiuCampDocument(),
					servei.getPinbalUnitatDir3(),
					servei.isPinbalDocumentObligatori(),
					servei.isPinbalComprovarDocument(),
					servei.isActivaGestioXsd(),
					servei.getMaxPeticionsMinut(),
					servei.getAjuda(),
					servei.isPinbalIniDadesExpecifiques(),
					servei.isPinbalAddDadesEspecifiques(),
					servei.isUseAutoClasse());
			serveiConfig.setPinbalUnitatDir3FromEntitat(servei.isPinbalUnitatDir3FromEntitat());
			if (servei.getFitxerAjudaNom() != null && !servei.getFitxerAjudaNom().isEmpty()) {
				serveiConfig.updateFitxerAjuda(
						servei.getFitxerAjudaNom(),
						servei.getFitxerAjudaMimeType(),
						servei.getFitxerAjudaContingut());
			}
		}
		// Refresca els permisos per accedir al servei
		if (rolAntic != null && !rolAntic.isEmpty()) {
			// Esborra el permis antic (si n'hi ha)
			PermisosHelper.revocarPermisRol(
					rolAntic,
					ServeiConfig.class,
					serveiConfig.getId(),
					BasePermission.READ,
					aclService);
		}
		if (serveiConfig.getRoleName() != null && !serveiConfig.getRoleName().isEmpty()) {
			// Activa el permis nou (si n'hi ha)
			PermisosHelper.assignarPermisRol(
					serveiConfig.getRoleName(),
					ServeiConfig.class,
					serveiConfig.getId(),
					BasePermission.READ,
					aclService);
		}
		return servei;
	}

	@Transactional
	@Override
	public ServeiDto delete(String serveiCodi) throws ServeiNotFoundException, ServeiAmbConsultesException {
		log.debug("Esborrant dades per al servicio SCSP (codi=" + serveiCodi + ")");
		Servicio servicio = getServicioByCode(serveiCodi);

		if (getScspHelper().servicioHasConsultes(serveiCodi)) {
			log.debug("El servicio SCSP te consultes realitzades (codi=" + serveiCodi + ")");
			throw new ServeiAmbConsultesException();
		}
		ServeiDto servei = toServeiDto(servicio);
		getScspHelper().deleteServicio(serveiCodi);
		ServeiConfig serveiConfig = serveiConfigRepository.findByServei(serveiCodi);
		if (serveiConfig != null)
			serveiConfigRepository.delete(serveiConfig);
		return servei;
	}

	@Transactional(readOnly = true)
	@Override
	public ServeiDto findAmbCodiPerAdminORepresentant(
			String serveiCodi) throws ServeiNotFoundException {
		log.debug("Obtenint informació del servicio (codi=" + serveiCodi + ") per a administrador o representant");

		Servicio servicio =  getServicioByCode(serveiCodi);
		return toServeiDto(servicio);
	}

	@Transactional(readOnly = true)
	@Override
	public ServeiDto findAmbCodiPerDelegat(
			Long entitatId,
			String serveiCodi) throws ServeiNotFoundException {
		log.debug("Obtenint informació del servicio (codi=" + serveiCodi + ") per al delegat");
		Servicio servicio = getServicioByCode(serveiCodi);
		ServeiConfig serveiConfig = serveiConfigRepository.findByServei(serveiCodi);
		if (serveiConfig != null && serveiConfig.getRoleName() != null && !serveiConfig.getRoleName().isEmpty()) {
			// Si l'accés al servei està restringit per Rol verifica els permisos
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (PermisosHelper.isGrantedAll(
					serveiConfig.getId(),
					ServeiConfig.class,
					new Permission[] {BasePermission.READ},
					aclService,
					auth)) {
				return toServeiDto(servicio);
			} else {
				log.debug("No té permisos per accedira al servicio (codi=" + serveiCodi + ")");
				throw new ServeiNotFoundException();
			}
		} else {
			// Si l'accés al servei no està restringit el retorna
			return toServeiDto(servicio);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public ServeiDto findById(Long id) {
		log.debug("Cercant el servei (id= " + id + ")");
		return dtoMappingHelper.getMapperFacade().map(
				serveiRepository.findOne(id),
				ServeiDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<ServeiDto> findActius() {
		log.debug("Cercant els servicios actius");
		List<ServeiDto> resposta = new ArrayList<ServeiDto>();
		List<Servicio> servicios = getScspHelper().findServicioAll();
		for (Servicio servicio : servicios)
			resposta.add(toServeiDto(servicio));
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public List<ServeiDto> findActius(String filtre) {
		log.debug("Cercant els servicios actius amb filtre: " + filtre);
		List<ServeiDto> resposta = new ArrayList<ServeiDto>();
		List<Servicio> servicios = getScspHelper().findServicioAll();
		for (Servicio servicio : servicios) {
			if ((filtre == null || filtre.isEmpty()) ||
					(filtre != null && !filtre.isEmpty() &&
							(servicio.getCodCertificado().toLowerCase().contains(filtre.toLowerCase()) ||
									servicio.getDescripcion().toLowerCase().contains(filtre.toLowerCase()))))
				resposta.add(toServeiDto(servicio));
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public Page<ServeiDto> findAmbFiltrePaginat(
			String codi,
			String descripcio,
			String emisor,
			Boolean activa,
			String scspVersionEsquema,
			Pageable pageable) {
		log.debug("Consulta de serveis segons filtre (codi=" + codi + ", descripcio=" + descripcio + ""
				+ "emisor=" + emisor + " activa=" + activa + ")");

		Page<Servei> paginaServeis = serveiRepository.findByFiltre(
				codi == null || codi.length() == 0,
				codi,
				descripcio == null || descripcio.length() == 0,
				descripcio,
				emisor == null || emisor.length() == 0,
				(emisor != null && emisor.length() > 0) ? Long.parseLong(emisor) : null,
				activa == null,
				activa,
				scspVersionEsquema == null || scspVersionEsquema.length() == 0,
				scspVersionEsquema,
				pageable);

		Page<ServeiDto> paginaDtos = dtoMappingHelper.pageEntities2pageDto(
				paginaServeis,
				ServeiDto.class,
				pageable);
		for (ServeiDto servei : paginaDtos.getContent()) {
			ServeiConfig serveiConfig = serveiConfigRepository.findByServei(servei.getCodi());
			if (serveiConfig != null && serveiConfig.isActiu() == true) {
				servei.setActiu(true);
			} else {
				servei.setActiu(false);
			}
		}

		return paginaDtos;
	}

	@Transactional(readOnly = true)
	@Override
	public Page<ServeiDto> findAmbFiltrePaginat(
			String codi,
			String descripcio,
			String emisor,
			Boolean activa,
			EntitatDto entitat,
			ProcedimentDto procediment,
			Pageable pageable) {
		log.debug("Consulta de serveis segons filtre (codi=" + codi + ", descripcio=" + descripcio + ""
				+ "emisor=" + emisor + " activa=" + activa + ")");
		List<String> serveisEntitat = entitatServeiRepository.findServeisByEntitatId(entitat.getId());
		List<ProcedimentServei> serveisProcediment = procedimentServeiRepository.findServeisProcediment(
				entitatRepository.findByCodi(entitat.getCodi()),
				procedimentRepository.findOne(procediment.getId())
		);
		List<String> serveisProcedimentActiusIds = procedimentServeiRepository.findServeisProcedimenActiustServeisIds(
				entitatRepository.findByCodi(entitat.getCodi()),
				procedimentRepository.findOne(procediment.getId())
		);
		if (serveisProcedimentActiusIds != null && serveisProcedimentActiusIds.isEmpty()) {
			serveisProcedimentActiusIds = null;
		}
		Page<Servei> paginaServeis = serveiRepository.findByFiltre(
				serveisEntitat,
				serveisProcedimentActiusIds,
				codi == null || codi.length() == 0,
				codi,
				descripcio == null || descripcio.length() == 0,
				descripcio,
				emisor == null || emisor.length() == 0,
				(emisor != null && emisor.length() > 0) ? Long.parseLong(emisor) : null,
				activa == null,
				activa,
				pageable
		);

		Page<ServeiDto> paginaDtos = dtoMappingHelper.pageEntities2pageDto(paginaServeis, ServeiDto.class, pageable);
		for (ServeiDto servei: paginaDtos.getContent()) {
			for (ProcedimentServei procedimentServei: serveisProcediment) {
				if (servei.getCodi().equals(procedimentServei.getServei())) {
					servei.setActiu(procedimentServei.isActiu());
					servei.setProcedimentCodi(procedimentServei.getProcediment().getCodi());
					break;
				}
			}
		}
		return paginaDtos;
	}

	@Transactional(readOnly = true)
	@Override
	public List<ServeiDto> findAmbEntitat(Long entitatId)
			throws EntitatNotFoundException {
		log.debug("Cercant els servicios actius per a l'entitat (id="
				+ entitatId + ")");
		Entitat entitat = entitatRepository.findOne(entitatId);
		if (entitat == null) {
			log.debug("No s'ha trobat l'entitat (id=" + entitatId + ")");
			throw new EntitatNotFoundException();
		}
		List<ServeiDto> resposta = new ArrayList<ServeiDto>();
		List<Servicio> servicios = getScspHelper().findServicioAll();
		for (Servicio servicio : servicios) {
			boolean trobat = false;
			for (String servei : entitat.getServeis()) {
				if (servei.equals(servicio.getCodCertificado())) {
					trobat = true;
					break;
				}
			}
			if (trobat)
				resposta.add(toServeiDto(servicio));
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public List<ServeiDto> findAmbEntitat(Long entitatId, String filtre)
			throws EntitatNotFoundException {
		log.debug("Cercant els servicios actius per a l'entitat (id="
				+ entitatId + ") y filtre: " + filtre);
		Entitat entitat = entitatRepository.findOne(entitatId);
		if (entitat == null) {
			log.debug("No s'ha trobat l'entitat (id=" + entitatId + ")");
			throw new EntitatNotFoundException();
		}
		List<ServeiDto> resposta = new ArrayList<ServeiDto>();
		List<Servicio> servicios = getScspHelper().findServicioAll();
		for (Servicio servicio : servicios) {
			boolean trobat = false;
			for (String servei : entitat.getServeis()) {
				if (servei.equals(servicio.getCodCertificado()) &&
						((filtre == null || filtre.isEmpty()) ||
								(filtre != null && !filtre.isEmpty() &&
										(servicio.getCodCertificado().toLowerCase().contains(filtre.toLowerCase()) ||
												servicio.getDescripcion().toLowerCase().contains(filtre.toLowerCase()))))) {
					trobat = true;
					break;
				}
			}
			if (trobat)
				resposta.add(toServeiDto(servicio));
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public List<ServeiDto> findAmbEntitatIProcediment(
			Long entitatId,
			Long procedimentId) throws EntitatNotFoundException, ProcedimentNotFoundException {
		log.debug("Cercant els servicios (entitatId=" + entitatId + ", procedimentId=" + procedimentId + ")");
		Entitat entitat = entitatRepository.findOne(entitatId);
		if (entitat == null) {
			log.debug("No s'ha trobat l'entitat (id=" + entitatId + ")");
			throw new EntitatNotFoundException();
		}
		Procediment procediment = procedimentRepository.findOne(procedimentId);
		if (procediment == null) {
			log.debug("No s'ha trobat el procediment (id=" + procedimentId + ")");
			throw new ProcedimentNotFoundException();
		}
		List<ProcedimentServei> procedimentServeis = procedimentServeiRepository.findByEntitatIdAndProcedimentId(
				entitatId,
				procedimentId);
		List<ServeiDto> resposta = new ArrayList<ServeiDto>();
		List<Servicio> servicios = getScspHelper().findServicioAll();
		for (Servicio servicio : servicios) {
			boolean trobat = false;
			for (ProcedimentServei procedimentServei : procedimentServeis) {
				if (procedimentServei.getServei().equals(servicio.getCodCertificado())) {
					trobat = true;
					break;
				}
			}
			if (trobat)
				resposta.add(toServeiDto(servicio));
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public List<ServeiDto> findAmbProcediment(
			Long procedimentId) throws ProcedimentNotFoundException {
		log.debug("Cercant els servicios (procedimentId=" + procedimentId + ")");
		Procediment procediment = procedimentRepository.findOne(procedimentId);
		if (procediment == null) {
			log.debug("No s'ha trobat el procediment (id=" + procedimentId + ")");
			throw new ProcedimentNotFoundException();
		}
		List<ProcedimentServei> procedimentServeis = procedimentServeiRepository.findByProcedimentId(
				procedimentId);
		List<ServeiDto> resposta = new ArrayList<ServeiDto>();
		List<Servicio> servicios = getScspHelper().findServicioAll();
		for (Servicio servicio : servicios) {
			boolean trobat = false;
			for (ProcedimentServei procedimentServei : procedimentServeis) {
				if (procedimentServei.getServei().equals(servicio.getCodCertificado())) {
					trobat = true;
					break;
				}
			}
			if (trobat)
				resposta.add(toServeiDto(servicio));
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public List<ServeiDto> findAmbEntitatIProcediment(
			Long entitatId,
			Long procedimentId,
			String filtre) throws EntitatNotFoundException, ProcedimentNotFoundException {
		log.debug("Cercant els servicios (entitatId=" + entitatId + ", procedimentId=" + procedimentId + ", filtre=" + filtre + "\")");
		Entitat entitat = entitatRepository.findOne(entitatId);
		if (entitat == null) {
			log.debug("No s'ha trobat l'entitat (id=" + entitatId + ")");
			throw new EntitatNotFoundException();
		}
		Procediment procediment = procedimentRepository.findOne(procedimentId);
		if (procediment == null) {
			log.debug("No s'ha trobat el procediment (id=" + procedimentId + ")");
			throw new ProcedimentNotFoundException();
		}
		List<ProcedimentServei> procedimentServeis = procedimentServeiRepository.findByEntitatIdAndProcedimentId(
				entitatId,
				procedimentId);
		List<ServeiDto> resposta = new ArrayList<ServeiDto>();
		List<Servicio> servicios = getScspHelper().findServicioAll();
		for (Servicio servicio : servicios) {
			boolean trobat = false;
			for (ProcedimentServei procedimentServei : procedimentServeis) {
				if (procedimentServei.getServei().equals(servicio.getCodCertificado()) &&
						((filtre == null || filtre.isEmpty()) ||
								(filtre != null && !filtre.isEmpty() &&
										(servicio.getCodCertificado().toLowerCase().contains(filtre.toLowerCase()) ||
												servicio.getDescripcion().toLowerCase().contains(filtre.toLowerCase()))))) {
					trobat = true;
					break;
				}
			}
			if (trobat)
				resposta.add(toServeiDto(servicio));
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public List<ProcedimentServeiDto> findPermesosAmbEntitatIUsuari(
			Long entitatId,
			String usuariCodi) throws EntitatNotFoundException {
		log.debug("Cercant serveis permesos per l'usuari (" +
				"entitatId=" + entitatId + ", " +
				"usuariCodi=" + usuariCodi + ")");
		Entitat entitat = entitatRepository.findOne(entitatId);
		if (entitat == null) {
			log.debug("No s'ha trobat l'entitat (id=" + entitatId + ")");
			throw new EntitatNotFoundException();
		}
		List<ProcedimentServei> procedimentServeis = procedimentServeiRepository.findByEntitatId(entitatId);
		PermisosHelper.filterGrantedAll(
				procedimentServeis,
				new ObjectIdentifierExtractor<ProcedimentServei>() {
					public Long getObjectIdentifier(ProcedimentServei object) {
						return object.getId();
					}
				},
				ProcedimentServei.class,
				new Permission[] {BasePermission.READ},
				aclService,
				usuariHelper.generarUsuariAutenticat(
						usuariCodi,
						false));
		List<ProcedimentServeiDto> resposta = new ArrayList<ProcedimentServeiDto>();
		for (ProcedimentServei procedimentServei: procedimentServeis) {
			ProcedimentServeiDto psd = new ProcedimentServeiDto();
			psd.setProcediment(
					dtoMappingHelper.getMapperFacade().map(
							procedimentServei.getProcediment(),
							ProcedimentDto.class));
			psd.setServei(
					toServeiDto(
							getScspHelper().getServicio(
									procedimentServei.getServei())));
			resposta.add(psd);
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public Integer countPermesosAmbEntitatIUsuari(Long entitatId, String usuariCodi) {
		List<ProcedimentServei> procedimentServeis = procedimentServeiRepository.findByEntitatId(entitatId);
		if (procedimentServeis == null || procedimentServeis.isEmpty()) {
			return 0;
		}
		PermisosHelper.filterGrantedAll(
				procedimentServeis,
				new ObjectIdentifierExtractor<ProcedimentServei>() {
					public Long getObjectIdentifier(ProcedimentServei object) {
						return object.getId();
					}
				},
				ProcedimentServei.class,
				new Permission[] {BasePermission.READ},
				aclService,
				usuariHelper.generarUsuariAutenticat(
						usuariCodi,
						false));
		return procedimentServeis.size();
	}

	@Transactional(readOnly = true)
	@Override
	public List<ServeiDto> findPermesosAmbProcedimentPerDelegat(
			Long entitatId,
			Long procedimentId) throws EntitatNotFoundException, ProcedimentNotFoundException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		log.debug("Cercant serveis permesos pel delegat (entitatId=" + entitatId + ", procedimentId=" + procedimentId + ", usuariCodi=" + auth.getName() + ")");
		// long t0 = System.currentTimeMillis();
		Entitat entitat = entitatRepository.findOne(entitatId);
		if (entitat == null) {
			log.debug("No s'ha trobat l'entitat (id=" + entitatId + ")");
			throw new EntitatNotFoundException();
		}
		// System.out.println(">>> 0 (" + (System.currentTimeMillis() - t0) + "ms)");
		// t0 = System.currentTimeMillis();
		Procediment procediment = null;
		if (procedimentId != null) {
			procediment = procedimentRepository.findOne(procedimentId);
			if (procediment == null)
				throw new ProcedimentNotFoundException();
		}
		// System.out.println(">>> 1 (" + (System.currentTimeMillis() - t0) + "ms)");
		// t0 = System.currentTimeMillis();
		EntitatUsuari entitatUsuari = entitatUsuariRepository.findByEntitatIdAndUsuariCodi(
				entitat.getId(),
				auth.getName());
		if (entitatUsuari != null && entitatUsuari.isDelegat() && entitatUsuari.isActiu()) {
			// System.out.println(">>> 2 (" + (System.currentTimeMillis() - t0) + "ms)");
			// t0 = System.currentTimeMillis();
			List<String> permesos = serveiHelper.findServeisPermesosPerUsuari(
					entitat.getId(),
					(procediment != null) ? procediment.getCodi() : null,
					auth);
			// System.out.println(">>> 3 (" + (System.currentTimeMillis() - t0) + "ms)");
			// t0 = System.currentTimeMillis();
			List<ServeiDto> resposta = new ArrayList<ServeiDto>();
			for (String servei: permesos) {
				resposta.add(toServeiDto(getScspHelper().getServicio(servei)));
			}
			// System.out.println(">>> 4 (" + (System.currentTimeMillis() - t0) + "ms)");
			return resposta;
		} else {
			return new ArrayList<ServeiDto>();
		}
	}

	@Transactional(readOnly = true)
	@Override
	public List<EmisorDto> findEmisorAll() {
		log.debug("Obtenint llistat d'emisors SCSP");
		List<EmisorDto> resposta = new ArrayList<EmisorDto>();
		for (EmisorCertificado emisor: getScspHelper().findEmisorCertificadoAll()) {
			EmisorDto dto = new EmisorDto();
			dto.setId(emisor.getId());
			dto.setNom(emisor.getNombre());
			dto.setCif(emisor.getCif());
			resposta.add(dto);
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public List<ClauPublicaDto> findClauPublicaAll() {
		log.debug("Obtenint llistat de claus públiques SCSP");
		List<ClauPublicaDto> resposta = new ArrayList<ClauPublicaDto>();
		for (ClavePublica clavePublica: getScspHelper().findClavePublicaAll()) {
			if (clavePublica != null) {
				ClauPublicaDto dto = new ClauPublicaDto();
				dto.setAlies(clavePublica.getAlias());
				dto.setNom(clavePublica.getNombre());
				dto.setNumSerie(clavePublica.getNumeroSerie());
				resposta.add(dto);
			}
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public List<ClauPrivadaDto> findClauPrivadaAll() {
		log.debug("Obtenint llistat de claus privades SCSP");
		List<ClauPrivadaDto> resposta = new ArrayList<ClauPrivadaDto>();
		for (ClavePrivada clavePrivada: getScspHelper().findClavePrivadaAll()) {
			if (clavePrivada != null) {
				ClauPrivadaDto dto = new ClauPrivadaDto();
				dto.setAlies(clavePrivada.getAlias());
				dto.setNom(clavePrivada.getNombre());
				dto.setNumSerie(clavePrivada.getNumeroSerie());
				resposta.add(dto);
			}
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public ArbreDto<DadaEspecificaDto> generarArbreDadesEspecifiques(
			String serveiCodi) throws ServeiNotFoundException, ScspException {
		log.debug("Generant arbre de dades específiques per al servei (codi=" + serveiCodi + ")");
		Servicio servicio = getServicioByCode(serveiCodi);
		if (servicio == null) {
			log.debug("No s'ha trobat el servicio (codi=" + serveiCodi + ")");
			throw new ServeiNotFoundException();
		}
		try {
			ArbreDto<DadaEspecificaDto> arbre = new ArbreDto<DadaEspecificaDto>();
			Tree<DadesEspecifiquesNode> tree = getScspHelper().generarArbreDadesEspecifiques(
					serveiCodi,
					isGestioXsdActiva(serveiCodi));
			//Tree<DadesEspecifiquesNode> tree = getScspHelper().generarArbreDadesEspecifiques(serveiCodi);
			if (tree != null && tree.getRootElement() != null) {
				NodeDto<DadaEspecificaDto> arrel = new NodeDto<DadaEspecificaDto>();
				copiarArbreDadesEspecifiques(
						tree.getRootElement(),
						arrel,
						new ArrayList<String>());
				arbre.setArrel(arrel);
			}
			return arbre;
		} catch (Exception ex) {
			log.error(
					"Error al generar arbre de dades específiques per al servei (codi=" + serveiCodi + ")",
					ex);
			throw new ScspException(
					"Error al generar arbre de dades específiques per al servei (codi=" + serveiCodi + ")",
					ex);
		}
	}

    /*@Transactional(readOnly = true)
	@Override
	public ArbreDto<DadaEspecificaDto> generarArbreDadesEspecifiques(
			String serveiCodi,
			boolean gestioXsdActiva) throws ServeiNotFoundException, ScspException {
		log.debug("Generant arbre de dades específiques per al servei (codi=" + serveiCodi + ")");
		Servicio servicio = getServicioByCode(serveiCodi);
		if (servicio == null) {
			log.debug("No s'ha trobat el servicio (codi=" + serveiCodi + ")");
			throw new ServeiNotFoundException();
		}
		try {
			ArbreDto<DadaEspecificaDto> arbre = new ArbreDto<DadaEspecificaDto>();
			Tree<DadesEspecifiquesNode> tree;
			if (gestioXsdActiva) {
				tree = getScspHelper().generarArbreDadesEspecifiques(serveiCodi, gestioXsdActiva);
			} else {
				tree = getScspHelper().generarArbreDadesEspecifiques(serveiCodi);
			}
			if (tree != null && tree.getRootElement() != null) {
				NodeDto<DadaEspecificaDto> arrel = new NodeDto<DadaEspecificaDto>();
				copiarArbreDadesEspecifiques(
						tree.getRootElement(),
						arrel,
						new ArrayList<String>());
				arbre.setArrel(arrel);
			}
			return arbre;
		} catch (Exception ex) {
			log.error(
					"Error al generar arbre de dades específiques per al servei (codi=" + serveiCodi + ")",
					ex);
			throw new ScspException(
					"Error al generar arbre de dades específiques per al servei (codi=" + serveiCodi + ")",
					ex);
		}
	}*/

	@Transactional(rollbackFor = ServeiNotFoundException.class)
	@Override
	public ServeiCampDto createServeiCamp(
			String serveiCodi,
			String path) throws ServeiNotFoundException {
		log.debug("Creant camp per al servei (codi=" + serveiCodi + ")");
		Servicio servicio = getServicioByCode(serveiCodi);
		if (servicio == null) {
			log.debug("No s'ha trobat el servicio (codi=" + serveiCodi + ")");
			throw new ServeiNotFoundException();
		}
		List<ServeiCamp> camps = serveiCampRepository.findByServeiAndGrupOrderByOrdreAsc(
				serveiCodi,
				true,
				null);
		ServeiCamp serveiCamp = null;
		for (ServeiCamp camp: camps) {
			if (camp.getPath().equals(path)) {
				serveiCamp = camp;
				break;
			}
		}
		// Només crea el camp si no existeix.
		// Si el camp ja està creat no fa res.
		if (serveiCamp == null) {
			serveiCamp = ServeiCamp.getBuilder(
					serveiCodi,
					path,
					camps.size()).build();
			if (esServeiCampEnum(serveiCodi, path)) {
				serveiCamp.updateTipus(ServeiCampTipus.ENUM);
				// Configura les descripcions de les opcions del enumerat
				// amb els valor per defecte.
				try {
					Tree<DadesEspecifiquesNode> tree = getScspHelper().generarArbreDadesEspecifiques(
							serveiCodi,
							isGestioXsdActiva(serveiCodi));
					DadesEspecifiquesNode nodeEnum = trobarNodeAmbPath(
							tree.getRootElement(),
							path,
							new ArrayList<String>());
					serveiCamp.updateEnumDescripcions(
							nodeEnum.getEnumValues().toArray(new String[nodeEnum.getEnumValues().size()]));
				} catch (Exception ex) {
					log.error(
							"Error al generar arbre de dades específiques per al servei (codi=" + serveiCodi + ")",
							ex);
				}
			}
		}
		return dtoMappingHelper.getMapperFacade().map(
				serveiCampRepository.save(serveiCamp),
				ServeiCampDto.class);
	}

	@Transactional(rollbackFor = ServeiCampNotFoundException.class)
	@Override
	public ServeiCampDto updateServeiCamp(
			ServeiCampDto modificat) throws ServeiCampNotFoundException {
		log.debug("Modificant el camp (id=" + modificat.getId() + ") del servei");
		ServeiCamp serveiCamp = serveiCampRepository.findOne(modificat.getId());
		if (serveiCamp == null) {
			log.debug("No s'ha trobat el camp (id=" + modificat.getId() + ") del servei");
			throw new ServeiCampNotFoundException();
		}
		ServeiCamp campPare = null;
		if (modificat.getCampPare() != null) {
			campPare = serveiCampRepository.findOne(
					modificat.getCampPare().getId());
		}
		ServeiCamp validacioDataCmpCamp2 = null;
		if (modificat.getValidacioDataCmpCamp2() != null) {
			validacioDataCmpCamp2 = serveiCampRepository.findOne(
					modificat.getValidacioDataCmpCamp2().getId());
		}
		serveiCamp.update(
				toServeiTipus(modificat.getTipus()),
				modificat.getEtiqueta(),
				modificat.getValorPerDefecte(),
				modificat.getComentari(),
				modificat.getEnumDescripcions(),
				modificat.getDataFormat(),
				campPare,
				modificat.getValorPare(),
				serveiCamp.getGrup(),
				modificat.isInicialitzar(),
				modificat.isObligatori(),
				modificat.isModificable(),
				modificat.isVisible(),
				modificat.getValidacioRegexp(),
				modificat.getValidacioMin(),
				modificat.getValidacioMax(),
				modificat.getValidacioDataCmpOperacio(),
				validacioDataCmpCamp2,
				modificat.getValidacioDataCmpNombre(),
				modificat.getValidacioDataCmpTipus());
		return dtoMappingHelper.getMapperFacade().map(
				serveiCamp,
				ServeiCampDto.class);
	}

	@Transactional(rollbackFor = ServeiCampNotFoundException.class)
	@Override
	public ServeiCampDto deleteServeiCamp(Long serveiCampId) throws ServeiCampNotFoundException {
		log.debug("Esborrant el camp (id=" + serveiCampId + ") del servei");
		ServeiCamp perEsborrar = serveiCampRepository.findOne(serveiCampId);
		if (perEsborrar == null) {
			log.debug("No s'ha trobat el camp (id=" + serveiCampId + ") del servei");
			throw new ServeiCampNotFoundException();
		}
		// Si te camps fills esborra la dependència de cada fill del
		// camp que volem esborrar
		for (ServeiCamp fill: perEsborrar.getCampsFills())
			fill.deleteCampPare();
		// Esborra el camp
		serveiCampRepository.delete(perEsborrar);
		// Reordena els altres camps del servei
		List<ServeiCamp> serveis = serveiCampRepository.findByServeiAndGrupOrderByOrdreAsc(
				perEsborrar.getServei(),
				perEsborrar.getGrup() == null,
				perEsborrar.getGrup());
		for (int i = 0; i < serveis.size(); i++)
			serveis.get(i).updateOrdre(i);
		return dtoMappingHelper.getMapperFacade().map(
				perEsborrar,
				ServeiCampDto.class);
	}

	@Transactional(rollbackFor = ServeiCampNotFoundException.class)
	@Override
	public void moveServeiCamp(
			String serveiCodi,
			Long serveiCampId,
			int indexDesti) throws ServeiCampNotFoundException {
		log.debug("Movent el camp del servei (codi=" + serveiCodi + ", serveiCampId=" + serveiCampId + ", " + indexDesti + ")");
		ServeiCamp perMoure = serveiCampRepository.findOne(serveiCampId);
		if (perMoure == null) {
			log.debug("No s'ha trobat el camp (id=" + serveiCampId + ") del servei");
			throw new ServeiCampNotFoundException();
		}
		List<ServeiCamp> camps = serveiCampRepository.findByServeiAndGrupOrderByOrdreAsc(
				serveiCodi,
				perMoure.getGrup() == null,
				perMoure.getGrup());
		int indexOrigen = perMoure.getOrdre();
		if (indexOrigen >= camps.size()) {
			log.debug("No s'ha trobat el camp (index=" + indexOrigen + ") del servei (codi=" + serveiCodi + ")");
			throw new ServeiCampNotFoundException();
		}
		if (indexOrigen != indexDesti) {
			camps.add(indexDesti, camps.remove(indexOrigen));
			for (int i = 0; i < camps.size(); i++)
				camps.get(i).updateOrdre(i);
		}
	}

	@Transactional(rollbackFor = {ServeiCampNotFoundException.class, ServeiCampGrupNotFoundException.class})
	@Override
	public void agrupaServeiCamp(
			Long serveiCampId,
			Long serveiCampGrupId) throws ServeiCampNotFoundException, ServeiCampGrupNotFoundException {
		log.debug("Agrupant el camp (serveiCampId=" + serveiCampId + ", serveiCampGrupId=" + serveiCampGrupId + ")");
		ServeiCamp perAgrupar = serveiCampRepository.findOne(serveiCampId);
		if (perAgrupar == null) {
			log.debug("No s'ha trobat el camp (id=" + serveiCampId + ")");
			throw new ServeiCampNotFoundException();
		}
		ServeiCampGrup grupOrigen = perAgrupar.getGrup();
		// Canvia el grup
		ServeiCampGrup grupDesti = null;
		if (serveiCampGrupId != null) {
			grupDesti = serveiCampGrupRepository.findOne(serveiCampGrupId);
			if (grupDesti == null) {
				log.debug("No s'ha trobat el grup (id=" + serveiCampGrupId + ")");
				throw new ServeiCampGrupNotFoundException();
			}
		}
		List<ServeiCamp> campsGrupDesti = serveiCampRepository.findByServeiAndGrupOrderByOrdreAsc(
				perAgrupar.getServei(),
				grupDesti == null,
				grupDesti);
		perAgrupar.updateGrup(grupDesti);
		perAgrupar.updateOrdre(campsGrupDesti.size());
		// Reordena el grup d'origen
		List<ServeiCamp> campsGrupOrigen = serveiCampRepository.findByServeiAndGrupOrderByOrdreAsc(
				perAgrupar.getServei(),
				grupOrigen == null,
				grupOrigen);
		for (int i = 0; i < campsGrupOrigen.size(); i++)
			campsGrupOrigen.get(i).updateOrdre(i);
	}

	@Transactional(readOnly = true)
	@Override
	public List<ServeiCampDto> findServeiCamps(String serveiCodi) throws ServeiNotFoundException {
		log.debug("Cercant els camps pel servicio (codi=" + serveiCodi + ")");
		Servicio servicio = getServicioByCode(serveiCodi);
		if (servicio == null) {
			log.debug("No s'ha trobat el servicio (codi=" + serveiCodi + ")");
			throw new ServeiNotFoundException();
		}
		List<ServeiCamp> camps = serveiCampRepository.findByServeiOrderByGrupOrdreAsc(serveiCodi);
		return dtoMappingHelper.getMapperFacade().mapAsList(
				camps,
				ServeiCampDto.class);
	}

	@Transactional(rollbackFor = ServeiNotFoundException.class)
	@Override
	public ServeiCampGrupDto createServeiCampGrup(ServeiCampGrupDto serveiCampGrup) throws ServeiNotFoundException {
		log.debug("Creant nou grup de camps (serveiCodi=" + serveiCampGrup.getServei() + ", nom=" + serveiCampGrup.getNom() + ")");
		Servicio servicio = getScspHelper().getServicio(serveiCampGrup.getServei());
		if (servicio == null) {
			log.debug("No s'ha trobat el servicio (codi=" + serveiCampGrup.getServei() + ")");
			throw new ServeiNotFoundException();
		}
		ServeiCampGrup pare = null;
		if (serveiCampGrup.getPareId() != null) {
			pare = serveiCampGrupRepository.findOne(serveiCampGrup.getPareId());
		}
		int grupsExistents = serveiCampGrupRepository.countByServeiAndPareOrderByOrdreAsc(
				serveiCampGrup.getServei(),
				pare == null,
				pare);
		ServeiCampGrup perCrear = ServeiCampGrup.getBuilder(
				serveiCampGrup.getServei(),
				pare,
				serveiCampGrup.getNom(),
				serveiCampGrup.getAjuda(),
				grupsExistents).build();
		return dtoMappingHelper.getMapperFacade().map(
				serveiCampGrupRepository.save(perCrear),
				ServeiCampGrupDto.class);
	}

	@Transactional(rollbackFor = ServeiCampGrupNotFoundException.class)
	@Override
	public ServeiCampGrupDto updateServeiCampGrup(ServeiCampGrupDto serveiCampGrup) throws ServeiCampGrupNotFoundException {
		log.debug("Modificant grup de camps (id=" + serveiCampGrup.getId() + ")");
		ServeiCampGrup perModificar = serveiCampGrupRepository.findOne(
				serveiCampGrup.getId());
		if (perModificar == null) {
			log.debug("No s'ha trobat el grup de camps (id=" + serveiCampGrup.getId() + ")");
			throw new ServeiCampGrupNotFoundException();
		}
		perModificar.update(serveiCampGrup.getNom(), serveiCampGrup.getAjuda());
		return dtoMappingHelper.getMapperFacade().map(
				perModificar,
				ServeiCampGrupDto.class);
	}

	@Transactional(rollbackFor = ServeiCampGrupNotFoundException.class)
	@Override
	public ServeiCampGrupDto deleteServeiCampGrup(Long serveiCampGrupId) throws ServeiCampGrupNotFoundException {
		log.debug("Esborrant grup de camps (id=" + serveiCampGrupId + ")");
		ServeiCampGrup perEsborrar = serveiCampGrupRepository.findOne(
				serveiCampGrupId);
		if (perEsborrar == null) {
			log.debug("No s'ha trobat el grup de camps (id=" + serveiCampGrupId + ")");
			throw new ServeiCampGrupNotFoundException();
		}
		// Deslliga els camps del grup per a evitar esborrar-los, i reordena
		int index = serveiCampRepository.countByServeiAndGrupOrderByOrdreAsc(
				perEsborrar.getServei(),
				perEsborrar.getPare() == null,
				perEsborrar.getPare());
		for (ServeiCamp camp: perEsborrar.getCamps()) {
			camp.updateGrup(perEsborrar.getPare());
			camp.updateOrdre(index++);
		}
		perEsborrar.getCamps().clear();
		perEsborrar.updatePare(null);
		serveiCampGrupRepository.flush();
		// Esborra el grup
		serveiCampGrupRepository.deleteById(perEsborrar.getId());
		return dtoMappingHelper.getMapperFacade().map(
				perEsborrar,
				ServeiCampGrupDto.class);
	}

	@Transactional(rollbackFor = ServeiCampGrupNotFoundException.class)
	@Override
	public void moveServeiCampGrup(
			Long serveiCampGrupId,
			boolean up) throws ServeiCampGrupNotFoundException {
		log.debug("Movent grup de camps (id=" + serveiCampGrupId + ", up=" + up + ")");
		ServeiCampGrup perMoure = serveiCampGrupRepository.findOne(
				serveiCampGrupId);
		if (perMoure == null) {
			log.debug("No s'ha trobat el grup de camps (id=" + serveiCampGrupId + ")");
			throw new ServeiCampGrupNotFoundException();
		}
		List<ServeiCampGrup> grups = serveiCampGrupRepository.findByServeiAndPareOrderByOrdreAsc(
				perMoure.getServei(),
				perMoure.getPare() == null,
				perMoure.getPare());
		if (up && perMoure.getOrdre() > 0) {
			grups.get(perMoure.getOrdre() - 1).updateOrdre(perMoure.getOrdre());
			perMoure.updateOrdre(perMoure.getOrdre() - 1);
		}
		if (!up && perMoure.getOrdre() < grups.size() - 1) {
			grups.get(perMoure.getOrdre() + 1).updateOrdre(perMoure.getOrdre());
			perMoure.updateOrdre(perMoure.getOrdre() + 1);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public List<ServeiCampGrupDto> findServeiCampGrups(String serveiCodi) throws ServeiNotFoundException {
		log.debug("Cercant els grups de camps pel servicio (codi=" + serveiCodi + ")");
		Servicio servicio = getServicioByCode(serveiCodi);
		if (servicio == null) {
			log.debug("No s'ha trobat el servicio (codi=" + serveiCodi + ")");
			throw new ServeiNotFoundException();
		}
		List<ServeiCampGrup> serveiCampGrups = serveiCampGrupRepository.findByServeiAndPareIsNullOrderByOrdreAsc(serveiCodi);
		return dtoMappingHelper.getMapperFacade().mapAsList(
				serveiCampGrups,
				ServeiCampGrupDto.class);
	}

	@Transactional(readOnly = true)
    @Override
    public ServeiCampGrupDto serveiCampGrupFindByNom(Long serveiId, String nom) {
		return dtoMappingHelper.getMapperFacade().map(
				serveiCampGrupRepository.findByNom(serveiId, nom),
				ServeiCampGrupDto.class);
    }

    @Transactional(rollbackFor = ServeiNotFoundException.class)
	@Override
	public ServeiBusDto createServeiBus(
			ServeiBusDto creat) throws ServeiNotFoundException, EntitatNotFoundException {
		log.debug("Creant redirecció del bus pel servicio (codi=" + creat.getServei() + ")");
		Servicio servicio = getScspHelper().getServicio(creat.getServei());
		if (servicio == null) {
			log.debug("No s'ha trobat el servicio (codi=" + creat.getServei() + ")");
			throw new ServeiNotFoundException();
		}
		Entitat entitat = entitatRepository.findOne(creat.getEntitat().getId());
		if (entitat == null) {
			log.debug("No s'ha trobat l'entitat (codi=" + creat.getEntitat().getId() + ")");
			throw new EntitatNotFoundException();
		}
		ServeiBus serveiBus = ServeiBus.getBuilder(
				creat.getServei(),
				creat.getUrlDesti(),
				entitat).build();
		return dtoMappingHelper.getMapperFacade().map(
				serveiBusRepository.save(serveiBus),
				ServeiBusDto.class);
	}

	@Transactional(rollbackFor = ServeiBusNotFoundException.class)
	@Override
	public ServeiBusDto updateServeiBus(
			ServeiBusDto modificat) throws ServeiBusNotFoundException, EntitatNotFoundException {
		log.debug("Modificant redirecció del bus pel servicio (codi=" + modificat.getServei() + ")");
		ServeiBus serveiBus = serveiBusRepository.findOne(modificat.getId());
		if (serveiBus == null) {
			log.debug("No s'ha trobat la redirecció del bus (id=" + modificat.getId() + ")");
			throw new ServeiBusNotFoundException();
		}
		Entitat entitat = entitatRepository.findOne(modificat.getEntitat().getId());
		if (entitat == null) {
			log.debug("No s'ha trobat l'entitat (codi=" + modificat.getEntitat().getId() + ")");
			throw new EntitatNotFoundException();
		}
		serveiBus.update(
				modificat.getUrlDesti(),
				entitat);
		return dtoMappingHelper.getMapperFacade().map(
				serveiBus,
				ServeiBusDto.class);
	}

	@Transactional(rollbackFor = ServeiBusNotFoundException.class)
	@Override
	public ServeiBusDto deleteServeiBus(
			Long serveiBusId) throws ServeiBusNotFoundException {
		log.debug("Esborrant redirecció del bus (id=" + serveiBusId + ")");
		ServeiBus serveiBus = serveiBusRepository.findOne(serveiBusId);
		if (serveiBus == null) {
			log.debug("No s'ha trobat la redirecció del bus (id=" + serveiBusId + ")");
			throw new ServeiBusNotFoundException();
		}
		// Esborra el registre
		serveiBusRepository.delete(serveiBus);
		// Retorna el registre antic
		return dtoMappingHelper.getMapperFacade().map(
				serveiBus,
				ServeiBusDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public ServeiBusDto findServeiBusById(Long id) throws ServeiBusNotFoundException {
		log.debug("Obtenint la redirecció del bus (id=" + id + ")");
		ServeiBus serveiBus = serveiBusRepository.findOne(id);
		if (serveiBus == null) {
			log.debug("No s'ha trobat la redirecció del bus (id=" + id + ")");
			throw new ServeiBusNotFoundException();
		}
		return dtoMappingHelper.getMapperFacade().map(
				serveiBus,
				ServeiBusDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<ServeiBusDto> findServeisBus(String serveiCodi) throws ServeiNotFoundException {
		log.debug("Obtenint les redireccions del bus pel servicio (codi=" + serveiCodi + ")");
		Servicio servicio = getServicioByCode(serveiCodi);
		if (servicio == null) {
			log.debug("No s'ha trobat el servicio (codi=" + serveiCodi + ")");
			throw new ServeiNotFoundException();
		}
		return dtoMappingHelper.getMapperFacade().mapAsList(
				serveiBusRepository.findByServeiOrderByIdAsc(serveiCodi),
				ServeiBusDto.class);
	}

	@Transactional(rollbackFor = ServeiNotFoundException.class)
	@Override
	public void addServeiJustificantCamp(
			ServeiJustificantCampDto camp) throws ServeiNotFoundException {
		log.debug("Traducció del camp de dades específiques (codi=" + camp.getServei() + ", campPath=" + camp.getXpath() + ")");
		Servicio servicio = getScspHelper().getServicio(camp.getServei());
		if (servicio == null) {
			log.debug("No s'ha trobat el servicio (codi=" + camp.getServei() + ")");
			throw new ServeiNotFoundException();
		}
		ServeiJustificantCamp serveiTraduccio = serveiJustificantCampRepository.findByServeiAndXpathAndLocaleIdiomaAndLocaleRegio(
				camp.getServei(),
				camp.getXpath(),
				DEFAULT_TRADUCCIO_LOCALE.getLanguage(),
				DEFAULT_TRADUCCIO_LOCALE.getCountry());
		if (serveiTraduccio != null) {
			if (camp.getTraduccio() != null && !camp.getTraduccio().isEmpty()) {
				serveiTraduccio.update(
						camp.getTraduccio());
			} else {
				serveiJustificantCampRepository.delete(serveiTraduccio);
			}
		} else if (camp.getTraduccio() != null && !camp.getTraduccio().isEmpty()) {
			serveiTraduccio = ServeiJustificantCamp.getBuilder(
					camp.getServei(),
					camp.getXpath(),
					DEFAULT_TRADUCCIO_LOCALE.getLanguage(),
					DEFAULT_TRADUCCIO_LOCALE.getCountry(),
					camp.getTraduccio()).build();
			serveiJustificantCampRepository.save(serveiTraduccio);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public List<ServeiJustificantCampDto> findServeiJustificantCamps(
			String serveiCodi) throws ServeiNotFoundException {
		log.debug("Obtenint els camps de dades específiques traduits pel servei (codi=" + serveiCodi + ")");
		Servicio servicio = getServicioByCode(serveiCodi);
		if (servicio == null) {
			log.debug("No s'ha trobat el servicio (codi=" + serveiCodi + ")");
			throw new ServeiNotFoundException();
		}
		return dtoMappingHelper.getMapperFacade().mapAsList(
				serveiJustificantCampRepository.findByServeiAndLocaleIdiomaAndLocaleRegio(
						serveiCodi,
						DEFAULT_TRADUCCIO_LOCALE.getLanguage(),
						DEFAULT_TRADUCCIO_LOCALE.getCountry()),
				ServeiJustificantCampDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<ServeiXsdDto> xsdFindByServei(
			String serveiCodi) throws IOException, ServeiNotFoundException {
		log.debug("Obtenint tots els fitxers XSD per a un servei (" +
				"serveiCodi=" + serveiCodi + ")");
		Servicio servicio = getServicioByCode(serveiCodi);

		return serveiXsdHelper.findAll(servicio.getCodCertificado());
	}

	@Transactional(readOnly = true)
	@Override
	public List<String> getRolsConfigurats() {
		log.debug("Obtenint tots els rols configurats per als diferents serveis");
		List<String> resposta = new ArrayList<String>();
		List<ServeiConfig> serveiConfigs = serveiConfigRepository.findAll();
		for (ServeiConfig serveiConfig: serveiConfigs) {
			if (serveiConfig.getRoleName() != null)
				resposta.add(serveiConfig.getRoleName());
		}
        /*List<ServeiCampDto> camps = dtoMappingHelper.getMapperFacade().mapAsList(
				serveiCampRepository.findAll(),
				ServeiCampDto.class);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XMLEncoder encoder = new XMLEncoder(baos);
		encoder.writeObject(camps);
		encoder.close();
		System.out.println(">>> " + baos.toString());*/
		return resposta;
	}

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
	public void xsdDelete(String codi, XsdTipusEnumDto tipus) throws IOException {
		log.debug("Esborra un fitxer XSD per a un servei (" +
				"codi=" + codi + ", " +
				"tipus=" + tipus + ")");
		Servicio servei = scspHelper.getServicio(codi);
		serveiXsdHelper.esborrarXsd(servei, tipus);
	}

	@Override
	public FitxerDto xsdDescarregar(String codi, XsdTipusEnumDto tipus) throws IOException {
		log.debug("Descarrega un fitxer XSD per a un servei (" +
				"codi=" + codi + ", " +
				"tipus=" + tipus + ")");
		Servicio servei = scspHelper.getServicio(codi);
		return serveiXsdHelper.descarregarXsd(servei, tipus);
	}

	@Override
	public void xsdCreate(String codi, ServeiXsdDto xsd, byte[] contingut) throws IOException {
		log.debug("Afegeix un fitxer XSD per a un servei (" +
				"codi=" + codi + ", " +
				"xsd=" + xsd.getNomArxiu() + ")");
		Servicio servei = scspHelper.getServicio(codi);
		serveiXsdHelper.modificarXsd(servei, xsd, contingut);
	}

	@Transactional(readOnly = true)
	@Override
	public List<ServeiDto> findAll() {
		log.debug("Cercant els serveis actius");
		List<ServeiDto> resposta = new ArrayList<ServeiDto>();
		List<Servicio> servicios = getScspHelper().findServicioAll();
		for (Servicio servicio : servicios) {
			resposta.add(toServeiDto(servicio));
		}
		return resposta;
	}

    @Override
	@Transactional(readOnly = true)
    public ServeiReglaDto serveiReglaFindByNom(Long serveiId, String nom) {
        return dtoMappingHelper.getMapperFacade().map(
				serveiReglaRepository.findByNom(serveiId, nom),
				ServeiReglaDto.class);
    }

    @Override
	@Transactional(readOnly = true)
    public ServeiReglaDto serveiReglaFindById(Long reglaId) {
//		Servei servei = serveiRepository.findOne(serveiId);
//		if (servei == null)
//			throw new ServeiNotFoundException();
		return dtoMappingHelper.getMapperFacade().map(
				serveiReglaRepository.findOne(reglaId),
				ServeiReglaDto.class);
    }

	@Override
	@Transactional
	public ServeiReglaDto serveiReglaCreate(String serveiCodi, ServeiReglaDto reglaDto) throws ServeiNotFoundException {
		Servei servei = getServeiByCodi(serveiCodi);
		Integer seguentOrdre = serveiReglaRepository.getSeguentOrdre(servei.getId());
		seguentOrdre = seguentOrdre == null ? 0 : seguentOrdre + 1;
		ServeiRegla regla = ServeiRegla.builder()
				.nom(reglaDto.getNom())
				.ordre(seguentOrdre)
				.modificat(reglaDto.getModificat())
				.modificatValor(reglaDto.getModificatValor())
				.afectatValor(reglaDto.getAfectatValor())
				.accio(reglaDto.getAccio())
				.servei(servei)
				.build();
		return dtoMappingHelper.getMapperFacade().map(serveiReglaRepository.save(regla), ServeiReglaDto.class);
	}

	@Override
	@Transactional
	public ServeiReglaDto serveiReglaUpdate(String serveiCodi, ServeiReglaDto reglaDto) throws ServeiNotFoundException {
		Servei servei = getServeiByCodi(serveiCodi);
		ServeiRegla regla = serveiReglaRepository.findOne(reglaDto.getId());
		if (regla == null)
			throw new NotFoundException(reglaDto.getId(), ServeiRegla.class);

		regla.setNom(reglaDto.getNom());
		regla.setModificat(reglaDto.getModificat());
		regla.setModificatValor(reglaDto.getModificatValor());
		regla.setAfectatValor(reglaDto.getAfectatValor());
		regla.setAccio(reglaDto.getAccio());
		return dtoMappingHelper.getMapperFacade().map(serveiReglaRepository.save(regla), ServeiReglaDto.class);
	}

	@Override
	@Transactional
	public void serveiReglaDelete(String serveiCodi, Long reglaId) throws ServeiNotFoundException {
		Servei servei = getServeiByCodi(serveiCodi);
		ServeiRegla regla = serveiReglaRepository.findOne(reglaId);
		if (regla == null)
			throw new NotFoundException(reglaId, ServeiRegla.class);
		serveiReglaRepository.delete(regla);
	}

	@Override
	@Transactional
	public boolean serveiReglaMoure(Long reglaId, int posicio) {
		log.debug("Moguent la regla (reglaId=" + reglaId + ", posicio=" + posicio + ")");
		boolean ret = false;
		ServeiRegla regla = serveiReglaRepository.findOne(reglaId);
		if (regla == null)
			throw new NotFoundException(reglaId, ServeiRegla.class);

		List<ServeiRegla> regles = serveiReglaRepository.findByServeiOrderByOrdreAsc(regla.getServei());
		if(posicio != regles.indexOf(regla)) {
			regles.remove(regla);
			regles.add(posicio, regla);
			int i = 0;
			for (ServeiRegla r : regles) {
				r.setOrdre(i++);
				serveiReglaRepository.save(r);
			}
		}
		return ret;
	}

	@Override
	@Transactional
	public List<ServeiReglaDto> serveiReglesFindAll(String serveiCodi) throws ServeiNotFoundException {
		Servei servei = getServeiByCodi(serveiCodi);
		List<ServeiRegla> regles = serveiReglaRepository.findByServeiOrderByOrdreAsc(servei);
		return dtoMappingHelper.getMapperFacade().mapAsList(regles, ServeiReglaDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Long> findCampIdsByReglesServei(String serveiCodi) throws ServeiNotFoundException {
		Servei servei = getServeiByCodi(serveiCodi);
		return serveiReglaRepository.findCampsRegles(serveiCodi);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Long> findGrupIdsByReglesServei(String serveiCodi) throws ServeiNotFoundException {
//		Servei servei = getServeiByCodi(serveiCodi);
		return serveiReglaRepository.findGrupsRegles(serveiCodi);
	}

	@Override
	@Transactional(readOnly = true)
	public List<CampFormProperties> getCampsByserveiRegla(String serveiCodi, String[] campsModificats) throws ServeiNotFoundException {
		Servei servei = getServeiByCodi(serveiCodi);
		Set<String> modificats = new HashSet<>();
		if (campsModificats != null) {
			modificats.addAll(Arrays.asList(campsModificats));
		}
		return reglaHelper.getCampFormProperties(servei, modificats);
	}

	@Override
	@Transactional(readOnly = true)
	public List<CampFormProperties> getGrupsByserveiRegla(String serveiCodi, String[] grupsModificats) throws ServeiNotFoundException {
		Servei servei = getServeiByCodi(serveiCodi);
		Set<String> modificats = new HashSet<>();
		if (grupsModificats != null) {
			modificats.addAll(Arrays.asList(grupsModificats));
		}
		return reglaHelper.getGrupFormProperties(servei, modificats);
	}

	private Servei getServeiByCodi(String serveiCodi) throws ServeiNotFoundException {
		List<Servei> serveis = serveiRepository.findByCode(serveiCodi);
		if (serveis.size() == 0) {
			log.debug("No s'ha trobat el servicio (codi=" + serveiCodi + ")");
			throw new ServeiNotFoundException();
		}
		return serveis.get(0);
	}

	private Servicio toServicioScsp(ServeiDto dto) {
		Servicio servicio = getScspHelper().getServicio(dto.getCodi());
		if (servicio == null) {
			servicio = new Servicio();
		}
		servicio.setCodCertificado(dto.getCodi());
		servicio.setDescripcion(dto.getDescripcio());
		if (dto.getScspEmisor() != null && dto.getScspEmisor().getCif() != null) {
			EmisorDto emisor = dto.getScspEmisor();
			for (EmisorCertificado emisorCertificado: getScspHelper().findEmisorCertificadoAll()) {
				if (emisor.getCif().equals(emisorCertificado.getCif())) {
					servicio.setEmisor(emisorCertificado);
					break;
				}
			}
		}
		servicio.setFechaAlta(dto.getScspFechaAlta());
		servicio.setFechaBaja(dto.getScspFechaBaja());
		servicio.setCaducidad(dto.getScspCaducidad());
		servicio.setUrlSincrona(dto.getScspUrlSincrona());
		servicio.setUrlAsincrona(dto.getScspUrlAsincrona());
		servicio.setActionSincrona(dto.getScspActionSincrona());
		servicio.setActionAsincrona(dto.getScspActionAsincrona());
		servicio.setActionSolicitud(dto.getScspActionSolicitud());
		servicio.setVersionEsquema(dto.getScspVersionEsquema());
		servicio.setTipoSeguridad(dto.getScspTipoSeguridad());
		if (dto.getScspClaveFirma() != null && dto.getScspClaveFirma().getAlies() != null) {
			ClauPrivadaDto clauPrivada = dto.getScspClaveFirma();
			for (ClavePrivada clavePrivada: getScspHelper().findClavePrivadaAll()) {
				if (clauPrivada.getAlies().equals(clavePrivada.getAlias())) {
					servicio.setClaveFirma(clavePrivada);
					break;
				}
			}
		}
		if (dto.getScspClaveCifrado() != null && dto.getScspClaveCifrado().getAlies() != null) {
			ClauPublicaDto clauPublica = dto.getScspClaveCifrado();
			for (ClavePublica clavePublica: getScspHelper().findClavePublicaAll()) {
				if (clauPublica.getAlies().equals(clavePublica.getAlias())) {
					servicio.setClaveCifrado(clavePublica);
					break;
				}
			}
		}
		servicio.setXpathCifradoSincrono(dto.getScspXpathCifradoSincrono());
		servicio.setXpathCifradoAsincrono(dto.getScspXpathCifradoAsincrono());
		servicio.setAlgoritmoEncriptacion(dto.getScspAlgoritmoCifrado());
		servicio.setValidacionFirma(dto.getScspValidacionFirma());
		servicio.setPrefijoPeticion(dto.getScspPrefijoPeticion());
		servicio.setEsquemas(dto.getScspEsquemas());
		servicio.setNumeroMaximoReenvios(dto.getScspNumeroMaximoReenvios());
		servicio.setMaxSolicitudesPeticion(dto.getScspMaxSolicitudesPeticion());
		servicio.setPrefijoIdTransmision(dto.getScspPrefijoIdTransmision());
		servicio.setXpathCodigoError(dto.getScspXpathCodigoError());
		servicio.setXpathLiteralError(dto.getScspXpathLiteralError());
		servicio.setTimeout(dto.getScspTimeout());
		return servicio;
	}

	private ServeiDto toServeiDto(Servicio servicio) {
		ServeiDto dto = new ServeiDto();
		dto.setCodi(servicio.getCodCertificado());
		dto.setId(servicio.getId());
		dto.setDescripcio(servicio.getDescripcion());
		if (servicio.getEmisor() != null) {
			EmisorDto emisor = new EmisorDto();
			EmisorCertificado emisorCertificado = servicio.getEmisor();
			emisor.setCif(emisorCertificado.getCif());
			emisor.setNom(
					getScspHelper().getEmisorNombre(emisorCertificado.getCif()));
			dto.setScspEmisor(emisor);
		}
		dto.setScspFechaAlta(servicio.getFechaAlta());
		dto.setScspFechaBaja(servicio.getFechaBaja());
		dto.setScspCaducidad(servicio.getCaducidad());
		dto.setScspUrlSincrona(servicio.getUrlSincrona());
		dto.setScspUrlAsincrona(servicio.getUrlAsincrona());
		dto.setScspActionSincrona(servicio.getActionSincrona());
		dto.setScspActionAsincrona(servicio.getActionAsincrona());
		dto.setScspActionSolicitud(servicio.getActionSolicitud());
		dto.setScspVersionEsquema(servicio.getVersionEsquema());
		dto.setScspTipoSeguridad(servicio.getTipoSeguridad());
		if (servicio.getClaveFirma() != null) {
			ClauPrivadaDto clauPrivada = new ClauPrivadaDto();
			ClavePrivada clavePrivada = servicio.getClaveFirma();
			clauPrivada.setAlies(clavePrivada.getAlias());
			clauPrivada.setNom(
					getScspHelper().getClavePrivadaNombre(clavePrivada.getAlias()));
			clauPrivada.setNumSerie(
					getScspHelper().getClavePrivadaNumeroSerie(clavePrivada.getAlias()));
			dto.setScspClaveFirma(clauPrivada);
		}
		if (servicio.getClaveCifrado() != null) {
			ClauPublicaDto clauPublica = new ClauPublicaDto();
			ClavePublica clavePublica = servicio.getClaveCifrado();
			clauPublica.setAlies(clavePublica.getAlias());
			clauPublica.setNom(
					getScspHelper().getClavePublicaNombre(clavePublica.getAlias()));
			clauPublica.setNumSerie(
					getScspHelper().getClavePublicaNumeroSerie(clavePublica.getAlias()));
			dto.setScspClaveCifrado(clauPublica);
		}
		dto.setScspXpathCifradoSincrono(servicio.getXpathCifradoSincrono());
		dto.setScspXpathCifradoAsincrono(servicio.getXpathCifradoAsincrono());
		dto.setScspAlgoritmoCifrado(servicio.getAlgoritmoEncriptacion());
		dto.setScspValidacionFirma(servicio.getValidacionFirma());
		dto.setScspPrefijoPeticion(servicio.getPrefijoPeticion());
		dto.setScspEsquemas(servicio.getEsquemas());
		dto.setScspNumeroMaximoReenvios(servicio.getNumeroMaximoReenvios());
		dto.setScspMaxSolicitudesPeticion(servicio.getMaxSolicitudesPeticion());
		dto.setScspPrefijoIdTransmision(servicio.getPrefijoIdTransmision());
		dto.setScspXpathCodigoError(servicio.getXpathCodigoError());
		dto.setScspXpathCodigoError(servicio.getXpathLiteralError());
		dto.setScspTimeout(servicio.getTimeout());
		ServeiConfig serveiConfig = serveiConfigRepository.findByServei(servicio.getCodCertificado());
		if (serveiConfig != null) {
			dto.setPinbalCustodiaCodi(serveiConfig.getCustodiaCodi());
			dto.setPinbalRoleName(serveiConfig.getRoleName());
			dto.setPinbalCondicioBusClass(serveiConfig.getCondicioBusClass());
			if (serveiConfig.getEntitatTipus() != null)
				dto.setPinbalEntitatTipus(
						EntitatTipusDto.valueOf(
								serveiConfig.getEntitatTipus().toString()));
			if (serveiConfig.getJustificantTipus() != null)
				dto.setPinbalJustificantTipus(
						JustificantTipusDto.valueOf(
								serveiConfig.getJustificantTipus().toString()));
			dto.setPinbalJustificantXpath(serveiConfig.getJustificantXpath());
			dto.setPinbalPermesDocumentTipusDni(serveiConfig.isPermesDocumentTipusDni());
			dto.setPinbalPermesDocumentTipusNif(serveiConfig.isPermesDocumentTipusNif());
			dto.setPinbalPermesDocumentTipusCif(serveiConfig.isPermesDocumentTipusCif());
			dto.setPinbalPermesDocumentTipusNie(serveiConfig.isPermesDocumentTipusNie());
			dto.setPinbalPermesDocumentTipusPas(serveiConfig.isPermesDocumentTipusPas());
			dto.setPinbalActiuCampNom(serveiConfig.isActiuCampNom());
			dto.setPinbalActiuCampLlinatge1(serveiConfig.isActiuCampLlinatge1());
			dto.setPinbalActiuCampLlinatge2(serveiConfig.isActiuCampLlinatge2());
			dto.setPinbalActiuCampNomComplet(serveiConfig.isActiuCampNomComplet());
			dto.setPinbalActiuCampDocument(serveiConfig.isActiuCampDocument());
			dto.setPinbalUnitatDir3(serveiConfig.getPinbalUnitatDir3());
			dto.setPinbalDocumentObligatori(serveiConfig.isDocumentObligatori());
			dto.setPinbalComprovarDocument(serveiConfig.isComprovarDocument());
			dto.setActivaGestioXsd(serveiConfig.isActivaGestioXsd());
			dto.setMaxPeticionsMinut(serveiConfig.getMaxPeticionsMinut());
			dto.setAjuda(serveiConfig.getAjuda());
			dto.setFitxerAjudaNom(serveiConfig.getFitxerAjudaNom());
			dto.setFitxerAjudaMimeType(serveiConfig.getFitxerAjudaMimeType());
			dto.setFitxerAjudaContingut(serveiConfig.getFitxerAjudaContingut());
			dto.setPinbalUnitatDir3FromEntitat(serveiConfig.isPinbalUnitatDir3FromEntitat());
			dto.setPinbalIniDadesExpecifiques(serveiConfig.isIniDadesEspecifiques());
			dto.setPinbalAddDadesEspecifiques(serveiConfig.isAddDadesEspecifiques());
			dto.setUseAutoClasse(serveiConfig.isUseAutoClasse());
		}
//		Long numeroProcedimentsAssociats = procedimentRepository.countByServei(servicio.getCodCertificado());
//		dto.setNumeroProcedimentsAssociats(numeroProcedimentsAssociats == null ? 0 : numeroProcedimentsAssociats);
		dto.setNumeroProcedimentsAssociats(procedimentRepository.countByServei(servicio.getCodCertificado()));
		return dto;
	}

	private void copiarArbreDadesEspecifiques(
			Node<DadesEspecifiquesNode> source,
			NodeDto<DadaEspecificaDto> target,
			List<String> path) {
		if (source.getData() != null) {
			DadaEspecificaDto dada = new DadaEspecificaDto();
			dada.setPath(path.toArray(new String[path.size()]));
			dada.setNom(source.getData().getNom());
			if (source.getData().getEnumValues().size() > 0)
				dada.setEnumeracioValors(
						source.getData().getEnumValues().toArray(
								new String[source.getData().getEnumValues().size()]));
			dada.setComplexa(source.getData().isComplex());
			dada.setTipusDadaComplexa(TipusDadaComplexaEnum.getTipus(source.getData().getGroupType()));
			target.setDades(dada);
		}
		if (source.getNumberOfChildren() > 0) {
			for (Node<DadesEspecifiquesNode> child: source.getChildren()) {
				NodeDto<DadaEspecificaDto> fill = new NodeDto<DadaEspecificaDto>();
				path.add(source.getData().getNom());
				copiarArbreDadesEspecifiques(child, fill, path);
				path.remove(path.size() - 1);
				target.addFill(fill);
			}
		}
	}

	private DadesEspecifiquesNode trobarNodeAmbPath(
			Node<DadesEspecifiquesNode> source,
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
			for (Node<DadesEspecifiquesNode> child: source.getChildren()) {
				currentPath.add(source.getData().getNom());
				DadesEspecifiquesNode trobat = trobarNodeAmbPath(child, pathToFind, currentPath);
				if (trobat != null)
					return trobat;
				currentPath.remove(currentPath.size() - 1);
			}
		}
		return null;
	}

	private boolean esServeiCampEnum(String serveiCodi, String path) {
		if (path != null) {
			try {
				ArbreDto<DadaEspecificaDto> arbreDadesEspecifiques = generarArbreDadesEspecifiques(serveiCodi);
				List<NodeDto<DadaEspecificaDto>> llistatDadesEspecifiques = arbreDadesEspecifiques.toList();
				for (NodeDto<DadaEspecificaDto> node: llistatDadesEspecifiques) {
					DadaEspecificaDto dadaEspecifica = node.getDades();
					if (path.equals(dadaEspecifica.getPathAmbSeparadorDefault())) {
						return dadaEspecifica.isEnumeracio();
					}
				}
			} catch (Exception ex) {
				log.warn("Error al obtenir l'arbre de dades específiques per al servei (codi=" + serveiCodi + ")");
			}
		}
		return false;
	}

	private ServeiCampTipus toServeiTipus(ServeiCampDtoTipus dtoTipus) {
		if (ServeiCampDtoTipus.TEXT.equals(dtoTipus)) {
			return ServeiCampTipus.TEXT;
		} else if (ServeiCampDtoTipus.NUMERIC.equals(dtoTipus)) {
			return ServeiCampTipus.NUMERIC;
		} else if (ServeiCampDtoTipus.DATA.equals(dtoTipus)) {
			return ServeiCampTipus.DATA;
		} else if (ServeiCampDtoTipus.ENUM.equals(dtoTipus)) {
			return ServeiCampTipus.ENUM;
		} else if (ServeiCampDtoTipus.PROVINCIA.equals(dtoTipus)) {
			return ServeiCampTipus.PROVINCIA;
		} else if (ServeiCampDtoTipus.MUNICIPI_3.equals(dtoTipus)) {
			return ServeiCampTipus.MUNICIPI_3;
		} else if (ServeiCampDtoTipus.MUNICIPI_5.equals(dtoTipus)) {
			return ServeiCampTipus.MUNICIPI_5;
		} else if (ServeiCampDtoTipus.ETIQUETA.equals(dtoTipus)) {
			return ServeiCampTipus.ETIQUETA;
		} else if (ServeiCampDtoTipus.BOOLEA.equals(dtoTipus)) {
			return ServeiCampTipus.BOOLEA;
		} else if (ServeiCampDtoTipus.DOC_IDENT.equals(dtoTipus)) {
			return ServeiCampTipus.DOC_IDENT;
		}
		return ServeiCampTipus.valueOf(dtoTipus.name());
	}

	private EntitatTipus toEntitatTipus(EntitatTipusDto dtoTipus) {
		if (EntitatTipusDto.GOVERN.equals(dtoTipus)) {
			return EntitatTipus.GOVERN;
		} else if (EntitatTipusDto.CONSELL.equals(dtoTipus)) {
			return EntitatTipus.CONSELL;
		} else if (EntitatTipusDto.AJUNTAMENT.equals(dtoTipus)) {
			return EntitatTipus.AJUNTAMENT;
		}
		return null;
	}

	private JustificantTipus toJustificantTipus(JustificantTipusDto dtoTipus) {
		if (JustificantTipusDto.GENERAT.equals(dtoTipus)) {
			return JustificantTipus.GENERAT;
		} else if (JustificantTipusDto.ADJUNT_PDF_BASE64.equals(dtoTipus)) {
			return JustificantTipus.ADJUNT_PDF_BASE64;
		}
		return null;
	}

	private ScspHelper getScspHelper() {
		if (scspHelper == null) {
			scspHelper = new ScspHelper(
					applicationContext,
					messageSource);
		}
		return scspHelper;
	}

	/**
	 * Obté una instància Servicio a partir del codi.
	 *
	 * Es fa amb dues consultes per integrar correctament la llibreria scsp
	 *
	 * @param serveiCodi
	 * @return
	 * @throws ServeiNotFoundException
	 */
	private Servicio getServicioByCode(String serveiCodi) throws ServeiNotFoundException {
		List<Servei> serveis = serveiRepository.findByCode(serveiCodi);
		if (serveis.size() == 0) {
			log.debug("No s'ha trobat el servicio (codi=" + serveiCodi + ")");
			throw new ServeiNotFoundException();
		}

		Servicio servicio =  getScspHelper().getServicioById(serveis.get(0).getId());
		if (servicio == null) {
			log.debug("No s'ha trobat el servicio SCSP (codi=" + serveiCodi + ")");
			throw new ServeiNotFoundException();
		}
		return servicio;
	}

	private boolean isGestioXsdActiva(String serveiCodi) {
		ServeiConfig serveiConfig = serveiConfigRepository.findByServei(serveiCodi);
		return serveiConfig != null && serveiConfig.isActivaGestioXsd();
	}

}
