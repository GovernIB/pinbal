/**
 * 
 */
package es.caib.pinbal.ejb;

import es.caib.pinbal.logic.intf.dto.*;
import es.caib.pinbal.logic.intf.dto.regles.CampFormProperties;
import es.caib.pinbal.logic.intf.dto.regles.ServeiReglaDto;
import es.caib.pinbal.logic.intf.service.exception.*;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.io.IOException;
import java.util.List;

/**
 * Implementació de ServeiService que empra una clase delegada per accedir a la
 * funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Primary
@Stateless
public class ServeiService extends AbstractService<es.caib.pinbal.logic.intf.service.ServeiService> implements es.caib.pinbal.logic.intf.service.ServeiService {

	@Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPRES"})
	public ServeiDto getServeiDtoByCodi(String serveiCodi) throws ServeiNotFoundException {
		return getDelegateService().getServeiDtoByCodi(serveiCodi);
	}
	
	@Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPRES"})
	public List<ServeiDto> getServeis(String text) {
		return getDelegateService().getServeis(text);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ServeiDto save(ServeiDto servei) throws ServeiNotFoundException {
		return getDelegateService().save(servei);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ServeiDto delete(
			String serveiCodi) throws ServeiNotFoundException, ServeiAmbConsultesException {
		return getDelegateService().delete(serveiCodi);
	}

	@Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPRES"})
	public ServeiDto findAmbCodiPerAdminORepresentant(
			String codi) throws ServeiNotFoundException {
		return getDelegateService().findAmbCodiPerAdminORepresentant(codi);
	}

	@Override
	@RolesAllowed("**")
	public ServeiDto findAmbCodiPerDelegat(
			Long entitatId,
			String codi) throws ServeiNotFoundException {
		return getDelegateService().findAmbCodiPerDelegat(entitatId, codi);
	}

	@Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPRES"})
	public ServeiDto findById(Long id) {
		return getDelegateService().findById(id);
	}
	
	@Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPORT"})
	public List<ServeiDto> findActius() {
		return getDelegateService().findActius();
	}
	
	@Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPORT"})
	public List<ServeiDto> findActius(String filtre) {
		return getDelegateService().findActius(filtre);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public Page<ServeiDto> findAmbFiltrePaginat(String codi, String descripcio, String emisor,
			Boolean activa, String scspVersionEsquema, Pageable pageable) {
		return getDelegateService().findAmbFiltrePaginat(codi, descripcio, emisor, activa, scspVersionEsquema, pageable);
	}
	
	@Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPRES"})
	public Page<ServeiDto> findAmbFiltrePaginat(
			String codi,
			String descripcio,
			String emisor,
			Boolean actiu,
			EntitatDto entitat,
			ProcedimentDto procediment,
			Pageable pageable) {
		return getDelegateService().findAmbFiltrePaginat(codi, descripcio, emisor, actiu, entitat, procediment, pageable);
	}
	
	@Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPRES", "PBL_AUDIT", "PBL_SUPERAUD"})
	public List<ServeiDto> findAmbEntitat(Long entitatId)
			throws EntitatNotFoundException {
		return getDelegateService().findAmbEntitat(entitatId);
	}
	
	@Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPRES", "PBL_AUDIT", "PBL_SUPERAUD"})
	public List<ServeiDto> findAmbEntitat(Long entitatId, String filtre)
			throws EntitatNotFoundException {
		return getDelegateService().findAmbEntitat(entitatId, filtre);
	}

	@Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPRES", "PBL_AUDIT", "PBL_SUPERAUD"})
	public List<ServeiDto> findAmbEntitatIProcediment(
			Long entitatId,
			Long procedimentId) throws EntitatNotFoundException, ProcedimentNotFoundException {
		return getDelegateService().findAmbEntitatIProcediment(entitatId, procedimentId);
	}
	
	@Override
	@RolesAllowed("PBL_ADMIN")
	public List<ServeiDto> findAmbProcediment(
			Long procedimentId) throws ProcedimentNotFoundException {
		return getDelegateService().findAmbProcediment(procedimentId);
	}
	
	@Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPRES", "PBL_AUDIT", "PBL_SUPERAUD"})
	public List<ServeiDto> findAmbEntitatIProcediment(
			Long entitatId,
			Long procedimentId,
			String filtre) throws EntitatNotFoundException, ProcedimentNotFoundException {
		return getDelegateService().findAmbEntitatIProcediment(entitatId, procedimentId, filtre);
	}

    @Override
    @RolesAllowed("PBL_REPRES")
    public List<ServeiDto> findAmbEntitatNotInProcediment(Long entitatId, Long procedimentId) throws EntitatNotFoundException, ProcedimentNotFoundException {
        return getDelegateService().findAmbEntitatNotInProcediment(entitatId, procedimentId);
    }

    @Override
	@RolesAllowed("PBL_REPRES")
	public List<ProcedimentServeiDto> findPermesosAmbEntitatIUsuari(
			Long entitatId,
			String usuariCodi) throws EntitatNotFoundException {
		return getDelegateService().findPermesosAmbEntitatIUsuari(entitatId, usuariCodi);
	}

    @Override
	@RolesAllowed("PBL_REPRES")
    public Integer countPermesosAmbEntitatIUsuari(Long entitatId, String usuariCodi) {
        return getDelegateService().countPermesosAmbEntitatIUsuari(entitatId, usuariCodi);
    }

    @Override
	@RolesAllowed("**")
	public List<ServeiDto> findPermesosAmbProcedimentPerDelegat(
			Long entitatId,
			Long procedimentId) throws EntitatNotFoundException, ProcedimentNotFoundException {
		return getDelegateService().findPermesosAmbProcedimentPerDelegat(
				entitatId,
				procedimentId);
	}

    @Override
	@RolesAllowed("**")
    public List<ServeiDto> getServeiPermesosPerDelegat(Long entitatId, Long procedimentId, Authentication auth) throws EntitatNotFoundException, ProcedimentNotFoundException {
        return getDelegateService().getServeiPermesosPerDelegat(entitatId, procedimentId, auth);
    }

    @Override
	@RolesAllowed("PBL_ADMIN")
	public List<EmisorDto> findEmisorAll() {
		return getDelegateService().findEmisorAll();
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public List<ClauPublicaDto> findClauPublicaAll() {
		return getDelegateService().findClauPublicaAll();
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public List<ClauPrivadaDto> findClauPrivadaAll() {
		return getDelegateService().findClauPrivadaAll();
	}
	
	@Override
	@RolesAllowed("**")
	public ArbreDto<DadaEspecificaDto> generarArbreDadesEspecifiques(
			String serveiCodi) throws ServeiNotFoundException, ScspException {
		return getDelegateService().generarArbreDadesEspecifiques(serveiCodi);
	}


	@Override
	@RolesAllowed("PBL_ADMIN")
	public ServeiCampDto createServeiCamp(
			String serveiCodi,
			String path) throws ServeiNotFoundException {
		return getDelegateService().createServeiCamp(serveiCodi, path);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ServeiCampDto updateServeiCamp(
			ServeiCampDto modificat) throws ServeiCampNotFoundException {
		return getDelegateService().updateServeiCamp(modificat);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ServeiCampDto deleteServeiCamp(Long serveiCampId) throws ServeiCampNotFoundException {
		return getDelegateService().deleteServeiCamp(serveiCampId);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public void moveServeiCamp(
			String serveiCodi,
			Long serveiCampId,
			int indexDesti) throws ServeiCampNotFoundException {
		getDelegateService().moveServeiCamp(serveiCodi, serveiCampId, indexDesti);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public void agrupaServeiCamp(
			Long serveiCampId,
			Long serveiCampGrupId) throws ServeiCampNotFoundException, ServeiCampGrupNotFoundException {
		getDelegateService().agrupaServeiCamp(serveiCampId, serveiCampGrupId);
	}

	@Override
	@RolesAllowed("**")
	public List<ServeiCampDto> findServeiCamps(String serveiCodi) throws ServeiNotFoundException {
		return getDelegateService().findServeiCamps(serveiCodi);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public void marcarArrelResposta(String serveiCodi, String path) {
		getDelegateService().marcarArrelResposta(serveiCodi, path);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public void desmarcarArrelResposta(String serveiCodi) {
		getDelegateService().desmarcarArrelResposta(serveiCodi);
	}

	@Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPRES"})
	public String getArrelRespostaPath(String serveiCodi) {
		return getDelegateService().getArrelRespostaPath(serveiCodi);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ServeiCampGrupDto createServeiCampGrup(ServeiCampGrupDto serveiCampGrup) throws ServeiNotFoundException {
		return getDelegateService().createServeiCampGrup(serveiCampGrup);
	}

    @Override
	@RolesAllowed("PBL_ADMIN")
	public ServeiCampGrupDto updateServeiCampGrup(ServeiCampGrupDto serveiCampGrup) throws ServeiCampGrupNotFoundException {
		return getDelegateService().updateServeiCampGrup(serveiCampGrup);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ServeiCampGrupDto deleteServeiCampGrup(Long serveiCampGrupId) throws ServeiCampGrupNotFoundException {
		return getDelegateService().deleteServeiCampGrup(serveiCampGrupId);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public void moveServeiCampGrup(
			Long serveiCampGrupId,
			boolean up) throws ServeiCampGrupNotFoundException {
		getDelegateService().moveServeiCampGrup(serveiCampGrupId, up);
	}

	@Override
	@RolesAllowed("**")
	public List<ServeiCampGrupDto> findServeiCampGrups(String serveiCodi) throws ServeiNotFoundException {
		return getDelegateService().findServeiCampGrups(serveiCodi);
	}

	@Override
	@RolesAllowed("**")
	public List<ServeiCampGrupDto> findServeiCampGrupsAndSubgrups(String serveiCodi) throws ServeiNotFoundException {
		return getDelegateService().findServeiCampGrupsAndSubgrups(serveiCodi);
	}

    @Override
	@RolesAllowed("**")
    public ServeiCampGrupDto serveiCampGrupFindByNom(String serveiCodi, String nom) {
        return getDelegateService().serveiCampGrupFindByNom(serveiCodi, nom);
    }

    @Override
	@RolesAllowed("PBL_ADMIN")
	public ServeiBusDto createServeiBus(
			ServeiBusDto creat) throws ServeiNotFoundException, EntitatNotFoundException {
		return getDelegateService().createServeiBus(creat);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ServeiBusDto updateServeiBus(
			ServeiBusDto modificat) throws ServeiBusNotFoundException, EntitatNotFoundException {
		return getDelegateService().updateServeiBus(modificat);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ServeiBusDto deleteServeiBus(
			Long serveiBusId) throws ServeiBusNotFoundException {
		return getDelegateService().deleteServeiBus(serveiBusId);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ServeiBusDto findServeiBusById(Long id) throws ServeiBusNotFoundException {
		return getDelegateService().findServeiBusById(id);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public List<ServeiBusDto> findServeisBus(String serveiCodi) throws ServeiNotFoundException {
		return getDelegateService().findServeisBus(serveiCodi);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public void addServeiJustificantCamp(
			ServeiJustificantCampDto camp) throws ServeiNotFoundException {
		getDelegateService().addServeiJustificantCamp(camp);
	}

	@Override
	@RolesAllowed("**")
	public List<ServeiJustificantCampDto> findServeiJustificantCamps(
			String serveiCodi) throws ServeiNotFoundException {
		return getDelegateService().findServeiJustificantCamps(serveiCodi);
	}

	@Override
	@PermitAll
	public List<String> getRolsConfigurats() {
		return getDelegateService().getRolsConfigurats();
	}

	@Override
	@RolesAllowed({"PBL_ADMIN"})
	public List<ServeiXsdDto> xsdFindByServei(
			String codi) throws IOException, ServeiNotFoundException {
		return getDelegateService().xsdFindByServei(codi);
	}

	@Override
	@RolesAllowed({"PBL_ADMIN"})
	public void xsdDelete(
			String codi, XsdTipusEnumDto tipus) throws IOException {
		getDelegateService().xsdDelete(codi, tipus);
	}

	@Override
	@RolesAllowed({"PBL_ADMIN"})
	public FitxerDto xsdDescarregar(
			String codi, XsdTipusEnumDto tipus) throws IOException {
		return getDelegateService().xsdDescarregar(codi, tipus);
	}

	@Override
	@RolesAllowed({"PBL_ADMIN"})
	public void xsdCreate(String codi, ServeiXsdDto xsd, byte[] contingut) throws IOException {
		getDelegateService().xsdCreate(codi, xsd, contingut);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public void updateVersio(String codi) {
		getDelegateService().updateVersio(codi);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public void saveActiu(
			String serveiCodi,
			boolean actiu) {
		getDelegateService().saveActiu(
				serveiCodi,
				actiu);
	}
	
	@Override
	@RolesAllowed("PBL_ADMIN")
	public List<ServeiDto> findAll() {
		return getDelegateService().findAll();
	}

    @Override
	@RolesAllowed("PBL_ADMIN")
    public ServeiReglaDto serveiReglaFindByNom(Long serveiId, String nom) {
        return getDelegateService().serveiReglaFindByNom(serveiId, nom);
    }

    @Override
	@RolesAllowed("PBL_ADMIN")
    public ServeiReglaDto serveiReglaFindById(Long reglaId) {
        return getDelegateService().serveiReglaFindById(reglaId);
    }

    @Override
	@RolesAllowed("PBL_ADMIN")
    public ServeiReglaDto serveiReglaCreate(String serveiCodi, ServeiReglaDto reglaDto) throws ServeiNotFoundException {
        return getDelegateService().serveiReglaCreate(serveiCodi, reglaDto);
    }

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ServeiReglaDto serveiReglaUpdate(String serveiCodi, ServeiReglaDto reglaDto) throws ServeiNotFoundException {
		return getDelegateService().serveiReglaUpdate(serveiCodi, reglaDto);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public void serveiReglaDelete(String serveiCodi, Long reglaId) throws ServeiNotFoundException {
		getDelegateService().serveiReglaDelete(serveiCodi, reglaId);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public boolean serveiReglaMoure(Long reglaId, int posicio) {
		return getDelegateService().serveiReglaMoure(reglaId, posicio);
	}

    @Override
	@RolesAllowed({"PBL_ADMIN"})
    public List<ServeiReglaDto> serveiReglesFindAll(String serveiCodi) throws ServeiNotFoundException {
        return getDelegateService().serveiReglesFindAll(serveiCodi);
    }

    @Override
	@RolesAllowed("**")
    public List<Long> findCampIdsByReglesServei(String serveiCodi) throws ServeiNotFoundException {
        return getDelegateService().findCampIdsByReglesServei(serveiCodi);
    }

	@Override
	@RolesAllowed("**")
	public List<Long> findGrupIdsByReglesServei(String serveiCodi) throws ServeiNotFoundException {
		return getDelegateService().findGrupIdsByReglesServei(serveiCodi);
	}

    @Override
	@RolesAllowed("**")
    public List<CampFormProperties> getCampsByserveiRegla(String serveiCodi, String[] campsModificats) throws ServeiNotFoundException {
        return getDelegateService().getCampsByserveiRegla(serveiCodi, campsModificats);
    }

	@Override
	@RolesAllowed("**")
	public List<CampFormProperties> getGrupsByserveiRegla(String serveiCodi, String[] grupsModificats) throws ServeiNotFoundException {
		return getDelegateService().getGrupsByserveiRegla(serveiCodi, grupsModificats);
	}

}
