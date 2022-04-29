package com.github.nagyesta.lowkeyvault.example.impl;

import com.azure.core.credential.TokenCredential;
import com.azure.core.http.HttpClient;
import com.azure.security.keyvault.keys.cryptography.CryptographyClient;
import com.azure.security.keyvault.keys.cryptography.CryptographyClientBuilder;
import com.azure.security.keyvault.keys.cryptography.CryptographyServiceVersion;
import com.azure.security.keyvault.keys.models.JsonWebKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Function that can provide a {@link CryptographyClient} based on a {@link JsonWebKey}.
 */
@Component
public class CryptographyClientProvider implements Function<JsonWebKey, CryptographyClient> {

    private final HttpClient httpClient;

    private final TokenCredential tokenCredential;

    @Autowired
    public CryptographyClientProvider(final HttpClient httpClient, final TokenCredential tokenCredential) {
        this.httpClient = httpClient;
        this.tokenCredential = tokenCredential;
    }

    @Override
    public CryptographyClient apply(final JsonWebKey jsonWebKey) {
        return new CryptographyClientBuilder()
                .serviceVersion(CryptographyServiceVersion.V7_2)
                .keyIdentifier(jsonWebKey.getId())
                .httpClient(httpClient)
                .credential(tokenCredential)
                .buildClient();
    }
}
