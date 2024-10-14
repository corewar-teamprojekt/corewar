plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "backend"

include("api")
include("mars-interpreter")
include("testing")
