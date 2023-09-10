package com.github.nagyesta.lowkeyvault.example.springcloudazurestarter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.cloud.azure.keyvault.secret.property-sources[0].challenge-resource-verification-enabled=false",
        "spring.cloud.azure.keyvault.secret.property-sources[0].endpoint=https://localhost:10543/"
})
class SpringCloudAzureStarterApplicationTests {

    @Value("${secret.name:Failed}")
    private String actual;

    @Test
    void contextLoads() {
        //given the context loads

        //when the actual field is set

        //then
        Assertions.assertEquals("It worked!", actual);
    }

}
