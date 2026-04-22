plugins {
    java
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("com.gradleup.shadow") version "8.3.8"
    // Reative quando o dev bundle 26.1.2 for publicado:
    // id("io.papermc.paperweight.userdev") version "2.0.0-beta.17"
}

group = property("group") as String
version = property("pluginVersion") as String

val paperApiVersion = property("paperApiVersion") as String
val javaVersion = (property("javaVersion") as String).toInt()

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(javaVersion))
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://jitpack.io") // Vault
}

dependencies {
    // Compilacao via paper-api enquanto o dev bundle 26.1.2 nao esta publico.
    // Quando migrar para paperweight, troque esta linha por:
    //   paperweight.paperDevBundle(paperApiVersion)
    compileOnly("io.papermc.paper:paper-api:${paperApiVersion}")

    // Persistencia (shaded)
    implementation("com.zaxxer:HikariCP:6.2.1")
    implementation("com.mysql:mysql-connector-j:9.1.0")
    implementation("org.xerial:sqlite-jdbc:3.46.1.3")

    // Soft-depends
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(javaVersion)
    }

    processResources {
        val props = mapOf(
            "version" to project.version.toString(),
            "paperApiVersion" to paperApiVersion
        )
        inputs.properties(props)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    shadowJar {
        archiveClassifier.set("")
        mergeServiceFiles()
        relocate("com.zaxxer.hikari", "com.bryanmz.betterclans.libs.hikari")
        relocate("org.sqlite", "com.bryanmz.betterclans.libs.sqlite")
        relocate("com.mysql", "com.bryanmz.betterclans.libs.mysql")
    }

    assemble {
        dependsOn(shadowJar)
    }

    runServer {
        minecraftVersion("1.21.4")
    }
}
