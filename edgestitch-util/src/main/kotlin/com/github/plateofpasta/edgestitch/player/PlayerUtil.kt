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

import com.github.plateofpasta.edgestitch.block.EdgestitchBlock
import com.github.plateofpasta.edgestitch.world.EdgestitchLocation
import io.netty.buffer.Unpooled
import java.io.IOException
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.LiteralText
import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.util.Formatting
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

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

/**
 * Sends the player a message.
 *
 * @param message [MutableText] to send the player.
 */
fun ServerPlayerEntity.sendMessage(message: MutableText) {
  val style = Style.EMPTY.withColor(Formatting.YELLOW)
  sendMessage(message.setStyle(style), false)
}

/**
 * Sends the player a message. Delegates to the [MutableText] version.
 *
 * @param message String to send the player.
 */
fun ServerPlayerEntity.sendMessage(message: String) {
  sendMessage(LiteralText(message))
}

/**
 * Variadic version that delegates to the appropriate sendMessage for each message.
 *
 * @param messages Variadic argument of messages.
 */
fun ServerPlayerEntity.sendMessages(vararg messages: String) {
  for (message in messages) {
    sendMessage(message)
  }
}

/**
 * Variadic version that delegates to the appropriate sendMessage for each message.
 *
 * @param messages Variadic argument of messages.
 */
fun ServerPlayerEntity.endMessages(vararg messages: MutableText) {
  for (message in messages) {
    sendMessage(message)
  }
}

/**
 * Sends a fake block at the 3D coordinate to be visualized on the client.
 *
 * @param block What the fake block should look like.
 * @param coord3D Position in the player's current world to change.
 */
@Environment(EnvType.SERVER)
fun ServerPlayerEntity.sendFakeBlock(block: BlockState, coord3D: Vec3d?) {
  // Construct the packet with a buffer so we can specific the block state at the location.
  val packet = BlockUpdateS2CPacket()
  val packetBuf = PacketByteBuf(Unpooled.buffer())
  packetBuf.writeBlockPos(BlockPos(coord3D))
  packetBuf.writeVarInt(Block.getRawIdFromState(block))
  try {
    // "Fill" the packet with our info.
    packet.read(packetBuf)
  } catch (e: IOException) {
    return
  }
  // Fabric network API.
  ServerSidePacketRegistry.INSTANCE.sendToPlayer(this, packet)
}
