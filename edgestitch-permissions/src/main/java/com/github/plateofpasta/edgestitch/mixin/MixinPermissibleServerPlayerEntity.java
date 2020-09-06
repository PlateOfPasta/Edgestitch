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

import com.github.plateofpasta.edgestitch.EdgestitchPermissions;
import com.github.plateofpasta.edgestitch.component.EdgestitchPermissionsComponent;
import com.github.plateofpasta.edgestitch.permission.Permissible;
import com.github.plateofpasta.edgestitch.permission.Permission;
import com.github.plateofpasta.edgestitch.permission.PermissionMap;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Collection;
import java.util.List;

/** Implements [Permissible] for a [ServerPlayerEntity]. */
@Mixin(ServerPlayerEntity.class)
public abstract class MixinPermissibleServerPlayerEntity implements Permissible {

  /** @return Permission map of this entity. */
  public PermissionMap getPermissionMap() {
    return EdgestitchPermissionsComponent.Companion.get(this).getPermissionMap();
  }

  /** @return Collection of all permissions for this entity. */
  public @NotNull Collection<Permission> getPermissions() {
    return getPermissionMap().getAll();
  }

  /**
   * Checks if this object has a permission associated with it.
   *
   * @param qualifiedName Fully qualified name of the permission.
   * @return `true` if the permission exists and is enabled for this, else `false`.
   */
  @Override
  public boolean hasPermission(@NotNull String qualifiedName) {
    return getPermissionMap().contains(qualifiedName);
  }

  /**
   * Adds a permission associated with the qualified name from this.
   *
   * @param qualifiedName Fully qualified name of a permission from a loaded Fabric Mod.
   * @return `true` if permission was successfully added.
   */
  public boolean addPermission(@NotNull String qualifiedName) {
    Permission permission = EdgestitchPermissions.LOADED_PERMISSIONS.get(qualifiedName);
    if (null == permission) {
      return false;
    }
    return getPermissionMap().add(permission);
  }

  /**
   * Removes a permission associated with the qualified name from this.
   *
   * @param qualifiedName Fully qualified name of a permission from a loaded Fabric Mod.
   * @return `true` if permission was successfully added.
   */
  @Override
  public boolean removePermission(@NotNull String qualifiedName) {
    return getPermissionMap().remove(qualifiedName);
  }

  /**
   * Load this object's list of permissions with the given list. Typically used for datastore
   * operations.
   *
   * @param permissions List of permissions to load.
   */
  @Override
  public void loadPermissions(List<Permission> permissions) {
    for (Permission perm : permissions) {
      getPermissionMap().add(perm);
    }
  }
}
