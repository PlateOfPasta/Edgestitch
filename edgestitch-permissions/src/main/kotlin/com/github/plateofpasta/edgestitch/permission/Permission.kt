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

import kotlinx.serialization.Serializable

/** Content of a permission. */
@Serializable
data class Permission(
    var modID: String, var name: String, var description: String, var level: Int
) : Comparable<Permission?> {
  val qualifiedName: String
    /** @return Fully qualified name of this permission. */
    get() = java.lang.String.join(".", modID, name)

  /**
   * Deep copy of this object.
   *
   * @return New copy of this object.
   */
  fun copy(): Permission {
    // Deep copy:
    //   - Strings are immutable -> implicit deep copy.
    //   - Numeric primitives are implicitly deep copy.
    return Permission(modID, name, description, level)
  }

  /**
   * Implements [Comparable] for this object. Delegates to the comparable implementation for the
   * fully qualified name.
   *
   * @param other the object to be compared.
   * @return a negative integer, zero, or a positive integer as this object is less than, equal to,
   * or greater than the specified object.
   * @throws NullPointerException Throws if the specified object is null.
   */
  override fun compareTo(other: Permission?): Int {
    if (null == other) {
      throw NullPointerException("The specified object is null")
    }
    return qualifiedName.compareTo(other.qualifiedName)
  }

  /**
   * Clamps the level between 0-4. Negatives are clamped to 0 and anything over 4 is clamped to 4.
   *
   * @param level Level to clamp.
   * @return Clamped level.
   */
  private fun clampLevel(level: Int): Int {
    return if (4 < level) {
      4
    } else {
      Math.max(0, level)
    }
  }

  /** @return List of strings split around the qualifier delimiter. */
  private fun splitQualifiedName(qualifiedName: String): Array<String> {
    return qualifiedName.split("\\.".toRegex()).toTypedArray()
  }
}
