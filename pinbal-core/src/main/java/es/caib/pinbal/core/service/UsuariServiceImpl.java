/**
 * 
 */
package es.caib.pinbal.core.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.EntitatUsuariDto;
import es.caib.pinbal.core.dto.InformeUsuariDto;
import es.caib.pinbal.core.dto.PaginaLlistatDto;
import es.caib.pinbal.core.dto.PaginacioAmbOrdreDto;
import es.caib.pinbal.core.dto.UsuariDto;
import es.caib.pinbal.core.helper.DtoMappingHelper;
import es.caib.pinbal.core.helper.PaginacioHelper;
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

/**
 * Implementació dels mètodes per al manteniment de la taula d'usuaris.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
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
		LOGGER.debug("Inicialitzant l'usuari (codi=" + auth.getName() + ")");
		usuariHelper.init(auth.getName());
	}

	@Transactional(readOnly = true)
	@Override
	@SuppressWarnings("unchecked")
	public PaginaLlistatDto<EntitatUsuariDto> findAmbFiltrePaginat(
			Long id_entitat,
			Boolean isRepresentant,
			Boolean isDelegat,
			Boolean isAuditor,
			Boolean isAplicacio,
			String codi,
			String nom,
			String nif,
			String departament,
			PaginacioAmbOrdreDto paginacioAmbOrdre) {
		LOGGER.debug("Consulta d'entitats segons filtre (codi=" + codi + ", nom=" + nom + ""
				+ "nif=" + nif + " Departament=" + departament + ")");
		
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
				PaginacioHelper.toSpringDataPageable(
						paginacioAmbOrdre,
						null));
		return PaginacioHelper.toPaginaLlistatDto(
				paginaEntitats,
				dtoMappingHelper,
				EntitatUsuariDto.class);
	}
	
	@Transactional(readOnly = true)
	@Override
	public UsuariDto getDades() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		LOGGER.debug("Consulta de les dades de l'usuari (codi=" + auth.getName() + ")");
		return dtoMappingHelper.getMapperFacade().map(
				usuariRepository.findOne(auth.getName()),
				UsuariDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public UsuariDto getDades(
			String usuariCodi) {
		LOGGER.debug("Consulta de les dades de l'usuari (codi=" + usuariCodi + ")");
		return dtoMappingHelper.getMapperFacade().map(
				usuariRepository.findOne(usuariCodi),
				UsuariDto.class);
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
		LOGGER.debug("Actualitzant dades no auditor de l'usuari (codi=" + codi + ", nif=" + nif + ") a l'entitat (id=" + id + ")");
		Entitat entitat = entitatRepository.findOne(id);
		if (entitat == null) {
			LOGGER.debug("No s'ha trobat l'entitat (id=" + id + ")");
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
		LOGGER.debug("Actualitzant dades no auditor de l'usuari (codi=" + codi + ", nif=" + nif + ") a l'entitat (id=" + id + ")");
		Entitat entitat = entitatRepository.findOne(id);
		if (entitat == null) {
			LOGGER.debug("No s'ha trobat l'entitat (id=" + id + ")");
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
			LOGGER.debug("No es pot modificar un usuari principal (codi=" + codi + ", nif=" + nif + ") de l'entitat (id=" + id + ")");
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
		LOGGER.debug("Actualitzant dades auditor de l'usuari (codi=" + codi + ", nif=" + nif + ") a l'entitat (id=" + id + ")");
		Entitat entitat = entitatRepository.findOne(id);
		if (entitat == null) {
			LOGGER.debug("No s'ha trobat l'entitat (id=" + id + ")");
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
			LOGGER.debug("No es pot modificar un usuari principal (codi=" + codi + ", nif=" + nif + ") de l'entitat (id=" + id + ")");
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
		LOGGER.debug("Marca l'usuari com a principal (codi=" + usuariCodi + ") a l'entitat (id=" + id + ")");
		Entitat entitat = entitatRepository.findOne(id);
		if (entitat == null) {
			LOGGER.debug("No s'ha trobat l'entitat (id=" + id + ")");
			throw new EntitatNotFoundException();
		}
		EntitatUsuari entitatUsuari = entitatUsuariRepository.findByEntitatIdAndUsuariCodi(id, usuariCodi);
		if (entitatUsuari == null) {
			LOGGER.debug("L'entitat (id=" + id + ") no té actiu l'usuari (codi=" + usuariCodi + ")");
			throw new EntitatUsuariNotFoundException();
		}
		return entitatUsuari.canviPrincipal();
	}

	@Transactional(readOnly = true)
	@Override
	public List<InformeUsuariDto> informeUsuarisAgrupatsEntitatDepartament() {
		LOGGER.debug("Generant informe d'usuaris agrupats per entitat i departament");
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
					LOGGER.warn("No s'han trobat les dades de l'usuari (nif=" + nif + ") al sistema extern");
					throw new UsuariExternNotFoundException();
				}
			} else {
				try {
					dadesUsuari = externHelper.dadesUsuariConsultarAmbUsuariCodi(codi);
				} catch (SistemaExternException ex) {
					LOGGER.warn("No s'han trobat les dades de l'usuari (codi=" + codi + ") al sistema extern");
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
					LOGGER.debug("Actualitzant les dades de l'usuari (codi=" + usuari.getCodi() + ")");
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

	private static final Logger LOGGER = LoggerFactory.getLogger(UsuariServiceImpl.class);

}
