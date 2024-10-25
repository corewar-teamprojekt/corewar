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
    // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-params
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.11.3")
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
