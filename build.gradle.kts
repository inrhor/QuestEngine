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
        dependencies {
            name("QuestEngine").with("bukkit")
            name("Adyeshach").optional(true)
            name("Citizens").optional(true)
        }
        prefix("QuestEngine")
    }
    version = "6.0.0-pre12"
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("ink.ptms.core:v11701:11701:mapped")
    compileOnly("ink.ptms.core:v11701:11701:universal")
    compileOnly("ink.ptms.core:v11604:11604:all")
    compileOnly("ink.ptms.core:v11600:11600:all")
    compileOnly("public:Citizens:1.0.0")
    compileOnly("ink.ptms:Adyeshach:1.2.1")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
}