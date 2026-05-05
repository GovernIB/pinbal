package es.caib.pinbal.logic.intf.dto;

import lombok.Data;

import java.util.List;

@Data
public class ConfigGroupDto {
    private String key;
    private String descriptionKey;
    private List<ConfigDto> configs;
    private List<ConfigGroupDto> innerConfigs;
}
