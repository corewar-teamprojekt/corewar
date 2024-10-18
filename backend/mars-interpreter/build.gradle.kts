plugins {
    kotlin("jvm") version "2.0.20"
    id("com.ncorti.ktfmt.gradle") version "0.20.1"
    jacoco
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
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
}

kotlin {
    jvmToolchain(21)
}
