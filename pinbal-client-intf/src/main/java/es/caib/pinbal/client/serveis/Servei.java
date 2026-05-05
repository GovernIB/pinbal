package es.caib.pinbal.client.serveis;

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

    private String emisor;

    public Servei(String codi, String descripcio, Boolean actiu) {
        this.codi = codi;
        this.descripcio = descripcio;
        this.actiu = actiu;
    }
}
