package es.caib.pinbal.persist.resourceentity;

import es.caib.pinbal.logic.intf.base.config.BaseConfig;
import es.caib.pinbal.logic.intf.model.UsuariResource;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * Entitat de base de dades d'usuari de l'aplicació.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "usuari")
@Getter
@Setter
@NoArgsConstructor
public class UsuariResourceEntity extends es.caib.pinbal.persist.base.entity.BaseResourceEntity<UsuariResource, String> {

	@Id
	@Column(name = "codi", length = 64, nullable = false)
	private String id;
	@Column(name = "nif", length = 15)
	private String nif;
	@Column(name = "nom", length = 200)
	private String nom;
	@Column(name = "inicialitzat")
	private boolean inicialitzat = false;
	@Column(name = "email", length = 200)
	private String email;
	@Column(name="idioma", length = 2)
	private String idioma;

//	@ManyToOne(optional = true, fetch = FetchType.LAZY)
//	@JoinColumn(name = "procediment_id", referencedColumnName = "id")
//	protected ProcedimentResourceEntity procediment;
//
//	@ManyToOne(optional = true, fetch = FetchType.LAZY)
//	@JoinColumn(name = "servei_codi", referencedColumnName = "id")
//	protected ServeiResourceEntity servei;
//
//	@ManyToOne(optional = true, fetch = FetchType.LAZY)
//	@JoinColumn(name = "entitat_id", referencedColumnName = "id")
//	private EntitatResourceEntity entitat;

	@Column(name = "departament", length = 250)
	private String departament;
	@Column(name = "finalitat", length = 250)
	private String finalitat;
	@Column(name = "num_elements_pagina")
	private Integer numElementsPagina;

	@Version
	private long version = 0;

	@Builder
	public UsuariResourceEntity(
		UsuariResource resource
//		EntitatResourceEntity entitatDefecte,
//		ServeiResourceEntity serveiDefecte,
//		ProcedimentResourceEntity procedimentDefecte
	) {
		this.id = resource.getCodi();
		this.nom = resource.getNom();
		this.nif = resource.getNif();
		this.inicialitzat = resource.isInicialitzat();
		this.email = resource.getEmail();
		this.idioma = resource.getIdioma();
		this.departament = resource.getDepartament();
		this.finalitat = resource.getFinalitat();
		this.numElementsPagina = resource.getNumElementsPagina();
//		this.entitatDefecte = entitatDefecte;
//		this.procedimentDefecte = procedimentDefecte;
//		this.organDefecte = serveiDefecte;
	}

	public String getCodi() {
		return id;
	}

//	@Converter
//	public static class IdiomaConverter implements AttributeConverter<Idioma, String> {
//		@Override
//		public String convertToDatabaseColumn(Idioma idioma) {
//			if (idioma == null) return null;
//			return idioma.name().toLowerCase();
//		}
//		@Override
//		public Idioma convertToEntityAttribute(String dbData) {
//			if (dbData == null) return null;
//			return Idioma.valueOf(dbData.toUpperCase());
//		}
//	}

}
