/**
 * 
 */
package es.caib.pinbal.ejb;

import es.caib.pinbal.client.dadesobertes.DadesObertesResposta;
import es.caib.pinbal.client.dadesobertes.DadesObertesRespostaConsulta;
import es.caib.pinbal.logic.intf.dto.*;
import es.caib.pinbal.logic.intf.dto.arxiu.ArxiuDetallDto;
import es.caib.pinbal.logic.intf.service.exception.*;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Implementació de ConsultaService que empra una clase delegada per accedir a la
 * funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Primary
@Stateless
public class HistoricConsultaServiceBean extends AbstractService<es.caib.pinbal.logic.intf.service.HistoricConsultaService> implements es.caib.pinbal.logic.intf.service.HistoricConsultaService {

    @Override
	@RolesAllowed("**")
    public ArxiuDetallDto obtenirArxiuInfo(Long consultaId) {
        return getDelegateService().obtenirArxiuInfo(consultaId);
    }

    @Override
	@RolesAllowed("**")
	public JustificantDto obtenirJustificant(
			Long id,
			boolean isAdmin) throws ConsultaNotFoundException, JustificantGeneracioException {
		return getDelegateService().obtenirJustificant(id, isAdmin);
	}

	@Override
	@RolesAllowed("PBL_WS")
	public JustificantDto obtenirJustificant(
			String idpeticion,
			String idsolicitud,
			boolean versioImprimible,
			boolean ambContingut) throws ConsultaNotFoundException, JustificantGeneracioException {
		return getDelegateService().obtenirJustificant(idpeticion, idsolicitud, versioImprimible, ambContingut);
	}

	@Override
	@RolesAllowed("**")
	public FitxerDto obtenirJustificantMultipleConcatenat(
			Long id) throws ConsultaNotFoundException, JustificantGeneracioException {
		return getDelegateService().obtenirJustificantMultipleConcatenat(id);
	}

