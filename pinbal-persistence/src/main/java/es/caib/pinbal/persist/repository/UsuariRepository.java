/**
 * 
 */
package es.caib.pinbal.persist.repository;

import es.caib.pinbal.persist.entity.Usuari;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.QueryHint;

import java.util.List;
import java.util.Optional;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un usuari que està emmagatzemat a dins la base de
 * dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface UsuariRepository extends JpaRepository<Usuari, String> {

	public Usuari findByCodi(String codi);

	public Usuari findByNif(String nif);

	@Query("from Usuari " +
			"where lower(codi) like lower(concat('%', :text, '%')) " +
			"   or lower(nom) like lower(concat('%', :text, '%'))")
	public List<Usuari> findByCodiOrNom(@Param("text") String text);

	@Query("from Usuari " +
			"where lower(codi) like lower(concat('%', :text, '%')) " +
			"   or lower(nom) like lower(concat('%', :text, '%')) " +
			"   or lower(nif) like lower(concat('%', :text, '%'))")
	public List<Usuari> findByCodiOrNomOrNif(@Param("text") String text);


    @Query("select distinct u.codi from Usuari u")
    List<String> findAllCodis();


	@Modifying
	@Query(value = "UPDATE ACL_SID SET SID = :codiNou " +
			"WHERE SID = :codiAntic " +
			"AND PRINCIPAL = 1 " +
			"AND NOT EXISTS (SELECT 1 " +
			"		FROM ACL_SID " +
			"		WHERE SID = :codiNou " +
			"		AND PRINCIPAL = 1)", nativeQuery = true)
	int updateUsuariPermis(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);


	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	@Query("from Usuari u where u.codi = :codi")
	Optional<Usuari> getByCodiReadOnlyNewTransaction(@Param("codi") String codi);

	// El proveïdor d'auditoria (AuditorAware) crida aquest mètode DINS del callback preUpdate del
	// flush per resoldre el lastModifiedBy. Amb flushMode=AUTO (per defecte) la consulta dispararia
	// un auto-flush, que tornaria a entrar a preUpdate -> auditor -> getByCodi -> auto-flush...
	// provocant una recursió infinita (StackOverflowError). Amb flushMode=COMMIT la consulta no fa
	// auto-flush però segueix participant en la transacció actual (no esgota el pool de connexions).
	@QueryHints(@QueryHint(name = "org.hibernate.flushMode", value = "COMMIT"))
	Optional<Usuari> getByCodi(String name);

}
