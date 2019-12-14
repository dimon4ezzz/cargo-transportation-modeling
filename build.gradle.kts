plugins {
    kotlin("jvm") version "1.3.60"
    application
}

group = "ru.edu.urfu.dimon4ezzz"
version = "1.0-SNAPSHOT"

application {
    mainClassName = "ru.edu.urfu.dimon4ezzz.cargo.MainKt"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}