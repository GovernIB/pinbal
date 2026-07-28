package es.caib.pinbal.scsp;

import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class PropertiesHelperTest {

    @After
    public void netejar() {
        System.clearProperty("pinbal.scsp.test.prop");
        System.clearProperty("pinbal.scsp.test.bool");
        System.clearProperty("pinbal.scsp.test.int");
        System.clearProperty("pinbal.scsp.test.long");
        System.clearProperty("pinbal.scsp.test.float");
        System.clearProperty("pinbal.scsp.test.double");
    }

    @Test
    public void getPropertiesEsUnSingleton() {
        assertSame(PropertiesHelper.getProperties(), PropertiesHelper.getProperties());
    }

    @Test
    public void getPropertyLlegeixDeLesPropietatsDelSistemaPerDefecte() {
        System.setProperty("pinbal.scsp.test.prop", "valor-sistema");
        assertEquals("valor-sistema", PropertiesHelper.getProperties().getProperty("pinbal.scsp.test.prop"));
    }

    @Test
    public void getPropertyAmbClauInexistentRetornaNull() {
        assertNull(PropertiesHelper.getProperties().getProperty("pinbal.scsp.clau.inexistent"));
    }

    @Test
    public void getPropertyAmbValorPerDefecte() {
        PropertiesHelper properties = PropertiesHelper.getProperties();
        assertEquals("per-defecte", properties.getProperty("pinbal.scsp.clau.inexistent", "per-defecte"));

        System.setProperty("pinbal.scsp.test.prop", "valor-real");
        assertEquals("valor-real", properties.getProperty("pinbal.scsp.test.prop", "per-defecte"));
    }

    @Test
    public void getAsBooleanParsejaElValor() {
        System.setProperty("pinbal.scsp.test.bool", "true");
        assertTrue(PropertiesHelper.getProperties().getAsBoolean("pinbal.scsp.test.bool"));
        System.setProperty("pinbal.scsp.test.bool", "false");
        assertFalse(PropertiesHelper.getProperties().getAsBoolean("pinbal.scsp.test.bool"));
    }

    @Test
    public void getAsIntParsejaElValor() {
        System.setProperty("pinbal.scsp.test.int", "42");
        assertEquals(42, PropertiesHelper.getProperties().getAsInt("pinbal.scsp.test.int"));
    }

    @Test
    public void getAsLongParsejaElValor() {
        System.setProperty("pinbal.scsp.test.long", "123456789012");
        assertEquals(123456789012L, PropertiesHelper.getProperties().getAsLong("pinbal.scsp.test.long"));
    }

    @Test
    public void getAsFloatParsejaElValor() {
        System.setProperty("pinbal.scsp.test.float", "3.14");
        assertEquals(3.14f, PropertiesHelper.getProperties().getAsFloat("pinbal.scsp.test.float"), 0.0001f);
    }

    @Test
    public void getAsDoubleParsejaElValor() {
        System.setProperty("pinbal.scsp.test.double", "2.71828");
        assertEquals(2.71828d, PropertiesHelper.getProperties().getAsDouble("pinbal.scsp.test.double"), 0.00001d);
    }
}
