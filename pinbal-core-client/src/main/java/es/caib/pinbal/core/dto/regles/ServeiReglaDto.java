package es.caib.pinbal.core.dto.regles;

import es.caib.pinbal.core.dto.CodiValor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServeiReglaDto {
    private Long id;
    private String nom;
    private Long serveiId;
    private ModificatEnum modificat;
    private LinkedHashSet<String> modificatValor;
    private LinkedHashSet<String> afectatValor;
    private AccioEnum accio;
    private int ordre;

    public List<CodiValor> getModificatCodiValor() {
        return getValorSetCodiValor(modificatValor);
    }

    public List<CodiValor> getAfectatCodiValor() {
        return getValorSetCodiValor(afectatValor);
    }

    private List<CodiValor> getValorSetCodiValor(LinkedHashSet<String> valors) {
        List<CodiValor> codisValor = new ArrayList<>();
        boolean isCamp = isValorCamp();
        for (String valor: valors) {
            if (isCamp) {
                String[] splValor = valor.split(" \\| ");
                codisValor.add(CodiValor.builder().codi(splValor[0]).valor(splValor[1]).build());
            } else {
                codisValor.add(CodiValor.builder().codi(valor).valor(valor).build());
            }
        }
        return codisValor;
    }

    private boolean isValorCamp() {
        return ModificatEnum.CAMPS.equals(modificat) || ModificatEnum.ALGUN_CAMP.equals(modificat);
    }
}
