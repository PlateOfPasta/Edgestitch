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
import com.mojang.brigadier.exceptions.CommandSyntaxException
import java.util.function.Predicate
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity

/** Predicate for chunk claim admin commands. Player must be an admin to execute these commands. */
class EdgestitchOpPredicate : Predicate<ServerCommandSource?> {
  /**
   * Evaluates this predicate on the given argument.
   *
   * @param source The input argument.
   * @return `true` if the input argument matches the predicate, otherwise `false`
   */
  override fun test(source: ServerCommandSource?): Boolean {
    if (null == source) {
      return false
    }
    var player: ServerPlayerEntity? = null
    var isPlayer: Boolean
    try {
      player = source.player
      isPlayer = true
    } catch (e: CommandSyntaxException) {
      isPlayer = false
    }
    return if (isPlayer) {
      (player as Permissible).hasPermission("edgestitch.operator")
    } else {
      source.hasPermissionLevel(4)
    }
  }
}
