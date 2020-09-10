package com.github.plateofpasta.edgestitch.player

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
