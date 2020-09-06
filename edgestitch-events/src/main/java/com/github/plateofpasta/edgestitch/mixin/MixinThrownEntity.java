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

import com.github.plateofpasta.edgestitch.event.ThrownEntityCollisionCallback;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Mixin to modify the collision behavior of {@link ThrownEntity} objects. */
@Mixin(ThrownEntity.class)
public abstract class MixinThrownEntity {

  /**
   * Redirect the only invocation of onCollision for thrown entities.
   *
   * @param thrownEntity Thrown entity invoking onCollision.
   * @param hitResult Parameter of onHit to modify.
   */
  @Redirect(
      method = "tick",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/entity/projectile/thrown/ThrownEntity;onCollision(Lnet/minecraft/util/hit/HitResult;)V"))
  private void onHitModifyHitResult(ThrownEntity thrownEntity, HitResult hitResult) {
    TypedActionResult<HitResult> result =
        ThrownEntityCollisionCallback.EVENT.invoker().onCollision(thrownEntity, hitResult);
    switch (result.getResult()) {
      case PASS:
        {
          // Invoke onCollision with without modifications.
          ((MixinAccessorProjectileEntity) thrownEntity).invokeOnCollision(hitResult);
          break;
        }
      case CONSUME:
      case SUCCESS:
        {
          // Invoke onCollision with modified hitResult.
          ((MixinAccessorProjectileEntity) thrownEntity).invokeOnCollision(result.getValue());
          break;
        }
      case FAIL:
        {
          // Thrown entity is marked for removal from the game.
          thrownEntity.remove();
          break;
        }
    }
  }
}
