package es.caib.pinbal.plugins;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignaturaResposta {
    private byte[] contingut;
    private String nom;
    private String mime;
    private String tipusFirma;
    private String tipusFirmaEni;
    private String perfilFirmaEni;
}
