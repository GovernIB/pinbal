package es.caib.pinbal.client.recobriment.v2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Validacio {

    public enum ValidacioTipus {
        EXPRESSIO_REGULAR,
        NUM_MINIM,
        NUM_MAXIM,
        NUM_RANG,
        DATA_COMPARACIO,
        DATA_DIFERENCIA
    }
    public enum ValidacioOperacio {
        LT,
        LTE,
        GT,
        GTE,
        EQ,
        NEQ
    }

    private ValidacioTipus tipus;
    private ValidacioOperacio operacio;

    private String regexp;
    private Integer min;
    private Integer max;
    private String campComparacio;
    private String diferencia;
}
