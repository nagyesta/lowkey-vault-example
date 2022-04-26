package com.github.nagyesta.lowkeyvault.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;

@Profile("process")
@Configuration
public class AzureAccessTestProcessConfiguration extends AzureAccessTestExternalStartConfiguration implements DisposableBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(AzureAccessTestProcessConfiguration.class);
    private final Process process;

    public AzureAccessTestProcessConfiguration() throws IOException {
        final String jarName = System.getProperty("lowkey-app-jar");
        process = new ProcessBuilder("java", "-jar", jarName, "&").start();
        LOGGER.warn("Starting Jar process (pid: {}): java -jar {} &", process.pid(), jarName);
    }

    @Override
    public void destroy() throws Exception {
        if (process != null && process.isAlive()) {
            LOGGER.warn("Stopping Jar process: {}", process.pid());
            process.destroyForcibly();
        }
    }
}
