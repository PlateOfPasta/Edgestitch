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

/** Interface for permission containing objects. */
interface Permissible {
  val permissions: Collection<Permission>

  /**
   * Checks if this object has a permission associated with it.
   *
   * @param qualifiedName Fully qualified name of the permission.
   * @return `true` if the permission exists and is enabled for this, else `false`.
   */
  fun hasPermission(qualifiedName: String): Boolean

  /**
   * Adds a permission associated with the qualified name from this.
   *
   * @param qualifiedName Fully qualified name of a permission from a loaded Fabric Mod.
   */
  fun addPermission(qualifiedName: String): Boolean

  /**
   * Removes a permission associated with the qualified name from this.
   *
   * @param qualifiedName Fully qualified name of a permission from a loaded Fabric Mod.
   */
  fun removePermission(qualifiedName: String): Boolean

  /**
   * Load this object's list of permissions with the given list. Typically used for datastore
   * operations.
   *
   * @param permissions List of permissions to load.
   */
  fun loadPermissions(permissions: List<Permission>)
}
