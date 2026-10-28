package es.caib.pinbal.client.recobriment.v2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Grup de camps de dades específiques d'un servei. Els grups formen un arbre:
 * cada grup pot contenir subgrups, ordenats pel seu ordre.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrupDadesEspecifiques {

    /**
     * Identificador del grup: noms dels grups des de l'arrel separats per '/'.
     * Coincideix amb el camp 'grup' de les dades específiques.
     */
    private String codi;
    private String nom;
    private int ordre;
    private String ajuda;
    @Builder.Default
    private List<GrupDadesEspecifiques> subgrups = new ArrayList<>();

}
