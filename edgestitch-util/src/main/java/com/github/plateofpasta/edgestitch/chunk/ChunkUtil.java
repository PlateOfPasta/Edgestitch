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

package com.github.plateofpasta.edgestitch.chunk;

import com.github.plateofpasta.edgestitch.mixin.MixinAccessorServerChunkManager;
import com.github.plateofpasta.edgestitch.mixin.MixinAccessorTACS;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/** Class for chunk utility functions. */
public class ChunkUtil {
  /**
   * Chunk regeneration requires a 17x17 list of chunks for certain generation steps. The center
   * chunk is the generation target.
   *
   * @param center Position of the chunk that is the generation target.
   * @return List of minecraft Chunks required for chunk regeneration.
   */
  private static List<net.minecraft.world.chunk.Chunk> populateBaseChunkList(ChunkPos center) {
    ArrayList<Chunk> chunkList = new ArrayList<>(17 * 17);
    int j = center.x;
    int k = center.z;

    for (int n = j - 8; n <= j + 8; ++n) {
      for (int o = k - 8; o <= k + 8; ++o) {
        chunkList.add(new ProtoChunk(new ChunkPos(n, o), UpgradeData.NO_UPGRADE_DATA));
      }
    }
    return chunkList;
  }

  /**
   * Regenerates the given chunk in this world.
   *
   * @param chunk Chunk to regenerate.
   * @return {@code true} if chunk regeneration succeeded, else {@code false};
   */
  public static boolean regenerateChunk(World world, ChunkPos chunk) {
    try {
      regenerateChunkImpl(world, chunk);
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  /**
   * Loads a 16x16x16 section of a chunk into this world.
   *
   * @param targetChunk Position of the chunk we're loading.
   * @param loadSection Section that we're loading blocks from.
   */
  private static void loadChunkSectionToWorld(
      World world, ChunkPos targetChunk, ChunkSection loadSection) {
    if (null == loadSection) {
      return;
    }
    // Chunk blocks are stored in YZX coordinate order in ChunkSections and Chunk NBT data.
    // Chunk sections range from 0 to 15 in height.
    for (int coordY = loadSection.getYOffset(); coordY <= loadSection.getYOffset() + 15; coordY++) {
      for (int coordZ = targetChunk.getStartZ(); coordZ <= targetChunk.getEndZ(); coordZ++) {
        for (int coordX = targetChunk.getStartX(); coordX <= targetChunk.getEndX(); coordX++) {
          if (loadSection.isEmpty()) {
            world.setBlockState(new BlockPos(coordX, coordY, coordZ), Blocks.AIR.getDefaultState());
          } else {
            world.setBlockState(
                new BlockPos(coordX, coordY, coordZ),
                // The (coord & 15) operation is used because that's how ProtoChunk does it.
                loadSection.getBlockState(coordX & 15, coordY & 15, coordZ & 15));
          }
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
   *     instance of ServerWorld.
   * @throws ExecutionException Failure of the regeneration process due to a chunk Future not
   *     completing successfully.
   * @throws InterruptedException Failure of the regeneration process due to a chunk Future not
   *     completing successfully.
   * @throws TimeoutException Failure of the regeneration process due to a chunk Future not
   *     completing successfully.
   */
  private static void regenerateChunkImpl(World world, ChunkPos chunkPos)
      throws ClassCastException, ExecutionException, InterruptedException, TimeoutException {
    ChunkStatus[] statusOrder =
        new ChunkStatus[] {
          ChunkStatus.STRUCTURE_STARTS,
          ChunkStatus.STRUCTURE_REFERENCES,
          ChunkStatus.BIOMES,
          ChunkStatus.NOISE,
          ChunkStatus.SURFACE,
          ChunkStatus.CARVERS,
          ChunkStatus.LIQUID_CARVERS,
          ChunkStatus.FEATURES
        };
    if (!(world instanceof ServerWorld)) {
      throw new ClassCastException("Could not cast world to ServerWorld for chunk regeneration");
    }
    ServerWorld serverWorld = (ServerWorld) world;
    // 17x17 chunk list is required
    List<Chunk> chunkList = populateBaseChunkList(chunkPos);
    ServerChunkManager chunkManager = serverWorld.getChunkManager();
    ThreadedAnvilChunkStorage tacs =
        ((MixinAccessorServerChunkManager) chunkManager).accessThreadedAnvilChunkStorage();
    MixinAccessorTACS publicTACS = (MixinAccessorTACS) tacs;

    for (ChunkStatus currentStatus : statusOrder) {
      // Run all of the chunk generation tasks in order.
      // There is no need to store the future return value because the chunk is maintained in
      // the chunkList.
      // Either all tasks complete successfully or we abort the process.
      currentStatus
          .runGenerationTask(
              serverWorld,
              chunkManager.getChunkGenerator(),
              publicTACS.publicGetStructureManager(),
              publicTACS.publicGetServerLightingProvider(),
              (chunk) -> {
                // This should never be called since we don't do the ChunkStatus.FULL task.
                throw new RuntimeException("Error in regenerating chunk");
              },
              chunkList)
          .get(10, TimeUnit.SECONDS)
          .orThrow();
    }
    net.minecraft.world.chunk.Chunk centerChunk = chunkList.get(chunkList.size() / 2);
    for (int i = 0; i < 16; i++) {
      ChunkSection section = centerChunk.getSectionArray()[i];
      if (null == section) {
        // Empty sections only contain air blocks.
        section = new ChunkSection(i << 4);
      }
      loadChunkSectionToWorld(world, centerChunk.getPos(), section);
    }
  }
}
