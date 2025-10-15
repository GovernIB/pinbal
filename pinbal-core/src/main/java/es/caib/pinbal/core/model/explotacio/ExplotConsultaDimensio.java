package es.caib.pinbal.core.model.explotacio;

import lombok.Data;

@Data
public class ExplotConsultaDimensio {

    private final Long entitatId;
    private final String entitatCodi;
    private final Long procedimentId;
    private final String procedimentCodi;
    private final String serveiCodi;
    private final String usuariCodi;

}
