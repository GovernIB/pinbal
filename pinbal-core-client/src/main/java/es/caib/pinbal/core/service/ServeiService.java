package es.caib.pinbal.core.service;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

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
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.core.service.exception.ScspException;
import es.caib.pinbal.core.service.exception.ServeiAmbConsultesException;
import es.caib.pinbal.core.service.exception.ServeiBusNotFoundException;
import es.caib.pinbal.core.service.exception.ServeiCampGrupNotFoundException;
import es.caib.pinbal.core.service.exception.ServeiCampNotFoundException;
import es.caib.pinbal.core.service.exception.ServeiNotFoundException;

/**
 * Declaració dels mètodes per a interactuar amb les funcionalitats SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ServeiService {

	/**
	 * Actualitza la informació d'un servei SCSP.
	 * 
	 * @param servei
	 *            Informació del servei a actualitzar.
	 * @return El servei creat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ServeiDto save(ServeiDto servei);

	/**
	 * Esborra la informació d'un servei SCSP.
	 * 
	 * @param @param serveiCodi
	 *            Codi del servei a esborrar.
	 * @return El servei esborrat.
	 * @throws ServeiNotFoundException
	 *             Si no s'ha trobat troba cap servei amb l'id especificat.
	 * @throws ServeiAmbConsultesException
	 *             Si el servei té consultes ja fetes.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ServeiDto delete(String serveiCodi) throws ServeiNotFoundException, ServeiAmbConsultesException;

	
	/**
	 * Consulta els serveis segons el filtre.
	 * 
	 * @param codi Codi a cercar.
	 * @param descripcio Nom a cercar.
	 * @param emisor Emisor a cercar.
	 * @param activa Si cercar actius (true) o inactius (false).
	 * @param paginacioAmbOrdre
	 * 				Paràmetres per a la paginació i ordenació dels resultats.
	 * @return
	 */
	public Page<ServeiDto> findAmbFiltrePaginat(
			String codi,
			String descripcio,
			String emisor,
			Boolean activa,
			Pageable pageable);
	
	/**
	 * Consulta els serveis d'un procediment segons el filtre.
	 * 
	 * @param codi Codi a cercar.
	 * @param descripcio Nom a cercar.
	 * @param emisor Emisor a cercar.
	 * @param activa Si cercar actius (true) o inactius (false).
	 * @param entitat Entitat de la sessió
	 * @param procediment Procediment al que es volen consultar els serveis.
	 * @param paginacioAmbOrdre
	 * 				Paràmetres per a la paginació i ordenació dels resultats.
	 * @return
	 */
	public Page<ServeiDto> findAmbFiltrePaginat(
			String codi,
			String descripcio,
			String emisor,
			Boolean activa,
			EntitatDto entitat,
			ProcedimentDto procediment,
			Pageable pageable);
	/**
	 * Obté el servei amb un codi determinat.
	 * 
	 * @param serveiCodi
	 *            Codi del servei a trobar.
	 * @return La informació del servei.
	 * @throws ServeiNotFoundException
	 *             Si no s'ha trobat troba cap servei amb l'id especificat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_REPRES')")
	public ServeiDto findAmbCodiPerAdminORepresentant(String serveiCodi) throws ServeiNotFoundException;

	/**
	 * Obté el servei amb un codi determinat comprovant que el representant
	 * tengui permisos per accedir-hi.
	 * 
	 * @param serveiCodi
	 *            Codi del servei a trobar.
	 * @return La informació del servei.
	 * @throws ServeiNotFoundException
	 *             Si no s'ha trobat troba cap servei amb l'id especificat.
	 */
	@PreAuthorize("hasRole('ROLE_DELEG')")
	public ServeiDto findAmbCodiPerDelegat(Long entitatId, String serveiCodi) throws ServeiNotFoundException;

	/**
	 * Llistat amb els serveis actius.
	 * 
	 * @return Un llistat amb els serveis actius.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<ServeiDto> findActius();

	/**
	 * Llistat amb els serveis actius per a una entitat.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @return Un llistat amb els serveis actius.
	 * @throws EntitatNotFoundException
	 *             Si no s'ha trobat troba cap entitat amb l'id especificat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_REPRES') or hasRole('ROLE_AUDIT') or hasRole('ROLE_SUPERAUD')")
	public List<ServeiDto> findAmbEntitat(Long entitatId) throws EntitatNotFoundException;

	/**
	 * Llistat amb els serveis d'una entitat donat un procediment.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param procedimentId
	 *            Atribut id del procediment.
	 * @return Una llista amb els serveis resultants de la consulta.
	 * @throws EntitatNotFoundException
	 *             Si no s'ha trobat troba cap entitat amb l'id especificat.
	 * @throws ProcedimentNotFoundException
	 *             Si no s'ha trobat troba cap procediment amb l'id especificat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_REPRES') or hasRole('ROLE_AUDIT') or hasRole('ROLE_SUPERAUD')")
	public List<ServeiDto> findAmbEntitatIProcediment(
			Long entitatId,
			Long procedimentId) throws EntitatNotFoundException, ProcedimentNotFoundException;

	/**
	 * Llistat amb les parelles procediment-servei a les quals un usuari
	 * delegat té accés dins una entitat.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param usuariCodi
	 *            Codi de l'usuari delegat a consultar.
	 * @return Un llistat amb les parelles procediment-servei.
	 * @throws EntitatNotFoundException
	 *             Si no s'ha trobat troba cap entitat amb l'id especificat.
	 */
	@PreAuthorize("hasRole('ROLE_REPRES')")
	public List<ProcedimentServeiDto> findPermesosAmbEntitatIUsuari(
			Long entitatId,
			String usuariCodi) throws EntitatNotFoundException;

	/**
	 * Llistat amb els serveis actius per a un usuari delegat que pertanyen
	 * a un procediment.
	 * 
	 * @param procedimentId
	 *            Atribut id del procediment.
	 * @return Un llistat amb els serveis actius.
	 * @throws EntitatNotFoundException
	 *             Si no s'ha trobat troba cap entitat amb l'id especificat.
	 * @throws ProcedimentNotFoundException
	 *             Si no s'ha trobat troba cap procediment amb l'id especificat.
	 */
	@PreAuthorize("hasRole('ROLE_DELEG')")
	public List<ServeiDto> findPermesosAmbProcedimentPerDelegat(
			Long entitatId,
			Long procedimentId) throws EntitatNotFoundException, ProcedimentNotFoundException;

	/**
	 * Llistat amb els emisors donats d'alta a les taules SCSP.
	 * 
	 * @return Un llistat amb els emisors.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<EmisorDto> findEmisorAll();

	/**
	 * Llistat amb les claus publiques donades d'alta a les taules SCSP.
	 * 
	 * @return Un llistat amb les claus publiques.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<ClauPublicaDto> findClauPublicaAll();

	/**
	 * Llistat amb les claus publiques donades d'alta a les taules SCSP.
	 * 
	 * @return Un llistat amb les claus privades.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<ClauPrivadaDto> findClauPrivadaAll();

	/**
	 * Retorna l'arbre de dades específiques donat un servei.
	 * 
	 * @param serveiCodi
	 *             El codi del servei.
	 * @return L'arbre de dades específiques.
	 * @throws ServeiNotFoundException
	 *             Si no s'ha trobat troba cap servei amb l'id especificat.
	 * @throws ScspException
	 *             Si hi ha hagut errors al generar l'arbre.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_DELEG')")
	public ArbreDto<DadaEspecificaDto> generarArbreDadesEspecifiques(String serveiCodi, boolean gestioXsdActiva) throws ServeiNotFoundException, ScspException;
	
	
	/**
	 * Retorna l'arbre de dades específiques donat un servei.
	 * 
	 * @param serveiCodi
	 *             El codi del servei.
	 * @return L'arbre de dades específiques.
	 * @throws ServeiNotFoundException
	 *             Si no s'ha trobat troba cap servei amb l'id especificat.
	 * @throws ScspException
	 *             Si hi ha hagut errors al generar l'arbre.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_DELEG')")
	public ArbreDto<DadaEspecificaDto> generarArbreDadesEspecifiques(String serveiCodi) throws ServeiNotFoundException, ScspException;
	
	/**
	 * Afegeix un camp al formulari del servei.
	 * 
	 * @param serveiCodi
	 *             El codi del servei per al nou camp.
	 * @param path
	 *             El path a dins el xsd per al nou camp.
	 * @return El camp afegit.
	 * @throws ServeiNotFoundException
	 *             Si no s'ha trobat cap servei amb el codi especificat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ServeiCampDto createServeiCamp(
			String serveiCodi,
			String path) throws ServeiNotFoundException;

	/**
	 * Modifica les dades d'un camp.
	 * 
	 * @param modificat
	 *             La informació del camp a modificar.
	 * @return El camp modificat.
	 * @throws ServeiCampNotFoundException
	 *             Si no s'ha trobat el camp amb l'id especificat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ServeiCampDto updateServeiCamp(ServeiCampDto modificat) throws ServeiCampNotFoundException;

	/**
	 * Esborra un camp del formulari del servei.
	 * 
	 * @param serveiCampId
	 *             Atribut id del camp.
	 * @return El ServeiCamp esborrat.
	 * @throws ServeiCampNotFoundException
	 *             Si no s'ha trobat el camp amb l'id especificat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ServeiCampDto deleteServeiCamp(Long serveiCampId) throws ServeiCampNotFoundException;

	/**
	 * Canvia l'ordre d'un camp a dins el servei.
	 * 
	 * @param serveiCodi
	 *            El codi del servei.
	 * @param serveiCampId
	 *             Atribut id del camp.
	 * @param indexDesti
	 *            Nou ordre.
	 * @return El camp mogut.
	 * @throws ServeiCampNotFoundException
	 *             Si no s'ha trobat el camp amb l'id especificat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ServeiCampDto moveServeiCamp(
			String serveiCodi,
			Long serveiCampId,
			int indexDesti) throws ServeiCampNotFoundException;

	/**
	 * Canvia un camp de grup.
	 * 
	 * @param serveiCampId
	 *             Atribut id del camp.
	 * @param serveiCampGrupId
	 *             Atribut id del grup.
	 * @return El grup de camps esborrat.
	 * @throws ServeiCampNotFoundException
	 *             Si no s'ha trobat el camp amb l'id especificat.
	 * @throws ServeiCampGrupNotFoundException
	 *             Si no s'ha trobat el grup de camps especificat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void agrupaServeiCamp(
			Long serveiCampId,
			Long serveiCampGrupId) throws ServeiCampNotFoundException, ServeiCampGrupNotFoundException;

	/**
	 * Llistat amb els camps d'un servei.
	 * 
	 * @param serveiCodi
	 *            El codi del servei.
	 * @return Un llistat amb els camps del servei.
	 * @throws ServeiNotFoundException
	 *             Si no s'ha trobat cap servei amb el codi especificat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_DELEG')")
	public List<ServeiCampDto> findServeiCamps(String serveiCodi) throws ServeiNotFoundException;

	/**
	 * Crea un nou grup de camps pel servei.
	 * 
	 * @param serveiCampGrup
	 *            La informació per a crear el grup de camps.
	 * @return El grup de camps creat.
	 * @throws ServeiNotFoundException
	 *             Si no s'ha trobat cap servei amb el codi especificat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ServeiCampGrupDto createServeiCampGrup(ServeiCampGrupDto serveiCampGrup) throws ServeiNotFoundException;

	/**
	 * Modifica un grup de camps pel servei.
	 * 
	 * @param serveiCampGrup
	 *            La informació per a modificar el grup de camps.
	 * @return El grup de camps modificat.
	 * @throws ServeiCampGrupNotFoundException
	 *             Si no s'ha trobat el grup de camps especificat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ServeiCampGrupDto updateServeiCampGrup(ServeiCampGrupDto serveiCampGrup) throws ServeiCampGrupNotFoundException;

	/**
	 * Esborra un grup de camps pel servei.
	 * 
	 * @param serveiCampGrupId
	 *             Atribut id del grup de camps.
	 * @return El grup de camps esborrat.
	 * @throws ServeiCampGrupNotFoundException
	 *             Si no s'ha trobat el grup de camps especificat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ServeiCampGrupDto deleteServeiCampGrup(Long serveiCampGrupId) throws ServeiCampGrupNotFoundException;

	/**
	 * Mou un grup de camps pel servei amunt o avall.
	 * 
	 * @param serveiCampGrupId
	 *             Atribut id del grup de camps.
	 * @param up
	 *             Un valor true indica que s'ha de moure cap amunt. Un
	 *             valor false indica que s'ha de moure cap abaix.
	 * @return El grup de camps esborrat.
	 * @throws ServeiCampGrupNotFoundException
	 *             Si no s'ha trobat el grup de camps especificat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void moveServeiCampGrup(
			Long serveiCampGrupId,
			boolean up) throws ServeiCampGrupNotFoundException;

	/**
	 * Llistat amb els grups de camps d'un servei.
	 * 
	 * @param serveiCodi
	 *            El codi del servei.
	 * @return Un llistat amb els grups de camps del servei.
	 * @throws ServeiNotFoundException
	 *             Si no s'ha trobat cap servei amb el codi especificat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_DELEG')")
	public List<ServeiCampGrupDto> findServeiCampGrups(String serveiCodi) throws ServeiNotFoundException;

	/**
	 * Crea una redirecció d'una consulta a un servei per al bus 
	 * de serveis. 
	 * 
	 * @param creat
	 *             La informació de la redirecció a crear.
	 * @return La redirecció creada.
	 * @throws ServeiNotFoundException
	 *             Si no s'ha trobat cap servei amb el codi especificat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ServeiBusDto createServeiBus(
			ServeiBusDto creat) throws ServeiNotFoundException, EntitatNotFoundException;

	/**
	 * Modifica una redirecció d'una consulta a un servei per al bus 
	 * de serveis. 
	 * 
	 * @param modificat
	 *             La informació de la redirecció a modificar.
	 * @return La redirecció modificada.
	 * @throws ServeiBusNotFoundException
	 *             Si no s'ha trobat la redirecció especificada.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ServeiBusDto updateServeiBus(
			ServeiBusDto modificat) throws ServeiBusNotFoundException, EntitatNotFoundException;

	/**
	 * Esborra una redirecció d'una consulta a un servei per al bus 
	 * de serveis. 
	 * 
	 * @param serveiBusId
	 *             Id de la redirecció a esborrar.
	 * @return La redirecció esborrada.
	 * @throws ServeiBusNotFoundException
	 *             Si no s'ha trobat la redirecció especificada.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ServeiBusDto deleteServeiBus(
			Long serveiBusId) throws ServeiBusNotFoundException;

	/**
	 * Retorna la redirecció donat el seu id.
	 * 
	 * @param id
	 *             Id de la redirecció a retornar.
	 * @return La redirecció trobada.
	 * @throws ServeiBusNotFoundException
	 *             Si no s'ha trobat la redirecció especificada.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ServeiBusDto findServeiBusById(Long id) throws ServeiBusNotFoundException;

	/**
	 * Llistat amb els redireccions de consultes a un servei per
	 * al bus de serveis ordenades.
	 * 
	 * @param serveiCodi
	 *            El codi del servei.
	 * @return Un llistat amb les redireccions del servei ordenades.
	 * @throws ServeiNotFoundException
	 *             Si no s'ha trobat cap servei amb el codi especificat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<ServeiBusDto> findServeisBus(String serveiCodi) throws ServeiNotFoundException;

	/**
	 * Modifica la configuració per a un camp de dades específiques del 
	 * justificant del servei.
	 * 
	 * @param camp
	 *            El camp del justificant a crear.
	 * @throws ServeiNotFoundException
	 *             Si no s'ha trobat cap servei amb el codi especificat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void addServeiJustificantCamp(
			ServeiJustificantCampDto camp) throws ServeiNotFoundException;

	/**
	 * Retorna tots els camps de de dades específiques del 
	 * justificant del servei.
	 * 
	 * @param serveiCodi
	 *            El codi del servei.
	 * @return Un llistat amb les traduccions del servei.
	 * @throws ServeiNotFoundException
	 *             Si no s'ha trobat cap servei amb el codi especificat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_DELEG')")
	public List<ServeiJustificantCampDto> findServeiJustificantCamps(
			String serveiCodi) throws ServeiNotFoundException;
	
	/**
	 * Retorna tots els fitxers XSD del servei.
	 * 
	 * @param serveiCodi
	 *            El codi del servei.
	 * @return Un llistat amb tots els fitxers XSD del servei.
	 * @throws IOException
	 *             Si no s'ha trobat cap servei amb el codi especificat.
	 * @throws ServeiNotFoundException 
	 */
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<ServeiXsdDto> xsdFindByServei(
			String codi) throws IOException, ServeiNotFoundException;
	
	/**
	 * Retorna tots els camps de de dades específiques del 
	 * justificant del servei.
	 * 
	 * @param serveiCodi
	 *            El codi del servei.
	 * @return Un llistat amb les traduccions del servei.
	 * @throws ServeiNotFoundException
	 *             Si no s'ha trobat cap servei amb el codi especificat.
	 */
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void xsdDelete(
			String codi,
			XsdTipusEnumDto tipus) throws IOException;
	
	
	/**
	 * Retorna tots els camps de de dades específiques del 
	 * justificant del servei.
	 * 
	 * @param serveiCodi
	 *            El codi del servei.
	 * @return Un llistat amb les traduccions del servei.
	 * @throws ServeiNotFoundException
	 *             Si no s'ha trobat cap servei amb el codi especificat.
	 */
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public FitxerDto xsdDescarregar(
			String codi,
			XsdTipusEnumDto tipus) throws IOException;
	

	/**
	 * Retorna tots els camps de de dades específiques del 
	 * justificant del servei.
	 * 
	 * @param serveiCodi
	 *            El codi del servei.
	 * @return Un llistat amb les traduccions del servei.
	 * @throws ServeiNotFoundException
	 *             Si no s'ha trobat cap servei amb el codi especificat.
	 */
	
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void xsdCreate(
			String codi,
			ServeiXsdDto xsd,
			byte[] contingut) throws IOException;

	/**
	 * Retorna tots els rols configurats als serveis.
	 * 
	 * @return Un llistat amb tots els rols.
	 */
	public List<String> getRolsConfigurats();

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	void saveActiu(
			String serveiCodi,
			boolean actiu);

}
