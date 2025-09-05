plugins {
    kotlin("jvm") version "2.1.10"
    id("io.ktor.plugin") version "3.1.2"
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
    implementation("io.ktor:ktor-server-status-pages:3.1.0")
    implementation("io.ktor:ktor-server-core:3.1.0")
    implementation("io.ktor:ktor-server-cors:3.1.0")
    implementation("org.postgresql:postgresql:42.7.4")
    implementation("com.h2database:h2:2.3.232")
    implementation("io.ktor:ktor-server-auth:3.1.0")
    implementation("io.ktor:ktor-server-auth-jvm:3.1.0")
    implementation("io.ktor:ktor-server-auth-jwt:3.1.0")
    implementation("io.ktor:ktor-server-core-jvm:3.1.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:3.1.0")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:3.1.0")
    implementation("org.jetbrains.exposed:exposed-core:0.56.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.56.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.56.0")
    implementation("io.ktor:ktor-server-host-common:3.1.0")
    implementation("io.ktor:ktor-server-netty:3.1.0")
    implementation("org.jetbrains.exposed:exposed-java-time:0.56.0")
    implementation("at.favre.lib:bcrypt:0.10.2")
    implementation("com.auth0:java-jwt:4.4.0")
    implementation("io.insert-koin:koin-ktor:4.0.4")
    implementation("io.insert-koin:koin-logger-slf4j:4.0.4")

    testImplementation("org.jetbrains.kotlin:kotlin-test:2.1.10")
    testImplementation("io.ktor:ktor-server-test-host:3.1.2")
    testImplementation("org.mockito:mockito-core:5.17.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.12.2")
    testRuntimeOnly("org.junit.platform:junit-platform-engine:1.12.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.12.2")


}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}
ktor {
    fatJar {
        //archiveFileName.set("employee.jar")
    }
}
application {
    mainClass.set("ApplicationKt")

}