package com.github.nagyesta.lowkeyvault.example.micronaut;

import com.github.nagyesta.lowkeyvault.testcontainers.LowkeyVaultContainer;
import com.github.nagyesta.lowkeyvault.testcontainers.LowkeyVaultContainerBuilder;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.SQLException;
import java.util.Map;

import static com.github.nagyesta.lowkeyvault.testcontainers.LowkeyVaultContainerBuilder.jdbcSecretSupplier;

@Testcontainers
@MicronautTest(startApplication = false)
//@BootstrapContextCompatible
class MicronautAkvDemoTest {

    @Container
    static LowkeyVaultContainer lowkeyVaultContainer = lowkeyVaultContainer();

    static LowkeyVaultContainer lowkeyVaultContainer() {
        return LowkeyVaultContainerBuilder.lowkeyVault("nagyesta/lowkey-vault:3.4.23")
                .hostTokenPort(10544)
                .dependsOnContainer(mySqlContainer(), jdbcSecretSupplier("datasource"))
                .mergeTrustStores()
                .setPropertiesAfterStartup(c -> Map.of(
                        "azure.key-vault.vault-url", c.getDefaultVaultBaseUrl()
                ))
                .build();
    }

    private static MySQLContainer<?> mySqlContainer() {
        return new MySQLContainer<>("mysql:9.2.0");
    }

    @Inject
    private MySqlConnectionCheck mySqlConnectionCheck;

    @Test
    void contextLoads() throws SQLException {
        mySqlConnectionCheck.verifyConnectivity();
    }
}
