package cn.inrhor.questengine.utlis.location

import org.bukkit.util.Vector
import kotlin.math.max
import kotlin.math.min

data class BoundingBox(
    var minX: Double, var minY: Double, var minZ: Double,
    var maxX: Double, var maxY: Double, var maxZ: Double) {

    fun move(vector: Vector): BoundingBox {
        return move(vector.x, vector.y, vector.z)
    }

    fun move(x: Double, y: Double, z: Double): BoundingBox {
        return BoundingBox(minX + x, minY + y, minZ + z, maxX + x, maxY + y, maxZ + z)
    }

    fun contains(v: Vector): Boolean {
        return contains(v.x, v.y, v.z)
    }

    fun contains(x: Double, y: Double, z: Double): Boolean {
        return x >= minX && x < maxX && y >= minY && y < maxY && z >= minZ && z < maxZ
    }

    fun contains(other: BoundingBox): Boolean {
        return contains(other.minX, other.minY, other.minZ, other.maxX, other.maxY, other.maxZ)
    }

    fun contains(min: Vector, max: Vector): Boolean {
        return contains(min(min.x, max.x), min(min.y, max.y), min(min.z, max.z), max(min.x, max.x), max(min.y, max.y), max(min.z, max.z))
    }

    fun contains(minX: Double, minY: Double, minZ: Double, maxX: Double, maxY: Double, maxZ: Double): Boolean {
        return this.minX <= minX && this.maxX >= maxX && this.minY <= minY && this.maxY >= maxY && this.minZ <= minZ && this.maxZ >= maxZ
    }

    companion object {
        fun initHitBox() = BoundingBox(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
    }
}