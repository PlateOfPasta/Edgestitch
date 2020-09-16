package com.github.plateofpasta.edgestitch.mixin;

import com.github.plateofpasta.edgestitch.event.HopperInsertCallback;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HopperBlockEntity.class)
public abstract class MixinHopperBlockEntity {

  /**
   * Mixin that injects into the beginning of insert. This could happen later at the point where
   * HopperBlockEntity#transfer occurs, but this is deemed unnecessary.
   *
   * @param info Callback info.
   */
  @Inject(method = "insert", at = @At(value = "INVOKE"), cancellable = true)
  private void onInsert(CallbackInfoReturnable<Boolean> info) {
    HopperBlockEntity thisHopper = (HopperBlockEntity) (Object) this;
    BlockPos targetPosition =
        thisHopper.getPos().offset(thisHopper.getCachedState().get(HopperBlock.FACING));
    ActionResult result = HopperInsertCallback.EVENT.invoker().onInsert(thisHopper, targetPosition);
    if (ActionResult.PASS != result) {
      info.setReturnValue(false);
      info.cancel();
    }
  }
}
