/**
 * 
 */
package es.caib.pinbal.wso2.mediator;

import org.apache.axiom.om.OMElement;

/**
 * Mediator de prova. Sempre retornara la mateixa entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SampleEntitatResolver implements EntitatResolver {

	@Override
	public String resolve(OMElement firstElement) {
		return "07033";
	}

}
