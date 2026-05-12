package es.caib.pinbal.persist.repository.acl;

import es.caib.pinbal.persist.entity.acl.AclClassEntity;
import es.caib.pinbal.persist.entity.acl.AclObjectIdentityEntity;
import es.caib.pinbal.persist.entity.acl.AclSidEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus ACL-SID.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AclObjectIdentityRepository extends JpaRepository<AclObjectIdentityEntity, Long> {

	@Query(	"select distinct oi.objectId " +
			"from AclObjectIdentityEntity oi JOIN oi.entries entry " +
			"where oi.classname.classname = :classname   " +
			" and  entry.sid in (:sids)   " +
			" and  entry.mask in (:masks)   " +
			" and  entry.granting = true   ")
	List<Long> findObjectsIdWithAnyPermissions(
			@Param("classname") String classname,
			@Param("sids") List<AclSidEntity> sids,
			@Param("masks") List<Integer> masks);

	@Query(
			"select distinct oi.objectId " +
					"from AclObjectIdentityEntity oi JOIN oi.entries entry " +
					"where oi.classname.classname = :classname " +
					"and entry.sid in (:sids) " +
					"and entry.granting = true " +
					"and size(oi.entries) = :maskCount " +
					"and entry.mask in (:masks) " +
					"group by oi.objectId " +
					"having count(distinct entry.mask) = :maskCount"
	)
	List<Long> findObjectsIdWithAllPermissions(
			@Param("classname") String classname,
			@Param("sids") List<AclSidEntity> sids,
			@Param("masks") List<Integer> masks,
			@Param("maskCount") int maskCount);


	@Query(	"select " +
			"   CASE WHEN count(oi) > 0 THEN true ELSE false END " +
			"from " +
			"    AclObjectIdentityEntity oi JOIN oi.entries entry " +
			"where " +
			"      oi.classname.classname = :classname   " +
			" and  entry.sid in (:sids)   " +
			" and  entry.mask in (:masks)   " +
			" and  entry.granting = true   ")
	boolean hasObjectsWithAnyPermissions(
			@Param("classname") String classname,
			@Param("sids") List<AclSidEntity> sids,
			@Param("masks") List<Integer> masks);

	AclObjectIdentityEntity findByClassnameAndObjectId(AclClassEntity classname, Long id);
}
