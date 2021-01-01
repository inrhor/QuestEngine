package cn.inrhor.questengine

import cn.inrhor.questengine.common.dialog.holo.DialogHolo
import cn.inrhor.questengine.common.hologram.IHolo
import io.izzel.taboolib.module.inject.TListener
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.inventory.ItemStack


@TListener
class Test : Listener {
    @EventHandler
    fun test(ev: PlayerDropItemEvent) {
        val item = ItemStack(Material.APPLE)
        val meta = item.itemMeta
        val lore = listOf("§d§r§9§7§a§d§3§1")
        meta?.lore = lore
        item.itemMeta = meta

//        val entityID = 1000

//        PacketHoloTest.sendHologram(entityID, ev.player, "测试全息", item)

        val player = ev.player
//        PacketHolo().sendHolo(player, UUID.randomUUID().toString(), player.location, mutableListOf("%animations_<typewriter cursor=_ pause=10 reverse>hashdkasdh</typewriter>%"), mutableListOf(item))

        /*val dialogHolo = DialogHolo(mutableListOf(player))
        dialogHolo.runRunnable()*/
        val holo = IHolo("test", player.location, mutableListOf(player), mutableListOf("asd", "eeeeee"), mutableListOf())
        holo.init()
    }

}