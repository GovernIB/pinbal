package es.caib.pinbal.logic.service;

import es.caib.pinbal.logic.config.ScheduleConfig;
import es.caib.pinbal.logic.helper.ConfigHelper;
import es.caib.pinbal.logic.helper.DtoMappingHelper;
import es.caib.pinbal.logic.helper.LoggerHelper;
import es.caib.pinbal.logic.helper.PluginHelper;
import es.caib.pinbal.logic.intf.dto.ConfigDto;
import es.caib.pinbal.logic.intf.dto.ConfigGroupDto;
import es.caib.pinbal.logic.intf.dto.ConfigSourceEnumDto;
import es.caib.pinbal.persist.entity.Config;
import es.caib.pinbal.persist.repository.ConfigGroupRepository;
import es.caib.pinbal.persist.repository.ConfigRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class ConfigServiceImplTest {

    @Mock private ConfigGroupRepository configGroupRepository;
    @Mock private ConfigRepository configRepository;
    @Mock private DtoMappingHelper dtoMappingHelper;
    @Mock private PluginHelper pluginHelper;
    @Mock private ScheduleConfig scheduleConfig;
    @Mock private ConfigHelper configHelper;

    @InjectMocks
    private ConfigServiceImpl configService;

    private Config config;
    private ConfigDto configDto;

    @BeforeEach
    public void setUp() {
        config = mock(Config.class);
        when(config.isEditable()).thenReturn(true);
        when(config.getGroupCode()).thenReturn("GENERAL");
        when(config.getKey()).thenReturn("es.caib.pinbal.test.key");

        configDto = new ConfigDto();
        configDto.setKey("es.caib.pinbal.test.key");
        configDto.setValue("valor");
    }

    @Test
    public void updateProperty_configEditable_actualitzaIRetornaDto() {
        when(configRepository.findById("es.caib.pinbal.test.key")).thenReturn(Optional.of(config));
        ConfigDto expected = new ConfigDto();
        when(dtoMappingHelper.convertir(config, ConfigDto.class)).thenReturn(expected);

        ConfigDto result = configService.updateProperty(configDto);

        assertNotNull(result);
        verify(config).update("valor");
        verify(configHelper).reloadDbProperties();
        verify(pluginHelper).resetPlugins("GENERAL");
    }

    @Test
    public void updateProperty_configNoEditable_retornaNull() {
        when(config.isEditable()).thenReturn(false);
        when(configRepository.findById("es.caib.pinbal.test.key")).thenReturn(Optional.of(config));

        ConfigDto result = configService.updateProperty(configDto);

        assertNull(result);
        verify(config, never()).update(any());
    }

    @Test
    public void updateProperty_valorNull_actualitzaAmbNull() {
        configDto.setValue("null");
        when(configRepository.findById("es.caib.pinbal.test.key")).thenReturn(Optional.of(config));
        when(dtoMappingHelper.convertir(config, ConfigDto.class)).thenReturn(configDto);

        configService.updateProperty(configDto);

        verify(config).update(null);
    }

    @Test
    public void updateProperty_clauLogger_resetLogs() {
        when(config.getKey()).thenReturn(LoggerHelper.PREFIX + ".com.example");
        configDto.setKey(LoggerHelper.PREFIX + ".com.example");
        when(configRepository.findById(anyString())).thenReturn(Optional.of(config));
        when(dtoMappingHelper.convertir(config, ConfigDto.class)).thenReturn(configDto);

        try (MockedStatic<LoggerHelper> mocked = mockStatic(LoggerHelper.class)) {
            configService.updateProperty(configDto);
            mocked.verify(LoggerHelper::resetLogs);
        }
    }

    @Test
    public void findAll_retornaGrups() {
        ConfigGroupDto grup = new ConfigGroupDto();
        grup.setConfigs(Collections.emptyList());
        grup.setInnerConfigs(Collections.emptyList());
        when(configGroupRepository.findByParentCodeIsNull(any(Sort.class)))
                .thenReturn(Collections.emptyList());
        when(dtoMappingHelper.convertirList(anyList(), eq(ConfigGroupDto.class)))
                .thenReturn(Collections.singletonList(grup));

        List<ConfigGroupDto> result = configService.findAll();

        assertEquals(1, result.size());
    }

    @Test
    public void syncFromJBossProperties_actualitzaPropietatsExistents() {
        Properties props = new Properties();
        props.setProperty("es.caib.pinbal.test.key", "nouValor");
        when(configHelper.getEnvironmentProperties()).thenReturn(props);
        when(configRepository.findById("es.caib.pinbal.test.key")).thenReturn(Optional.of(config));

        List<String> result = configService.syncFromJBossProperties();

        assertNotNull(result);
        verify(config).update("nouValor");
        verify(configHelper).reloadDbProperties();
        verify(pluginHelper).resetPlugins();
    }

    @Test
    public void syncFromJBossProperties_clauNoExisteixABBDD_saltaClau() {
        Properties props = new Properties();
        props.setProperty("es.caib.pinbal.inexistent", "valor");
        when(configHelper.getEnvironmentProperties()).thenReturn(props);
        when(configRepository.findById("es.caib.pinbal.inexistent")).thenReturn(Optional.empty());

        List<String> result = configService.syncFromJBossProperties();

        assertTrue(result.isEmpty());
    }

    @Test
    public void propagateDbProperties_cridaReload() {
        configService.propagateDbProperties();
        verify(configHelper).reloadDbProperties();
    }

    @Test
    public void reiniciarTasques_cridaRestartSchedulledTasks() {
        configService.reiniciarTasques();
        verify(scheduleConfig).restartSchedulledTasks();
    }

    @Test
    public void getTempsErrorsMonitorIntegracio_retorna48() {
        assertEquals("48", configService.getTempsErrorsMonitorIntegracio());
    }

    @Test
    public void findAll_configSourceFile_llegeixDelFitxer() {
        ConfigDto configFileProp = new ConfigDto();
        configFileProp.setKey("es.caib.pinbal.file.prop");
        configFileProp.setValue("valorBBDD");
        configFileProp.setSourceProperty(ConfigSourceEnumDto.FILE);

        ConfigGroupDto grup = new ConfigGroupDto();
        grup.setConfigs(Collections.singletonList(configFileProp));
        grup.setInnerConfigs(Collections.emptyList());

        when(configGroupRepository.findByParentCodeIsNull(any(Sort.class))).thenReturn(Collections.emptyList());
        when(dtoMappingHelper.convertirList(anyList(), eq(ConfigGroupDto.class)))
                .thenReturn(Collections.singletonList(grup));
        when(configHelper.getConfig("es.caib.pinbal.file.prop", "valorBBDD")).thenReturn("valorFitxer");

        List<ConfigGroupDto> result = configService.findAll();

        assertEquals(1, result.size());
    }
}
