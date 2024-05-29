package com.sky;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
@Slf4j
@EnableCaching //缓存注解功能
@EnableScheduling //开启任务调度
@SpringBootApplication
@EnableTransactionManagement //开启注解方式的事务管理
public class SkyApplication {

    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(SkyApplication.class, args);
        log.info("server started");
        String serverPort = context.getEnvironment().getProperty("server.port");
        String contextPath = context.getEnvironment().getProperty("server.servlet.context-path", "");
        System.out.println("Application is running at: http://localhost:" + serverPort + contextPath);
        System.out.println("http://localhost:8080/doc.html#/home");
    }
}
