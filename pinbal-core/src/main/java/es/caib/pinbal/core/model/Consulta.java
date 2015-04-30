/**
 * 
 */
package es.caib.pinbal.core.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import es.caib.pinbal.core.audit.PinbalAuditable;
import es.caib.pinbal.core.audit.PinbalAuditingEntityListener;

/**
 * Classe de model de dades que conté la informació d'una consulta.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "pbl_consulta")
@org.hibernate.annotations.Table(
		appliesTo = "pbl_consulta",
		indexes = {
				@Index(name = "pbl_consulta_procserv_i", columnNames = {"procserv_id"}),
				@Index(name = "pbl_consulta_createdby_i", columnNames = {"createdby_codi"})})
@EntityListeners(PinbalAuditingEntityListener.class)
public class Consulta extends PinbalAuditable<Long> {

	public static final int ERROR_MAX_LENGTH = 4000;

	public enum EstatTipus {
		Pendent,
		Processant,
		Tramitada,
		Error
	}

	private static final long serialVersionUID = -6657066865382086237L;

	@Column(name = "peticion_id", length = 26, nullable = false)
	private String scspPeticionId;
	@Column(name = "solicitud_id", length = 64, nullable = false)
	private String scspSolicitudId;

	@Column(name = "departament", length = 64, nullable = false)
	private String departamentNom;

	@Column(name = "funcionari_nom", length = 128)
	private String funcionariNom;
	@Column(name = "funcionari_docnum", length = 16)
	private String funcionariDocumentNum;
	@Column(name = "titular_doctip", length = 16, nullable = false)
	private String titularDocumentTipus;
	@Column(name = "titular_docnum", length = 14, nullable = false)
	private String titularDocumentNum;
	@Column(name = "titular_nom", length = 40)
	private String titularNom;
	@Column(name = "titular_lling1", length = 40)
	private String titularLlinatge1;
	@Column(name = "titular_lling2", length = 40)
	private String titularLlinatge2;
	@Column(name = "titular_nomcomplet", length = 122)
	private String titularNomComplet;

	@Column(name = "estat", nullable = false)
	private EstatTipus estat;

	@Column(name = "error", length = ERROR_MAX_LENGTH)
	private String error;

	@Column(name = "recobriment")
	private boolean recobriment = false;
	@Column(name = "multiple")
	private boolean multiple = false;

	@Column(name = "custodiat")
	private boolean custodiat = false;

	@ManyToOne(optional=true, fetch=FetchType.LAZY)
	@JoinColumn(name="pare_id")
	@ForeignKey(name="pbl_consulta_pare_fk")
	private Consulta pare;
	@OneToMany(mappedBy="pare", cascade={CascadeType.ALL})
	@OrderBy("scspSolicitudId asc")
	private List<Consulta> fills = new ArrayList<Consulta>();

	@ManyToOne(optional=false, fetch=FetchType.EAGER)
	@JoinColumn(name="procserv_id")
	@ForeignKey(name="pbl_procserv_consulta_fk")
	private ProcedimentServei procedimentServei;

	@Version
	private long version = 0;



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
				recobriment,
				multiple,
				pare);
	}

	public String getFuncionariNom() {
		return funcionariNom;
	}
	public String getFuncionariDocumentNum() {
		return funcionariDocumentNum;
	}
	public String getTitularNom() {
		return titularNom;
	}
	public String getTitularLlinatge1() {
		return titularLlinatge1;
	}
	public String getTitularLlinatge2() {
		return titularLlinatge2;
	}
	public String getTitularNomComplet() {
		return titularNomComplet;
	}
	public String getTitularDocumentTipus() {
		return titularDocumentTipus;
	}
	public String getTitularDocumentNum() {
		return titularDocumentNum;
	}
	public ProcedimentServei getProcedimentServei() {
		return procedimentServei;
	}
	public EstatTipus getEstat() {
		return estat;
	}
	public String getScspPeticionId() {
		return scspPeticionId;
	}
	public String getScspSolicitudId() {
		return scspSolicitudId;
	}
	public String getDepartamentNom() {
		return departamentNom;
	}
	public String getError() {
		return error;
	}
	public boolean isRecobriment() {
		return recobriment;
	}
	public boolean isMultiple() {
		return multiple;
	}
	public boolean isCustodiat() {
		return custodiat;
	}
	public Consulta getPare() {
		return pare;
	}
	public List<Consulta> getFills() {
		return fills;
	}

	public void updateEstat(EstatTipus estat) {
		this.estat = estat;
	}
	public void updateEstatError(String error) {
		this.estat = EstatTipus.Error;
		if (error != null && error.length() > ERROR_MAX_LENGTH) {
			String tokenFinal = " [...]";
			this.error = error.substring(0, (ERROR_MAX_LENGTH + tokenFinal.length())) + tokenFinal;
		} else {
			this.error = error;
		}
	}
	public void updateCustodiat(boolean custodiat) {
		this.custodiat = custodiat;
	}
	public void updateScspSolicitudId(String scspSolicitudId) {
		this.scspSolicitudId = scspSolicitudId;
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
