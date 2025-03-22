package com.github.nagyesta.lowkeyvault.example.micronaut;

import io.micronaut.runtime.Micronaut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ApplicationLocal {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationLocal.class);

    public static void main(final String[] args) {
        try (var lowkeyVault = MicronautAkvDemoTest.lowkeyVaultContainer()) {
            lowkeyVault.start();
            final var context = Micronaut.run(ApplicationLocal.class);
            context.getBean(MySqlConnectionCheck.class)
                    .verifyConnectivity();
            context.stop();
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
