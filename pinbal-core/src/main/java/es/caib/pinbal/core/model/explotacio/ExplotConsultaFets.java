package es.caib.pinbal.core.model.explotacio;

import lombok.Data;

@Data
public class ExplotConsultaFets {

    private final Long entitatId;
    private final Long procedimentId;
    private final String serveiCodi;
    private final String usuariCodi;
    private final Long recobrimentOk;
    private final Long recobrimentError;
    private final Long recobrimentPend;
    private final Long webOk;
    private final Long webError;
    private final Long webPend;

}
