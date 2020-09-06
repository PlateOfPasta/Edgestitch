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

package com.github.plateofpasta.edgestitch.component

import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactory
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer
import nerdhub.cardinal.components.api.ComponentRegistry
import nerdhub.cardinal.components.api.util.RespawnCopyStrategy
import net.minecraft.util.Identifier

/** Static registry for [EdgestitchPermissionsComponent] components. */
class ComponentStaticRegistry : EntityComponentInitializer {
  companion object {
    val EDGESTITCH_COMPONENT =
        ComponentRegistry.INSTANCE
            .registerStatic(
                Identifier("edgestitch:permissions"), EdgestitchPermissionsComponent::class.java)
  }

  /**
   * Called to register component factories for statically declared component types.
   *
   * @param registry an [EntityComponentFactoryRegistry] for statically declared components.
   */
  override fun registerEntityComponentFactories(registry: EntityComponentFactoryRegistry) {
    registry.registerForPlayers(
        EDGESTITCH_COMPONENT,
        EntityComponentFactory { EdgestitchPermissionsComponent() },
        RespawnCopyStrategy.ALWAYS_COPY)
  }
}
