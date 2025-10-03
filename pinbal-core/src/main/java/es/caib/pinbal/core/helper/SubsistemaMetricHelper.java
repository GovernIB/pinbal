package es.caib.pinbal.core.helper;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import es.caib.comanda.ms.salut.model.EstatByPercent;
import es.caib.comanda.ms.salut.model.EstatSalutEnum;
import es.caib.comanda.ms.salut.model.SubsistemaSalut;
import es.caib.pinbal.helper.LastRequestsFifo;
import lombok.Builder;
import lombok.Getter;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

//@Component
//@RequiredArgsConstructor
public class SubsistemaMetricHelper {


    private static final MetricRegistry registry = new MetricRegistry();
    private static MetricRegistry localRegistry = new MetricRegistry();

    private static final Map<String, Metrics> METRICS = new HashMap<>();
    static {
        for (SubsistemesEnum s : SubsistemesEnum.values()) {
            getMetrica(s.name());
        }
    }


    @Getter
    public enum SubsistemesEnum {
        CWS("Consulta web síncrona", true),
        CRS("Consulta REST síncrona", true),
        CWA("Consulta web asíncrona", false),
        CRA("Consulta REST asíncrona", false),
        JUS("Generar justificant", false);

        private final String nom;
        private final boolean sistemaCritic;

        SubsistemesEnum(String nom, boolean sistemaCritic) {
            this.nom = nom;
            this.sistemaCritic = sistemaCritic;
        }

        public static SubsistemesEnum valueOfCodi(String codi) {
            for (SubsistemesEnum subsistema : SubsistemesEnum.values()) {
                if (subsistema.name().equals(codi)) {
                    return subsistema;
                }
            }
            return null;
        }
    }

    @Getter
    private static class Metrics {
        private static final int MIN_PETICIONS = 20;

        private String nom;
        private Timer timerOkGlobal;
        private Counter counterErrorGlobal;
        private Timer timerOkLocal;
        private Counter counterErrorLocal;
        private EstatSalutEnum darrerEstat = EstatSalutEnum.UNKNOWN;
        // FIFO de les darreres MIN_PETICIONS peticions
        private final LastRequestsFifo lastRequestsFifo = new LastRequestsFifo(MIN_PETICIONS);

