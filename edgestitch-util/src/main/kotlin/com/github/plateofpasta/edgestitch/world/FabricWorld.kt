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

import com.github.plateofpasta.edgestitch.block.FabricBlock
import com.github.plateofpasta.edgestitch.chunk.ChunkUtil
import net.minecraft.util.Nameable
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.Heightmap
import net.minecraft.world.World

/** Abstracts Minecraft primitive [World]. */
open class FabricWorld(protected val impl: World) {
  val maxY: Int
    // todo Maybe refactor if dimensional roofs matter (e.g. nether). For example
    // return this.world.getDimension().hasCeiling() ? 128 : 256;
    get() = impl.height

  /**
   * Gets the block at the block position.
   *
   * @param blockPos Position of the target block.
   * @return Target block.
   */
  fun getBlock(blockPos: BlockPos?): FabricBlock {
    return FabricBlock(impl.getBlockState(blockPos))
  }

  /**
   * Gets the block at the 3D vector location.
   *
   * @param blockVec3D 3D vector position.
   * @return Target block.
   */
  fun getBlock(blockVec3D: Vec3d?): FabricBlock {
    return FabricBlock(impl.getBlockState(BlockPos(blockVec3D)))
  }

  /**
   * Gets the chunk X-coordinate of the given block position.
   *
   * @param blockPos Block position used to look up the chunk.
   * @return X-coordinate.
   */
  fun getBlockChunkX(blockPos: BlockPos?): Int {
    return ChunkPos(blockPos).x
  }

  /**
   * Gets the chunk Z-coordinate of the given block position.
   *
   * @param blockPos Block position used to look up the chunk.
   * @return Z-coordinate.
   */
  fun getBlockChunkZ(blockPos: BlockPos?): Int {
    return ChunkPos(blockPos).z
  }

  /**
   * Gets the highest Y-coordinate in the chunk at the (X,Z)-coordinate location.
   *
   * @param x X-coordinate.
   * @param z Z-coordinate.
   * @return Highest Y-coordinate.
   */
  fun getHighestY(x: Int, z: Int): Int {
    return impl.getTopY(Heightmap.Type.WORLD_SURFACE, x, z)
  }

  /**
   * Gets the highest Y-coordinate in the chunk at the location. Delegates to (X,Z)-coordinate
   * version.
   *
   * @param location Location in a chunk to get the highest Y-coordinate.
   * @return Highest Y-coordinate.
   */
  fun getHighestY(location: FabricLocation): Int {
    return this.getHighestY(location.x, location.z)
  }

  /**
   * Regenerates the given chunk in this world.
   *
   * @param chunk Chunk to regenerate.
   * @return `true` if chunk regeneration succeeded, else `false`;
   */
  fun regenerateChunk(chunk: ChunkPos): Boolean {
    try {
      ChunkUtil.regenerateChunk(impl, chunk)
    } catch (e: Exception) {
      return false
    }
    return true
  }

  /**
   * Gets the world name.
   *
   * @return Name or an empty string if the name was null.
   */
  fun getName(): String {
    return (impl as Nameable).name.asString() ?: ""
  }

  companion object {
    /**
     * Gets the world name.
     *
     * @param world Minecraft world, same type required by constructor.
     * @return Name or an empty string if the name was null.
     */
    fun getName(world: World): String {
      return (world as Nameable).name.asString() ?: ""
    }
  }
}
