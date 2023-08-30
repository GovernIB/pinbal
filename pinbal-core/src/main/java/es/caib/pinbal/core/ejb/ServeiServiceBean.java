/**
 * 
 */
package es.caib.pinbal.core.ejb;

import es.caib.pinbal.core.dto.ArbreDto;
import es.caib.pinbal.core.dto.ClauPrivadaDto;
import es.caib.pinbal.core.dto.ClauPublicaDto;
import es.caib.pinbal.core.dto.DadaEspecificaDto;
import es.caib.pinbal.core.dto.EmisorDto;
import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.FitxerDto;
import es.caib.pinbal.core.dto.ProcedimentDto;
import es.caib.pinbal.core.dto.ProcedimentServeiDto;
import es.caib.pinbal.core.dto.ServeiBusDto;
import es.caib.pinbal.core.dto.ServeiCampDto;
import es.caib.pinbal.core.dto.ServeiCampGrupDto;
import es.caib.pinbal.core.dto.ServeiDto;
import es.caib.pinbal.core.dto.ServeiJustificantCampDto;
import es.caib.pinbal.core.dto.ServeiXsdDto;
import es.caib.pinbal.core.dto.XsdTipusEnumDto;
import es.caib.pinbal.core.dto.regles.CampFormProperties;
import es.caib.pinbal.core.dto.regles.ServeiReglaDto;
import es.caib.pinbal.core.service.ServeiService;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.core.service.exception.ScspException;
import es.caib.pinbal.core.service.exception.ServeiAmbConsultesException;
import es.caib.pinbal.core.service.exception.ServeiBusNotFoundException;
import es.caib.pinbal.core.service.exception.ServeiCampGrupNotFoundException;
import es.caib.pinbal.core.service.exception.ServeiCampNotFoundException;
import es.caib.pinbal.core.service.exception.ServeiNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.io.IOException;
import java.util.List;

