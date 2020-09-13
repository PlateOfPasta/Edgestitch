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

import com.google.common.collect.Lists
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import java.util.concurrent.CompletableFuture
import net.minecraft.server.command.CommandSource
import net.minecraft.server.command.ServerCommandSource

/**
 * Suggester class for suggesting online player names. This is built-in to the
 * [net.minecraft.command.argument.EntityArgumentType.player] argument type, but it is also tied to
 * this argument type. Therefore, this is useful if you want a player name as a string argument, not
 * an entity argument.
 */
class PlayerNameSuggestions : SuggestionProvider<ServerCommandSource?> {
  /**
   * Suggest implementation.
   *
   * @param context Context of the command.
   * @param builder Suggestion builder for forming suggestions.
   * @return Suggestion future.
   */
  override fun getSuggestions(
      context: CommandContext<ServerCommandSource?>?, builder: SuggestionsBuilder?
  ): CompletableFuture<Suggestions> {
    return CommandSource.suggestMatching(context?.source?.playerNames, builder)
  }
}
