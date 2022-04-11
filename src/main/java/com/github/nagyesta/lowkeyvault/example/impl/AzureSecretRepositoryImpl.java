package com.github.nagyesta.lowkeyvault.example.impl;

import com.azure.security.keyvault.secrets.SecretClient;
import com.github.nagyesta.lowkeyvault.example.AzureSecretRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AzureSecretRepositoryImpl implements AzureSecretRepository {

    private final SecretClient secretClient;
    @Value("${secret.name.user}")
    private String userName;
    @Value("${secret.name.pass}")
    private String password;
    @Value("${secret.name.url}")
    private String connectionUrl;

    @Autowired
    public AzureSecretRepositoryImpl(SecretClient secretClient) {
        this.secretClient = secretClient;
    }

    @Override
    public String getDatabaseConnectionUrl() {
        return secretClient.getSecret(connectionUrl).getValue();
    }

    @Override
    public String getDatabaseUserName() {
        return secretClient.getSecret(userName).getValue();
    }

    @Override
    public String getDatabasePassword() {
        return secretClient.getSecret(password).getValue();
    }
}
