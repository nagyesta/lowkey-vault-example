package com.github.nagyesta.lowkeyvault.example;

import com.azure.security.keyvault.certificates.CertificateClient;
import com.azure.security.keyvault.certificates.models.CertificateContentType;
import com.azure.security.keyvault.certificates.models.CertificateKeyCurveName;
import com.azure.security.keyvault.certificates.models.CertificateKeyType;
import com.azure.security.keyvault.certificates.models.CertificatePolicy;
import com.azure.security.keyvault.keys.KeyClient;
import com.azure.security.keyvault.keys.models.CreateRsaKeyOptions;
import com.azure.security.keyvault.keys.models.KeyOperation;
import com.azure.security.keyvault.secrets.SecretClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPrivateKey;

@ActiveProfiles("managed-identity")
@SpringBootTest(classes = {
        AzureAccessTestDockerConfiguration.class,
        LowkeyVaultExampleApplication.class},
        properties = {
                "vault.url=https://localhost:8443",
                "vault.disable-challenge-resource-verification=true",
                "logging.level.root=WARN"
        }
)
class LowkeyVaultExampleApplicationTests {

    private static final int RSA_KEY_SIZE = 2048;
    @Autowired
    private AzureKeyRepository keyRepository;
    @Autowired
    private AzureSecretRepository secretRepository;
    @Autowired
    private AzureCertificateRepository certificateRepository;
    @Autowired
    private KeyClient keyClient;
    @Autowired
    private SecretClient secretClient;
    @Autowired
    private CertificateClient certificateClient;
    @Value("${key.name}")
    private String keyName;
    @Value("${secret.name.user}")
    private String userName;
    @Value("${secret.name.pass}")
    private String password;
    @Value("${secret.name.url}")
    private String connectionUrl;
    @Value("${certificate.name}")
    private String certificateName;

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
                .setKeySize(RSA_KEY_SIZE));

        //when
        final byte[] encrypted = keyRepository.encrypt(secret);
        final String decrypted = keyRepository.decrypt(encrypted);

        //then
        Assertions.assertEquals(secret, decrypted);
        Assertions.assertNotEquals(encrypted.length, decrypted.getBytes(StandardCharsets.UTF_8).length);
    }

    @Test
    void testCertificateRepositoryGetCertificateAndGetPrivateKeyShouldReturnGeneratedCertificateAndKeyWhenCalled() {
        //given
        final String subject = "CN=example.com";
        final CertificatePolicy policy = new CertificatePolicy("Self", subject)
                .setKeyType(CertificateKeyType.EC)
                .setKeyCurveName(CertificateKeyCurveName.P_256)
                .setContentType(CertificateContentType.PKCS12);
        certificateClient.beginCreateCertificate(certificateName, policy).waitForCompletion();

        //when
        final X509Certificate actualCertificate = certificateRepository.getCertificate();
        final ECPrivateKey actualPrivateKey = certificateRepository.getPrivateKey();

        //then
        Assertions.assertEquals(subject, actualCertificate.getSubjectX500Principal().getName());
        Assertions.assertEquals(CertificateKeyType.EC.toString(), actualPrivateKey.getAlgorithm());
    }

}
