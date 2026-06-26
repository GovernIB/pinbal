package es.caib.pinbal.logic.helper;

import es.caib.pinbal.logic.intf.service.ConfigService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StartupApplicationListenerTest {

    @Mock private ConfigService configService;

    @InjectMocks
    private StartupApplicationListener startupApplicationListener;

    @Test
    public void onApplicationEvent_cridaActualitzarPropietats() throws Exception {
        ContextRefreshedEvent event = mock(ContextRefreshedEvent.class);
        startupApplicationListener.onApplicationEvent(event);
        verify(configService).actualitzarPropietatsJBossBdd();
    }

    @Test
    public void onApplicationEvent_configuracioSeguretatRestaurada() throws Exception {
        ContextRefreshedEvent event = mock(ContextRefreshedEvent.class);
        var authBefore = SecurityContextHolder.getContext().getAuthentication();
        startupApplicationListener.onApplicationEvent(event);
        var authAfter = SecurityContextHolder.getContext().getAuthentication();
        assertEquals(authBefore, authAfter);
    }

    @Test
    public void onApplicationEvent_errorConfigService_noLlancaException() throws Exception {
        ContextRefreshedEvent event = mock(ContextRefreshedEvent.class);
        doThrow(new RuntimeException("Error")).when(configService).actualitzarPropietatsJBossBdd();
        assertDoesNotThrow(() -> startupApplicationListener.onApplicationEvent(event));
    }
}
