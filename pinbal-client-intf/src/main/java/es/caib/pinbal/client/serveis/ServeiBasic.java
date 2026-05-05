package es.caib.pinbal.client.serveis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServeiBasic {

    private String codi;
    private String descripcio;
    private Boolean actiu = false;

}
