package com.github.nagyesta.lowkeyvault.example.springcloudazurestarter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Hello {

    private static final Logger LOGGER = LoggerFactory.getLogger(Hello.class);

    public Hello(final @Value("${secret.name}") String name) {
        LOGGER.warn(name);
    }
}
