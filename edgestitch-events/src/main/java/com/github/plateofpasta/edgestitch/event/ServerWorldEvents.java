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

package com.github.plateofpasta.edgestitch.event;

import com.github.plateofpasta.edgestitch.mixin.MixinServerWorld;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Events related to {@link net.minecraft.server.world.ServerWorld}. */
public class ServerWorldEvents {
  public static final Event<ServerWorldCanPlayerModifyCallback> PLAYER_MODIFY =
      EventFactory.createArrayBacked(
          ServerWorldCanPlayerModifyCallback.class,
          (listeners) ->
              (player, pos) -> {
                for (ServerWorldCanPlayerModifyCallback event : listeners) {
                  ActionResult result = event.modifyAt(player, pos);

                  if (result != ActionResult.PASS) {
                    return result;
                  }
                }
                return ActionResult.PASS;
              });

  public static final Event<ServerWorldSpawnEntityCallback> ENTITY_SPAWN =
      EventFactory.createArrayBacked(
          ServerWorldSpawnEntityCallback.class,
          (listeners) ->
              (world, entity) -> {
                for (ServerWorldSpawnEntityCallback event : listeners) {
                  TypedActionResult<Boolean> result = event.spawnEvent(world, entity);
                  if (result.getResult() != ActionResult.PASS) {
                    return result;
                  }
                }
                return TypedActionResult.pass(true);
              });

  /** Prevent instantiations of this class. */
  private ServerWorldEvents() {}

  /**
   * Fabric callback implementation for when a player tries to modify something at a position in the
   * game world.
   *
   * @see MixinServerWorld
   */
  @FunctionalInterface
  public interface ServerWorldCanPlayerModifyCallback {
    /**
     * Callback for this interface.
     *
     * @param player Player dropping the item.
     * @param pos Position in the game world the player is modifying.
     */
    ActionResult modifyAt(PlayerEntity player, BlockPos pos);
  }

  /**
   * Fabric callback implementation for when entities are spawned in the ServerWorld.
   *
   * @see MixinServerWorld
   */
  @FunctionalInterface
  public interface ServerWorldSpawnEntityCallback {
    /**
     * Callback for this interface.
     *
     * @param world World entity is being spawned in.
     * @param entity Entity being spawned.
     * @return PASS if the function should continue operation, else FAIL if the injected function
     *     should return early with the given Boolean value.
     */
    TypedActionResult<Boolean> spawnEvent(World world, Entity entity);
  }
}
