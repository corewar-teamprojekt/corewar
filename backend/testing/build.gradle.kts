plugins {
    kotlin("jvm")
    application
}

application {
    mainClass.set("software.shonk.testing.MainKt")
}

group = "software.shonk"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(project(":mars-interpreter"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}