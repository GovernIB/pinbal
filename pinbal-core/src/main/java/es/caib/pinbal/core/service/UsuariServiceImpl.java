/**
 * 
 */
package es.caib.pinbal.core.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.pinbal.core.dto.EntitatUsuariDto;
import es.caib.pinbal.core.dto.InformeUsuariDto;
import es.caib.pinbal.core.dto.UsuariDto;
import es.caib.pinbal.core.helper.DtoMappingHelper;
import es.caib.pinbal.core.helper.PluginHelper;
import es.caib.pinbal.core.helper.UsuariHelper;
import es.caib.pinbal.core.model.Entitat;
import es.caib.pinbal.core.model.EntitatUsuari;
import es.caib.pinbal.core.model.Usuari;
import es.caib.pinbal.core.repository.EntitatRepository;
import es.caib.pinbal.core.repository.EntitatUsuariRepository;
import es.caib.pinbal.core.repository.ProcedimentServeiRepository;
import es.caib.pinbal.core.repository.UsuariRepository;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatUsuariNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatUsuariProtegitException;
import es.caib.pinbal.core.service.exception.UsuariExternNotFoundException;
import es.caib.pinbal.plugins.DadesUsuari;
import es.caib.pinbal.plugins.SistemaExternException;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementació dels mètodes per al manteniment de la taula d'usuaris.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class UsuariServiceImpl implements UsuariService {

	@Resource
	private EntitatRepository entitatRepository;
	@Resource
	private UsuariRepository usuariRepository;
	@Resource
	private EntitatUsuariRepository entitatUsuariRepository;
	@Resource
	private ProcedimentServeiRepository procedimentServeiRepository;

	@Resource
	private DtoMappingHelper dtoMappingHelper;
	@Resource
	private UsuariHelper usuariHelper;
	@Resource
	private PluginHelper externHelper;

	@Resource
	private MutableAclService aclService;

	@Transactional
	@Override
	public void inicialitzarUsuariActual() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		log.debug("Inicialitzant l'usuari (codi=" + auth.getName() + ")");
		usuariHelper.init(auth.getName());
	}

	@Transactional(readOnly = true)
	@Override
	public Page<EntitatUsuariDto> findAmbFiltrePaginat(
			Long id_entitat,
			Boolean isRepresentant,
			Boolean isDelegat,
			Boolean isAuditor,
			Boolean isAplicacio,
			String codi,
			String nom,
			String nif,
			String departament,
			Pageable pageable) {
		log.debug("Consulta d'entitats segons filtre (" +
				"codi=" + codi + ", " +
				"nom=" + nom + ", " +
				"nif=" + nif + ", " +
				"departament=" + departament + ")");
		Page<EntitatUsuari> paginaEntitats = entitatUsuariRepository.findByFiltre(
				id_entitat == null,
				entitatRepository.findOne(id_entitat),
				isRepresentant != null && isRepresentant == true,
				isDelegat != null && isDelegat == true,
				isAuditor != null && isAuditor == true,
				isAplicacio != null && isAplicacio == true,
				codi == null || codi.length() == 0,
				codi,
				nom == null || nom.length() == 0,
				nom,
				nif == null || nif.length() == 0,
				nif,
				departament == null || departament.length() == 0,
				departament,
				pageable);
		return dtoMappingHelper.pageEntities2pageDto(
				paginaEntitats,
				EntitatUsuariDto.class,
				pageable);
	}
	
	@Transactional(readOnly = true)
	@Override
	public UsuariDto getDades() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		log.debug("Consulta de les dades de l'usuari (codi=" + auth.getName() + ")");
		return dtoMappingHelper.getMapperFacade().map(
				usuariRepository.findOne(auth.getName()),
				UsuariDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public UsuariDto getDades(
			String usuariCodi) {
		log.debug("Consulta de les dades de l'usuari (codi=" + usuariCodi + ")");
		return dtoMappingHelper.getMapperFacade().map(
				usuariRepository.findOne(usuariCodi),
				UsuariDto.class);
	}

	@Transactional
	@Override
	public UsuariDto getUsuariActual() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		log.debug("Obtenint usuari actual");
		Usuari usuari = usuariRepository.findOne(auth.getName());
		if (usuari.getEmail() == null || usuari.getEmail().isEmpty()) {
			try {
				DadesUsuari dadesUsuari = externHelper.dadesUsuariConsultarAmbUsuariCodi(auth.getName());
				usuari.updateEmail(dadesUsuari.getEmail());
			} catch (SistemaExternException ex) {
				log.error("Error al consultar les dades de l'usuari (codi=" + auth.getName() + ") al sistema extern", ex);
			}
		}
		return toUsuariDtoAmbRols(
				usuari);
	}

	@Transactional
	@Override
	public UsuariDto updateUsuariActual(UsuariDto dto) {
		log.debug("Actualitzant configuració de usuari actual");
		Usuari usuari = usuariRepository.findOne(dto.getCodi());
		usuari.updateIdioma(
				dto.getIdioma());
		return toUsuariDtoAmbRols(usuari);
	}

	@Transactional(rollbackFor = EntitatNotFoundException.class)
	@Override
	public void actualitzarDadesAdmin(
			Long id,
			String codi,
			String nif,
			String departament,
			boolean representant,
			boolean delegat,
			boolean auditor,
			boolean aplicacio,
			boolean afegir) throws EntitatNotFoundException, UsuariExternNotFoundException {
		log.debug("Actualitzant dades no auditor de l'usuari (" +
				"codi=" + codi + ", " +
				"nif=" + nif + ") a l'entitat (" +
				"id=" + id + ")");
		Entitat entitat = entitatRepository.findOne(id);
		if (entitat == null) {
			log.debug("No s'ha trobat l'entitat (id=" + id + ")");
			throw new EntitatNotFoundException();
		}
		usuariActualitzarDades(
				entitat,
				codi,
				nif,
				departament,
				true,
				representant,
				true,
				delegat,
				true,
				auditor,
				true,
				aplicacio,
				afegir);
	}

	@Transactional(rollbackFor = EntitatNotFoundException.class)
	@Override
	public void actualitzarDadesRepresentant(
			Long id,
			String codi,
			String nif,
			String departament,
			boolean representant,
			boolean delegat,
			boolean aplicacio,
			boolean afegir) throws EntitatNotFoundException, EntitatUsuariProtegitException, UsuariExternNotFoundException {
		log.debug("Actualitzant dades no auditor de l'usuari (codi=" + codi + ", nif=" + nif + ") a l'entitat (id=" + id + ")");
		Entitat entitat = entitatRepository.findOne(id);
		if (entitat == null) {
			log.debug("No s'ha trobat l'entitat (id=" + id + ")");
			throw new EntitatNotFoundException();
		}
		EntitatUsuari entitatUsuari;
		boolean idPerNif = (codi == null || codi.isEmpty());
		if (idPerNif)
			entitatUsuari = entitatUsuariRepository.findByEntitatIdAndUsuariNif(
					id,
					nif);
		else
			entitatUsuari = entitatUsuariRepository.findByEntitatIdAndUsuariCodi(
					id,
					codi);
		if (entitatUsuari != null && entitatUsuari.isPrincipal()) {
			log.debug("No es pot modificar un usuari principal (codi=" + codi + ", nif=" + nif + ") de l'entitat (id=" + id + ")");
			throw new EntitatUsuariProtegitException();
		}
		usuariActualitzarDades(
				entitat,
				codi,
				nif,
				departament,
				true,
				representant,
				true,
				delegat,
				false,
				false,
				true,
				aplicacio,
				afegir);
	}

	@Transactional(rollbackFor = EntitatNotFoundException.class)
	@Override
	public void actualitzarDadesAuditor(
			Long id,
			String codi,
			String nif,
			boolean auditor,
			boolean afegir) throws EntitatNotFoundException, EntitatUsuariProtegitException, UsuariExternNotFoundException {
		log.debug("Actualitzant dades auditor de l'usuari (codi=" + codi + ", nif=" + nif + ") a l'entitat (id=" + id + ")");
		Entitat entitat = entitatRepository.findOne(id);
		if (entitat == null) {
			log.debug("No s'ha trobat l'entitat (id=" + id + ")");
			throw new EntitatNotFoundException();
		}
		EntitatUsuari entitatUsuari;
		boolean idPerNif = (codi == null || codi.isEmpty());
		if (idPerNif)
			entitatUsuari = entitatUsuariRepository.findByEntitatIdAndUsuariNif(
					id,
					nif);
		else
			entitatUsuari = entitatUsuariRepository.findByEntitatIdAndUsuariCodi(
					id,
					codi);
		if (entitatUsuari != null && entitatUsuari.isPrincipal()) {
			log.debug("No es pot modificar un usuari principal (codi=" + codi + ", nif=" + nif + ") de l'entitat (id=" + id + ")");
			throw new EntitatUsuariProtegitException();
		}
		usuariActualitzarDades(
				entitat,
				codi,
				nif,
				null,
				false,
				false,
				false,
				false,
				true,
				auditor,
				false,
				false,
				afegir);
	}

	@Transactional(rollbackFor = {EntitatNotFoundException.class, EntitatUsuariNotFoundException.class})
	@Override
	public boolean establirPrincipal(
			Long id,
			String usuariCodi) throws EntitatNotFoundException, EntitatUsuariNotFoundException {
		log.debug("Marca l'usuari com a principal (codi=" + usuariCodi + ") a l'entitat (id=" + id + ")");
		Entitat entitat = entitatRepository.findOne(id);
		if (entitat == null) {
			log.debug("No s'ha trobat l'entitat (id=" + id + ")");
			throw new EntitatNotFoundException();
		}
		EntitatUsuari entitatUsuari = entitatUsuariRepository.findByEntitatIdAndUsuariCodi(id, usuariCodi);
		if (entitatUsuari == null) {
			log.debug("L'entitat (id=" + id + ") no té actiu l'usuari (codi=" + usuariCodi + ")");
			throw new EntitatUsuariNotFoundException();
		}
		return entitatUsuari.canviPrincipal();
	}

	@Transactional(readOnly = true)
	@Override
	public List<InformeUsuariDto> informeUsuarisAgrupatsEntitatDepartament() {
		log.debug("Generant informe d'usuaris agrupats per entitat i departament");
		List<EntitatUsuari> usuaris = entitatUsuariRepository.findAllOrderByEntitatAndDepartament();
		Entitat entitatActual = null;
		String departamentActual = null;
		List<InformeUsuariDto> resposta = new ArrayList<InformeUsuariDto>();
		for (EntitatUsuari usuari: usuaris) {
			if (entitatActual == null)
				entitatActual = usuari.getEntitat();
			if (departamentActual == null)
				departamentActual = usuari.getDepartament();
			InformeUsuariDto informeUsuari = dtoMappingHelper.getMapperFacade().map(
					usuari,
					InformeUsuariDto.class);
			resposta.add(informeUsuari);
		}
		return resposta;
	}



	private void usuariActualitzarDades(
			Entitat entitat,
			String codi,
			String nif,
			String departament,
			boolean canviarRepresentant,
			boolean representant,
			boolean canviarDelegat,
			boolean delegat,
			boolean canviarAuditor,
			boolean auditor,
			boolean canviarAplicacio,
			boolean aplicacio,
			boolean afegir) throws UsuariExternNotFoundException {
		boolean idPerNif = (codi == null || codi.isEmpty());
		Usuari usuari;
		if (idPerNif) {
			usuari = usuariRepository.findByNif(nif);
		} else {
			usuari = usuariRepository.findByCodi(codi);
		}
		if (usuari == null || !usuari.isInicialitzat()) {
			// Consulta les dades de l'usuari al sistema extern
			DadesUsuari dadesUsuari = null;
			if (idPerNif) {
				try {
					dadesUsuari = externHelper.dadesUsuariConsultarAmbUsuariNif(nif);
				} catch (SistemaExternException ex) {
					log.warn("No s'han trobat les dades de l'usuari (nif=" + nif + ") al sistema extern");
					throw new UsuariExternNotFoundException();
				}
			} else {
				try {
					dadesUsuari = externHelper.dadesUsuariConsultarAmbUsuariCodi(codi);
				} catch (SistemaExternException ex) {
					log.warn("No s'han trobat les dades de l'usuari (codi=" + codi + ") al sistema extern");
					throw new UsuariExternNotFoundException();
				}
			}
			if (usuari == null) {
				// Si l'usuari no existeix el crea
				if (dadesUsuari != null) {
					usuari = usuariRepository.save(
							Usuari.getBuilderInicialitzat(
									dadesUsuari.getCodi(),
									dadesUsuari.getNom(),
									dadesUsuari.getNif()).build());
				} else {
					if (idPerNif) {
						usuari = usuariRepository.save(
								Usuari.getBuilderNoInicialitzatNif(
										nif).build());
					} else {
						usuari = usuariRepository.save(
								Usuari.getBuilderNoInicialitzatCodi(
										codi).build());
					}
				}
			} else {
				// Si l'usuari ja existeix actualitza les seves dades
				if (!usuari.isInicialitzat() && dadesUsuari != null) {
					log.debug("Actualitzant les dades de l'usuari (codi=" + usuari.getCodi() + ")");
					usuari = usuariHelper.moure(
							usuari,
							dadesUsuari,
							usuariRepository,
							procedimentServeiRepository,
							aclService);
				}
			}
		}
		// Crea un registre de tipus entitatUsuari si no existeix
		EntitatUsuari entitatUsuari;
		if (idPerNif) {
			entitatUsuari = entitatUsuariRepository.findByEntitatIdAndUsuariNif(
					entitat.getId(),
					nif);
		} else {
			entitatUsuari = entitatUsuariRepository.findByEntitatIdAndUsuariCodi(
					entitat.getId(),
					codi);
		}
		if (entitatUsuari == null) {
			entitatUsuari = EntitatUsuari.getBuilder(
					entitat,
					usuari,
					departament,
					representant,
					delegat,
					auditor,
					aplicacio).build();
			entitatUsuariRepository.save(entitatUsuari);
		} else {
			boolean representatPerUpdate = (canviarRepresentant) ? representant : entitatUsuari.isRepresentant();
			boolean delegatPerUpdate = (canviarDelegat) ? delegat : entitatUsuari.isDelegat();
			boolean auditorPerUpdate = (canviarAuditor) ? auditor : entitatUsuari.isAuditor();
			boolean aplicacioPerUpdate = (canviarAplicacio) ? aplicacio : entitatUsuari.isAuditor();
			if (afegir) {
				representatPerUpdate = entitatUsuari.isRepresentant() || representant;
				delegatPerUpdate = entitatUsuari.isDelegat() || delegat;
				auditorPerUpdate = entitatUsuari.isAuditor() || auditor;
				aplicacioPerUpdate = entitatUsuari.isAplicacio() || aplicacio;
			}
			entitatUsuari.update(
					departament,
					representatPerUpdate,
					delegatPerUpdate,
					auditorPerUpdate,
					aplicacioPerUpdate);
		}
	}

	private UsuariDto toUsuariDtoAmbRols(
			Usuari usuari) {
		UsuariDto dto = dtoMappingHelper.convertir(
				usuari,
				UsuariDto.class);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth.getAuthorities() != null) {
			String[] rols = new String[auth.getAuthorities().size()];
			int index = 0;
			for (GrantedAuthority grantedAuthority: auth.getAuthorities()) {
				rols[index++] = grantedAuthority.getAuthority();
			}
			dto.setRols(rols);
		}
		return dto;
	}

}
