package es.caib.pinbal.scsp.mock;

import es.scsp.common.dao.PeticionRespuestaDao;
import es.scsp.common.exceptions.ScspException;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests addicionals per a ClienteUnicoMockPersistent que injecten els DAOs (camps
 * {@code @Autowired} sense setter) via reflexió amb Mockito, evitant necessitar una sessió
 * Hibernate real.
 */
public class ClienteUnicoMockPersistentDaoTest {

    private ClienteUnicoMockPersistent mock;
    private PeticionRespuestaDao peticionRespuestaDao;

    @Before
    public void configurar() throws Exception {
        mock = new ClienteUnicoMockPersistent();
        peticionRespuestaDao = mock(PeticionRespuestaDao.class);
        setField("peticionRespuestaDao", peticionRespuestaDao);
    }

    private void setField(String name, Object value) throws Exception {
        Field field = ClienteUnicoMockPersistent.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(mock, value);
    }

    @Test
    public void generaJustificanteTransmisionRetornaUnPdfMockSenseAccedirABbdd() throws ScspException {
        ByteArrayOutputStream baos = mock.generaJustificanteTransmision("TRANS1", "PET1");

        assertTrue(baos.size() > 0);
        assertTrue(baos.toString().startsWith("%PDF-1.4"));
    }

    @Test
    public void recuperaRespuestaAmbPeticioInexistentLlancaScspException() throws Exception {
        when(peticionRespuestaDao.select("NO-EXISTEIX")).thenReturn(null);

        try {
            mock.recuperaRespuesta("NO-EXISTEIX");
            fail("Hauria d'haver llançat ScspException");
        } catch (ScspException e) {
            // esperat
        }
    }
}
