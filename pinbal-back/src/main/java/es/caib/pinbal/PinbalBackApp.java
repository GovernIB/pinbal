package es.caib.pinbal;

import es.caib.pinbal.logic.intf.base.config.BaseConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.jersey.JerseyServerMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWarDeployment;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * Classe principal del backoffice de PINBAL per a executar amb el WAR.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@ConditionalOnWarDeployment
@EnableAsync
@SpringBootApplication(exclude = {
	DataSourceAutoConfiguration.class,
	DataSourceTransactionManagerAutoConfiguration.class,
	JpaRepositoriesAutoConfiguration.class,
	HibernateJpaAutoConfiguration.class,
	TransactionAutoConfiguration.class,
	LiquibaseAutoConfiguration.class,
	FreeMarkerAutoConfiguration.class,
	WebSocketServletAutoConfiguration.class,
	JerseyServerMetricsAutoConfiguration.class,
	UserDetailsServiceAutoConfiguration.class
})
@ComponentScan(
		basePackages = { BaseConfig.BASE_PACKAGE },
		excludeFilters = {
				@ComponentScan.Filter(
						type = FilterType.REGEX,
						pattern = {
								"es\\.caib\\." + BaseConfig.APP_NAME + "\\.logic\\..*",
								"es\\.caib\\." + BaseConfig.APP_NAME + "\\.persist\\..*",
								"es\\.caib\\." + BaseConfig.APP_NAME + "\\.ejb\\..*" })
		})
@PropertySource(
		ignoreResourceNotFound = true,
		value = { "classpath:application.properties" })
public class PinbalBackApp extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(PinbalBackApp.class, args);
	}

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		try {
			InputStream manifestStream = servletContext.getResourceAsStream("/META-INF/MANIFEST.MF");
			if (manifestStream == null) {
			    log.warn("No s'ha trobat l'arxiu MANIFEST.MF");
			} else {
				Manifest manifest = new Manifest(manifestStream);
				Attributes attributes = manifest.getMainAttributes();
				String version = attributes.getValue("Implementation-Version");
				String buildTimestamp = attributes.getValue("Build-Timestamp");
				log.info("Carregant l'aplicació PINBAL versió " + version + " generada en data " + buildTimestamp);
			}
		} catch (IOException ex) {
			throw new ServletException("No s'ha pogut llegir l'arxiu MANIFEST.MF", ex);
		}
		super.onStartup(servletContext);
	}

}
