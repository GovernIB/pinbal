/**
 * 
 */
package es.caib.pinbal.core.repository;

import es.caib.pinbal.core.dto.IntegracioAccioEstatEnumDto;
import es.caib.pinbal.core.dto.IntegracioAccioTipusEnumDto;
import es.caib.pinbal.core.model.IntegracioAccioEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar un monitor d'integració
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface IntegracioAccioRepository extends JpaRepository<IntegracioAccioEntity, Long> {
	
	@Query( " from IntegracioAccioEntity mon " +
			"where lower(mon.codi) like lower(:codiMonitor) " +
			"  and (:isDataNula = true or mon.data between :data and :dataFi) " +
			"  and (:isNullDescripcio = true or lower(mon.descripcio) like lower('%'||:descripcio||'%')) " +
			"  and (:isNullEstat = true or mon.estat like :estat) " +
			"  and (:isNullTipus = true or mon.tipus like :tipus) " +
			"  and (:isNullPeticio = true or lower(mon.idPeticio) like lower('%'||:idPeticio||'%')) " +
			"order by mon.id desc")
	Page<IntegracioAccioEntity> findByFiltrePaginat(
			@Param("codiMonitor") String codiMonitor, 
			@Param("isDataNula") boolean isDataNula, 
			@Param("data") Date data, 
			@Param("dataFi") Date dataFi, 
			@Param("isNullDescripcio") boolean isNullDescripcio, 
			@Param("descripcio") String descripcio,		
			@Param("isNullEstat") boolean isNullEstat, 
			@Param("estat") IntegracioAccioEstatEnumDto estat,
			@Param("isNullTipus") boolean isNullTipus, 
			@Param("tipus") IntegracioAccioTipusEnumDto tipus,
			@Param("isNullPeticio") boolean isNullPeticio, 
			@Param("idPeticio") String idPeticio,		
			Pageable pageable);
	
	@Query( "select count(mon) " +
			"from " + 
			"IntegracioAccioEntity mon " +  
			"where " + 
			"lower(mon.codi) like lower(:codi)")
	public Integer countByCodi(
			@Param("codi") String codi);
	
	@Query( "select count(mon) " +
			"from " + 
			"IntegracioAccioEntity mon "  
			)
	public Integer countAll();
	
	/** Esborra les dades filtrant pel codi */
	@Modifying
	@Query("delete from IntegracioAccioEntity mon " +
			"where mon.codi = :codi")
	public void deleteByCodiMonitor(
			@Param("codi") String codi);
	
	/** Esborra totes les dades */
	@Modifying
	@Query("delete from IntegracioAccioEntity mon ")
	public void deleteAll();
	
	@Query(	"select mon.codi, count(mon)" +
			"from IntegracioAccioEntity mon " +
			"where mon.estat = 'ERROR' " + 
			"and mon.data >= :dataInici " + 
			"group by mon.codi ")
	public List<Object[]> countErrorsGroupByCodi(
			@Param("dataInici") Date dataInici);
	
	@Query(	" 	select count(mon) " + 
			"	from IntegracioAccioEntity mon " +
			"	where mon.data < :data ")
	public Integer countMonitorByDataBefore(
			@Param("data") Date data);
	
	/** Esborra les dades anteriors a la data passada per paràmetre. */
	@Modifying
	@Query(	"delete from IntegracioAccioEntity mon " +
			"where mon.data < :data ")
	public void deleteDataBefore(
			@Param("data") Date data);

	@Query(	"select count(mon)" +
			"from IntegracioAccioEntity mon " +
			"where mon.estat = 'ERROR' " +
			"and mon.codi= :codi")
	long countErrorsByCodi(@Param("codi") String codi);

	@Query(	"select mon.codi, count(mon)" +
			"from IntegracioAccioEntity mon " +
			"where mon.estat = 'ERROR' " +
			"group by mon.codi ")
	public List<Object[]> countErrorsGroupByCodi();
}

