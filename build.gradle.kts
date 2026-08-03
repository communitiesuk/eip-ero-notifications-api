import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.tasks.KtLintCheckTask
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask
import org.owasp.dependencycheck.reporting.ReportGenerator.Format.HTML
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage
import java.lang.ProcessBuilder.Redirect

plugins {
    id("org.springframework.boot") version "4.1.0"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("jvm") version "2.4.10"
    kotlin("kapt") version "2.4.10"
    kotlin("plugin.spring") version "2.4.10"
    kotlin("plugin.jpa") version "2.4.10"
    kotlin("plugin.allopen") version "2.4.10"
    id("org.jlleitschuh.gradle.ktlint") version "14.2.0"
    id("org.openapi.generator") version "7.24.0"
    id("org.owasp.dependencycheck") version "12.2.2"
}

group = "uk.gov.dluhc"
version = "latest"
java.sourceCompatibility = JavaVersion.VERSION_17

ext["snakeyaml.version"] = "2.2"
extra["springCloudAwsVersion"] = "4.1.0"
extra["awsSdkVersion"] = "2.49.4"
// EROPSPT-733 - Pinned versions brought in by springboot - if updating springboot, check if these are still needed.
extra["logback.version"] = "1.5.37"
extra["netty.version"] = "4.2.16.Final"
extra["log4j2.version"] = "2.25.5"
extra["tomcat.version"] = "11.0.24"
extra["jackson.version"] = "3.1.5"

allOpen {
    annotations("jakarta.persistence.Entity", "jakarta.persistence.MappedSuperclass", "jakarta.persistence.Embedabble")
}

val awsProfile = System.getenv("AWS_PROFILE_ARG") ?: "--profile code-artifact"
val codeArtifactToken =
    "aws codeartifact get-authorization-token --domain erop-artifacts --domain-owner 063998039290 --query authorizationToken --output text $awsProfile".runCommand()

repositories {
    mavenCentral()
    maven {
        url = uri("https://erop-artifacts-063998039290.d.codeartifact.eu-west-2.amazonaws.com/maven/api-repo/")
        credentials {
            username = "aws"
            password = codeArtifactToken
        }
    }
}

