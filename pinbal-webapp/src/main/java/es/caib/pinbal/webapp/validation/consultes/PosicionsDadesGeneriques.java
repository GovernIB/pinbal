package es.caib.pinbal.webapp.validation.consultes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PosicionsDadesGeneriques {
    private int posicioExpedient = -1;
    private int posicioTitularDocumentTipus = -1;
    private int posiciotitularDocumentNumero = -1;
    private int posicioTitularNom = -1;
    private int posicioTitularLlinatge1 = -1;
    private int posicioTitularLlinatge2 = -1;
    private int posicioTitularNomComplet = -1;
}
