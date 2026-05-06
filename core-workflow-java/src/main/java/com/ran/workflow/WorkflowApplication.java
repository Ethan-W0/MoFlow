package com.ran.workflow;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Slf4j
public class WorkflowApplication {
    public static void main(String[] args) {
        SpringApplication.run(WorkflowApplication.class , args);
    }
    @Bean
    public ApplicationListener<ApplicationReadyEvent> applicationReadyEventListener(){
        return event -> {
            String version = WorkflowApplication.class.getPackage().getImplementationVersion();

            // 打包时没填版本号（比如 IDE 里直接跑），就用 "dev"
            if (version == null || version.isBlank()) {
                version = "dev";
            }
            String port = event.getApplicationContext().getEnvironment().getProperty("local.server.port", "unknown");
            log.info("""

                ========================================
                  Java Workflow Engine Started!
                ========================================
                  Version: {}
                  Port: {}
                  Health: http://localhost:{}/actuator/health
                ========================================

                """, version, port, port);
        };
    }
}
