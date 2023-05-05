package es.caib.pinbal.core.dto.regles;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServeiReglaValorDto {
    private Long id;
    private Long reglaId;
    private ReglaTipusEnum tipus;
    private String valor;
}
