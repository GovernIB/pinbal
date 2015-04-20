/**
 * 
 */
package es.caib.pinbal.wso2.mediator;

import org.apache.axiom.om.OMElement;

/**
 * Mediator que imprimeix la petició i sempre retorna el mateix
 * codi d'entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PrintEntitatResolver implements EntitatResolver {

	@Override
	public String resolve(OMElement firstElement) {
		System.out.println(">>> " + firstElement.toString());
		return "07033";
	}

}
