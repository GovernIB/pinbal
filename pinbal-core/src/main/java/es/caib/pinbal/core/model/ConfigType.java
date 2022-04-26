package es.caib.pinbal.core.model;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Classe del model de dades que representa un del tipus de dades possibles per a una propietat de configuració.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Entity
@Table(	name = "PBL_CONFIG_TYPE")
public class ConfigType {
    @Id
    @Column(name = "code", length = 128, nullable = false)
    private String code;

    @Column(name = "value", length = 2048, nullable = false)
    private String value;

    public List<String> getValidValues() {
        if (value == null || value.isEmpty()) {
            return Collections.emptyList();
        }

        String[] values = value.split(",");
        return Arrays.asList(values);

    }
}
