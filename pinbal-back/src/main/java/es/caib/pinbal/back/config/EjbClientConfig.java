/**
 *
 */
package es.caib.pinbal.back.config;

import es.caib.pinbal.logic.intf.base.service.PermissionEvaluatorService;
import es.caib.pinbal.logic.intf.base.service.ResourceApiService;
import es.caib.pinbal.logic.intf.resourceservice.AvisResourceService;
import es.caib.pinbal.logic.intf.resourceservice.CacheResourceService;
import es.caib.pinbal.logic.intf.resourceservice.ClauPrivadaResourceService;
import es.caib.pinbal.logic.intf.resourceservice.ClauPublicaResourceService;
import es.caib.pinbal.logic.intf.resourceservice.ConfigGroupResourceService;
import es.caib.pinbal.logic.intf.resourceservice.ConfigResourceService;
import es.caib.pinbal.logic.intf.resourceservice.ConsultaResourceService;
import es.caib.pinbal.logic.intf.resourceservice.EmissorCertResourceService;
import es.caib.pinbal.logic.intf.resourceservice.EntitatResourceService;
import es.caib.pinbal.logic.intf.resourceservice.EntitatServeiResourceService;
import es.caib.pinbal.logic.intf.resourceservice.EntitatUsuariResourceService;
import es.caib.pinbal.logic.intf.resourceservice.HistoricConsultaResourceService;
import es.caib.pinbal.logic.intf.resourceservice.OrganGestorResourceService;
import es.caib.pinbal.logic.intf.resourceservice.OrganismeCessionariResourceService;
import es.caib.pinbal.logic.intf.resourceservice.ParamConfResourceService;
import es.caib.pinbal.logic.intf.resourceservice.ProcedimentResourceService;
import es.caib.pinbal.logic.intf.resourceservice.ServeiBusResourceService;
import es.caib.pinbal.logic.intf.resourceservice.ServeiResourceService;
import es.caib.pinbal.logic.intf.resourceservice.UsuariResourceService;
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

	private static final String EJB_JNDI_PREFIX = "java:app/pinbal-ejb/";
	private static final String EJB_JNDI_SUFFIX = "Ejb";

	@Bean
	public LocalStatelessSessionProxyFactoryBean aplicacioService() {
		return getLocalEjbFactoyBean(AplicacioService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean avisService() {
		return getLocalEjbFactoyBean(AvisService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean configService() {
		return getLocalEjbFactoyBean(ConfigService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean consultaService() {
		return getLocalEjbFactoyBean(ConsultaService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean dadesExternesService() {
		return getLocalEjbFactoyBean(DadesExternesService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean entitatService() {
		return getLocalEjbFactoyBean(EntitatService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean estadisticaService() {
		return getLocalEjbFactoyBean(EstadisticaService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean gestioRestService() {
		return getLocalEjbFactoyBean(GestioRestService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean historicConsultaService() {
		return getLocalEjbFactoyBean(HistoricConsultaService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean integracioAccioService() {
		return getLocalEjbFactoyBean(IntegracioAccioService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean organGestorService() {
		return getLocalEjbFactoyBean(OrganGestorService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean permissionEvaluatorService() {
		return getLocalEjbFactoyBean(PermissionEvaluatorService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean procedimentService() {
		return getLocalEjbFactoyBean(ProcedimentService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean propertyService() {
		return getLocalEjbFactoyBean(PropertyService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean recobrimentService() {
		return getLocalEjbFactoyBean(RecobrimentService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean resourceApiService() {
		return getLocalEjbFactoyBean(ResourceApiService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean salutService() {
		return getLocalEjbFactoyBean(SalutService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean scspService() {
		return getLocalEjbFactoyBean(ScspService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean serveiService() {
		return getLocalEjbFactoyBean(ServeiService.class, false);
	}
	@Bean
	public LocalStatelessSessionProxyFactoryBean usuariService() {
		return getLocalEjbFactoyBean(UsuariService.class, false);
	}


	// Resource Services

	@Bean
	public LocalStatelessSessionProxyFactoryBean usuariResourceService() {
		return getLocalEjbFactoyBean(UsuariResourceService.class, true);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean consultaResourceService() {
		return getLocalEjbFactoyBean(ConsultaResourceService.class, true);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean historicConsultaResourceService() {
		return getLocalEjbFactoyBean(HistoricConsultaResourceService.class, true);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean entitatResourceService() {
		return getLocalEjbFactoyBean(EntitatResourceService.class, true);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean entitatServeiResourceService() {
		return getLocalEjbFactoyBean(EntitatServeiResourceService.class, true);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean entitatUsuariResourceService() {
		return getLocalEjbFactoyBean(EntitatUsuariResourceService.class, true);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean serveiResourceService() {
		return getLocalEjbFactoyBean(ServeiResourceService.class, true);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean procedimentResourceService() {
		return getLocalEjbFactoyBean(ProcedimentResourceService.class, true);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean organGestorResourceService() {
		return getLocalEjbFactoyBean(OrganGestorResourceService.class, true);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean serveiBusResourceService() {
		return getLocalEjbFactoyBean(ServeiBusResourceService.class, true);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean paramConfResourceService() {
		return getLocalEjbFactoyBean(ParamConfResourceService.class, true);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean emissorCertResourceService() {
		return getLocalEjbFactoyBean(EmissorCertResourceService.class, true);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean clauPublicaResourceService() {
		return getLocalEjbFactoyBean(ClauPublicaResourceService.class, true);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean organismeCessionariResourceService() {
		return getLocalEjbFactoyBean(OrganismeCessionariResourceService.class, true);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean clauPrivadaResourceService() {
		return getLocalEjbFactoyBean(ClauPrivadaResourceService.class, true);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean avisResourceService() {
		return getLocalEjbFactoyBean(AvisResourceService.class, true);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean cacheResourceService() {
		return getLocalEjbFactoyBean(CacheResourceService.class, true);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean configGroupResourceService() {
		return getLocalEjbFactoyBean(ConfigGroupResourceService.class, true);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean configResourceService() {
		return getLocalEjbFactoyBean(ConfigResourceService.class, true);
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
