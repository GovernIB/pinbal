/**
 * 
 */
package es.caib.pinbal.core.ejb;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.pinbal.core.dto.ArxiuDto;
import es.caib.pinbal.core.dto.ConsultaDto;
import es.caib.pinbal.core.dto.ConsultaFiltreDto;
import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.EstadisticaDto;
import es.caib.pinbal.core.dto.EstadistiquesFiltreDto;
import es.caib.pinbal.core.dto.InformeGeneralEstatDto;
import es.caib.pinbal.core.dto.PaginaLlistatDto;
import es.caib.pinbal.core.dto.PaginacioAmbOrdreDto;
import es.caib.pinbal.core.dto.RecobrimentSolicitudDto;
import es.caib.pinbal.core.service.ConsultaService;
import es.caib.pinbal.core.service.exception.ConsultaNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.JustificantGeneracioException;
import es.caib.pinbal.core.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentServeiNotFoundException;
import es.caib.pinbal.core.service.exception.ScspException;
import es.caib.pinbal.core.service.exception.ServeiNotAllowedException;
import es.caib.pinbal.core.service.exception.ValidacioDadesPeticioException;

/**
 * Implementació de ConsultaService que empra una clase delegada per accedir a la
 * funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ConsultaServiceBean implements ConsultaService {

	@Autowired
	ConsultaService delegate;



	@Override
	@RolesAllowed("tothom")
	public ConsultaDto novaConsulta(
			ConsultaDto consulta) throws ProcedimentServeiNotFoundException, ServeiNotAllowedException, ScspException {
		return delegate.novaConsulta(consulta);
	}

	@Override
	@RolesAllowed("tothom")
	public ConsultaDto novaConsultaInit(
			ConsultaDto consulta) throws ProcedimentServeiNotFoundException, ServeiNotAllowedException, ScspException {
		return delegate.novaConsultaInit(consulta);
	}

	@Override
	@RolesAllowed("tothom")
	public void novaConsultaEnviament(
			Long consultaId,
			ConsultaDto consulta) throws ConsultaNotFoundException, ScspException {
		delegate.novaConsultaEnviament(consultaId, consulta);
	}

	@Override
	@RolesAllowed("tothom")
	public ConsultaDto novaConsultaEstat(
			Long consultaId) throws ScspException {
		return delegate.novaConsultaEstat(consultaId);
	}

	@Override
	@RolesAllowed("tothom")
	public ConsultaDto novaConsultaMultiple(
			ConsultaDto consulta) throws ValidacioDadesPeticioException, ProcedimentServeiNotFoundException, ServeiNotAllowedException, ScspException {
		return delegate.novaConsultaMultiple(consulta);
	}

	@Override
	@RolesAllowed("PBL_WS")
	public ConsultaDto novaConsultaRecobriment(
			String serveiCodi,
			RecobrimentSolicitudDto solicitud) throws EntitatNotFoundException, ProcedimentNotFoundException, ProcedimentServeiNotFoundException, ServeiNotAllowedException, ScspException {
		return delegate.novaConsultaRecobriment(
				serveiCodi,
				solicitud);
	}

	@Override
	@RolesAllowed("PBL_WS")
	public ConsultaDto novaConsultaRecobrimentInit(
			String serveiCodi,
			RecobrimentSolicitudDto solicitud) throws EntitatNotFoundException, ProcedimentNotFoundException, ProcedimentServeiNotFoundException, ServeiNotAllowedException, ScspException {
		return delegate.novaConsultaRecobrimentInit(
				serveiCodi,
				solicitud);
	}
	@Override
	@RolesAllowed("PBL_WS")
	public void novaConsultaRecobrimentEnviament(
			Long consultaId,
			RecobrimentSolicitudDto solicitud) throws ConsultaNotFoundException, ScspException {
		delegate.novaConsultaRecobrimentEnviament(
				consultaId,
				solicitud);
	}
	@Override
	@RolesAllowed("PBL_WS")
	public ConsultaDto novaConsultaRecobrimentEstat(
			Long consultaId) throws ScspException {
		return delegate.novaConsultaRecobrimentEstat(consultaId);
	}

	@Override
	@RolesAllowed("PBL_WS")
	public ConsultaDto novaConsultaRecobrimentMultiple(
			String serveiCodi,
			List<RecobrimentSolicitudDto> solicituds) throws EntitatNotFoundException, ProcedimentNotFoundException, ProcedimentServeiNotFoundException, ServeiNotAllowedException, ScspException {
		return delegate.novaConsultaRecobrimentMultiple(
				serveiCodi,
				solicituds);
	}

	@Override
	@RolesAllowed("tothom")
	public ArxiuDto obtenirJustificant(
			Long id) throws ConsultaNotFoundException, JustificantGeneracioException {
		return delegate.obtenirJustificant(id);
	}

	@Override
	@RolesAllowed("tothom")
	public ArxiuDto obtenirJustificantMultipleConcatenat(
			Long id) throws ConsultaNotFoundException, JustificantGeneracioException {
		return delegate.obtenirJustificantMultipleConcatenat(id);
	}

	@Override
	@RolesAllowed("tothom")
	public ArxiuDto obtenirJustificantMultipleZip(
			Long id) throws ConsultaNotFoundException, JustificantGeneracioException {
		return delegate.obtenirJustificantMultipleZip(id);
	}

	@Override
	@RolesAllowed("tothom")
	public PaginaLlistatDto<ConsultaDto> findSimplesByFiltrePaginatPerDelegat(
			Long entitatId,
			ConsultaFiltreDto filtre,
			PaginacioAmbOrdreDto paginacioAmbOrdre) throws EntitatNotFoundException {
		return delegate.findSimplesByFiltrePaginatPerDelegat(
				entitatId,
				filtre,
				paginacioAmbOrdre);
	}

	@Override
	@RolesAllowed("tothom")
	public PaginaLlistatDto<ConsultaDto> findMultiplesByFiltrePaginatPerDelegat(
			Long entitatId,
			ConsultaFiltreDto filtre,
			PaginacioAmbOrdreDto paginacioAmbOrdre) throws EntitatNotFoundException {
		return delegate.findMultiplesByFiltrePaginatPerDelegat(
				entitatId,
				filtre,
				paginacioAmbOrdre);
	}

	@Override
	@RolesAllowed("PBL_AUDIT")
	public PaginaLlistatDto<ConsultaDto> findByFiltrePaginatPerAuditor(
			Long entitatId,
			ConsultaFiltreDto filtre,
			PaginacioAmbOrdreDto paginacioAmbOrdre) throws EntitatNotFoundException {
		return delegate.findByFiltrePaginatPerAuditor(
				entitatId,
				filtre,
				paginacioAmbOrdre);
	}

	@Override
	@RolesAllowed("PBL_SUPERAUD")
	public PaginaLlistatDto<ConsultaDto> findByFiltrePaginatPerSuperauditor(
			Long entitatId,
			ConsultaFiltreDto filtre,
			PaginacioAmbOrdreDto paginacioAmbOrdre) throws EntitatNotFoundException {
		return delegate.findByFiltrePaginatPerSuperauditor(
				entitatId,
				filtre,
				paginacioAmbOrdre);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public PaginaLlistatDto<ConsultaDto> findByFiltrePaginatPerAdmin(
			Long entitatId,
			ConsultaFiltreDto filtre,
			PaginacioAmbOrdreDto paginacioAmbOrdre) throws EntitatNotFoundException {
	return delegate.findByFiltrePaginatPerAdmin(entitatId, filtre, paginacioAmbOrdre);
	}

	@Override
	@RolesAllowed("tothom")
	public ConsultaDto findOneDelegat(
			Long id) throws ConsultaNotFoundException, ScspException {
		return delegate.findOneDelegat(id);
	}

	@Override
	@RolesAllowed("PBL_AUDIT")
	public ConsultaDto findOneAuditor(
			Long id) throws ConsultaNotFoundException, ScspException {
		return delegate.findOneAuditor(id);
	}

	@Override
	@RolesAllowed("PBL_SUPERAUD")
	public ConsultaDto findOneSuperauditor(
			Long id) throws ConsultaNotFoundException, ScspException {
		return delegate.findOneSuperauditor(id);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ConsultaDto findOneAdmin(Long id) throws ConsultaNotFoundException, ScspException {
		return delegate.findOneAdmin(id);
	}

	@Override
	@RolesAllowed("tothom")
	public List<ConsultaDto> findAmbPare(
			Long pareId) throws ConsultaNotFoundException, ScspException {
		return delegate.findAmbPare(pareId);
	}

	@Override
	@RolesAllowed("tothom")
	public long countConsultesMultiplesProcessant(
			Long entitatId) throws EntitatNotFoundException {
		return delegate.countConsultesMultiplesProcessant(entitatId);
	}

	@Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPRES"})
	public List<EstadisticaDto> findEstadistiquesByFiltre(
			EstadistiquesFiltreDto filtre) throws EntitatNotFoundException {
		return delegate.findEstadistiquesByFiltre(filtre);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public Map<EntitatDto, List<EstadisticaDto>> findEstadistiquesGlobalsByFiltre(
			EstadistiquesFiltreDto filtre) {
		return delegate.findEstadistiquesGlobalsByFiltre(filtre);
	}

	@Override
	@RolesAllowed("PBL_AUDIT")
	public List<Long> auditoriaGenerarAuditor(
			Long entitatId,
			Date dataInici,
			Date dataFi,
			int numConsultes) throws EntitatNotFoundException {
		return delegate.auditoriaGenerarAuditor(
				entitatId,
				dataInici,
				dataFi,
				numConsultes);
	}

	@Override
	@RolesAllowed("PBL_AUDIT")
	public List<ConsultaDto> auditoriaConsultarAuditor(
			Long entitatId,
			List<Long> consultaIds) throws EntitatNotFoundException, ScspException {
		return delegate.auditoriaConsultarAuditor(
				entitatId,
				consultaIds);
	}

	@Override
	@RolesAllowed("PBL_SUPERAUD")
	public List<Long> auditoriaGenerarSuperauditor(
			Date dataInici,
			Date dataFi,
			int numEntitats,
			int numConsultes) {
		return delegate.auditoriaGenerarSuperauditor(
				dataInici,
				dataFi,
				numEntitats,
				numConsultes);
	}

	@Override
	@RolesAllowed("PBL_SUPERAUD")
	public Map<EntitatDto, List<ConsultaDto>> auditoriaConsultarSuperauditor(
			List<Long> consultaIds) throws ScspException {
		return delegate.auditoriaConsultarSuperauditor(consultaIds);
	}

	@Override
	public void revisarEstatPeticionsMultiplesPendents() {
		delegate.revisarEstatPeticionsMultiplesPendents();
	}

	@Override
	@RolesAllowed("tothom")
	public boolean isOptimitzarTransaccionsNovaConsulta() {
		return delegate.isOptimitzarTransaccionsNovaConsulta();
	}
	
	@Override
	@RolesAllowed("PBL_ADMIN")
	public List<InformeGeneralEstatDto> informeGeneralEstat() {
		return delegate.informeGeneralEstat();
	}

}
