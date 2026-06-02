package es.caib.pinbal.logic.helper;

import es.caib.pinbal.logic.intf.base.config.BaseConfig;
import es.caib.pinbal.logic.intf.service.exception.NotDefinedConfigException;
import es.caib.pinbal.persist.entity.ConfigGroup;
import es.caib.pinbal.persist.repository.ConfigGroupRepository;
import es.caib.pinbal.persist.repository.ConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import static es.caib.pinbal.logic.config.ReadDbPropertiesPostProcessor.DBAPP_PROPERTIES;

@Component
@RequiredArgsConstructor
@Slf4j
@PropertySource(ignoreResourceNotFound = true, value = {
        "file://${" + BaseConfig.APP_PROPERTIES + "}",
        "file://${" + BaseConfig.APP_SYSTEM_PROPERTIES + "}"})
public class ConfigHelper {

    private final ConfigurableEnvironment environment;
    private final ConfigRepository configRepository;
    private final ConfigGroupRepository configGroupRepository;


    // Crear una instància estàtica
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static ConfigHelper instance = null;

    public static ConfigHelper getInstance() {
        return instance;
    }

    @PostConstruct
    public void postConstruct() {
        ConfigHelper.instance = this;
    }
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private Optional<String> getPropietat(String key) {
        var propertyValue = environment.getProperty(key);
        return propertyValue != null ? Optional.of(propertyValue) : Optional.empty();
    }

    @Transactional(readOnly = true)
    public String getConfig(String key) throws NotDefinedConfigException {
        return getPropietat(key).orElse(null);
    }
    @Transactional(readOnly = true)
    public String getConfig(String key, String defaultValue) {
        return getPropietat(key).orElse(defaultValue);
    }

    @Transactional(readOnly = true)
    public boolean getConfigAsBoolean(String key) {
        return getPropietat(key).map(Boolean::parseBoolean).orElseThrow(() -> new NotDefinedConfigException(key));
    }
    @Transactional(readOnly = true)
    public boolean getConfigAsBoolean(String key, boolean defaultValue) {
        return getPropietat(key).map(Boolean::parseBoolean).orElse(defaultValue);
    }

    @Transactional(readOnly = true)
    public int getConfigAsInt(String key) {
        return getPropietat(key).map(Integer::parseInt).orElseThrow(() -> new NotDefinedConfigException(key));
    }
    @Transactional(readOnly = true)
    public int getConfigAsInt(String key, @NotNull Integer defaultValue) {
        return getPropietat(key).map(Integer::parseInt).orElse(defaultValue);
    }

    @Transactional(readOnly = true)
    public long getConfigAsLong(String key) {
        return getPropietat(key).map(Long::parseLong).orElseThrow(() -> new NotDefinedConfigException(key));
    }
    @Transactional(readOnly = true)
    public long getConfigAsLong(String key, @NotNull Long defaultValue) {
        return getPropietat(key).map(Long::parseLong).orElse(defaultValue);
    }

    @Transactional(readOnly = true)
    public float getAsFloat(String key) {
        return getPropietat(key).map(Float::parseFloat).orElseThrow(() -> new NotDefinedConfigException(key));
    }
    @Transactional(readOnly = true)
    public float getAsFloat(String key, @NotNull Float defaultValue) {
        return getPropietat(key).map(Float::parseFloat).orElse(defaultValue);
    }

    @Transactional(readOnly = true)
    public Map<String, String> getGroupProperties(String codeGroup) {
        Map<String, String> properties = new HashMap<>();
        ConfigGroup configGroup = configGroupRepository.findById(codeGroup).orElse(null);
        fillGroupProperties(configGroup, properties);
        return properties;
    }

    private void fillGroupProperties(ConfigGroup configGroup, Map<String, String> outProperties) {

        if (configGroup == null) {
            return;
        }
        for (var config : configGroup.getConfigs()) {
            outProperties.put(config.getKey(), getConfig(config.getKey()));
        }

        if (configGroup.getInnerConfigs() == null) {
            return;
        }
        for (var child : configGroup.getInnerConfigs()) {
            fillGroupProperties(child, outProperties);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void schedullingLoadDbProperties() {
        reloadDbProperties();
    }

    public void reloadDbProperties() {

        Map<String, Object> propertySource = new HashMap<>();
        var dbProperties = configRepository.findDbProperties();
        dbProperties.forEach(p -> propertySource.put(p.getKey(), p.getValue()));
        if (environment.getPropertySources().contains(DBAPP_PROPERTIES)) {
            environment.getPropertySources().replace(DBAPP_PROPERTIES, new MapPropertySource(DBAPP_PROPERTIES, propertySource));
            return;
        }
        environment.getPropertySources().addFirst(new MapPropertySource(DBAPP_PROPERTIES, propertySource));
    }

    public Properties getEnvironmentProperties() {

        var properties = new Properties();
        environment.getPropertySources().stream().forEach(ps -> {
            if (ps instanceof MapPropertySource) {
                properties.putAll(((MapPropertySource) ps).getSource().entrySet().stream().filter(x -> x.getValue() != null)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
            }
            if (ps instanceof CompositePropertySource) {
                ((CompositePropertySource) ps).getPropertySources().stream().forEach( cps -> {
                    if (cps instanceof MapPropertySource) {
                        properties.putAll(((MapPropertySource) cps).getSource().entrySet().stream().filter(x -> x.getValue() != null)
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
                    }
                });
            }
        });
        return properties;
    }

}
