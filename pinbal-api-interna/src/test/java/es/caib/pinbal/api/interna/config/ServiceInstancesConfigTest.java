package es.caib.pinbal.api.interna.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ServiceInstancesConfigTest {

    @Test
    public void testInstantiation() {
        assertNotNull(new ServiceInstancesConfig());
    }
}