	@Override
	@RolesAllowed("**")
	public FitxerDto obtenirJustificantMultipleZip(
			Long id) throws ConsultaNotFoundException, JustificantGeneracioException {
		return getDelegateService().obtenirJustificantMultipleZip(id);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public FitxerDto descarregarXmlTokensZip(Long id) throws ConsultaNotFoundException {
		return getDelegateService().descarregarXmlTokensZip(id);
	}

	@Override
	@RolesAllowed("**")
	public JustificantDto reintentarGeneracioJustificant(
			Long id,
			boolean descarregar) throws ConsultaNotFoundException, JustificantGeneracioException {
		return getDelegateService().reintentarGeneracioJustificant(id, descarregar);
	}
	
	@Override
	@RolesAllowed("**")
	public Page<ConsultaDto> findSimplesByFiltrePaginatPerDelegat(
			Long entitatId,
			ConsultaFiltreDto filtre,
			Pageable pageable) throws EntitatNotFoundException {
		return getDelegateService().findSimplesByFiltrePaginatPerDelegat(
				entitatId,
				filtre,
				pageable);
	}

	@Override
	@RolesAllowed("**")
	public Page<ConsultaDto> findMultiplesByFiltrePaginatPerDelegat(
			Long entitatId,
			ConsultaFiltreDto filtre,
			Pageable pageable) throws EntitatNotFoundException {
		return getDelegateService().findMultiplesByFiltrePaginatPerDelegat(
				entitatId,
				filtre,
				pageable);
	}

	@Override
	@RolesAllowed("PBL_AUDIT")
	public Page<ConsultaDto> findByFiltrePaginatPerAuditor(
			Long entitatId,
			ConsultaFiltreDto filtre,
			Pageable pageable) throws EntitatNotFoundException {
		return getDelegateService().findByFiltrePaginatPerAuditor(
				entitatId,
				filtre,
				pageable);
	}

    @Override
	@RolesAllowed("PBL_AUDIT")
    public List<ConsultaDto> findByFiltrePerAuditor(Long entitatId, ConsultaFiltreDto filtre) throws EntitatNotFoundException {
        return getDelegateService().findByFiltrePerAuditor(entitatId, filtre);
    }

    @Override
	@RolesAllowed("PBL_SUPERAUD")
	public Page<ConsultaDto> findByFiltrePaginatPerSuperauditor(
			Long entitatId,
			ConsultaFiltreDto filtre,
			Pageable pageable) throws EntitatNotFoundException {
		return getDelegateService().findByFiltrePaginatPerSuperauditor(
				entitatId,
				filtre,
				pageable);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public Page<ConsultaDto> findByFiltrePaginatPerAdmin(
			ConsultaFiltreDto filtre,
			Pageable pageable) throws EntitatNotFoundException {
		return getDelegateService().findByFiltrePaginatPerAdmin(filtre, pageable);
	}

	@Override
//	@RolesAllowed("PBL_REPORT")
	public List<DadesObertesRespostaConsulta> findByFiltrePerOpenData(
			String entitatCodi,
			Date dataInici,
			Date dataFi,
			String procedimentCodi,
			String serveiCodi) throws EntitatNotFoundException, ProcedimentNotFoundException {
		return getDelegateService().findByFiltrePerOpenData(
				entitatCodi,
				dataInici,
				dataFi,
				procedimentCodi,
				serveiCodi);
	}

    @Override
    public DadesObertesResposta findByFiltrePerOpenDataV2(ConsultaOpenDataDto consultaOpenDataDto) throws ProcedimentNotFoundException, EntitatNotFoundException {
        return getDelegateService().findByFiltrePerOpenDataV2(consultaOpenDataDto);
    }

    @Override
	@RolesAllowed("**")
	public ConsultaDto findOneDelegat(
			Long id) throws ConsultaNotFoundException, ScspException {
		return getDelegateService().findOneDelegat(id);
	}

	@Override
	@RolesAllowed("PBL_AUDIT")
	public ConsultaDto findOneAuditor(
			Long id) throws ConsultaNotFoundException, ScspException {
		return getDelegateService().findOneAuditor(id);
	}

	@Override
	@RolesAllowed("PBL_SUPERAUD")
	public ConsultaDto findOneSuperauditor(
			Long id) throws ConsultaNotFoundException, ScspException {
		return getDelegateService().findOneSuperauditor(id);
	}

	@Override
	@RolesAllowed("PBL_ADMIN")
	public ConsultaDto findOneAdmin(Long id) throws ConsultaNotFoundException, ScspException {
		return getDelegateService().findOneAdmin(id);
	}

	@Override
	@RolesAllowed("**")
	public List<ConsultaDto> findAmbPare(
			Long pareId) throws ConsultaNotFoundException, ScspException {
		return getDelegateService().findAmbPare(pareId);
	}

	@Override
	@RolesAllowed("PBL_AUDIT")
	public List<Long> auditoriaGenerarAuditor(
			Long entitatId,
			Date dataInici,
			Date dataFi,
			int numConsultes) throws EntitatNotFoundException {
		return getDelegateService().auditoriaGenerarAuditor(
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
		return getDelegateService().auditoriaConsultarAuditor(
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
		return getDelegateService().auditoriaGenerarSuperauditor(
				dataInici,
				dataFi,
				numEntitats,
				numConsultes);
	}

	@Override
	@RolesAllowed("PBL_SUPERAUD")
	public Map<EntitatDto, List<ConsultaDto>> auditoriaConsultarSuperauditor(
			List<Long> consultaIds) throws ScspException {
		return getDelegateService().auditoriaConsultarSuperauditor(consultaIds);
	}

	@Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPORT"})
	public List<InformeGeneralEstatDto> informeGeneralEstat(Date dataInici, Date dataFi) {
		return getDelegateService().informeGeneralEstat(dataInici, dataFi);
	}

	@Override
	@RolesAllowed({"PBL_ADMIN", "PBL_REPORT"})
	public List<CarregaDto> findEstadistiquesCarrega() {
		return getDelegateService().findEstadistiquesCarrega();
	}

	@Override
	public void arxivarConsultesAntigues() {
		getDelegateService().arxivarConsultesAntigues();
	}

}
