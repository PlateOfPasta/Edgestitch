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

import com.github.plateofpasta.edgestitch.event.ServerPlayerEvents;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Mixin for ServerPlayerEntity. */
@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity {
  /**
   * Mixin for when the ServerPlayerEntity drops an item. todo Figure out if this just prevents the
   * item entity from spawning or completely prevents the drop.
   *
   * @param stack
   * @param bl Unnamed boolean passed to super PlayerEntity#onDrop. Related to calculating
   *     ItemEntity velocity. Ignored.
   * @param bl2 Unnamed boolean pass to super PlayerEntity#onDrop. Related to getting the UUID of
   *     the thrower entity. Ignored.
   * @param info Callback info.
   */
  @Inject(method = "dropItem", at = @At("HEAD"), cancellable = true)
  public void onDropBefore(
      ItemStack stack, boolean bl, boolean bl2, CallbackInfoReturnable<ItemEntity> info) {
    TypedActionResult<ItemEntity> result =
        ServerPlayerEvents.PLAYER_DROP_ITEM
            .invoker()
            .drop((ServerPlayerEntity) (Object) (this), stack);
    if (result.getResult() != ActionResult.PASS) {
      // Return a null ItemEntity.
      info.setReturnValue(result.getValue());
      info.cancel();
      return;
    }
  }
}
