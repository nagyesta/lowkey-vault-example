package com.github.nagyesta.lowkeyvault.example.impl;

import com.azure.security.keyvault.keys.KeyClient;
import com.azure.security.keyvault.keys.cryptography.CryptographyClient;
import com.azure.security.keyvault.keys.cryptography.models.EncryptionAlgorithm;
import com.azure.security.keyvault.keys.models.JsonWebKey;
import com.azure.security.keyvault.keys.models.KeyVaultKey;
import com.github.nagyesta.lowkeyvault.example.AzureKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.function.Function;

/**
 * Implements Azure Key Vault key access.
 */
@Component
public class AzureKeyRepositoryImpl implements AzureKeyRepository {

    private final KeyClient keyClient;
    private final Function<JsonWebKey, CryptographyClient> cryptographyClientProvider;
    @Value("${key.name}")
    private String vaultKeyName;

    @Autowired
    public AzureKeyRepositoryImpl(final KeyClient keyClient,
                                  final Function<JsonWebKey, CryptographyClient> cryptographyClientProvider) {
        this.keyClient = keyClient;
        this.cryptographyClientProvider = cryptographyClientProvider;
    }

    @Override
    public String decrypt(final byte[] ciphertext) {
        final KeyVaultKey key = keyClient.getKey(vaultKeyName);
        final byte[] plainText = cryptographyClientProvider.apply(key.getKey())
                .decrypt(EncryptionAlgorithm.RSA_OAEP_256, ciphertext)
                .getPlainText();
        return new String(plainText, StandardCharsets.UTF_8);
    }

    @Override
    public byte[] encrypt(final String plainText) {
        final KeyVaultKey key = keyClient.getKey(vaultKeyName);
        return cryptographyClientProvider.apply(key.getKey())
                .encrypt(EncryptionAlgorithm.RSA_OAEP_256, plainText.getBytes(StandardCharsets.UTF_8))
                .getCipherText();
    }
}
