package es.caib.pinbal.client.recobriment.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PeticioAsincrona {

    private DadesComunes dadesComunes;
    private List<SolicitudSimple> solicituds;

}
