package es.caib.pinbal.client.serveis;

import com.fasterxml.jackson.annotation.JsonView;
import es.caib.pinbal.client.comu.Vistes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Servei {

    private String codi;
    private String descripcio;
    private Boolean actiu = false;

    @JsonView(Vistes.VistaDetallada.class)
    private String emisor;

    public Servei(String codi, String descripcio, Boolean actiu) {
        this.codi = codi;
        this.descripcio = descripcio;
        this.actiu = actiu;
    }
}
