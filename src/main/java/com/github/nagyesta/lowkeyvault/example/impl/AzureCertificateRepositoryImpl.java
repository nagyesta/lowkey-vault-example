package com.github.nagyesta.lowkeyvault.example.impl;

import com.azure.security.keyvault.secrets.SecretClient;
import com.github.nagyesta.lowkeyvault.example.AzureCertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPrivateKey;

@Component
public class AzureCertificateRepositoryImpl implements AzureCertificateRepository {

    @Value("${certificate.name}")
    private String certificateName;
    private final SecretClient secretClient;

    @Autowired
    public AzureCertificateRepositoryImpl(final SecretClient secretClient) {
        this.secretClient = secretClient;
    }

    @Override
    public X509Certificate getCertificate() {
        try {
            final KeyStore keyStore = fetchKeyStore();
            final String alias = keyStore.aliases().nextElement();
            return (X509Certificate) keyStore.getCertificateChain(alias)[0];
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public ECPrivateKey getPrivateKey() {
        try {
            final KeyStore keyStore = fetchKeyStore();
            final String alias = keyStore.aliases().nextElement();
            return (ECPrivateKey) keyStore.getKey(alias, "".toCharArray());
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private KeyStore fetchKeyStore() {
        try {
            final String base64 = secretClient.getSecret(certificateName).getValue();
            final byte[] bytes = java.util.Base64.getDecoder().decode(base64);
            final KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(new ByteArrayInputStream(bytes), "".toCharArray());
            return keyStore;
        } catch (final Exception e) {
            throw new IllegalStateException("Unable to load key store.", e);
        }
    }
}
