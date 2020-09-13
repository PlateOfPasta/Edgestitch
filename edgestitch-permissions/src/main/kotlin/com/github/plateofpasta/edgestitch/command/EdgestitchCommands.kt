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

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.LiteralText

const val EDGESTITCH_COMMANDS_NAMESPACE = "edgestitch"

/** Handles initializing and registering Edgestitch permissions. */
fun initCommands() {
  // Build commands.
  val builder =
      CommandManager.literal(EDGESTITCH_COMMANDS_NAMESPACE)
          .requires(EdgestitchOpPredicate())
          // Add command.
          .then(
              CommandManager.literal(Add.NAMESPACE)
                  .then(
                      CommandManager.argument(Add.ARG0_NAMESPACE, StringArgumentType.string())
                          .suggests(PlayerNameSuggestions())
                          .then(
                              CommandManager.argument(
                                      Add.ARG1_NAMESPACE, StringArgumentType.string())
                                  .suggests(PermissionSuggestions())
                                  .executes(Add()))))
          // Remove command.
          .then(
              CommandManager.literal(Remove.NAMESPACE)
                  .then(
                      CommandManager.argument(Remove.ARG0_NAMESPACE, StringArgumentType.string())
                          .suggests(PlayerNameSuggestions())
                          .then(
                              CommandManager.argument(
                                      Remove.ARG1_NAMESPACE, StringArgumentType.string())
                                  .suggests(PermissionSuggestions())
                                  .executes(Remove()))))
          // List command.
          .then(
              CommandManager.literal(ListCommand.NAMESPACE)
                  .then(
                      CommandManager.argument(
                              ListCommand.ARG0_NAMESPACE, EntityArgumentType.player())
                          .executes(ListCommand())))
          // Describe command.
          .then(
              CommandManager.literal(Describe.NAMESPACE)
                  .then(
                      CommandManager.argument(Describe.ARG0_NAMESPACE, StringArgumentType.string())
                          .suggests(PermissionSuggestions())
                          .executes(Describe())))

  // Register commands.
  CommandRegistrationCallback.EVENT
      .register(
          CommandRegistrationCallback {
          dispatcher: CommandDispatcher<ServerCommandSource?>,
          dedicated: Boolean ->
            if (dedicated) {
              dispatcher.register(builder)
            }
          })
}

/**
 * Helper function for sending a common command error message.
 * @param source Source to send the message to.
 */
internal fun notFoundMessage(source: ServerCommandSource) {
  source.sendError(LiteralText("Given permission was not found."))
}
