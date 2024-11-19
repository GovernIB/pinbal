/**
 * 
 */
package es.caib.pinbal.core.dto;

/**
 * Enumeració amb els possibles estats d'accions d'integració.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum IntegracioAccioEstatEnumDto {
	OK,
	ERROR;
	public static IntegracioAccioEstatEnumDto[] sortedValues() {
		return new IntegracioAccioEstatEnumDto[] {
				IntegracioAccioEstatEnumDto.OK,
				IntegracioAccioEstatEnumDto.ERROR};
	}
}
