plugins {
    kotlin("jvm") version "1.3.60"
    java
    distribution
}

group = "com.epam"
version = ""

repositories {
    mavenCentral()
}
val jarDep: Configuration by configurations.creating
configurations {
    implementation.get().extendsFrom(jarDep)
}
dependencies {
    implementation(project(":runtime"))
    implementation(kotlin("stdlib-jdk8"))
    jarDep("org.javassist:javassist:3.18.1-GA")
    jarDep("com.google.code.gson:gson:2.8.5")
    testImplementation("org.apache.httpcomponents:httpclient:4.3.6")
    testImplementation("org.junit.jupiter:junit-jupiter:5.4.2")
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
    jar {
        manifest {
            attributes(mapOf(
                    "PreMain-Class" to "com.epam.drill.DrillCoverageTestAgent",
                    "Can-Retransform-Classes" to "true",
                    "Boot-Class-Path" to "runtime.jar"))
        }
        from(provider {
            jarDep.map {
                if (it.isDirectory) it else zipTree(it)
            }
        })
    }
    distributions {
        main {
            distributionBaseName.set("auto-test-agent")
            contents {
                from("build/libs")
            }
        }
    }
    test {
        dependsOn(jar)
        val args = listOf(
                "adminUrl" to "localhost:8090",
                "agentId" to "Petclinic",
                "pluginId" to "test2code"
        ).joinToString(",") { "${it.first}=${it.second}" }
        jvmArgs = listOf("-javaagent:${jar.get().archiveFile.get()}=$args")
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}