        public Metrics(String nom) {
            this.nom = nom;
            // Globals al registry principal (si disponible)
            this.timerOkGlobal = registry.timer("subsistema." + nom);
            this.counterErrorGlobal = registry.counter("subsistema." + nom + ".errors");

            // Locals per a salut
            this.timerOkLocal = localRegistry.timer("subsistema." + nom + ".local");
            this.counterErrorLocal = localRegistry.counter("subsistema." + nom + ".local.errors");
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
            localRegistry.remove("subsistema." + nom + ".local");
            localRegistry.remove("subsistema." + nom + ".local.errors");
            this.timerOkLocal = localRegistry.timer("subsistema." + nom + ".local");
            this.counterErrorLocal = localRegistry.counter("subsistema." + nom + ".local.errors");
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
            EstatSalutEnum estat = EstatByPercent.calculaEstat(percentErrors);
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


    @PostConstruct
    public void init() {
        for (SubsistemesEnum s : SubsistemesEnum.values()) {
            getMetrica(s.name());
        }
    }

    public static Metrics getMetrica(String subsistema) {
        Metrics metrica = METRICS.get(subsistema);
        if (metrica == null) {
            metrica = new Metrics(subsistema);
            METRICS.put(subsistema, metrica);
        }

        return metrica;
    }

    private static void resetLocalTimers() {
        for (Metrics metrica : METRICS.values()) {
            metrica.resetLocalTimers();
        }
    }

    public static void addSuccessOperation(String subsistema, long duracio) {
        getMetrica(subsistema).addSuccess(duracio);
    }

    public static void addSuccessOperation(String subsistema, String servei, long duracio) {
        getMetrica(subsistema).addSuccess(duracio);
        getMetrica(servei).addSuccess(duracio);
    }

    public static void addErrorOperation(String subsistema) {
        getMetrica(subsistema).addError();
    }

    public static void addErrorOperation(String subsistema, String servei) {
        getMetrica(subsistema).addError();
        getMetrica(servei).addError();
    }


    // Obtenció de la informació de salut
    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static SubsistemesInfo getSubsistemesInfo() {
        final List<SubsistemaSalut> subsistemasSalut = getSubsistemesSalut();
        final EstatSalutEnum estatGlobal = calculateGlobalHealth(subsistemasSalut);
        return SubsistemesInfo.builder().subsistemesSalut(subsistemasSalut).estatGlobal(estatGlobal).build();
    }

    private static List<SubsistemaSalut> getSubsistemesSalut() {
        List<SubsistemaSalut> subsistemasSalut = new ArrayList<>();

        for (Map.Entry<String, Metrics> metricaEntry : METRICS.entrySet()) {

            String subsistema = metricaEntry.getKey();
            Metrics metrica = metricaEntry.getValue();

            subsistemasSalut.add(SubsistemaSalut.builder()
                    .codi(subsistema)
                    .latencia(metrica.getPeriodeMean())
                    .estat(metrica.getEstatPeriode())
                    .totalOk(metrica.getTotalSuccess())
                    .totalError(metrica.getTotalError())
                    .totalTempsMig(metrica.getTotalMean())
                    .peticionsOkUltimPeriode(metrica.getPeriodeSuccess())
                    .peticionsErrorUltimPeriode(metrica.getPeriodeError())
                    .tempsMigUltimPeriode(metrica.getPeriodeMean())
                    .build());
        }

        resetLocalTimers();
        return subsistemasSalut;
    }

    private static EstatSalutEnum calculateGlobalHealth(List<SubsistemaSalut> subsistemes) {

        // Ordre de severitat: DOWN > ERROR > DEGRADED > WARN > UP > UNKNOWN
        EstatSalutEnum estatSubsistemes = EstatSalutEnum.UNKNOWN;
        boolean anyDown = false;
        boolean anyError = false;
        boolean anyDegraded = false;
        boolean anyWarn = false;
        boolean anyUp = false;
        boolean allCriticalDown = true;

        for (SubsistemaSalut s : subsistemes) {
            SubsistemesEnum subsistemaEnum = SubsistemesEnum.valueOfCodi(s.getCodi());
            boolean critic = subsistemaEnum != null && subsistemaEnum.isSistemaCritic();

            switch (s.getEstat()) {
                case UP:
                    anyUp = true;
                    if (critic) allCriticalDown = false;
                    break;
                case WARN:
                    anyWarn = true;
                    if (critic) allCriticalDown = false;
                    break;
                case DEGRADED:
                    anyDegraded = true;
                    if (critic) allCriticalDown = false;
                    break;
                case ERROR:
                    anyError = true;
                    if (critic) allCriticalDown = false;
                    break;
                case DOWN:
                    if (critic) {
                        anyDown = true;
                    } else {
                        anyError = true;
                    }
                    break;
                default:
                    // UNKNOWN o altres
            }
        }

        if (allCriticalDown) {
            estatSubsistemes = EstatSalutEnum.DOWN;
        } else if (anyError) {
            estatSubsistemes = EstatSalutEnum.ERROR;
        } else if(anyDegraded) {
            estatSubsistemes =EstatSalutEnum.DEGRADED;
        } else if (anyWarn) {
            estatSubsistemes = EstatSalutEnum.WARN;
        } else if (anyUp) {
            estatSubsistemes = EstatSalutEnum.UP;
        } else {
            estatSubsistemes = EstatSalutEnum.UNKNOWN;
        }

        // Comprovam l'estat dels serveis
        int serveisDownCount = 0;
        int serveisNoDownCount = 0;

        for (Map.Entry<String, Metrics> metricaEntry : METRICS.entrySet()) {
            String codi = metricaEntry.getKey();
            Metrics metrica = metricaEntry.getValue();

            // Si no és un subsistema conegut, és un servei
            if (SubsistemesEnum.valueOfCodi(codi) == null) {
                if (metrica.getEstatPeriode() == EstatSalutEnum.DOWN) {
                    serveisDownCount++;
                } else {
                    serveisNoDownCount++;
                }
            }
        }

        int serveisTotal = serveisDownCount + serveisNoDownCount;
        double percentatgeServeisDown = (serveisDownCount * 100.0) / Math.max(1L, serveisTotal);
        EstatSalutEnum estatServeis = EstatByPercent.calculaEstat(percentatgeServeisDown);

        return EstatByPercent.mergeEstats(estatSubsistemes, estatServeis);
    }

    @Getter
    @Builder
    public static class SubsistemesInfo {
        private final List<SubsistemaSalut> subsistemesSalut;
        private final EstatSalutEnum estatGlobal;
    }

}
