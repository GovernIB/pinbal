package es.caib.pinbal.back.validation.consultes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PosicionsDadesGeneriques {
    @Builder.Default private int posicioTipusDocument = -1;
    @Builder.Default private int posicioExpedient = -1;
    @Builder.Default private int posicioTitularDocumentTipus = -1;
    @Builder.Default private int posiciotitularDocumentNumero = -1;
    @Builder.Default private int posicioTitularNom = -1;
    @Builder.Default private int posicioTitularLlinatge1 = -1;
    @Builder.Default private int posicioTitularLlinatge2 = -1;
    @Builder.Default private int posicioTitularNomComplet = -1;
}
