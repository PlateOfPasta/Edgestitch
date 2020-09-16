package com.github.plateofpasta.edgestitch.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

/**
 * Callback event for when a hopper block tries to insert items into the target inventory. This
 * happens early in the insertion logic, so users are not provided references to specific items
 * being transferred.
 */
@FunctionalInterface
public interface HopperInsertCallback {
  Event<HopperInsertCallback> EVENT =
      EventFactory.createArrayBacked(
          HopperInsertCallback.class,
          (hopperBlockEntity, insertPosition) -> ActionResult.PASS,
          (listeners) ->
              (hopperBlockEntity, insertPosition) -> {
                for (HopperInsertCallback event : listeners) {
                  ActionResult result = event.onInsert(hopperBlockEntity, insertPosition);
                  if (result != ActionResult.PASS) {
                    return result;
                  }
                }
                return ActionResult.PASS;
              });

  /**
   * Callback for this interface.
   *
   * @param hopperBlockEntity Hopper block that is performing the insertion operation.
   * @param insertPosition Position in the world that the hopper is inserting into.
   * @return PASS if the current hit result should be used, else FAIL if a new hit result should be
   *     injected.
   */
  ActionResult onInsert(HopperBlockEntity hopperBlockEntity, BlockPos insertPosition);
}
