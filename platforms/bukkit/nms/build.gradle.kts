plugins {
    id("io.papermc.paperweight.userdev")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    api(project(":platforms:bukkit:common"))
    paperweight.paperDevBundle(Versions.Bukkit.paperDevBundle)
    implementation("xyz.jpenilla", "reflection-remapper", Versions.Bukkit.reflectionRemapper)
}

tasks.register("printClasspath") {
    doLast {
        configurations.compileClasspath.get().files.forEach { file ->
            println("CP_ENTRY: " + file.absolutePath)
        }
    }
}