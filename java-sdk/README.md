![LowkeyVault](https://raw.githubusercontent.com/nagyesta/lowkey-vault/main/.github/assets/LowkeyVault-logo-full.png)

[![GitHub license](https://img.shields.io/github/license/nagyesta/lowkey-vault-example?color=informational)](https://raw.githubusercontent.com/nagyesta/lowkey-vault-example/main/LICENSE)
[![Lowkey secure](https://img.shields.io/badge/lowkey-secure-0066CC)](https://github.com/nagyesta/lowkey-vault)
[![Java version](https://img.shields.io/badge/Java%20version-17-yellow?logo=java)](https://img.shields.io/badge/Java%20version-17-yellow?logo=java)

# Lowkey Vault - Example - Java SDK

In this example, the core of the application was generated with [Spring Initializr](https://start.spring.io)
The image is started with [Testcontainers](https://testcontainers.org/). The test cases are using
[Azure Key Vault Key client](https://docs.microsoft.com/en-us/azure/key-vault/keys/quick-create-java)
and [Azure Key Vault Secret client](https://docs.microsoft.com/en-us/azure/key-vault/secrets/quick-create-java).

### Points of interest

* [Application context root](./src/main/java/com/github/nagyesta/lowkeyvault/example/LowkeyVaultExampleApplication.java)
* [Key "repository"](./src/main/java/com/github/nagyesta/lowkeyvault/example/impl/AzureKeyRepositoryImpl.java)
* [Secret "repository"](./src/main/java/com/github/nagyesta/lowkeyvault/example/impl/AzureSecretRepositoryImpl.java)
* [Certificate "repository"](./src/main/java/com/github/nagyesta/lowkeyvault/example/impl/AzureCertificateRepositoryImpl.java)
* [Test configuration using Docker](./src/test/java/com/github/nagyesta/lowkeyvault/example/AzureAccessTestDockerConfiguration.java)
* [JUnit Jupiter Test](./src/test/java/com/github/nagyesta/lowkeyvault/example/LowkeyVaultExampleApplicationTests.java)

Note: In order to better understand what is needed in general to make similar examples work, please find a generic overview [here](https://github.com/nagyesta/lowkey-vault/wiki/Example:-How-can-you-use-Lowkey-Vault-in-your-tests).

### Usage

Run the tests and let the context run Lowkey Vault using Docker:

```shell
./gradlew clean build
```

In this mode, the `managed-identity` profile is activated by default by the Test configuration. Gradle is used
for setting the two required environment variables in [test task](build.gradle.kts#L60):
* ```IDENTITY_ENDPOINT``` must be set to point to the `/metadata/identity/oauth2/token` path of Assumed Identity e.g., http://localhost:8080/metadata/identity/oauth2/token
* ```IDENTITY_HEADER``` can be set to anything (just needs to exist) e.g., `header`

> [!TIP]
> Since v2.4.2, Lowkey Vault is providing the same token endpoint on the `8080` port by default. Therefore, you don't need to start another container.

By setting these, the following things will happen:
1. The [test configuration](./src/test/java/com/github/nagyesta/lowkeyvault/example/AzureAccessTestDockerConfiguration.java)
   will create a `DefaultAzureCredential` instance for the authentication due to the `managed-identity` Spring profile.
2. The created credential will use the configured identity endpoint as token source
3. The Assumed Identity container that is started by the test configuration will issue a dummy token whenever the managed
   identity logic used by the `DefaultAzureCredential` requires one.
4. Lowkey Vault will accept the dummy token
