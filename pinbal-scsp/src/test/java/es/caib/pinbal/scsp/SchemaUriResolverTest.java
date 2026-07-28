package es.caib.pinbal.scsp;

import org.junit.Test;
import org.xml.sax.InputSource;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class SchemaUriResolverTest {

    @Test
    public void resolveEntityAmbEsquemaExistentRetornaElContingut() {
        SchemaUriResolver resolver = new SchemaUriResolver("196", "V3");

        InputSource source = resolver.resolveEntity(null, "peticion.xsd", null);

        assertNotNull(source);
        assertNotNull(source.getByteStream());
    }

    @Test
    public void resolveEntityAmbEsquemaInexistentRetornaStreamNul() {
        SchemaUriResolver resolver = new SchemaUriResolver("196", "V3");

        InputSource source = resolver.resolveEntity(null, "no-existeix.xsd", null);

        assertNotNull(source);
        assertNull(source.getByteStream());
    }

    @Test
    public void resolveEntitySenseVersioNoAfegeixSufixDeVersio() {
        SchemaUriResolver resolver = new SchemaUriResolver("196", null);

        // Sense versió, cerca directament a /schemas/196/peticion.xsd (no existeix aquest directori),
        // per tant ha de retornar un InputSource amb stream nul, no llançar cap excepció.
        InputSource source = resolver.resolveEntity(null, "peticion.xsd", null);

        assertNotNull(source);
        assertNull(source.getByteStream());
    }

    @Test
    public void resolveEntityAmbVersioSenseVLaIgnora() {
        SchemaUriResolver resolver = new SchemaUriResolver("196", "3");

        // "3" no conté cap "V", per tant no s'afegeix sufix de versió al directori.
        InputSource source = resolver.resolveEntity(null, "peticion.xsd", null);

        assertNotNull(source);
        assertNull(source.getByteStream());
    }
}
