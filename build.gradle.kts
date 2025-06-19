plugins {
    kotlin("jvm") version "2.1.21"

    id("io.ktor.plugin") version "3.1.3"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.21"
}

group = "com.example"
version = "0.0.1"

application {
    mainClass.set("ApplicationKt")

    var isDevelopment: Boolean = true //project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor server core
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")

    // Content negotiation (JSON)
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")

    // Authentication & JWT
    implementation("io.ktor:ktor-server-auth-jvm")
    implementation("io.ktor:ktor-server-auth-jwt-jvm")

    // JWT library (this was missing!)
    implementation("com.auth0:java-jwt:4.4.0")

    // CORS (if you need frontend integration)
    implementation("io.ktor:ktor-server-cors:3.1.3")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.4.11")

    // Testing
    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:2.1.21")

}

tasks.test {
    useJUnitPlatform()
}