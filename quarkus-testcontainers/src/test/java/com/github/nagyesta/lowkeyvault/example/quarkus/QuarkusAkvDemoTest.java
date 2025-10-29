package com.github.nagyesta.lowkeyvault.example.quarkus;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

@QuarkusTest
@QuarkusTestResource(value = LowkeyVaultTestResource.class, restrictToAnnotatedClass = true)
class QuarkusAkvDemoTest {

    @Inject
    MySqlConnectionCheck mySqlConnectionCheck;

    @Test
    void contextLoads() throws SQLException {
        mySqlConnectionCheck.verifyConnectivity();
    }
}
