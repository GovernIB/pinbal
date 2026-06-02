/**
 * 
 */
package es.caib.pinbal.persist.repository;

import es.caib.pinbal.persist.entity.Config;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus notificacio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ConfigRepository extends JpaRepository<Config, String> {

    @Modifying
    @Query(value = "UPDATE PBL_CONFIG SET LASTMODIFIEDBY_CODI = :codiNou WHERE LASTMODIFIEDBY_CODI = :codiAntic", nativeQuery = true)
    int updateUsuariAuditoria(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);

    @Query("FROM Config c WHERE c.sourceProperty = es.caib.pinbal.logic.intf.dto.ConfigSourceEnumDto.DATABASE")
    List<Config> findDbProperties();

    @Query("FROM Config c WHERE c.sourceProperty = es.caib.pinbal.logic.intf.dto.ConfigSourceEnumDto.FILE")
    List<Config> findFileProperties();
}