package com.github.nagyesta.lowkeyvault.example;

import java.security.cert.X509Certificate;
import java.security.interfaces.ECPrivateKey;

/**
 * Interface for fetching the certificate and private key from Azure Key Vault.
 */
public interface AzureCertificateRepository {

    /**
     * Returns the certificate fetched from Azure Key Vault.
     *
     * @return The certificate.
     */
    X509Certificate getCertificate();

    /**
     * Returns the private key fetched from Azure Key Vault.
     *
     * @return The private key.
     */
    ECPrivateKey getPrivateKey();
}
