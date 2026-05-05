package es.caib.pinbal.client.recobriment.v2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PeticioRespostaAsincrona extends Resposta {

    private Date dataEstimadaResposta;
    private DadesComunesResposta dadesComunes;
    private List<PeticioResposta> respostes;

}
