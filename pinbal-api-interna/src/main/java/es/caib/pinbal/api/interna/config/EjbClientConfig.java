/**
 * 
 */
package es.caib.pinbal.api.interna.config;

import es.caib.pinbal.logic.intf.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ejb.access.LocalStatelessSessionProxyFactoryBean;

/**
 * Configuració d'accés als services de Spring mitjançant EJBs.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Configuration
public class EjbClientConfig {

	private static final String EJB_JNDI_PREFIX = "java:app/pinbal-ejb/";
	private static final String EJB_JNDI_SUFFIX = "";

	@Bean
	public LocalStatelessSessionProxyFactoryBean estadisticaService() {
		return getLocalEjbFactoyBean(EstadisticaService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean salutService() {
		return getLocalEjbFactoyBean(SalutService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean recobrimentService() {
		return getLocalEjbFactoyBean(RecobrimentService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean entitatService() {
		return getLocalEjbFactoyBean(EntitatService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean gestioRestService() {
		return getLocalEjbFactoyBean(GestioRestService.class);
	}


	private LocalStatelessSessionProxyFactoryBean getLocalEjbFactoyBean(Class<?> serviceClass) {

		var jndiName = EJB_JNDI_PREFIX + serviceClass.getSimpleName() + EJB_JNDI_SUFFIX;
		log.debug("Creating EJB proxy for serviceClass with JNDI name " + jndiName);
		var factory = new LocalStatelessSessionProxyFactoryBean();
		factory.setBusinessInterface(serviceClass);
		factory.setJndiName(jndiName);
		return factory;
	}

}
