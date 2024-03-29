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

    /**
     * Allows us to use either the real azure credentials or the dummy credentials depending on environment config.
     */
    private final TokenCredential tokenCredential;
    /**
     * Allows us to use custom HTTP client implementations depending on the environment.
     */
    private final HttpClient httpClient;
    @Value("${vault.url}")
    private String vaultUrl;
    @Value("${vault.disable-challenge-resource-verification:false}")
    private boolean disableChallengeResourceVerification;

    @Autowired
    public AzureConfiguration(final TokenCredential tokenCredential, final HttpClient httpClient) {
        this.tokenCredential = tokenCredential;
        this.httpClient = httpClient;
    }

    @Bean
    public KeyClient keyClient() {
        final KeyClientBuilder builder = new KeyClientBuilder()
                .vaultUrl(vaultUrl)
                .httpClient(httpClient)
                .serviceVersion(KeyServiceVersion.V7_4)
                .credential(tokenCredential);
        if (disableChallengeResourceVerification) {
            builder.disableChallengeResourceVerification();
        }
        return builder.buildClient();
    }

    @Bean
    public SecretClient secretClient() {
        final SecretClientBuilder builder = new SecretClientBuilder()
                .vaultUrl(vaultUrl)
                .httpClient(httpClient)
                .serviceVersion(SecretServiceVersion.V7_4)
                .credential(tokenCredential);
        if (disableChallengeResourceVerification) {
            builder.disableChallengeResourceVerification();
        }
        return builder.buildClient();
    }
}
