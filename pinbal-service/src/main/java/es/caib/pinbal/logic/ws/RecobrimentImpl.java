/**
 * 
 */
package es.caib.pinbal.logic.ws;

import es.caib.pinbal.logic.helper.RecobrimentHelper;
import es.caib.pinbal.logic.intf.ws.Recobriment;
import es.scsp.bean.common.confirmacion.ConfirmacionPeticion;
import es.scsp.bean.common.peticion.Peticion;
import es.scsp.bean.common.respuesta.Respuesta;
import es.scsp.common.exceptions.ScspException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementació dels mètodes per al recobriment de les peticions
 * SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@RequiredArgsConstructor
@Service
//@WebService(
//		name = "Recobriment",
//		serviceName = "RecobrimentService",
//		portName = "RecobrimentServicePort",
//		endpointInterface = "es.caib.pinbal.core.ws.Recobriment",
//		targetNamespace = "http://www.caib.es/pinbal/ws/recobriment")
public class RecobrimentImpl implements Recobriment {

	private final RecobrimentHelper recobrimentHelper;

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
