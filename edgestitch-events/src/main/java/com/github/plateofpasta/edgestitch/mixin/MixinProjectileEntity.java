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

import com.github.plateofpasta.edgestitch.event.ProjectileHitCallback;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Mixin for changing the behavior of projectile entities. */
@Mixin(ProjectileEntity.class)
public abstract class MixinProjectileEntity {

  /**
   * Mixin to modify the passed in parameter of onHit and thus modify the behavior of the method.
   *
   * @param hitResult Parameter of onHit to modify.
   * @return Possibly modified HitResult.
   */
  @ModifyVariable(
      method = "onCollision(Lnet/minecraft/util/hit/HitResult;)V",
      at = @At("HEAD"),
      name = "hitResult")
  private HitResult onHitModifyHitResult(HitResult hitResult) {
    TypedActionResult<HitResult> result =
        ProjectileHitCallback.EVENT.invoker().onHit((ProjectileEntity) (Object) this, hitResult);
    if (ActionResult.PASS != result.getResult()) {
      return result.getValue();
    } else {
      return hitResult;
    }
  }
}
