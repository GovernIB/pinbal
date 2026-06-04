package es.caib.pinbal.back.command;

import es.caib.pinbal.logic.intf.dto.ConfigDto;
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
