package es.caib.pinbal.core.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.pinbal.core.audit.PinbalAuditable;
import es.caib.pinbal.core.audit.PinbalAuditingEntityListener;

/**
 * Dades d'un camp per al formulari de configuracio d'un servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name = "pbl_servei_config" )
@EntityListeners(PinbalAuditingEntityListener.class)
public class ServeiConfig extends PinbalAuditable<Long> {

	public enum EntitatTipus {
		GOVERN,
		CONSELL,
		AJUNTAMENT
	}
	public enum JustificantTipus {
		GENERAT,
		ADJUNT_PDF_BASE64
	}

	private static final long serialVersionUID = -348876812591122748L;

	@Column(name = "servei_id", length = 64, nullable = false, unique = true)
	private String servei;
	@Column(name = "custodia_codi", length = 64)
	private String custodiaCodi;
	@Column(name = "role_name", length = 64)
	private String roleName;
	@Column(name = "condicio_bus_class", length = 255)
	private String condicioBusClass;
	@Column(name = "entitat_tipus")
	private EntitatTipus entitatTipus;
	@Column(name = "justificant_tipus", nullable = false)
	private JustificantTipus justificantTipus;
	@Column(name = "justificant_xpath", length = 255)
	private String justificantXpath;
	@Column(name = "permes_doctip_dni")
	private boolean permesDocumentTipusDni = true;
	@Column(name = "permes_doctip_nif")
	private boolean permesDocumentTipusNif = true;
	@Column(name = "permes_doctip_cif")
	private boolean permesDocumentTipusCif = true;
	@Column(name = "permes_doctip_nie")
	private boolean permesDocumentTipusNie = true;
	@Column(name = "permes_doctip_pas")
	private boolean permesDocumentTipusPas = true;
	@Column(name = "actiu_camp_nom")
	private boolean actiuCampNom = true;
	@Column(name = "actiu_camp_llin1")
	private boolean actiuCampLlinatge1 = true;
	@Column(name = "actiu_camp_llin2")
	private boolean actiuCampLlinatge2 = true;
	@Column(name = "actiu_camp_nomcomp")
	private boolean actiuCampNomComplet = true;
	@Column(name = "actiu_camp_doc")
	private boolean actiuCampDocument = true;
	@Column(name = "document_obligatori")
	private boolean documentObligatori = false;
	@Column(name = "comprovar_document")
	private boolean comprovarDocument = true;
	
	@Lob
	@Column(name = "ajuda")
	private String ajuda;

	@Version
	private long version = 0;



	/**
	 * Obté el Builder per a crear objectes de tipus ServeiConfig.
	 * 
	 * @param servei
	 *            Codi del servei al que fa referència aquest configuració.
	 * @param custodiaCodi
	 *            Codi de custòdia per als justificants d'aquest servei.
	 * @param roleName
	 *            Rol de seguretat per a controlar l'accés a aquest servei.
	 * @param condicioBusClass
	 *            Classe que implementa la interfície CondicioBus i que el
	 *            bus haurà d'instanciar per a obtenir l'entitat emissora cap
	 *            a on ha de redirigir la petició.
	 * @param entitatTipus
	 *            Tipus d'entitats disponibles per a definir les redireccions.
	 * @param justificantTipus
	 *            Tipus de justificant que proporcionarà aquest servei.
	 * @param justificantXpath
	 *            Xpath per a localitzar el justificant a les dades específiques.
	 * @return Una nova instància del Builder.
	 */
	public static Builder getBuilder(
			String servei,
			String custodiaCodi,
			String roleName,
			String condicioBusClass,
			EntitatTipus entitatTipus,
			JustificantTipus justificantTipus,
			String justificantXpath,
			String ajuda) {
		return new Builder(
				servei,
				custodiaCodi,
				roleName,
				condicioBusClass,
				entitatTipus,
				justificantTipus,
				justificantXpath,
				ajuda);
	}

	public String getServei() {
		return servei;
	}
	public String getCustodiaCodi() {
		return custodiaCodi;
	}
	public String getRoleName() {
		return roleName;
	}
	public String getCondicioBusClass() {
		return condicioBusClass;
	}
	public EntitatTipus getEntitatTipus() {
		return entitatTipus;
	}
	public JustificantTipus getJustificantTipus() {
		return justificantTipus;
	}
	public String getJustificantXpath() {
		return justificantXpath;
	}
	public boolean isPermesDocumentTipusDni() {
		return permesDocumentTipusDni;
	}
	public boolean isPermesDocumentTipusNif() {
		return permesDocumentTipusNif;
	}
	public boolean isPermesDocumentTipusCif() {
		return permesDocumentTipusCif;
	}
	public boolean isPermesDocumentTipusNie() {
		return permesDocumentTipusNie;
	}
	public boolean isPermesDocumentTipusPas() {
		return permesDocumentTipusPas;
	}
	public boolean isActiuCampNom() {
		return actiuCampNom;
	}
	public boolean isActiuCampLlinatge1() {
		return actiuCampLlinatge1;
	}
	public boolean isActiuCampLlinatge2() {
		return actiuCampLlinatge2;
	}
	public boolean isActiuCampNomComplet() {
		return actiuCampNomComplet;
	}
	public boolean isActiuCampDocument() {
		return actiuCampDocument;
	}
	public boolean isDocumentObligatori() {
		return documentObligatori;
	}
	public boolean isComprovarDocument() {
		return comprovarDocument;
	}
	public String getAjuda() {
		return ajuda;
	}
	public void setAjuda(String ajuda) {
		this.ajuda = ajuda;
	}

	public long getVersion() {
		return version;
	}

	public void update(
			String custodiaCodi,
			String roleName,
			String condicioBusClass,
			EntitatTipus entitatTipus,
			JustificantTipus justificantTipus,
			String justificantXpath,
			boolean permesDocumentTipusDni,
			boolean permesDocumentTipusNif,
			boolean permesDocumentTipusCif,
			boolean permesDocumentTipusNie,
			boolean permesDocumentTipusPas,
			boolean actiuCampNom,
			boolean actiuCampLlinatge1,
			boolean actiuCampLlinatge2,
			boolean actiuCampNomComplet,
			boolean actiuCampDocument,
			boolean documentObligatori,
			boolean comprovarDocument,
			String ajuda) {
		this.custodiaCodi = custodiaCodi;
		this.roleName = roleName;
		this.condicioBusClass = condicioBusClass;
		this.entitatTipus = entitatTipus;
		this.justificantTipus = justificantTipus;
		this.justificantXpath = justificantXpath;
		this.permesDocumentTipusDni = permesDocumentTipusDni;
		this.permesDocumentTipusNif = permesDocumentTipusNif;
		this.permesDocumentTipusCif = permesDocumentTipusCif;
		this.permesDocumentTipusNie = permesDocumentTipusNie;
		this.permesDocumentTipusPas = permesDocumentTipusPas;
		this.actiuCampNom = actiuCampNom;
		this.actiuCampLlinatge1 = actiuCampLlinatge1;
		this.actiuCampLlinatge2 = actiuCampLlinatge2;
		this.actiuCampNomComplet = actiuCampNomComplet;
		this.actiuCampDocument = actiuCampDocument;
		this.documentObligatori = documentObligatori;
		this.comprovarDocument = comprovarDocument;
		this.ajuda = ajuda;
	}

	/**
	 * La classe Builder emprada per a crear nous objectes de tipus ServeiConfig.
	 */
	public static class Builder {
		ServeiConfig built;
		/**
		 * Crea una nova instància del ServeiConfig.
		 * 
		 * @param servei
		 *            Codi del servei al que fa referència aquest configuració.
		 * @param custodiaCodi
		 *            Codi de custòdia per als justificants d'aquest servei.
		 * @param roleName
		 *            Rol de seguretat per a controlar l'accés a aquest servei.
		 * @param condicioBusClass
		 *            Classe que implementa la interfície CondicioBus i que el
		 *            bus haurà d'instanciar per a obtenir l'entitat emissora cap
		 *            a on ha de redirigir la petició.
		 * @param entitatTipus
		 *            Tipus d'entitats disponibles per a definir les redireccions.
		 */
		Builder(
				String servei,
				String custodiaCodi,
				String roleName,
				String condicioBusClass,
				EntitatTipus entitatTipus,
				JustificantTipus justificantTipus,
				String justificantXpath,
				String ajuda) {
			built = new ServeiConfig();
			built.servei = servei;
			built.custodiaCodi = custodiaCodi;
			built.roleName = roleName;
			built.condicioBusClass = condicioBusClass;
			built.entitatTipus = entitatTipus;
			built.justificantTipus = justificantTipus;
			built.justificantXpath = justificantXpath;
			built.ajuda = ajuda;
		}
		/**
		 * Construeix el nou objecte de tipus ServeiConfig.
		 * 
		 * @return L'objecte de tipus ServeiConfig creat.
		 */
		public ServeiConfig build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((servei == null) ? 0 : servei.hashCode());
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
		ServeiConfig other = (ServeiConfig) obj;
		if (servei == null) {
			if (other.servei != null)
				return false;
		} else if (!servei.equals(other.servei))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
