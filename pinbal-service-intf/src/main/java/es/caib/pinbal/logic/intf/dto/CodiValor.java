package es.caib.pinbal.logic.intf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CodiValor {

    private String codi;
    private String valor;

    public String toString() {
        return "CodiValor(codi=" + this.getCodi() + ", valor=" + this.getValor() + ")";
    }

}
