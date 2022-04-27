package es.caib.pinbal.core.model;

import es.caib.pinbal.core.dto.ConfigSourceEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Classe del model de dades que representa una alerta d'error en segón pla.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Entity
@Table(	name = "PBL_CONFIG")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Config {
    @Id
    @Column(name = "key", length = 256, nullable = false)
    private String key;

    @Column(name = "value", length = 2048, nullable = true)
    private String value;

    @Column(name = "description_key", length = 2048, nullable = true)
    private String descriptionKey;

    @Column(name = "source_property", nullable = false)
    @Enumerated(EnumType.STRING)
    private ConfigSourceEnumDto sourceProperty;

    @Column(name = "group_code", length = 2048, nullable = true)
    private String groupCode;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "type_code", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "pbl_config_type_fk"))
    private ConfigType type;

    @Column(name = "position")
    private int position;

    @ManyToOne
    private Usuari lastModifiedBy;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    public Config(String key, String value) {
        this.key = key;
        this.value = value;
    }
    /**
     * Per a mapejar el Dto de la vista.
     *
     * @return El llistat de possibles valors que pot prendre la propietat
     */
    public List<String> getValidValues() {
       return type == null ? Collections.<String>emptyList() : type.getValidValues();
    }
    public String getTypeCode() {
        return type == null ? "" : type.getCode();
    }

    public void update(String value) {
        this.value = value;
    }
}
