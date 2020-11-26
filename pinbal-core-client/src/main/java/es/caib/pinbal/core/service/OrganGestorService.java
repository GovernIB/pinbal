package es.caib.pinbal.core.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.pinbal.core.dto.OrganGestorDto;
import es.caib.pinbal.core.service.exception.NotFoundException;

/**
 * Declaració dels mètodes per a la gestió de meta-expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface OrganGestorService {

	/**
	 * Obté una llista amb tots els organs gestors de la base de dades
	 * 
	 * @return Llistat de tots els organs gestors
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_REPRES')")
	public List<OrganGestorDto> findAll();

	/**
	 * Obté l'element amb l'identificador donat.
	 * 
	 * @param id Identificador de l'element a consultar
	 * 
	 * @return L'objecte del registre consultat.
	 * @throws NotFoundException Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_REPRES')")
	public OrganGestorDto findItem(Long id) throws NotFoundException;

	/**
	 * Obté una llista amb tots els organs gestors de l'entitat especificada per
	 * paràmetre.
	 * 
	 * @param entitatId Id de l'entitat.
	 * @return Llistat dels organs gestors de l'entitat
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_REPRES')")
	public List<OrganGestorDto> findByEntitat(Long entitatId);

	/**
	 * Consulta tots els organs gestors de l'entitat actual de forma paginada i
	 * aplicant el filtre.
	 * 
	 * @param entitatId       Identificador de l'entitat actual
	 * @param filtre          Filtre a aplicar als resultats
	 * @param paginacioParams Paràmetres per a dur a terme la paginació del
	 *                        resultats.
	 * @return La pàgina amb els organs gestors
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_REPRES')")
	public Page<OrganGestorDto> findPageOrgansGestorsAmbFiltrePaginat(Long entitatId, String filtre, Pageable pageable);

	/**
	 * Actualitza els organs gestors de la base de dades amb els de Dir3
	 * 
	 * @param entitatId Identificador de l'entitat actual
	 * @return Indica si la sincronització ha tengut èxit
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_REPRES')")
	public boolean syncDir3OrgansGestors(Long entitatId) throws Exception;

}
