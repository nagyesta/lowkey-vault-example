plugins {
    id("java")
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
    checkstyle
}

group = "com.github.nagyesta"
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
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.azure:azure-security-keyvault-secrets")
    implementation("com.azure:azure-security-keyvault-keys")
    implementation("com.azure:azure-security-keyvault-certificates")
    implementation("com.azure:azure-identity")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.github.nagyesta.lowkey-vault:lowkey-vault-client")
    testImplementation("com.github.nagyesta.lowkey-vault:lowkey-vault-testcontainers")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
    dependencies {
        dependency("org.springframework.boot:spring-boot-starter-web:3.4.3")
        dependency("org.springframework.boot:spring-boot-configuration-processor:3.4.3")
        dependency("org.springframework.boot:spring-boot-starter-test:3.4.3")
        dependency("com.azure:azure-security-keyvault-secrets:4.9.3")
        dependency("com.azure:azure-security-keyvault-keys:4.9.3")
        dependency("com.azure:azure-security-keyvault-certificates:4.7.3")
        dependency("com.azure:azure-identity:1.15.4")
        dependency("com.github.nagyesta.lowkey-vault:lowkey-vault-client:2.14.6")
        dependency("com.github.nagyesta.lowkey-vault:lowkey-vault-testcontainers:2.14.6")
    }
}

tasks.test {
    useJUnitPlatform()
    systemProperty("lowkey-version", "2.14.6")
    // Only needed if Assumed Identity and DefaultAzureCredential is used to simulate IMDS managed identity
    environment("IDENTITY_ENDPOINT", "http://localhost:8080/metadata/identity/oauth2/token")
    environment("IDENTITY_HEADER", "header")
    testLogging.showStandardStreams = true
}
