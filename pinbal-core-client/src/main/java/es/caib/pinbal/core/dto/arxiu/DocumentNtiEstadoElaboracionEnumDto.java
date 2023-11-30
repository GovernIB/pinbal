/**
 * 
 */
package es.caib.pinbal.core.dto.arxiu;


/**
 * Enumeració amb els possibles estats d'elaboració (NTI) dels documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum DocumentNtiEstadoElaboracionEnumDto {
	EE01, // Original
	EE02, // Copia electrònica autèntica amb canvi de format
	EE03, // Copia electrònica autèntica de documento paper
	EE04, // Copia electrònica parcial autèntica
	EE99  // Otros
}
