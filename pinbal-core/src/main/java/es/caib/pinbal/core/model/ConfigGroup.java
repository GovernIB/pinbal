package es.caib.pinbal.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.util.Set;

@Getter
@Entity
@Table(	name = "PBL_CONFIG_GROUP")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigGroup {

    @Id
    @Column(name = "code", length = 128, nullable = false)
    private String key;

    @Column(name = "description_key", length = 512, nullable = true)
    private String descriptionKey;

    @Column(name = "position")
    private int position;

    @Column(name = "parent_code")
    private String parentCode;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_code")
    @OrderBy("position ASC")
    private Set<Config> configs;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_code")
    @OrderBy("position ASC")
    private Set<ConfigGroup> innerConfigs;
}
