/**
 * 
 */
package es.caib.pinbal.core.service;

import es.caib.comanda.ms.salut.model.AppInfo;
import es.caib.comanda.ms.salut.model.ContextInfo;
import es.caib.comanda.ms.salut.model.DetallSalut;
import es.caib.comanda.ms.salut.model.EstatSalut;
import es.caib.comanda.ms.salut.model.EstatSalutEnum;
import es.caib.comanda.ms.salut.model.IntegracioApp;
import es.caib.comanda.ms.salut.model.IntegracioInfo;
import es.caib.comanda.ms.salut.model.IntegracioSalut;
import es.caib.comanda.ms.salut.model.Manual;
import es.caib.comanda.ms.salut.model.MissatgeSalut;
import es.caib.comanda.ms.salut.model.SalutInfo;
import es.caib.comanda.ms.salut.model.SubsistemaSalut;
import es.caib.pinbal.core.helper.MonitorHelper;
import es.caib.pinbal.core.helper.SubsistemaMetricHelper;
import es.caib.pinbal.core.helper.SubsistemaMetricHelper.SubsistemesEnum;
import es.caib.pinbal.core.helper.SubsistemaMetricHelper.SubsistemesInfo;
import es.caib.pinbal.core.model.Avis;
import es.caib.pinbal.core.model.Servei;
import es.caib.pinbal.core.repository.AvisRepository;
import es.caib.pinbal.core.repository.ServeiRepository;
import es.caib.pinbal.plugins.helper.PluginMetricHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
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
    public List<AppInfo> getSubsistemes() {
        List<AppInfo> subsistemes = new ArrayList<>();
        // Subsistemes per consultes i obtenció de justificant
        for(SubsistemesEnum subsistema: SubsistemesEnum.values()) {
            subsistemes.add(AppInfo.builder().codi(subsistema.name()).nom(subsistema.getNom()).build());
        }
        // Un subsistema per servei actiu
        List<Servei> serveisActius = serveiRepository.findActius();
        for (Servei servei : serveisActius) {
            subsistemes.add(AppInfo.builder().codi(servei.getCodi()).nom(servei.getDescripcio()).build());
        }
        return subsistemes;
    }

    @Override
    public List<ContextInfo> getContexts(String baseUrl) {
        List<ContextInfo> contexts = new ArrayList<>();
        List<Manual> manuals = new ArrayList<>();
        manuals.add(Manual.builder().nom("Manual d'usuari administrador").path("https://github.com/GovernIB/pinbal/raw/pinbal-1.4/doc/pdf/01_pinbal_usuari_admin.pdf").build());
        manuals.add(Manual.builder().nom("Manual d'usuari representant").path("https://github.com/GovernIB/pinbal/raw/pinbal-1.4/doc/pdf/02_pinbal_usuari_representant.pdf").build());
        manuals.add(Manual.builder().nom("Manual d'usuari delegat").path("https://github.com/GovernIB/pinbal/raw/pinbal-1.4/doc/pdf/03_pinbal_usuari_delegat.pdf").build());
        manuals.add(Manual.builder().nom("Manual d'usuari auditor").path("https://github.com/GovernIB/pinbal/raw/pinbal-1.4/doc/pdf/04_pinbal_usuari_auditor.pdf").build());
        manuals.add(Manual.builder().nom("Manual d'usuari superauditor").path("https://github.com/GovernIB/pinbal/raw/pinbal-1.4/doc/pdf/05_pinbal_usuari_superauditor.pdf").build());
        manuals.add(Manual.builder().nom("Manual d'integració").path("https://github.com/GovernIB/pinbal/raw/pinbal-1.4/doc/pdf/12_pinbal_integracio_restv2.pdf").build());
        contexts.add(ContextInfo.builder()
                .codi("BACK")
                .nom("Backoffice")
                .path(baseUrl + "/pinbal")
                .manuals(manuals)
                .build());
        contexts.add(ContextInfo.builder()
                .codi("INT")
                .nom("API interna")
                .path(baseUrl + "/pinbalapi/interna")
                .api(baseUrl + "/pinbalapi/interna/api/rest")
                .build());
        contexts.add(ContextInfo.builder()
                .codi("EXT")
                .nom("API externa")
                .path(baseUrl + "/pinbalapi/externa")
                .api(baseUrl + "/pinbalapi/externa/api/rest")
                .build());
        return contexts;
    }

    @Override
    public SalutInfo checkSalut(String versio) {

        EstatSalut estatSalut = checkEstatSalut();                  // Estat
        EstatSalut salutDatabase = checkDatabase();                 // Base de dades
        List<IntegracioSalut> integracions = checkIntegracions();   // Integracions
        SubsistemesInfo subsistemesInfo = checkSubsistemes();       // Subsistemes
        List<SubsistemaSalut> subsistemes = subsistemesInfo.getSubsistemesSalut();
        List<DetallSalut> altres = checkAltres();                   // Altres
        List<MissatgeSalut> missatges = checkMissatges();           // Missatges

        EstatSalutEnum estatGlobalSubsistemes = subsistemesInfo.getEstatGlobal();
        if (EstatSalutEnum.UP.equals(estatSalut.getEstat()) && !EstatSalutEnum.UP.equals(estatGlobalSubsistemes) && !EstatSalutEnum.UNKNOWN.equals(estatGlobalSubsistemes)) {
            estatSalut = EstatSalut.builder()
                    .estat(estatGlobalSubsistemes)
                    .latencia(estatSalut.getLatencia())
                    .build();
        }

        return SalutInfo.builder()
                .codi("PBL")
                .versio(versio)
                .data(new Date())
                .estat(estatSalut)
                .bd(salutDatabase)
                .integracions(integracions)
                .subsistemes(subsistemes)
                .altres(altres)
                .missatges(missatges)
                .build();
    }

    // Comprova l'estat de la pròpia aplicació i mesura una latència mínima d'execució
    private EstatSalut checkEstatSalut() {
        try {
            long start = System.nanoTime();
            // Operació mínima local per avaluar latència de l'aplicació
            // (per exemple, accés a una propietat del sistema)
            String javaVersion = System.getProperty("java.version");
            int latencyMs = (int) ((System.nanoTime() - start) / 1_000_000L);
            return EstatSalut.builder()
                    .estat(EstatSalutEnum.UP)
                    .latencia(latencyMs)
                    .build();
        } catch (Exception e) {
            log.error("Error checkEstatSalut", e);
            return EstatSalut.builder().estat(EstatSalutEnum.DOWN).build();
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
            return EstatSalut.builder()
                    .estat(EstatSalutEnum.UP)
                    .latencia((int) latencyMs)
                    .build();
        } catch (Exception e) {
            log.error("Error checkDatabase", e);
            return EstatSalut.builder().estat(EstatSalutEnum.DOWN).build();
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

    private List<DetallSalut> checkAltres() {
        List<DetallSalut> detalls = new ArrayList<>();
        detalls.add(DetallSalut.builder().codi("PRC").nom("Processadors").valor(String.valueOf(Runtime.getRuntime().availableProcessors())).build());
        detalls.add(DetallSalut.builder().codi("SCPU").nom("Càrrega del sistema").valor(MonitorHelper.getCPULoad()).build());
        detalls.add(DetallSalut.builder().codi("MED").nom("Memòria disponible").valor(MonitorHelper.humanReadableByteCount(Runtime.getRuntime().freeMemory())).build());
        detalls.add(DetallSalut.builder().codi("MET").nom("Memòria total").valor(MonitorHelper.humanReadableByteCount(Runtime.getRuntime().totalMemory())).build());

        long totalSpace = 0L;
        long freeSpace = 0L;
        try {
            File root = new File("/");
            totalSpace = root.getTotalSpace();
            freeSpace = root.getFreeSpace();
        } catch (Exception ignore) {
        }
        detalls.add(DetallSalut.builder().codi("EDT").nom("Espai de disc total").valor(MonitorHelper.humanReadableByteCount(totalSpace)).build());
        detalls.add(DetallSalut.builder().codi("EDL").nom("Espai de disc lliure").valor(MonitorHelper.humanReadableByteCount(freeSpace)).build());
        String os = System.getProperty("os.name") + " " + System.getProperty("os.version") + " (" + System.getProperty("os.arch") + ")";
        detalls.add(DetallSalut.builder().codi("SO").nom("Sistema operatiu").valor(os).build());

        return detalls;
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
        return MissatgeSalut.builder()
                .missatge(avis.getMissatge())
                .data(avis.getDataInici())
                .nivell(avis.getAvisNivell().name())
                .build();
    }

}
