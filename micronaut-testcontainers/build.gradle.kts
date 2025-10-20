plugins {
    id("io.micronaut.application") version "4.6.0"
    id("io.micronaut.test-resources") version "4.6.0"
    id("io.micronaut.aot") version "4.6.0"
}

version = "0.1"
group = "com.github.nagyesta"

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("io.micronaut:micronaut-http-validation")
    annotationProcessor("io.micronaut.serde:micronaut-serde-processor")
    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut.azure:micronaut-azure-secret-manager")
    implementation("io.micronaut.discovery:micronaut-discovery-client")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    implementation("com.mysql:mysql-connector-j")
    implementation("com.azure:azure-security-keyvault-secrets:4.10.3")
    testImplementation("org.testcontainers:junit-jupiter:1.21.3")
    testImplementation("org.testcontainers:mysql:1.21.3")
    testImplementation("com.github.nagyesta.lowkey-vault:lowkey-vault-testcontainers:4.0.67")
    testRuntimeOnly("org.slf4j:slf4j-simple")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}


application {
    mainClass = "com.github.nagyesta.lowkeyvault.example.micronaut.Application"
}
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}


graalvmNative.toolchainDetection = false

micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("com.github.nagyesta.*")
    }
    testResources {
        additionalModules.add("jdbc-mysql")
    }
    aot {
        // Please review carefully the optimizations enabled below
        // Check https://micronaut-projects.github.io/micronaut-aot/latest/guide/ for more details
        optimizeServiceLoading = false
        convertYamlToJava = false
        precomputeOperations = true
        cacheEnvironment = true
        optimizeClassLoading = true
        deduceEnvironment = true
        optimizeNetty = true
        replaceLogbackXml = true
    }
}


tasks.named<io.micronaut.gradle.docker.NativeImageDockerfile>("dockerfileNative") {
    jdkVersion = "17"
}

tasks.test {
    useJUnitPlatform()
    // Only needed if Assumed Identity and DefaultAzureCredential is used to
    // simulate IMDS managed identity
    environment("IDENTITY_ENDPOINT", "http://localhost:10544/metadata/identity/oauth2/token")
    environment("IDENTITY_HEADER", "header")
    testLogging.showStandardStreams = true
}

tasks.register<JavaExec>("runTestcontainers") {
    group = "application"
    classpath = sourceSets.test.get().runtimeClasspath
    mainClass.set("com.github.nagyesta.lowkeyvault.example.micronaut.ApplicationLocal")
    // Only needed if Assumed Identity and DefaultAzureCredential is used to simulate IMDS managed identity
    environment("IDENTITY_ENDPOINT", "http://localhost:10544/metadata/identity/oauth2/token")
    environment("IDENTITY_HEADER", "header")
    dependsOn("build")
}

