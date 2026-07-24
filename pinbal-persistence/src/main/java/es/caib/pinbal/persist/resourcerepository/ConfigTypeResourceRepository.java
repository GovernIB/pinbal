package es.caib.pinbal.persist.resourcerepository;

import es.caib.pinbal.persist.resourceentity.ConfigTypeResourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositori intern (no exposat com a recurs REST) del catàleg de tipus de propietat de
 * configuració, usat només per {@code ConfigResourceServiceImpl}.
 *
 * @author Límit Tecnologies
 */
public interface ConfigTypeResourceRepository extends JpaRepository<ConfigTypeResourceEntity, String> {

}
