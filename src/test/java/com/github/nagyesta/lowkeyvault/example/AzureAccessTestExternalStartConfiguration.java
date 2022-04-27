package com.github.nagyesta.lowkeyvault.example;

import com.azure.core.credential.BasicAuthenticationCredential;
import com.azure.core.credential.TokenCredential;
import com.azure.core.http.HttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * This example shows how the default Azure client can be used with Lowkey Vault
 * (as long as the trust store has the self-signed Lowkey Vault certificate).
 */
@Profile({"!docker & !process"})
@Configuration
public class AzureAccessTestExternalStartConfiguration {

    private static final String DUMMY = "DUMMY";

    @Bean
    @Primary
    public TokenCredential tokenCredential() {
        return new BasicAuthenticationCredential(DUMMY, DUMMY);
    }

    @Bean
    @Primary
    public HttpClient httpClient() {
        return HttpClient.createDefault();
    }
}
