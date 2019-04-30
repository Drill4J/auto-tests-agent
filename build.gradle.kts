import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.30"
    java
}

group = "com.epam"
version = ""

repositories {
    mavenCentral()
}

dependencies {
    compile("org.javassist:javassist:3.18.1-GA")
    implementation("org.apache.httpcomponents:httpclient:4.3.6")
    implementation(project(":runtime"))
    testImplementation(kotlin("test-junit"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks {
    val jar = named<Jar>("jar") {
        manifest {
            attributes(mapOf(
                    "PreMain-Class" to "com.epam.drill.DrillCoverageTestAgent",
                    "Boot-Class-Path" to "runtime.jar"))
        }

        from(provider {
            configurations["compile"].map {
                if (it.isDirectory) it else zipTree(it)
            }
        })
    }


    named<Test>("test") {
        dependsOn(jar)
        setJvmArgs(listOf("-javaagent:${jar.get().archivePath}=autoTestScope"))
    }
}