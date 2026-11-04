package es.caib.pinbal.client.recobriment.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SolicitudSimple {

//    private String id;
    private Titular titular;
    private String expedient;
    /**
     * Valors de les dades específiques, indexats pel codi (path) del camp.
     * Els camps de tipus FILE (fitxers binaris o XML) s'envien codificats en base64.
     */
    private Map<String, String> dadesEspecifiques;

}
