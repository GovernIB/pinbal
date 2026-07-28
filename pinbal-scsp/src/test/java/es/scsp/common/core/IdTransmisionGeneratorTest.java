package es.scsp.common.core;

import es.scsp.common.dao.ParametroConfiguracionDao;
import es.scsp.common.dao.SecuenciaIdTransmisionDao;
import es.scsp.common.domain.core.ParametroConfiguracion;
import es.scsp.common.domain.core.Servicio;
import es.scsp.common.exceptions.ScspException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IdTransmisionGeneratorTest {

    private IdTransmisionGenerator idTransmisionGenerator;
    private SecuenciaIdTransmisionDao secuenciaDao;
    private ParametroConfiguracionDao paramDao;

    @Before
    public void configurar() {
        idTransmisionGenerator = new IdTransmisionGenerator();
        secuenciaDao = mock(SecuenciaIdTransmisionDao.class);
        paramDao = mock(ParametroConfiguracionDao.class);
        idTransmisionGenerator.setSecuenciaIdTransmisionDao(secuenciaDao);
        idTransmisionGenerator.setParamDao(paramDao);
    }

    private static Servicio servicio(String prefijo, String versionEsquema) {
        Servicio servicio = new Servicio();
        servicio.setCodCertificado("CERT1");
        servicio.setPrefijoIdTransmision(prefijo);
        servicio.setVersionEsquema(versionEsquema);
        return servicio;
    }

    @Test
    public void getIdTransmisionAmbPrefixDelServeiGeneraIdAmbTIZerosDavant() throws Exception {
        when(secuenciaDao.next("ABC")).thenReturn("1");

        String id = idTransmisionGenerator.getIdTransmision(servicio("ABC", "V3"));

        // longitudSecuencial = 28 - 3 = 25
        assertEquals("TABC" + "0".repeat(24) + "1", id);
    }

    @Test
    public void getIdTransmisionAmbPrefixDelParametreQuanElServeiNoEnTe() throws Exception {
        ParametroConfiguracion param = new ParametroConfiguracion();
        param.setValor("XYZ");
        when(paramDao.select("prefijo.idtransmision")).thenReturn(param);
        when(secuenciaDao.next("XYZ")).thenReturn("7");

        String id = idTransmisionGenerator.getIdTransmision(servicio(null, "V3"));

        assertEquals("TXYZ" + "0".repeat(24) + "7", id);
    }

    @Test
    public void getIdTransmisionSensePrefixNiParametreLlancaScspException() {
        when(paramDao.select("prefijo.idtransmision")).thenReturn(null);
        try {
            idTransmisionGenerator.getIdTransmision(servicio(null, "V3"));
            fail("Hauria d'haver llançat ScspException");
        } catch (ScspException e) {
            // esperat
        }
    }

    @Test
    public void getIdTransmisionAmbPrefixDelParametreMassaCurtLlancaScspException() {
        ParametroConfiguracion param = new ParametroConfiguracion();
        param.setValor("AB");
        when(paramDao.select("prefijo.idtransmision")).thenReturn(param);
        try {
            idTransmisionGenerator.getIdTransmision(servicio(null, "V3"));
            fail("Hauria d'haver llançat ScspException");
        } catch (ScspException e) {
            // esperat
        }
    }

    @Test
    public void getIdTransmisionAmbPrefixDelParametreMassaLlargLlancaScspException() {
        ParametroConfiguracion param = new ParametroConfiguracion();
        param.setValor("ABCDEFGHIJ");
        when(paramDao.select("prefijo.idtransmision")).thenReturn(param);
        try {
            idTransmisionGenerator.getIdTransmision(servicio(null, "V3"));
            fail("Hauria d'haver llançat ScspException");
        } catch (ScspException e) {
            // esperat
        }
    }

    @Test
    public void getIdTransmisionAmbEsquemaV2UsaLongitudCurta() throws Exception {
        when(secuenciaDao.next("ABC")).thenReturn("1");

        String id = idTransmisionGenerator.getIdTransmision(servicio("ABC", "esquema-V2"));

        // longitudSecuencial = 25 - 3 = 22
        assertEquals("TABC" + "0".repeat(21) + "1", id);
    }

    @Test
    public void getIdTransmisionAmbSecuencialExcessiuLlancaScspException() throws Exception {
        when(secuenciaDao.next("ABC")).thenReturn("1234567890123456789012345678901");

        try {
            idTransmisionGenerator.getIdTransmision(servicio("ABC", "V3"));
            fail("Hauria d'haver llançat ScspException");
        } catch (ScspException e) {
            // esperat
        }
    }
}
