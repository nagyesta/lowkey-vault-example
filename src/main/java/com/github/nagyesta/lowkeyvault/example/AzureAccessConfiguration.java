package com.github.nagyesta.lowkeyvault.example;

import com.azure.core.credential.BasicAuthenticationCredential;
import com.azure.core.credential.TokenCredential;
import com.azure.core.http.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures Azure Key Vault access using the provided parameters.
 */
@Configuration
public class AzureAccessConfiguration {

    @Value("${vault.user}")
    private String keyVaultUserName;
    @Value("${vault.pass}")
    private String keyVaultPassword;

    @Bean
    @ConditionalOnMissingBean(type = "com.azure.core.credential.TokenCredential")
    public TokenCredential tokenCredential() {
        return new BasicAuthenticationCredential(keyVaultUserName, keyVaultPassword);
    }

    @Bean
    @ConditionalOnMissingBean(type = "com.azure.core.http.HttpClient")
    public HttpClient httpClient() {
        return HttpClient.createDefault();
    }

}
