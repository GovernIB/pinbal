package es.caib.pinbal.webapp.command;

import es.caib.pinbal.core.dto.ConfigDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigCommand {
    private String key;
    private String value;

    public boolean isBooleanValue() {
        return value!=null && value.equals("true");
    }

    public void setBooleanValue(boolean booleanValue) {
        this.value = booleanValue ? "true" : "false";
    }

    public ConfigDto asDto() {
        return ConfigDto.builder()
                .key(this.key)
                .value(this.value)
                .build();
    }
}
