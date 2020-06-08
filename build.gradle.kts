plugins {
    kotlin("jvm") version "1.3.72"
    application
}

group = "ru.edu.urfu.dimon4ezzz"
version = "1.0-SNAPSHOT"

application {
    mainClassName = "MainKt"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jgrapht:jgrapht-core:1.3.1")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    jar {
        manifest {
            attributes["Main-Class"] = "ru.edu.urfu.d4zzz.MainKt"
        }
        from(configurations.compileClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    }
}