![LowkeyVault](https://raw.githubusercontent.com/nagyesta/lowkey-vault/main/.github/assets/LowkeyVault-logo-full.png)

[![GitHub license](https://img.shields.io/github/license/nagyesta/lowkey-vault-example?color=informational)](https://raw.githubusercontent.com/nagyesta/lowkey-vault-example/main/LICENSE)
[![Java version](https://img.shields.io/badge/Java%20version-11-yellow?logo=java)](https://img.shields.io/badge/Java%20version-11-yellow?logo=java)

# Lowkey Vault - Example

This is an example for [Lowkey Vault](https://github.com/nagyesta/lowkey-vault). The core of the application was generated
from [Spring Initializr](https://start.spring.io)
The image is started with [Testcontainers](https://testcontainers.org/). The test cases are using
[Azure Key Vault Key client](https://docs.microsoft.com/en-us/azure/key-vault/keys/quick-create-java)
and [Azure Key Vault Secret client](https://docs.microsoft.com/en-us/azure/key-vault/secrets/quick-create-java).

### Points of interest

* [Application context root](src/main/java/com/github/nagyesta/lowkeyvault/example/LowkeyVaultExampleApplication.java)
* [Key "repository"](src/main/java/com/github/nagyesta/lowkeyvault/example/impl/AzureKeyRepositoryImpl.java)
* [Secret "repository"](src/main/java/com/github/nagyesta/lowkeyvault/example/impl/AzureSecretRepositoryImpl.java)
* [Test configuration](src/test/java/com/github/nagyesta/lowkeyvault/example/AzureAccessTestConfiguration.java)
* [JUnit Jupiter Test](src/test/java/com/github/nagyesta/lowkeyvault/example/LowkeyVaultExampleApplicationTests.java)
