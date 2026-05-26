/**
 *
 */
package es.caib.pinbal.api.interna.config;

import es.caib.pinbal.api.interna.ws.RecobrimentBean;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.ws.Endpoint;

/**
 * Publicacio del servei SOAP de recobriment quan l'API interna s'executa amb
 * Spring Boot. Sobre JBoss el publica JBossWS a partir del mateix bean EJB.
 */
@Configuration
@ConditionalOnExpression("#{systemProperties['jboss.server.base.dir'] == null}")
public class RecobrimentSoapBootConfig {

	@Value("${es.caib.pinbal.recobriment.soap.servlet-mapping:/ws/*}")
	private String servletMapping;

	@Value("${es.caib.pinbal.recobriment.soap.endpoint-path:/recobriment}")
	private String endpointPath;

	@Bean
	public ServletRegistrationBean<CXFServlet> cxfServletRegistration() {
		ServletRegistrationBean<CXFServlet> registration = new ServletRegistrationBean<CXFServlet>(
				new CXFServlet(),
				servletMapping);
		registration.setName("recobrimentSoapServlet");
		return registration;
	}

	@Bean(name = Bus.DEFAULT_BUS_ID)
	public SpringBus springBus() {
		return new SpringBus();
	}

	@Bean
	public Endpoint recobrimentSoapEndpoint(Bus bus, RecobrimentBean recobrimentBean) {
		EndpointImpl endpoint = new EndpointImpl(bus, recobrimentBean);
		endpoint.publish(endpointPath);
		return endpoint;
	}

}
