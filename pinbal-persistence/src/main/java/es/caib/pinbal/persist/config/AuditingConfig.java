/**
 * 
 */
package es.caib.pinbal.persist.config;

import es.caib.pinbal.persist.base.config.BaseAuditingConfig;
import es.caib.pinbal.persist.base.entity.AuditableEntity;
import es.caib.pinbal.persist.entity.Usuari;
import es.caib.pinbal.persist.repository.UsuariRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Configuració per a les entitats de base de dades auditables.
 * 
 * @author Limit Tecnologies
 */
@Configuration
@EnableJpaAuditing
public class AuditingConfig extends BaseAuditingConfig {

	@Autowired
	private UsuariRepository usuariRepository;

	@Bean
	public AuditorAware<Usuari> auditorProvider() {
		return () -> {
			try {
				var authentication = SecurityContextHolder.getContext().getAuthentication();
				if (authentication == null || !authentication.isAuthenticated() || "SCHEDULLER".equals(authentication.getName()) || "anonymousUser".equals(authentication.getName()) || "INIT".equals(authentication.getName())) {
					return Optional.empty();
				}
				// getByCodi participa en la transacció actual (evita REQUIRES_NEW anidat que esgota el pool)
				var usuari = usuariRepository.getByCodi(authentication.getName());
				if (usuari.isPresent()) {
					return usuari;
				}
				// No cridem inicialitzarUsuariActual() des de l'auditor: pot invocar plugins externs
				// que fallen i criden addAccioError, que torna a disparar l'auditor (bucle).
				// La inicialització de l'usuari es fa durant el flux normal d'autenticació.
				return Optional.empty();
			} catch (Exception e) {
				logger.warn("No s'ha pogut obtenir l'auditor actual, es deixa buit", e);
				return Optional.empty();
			}
		};
	}

	private static final Logger logger = LoggerFactory.getLogger(AuditingConfig.class);

	public static class CustomAuditingEntityListener {
		@PrePersist
		public void beforeInsert(Object entity) {
			if (entity instanceof AuditableEntity) {
				AuditableEntity auditableEntity = (AuditableEntity)entity;
				auditableEntity.updateCreated(
						getCurrentAuditor(),
						LocalDateTime.now());
			}
		}
		@PreUpdate
		public void beforeUpdate(Object entity) {
			if (entity instanceof AuditableEntity) {
				AuditableEntity auditableEntity = (AuditableEntity)entity;
				auditableEntity.updateLastModified(
						getCurrentAuditor(),
						LocalDateTime.now());
			}
		}
		private String getCurrentAuditor() {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication != null && authentication.isAuthenticated()) {
				return authentication.getName();
			} else {
				return null;
			}
		}
	}

}
