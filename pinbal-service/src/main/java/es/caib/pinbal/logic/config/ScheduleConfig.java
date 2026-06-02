package es.caib.pinbal.logic.config;

import es.caib.pinbal.logic.helper.ConfigHelper;
import es.caib.pinbal.logic.intf.service.ConsultaService;
import es.caib.pinbal.logic.intf.service.HistoricConsultaService;
import es.caib.pinbal.logic.intf.service.IntegracioAccioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableScheduling
public class ScheduleConfig implements SchedulingConfigurer {

    public static final String REVISAR_ESTAT_PETICIONS_MULTIPLES_DELAY = "es.caib.pinbal.tasca.auto.comprovacio.repeticio";
    public static final String GENERAR_JUSTIFICANTS_DELAY = "es.caib.pinbal.tasca.auto.justificant.repeticio";
    public static final String CONSULTA_PENDENTS_DELAY = "es.caib.pinbal.tasca.auto.consulta.pendent.repeticio";
    public static final String TANCAR_EXPEDIENTS_CRON = "es.caib.pinbal.tasca.auto.exp.tancar.cron";
    public static final String ESBORRAR_CONTINGUT_INTEGRACIO = "es.caib.pinbal.tasca.auto.exp.esborrar.monitor";
    public static final String GENERAR_REPORT_EMAIL_CRON = "es.caib.pinbal.tasca.auto.email.report.estat.cron";
    public static final String ARXIVAR_CONSULTES_CRON = "es.caib.pinbal.tasca.auto.arxivar.consultes.cron";
    public static final String GENERAR_DADES_EXPLOTACIO_CRON = "es.caib.pinbal.tasca.auto.generar.explotacio.cron";

    private final ConsultaService consultaService;
    private final HistoricConsultaService historicConsultaService;
    private final IntegracioAccioService integracioAccioService;
    private final ConfigHelper configHelper;


