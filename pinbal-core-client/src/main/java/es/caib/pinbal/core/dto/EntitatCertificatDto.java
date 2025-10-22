package es.caib.pinbal.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * Classe de model de dades que conté la informació de certificats per entitat.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EntitatCertificatDto extends AbstractIdentificable<Long> implements Serializable {

    private EntitatDto entitat;
    private String alias;  // Alias dins el keystore
    private String numeroSerie;
    private String cn;  // Common Name
    private Date dataCaducitat;
    private Boolean actiu;
    private Date dataAlta;
    private Date dataBaixa;

}
