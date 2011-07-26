/*
 * MyResidence, Bukkit plugin for managing your towns and residences
 * Copyright (C) 2011, Michael Hohl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.co.hohl.myresidence;

import java.util.List;

/**
 * Represents a manager for the flags of towns and residences.
 *
 * @author Michael Hohl
 */
public interface FlagManager<T> {
  /**
   * Checks if residence or town has has the flag set.
   *
   * @param flag flag to check.
   * @return true, if the flag is set.
   */
  boolean hasFlag(T flag);

  /**
   * Returns all flags of a town or residence.
   *
   * @return the flags set.
   */
  List<T> getFlags();

  /**
   * Sets the passed flag.
   *
   * @param flag the flag to set.
   */
  void setFlag(T flag);

  /**
   * Remove the passed flag.
   *
   * @param flag the flag to remove.
   */
  void removeFlag(T flag);
}
