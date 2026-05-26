/**
 * 
 */
package es.caib.pinbal.ejb.config;

import es.caib.pinbal.logic.intf.base.config.BaseConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Creació del context Spring per a la capa dels EJBs.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Configuration
@EnableAutoConfiguration(exclude = {
		FreeMarkerAutoConfiguration.class,
})
@ComponentScan({
		BaseConfig.BASE_PACKAGE + ".logic",
		BaseConfig.BASE_PACKAGE + ".persist"
})
@PropertySource(ignoreResourceNotFound = true, value = {
		"classpath:application.properties",
		"file://${" + BaseConfig.APP_PROPERTIES + "}",
		"file://${" + BaseConfig.APP_SYSTEM_PROPERTIES + "}"})
public class EjbContextConfig {

	private static boolean initialized;
	private static ApplicationContext applicationContext;

	public static ApplicationContext getApplicationContext() {
		if (!initialized) {
			initialized = true;
			log.info("Starting EJB spring application...");
			applicationContext = new AnnotationConfigApplicationContext(EjbContextConfig.class);
			log.info("...EJB spring application started.");
		}
		return applicationContext;
	}

}
