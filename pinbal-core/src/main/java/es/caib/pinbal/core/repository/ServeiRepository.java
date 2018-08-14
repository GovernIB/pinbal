/**
 * 
 */
package es.caib.pinbal.core.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.pinbal.core.model.Servei;


/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ServeiRepository extends JpaRepository<Servei, Long> {

	public Servei findByCodi(String codi);

	@Query(	"select " +
			"    sen " +
			"from " +
			"    Servei sen " +
			"where " +
			"    sen in (:serveisPermesos)")
	public Page<Servei> findPermesosPaginat(
			@Param("serveisPermesos") List<Servei> serveisPermesos,
			Pageable pageable);

	@Query(	"select " +
			"    sen " +
			"from " +
			"    Servei sen " +
			"where " +
			"    sen in (:serveisPermesos)")
	public List<Servei> findPermesosOrdenat(
			@Param("serveisPermesos") List<Servei> serveisPermesos,
			Sort sort);

	@Query(	"select " +
			"    sen " +
			"from " +
			"    Servei sen " +
			"where " +
			"    sen.codi in (:serveiCodis) " +
			"order by " +
			"    sen.nom asc")
	public List<Servei> findServeisPerCodis(
			@Param("serveiCodis") List<String> serveiCodis);


}
