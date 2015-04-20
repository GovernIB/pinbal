/**
 * 
 */
package es.caib.pinbal.wso2.mediator;

import org.apache.axiom.om.OMElement;

/**
 * Interfície per a resoldre un codi d'entitat a partir
 * d'una petició SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EntitatResolver {

	public String resolve(OMElement firstElement);

}
