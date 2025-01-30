/**
 * 
 */
package es.caib.pinbal.core.service;

import es.caib.pinbal.core.dto.EntitatUsuariDto;
import es.caib.pinbal.core.dto.FiltreActiuEnumDto;
import es.caib.pinbal.core.dto.InformeProcedimentDto;
import es.caib.pinbal.core.dto.PaginacioAmbOrdreDto;
import es.caib.pinbal.core.dto.ProcedimentDto;
import es.caib.pinbal.core.dto.ProcedimentServeiNomDto;
import es.caib.pinbal.core.dto.ProcedimentServeiSimpleDto;
import es.caib.pinbal.core.dto.ServeiDto;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatUsuariNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentServeiNotFoundException;
import es.caib.pinbal.core.service.exception.ServeiNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Declaració dels mètodes per a obtenir i modificar informació d'un procediment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ProcedimentService {

	/**
	 * Crea un nou procediment.
	 * 
	 * @param creat
	 *            Informació del procediment a crear.
	 * @return El procediment creat.
	 * @throws EntitatNotFoundException
	 *             Si no s'ha trobat cap entitat amb l'id especificat.
	 */
	@PreAuthorize("hasRole('ROLE_REPRES')")
	public ProcedimentDto create(ProcedimentDto creat) throws EntitatNotFoundException;

	/**
	 * Esborra un procediment.
	 * 
	 * @param procedimentId
	 *            Atribut id del procediment a esborrar.
	 * @return El procediment esborrat.
	 * @throws ProcedimentNotFoundException
	 *             Si no s'ha trobat cap entitat amb l'id especificat.
	 */
	@PreAuthorize("hasRole('ROLE_REPRES')")
	public ProcedimentDto delete(Long procedimentId) throws ProcedimentNotFoundException;

	/**
	 * Llistat amb els procediments d'una entitat.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @return Un llistat de procediments.
	 * @throws EntitatNotFoundException
	 *             Si no s'ha trobat cap entitat amb l'id especificat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_REPRES') or hasRole('ROLE_AUDIT') or hasRole('ROLE_SUPERAUD')")
	public List<ProcedimentDto> findAmbEntitat(Long entitatId) throws EntitatNotFoundException;

	/**
	 * Llistat amb els procediments d'una entitat.
	 * 
	 * Selecciona les que tenen el patró espedificat al parametre de filtre al nom o al codi.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param filtre
	 *            Paràmetre de filtre 
	 * @return Un llistat de procediments.
	 * @throws EntitatNotFoundException
	 *             Si no s'ha trobat cap entitat amb l'id especificat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_REPRES') or hasRole('ROLE_AUDIT') or hasRole('ROLE_SUPERAUD')")
	public List<ProcedimentDto> findAmbEntitat(Long entitatId, String filtre) throws EntitatNotFoundException;
	
	/**
	 * Consulta els procediments segons el filtre.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param codi
	 *            Codi a cercar.
	 * @param nom
	 *            Nom a cercar.
	 * @param departament
	 *            Departament a cercar.
	 * @param organGestorId TODO
	 * @param codiSia TODO
	 * @param actiu TODO
	 * @param paginacioParams
	 *            Paràmetres per a la paginació i ordenació dels resultats.
	 * @return El llistat de procediments paginat.
	 * @throws EntitatNotFoundException
	 *             Si no s'ha trobat cap entitat amb l'id especificat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_REPRES') or hasRole('ROLE_AUDIT') or hasRole('ROLE_SUPERAUD')")
	public Page<ProcedimentDto> findAmbFiltrePaginat(
			Long entitatId,
			String codi,
			String nom,
			String departament,
			Long organGestorId,
			String codiSia,
			FiltreActiuEnumDto actiu,
			PaginacioAmbOrdreDto paginacioParams) throws EntitatNotFoundException;

	/**
	 * Llistat amb els procediments d'una entitat.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param codi
	 *            El codi del procediment.
	 * @return El procediment que coincidex amb els paràmetres especificats.
	 * @throws EntitatNotFoundException
	 *             Si no s'ha trobat cap entitat amb l'id especificat.
	 */
	@PreAuthorize("hasRole('ROLE_REPRES') or hasRole('ROLE_REPORT')")
	public ProcedimentDto findAmbEntitatICodi(Long entitatId, String codi) throws EntitatNotFoundException;

	/**
	 * Consulta un procediment donat el seu id.
	 * 
	 * @param id
	 *            Atribut id del procediment a trobar.
	 * @return El procediment trobat. Si no s'ha trobat cap procediment retorna null.
	 */
	@PreAuthorize("hasRole('ROLE_REPRES')")
	public ProcedimentDto findById(Long id);

	/**
	 * Actualitza la informació d'un procediment.
	 * 
	 * @param modificat
	 *            Informació del procediment a modificar.
	 * @return El procediment ja modificat.
	 * @throws ProcedimentNotFoundException
	 *             Si no s'ha trobat cap procediment amb l'id especificat.
	 */
	@PreAuthorize("hasRole('ROLE_REPRES')")
	public ProcedimentDto update(ProcedimentDto modificat) throws ProcedimentNotFoundException;

	/**
	 * Activa o desactiva un procediment.
	 * 
	 * @param id
	 *            Atribut id del procediment a modificar.
	 * @param actiu
	 *            Valor de la propietat actiu a configurar.
	 * @return El procediment ja modificat.
	 * @throws ProcedimentNotFoundException
	 *             Si no s'ha trobat cap procediment amb l'id especificat.
	 */
	@PreAuthorize("hasRole('ROLE_REPRES')")
	public ProcedimentDto updateActiu(Long id, boolean actiu) throws ProcedimentNotFoundException;

	/**
	 * Activa un servei a un procediment.
	 * 
	 * @param id
	 *            Atribut id del procediment.
	 * @param serveiCodi
	 *            Codi del servei.
	 * @throws ProcedimentNotFoundException
	 *            Si no hi ha cap procediment amb l'id especificat.
	 * @throws ServeiNotFoundException
	 *            Si no hi ha cap servei disponible amb el codi especificat.
	 */
	@PreAuthorize("hasRole('ROLE_REPRES')")
	public void serveiEnable(Long id, String serveiCodi) throws ProcedimentNotFoundException, ServeiNotFoundException;
	
	/**
	 * Insereix un codi de procediment adicional
	 * 
	 * @param procedimentId
	 *            Atribut id del procediment.
	 * @param serveiCodi
	 *            Codi del servei.
	 * @param procedimentCodi
	 *            Codi adicional de procediment.
	 * @throws ProcedimentNotFoundException
	 *            Si no hi ha cap procediment amb l'id especificat.
	 * @throws ServeiNotFoundException
	 *            Si no hi ha cap servei disponible amb el codi especificat.
	 */
	@PreAuthorize("hasRole('ROLE_REPRES')")
	public boolean putProcedimentCodi(Long procedimentId,String serveiCodi,String procedimentCodi) throws ProcedimentNotFoundException, ServeiNotFoundException;

	/**
	 * Desactiva un servei d'un procediment.
	 * 
	 * @param id
	 *            Atribut id del procediment.
	 * @param serveiCodi
	 *            Codi del servei.
	 * @throws ProcedimentNotFoundException
	 *            Si no hi ha cap procediment amb l'id especificat.
	 * @throws ProcedimentServeiNotFoundException
	 *            Si el procediment no té aquest servei afegit.
	 */
	@PreAuthorize("hasRole('ROLE_REPRES')")
	public void serveiDisable(Long id, String serveiCodi) throws ProcedimentNotFoundException, ProcedimentServeiNotFoundException;

	/**
	 * Atorga permís per a accedir a un servei d'un procediment
	 * 
	 * @param id
	 *            Atribut id del procediment.
	 * @param serveiCodi
	 *            Codi del servei.
	 * @param usuariCodi
	 *            Codi de l'usuari a qui s'atorga el permís.
	 * @throws ProcedimentNotFoundException
	 *            Si no hi ha cap procediment amb l'id especificat.
	 * @throws ProcedimentServeiNotFoundException
	 *            Si el procediment no té aquest servei afegit.
	 * @throws EntitatUsuariNotFoundException
	 *            Si l'usuari no està lligat a l'entitat del procediment.
	 */
	@PreAuthorize("hasRole('ROLE_REPRES')")
	public void serveiPermisAllow(Long id, String serveiCodi, String usuariCodi) throws ProcedimentNotFoundException, ProcedimentServeiNotFoundException, EntitatUsuariNotFoundException;

	/**
	 * Denega el permís per a accedir a un servei d'un procediment
	 * 
	 * @param id
	 *            Atribut id del procediment.
	 * @param serveiCodi
	 *            Codi del servei.
	 * @param usuariCodi
	 *            Codi de l'usuari a qui s'atorga el permís.
	 * @throws ProcedimentNotFoundException
	 *            Si no hi ha cap procediment amb l'id especificat.
	 * @throws ProcedimentServeiNotFoundException
	 *            Si el procediment no té aquest servei afegit.
	 * @throws EntitatUsuariNotFoundException
	 *            Si l'usuari no està lligat a l'entitat del procediment.
	 */
	@PreAuthorize("hasRole('ROLE_REPRES')")
	public void serveiPermisDeny(Long id, String serveiCodi, String usuariCodi) throws ProcedimentNotFoundException, ProcedimentServeiNotFoundException, EntitatUsuariNotFoundException;

	/**
	 * Denega el permís per a accedir a tots els procediemtn-servei d'un usuari
	 * 
	 * @param usuariCodi
	 *            Codi de l'usuari a qui es deneguen els permisos.
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @throws EntitatUsuariNotFoundException
	 *            Si l'usuari no està lligat a l'entitat.
	 * @param entitatId 
	 */
	@PreAuthorize("hasRole('ROLE_REPRES')")
	public void serveiPermisDenyAll(String usuariCodi, Long entitatId) throws EntitatUsuariNotFoundException;

	@PreAuthorize("hasRole('ROLE_REPRES') or hasRole('ROLE_WS')")
	public void serveiPermisAllowSelected(String usuariCodi, List<ProcedimentServeiSimpleDto> procedimentsServeis, Long entitatId) throws EntitatUsuariNotFoundException, ProcedimentServeiNotFoundException;

	@PreAuthorize("hasRole('ROLE_REPRES')")
	public void serveiPermisDenySelected(String usuariCodi, List<ProcedimentServeiSimpleDto> procedimentsServeis, Long entitatId) throws EntitatUsuariNotFoundException;

	@PreAuthorize("hasRole('ROLE_REPRES')")
	public List<ProcedimentServeiNomDto> serveiDisponibles(String usuariCodi, Long procedimentId, Long entitatId) throws EntitatUsuariNotFoundException;

	/**
	 * Consulta d'usuaris amb permisos per a accedir a un servei determinat.
	 * 
	 * @param id
	 *            Atribut id del procediment.
	 * @param serveiCodi
	 *            Codi del servei.
	 * @return la llista d'usuaris amb accés
	 * @throws ProcedimentNotFoundException
	 *            Si no hi ha cap procediment amb l'id especificat.
	 * @throws ProcedimentServeiNotFoundException
	 *            Si el procediment no té aquest servei afegit.
	 */
	@PreAuthorize("hasRole('ROLE_REPRES')")
	public List<EntitatUsuariDto> findUsuarisAmbPermisPerServei(Long id, String serveiCodi) throws ProcedimentNotFoundException, ProcedimentServeiNotFoundException;

	/**
	 * Consulta d'usuaris amb permisos per a accedir a un servei determinat.
	 *
	 * @param procedimentId
	 *            Atribut id del procediment.
	 * @param serveiCodi
	 *            Codi del servei.
	 * @return la llista d'usuaris amb accés
	 * @throws ProcedimentNotFoundException
	 *            Si no hi ha cap procediment amb l'id especificat.
	 * @throws ProcedimentServeiNotFoundException
	 *            Si el procediment no té aquest servei afegit.
	 */
	@PreAuthorize("hasRole('ROLE_REPRES')")
	public Page<EntitatUsuariDto> findUsuarisAmbPermisPerServei(
			Long procedimentId,
			String serveiCodi,
			String codi,
			String nif,
			String nom,
			Pageable pageable) throws ProcedimentNotFoundException, ProcedimentServeiNotFoundException;

	/**
	 * Consulta dels procediments d'una entitat accessibles per un
	 * determinat delegat.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @return Un llistat de procediments.
	 * @throws EntitatNotFoundException
	 *             Si no s'ha trobat cap entitat amb l'id especificat.
	 */
	@PreAuthorize("hasRole('ROLE_DELEG')")
	public List<ProcedimentDto> findAmbEntitatPerDelegat(Long entitatId) throws EntitatNotFoundException;

	/**
	 * Consulta dels procediments d'una entitat que contenen un servei determinat.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param serveiCodi
	 *            Codi del servei.
	 * @return Un llistat de procediments.
	 * @throws EntitatNotFoundException
	 *             Si no s'ha trobat cap entitat amb l'id especificat.
	 */
	@PreAuthorize("hasRole('ROLE_DELEG')")
	public List<ProcedimentDto> findActiusAmbEntitatIServeiCodi(Long entitatId, String serveiCodi) throws EntitatNotFoundException;

	/**
	 * Dades per a generar l'informe de procediments agrupats per
	 * entitat i departament.
	 * 
	 * @return El llistat de dades per a generar l'informe.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_REPORT')")
	public List<InformeProcedimentDto> informeProcedimentsAgrupatsEntitatDepartament();

	/**
	 * Consulta dels procediments que contenen un servei determinat
	 * @param serveiCodi
	 * 			Codi del servei
	 * @return	Un llistat de procediemtns
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	List<ProcedimentDto> findAmbServeiCodi(String serveiCodi);
	
	/**
	 * Consulta de tots les procediments
	 * 
	 * @return	Un llistat de procediemtns
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<ProcedimentDto> findAll();

	/**
	 * Obté tots els serveis actius que encara no han estat associats al procediment.
	 * Els objectes ServeiDto no estan emplenats completament. ünicament tenen emplenats els camps codi i descripció
	 *
	 * @param procedimentId identificador del procediment
	 * @return Llistat de codi i nom dels serveis
	 */
	@PreAuthorize("hasRole('ROLE_REPRES')")
	List<ServeiDto> serveisDisponiblesPerProcediment(Long procedimentId);
}
