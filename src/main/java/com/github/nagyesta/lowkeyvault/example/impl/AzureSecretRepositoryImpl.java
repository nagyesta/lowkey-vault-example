package com.github.nagyesta.lowkeyvault.example.impl;

import com.azure.security.keyvault.secrets.SecretClient;
import com.github.nagyesta.lowkeyvault.example.AzureSecretRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Implements Azure Key Vault secret access.
 */
@Component
public class AzureSecretRepositoryImpl implements AzureSecretRepository {

    private final SecretClient secretClient;
    @Value("${secret.name.user}")
    private String vaultSecretNameForDbUserName;
    @Value("${secret.name.pass}")
    private String vaultSecretNameForDbPassword;
    @Value("${secret.name.url}")
    private String vaultSecretNameForDbConnectionUrl;

    @Autowired
    public AzureSecretRepositoryImpl(final SecretClient secretClient) {
        this.secretClient = secretClient;
    }

    @Override
    public String getDatabaseConnectionUrl() {
        return secretClient.getSecret(vaultSecretNameForDbConnectionUrl).getValue();
    }

    @Override
    public String getDatabaseUserName() {
        return secretClient.getSecret(vaultSecretNameForDbUserName).getValue();
    }

    @Override
    public String getDatabasePassword() {
        return secretClient.getSecret(vaultSecretNameForDbPassword).getValue();
    }
}
