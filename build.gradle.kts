plugins {
    java
    id("io.izzel.taboolib") version "1.33"
    id("org.jetbrains.kotlin.jvm") version "1.5.31"
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
            "module-metrics",
            "platform-bukkit")
    description {
        contributors {
            name("inrhor")
            desc("Minecraft Quest Engine System")
        }
        dependencies {
            name("Adyeshach").optional(true)
            name("Citizens").optional(true)
            name("ProtocolLib").optional(true)
            name("PlaceholderAPI").optional(true)
            name("BigDoors").optional(true)
        }
        prefix("QuestEngine")
    }
    classifier = null
    version = "6.0.7-6"
}

repositories {
    mavenCentral()
    maven("https://repo.tabooproject.org/storages/public/releases")
    maven("https://repo.dmulloy2.net/repository/public/") // ProtocolLib
}

dependencies {
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
    compileOnly("ink.ptms:Adyeshach:1.3.19")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.7.0")
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib:1.6.0")
    compileOnly(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
}