package es.caib.pinbal.core.helper;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import es.caib.pinbal.core.dto.ConfigSourceEnumDto;
import es.caib.pinbal.core.model.Config;
import es.caib.pinbal.core.model.ConfigGroup;
import es.caib.pinbal.core.repository.ConfigGroupRepository;
import es.caib.pinbal.core.repository.ConfigRepository;
import es.caib.pinbal.core.service.exception.NotDefinedConfigException;
import lombok.extern.slf4j.Slf4j;

@Component
public class ConfigHelper {

    private static ConfigHelper instance = null;

    public static ConfigHelper getInstance() {
        return instance;
    }

    @Autowired
    private ConfigRepository configRepository;
    @Autowired
    private ConfigGroupRepository configGroupRepository;

    @Transactional(readOnly = true)
    public String getConfig(String key) throws NotDefinedConfigException {
        Config config = configRepository.findOne(key);
        if (config == null) {
            throw new NotDefinedConfigException(key);
        }
        return getConfig(config);
    }

    @Transactional(readOnly = true)
    public String getConfig(String key, String defaultValue) {
        String value = null;
        try {
            value = getConfig(key);
        } catch (NotDefinedConfigException ex) {}

        if (value == null)
            value = defaultValue;

        return value;
    }

    @Transactional(readOnly = true)
    public Map<String, String> getGroupProperties(String codeGroup) {
        Map<String, String> properties = new HashMap<>();
        ConfigGroup configGroup = configGroupRepository.findOne(codeGroup);
        fillGroupProperties(configGroup, properties);
        return properties;
    }

    private void fillGroupProperties(ConfigGroup configGroup, Map<String, String> outProperties) {
        if (configGroup == null) {
            return;
        }
        for (Config config : configGroup.getConfigs()) {
            outProperties.put(config.getKey(), getConfig(config));
        }

        if (configGroup.getInnerConfigs() != null) {
            for (ConfigGroup child : configGroup.getInnerConfigs()) {
                fillGroupProperties(child, outProperties);
            }
        }
    }

    @Transactional(readOnly = true)
    public boolean getAsBoolean(String key) {
        return Boolean.parseBoolean(getConfig(key));
    }
    @Transactional(readOnly = true)
    public boolean getAsBoolean(String key, boolean defaultValue) {
        return Boolean.parseBoolean(getConfig(key, defaultValue ? "true" : "false"));
    }
    @Transactional(readOnly = true)
    public int getAsInt(String key) {
        return new Integer(getConfig(key));
    }
    @Transactional(readOnly = true)
    public int getAsInt(String key, @NotNull Integer defaultValue) {
        return new Integer(getConfig(key, defaultValue.toString()));
    }
    @Transactional(readOnly = true)
    public long getAsLong(String key) {
        return new Long(getConfig(key));
    }
    @Transactional(readOnly = true)
    public long getAsLong(String key, @NotNull Long defaultValue) {
        return new Long(getConfig(key, defaultValue.toString()));
    }
    @Transactional(readOnly = true)
    public float getAsFloat(String key) {
        return new Float(getConfig(key));
    }

    public String getJBossProperty(String key) {
        return JBossPropertiesHelper.getProperties().getProperty(key);
    }
    public String getJBossProperty(String key, String defaultValue) {
        return JBossPropertiesHelper.getProperties().getProperty(key, defaultValue);
    }

    private String getConfig(Config config) throws NotDefinedConfigException {
        if (ConfigSourceEnumDto.FILE.equals(config.getSourceProperty())) {
            // Les propietats de Jboss es llegeixen del fitxer de properties i si no estan definides prenen el valor especificat per defecte a la base de dades.
            return getJBossProperty(config.getKey(), config.getValue());
        }
        return config.getValue();
    }

	@Slf4j
    @SuppressWarnings("serial")
    public static class JBossPropertiesHelper extends Properties {

        private static final String APPSERV_PROPS_PATH = "es.caib.pinbal.properties.path";

        private static JBossPropertiesHelper instance = null;

        private boolean llegirSystem = true;

        public static JBossPropertiesHelper getProperties() {
            return getProperties(null);
        }
        public static JBossPropertiesHelper getProperties(String path) {
            String propertiesPath = path;
            if (propertiesPath == null) {
                propertiesPath = System.getProperty(APPSERV_PROPS_PATH);
            }
            if (instance == null) {
                instance = new JBossPropertiesHelper();
                if (propertiesPath != null) {
                    instance.llegirSystem = false;
                    log.info("Llegint les propietats de l'aplicació del path: " + propertiesPath);
                    try {
                        if (propertiesPath.startsWith("classpath:")) {
                            instance.load(
                                    JBossPropertiesHelper.class.getClassLoader().getResourceAsStream(
                                            propertiesPath.substring("classpath:".length())));
                        } else if (propertiesPath.startsWith("file://")) {
                            FileInputStream fis = new FileInputStream(
                                    propertiesPath.substring("file://".length()));
                            instance.load(fis);
                        } else {
                            FileInputStream fis = new FileInputStream(propertiesPath);
                            instance.load(fis);
                        }
                    } catch (Exception ex) {
                        log.error("No s'han pogut llegir els properties", ex);
                    }
                }
            }
            return instance;
        }

        public String getProperty(String key) {
            if (llegirSystem)
                return System.getProperty(key);
            else
                return super.getProperty(key);
        }
        public String getProperty(String key, String defaultValue) {
            String val = getProperty(key);
            return (val == null) ? defaultValue : val;
        }

        public boolean isLlegirSystem() {
            return llegirSystem;
        }
        public void setLlegirSystem(boolean llegirSystem) {
            this.llegirSystem = llegirSystem;
        }


        public Properties findAll() {
            return findByPrefixProperties(null);
        }

        public Map<String, String> findByPrefix(String prefix) {
            Map<String, String> properties = new HashMap<String, String>();
            if (llegirSystem) {
                for (Object key: System.getProperties().keySet()) {
                    if (key instanceof String) {
                        String keystr = (String)key;
                        if (keystr.startsWith(prefix)) {
                            properties.put(
                                    keystr,
                                    System.getProperty(keystr));
                        }
                    }
                }
            } else {
                for (Object key: this.keySet()) {
                    if (key instanceof String) {
                        String keystr = (String)key;
                        if (keystr.startsWith(prefix)) {
                            properties.put(
                                    keystr,
                                    getProperty(keystr));
                        }
                    }
                }
            }
            return properties;
        }

        public Properties findByPrefixProperties(String prefix) {
            Properties properties = new Properties();
            if (llegirSystem) {
                for (Object key: System.getProperties().keySet()) {
                    if (key instanceof String) {
                        String keystr = (String)key;
                        if (prefix == null || keystr.startsWith(prefix)) {
                            properties.put(
                                    keystr,
                                    System.getProperty(keystr));
                        }
                    }
                }
            } else {
                for (Object key: this.keySet()) {
                    if (key instanceof String) {
                        String keystr = (String)key;
                        if (prefix == null || keystr.startsWith(prefix)) {
                            properties.put(
                                    keystr,
                                    getProperty(keystr));
                        }
                    }
                }
            }
            return properties;
        }
    }

    @PostConstruct
    public void postConstruct() {
        ConfigHelper.instance = this;
    }

}
