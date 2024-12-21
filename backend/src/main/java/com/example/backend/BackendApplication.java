package com.example.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

//@SpringBootApplication(exclude = {
//        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
//})
@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication
                .run(BackendApplication.class);
    }

}
