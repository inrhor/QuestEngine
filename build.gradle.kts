plugins {
    `java-library`
    `maven-publish`
    id("io.izzel.taboolib") version "1.55"
    id("org.jetbrains.kotlin.jvm") version "1.5.10"
}

taboolib {
    install("common",
            "common-5",
            "module-chat",
            "module-configuration",
            "module-database",
            "module-kether",
            "module-lang",
            "module-metrics",
            "module-nms",
            "module-nms-util",
            "module-effect",
            "module-ui",
            "module-navigation",
            "module-metrics",
            "platform-bukkit")
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
        }
        prefix("QuestEngine")
    }
    classifier = null
    version = "6.0.10-68"
}

repositories {
    mavenCentral()
    maven("https://repo.dmulloy2.net/repository/public/") // ProtocolLib
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://nexus.phoenixdevt.fr/repository/maven-public/")
}

dependencies {
    taboo("ink.ptms:um:1.0.0-beta-23")
    compileOnly("ink.ptms.core:v11900:11900-minimize:mapped")
    compileOnly("ink.ptms.core:v11701:11701-minimize:mapped")
    compileOnly("ink.ptms.core:v11701:11701-minimize:universal")
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

sourceSets {
    main {
        java {
            srcDir("src/main")
        }
    }
}

java {
    withSourcesJar()
}