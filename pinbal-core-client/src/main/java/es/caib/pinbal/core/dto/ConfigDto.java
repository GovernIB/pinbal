package es.caib.pinbal.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigDto {
    private String key;
    private String value;
    private String descriptionKey;
    private ConfigSourceEnumDto sourceProperty;

    private String typeCode;
    private List<String> validValues;
}
