package com.github.nagyesta.lowkeyvault.example;

import com.azure.core.credential.TokenCredential;
import com.azure.core.http.HttpClient;
import com.azure.security.keyvault.certificates.CertificateClient;
import com.azure.security.keyvault.certificates.CertificateClientBuilder;
import com.azure.security.keyvault.certificates.CertificateServiceVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class AzureAccessCommonTestConfiguration {

    @Value("${vault.url}")
    private String vaultUrl;
    @Value("${vault.disable-challenge-resource-verification:false}")
    private boolean disableChallengeResourceVerification;

    @Bean
    @Autowired
    public CertificateClient certificateClient(
            final HttpClient httpClient, final TokenCredential tokenCredential) {
        final CertificateClientBuilder builder = new CertificateClientBuilder()
                .vaultUrl(vaultUrl)
                .httpClient(httpClient)
                .serviceVersion(CertificateServiceVersion.V7_3)
                .credential(tokenCredential);
        if (disableChallengeResourceVerification) {
            builder.disableChallengeResourceVerification();
        }
        return builder.buildClient();
    }
}
