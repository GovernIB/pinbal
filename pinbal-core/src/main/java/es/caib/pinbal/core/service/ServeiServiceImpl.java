/**
 * 
 */
package es.caib.pinbal.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.pinbal.core.dto.ArbreDto;
import es.caib.pinbal.core.dto.ClauPrivadaDto;
import es.caib.pinbal.core.dto.ClauPublicaDto;
import es.caib.pinbal.core.dto.DadaEspecificaDto;
import es.caib.pinbal.core.dto.EmisorDto;
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
import es.caib.pinbal.core.helper.DtoMappingHelper;
import es.caib.pinbal.core.helper.PermisosHelper;
import es.caib.pinbal.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.pinbal.core.helper.PluginHelper;
import es.caib.pinbal.core.helper.ServeiHelper;
import es.caib.pinbal.core.helper.UsuariHelper;
import es.caib.pinbal.core.model.Entitat;
import es.caib.pinbal.core.model.EntitatUsuari;
import es.caib.pinbal.core.model.Procediment;
import es.caib.pinbal.core.model.ProcedimentServei;
import es.caib.pinbal.core.model.ServeiBus;
import es.caib.pinbal.core.model.ServeiCamp;
import es.caib.pinbal.core.model.ServeiCamp.ServeiCampTipus;
import es.caib.pinbal.core.model.ServeiCampGrup;
import es.caib.pinbal.core.model.ServeiConfig;
import es.caib.pinbal.core.model.ServeiConfig.EntitatTipus;
import es.caib.pinbal.core.model.ServeiConfig.JustificantTipus;
import es.caib.pinbal.core.model.ServeiJustificantCamp;
import es.caib.pinbal.core.repository.EntitatRepository;
import es.caib.pinbal.core.repository.EntitatUsuariRepository;
import es.caib.pinbal.core.repository.ProcedimentRepository;
import es.caib.pinbal.core.repository.ProcedimentServeiRepository;
import es.caib.pinbal.core.repository.ServeiBusRepository;
import es.caib.pinbal.core.repository.ServeiCampGrupRepository;
import es.caib.pinbal.core.repository.ServeiCampRepository;
import es.caib.pinbal.core.repository.ServeiConfigRepository;
import es.caib.pinbal.core.repository.ServeiJustificantCampRepository;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
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
import es.scsp.common.domain.ClavePrivada;
import es.scsp.common.domain.ClavePublica;
import es.scsp.common.domain.EmisorCertificado;
import es.scsp.common.domain.Servicio;

