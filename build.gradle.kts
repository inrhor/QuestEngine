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
    version = "6.0.10-91"
}

repositories {
    mavenCentral()
    maven("https://jitpack.io") // ItemsAdder
    maven("https://repo.dmulloy2.net/repository/public/") // ProtocolLib
    maven("https://repo.codemc.org/repository/maven-public/") // authMe
    maven("https://nexus.phoenixdevt.fr/repository/maven-public/")
    maven {
        url = uri("http://106.13.64.5:8081/repository/maven-public/")
        isAllowInsecureProtocol = true
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
    compileOnly("public:Citizens:1.0.0")
    compileOnly("public:WorldGuard:7.0.7")
    compileOnly("ink.ptms:Adyeshach:1.4.21")
    compileOnly("fr.xephi:authme:5.6.0-beta2")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.7.0")
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib:1.6.0")
    compileOnly("io.lumine:MythicLib-dist:1.5.1-SNAPSHOT")
    compileOnly("com.github.LoneDev6:api-itemsadder:3.2.5")
    compileOnly("api:csgplus:1.8.2")
    compileOnly("hiusers:BigDoors:1.0.0")
    compileOnly("hiusers:ServerTutorialPlus:1.22.1")
    compileOnly("hiusers:WorldEdit:7.2.13")
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