/**
 * 
 */
package es.caib.pinbal.persist.repository.acl;

import es.caib.pinbal.persist.entity.acl.AclClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus ACL-SID.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AclClassRepository extends JpaRepository<AclClassEntity, Long> {

	AclClassEntity findByClassname(String classname);

}
