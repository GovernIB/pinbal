package es.caib.pinbal.back.config;

import org.junit.jupiter.api.Test;
import org.springframework.ejb.access.LocalStatelessSessionProxyFactoryBean;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@code EjbClientConfig} exposa un mètode {@code @Bean} gairebé idèntic per cada servei EJB
 * (delega tots en el mateix helper privat {@code getLocalEjbFactoyBean}). En comptes d'escriure
 * una assertion per mètode, s'invoquen tots reflectivament per cobrir-los tots amb poc codi.
 */
public class EjbClientConfigTest {

    @Test
    public void totsElsBeansEjbGenerenUnJndiNameCoherentAmbElServei() throws Exception {
        EjbClientConfig config = new EjbClientConfig();
        int metodesProvats = 0;

        for (Method method : EjbClientConfig.class.getDeclaredMethods()) {
            if (method.getParameterCount() == 0
                    && LocalStatelessSessionProxyFactoryBean.class.equals(method.getReturnType())) {
                method.setAccessible(true);
                LocalStatelessSessionProxyFactoryBean bean = (LocalStatelessSessionProxyFactoryBean) method.invoke(config);

                assertNotNull(bean);
                metodesProvats++;
            }
        }

        assertTrue(metodesProvats > 30, "S'esperaven més de 30 mètodes @Bean, se n'han trobat " + metodesProvats);
    }

    @Test
    public void serveiSenseSufixNoAfegeixEjbAlJndiName() {
        EjbClientConfig config = new EjbClientConfig();

        LocalStatelessSessionProxyFactoryBean bean = config.usuariService();

        assertEquals("java:app/pinbal-ejb/UsuariService", bean.getJndiName());
    }

    @Test
    public void serveiDeRecursAmbSufixAfegeixEjbAlJndiName() {
        EjbClientConfig config = new EjbClientConfig();

        LocalStatelessSessionProxyFactoryBean bean = config.usuariResourceService();

        assertEquals("java:app/pinbal-ejb/UsuariResourceServiceEjb", bean.getJndiName());
    }
}
