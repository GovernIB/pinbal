package es.caib.pinbal.core.dto.dadesexternes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pais {
    private String codi_numeric;
    private String alpha2;
    private String alpha3;
    private String nom;
}
