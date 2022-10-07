/**
 * 
 */
package es.caib.pinbal.core.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
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
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.pinbal.core.audit.PinbalAuditable;
import es.caib.pinbal.core.dto.ProcedimentClaseTramiteEnumDto;
import es.caib.pinbal.core.dto.ProcedimentServeiSimpleDto;
import lombok.Getter;

/**
 * Classe de model de dades que conté la informació d'un procediment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Entity
@Table(	name = "pbl_procediment",
		uniqueConstraints = {
			@UniqueConstraint(columnNames = {"entitat_id", "codi"})},
		indexes = {
				@Index(name = "pbl_procediment_entitat_i", columnList = "entitat_id")})
@EntityListeners(AuditingEntityListener.class)
public class Procediment extends PinbalAuditable<Long> {

	private static final long serialVersionUID = -6657066865382086237L;

	@Column(name = "codi", length = 20, nullable = false)
	private String codi;

	@Column(name = "nom", length = 255, nullable = false)
	private String nom;

	@Column(name = "departament", length = 64)
	private String departament;

	@Column(name = "actiu")
	private boolean actiu = true;

	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "organ_gestor_id")
	private OrganGestor organGestor;

	@Column(name = "codi_sia")
	private String codiSia;

	@Column(name = "valor_camp_automatizado")
	private Boolean valorCampAutomatizado;

	@Column(name = "valor_camp_clasetramite")
	@Convert(converter = ProcedimentClasseTramiteConverter.class)
	private ProcedimentClaseTramiteEnumDto valorCampClaseTramite;

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(
			name = "entitat_id",
			foreignKey = @ForeignKey(name = "pbl_entitat_procediment_fk"))
	private Entitat entitat;

	@OneToMany(mappedBy = "procediment", cascade = {CascadeType.ALL})
	@OrderBy("servei asc")
	private List<ProcedimentServei> serveis = new ArrayList<ProcedimentServei>();

	@Version
	private long version = 0;


	/**
	 * Obté el Builder per a crear objectes de tipus Procediment.
	 * 
	 * @param entitat
	 *            L'entitat del registre.
	 * @param codi
	 *            El codi del procediment (CodProcedimiento en les peticions SCSP).
	 * @param nom
	 *            El nom del procediment (NombreProcedimiento en les peticions SCSP).
	 * @param departament
	 *            El departament del procediment.
	 * @return Una nova instància del Builder.
	 */
	public static Builder getBuilder(
			Entitat entitat,
			String codi,
			String nom,
			String departament,
			OrganGestor organGestor,
			String codiSia,
			Boolean valorCampAutomatizado,
			ProcedimentClaseTramiteEnumDto valorCampClaseTramite) {
		return new Builder(
				entitat,
				codi,
				nom,
				departament,
				organGestor,
				codiSia,
				valorCampAutomatizado,
				valorCampClaseTramite);
	}

	public List<ProcedimentServeiSimpleDto> getServeisActius() {
		List<ProcedimentServeiSimpleDto> resposta = new ArrayList<ProcedimentServeiSimpleDto>();
		for (ProcedimentServei servei: serveis) {
			ProcedimentServeiSimpleDto nodeProcedimentServei = new ProcedimentServeiSimpleDto();
			nodeProcedimentServei.setProcedimentCodi(servei.getProcedimentCodi());
			nodeProcedimentServei.setServeiCodi(servei.getServei());
			nodeProcedimentServei.setActiu(servei.isActiu());
			
			resposta.add(nodeProcedimentServei);
		}
		return resposta;
	}

	public void update(
			String codi,
			String nom,
			String departament,
			OrganGestor organGestor,
			String codiSia,
			Boolean valorCampAutomatizado,
			ProcedimentClaseTramiteEnumDto valorCampClaseTramite) {
		this.codi = codi;
		this.nom = nom;
		this.departament = departament;
		this.organGestor = organGestor;
		this.codiSia = codiSia;
		this.valorCampAutomatizado = valorCampAutomatizado;
		this.valorCampClaseTramite = valorCampClaseTramite;
	}
	public void updateActiu(boolean actiu) {
		this.actiu = actiu;
	}

	public void addServei(String servei) {
		serveis.add(
				ProcedimentServei.getBuilder(
						this,
						servei).build());
	}

	/**
	 * La classe Builder emprada per a crear nous objectes de tipus Procediment.
	 */
	public static class Builder {
		Procediment built;

		/**
		 * Crea una nova instància del Builder.
		 * 
		 * @param entitat
		 *            L'entitat del procediment. 
		 * @param codi
		 *            El codi del procediment.
		 * @param nom
		 *            El nom del procediment.
		 * @param departament
		 *            El departament del procediment.
		 */
		Builder(
				Entitat entitat,
				String codi,
				String nom,
				String departament,
				OrganGestor organGestor,
				String codiSia,
				Boolean valorCampAutomatizado,
				ProcedimentClaseTramiteEnumDto valorCampClaseTramite) {
			built = new Procediment();
			built.entitat = entitat;
			built.codi = codi;
			built.nom = nom;
			built.departament = departament;
			built.organGestor = organGestor;
			built.codiSia = codiSia;
			built.valorCampAutomatizado = valorCampAutomatizado;
			built.valorCampClaseTramite = valorCampClaseTramite;
		}

		/**
		 * Construeix el nou objecte de tipus Procediment.
		 * 
		 * @return L'objecte de tipus Procediment creat.
		 */
		public Procediment build() {
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
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
		result = prime * result + ((entitat == null) ? 0 : entitat.hashCode());
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
		Procediment other = (Procediment) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		if (entitat == null) {
			if (other.entitat != null)
				return false;
		} else if (!entitat.equals(other.entitat))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
