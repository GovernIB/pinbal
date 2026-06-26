package es.caib.pinbal.logic.service;

import es.caib.pinbal.logic.helper.ConfigHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PropertyServiceImplTest {

    @Mock
    private ConfigHelper configHelper;

    @InjectMocks
    private PropertyServiceImpl propertyService;

    @Test
    public void get_clauExisteix_retornaValor() {
        when(configHelper.getConfig("es.caib.pinbal.test.key")).thenReturn("valor");

        String result = propertyService.get("es.caib.pinbal.test.key");

        assertEquals("valor", result);
    }

    @Test
    public void get_clauNoExisteix_retornaNull() {
        when(configHelper.getConfig("es.caib.pinbal.inexistent")).thenReturn(null);

        assertNull(propertyService.get("es.caib.pinbal.inexistent"));
    }

    @Test
    public void getWithDefault_clauExisteix_retornaValorReal() {
        when(configHelper.getConfig("es.caib.pinbal.key", "default")).thenReturn("real");

        assertEquals("real", propertyService.get("es.caib.pinbal.key", "default"));
    }

    @Test
    public void getWithDefault_clauNoExisteix_retornaDefault() {
        when(configHelper.getConfig("es.caib.pinbal.missing", "default")).thenReturn("default");

        assertEquals("default", propertyService.get("es.caib.pinbal.missing", "default"));
    }

    @Test
    public void get_delegaAlConfigHelper() {
        propertyService.get("qualsevolClau");
        verify(configHelper).getConfig("qualsevolClau");
    }
}
