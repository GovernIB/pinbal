package es.caib.pinbal.core.service;

import es.caib.pinbal.client.procediments.Procediment;
import es.caib.pinbal.client.procediments.ProcedimentPatch;
import es.caib.pinbal.client.serveis.Servei;
import es.caib.pinbal.client.usuaris.FiltreUsuaris;
import es.caib.pinbal.client.usuaris.PermisosServei;
import es.caib.pinbal.client.usuaris.UsuariEntitat;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatUsuariNotFoundException;
import es.caib.pinbal.core.service.exception.OrganNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentServeiNotFoundException;
import es.caib.pinbal.core.service.exception.ServeiNotFoundException;
import es.caib.pinbal.core.service.exception.UsuariNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Servei per la gestió de procediments i l'assignació de permisos a usuaris
 */
public interface GestioRestService {

    /**
     * Crea un nou procediment.
     *
     * @param procediment
     *            Informació del procediment a crear.
     * @return El procediment creat.
     * @throws EntitatNotFoundException
     *             Si no s'ha trobat cap entitat amb el codi especificat al camp entitatCodi.
     */
    @PreAuthorize("hasRole('ROLE_WS') and hasRole('ROLE_REPRES')")
    public Procediment create(Procediment procediment) throws EntitatNotFoundException, OrganNotFoundException;

    /**
     * Modifica un procediment.
     *
     * @param procediment
     *            Informació del procediment a modificar.
     * @return El procediment creat.
     * @throws EntitatNotFoundException
     *             Si no s'ha trobat cap entitat amb el codi especificat al camp entitatCodi.
     */
    @PreAuthorize("hasRole('ROLE_WS') and hasRole('ROLE_REPRES')")
    public Procediment update(Procediment procediment) throws EntitatNotFoundException, ProcedimentNotFoundException, OrganNotFoundException;

    /**
     * Modifica un procediment.
     *
     * @param procedimentPatch
     *            Informació dels canvis a realitzar al procediment.
     * @return El procediment creat.
     * @throws EntitatNotFoundException
     *             Si no s'ha trobat cap entitat amb el codi especificat al camp entitatCodi.
     */
    @PreAuthorize("hasRole('ROLE_WS') and hasRole('ROLE_REPRES')")
    public Procediment updateParcial(Long procedimentId, ProcedimentPatch procedimentPatch) throws EntitatNotFoundException, ProcedimentNotFoundException, OrganNotFoundException;

    /**
     * Habilita un servei per a un procediment.
     * @param procedimentId ID del procediment.
     * @param serveiCodi Codi del servei.
     */
    @PreAuthorize("hasRole('ROLE_WS') and hasRole('ROLE_REPRES')")
    void serveiEnable(Long procedimentId, String serveiCodi) throws ProcedimentNotFoundException, ServeiNotFoundException;

    /**
     * Recupera procediments amb filtratge i paginació.
     * @param entitatCodi Codi de l'entitat.
     * @param nom Codi del procediment (opcional).
     * @param pageable Informació de paginació.
     * @return Pàgina de procediments.
     */
    @PreAuthorize("hasRole('ROLE_WS') and hasRole('ROLE_REPRES')")
    Page<Procediment> findProcedimentsPaginat(String entitatCodi, String codi, String nom, String organGestor, Pageable pageable) throws EntitatNotFoundException, OrganNotFoundException;

    /**
     * Recupera un procediment pel seu ID.
     * @param procedimentId ID del procediment.
     * @return Dades del procediment.
     */
    @PreAuthorize("hasRole('ROLE_WS') and hasRole('ROLE_REPRES')")
    Procediment getProcedimentById(Long procedimentId);

    /**
     * Recupera un procediment pel seu Codi.
     * @param procedimentCodi Codi del procediment.
     * @param entitatCodi Codi de l'entitat.
     * @return Dades del procediment.
     */
    @PreAuthorize("hasRole('ROLE_WS') and hasRole('ROLE_REPRES')")
    Procediment getProcedimentAmbEntitatICodi(String entitatCodi, String procedimentCodi) throws EntitatNotFoundException;

    /**
     * Recupera procediments amb filtratge i paginació.
     * @param procedimentId ID del procediment.
     * @param pageable Informació de paginació.
     * @return Pàgina de procediments.
     */
    @PreAuthorize("hasRole('ROLE_WS') and hasRole('ROLE_REPRES')")
    Page<Servei> findServeisByProcedimentPaginat(Long procedimentId, Pageable pageable) throws ProcedimentNotFoundException;

    /**
     * Recupera serveis amb filtratge i paginació.
     * @param codi Part del codi del servei. Per filtrar (opcional).
     * @param descripcio Part de la descripcio del servei. Per filtrar (opcional).
     * @param pageable Informació de paginació.
     * @return Pàgina de procediments.
     */
    @PreAuthorize("hasRole('ROLE_WS') and hasRole('ROLE_REPRES')")
    Page<Servei> findServeisPaginat(String codi, String descripcio, Pageable pageable);

    /**
     * Recupera un servei pel seu Codi.
     * @param serveiCodi Codi del servei.
     * @return Dades del procediment.
     */
    @PreAuthorize("hasRole('ROLE_WS') and hasRole('ROLE_REPRES')")
    Servei getServeiByCodi(String serveiCodi);

    /**
     * Crea o actualitza un usuari.
     * @param usuariEntitat Dades de l'usuari.
     */
    @PreAuthorize("hasRole('ROLE_WS') and hasRole('ROLE_REPRES')")
    void createOrUpdateUsuari(UsuariEntitat usuariEntitat) throws Exception;

    /**
     * Recupera usuaris amb filtratge i paginació.
     * @param entitatCodi ID de l'entitat.
     * @param filtreUsuaris filtre per filtrar els usuaris.
     * @param pageable Informació de paginació.
     * @return Pàgina d'usuaris.
     */
    @PreAuthorize("hasRole('ROLE_WS') and hasRole('ROLE_REPRES')")
    Page<UsuariEntitat> findUsuarisPaginat(String entitatCodi, FiltreUsuaris filtreUsuaris, Pageable pageable) throws EntitatNotFoundException ;

    /**
     * Recupera un usuari pel seu Codi.
     * @param usuariCodi Codi de l'usuari'.
     * @param entitatCodi Codi de l'entitat.
     * @return Dades del procediment.
     */
    @PreAuthorize("hasRole('ROLE_WS') and hasRole('ROLE_REPRES')")
    UsuariEntitat getUsuariAmbEntitatICodi(String entitatCodi, String usuariCodi) throws EntitatNotFoundException ;

    /**
     * Atorga permisos seleccionats a un usuari per a procediments i serveis.
     * @param permisosServei Dades dels permisos a atorgar.
     */
    @PreAuthorize("hasRole('ROLE_WS') and hasRole('ROLE_REPRES')")
    void serveiGrantPermis(PermisosServei permisosServei) throws EntitatNotFoundException, EntitatUsuariNotFoundException, ProcedimentServeiNotFoundException;

    /**
     * Recupera permisos associats a un usuari.
     *
     * @param usuariCodi  Codi de l'usuari.
     * @param entitatCodi Codi de l'entitat.
     * @return Llista de recursos HATOAS de tipus PermisDto.
     */
    @PreAuthorize("hasRole('ROLE_WS') and hasRole('ROLE_REPRES')")
    PermisosServei permisosPerUsuariEntitat(String entitatCodi, String usuariCodi) throws EntitatNotFoundException, UsuariNotFoundException;

//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    String executeSql(String sql);
}
