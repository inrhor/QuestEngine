package cn.inrhor.questengine.common.effect

import org.bukkit.entity.Player
import taboolib.common.platform.ProxyParticle
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.util.Location
import taboolib.module.effect.ParticleSpawner

class EffectSpawner(val viewer: Player): ParticleSpawner {
    override fun spawn(location: Location) {
        ProxyParticle.VILLAGER_HAPPY.sendTo(adaptPlayer(viewer), location)
    }
}