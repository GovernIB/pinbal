package es.caib.pinbal.core.model;

import es.caib.pinbal.core.audit.PinbalAuditable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import java.util.Date;

/**
 * Classe de model de dades que conté la informació de certificats per entitat.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "pbl_entitat_certificat",
        uniqueConstraints = {@UniqueConstraint(columnNames={"entitat_id", "alias"})}
)
@EntityListeners(AuditingEntityListener.class)
public class EntitatCertificat extends PinbalAuditable<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entitat_id")
    private Entitat entitat;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "clau_privada_id")
    private ClauPrivada clau;

    @Column(name = "actiu")
    private Boolean actiu;

    @Column(name = "data_alta")
    private Date dataAlta;

    @Column(name = "data_baixa")
    private Date dataBaixa;

    @Version
    private long version = 0;

    @PrePersist
    protected void onCreate() {
        if (dataAlta == null) {
            dataAlta = new Date();
        }
    }

}
