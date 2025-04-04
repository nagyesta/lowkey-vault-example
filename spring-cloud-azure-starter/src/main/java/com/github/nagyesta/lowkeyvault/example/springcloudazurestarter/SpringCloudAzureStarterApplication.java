package com.github.nagyesta.lowkeyvault.example.springcloudazurestarter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.SQLException;

@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
@SpringBootApplication
public class SpringCloudAzureStarterApplication {

    public static void main(final String[] args) throws SQLException {
        SpringApplication.run(SpringCloudAzureStarterApplication.class, args)
                .getBean(MySqlConnectionCheck.class)
                .verifyConnectivity();
    }

}
