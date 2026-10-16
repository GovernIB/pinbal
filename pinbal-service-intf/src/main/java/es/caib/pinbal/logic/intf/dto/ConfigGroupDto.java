package es.caib.pinbal.logic.intf.dto;

import es.caib.pinbal.logic.intf.helper.ToStringHelper;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
public class ConfigGroupDto {
    private String key;
    private String descriptionKey;
    @ToString.Exclude
    private List<ConfigDto> configs;
    @ToString.Exclude
    private List<ConfigGroupDto> innerConfigs;

    @ToString.Include(name = "configs.size")
    private Integer configsMida() {
        return ToStringHelper.mida(configs);
    }

    @ToString.Include(name = "innerConfigs.size")
    private Integer innerConfigsMida() {
        return ToStringHelper.mida(innerConfigs);
    }

}
