package com.github.nagyesta.lowkeyvault.example;

import com.azure.core.credential.BasicAuthenticationCredential;
import com.azure.core.credential.TokenCredential;
import com.azure.core.http.HttpClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nagyesta.lowkeyvault.http.ApacheHttpClientProvider;
import com.github.nagyesta.lowkeyvault.http.AuthorityOverrideFunction;
import com.github.nagyesta.lowkeyvault.http.management.LowkeyVaultManagementClient;
import com.github.nagyesta.lowkeyvault.testcontainers.LowkeyVaultContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.testcontainers.images.PullPolicy;
import org.testcontainers.utility.DockerImageName;

import java.util.Collections;

@Profile("docker")
@Configuration
public class AzureAccessTestDockerConfiguration implements DisposableBean {

    private final static Logger LOGGER = LoggerFactory.getLogger(AzureAccessTestDockerConfiguration.class);
    private final LowkeyVaultContainer lowkeyVaultContainer;

    public AzureAccessTestDockerConfiguration() {
        final DockerImageName imageName = DockerImageName.parse("nagyesta/lowkey-vault:1.2.0");
        lowkeyVaultContainer = new LowkeyVaultContainer(imageName, Collections.emptySet())
                .withImagePullPolicy(PullPolicy.defaultPolicy())
                .withExposedPorts(8443);
        lowkeyVaultContainer.start();
        LOGGER.warn("Started container: {} {}", imageName, lowkeyVaultContainer.getContainerName());
    }

    @Bean
    @Primary
    public TokenCredential tokenCredential() {
        return new BasicAuthenticationCredential(lowkeyVaultContainer.getUsername(), lowkeyVaultContainer.getPassword());
    }

    @Bean
    public AuthorityOverrideFunction overrideFunction() {
        return new AuthorityOverrideFunction(lowkeyVaultContainer.getDefaultVaultAuthority(), lowkeyVaultContainer.getEndpointAuthority());
    }

    @Bean
    public ApacheHttpClientProvider apacheHttpClientProvider(@Autowired AuthorityOverrideFunction overrideFunction) {
        return new ApacheHttpClientProvider(lowkeyVaultContainer.getDefaultVaultBaseUrl(), overrideFunction);
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
        LOGGER.warn("Stopping Docker container: {}", lowkeyVaultContainer.getContainerName());
        lowkeyVaultContainer.stop();
    }
}
