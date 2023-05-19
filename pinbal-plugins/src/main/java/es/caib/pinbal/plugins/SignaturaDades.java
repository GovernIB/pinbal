package es.caib.pinbal.plugins;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SignaturaDades {
    private String nom;
    private String motiu;
    private FirmaServidorPlugin.TipusFirma tipusFirma;
    private byte[] contingut;
    private String contentType;
    private String tipusDocumental;
    private String idioma;
}
