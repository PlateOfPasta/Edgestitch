package com.github.plateofpasta.edgestitch.mixin;

import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Mixin for accessing protected members of {@link PlayerManager}. */
@Mixin(PlayerManager.class)
public interface MixinAccessorPlayerManager {
  @Invoker("savePlayerData")
  void invokeSavePlayerData(ServerPlayerEntity player);
}
