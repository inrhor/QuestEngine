plugins {
    `java-library`
    `maven-publish`
    id("io.izzel.taboolib") version "1.56"
    id("org.jetbrains.kotlin.jvm") version "1.8.0"
}

taboolib {
    install("common")
    install("common-5")
    install("module-chat")
    install("module-configuration")
    install("module-database")
    install("module-kether")
    install("module-lang")
    install("module-metrics")
    install("module-nms")
    install("module-nms-util")
    install("module-effect")
    install("module-ui")
    install("module-navigation")
    install("module-metrics")
    install("platform-bukkit")
    install("expansion-command-helper")
    description {
        contributors {
            name("inrhor")
            desc("Minecraft Quest Engine Core")
        }
        dependencies {
            name("Adyeshach").optional(true)
            name("Citizens").optional(true)
            name("ProtocolLib").optional(true)
            name("PlaceholderAPI").optional(true)
            name("BigDoors").optional(true)
            name("WorldGuard").optional(true)
            name("Csg-Plus").optional(true)
            name("ItemsAdder").optional(true)
            name("MMOItems").optional(true)
            name("AuthMe").optional(true)
            name("Invero").optional(true)
        }
        prefix("QuestEngine")
    }
    classifier = null
    version = "6.0.12-61"
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.hiusers.com/releases")
    }
}

dependencies {
    taboo("ink.ptms:um:1.0.0-beta-23")
    compileOnly("ink.ptms.core:v11903:11903:mapped")
    compileOnly("ink.ptms.core:v11903:11903:universal")
    compileOnly("ink.ptms.core:v11604:11604")
    compileOnly("ink.ptms.core:v11600:11600-minimize")
    compileOnly("ink.ptms.core:v11500:11500")
    compileOnly("ink.ptms.core:v11400:11400-minimize")
    compileOnly("ink.ptms.core:v11300:11300")
    compileOnly("ink.ptms.core:v11200:11200-minimize")
    compileOnly("ink.ptms.core:v11100:11100")
    compileOnly("ink.ptms.core:v11000:11000")
    compileOnly("ink.ptms.core:v10900:10900")
    implementation("plugin:BigDoors:0.1.8.48")
    implementation("plugin:WorldEdit:7.2.15")
    implementation("plugin:WorldGuard:1.5.0")
    implementation("plugin:AuthMe:5.6.0")
    implementation("plugin:CsgPlusPro:1.9.4")
    implementation("plugin:ServerTutorialPlus:1.24.5")
    implementation("plugin:MythicLib:1.5.2")
    implementation("plugin:ItemsAdder:3.6.3")
    implementation("plugin:AuthMe:5.6.0")
    implementation("plugin:ProtocolLib:5.1.0")
    implementation("api:Citizens:2.0.33")
    compileOnly("ink.ptms:Adyeshach:1.4.21")
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib:1.6.0")
    compileOnly("cc.trixey.invero:framework-common:1.0.0-snapshot-1")
    compileOnly("cc.trixey.invero:framework-bukkit:1.0.0-snapshot-1")
    compileOnly("cc.trixey.invero:module-common:1.0.0-snapshot-1")
    compileOnly("cc.trixey.invero:module-core:1.0.0-snapshot-1")

    compileOnly(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

publishing {
    repositories {
        maven {
            url = uri("https://repo.tabooproject.org/repository/releases")
            credentials {
                username = project.findProperty("taboolibUsername").toString()
                password = project.findProperty("taboolibPassword").toString()
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("library") {
            from(components["java"])
            groupId = project.group.toString()
        }
    }
}