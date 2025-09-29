![LowkeyVault](https://raw.githubusercontent.com/nagyesta/lowkey-vault/main/.github/assets/LowkeyVault-logo-full.png)

[![GitHub license](https://img.shields.io/github/license/nagyesta/lowkey-vault-example?color=informational)](https://raw.githubusercontent.com/nagyesta/lowkey-vault-example/main/LICENSE)
[![Lowkey secure](https://img.shields.io/badge/lowkey-secure-0066CC)](https://github.com/nagyesta/lowkey-vault)
[![Java version](https://img.shields.io/badge/Java%20version-17-yellow?logo=java)](https://img.shields.io/badge/Java%20version-17-yellow?logo=java)

# Lowkey Vault - Example - Micronaut with Testcontainers

This example is using the official Azure integration using a Key Vault backed property source. The application
was generated with [Micronaut Launch](https://micronaut.io/launch/). The Lowkey Vault and the MySQL images are started 
using Testcontainers.

### Points of interest

* [Application context root](./src/main/java/com/github/nagyesta/lowkeyvault/example/micronaut/Application.java)
* [A bean using the property source](./src/main/java/com/github/nagyesta/lowkeyvault/example/micronaut/MySqlConnectionCheck.java)
* [Generic Property source configuration](./src/main/resources/application.properties)
* [Test for the property resolution](./src/test/java/com/github/nagyesta/lowkeyvault/example/micronaut/MicronautAkvDemoTest.java)
* [Gradle configuration for tests](./build.gradle.kts#L73)
* [Gradle configuration for Run](./build.gradle.kts#L82)

Note: In order to better understand what is needed in general to make similar examples work, please find a generic overview [here](https://github.com/nagyesta/lowkey-vault/wiki/Example:-How-can-you-use-Lowkey-Vault-in-your-tests).

In this example, the containers are automatically started using Testcontainers both for the tests and the local app start.
The Lowkey Vault container will be automatically populated with the DB credentials by defining the JDBC dependency.
The Gradle configuration ensures that the two required environment variables are configured as well:
* ```IDENTITY_ENDPOINT``` must be set to point to the `/metadata/identity/oauth2/token` path of Assumed Identity e.g., http://localhost:10544/metadata/identity/oauth2/token
* ```IDENTITY_HEADER``` can be set to anything (just needs to exist) e.g., `header`

> [!TIP]
> Since v2.4.2, Lowkey Vault is providing the same token endpoint on the `8080` port by default. Therefore, you don't need to start another container.

### Usage

Run the tests and let the context run Lowkey Vault:

```shell
./gradlew clean build
```

Run the application locally:

```shell
./gradlew runTestcontainers
```
