package es.caib.pinbal.core.dto.dadesexternes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Municipi {
    private String codi;
    private String nom;
}