/**
 * Implementació dels mètodes per a interactuar amb les funcionalitats SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ServeiServiceImpl implements ServeiService {

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
	private ServeiHelper serveiHelper;
	@Resource
	private ScspHelper scspHelper;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private DtoMappingHelper dtoMappingHelper;
	@Resource
	private UsuariHelper usuariHelper;

	@Resource
	private MutableAclService aclService;



	@Transactional
	@Override
	public ServeiDto save(ServeiDto servei) {
		LOGGER.debug("Guardant dades per al servicio SCSP (codi=" + servei.getCodi() + ")");
		scspHelper.saveServicio(toServicioScsp(servei));
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
					servei.getFitxerAjudaNom(),
					servei.getFitxerAjudaMimeType(),
					servei.getFitxerAjudaContingut()).build();
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
					servei.isPinbalDocumentObligatori(),
					servei.isPinbalComprovarDocument(),
					servei.getAjuda(),
					servei.getFitxerAjudaNom(),
					servei.getFitxerAjudaMimeType(),
					servei.getFitxerAjudaContingut());
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
		LOGGER.debug("Esborrant dades per al servicio SCSP (codi=" + serveiCodi + ")");
		Servicio servicio = scspHelper.getServicio(serveiCodi);
		if (servicio == null) {
			LOGGER.debug("No s'ha trobat el servicio SCSP (codi=" + serveiCodi + ")");
			throw new ServeiNotFoundException();
		}
		if (scspHelper.servicioHasConsultes(serveiCodi)) {
			LOGGER.debug("El servicio SCSP te consultes realitzades (codi=" + serveiCodi + ")");
			throw new ServeiAmbConsultesException();
		}
		ServeiDto servei = toServeiDto(servicio);
		scspHelper.deleteServicio(serveiCodi);
		ServeiConfig serveiConfig = serveiConfigRepository.findByServei(serveiCodi);
		if (serveiConfig != null)
			serveiConfigRepository.delete(serveiConfig);
		return servei;
	}

	@Transactional(readOnly = true)
	@Override
	public ServeiDto findAmbCodiPerAdminORepresentant(
			String serveiCodi) throws ServeiNotFoundException {
		LOGGER.debug("Obtenint informació del servicio (codi=" + serveiCodi + ") per a administrador o representant");
		Servicio servicio = scspHelper.getServicio(serveiCodi);
		if (servicio != null) {
			return toServeiDto(servicio);
		} else {
			LOGGER.debug("No s'ha trobat el servicio (codi=" + serveiCodi + ")");
			throw new ServeiNotFoundException();
		}
	}

	@Transactional(readOnly = true)
	@Override
	public ServeiDto findAmbCodiPerDelegat(
			Long entitatId,
			String serveiCodi) throws ServeiNotFoundException {
		LOGGER.debug("Obtenint informació del servicio (codi=" + serveiCodi + ") per al delegat");
		Servicio servicio = scspHelper.getServicio(serveiCodi);
		if (servicio != null) {
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
					LOGGER.debug("No té permisos per accedira al servicio (codi=" + serveiCodi + ")");
					throw new ServeiNotFoundException();
				}
			} else {
				// Si l'accés al servei no està restringit el retorna
				return toServeiDto(servicio);
			}
		} else {
			LOGGER.debug("No s'ha trobat el servicio (codi=" + serveiCodi + ")");
			throw new ServeiNotFoundException();
		}
	}

	@Transactional(readOnly = true)
	@Override
	public List<ServeiDto> findActius() {
		LOGGER.debug("Cercant els servicios actius");
		List<ServeiDto> resposta = new ArrayList<ServeiDto>();
		List<Servicio> servicios = scspHelper.findServicioAll();
		for (Servicio servicio : servicios)
			resposta.add(toServeiDto(servicio));
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public List<ServeiDto> findAmbEntitat(Long entitatId)
			throws EntitatNotFoundException {
		LOGGER.debug("Cercant els servicios actius per a l'entitat (id="
				+ entitatId + ")");
		Entitat entitat = entitatRepository.findOne(entitatId);
		if (entitat == null) {
			LOGGER.debug("No s'ha trobat l'entitat (id=" + entitatId + ")");
			throw new EntitatNotFoundException();
		}
		List<ServeiDto> resposta = new ArrayList<ServeiDto>();
		List<Servicio> servicios = scspHelper.findServicioAll();
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
	public List<ServeiDto> findAmbEntitatIProcediment(
			Long entitatId,
			Long procedimentId) throws EntitatNotFoundException, ProcedimentNotFoundException {
		LOGGER.debug("Cercant els servicios (entitatId=" + entitatId + ", procedimentId=" + procedimentId + ")");
		Entitat entitat = entitatRepository.findOne(entitatId);
		if (entitat == null) {
			LOGGER.debug("No s'ha trobat l'entitat (id=" + entitatId + ")");
			throw new EntitatNotFoundException();
		}
		Procediment procediment = procedimentRepository.findOne(procedimentId);
		if (procediment == null) {
			LOGGER.debug("No s'ha trobat el procediment (id=" + procedimentId + ")");
			throw new ProcedimentNotFoundException();
		}
		List<ProcedimentServei> procedimentServeis = procedimentServeiRepository.findByEntitatIdAndProcedimentId(
				entitatId,
				procedimentId);
		List<ServeiDto> resposta = new ArrayList<ServeiDto>();
		List<Servicio> servicios = scspHelper.findServicioAll();
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
	public List<ProcedimentServeiDto> findPermesosAmbEntitatIUsuari(
			Long entitatId,
			String usuariCodi) throws EntitatNotFoundException {
		LOGGER.debug("Cercant serveis permesos per l'usuari (" +
				"entitatId=" + entitatId + ", " +
				"usuariCodi=" + usuariCodi + ")");
		Entitat entitat = entitatRepository.findOne(entitatId);
		if (entitat == null) {
			LOGGER.debug("No s'ha trobat l'entitat (id=" + entitatId + ")");
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
							scspHelper.getServicio(
									procedimentServei.getServei())));
			resposta.add(psd);
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public List<ServeiDto> findPermesosAmbProcedimentPerDelegat(
			Long entitatId,
			Long procedimentId) throws EntitatNotFoundException, ProcedimentNotFoundException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		LOGGER.debug("Cercant serveis permesos pel delegat (entitatId=" + entitatId + ", procedimentId=" + procedimentId + ", usuariCodi=" + auth.getName() + ")");
		Entitat entitat = entitatRepository.findOne(entitatId);
		if (entitat == null) {
			LOGGER.debug("No s'ha trobat l'entitat (id=" + entitatId + ")");
			throw new EntitatNotFoundException();
		}
		Procediment procediment = null;
		if (procedimentId != null) {
			procediment = procedimentRepository.findOne(procedimentId);
			if (procediment == null)
				throw new ProcedimentNotFoundException();
		}
		EntitatUsuari entitatUsuari = entitatUsuariRepository.findByEntitatIdAndUsuariCodi(
				entitat.getId(),
				auth.getName());
		if (entitatUsuari == null || !entitatUsuari.isDelegat()) {
			LOGGER.debug("Aquest usuari no té permisos per accedir com a delegat a l'entitat (id=" + entitat.getId() + ", usuariCodi=" + auth.getName() + ")");
			throw new EntitatNotFoundException();
		}
		List<String> permesos = serveiHelper.findServeisPermesosPerUsuari(
				entitat.getId(),
				(procediment != null) ? procediment.getCodi() : null,
				auth);
		List<ServeiDto> resposta = new ArrayList<ServeiDto>();
		for (String servei: permesos)
			resposta.add(toServeiDto(scspHelper.getServicio(servei)));
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public List<EmisorDto> findEmisorAll() {
		LOGGER.debug("Obtenint llistat d'emisors SCSP");
		List<EmisorDto> resposta = new ArrayList<EmisorDto>();
		for (EmisorCertificado emisor: scspHelper.findEmisorCertificadoAll()) {
			EmisorDto dto = new EmisorDto();
			dto.setNom(emisor.getNombre());
			dto.setCif(emisor.getCif());
			resposta.add(dto);
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public List<ClauPublicaDto> findClauPublicaAll() {
		LOGGER.debug("Obtenint llistat de claus públiques SCSP");
		List<ClauPublicaDto> resposta = new ArrayList<ClauPublicaDto>();
		for (ClavePublica clavePublica: scspHelper.findClavePublicaAll()) {
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
		LOGGER.debug("Obtenint llistat de claus privades SCSP");
		List<ClauPrivadaDto> resposta = new ArrayList<ClauPrivadaDto>();
		for (ClavePrivada clavePrivada: scspHelper.findClavePrivadaAll()) {
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
		LOGGER.debug("Generant arbre de dades específiques per al servei (codi=" + serveiCodi + ")");
		Servicio servicio = scspHelper.getServicio(serveiCodi);
		if (servicio == null) {
			LOGGER.debug("No s'ha trobat el servicio (codi=" + serveiCodi + ")");
			throw new ServeiNotFoundException();
		}
		try {
			ArbreDto<DadaEspecificaDto> arbre = new ArbreDto<DadaEspecificaDto>();
			Tree<DadesEspecifiquesNode> tree = scspHelper.generarArbreDadesEspecifiques(serveiCodi);
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
			LOGGER.error(
					"Error al generar arbre de dades específiques per al servei (codi=" + serveiCodi + ")",
					ex);
			throw new ScspException(
					"Error al generar arbre de dades específiques per al servei (codi=" + serveiCodi + ")",
					ex);
		}
	}

	@Transactional(rollbackFor = ServeiNotFoundException.class)
	@Override
	public ServeiCampDto createServeiCamp(
			String serveiCodi,
			String path) throws ServeiNotFoundException {
		LOGGER.debug("Creant camp per al servei (codi=" + serveiCodi + ")");
		Servicio servicio = scspHelper.getServicio(serveiCodi);
		if (servicio == null) {
			LOGGER.debug("No s'ha trobat el servicio (codi=" + serveiCodi + ")");
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
					Tree<DadesEspecifiquesNode> tree = scspHelper.generarArbreDadesEspecifiques(serveiCodi);
					DadesEspecifiquesNode nodeEnum = trobarNodeAmbPath(
							tree.getRootElement(),
							path,
							new ArrayList<String>());
					serveiCamp.updateEnumDescripcions(
							nodeEnum.getEnumValues().toArray(new String[nodeEnum.getEnumValues().size()]));
				} catch (Exception ex) {
					LOGGER.error(
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
		LOGGER.debug("Modificant el camp (id=" + modificat.getId() + ") del servei");
		ServeiCamp serveiCamp = serveiCampRepository.findOne(modificat.getId());
		if (serveiCamp == null) {
			LOGGER.debug("No s'ha trobat el camp (id=" + modificat.getId() + ") del servei");
			throw new ServeiCampNotFoundException();
		}
		ServeiCamp campPare = null;
		if (modificat.getCampPare() != null) {
			campPare = serveiCampRepository.findOne(
					modificat.getCampPare().getId());
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
				modificat.isObligatori(),
				modificat.isModificable(),
				modificat.isVisible());
        return dtoMappingHelper.getMapperFacade().map(
        		serveiCamp,
        		ServeiCampDto.class);
	}

	@Transactional(rollbackFor = ServeiCampNotFoundException.class)
	@Override
	public ServeiCampDto deleteServeiCamp(Long serveiCampId) throws ServeiCampNotFoundException {
		LOGGER.debug("Esborrant el camp (id=" + serveiCampId + ") del servei");
		ServeiCamp perEsborrar = serveiCampRepository.findOne(serveiCampId);
		if (perEsborrar == null) {
			LOGGER.debug("No s'ha trobat el camp (id=" + serveiCampId + ") del servei");
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
	public ServeiCampDto moveServeiCamp(
			String serveiCodi,
			Long serveiCampId,
			int indexDesti) throws ServeiCampNotFoundException {
		LOGGER.debug("Movent el camp del servei (codi=" + serveiCodi + ", serveiCampId=" + serveiCampId + ", " + indexDesti + ")");
		ServeiCamp perMoure = serveiCampRepository.findOne(serveiCampId);
		if (perMoure == null) {
			LOGGER.debug("No s'ha trobat el camp (id=" + serveiCampId + ") del servei");
			throw new ServeiCampNotFoundException();
		}
		List<ServeiCamp> camps = serveiCampRepository.findByServeiAndGrupOrderByOrdreAsc(
				serveiCodi,
				perMoure.getGrup() == null,
				perMoure.getGrup());
		int indexOrigen = perMoure.getOrdre();
		if (indexOrigen >= camps.size()) {
			LOGGER.debug("No s'ha trobat el camp (index=" + indexOrigen + ") del servei (codi=" + serveiCodi + ")");
			throw new ServeiCampNotFoundException();
		}
		if (indexOrigen != indexDesti) {
			if (indexOrigen < indexDesti) {
				camps.add(
						indexDesti,
						camps.remove(indexOrigen));
			} else {
				camps.add(
						indexDesti,
						camps.remove(indexOrigen));
			}
			for (int i = 0; i < camps.size(); i++)
				camps.get(i).updateOrdre(i);
		}
		
        return dtoMappingHelper.getMapperFacade().map(
        		camps.get(indexDesti),
        		ServeiCampDto.class);
	}

	@Transactional(rollbackFor = {ServeiCampNotFoundException.class, ServeiCampGrupNotFoundException.class})
	@Override
	public void agrupaServeiCamp(
			Long serveiCampId,
			Long serveiCampGrupId) throws ServeiCampNotFoundException, ServeiCampGrupNotFoundException {
		LOGGER.debug("Agrupant el camp (serveiCampId=" + serveiCampId + ", serveiCampGrupId=" + serveiCampGrupId + ")");
		ServeiCamp perAgrupar = serveiCampRepository.findOne(serveiCampId);
		if (perAgrupar == null) {
			LOGGER.debug("No s'ha trobat el camp (id=" + serveiCampId + ")");
			throw new ServeiCampNotFoundException();
		}
		ServeiCampGrup grupOrigen = perAgrupar.getGrup();
		// Canvia el grup
		ServeiCampGrup grupDesti = null;
		if (serveiCampGrupId != null) {
			grupDesti = serveiCampGrupRepository.findOne(serveiCampGrupId);
			if (grupDesti == null) {
				LOGGER.debug("No s'ha trobat el grup (id=" + serveiCampGrupId + ")");
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
		LOGGER.debug("Cercant els camps pel servicio (codi=" + serveiCodi + ")");
		Servicio servicio = scspHelper.getServicio(serveiCodi);
		if (servicio == null) {
			LOGGER.debug("No s'ha trobat el servicio (codi=" + serveiCodi + ")");
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
		LOGGER.debug("Creant nou grup de camps (serveiCodi=" + serveiCampGrup.getServei() + ", nom=" + serveiCampGrup.getNom() + ")");
		Servicio servicio = scspHelper.getServicio(serveiCampGrup.getServei());
		if (servicio == null) {
			LOGGER.debug("No s'ha trobat el servicio (codi=" + serveiCampGrup.getServei() + ")");
			throw new ServeiNotFoundException();
		}
		List<ServeiCampGrup> grupsExistents = serveiCampGrupRepository.findByServeiOrderByOrdreAsc(
				serveiCampGrup.getServei());
		ServeiCampGrup perCrear = ServeiCampGrup.getBuilder(
				serveiCampGrup.getServei(),
				serveiCampGrup.getNom(),
				grupsExistents.size()).build(); 
		return dtoMappingHelper.getMapperFacade().map(
				serveiCampGrupRepository.save(perCrear),
				ServeiCampGrupDto.class);
	}

	@Transactional(rollbackFor = ServeiCampGrupNotFoundException.class)
	@Override
	public ServeiCampGrupDto updateServeiCampGrup(ServeiCampGrupDto serveiCampGrup) throws ServeiCampGrupNotFoundException {
		LOGGER.debug("Modificant grup de camps (id=" + serveiCampGrup.getId() + ")");
		ServeiCampGrup perModificar = serveiCampGrupRepository.findOne(
				serveiCampGrup.getId());
		if (perModificar == null) {
			LOGGER.debug("No s'ha trobat el grup de camps (id=" + serveiCampGrup.getId() + ")");
			throw new ServeiCampGrupNotFoundException();
		}
		perModificar.update(serveiCampGrup.getNom());
		return dtoMappingHelper.getMapperFacade().map(
				perModificar,
				ServeiCampGrupDto.class);
	}

	@Transactional(rollbackFor = ServeiCampGrupNotFoundException.class)
	@Override
	public ServeiCampGrupDto deleteServeiCampGrup(Long serveiCampGrupId) throws ServeiCampGrupNotFoundException {
		LOGGER.debug("Esborrant grup de camps (id=" + serveiCampGrupId + ")");
		ServeiCampGrup perEsborrar = serveiCampGrupRepository.findOne(
				serveiCampGrupId);
		if (perEsborrar == null) {
			LOGGER.debug("No s'ha trobat el grup de camps (id=" + serveiCampGrupId + ")");
			throw new ServeiCampGrupNotFoundException();
		}
		// Deslliga els camps del grup per a evitar esborrar-los
		for (ServeiCamp camp: perEsborrar.getCamps())
			camp.updateGrup(null);
		perEsborrar.getCamps().clear();
		// Esborra el grup
		serveiCampGrupRepository.delete(perEsborrar);
		return dtoMappingHelper.getMapperFacade().map(
				perEsborrar,
				ServeiCampGrupDto.class);
	}

	@Transactional(rollbackFor = ServeiCampGrupNotFoundException.class)
	@Override
	public void moveServeiCampGrup(
			Long serveiCampGrupId,
			boolean up) throws ServeiCampGrupNotFoundException {
		LOGGER.debug("Movent grup de camps (id=" + serveiCampGrupId + ", up=" + up + ")");
		ServeiCampGrup perMoure = serveiCampGrupRepository.findOne(
				serveiCampGrupId);
		if (perMoure == null) {
			LOGGER.debug("No s'ha trobat el grup de camps (id=" + serveiCampGrupId + ")");
			throw new ServeiCampGrupNotFoundException();
		}
		List<ServeiCampGrup> grups = serveiCampGrupRepository.findByServeiOrderByOrdreAsc(
				perMoure.getServei());
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
		LOGGER.debug("Cercant els grups de camps pel servicio (codi=" + serveiCodi + ")");
		Servicio servicio = scspHelper.getServicio(serveiCodi);
		if (servicio == null) {
			LOGGER.debug("No s'ha trobat el servicio (codi=" + serveiCodi + ")");
			throw new ServeiNotFoundException();
		}
		return dtoMappingHelper.getMapperFacade().mapAsList(
				serveiCampGrupRepository.findByServeiOrderByOrdreAsc(serveiCodi),
				ServeiCampGrupDto.class);
	}

	@Transactional(rollbackFor = ServeiNotFoundException.class)
	@Override
	public ServeiBusDto createServeiBus(
			ServeiBusDto creat) throws ServeiNotFoundException, EntitatNotFoundException {
		LOGGER.debug("Creant redirecció del bus pel servicio (codi=" + creat.getServei() + ")");
		Servicio servicio = scspHelper.getServicio(creat.getServei());
		if (servicio == null) {
			LOGGER.debug("No s'ha trobat el servicio (codi=" + creat.getServei() + ")");
			throw new ServeiNotFoundException();
		}
		Entitat entitat = entitatRepository.findOne(creat.getEntitat().getId());
		if (entitat == null) {
			LOGGER.debug("No s'ha trobat l'entitat (codi=" + creat.getEntitat().getId() + ")");
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
		LOGGER.debug("Modificant redirecció del bus pel servicio (codi=" + modificat.getServei() + ")");
		ServeiBus serveiBus = serveiBusRepository.findOne(modificat.getId());
		if (serveiBus == null) {
			LOGGER.debug("No s'ha trobat la redirecció del bus (id=" + modificat.getId() + ")");
			throw new ServeiBusNotFoundException();
		}
		Entitat entitat = entitatRepository.findOne(modificat.getEntitat().getId());
		if (entitat == null) {
			LOGGER.debug("No s'ha trobat l'entitat (codi=" + modificat.getEntitat().getId() + ")");
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
		LOGGER.debug("Esborrant redirecció del bus (id=" + serveiBusId + ")");
		ServeiBus serveiBus = serveiBusRepository.findOne(serveiBusId);
		if (serveiBus == null) {
			LOGGER.debug("No s'ha trobat la redirecció del bus (id=" + serveiBusId + ")");
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
		LOGGER.debug("Obtenint la redirecció del bus (id=" + id + ")");
		ServeiBus serveiBus = serveiBusRepository.findOne(id);
		if (serveiBus == null) {
			LOGGER.debug("No s'ha trobat la redirecció del bus (id=" + id + ")");
			throw new ServeiBusNotFoundException();
		}
		return dtoMappingHelper.getMapperFacade().map(
				serveiBus,
				ServeiBusDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<ServeiBusDto> findServeisBus(String serveiCodi) throws ServeiNotFoundException {
		LOGGER.debug("Obtenint les redireccions del bus pel servicio (codi=" + serveiCodi + ")");
		Servicio servicio = scspHelper.getServicio(serveiCodi);
		if (servicio == null) {
			LOGGER.debug("No s'ha trobat el servicio (codi=" + serveiCodi + ")");
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
		LOGGER.debug("Traducció del camp de dades específiques (codi=" + camp.getServei() + ", campPath=" + camp.getXpath() + ")");
		Servicio servicio = scspHelper.getServicio(camp.getServei());
		if (servicio == null) {
			LOGGER.debug("No s'ha trobat el servicio (codi=" + camp.getServei() + ")");
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
		LOGGER.debug("Obtenint els camps de dades específiques traduits pel servei (codi=" + serveiCodi + ")");
		Servicio servicio = scspHelper.getServicio(serveiCodi);
		if (servicio == null) {
			LOGGER.debug("No s'ha trobat el servicio (codi=" + serveiCodi + ")");
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
	public List<String> getRolsConfigurats() {
		LOGGER.debug("Obtenint tots els rols configurats per als diferents serveis");
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
	


	private Servicio toServicioScsp(ServeiDto dto) {
		Servicio servicio = scspHelper.getServicio(dto.getCodi());
		if (servicio == null) {
			servicio = new Servicio();
		}
		servicio.setCodCertificado(dto.getCodi());
		servicio.setDescripcion(dto.getDescripcio());
		if (dto.getScspEmisor() != null && dto.getScspEmisor().getCif() != null) {
			EmisorDto emisor = dto.getScspEmisor();
			for (EmisorCertificado emisorCertificado: scspHelper.findEmisorCertificadoAll()) {
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
			for (ClavePrivada clavePrivada: scspHelper.findClavePrivadaAll()) {
				if (clauPrivada.getAlies().equals(clavePrivada.getAlias())) {
					servicio.setClaveFirma(clavePrivada);
					break;
				}
			}
		}
		if (dto.getScspClaveCifrado() != null && dto.getScspClaveCifrado().getAlies() != null) {
			ClauPublicaDto clauPublica = dto.getScspClaveCifrado();
			for (ClavePublica clavePublica: scspHelper.findClavePublicaAll()) {
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
		dto.setDescripcio(servicio.getDescripcion());
		if (servicio.getEmisor() != null) {
			EmisorDto emisor = new EmisorDto();
			EmisorCertificado emisorCertificado = servicio.getEmisor();
			emisor.setCif(emisorCertificado.getCif());
			emisor.setNom(
					scspHelper.getEmisorNombre(emisorCertificado.getCif()));
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
					scspHelper.getClavePrivadaNombre(clavePrivada.getAlias()));
			clauPrivada.setNumSerie(
					scspHelper.getClavePrivadaNumeroSerie(clavePrivada.getAlias()));
			dto.setScspClaveFirma(clauPrivada);
		}
		if (servicio.getClaveCifrado() != null) {
			ClauPublicaDto clauPublica = new ClauPublicaDto();
			ClavePublica clavePublica = servicio.getClaveCifrado();
			clauPublica.setAlies(clavePublica.getAlias());
			clauPublica.setNom(
					scspHelper.getClavePublicaNombre(clavePublica.getAlias()));
			clauPublica.setNumSerie(
					scspHelper.getClavePublicaNumeroSerie(clavePublica.getAlias()));
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
			dto.setPinbalDocumentObligatori(serveiConfig.isDocumentObligatori());
			dto.setPinbalComprovarDocument(serveiConfig.isComprovarDocument());
			dto.setAjuda(serveiConfig.getAjuda());
			dto.setFitxerAjudaNom(serveiConfig.getFitxerAjudaNom());
			dto.setFitxerAjudaMimeType(serveiConfig.getFitxerAjudaMimeType());
			dto.setFitxerAjudaContingut(serveiConfig.getFitxerAjudaContingut());
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
				LOGGER.warn("Error al obtenir l'arbre de dades específiques per al servei (codi=" + serveiCodi + ")");
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
		} else if (ServeiCampDtoTipus.MUNICIPI.equals(dtoTipus)) {
			return ServeiCampTipus.MUNICIPI;
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

	private static final Logger LOGGER = LoggerFactory.getLogger(ServeiServiceImpl.class);

}