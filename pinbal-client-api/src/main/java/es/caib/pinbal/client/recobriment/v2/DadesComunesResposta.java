package es.caib.pinbal.client.recobriment.v2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DadesComunesResposta {

    private String serveiCodi;
    private String idPeticio;
    private Emisor emisor;
    private Solicitant solicitant;
}
