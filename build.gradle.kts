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
    implementation(kotlin("stdlib-jdk8"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.4.2")
    compile("com.google.code.gson:gson:2.8.5")
}


tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks {
    val jar = named<Jar>("jar") {
        manifest {
            attributes(mapOf(
                    "PreMain-Class" to "com.epam.drill.DrillCoverageTestAgent",
                    "Can-Retransform-Classes" to "true",
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
        setJvmArgs(listOf("-javaagent:${jar.get().archivePath}=adminUrl=localhost:8090,agentId=Agent1"))
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}