/**
 * 
 */
package es.caib.pinbal.core.dto.arxiu;


/**
 * Enumeració amb els possibles tipus de firma (NTI) dels documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum DocumentNtiTipoFirmaEnumDto {
	TF01, // CSV
	TF02, // XAdES internally detached signature
	TF03, // XAdES enveloped signature
	TF04, // CAdES detached/explicit signature
	TF05, // CAdES attached/implicit signature
	TF06, // PAdES
	TF07, // SMIME
	TF08, // ODT
	TF09  // OOXML
}
