package com.github.nagyesta.lowkeyvault.example.quarkus;

import com.github.nagyesta.lowkeyvault.testcontainers.LowkeyVaultContainer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

import static com.github.nagyesta.lowkeyvault.testcontainers.LowkeyVaultContainerBuilder.lowkeyVault;

public class LowkeyVaultTestResource implements QuarkusTestResourceLifecycleManager {

    MySQLContainer<?> mySQLContainer;
    LowkeyVaultContainer lowkeyVaultContainer;

    @Override
    public Map<String, String> start() {
        mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:9.2.0"));
        lowkeyVaultContainer = lowkeyVault(DockerImageName.parse("nagyesta/lowkey-vault:4.0.58-ubi9-minimal"))
                .dependsOnContainer(mySQLContainer, sql -> Map.of(
                        "jdbc-url", mySQLContainer.getJdbcUrl(),
                        "jdbc-user", mySQLContainer.getUsername(),
                        "jdbc-pass", mySQLContainer.getPassword()
                ))
                .hostTokenPort(10544)
                .mergeTrustStores()
                .build();
        lowkeyVaultContainer.start();
        return Map.of(
                "quarkus.azure.keyvault.secret.endpoint", lowkeyVaultContainer.getDefaultVaultBaseUrl()
        );
    }

    @Override
    public void stop() {
        lowkeyVaultContainer.stop();
    }
}
