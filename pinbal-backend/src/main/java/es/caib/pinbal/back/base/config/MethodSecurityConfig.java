package es.caib.pinbal.back.base.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

/**
 * Configuració de la seguretat a nivell de mètode.
 * 
 * @author Límit Tecnologies
 */
@Configuration
@RequiredArgsConstructor
@DependsOn({ "permissionEvaluatorService" })
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class MethodSecurityConfig {

	public static final String DEFAULT_ROLE_PREFIX = "";

	private final PermissionEvaluator permissionEvaluator;

	@Bean
	public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
		DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
		expressionHandler.setPermissionEvaluator(permissionEvaluator);
		expressionHandler.setDefaultRolePrefix(DEFAULT_ROLE_PREFIX);
		return expressionHandler;
	}

}
