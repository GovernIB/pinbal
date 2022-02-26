/**
 * 
 */
package es.caib.pinbal.core.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.pinbal.client.dadesobertes.DadesObertesRespostaConsulta;
import es.caib.pinbal.core.dto.CarregaDto;
import es.caib.pinbal.core.dto.ConsultaDto;
import es.caib.pinbal.core.dto.ConsultaFiltreDto;
import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.EstadisticaDto;
import es.caib.pinbal.core.dto.EstadistiquesFiltreDto;
import es.caib.pinbal.core.dto.FitxerDto;
import es.caib.pinbal.core.dto.InformeGeneralEstatDto;
import es.caib.pinbal.core.dto.InformeProcedimentServeiDto;
import es.caib.pinbal.core.dto.InformeRepresentantFiltreDto;
import es.caib.pinbal.core.dto.JustificantDto;
import es.caib.pinbal.core.dto.RecobrimentSolicitudDto;
import es.caib.pinbal.core.service.exception.ConsultaNotFoundException;
import es.caib.pinbal.core.service.exception.ConsultaScspException;
import es.caib.pinbal.core.service.exception.ConsultaScspGeneracioException;
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
	 * @throws ConsultaScspException
	 *            Si hi ha hagut algun error en la consulta al servei SCSP.
	 */
	@PreAuthorize("hasRole('ROLE_DELEG')")
	public ConsultaDto novaConsulta(
			ConsultaDto consulta) throws ProcedimentServeiNotFoundException, ServeiNotAllowedException, ConsultaScspException;

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
	 * @throws ConsultaScspGeneracioException
	 *            Si hi ha hagut algun error generant la petició SCSP.
	 */
	@PreAuthorize("hasRole('ROLE_DELEG')")
	public ConsultaDto novaConsultaInit(
			ConsultaDto consulta) throws ProcedimentServeiNotFoundException, ServeiNotAllowedException, ConsultaScspGeneracioException;

	/**
	 * Realitza una consulta mitjançant les llibreries SCSP. Segona passa
	 * del procés per fer peticions al recobriment evitant bloquejar connexions
	 * a l'espera de rebre la resposta SCSP.
	 * 
	 * @param consulta
	 *            Dades de la consulta a executar.
	 * @throws ConsultaNotFoundException
	 *            Si la consulta no és accessible per aquest usuari.
	 * @throws ConsultaScspException
	 *            Si hi ha hagut algun error fent la consulta al servei SCSP.
	 */
	@PreAuthorize("hasRole('ROLE_DELEG')")
	public void novaConsultaEnviament(
			Long consultaId,
			ConsultaDto consulta) throws ProcedimentServeiNotFoundException, ConsultaNotFoundException, ConsultaScspException;

	/**
	 * Realitza una consulta mitjançant les llibreries SCSP. Tercera passa
	 * del procés per fer peticions al recobriment evitant bloquejar connexions
	 * a l'espera de rebre la resposta SCSP.
	 * 
	 * @param consultaId
	 *            Id de la consulta creada en el primer pas.
	 * @return la consulta modificada.
	 * @throws ConsultaNotFoundException
	 *            Si la consulta no és accessible per aquest usuari.
	 * @throws ConsultaScspException
	 *            Si hi ha hagut algun error al recuperar l'estat de la consulta.
	 */
	@PreAuthorize("hasRole('ROLE_DELEG')")
	public ConsultaDto novaConsultaEstat(
			Long consultaId) throws ConsultaNotFoundException, ConsultaScspException;

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
	 * @throws ConsultaScspException
	 *            Si hi ha hagut algun error en la consulta al servei SCSP.
	 */
	@PreAuthorize("hasRole('ROLE_DELEG')")
	public ConsultaDto novaConsultaMultiple(
			ConsultaDto consulta) throws ValidacioDadesPeticioException, ProcedimentServeiNotFoundException, ServeiNotAllowedException, ConsultaScspException;

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
	 * @throws ConsultaScspException
	 *            Si hi ha hagut algun error en la consulta al servei SCSP.
	 */
	@PreAuthorize("hasRole('ROLE_WS')")
	public ConsultaDto novaConsultaRecobriment(
			String serveiCodi,
			RecobrimentSolicitudDto solicitud) throws EntitatNotFoundException, ProcedimentNotFoundException, ProcedimentServeiNotFoundException, ServeiNotAllowedException, ConsultaScspException;

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
	 * @throws ConsultaScspException
	 *            Si hi ha hagut algun error en la consulta al servei SCSP.
	 */
	@PreAuthorize("hasRole('ROLE_WS')")
	public ConsultaDto novaConsultaRecobrimentInit(
			String serveiCodi,
			RecobrimentSolicitudDto solicitud) throws EntitatNotFoundException, ProcedimentNotFoundException, ProcedimentServeiNotFoundException, ServeiNotAllowedException, ConsultaScspException;

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
	 * @throws ConsultaScspException
	 *            Si hi ha hagut algun error en la consulta al servei SCSP.
	 */
	@PreAuthorize("hasRole('ROLE_WS')")
	public void novaConsultaRecobrimentEnviament(
			Long consultaId,
			RecobrimentSolicitudDto solicitud) throws ConsultaNotFoundException, ConsultaScspException;

	/**
	 * Realitza una consulta mitjançant les llibreries SCSP provinent del
	 * recobriment. Tercera passa del procésfindByFiltrePaginatPerAdmin per fer peticions al recobriment
	 * evitant bloquejar connexions a l'espera de rebre la resposta SCSP.
	 * 
	 * @param consultaId
	 *            Id de la consulta creada en el primer pas.
	 * @return la consulta modificada.
	 * @throws ConsultaNotFoundException
	 *            Si la consulta no és accessible per aquest usuari.
	 * @throws ConsultaScspException
	 *            Si hi ha hagut algun error en la consulta al servei SCSP.
	 */
	@PreAuthorize("hasRole('ROLE_WS')")
	public ConsultaDto novaConsultaRecobrimentEstat(
			Long consultaId) throws ConsultaNotFoundException, ConsultaScspException;

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
	 * @throws ConsultaScspException
	 *            Si hi ha hagut algun error en la consulta al servei SCSP.
	 */
	public ConsultaDto novaConsultaRecobrimentMultiple(
			String serveiCodi,
			List<RecobrimentSolicitudDto> solicituds) throws EntitatNotFoundException, ProcedimentNotFoundException, ProcedimentServeiNotFoundException, ServeiNotAllowedException, ConsultaScspException;

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
	public JustificantDto obtenirJustificant(
			Long id) throws ConsultaNotFoundException, JustificantGeneracioException;

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
			String idsolicitud) throws ConsultaNotFoundException, JustificantGeneracioException;

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
	@PreAuthorize("hasRole('ROLE_REPORT')")
	public List<DadesObertesRespostaConsulta> findByFiltrePerOpenData(
			String entitatCodi,
			Date dataInici,
			Date dataFi,
			String procedimentCodi,
			String serveiCodi) throws EntitatNotFoundException, ProcedimentNotFoundException;

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
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_REPRES') or hasRole('ROLE_REPORT')")
	public List<EstadisticaDto> findEstadistiquesByFiltre(EstadistiquesFiltreDto filtre) throws EntitatNotFoundException;

	/**
	 * Retorna les estadístiques per a totes les entitats amb consultes fetes i les
	 * estadístiques globals. Les estadístiques globals es fiquen a la resposta amb
	 * la clau null.
	 * 
	 * @param filtre
	 * @return una taula hash amb les estadístiques organitzades per entitat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_REPORT')")
	public Map<EntitatDto, List<EstadisticaDto>> findEstadistiquesGlobalsByFiltre(
			EstadistiquesFiltreDto filtre);

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

	/**
	 * Tasca automàtica per a revisar l'estat de les peticions múltiples
	 * pendents per veure si ja han estat processades.
	 */
	public void autoRevisarEstatPeticionsMultiplesPendents();

	/**
	 * Tasca automàtica per a generar els justificants pendents de les
	 * peticions SCSP ja tramitades.
	 */
	public void autoGenerarJustificantsPendents();

	/**
	 * Tasca automàtica per a tancar els expedients pendents que contenen
	 * els justificants pendents ja generats.
	 */
	public void autoTancarExpedientsPendents();

	/**
	 * Tasca automàtica per generar al final del dia un petit report 
	 * de l'estat de PINBAL, que s'envia per correu als administradors.
	 */
	public void autoGenerarEmailReportEstat();
	
	/**
	 * Tasca automàtica per a enviar les peticions SCSP pendents.
	 */
	public void autoEnviarPeticionsPendents();

	/**
	 * Retorna si les peticions al SCSP s'han de fer en una sola passa o en
	 * 3 passes per a optimitzar el temps que la transacció bloqueja les
	 * connexions del pool.
	 * 
	 * @return true si s'ha d'optimitzar en 3 peticions o false en cas contrari. 
	 */
	@PreAuthorize("hasRole('ROLE_DELEG') or hasRole('ROLE_WS')")
	public boolean isOptimitzarTransaccionsNovaConsulta();

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_REPORT')")
	public List<InformeGeneralEstatDto> informeGeneralEstat(
			Date dataInici,
			Date dataFi);

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_REPRES')")
	public List<InformeProcedimentServeiDto> informeUsuarisEntitatOrganProcedimentServei(
			Long entitatId, 
			String rolActual, 
			InformeRepresentantFiltreDto filtre);
}
