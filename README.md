![LowkeyVault](https://raw.githubusercontent.com/nagyesta/lowkey-vault/main/.github/assets/LowkeyVault-logo-full.png)

[![GitHub license](https://img.shields.io/github/license/nagyesta/lowkey-vault-example?color=informational)](https://raw.githubusercontent.com/nagyesta/lowkey-vault-example/main/LICENSE)
[![GitHub Workflow Status](https://img.shields.io/github/actions/workflow/status/nagyesta/lowkey-vault-example/gradle.yml?logo=github&branch=main)](https://github.com/nagyesta/lowkey-vault-example/actions/workflows/gradle.yml)
[![Lowkey secure](https://img.shields.io/badge/lowkey-secure-0066CC)](https://github.com/nagyesta/lowkey-vault)

# Lowkey Vault - Example

This repository contains the following examples for [Lowkey Vault](https://github.com/nagyesta/lowkey-vault):

## [Java SDK](./java-sdk)

[![Java version](https://img.shields.io/badge/Java%20version-11%20with%20Docker|17%20with%20Jar-yellow?logo=java)](https://img.shields.io/badge/Java%20version-11%20with%20Docker|17%20with%20Jar-yellow?logo=java)

In this example, the core of the application was generated with [Spring Initializr](https://start.spring.io)
The image is started with [Testcontainers](https://testcontainers.org/). The test cases are using
[Azure Key Vault Key client](https://docs.microsoft.com/en-us/azure/key-vault/keys/quick-create-java)
and [Azure Key Vault Secret client](https://docs.microsoft.com/en-us/azure/key-vault/secrets/quick-create-java).

### Points of interest

* [Application context root](java-sdk/src/main/java/com/github/nagyesta/lowkeyvault/example/LowkeyVaultExampleApplication.java)
* [Key "repository"](java-sdk/src/main/java/com/github/nagyesta/lowkeyvault/example/impl/AzureKeyRepositoryImpl.java)
* [Secret "repository"](java-sdk/src/main/java/com/github/nagyesta/lowkeyvault/example/impl/AzureSecretRepositoryImpl.java)
* [Certificate "repository"](java-sdk/src/main/java/com/github/nagyesta/lowkeyvault/example/impl/AzureCertificateRepositoryImpl.java)
* [Test configuration (Docker mode)](java-sdk/src/test/java/com/github/nagyesta/lowkeyvault/example/AzureAccessTestDockerConfiguration.java)
* [Test configuration (Jar process mode)](java-sdk/src/test/java/com/github/nagyesta/lowkeyvault/example/AzureAccessTestProcessConfiguration.java)
* [Test configuration (External Jar)](java-sdk/src/test/java/com/github/nagyesta/lowkeyvault/example/AzureAccessTestExternalStartConfiguration.java)
* [JUnit Jupiter Test](java-sdk/src/test/java/com/github/nagyesta/lowkeyvault/example/LowkeyVaultExampleApplicationTests.java)

Note: In order to better understand what is needed in general to make similar examples work, please find a generic overview [here](https://github.com/nagyesta/lowkey-vault/wiki/Example:-How-can-you-use-Lowkey-Vault-in-your-tests).

### Usage

#### Docker

Run the tests and let the context run Lowkey Vault using Docker:

```shell
./gradlew clean build -Pdocker
```

In this mode, not only the `docker`, but the `managed-identity` profile is activated by the Gradle
[test task](java-sdk/build.gradle#L51). Also, the same configuration ensures, that the two required environment
variables are configured as well:
* ```IDENTITY_ENDPOINT``` must be set to point to the `/metadata/identity/oauth2/token` path of Assumed Identity e.g., http://localhost:8080/metadata/identity/oauth2/token
* ```IDENTITY_HEADER``` can be set to anything (just needs to exist) e.g., `header`

By setting these, the following things will happen:
1. The [test configuration](java-sdk/src/test/java/com/github/nagyesta/lowkeyvault/example/AzureAccessTestDockerConfiguration.java)
   will create a `DefaultAzureCredential` instance for the authentication due to the `managed-identity` Spring profile.
2. The created credential will use the configured identity endpoint as token source
3. The Assumed Identity container that is started by the test configuration will issue a dummy token whenever the managed
   identity logic used by the `DefaultAzureCredential` requires one.
4. Lowkey Vault will accept the dummy token

#### Jar

Run the tests and let the context run Lowkey Vault using Jar:

```shell
./gradlew clean build -Pprocess
```

Note: managed identity will NOT be active with this profile, so no need to start Assumed Identity as well.

#### External

Start Lowkey Vault manually:

```shell
java -jar lowkey-vault-app-<version>.jar
```

Run the tests using the externally started Lowkey Vault:

```shell
./gradlew clean build
```

Note: managed identity will NOT be active with this profile, so no need to start Assumed Identity as well.

## [Spring Cloud Azure Starter](./spring-cloud-azure-starter)

[![Java version](https://img.shields.io/badge/Java%20version-17-yellow?logo=java)](https://img.shields.io/badge/Java%20version-17-yellow?logo=java)

This example is using the official Azure Starter for setting up a Key Vault backed property source. The application
was generated with [Spring Initializr](https://start.spring.io). The Lowkey Vault and Assumed Identity images are
started using Docker Compose.

**Warning:** The example depends on the `5.6.0+` version of the Spring Cloud Azure Starter implementation.

### Points of interest

* [Application context root](spring-cloud-azure-starter/src/main/java/com/github/nagyesta/lowkeyvault/example/springcloudazurestarter/SpringCloudAzureStarterApplication.java)
* [A bean using the property source](spring-cloud-azure-starter/src/main/java/com/github/nagyesta/lowkeyvault/example/springcloudazurestarter/Hello.java)
* [Generic Property source configuration](spring-cloud-azure-starter/src/main/resources/application.properties)
* [DEV Property source configuration for local runs](spring-cloud-azure-starter/src/main/resources/application-dev.properties)
* [Test for the property resolution](spring-cloud-azure-starter/src/test/java/com/github/nagyesta/lowkeyvault/example/springcloudazurestarter/SpringCloudAzureStarterApplicationTests.java)
* [Gradle configuration for tests](spring-cloud-azure-starter/build.gradle#L47)
* [Gradle configuration for Boot Run](spring-cloud-azure-starter/build.gradle#L61)

Note: In order to better understand what is needed in general to make similar examples work, please find a generic overview [here](https://github.com/nagyesta/lowkey-vault/wiki/Example:-How-can-you-use-Lowkey-Vault-in-your-tests).

In this mode, the containers are automatically started using the [docker-compose.yml](spring-cloud-azure-starter/local/docker-compose.yml).
The Lowkey Vault container will restore a previously saved state including the secret which will be used by the application.
The Gradle configuration ensures, that the two required environment variables are configured as well:
* ```IDENTITY_ENDPOINT``` must be set to point to the `/metadata/identity/oauth2/token` path of Assumed Identity e.g., http://localhost:10544/metadata/identity/oauth2/token
* ```IDENTITY_HEADER``` can be set to anything (just needs to exist) e.g., `header`

### Usage

Run the tests and let the context run Lowkey Vault using Docker Compose:

```shell
./gradlew clean build
```

Run the application locally:

```shell
./gradlew bootRun
```
