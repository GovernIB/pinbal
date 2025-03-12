package es.caib.pinbal.client.recobriment.v2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dades del funcionari que realitza una consulta.
 * És obligatori informar o bé el codi o el nif.
 * No fa falta informar els dos, però si s'informen els dos es validarà que el codi i el nif corresponguin al mateix usuari.
 * Tampoc fa falta informar el nom del funcionari. Però si s'informa es validarà que el nom corresponguin exactament al de l'usuari.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Funcionari {

    private String nom;
    private String nif;
}
