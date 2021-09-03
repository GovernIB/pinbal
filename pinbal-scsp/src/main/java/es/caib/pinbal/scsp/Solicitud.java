/**
 * 
 */
package es.caib.pinbal.scsp;

import java.util.Map;

import org.w3c.dom.Element;

import lombok.Getter;
import lombok.Setter;

/**
 * Dades per a una sol·licitud cap a SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class Solicitud {

	private String serveiCodi;
	private String procedimentCodi;
	private String procedimentNom;
	private Boolean procedimentValorCampAutomatizado;
	private Integer procedimentValorCampClaseTramite;
	private String solicitantIdentificacio;
	private String solicitantNom;
	private String funcionariNom;
	private String funcionariNif;
	private DocumentTipus titularDocumentTipus;
	private String titularDocument;
	private String titularNom;
	private String titularLlinatge1;
	private String titularLlinatge2;
	private String titularNomComplet;
	private String finalitat;
	private Consentiment consentiment;
	private String unitatTramitadora;
	private String unitatTramitadoraCodi;
	private String expedientId;

	private Element dadesEspecifiquesElement;
	private Map<String, Object> dadesEspecifiquesMap;

}
