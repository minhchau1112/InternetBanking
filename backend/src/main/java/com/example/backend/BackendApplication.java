package com.example.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

@SpringBootApplication(exclude = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
})
public class BackendApplication {

    static {
        // Add BouncyCastle as a security provider
        Security.addProvider(new BouncyCastleProvider());
    }

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication
                .run(BackendApplication.class);
    }

}
