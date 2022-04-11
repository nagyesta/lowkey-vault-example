package com.github.nagyesta.lowkeyvault.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class LowkeyVaultExampleApplication {

    public static void main(String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(LowkeyVaultExampleApplication.class, args);
    }

}
