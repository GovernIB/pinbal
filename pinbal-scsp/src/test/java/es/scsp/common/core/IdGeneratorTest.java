package es.scsp.common.core;

import es.scsp.common.dao.ParametroConfiguracionDao;
import es.scsp.common.dao.PeticionRespuestaDao;
import es.scsp.common.dao.SecuenciaIdPeticionDao;
import es.scsp.common.domain.core.ParametroConfiguracion;
import es.scsp.common.domain.core.PeticionRespuesta;
import es.scsp.common.domain.core.Servicio;
import es.scsp.common.exceptions.ScspException;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IdGeneratorTest {

    private IdGenerator idGenerator;
    private SecuenciaIdPeticionDao secuenciaDao;
    private ParametroConfiguracionDao paramDao;
    private PeticionRespuestaDao peticionRespuestaDao;

    @Before
    public void configurar() throws Exception {
        idGenerator = new IdGenerator();
        secuenciaDao = mock(SecuenciaIdPeticionDao.class);
        paramDao = mock(ParametroConfiguracionDao.class);
        peticionRespuestaDao = mock(PeticionRespuestaDao.class);

        idGenerator.setSecuenciaIdPeticionDao(secuenciaDao);
        idGenerator.setParamDao(paramDao);
        setPeticionRespuestaDao(idGenerator, peticionRespuestaDao);
    }

    private static void setPeticionRespuestaDao(IdGenerator target, PeticionRespuestaDao dao) throws Exception {
        Field field = IdGenerator.class.getDeclaredField("peticionRespuestaDao");
        field.setAccessible(true);
        field.set(target, dao);
    }

    private static Servicio servicio(String prefijo, String versionEsquema) {
        Servicio servicio = new Servicio();
        servicio.setCodCertificado("CERT1");
        servicio.setPrefijoPeticion(prefijo);
        servicio.setVersionEsquema(versionEsquema);
        return servicio;
    }

    @Test
    public void getIdPeticionAmbPrefixDelServeiIGeneraIdAmbZerosDavant() throws Exception {
        when(secuenciaDao.next("ABC")).thenReturn("1");
        when(paramDao.select("tipoId")).thenReturn(null);
        when(peticionRespuestaDao.select(anyString())).thenReturn(null);

        String id = idGenerator.getIdPeticion(servicio("ABC", "V3"));

        // longitudSecuencial = 16 - 3 = 13
        assertEquals("ABC0000000000001", id);
        assertEquals(16, id.length());
    }

    @Test
    public void getIdPeticionAmbPrefixDelParametreQuanElServeiNoEnTe() throws Exception {
        ParametroConfiguracion param = new ParametroConfiguracion();
        param.setValor("XYZ");
        when(paramDao.select("prefijo.idpeticion")).thenReturn(param);
        when(paramDao.select("tipoId")).thenReturn(null);
        when(secuenciaDao.next("XYZ")).thenReturn("5");
        when(peticionRespuestaDao.select(anyString())).thenReturn(null);

        String id = idGenerator.getIdPeticion(servicio(null, "V3"));

        assertTrue(id.startsWith("XYZ"));
    }

    @Test
    public void getIdPeticionSensePrefixNiParametreLlancaScspException() {
        when(paramDao.select("prefijo.idpeticion")).thenReturn(null);
        try {
            idGenerator.getIdPeticion(servicio(null, "V3"));
            fail("Hauria d'haver llançat ScspException");
        } catch (ScspException e) {
            // esperat
        }
    }

    @Test
    public void getIdPeticionAmbParametreBuitLlancaScspException() {
        ParametroConfiguracion param = new ParametroConfiguracion();
        param.setValor("");
        when(paramDao.select("prefijo.idpeticion")).thenReturn(param);
        try {
            idGenerator.getIdPeticion(servicio(null, "V3"));
            fail("Hauria d'haver llançat ScspException");
        } catch (ScspException e) {
            // esperat
        }
    }

    @Test
    public void getIdPeticionAmbPrefixMassaCurtLlancaScspException() {
        try {
            idGenerator.getIdPeticion(servicio("AB", "V3"));
            fail("Hauria d'haver llançat ScspException");
        } catch (ScspException e) {
            // esperat
        }
    }

    @Test
    public void getIdPeticionAmbPrefixMassaLlargLlancaScspException() {
        try {
            idGenerator.getIdPeticion(servicio("ABCDEFGHIJ", "V3"));
            fail("Hauria d'haver llançat ScspException");
        } catch (ScspException e) {
            // esperat
        }
    }

    @Test
    public void getIdPeticionAmbEsquemaV2UsaLongitudCurta() throws Exception {
        when(secuenciaDao.next("ABC")).thenReturn("1");
        when(peticionRespuestaDao.select(anyString())).thenReturn(null);

        String id = idGenerator.getIdPeticion(servicio("ABC", "esquema-V2"));

        // longitudSecuencial = 16 - 3 = 13 (igual que sense V2 en aquest cas concret)
        assertEquals("ABC0000000000001", id);
    }

    @Test
    public void getIdPeticionAmbTipoIdLongUsaLongitudMesLlarga() throws Exception {
        ParametroConfiguracion tipoId = new ParametroConfiguracion();
        tipoId.setValor("long");
        when(paramDao.select("tipoId")).thenReturn(tipoId);
        when(secuenciaDao.next("ABC")).thenReturn("1");
        when(peticionRespuestaDao.select(anyString())).thenReturn(null);

        String id = idGenerator.getIdPeticion(servicio("ABC", "V3"));

        // longitudSecuencial = 26 - 3 = 23
        assertEquals("ABC" + "0".repeat(22) + "1", id);
        assertEquals(3 + 23, id.length());
    }

    @Test
    public void getIdPeticionAmbSecuencialExcessiuLlancaScspException() throws Exception {
        // Ha de superar tant longitudSecuencial (16-3=13) com el límit absolut de 26 (prefix+seqüencial).
        when(secuenciaDao.next("ABC")).thenReturn("1234567890123456789012345");
        try {
            idGenerator.getIdPeticion(servicio("ABC", "V3"));
            fail("Hauria d'haver llançat ScspException");
        } catch (ScspException e) {
            // esperat
        }
    }

    @Test
    public void getIdPeticionReintentaSiJaExisteixUnaPeticioAmbAquestId() throws Exception {
        when(secuenciaDao.next("ABC")).thenReturn("1", "2");
        when(paramDao.select("tipoId")).thenReturn(null);
        when(peticionRespuestaDao.select("ABC0000000000001")).thenReturn(new PeticionRespuesta());
        when(peticionRespuestaDao.select("ABC0000000000002")).thenReturn(null);

        String id = idGenerator.getIdPeticion(servicio("ABC", "V3"));

        assertEquals("ABC0000000000002", id);
    }
}
