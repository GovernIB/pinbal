/**
 * 
 */
package es.caib.pinbal.core.service;

import es.caib.pinbal.client.comu.EntitatInfo;
import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.EntitatDto.EntitatTipusDto;
import es.caib.pinbal.core.dto.OrganGestorDto;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatServeiNotFoundException;
import es.caib.pinbal.core.service.exception.ServeiNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Declaració dels mètodes per a obtenir i modificar informació d'una entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EntitatService {

	/**
	 * Crea una nova entitat.
	 * 
	 * @param creada
	 *            Informació de l'entitat a crear.
	 * @return L'entitat creada.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public EntitatDto create(EntitatDto creada);

	/**
	 * Actualitza la informació d'una entitat.
	 * 
	 * @param modificada
	 *            Informació de l'entitat a modificar.
	 * @return L'entitat modificada.
	 * @throws EntitatNotFoundException
	 *             Si no s'ha trobat troba cap entitat amb l'id especificat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public EntitatDto update(EntitatDto modificada) throws EntitatNotFoundException;

	/**
	 * Esborra una entitat.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a esborrar.
	 * @return L'entitat esborrada.
	 * @throws EntitatNotFoundException
	 *             Si no s'ha trobat cap entitat amb l'id especificat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public EntitatDto delete(Long entitatId) throws EntitatNotFoundException;

	/**
	 * Llistat amb totes les entitats.
	 * 
	 * @return Un llistat d'entitats.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<EntitatDto> findAll();


	public List<EntitatInfo> getEntitatsInfo();

	/**
	 * Consulta les entitats segons el filtre.
	 * 
	 * @param codi
	 *            Codi a cercar.
	 * @param nom
	 *            Nom a cercar.
	 * @param pageable
	 *            Paràmetres per a la paginació i ordenació dels resultats.
	 * @param unitatArrel 
	 * 				Unitatat arrel a cercar
	 * @return El llistat d'entitats paginat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public Page<EntitatDto> findAmbFiltrePaginat(
			String codi,
			String nom,
			String cif,
			Boolean activa,
			String tipus,
			Pageable pageable, 
			String unitatArrel);
	
	/**
	 * Consulta una entitat donat el seu id.
	 * 
	 * @param id
	 *            Atribut id de l'entitat a trobar.
	 * @return L'entitat trobada. Si no s'ha trobat cap entitat retorna null.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_REPRES') or hasRole('ROLE_DELEG') or hasRole('ROLE_AUDIT') or hasRole('ROLE_SUPERAUD')")
	public EntitatDto findById(Long id);

	/**
	 * Consulta una entitat donat el seu codi.
	 * 
	 * @param codi
	 *            Atribut codi de l'entitat a trobar.
	 * @return L'entitat trobada. Si no s'ha trobat cap entitat retorna null.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public EntitatDto findByCodi(String codi);
	
	/**
	 * Consulta la primera entitat amb el tipus donat.
	 * 
	 * @param tipus
	 *            Atribut tipus de l'entitat a trobar.
	 * @return L'entitat trobada. Si no s'ha trobat cap entitat retorna null.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public EntitatDto findTopByTipus(EntitatTipusDto codi);
	
	/**
	 * Consulta una entitat donat el seu cif.
	 * 
	 * @param cif
	 *            Atribut cif de l'entitat a trobar.
	 * @return L'entitat trobada. Si no s'ha trobat cap entitat retorna null.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public EntitatDto findByCif(String cif);

	/**
	 * Obté una llista amb tots els òrgans gestors de l'entitat.
	 * 
	 * @param id
	 *            Identificador de l'entitat
	 * @return Llista amb tots els organs gestors asociats a la entitat
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_REPRES')")
	public List<OrganGestorDto> getOrgansGestors(Long id);
	
	/**
	 * Activa o desactiva l'entitat.
	 * 
	 * @param id
	 *            Atribut id de l'entitat a modificar.
	 * @param activa
	 *            Valor de la propietat activa a configurar.
	 * @return L'entitat modificada.
	 * @throws EntitatNotFoundException
	 *             Si no s'ha trobat troba cap entitat amb l'id especificat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public EntitatDto updateActiva(Long id, boolean activa) throws EntitatNotFoundException;

	/**
	 * Afegeix un servei a una entitat.
	 * 
	 * @param id
	 *            Atribut id de l'entitat.
	 * @param serveiCodi
	 *            Codi del servei.
	 * @throws EntitatNotFoundException
	 *            Si no hi ha cap entitat amb l'id especificat.
	 * @throws ServeiNotFoundException
	 *            Si no hi ha cap servei disponible amb el codi especificat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void addServei(Long id, String serveiCodi) throws EntitatNotFoundException, ServeiNotFoundException;

	/**
	 * Esborra un servei d'una entitat.
	 * 
	 * @param id
	 *            Atribut id de l'entitat.
	 * @param serveiCodi
	 *            Codi del servei.
	 * @throws EntitatNotFoundException
	 *            Si no hi ha cap entitat amb l'id especificat.
	 * @throws EntitatServeiNotFoundException
	 *            Si l'entitat no té aquest servei afegit.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void removeServei(Long id, String serveiCodi) throws EntitatNotFoundException, EntitatServeiNotFoundException;

	/**
	 * Consulta les entitats per a un usuari.
	 * 
	 * @param usuariCodi
	 *            Codi de l'usuari.
	 * @return El llistat d'entitats.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_REPRES') or hasRole('ROLE_DELEG') or hasRole('ROLE_AUDIT') or hasRole('ROLE_SUPERAUD')")
	public List<EntitatDto> findActivesAmbUsuariCodi(String usuariCodi);

	/**
	 * Consulta les entitats associades a un determinat emissor.
	 * Per a obtenir el tipus de les entitats a mostrar es fa la conversió
	 * del cif cap al seu valor numèric i s'agafa com a índex per a l'enumerat
	 * de tipus.
	 *  
	 * @param serveiCodi
	 *            Codi del servei.
	 * @return El llistat d'entitats.
	 * @throws ServeiNotFoundException
	 *            Si no hi ha cap servei amb el codi especificat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<EntitatDto> findDisponiblesPerRedireccionsBus(String serveiCodi) throws ServeiNotFoundException;

}
