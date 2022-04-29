package com.github.nagyesta.lowkeyvault.example;

/**
 * Repository interface fetching DB connection credentials from Azure Key Vault.
 */
public interface AzureSecretRepository {

    /**
     * Loads DB connection URL from Azure Key Vault.
     *
     * @return connection URL
     */
    String getDatabaseConnectionUrl();

    /**
     * Loads DB username from Azure Key Vault.
     *
     * @return username
     */
    String getDatabaseUserName();

    /**
     * Loads DB password from Azure Key Vault.
     *
     * @return password
     */
    String getDatabasePassword();
}
