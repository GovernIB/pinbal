package es.caib.pinbal.core.model.explotacio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ExplotConsultaFets {

    private final Long entitatId;
    private final Long procedimentId;
    private final String serveiCodi;
    private final String usuariCodi;
    private final long recOk;
    private final long recError;
    private final long recPend;
    private final long recProc;
    private final long recMassOk;
    private final long recMassError;
    private final long recMassPend;
    private final long recMassProc;
    private final long webOk;
    private final long webError;
    private final long webPend;
    private final long webProc;
    private final long webMassOk;
    private final long webMassError;
    private final long webMassPend;
    private final long webMassProc;

    public ExplotConsultaFets minus(ExplotConsultaFets fetAnterior) {
        if (!this.entitatId.equals(fetAnterior.entitatId)) {
            throw new RuntimeException("Error al obtenir les dades estadístiques. Entitat incorrecta");
        }
        if (!this.procedimentId.equals(fetAnterior.procedimentId)) {
            throw new RuntimeException("Error al obtenir les dades estadístiques. Procediment incorrecta");
        }
        if (!this.serveiCodi.equals(fetAnterior.serveiCodi)) {
            throw new RuntimeException("Error al obtenir les dades estadístiques. Servei incorrecta");
        }
        return ExplotConsultaFets.builder()
                .entitatId(this.entitatId)
                .procedimentId(this.procedimentId)
                .serveiCodi(this.serveiCodi)
                .recOk(this.recOk - fetAnterior.getRecError())
                .recError(this.recError - fetAnterior.getRecError())
                .recPend(this.recPend - fetAnterior.getRecPend())
                .recProc(this.recProc - fetAnterior.getRecProc())
                .recMassOk(this.recMassOk - fetAnterior.getRecMassOk())
                .recMassError(this.recMassError - fetAnterior.getRecMassError())
                .recMassPend(this.recMassPend - fetAnterior.getRecMassPend())
                .recMassProc(this.recMassProc - fetAnterior.getRecMassProc())
                .webOk(this.webOk - fetAnterior.getWebOk())
                .webError(this.webError - fetAnterior.getWebError())
                .webPend(this.webPend - fetAnterior.getWebPend())
                .webProc(this.webProc - fetAnterior.getWebProc())
                .webMassOk(this.webMassOk - fetAnterior.getWebMassOk())
                .webMassError(this.webMassError - fetAnterior.getWebMassError())
                .webMassPend(this.webMassPend - fetAnterior.getWebMassPend())
                .webMassProc(this.webMassProc - fetAnterior.getWebMassProc())
                .build();
    }
}
