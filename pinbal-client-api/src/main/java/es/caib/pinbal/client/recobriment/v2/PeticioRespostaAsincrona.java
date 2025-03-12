package es.caib.pinbal.client.recobriment.v2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeticioRespostaAsincrona {

    private boolean error;
    private String messageError;
    private Map<String, List<String>> errorsValidacio;
//    private ScspConfirmacionPeticion resposta;

    private DadesComunesResposta dadesComunes;
    private List<PeticioResposta> respostes;

}
