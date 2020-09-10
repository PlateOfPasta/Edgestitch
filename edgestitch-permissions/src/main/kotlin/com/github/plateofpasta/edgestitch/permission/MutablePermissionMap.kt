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

package com.github.plateofpasta.edgestitch.permission

import java.util.*

/** Class for handling collections of [Permission]. */
class MutablePermissionMap : PermissionMap {

  /** Explicit default constructor that handles the constructor of internal collections. */
  constructor() : super()

  /**
   * Collection constructor which initializes the internal collections from the parameter.
   * @param permissionList Collection of permissions to add to this
   */
  constructor(permissionList: Collection<Permission>) : super(permissionList)

  /**
   * Constructs a permission map from a [PermissionParser].
   * @param permissionParser Contains parsed permissions.
   */
  constructor(permissionParser: PermissionParser) : super(permissionParser)

  /**
   * Adds (deep copies) a permissions to the internal collections by its qualified name.
   *
   * @param permission Permission to add.
   * @return `true` the permission was added, else `false`.
   */
  fun add(permission: Permission): Boolean {
    return this.addInternal(permission)
  }

  /**
   * Removes a permission by its qualified name.
   *
   * @param qualifiedName Fully qualified name of a permission.
   * @return `true` the permission was removed, else `false`.
   */
  fun remove(qualifiedName: String?): Boolean {
    val perm = permissions.remove(qualifiedName) ?: return false
    val modPermissionSet = modPermissions[perm.modID] ?: return false
    return modPermissionSet.remove(perm)
  }

  /**
   * Add permissions by its qualified name.
   *
   * @param modID Mod ID of a loaded fabric mod.
   * @param permissions Permissions to add to this map.
   */
  fun addAllByMod(modID: String, permissions: Collection<Permission>) {
    modPermissions.computeIfAbsent(modID) { TreeSet() }.addAll(permissions)
  }
}
