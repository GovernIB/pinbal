package es.caib.pinbal.api.interna.config;

import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RecobrimentSoapBootConfigTest {

    private final RecobrimentSoapBootConfig config = new RecobrimentSoapBootConfig();

    @Test
    public void testCxfServletRegistration() {
        ReflectionTestUtils.setField(config, "servletMapping", "/ws/*");

        ServletRegistrationBean<CXFServlet> registration = config.cxfServletRegistration();

        assertNotNull(registration);
        assertEquals("recobrimentSoapServlet", registration.getServletName());
        assertEquals(java.util.Collections.singleton("/ws/*"), registration.getUrlMappings());
    }

    @Test
    public void testSpringBus() {
        SpringBus bus = config.springBus();
        assertNotNull(bus);
    }
}
