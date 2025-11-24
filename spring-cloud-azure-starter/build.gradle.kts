plugins {
    id("java")
    id("org.springframework.boot") version "4.0.0"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.avast.gradle.docker-compose") version "0.17.20"
    checkstyle
}

group = "com.github.nagyesta.lowkeyvault.example"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

tasks.withType<Checkstyle>().configureEach {
    configProperties = mutableMapOf<String, Any>(
        "base_dir" to rootDir.absolutePath.toString(),
        "cache_file" to layout.buildDirectory.file("checkstyle/cacheFile").get().asFile.absolutePath.toString()
    )
    checkstyle.toolVersion = "10.21.4"
    checkstyle.configFile = file("../config/checkstyle/checkstyle.xml")
    reports {
        xml.required.set(false)
        html.required.set(true)
        html.stylesheet = project.resources.text.fromFile("../config/checkstyle/checkstyle-stylesheet.xsl")
    }
}

dependencies {
    implementation("com.azure.spring:spring-cloud-azure-starter-keyvault-secrets")
    implementation("com.azure.spring:spring-cloud-azure-starter-keyvault")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    runtimeOnly("com.mysql:mysql-connector-j")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
    imports {
        mavenBom("com.azure.spring:spring-cloud-azure-dependencies:6.0.0")
    }
}

dockerCompose {
    useComposeFiles.set(listOf("docker-compose.yml"))
    forceRecreate = true
    waitForTcpPorts = true
    retainContainersOnStartupFailure = false
    captureContainersOutput = false

    setProjectName("lowkey-vault-spring-starter")
    dockerComposeWorkingDirectory = project.file("${projectDir}/local")
}

tasks.test {
    systemProperty("spring.profiles.active", "dev")
    useJUnitPlatform()
    systemProperty("javax.net.ssl.trustStore", file("$projectDir/local/lowkey-vault/lowkey-vault-keystore.p12"))
    systemProperty("javax.net.ssl.trustStorePassword", "changeit")
    systemProperty("javax.net.ssl.trustStoreType", "PKCS12")
    // Only needed if Assumed Identity and DefaultAzureCredential is used to simulate IMDS managed identity
    environment("IDENTITY_ENDPOINT", "http://localhost:10544/metadata/identity/oauth2/token")
    environment("IDENTITY_HEADER", "header")
    testLogging.showStandardStreams = true
    // make sure the containers are running
    dependsOn(tasks.composeUp)
    finalizedBy(tasks.composeDown)
}

tasks.bootRun {
    systemProperty("spring.profiles.active", "dev")
    systemProperty("javax.net.ssl.trustStore", file("$projectDir/local/lowkey-vault/lowkey-vault-keystore.p12"))
    systemProperty("javax.net.ssl.trustStorePassword", "changeit")
    systemProperty("javax.net.ssl.trustStoreType", "PKCS12")
    // Only needed if Assumed Identity and DefaultAzureCredential is used to simulate IMDS managed identity
    environment("IDENTITY_ENDPOINT", "http://localhost:10544/metadata/identity/oauth2/token")
    environment("IDENTITY_HEADER", "header")
    // make sure the containers are running
    dependsOn(tasks.composeUp)
    finalizedBy(tasks.composeDown)
}
