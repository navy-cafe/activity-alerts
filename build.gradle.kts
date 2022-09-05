plugins {
    id("java")
    `maven-publish`
    id("io.papermc.paperweight.userdev")
    id("com.github.johnrengelman.shadow")
    id("org.checkerframework")
}

group = "cafe.navy"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    paperDevBundle("1.19.2-R0.1-SNAPSHOT")
    implementation(libs.bedrock.core)
    implementation(libs.cloud.paper)
    implementation(libs.configurate.hocon)
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(18)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name()
        expand(project.properties)
    }
    shadowJar {
        fun reloc(pkg: MinimalExternalModuleDependency) = relocate(pkg.module.group, "${pkg.module.group}.dependencies")

        reloc(libs.cloud.paper.get())
        reloc(libs.bedrock.core.get())
        reloc(libs.configurate.hocon.get())
    }
    reobfJar {
        outputJar.set(rootProject.layout.buildDirectory.file("libs/${project.name}.jar"))
    }
}
