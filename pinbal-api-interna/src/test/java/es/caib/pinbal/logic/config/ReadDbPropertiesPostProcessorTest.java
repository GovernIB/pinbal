package es.caib.pinbal.logic.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;

import static org.mockito.Mockito.mock;

public class ReadDbPropertiesPostProcessorTest {

    @Test
    public void testPostProcessEnvironment() {
        ReadDbPropertiesPostProcessor postProcessor = new ReadDbPropertiesPostProcessor();
        ConfigurableEnvironment environment = mock(ConfigurableEnvironment.class);
        SpringApplication application = mock(SpringApplication.class);

        postProcessor.postProcessEnvironment(environment, application);
    }
}
