plugins {
    kotlin("jvm") version "2.0.20"
    id("com.ncorti.ktfmt.gradle") version "0.20.1"
    id("com.adarshr.test-logger") version "4.0.0"
    jacoco
}

ktfmt { kotlinLangStyle() }
group = "software.shonk"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("ch.qos.logback:logback-classic:1.5.12")

    testImplementation(kotlin("test"))
    // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-params
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.11.3")
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-reflect
    testImplementation("org.jetbrains.kotlin:kotlin-reflect:2.0.21")
}

tasks.test {
    testlogger {
        showPassed = false
    }

    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
}

kotlin {
    jvmToolchain(21)
}

