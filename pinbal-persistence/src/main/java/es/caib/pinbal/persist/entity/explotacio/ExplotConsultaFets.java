package es.caib.pinbal.persist.entity.explotacio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
public class ExplotConsultaFets {

    private final Long entitatId;
    private final String entitatCodi;
    private final Long procedimentId;
    private final String procedimentCodi;
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
        if (fetAnterior == null) {
            throw new RuntimeException("Error al obtenir les dades estadístiques. Fet anterior no informat");
        }
        if (!Objects.equals(this.entitatId, fetAnterior.entitatId)) {
            throw new RuntimeException("Error al obtenir les dades estadístiques. Entitat incorrecta");
        }
        if (!Objects.equals(this.procedimentId, fetAnterior.procedimentId)) {
            throw new RuntimeException("Error al obtenir les dades estadístiques. Procediment incorrecta");
        }
        if (!Objects.equals(this.serveiCodi, fetAnterior.serveiCodi)) {
            throw new RuntimeException("Error al obtenir les dades estadístiques. Servei incorrecta");
        }
        if (!Objects.equals(this.usuariCodi, fetAnterior.usuariCodi)) {
            throw new RuntimeException("Error al obtenir les dades estadístiques. Usuari incorrecta");
        }
        return ExplotConsultaFets.builder()
                .entitatId(this.entitatId)
                .entitatCodi(this.entitatCodi)
                .procedimentId(this.procedimentId)
                .procedimentCodi(this.procedimentCodi)
                .serveiCodi(this.serveiCodi)
                .recOk(this.recOk - fetAnterior.getRecOk())
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

    /**
     * Suma els fets d'un altre registre que comparteix entitat, procediment i servei però que pot
     * correspondre a un usuari diferent (p.e. quan s'agreguen els fets de tots els usuaris d'un
     * mateix procediment/servei). Si l'usuari no coincideix, el resultat queda sense usuari
     * (usuariCodi=null) ja que deixa de representar un únic usuari.
     */
    public ExplotConsultaFets plus(ExplotConsultaFets altre) {
        if (altre == null) {
            return this;
        }
        if (!Objects.equals(this.entitatId, altre.entitatId)) {
            throw new RuntimeException("Error al obtenir les dades estadístiques. Entitat incorrecta");
        }
        if (!Objects.equals(this.procedimentId, altre.procedimentId)) {
            throw new RuntimeException("Error al obtenir les dades estadístiques. Procediment incorrecta");
        }
        if (!Objects.equals(this.serveiCodi, altre.serveiCodi)) {
            throw new RuntimeException("Error al obtenir les dades estadístiques. Servei incorrecta");
        }
        return ExplotConsultaFets.builder()
                .entitatId(this.entitatId)
                .entitatCodi(this.entitatCodi)
                .procedimentId(this.procedimentId)
                .procedimentCodi(this.procedimentCodi)
                .serveiCodi(this.serveiCodi)
                .usuariCodi(Objects.equals(this.usuariCodi, altre.usuariCodi) ? this.usuariCodi : null)
                .recOk(this.recOk + altre.getRecOk())
                .recError(this.recError + altre.getRecError())
                .recPend(this.recPend + altre.getRecPend())
                .recProc(this.recProc + altre.getRecProc())
                .recMassOk(this.recMassOk + altre.getRecMassOk())
                .recMassError(this.recMassError + altre.getRecMassError())
                .recMassPend(this.recMassPend + altre.getRecMassPend())
                .recMassProc(this.recMassProc + altre.getRecMassProc())
                .webOk(this.webOk + altre.getWebOk())
                .webError(this.webError + altre.getWebError())
                .webPend(this.webPend + altre.getWebPend())
                .webProc(this.webProc + altre.getWebProc())
                .webMassOk(this.webMassOk + altre.getWebMassOk())
                .webMassError(this.webMassError + altre.getWebMassError())
                .webMassPend(this.webMassPend + altre.getWebMassPend())
                .webMassProc(this.webMassProc + altre.getWebMassProc())
                .build();
    }
}
