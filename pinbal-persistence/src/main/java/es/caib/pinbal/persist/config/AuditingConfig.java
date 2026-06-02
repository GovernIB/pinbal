/**
 * 
 */
package es.caib.pinbal.persist.config;

import es.caib.pinbal.logic.intf.service.UsuariService;
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
	@Autowired
	private UsuariService usuariService;

	@Bean
	public AuditorAware<Usuari> auditorProvider() {
		return () -> {
			var authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !authentication.isAuthenticated() || "SCHEDULLER".equals(authentication.getName()) || "anonymousUser".equals(authentication.getName())) {
				return Optional.empty();
			}
			var usuari = usuariRepository.getByCodiReadOnlyNewTransaction(authentication.getName());
			if (usuari.isPresent()) {
				return usuari;
			}
			usuariService.inicialitzarUsuariActual();
			return usuariRepository.getByCodi(authentication.getName());
		};
	}

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
