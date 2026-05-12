/**
 * 
 */
package es.caib.pinbal.persist.repository.acl;

import es.caib.pinbal.persist.entity.acl.AclEntryEntity;
import es.caib.pinbal.persist.entity.acl.AclObjectIdentityEntity;
import es.caib.pinbal.persist.entity.acl.AclSidEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus ACL-SID.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AclEntryRepository extends JpaRepository<AclEntryEntity, Long> {

    Optional<AclEntryEntity> findById(Long id);
    List<AclEntryEntity> findByAclObjectIdentity(AclObjectIdentityEntity objectIdentity);
    Integer countByAclObjectIdentity(AclObjectIdentityEntity objectIdentity);
    AclEntryEntity findByAclObjectIdentityAndSidAndMask(AclObjectIdentityEntity objectIdentity, AclSidEntity sid, Integer mask);


//	@Query(	"select " +
//			"    sid " +
//			"from " +
//			"    AclSidEntity " +
//			"where " +
//			"    principal = false")
//	public List<String> findSidByPrincipalFalse();
//
//	@Query(	"from " +
//			"    AclSidEntity " +
//			"where " +
//			"    sid = :name " +
//			"    and principal = true")
//	public AclSidEntity getUserSid(@Param("name") String name);
//
//	@Query(	"from " +
//			"    AclSidEntity " +
//			"where " +
//			"     sid in (:name) " +
//			" and principal = false")
//	public List<AclSidEntity> findRolesSid(@Param("name") List<String> name);

}
