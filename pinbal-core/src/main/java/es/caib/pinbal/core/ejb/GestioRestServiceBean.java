package es.caib.pinbal.core.ejb;

import es.caib.pinbal.client.procediments.Procediment;
import es.caib.pinbal.client.procediments.ProcedimentPatch;
import es.caib.pinbal.client.serveis.Servei;
import es.caib.pinbal.client.usuaris.FiltreUsuaris;
import es.caib.pinbal.client.usuaris.PermisosServei;
import es.caib.pinbal.client.usuaris.UsuariEntitat;
import es.caib.pinbal.core.service.GestioRestService;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatUsuariNotFoundException;
import es.caib.pinbal.core.service.exception.OrganNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentServeiNotFoundException;
import es.caib.pinbal.core.service.exception.ServeiNotFoundException;
import es.caib.pinbal.core.service.exception.UsuariNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class GestioRestServiceBean implements GestioRestService {

    @Autowired
    GestioRestService delegate;


    @Override
    @RolesAllowed("PBL_WS")
    public Procediment create(Procediment procediment) throws EntitatNotFoundException, OrganNotFoundException {
        return delegate.create(procediment);
    }

    @Override
    @RolesAllowed("PBL_WS")
    public Procediment update(Procediment procediment) throws EntitatNotFoundException, ProcedimentNotFoundException, OrganNotFoundException {
        return delegate.update(procediment);
    }

    @Override
    @RolesAllowed("PBL_WS")
    public Procediment updateParcial(Long procedimentId, ProcedimentPatch procedimentPatch) throws EntitatNotFoundException, ProcedimentNotFoundException, OrganNotFoundException {
        return delegate.updateParcial(procedimentId, procedimentPatch);
    }

    @Override
    @RolesAllowed("PBL_WS")
    public void serveiEnable(Long procedimentId, String serveiCodi) throws ProcedimentNotFoundException, ServeiNotFoundException {
        delegate.serveiEnable(procedimentId, serveiCodi);
    }

    @Override
    @RolesAllowed("PBL_WS")
    public Page<Procediment> findProcedimentsPaginat(String entitatCodi, String codi, String nom, String organGestor, Pageable pageable) throws EntitatNotFoundException, OrganNotFoundException {
        return delegate.findProcedimentsPaginat(entitatCodi, codi, nom, organGestor, pageable);
    }

    @Override
    @RolesAllowed("PBL_WS")
    public Procediment getProcedimentById(Long procedimentId) {
        return delegate.getProcedimentById(procedimentId);
    }

    @Override
    @RolesAllowed("PBL_WS")
    public Procediment getProcedimentAmbEntitatICodi(String entitatCodi, String procedimentCodi) throws EntitatNotFoundException {
        return delegate.getProcedimentAmbEntitatICodi(entitatCodi, procedimentCodi);
    }

    @Override
    @RolesAllowed("PBL_WS")
    public Page<Servei> findServeisByProcedimentPaginat(Long procedimentId, Pageable pageable) throws ProcedimentNotFoundException {
        return delegate.findServeisByProcedimentPaginat(procedimentId, pageable);
    }

    @Override
    @RolesAllowed("PBL_WS")
    public Page<Servei> findServeisPaginat(String codi, String descripcio, Pageable pageable) {
        return delegate.findServeisPaginat(codi, descripcio, pageable);
    }

    @Override
    @RolesAllowed("PBL_WS")
    public Servei getServeiByCodi(String serveiCodi) {
        return delegate.getServeiByCodi(serveiCodi);
    }

    @Override
    @RolesAllowed("PBL_WS")
    public void createOrUpdateUsuari(UsuariEntitat usuariEntitat) throws Exception {
        delegate.createOrUpdateUsuari(usuariEntitat);
    }

    @Override
    @RolesAllowed("PBL_WS")
    public UsuariEntitat getUsuariAmbEntitatICodi(String entitatCodi, String usuariCodi) throws EntitatNotFoundException {
        return delegate.getUsuariAmbEntitatICodi(entitatCodi, usuariCodi);
    }

    @Override
    @RolesAllowed("PBL_WS")
    public void serveiGrantPermis(PermisosServei permisosServei) throws EntitatNotFoundException, EntitatUsuariNotFoundException, ProcedimentServeiNotFoundException {
        delegate.serveiGrantPermis(permisosServei);
    }

    @Override
    @RolesAllowed("PBL_WS")
    public PermisosServei permisosPerUsuariEntitat(String entitatCodi, String usuariCodi) throws EntitatNotFoundException, UsuariNotFoundException {
        return delegate.permisosPerUsuariEntitat(entitatCodi, usuariCodi);
    }

    @Override
    @RolesAllowed("PBL_WS")
    public Page<UsuariEntitat> findUsuarisPaginat(String entitatCodi, FiltreUsuaris filtreUsuaris, Pageable pageable) throws EntitatNotFoundException {
        return delegate.findUsuarisPaginat(entitatCodi, filtreUsuaris, pageable);
    }
}
