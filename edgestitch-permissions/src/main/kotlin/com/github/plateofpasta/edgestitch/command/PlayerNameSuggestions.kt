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
