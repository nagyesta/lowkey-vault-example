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

import static com.github.nagyesta.lowkeyvault.testcontainers.LowkeyVaultContainerBuilder.lowkeyVault;

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
        final String version = System.getProperty("lowkey-version");
        final DockerImageName imageName = DockerImageName.parse("nagyesta/lowkey-vault:" + version);
        lowkeyVaultContainer = lowkeyVault(imageName)
                .build()
                .withImagePullPolicy(PullPolicy.defaultPolicy());
        lowkeyVaultContainer.start();
        LOGGER.warn("Started container: {} {}", imageName, lowkeyVaultContainer.getContainerName());
    }

    /**
     * Bypass authantication as Lowkey Vault does not need/support authentication with the real service.
     */
    @Bean
    @Primary
    public TokenCredential tokenCredential() {
        return new BasicAuthenticationCredential(lowkeyVaultContainer.getUsername(), lowkeyVaultContainer.getPassword());
    }

    /**
     * Set up the routing using logical and physical addresss translation provided by the Lowkey Vault Client.
     * Optional: you can use the built-in HTTP client if you are using only one vault and you don't need other
     * Lowkey Vault Client features either.
     * @see <a href="https://github.com/nagyesta/lowkey-vault/blob/main/lowkey-vault-client/README.md">Lowkey Vault Client features</a>
     */
    @Bean
    public AuthorityOverrideFunction overrideFunction() {
        return new AuthorityOverrideFunction(lowkeyVaultContainer.getDefaultVaultAuthority(), lowkeyVaultContainer.getEndpointAuthority());
    }

    /**
     * Create a HTTP provider using the implementation from Lowkey Vault Client to use the extra features of the client.
     * Optional: If you don't need Lowkey Vault Client features, you can simply use the default HTTP client provider.
     * @see <a href="https://github.com/nagyesta/lowkey-vault/blob/main/lowkey-vault-client/README.md">Lowkey Vault Client features</a>
     */
    @Bean
    public ApacheHttpClientProvider apacheHttpClientProvider(@Autowired final AuthorityOverrideFunction overrideFunction) {
        return new ApacheHttpClientProvider(lowkeyVaultContainer.getDefaultVaultBaseUrl(), overrideFunction);
    }

    /**
     * Create a HTTP client using the previous beans. Spring will pass this to the Azure client.
     * Optional: you can use the built-in HTTP client if you don't need the Lowkey Vault Client fetaures.
     * @see <a href="https://github.com/nagyesta/lowkey-vault/blob/main/lowkey-vault-client/README.md">Lowkey Vault Client features</a>
     */
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
