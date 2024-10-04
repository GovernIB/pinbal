/**
 * 
 */
package es.caib.pinbal.core.model;

import es.caib.pinbal.core.dto.NumElementsPaginaEnum;
import lombok.Getter;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Classe de model de dades que conté la informació d'un usuari. L'única
 * utilitat d'aquesta classe és permetre emprar el mecanisme d'auditoria
 * de Spring-Data.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Entity
@Table(name = "pbl_usuari")
public class Usuari implements Serializable {

	private static final String NOM_USUARI_NOINIT_NIF = "NIF";
	private static final String NOM_USUARI_NOINIT_CODI = "CODI";

	@Id
	@Column(name = "codi", length = 64, nullable = false, unique = true)
	private String codi;

	@Column(name = "nom", length = 200)
	private String nom;

	@Column(name = "nif", length = 15, unique = true)
	private String nif;

	@Column(name = "inicialitzat")
	private boolean inicialitzat = false;
	
	@Column(name = "email", length = 200)
	private String email;
	
	@Column(name="idioma", length = 2)
	private String idioma;

	@OneToMany(mappedBy="usuari", cascade={CascadeType.ALL})
	private Set<EntitatUsuari> entitats = new HashSet<EntitatUsuari>();

	// Valors per defecte
	// ==============================================================
	// Filtres
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "procediment_id", insertable = false, updatable = false)
	private Procediment procediment;
	@Column(name = "procediment_id")
	private Long procedimentId;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "servei_codi",
			referencedColumnName = "CODCERTIFICADO",
			insertable = false, updatable = false,
			foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Servei servei;
	@Column(name = "servei_codi", length = 64)
	private String serveiCodi;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "entitat_id", insertable = false, updatable = false)
	private Entitat entitat;
	@Column(name = "entitat_id")
	private Long entitatId;
	// Formulari
	@Column(name = "departament", length = 250)
	private String departament;
	@Column(name = "finalitat", length = 250)
	private String finalitat;
	@Column(name = "num_elements_pagina")
	private Integer numElementsPagina;

	@Version
	private long version = 0;



	/**
	 * Obté el Builder per a crear objectes de tipus Usuari inicialitzats.
	 * 
	 * @param codi
	 *            El codi de l'usuari.
	 * @param nom
	 *            El nom de l'usuari.
	 * @param nif
	 *            El nif de l'usuari.
	 * @return Una nova instància del Builder.
	 */
	public static Builder getBuilderInicialitzat(
			String codi,
			String nom,
			String nif) {
		return new Builder(
				codi,
				nom,
				nif,
				true);
	}
	/**
	 * Obté el Builder per a crear objectes de tipus Usuari sense
	 * inicialitzar disposant del codi.
	 * 
	 * @param codi
	 *            El codi de l'usuari.
	 * @return Una nova instància del Builder.
	 */
	public static Builder getBuilderNoInicialitzatCodi(String codi) {
		return new Builder(
				codi,
				NOM_USUARI_NOINIT_CODI,
				codi,
				false);
	}
	/**
	 * Obté el Builder per a crear objectes de tipus Usuari sense
	 * inicialitzar disposant del NIF.
	 * 
	 * @param nif
	 *            El nif de l'usuari.
	 * @return Una nova instància del Builder.
	 */
	public static Builder getBuilderNoInicialitzatNif(String nif) {
		return new Builder(
				nif,
				NOM_USUARI_NOINIT_NIF,
				nif,
				false);
	}

	public boolean isNoInicialitzatNif() {
		return !inicialitzat && NOM_USUARI_NOINIT_NIF.equals(nom);
	}
	public boolean isNoInicialitzatCodi() {
		return !inicialitzat && NOM_USUARI_NOINIT_CODI.equals(nom);
	}

	public void update(
			String nom,
			String nif) {
		this.nom = nom;
		this.nif = nif;
		this.inicialitzat = true;
	}
	
	public void updateEmail(String email) {
		this.email = email;
	}
//	public void updateIdioma(String idioma) {
//		this.idioma = idioma;
//	}
	public void updateValorsPerDefecte(
			String idioma,
			Long procedimentId,
			String serveiCodi,
			String departament,
			String finalitat,
			Integer numElementsPagina) {

		this.idioma = idioma;
		this.procedimentId = procedimentId;
        this.serveiCodi = serveiCodi;
        this.departament = departament;
        this.finalitat = finalitat;
		this.numElementsPagina = numElementsPagina;
	}
	public void updateValorsPerDefecte(
			String idioma,
			Long procedimentId,
			String serveiCodi,
			Long entitatId,
			String departament,
			String finalitat,
			Integer numElementsPagina) {

		this.idioma = idioma;
		this.procedimentId = procedimentId;
		this.serveiCodi = serveiCodi;
		this.entitatId = entitatId;
		this.departament = departament;
		this.finalitat = finalitat;
		this.numElementsPagina = numElementsPagina;
	}
	public void moureEntitats(
			Usuari usuariNou) {
		for (EntitatUsuari eu: entitats) {
			usuariNou.addEntitat(eu);
			eu.updateUsuari(usuariNou);
		}
		entitats.clear();
	}

	private void addEntitat(EntitatUsuari entitat) {
		entitats.add(entitat);
	}

	/**
	 * La classe Builder emprada per a crear nous objectes de tipus Usuari.
	 */
	public static class Builder {
		Usuari built;

		/**
		 * Crea una nova instància del Builder.
		 * 
		 * @param codi
		 *            El codi de l'usuari.
		 * @param nom
		 *            El nom de l'usuari.
		 * @param nif
		 *            El nif de l'usuari.
		 * @param inicialitzat
		 *            Indica si l'usuari està correctament configurat.
		 */
		Builder(String codi,
				String nom,
				String nif,
				boolean inicialitzat) {
			built = new Usuari();
			built.codi = codi;
			built.nom = nom;
			built.nif = nif;
			built.inicialitzat = inicialitzat;
			built.numElementsPagina = NumElementsPaginaEnum.DEU.getElements();
		}

		/**
		 * Construeix el nou objecte de tipus Usuari.
		 * 
		 * @return L'objecte de tipus Usuari creat.
		 */
		public Usuari build() {
			return built;
		}
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
		Usuari other = (Usuari) obj;
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

	private static final long serialVersionUID = -6657066865382086237L;

}
