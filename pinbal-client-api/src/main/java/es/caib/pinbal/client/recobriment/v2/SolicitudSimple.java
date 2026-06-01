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
    private Map<String, String> dadesEspecifiques;

}
