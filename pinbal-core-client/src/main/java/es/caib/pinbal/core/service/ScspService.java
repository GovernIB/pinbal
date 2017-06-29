/**
 * 
 */
package es.caib.pinbal.core.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.pinbal.core.dto.EmissorCertDto;
import es.caib.pinbal.core.dto.ParamConfDto;
import es.caib.pinbal.core.dto.ClauPublicaDto;
import es.caib.pinbal.core.service.exception.ClauPublicaNotFoundException;
import es.caib.pinbal.core.service.exception.EmissorCertNotFoundException;
import es.caib.pinbal.core.service.exception.ParamConfNotFoundException;

/**
 * Declaració dels mètodes per a obtenir i modificar
 * informació de les taules SCSP
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ScspService {
	
	
	// Funcions de la taula de paràmetres de configuració.
	
	/**
	 * Consulta una entitat donat el seu id.
	 * 
	 * @param nom
	 * 			Atribut nom del paràmetre de configuració a trobar.
	 *            
	 * @return	El paràmetre de configuració trobat. Si no s'ha trobat cap retorna null.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ParamConfDto findParamConfByNom(String nom);
	
	/**
	 * Crea un nou paràmetre de configuració.
	 * 
	 * @param dto
	 *            Informació per crear un nou paràmetre de configuració.
	 *            
	 * @return El paràmetre de configuració creat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ParamConfDto createParamConf(ParamConfDto dto);

	/**
	 * Actualitza la informació d'un paràmetre de configuració.
	 * 
	 * @param dto
	 *            Informació del paràmetre de configuració a modificar.
	 *            
	 * @return El paràmetre de configuració modificat.
	 * 
	 * @throws ParamConfNotFoundException
	 *             Si no s'ha trobat troba cap paràmetre de configuració
	 *             amb el nom especificat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ParamConfDto updateParamConf(ParamConfDto dto) throws ParamConfNotFoundException;

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
	public ParamConfDto deleteParamConf(String nom) throws ParamConfNotFoundException;

	/**
	 * Llistat amb tots els paràmetres de configuració.
	 * 
	 * @return Un llistat de paràmetres de configuració.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<ParamConfDto> findAllParamConf();
	
	
	// Funcions de la taula de emissor certificat.
	
	/**
	 * Consulta un emissor certificat donat el seu id.
	 * 
	 * @param id
	 * 			L'dentificador del emissor certificat a trobar.
	 *            
	 * @return	El emissor certificat trobat. Si no s'ha trobat cap retorna null.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public EmissorCertDto findEmissorCertById(Long id);
	
	/**
	 * Crea un nou emissor certificat.
	 * 
	 * @param dto
	 *            La informació per crear un nou emissor certificat.
	 *            
	 * @return El emissor certificat creat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public EmissorCertDto createEmissorCert(EmissorCertDto dto);

	/**
	 * Actualitza la informació d'un emissor certificat.
	 * 
	 * @param dto
	 *            Informació del emissor certificat a modificar.
	 *            
	 * @return El emissor certificat modificat.
	 * 
	 * @throws EmissorCertNotFoundException
	 *             Si no s'ha trobat troba cap emissor certificat
	 *             amb el id especificat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public EmissorCertDto updateEmissorCert(EmissorCertDto dto) throws EmissorCertNotFoundException;

	/**
	 * Esborra un emissor certificat.
	 * 
	 * @param id
	 *            L'dentificador del emissor certificat a esborrar.
	 *            
	 * @return El emissor certificat esborrat.
	 * 
	 * @throws EmissorCertNotFoundException
	 *             Si no s'ha trobat cap emissor certificat
	 *             amb el nom especificat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public EmissorCertDto deleteEmissorCert(Long id) throws EmissorCertNotFoundException;

	/**
	 * Llistat amb tots els emissors certificats.
	 * 
	 * @return Un llistat de emissors certificats.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<EmissorCertDto> findAllEmissorCert();
	
	
	// Funcions de la taula de claus públiques.
	
	/**
	 * Consulta una clau pública donat el seu id.
	 * 
	 * @param id
	 * 			L'dentificador de la clau pública a trobar.
	 *            
	 * @return	La clau pública trobada. Si no s'ha trobat cap retorna null.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ClauPublicaDto findClauPublicaById(Long id);
	
	/**
	 * Crea una nova clau pública.
	 * 
	 * @param dto
	 *            La informació per crear una nova clau pública.
	 *            
	 * @return El clau pública creat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ClauPublicaDto createClauPublica(ClauPublicaDto dto);

	/**
	 * Actualitza la informació d'una clau pública.
	 * 
	 * @param dto
	 *            Informació de la clau pública a modificar.
	 *            
	 * @return El clau pública modificada.
	 * 
	 * @throws ClauPublicaNotFoundException
	 *             Si no s'ha trobat troba cap clau pública
	 *             amb el id especificat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ClauPublicaDto updateClauPublica(ClauPublicaDto dto) throws ClauPublicaNotFoundException;

	/**
	 * Esborra una clau pública.
	 * 
	 * @param id
	 *            L'identificador de la clau pública a esborrar.
	 *            
	 * @return El clau pública esborrada.
	 * 
	 * @throws ClauPublicaNotFoundException
	 *             Si no s'ha trobat cap clau pública
	 *             amb el nom especificat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ClauPublicaDto deleteClauPublica(Long id) throws ClauPublicaNotFoundException;

	/**
	 * Llistat amb totes les claus públiques.
	 * 
	 * @return Un llistat de claus públiques.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<ClauPublicaDto> findAllClauPublica();
	
}
