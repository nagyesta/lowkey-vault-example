package com.github.nagyesta.lowkeyvault.example;

import com.azure.core.credential.BasicAuthenticationCredential;
import com.azure.core.credential.TokenCredential;
import com.azure.core.http.HttpClient;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.github.nagyesta.lowkeyvault.http.ApacheHttpClientProvider;
import com.github.nagyesta.lowkeyvault.http.AuthorityOverrideFunction;
import com.github.nagyesta.lowkeyvault.testcontainers.LowkeyVaultContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
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
@Configuration
public class AzureAccessTestDockerConfiguration extends AzureAccessCommonTestConfiguration implements DisposableBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(AzureAccessTestDockerConfiguration.class);
    private static final int HOST_TOKEN_PORT = 8080;
    private static final int HOST_PORT = 8443;
    private final LowkeyVaultContainer lowkeyVaultContainer;

    public AzureAccessTestDockerConfiguration() {
        final DockerImageName imageName = DockerImageName.parse("nagyesta/lowkey-vault:4.1.0");
        lowkeyVaultContainer = lowkeyVault(imageName)
                .hostTokenPort(HOST_TOKEN_PORT)
                .logicalPort(HOST_PORT)
                .build()
                .withImagePullPolicy(PullPolicy.defaultPolicy());
        lowkeyVaultContainer.start();
        LOGGER.warn("Started container: {} {}", imageName, lowkeyVaultContainer.getContainerName());
    }

    /**
     * Bypass authentication as Lowkey Vault does not need/support authentication with the real service.
     *
     * @return dummy credential
     */
    @Profile("!managed-identity")
    @Bean
    @Primary
    public TokenCredential fakeTokenCredential() {
        LOGGER.info("Bypassing authentication with dummy token");
        return new BasicAuthenticationCredential(lowkeyVaultContainer.getUsername(), lowkeyVaultContainer.getPassword());
    }

    /**
     * Bypass authentication with dummy tokens obtained from Assumed Identity (using the IMDS managed identity mechanism).
     *
     * @return default Azure credential
     */
    @Profile("managed-identity")
    @Bean
    @Primary
    public TokenCredential managedIdentityTokenCredential() {
        LOGGER.info("IDENTITY_ENDPOINT: {}", System.getenv("IDENTITY_ENDPOINT"));
        LOGGER.info("IDENTITY_HEADER: {}", System.getenv("IDENTITY_HEADER"));
        LOGGER.info("Bypassing authentication with dummy token from: {}", lowkeyVaultContainer.getTokenEndpointUrl());
        return new DefaultAzureCredentialBuilder().build();
    }

    /**
     * Set up the routing using logical and physical addresss translation provided by the Lowkey Vault Client.
     * Optional: you can use the built-in HTTP client if you are using only one vault and you don't need other
     * Lowkey Vault Client features either.
     *
     * @return override function
     * @see <a href="https://github.com/nagyesta/lowkey-vault/blob/main/lowkey-vault-client/README.md">Lowkey Vault Client features</a>
     */
    @Bean
    public AuthorityOverrideFunction overrideFunction() {
        return new AuthorityOverrideFunction(lowkeyVaultContainer.getDefaultVaultAuthority(), lowkeyVaultContainer.getEndpointAuthority());
    }

    /**
     * Creates an HTTP provider using the implementation from Lowkey Vault Client to use the extra features of the client.
     * Optional: If you don't need Lowkey Vault Client features, you can simply use the default HTTP client provider.
     *
     * @param overrideFunction the override function translating between logical and physical addresses.
     * @return HTTP client provider
     * @see <a href="https://github.com/nagyesta/lowkey-vault/blob/main/lowkey-vault-client/README.md">Lowkey Vault Client features</a>
     */
    @Bean
    public ApacheHttpClientProvider apacheHttpClientProvider(final AuthorityOverrideFunction overrideFunction) {
        return new ApacheHttpClientProvider(lowkeyVaultContainer.getDefaultVaultBaseUrl(), overrideFunction);
    }

    /**
     * Create a HTTP client using the previous beans. Spring will pass this to the Azure client.
     * Optional: you can use the built-in HTTP client if you don't need the Lowkey Vault Client features.
     *
     * @param apacheHttpClientProvider The custom HTTP client provider supporting Lowkey Vault features.
     * @return HTTP client
     * @see <a href="https://github.com/nagyesta/lowkey-vault/blob/main/lowkey-vault-client/README.md">Lowkey Vault Client features</a>
     */
    @Bean
    @Primary
    public HttpClient httpClient(final ApacheHttpClientProvider apacheHttpClientProvider) {
        return apacheHttpClientProvider.createInstance();
    }

    @Override
    public void destroy() {
        LOGGER.warn("Stopping Docker container: {}", lowkeyVaultContainer.getContainerName());
        lowkeyVaultContainer.stop();
    }
}
