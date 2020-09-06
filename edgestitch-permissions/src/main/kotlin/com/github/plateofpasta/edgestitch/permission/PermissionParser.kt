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
import net.fabricmc.loader.api.metadata.CustomValue
import net.fabricmc.loader.api.metadata.CustomValue.CvObject

/** Handles parsing permissions from Fabric mod loader API's [CustomValue]. */
class PermissionParser {
  private val permissions: MutableMap<String, Permission> = TreeMap()

  /**
   * Gets the map of parsed permissions.
   *
   * @return Parsed permissions.
   */
  internal fun getPermissions(): Map<String, Permission> {
    return permissions
  }

  /**
   * Parses a mod's permissions from its JSON config.
   *
   * @param modID Mod ID being parsed.
   * @param permissionList Extracted from the FAPI config "custom" field.
   */
  fun parse(modID: String, permissionList: CvObject) {
    for ((key, value1) in permissionList) {
      parsePermission(modID, key, value1.asObject)
    }
  }

  /**
   * Handler for parsing a specific permission from the permission list.
   *
   * @param modID Mod ID being parsed.
   * @param permissionName Name of the permission.
   * @param permissionContents Contents of the permission (JSON object).
   */
  private fun parsePermission(
      modID: String, permissionName: String?, permissionContents: CvObject
  ) {
    if (null == permissionName || permissionName.isEmpty()) {
      return
    }
    if (permissionContents.containsKey(SUBFIELD_1_NAME) &&
        permissionContents.containsKey(SUBFIELD_2_NAME)) {
      val perm =
          Permission(
              modID,
              permissionName,
              permissionContents[SUBFIELD_1_NAME].asString,
              permissionContents[SUBFIELD_2_NAME].asNumber.toInt())
      permissions[perm.qualifiedName] = perm
    }
  }

  companion object {
    private const val SUBFIELD_1_NAME = "description"
    private const val SUBFIELD_2_NAME = "default"
  }
}
