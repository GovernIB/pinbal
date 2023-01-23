/**
 * 
 */
package es.caib.pinbal.client.dadesobertes;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Estructura d'un element de la resposta per a les dades obertes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonAutoDetect
@XmlRootElement
public class DadesObertesRespostaConsulta {

	private String entitatCodi;
	private String entitatNom;
	private String entitatNif;
	private String entitatTipus;
	private String departamentCodi;
	private String departamentNom;
	private String procedimentCodi;
	private String procedimentNom;
	private String serveiCodi;
	private String serveiNom;
	private String emissor;
	private String emissorNif;
	private String consentiment;
	private String finalitat;
	private String titularTipusDoc;
	private String solicitudId;
	private Date data;
	private DadesObertesConsultaTipus tipus;
	private DadesObertesConsultaResultat resultat;

	public DadesObertesRespostaConsulta(
			String entitatCodi,
			String entitatNom,
			String entitatNif,
			String entitatTipus,
			String departamentCodi,
			String departamentNom,
			String procedimentCodi,
			String procedimentNom,
			String serveiCodi,
			String serveiNom,
			String emissor,
			String emissorNif,
			String consentiment,
			String finalitat,
			String titularTipusDoc,
			String solicitudId,
			Date data,
			boolean recobriment,
			String estat) {
		super();
		this.entitatCodi = entitatCodi;
		this.entitatNom = entitatNom;
		this.entitatNif = entitatNif;
		this.entitatTipus = entitatTipus;
		this.departamentCodi = departamentCodi;
		this.departamentNom = departamentNom;
		this.procedimentCodi = procedimentCodi;
		this.procedimentNom = procedimentNom;
		this.serveiCodi = serveiCodi;
		this.serveiNom = serveiNom;
		this.emissor = emissor;
		this.emissorNif = emissorNif;
		this.consentiment = consentiment;
		if (finalitat != null) {
			int index = finalitat.lastIndexOf("#");
			if (index != -1) {
				this.finalitat = finalitat.substring(index + 1);
			} else {
				this.finalitat = finalitat;
			}
		}
		this.titularTipusDoc = titularTipusDoc;
		this.solicitudId = solicitudId;
		this.data = data;
		this.tipus = recobriment ? DadesObertesConsultaTipus.RECOBRIMENT : DadesObertesConsultaTipus.WEB;
		if ("0".equals(estat)) {
			this.resultat = DadesObertesConsultaResultat.PROCES;
		} else if ("1".equals(estat)) {
			this.resultat = DadesObertesConsultaResultat.PROCES;
		} else if ("2".equals(estat)) {
			this.resultat = DadesObertesConsultaResultat.OK;
		} else if ("3".equals(estat)) {
			this.resultat = DadesObertesConsultaResultat.ERROR;
		}
	}

	public enum DadesObertesConsultaTipus {
		WEB,
		RECOBRIMENT
	}

	public enum DadesObertesConsultaResultat {
		OK,
		PROCES,
		ERROR
	}

}
