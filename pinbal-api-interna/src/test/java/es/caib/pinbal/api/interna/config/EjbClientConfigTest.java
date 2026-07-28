package es.caib.pinbal.api.interna.config;

import es.caib.pinbal.logic.intf.service.EntitatService;
import es.caib.pinbal.logic.intf.service.EstadisticaService;
import es.caib.pinbal.logic.intf.service.GestioRestService;
import es.caib.pinbal.logic.intf.service.RecobrimentService;
import es.caib.pinbal.logic.intf.service.SalutService;
import org.junit.jupiter.api.Test;
import org.springframework.ejb.access.LocalStatelessSessionProxyFactoryBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EjbClientConfigTest {

    private final EjbClientConfig config = new EjbClientConfig();

    @Test
    public void testEstadisticaService() {
        LocalStatelessSessionProxyFactoryBean bean = config.estadisticaService();
        assertEquals("java:app/pinbal-ejb/EstadisticaService", bean.getJndiName());
        assertEquals(EstadisticaService.class, bean.getBusinessInterface());
    }

    @Test
    public void testSalutService() {
        LocalStatelessSessionProxyFactoryBean bean = config.salutService();
        assertEquals("java:app/pinbal-ejb/SalutService", bean.getJndiName());
        assertEquals(SalutService.class, bean.getBusinessInterface());
    }

    @Test
    public void testRecobrimentService() {
        LocalStatelessSessionProxyFactoryBean bean = config.recobrimentService();
        assertEquals("java:app/pinbal-ejb/RecobrimentService", bean.getJndiName());
        assertEquals(RecobrimentService.class, bean.getBusinessInterface());
    }

    @Test
    public void testEntitatService() {
        LocalStatelessSessionProxyFactoryBean bean = config.entitatService();
        assertEquals("java:app/pinbal-ejb/EntitatService", bean.getJndiName());
        assertEquals(EntitatService.class, bean.getBusinessInterface());
    }

    @Test
    public void testGestioRestService() {
        LocalStatelessSessionProxyFactoryBean bean = config.gestioRestService();
        assertEquals("java:app/pinbal-ejb/GestioRestService", bean.getJndiName());
        assertEquals(GestioRestService.class, bean.getBusinessInterface());
        assertTrue(bean instanceof LocalStatelessSessionProxyFactoryBean);
    }
}
