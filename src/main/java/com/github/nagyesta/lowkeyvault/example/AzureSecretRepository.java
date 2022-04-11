package com.github.nagyesta.lowkeyvault.example;

public interface AzureSecretRepository {

    String getDatabaseConnectionUrl();

    String getDatabaseUserName();

    String getDatabasePassword();
}
