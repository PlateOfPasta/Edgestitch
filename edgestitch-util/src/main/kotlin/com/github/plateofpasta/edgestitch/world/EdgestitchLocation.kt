/*
 * MIT License
 *
 * Copyright (c) 2020-2020 PlateOfPasta
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.plateofpasta.edgestitch.world

import com.github.plateofpasta.edgestitch.block.EdgestitchBlock
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

/** Abstracts Minecraft primitives [World] and [BlockPos]. */
open class EdgestitchLocation(val world: EdgestitchWorld, val blockPos: BlockPos) {
  val chunkX: Int
    get() = world.getBlockChunkX(blockPos)
  val chunkZ: Int
    get() = world.getBlockChunkZ(blockPos)
  val x: Int
    get() = blockPos.x
  val y: Int
    get() = blockPos.y
  val z: Int
    get() = blockPos.z
  val block: EdgestitchBlock
    get() = world.getBlock(blockPos)

  /**
   * Coordinate based constructor.
   *
   * @param world Location world.
   * @param x X-coordinate.
   * @param y Y-coordinate.
   * @param z Z-coordinate.
   */
  constructor(world: EdgestitchWorld, x: Int, y: Int, z: Int) : this(world, BlockPos(x, y, z))

  /**
   * 3D vector based constructor.
   *
   * @param world Location world.
   * @param coord3D 3D vector of location.
   */
  constructor(world: EdgestitchWorld, coord3D: Vec3d?) : this(world, BlockPos(coord3D))

  /**
   * Constructor that uses minecraft primitives.
   * @param world Location world.
   * @param blockPos Location position.
   */
  constructor(world: World, blockPos: BlockPos) : this(EdgestitchWorld(world), blockPos)

  /**
   * Checks if the position of the given location is equivalent to this location.
   *
   * @param other Location to compare against.
   * @return `true` if this and other have equivalent positions in the world, else `false`.
   */
  fun equalsPosition(other: EdgestitchLocation): Boolean {
    return blockPos == other.blockPos
  }

  /**
   * Gets the distance between two locations. Delegates to [distanceSquared].
   *
   * @param other Other location.
   * @return Distance between this location and other.
   * @throws IllegalArgumentException Throws if the locations have different worlds.
   */
  @Throws(IllegalArgumentException::class)
  fun distance(other: EdgestitchLocation): Double {
    return Math.sqrt(distanceSquared(other))
  }

  /**
   * Gets the square distance between two locations.
   *
   * @param other Other location.
   * @return Square distance between this location and other.
   * @throws IllegalArgumentException Throws if the locations have different worlds.
   */
  @Throws(IllegalArgumentException::class)
  fun distanceSquared(other: EdgestitchLocation): Double {
    require(world == other.world) { "Locations do not share the same world" }
    return blockPos.getSquaredDistance(other.blockPos)
  }

  /**
   * Gets the distance between two locations without checking if the locations are in the same
   * world. Delegates to [distanceSquaredUnsafe].
   *
   * @param other Other location.
   * @return Distance between this location and other.
   * @throws IllegalArgumentException Throws if the locations have different worlds.
   */
  fun distanceUnsafe(other: EdgestitchLocation): Double {
    return Math.sqrt(distanceSquaredUnsafe(other))
  }

  /**
   * Gets the square distance between two locations without checking if the locations are in the
   * same world.
   *
   * @param other Other location.
   * @return Square distance between this location and other.
   * @throws RuntimeException Throws if the locations have different worlds.
   */
  fun distanceSquaredUnsafe(other: EdgestitchLocation): Double {
    return blockPos.getSquaredDistance(other.blockPos)
  }
}
