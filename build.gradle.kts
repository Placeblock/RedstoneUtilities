plugins {
    id("java")
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("io.papermc.paperweight.userdev") version "1.3.5"
    id("xyz.jpenilla.run-paper") version "1.0.6" // Adds runServer and runMojangMappedServer tasks for testing

}

bukkit {
    load = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder.POSTWORLD
    main = "de.placeblock.redstoneutilities.RedstoneUtilities"
    description = "RedstoneUtilities"
    apiVersion = "1.20"
    authors = listOf("Placeblock")
    name = "RedstoneUtilities"
}

group = "de.placeblock"
version = "1.4"

repositories {
    mavenCentral()
}

dependencies {
    paperDevBundle("1.20-R0.1-SNAPSHOT")

    annotationProcessor("org.projectlombok:lombok:1.18.26")
    compileOnly("org.projectlombok:lombok:1.18.26")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks {
    // Configure reobfJar to run when invoking the build task
    assemble {
        dependsOn(reobfJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release.set(17)
    }

    jar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        dependsOn(configurations.runtimeClasspath)
        from("${project.rootProject.buildDir}/resources/main")
        from({
            configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
        })
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
    }

    reobfJar {
        outputJar.set(layout.buildDirectory.file("${project.rootProject.buildDir}/libs/plugin/${project.rootProject.name}-${project.version}.jar"))
    }

    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("1.20.1")
    }
}