dependencies {
    // framework
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("tools.jackson.module:jackson-module-kotlin")
    implementation("tools.jackson.core:jackson-databind")
    implementation("io.github.oshai:kotlin-logging-jvm:8.0.4")
    implementation("org.apache.commons:commons-lang3:3.20.0")
    implementation("org.mapstruct:mapstruct:1.6.3")
    kapt("org.mapstruct:mapstruct-processor:1.6.3")

    // internal libs
    implementation("uk.gov.dluhc:logging-library:4.2.0")
    implementation("uk.gov.dluhc:messaging-support-library:3.2.1")
    implementation("uk.gov.dluhc:internal-auth-library:2.2.0")

    // api
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-aspectj")
    implementation("io.swagger.core.v3:swagger-annotations:2.2.52")
    implementation("org.springframework:spring-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // UK Government
    implementation("uk.gov.service.notify:notifications-java-client:6.0.1-RELEASE")

    // Logging
    runtimeOnly("net.logstash.logback:logstash-logback-encoder:9.0")

    // spring security
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    // AWS v2 dependencies
    implementation("software.amazon.awssdk:dynamodb")
    implementation("software.amazon.awssdk:dynamodb-enhanced")

    // AWS v1 dependencies
    implementation(platform("io.awspring.cloud:spring-cloud-aws-dependencies:${property("springCloudAwsVersion")}"))
    implementation("io.awspring.cloud:spring-cloud-aws-starter")
    implementation("io.awspring.cloud:spring-cloud-aws-starter-sqs")
    implementation("io.awspring.cloud:spring-cloud-aws-starter-s3")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // Test implementations
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-webtestclient")
    testImplementation("org.springframework.security:spring-security-test")

    testImplementation("org.testcontainers:junit-jupiter:2.0.5")
    testImplementation("org.testcontainers:testcontainers:2.0.5")

    testImplementation("org.awaitility:awaitility-kotlin:4.3.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:6.3.0")

    testImplementation("com.github.tomakehurst:wiremock-jre8-standalone:3.0.1")
    testImplementation("net.datafaker:datafaker:2.7.0")

    testImplementation(platform("software.amazon.awssdk:bom:${property("awsSdkVersion")}"))
    testImplementation("software.amazon.awssdk:auth")
    testImplementation("software.amazon.awssdk:sts")

    // Libraries to support creating JWTs in tests
    testImplementation("io.jsonwebtoken:jjwt-impl:0.13.0")
    testImplementation("io.jsonwebtoken:jjwt-jackson:0.13.0")
    // EROPSPT-733: Jackson v2 packages used by jjwt, should be reviewed if upgrading jjwt-jackson
    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.22.1")
    testImplementation("com.fasterxml.jackson.core:jackson-core:2.22.1")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

tasks.withType<KotlinCompile> {
    dependsOn(tasks.withType<GenerateTask>())
}

tasks.withType<Test> {
    dependsOn(tasks.withType<GenerateTask>())
    useJUnitPlatform()
    jvmArgs("--add-opens", "java.base/java.time=ALL-UNNAMED")
    // TODO: EROPSPT-608 this is a temporary fix for testcontainer versions
    systemProperty("api.version", "1.44")
}

tasks.withType<GenerateTask> {
    enabled = false
    validateSpec.set(true)
    outputDir.set("$projectDir/build/generated")
    generatorName.set("kotlin-spring")
    generateModelTests.set(false)
    generateModelDocumentation.set(false)
    globalProperties.set(
        mapOf(
            "apis" to "false",
            "invokers" to "false",
            "models" to "",
        ),
    )
    configOptions.set(
        mapOf(
            "dateLibrary" to "java8",
            "enumPropertyNaming" to "UPPERCASE",
            "useBeanValidation" to "true",
            "useSpringBoot3" to "true",
        ),
    )
}

tasks.register("generate-models-from-openapi-document-NotificationsAPIs.yaml", GenerateTask::class) {
    enabled = true
    inputSpec.set("$projectDir/src/main/resources/openapi/NotificationsAPIs.yaml")
    packageName.set("uk.gov.dluhc.notificationsapi")
    configOptions.put("documentationProvider", "none")
}

tasks.register("generate-models-from-openapi-document-shared-stats-update-sqs-messaging.yaml", GenerateTask::class) {
    enabled = true
    inputSpec.set("$projectDir/src/main/resources/openapi/sqs/applications-api/shared-stats-update-sqs-messaging.yaml")
    packageName.set("uk.gov.dluhc.applicationsapi.messaging")
}

tasks.register("generate-models-from-openapi-document-Notifications-sqs-message-types.yaml", GenerateTask::class) {
    enabled = true
    inputSpec.set("$projectDir/src/main/resources/openapi/sqs/Notifications-sqs-messaging.yaml")
    packageName.set("uk.gov.dluhc.notificationsapi.messaging")
}

tasks.register("generate-models-from-openapi-document-EROManagementAPIs.yaml", GenerateTask::class) {
    enabled = true
    inputSpec.set("$projectDir/src/main/resources/openapi/external/EROManagementAPIs.yaml")
    packageName.set("uk.gov.dluhc.eromanagementapi")
}

// Add the generated code to the source sets
sourceSets["main"].java {
    this.srcDir("$projectDir/build/generated")
}

// Linting is dependent on GenerateTask
tasks.withType<KtLintCheckTask> {
    dependsOn(tasks.withType<GenerateTask>())
}

tasks.withType<BootBuildImage> {
    builder.set("paketobuildpacks/builder-jammy-base")
    environment = mapOf("BP_HEALTH_CHECKER_ENABLED" to "true")
    buildpacks = listOf(
        "urn:cnb:builder:paketo-buildpacks/java",
        "docker.io/paketobuildpacks/health-checker",
    )
}

// Exclude generated code from linting
ktlint {
    filter {
        exclude { projectDir.toURI().relativize(it.file.toURI()).path.contains("/generated/") }
    }
}

kapt {
    arguments {
        arg("mapstruct.defaultComponentModel", "spring")
        arg("mapstruct.unmappedTargetPolicy", "IGNORE")
    }
}

fun String.runCommand(): String {
    val parts = this.split("\\s".toRegex())
    val process = ProcessBuilder(*parts.toTypedArray())
        .redirectOutput(Redirect.PIPE)
        .start()
    process.waitFor()
    return process.inputStream.bufferedReader().readText().trim()
}

// Configuration for the OWASP dependency check
dependencyCheck {
    autoUpdate = true
    failOnError = true
    failBuildOnCVSS = 0.toFloat()
    analyzers.assemblyEnabled = false
    analyzers.centralEnabled = true
    format = HTML.name
    suppressionFiles = listOf("owasp.suppressions.xml")
}
