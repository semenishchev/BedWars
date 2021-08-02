plugins {
    kotlin("jvm") version "1.4.10"
}

repositories {
    jcenter()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://gitlab.com/XjCyan1de/maven-repo/-/raw/master/")
    maven("https://jitpack.io/")
    maven("https://maven.enginehub.org/repo/")
    maven ("https://repo.dmulloy2.net/nexus/repository/public/" )
}

dependencies {
    compileOnly("com.destroystokyo.paper", "paper-api", "1.16.4-R0.1-SNAPSHOT")
    compileOnly("com.destroystokyo.paper", "paper", "1.16.4-R0.1-SNAPSHOT")
    compileOnly("com.googlecode.json-simple", "json-simple", "1.1")
    testImplementation(kotlin("test-junit"))
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