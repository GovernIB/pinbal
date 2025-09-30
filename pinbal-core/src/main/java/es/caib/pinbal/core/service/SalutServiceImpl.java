/**
 * 
 */
package es.caib.pinbal.core.service;

import es.caib.comanda.ms.salut.model.AppInfo;
import es.caib.comanda.ms.salut.model.ContextInfo;
import es.caib.comanda.ms.salut.model.IntegracioApp;
import es.caib.comanda.ms.salut.model.IntegracioInfo;
import es.caib.comanda.ms.salut.model.Manual;
import es.caib.comanda.ms.salut.model.SalutInfo;
import es.caib.pinbal.core.model.Servei;
import es.caib.pinbal.core.repository.ServeiRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementació dels mètodes per a gestionar l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class SalutServiceImpl implements SalutService {

//	@Resource
//	private IntegracioHelper integracioHelper;
//	@Resource
//	private CacheHelper cacheHelper;
//	@Resource
//	private PaginacioHelper paginacioHelper;
    @Autowired
    private ServeiRepository serveiRepository;


    @Override
    public List<IntegracioInfo> getIntegracions() {
        List<IntegracioInfo> integracions = new ArrayList<>();
        integracions.add(new IntegracioInfo(IntegracioApp.USR.toString(), IntegracioApp.USR.getNom())); //Dades d'usuaris
        integracions.add(new IntegracioInfo(IntegracioApp.SIG.toString(), IntegracioApp.SIG.getNom())); //Signatura
        integracions.add(new IntegracioInfo(IntegracioApp.PFI.toString(), IntegracioApp.PFI.getNom())); //portafirmes
        integracions.add(new IntegracioInfo(IntegracioApp.DIR.toString(), IntegracioApp.DIR.getNom())); //Unitats organitzatives
        integracions.add(new IntegracioInfo(IntegracioApp.ARX.toString(), IntegracioApp.ARX.getNom())); //Arxiu
        integracions.add(new IntegracioInfo(IntegracioApp.GDC.toString(), IntegracioApp.GDC.getNom())); //Gestor documental - Custòdia
        return integracions;
    }

    @Override
    public List<AppInfo> getSubsistemes() {
        List<AppInfo> subsistemes = new ArrayList<>();
        // Consulta WEB

        // Consulta REST

        // Consultes massives

        // Un subsistema per servei actiu
        List<Servei> serveisActius = serveiRepository.findActius();
        for (Servei servei : serveisActius) {
            subsistemes.add(AppInfo.builder().codi(servei.getCodi()).nom(servei.getDescripcio()).build());
        }

//        subsistemes.add(AppInfo.builder().codi("SUB_EXP").nom("Tramitació d'expedients").build());
//        subsistemes.add(AppInfo.builder().codi("SUB_PRC").nom("Gestió de procediments").build());
//        subsistemes.add(AppInfo.builder().codi("SUB_MAS").nom("Accions massives").build());
//        subsistemes.add(AppInfo.builder().codi("SUB_CPF").nom("Callback PORTAFIB").build());
//        subsistemes.add(AppInfo.builder().codi("SUB_CNB").nom("Callback NOTIB").build());
//        subsistemes.add(AppInfo.builder().codi("SUB_CDI").nom("Callback DISTRIBUCIO").build());
//        subsistemes.add(AppInfo.builder().codi("SUB_GDO").nom("Gestió documental FileSystem").build());
        return subsistemes;
    }

    @Override
    public List<ContextInfo> getContexts(String baseUrl) {
        List<ContextInfo> contexts = new ArrayList<>();
        List<Manual> manuals = new ArrayList<>();
        manuals.add(Manual.builder().nom("Manual d'usuari").path("https://github.com/GovernIB/ripea/raw/ripea-1.0-dev/doc/pdf/01_ripea_manual_usuari.pdf").build());
        manuals.add(Manual.builder().nom("Manual d'administració").path("https://github.com/GovernIB/ripea/raw/ripea-1.0-dev/doc/pdf/02_ripea_manual_administradors.pdf").build());
        contexts.add(ContextInfo.builder()
                .codi("BACK")
                .nom("Backoffice")
                .path(baseUrl + "/ripeaback")
                .manuals(manuals)
                .build());
        contexts.add(ContextInfo.builder()
                .codi("INT")
                .nom("API interna")
                .path(baseUrl + "/ripeaapi/interna")
                .api(baseUrl + "/ripeaapi/interna/rest")
                .build());
        contexts.add(ContextInfo.builder()
                .codi("EXT")
                .nom("API externa")
                .path(baseUrl + "/ripeaapi/externa")
                .api(baseUrl + "/ripeaapi/externa/rest")
                .build());
        return contexts;
    }

    @Override
    public SalutInfo checkSalut(String versio, String performanceUrl) {
        return null;
    }


}
