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

import com.github.plateofpasta.edgestitch.permission.Permissible
import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.server.command.ServerCommandSource

/**
 * Removes a permission to the target player. Note, the permission must be associated with loaded
 * mod.
 */
class Remove : ModifyBase, Command<ServerCommandSource> {
  /**
   * Command execution logic.
   *
   * @param context Context for the command.
   * @return `0` if success, else `-1`.
   * @throws CommandSyntaxException Throws if an error occurred parsing the context.
   */
  @Throws(CommandSyntaxException::class)
  override fun run(context: CommandContext<ServerCommandSource>): Int {
    return this.runBase(
        context, ARG0_NAMESPACE, ARG1_NAMESPACE, Permissible::removePermission, NAMESPACE)
  }

  companion object {
    const val NAMESPACE: String = "remove"
    const val ARG0_NAMESPACE: String = "player"
    const val ARG1_NAMESPACE: String = "permission"
  }
}
