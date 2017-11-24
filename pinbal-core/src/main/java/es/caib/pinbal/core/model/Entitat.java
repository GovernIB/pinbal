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
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.pinbal.core.audit.PinbalAuditable;

/**
 * Classe de model de dades que conté la informació d'una entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "pbl_entitat")
@EntityListeners(AuditingEntityListener.class)
public class Entitat extends PinbalAuditable<Long> {

	public enum EntitatTipus {
		ALTRES,
		GOVERN,
		CONSELL,
		AJUNTAMENT
	}

	private static final long serialVersionUID = -6657066865382086237L;

	@Column(name = "codi", length = 64, nullable = false, unique = true)
	private String codi;

	@Column(name = "nom", length = 255, nullable = false)
	private String nom;

	@Column(name = "cif", length = 16, nullable = false)
	private String cif;

	@Column(name = "tipus", nullable = false)
	private EntitatTipus tipus;

	@Column(name = "activa")
	private boolean activa = true;

	@OneToMany(mappedBy = "entitat", cascade = {CascadeType.ALL})
	@OrderBy("servei asc")
	private List<EntitatServei> serveis = new ArrayList<EntitatServei>();

	@OneToMany(mappedBy = "entitat", cascade = {CascadeType.ALL})
	@OrderBy("id asc")
	private List<ServeiBus> serveisBus = new ArrayList<ServeiBus>();

	@OneToMany(mappedBy ="entitat", cascade = {CascadeType.ALL})
	@OrderBy("usuari asc")
	private List<EntitatUsuari> usuaris = new ArrayList<EntitatUsuari>();

	@OneToMany(mappedBy = "entitat", cascade = {CascadeType.ALL})
	@OrderBy("nom asc")
	private List<Procediment> procediments = new ArrayList<Procediment>();

	@Version
	private long version = 0;



	/**
	 * Obté el Builder per a crear objectes de tipus Entitat.
	 * 
	 * @param codi
	 *            El codi de l'entitat (NombreSolicitante en les peticions SCSP).
	 * @param nom
	 *            El nom de l'entitat.
	 * @param cif
	 *            El CIF de l'entitat (IdentificadorSolicitante en les peticions SCSP).
	 * @param responsable
	 *            El responsable de l'entitat.
	 * @param tipus
	 *            Indica el tipus d'entitat (govern, consell o ajuntament).
	 * @return Una nova instància del Builder.
	 */
	public static Builder getBuilder(
			String codi,
			String nom,
			String cif,
			EntitatTipus tipus) {
		return new Builder(
				codi,
				nom,
				cif,
				tipus);
	}

	public String getCodi() {
		return codi;
	}

	public String getNom() {
		return nom;
	}

	public String getCif() {
		return cif;
	}

	public EntitatTipus getTipus() {
		return tipus;
	}

	public boolean isActiva() {
		return activa;
	}

	public List<String> getServeis() {
		List<String> resposta = new ArrayList<String>();
		for (EntitatServei servei: serveis)
			resposta.add(servei.getServei());
		return resposta;
	}

	public List<ServeiBus> getServeisBus() {
		return serveisBus;
	}

	public List<EntitatUsuari> getUsuaris() {
		return usuaris;
	}

	public List<Procediment> getProcediments() {
		return procediments;
	}

	public long getVersion() {
		return version;
	}

	public void update(
			String codi,
			String nom,
			String cif,
			EntitatTipus tipus) {
		this.codi = codi;
		this.nom = nom;
		this.cif = cif;
		this.tipus = tipus;
	}
	public void updateActiva(boolean activa) {
		this.activa = activa;
	}

	public void addProcediment(Procediment procediment) {
		procediments.add(procediment);
	}
	public void addServei(String servei) {
		serveis.add(
				EntitatServei.getBuilder(
						this,
						servei).build());
	}
	public void addUsuari(
			Usuari usuari,
			String departament,
			boolean representant,
			boolean delegat,
			boolean auditor,
			boolean aplicacio) {
		usuaris.add(
				EntitatUsuari.getBuilder(
						this,
						usuari,
						departament,
						representant,
						delegat,
						auditor,
						aplicacio).build());
	}
	public void addServeiBus(
			String servei,
			String urlDesti) {
		serveisBus.add(
				ServeiBus.getBuilder(
						servei,
						urlDesti,
						this).build());
	}

	/**
	 * La classe Builder emprada per a crear nous objectes de tipus Entitat.
	 */
	public static class Builder {
		Entitat built;

		/**
		 * Crea una nova instància del Builder.
		 * 
		 * @param codi
		 *            El codi de l'entitat.
		 * @param nom
		 *            El nom de l'entitat.
		 * @param cif
		 *            El cif de l'entitat.
		 * @param tipus
		 *            Indica el tipus d'entitat (govern, consell o ajuntament).
		 */
		Builder(
				String codi,
				String nom,
				String cif,
				EntitatTipus tipus) {
			built = new Entitat();
			built.codi = codi;
			built.nom = nom;
			built.cif = cif;
			built.tipus = tipus;
		}

		/**
		 * Construeix el nou objecte de tipus Entitat.
		 * 
		 * @return L'objecte de tipus Entitat creat.
		 */
		public Entitat build() {
			return built;
		}
	}

	public void configurarIdPerTest(Long id) {
		this.setId(id);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Entitat other = (Entitat) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
