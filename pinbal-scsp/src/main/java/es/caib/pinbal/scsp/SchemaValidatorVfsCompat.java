package es.caib.pinbal.scsp;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import java.io.InputStream;
import java.io.Reader;

/**
 * Suport per a SchemaValidator en entorns JBoss EAP amb VFS.
 *
 * ClasspathDirectoryResolver resol xs:include / xs:import relatius contra
 * un directori base dins el classpath (p.ex. /schemas/SVDDGPCIWS02v3/).
 * Això permet que SchemaValidator carregui esquemes XSD des de JARs sense
 * necessitar accés a fitxers reals al sistema de fitxers.
 */
public class SchemaValidatorVfsCompat {

    public static class ClasspathDirectoryResolver implements LSResourceResolver {

        private final String baseDir;

        public ClasspathDirectoryResolver(String baseDir) {
            this.baseDir = baseDir;
        }

        @Override
        public LSInput resolveResource(String type, String namespaceURI,
                String publicId, String systemId, String baseURI) {
            if (systemId == null) {
                return null;
            }
            String resourcePath;
            if (systemId.contains("://") || systemId.startsWith("/")) {
                resourcePath = systemId;
            } else {
                resourcePath = baseDir + systemId;
            }
            InputStream is = SchemaValidatorVfsCompat.class.getResourceAsStream(resourcePath);
            if (is == null) {
                String noSlash = resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath;
                is = Thread.currentThread().getContextClassLoader().getResourceAsStream(noSlash);
            }
            if (is == null) {
                return null;
            }
            return new LSInputImpl(publicId, systemId, is);
        }

        private static class LSInputImpl implements LSInput {
            private final String publicId;
            private final String systemId;
            private final InputStream byteStream;

            LSInputImpl(String publicId, String systemId, InputStream byteStream) {
                this.publicId = publicId;
                this.systemId = systemId;
                this.byteStream = byteStream;
            }

            @Override public Reader getCharacterStream() { return null; }
            @Override public void setCharacterStream(Reader r) {}
            @Override public InputStream getByteStream() { return byteStream; }
            @Override public void setByteStream(InputStream s) {}
            @Override public String getStringData() { return null; }
            @Override public void setStringData(String s) {}
            @Override public String getSystemId() { return systemId; }
            @Override public void setSystemId(String s) {}
            @Override public String getPublicId() { return publicId; }
            @Override public void setPublicId(String s) {}
            @Override public String getBaseURI() { return null; }
            @Override public void setBaseURI(String s) {}
            @Override public String getEncoding() { return null; }
            @Override public void setEncoding(String s) {}
            @Override public boolean getCertifiedText() { return false; }
            @Override public void setCertifiedText(boolean b) {}
        }
    }
}
