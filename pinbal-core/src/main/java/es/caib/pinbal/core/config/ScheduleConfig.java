package es.caib.pinbal.core.config;

import es.caib.pinbal.core.helper.ConfigHelper;
import es.caib.pinbal.core.service.ConsultaService;
import es.caib.pinbal.core.service.HistoricConsultaService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@EnableScheduling
public class ScheduleConfig implements SchedulingConfigurer {

    public static final String REVISAR_ESTAT_PETICIONS_MULTIPLES_DELAY = "es.caib.pinbal.tasca.auto.comprovacio.repeticio";
    public static final String GENERAR_JUSTIFICANTS_DELAY = "es.caib.pinbal.tasca.auto.justificant.repeticio";
    public static final String TANCAR_EXPEDIENTS_CRON = "es.caib.pinbal.tasca.auto.exp.tancar.cron";
    public static final String GENERAR_REPORT_EMAIL_CRON = "es.caib.pinbal.tasca.auto.email.report.estat.cron";
    public static final String ARXIVAR_CONSULTES_CRON = "es.caib.pinbal.tasca.auto.arxivar.consultes.cron";
    public static final String GENERAR_DADES_EXPLOTACIO_CRON = "es.caib.pinbal.tasca.auto.generar.explotacio.cron";

    @Autowired
    private ConsultaService consultaService;
    @Autowired
    private HistoricConsultaService historicConsultaService;
    @Autowired
    TaskScheduler taskScheduler;
    @Autowired
    private ConfigHelper configHelper;

    private ScheduledTaskRegistrar taskRegistrar;

    public void restartSchedulledTasks() {
        if (taskRegistrar != null) {
            taskRegistrar.destroy();
            taskRegistrar.afterPropertiesSet();
        }

    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskScheduler);
        this.taskRegistrar = taskRegistrar;

        // 1. Revisar l'estat de les peticions múltiples pendents per veure si ja han estat processades
        ////////////////////////////////////////////////////////////////
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        log.info("[SCH] autoRevisarEstatPeticionsMultiplesPendents");
                        consultaService.autoRevisarEstatPeticionsMultiplesPendents();
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                        PeriodicTrigger trigger = new PeriodicTrigger(configHelper.getAsLong(REVISAR_ESTAT_PETICIONS_MULTIPLES_DELAY, 600000L), TimeUnit.MILLISECONDS);
                        trigger.setFixedRate(true);
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        return nextExecution;
                    }
                }
        );

        // 2. Generar els justificants pendents de les peticions SCSP ja tramitades
        ////////////////////////////////////////////////////////////////
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        log.info("[SCH] autoGenerarJustificantsPendents");
                        consultaService.autoGenerarJustificantsPendents();
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                        PeriodicTrigger trigger = new PeriodicTrigger(configHelper.getAsLong(GENERAR_JUSTIFICANTS_DELAY, 60000L), TimeUnit.MILLISECONDS);
                        trigger.setFixedRate(true);
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        return nextExecution;
                    }
                }
        );

        // 3. Tancar els expedients pendents que contenen els justificants pendents ja generats
        /////////////////////////////////////////////////////////////////////////
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        log.info("[SCH] autoTancarExpedientsPendents");
                        consultaService.autoTancarExpedientsPendents();
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                        CronTrigger trigger = new CronTrigger(configHelper.getConfig(TANCAR_EXPEDIENTS_CRON, "0 0 2 * * ?"));
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        return nextExecution;
                    }
                }
        );

        // 4. Generar al final del dia un petit report de l'estat de PINBAL, que s'envia per correu als administradors
        /////////////////////////////////////////////////////////////////////////
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        log.info("[SCH] autoGenerarEmailReportEstat");
                        consultaService.autoGenerarEmailReportEstat();
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                        CronTrigger trigger = new CronTrigger(configHelper.getConfig(GENERAR_REPORT_EMAIL_CRON, "0 0 3 * * ?"));
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        return nextExecution;
                    }
                }
        );

        // 5. Arxivar les consultes antigues
        /////////////////////////////////////////////////////////////////////////
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        log.info("[SCH] arxivarConsultesAntigues");
                        historicConsultaService.arxivarConsultesAntigues();
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                        CronTrigger trigger = new CronTrigger(configHelper.getConfig(ARXIVAR_CONSULTES_CRON, "0 0 1 * * ?"));
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        return nextExecution;
                    }
                }
        );

        // 6. Generar dades d'explotació
        /////////////////////////////////////////////////////////////////////////
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        log.info("[SCH] generarDadesExplotacio");
                        consultaService.generarDadesExplotacio();
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                        CronTrigger trigger = new CronTrigger(configHelper.getConfig(GENERAR_DADES_EXPLOTACIO_CRON, "0 30 3 * * ?"));
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        return nextExecution;
                    }
                }
        );

        // 6. Enviar les peticions SCSP pendents
        ////////////////////////////////////////////////////////////////
//        taskRegistrar.addTriggerTask(
//                new Runnable() {
//                    @SneakyThrows
//                    @Override
//                    public void run() {
//                        consultaService.autoEnviarPeticionsPendents();
//                    }
//                },
//                new Trigger() {
//                    @Override
//                    public Date nextExecutionTime(TriggerContext triggerContext) {
//                        PeriodicTrigger trigger = new PeriodicTrigger(10000L, TimeUnit.MILLISECONDS);
//                        trigger.setFixedRate(true);
//                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
//                        return nextExecution;
//                    }
//                }
//        );

    }
}