    @Bean("serviceTaskScheduler")
    public ThreadPoolTaskScheduler serviceTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.setThreadNamePrefix("pinbal-scheduled-task-pool");
        return scheduler;
    }

    private ScheduledTaskRegistrar taskRegistrar;

    public void restartSchedulledTasks() {
        if (taskRegistrar != null) {
            taskRegistrar.destroy();
            taskRegistrar.afterPropertiesSet();
        }

    }

    private void registerSchedulledTasks() {

        Supplier<ConsultaService> consultaServiceSupplier = () -> consultaService;
        Supplier<HistoricConsultaService> historicConsultaServiceSupplier = () -> historicConsultaService;
        Supplier<IntegracioAccioService> integracioAccioServiceSupplier = () -> integracioAccioService;

        // 1. Revisar l'estat de les peticions múltiples pendents per veure si ja han estat processades
        /////////////////////////////////////////////////////////////////////////
        registerPeriodicTask(
                "autoRevisarEstatPeticionsMultiplesPendents",
                consultaServiceSupplier,
                (Supplier<ConsultaService> s) -> s.get().autoRevisarEstatPeticionsMultiplesPendents(),
                REVISAR_ESTAT_PETICIONS_MULTIPLES_DELAY,
                600000L);

        // 2. Generar els justificants pendents de les peticions SCSP ja tramitades
        /////////////////////////////////////////////////////////////////////////
        registerPeriodicTask(
                "autoGenerarJustificantsPendents",
                consultaServiceSupplier,
                (Supplier<ConsultaService> s) -> s.get().autoGenerarJustificantsPendents(),
                GENERAR_JUSTIFICANTS_DELAY,
                60000L);

        // 3. Tancar els expedients pendents que contenen els justificants pendents ja generats
        /////////////////////////////////////////////////////////////////////////
        registerCronTask(
                "autoTancarExpedientsPendents",
                consultaServiceSupplier,
                (Supplier<ConsultaService> s) -> s.get().autoTancarExpedientsPendents(),
                TANCAR_EXPEDIENTS_CRON,
                "0 0 2 * * ?");

        // 4. Generar al final del dia un petit report de l'estat de PINBAL, que s'envia per correu als administradors
        /////////////////////////////////////////////////////////////////////////
        registerCronTask(
                "autoGenerarEmailReportEstat",
                consultaServiceSupplier,
                (Supplier<ConsultaService> s) -> s.get().autoGenerarEmailReportEstat(),
                GENERAR_REPORT_EMAIL_CRON,
                "0 0 3 * * ?");

        // 5. Arxivar les consultes antigues
        /////////////////////////////////////////////////////////////////////////
        registerCronTask(
                "arxivarConsultesAntigues",
                historicConsultaServiceSupplier,
                (Supplier<HistoricConsultaService> s) -> s.get().arxivarConsultesAntigues(),
                ARXIVAR_CONSULTES_CRON,
                "0 0 1 * * ?");

        // 6. Generar dades d'explotació
        /////////////////////////////////////////////////////////////////////////
        registerCronTask(
                "generarDadesExplotacio",
                consultaServiceSupplier,
                (Supplier<ConsultaService> s) -> s.get().generarDadesExplotacio(),
                GENERAR_DADES_EXPLOTACIO_CRON,
                "0 30 3 * * ?");

        // 7. Esborrar el monitor d'integracions
        /////////////////////////////////////////////////////////////////////////
        registerPeriodicTask(
                "esborrarDadesAntigesMonitorIntegracio",
                integracioAccioServiceSupplier,
                (Supplier<IntegracioAccioService> s) -> s.get().esborrarDadesAntigesMonitorIntegracio(),
                ESBORRAR_CONTINGUT_INTEGRACIO,
                3600L,
                TimeUnit.SECONDS);

        // 8. Enviar les peticions SCSP pendents
        /////////////////////////////////////////////////////////////////////////
        registerPeriodicTask(
                "autoEnviarPeticionsPendents",
                consultaServiceSupplier,
                (Supplier<ConsultaService> s) -> s.get().autoEnviarPeticionsPendents(),
                CONSULTA_PENDENTS_DELAY,
                10000L,
                TimeUnit.SECONDS);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(serviceTaskScheduler());
        this.taskRegistrar = taskRegistrar;
        configHelper.schedullingLoadDbProperties();
        registerSchedulledTasks();
    }

    private <T> void registerCronTask(String taskName,
                                      Supplier<T> supplier,
                                      Consumer<Supplier<T>> method,
                                      String cronConfig,
                                      String defualtCron) {
        taskRegistrar.addTriggerTask(
                () -> executeSchedulledMethod(supplier, method, taskName),
                triggerContext -> {
                    // Creating a trigger here
                    var trigger = cronConfig != null ? new CronTrigger(configHelper.getConfig(cronConfig, defualtCron)) : new CronTrigger(defualtCron);
                    // Compute the next execution time
                    return trigger.nextExecutionTime(triggerContext);
                });
    }

    private <T> void registerPeriodicTask(String taskName,
                                          Supplier<T> supplier,
                                          Consumer<Supplier<T>> method,
                                          String periodeConfig,
                                          Long defualtPeriode) {
        registerPeriodicTask(taskName, supplier, method, periodeConfig, defualtPeriode, TimeUnit.MILLISECONDS);
    }
    private <T> void registerPeriodicTask(String taskName,
                                          Supplier<T> supplier,
                                          Consumer<Supplier<T>> method,
                                          String periodeConfig,
                                          Long defualtPeriode,
                                          TimeUnit timeUnit) {
        taskRegistrar.addTriggerTask(
                () -> executeSchedulledMethod(supplier, method, taskName),
                triggerContext -> {
                    // Creating a trigger here
                    PeriodicTrigger trigger = new PeriodicTrigger(configHelper.getConfigAsLong(periodeConfig, defualtPeriode), timeUnit);
                    trigger.setFixedRate(true);
                    // Compute the next execution time
                    Date nextExecution = trigger.nextExecutionTime(triggerContext);
                    return nextExecution;
                });
    }

    private <T> void executeSchedulledMethod(
            Supplier<T> supplier,
            Consumer<Supplier<T>> method,
            String methodToExecute){
        try {
//            Supplier<SchedulledService> schedulledServiceSupplier = () -> schedulledService;
            log.info("[SCH] Iniciant tasca en segon pla: " + methodToExecute);
            method.accept(supplier);
        } catch(Exception e) {
            log.error("[SCH] Error en tasca en segon pla: " + methodToExecute, e);
        }
    }

}
