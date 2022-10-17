import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
}

repositories {
    mavenLocal()
    maven("https://papermc.io/repo/repository/maven-public/")
    mavenCentral()
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
    compileOnly("io.papermc.paper:paper:1.17.1-R0.1-SNAPSHOT")
    compileOnly("com.googlecode.json-simple", "json-simple", "1.1")
    testImplementation(kotlin("test-junit"))
    implementation(kotlin("stdlib-jdk8"))
}
tasks.withType<Jar>() {

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
tasks {
    compileKotlin { kotlinOptions.jvmTarget = "1.8" }
    compileTestKotlin { kotlinOptions.jvmTarget = "1.8" }
    compileJava { options.encoding = "UTF-8" }
    jar {
        val out = "D:\\A Rubynex Server\\back\\server\\bedwars1x8\\plugins\\"
        destinationDirectory.set(File(out))
        doFirst {
                from({
                configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }
            })
        }
        exclude("META-INF/MANIFEST.MF", "META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
    }
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}