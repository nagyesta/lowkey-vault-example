package com.github.nagyesta.lowkeyvault.example.springcloudazurestarter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.SQLException;

import static com.github.nagyesta.lowkeyvault.example.springcloudazurestarter.SpringCloudAzureStarterApplicationTests.getLowkeyVaultContainer;

@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
@SpringBootApplication
public class SpringCloudAzureStarterApplicationLocal {

    public static void main(final String[] args) throws SQLException {
        try (var lowkeyVault = getLowkeyVaultContainer()) {
            lowkeyVault.start();
            SpringApplication.run(SpringCloudAzureStarterApplication.class, args)
                    .getBean(MySqlConnectionCheck.class)
                    .verifyConnectivity();
        }
    }

}
