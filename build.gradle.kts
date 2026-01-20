plugins {
    id("java-library")
    id("scala")
    id("maven-publish")
    id("com.gtnewhorizons.retrofuturagradle") version "1.4.0"
}

group = "cn.academy"
version = "1.0.7"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
        vendor.set(org.gradle.jvm.toolchain.JvmVendorSpec.AZUL)
    }
    withSourcesJar()
}

minecraft {
    mcVersion.set("1.7.10")
    username.set("Developer")
    extraRunJvmArguments.add("-ea:${project.group}")
}

// Configure source sets for mixed Java/Scala
sourceSets {
    main {
        java.setSrcDirs(emptyList<File>())
        scala.setSrcDirs(listOf("src/main/java", "src/main/scala"))
    }
}

tasks.processResources {
    val projVersion = project.version.toString()
    inputs.property("version", projVersion)
    inputs.property("mcversion", "1.7.10")

    filesMatching("mcmod.info") {
        expand(mapOf("version" to projVersion, "mcversion" to "1.7.10"))
    }
}

// Replace @VERSION@ in Java/Scala source files
tasks.withType<ScalaCompile> {
    doFirst {
        val srcFile = file("src/main/java/cn/academy/core/AcademyCraft.java")
        if (srcFile.exists()) {
            val content = srcFile.readText()
            val replaced = content
                .replace("@VERSION@", project.version.toString())
                .replace("@@LL_VERSION@", "@1.2.3")
            srcFile.writeText(replaced)
        }
    }
    doLast {
        // Restore the original file after compilation
        val srcFile = file("src/main/java/cn/academy/core/AcademyCraft.java")
        if (srcFile.exists()) {
            val content = srcFile.readText()
            val restored = content
                .replace("= \"${project.version}\"", "= \"@VERSION@\"")
                .replace("@1.2.3", "@@LL_VERSION@")
            srcFile.writeText(restored)
        }
    }
}

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        name = "GTNH Maven"
        url = uri("https://nexus.gtnewhorizons.com/repository/public/")
    }
    maven {
        name = "CurseMaven"
        url = uri("https://cursemaven.com")
    }
}

dependencies {
    implementation("org.scala-lang:scala-library:2.11.7")

    // LambdaLib from mavenLocal
    implementation("cn.lambdalib:LambdaLib:1.2.3:dev")

    // CodeChickenLib and CodeChickenCore from GTNH
    implementation(rfg.deobf("com.github.GTNewHorizons:CodeChickenLib:1.1.5.7:dev"))
    implementation(rfg.deobf("com.github.GTNewHorizons:CodeChickenCore:1.1.8:dev"))
    runtimeOnly(rfg.deobf("com.github.GTNewHorizons:NotEnoughItems:2.6.34-GTNH:dev"))
}

tasks.jar {
    manifest {
        attributes(
            "FMLCorePluginContainsFMLMod" to "true"
        )
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<ScalaCompile> {
    scalaCompileOptions.encoding = "UTF-8"
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
