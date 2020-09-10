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
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.LiteralText

/** Interface for Add/Remove permission commands. */
interface ModifyBase {
  /**
   * Command execution logic.
   *
   * @param context Context for the command.
   * @param arg0_namespace Name for parsing the first argument.
   * @param arg1_namespace Name for parsing the second argument.
   * @param modifyMethod Operation to perform on the permission.
   * @param operationName Name of the operation being performed.
   * @return `0` if success, else `-1`.
   * @throws CommandSyntaxException Throws if an error occurred parsing the context.
   */
  @Throws(CommandSyntaxException::class)
  fun runBase(
      context: CommandContext<ServerCommandSource>,
      arg0_namespace: String,
      arg1_namespace: String,
      modifyMethod: Permissible.(String) -> Boolean,
      operationName: String
  ): Int {
    val source = context.source
    val targetPlayer = EntityArgumentType.getPlayer(context, arg0_namespace)
    val qualifiedName = context.getArgument(arg1_namespace, String::class.java)
    if (null == targetPlayer) {
      source.sendError(LiteralText("Specified player could not be resolved."))
      return -1
    }
    // Check if command is loaded.
    if (!EdgestitchPermissions.LOADED_PERMISSIONS.contains(qualifiedName)) {
      notFoundMessage(source)
      return -1
    }
    // Player saying the command must have the appropriate operator level to modify the permission
    // of the target.
    val permission = EdgestitchPermissions.LOADED_PERMISSIONS[qualifiedName]

    if (!source.hasPermissionLevel(permission!!.level)) {
      source.sendError(LiteralText("You do not have the correct op level to perform that action."))
      return -1
    }

    if (qualifiedName.isEmpty()) {
      source.sendError(LiteralText("No permission was provided."))
    }
    if ((targetPlayer as Permissible).modifyMethod(qualifiedName)) {
      source.sendFeedback(
          LiteralText(String.format("Modified permission %s", qualifiedName)), false)
    } else {
      source.sendError(
          LiteralText(String.format("Cannot %s the permission more than once", operationName)))
      return -1
    }
    return 0
  }
}
