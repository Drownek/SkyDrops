plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.1"
    id("de.eldoria.plugin-yml.bukkit") version "0.7.1"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "me.drownek"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://jitpack.io")
    maven("https://storehouse.okaeri.eu/repository/maven-releases/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots")
    maven("https://repo.panda-lang.org/releases")
}

dependencies {
    implementation("com.github.Drownek.light-platform:light-platform-bukkit:2.1.1")
    implementation("me.drownek:data-gatherer-bukkit:2.0.1")
    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")

    implementation("de.rapha149.signgui:signgui:2.5.4")

    /* lombok */
    val lombok = "1.18.32"
    compileOnly("org.projectlombok:lombok:$lombok")
    annotationProcessor("org.projectlombok:lombok:$lombok")
}

bukkit {
    main = "me.drownek.skydrops.SkyDropsPlugin"
    apiVersion = "1.13"
    author = "Drownek"
}

tasks.shadowJar {
    archiveClassifier.set("")
//    listOf(
//        "de.rapha149.signgui"
//    ).forEach { relocate(it, "me.drownek.skydrops.libs.$it") }
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

tasks.runServer {
    minecraftVersion("1.19.4")
    javaLauncher = javaToolchains.launcherFor {
        vendor = JvmVendorSpec.JETBRAINS
        languageVersion = JavaLanguageVersion.of(21)
    }
    jvmArgs(
        listOf(
            "-Xms512M",
            "-Xmx2G",
            "-XX:+UseG1GC",
            "-XX:+UnlockExperimentalVMOptions",
            "-XX:+ParallelRefProcEnabled",
            "-XX:MaxGCPauseMillis=100",
            "-XX:+AlwaysPreTouch",
            "-XX:G1HeapRegionSize=4M",
            "-XX:+UseFastUnorderedTimeStamps",
            "-Dcom.mojang.eula.agree=true",
            "-XX:+AllowEnhancedClassRedefinition"
        )
    )
}
