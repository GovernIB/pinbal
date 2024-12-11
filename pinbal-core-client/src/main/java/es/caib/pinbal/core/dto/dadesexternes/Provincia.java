package es.caib.pinbal.core.dto.dadesexternes;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Provincia {
    private String codi;
    @JsonProperty("nom_ca")
    private String nom;
}
