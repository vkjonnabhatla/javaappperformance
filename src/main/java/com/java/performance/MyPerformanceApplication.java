package com.java.performance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.ClassUtils;

import java.io.Closeable;
import java.io.IOException;

@SpringBootApplication
public class MyPerformanceApplication {

    private static ConfigurableApplicationContext appContext;


    public static void main(String[] args) {
        appContext = SpringApplication.run(MyPerformanceApplication.class, args);
    }

    @Bean
    public TaskExecutor getTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("MyTaskExecutor-");
        executor.initialize();
        return executor;
    }

    //Programmatically Restarting a Spring Boot Application
    public static void restart(){


        Thread th = new Thread(() -> {
            ApplicationArguments args = appContext.getBean(ApplicationArguments.class);
            //SpringApplication springApplication = new SpringApplication();
            //springApplication.setEnvironment(appContext.getEnvironment());
            //appContext.registerShutdownHook();
            appContext.close();
            //ClassUtils.overrideThreadContextClassLoader(springApplication.getClass().getClassLoader());
            SpringApplication.run(MyPerformanceApplication.class, args.getSourceArgs());
        });

        th.setDaemon(false);
        th.start();
    }

    private static void close() {
        ApplicationContext context = appContext;
        while (context instanceof Closeable) {
            try {
                ((Closeable) context).close();
            }
            catch (IOException e) {
                System.out.println("Cannot close context: ");
            }
            context = context.getParent();
        }
    }
}
