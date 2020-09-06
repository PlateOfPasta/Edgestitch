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

package com.github.plateofpasta.edgestitch.command

import com.github.plateofpasta.edgestitch.EdgestitchPermissions
import com.github.plateofpasta.edgestitch.permission.Permissible
import com.github.plateofpasta.edgestitch.permission.Permission
import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import java.util.stream.Collectors
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.LiteralText

/**
 * Lists all of the permissions that are for currently loaded mods. Note, a player can have more
 * permissions stored for mods that are not loaded.
 */
class ListCommand : Command<ServerCommandSource> {
  /**
   * Command execution logic.
   *
   * @param context Context for the command.
   * @return `0` if success, else `-1`.
   * @throws CommandSyntaxException Throws if an error occurred parsing the context.
   */
  @Throws(CommandSyntaxException::class)
  override fun run(context: CommandContext<ServerCommandSource>): Int {
    val source = context.source
    val targetPlayer = EntityArgumentType.getPlayer(context, ARG0_NAMESPACE)
    if (null == targetPlayer) {
      source.sendError(LiteralText("Specified player could not be resolved."))
      return -1
    }
    val permissions = (targetPlayer as Permissible).permissions
    source.sendFeedback(
        LiteralText(
            String.format(
                "Player %s has the following permissions:", targetPlayer.displayName.asString())),
        false)
    if (permissions.isEmpty()) {
      source.sendError(LiteralText("No permissions were found"))
    } else {
      source.sendFeedback(
          LiteralText(
              permissions.stream()
                  .map(Permission::qualifiedName)
                  .filter(EdgestitchPermissions.LOADED_PERMISSIONS::contains)
                  .collect(Collectors.joining("\n"))),
          false)
    }
    return 0
  }

  companion object {
    const val NAMESPACE = "list"
    const val ARG0_NAMESPACE = "player"
  }
}
