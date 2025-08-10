package com.github.nagyesta.lowkeyvault.example.springcloudazurestarter;

import com.github.nagyesta.lowkeyvault.testcontainers.LowkeyVaultContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.SQLException;

import static com.github.nagyesta.lowkeyvault.testcontainers.LowkeyVaultContainerBuilder.*;

@Testcontainers
@SpringBootTest(classes = SpringCloudAzureStarterApplication.class)
@SuppressWarnings({"checkstyle:VisibilityModifier", "checkstyle:MagicNumber"})
class SpringCloudAzureStarterApplicationTests {

    @Container
    static LowkeyVaultContainer lowkeyVaultContainer = getLowkeyVaultContainer();

    static LowkeyVaultContainer getLowkeyVaultContainer() {
        return lowkeyVault("nagyesta/lowkey-vault:3.4.23")
                .hostTokenPort(10544)
                .dependsOnContainer(getMySqlContainer(), springJdbcSecretSupplier())
                .mergeTrustStores()
                .setPropertiesAfterStartup(springCloudAzureKeyVaultPropertySupplier())
                .build();
    }

    private static MySQLContainer<?> getMySqlContainer() {
        return new MySQLContainer<>("mysql:9.2.0");
    }

    @Autowired
    private MySqlConnectionCheck mySqlConnectionCheck;

    @Test
    void contextLoads() throws SQLException {
        //given the context loads

        //when the actual field is set
        mySqlConnectionCheck.verifyConnectivity();

        //then no exception
    }

}
