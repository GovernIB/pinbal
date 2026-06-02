/**
 * 
 */
package es.caib.pinbal.logic.service;

import es.caib.comanda.model.server.monitoring.ContextInfo;
import es.caib.comanda.model.server.monitoring.EstatSalut;
import es.caib.comanda.model.server.monitoring.EstatSalutEnum;
import es.caib.comanda.model.server.monitoring.FitxerContingut;
import es.caib.comanda.model.server.monitoring.FitxerInfo;
import es.caib.comanda.model.server.monitoring.InformacioSistema;
import es.caib.comanda.model.server.monitoring.IntegracioInfo;
import es.caib.comanda.model.server.monitoring.IntegracioSalut;
import es.caib.comanda.model.server.monitoring.Manual;
import es.caib.comanda.model.server.monitoring.MissatgeSalut;
import es.caib.comanda.model.server.monitoring.SalutInfo;
import es.caib.comanda.model.server.monitoring.SalutNivell;
import es.caib.comanda.model.server.monitoring.SubsistemaInfo;
import es.caib.comanda.model.server.monitoring.SubsistemaSalut;
import es.caib.comanda.ms.log.helper.LogFileStream;
import es.caib.comanda.ms.log.helper.LogHelper;
import es.caib.comanda.ms.salut.helper.IntegracioApp;
import es.caib.comanda.ms.salut.helper.MonitorHelper;
import es.caib.pinbal.logic.helper.ConfigHelper;
import es.caib.pinbal.logic.helper.SubsistemaMetricHelper;
import es.caib.pinbal.logic.helper.SubsistemaMetricHelper.SubsistemesEnum;
import es.caib.pinbal.logic.helper.SubsistemaMetricHelper.SubsistemesInfo;
import es.caib.pinbal.logic.intf.service.SalutService;
import es.caib.pinbal.persist.entity.Avis;
import es.caib.pinbal.persist.entity.Servei;
import es.caib.pinbal.persist.repository.AvisRepository;
import es.caib.pinbal.persist.repository.ServeiRepository;
import es.caib.pinbal.plugin.PluginMetricHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Implementació dels mètodes per a gestionar l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class SalutServiceImpl implements SalutService {

    @Autowired
    private ServeiRepository serveiRepository;
    @Autowired
    private AvisRepository avisRepository;
    @Autowired
    private ConfigHelper configHelper;


    @Override
    public List<IntegracioInfo> getIntegracions() {
        List<IntegracioInfo> integracions = new ArrayList<>();
        integracions.add(new IntegracioInfo(IntegracioApp.USR.toString(), IntegracioApp.USR.getNom())); //Dades d'usuaris
        integracions.add(new IntegracioInfo(IntegracioApp.SIG.toString(), IntegracioApp.SIG.getNom())); //Signatura
        integracions.add(new IntegracioInfo(IntegracioApp.PFI.toString(), IntegracioApp.PFI.getNom())); //portafirmes
        integracions.add(new IntegracioInfo(IntegracioApp.DIR.toString(), IntegracioApp.DIR.getNom())); //Unitats organitzatives
        integracions.add(new IntegracioInfo(IntegracioApp.ARX.toString(), IntegracioApp.ARX.getNom())); //Arxiu
        integracions.add(new IntegracioInfo(IntegracioApp.CUS.toString(), IntegracioApp.CUS.getNom())); //Custòdia
        return integracions;
    }

    @Override
    public List<SubsistemaInfo> getSubsistemes() {
        List<SubsistemaInfo> subsistemes = new ArrayList<>();
        // Subsistemes per consultes i obtenció de justificant
        for(SubsistemesEnum subsistema: SubsistemesEnum.values()) {
            subsistemes.add(new SubsistemaInfo().codi(subsistema.name()).nom(subsistema.getNom()));
        }
        // Un subsistema per servei actiu
        List<Servei> serveisActius = serveiRepository.findActius();
        for (Servei servei : serveisActius) {
            subsistemes.add(new SubsistemaInfo().codi(servei.getCodi()).nom(servei.getDescripcio()));
        }
        return subsistemes;
    }

    @Override
    public List<ContextInfo> getContexts(String baseUrl) {
        List<ContextInfo> contexts = new ArrayList<>();
        List<Manual> manuals = new ArrayList<>();
        manuals.add(new Manual().nom("Manual d'usuari administrador").path("https://github.com/GovernIB/pinbal/raw/pinbal-1.4/doc/pdf/01_pinbal_usuari_admin.pdf"));
        manuals.add(new Manual().nom("Manual d'usuari representant").path("https://github.com/GovernIB/pinbal/raw/pinbal-1.4/doc/pdf/02_pinbal_usuari_representant.pdf"));
        manuals.add(new Manual().nom("Manual d'usuari delegat").path("https://github.com/GovernIB/pinbal/raw/pinbal-1.4/doc/pdf/03_pinbal_usuari_delegat.pdf"));
        manuals.add(new Manual().nom("Manual d'usuari auditor").path("https://github.com/GovernIB/pinbal/raw/pinbal-1.4/doc/pdf/04_pinbal_usuari_auditor.pdf"));
        manuals.add(new Manual().nom("Manual d'usuari superauditor").path("https://github.com/GovernIB/pinbal/raw/pinbal-1.4/doc/pdf/05_pinbal_usuari_superauditor.pdf"));
        manuals.add(new Manual().nom("Manual d'integració").path("https://github.com/GovernIB/pinbal/raw/pinbal-1.4/doc/pdf/12_pinbal_integracio_restv2.pdf"));
        contexts.add(new ContextInfo()
                .codi("BACK")
                .nom("Backoffice")
                .path(baseUrl + "/pinbal")
                .manuals(manuals));
        contexts.add(new ContextInfo()
                .codi("INT")
                .nom("API interna")
                .path(baseUrl + "/pinbalapi/interna")
                .api(baseUrl + "/pinbalapi/interna/api/rest"));
        contexts.add(new ContextInfo()
                .codi("EXT")
                .nom("API externa")
                .path(baseUrl + "/pinbalapi/externa")
                .api(baseUrl + "/pinbalapi/externa/api/rest"));
        return contexts;
    }

    @Override
    public SalutInfo checkSalut(String versio) {

        EstatSalut estatSalut = checkEstatSalut();                  // Estat
        EstatSalut salutDatabase = checkDatabase();                 // Base de dades
        List<IntegracioSalut> integracions = checkIntegracions();   // Integracions
        SubsistemesInfo subsistemesInfo = checkSubsistemes();       // Subsistemes
        List<SubsistemaSalut> subsistemes = subsistemesInfo.getSubsistemesSalut();
        InformacioSistema infoSistema = MonitorHelper.getInfoSistema(); // Informació sistemes
        List<MissatgeSalut> missatges = checkMissatges();           // Missatges

        // So l'estat dels subsistemes no és UP, llavors l'estat de l'aplicació tampoc ho serà
        EstatSalutEnum estatGlobalSubsistemes = subsistemesInfo.getEstatGlobal();
        if (EstatSalutEnum.UP.equals(estatSalut.getEstat()) && !EstatSalutEnum.UP.equals(estatGlobalSubsistemes) && !EstatSalutEnum.UNKNOWN.equals(estatGlobalSubsistemes)) {
            estatSalut = new EstatSalut()
                    .estat(estatGlobalSubsistemes)
                    .latencia(estatSalut.getLatencia());
        }

        return new SalutInfo()
                .codi("PBL")
                .versio(versio)
                .data(OffsetDateTime.now())
                .estatGlobal(estatSalut)
                .estatBaseDeDades(salutDatabase)
                .integracions(integracions)
                .subsistemes(subsistemes)
                .informacioSistema(infoSistema)
                .missatges(missatges);
    }


    // Comprova l'estat de la pròpia aplicació i mesura una latència mínima d'execució
    private EstatSalut checkEstatSalut() {
        try {
            long start = System.nanoTime();
            // Operació mínima local per avaluar latència de l'aplicació
            // (per exemple, accés a una propietat del sistema)
            String javaVersion = System.getProperty("java.version");
            int latencyMs = (int) ((System.nanoTime() - start) / 1_000_000L);
            return new EstatSalut()
                    .estat(EstatSalutEnum.UP)
                    .latencia(latencyMs);
        } catch (Exception e) {
            log.error("Error checkEstatSalut", e);
            return new EstatSalut().estat(EstatSalutEnum.DOWN);
        }
    }


    // Comprovació de la base de dades: es mesura el temps d'execució d'una consulta molt lleugera
    // per determinar la latència i l'estat (UP/ERROR). S'empra el repository existent per evitar
    // dependències addicionals de DataSource i mantenir canvis mínims.
    private EstatSalut checkDatabase() {
        try {
            long start = System.currentTimeMillis();
            // Operació mínima que obliga a obrir connexió i fer una consulta contra la BBDD
            serveiRepository.count();
            long latencyMs = System.currentTimeMillis() - start;

            // Construcció d'EstatSalut amb la informació bàsica
            return new EstatSalut()
                    .estat(EstatSalutEnum.UP)
                    .latencia((int) latencyMs);
        } catch (Exception e) {
            log.error("Error checkDatabase", e);
            return new EstatSalut().estat(EstatSalutEnum.DOWN);
        }
    }

    private List<IntegracioSalut> checkIntegracions() {

        List<IntegracioSalut> integracionsSalut = new ArrayList<>();
        try {
            integracionsSalut = PluginMetricHelper.getIntegracionsSalut();
        } catch (Exception e) {
            log.error("Error checkIntegracions", e);
            return Collections.emptyList();
        }
        return integracionsSalut;
    }

    private SubsistemesInfo checkSubsistemes() {

        SubsistemesInfo subsistemesSalut = null;
        try {
            subsistemesSalut = SubsistemaMetricHelper.getSubsistemesInfo();
        } catch (Exception e) {
            log.error("Error checkIntegracions", e);
        }
        return subsistemesSalut;
    }

    private List<MissatgeSalut> checkMissatges() {

        List<MissatgeSalut> missatges = new ArrayList<>();
        try {
            List<Avis> avisos = avisRepository.findActive(DateUtils.truncate(new Date(), Calendar.DATE));
            if (avisos != null && !avisos.isEmpty()) {
                for (Avis avis: avisos) {
                    missatges.add(toMissatgeSalut(avis));
                }
            }
            return missatges;
        } catch (Exception e) {
            log.error("Error checkMissatges", e);
            return Collections.emptyList();
        }
    }

    private MissatgeSalut toMissatgeSalut(Avis avis) {
        return new MissatgeSalut()
                .missatge(avis.getMissatge())
                .data(avis.getDataInici().toInstant().atOffset(ZoneOffset.UTC))
                .nivell(toSalutNivell(avis));
    }

    private SalutNivell toSalutNivell(Avis avis) {
        switch (avis.getAvisNivell()) {
            case WARNING: return SalutNivell.WARN;
            case ERROR: return SalutNivell.ERROR;
            case INFO:
            default: return SalutNivell.INFO;
        }
    }


    // LOGS
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public List<FitxerInfo> getFitxersLog() {
        return LogHelper.llistarFitxers(getLogDir(), "pinbal");
    }

    @Override
    public FitxerContingut getFitxerLogByNom(String nom) {
        LogHelper.setAppNom("pinbal");
        return LogHelper.getFitxerByNom(getLogDir(), nom);
    }

    @Override
    public LogFileStream getFitxerLogStream(String nom) {
        LogHelper.setAppNom("pinbal");
        return LogHelper.getFileStreamByNom(getLogDir(), nom);
    }

    @Override
    public List<String> getFitxerLogLinies(String nom, Long nLinies) {
        LogHelper.setAppNom("pinbal");
        return LogHelper.readLastNLines(getLogDir(), nom, nLinies);
    }

    private String getLogDir() {
        return configHelper.getConfig("es.caib.pinbal.log.dir");
    }

}
