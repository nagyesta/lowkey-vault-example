package com.github.nagyesta.lowkeyvault.example;

import com.azure.core.credential.BasicAuthenticationCredential;
import com.azure.core.credential.TokenCredential;
import com.azure.core.http.HttpClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nagyesta.lowkeyvault.http.ApacheHttpClientProvider;
import com.github.nagyesta.lowkeyvault.http.AuthorityOverrideFunction;
import com.github.nagyesta.lowkeyvault.http.management.LowkeyVaultManagementClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile({"!docker & !process"})
@Configuration
public class AzureAccessTestExternalStartConfiguration {

    private static final String DUMMY = "DUMMY";
    private static final String LOCALHOST_8443 = "localhost:8443";
    private static final String HTTPS_LOCALHOST_8443 = "https://" + LOCALHOST_8443 + "/";

    @Bean
    @Primary
    public TokenCredential tokenCredential() {
        return new BasicAuthenticationCredential(DUMMY, DUMMY);
    }

    @Bean
    public AuthorityOverrideFunction overrideFunction() {
        return new AuthorityOverrideFunction(LOCALHOST_8443, LOCALHOST_8443);
    }

    @Bean
    public ApacheHttpClientProvider apacheHttpClientProvider(@Autowired AuthorityOverrideFunction overrideFunction) {
        return new ApacheHttpClientProvider(HTTPS_LOCALHOST_8443, overrideFunction);
    }

    @Bean
    @Primary
    public HttpClient httpClient(@Autowired ApacheHttpClientProvider apacheHttpClientProvider) {
        return apacheHttpClientProvider.createInstance();
    }

    @Bean
    public LowkeyVaultManagementClient lowkeyVaultManagementClient(@Autowired ApacheHttpClientProvider apacheHttpClientProvider) {
        return apacheHttpClientProvider.getLowkeyVaultManagementClient(new ObjectMapper());
    }
}
