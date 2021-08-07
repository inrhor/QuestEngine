plugins {
    java
    id("io.izzel.taboolib") version "1.12"
    id("org.jetbrains.kotlin.jvm") version "1.5.20"
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
            "platform-bukkit")
    description {
        contributors {
            name("inrhor")
        }
        dependencies {
            name("Adyeshach").optional(true)
            name("Citizens").optional(true)
            name("ProtocolLib").optional(true)
        }
        prefix("QuestEngine")
    }
    classifier = null
    version = "6.0.0-pre28"
}

repositories {
    mavenCentral()
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    compileOnly("ink.ptms.core:v11701:11701:mapped")
    compileOnly("ink.ptms.core:v11701:11701:universal")
    compileOnly("ink.ptms.core:v11604:11604:all")
    compileOnly("ink.ptms.core:v11600:11600:all")
    compileOnly("ink.ptms.core:v11500:11500:all")
    compileOnly("ink.ptms.core:v11400:11400:all")
    compileOnly("ink.ptms.core:v11300:11300:all")
    compileOnly("ink.ptms.core:v11200:11200:all")
    compileOnly("ink.ptms.core:v11100:11100:all")
    compileOnly("ink.ptms.core:v11000:11000:all")
    compileOnly("ink.ptms.core:v10900:10900:all")
    compileOnly("public:Citizens:1.0.0")
    compileOnly("ink.ptms:Adyeshach:1.2.1")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.7.0")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
}