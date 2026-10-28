package es.caib.pinbal.client.recobriment.v2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Regla del formulari de dades específiques d'un servei.
 * <p>
 * Quan es compleix la condició sobre els elements modificats (camps o grups
 * amb algun valor informat), s'aplica l'acció als elements afectats. Les
 * regles s'avaluen per ordre, i cada una pot sobreescriure l'efecte de les
 * anteriors. Els camps s'identifiquen pel seu codi (path) i els grups pel seu
 * codi (veure {@link GrupDadesEspecifiques#getCodi()}).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Regla {

    public enum ReglaModificat {
        /** Tots els camps modificats tenen valor. */
        CAMPS,
        /** Algun dels camps modificats té valor. */
        ALGUN_CAMP,
        /** Tots els grups modificats tenen algun camp amb valor. */
        GRUPS,
        /** Algun dels grups modificats té algun camp amb valor. */
        ALGUN_GRUP
    }
    public enum ReglaAccio {
        MOSTRAR,
        OCULTAR,
        EDITAR,
        BLOQUEJAR,
        OBLIGATORI,
        OPCIONAL
    }

    private String nom;
    private int ordre;
    private ReglaModificat modificat;
    private List<String> modificatValors;
    private ReglaAccio accio;
    private List<String> afectatValors;

}
