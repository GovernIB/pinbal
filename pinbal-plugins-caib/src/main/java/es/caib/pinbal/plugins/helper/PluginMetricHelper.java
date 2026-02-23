package es.caib.pinbal.plugins.helper;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import es.caib.comanda.ms.salut.helper.EstatHelper;
import es.caib.comanda.ms.salut.model.EstatSalutEnum;
import es.caib.comanda.ms.salut.model.IntegracioApp;
import es.caib.comanda.ms.salut.model.IntegracioPeticions;
import es.caib.comanda.ms.salut.model.IntegracioSalut;
import es.caib.pinbal.helper.LastRequestsFifo;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PluginMetricHelper {


    private static final MetricRegistry registry = new MetricRegistry();
    private static MetricRegistry localRegistry = new MetricRegistry();

    private static final Map<IntegracioApp, Metrics> METRICS = new HashMap<>();


    private static List<IntegracioApp> plugins = new ArrayList<>();
    static {
        plugins.add(IntegracioApp.USR);
        plugins.add(IntegracioApp.SIG);
        plugins.add(IntegracioApp.PFI);
        plugins.add(IntegracioApp.DIR);
        plugins.add(IntegracioApp.ARX);
        plugins.add(IntegracioApp.CUS);

        getMetrica(IntegracioApp.USR);
        getMetrica(IntegracioApp.SIG);
        getMetrica(IntegracioApp.PFI);
        getMetrica(IntegracioApp.DIR);
        getMetrica(IntegracioApp.ARX);
        getMetrica(IntegracioApp.CUS);
    }

    @Getter
    private static class Metrics {
        private static final int MIN_PETICIONS = 20;

        private IntegracioApp plugin;
        private Timer timerOkGlobal;
        private Counter counterErrorGlobal;
        private Timer timerOkLocal;
        private Counter counterErrorLocal;
        private EstatSalutEnum darrerEstat = EstatSalutEnum.UNKNOWN;
        // FIFO de les darreres MIN_PETICIONS peticions
        private final LastRequestsFifo lastRequestsFifo = new LastRequestsFifo(MIN_PETICIONS);
        @Setter
        private String endpoint;

        public Metrics(IntegracioApp plugin) {
            this.plugin = plugin;
            // Globals al registry principal (si disponible)
            this.timerOkGlobal = registry.timer("subsistema." + plugin.name());
            this.counterErrorGlobal = registry.counter("subsistema." + plugin.name() + ".errors");

            // Locals per a salut
            this.timerOkLocal = localRegistry.timer("subsistema." + plugin.name() + ".local");
            this.counterErrorLocal = localRegistry.counter("subsistema." + plugin.name() + ".local.errors");
        }

        // Registra el resultat d'una petició al FIFO de les darreres MIN_PETICIONS
        private void recordOutcome(boolean ok) {
            this.lastRequestsFifo.add(ok);
        }

        public void addSuccess(Long duracio) {
            this.timerOkGlobal.update(duracio, TimeUnit.MILLISECONDS);
            this.timerOkLocal.update(duracio, TimeUnit.MILLISECONDS);
            recordOutcome(true);
        }

        public void addError() {
            this.counterErrorGlobal.inc();
            this.counterErrorLocal.inc();
            recordOutcome(false);
        }

        public void resetLocalTimers() {
            // Reinicia els comptadors locals del període
            localRegistry.remove("subsistema." + plugin.name() + ".local");
            localRegistry.remove("subsistema." + plugin.name() + ".local.errors");
            this.timerOkLocal = localRegistry.timer("subsistema." + plugin.name() + ".local");
            this.counterErrorLocal = localRegistry.counter("subsistema." + plugin.name() + ".local.errors");
        }

        public EstatSalutEnum getEstatPeriode() {
            final long okActual = timerOkLocal.getCount();
            final long koActual = counterErrorLocal.getCount();
            final long totalActual = okActual + koActual;

            double percentErrors;

            // Si el darrer període té 20 o més peticions, utilitzam exclusivament aquest període
            if (totalActual >= MIN_PETICIONS) {
                percentErrors = (koActual * 100.0) / totalActual;
            } else {
                // Si el darrer període té menys de 20 peticions, utilitzam les últimes 20 peticions via FIFO
                int sizeSnapshot = this.lastRequestsFifo.size();
                if (sizeSnapshot == 0) {
                    return darrerEstat;
                }
                percentErrors = this.lastRequestsFifo.getErrorPercent();
            }
            EstatSalutEnum estat = EstatHelper.calculaEstat(percentErrors);
            this.darrerEstat = estat;
            return estat;
        }

        public int getPeriodeMean() {
            return (int) this.timerOkLocal.getMeanRate();
        }

        public long getPeriodeSuccess() {
            return this.timerOkLocal.getCount();
        }

        public long getPeriodeError() {
            return this.counterErrorLocal.getCount();
        }

        public int getTotalMean() {
            return (int) this.timerOkGlobal.getMeanRate();
        }

        public long getTotalSuccess() {
            return this.timerOkGlobal.getCount();
        }

        public long getTotalError() {
            return this.counterErrorGlobal.getCount();
        }

    }


    public static Metrics getMetrica(IntegracioApp plugin) {
        Metrics metrica = METRICS.get(plugin);
        if (metrica == null) {
            metrica = new Metrics(plugin);
            METRICS.put(plugin, metrica);
        }

        return metrica;
    }

    private static void resetLocalTimers() {
        for (Metrics metrica : METRICS.values()) {
            metrica.resetLocalTimers();
        }
    }

    public static void addSuccessOperation(IntegracioApp plugin, long duracio) {
        getMetrica(plugin).addSuccess(duracio);
    }

    public static void addErrorOperation(IntegracioApp plugin) {
        getMetrica(plugin).addError();
    }

    public static void addEndpoint(IntegracioApp plugin, String endpoint) {
        getMetrica(plugin).setEndpoint(endpoint);
    }


    // Obtenció de la informació de salut
    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static List<IntegracioSalut> getIntegracionsSalut() {
        List<IntegracioSalut> integracionsSalut = new ArrayList<>();

        for (Map.Entry<IntegracioApp, Metrics> metricaEntry : METRICS.entrySet()) {

            IntegracioApp plugin = metricaEntry.getKey();
            Metrics metrica = metricaEntry.getValue();

            IntegracioPeticions integracioPeticions = IntegracioPeticions.builder()
                    .totalOk(metrica.getTotalSuccess())
                    .totalError(metrica.getTotalError())
                    .totalTempsMig(metrica.getTotalMean())
                    .peticionsOkUltimPeriode(metrica.getPeriodeSuccess())
                    .peticionsErrorUltimPeriode(metrica.getPeriodeError())
                    .tempsMigUltimPeriode(metrica.getPeriodeMean())
                    .endpoint(metrica.getEndpoint())
                    .build();
            integracionsSalut.add(IntegracioSalut.builder()
                    .codi(plugin.name())
                    .latencia(metrica.getPeriodeMean())
                    .estat(metrica.getEstatPeriode())
                    .peticions(integracioPeticions)
                    .build());
        }

        resetLocalTimers();
        return integracionsSalut;
    }

}
