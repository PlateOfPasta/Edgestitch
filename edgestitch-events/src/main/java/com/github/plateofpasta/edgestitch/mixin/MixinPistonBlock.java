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

import com.github.plateofpasta.edgestitch.event.PistonEvents;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PistonBlock.class)
public class MixinPistonBlock {

  /**
   * @param world World the piston block is in.
   * @param pistonPos Block position of the piston block.
   * @param facingDir Direction the piston is facing.
   * @param isRetracted Whether the piston is retracted (always true for extend events).
   * @param info Callback info.
   * @param toPos Where the piston head should be pushing to.
   * @param pistonHandler Captured local variable that contains the results of calculatePush.
   */
  @Inject(
      method = "move",
      at =
          @At(
              value = "INVOKE_ASSIGN",
              target = "Lnet/minecraft/block/piston/PistonHandler;calculatePush()Z",
              ordinal = 0),
      locals = LocalCapture.CAPTURE_FAILHARD,
      cancellable = true)
  private void handleCalculatePush(
      World world,
      BlockPos pistonPos,
      Direction facingDir,
      boolean isRetracted,
      CallbackInfoReturnable<Boolean> info,
      BlockPos toPos,
      PistonHandler pistonHandler) {
    final ActionResult result;
    if (isRetracted) {
      result =
          PistonEvents.PISTON_EXTEND.invoker().extend(world, pistonPos, facingDir, pistonHandler);
    } else {
      // @TODO Fix this so the client is synchronized with a cancelled retract event.
      result =
          PistonEvents.PISTON_RETRACT.invoker().retract(world, pistonPos, facingDir, pistonHandler);
    }

    if (ActionResult.PASS != result) {
      info.setReturnValue(false);
      info.cancel();
    }
  }
}
