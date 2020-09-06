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

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PistonEvents {
  public static final Event<PistonExtendCallback> PISTON_EXTEND =
      EventFactory.createArrayBacked(
          PistonExtendCallback.class,
          (listeners) ->
              (world, pistonPos, facingDir, pistonHandler) -> {
                for (PistonExtendCallback event : listeners) {
                  ActionResult result = event.extend(world, pistonPos, facingDir, pistonHandler);
                  if (result != ActionResult.PASS) {
                    return result;
                  }
                }
                return ActionResult.PASS;
              });

  public static final Event<PistonRetract> PISTON_RETRACT =
      EventFactory.createArrayBacked(
          PistonRetract.class,
          (listeners) ->
              (world, pistonPos, facingDir, pistonHandler) -> {
                for (PistonRetract event : listeners) {
                  ActionResult result = event.retract(world, pistonPos, facingDir, pistonHandler);
                  if (result != ActionResult.PASS) {
                    return result;
                  }
                }
                return ActionResult.PASS;
              });

  /** Prevent instantiations of this class. */
  private PistonEvents() {}

  /** Event handler for when a piston extends. */
  @FunctionalInterface
  public interface PistonExtendCallback {
    /**
     * Callback for this interface.
     *
     * @param world World in which the piston exists.
     * @param pistonPos Block position of the piston body.
     * @param facingDir Direction the piston is facing.
     * @param pistonHandler Handler used to calculate the list of blocks that may be moved.
     * @return PASS if the piston extension should proceed as normal, else FAIL if the piston
     *     movement should be canceled.
     */
    ActionResult extend(
        World world, BlockPos pistonPos, Direction facingDir, PistonHandler pistonHandler);
  }

  /** Event handler for when a piston extends. */
  public interface PistonRetract {
    /**
     * Callback for this interface.
     *
     * @param world World in which the piston exists.
     * @param pistonPos Block position of the piston body.
     * @param facingDir Direction the piston is facing.
     * @param pistonHandler Handler used to calculate the list of blocks that may be moved.
     * @return PASS if the piston extension should proceed as normal, else FAIL if the piston
     *     movement should be canceled.
     */
    ActionResult retract(
        World world, BlockPos pistonPos, Direction facingDir, PistonHandler pistonHandler);
  }
}
