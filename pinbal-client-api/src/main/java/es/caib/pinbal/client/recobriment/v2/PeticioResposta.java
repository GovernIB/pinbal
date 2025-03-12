package es.caib.pinbal.client.recobriment.v2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeticioResposta {

    private String idSolicitud;
    private String idTransmissio;
    private Date dataResposta;

    private Titular titular;
    private String expedient;
    private Map<String, String> dadesEspecifiques;

}
