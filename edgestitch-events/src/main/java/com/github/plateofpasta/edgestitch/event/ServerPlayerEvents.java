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

import com.github.plateofpasta.edgestitch.mixin.MixinPlayerManager;
import com.github.plateofpasta.edgestitch.mixin.MixinServerPlayerEntity;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;

/** Events related to {@link net.minecraft.server.network.ServerPlayerEntity}. */
public class ServerPlayerEvents {
  public static final Event<ServerPlayerConnectCallback> PLAYER_CONNECT =
      EventFactory.createArrayBacked(
          ServerPlayerConnectCallback.class,
          (listeners) ->
              (playerName) -> {
                for (ServerPlayerConnectCallback event : listeners) {
                  event.connect(playerName);
                }
              });

  public static final Event<ServerPlayerDisconnectCallback> PLAYER_DISCONNECT =
      EventFactory.createArrayBacked(
          ServerPlayerDisconnectCallback.class,
          (listeners) ->
              (playerName) -> {
                for (ServerPlayerDisconnectCallback event : listeners) {
                  event.disconnect(playerName);
                }
              });

  public static final Event<ServerPlayerDropItemCallback> PLAYER_DROP_ITEM =
      EventFactory.createArrayBacked(
          ServerPlayerDropItemCallback.class,
          (listeners) ->
              (playerEntity, stack) -> {
                for (ServerPlayerDropItemCallback event : listeners) {
                  TypedActionResult<ItemEntity> result = event.drop(playerEntity, stack);

                  if (result.getResult() != ActionResult.PASS) {
                    return result;
                  }
                }
                return TypedActionResult.pass(null);
              });

  /** Prevent instantiations of this class. */
  private ServerPlayerEvents() {}

  /**
   * Fabric callback implementation for when a player connects to a server.
   *
   * @see MixinPlayerManager
   */
  @FunctionalInterface
  public interface ServerPlayerConnectCallback {
    /**
     * Callback for this interface. Provides the player name to the implementation.
     *
     * @param playerName String player name.
     */
    void connect(String playerName);
  }

  /**
   * Fabric callback implementation for when a player disconnects to a server.
   *
   * @see MixinPlayerManager
   */
  @FunctionalInterface
  public interface ServerPlayerDisconnectCallback {

    /**
     * Callback for this interface. Provides the player name to the implementation.
     *
     * @param playerName String player name.
     */
    void disconnect(String playerName);
  }

  /**
   * Fabric callback implementation for when a server player drops an item. PASS means the target
   * method is allowed to continue processing and the returnable object is null. FAIL means the
   * target method will return early with the given returnable value.
   *
   * @see MixinServerPlayerEntity
   */
  @FunctionalInterface
  public interface ServerPlayerDropItemCallback {
    /**
     * Callback for this interface.
     *
     * @param playerEntity Player dropping the item.
     * @param stack Item stack being dropped.
     */
    TypedActionResult<ItemEntity> drop(PlayerEntity playerEntity, ItemStack stack);
  }
}
