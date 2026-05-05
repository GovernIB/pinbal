package es.caib.pinbal.logic.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Classe buida per evitar error al executar els postProcessos
 */
@Slf4j
public class ReadDbPropertiesPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        log.debug("[PPE] Executant el postProcessEnvironment a l'api externa!");
    }
}
