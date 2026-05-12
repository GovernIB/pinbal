package es.caib.pinbal.plugin.dadescomunes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProvinciaML {
    private String codi;
    private String nom_ca;
    private String nom_es;
    private String nom;
}
