plugins {
    kotlin("jvm") version "2.0.20"
    id("com.ncorti.ktfmt.gradle") version "0.20.1"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
}

ktfmt { kotlinLangStyle() }
group = "software.shonk"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}
