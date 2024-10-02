/**
 * 
 */
package es.caib.pinbal.core.model;

import es.caib.pinbal.core.dto.ConsultaDto.Consentiment;
import es.caib.pinbal.core.dto.EstatTipus;
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
		name = "pbl_consulta",
		indexes = {
				@Index(name = "pbl_consulta_procserv_i", columnList = "procserv_id"),
				@Index(name = "pbl_consulta_createdby_i", columnList = "createdby_codi")})
@AssociationOverrides({
		@AssociationOverride(name = "procedimentServei", foreignKey = @ForeignKey(name = "pbl_procserv_consulta_fk")),
		@AssociationOverride(name = "procediment", foreignKey = @ForeignKey(name = "pbl_consultah_proced_fk")),
		@AssociationOverride(name = "entitat", foreignKey = @ForeignKey(name = "pbl_consultah_entitat_fk"))
})
@EntityListeners(AuditingEntityListener.class)
public class Consulta extends SuperConsulta {

	private static final long serialVersionUID = -6657066865382086237L;

	@ManyToOne(optional=true, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "pare_id",
			foreignKey = @ForeignKey(name = "pbl_consulta_pare_fk"))
	private Consulta pare;

	@OneToMany(mappedBy = "pare", cascade = {CascadeType.ALL})
	@OrderBy("scspSolicitudId asc")
	private List<Consulta> fills = new ArrayList<Consulta>();


	/**
	 * Obté el Builder per a crear objectes de tipus Consulta.
	 * 
	 * @param scspPeticionId
	 *            Identificador de la petición SCSP.
	 * @param funcionariNom
	 *            El nom del funcionari de la consulta.
	 * @param funcionariDocumentNum
	 *            El número de document del funcionari de la consulta.
	 * @param titularDocumentTipus
	 *            El tipus de document del titular de la consulta.
	 * @param titularDocumentNum
	 *            El número de document del titular de la consulta.
	 * @param titularNom
	 *            El nom del titular de la consulta.
	 * @param titularLlinatge1
	 *            El primer llinatge del titular de la consulta.
	 * @param titularLlinatge2
	 *            El segon llinatge del titular de la consulta.
	 * @param titularNomComplet
	 *            El nom complet del titular de la consulta.
	 * @param departamentNom
	 *            El nom del departament.
	 * @param procedimentServei
	 *            El procediment-servei de la consulta.
	 * @param recobriment
	 *            Indica si és una consulta provinent del recobriment.
	 * @param multiple
	 *            Indica si és una consulta múltiple.
	 * @param pare
	 *            La consulta múltiple pare d'aquesta consulta.
	 * @return Una nova instància del Builder.
	 */
	public static Builder getBuilder(
			String scspPeticionId,
			String funcionariNom,
			String funcionariDocumentNum,
			String titularDocumentTipus,
			String titularDocumentNum,
			String titularNom,
			String titularLlinatge1,
			String titularLlinatge2,
			String titularNomComplet,
			String departamentNom,
			ProcedimentServei procedimentServei,
			String finalitat,
			Consentiment consentiment,
			String expedientId,
			boolean recobriment,
			boolean multiple,
			Consulta pare) {
		return new Builder(
				scspPeticionId,
				funcionariNom,
				funcionariDocumentNum,
				titularDocumentTipus,
				titularDocumentNum,
				titularNom,
				titularLlinatge1,
				titularLlinatge2,
				titularNomComplet,
				departamentNom,
				procedimentServei,
				finalitat,
				consentiment,
				expedientId,
				recobriment,
				multiple,
				pare);
	}


	public void updateEstat(EstatTipus estat) {
		this.estat = estat;
	}
	public void updateEstatError(String error) {
		this.estat = EstatTipus.Error;
		if (error != null && error.length() > ERROR_SCSP_MAX_LENGTH) {
			String tokenFinal = " [...]";
			this.error = error.substring(0, (ERROR_SCSP_MAX_LENGTH - tokenFinal.length())) + tokenFinal;
		} else {
			this.error = error;
		}
	}

	public void updateScspSolicitudId(String scspSolicitudId) {
		this.scspSolicitudId = scspSolicitudId;
	}
	public void updateArxiuExpedientTancat(boolean arxiuExpedientTancat) {
		this.arxiuExpedientTancat = arxiuExpedientTancat;
	}
	public void updateDadesEspecifiques(String dadesEspecifiques) {
		this.dadesEspecifiques = dadesEspecifiques;
	}

	/**
	 * La classe Builder emprada per a crear nous objectes de tipus Entitat.
	 */
	public static class Builder {
		Consulta built;

		/**
		 * Crea una nova instància del Builder.
		 * 
		 * @param scspPeticionId
		 *            Identificador de la petición SCSP.
		 * @param funcionariNom
		 *            El nom del funcionari de la consulta.
		 * @param funcionariDocumentNum
		 *            El número de document del funcionari de la consulta.
		 * @param titularDocumentTipus
		 *            El tipus de document del titular de la consulta.
		 * @param titularDocumentNum
		 *            El número de document del titular de la consulta.
		 * @param titularNom
		 *            El nom del titular de la consulta.
		 * @param titularLlinatge1
		 *            El primer llinatge del titular de la consulta.
		 * @param titularLlinatge2
		 *            El segon llinatge del titular de la consulta.
		 * @param titularNomComplet
		 *            El nom complet del titular de la consulta.
		 * @param departamentNom
		 *            El nom del departament.
		 * @param procedimentServei
		 *            El procediment-servei de la consulta.
		 * @param recobriment
		 *            Indica si és una consulta provinent del recobriment.
		 * @param multiple
		 *            Indica si és una consulta múltiple.
		 * @param pare
		 *            La consulta múltiple pare d'aquesta consulta.
		 */
		Builder(
				String scspPeticionId,
				String funcionariNom,
				String funcionariDocumentNum,
				String titularDocumentTipus,
				String titularDocumentNum,
				String titularNom,
				String titularLlinatge1,
				String titularLlinatge2,
				String titularNomComplet,
				String departamentNom,
				ProcedimentServei procedimentServei,
				String finalitat,
				Consentiment consentiment,
				String expedientId,
				boolean recobriment,
				boolean multiple,
				Consulta pare) {
			built = new Consulta();
			built.scspPeticionId = scspPeticionId;
			built.scspSolicitudId = scspPeticionId;
			built.funcionariNom = funcionariNom;
			built.funcionariDocumentNum = funcionariDocumentNum;
			built.titularDocumentTipus = titularDocumentTipus;
			built.titularDocumentNum = titularDocumentNum;
			built.departamentNom = departamentNom;
			built.titularNom = titularNom;
			built.titularLlinatge1 = titularLlinatge1;
			built.titularLlinatge2 = titularLlinatge2;
			built.titularNomComplet = titularNomComplet;
			built.procedimentServei = procedimentServei;
			built.procediment = procedimentServei.getProcediment();
			built.entitat = procedimentServei.getProcediment().getEntitat();
			built.serveiCodi = procedimentServei.getServei();
			built.finalitat = finalitat;
			built.consentiment = consentiment;
			built.expedientId = expedientId;
			built.recobriment = recobriment;
			built.multiple = multiple;
			built.pare = pare;
			built.estat = EstatTipus.Pendent;
		}

		/**
		 * Construeix el nou objecte de tipus Entitat.
		 * 
		 * @return L'objecte de tipus Entitat creat.
		 */
		public Consulta build() {
			return built;
		}

	}

	public void configurarIdPerTest(Long id) {
		this.setId(id);
	}

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
		Consulta other = (Consulta) obj;
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
