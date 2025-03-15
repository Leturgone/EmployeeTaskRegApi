plugins {
    kotlin("jvm") version "2.1.10"
    kotlin("plugin.serialization") version "1.4.21"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("ch.qos.logback:logback-classic:1.5.16")
    implementation("io.ktor:ktor-server-status-pages:3.0.3")
    implementation("io.ktor:ktor-server-core:3.0.3")
    implementation("org.postgresql:postgresql:42.7.4")
    implementation("com.h2database:h2:2.3.232")
    implementation("io.ktor:ktor-server-core-jvm:3.0.3")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:3.0.3")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:3.0.3")
    implementation("org.jetbrains.exposed:exposed-core:0.56.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.56.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.56.0")
    implementation("io.ktor:ktor-server-host-common:3.0.3")
    implementation("io.ktor:ktor-server-netty:3.0.3")
    implementation("org.jetbrains.exposed:exposed-java-time:0.56.0")
    implementation("at.favre.lib:bcrypt:0.10.2")
    implementation("com.auth0:java-jwt:4.4.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")

}