/**
 * 
 */
package es.caib.pinbal.core.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.pinbal.core.dto.PaginaLlistatDto;
import es.caib.pinbal.core.dto.PaginacioAmbOrdreDto;
import es.caib.pinbal.core.dto.ParamConfDto;
import es.caib.pinbal.core.service.exception.ParamConfNotFoundException;

/**
 * Declaració dels mètodes per a obtenir i modificar
 * informació d'un paràmetre de configuració.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ScspService {
	
	/**
	 * Consulta una entitat donat el seu id.
	 * 
	 * @param nom
	 * 			Atribut nom del paràmetre de configuració a trobar.
	 *            
	 * @return	El paràmetre de configuració trobat. Si no s'ha trobat cap retorna null.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ParamConfDto findByNom(String nom);
	
	/**
	 * Crea un nou paràmetre de configuració.
	 * 
	 * @param dto
	 *            Informació per crear un nou paràmetre de configuració.
	 *            
	 * @return El paràmetre de configuració creat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ParamConfDto create(ParamConfDto dto);

	/**
	 * Actualitza la informació d'un paràmetre de configuració.
	 * 
	 * @param dto
	 *            Informació del paràmetre de configuració a modificar.
	 *            
	 * @return El paràmetre de configuració modificada.
	 * 
	 * @throws ParamConfNotFoundException
	 *             Si no s'ha trobat troba cap paràmetre de configuració
	 *             amb el nom especificat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ParamConfDto update(ParamConfDto dto) throws ParamConfNotFoundException;

	/**
	 * Esborra un paràmetre de configuració.
	 * 
	 * @param nom
	 *            Atribut nom del paràmetre de configuració a esborrar.
	 *            
	 * @return El paràmetre de configuració esborrat.
	 * 
	 * @throws ParamConfNotFoundException
	 *             Si no s'ha trobat cap paràmetre de configuració
	 *             amb el nom especificat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ParamConfDto delete(String nom) throws ParamConfNotFoundException;

	/**
	 * Llistat amb tots els paràmetres de configuració.
	 * 
	 * @return Un llistat de paràmetres de configuració.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<ParamConfDto> findAll();
	
	/**
	 * Llistat amb tots els paràmetres de configuració paginats.
	 * 
	 * @return Un llistat de paràmetres de configuració paginats.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public PaginaLlistatDto<ParamConfDto> findAllPaginat(
			PaginacioAmbOrdreDto paginacioAmbOrdre);
	
}
