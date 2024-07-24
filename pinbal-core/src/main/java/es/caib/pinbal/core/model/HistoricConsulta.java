/**
 * 
 */
package es.caib.pinbal.core.model;

import lombok.Getter;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de model de dades que conté la informació d'una consulta.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Entity
@Table(
		name = "pbl_consulta_hist",
		indexes = {
				@Index(name = "pbl_consultah_procserv_i", columnList = "procserv_id"),
				@Index(name = "pbl_consultah_createdby_i", columnList = "createdby_codi")})
@AssociationOverrides({
		@AssociationOverride(name = "procedimentServei", foreignKey = @ForeignKey(name = "pbl_consultah_procserv_fk")),
		@AssociationOverride(name = "procediment", foreignKey = @ForeignKey(name = "pbl_consultah_proced_fk")),
		@AssociationOverride(name = "entitat", foreignKey = @ForeignKey(name = "pbl_consultah_entitat_fk"))
})
@EntityListeners(AuditingEntityListener.class)
public class HistoricConsulta extends SuperConsulta {

	private static final long serialVersionUID = -6657066865382086237L;

	@ManyToOne(optional=true, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "pare_id",
			foreignKey = @ForeignKey(name = "pbl_consultah_pare_fk"))
	private HistoricConsulta pare;

	@OneToMany(mappedBy = "pare", cascade = {CascadeType.ALL})
	@OrderBy("scspSolicitudId asc")
	private List<HistoricConsulta> fills = new ArrayList<>();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((scspPeticionId == null) ? 0 : scspPeticionId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		HistoricConsulta other = (HistoricConsulta) obj;
		if (scspPeticionId == null) {
			if (other.scspPeticionId != null)
				return false;
		} else if (!scspPeticionId.equals(other.scspPeticionId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
