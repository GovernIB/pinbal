package es.caib.pinbal.scsp;

import org.junit.Test;
import org.w3c.dom.ls.LSInput;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class SchemaValidatorVfsCompatTest {

    private final SchemaValidatorVfsCompat.ClasspathDirectoryResolver resolver =
            new SchemaValidatorVfsCompat.ClasspathDirectoryResolver("/schemas/196v3/");

    @Test
    public void resolveResourceAmbSystemIdNullRetornaNull() {
        assertNull(resolver.resolveResource("type", "ns", "publicId", null, "baseUri"));
    }

    @Test
    public void resolveResourceAmbRutaRelativaLaResolContraElBaseDir() {
        LSInput input = resolver.resolveResource("type", "ns", "pub1", "peticion.xsd", "baseUri");

        assertNotNull(input);
        assertNotNull(input.getByteStream());
        assertEquals("peticion.xsd", input.getSystemId());
        assertEquals("pub1", input.getPublicId());
        assertNull(input.getCharacterStream());
        assertNull(input.getStringData());
        assertNull(input.getBaseURI());
        assertNull(input.getEncoding());
        assertEquals(false, input.getCertifiedText());
    }

    @Test
    public void resolveResourceAmbRutaAbsolutaIgnoraElBaseDir() {
        LSInput input = resolver.resolveResource("type", "ns", "pub2", "/schemas/196v3/peticion.xsd", "baseUri");

        assertNotNull(input);
        assertNotNull(input.getByteStream());
    }

    @Test
    public void resolveResourceAmbUrlAbsolutaNoTrobaRes() {
        LSInput input = resolver.resolveResource("type", "ns", "pub3", "http://example.com/no-existeix.xsd", "baseUri");
        assertNull(input);
    }

    @Test
    public void resolveResourceAmbRecursInexistentRetornaNull() {
        LSInput input = resolver.resolveResource("type", "ns", "pub4", "no-existeix.xsd", "baseUri");
        assertNull(input);
    }
}
