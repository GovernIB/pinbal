package es.caib.pinbal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWarDeployment;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Classe principal del backoffice de PINBAL per executar amb SpringBoot.
 * 
 * @author Límit Tecnologies
 */
@EnableAsync
@SpringBootApplication
@ConditionalOnNotWarDeployment
@PropertySource(
		ignoreResourceNotFound = true,
		value = { "classpath:application.properties" })
public class PinbalBackBootApp {

	public static void main(String[] args) {
		SpringApplication.run(PinbalBackBootApp.class, args);
	}

}
