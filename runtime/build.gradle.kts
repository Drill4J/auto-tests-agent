import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    java
}

group = "com.epam"
version = ""

repositories {
    mavenCentral()
}


tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks {
    named<Jar>("jar") {
       destinationDir = file("../build/libs")
    }
}