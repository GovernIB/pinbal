package es.caib.pinbal.logic.service;

import es.caib.pinbal.logic.config.ScheduleConfig;
import es.caib.pinbal.logic.helper.ConfigHelper;
import es.caib.pinbal.logic.helper.DtoMappingHelper;
import es.caib.pinbal.logic.helper.LoggerHelper;
import es.caib.pinbal.logic.helper.PluginHelper;
import es.caib.pinbal.logic.intf.dto.ConfigDto;
import es.caib.pinbal.logic.intf.dto.ConfigGroupDto;
import es.caib.pinbal.logic.intf.dto.ConfigSourceEnumDto;
import es.caib.pinbal.logic.intf.service.ConfigService;
import es.caib.pinbal.persist.entity.Config;
import es.caib.pinbal.persist.repository.ConfigGroupRepository;
import es.caib.pinbal.persist.repository.ConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Classe que implementa els metodes per consultar i editar les configuracions de l'aplicació.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ConfigServiceImpl implements ConfigService {

    private final ConfigGroupRepository configGroupRepository;
    private final ConfigRepository configRepository;
    private final DtoMappingHelper dtoMappingHelper;
    private final PluginHelper pluginHelper;
    private final ScheduleConfig scheduleConfig;
	private final ConfigHelper configHelper;

    @Override
    @Transactional
    public ConfigDto updateProperty(ConfigDto property) {
        log.info(String.format("Actualització valor propietat %s a %s ", property.getKey(), property.getValue()));
        Config config = configRepository.findById(property.getKey()).orElseThrow();
        if (!config.isEditable()) {
            log.error("ATENCIÓ S'ESTÀ INTENTANT GUARDAR UNA PROPIETAT QUE NO ÉS CONFIGURABLE");
            return null;
        }
        config.update(!"null".equals(property.getValue()) ? property.getValue() : null);
        configHelper.reloadDbProperties();
        pluginHelper.resetPlugins(config.getGroupCode());
        if (property.getKey().startsWith(LoggerHelper.PREFIX)) {
            LoggerHelper.resetLogs();
        }
        return dtoMappingHelper.convertir(config, ConfigDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConfigGroupDto> findAll() {
        log.info("Consulta totes les propietats");
        List<ConfigGroupDto> configGroupDtoList =  dtoMappingHelper.convertirList(
                configGroupRepository.findByParentCodeIsNull(Sort.by(Sort.Direction.ASC, "position")),
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
        var properties = configHelper.getEnvironmentProperties();
        List<String> editedProperties = new ArrayList<>();
        List<String> propertiesList = new ArrayList<>(properties.stringPropertyNames());
        Collections.sort(propertiesList);
        String value;
        Config config;
        for (var key : propertiesList) {
            value = properties.getProperty(key);
            log.info(key + " : " + value);
            config = configRepository.findById(key).orElse(null);
            if (config == null) {
                continue;
            }
            config.update(value);
            editedProperties.add(config.getKey());
        }
        configHelper.reloadDbProperties();
        pluginHelper.resetPlugins();
        return editedProperties;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void actualitzarPropietatsJBossBdd() {

        List<Config> configs = configRepository.findFileProperties();
        String property;
        for(Config config : configs) {
            property = configHelper.getConfig(config.getKey());
            config.update(property);
            configRepository.save(config);
        }
    }

    @Override
    public void reiniciarTasques() {
        scheduleConfig.restartSchedulledTasks();
    }

    @Override
    public void propagateDbProperties() {
        configHelper.reloadDbProperties();
    }

    private void processPropertyValues(ConfigGroupDto cGroup) {

        List<ConfigDto> configs = new ArrayList<>();
        for (ConfigDto config: cGroup.getConfigs()) {
            if ("PASS".equals(config.getTypeCode())){
                config.setValue("*****");
            } else if (ConfigSourceEnumDto.FILE.equals(config.getSourceProperty())) {
                // Les propietats de Jboss es llegeixen del fitxer de properties i si no estan definides prenen el valor especificat a la base de dades.
                config.setValue(configHelper.getConfig(config.getKey(), config.getValue()));
            }
            configs.add(config);
        }
        cGroup.setConfigs(configs);
        if (cGroup.getInnerConfigs() == null || cGroup.getInnerConfigs().isEmpty()) {
            return;
        }
        for (ConfigGroupDto child : cGroup.getInnerConfigs()) {
            processPropertyValues(child);
        }
    }
    
	@Override
	public String getTempsErrorsMonitorIntegracio() {
		return "48";
	}
}
