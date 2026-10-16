package es.caib.pinbal.persist.entity;

import es.caib.pinbal.logic.intf.dto.regles.AccioEnum;
import es.caib.pinbal.logic.intf.dto.regles.ModificatEnum;
import es.caib.pinbal.persist.audit.PinbalAuditable;
import es.caib.pinbal.persist.base.entity.EntityToStringHelper;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper=false)
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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", getId())
                .append("nom", nom)
                .append("servei.id", EntityToStringHelper.identificador(servei, Servei::getId))
                .append("modificat", modificat)
                .append("modificatValor.size", EntityToStringHelper.mida(modificatValor))
                .append("afectatValor.size", EntityToStringHelper.mida(afectatValor))
                .append("accio", accio)
                .append("ordre", ordre)
                .toString();
    }

}
