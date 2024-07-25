package es.caib.pinbal.core.model.explotacio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "pbl_explot_consulta_fet")
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class ExplotConsultaFetsEntity extends AbstractPersistable<Long> implements Serializable {

	private static final long serialVersionUID = 2900135379128738307L;

	@Column(name = "num_rec_ok")
	private long numRecobrimentOk;

	@Column(name = "num_rec_error")
	private long numRecobrimentError;

	@Column(name = "num_rec_pend")
	private long numRecobrimentPendent;

	@Column(name = "num_rec_proc")
	private long numRecobrimentProcessant;

	@Column(name = "num_rec_mass_ok")
	private long numRecobrimentMassiuOk;

	@Column(name = "num_rec_mass_error")
	private long numRecobrimentMassiuError;

	@Column(name = "num_rec_mass_pend")
	private long numRecobrimentMassiuPendent;

	@Column(name = "num_rec_mass_proc")
	private long numRecobrimentMassiuProcessant;

	@Column(name = "num_web_ok")
	private long numWebOk;

	@Column(name = "num_web_error")
	private long numWebError;

	@Column(name = "num_web_pend")
	private long numWebPendent;

	@Column(name = "num_web_proc")
	private long numWebProcessant;

	@Column(name = "num_web_mass_ok")
	private long numWebMassiuOk;

	@Column(name = "num_web_mass_error")
	private long numWebMassiuError;

	@Column(name = "num_web_mass_pend")
	private long numWebMassiuPendent;

	@Column(name = "num_web_mass_proc")
	private long numWebMassiuProcessant;

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
		return numWebOk + numWebError + numWebPendent;
	}

	private long getNumTotal() {
		return getNumRecobrimentTotal() + getNumWebUITotal();
	}

}
