package com.github.nagyesta.lowkeyvault.example.quarkus;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;

@QuarkusMain
public class QuarkusAkvDemo implements QuarkusApplication {

    @Inject
    private MySqlConnectionCheck connectionCheck;

    public static void main(final String[] args) {
        Quarkus.run(QuarkusAkvDemo.class, args);
    }

    @Override
    public int run(final String... args) throws Exception {
        connectionCheck.verifyConnectivity();
        return 0;
    }
}
