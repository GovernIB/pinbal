package es.caib.notib.logic.config;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

@Component("serviceTaskScheduler")
public class TaskScheduler extends ThreadPoolTaskScheduler {

    public TaskScheduler() {
        super();
        setPoolSize(10);
        setThreadNamePrefix("notib-scheduled-task-pool");
    }

}
