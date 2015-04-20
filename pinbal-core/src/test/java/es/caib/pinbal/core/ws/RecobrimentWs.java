/**
 * 
 */
package es.caib.pinbal.core.ws;

import javax.jws.WebService;

/**
 * Declaració dels mètodes per al recobriment de les peticions
 * SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@WebService(
		name = "Recobriment",
		serviceName = "RecobrimentService",
		portName = "RecobrimentServicePort",
		targetNamespace = "http://www.caib.es/pinbal/ws/recobriment")
public interface RecobrimentWs extends Recobriment {

}
