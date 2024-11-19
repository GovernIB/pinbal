/**
 * 
 */
package es.caib.pinbal.core.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.pinbal.core.model.IntegracioAccioParamEntity;

/**
 * Definició dels mètodes necessaris per a gestionar un parametre del monitor d'integració
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface IntegracioAccioParamRepository extends JpaRepository<IntegracioAccioParamEntity, Long> {
	
	/** Esborra els paràmetres per codi el monitor. */
	@Modifying
	@Query("delete IntegracioAccioParamEntity monParam " +
			"where monParam.integracioAccio.id in " +
			"( 	select mon.id " + 
			"	from IntegracioAccioEntity mon " +
			"	where mon.codi = :codi )")
	public void deleteByIntegracioAccioCodi(
			@Param("codi") String codi);
	
	/** Esborra tots els paràmetres del monitor. */
	@Modifying
	@Query("delete IntegracioAccioParamEntity monParam")
	public void deleteAll();
	
	/** Esborra les dades anteriors a la data passada per paràmetre. */
	@Modifying
	@Query("delete IntegracioAccioParamEntity monParam " +
			"where monParam.integracioAccio.id in " +
			"( 	select mon.id " + 
			"	from IntegracioAccioEntity mon " +
			"	where mon.data < :data )")
	public void deleteDataBefore(
			@Param("data") Date data);

}

