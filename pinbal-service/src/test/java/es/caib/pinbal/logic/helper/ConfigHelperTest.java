package es.caib.pinbal.logic.helper;

import es.caib.pinbal.logic.intf.service.exception.NotDefinedConfigException;
import es.caib.pinbal.persist.entity.Config;
import es.caib.pinbal.persist.entity.ConfigGroup;
import es.caib.pinbal.persist.repository.ConfigGroupRepository;
import es.caib.pinbal.persist.repository.ConfigRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static es.caib.pinbal.logic.config.ReadDbPropertiesPostProcessor.DBAPP_PROPERTIES;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class ConfigHelperTest {

    @Mock private ConfigurableEnvironment environment;
    @Mock private ConfigRepository configRepository;
    @Mock private ConfigGroupRepository configGroupRepository;

    @InjectMocks
    private ConfigHelper configHelper;

    private final MutablePropertySources propertySources = new MutablePropertySources();

    @BeforeEach
    public void setUp() {
        when(environment.getPropertySources()).thenReturn(propertySources);
    }

    @AfterEach
    public void tearDown() {
        System.clearProperty("prop.test1");
        System.clearProperty("prop.test2");
    }

    @Test
    public void getConfig_propietatExisteix_retornaValor() {
        when(environment.getProperty("clau1")).thenReturn("valor1");

        String result = configHelper.getConfig("clau1");

        assertEquals("valor1", result);
    }

    @Test
    public void getConfig_propietatNoExisteix_retornaNull() {
        when(environment.getProperty("clauInexistent")).thenReturn(null);

        assertNull(configHelper.getConfig("clauInexistent"));
    }

    @Test
    public void getConfig_ambValorPerDefecte_propietatNoExisteix_retornaDefecte() {
        when(environment.getProperty("clauInexistent")).thenReturn(null);

        assertEquals("defecte", configHelper.getConfig("clauInexistent", "defecte"));
    }

    @Test
    public void getConfigAsBoolean_propietatExisteix_retornaBoolean() {
        when(environment.getProperty("flag")).thenReturn("true");

        assertTrue(configHelper.getConfigAsBoolean("flag"));
    }

    @Test
    public void getConfigAsBoolean_propietatNoExisteix_llancaExcepcio() {
        when(environment.getProperty("flagInexistent")).thenReturn(null);

        assertThrows(NotDefinedConfigException.class, () -> configHelper.getConfigAsBoolean("flagInexistent"));
    }

    @Test
    public void getConfigAsBoolean_ambDefecte_propietatNoExisteix_retornaDefecte() {
        when(environment.getProperty("flagInexistent")).thenReturn(null);

        assertFalse(configHelper.getConfigAsBoolean("flagInexistent", false));
    }

    @Test
    public void getConfigAsInt_propietatExisteix_retornaInt() {
        when(environment.getProperty("numero")).thenReturn("42");

        assertEquals(42, configHelper.getConfigAsInt("numero"));
    }

    @Test
    public void getConfigAsInt_propietatNoExisteix_llancaExcepcio() {
        when(environment.getProperty("numeroInexistent")).thenReturn(null);

        assertThrows(NotDefinedConfigException.class, () -> configHelper.getConfigAsInt("numeroInexistent"));
    }

    @Test
    public void getConfigAsLong_propietatExisteix_retornaLong() {
        when(environment.getProperty("numeroLlarg")).thenReturn("123456789012");

        assertEquals(123456789012L, configHelper.getConfigAsLong("numeroLlarg"));
    }

    @Test
    public void getAsFloat_propietatExisteix_retornaFloat() {
        when(environment.getProperty("decimal")).thenReturn("3.14");

        assertEquals(3.14f, configHelper.getAsFloat("decimal"));
    }

    @Test
    public void getAsFloat_ambDefecte_propietatNoExisteix_retornaDefecte() {
        when(environment.getProperty("decimalInexistent")).thenReturn(null);

        assertEquals(1.5f, configHelper.getAsFloat("decimalInexistent", 1.5f));
    }

    @Test
    public void getGroupProperties_grupNoExisteix_retornaMapaBuit() {
        when(configGroupRepository.findById("GRUP1")).thenReturn(Optional.empty());

        Map<String, String> result = configHelper.getGroupProperties("GRUP1");

        assertTrue(result.isEmpty());
    }

    @Test
    public void getGroupProperties_grupAmbConfigs_retornaValorsDeCadaConfig() {
        Config c1 = new Config("clauA", "valorA");
        Config c2 = new Config("clauB", "valorB");
        ConfigGroup group = ConfigGroup.builder()
                .key("GRUP1")
                .configs(Set.of(c1, c2))
                .build();
        when(configGroupRepository.findById("GRUP1")).thenReturn(Optional.of(group));
        when(environment.getProperty("clauA")).thenReturn("valorA");
        when(environment.getProperty("clauB")).thenReturn("valorB");

        Map<String, String> result = configHelper.getGroupProperties("GRUP1");

        assertEquals("valorA", result.get("clauA"));
        assertEquals("valorB", result.get("clauB"));
    }

    @Test
    public void getGroupProperties_grupAmbSubgrups_recorreRecursivament() {
        Config childConfig = new Config("clauFilla", "valorFilla");
        ConfigGroup childGroup = ConfigGroup.builder()
                .key("GRUP1_FILL")
                .configs(Set.of(childConfig))
                .build();
        ConfigGroup parentGroup = ConfigGroup.builder()
                .key("GRUP1")
                .configs(Set.of())
                .innerConfigs(Set.of(childGroup))
                .build();
        when(configGroupRepository.findById("GRUP1")).thenReturn(Optional.of(parentGroup));
        when(environment.getProperty("clauFilla")).thenReturn("valorFilla");

        Map<String, String> result = configHelper.getGroupProperties("GRUP1");

        assertEquals("valorFilla", result.get("clauFilla"));
    }

    @Test
    public void reloadDbProperties_senseFontPrevia_lAfegeixAlPrincipi() {
        Config c1 = new Config("prop.test1", "valor1");
        when(configRepository.findDbProperties()).thenReturn(List.of(c1));

        configHelper.reloadDbProperties();

        assertTrue(propertySources.contains(DBAPP_PROPERTIES));
        assertEquals("valor1", propertySources.get(DBAPP_PROPERTIES).getProperty("prop.test1"));
        assertEquals("valor1", System.getProperty("prop.test1"));
    }

    @Test
    public void reloadDbProperties_ambFontExistent_laSubstitueix() {
        Map<String, Object> initial = new HashMap<>();
        initial.put("prop.test1", "antic");
        propertySources.addFirst(new MapPropertySource(DBAPP_PROPERTIES, initial));

        Config c1 = new Config("prop.test1", "nou");
        when(configRepository.findDbProperties()).thenReturn(List.of(c1));

        configHelper.reloadDbProperties();

        assertEquals("nou", propertySources.get(DBAPP_PROPERTIES).getProperty("prop.test1"));
        assertEquals("nou", System.getProperty("prop.test1"));
    }

    @Test
    public void getEnvironmentProperties_recullPropietatsDeMapPropertySource() {
        Map<String, Object> source = new HashMap<>();
        source.put("prop.test1", "valor1");
        source.put("prop.test2", "valor2");
        propertySources.addFirst(new MapPropertySource("test-source", source));

        java.util.Properties result = configHelper.getEnvironmentProperties();

        assertEquals("valor1", result.getProperty("prop.test1"));
        assertEquals("valor2", result.getProperty("prop.test2"));
    }
}
