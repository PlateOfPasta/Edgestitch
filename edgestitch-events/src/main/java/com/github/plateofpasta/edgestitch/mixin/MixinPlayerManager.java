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

package com.github.plateofpasta.edgestitch.mixin;

import com.github.plateofpasta.edgestitch.event.ServerPlayerEvents;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Mixin for passing the name of the world when loaded by the MinecraftServer. */
@Mixin(PlayerManager.class)
public abstract class MixinPlayerManager {
  /**
   * Invokes a callback for when a player connects to a server.
   *
   * @param connection Ignored.
   * @param player Object that provides the player name.
   * @param info Ignored.
   */
  @Inject(method = "onPlayerConnect", at = @At("TAIL"))
  public void onPlayerConnectAfter(
      ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {
    ServerPlayerEvents.PLAYER_CONNECT.invoker().connect(player.getName().asString());
  }

  /**
   * Invokes a callback for when a player is removed (disconnects) from a server.
   *
   * @param player Object that provides the player name.
   * @param info Ignored.
   */
  @Inject(method = "remove", at = @At("TAIL"))
  public void onPlayerRemoveAfter(ServerPlayerEntity player, CallbackInfo info) {
    ServerPlayerEvents.PLAYER_DISCONNECT.invoker().disconnect(player.getName().asString());
  }
}
