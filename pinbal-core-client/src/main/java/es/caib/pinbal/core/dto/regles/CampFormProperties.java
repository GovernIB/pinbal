package es.caib.pinbal.core.dto.regles;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CampFormProperties {
    private boolean visible;
    private boolean editable;
    private boolean obligatori;
}
