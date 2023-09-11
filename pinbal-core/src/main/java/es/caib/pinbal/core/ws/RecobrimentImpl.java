/**
 * 
 */
package es.caib.pinbal.core.ws;

import es.caib.pinbal.core.helper.RecobrimentHelper;
import es.scsp.bean.common.ConfirmacionPeticion;
import es.scsp.bean.common.Peticion;
import es.scsp.bean.common.Respuesta;
import es.scsp.common.exceptions.ScspException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

/**
 * Implementació dels mètodes per al recobriment de les peticions
 * SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
@WebService(
		name = "Recobriment",
		serviceName = "RecobrimentService",
		portName = "RecobrimentServicePort",
		endpointInterface = "es.caib.pinbal.core.ws.Recobriment",
		targetNamespace = "http://www.caib.es/pinbal/ws/recobriment")
public class RecobrimentImpl implements Recobriment {

	@Autowired
	private RecobrimentHelper recobrimentHelper;

	@Override
	public Respuesta peticionSincrona(
			Peticion peticion) throws ScspException {
		return recobrimentHelper.peticionSincrona(peticion);
	}

	@Override
	public ConfirmacionPeticion peticionAsincrona(
			Peticion peticion) throws ScspException {
		return recobrimentHelper.peticionAsincrona(peticion);
	}

	@Override
	public Respuesta getRespuesta(
			String idpeticion) throws ScspException {
		return recobrimentHelper.getRespuesta(idpeticion);
	}

	@Override
	public byte[] getJustificante(
			String idpeticion,
			String idsolicitud) throws ScspException {
		return recobrimentHelper.getJustificante(idpeticion, idsolicitud, false, true).getContingut();
	}

}
