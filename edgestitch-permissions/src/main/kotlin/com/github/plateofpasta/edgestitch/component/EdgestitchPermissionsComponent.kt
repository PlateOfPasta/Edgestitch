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

import com.github.plateofpasta.edgestitch.EdgestitchPermissions
import com.github.plateofpasta.edgestitch.permission.Permission
import com.github.plateofpasta.edgestitch.permission.PermissionMap
import drawer.getFrom
import drawer.put
import kotlinx.serialization.list
import nerdhub.cardinal.components.api.component.Component
import net.minecraft.nbt.CompoundTag

/** Cardinal component implementation for Edgestitch permissions. */
class EdgestitchPermissionsComponent : Component {
  var permissionMap = PermissionMap()

  /**
   * Deserialize from a compound NBT tag into this component's permissions.
   * @param compoundTag [CompoundTag] to serialize from.
   */
  override fun fromTag(compoundTag: CompoundTag) {
    this.permissionMap =
        PermissionMap(Permission.serializer().list.getFrom(compoundTag, PERMISSION_TAG_KEY))
  }

  /**
   * Serialize to a compound NBT tag.
   * @param compoundTag [CompoundTag] to serialize into.
   * @return [CompoundTag] filled with this component's permissions.
   */
  override fun toTag(compoundTag: CompoundTag): CompoundTag {
    Permission.serializer().list.put(permissionMap.all.toList(), compoundTag, PERMISSION_TAG_KEY)
    return compoundTag
  }

  companion object {
    private const val PERMISSION_TAG_KEY = EdgestitchPermissions.CONFIG_NAME_FIELD

    /**
     * Helper for getting a component from a provider using the static component registry.
     * @param provider Provider for this component type.
     */
    fun <T> get(provider: T): EdgestitchPermissionsComponent {
      return ComponentStaticRegistry.EDGESTITCH_COMPONENT.get(provider)
    }
  }
}
