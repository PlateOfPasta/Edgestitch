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

package com.github.plateofpasta.edgestitch.mixin;

import com.github.plateofpasta.edgestitch.event.ServerWorldEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

/** Mixin for passing the name of the world when it is closed. */
@Mixin(ServerWorld.class)
public abstract class MixinServerWorld extends World {
  /**
   * Constructor required for the Mixin to compile, since the World class does not have a default
   * constructor.
   *
   * @param properties Ignored.
   * @param registryKey Ignored.
   * @param dimensionType Ignored.
   * @param supplier Ignored.
   * @param bl Ignored.
   * @param bl2 Ignored.
   * @param l Ignored.
   */
  protected MixinServerWorld(
      MutableWorldProperties properties,
      RegistryKey<World> registryKey,
      DimensionType dimensionType,
      Supplier<Profiler> supplier,
      boolean bl,
      boolean bl2,
      long l) {
    super(properties, registryKey, dimensionType, supplier, bl, bl2, l);
  }

  /**
   * Mixin hooked into the head of spawnEntity.
   *
   * @param entity Entity to be spawned into the world.
   * @param info Mixin info.
   */
  @Inject(method = "spawnEntity", at = @At("HEAD"), cancellable = true)
  public void spawnEntityMixin(Entity entity, CallbackInfoReturnable<Boolean> info) {
    TypedActionResult<Boolean> result =
        ServerWorldEvents.ENTITY_SPAWN.invoker().spawnEvent(this, entity);

    if (result.getResult() != ActionResult.PASS) {
      info.setReturnValue(result.getValue());
      info.cancel();
      return;
    }
  }

  /**
   * Mixin to check if a player can modify a position in the ServerWorld.
   *
   * @param player Player performing an action.
   * @param pos Position in the gameworld the player is performing the action.
   * @param info Mixin info.
   */
  @Inject(method = "canPlayerModifyAt", at = @At("HEAD"), cancellable = true)
  public void canPlayerModifyAtMixinClaimCheck(
      PlayerEntity player, BlockPos pos, CallbackInfoReturnable<Boolean> info) {
    ActionResult result = ServerWorldEvents.PLAYER_MODIFY.invoker().modifyAt(player, pos);
    if (result != ActionResult.PASS) {
      info.setReturnValue(false);
      info.cancel();
      return;
    }
  }
}
