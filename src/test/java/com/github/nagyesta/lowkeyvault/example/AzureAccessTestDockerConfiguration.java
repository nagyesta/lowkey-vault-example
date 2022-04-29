package com.github.nagyesta.lowkeyvault.example;

import com.azure.core.credential.BasicAuthenticationCredential;
import com.azure.core.credential.TokenCredential;
import com.azure.core.http.HttpClient;
import com.github.nagyesta.lowkeyvault.http.ApacheHttpClientProvider;
import com.github.nagyesta.lowkeyvault.http.AuthorityOverrideFunction;
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

/**
 * This example shows two separate things:
 * <ol>
 * <li>How Lowkey Vault container can be started with Testcontainers</li>
 * <li>How the Lowkey Vault Client can be used.</li>
 * </ol>
 */
@Profile("docker")
@Configuration
public class AzureAccessTestDockerConfiguration implements DisposableBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(AzureAccessTestDockerConfiguration.class);
    private final LowkeyVaultContainer lowkeyVaultContainer;

    public AzureAccessTestDockerConfiguration() {
        final DockerImageName imageName = DockerImageName.parse("nagyesta/lowkey-vault:1.2.2");
        lowkeyVaultContainer = new LowkeyVaultContainer(imageName, Collections.emptySet())
                .withImagePullPolicy(PullPolicy.defaultPolicy());
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
    public ApacheHttpClientProvider apacheHttpClientProvider(@Autowired final AuthorityOverrideFunction overrideFunction) {
        return new ApacheHttpClientProvider(lowkeyVaultContainer.getDefaultVaultBaseUrl(), overrideFunction);
    }

    @Bean
    @Primary
    public HttpClient httpClient(@Autowired final ApacheHttpClientProvider apacheHttpClientProvider) {
        return apacheHttpClientProvider.createInstance();
    }

    @Override
    public void destroy() {
        LOGGER.warn("Stopping Docker container: {}", lowkeyVaultContainer.getContainerName());
        lowkeyVaultContainer.stop();
    }
}
