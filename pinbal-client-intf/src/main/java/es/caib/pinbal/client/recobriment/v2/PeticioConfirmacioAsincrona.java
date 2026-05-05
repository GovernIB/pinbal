package es.caib.pinbal.client.recobriment.v2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PeticioConfirmacioAsincrona extends Resposta {

    private ConfirmacioPeticio confirmacioPeticio;

}
