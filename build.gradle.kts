import org.gradle.kotlin.dsl.register
import xyz.jpenilla.runpaper.task.RunServer
import java.util.Properties
import kotlin.apply
import kotlin.collections.set

plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.1"
    id("de.eldoria.plugin-yml.bukkit") version "0.7.1"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "me.drownek"
version = "1.0"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://jitpack.io")
    maven("https://storehouse.okaeri.eu/repository/maven-releases/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots")
    maven("https://repo.panda-lang.org/releases")
}

dependencies {
    implementation("com.github.Drownek.light-platform:light-platform-bukkit:2.1.1-beta2")
    implementation("com.github.Drownek:data-gatherer-bukkit:2.0.2")

    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")

    implementation("de.rapha149.signgui:signgui:2.5.4")

    compileOnly("com.github.decentsoftware-eu:decentholograms:2.8.5")

    /* lombok */
    val lombok = "1.18.32"
    compileOnly("org.projectlombok:lombok:$lombok")
    annotationProcessor("org.projectlombok:lombok:$lombok")
}

bukkit {
    main = "me.drownek.skydrops.SkyDropsPlugin"
    apiVersion = "1.13"
    author = "Drownek"
    depend = listOf("DecentHolograms")
    permissions {
        register("skydrops.command.editdrops") {
            description = "Allows editing airdrop loot through the GUI"
        }
        register("skydrops.command.reload") {
            description = "Allows reloading plugin configurations"
        }
        register("skydrops.command.removedrops") {
            description = "Allows removing all active airdrops from the world"
        }
        register("skydrops.command.spawndrop") {
            description = "Allows manually spawning airdrops at specified locations"
        }
        register("skydrops.command.setnextlocation") {
            description = "Allows setting the next airdrop location"
        }
        register("skydrops.command.settings") {
            description = "Allows opening in-game GUI settings"
        }
    }
}

tasks.shadowJar {
    minimize {
        // exclude every version of the SignGUI dependency using a Regex string
        exclude(dependency("de\\.rapha149\\.signgui:signgui:.*"))
    }

    archiveClassifier.set("")

    exclude(
        "org/intellij/lang/annotations/**",
        "org/jetbrains/annotations/**",
        "META-INF/**",
        "javax/**"
    )

    listOf(
        "de.rapha149.signgui",
        "net.kyori"
    ).forEach { relocate(it, "me.drownek.skydrops.libs.$it") }

    /* Fail as it wont work on server versions with plugin remapping */
    duplicatesStrategy = DuplicatesStrategy.FAIL
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
    options.encoding = "UTF-8"
}

val runVersions = mapOf(
    "1.18.2" to 17,
    "1.19.4" to 17,
    "1.20.6" to 21,
    "1.21.5" to 21,
    "1.21.6" to 21,
    "1.21.7" to 21,
)

tasks {
    val randomPort = true
    val port = 25566

    runVersions.forEach { (key, value) ->
        val n = key.replace(".", "_")
        register("run$n", RunServer::class) {
            minecraftVersion(key)

            /* Automatically accept EULA */
            jvmArgs("-Dcom.mojang.eula.agree=true")

            downloadPlugins {
                url("https://ci.lucko.me/job/spark/494/artifact/spark-bukkit/build/libs/spark-1.10.144-bukkit.jar")
                url("https://github.com/DecentSoftware-eu/DecentHolograms/releases/download/2.9.6/DecentHolograms-2.9.6.jar")
                url("https://github.com/ViaVersion/ViaVersion/releases/download/5.4.2/ViaVersion-5.4.2.jar")
                url("https://github.com/ViaVersion/ViaBackwards/releases/download/5.4.2/ViaBackwards-5.4.2.jar")
                url("https://github.com/Test-Account666/PlugManX/releases/download/2.4.1/PlugManX-2.4.1.jar")
            }

            val runDir = layout.projectDirectory.dir("run$n")
            runDirectory.set(runDir)
            pluginJars.from(shadowJar.flatMap { it.archiveFile })

            /* Start server with specified Java version */
            val toolchains = project.extensions.getByType<JavaToolchainService>()
            javaLauncher.set(toolchains.launcherFor {
                languageVersion.set(JavaLanguageVersion.of(value))
            })

            /* Assign random or specified port for multiple instances at the same time */
            doFirst {
                val runDirFile = runDir.asFile
                if (!runDirFile.exists()) {
                    runDirFile.mkdirs()
                }

                val serverPropertiesFile = runDirFile.resolve("server.properties")
                if (!serverPropertiesFile.exists()) {
                    serverPropertiesFile.createNewFile()
                }

                val props = Properties().apply {
                    serverPropertiesFile.inputStream().use { load(it) }
                }

                val actualPort = if (randomPort) (20000..40000).random() else port
                props["server-port"] = actualPort.toString()

                serverPropertiesFile.outputStream().use { props.store(it, null) }

                println(">> Starting server $key on port $actualPort")
            }
        }
    }
}