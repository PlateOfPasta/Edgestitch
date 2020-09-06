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

import com.github.plateofpasta.edgestitch.event.FluidFlowCallback;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Mixin to change the behavior of fluid flow. */
@Mixin(FlowableFluid.class)
public abstract class MixinBaseFluid {

  /**
   * Mixin to change the behavior of fluid flow.
   *
   * @param world World fluid is in.
   * @param pos Position being flowed into.
   * @param state Block state being flowed into.
   * @param direction Direction of flow.
   * @param fluidState State of the fluid.
   * @param info Mixin callback info.
   */
  @Inject(method = "flow", at = @At("HEAD"), cancellable = true)
  private void flowDetectionMixin(
      WorldAccess world,
      BlockPos pos,
      BlockState state,
      Direction direction,
      FluidState fluidState,
      CallbackInfo info) {
    // Pass automatically if world is not a World (i.e. ChunkRegion).
    if (!(world instanceof World)) {
      return;
    }
    ActionResult result =
        FluidFlowCallback.EVENT.invoker().flow((World) world, pos, state, direction, fluidState);
    if (ActionResult.PASS != result) {
      info.cancel();
    }
  }
}
