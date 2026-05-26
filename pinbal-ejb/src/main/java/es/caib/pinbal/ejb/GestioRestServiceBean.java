package es.caib.pinbal.ejb;

import es.caib.pinbal.client.procediments.Procediment;
import es.caib.pinbal.client.procediments.ProcedimentPatch;
import es.caib.pinbal.client.serveis.Servei;
import es.caib.pinbal.client.usuaris.FiltreUsuaris;
import es.caib.pinbal.client.usuaris.PermisosServei;
import es.caib.pinbal.client.usuaris.UsuariEntitat;
import es.caib.pinbal.logic.intf.service.exception.*;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

@Primary
@Stateless
public class GestioRestServiceBean extends AbstractService<es.caib.pinbal.logic.intf.service.GestioRestService> implements es.caib.pinbal.logic.intf.service.GestioRestService {

    @Override
    @RolesAllowed("PBL_WS")
    public Procediment create(Procediment procediment) throws EntitatNotFoundException, OrganNotFoundException {
        return getDelegateService().create(procediment);
    }

    @Override
    @RolesAllowed("PBL_WS")
    public Procediment update(Procediment procediment) throws EntitatNotFoundException, ProcedimentNotFoundException, OrganNotFoundException {
        return getDelegateService().update(procediment);
    }

    @Override
    @RolesAllowed("PBL_WS")
    public Procediment updateParcial(Long procedimentId, ProcedimentPatch procedimentPatch) throws EntitatNotFoundException, ProcedimentNotFoundException, OrganNotFoundException {
        return getDelegateService().updateParcial(procedimentId, procedimentPatch);
    }

    @Override
    @RolesAllowed("PBL_WS")
    public void serveiEnable(Long procedimentId, String serveiCodi) throws ProcedimentNotFoundException, ServeiNotFoundException {
        getDelegateService().serveiEnable(procedimentId, serveiCodi);
    }

    @Override
    @RolesAllowed("PBL_WS")
    public Page<Procediment> findProcedimentsPaginat(String entitatCodi, String codi, String nom, String organGestor, Pageable pageable) throws EntitatNotFoundException, OrganNotFoundException {
        return getDelegateService().findProcedimentsPaginat(entitatCodi, codi, nom, organGestor, pageable);
    }

    @Override
    @RolesAllowed("PBL_WS")
    public Procediment getProcedimentById(Long procedimentId) {
        return getDelegateService().getProcedimentById(procedimentId);
    }

    @Override
    @RolesAllowed("PBL_WS")
    public Procediment getProcedimentAmbEntitatICodi(String entitatCodi, String procedimentCodi) throws EntitatNotFoundException {
        return getDelegateService().getProcedimentAmbEntitatICodi(entitatCodi, procedimentCodi);
    }

    @Override
    @RolesAllowed("PBL_WS")
    public Page<Servei> findServeisByProcedimentPaginat(Long procedimentId, Pageable pageable) throws ProcedimentNotFoundException {
        return getDelegateService().findServeisByProcedimentPaginat(procedimentId, pageable);
    }

    @Override
    @RolesAllowed("PBL_WS")
    public Page<Servei> findServeisPaginat(String codi, String descripcio, Pageable pageable) {
        return getDelegateService().findServeisPaginat(codi, descripcio, pageable);
    }

    @Override
    @RolesAllowed("PBL_WS")
    public Servei getServeiByCodi(String serveiCodi) {
        return getDelegateService().getServeiByCodi(serveiCodi);
    }

    @Override
    @RolesAllowed("PBL_WS")
    public void createOrUpdateUsuari(UsuariEntitat usuariEntitat) throws Exception {
        getDelegateService().createOrUpdateUsuari(usuariEntitat);
    }

    @Override
    @RolesAllowed("PBL_WS")
    public UsuariEntitat getUsuariAmbEntitatICodi(String entitatCodi, String usuariCodi) throws EntitatNotFoundException {
        return getDelegateService().getUsuariAmbEntitatICodi(entitatCodi, usuariCodi);
    }

    @Override
    @RolesAllowed("PBL_WS")
    public void serveiGrantPermis(PermisosServei permisosServei) throws EntitatNotFoundException, EntitatUsuariNotFoundException, ProcedimentServeiNotFoundException {
        getDelegateService().serveiGrantPermis(permisosServei);
    }

    @Override
    @RolesAllowed("PBL_WS")
    public PermisosServei permisosPerUsuariEntitat(String entitatCodi, String usuariCodi) throws EntitatNotFoundException, UsuariNotFoundException {
        return getDelegateService().permisosPerUsuariEntitat(entitatCodi, usuariCodi);
    }

//    @Override
//    @RolesAllowed("PBL_ADMIN")
//    public String executeSql(String sql) {
//        return getDelegateService().executeSql(sql);
//    }

    @Override
    @RolesAllowed("PBL_WS")
    public Page<UsuariEntitat> findUsuarisPaginat(String entitatCodi, FiltreUsuaris filtreUsuaris, Pageable pageable) throws EntitatNotFoundException {
        return getDelegateService().findUsuarisPaginat(entitatCodi, filtreUsuaris, pageable);
    }
}
