package es.caib.pinbal.plugin.dadescomunes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaisML {
    private String codi_numeric;
    private String alpha2;
    private String alpha3;
    private String nom_ca;
    private String nom_es;
    private String nom;
}
