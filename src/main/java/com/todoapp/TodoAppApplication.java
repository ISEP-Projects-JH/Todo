package com.todoapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class TodoAppApplication {
    
    private static final Logger logger = LoggerFactory.getLogger(TodoAppApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(TodoAppApplication.class, args);
        logger.info("TodoApp backend is running on http://localhost:8000");
    }
}
