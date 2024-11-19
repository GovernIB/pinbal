/**
 * 
 */
package es.caib.pinbal.core.dto;


/**
 * Enumeració amb els possibles tipus d'accions d'integració.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum IntegracioAccioTipusEnumDto {
	ENVIAMENT,
	RECEPCIO;
	public static IntegracioAccioTipusEnumDto[] sortedValues() {
		return new IntegracioAccioTipusEnumDto[] {
				IntegracioAccioTipusEnumDto.ENVIAMENT,
				IntegracioAccioTipusEnumDto.RECEPCIO};
	}
}
