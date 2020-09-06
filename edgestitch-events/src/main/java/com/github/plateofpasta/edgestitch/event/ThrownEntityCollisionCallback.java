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

import com.github.plateofpasta.edgestitch.mixin.MixinThrownEntity;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;

/**
 * Fabric API event interface for when a {@link ThrownEntity} hits something.
 *
 * @see MixinThrownEntity
 */
@FunctionalInterface
public interface ThrownEntityCollisionCallback {
  Event<ThrownEntityCollisionCallback> EVENT =
      EventFactory.createArrayBacked(
          ThrownEntityCollisionCallback.class,
          (listeners) ->
              (thrownEntity, hitResult) -> {
                for (ThrownEntityCollisionCallback event : listeners) {
                  TypedActionResult<HitResult> result = event.onCollision(thrownEntity, hitResult);
                  if (result.getResult() != ActionResult.PASS) {
                    // Return modified hitResult.
                    return result;
                  }
                }
                // Return unmodified hitResult.
                return TypedActionResult.pass(hitResult);
              });

  /**
   * Callback for this interface.
   *
   * @param thrownEntity Entity being thrown.
   * @param hitResult Current projectile hit result.
   * @return PASS if the current hit result should be used (unmodified), CONSUME or SUCCESS if the
   *     modified HitResult should be used, or FAIL if the thrown entity should be marked for
   *     removal from the game world.
   */
  TypedActionResult<HitResult> onCollision(ThrownEntity thrownEntity, HitResult hitResult);
}
