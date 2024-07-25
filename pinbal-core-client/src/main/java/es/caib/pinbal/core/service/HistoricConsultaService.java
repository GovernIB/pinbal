/**
 * 
 */
package es.caib.pinbal.core.service;

import es.caib.pinbal.client.dadesobertes.DadesObertesResposta;
import es.caib.pinbal.client.dadesobertes.DadesObertesRespostaConsulta;
import es.caib.pinbal.core.dto.CarregaDto;
import es.caib.pinbal.core.dto.ConsultaDto;
import es.caib.pinbal.core.dto.ConsultaFiltreDto;
import es.caib.pinbal.core.dto.ConsultaOpenDataDto;
import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.FitxerDto;
import es.caib.pinbal.core.dto.InformeGeneralEstatDto;
import es.caib.pinbal.core.dto.InformeProcedimentServeiDto;
import es.caib.pinbal.core.dto.InformeRepresentantFiltreDto;
import es.caib.pinbal.core.dto.JustificantDto;
import es.caib.pinbal.core.dto.arxiu.ArxiuDetallDto;
import es.caib.pinbal.core.service.exception.ConsultaNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.JustificantGeneracioException;
import es.caib.pinbal.core.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.core.service.exception.ScspException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Declaració dels mètodes per a gestionar les consultes a SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface HistoricConsultaService {

	@PreAuthorize("hasRole('ROLE_DELEG') or hasRole('ROLE_ADMIN')")
	ArxiuDetallDto obtenirArxiuInfo(Long consultaId);

	/**
	 * Obté el justificant de la consulta.
	 *
	 * @param id
	 *            Atribut id de la consulta.
	 * @param isAdmin
	 *            Indica si aquesta petició es fa com a administrador.
	 * @return l'arxiu amb el document generat.
	 * @throws ConsultaNotFoundException
	 *            Si la consulta no és accessible per aquest usuari.
	 * @throws JustificantGeneracioException
	 *            Si es produeixen errors al generar el justificant.
	 */
	@PreAuthorize("hasRole('ROLE_DELEG')")
	public JustificantDto obtenirJustificant(
            Long id,
            boolean isAdmin) throws ConsultaNotFoundException, JustificantGeneracioException;

	/**
	 * Obté el justificant de la consulta.
	 *
	 * @param idpeticion
	 *            Identificador de la petició SCSP.
	 * @param idsolicitud
	 *            Identificador de la sol·licitud SCSP.
	 * @return l'arxiu amb el document generat.
	 * @throws ConsultaNotFoundException
	 *            Si la consulta no és accessible per aquest usuari.
	 * @throws JustificantGeneracioException
	 *            Si es produeixen errors al generar el justificant.
	 */
	@PreAuthorize("hasRole('ROLE_WS')")
	public JustificantDto obtenirJustificant(
            String idpeticion,
            String idsolicitud,
			boolean versioImprimible,
			boolean ambContingut) throws ConsultaNotFoundException, JustificantGeneracioException;

	/**
	 * Obté tots els justificants de la consulta múltiple comprimits
	 * en un arxiu ZIP.
	 *
	 * @param id
	 *            Atribut id de la consulta.
	 * @return l'arxiu amb el document generat.
	 * @throws ConsultaNotFoundException
	 *            Si la consulta no és accessible per aquest usuari.
	 * @throws JustificantGeneracioException
	 *            Si es produeixen errors al generar el justificant.
	 */
	@PreAuthorize("hasRole('ROLE_DELEG')")
	public FitxerDto obtenirJustificantMultipleConcatenat(
            Long id) throws ConsultaNotFoundException, JustificantGeneracioException;

	/**
	 * Obté tots els justificants de la consulta múltiple comprimits
	 * en un arxiu ZIP.
	 *
	 * @param id
	 *            Atribut id de la consulta.
	 * @return l'arxiu amb el document generat.
	 * @throws ConsultaNotFoundException
	 *            Si la consulta no és accessible per aquest usuari.
	 * @throws JustificantGeneracioException
	 *            Si es produeixen errors al generar el justificant.
	 */
	@PreAuthorize("hasRole('ROLE_DELEG')")
	public FitxerDto obtenirJustificantMultipleZip(
            Long id) throws ConsultaNotFoundException, JustificantGeneracioException;

	/**
	 * Torna a intentar la generació i custòdia del justificant.
	 *
	 * @param id
	 *            Atribut id de la consulta.
	 * @param descarregar
	 *            Indica si la s'ha de retornar el fitxer amb el justificant.
	 * @return l'arxiu amb el document generat.
	 * @throws ConsultaNotFoundException
	 *            Si la consulta no és accessible per aquest usuari.
	 * @throws JustificantGeneracioException
	 *            Si es produeixen errors al generar el justificant.
	 */
	@PreAuthorize("hasRole('ROLE_DELEG')")
	public JustificantDto reintentarGeneracioJustificant(
            Long id,
            boolean descarregar) throws ConsultaNotFoundException, JustificantGeneracioException;

	/**
	 * Retorna una pàgina de les consultes simples realitzades donada
	 * una entitat. Només es retornaran les consultes que l'usuari hagi
	 * realitzat.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param filtre
	 *            Filtre de consultes.
	 * @param pageable
	 *            Informació per a la paginació i ordenació de resultats.
	 * @return el llistat de consultes simples.
	 * @throws EntitatNotFoundException
	 *            Si l'entitat especificada no existeix.
	 */
	@PreAuthorize("hasRole('ROLE_DELEG')")
	public Page<ConsultaDto> findSimplesByFiltrePaginatPerDelegat(
            Long entitatId,
            ConsultaFiltreDto filtre,
            Pageable pageable) throws EntitatNotFoundException;

	/**
	 * Retorna una pàgina de les consultes múltiples realitzades donada
	 * una entitat. Només es retornaran les consultes que l'usuari hagi
	 * realitzat.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param filtre
	 *            Filtre de consultes.
	 * @param pageable
	 *            Informació per a la paginació i ordenació de resultats.
	 * @return el llistat de consultes múltiples.
	 * @throws EntitatNotFoundException
	 *            Si l'entitat especificada no existeix.
	 */
	@PreAuthorize("hasRole('ROLE_DELEG')")
	public Page<ConsultaDto> findMultiplesByFiltrePaginatPerDelegat(
            Long entitatId,
            ConsultaFiltreDto filtre,
            Pageable pageable) throws EntitatNotFoundException;

	/**
	 * Retorna una pàgina de les consultes realitzades donada una entitat.
	 * Comprova si l'usuari té accés a l'entitat.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param filtre
	 *            Filtre de consultes.
	 * @param pageable
	 *            Informació per a la paginació i ordenació de resultats.
	 * @return el llistat de consultes.
	 * @throws EntitatNotFoundException
	 *            Si l'entitat especificada no existeix.
	 */
	@PreAuthorize("hasRole('ROLE_AUDIT')")
	public Page<ConsultaDto> findByFiltrePaginatPerAuditor(
            Long entitatId,
            ConsultaFiltreDto filtre,
            Pageable pageable) throws EntitatNotFoundException;

	@PreAuthorize("hasRole('ROLE_AUDIT')")
	List<ConsultaDto> findByFiltrePerAuditor(Long entitatId, ConsultaFiltreDto filtre) throws EntitatNotFoundException;

	/**
	 * Retorna una pàgina de les consultes realitzades donada una entitat.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param filtre
	 *            Filtre de consultes.
	 * @param pageable
	 *            Informació per a la paginació i ordenació de resultats.
	 * @return el llistat de consultes.
	 * @throws EntitatNotFoundException
	 *            Si l'entitat especificada no existeix.
	 */
	@PreAuthorize("hasRole('ROLE_SUPERAUD')")
	public Page<ConsultaDto> findByFiltrePaginatPerSuperauditor(
            Long entitatId,
            ConsultaFiltreDto filtre,
            Pageable pageable) throws EntitatNotFoundException;

	/**
	 * Retorna una llista de les consultes realitzades donada una entitat
	 * i el filtre.
	 * 
	 * @param entitatCodi
	 *            Atribut id de l'entitat.
	 * @param dataInici
	 * @param dataFi
	 * @param procedimentCodi
	 * @param serveiCodi
	 * @return la llista de consultes.
	 * @throws EntitatNotFoundException
	 *            Si l'entitat especificada no existeix.
	 * @throws ProcedimentNotFoundException
	 *            Si el procediment especificat no existeix.
	 */
//	@PreAuthorize("hasRole('ROLE_REPORT')")
	public List<DadesObertesRespostaConsulta> findByFiltrePerOpenData(
            String entitatCodi,
            Date dataInici,
            Date dataFi,
            String procedimentCodi,
            String serveiCodi) throws EntitatNotFoundException, ProcedimentNotFoundException;

	public DadesObertesResposta findByFiltrePerOpenDataV2(ConsultaOpenDataDto consultaOpenDataDto) throws EntitatNotFoundException, ProcedimentNotFoundException;

	/**
	 * Retorna una pàgina de les consultes realitzades donada una entitat.
	 * 
	 * @param filtre
	 *            Filtre de consultes.
	 * @param pageable
	 *            Informació per a la paginació i ordenació de resultats.
	 * @return el llistat de consultes.
	 * @throws EntitatNotFoundException
	 *            Si l'entitat especificada no existeix.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public Page<ConsultaDto> findByFiltrePaginatPerAdmin(
            ConsultaFiltreDto filtre,
            Pageable pageable) throws EntitatNotFoundException;

	/**
	 * Retorna la informació d'una consulta donat el seu id.
	 * 
	 * @param id
	 *            Atribut id de la consulta.
	 * @return la informació de la consulta.
	 * @throws ConsultaNotFoundException
	 *            Si la consulta especificada no existeix.
	 * @throws ScspException
	 *            Si es produeixen errors al accedir al SCSP.
	 */
	@PreAuthorize("hasRole('ROLE_DELEG')")
	public ConsultaDto findOneDelegat(Long id) throws ConsultaNotFoundException, ScspException;

	/**
	 * Retorna la informació d'una consulta donat el seu id.
	 * 
	 * @param id
	 *            Atribut id de la consulta.
	 * @return la informació de la consulta.
	 * @throws ConsultaNotFoundException
	 *            Si la consulta especificada no existeix.
	 * @throws ScspException
	 *            Si es produeixen errors al accedir al SCSP.
	 */
	@PreAuthorize("hasRole('ROLE_AUDIT')")
	public ConsultaDto findOneAuditor(Long id) throws ConsultaNotFoundException, ScspException;

	/**
	 * Retorna la informació d'una consulta donat el seu id.
	 * 
	 * @param id
	 *            Atribut id de la consulta.
	 * @return la informació de la consulta.
	 * @throws ConsultaNotFoundException
	 *            Si la consulta especificada no existeix.
	 * @throws ScspException
	 *            Si es produeixen errors al accedir al SCSP.
	 */
	@PreAuthorize("hasRole('ROLE_SUPERAUD')")
	public ConsultaDto findOneSuperauditor(Long id) throws ConsultaNotFoundException, ScspException;

	/**
	 * Retorna la informació d'una consulta donat el seu id.
	 * 
	 * @param id
	 *            Atribut id de la consulta.
	 * @return la informació de la consulta.
	 * @throws ConsultaNotFoundException
	 *            Si la consulta especificada no existeix.
	 * @throws ScspException
	 *            Si es produeixen errors al accedir al SCSP.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ConsultaDto findOneAdmin(Long id) throws ConsultaNotFoundException, ScspException;

	/**
	 * Retorna una llista de consultes filles del pare especificat.
	 * 
	 * @param pareId
	 *            Atribut id de la consulta pare.
	 * @return La llista de consultes amb el pare especificat.
	 * @throws ConsultaNotFoundException
	 *            Si la consulta especificada no existeix.
	 * @throws ScspException
	 *            Si es produeixen errors al accedir al SCSP.
	 */
	@PreAuthorize("hasRole('ROLE_DELEG')")
	public List<ConsultaDto> findAmbPare(Long pareId) throws ConsultaNotFoundException, ScspException;

	/**
	 * Retorna informació sobre la càrrega del sistema.
	 * 
	 * @return la llista d'informació de càrrega.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_REPORT')")
	public List<CarregaDto> findEstadistiquesCarrega();

	/**
	 * Retorna una llista d'ids de consultes de l'entitat seleccionades aleatòriament.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param dataInici
	 *            Data inicial per a seleccionar les consultes.
	 * @param dataFi
	 *            Data final per a seleccionar les consultes.
	 * @param numConsultes
	 *            Nombre de consultes a seleccionar.
	 * @return el llistat d'ids de consulta.
	 * @throws EntitatNotFoundException
	 *            Si l'entitat especificada no existeix.
	 */
	@PreAuthorize("hasRole('ROLE_AUDIT')")
	public List<Long> auditoriaGenerarAuditor(
            Long entitatId,
            Date dataInici,
            Date dataFi,
            int numConsultes) throws EntitatNotFoundException;

	/**
	 * Retorna una llista de consultes donada una llista d'ids.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param consultaIds
	 *            Ids de les consultes.
	 * @return el llistat de consultes.
	 * @throws EntitatNotFoundException
	 *            Si l'entitat especificada no existeix.
	 * @throws ScspException
	 *            Si es produeixen errors al accedir al SCSP.
	 */
	@PreAuthorize("hasRole('ROLE_AUDIT')")
	public List<ConsultaDto> auditoriaConsultarAuditor(
            Long entitatId,
            List<Long> consultaIds) throws EntitatNotFoundException, ScspException;

	/**
	 * Retorna una llista d'ids de consultes seleccionades aleatòriament.
	 * 
	 * @param dataInici
	 *            Data inicial per a seleccionar les consultes.
	 * @param dataFi
	 *            Data final per a seleccionar les consultes.
	 * @param numEntitats
	 *            Nombre d'entitats a seleccionar.
	 * @param numConsultes
	 *            Nombre de consultes a seleccionar.
	 * @return el llistat d'ids de consulta.
	 */
	@PreAuthorize("hasRole('ROLE_SUPERAUD')")
	public List<Long> auditoriaGenerarSuperauditor(
            Date dataInici,
            Date dataFi,
            int numEntitats,
            int numConsultes);

	/**
	 * Retorna una llista de consultes donada una llista d'ids.
	 * 
	 * @param consultaIds
	 *            Ids de les consultes.
	 * @return el llistat de consultes.
	 * @throws ScspException
	 *            Si es produeixen errors al accedir al SCSP.
	 */
	@PreAuthorize("hasRole('ROLE_SUPERAUD')")
	public Map<EntitatDto, List<ConsultaDto>> auditoriaConsultarSuperauditor(
            List<Long> consultaIds) throws ScspException;


	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_REPORT')")
	public List<InformeGeneralEstatDto> informeGeneralEstat(
            Date dataInici,
            Date dataFi);

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_REPRES')")
	public List<InformeProcedimentServeiDto> informeUsuarisEntitatOrganProcedimentServei(
            Long entitatId,
            String rolActual,
            InformeRepresentantFiltreDto filtre);

	/**
	 * Tasca automàtica per a arxivar les consultes antigues
	 */
    public void arxivarConsultesAntigues();

}
