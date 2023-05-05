package es.caib.pinbal.core.model;

import es.caib.pinbal.core.audit.PinbalAuditable;
import es.caib.pinbal.core.dto.regles.AccioEnum;
import es.caib.pinbal.core.dto.regles.ModificatEnum;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="pbl_servei_regla",
        uniqueConstraints={@UniqueConstraint(columnNames={"servei_id", "nom"})},
        indexes = {@Index(name = "pbl_regla_servei_i", columnList = "servei_id")})
@EntityListeners(AuditingEntityListener.class)
public class ServeiRegla extends PinbalAuditable<Long> {

    @Column(name="nom", length = 255)
    private String nom;

    @NotNull
    @ManyToOne(optional=false)
    @JoinColumn(name="servei_id",
        foreignKey = @ForeignKey(name="pbl_regla_servei_fk"))
    private Servei servei;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name="modificat")
    private ModificatEnum modificat;

    @ElementCollection
    @CollectionTable(name="pbl_servei_regla_valor_mod", joinColumns=@JoinColumn(name="regla_id"))
    @Column(name = "valor")
    private Set<String> modificatValor;

    @ElementCollection
    @CollectionTable(name="pbl_servei_regla_valor_afc", joinColumns=@JoinColumn(name="regla_id"))
    @Column(name = "valor")
    private Set<String> afectatValor;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name="accio")
    private AccioEnum accio;

    @NotNull
    @Column(name="ordre")
    private int ordre;

}
