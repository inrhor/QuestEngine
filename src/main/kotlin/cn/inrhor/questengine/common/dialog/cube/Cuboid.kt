package cn.inrhor.questengine.common.dialog.cube

import org.bukkit.Location
import org.bukkit.World
import kotlin.math.max
import kotlin.math.min

class Cuboid(world: World?, x1: Int, y1: Int, z1: Int, x2: Int, y2: Int, z2: Int) {
    private val world: World = world!!
    private val minX: Int = min(x1, x2)
    private val maxX: Int = max(x1, x2)
    private val minY: Int = min(y1, y2)
    private val maxY: Int = max(y1, y2)
    private val minZ: Int = min(z1, z2)
    private val maxZ: Int = max(z1, z2)

    constructor(loc1: Location, loc2: Location) : this(
        loc1.world,
        loc1.blockX,
        loc1.blockY,
        loc1.blockZ,
        loc2.blockX,
        loc2.blockY,
        loc2.blockZ
    ) {
    }

    private fun getWorld(): World {
        return world
    }

    operator fun contains(cuboid: Cuboid): Boolean {
        return cuboid.getWorld() == world && cuboid.minX >= minX && cuboid.maxX <= maxX && cuboid.minY >= minY && cuboid.maxY <= maxY && cuboid.minZ >= minZ && cuboid.maxZ <= maxZ
    }

    operator fun contains(location: Location): Boolean {
        return contains(location.blockX, location.blockY, location.blockZ)
    }

    fun contains(x: Int, y: Int, z: Int): Boolean {
        return x in minX..maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ
    }

}