/**
 * 
 */
package es.caib.pinbal.core.service;

import es.caib.pinbal.core.dto.EntitatUsuariDto;
import es.caib.pinbal.core.dto.InformeUsuariDto;
import es.caib.pinbal.core.dto.UsuariDto;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatUsuariNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatUsuariProtegitException;
import es.caib.pinbal.core.service.exception.UsuariExternNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Declaració dels mètodes per al manteniment dels usuaris.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface UsuariService {

	/**
	 * Inicialitza les dades de l'usuari actual a la taula d'usuaris consultant
	 * el plugin de dades d'usuari.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_REPRES') or hasRole('ROLE_DELEG') or hasRole('ROLE_AUDIT') or hasRole('ROLE_SUPERAUD') or hasRole('ROLE_WS')")
	public void inicialitzarUsuariActual();

	/**
	 * Consulta les entitats segons el filtre.
	 * 
	 * @param codi
	 *            Codi a cercar.
	 * @param nom
	 *            Nom a cercar.
	 * @param paginacioAmbOrdre
	 *            Paràmetres per a la paginació i ordenació dels resultats.
	 * @return El llistat d'entitats paginat.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public Page<EntitatUsuariDto> findAmbFiltrePaginat(
			Long id_entitat,
			Boolean isRepresentant,
			Boolean isDelegat,
			Boolean isAuditor,
			Boolean isAplicacio,
			String codi,
			String nom,
			String nif,
			String departament,
			Pageable pageable);
	
	/**
	 * Obté les dades de l'usuari actual del sistema extern definit al plugin
	 * de dades d'usuari.
	 * 
	 * @return Les dades de l'usuari.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_REPRES') or hasRole('ROLE_DELEG') or hasRole('ROLE_AUDIT') or hasRole('ROLE_SUPERAUD') or hasRole('ROLE_WS')")
	public UsuariDto getDades();

	/**
	 * Obté les dades d'un usuari del sistema extern definit al plugin
	 * de dades d'usuari.
	 * 
	 * @param usuariCodi
	 *            El codi de l'usuari.
	 * 
	 * @return Les dades de l'usuari.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_REPRES') or hasRole('ROLE_DELEG') or hasRole('ROLE_AUDIT') or hasRole('ROLE_SUPERAUD') or hasRole('ROLE_WS')")
	public UsuariDto getDades(
			String usuariCodi);

	/**
	 * Actualitza les dades d'un usuari  per a un administrador.
	 * 
	 * @param id
	 *            Atribut id de l'entitat.
	 * @param codi
	 *            Codi de l'usuari.
	 * @param nif
	 *            NIF de l'usuari.
	 * @param departament
	 *            Departament de l'usuari.
	 * @param representant
	 *            Indica si l'usuari és representant de l'entitat.
	 * @param delegat
	 *            Indica si l'usuari és delegat de l'entitat.
	 * @param auditor
	 *            Indica si l'usuari és auditor de l'entitat.
	 * @param aplicacio
	 *            Indica si l'usuari és una aplicació amb accés a l'entitat.
	 * @param afegir
	 *            Si es false els rols s'han de configurar talment. Si és
	 *            true s'han d'afegir.
	 * @param actiu
	 *            Indica si l'usuari està actiu.
	 * @throws EntitatNotFoundException
	 *            Si no hi ha cap entitat amb l'id especificat.
	 * @throws UsuariExternNotFoundException
	 *            Si l'usuari no existeix al sistema extern.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void actualitzarDadesAdmin(
			Long id,
			String codi,
			String nif,
			String departament,
			boolean representant,
			boolean delegat,
			boolean auditor,
			boolean aplicacio,
			boolean afegir,
			boolean actiu) throws EntitatNotFoundException, UsuariExternNotFoundException;

	/**
	 * Actualitza les dades d'un usuari per a un representant.
	 * 
	 * @param id
	 *            Atribut id de l'entitat.
	 * @param codi
	 *            Codi de l'usuari.
	 * @param nif
	 *            NIF de l'usuari.
	 * @param departament
	 *            Departament de l'usuari.
	 * @param representant
	 *            Indica si l'usuari és representant de l'entitat.
	 * @param delegat
	 *            Indica si l'usuari és delegat de l'entitat.
	 * @param aplicacio
	 *            Indica si l'usuari és una aplicació amb accés a l'entitat.
	 * @param afegir
	 *            Si es false els rols s'han de configurar talment. Si és
	 *            true s'han d'afegir.
	 * @param actiu
	 *            Indica si l'usuari està actiu.
	 * @throws EntitatNotFoundException
	 *            Si no hi ha cap entitat amb l'id especificat.
	 * @throws EntitatUsuariProtegitException
	 *            Si s'intenta modificar un usuari principal.
	 * @throws UsuariExternNotFoundException
	 *            Si l'usuari no existeix al sistema extern.
	 */
	@PreAuthorize("hasRole('ROLE_REPRES')")
	public void actualitzarDadesRepresentant(
			Long id,
			String codi,
			String nif,
			String departament,
			boolean representant,
			boolean delegat,
			boolean aplicacio,
			boolean afegir,
			boolean actiu) throws EntitatNotFoundException, EntitatUsuariProtegitException, UsuariExternNotFoundException;

	/**
	 * Actualitza les dades d'un usuari per a un auditor.
	 * 
	 * @param id
	 *            Atribut id de l'entitat.
	 * @param codi
	 *            Codi de l'usuari.
	 * @param nif
	 *            NIF de l'usuari.
	 * @param auditor
	 *            Indica si l'usuari a crear és auditor de l'entitat.
	 * @param afegir
	 *            Si es false els rols s'han de configurar talment. Si és
	 *            true s'han d'afegir.
	 * @throws EntitatNotFoundException
	 *            Si no hi ha cap entitat amb l'id especificat.
	 * @throws EntitatUsuariProtegitException
	 *            Si s'intenta modificar un usuari principal.
	 * @throws UsuariExternNotFoundException
	 *            Si l'usuari no existeix al sistema extern.
	 */
	@PreAuthorize("hasRole('ROLE_AUDIT')")
	public void actualitzarDadesAuditor(
			Long id,
			String codi,
			String nif,
			boolean auditor,
			boolean afegir) throws EntitatNotFoundException, EntitatUsuariProtegitException, UsuariExternNotFoundException;

	/**
	 * Configura un usuari com a principal de l'entitat.
	 * 
	 * @param id
	 *            Atribut id de l'entitat.
	 * @param usuariCodi
	 *            Codi de l'usuari.
	 * @return true si l'usuari ha quedat protegit o false en cas contrari.
	 * @throws EntitatNotFoundException
	 *            Si no hi ha cap entitat amb l'id especificat.
	 * @throws EntitatUsuariNotFoundException
	 *            Si l'entitat no té aquest usuari afegit.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public boolean establirPrincipal(
			Long id,
			String usuariCodi) throws EntitatNotFoundException, EntitatUsuariNotFoundException;

	/**
	 * Activar o desactivar un usuari.
	 *
	 * @param entitatId Atribut id de l'entitat.
	 * @param usuariCodi Codi de l'usuari.
	 *
	 * @return true si s'usuari queda en estat actiu o false en cas contrari.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_REPRES')")
	boolean canviActiu(Long entitatId, String usuariCodi) throws EntitatNotFoundException, EntitatUsuariNotFoundException;

	/**
	 * Retorna les dades necessàries per a generar l'informe d'usuaris.
	 * 
	 * @return Les dades per a generar l'informe.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_REPRES') or hasRole('ROLE_DELEG') or hasRole('ROLE_AUDIT') or hasRole('ROLE_REPORT')")
	public List<InformeUsuariDto> informeUsuarisAgrupatsEntitatDepartament();

	/**
	 * Obté l'usuari actual.
	 * 
	 * @return L'usuari actual.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_REPRES') or hasRole('ROLE_DELEG') or hasRole('ROLE_AUDIT')")
	public UsuariDto getUsuariActual();
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_REPRES') or hasRole('ROLE_DELEG') or hasRole('ROLE_AUDIT')")
	public String getIdiomaUsuariActual();

	/**
	 * Modifica la configuració de l'usuari actual
	 * 
	 * @return L'usuari actual.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_REPRES') or hasRole('ROLE_DELEG') or hasRole('ROLE_AUDIT')")
	public UsuariDto updateUsuariActual(UsuariDto dto, boolean updateEntitat);

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_REPRES') or hasRole('ROLE_DELEG') or hasRole('ROLE_AUDIT') or hasRole('ROLE_SUPERAUD') or hasRole('ROLE_WS')")
    public List<UsuariDto> findLikeCodiONom(String text);

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_REPRES') or hasRole('ROLE_DELEG') or hasRole('ROLE_AUDIT') or hasRole('ROLE_SUPERAUD') or hasRole('ROLE_WS')")
	public List<UsuariDto> findLikeCodiONomONif(String text);

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_REPRES') or hasRole('ROLE_DELEG') or hasRole('ROLE_AUDIT') or hasRole('ROLE_SUPERAUD') or hasRole('ROLE_WS')")
	public EntitatUsuariDto getEntitatUsuari(Long entitatId, String usuariCodi);

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_REPRES') or hasRole('ROLE_DELEG') or hasRole('ROLE_AUDIT') or hasRole('ROLE_SUPERAUD') or hasRole('ROLE_WS')")
    public UsuariDto getUsuariExtern(String codi) throws Exception;

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_REPRES') or hasRole('ROLE_DELEG') or hasRole('ROLE_AUDIT') or hasRole('ROLE_SUPERAUD') or hasRole('ROLE_WS')")
	List<UsuariDto> getUsuarisExterns(String text) throws Exception;
}
