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

package com.github.plateofpasta.edgestitch.chunk

import com.github.plateofpasta.edgestitch.mixin.MixinAccessorServerChunkManager
import com.github.plateofpasta.edgestitch.mixin.MixinAccessorTACS
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import net.minecraft.block.Blocks
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.World
import net.minecraft.world.chunk.*

/**
 * Regenerates the given chunk in this world.
 *
 * @param chunk Chunk to regenerate.
 * @return `true` if chunk regeneration succeeded, else `false`;
 */
fun regenerateChunk(world: World, chunk: ChunkPos): Boolean {
  try {
    regenerateChunkImpl(world, chunk)
  } catch (e: Exception) {
    return false
  }
  return true
}

/**
 * Chunk regeneration requires a 17x17 list of chunks for certain generation steps. The center chunk
 * is the generation target.
 *
 * @param center Position of the chunk that is the generation target.
 * @return List of minecraft Chunks required for chunk regeneration.
 */
private fun populateBaseChunkList(center: ChunkPos): List<Chunk> {
  val chunkList = ArrayList<Chunk>(17 * 17)
  val j = center.x
  val k = center.z
  for (n in (j - 8)..(j + 8)) {
    for (o in (k - 8)..(k + 8)) {
      chunkList.add(ProtoChunk(ChunkPos(n, o), UpgradeData.NO_UPGRADE_DATA))
    }
  }
  return chunkList
}

/**
 * Loads a 16x16x16 section of a chunk into this world.
 *
 * @param targetChunk Position of the chunk we're loading.
 * @param loadSection Section that we're loading blocks from.
 */
private fun loadChunkSectionToWorld(
    world: World, targetChunk: ChunkPos, loadSection: ChunkSection?
) {
  if (null == loadSection) {
    return
  }
  // Chunk blocks are stored in YZX coordinate order in ChunkSections and Chunk NBT data.
  // Chunk sections range from 0 to 15 in height.
  for (coordY in (loadSection.yOffset)..(loadSection.yOffset + 15)) {
    for (coordZ in (targetChunk.startZ)..(targetChunk.endZ)) {
      for (coordX in (targetChunk.startX)..(targetChunk.endX)) {
        val blockState =
            if (loadSection.isEmpty) {
              Blocks.AIR.defaultState
            } else {
              // The (coord and 15) operation is used because that's how ProtoChunk does it.
              loadSection.getBlockState(coordX and 15, coordY and 15, coordZ and 15)
            }
        world.setBlockState(BlockPos(coordX, coordY, coordZ), blockState)
      }
    }
  }
}

/**
 * Regenerates a chunk in this world. Guaranteed to regenerate everything exactly the same except
 * features (trees, flowers, grass, etc.); features are still generated, but not necessarily the
 * same as the original.
 *
 * @param chunkPos Position of the chunk to regenerate.
 * @throws ClassCastException Failure of regeneration process due to this object not wrapping an
 * instance of ServerWorld.
 * @throws ExecutionException Failure of the regeneration process due to a chunk Future not
 * completing successfully.
 * @throws InterruptedException Failure of the regeneration process due to a chunk Future not
 * completing successfully.
 * @throws TimeoutException Failure of the regeneration process due to a chunk Future not completing
 * successfully.
 */
@Throws(
    ClassCastException::class,
    ExecutionException::class,
    InterruptedException::class,
    TimeoutException::class)
private fun regenerateChunkImpl(world: World, chunkPos: ChunkPos) {
  val statusOrder =
      arrayOf(
          ChunkStatus.STRUCTURE_STARTS,
          ChunkStatus.STRUCTURE_REFERENCES,
          ChunkStatus.BIOMES,
          ChunkStatus.NOISE,
          ChunkStatus.SURFACE,
          ChunkStatus.CARVERS,
          ChunkStatus.LIQUID_CARVERS,
          ChunkStatus.FEATURES)
  if (world !is ServerWorld) {
    throw ClassCastException("Could not cast world to ServerWorld for chunk regeneration")
  }
  // 17x17 chunk list is required
  val chunkList = populateBaseChunkList(chunkPos)
  val chunkManager = world.chunkManager
  val tacs = (chunkManager as MixinAccessorServerChunkManager).accessThreadedAnvilChunkStorage()
  val publicTACS = tacs as MixinAccessorTACS
  for (currentStatus in statusOrder) {
    // Run all of the chunk generation tasks in order.
    // There is no need to store the future return value because the chunk is maintained in
    // the chunkList.
    // Either all tasks complete successfully or we abort the process.
    currentStatus.runGenerationTask(
            world,
            chunkManager.chunkGenerator,
            publicTACS.publicGetStructureManager(),
            publicTACS.publicGetServerLightingProvider(),
            { throw RuntimeException("Error in regenerating chunk") },
            chunkList)
        .get(10, TimeUnit.SECONDS)
        .orThrow()
  }
  val centerChunk = chunkList[chunkList.size / 2]
  for (i in 0..15) {
    var section = centerChunk.sectionArray[i]
    if (null == section) {
      // Empty sections only contain air blocks.
      section = ChunkSection(i shl 4)
    }
    loadChunkSectionToWorld(world, centerChunk.pos, section)
  }
}
