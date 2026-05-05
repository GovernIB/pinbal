package es.caib.pinbal.client.recobriment.v2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmacioPeticio {

    private String serveiCodi;
    private String idPeticio;
    private int numSolicituds;
    private Date dataEnviamentPeticio;
    private Date dataEstimadaResposta;
}
