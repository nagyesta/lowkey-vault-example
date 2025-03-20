package com.github.nagyesta.lowkeyvault.example.springcloudazurestarter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.SQLException;

@SpringBootTest(properties = {
        "spring.cloud.azure.keyvault.secret.property-sources[0].challenge-resource-verification-enabled=false",
        "spring.cloud.azure.keyvault.secret.property-sources[0].endpoint=https://localhost:10543/"
})
class SpringCloudAzureStarterApplicationTests {

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
