package com.github.nagyesta.lowkeyvault.example;

/**
 * Repository interface encrypting/decrypting data using Azure Key Vault.
 */
public interface AzureKeyRepository {

    /**
     * Decrypts the cipher using Azure Key Vault.
     *
     * @param cipher encrypted data.
     * @return decrypted data
     */
    String decrypt(byte[] cipher);

    /**
     * Encrypts the clear text using Azure Key Vault.
     *
     * @param clearText Plain text that should be encrypted.
     * @return encrypted data
     */
    byte[] encrypt(String clearText);

}
