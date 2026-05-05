/**
 * 
 */
package es.caib.pinbal.logic.intf.dto;

import es.caib.pinbal.logic.intf.dto.ConsultaDto.Consentiment;
import es.caib.pinbal.logic.intf.dto.ConsultaDto.DocumentTipus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.w3c.dom.Element;

import java.io.Serializable;

/**
 * Dades d'una sol·licitud provinent del recobriment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@ToString
public class RecobrimentSolicitudDto implements Serializable {

	private String id;
	private String entitatCif;
	private String procedimentCodi;
	private String funcionariNif;
	private String funcionariNom;
	private String departamentNom;
	private String unitatTramitadoraCodi;
	private Consentiment consentiment;
	private String finalitat;
	private String expedientId;
	private DocumentTipus titularDocumentTipus;
	private String titularDocumentNum;
	private String titularNom;
	private String titularLlinatge1;
	private String titularLlinatge2;
	private String titularNomComplet;
	private Element dadesEspecifiques;
	private boolean aplicacioGuardaJustificantArxiu;

	private static final long serialVersionUID = -139254994389509932L;

}
