package es.caib.pinbal.plugin;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class PropertiesHelperTest {

    @BeforeEach
    @AfterEach
    void resetSingleton() throws Exception {
        Field field = PropertiesHelper.class.getDeclaredField("instance");
        field.setAccessible(true);
        field.set(null, null);
        System.clearProperty("es.caib.pinbal.properties.path");
    }

    @Test
    void getPropertiesWithoutPathFallsBackToSystemProperties() {
        System.setProperty("pinbal.test.prop", "valor-sistema");
        try {
            PropertiesHelper properties = PropertiesHelper.getProperties();
            assertThat(properties.getProperty("pinbal.test.prop")).isEqualTo("valor-sistema");
        } finally {
            System.clearProperty("pinbal.test.prop");
        }
    }

    @Test
    void getPropertiesIsASingleton() {
        PropertiesHelper first = PropertiesHelper.getProperties();
        PropertiesHelper second = PropertiesHelper.getProperties();
        assertThat(first).isSameAs(second);
    }

    @Test
    void getPropertiesFromClasspathPath() {
        PropertiesHelper properties = PropertiesHelper.getProperties("classpath:test.properties");
        assertThat(properties.getProperty("es.caib.pinbal.plugin.firmaservidor.portafib.location")).isEqualTo("Palma");
    }

    @Test
    void getPropertiesFromFileUrlPath(@org.junit.jupiter.api.io.TempDir File tempDir) throws Exception {
        File propsFile = new File(tempDir, "custom.properties");
        try (FileWriter writer = new FileWriter(propsFile)) {
            writer.write("clau.prova=valor-fitxer\n");
        }
        PropertiesHelper properties = PropertiesHelper.getProperties("file://" + propsFile.getAbsolutePath());
        assertThat(properties.getProperty("clau.prova")).isEqualTo("valor-fitxer");
    }

    @Test
    void getPropertiesFromPlainFilePath(@org.junit.jupiter.api.io.TempDir File tempDir) throws Exception {
        File propsFile = new File(tempDir, "custom2.properties");
        try (FileWriter writer = new FileWriter(propsFile)) {
            writer.write("clau.prova2=valor-fitxer2\n");
        }
        PropertiesHelper properties = PropertiesHelper.getProperties(propsFile.getAbsolutePath());
        assertThat(properties.getProperty("clau.prova2")).isEqualTo("valor-fitxer2");
    }

    @Test
    void getPropertiesWithUnreadableFileDoesNotThrow() {
        PropertiesHelper properties = PropertiesHelper.getProperties("/path/que/no/existeix/mai.properties");
        assertThat(properties).isNotNull();
    }

    @Test
    void getPropertyReturnsNullWhenMissing() {
        PropertiesHelper properties = PropertiesHelper.getProperties("classpath:test.properties");
        assertThat(properties.getProperty("clau.inexistent")).isNull();
    }

    @Test
    void getPropertyWithDefaultValue() {
        PropertiesHelper properties = PropertiesHelper.getProperties("classpath:test.properties");
        assertThat(properties.getProperty("clau.inexistent", "per-defecte")).isEqualTo("per-defecte");
        assertThat(properties.getProperty("es.caib.pinbal.plugin.firmaservidor.portafib.location", "per-defecte")).isEqualTo("Palma");
    }

    @Test
    void getAsBooleanParsesValue() {
        Properties defaults = new Properties();
        defaults.setProperty("bool.true", "true");
        defaults.setProperty("bool.false", "false");
        PropertiesHelper properties = PropertiesHelper.getProperties();
        properties.putAll(defaults);
        assertThat(properties.getAsBoolean("bool.true")).isTrue();
        assertThat(properties.getAsBoolean("bool.false")).isFalse();
    }

    @Test
    void getAsIntParsesValue() {
        PropertiesHelper properties = PropertiesHelper.getProperties();
        properties.setProperty("int.value", "42");
        assertThat(properties.getAsInt("int.value")).isEqualTo(42);
    }

    @Test
    void getAsIntWithDefaultReturnsParsedValueWhenValid() {
        PropertiesHelper properties = PropertiesHelper.getProperties();
        properties.setProperty("int.value", "7");
        assertThat(properties.getAsInt("int.value", 99)).isEqualTo(7);
    }

    @Test
    void getAsIntWithDefaultReturnsDefaultOnBadValue() {
        PropertiesHelper properties = PropertiesHelper.getProperties();
        properties.setProperty("int.bad", "no-es-un-numero");
        assertThat(properties.getAsInt("int.bad", 99)).isEqualTo(99);
    }

    @Test
    void getAsLongParsesValue() {
        PropertiesHelper properties = PropertiesHelper.getProperties();
        properties.setProperty("long.value", "123456789012");
        assertThat(properties.getAsLong("long.value")).isEqualTo(123456789012L);
    }

    @Test
    void getAsFloatParsesValue() {
        PropertiesHelper properties = PropertiesHelper.getProperties();
        properties.setProperty("float.value", "3.14");
        assertThat(properties.getAsFloat("float.value")).isEqualTo(3.14f);
    }

    @Test
    void getAsDoubleParsesValue() {
        PropertiesHelper properties = PropertiesHelper.getProperties();
        properties.setProperty("double.value", "2.71828");
        assertThat(properties.getAsDouble("double.value")).isEqualTo(2.71828d);
    }

    @Test
    void findByPrefixReturnsOnlyMatchingKeys() {
        PropertiesHelper properties = PropertiesHelper.getProperties();
        properties.setProperty("prefix.a", "1");
        properties.setProperty("prefix.b", "2");
        properties.setProperty("altre.c", "3");

        Properties result = properties.findByPrefix("prefix.");

        assertThat(result).hasSize(2);
        assertThat(result.getProperty("prefix.a")).isEqualTo("1");
        assertThat(result.getProperty("prefix.b")).isEqualTo("2");
        assertThat(result.containsKey("altre.c")).isFalse();
    }

    @Test
    void findAllReturnsEveryKey() {
        PropertiesHelper properties = PropertiesHelper.getProperties();
        properties.setProperty("qualsevol.clau", "valor");

        Properties result = properties.findAll();

        assertThat(result.getProperty("qualsevol.clau")).isEqualTo("valor");
    }
}
