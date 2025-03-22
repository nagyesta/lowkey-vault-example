plugins {
    id("java")
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
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
    implementation("com.azure.spring:spring-cloud-azure-starter-keyvault")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    runtimeOnly("com.mysql:mysql-connector-j")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.testcontainers:mysql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("com.github.nagyesta.lowkey-vault:lowkey-vault-testcontainers")
}

dependencyManagement {
    imports {
        mavenBom("com.azure.spring:spring-cloud-azure-dependencies:5.20.1")
    }
    dependencies {
        dependency("org.testcontainers:mysql:1.20.6")
        dependency("org.testcontainers:junit-jupiter:1.20.6")
        dependency("com.github.nagyesta.lowkey-vault:lowkey-vault-testcontainers:2.14.6")
    }
}

tasks.test {
    useJUnitPlatform()
    // Only needed if Assumed Identity and DefaultAzureCredential is used to simulate IMDS managed identity
    environment("IDENTITY_ENDPOINT", "http://localhost:10544/metadata/identity/oauth2/token")
    environment("IDENTITY_HEADER", "header")
    testLogging.showStandardStreams = true
}

tasks.register<JavaExec>("bootRunTestcontainers") {
    group = "application"
    classpath = sourceSets.test.get().runtimeClasspath
    mainClass.set("com.github.nagyesta.lowkeyvault.example.springcloudazurestarter.SpringCloudAzureStarterApplicationLocal")
    systemProperty("spring.profiles.active", "dev")
    // Only needed if Assumed Identity and DefaultAzureCredential is used to simulate IMDS managed identity
    environment("IDENTITY_ENDPOINT", "http://localhost:10544/metadata/identity/oauth2/token")
    environment("IDENTITY_HEADER", "header")
    dependsOn("build")
}
