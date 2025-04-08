/**
 * 
 */
package es.caib.pinbal.core.repository;

import es.caib.pinbal.core.model.Config;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus notificacio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ConfigRepository extends JpaRepository<Config, String> {

    @Modifying
    @Query(value = "UPDATE PBL_CONFIG SET LASTMODIFIEDBY_CODI = :codiNou WHERE LASTMODIFIEDBY_CODI = :codiAntic", nativeQuery = true)
    void updateUsuariAuditoria(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);
}