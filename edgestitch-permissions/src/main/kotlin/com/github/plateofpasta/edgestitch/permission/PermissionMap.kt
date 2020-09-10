package com.github.plateofpasta.edgestitch.permission

import java.util.*

open class PermissionMap {
  protected val permissions: MutableMap<String, Permission> = HashMap()
  protected val modPermissions: MutableMap<String, MutableSet<Permission>> = HashMap()

  /** Explicit default constructor that handles the constructor of internal collections. */
  constructor()

  /**
   * Collection constructor which initializes the internal collections from the parameter.
   * @param permissionList Collection of permissions to add to this
   */
  constructor(permissionList: Collection<Permission>) {
    for (permission in permissionList) {
      this.addInternal(permission)
    }
  }

  /**
   * Constructs a permission map from a [PermissionParser].
   * @param permissionParser Contains parsed permissions.
   */
  constructor(permissionParser: PermissionParser) {
    for ((_, permission) in permissionParser.getPermissions().entries) {
      this.addInternal(permission)
    }
  }

  /**
   * Checks if a permission is loaded.
   *
   * @param qualifiedName Fully qualified name of a permission.
   * @return `true` permission was loaded from some other Fabric mod at initialization, else
   * `false`.
   */
  operator fun contains(qualifiedName: String?): Boolean {
    return permissions.containsKey(qualifiedName)
  }

  /**
   * Adds (deep copies) a permissions to the internal collections by its qualified name.
   *
   * @param permission Permission to add.
   * @return `true` the permission was added, else `false`.
   */
  protected fun addInternal(permission: Permission): Boolean {
    val copiedPerm = permission.copy()
    permissions[permission.qualifiedName] = copiedPerm
    return modPermissions.computeIfAbsent(copiedPerm.modID) { TreeSet() }.add(copiedPerm)
  }

  /**
   * Gets a permission's contents by its qualified name.
   *
   * @param qualifiedName Fully qualified name of a permission.
   * @return [Permission] if the fully qualified name maps to it, else `null`.
   */
  operator fun get(qualifiedName: String?): Permission? {
    return permissions[qualifiedName]
  }

  /**
   * Gets all permissions.
   *
   * @return [Collection] of [Permission] if the mod ID has permissions, else `null`.
   */
  val all: Collection<Permission>
    get() = permissions.values

  /**
   * Gets all permissions associated with a mod ID.
   *
   * @param modID Mod ID of a loaded fabric mod.
   * @return [Collection] of [Permission] if the mod ID has permissions, else `null`.
   */
  fun getAllByMod(modID: String?): Collection<Permission>? {
    return modPermissions[modID]
  }
}
