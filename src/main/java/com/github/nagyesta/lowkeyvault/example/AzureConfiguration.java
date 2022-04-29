package com.github.nagyesta.lowkeyvault.example;

import com.azure.core.credential.TokenCredential;
import com.azure.core.http.HttpClient;
import com.azure.security.keyvault.keys.KeyClient;
import com.azure.security.keyvault.keys.KeyClientBuilder;
import com.azure.security.keyvault.keys.KeyServiceVersion;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.azure.security.keyvault.secrets.SecretServiceVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures Azure Key Vault clients using the provided dependencies.
 */
@Configuration
public class AzureConfiguration {

    private final TokenCredential tokenCredential;
    private final HttpClient httpClient;
    @Value("${vault.url}")
    private String vaultUrl;

    @Autowired
    public AzureConfiguration(final TokenCredential tokenCredential, final HttpClient httpClient) {
        this.tokenCredential = tokenCredential;
        this.httpClient = httpClient;
    }

    @Bean
    public KeyClient keyClient() {
        return new KeyClientBuilder()
                .vaultUrl(vaultUrl)
                .httpClient(httpClient)
                .serviceVersion(KeyServiceVersion.V7_2)
                .credential(tokenCredential)
                .buildClient();
    }

    @Bean
    public SecretClient secretClient() {
        return new SecretClientBuilder()
                .vaultUrl(vaultUrl)
                .httpClient(httpClient)
                .serviceVersion(SecretServiceVersion.V7_2)
                .credential(tokenCredential)
                .buildClient();
    }
}
