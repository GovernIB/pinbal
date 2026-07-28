package es.caib.pinbal.scsp.mock;

import es.scsp.client.ClienteUnico;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ClienteUnicoFactoryTest {

    private ClienteUnicoFactory factory;
    private ApplicationContext applicationContext;
    private Environment environment;

    @Before
    public void configurar() throws Exception {
        factory = new ClienteUnicoFactory();
        applicationContext = mock(ApplicationContext.class);
        environment = mock(Environment.class);

        setField("applicationContext", applicationContext);
        setField("environment", environment);
    }

    private void setField(String name, Object value) throws Exception {
        Field field = ClienteUnicoFactory.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(factory, value);
    }

    @Test
    public void ambPerfilMockScspDbActiuRetornaElMockPersistent() {
        ClienteUnicoMockPersistent bean = mock(ClienteUnicoMockPersistent.class);
        when(environment.getActiveProfiles()).thenReturn(new String[]{"mock-scsp-db"});
        when(applicationContext.getBean(ClienteUnicoMockPersistent.class)).thenReturn(bean);

        ClienteUnico resultat = factory.getClienteUnico();

        assertSame(bean, resultat);
    }

    @Test
    public void ambPerfilMockScspActiuRetornaElMockSimple() {
        ClienteUnicoMock bean = mock(ClienteUnicoMock.class);
        when(environment.getActiveProfiles()).thenReturn(new String[]{"mock-scsp"});
        when(applicationContext.getBean(ClienteUnicoMock.class)).thenReturn(bean);

        ClienteUnico resultat = factory.getClienteUnico();

        assertSame(bean, resultat);
    }

    @Test
    public void senseCapPerfilActiuRetornaElClienteUnicoPerDefecte() {
        ClienteUnico bean = mock(ClienteUnico.class);
        when(environment.getActiveProfiles()).thenReturn(new String[]{});
        when(applicationContext.getBean("clienteUnico")).thenReturn(bean);

        ClienteUnico resultat = factory.getClienteUnico();

        assertSame(bean, resultat);
    }

    @Test
    public void ambPerfilDesconegutRetornaElClienteUnicoPerDefecte() {
        ClienteUnico bean = mock(ClienteUnico.class);
        when(environment.getActiveProfiles()).thenReturn(new String[]{"altre-perfil"});
        when(applicationContext.getBean("clienteUnico")).thenReturn(bean);

        ClienteUnico resultat = factory.getClienteUnico();

        assertSame(bean, resultat);
    }

    @Test
    public void ambPerfilMockScspDbActiuPeroBeanNoTrobatFaFallbackAlPerDefecte() {
        ClienteUnico bean = mock(ClienteUnico.class);
        when(environment.getActiveProfiles()).thenReturn(new String[]{"mock-scsp-db"});
        when(applicationContext.getBean(ClienteUnicoMockPersistent.class))
                .thenThrow(new RuntimeException("no trobat"));
        when(applicationContext.getBean("clienteUnico")).thenReturn(bean);

        ClienteUnico resultat = factory.getClienteUnico();

        assertSame(bean, resultat);
    }
}
