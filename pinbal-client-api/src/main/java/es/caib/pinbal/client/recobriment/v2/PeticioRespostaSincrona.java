package es.caib.pinbal.client.recobriment.v2;

import es.caib.pinbal.client.recobriment.model.ScspRespuesta;
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
public class PeticioRespostaSincrona {

    private boolean error;
    private String messageError;
    private Map<String, List<String>> errorsValidacio;
    private ScspRespuesta resposta;

}
