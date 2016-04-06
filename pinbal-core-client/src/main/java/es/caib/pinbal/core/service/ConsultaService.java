/**
 * 
 */
package es.caib.pinbal.core.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;

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
import es.caib.pinbal.core.service.exception.ConsultaNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.JustificantGeneracioException;
import es.caib.pinbal.core.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentServeiNotFoundException;
import es.caib.pinbal.core.service.exception.ScspException;
import es.caib.pinbal.core.service.exception.ServeiNotAllowedException;
import es.caib.pinbal.core.service.exception.ValidacioDadesPeticioException;

/**
 * Declaració dels mètodes per a gestionar les consultes a SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ConsultaService {

	/**
	 * Realitza una consulta mitjançant les llibreries SCSP.
	 * 
	 * @param consulta
	 *            Dades de la consulta a executar.
	 * @return la nova consulta creada.
	 * @throws ProcedimentServeiNotFoundException
	 *            Si el procediment-servei especificat no existeix.
	 * @throws ServeiNotAllowedException
	 *            Si el funcionari no té accés al servei.
	 * @throws ScspException
	 *            Si hi ha hagut algun error en la consulta al servei SCSP.
	 */
	@PreAuthorize("hasRole('ROLE_DELEG')")
	public ConsultaDto novaConsulta(
			ConsultaDto consulta) throws ProcedimentServeiNotFoundException, ServeiNotAllowedException, ScspException;

	/**
	 * Realitza una consulta mitjançant les llibreries SCSP. Primera passa
	 * del procés per fer peticions al recobriment evitant bloquejar connexions
	 * a l'espera de rebre la resposta SCSP.
	 * 
	 * @param consulta
	 *            Dades de la consulta a executar.
	 * @return la nova consulta creada.
	 * @throws ProcedimentServeiNotFoundException
	 *            Si el procediment-servei especificat no existeix.
	 * @throws ServeiNotAllowedException
	 *            Si el funcionari no té accés al servei.
	 * @throws ScspException
	 *            Si hi ha hagut algun error en la consulta al servei SCSP.
	 */
	@PreAuthorize("hasRole('ROLE_DELEG')")
	public ConsultaDto novaConsultaInit(
			ConsultaDto consulta) throws ProcedimentServeiNotFoundException, ServeiNotAllowedException, ScspException;

	/**
	 * Realitza una consulta mitjançant les llibreries SCSP. Segona passa
	 * del procés per fer peticions al recobriment evitant bloquejar connexions
	 * a l'espera de rebre la resposta SCSP.
	 * 
	 * @param consulta
	 *            Dades de la consulta a executar.
	 * @throws ConsultaNotFoundException
	 *            Si la consulta no és accessible per aquest usuari.
	 * @throws ScspException
	 *            Si hi ha hagut algun error en la consulta al servei SCSP.
	 */
	@PreAuthorize("hasRole('ROLE_DELEG')")
	public void novaConsultaEnviament(
			Long consultaId,
			ConsultaDto consulta) throws ConsultaNotFoundException, ScspException;

	/**
	 * Realitza una consulta mitjançant les llibreries SCSP. Tercera passa
	 * del procés per fer peticions al recobriment evitant bloquejar connexions
	 * a l'espera de rebre la resposta SCSP.
	 * 
	 * @param consultaId
	 *            Id de la consulta creada en el primer pas.
	 * @return la consulta modificada.
	 * @throws ScspException
	 *            Si hi ha hagut algun error en la consulta al servei SCSP.
	 */
	@PreAuthorize("hasRole('ROLE_DELEG')")
	public ConsultaDto novaConsultaEstat(
			Long consultaId) throws ScspException;

	/**
	 * Realitza una consulta múltiple mitjançant les llibreries SCSP.
	 * 
	 * @param consulta
	 *            Dades de la consulta a executar.
	 * @return la nova consulta creada.
	 * @throws ValidacioDadesPeticioException
	 *            Si la validació de les dades de les sol·licituds no és correcta.
	 * @throws ProcedimentServeiNotFoundException
	 *            Si el procediment-servei especificat no existeix.
	 * @throws ServeiNotAllowedException
	 *            Si el funcionari no té accés al servei.
	 * @throws ScspException
	 *            Si hi ha hagut algun error en la consulta al servei SCSP.
	 */
	@PreAuthorize("hasRole('ROLE_DELEG')")
	public ConsultaDto novaConsultaMultiple(
			ConsultaDto consulta) throws ValidacioDadesPeticioException, ProcedimentServeiNotFoundException, ServeiNotAllowedException, ScspException;

	/**
	 * Realitza una consulta mitjançant les llibreries SCSP provinent
	 * del recobriment.
	 * 
	 * @param serveiCodi
	 *            Codi del servei SCSP que es vol consultar.
	 * @param solicitud
	 *            La sol·licitud de la consulta.
	 * @return la nova consulta creada.
	 * @throws EntitatNotFoundException
	 *            Si l'entitat especificada no existeix.
	 * @throws ProcedimentNotFoundException
	 *            Si el procediment especificat no existeix.
	 * @throws ProcedimentServeiNotFoundException
	 *            Si la combinació procediment-servei especificada no existeix.
	 * @throws ServeiNotAllowedException
	 *            Si el funcionari no té accés al servei.
	 * @throws ScspException
	 *            Si hi ha hagut algun error en la consulta al servei SCSP.
	 */
	@PreAuthorize("hasRole('ROLE_WS')")
	public ConsultaDto novaConsultaRecobriment(
			String serveiCodi,
			RecobrimentSolicitudDto solicitud) throws EntitatNotFoundException, ProcedimentNotFoundException, ProcedimentServeiNotFoundException, ServeiNotAllowedException, ScspException;

	/**
	 * Realitza una consulta mitjançant les llibreries SCSP provinent del
	 * recobriment. Primera passa del procés per fer peticions al recobriment
	 * evitant bloquejar connexions a l'espera de rebre la resposta SCSP.
	 * 
	 * @param serveiCodi
	 *            Codi del servei SCSP que es vol consultar.
	 * @param solicitud
	 *            La sol·licitud de la consulta.
	 * @return la nova consulta creada.
	 * @throws EntitatNotFoundException
	 *            Si l'entitat especificada no existeix.
	 * @throws ProcedimentNotFoundException
	 *            Si el procediment especificat no existeix.
	 * @throws ProcedimentServeiNotFoundException
	 *            Si la combinació procediment-servei especificada no existeix.
	 * @throws ServeiNotAllowedException
	 *            Si el funcionari no té accés al servei.
	 * @throws ScspException
	 *            Si hi ha hagut algun error en la consulta al servei SCSP.
	 */
	@PreAuthorize("hasRole('ROLE_WS')")
	public ConsultaDto novaConsultaRecobrimentInit(
			String serveiCodi,
			RecobrimentSolicitudDto solicitud) throws EntitatNotFoundException, ProcedimentNotFoundException, ProcedimentServeiNotFoundException, ServeiNotAllowedException, ScspException;

	/**
	 * Realitza una consulta mitjançant les llibreries SCSP provinent del
	 * recobriment. Segona passa del procés per fer peticions al recobriment
	 * evitant bloquejar connexions a l'espera de rebre la resposta SCSP.
	 * 
	 * @param consultaId
	 *            Id de la consulta creada en el primer pas.
	 * @param solicitud
	 *            La sol·licitud de la consulta.
	 * @throws ConsultaNotFoundException
	 *            Si la consulta no és accessible per aquest usuari.
	 * @throws ScspException
	 *            Si hi ha hagut algun error en la consulta al servei SCSP.
	 */
	@PreAuthorize("hasRole('ROLE_WS')")
	public void novaConsultaRecobrimentEnviament(
			Long consultaId,
			RecobrimentSolicitudDto solicitud) throws ConsultaNotFoundException, ScspException;

	/**
	 * Realitza una consulta mitjançant les llibreries SCSP provinent del
	 * recobriment. Tercera passa del procés per fer peticions al recobriment
	 * evitant bloquejar connexions a l'espera de rebre la resposta SCSP.
	 * 
	 * @param consultaId
	 *            Id de la consulta creada en el primer pas.
	 * @return la consulta modificada.
	 * @throws ScspException
	 *            Si hi ha hagut algun error en la consulta al servei SCSP.
	 */
	@PreAuthorize("hasRole('ROLE_WS')")
	public ConsultaDto novaConsultaRecobrimentEstat(
			Long consultaId) throws ScspException;

	/**
	 * Realitza una consulta mitjançant les llibreries SCSP provinent
	 * del recobriment.
	 * 
	 * @param serveiCodi
	 *            Codi del servei SCSP que es vol consultar.
	 * @param solicituds
	 *            Les sol·licituds de la consulta.
	 * @return la nova consulta creada.
	 * @throws EntitatNotFoundException
	 *            Si l'entitat especificada no existeix.
	 * @throws ProcedimentNotFoundException
	 *            Si el procediment especificat no existeix.
	 * @throws ProcedimentServeiNotFoundException
	 *            Si la combinació procediment-servei especificada no existeix.
	 * @throws ServeiNotAllowedException
	 *            Si el funcionari no té accés al servei.
	 * @throws ScspException
	 *            Si hi ha hagut algun error en la consulta al servei SCSP.
	 */
	public ConsultaDto novaConsultaRecobrimentMultiple(
			String serveiCodi,
			List<RecobrimentSolicitudDto> solicituds) throws EntitatNotFoundException, ProcedimentNotFoundException, ProcedimentServeiNotFoundException, ServeiNotAllowedException, ScspException;

	/**
	 * Obté el justificant de la consulta.
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
	public ArxiuDto obtenirJustificant(
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
	public ArxiuDto obtenirJustificantMultipleConcatenat(
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
	public ArxiuDto obtenirJustificantMultipleZip(
			Long id) throws ConsultaNotFoundException, JustificantGeneracioException;

	/**
	 * Torna a intentar la generació i custòdia del justificant.
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
	public void reintentarGeneracioJustificant(
			Long id) throws ConsultaNotFoundException, JustificantGeneracioException;

	/**
	 * Retorna una pàgina de les consultes simples realitzades donada
	 * una entitat. Només es retornaran les consultes que l'usuari hagi
	 * realitzat.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param filtre
	 *            Filtre de consultes.
	 * @param paginacioAmbOrdre
	 *            Informació per a la paginació i ordenació de resultats.
	 * @return el llistat de consultes simples.
	 * @throws EntitatNotFoundException
	 *            Si l'entitat especificada no existeix.
	 */
	@PreAuthorize("hasRole('ROLE_DELEG')")
	public PaginaLlistatDto<ConsultaDto> findSimplesByFiltrePaginatPerDelegat(
			Long entitatId,
			ConsultaFiltreDto filtre,
			PaginacioAmbOrdreDto paginacioAmbOrdre) throws EntitatNotFoundException;

	/**
	 * Retorna una pàgina de les consultes múltiples realitzades donada
	 * una entitat. Només es retornaran les consultes que l'usuari hagi
	 * realitzat.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param filtre
	 *            Filtre de consultes.
	 * @param paginacioAmbOrdre
	 *            Informació per a la paginació i ordenació de resultats.
	 * @return el llistat de consultes múltiples.
	 * @throws EntitatNotFoundException
	 *            Si l'entitat especificada no existeix.
	 */
	@PreAuthorize("hasRole('ROLE_DELEG')")
	public PaginaLlistatDto<ConsultaDto> findMultiplesByFiltrePaginatPerDelegat(
			Long entitatId,
			ConsultaFiltreDto filtre,
			PaginacioAmbOrdreDto paginacioAmbOrdre) throws EntitatNotFoundException;

	/**
	 * Retorna una pàgina de les consultes realitzades donada una entitat.
	 * Comprova si l'usuari té accés a l'entitat.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param filtre
	 *            Filtre de consultes.
	 * @param paginacioAmbOrdre
	 *            Informació per a la paginació i ordenació de resultats.
	 * @return el llistat de consultes.
	 * @throws EntitatNotFoundException
	 *            Si l'entitat especificada no existeix.
	 */
	@PreAuthorize("hasRole('ROLE_AUDIT')")
	public PaginaLlistatDto<ConsultaDto> findByFiltrePaginatPerAuditor(
			Long entitatId,
			ConsultaFiltreDto filtre,
			PaginacioAmbOrdreDto paginacioAmbOrdre) throws EntitatNotFoundException;

	/**
	 * Retorna una pàgina de les consultes realitzades donada una entitat.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param filtre
	 *            Filtre de consultes.
	 * @param paginacioAmbOrdre
	 *            Informació per a la paginació i ordenació de resultats.
	 * @return el llistat de consultes.
	 * @throws EntitatNotFoundException
	 *            Si l'entitat especificada no existeix.
	 */
	@PreAuthorize("hasRole('ROLE_SUPERAUD')")
	public PaginaLlistatDto<ConsultaDto> findByFiltrePaginatPerSuperauditor(
			Long entitatId,
			ConsultaFiltreDto filtre,
			PaginacioAmbOrdreDto paginacioAmbOrdre) throws EntitatNotFoundException;

	/**
	 * Retorna una pàgina de les consultes realitzades donada una entitat.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param filtre
	 *            Filtre de consultes.
	 * @param paginacioAmbOrdre
	 *            Informació per a la paginació i ordenació de resultats.
	 * @return el llistat de consultes.
	 * @throws EntitatNotFoundException
	 *            Si l'entitat especificada no existeix.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public PaginaLlistatDto<ConsultaDto> findByFiltrePaginatPerAdmin(
			Long entitatId,
			ConsultaFiltreDto filtre,
			PaginacioAmbOrdreDto paginacioAmbOrdre) throws EntitatNotFoundException;

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
	 * Retorna el nombre de consultes múltiples fetes per l'usuari
	 * actual que estan en estat processant a dins l'entitat especificada.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @return El nombre de consultes múltiples en procés.
	 * @throws EntitatNotFoundException
	 *             Si no s'ha trobat cap entitat amb l'id especificat.
	 */
	@PreAuthorize("hasRole('ROLE_DELEG')")
	public long countConsultesMultiplesProcessant(
			Long entitatId) throws EntitatNotFoundException;

	/**
	 * Retorna una estadística d'us dels procediments i serveis
	 * d'una entitat.
	 * 
	 * @param filtre
	 *            Filtre de consultes.
	 * @return el llistat amb les estadístiques.
	 * @throws EntitatNotFoundException
	 *            Si l'entitat especificada no existeix.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_REPRES')")
	public List<EstadisticaDto> findEstadistiquesByFiltre(EstadistiquesFiltreDto filtre) throws EntitatNotFoundException;

	/**
	 * Retorna les estadístiques per a totes les entitats amb consultes fetes i les
	 * estadístiques globals. Les estadístiques globals es fiquen a la resposta amb
	 * la clau null.
	 * 
	 * @param filtre
	 * @return una taula hash amb les estadístiques organitzades per entitat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public Map<EntitatDto, List<EstadisticaDto>> findEstadistiquesGlobalsByFiltre(
			EstadistiquesFiltreDto filtre);

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
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param consultaIds
	 *            Ids de les consultes.
	 * @return el llistat de consultes.
	 * @throws ScspException
	 *            Si es produeixen errors al accedir al SCSP.
	 */
	@PreAuthorize("hasRole('ROLE_SUPERAUD')")
	public Map<EntitatDto, List<ConsultaDto>> auditoriaConsultarSuperauditor(
			List<Long> consultaIds) throws ScspException;

	/**
	 * Revisa l'estat de les peticions múltiples pendents per veure
	 * si ja han estat processades.
	 */
	public void revisarEstatPeticionsMultiplesPendents();

	/**
	 * Retorna si les peticions al SCSP s'han de fer en una sola passa o en
	 * 3 passes per a optimitzar el temps que la transacció bloqueja les
	 * connexions del pool.
	 * 
	 * @return true si s'ha d'optimitzar en 3 peticions o false en cas contrari. 
	 */
	@PreAuthorize("hasRole('ROLE_DELEG')")
	public boolean isOptimitzarTransaccionsNovaConsulta();

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<InformeGeneralEstatDto> informeGeneralEstat(Date dataInici, Date dataFi);
	
}
