package es.caib.pinbal.core.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="pbl_servei_regla_valor_mod")
public class ServeiReglaCampModificat implements Serializable {

    @Id
    @Column(name="regla_id")
    private Long reglaId;

    @Id
    @Column(name="valor", length = 255)
    private String valor;

}
