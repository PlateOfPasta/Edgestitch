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

package com.github.plateofpasta.edgestitch

import com.github.plateofpasta.edgestitch.command.initCommands
import com.github.plateofpasta.edgestitch.permission.MutablePermissionMap
import com.github.plateofpasta.edgestitch.permission.PermissionMap
import com.github.plateofpasta.edgestitch.permission.PermissionParser
import net.fabricmc.loader.api.FabricLoader

/** Implements Fabric dedicated server mod initializer. */
class EdgestitchPermissions {

  /** Loads all mod permissions from their configs. */
  fun init() {
    loadModPermissions()
    initCommands()
  }

  companion object {
    /**
     * Name of the top level object within the fabric.mod.json "custom" object that contains a mod's
     * Edgestitch permissions.
     */
    const val CONFIG_NAME_FIELD = "edgestitch-permissions"
    lateinit var LOADED_PERMISSIONS: PermissionMap

    /** Loads permissions from all loaded Fabric mods. */
    private fun loadModPermissions() {
      val permissionParser = PermissionParser()
      for (container in FabricLoader.getInstance().allMods) {
        val metadata = container.metadata
        if (metadata.containsCustomValue(CONFIG_NAME_FIELD)) {
          permissionParser.parse(metadata.id, metadata.getCustomValue(CONFIG_NAME_FIELD).asObject)
        }
      }
      LOADED_PERMISSIONS = PermissionMap(permissionParser)
    }

    /** Performs the loading procedure again. */
    fun reloadModPermissions() {
      loadModPermissions()
    }
  }
}
