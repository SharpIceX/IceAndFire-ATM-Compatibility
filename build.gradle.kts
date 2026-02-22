/**
 * SPDX-FileCopyrightText: 2026 锐冰(SharpIce) <VupRbl@163.com>
 *
 * SPDX-License-Identifier: 0BSD
 */

plugins {
    id("java-library")
    id("net.neoforged.moddev") version "2.0.140"
    id("idea")
}

val mod_id: String by project

tasks.named<Wrapper>("wrapper").configure {
    distributionType = Wrapper.DistributionType.BIN
}

version = project.property("mod_version") as String
group = project.property("mod_group_id") as String

repositories {
    maven {
        name = "CurseMaven"
        url = uri("https://cursemaven.com")
        content {
            includeGroup("curse.maven")
        }
    }
}

base {
    archivesName.set(mod_id)
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))

neoForge {
    version = libs.versions.neoforge.get()

	parchment {
        mappingsVersion.set(libs.versions.parchment.mappings.get())
        minecraftVersion.set(libs.versions.parchment.minecraft.get())
    }

    runs {
        create("client") {
            client()
            systemProperty("neoforge.enabledGameTestNamespaces", mod_id)
        }

        create("server") {
            server()
            programArgument("--nogui")
            systemProperty("neoforge.enabledGameTestNamespaces", mod_id)
        }

        create("gameTestServer") {
            type.set("gameTestServer")
            systemProperty("neoforge.enabledGameTestNamespaces", mod_id)
        }

        create("data") {
            data()
            programArguments.addAll("--mod", mod_id, "--all", "--output", file("src/generated/resources/").absolutePath, "--existing", file("src/main/resources/").absolutePath)
        }

        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")
            logLevel.set(org.slf4j.event.Level.DEBUG)
        }
    }

    mods {
        create(mod_id) {
            sourceSet(sourceSets.main.get())
			sourceSet(sourceSets.test.get())
        }
    }
}

sourceSets.main.get().resources.srcDir("src/generated/resources")

val localRuntime: Configuration by configurations.creating
configurations.runtimeClasspath.get().extendsFrom(localRuntime)

dependencies {
	// Allthemodium
    runtimeOnly(libs.mod.allthemodium)
	runtimeOnly(libs.mod.ato)
    runtimeOnly(libs.mod.geckolib)

	// Ice And Fire Community Edition
    implementation(libs.mod.iceandfire.ce)
	implementation(libs.mod.uranus)
    runtimeOnly(libs.mod.jupiter)

	// FTB Quests
    implementation(libs.mod.ftb.quests)
	implementation(libs.mod.ftb.libray)
	runtimeOnly(libs.mod.ftb.teams)
}

val generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {
    val replaceProperties = mapOf(
        "minecraft_version"       to libs.versions.minecraft.get(),
        "neo_version"             to libs.versions.neoforge.get(),
        "mod_id"                  to project.property("mod_id"),
        "mod_name"                to project.property("mod_name"),
        "mod_version"             to project.property("mod_version"),
		"minecraft_version_range"             to project.property("minecraft_version_range")
    )
    inputs.properties(replaceProperties)
    expand(replaceProperties)
    from("src/main/templates")
    into("build/generated/sources/modMetadata")
}

sourceSets.main.get().resources.srcDir(generateModMetadata)
neoForge.ideSyncTask(generateModMetadata)

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

java {
    withSourcesJar()
}

tasks.named<Jar>("jar") {
    from(project.rootDir) {
        include("COPYING")
    }

	manifest {
        attributes(mapOf(
            "Specification-Title" to project.property("mod_id"),
            "Specification-Vendor" to "锐冰(SharpIce)",
            "Specification-Version" to "1",
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "锐冰(SharpIce)",
            "Built-On-Minecraft" to libs.versions.minecraft.get()
        ))
    }
}

tasks.named<Jar>("sourcesJar") {
    from(project.rootDir) {
        include("COPYING")
    }
}
