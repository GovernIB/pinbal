package es.caib.pinbal.persist.entity.explotacio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor
public class EstadisticaKey {
    Long entitatId;
    Long procedimentId;
    String serveiCodi;
}
