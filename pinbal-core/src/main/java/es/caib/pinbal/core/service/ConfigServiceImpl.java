package es.caib.pinbal.core.service;

import es.caib.pinbal.core.config.ScheduleConfig;
import es.caib.pinbal.core.dto.ConfigDto;
import es.caib.pinbal.core.dto.ConfigGroupDto;
import es.caib.pinbal.core.dto.ConfigSourceEnumDto;
import es.caib.pinbal.core.helper.ConfigHelper;
import es.caib.pinbal.core.helper.DtoMappingHelper;
import es.caib.pinbal.core.helper.PluginHelper;
import es.caib.pinbal.core.model.Config;
import es.caib.pinbal.core.repository.ConfigGroupRepository;
import es.caib.pinbal.core.repository.ConfigRepository;
import es.caib.pinbal.core.repository.ScspParametreConfiguracioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Classe que implementa els metodes per consultar i editar les configuracions de l'aplicació.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class ConfigServiceImpl implements ConfigService {

    @Autowired
    private ConfigGroupRepository configGroupRepository;
    @Autowired
    private ConfigRepository configRepository;
    @Autowired
    private ScspParametreConfiguracioRepository scspParametreConfiguracioRepository;
    @Autowired
    private DtoMappingHelper dtoMappingHelper;
    @Autowired
    private PluginHelper pluginHelper;
    @Autowired
    private ScheduleConfig scheduleConfig;

    @Override
    @Transactional
    public ConfigDto updateProperty(ConfigDto property) {
        log.info(String.format("Actualització valor propietat %s a %s ", property.getKey(), property.getValue()));
        Config config = configRepository.findOne(property.getKey());
        config.update(property.getValue());
        pluginHelper.reloadProperties(config.getGroupCode());
        if (property.getKey().endsWith(".class")){
            pluginHelper.resetPlugins();
        }
        return dtoMappingHelper.convertir(config, ConfigDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConfigGroupDto> findAll() {
        log.info("Consulta totes les propietats");
        List<ConfigGroupDto> configGroupDtoList =  dtoMappingHelper.convertirList(
                configGroupRepository.findByParentCodeIsNull(new Sort(Sort.Direction.ASC, "position")),
                ConfigGroupDto.class);

        for (ConfigGroupDto cGroup: configGroupDtoList) {
            processPropertyValues(cGroup);
        }
        return configGroupDtoList;
    }

    @Override
    @Transactional
    public List<String> syncFromJBossProperties() {
        log.info("Sincronitzant les propietats amb JBoss");
        Properties properties = ConfigHelper.JBossPropertiesHelper.getProperties().findAll();
        List<String> editedProperties = new ArrayList<>();
        List<String> propertiesList = new ArrayList<>(properties.stringPropertyNames());
        Collections.sort(propertiesList);
        for (String key : propertiesList) {
            String value = properties.getProperty(key);
            log.info(key + " : " + value);
            Config config = configRepository.findOne(key);
            if (config != null) {
                config.update(value);
                pluginHelper.reloadProperties(config.getGroupCode());
                if (config.getKey().endsWith(".class")){
                    pluginHelper.resetPlugins();
                }
                editedProperties.add(config.getKey());
            }
        }
        return editedProperties;
    }

    @Override
    public void reiniciarTasques() {
        scheduleConfig.restartSchedulledTasks();
    }

    private void processPropertyValues(ConfigGroupDto cGroup) {
        for (ConfigDto config: cGroup.getConfigs()) {
            if ("PASS".equals(config.getTypeCode())){
                config.setValue("*****");
            } else if (ConfigSourceEnumDto.FILE.equals(config.getSourceProperty())) {
                // Les propietats de Jboss es llegeixen del fitxer de properties i si no estan definides prenen el valor especificat a la base de dades.
                config.setValue(ConfigHelper.JBossPropertiesHelper.getProperties().getProperty(config.getKey(), config.getValue()));
            }
        }

        if (cGroup.getInnerConfigs() != null && !cGroup.getInnerConfigs().isEmpty()) {
            for (ConfigGroupDto child : cGroup.getInnerConfigs()) {
                processPropertyValues(child);
            }
        }
    }
}
