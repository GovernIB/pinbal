package es.caib.pinbal.core.model.explotacio;

import es.caib.pinbal.core.dto.ConsultaDto.EstatTipus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

import static es.caib.pinbal.client.dadesobertes.DadesObertesRespostaConsulta.DadesObertesConsultaTipus;

@Entity
@Table(name = "pbl_explot_consulta_fet")
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class ExplotConsultaFetsEntity implements Serializable {

	private static final long serialVersionUID = 2900135379128738307L;

	@Id
	@Column(name = "id")
	private Long id;

	@Column(name = "num_rec_ok")
	private long numRecobrimentOk;

	@Column(name = "num_rec_error")
	private long numRecobrimentError;

	@Column(name = "num_rec_pend")
	private long numRecobrimentPendent;

	@Column(name = "num_web_ok")
	private long numWebUIOk;

	@Column(name = "num_web_error")
	private long numWebUIError;

	@Column(name = "num_web_pend")
	private long numWebPendent;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name="dimensio_id")
	protected ExplotConsultaDimensioEntity consultaDimensio;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name="temps_id")
	protected ExplotTempsEntity temps;

	private long getNumRecobrimentTotal() {
		return numRecobrimentOk + numRecobrimentError + numRecobrimentPendent;
	}

	private long getNumWebUITotal() {
		return numWebUIOk + numWebUIError + numWebPendent;
	}

	private long getNumTotal() {
		return getNumRecobrimentTotal() + getNumWebUITotal();
	}

}
