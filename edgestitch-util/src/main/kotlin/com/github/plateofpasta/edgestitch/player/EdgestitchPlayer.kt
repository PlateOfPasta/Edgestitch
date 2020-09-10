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
import com.github.plateofpasta.edgestitch.world.EdgestitchWorld
import io.netty.buffer.Unpooled
import java.io.IOException
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket
import net.minecraft.text.LiteralText
import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.util.Formatting
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

/** Abstracts Minecraft primitive [PlayerEntity]. */
open class EdgestitchPlayer(protected val player: PlayerEntity) {
  val name: String
    get() = player.name.asString()
  val world: EdgestitchWorld
    get() = EdgestitchWorld(player.entityWorld)
  val location: EdgestitchLocation
    get() = EdgestitchLocation(player.entityWorld, player.blockPos)
  val isInsideVehicle: Boolean
    get() = player.hasVehicle()

  /**
   * Sends the player a message.
   *
   * @param message [MutableText] to send the player.
   */
  fun sendMessage(message: MutableText) {
    val style = Style.EMPTY.withColor(Formatting.YELLOW)
    player.sendMessage(message.setStyle(style), false)
  }

  /**
   * Sends the player a message. Delegates to the [MutableText] version.
   *
   * @param message String to send the player.
   */
  fun sendMessage(message: String?) {
    this.sendMessage(LiteralText(message))
  }

  /**
   * Variadic version that delegates to the appropriate sendMessage for each message.
   *
   * @param messages Variadic argument of messages.
   */
  fun sendMessages(vararg messages: String?) {
    for (message in messages) {
      this.sendMessage(message)
    }
  }

  /**
   * Variadic version that delegates to the appropriate sendMessage for each message.
   *
   * @param messages Variadic argument of messages.
   */
  fun sendMessages(vararg messages: MutableText) {
    for (message in messages) {
      this.sendMessage(message)
    }
  }

  /**
   * Teleports the player to the location.
   *
   * @param location Location to teleport to.
   */
  fun teleport(location: EdgestitchLocation) {
    player.teleport(location.x.toDouble(), location.y.toDouble(), location.z.toDouble())
  }

  /**
   * Sends a fake block at the 3D coordinate to be visualized on the client.
   *
   * @param block What the fake block should look like.
   * @param coord3D Position in the player's current world to change.
   */
  fun sendFakeBlock(block: EdgestitchBlock, coord3D: Vec3d?) {
    // Construct the packet with a buffer so we can specific the block state at the location.
    val packet = BlockUpdateS2CPacket()
    val packetBuf = PacketByteBuf(Unpooled.buffer())
    packetBuf.writeBlockPos(BlockPos(coord3D))
    packetBuf.writeVarInt(block.blockStateVarInt)
    try {
      // "Fill" the packet with our info.
      packet.read(packetBuf)
    } catch (e: IOException) {
      return
    }
    // Fabric network API.
    ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, packet)
  }
}
