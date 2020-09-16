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

package com.github.plateofpasta.edgestitch.event;

import com.github.plateofpasta.edgestitch.mixin.MixinBaseFluid;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/**
 * Fabric API event interface for when a {@link net.minecraft.fluid.FlowableFluid} is flowing into
 * another block.
 *
 * @see MixinBaseFluid
 */
@FunctionalInterface
public interface FluidFlowCallback {
  Event<FluidFlowCallback> EVENT =
      EventFactory.createArrayBacked(
          FluidFlowCallback.class,
          (world, blockPos, blockState, direction, fluidState) -> ActionResult.PASS,
          (listeners) ->
              (world, blockPos, blockState, direction, fluidState) -> {
                for (FluidFlowCallback event : listeners) {
                  ActionResult result =
                      event.flow(world, blockPos, blockState, direction, fluidState);

                  if (result != ActionResult.PASS) {
                    return result;
                  }
                }
                return ActionResult.PASS;
              });

  /**
   * Callback for this interface.
   *
   * @param world World in which the fluid flow event is occurring.
   * @param blockPos Block position of the world of the block being flowed into.
   * @param blockState State of the block being flowed into.
   * @param direction Direction of fluid flow.
   * @param fluidState State of the flowing fluid.
   * @return PASS if the fluid should be allowed to flow normally, else FAIL if the fluid flow
   *     should not occur.
   */
  ActionResult flow(
      World world,
      BlockPos blockPos,
      BlockState blockState,
      Direction direction,
      FluidState fluidState);
}
