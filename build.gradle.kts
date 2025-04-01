import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.tasks.KtLintCheckTask
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask
import org.owasp.dependencycheck.reporting.ReportGenerator.Format.HTML
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage
import java.lang.ProcessBuilder.Redirect

plugins {
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("jvm") version "1.9.10"
    kotlin("kapt") version "1.9.10"
    kotlin("plugin.spring") version "1.9.10"
    kotlin("plugin.jpa") version "1.9.10"
    kotlin("plugin.allopen") version "1.9.10"
    id("org.jlleitschuh.gradle.ktlint") version "11.3.1"
    id("org.jlleitschuh.gradle.ktlint-idea") version "11.3.1"
    id("org.openapi.generator") version "7.0.1"
    id("org.owasp.dependencycheck") version "12.1.0"
}

group = "uk.gov.dluhc"
version = "latest"
java.sourceCompatibility = JavaVersion.VERSION_17

ext["snakeyaml.version"] = "2.2"
// Spring cloud 3.x integrates with AWS v2, until that is released this project has both AWS v1 and v2 SDK libraries
extra["springCloudAwsVersion"] = "3.1.1"
extra["awsSdkVersion"] = "2.26.20"

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

apply(plugin = "org.jlleitschuh.gradle.ktlint")
apply(plugin = "org.openapi.generator")
apply(plugin = "org.springframework.boot")
apply(plugin = "io.spring.dependency-management")
apply(plugin = "org.jetbrains.kotlin.jvm")
apply(plugin = "org.jetbrains.kotlin.plugin.spring")
apply(plugin = "org.jetbrains.kotlin.plugin.jpa")
apply(plugin = "org.jetbrains.kotlin.plugin.allopen")

dependencies {
    // framework
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.4")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    kapt("org.mapstruct:mapstruct-processor:1.5.5.Final")

    // internal libs
    implementation("uk.gov.dluhc:logging-library:3.0.3")
    implementation("uk.gov.dluhc:messaging-support-library:2.1.0")

    // api
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("io.swagger.core.v3:swagger-annotations:2.2.7")
    implementation("org.springframework:spring-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // UK Government
    implementation("uk.gov.service.notify:notifications-java-client:5.2.1-RELEASE")

    // Logging
    runtimeOnly("net.logstash.logback:logstash-logback-encoder:7.3")

    // spring security
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    // later version of nimbus-jose-jwt than brought in transitively by spring security - earlier version triggers CVE-2023-1370
    implementation("com.nimbusds:nimbus-jose-jwt:9.37.3")

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
    testImplementation("org.springframework.security:spring-security-test")

    testImplementation("org.testcontainers:junit-jupiter:1.19.1")
    testImplementation("org.testcontainers:testcontainers:1.19.1")

    testImplementation("org.awaitility:awaitility-kotlin:4.2.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")

    testImplementation("com.github.tomakehurst:wiremock-jre8-standalone:2.35.0")
    testImplementation("net.datafaker:datafaker:1.7.0")

    testImplementation(platform("software.amazon.awssdk:bom:${property("awsSdkVersion")}"))
    testImplementation("software.amazon.awssdk:auth")
    testImplementation("software.amazon.awssdk:sts")

    // Libraries to support creating JWTs in tests
    testImplementation("io.jsonwebtoken:jjwt-impl:0.11.5")
    testImplementation("io.jsonwebtoken:jjwt-jackson:0.11.5")
}

tasks.withType<KotlinCompile> {
    dependsOn(tasks.withType<GenerateTask>())
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    dependsOn(tasks.withType<GenerateTask>())
    useJUnitPlatform()
    jvmArgs("--add-opens", "java.base/java.time=ALL-UNNAMED")
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

tasks.create("generate-models-from-openapi-document-NotificationsAPIs.yaml", GenerateTask::class) {
    enabled = true
    inputSpec.set("$projectDir/src/main/resources/openapi/NotificationsAPIs.yaml")
    packageName.set("uk.gov.dluhc.notificationsapi")
    configOptions.put("documentationProvider", "none")
}

tasks.create("generate-models-from-openapi-document-UpdateStatisticsMessage.yaml", GenerateTask::class) {
    enabled = true
    inputSpec.set("$projectDir/src/main/resources/openapi/sqs/applications-api/UpdateStatisticsMessage.yaml")
    packageName.set("uk.gov.dluhc.applicationsapi.messaging")
}

tasks.create("generate-models-from-openapi-document-Notifications-sqs-message-types.yaml", GenerateTask::class) {
    enabled = true
    inputSpec.set("$projectDir/src/main/resources/openapi/sqs/Notifications-sqs-messaging.yaml")
    packageName.set("uk.gov.dluhc.notificationsapi.messaging")
}

tasks.create("generate-models-from-openapi-document-vca-sqs-messaging-erop.yaml", GenerateTask::class) {
    enabled = true
    inputSpec.set("$projectDir/src/main/resources/openapi/sqs/vca-api-sqs-messaging-erop.yaml")
    packageName.set("uk.gov.dluhc.votercardapplicationsapi.messaging")
}

tasks.create("generate-models-from-openapi-document-postal-sqs-messaging-erop.yaml", GenerateTask::class) {
    enabled = true
    inputSpec.set("$projectDir/src/main/resources/openapi/sqs/postal-api-sqs-messaging-erop.yaml")
    packageName.set("uk.gov.dluhc.postalapplicationsapi.messaging")
}

tasks.create("generate-models-from-openapi-document-proxy-sqs-messaging-erop.yaml", GenerateTask::class) {
    enabled = true
    inputSpec.set("$projectDir/src/main/resources/openapi/sqs/proxy-api-sqs-messaging-erop.yaml")
    packageName.set("uk.gov.dluhc.proxyapplicationsapi.messaging")
}

tasks.create("generate-models-from-openapi-document-overseas-sqs-messaging-erop.yaml", GenerateTask::class) {
    enabled = true
    inputSpec.set("$projectDir/src/main/resources/openapi/sqs/overseas-api-sqs-messaging-erop.yaml")
    packageName.set("uk.gov.dluhc.overseasapplicationsapi.messaging")
}

tasks.create("generate-models-from-openapi-document-EROManagementAPIs.yaml", GenerateTask::class) {
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
        "gcr.io/paketo-buildpacks/health-checker",
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

/* Configuration for the OWASP dependency check */
dependencyCheck {
    autoUpdate = true
    failOnError = true
    failBuildOnCVSS = 0.toFloat()
    analyzers.assemblyEnabled = false
    analyzers.centralEnabled = true
    format = HTML.name
    suppressionFiles = listOf("owasp.suppressions.xml")
}
