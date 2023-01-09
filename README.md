![Logo](https://image-1253288465.cos.ap-shanghai.myqcloud.com/2021/08/12/1105255dd4e38.png)

![bstats](https://bstats.org/signatures/bukkit/QuestEngine.svg)
---

[![CodeFactor](https://www.codefactor.io/repository/github/inrhor/questengine/badge)](https://www.codefactor.io/repository/github/inrhor/questengine) 
[![CodeSize](https://img.shields.io/github/languages/code-size/inrhor/QuestEngine)](https://img.shields.io/github/languages/code-size/inrhor/QuestEngine) 
[![GPL-3](https://img.shields.io/github/license/inrhor/QuestEngine)](https://img.shields.io/github/license/inrhor/QuestEngine) 

[![Minecraft-Java](https://img.shields.io/badge/minecraft-Java%201.12--1.19-purple)](https://img.shields.io/badge/minecraft-Java%201.12--1.17-purple)

---
Minecraft - 面向异世界的任务引擎系统

---
### 附属
- 任务笔记
  - [UiUniverse](https://github.com/inrhor/UiUniverse)

---
### 相关链接
- [文档](http://www.questengine.cn/)
- [爱发电](https://afdian.net/item?plan_id=667f008029d611ed900252540025c377)

---
### Kts
```
repositories {
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("com.github.inrhor:QuestEngine:版本")
}
```

### Gradle
```
allprojects {
  repositories {
    maven { url 'https://jitpack.io' }
  }
}

dependencies {
    implementation 'com.github.inrhor:QuestEngine:版本'
}
```

### Maven
```
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.inrhor</groupId>
    <artifactId>QuestEngine</artifactId>
    <version>版本</version>
</dependency>
```
