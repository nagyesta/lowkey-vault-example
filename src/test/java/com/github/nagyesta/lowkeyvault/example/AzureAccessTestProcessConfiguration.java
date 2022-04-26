package com.github.nagyesta.lowkeyvault.example;

import com.azure.core.credential.BasicAuthenticationCredential;
import com.azure.core.credential.TokenCredential;
import com.azure.core.http.HttpClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nagyesta.lowkeyvault.http.ApacheHttpClientProvider;
import com.github.nagyesta.lowkeyvault.http.AuthorityOverrideFunction;
import com.github.nagyesta.lowkeyvault.http.management.LowkeyVaultManagementClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.io.IOException;

@Profile("!docker")
@Configuration
public class AzureAccessTestProcessConfiguration implements DisposableBean {

    private final static Logger LOGGER = LoggerFactory.getLogger(AzureAccessTestProcessConfiguration.class);
    private final Process process;

    public AzureAccessTestProcessConfiguration() throws IOException {
        final String jarName = System.getProperty("lowkey-app-jar");
        process = new ProcessBuilder("java", "-jar", jarName, "&").start();
        LOGGER.warn("Starting Jar process (pid: {}): java -jar {} &", process.pid(), jarName);
    }

    @Bean
    @Primary
    public TokenCredential tokenCredential() {
        return new BasicAuthenticationCredential("DUMMY", "DUMMY");
    }

    @Bean
    public AuthorityOverrideFunction overrideFunction() {
        return new AuthorityOverrideFunction("localhost:8443", "localhost:8443");
    }

    @Bean
    public ApacheHttpClientProvider apacheHttpClientProvider(@Autowired AuthorityOverrideFunction overrideFunction) {
        return new ApacheHttpClientProvider("https://localhost:8443/", overrideFunction);
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

    @Override
    public void destroy() throws Exception {
        if (process != null && process.isAlive()) {
            LOGGER.warn("Stopping Jar process: {}", process.pid());
            process.destroyForcibly();
        }
    }
}
