/**
 * 
 */
package es.caib.pinbal.core.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Informació d'un arxiu.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class CacheDto implements Serializable, Identificable<String> {
	
	private String codi;
	private long localHeapSize;

	@Override
	public String getId() {
		return codi;
	}

	@Override
	public String getDefaultIdentifier() {
		return this.getClass().getSimpleName() + "#" + codi;
	}
}
