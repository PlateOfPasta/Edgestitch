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

package com.github.plateofpasta.edgestitch.player

import com.github.plateofpasta.edgestitch.world.EdgestitchLocation
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity

/**
 * Creates a [ServerPlayerEntity] from a player name with all of the NBT player data loaded. Works
 * for online players or offline players that have connected to the server at some point (i.e. their
 * data is stored in the server data files).
 * @param server Server to load the player data from.
 * @param playerName Player name to resolve.
 * @return [ServerPlayerEntity] with the player's NBT data loaded or `null` if the player name was
 * unresolvable.
 */
@Environment(EnvType.SERVER)
fun resolvePlayer(server: MinecraftServer, playerName: String): ServerPlayerEntity? {
  val playerManager = server.playerManager
  var player = playerManager.getPlayer(playerName)
  // Player is online.
  if (null != player) {
    return player
  }
  // Player is offline, try to load data from game data files.
  val profile = server.userCache.findByName(playerName) ?: return null
  // creatPlayer will disconnect the player if they are currently online.
  player = playerManager.createPlayer(profile) ?: return null
  val tag = playerManager.loadPlayerData(player) ?: return null
  return player
}