/**
 * Implementació de ServeiService que empra una clase delegada per accedir a la
 * funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ServeiServiceBean implements ServeiService {

	@Autowired
	ServeiService delegate;



	@Override
	@RolesAllowed("PBL_ADMIN")
	public ServeiDto save(ServeiDto servei) {
		return delegate.save(servei);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ServeiDto delete(
			String serveiCodi) throws ServeiNotFoundException, ServeiAmbConsultesException {
		return delegate.delete(serveiCodi);
	}

	@Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPRES"})
	public ServeiDto findAmbCodiPerAdminORepresentant(
			String codi) throws ServeiNotFoundException {
		return delegate.findAmbCodiPerAdminORepresentant(codi);
	}

	@Override
	@RolesAllowed("tothom")
	public ServeiDto findAmbCodiPerDelegat(
			Long entitatId,
			String codi) throws ServeiNotFoundException {
		return delegate.findAmbCodiPerDelegat(entitatId, codi);
	}

	@Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPRES"})
	public ServeiDto findById(Long id) {
		return delegate.findById(id);
	}
	
	@Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPORT"})
	public List<ServeiDto> findActius() {
		return delegate.findActius();
	}
	
	@Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPORT"})
	public List<ServeiDto> findActius(String filtre) {
		return delegate.findActius(filtre);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public Page<ServeiDto> findAmbFiltrePaginat(String codi, String descripcio, String emisor,
			Boolean activa, String scspVersionEsquema, Pageable pageable) {
		return delegate.findAmbFiltrePaginat(codi, descripcio, emisor, activa, scspVersionEsquema, pageable);
	}
	
	@Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPRES"})
	public Page<ServeiDto> findAmbFiltrePaginat(
			String codi,
			String descripcio,
			String emisor,
			Boolean activa,
			EntitatDto entitat,
			ProcedimentDto procediment,
			Pageable pageable) {
		return delegate.findAmbFiltrePaginat(codi, descripcio, emisor, activa, entitat, procediment, pageable);
	}
	
	@Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPRES", "PBL_AUDIT", "PBL_SUPERAUD"})
	public List<ServeiDto> findAmbEntitat(Long entitatId)
			throws EntitatNotFoundException {
		return delegate.findAmbEntitat(entitatId);
	}
	
	@Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPRES", "PBL_AUDIT", "PBL_SUPERAUD"})
	public List<ServeiDto> findAmbEntitat(Long entitatId, String filtre)
			throws EntitatNotFoundException {
		return delegate.findAmbEntitat(entitatId, filtre);
	}

	@Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPRES", "PBL_AUDIT", "PBL_SUPERAUD"})
	public List<ServeiDto> findAmbEntitatIProcediment(
			Long entitatId,
			Long procedimentId) throws EntitatNotFoundException, ProcedimentNotFoundException {
		return delegate.findAmbEntitatIProcediment(entitatId, procedimentId);
	}
	
	@Override
	@RolesAllowed("PBL_ADMIN")
	public List<ServeiDto> findAmbProcediment(
			Long procedimentId) throws ProcedimentNotFoundException {
		return delegate.findAmbProcediment(procedimentId);
	}
	
	@Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPRES", "PBL_AUDIT", "PBL_SUPERAUD"})
	public List<ServeiDto> findAmbEntitatIProcediment(
			Long entitatId,
			Long procedimentId,
			String filtre) throws EntitatNotFoundException, ProcedimentNotFoundException {
		return delegate.findAmbEntitatIProcediment(entitatId, procedimentId, filtre);
	}

	@Override
	@RolesAllowed("PBL_REPRES")
	public List<ProcedimentServeiDto> findPermesosAmbEntitatIUsuari(
			Long entitatId,
			String usuariCodi) throws EntitatNotFoundException {
		return delegate.findPermesosAmbEntitatIUsuari(entitatId, usuariCodi);
	}

    @Override
	@RolesAllowed("PBL_REPRES")
    public Integer countPermesosAmbEntitatIUsuari(Long entitatId, String usuariCodi) {
        return delegate.countPermesosAmbEntitatIUsuari(entitatId, usuariCodi);
    }

    @Override
	@RolesAllowed("tothom")
	public List<ServeiDto> findPermesosAmbProcedimentPerDelegat(
			Long entitatId,
			Long procedimentId) throws EntitatNotFoundException, ProcedimentNotFoundException {
		return delegate.findPermesosAmbProcedimentPerDelegat(
				entitatId,
				procedimentId);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public List<EmisorDto> findEmisorAll() {
		return delegate.findEmisorAll();
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public List<ClauPublicaDto> findClauPublicaAll() {
		return delegate.findClauPublicaAll();
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public List<ClauPrivadaDto> findClauPrivadaAll() {
		return delegate.findClauPrivadaAll();
	}
	
	@Override
	@RolesAllowed({"PBL_ADMIN", "tothom"})
	public ArbreDto<DadaEspecificaDto> generarArbreDadesEspecifiques(
			String serveiCodi) throws ServeiNotFoundException, ScspException {
		return delegate.generarArbreDadesEspecifiques(serveiCodi);
	}


	@Override
	@RolesAllowed("PBL_ADMIN")
	public ServeiCampDto createServeiCamp(
			String serveiCodi,
			String path) throws ServeiNotFoundException {
		return delegate.createServeiCamp(serveiCodi, path);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ServeiCampDto updateServeiCamp(
			ServeiCampDto modificat) throws ServeiCampNotFoundException {
		return delegate.updateServeiCamp(modificat);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ServeiCampDto deleteServeiCamp(Long serveiCampId) throws ServeiCampNotFoundException {
		return delegate.deleteServeiCamp(serveiCampId);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public void moveServeiCamp(
			String serveiCodi,
			Long serveiCampId,
			int indexDesti) throws ServeiCampNotFoundException {
		delegate.moveServeiCamp(serveiCodi, serveiCampId, indexDesti);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public void agrupaServeiCamp(
			Long serveiCampId,
			Long serveiCampGrupId) throws ServeiCampNotFoundException, ServeiCampGrupNotFoundException {
		delegate.agrupaServeiCamp(serveiCampId, serveiCampGrupId);
	}

	@Override
	@RolesAllowed({"PBL_ADMIN", "tothom"})
	public List<ServeiCampDto> findServeiCamps(String serveiCodi) throws ServeiNotFoundException {
		return delegate.findServeiCamps(serveiCodi);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ServeiCampGrupDto createServeiCampGrup(ServeiCampGrupDto serveiCampGrup) throws ServeiNotFoundException {
		return delegate.createServeiCampGrup(serveiCampGrup);
	}

    @Override
	@RolesAllowed("PBL_ADMIN")
	public ServeiCampGrupDto updateServeiCampGrup(ServeiCampGrupDto serveiCampGrup) throws ServeiCampGrupNotFoundException {
		return delegate.updateServeiCampGrup(serveiCampGrup);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ServeiCampGrupDto deleteServeiCampGrup(Long serveiCampGrupId) throws ServeiCampGrupNotFoundException {
		return delegate.deleteServeiCampGrup(serveiCampGrupId);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public void moveServeiCampGrup(
			Long serveiCampGrupId,
			boolean up) throws ServeiCampGrupNotFoundException {
		delegate.moveServeiCampGrup(serveiCampGrupId, up);
	}

	@Override
	@RolesAllowed({"PBL_ADMIN", "tothom"})
	public List<ServeiCampGrupDto> findServeiCampGrups(String serveiCodi) throws ServeiNotFoundException {
		return delegate.findServeiCampGrups(serveiCodi);
	}

	@Override
	@RolesAllowed({"PBL_ADMIN", "tothom"})
	public List<ServeiCampGrupDto> findServeiCampGrupsAndSubgrups(String serveiCodi) throws ServeiNotFoundException {
		return delegate.findServeiCampGrupsAndSubgrups(serveiCodi);
	}

    @Override
	@RolesAllowed({"PBL_ADMIN", "tothom"})
    public ServeiCampGrupDto serveiCampGrupFindByNom(Long serveiId, String nom) {
        return delegate.serveiCampGrupFindByNom(serveiId, nom);
    }

    @Override
	@RolesAllowed("PBL_ADMIN")
	public ServeiBusDto createServeiBus(
			ServeiBusDto creat) throws ServeiNotFoundException, EntitatNotFoundException {
		return delegate.createServeiBus(creat);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ServeiBusDto updateServeiBus(
			ServeiBusDto modificat) throws ServeiBusNotFoundException, EntitatNotFoundException {
		return delegate.updateServeiBus(modificat);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ServeiBusDto deleteServeiBus(
			Long serveiBusId) throws ServeiBusNotFoundException {
		return delegate.deleteServeiBus(serveiBusId);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ServeiBusDto findServeiBusById(Long id) throws ServeiBusNotFoundException {
		return delegate.findServeiBusById(id);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public List<ServeiBusDto> findServeisBus(String serveiCodi) throws ServeiNotFoundException {
		return delegate.findServeisBus(serveiCodi);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public void addServeiJustificantCamp(
			ServeiJustificantCampDto camp) throws ServeiNotFoundException {
		delegate.addServeiJustificantCamp(camp);
	}

	@Override
	@RolesAllowed({"PBL_ADMIN", "tothom"})
	public List<ServeiJustificantCampDto> findServeiJustificantCamps(
			String serveiCodi) throws ServeiNotFoundException {
		return delegate.findServeiJustificantCamps(serveiCodi);
	}

	@Override
	@PermitAll
	public List<String> getRolsConfigurats() {
		return delegate.getRolsConfigurats();
	}

	@Override
	@RolesAllowed({"PBL_ADMIN"})
	public List<ServeiXsdDto> xsdFindByServei(
			String codi) throws IOException, ServeiNotFoundException {
		return delegate.xsdFindByServei(codi);
	}

	@Override
	public void xsdDelete(
			String codi, XsdTipusEnumDto tipus) throws IOException {
		delegate.xsdDelete(codi, tipus);
	}

	@Override
	public FitxerDto xsdDescarregar(
			String codi, XsdTipusEnumDto tipus) throws IOException {
		return delegate.xsdDescarregar(codi, tipus);
	}

	@Override
	public void xsdCreate(String codi, ServeiXsdDto xsd, byte[] contingut) throws IOException {
		delegate.xsdCreate(codi, xsd, contingut);
	}

	@Override
	public void saveActiu(
			String serveiCodi,
			boolean actiu) {
		delegate.saveActiu(
				serveiCodi,
				actiu);
	}
	
	@Override
	@RolesAllowed("PBL_ADMIN")
	public List<ServeiDto> findAll() {
		return delegate.findAll();
	}

    @Override
	@RolesAllowed("PBL_ADMIN")
    public ServeiReglaDto serveiReglaFindByNom(Long serveiId, String nom) {
        return delegate.serveiReglaFindByNom(serveiId, nom);
    }

    @Override
	@RolesAllowed("PBL_ADMIN")
    public ServeiReglaDto serveiReglaFindById(Long reglaId) {
        return delegate.serveiReglaFindById(reglaId);
    }

    @Override
	@RolesAllowed("PBL_ADMIN")
    public ServeiReglaDto serveiReglaCreate(String serveiCodi, ServeiReglaDto reglaDto) throws ServeiNotFoundException {
        return delegate.serveiReglaCreate(serveiCodi, reglaDto);
    }

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ServeiReglaDto serveiReglaUpdate(String serveiCodi, ServeiReglaDto reglaDto) throws ServeiNotFoundException {
		return delegate.serveiReglaUpdate(serveiCodi, reglaDto);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public void serveiReglaDelete(String serveiCodi, Long reglaId) throws ServeiNotFoundException {
		delegate.serveiReglaDelete(serveiCodi, reglaId);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public boolean serveiReglaMoure(Long reglaId, int posicio) {
		return delegate.serveiReglaMoure(reglaId, posicio);
	}

    @Override
	@RolesAllowed({"PBL_ADMIN"})
    public List<ServeiReglaDto> serveiReglesFindAll(String serveiCodi) throws ServeiNotFoundException {
        return delegate.serveiReglesFindAll(serveiCodi);
    }

    @Override
	@RolesAllowed({"PBL_ADMIN", "tothom"})
    public List<Long> findCampIdsByReglesServei(String serveiCodi) throws ServeiNotFoundException {
        return delegate.findCampIdsByReglesServei(serveiCodi);
    }

	@Override
	@RolesAllowed({"PBL_ADMIN", "tothom"})
	public List<Long> findGrupIdsByReglesServei(String serveiCodi) throws ServeiNotFoundException {
		return delegate.findGrupIdsByReglesServei(serveiCodi);
	}

    @Override
	@RolesAllowed({"PBL_ADMIN", "tothom"})
    public List<CampFormProperties> getCampsByserveiRegla(String serveiCodi, String[] campsModificats) throws ServeiNotFoundException {
        return delegate.getCampsByserveiRegla(serveiCodi, campsModificats);
    }

	@Override
	@RolesAllowed({"PBL_ADMIN", "tothom"})
	public List<CampFormProperties> getGrupsByserveiRegla(String serveiCodi, String[] grupsModificats) throws ServeiNotFoundException {
		return delegate.getGrupsByserveiRegla(serveiCodi, grupsModificats);
	}

}
