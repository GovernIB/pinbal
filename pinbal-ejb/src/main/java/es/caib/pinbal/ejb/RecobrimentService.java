/**
 * 
 */
package es.caib.pinbal.ejb;

import es.caib.pinbal.client.procediments.ProcedimentBasic;
import es.caib.pinbal.client.recobriment.model.ScspConfirmacionPeticion;
import es.caib.pinbal.client.recobriment.model.ScspJustificante;
import es.caib.pinbal.client.recobriment.model.ScspPeticion;
import es.caib.pinbal.client.recobriment.model.ScspRespuesta;
import es.caib.pinbal.client.recobriment.v2.*;
import es.caib.pinbal.client.serveis.ServeiBasic;
import es.caib.pinbal.logic.intf.service.exception.*;
import org.springframework.context.annotation.Primary;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;

/**
 * Implementació de RecobrimentService que empra una clase delegada per accedir a la
 * funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Primary
@Stateless
public class RecobrimentService extends AbstractService<es.caib.pinbal.logic.intf.service.RecobrimentService> implements es.caib.pinbal.logic.intf.service.RecobrimentService {

	@Override
	@RolesAllowed("PBL_WS")
	public ScspRespuesta peticionSincrona(
			ScspPeticion peticion) throws RecobrimentScspException {
		return getDelegateService().peticionSincrona(peticion);
	}

	@Override
	@RolesAllowed("PBL_WS")
	public ScspConfirmacionPeticion peticionAsincrona(
			ScspPeticion peticion) throws RecobrimentScspException {
		return getDelegateService().peticionAsincrona(peticion);
	}

	@Override
	@RolesAllowed("PBL_WS")
	public ScspRespuesta getRespuesta(
			String idPeticion) throws RecobrimentScspException {
		return getDelegateService().getRespuesta(idPeticion);
	}

	@Override
	@RolesAllowed("PBL_WS")
	public ScspJustificante getJustificante(
			String idPeticion,
			String idSolicitud) throws RecobrimentScspException {
		return getDelegateService().getJustificante(idPeticion, idSolicitud);
	}

    @Override
	@RolesAllowed("PBL_WS")
    public ScspJustificante getJustificanteImprimible(String idPeticion, String idSolicitud) throws RecobrimentScspException {
        return getDelegateService().getJustificanteImprimible(idPeticion, idSolicitud);
    }

	@Override
	@RolesAllowed("PBL_WS")
	public String getJustificanteCsv(String idPeticion, String idSolicitud) throws RecobrimentScspException {
		return getDelegateService().getJustificanteCsv(idPeticion, idSolicitud);
	}

	@Override
	@RolesAllowed("PBL_WS")
	public String getJustificanteUuid(String idPeticion, String idSolicitud) throws RecobrimentScspException {
		return getDelegateService().getJustificanteUuid(idPeticion, idSolicitud);
	}


	// V2
	// /////////////////////////////////////////////////////////////

	@Override
	public List<Entitat> getEntitats() {
		return getDelegateService().getEntitats();
	}

    @Override
	@RolesAllowed("PBL_WS")
    public List<ProcedimentBasic> getProcediments(String entitatCodi) throws EntitatNotFoundException {
        return getDelegateService().getProcediments(entitatCodi);
    }

	@Override
	@RolesAllowed("PBL_WS")
	public List<ServeiBasic> getServeis() {
		return getDelegateService().getServeis();
	}

	@Override
	@RolesAllowed("PBL_WS")
	public List<ServeiBasic> getServeisByEntitat(String entitatCodi) throws EntitatNotFoundException {
		return getDelegateService().getServeisByEntitat(entitatCodi);
	}

	@Override
	@RolesAllowed("PBL_WS")
	public List<ServeiBasic> getServeisByProcediment(String entitatCodi, String procedimentCodi) throws ProcedimentNotFoundException {
		return getDelegateService().getServeisByProcediment(entitatCodi, procedimentCodi);
	}

	@Override
	@RolesAllowed("PBL_WS")
	public List<DadaEspecifica> getDadesEspecifiquesByServei(String serveiCodi) throws ServeiNotFoundException {
		return getDelegateService().getDadesEspecifiquesByServei(serveiCodi);
	}

    @Override
    @RolesAllowed("PBL_WS")
    public List<DadaEspecificaBasic> getDadesEspecifiquesByServeiResposta(String serveiCodi) throws Exception {
        return getDelegateService().getDadesEspecifiquesByServeiResposta(serveiCodi);
    }

    @Override
	@RolesAllowed("PBL_WS")
	public List<ValorEnum> getValorsEnumByServei(String serveiCodi, String campCodi, String enumCodi, String filtre) throws Exception {
		return getDelegateService().getValorsEnumByServei(serveiCodi, campCodi, enumCodi, filtre);
	}

	@Override
	@RolesAllowed("PBL_WS")
	public Map<String, List<String>> validatePeticio(String serveiCodi, PeticioSincrona peticio) {
		return getDelegateService().validatePeticio(serveiCodi, peticio);
	}

	@Override
	@RolesAllowed("PBL_WS")
	public Map<String, List<String>> validatePeticio(String serveiCodi, PeticioAsincrona peticio) {
		return getDelegateService().validatePeticio(serveiCodi, peticio);
	}

	@Override
	@RolesAllowed("PBL_WS")
	public PeticioRespostaSincrona peticionSincrona(PeticioSincrona peticio) {
		return getDelegateService().peticionSincrona(peticio);
	}

	@Override
	@RolesAllowed("PBL_WS")
	public PeticioConfirmacioAsincrona peticionAsincrona(PeticioAsincrona peticio) {
		return getDelegateService().peticionAsincrona(peticio);
	}

	@Override
	@RolesAllowed("PBL_WS")
	public PeticioRespostaAsincrona getResposta(String idPeticion) throws RecobrimentScspException, ConsultaNotFoundException {
		return getDelegateService().getResposta(idPeticion);
	}

	@Override
	@RolesAllowed("PBL_WS")
	public ScspJustificante getJustificant(String idPeticion, String idSolicitud) throws RecobrimentScspException, ConsultaNotFoundException {
		return getDelegateService().getJustificant(idPeticion, idSolicitud);
	}

	@Override
	@RolesAllowed("PBL_WS")
	public ScspJustificante getJustificantImprimible(String idPeticion, String idSolicitud) throws RecobrimentScspException, ConsultaNotFoundException {
		return getDelegateService().getJustificantImprimible(idPeticion, idSolicitud);
	}

	@Override
	@RolesAllowed("PBL_WS")
	public String getJustificantCsv(String idPeticion, String idSolicitud) throws RecobrimentScspException, ConsultaNotFoundException {
		return getDelegateService().getJustificantCsv(idPeticion, idSolicitud);
	}

	@Override
	@RolesAllowed("PBL_WS")
	public String getJustificantUuid(String idPeticion, String idSolicitud) throws RecobrimentScspException, ConsultaNotFoundException {
		return getDelegateService().getJustificantUuid(idPeticion, idSolicitud);
	}

}
