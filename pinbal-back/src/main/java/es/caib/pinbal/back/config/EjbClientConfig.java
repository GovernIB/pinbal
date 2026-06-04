/**
 *
 */
package es.caib.pinbal.back.config;

import es.caib.pinbal.logic.intf.service.AplicacioService;
import es.caib.pinbal.logic.intf.service.AvisService;
import es.caib.pinbal.logic.intf.service.ConfigService;
import es.caib.pinbal.logic.intf.service.ConsultaService;
import es.caib.pinbal.logic.intf.service.DadesExternesService;
import es.caib.pinbal.logic.intf.service.EntitatService;
import es.caib.pinbal.logic.intf.service.EstadisticaService;
import es.caib.pinbal.logic.intf.service.GestioRestService;
import es.caib.pinbal.logic.intf.service.HistoricConsultaService;
import es.caib.pinbal.logic.intf.service.IntegracioAccioService;
import es.caib.pinbal.logic.intf.service.OrganGestorService;
import es.caib.pinbal.logic.intf.service.ProcedimentService;
import es.caib.pinbal.logic.intf.service.PropertyService;
import es.caib.pinbal.logic.intf.service.RecobrimentService;
import es.caib.pinbal.logic.intf.service.SalutService;
import es.caib.pinbal.logic.intf.service.ScspService;
import es.caib.pinbal.logic.intf.service.ServeiService;
import es.caib.pinbal.logic.intf.service.UsuariService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWarDeployment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.ejb.access.LocalStatelessSessionProxyFactoryBean;

/**
 * Configuració d'accés als services de Spring mitjançant EJBs.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Configuration
@ConditionalOnWarDeployment
@Order(Ordered.HIGHEST_PRECEDENCE)
public class EjbClientConfig {

	private static final String EJB_JNDI_PREFIX = "java:app/notib-ejb/";
	private static final String EJB_JNDI_SUFFIX = "Ejb";

	@Bean
	public LocalStatelessSessionProxyFactoryBean aplicacioService() {
		return getLocalEjbFactoyBean(AplicacioService.class, true);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean avisService() {
		return getLocalEjbFactoyBean(AvisService.class, true);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean configService() {
		return getLocalEjbFactoyBean(ConfigService.class, true);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean consultaService() {
		return getLocalEjbFactoyBean(ConsultaService.class, true);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean dadesExternesService() {
		return getLocalEjbFactoyBean(DadesExternesService.class, true);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean entitatService() {
		return getLocalEjbFactoyBean(EntitatService.class, true);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean estadisticaService() {
		return getLocalEjbFactoyBean(EstadisticaService.class, true);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean gestioRestService() {
		return getLocalEjbFactoyBean(GestioRestService.class, true);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean historicConsultaService() {
		return getLocalEjbFactoyBean(HistoricConsultaService.class, true);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean integracioAccioService() {
		return getLocalEjbFactoyBean(IntegracioAccioService.class, true);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean organGestorService() {
		return getLocalEjbFactoyBean(OrganGestorService.class, true);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean procedimentService() {
		return getLocalEjbFactoyBean(ProcedimentService.class, true);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean propertyService() {
		return getLocalEjbFactoyBean(PropertyService.class, true);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean recobrimentService() {
		return getLocalEjbFactoyBean(RecobrimentService.class, true);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean salutService() {
		return getLocalEjbFactoyBean(SalutService.class, true);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean scspService() {
		return getLocalEjbFactoyBean(ScspService.class, true);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean serveiService() {
		return getLocalEjbFactoyBean(ServeiService.class, true);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean usuariService() {
		return getLocalEjbFactoyBean(UsuariService.class, true);
	}

	private LocalStatelessSessionProxyFactoryBean getLocalEjbFactoyBean(
		Class<?> serviceClass,
		boolean withSuffix) {
		String jndiName = EJB_JNDI_PREFIX + serviceClass.getSimpleName() + (withSuffix ? EJB_JNDI_SUFFIX : "");
		log.info("Creating EJB proxy for serviceClass with JNDI name " + jndiName);
		LocalStatelessSessionProxyFactoryBean factory = new LocalStatelessSessionProxyFactoryBean();
		factory.setBusinessInterface(serviceClass);
		factory.setJndiName(jndiName);
		return factory;
	}

}
