package com.todoapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class TodoAppApplication {

    private static final Logger logger =
            LoggerFactory.getLogger(TodoAppApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(TodoAppApplication.class, args);
    }

    @Bean
    CommandLineRunner logServerPort(Environment env) {
        return args -> {
            String port = env.getProperty("local.server.port");
            if (port == null) {
                port = env.getProperty("server.port", "8080");
            }

            logger.info(
                    "TodoApp backend is running on http://localhost:{}",
                    port
            );
        };
    }
}
