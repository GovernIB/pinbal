package es.caib.pinbal.core.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.pinbal.core.audit.PinbalAuditable;
import lombok.Getter;
import lombok.Setter;

/**
 * Dades d'un camp per al formulari de configuracio d'un servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@Entity
@Table(	name = "pbl_servei_config" )
@EntityListeners(AuditingEntityListener.class)
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
	@Column(name = "unitat_dir3_from_entitat", length = 10)
	private boolean pinbalUnitatDir3FromEntitat;
	@Column(name = "unitat_dir3", length = 10)
	private String pinbalUnitatDir3;
	@Column(name = "document_obligatori")
	private boolean documentObligatori = false;
	@Column(name = "comprovar_document")
	private boolean comprovarDocument = true;
	@Column(name = "activa_gestio_xsd")
	private boolean activaGestioXsd = false;
	@Column(name = "max_peticions_min")
	private Integer maxPeticionsMinut;
	@Column(name = "actiu", nullable = false)
	private boolean actiu;
	
	// Ajuda
	@Lob
	@Column(name = "ajuda")
	private String ajuda;
	
	@Column(name = "fitxer_ajuda_nom")
	private String fitxerAjudaNom;
	@Column(name = "fitxer_ajuda_mime")
	private String fitxerAjudaMimeType;
	@Lob
	@Column(name = "fitxer_ajuda_contingut")
	private byte[] fitxerAjudaContingut;

	@Column(name = "ini_dades_especifiques")
	private boolean iniDadesEspecifiques = false;

	@Column(name = "use_auto_classe")
	private boolean useAutoClasse = true;

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
			String ajuda,
			boolean activaGestioXsd,
			Integer maxPeticionsMinut,
			String fitxerAjudaNom,
			String fitxerAjudaMimeType,
			byte[] fitxerAjudaContingut,
			boolean iniDadesEspecifiques,
			boolean useAutoClasse,
			boolean actiu) {
		return new Builder(
				servei,
				custodiaCodi,
				roleName,
				condicioBusClass,
				entitatTipus,
				justificantTipus,
				justificantXpath,
				ajuda,
				activaGestioXsd,
				maxPeticionsMinut,
				fitxerAjudaNom,
				fitxerAjudaMimeType,
				fitxerAjudaContingut,
				iniDadesEspecifiques,
				useAutoClasse,
				actiu);
	}

	public void updateActiu(boolean actiu) {
		this.actiu = actiu;
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
			String pinbalUnitatDir3,
			boolean documentObligatori,
			boolean comprovarDocument,
			boolean activaGestioXsd,
			Integer maxPeticionsMinut,
			String ajuda, 
			boolean iniDadesEspecifiques,
			boolean useAutoClasse) {
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
		this.pinbalUnitatDir3 = pinbalUnitatDir3;
		this.documentObligatori = documentObligatori;
		this.comprovarDocument = comprovarDocument;
		this.activaGestioXsd = activaGestioXsd;
		this.maxPeticionsMinut = maxPeticionsMinut;
		this.ajuda = ajuda;
		this.iniDadesEspecifiques = iniDadesEspecifiques;
		this.useAutoClasse = useAutoClasse;
	}
	public void updateFitxerAjuda(
			String fitxerAjudaNom,
			String fitxerAjudaMimeType,
			byte[] fitxerAjudaContingut) {
		this.fitxerAjudaNom = fitxerAjudaNom;
		this.fitxerAjudaMimeType = fitxerAjudaMimeType;
		this.fitxerAjudaContingut = fitxerAjudaContingut;
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
				String ajuda,
				boolean activaGestioXsd,
				Integer maxPeticionsMinut,
				String fitxerAjudaNom,
				String fitxerAjudaMimeType,
				byte[] fitxerAjudaContingut,
				boolean iniDadesEspecifiques,
				boolean useAutoClasse,
				boolean actiu) {
			built = new ServeiConfig();
			built.servei = servei;
			built.custodiaCodi = custodiaCodi;
			built.roleName = roleName;
			built.condicioBusClass = condicioBusClass;
			built.entitatTipus = entitatTipus;
			built.justificantTipus = justificantTipus;
			built.justificantXpath = justificantXpath;
			built.ajuda = ajuda;
			built.activaGestioXsd = activaGestioXsd;
			built.maxPeticionsMinut = maxPeticionsMinut;
			built.fitxerAjudaNom = fitxerAjudaNom;
			built.fitxerAjudaMimeType = fitxerAjudaMimeType;
			built.fitxerAjudaContingut = fitxerAjudaContingut;
			built.iniDadesEspecifiques = iniDadesEspecifiques;
			built.useAutoClasse = useAutoClasse;
			built.actiu = actiu;
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
