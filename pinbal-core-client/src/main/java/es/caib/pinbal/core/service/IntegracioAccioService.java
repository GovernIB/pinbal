package es.caib.pinbal.core.service;

import es.caib.pinbal.core.dto.IntegracioAccioDto;
import es.caib.pinbal.core.dto.IntegracioDto;
import es.caib.pinbal.core.dto.IntegracioFiltreDto;
import es.caib.pinbal.core.dto.PaginaDto;
import es.caib.pinbal.core.dto.PaginacioAmbOrdreDto;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Declaració dels mètodes per a la gestió de meta-expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface IntegracioAccioService {
	
//	@PreAuthorize("hasRole('ROLE_ADMIN')")
//	public List<IntegracioAccioDto> findAll();
	
	/**
	 * Crea un nou item monitorIntegracio.
	 * 
	 * @param monitorIntegracio
	 *            Informació de l'item monitorIntegracio a crear.
	 * @return El/La MonitorIntegracio creat/creada
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public IntegracioAccioDto create(IntegracioAccioDto integracioAccio);
	
	/**
	 * Llistat amb tots els items monitorIntegracio paginats.
	 * 
	 * @param paginacioParams
	 *            Paràmetres per a dur a terme la paginació del resultats.
	 * @return La pàgina d'items MonitorIntegracio.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public PaginaDto<IntegracioAccioDto> findPaginat(PaginacioAmbOrdreDto paginacioParams, IntegracioFiltreDto integracioFiltreDto);

	/** Mètode per esborrar dades per a una integració específica.
	 * 
	 * @param codi Codi de la integració a esborrar.
	 * @return Retorna el número de registres esborrats.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public int delete(String codi);
	
	public int deleteAll();
	
	/** Mètode per esborrar dades anteriors a una data passada per paràmetre */
	public int esborrarDadesAntigues(Date data);
	
	/** 
	 * Esborra les dades antigues del monitor d'integracions
	 */
	public void esborrarDadesAntigesMonitorIntegracio();
	
	/** Consulta el número d'errors per integració. */
	public Map<String, Integer> countErrors(int numeroHores);
	
	/**
	 * Obté les integracions disponibles.
	 * 
	 * @return La llista d'integracions.
	 */
	public List<IntegracioDto> integracioFindAll();

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	List<IntegracioDto> getAll();

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	IntegracioAccioDto findById(Long id);
}
