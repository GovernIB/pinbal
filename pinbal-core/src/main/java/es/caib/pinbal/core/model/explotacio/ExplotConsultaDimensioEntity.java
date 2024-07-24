package es.caib.pinbal.core.model.explotacio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;

@Entity
@Table(name = "pbl_explot_consulta_dim",
		uniqueConstraints = {@UniqueConstraint(name = "pbl_explot_consulta_dim_uk", columnNames = {"entitat_id", "procediment_id", "servei_codi", "usuari_codi"})}
//		indexes = {@Index(name = "pbl_explot_consulta_dim_i", columnList = "entitat_id, procediment_id, servei_codi, usuari_codi")}
)
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode
public class ExplotConsultaDimensioEntity extends AbstractPersistable<Long> implements Serializable {

	private static final long serialVersionUID = 2900135379128738307L;

	@Column(name = "entitat_id")
	private Long entitatId;

	@Column(name = "procediment_id")
	private Long procedimentId;

	@Column(name = "servei_codi")
	private String serveiCodi;

	@Column(name = "usuari_codi")
	private String usuariCodi;

//	@Column(name = "tipus")
//	@Enumerated(EnumType.STRING)
//	private DadesObertesConsultaTipus tipus;
//
//	@Column(name = "estat")
//	@Enumerated(EnumType.STRING)
//	private EstatTipus estat;

}
