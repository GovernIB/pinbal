package es.caib.pinbal.core.model.explotacio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "pbl_explot_consulta_dim")
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class ExplotConsultaDimensioEntity implements Serializable {

	private static final long serialVersionUID = 2900135379128738307L;

	@Id
	@Column(name = "id")
	private Long id;

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
