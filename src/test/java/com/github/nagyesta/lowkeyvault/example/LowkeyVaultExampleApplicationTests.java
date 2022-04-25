package com.github.nagyesta.lowkeyvault.example;

import com.azure.security.keyvault.keys.KeyClient;
import com.azure.security.keyvault.keys.models.CreateRsaKeyOptions;
import com.azure.security.keyvault.keys.models.KeyOperation;
import com.azure.security.keyvault.secrets.SecretClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;

@SpringBootTest(classes = {
        AzureAccessTestProcessConfiguration.class, AzureAccessTestDockerConfiguration.class, LowkeyVaultExampleApplication.class},
        properties = {"vault.url=https://localhost:8443", "logging.level.root=WARN"})
class LowkeyVaultExampleApplicationTests {

    @Autowired
    private AzureKeyRepository keyRepository;
    @Autowired
    private AzureSecretRepository secretRepository;
    @Autowired
    private KeyClient keyClient;
    @Autowired
    private SecretClient secretClient;
    @Value("${key.name}")
    private String keyName;
    @Value("${secret.name.user}")
    private String userName;
    @Value("${secret.name.pass}")
    private String password;
    @Value("${secret.name.url}")
    private String connectionUrl;

    @Test
    void testSecretRepositoryShouldFetchDBCredentialsWhenCalled() {
        //given
        final String admin = "admin";
        final String pass = "s3cret";
        final String url = "jdbc:h2:mem:test_mem";
        secretClient.setSecret(userName, admin);
        secretClient.setSecret(password, pass);
        secretClient.setSecret(connectionUrl, url);

        //when
        final String user = secretRepository.getDatabaseUserName();
        final String password = secretRepository.getDatabasePassword();
        final String connectionUrl = secretRepository.getDatabaseConnectionUrl();

        //then
        Assertions.assertEquals(admin, user);
        Assertions.assertEquals(pass, password);
        Assertions.assertEquals(url, connectionUrl);
    }


    @Test
    void testKeyRepositoryEncryptThenDecryptShouldResultInOriginalTextWhenCalled() {
        //given
        final String secret = "a secret message";
        keyClient.createRsaKey(new CreateRsaKeyOptions(keyName)
                .setKeyOperations(KeyOperation.ENCRYPT, KeyOperation.DECRYPT, KeyOperation.WRAP_KEY, KeyOperation.UNWRAP_KEY)
                .setKeySize(2048));

        //when
        final byte[] encrypted = keyRepository.encrypt(secret);
        final String decrypted = keyRepository.decrypt(encrypted);

        //then
        Assertions.assertEquals(secret, decrypted);
        Assertions.assertNotEquals(encrypted.length, decrypted.getBytes(StandardCharsets.UTF_8).length);
    }

}
