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

import at.co.hohl.myresidence.storage.persistent.Residence;

import java.util.List;

/**
 * Listener for invalid residences.
 *
 * @author Michael Hohl
 */
public interface InvalidResidenceListener {
  /**
   * Called when invalid residences are found.
   *
   * @param invalidResidences the found invalid residences.
   */
  void invalidResidencesFound(final List<Residence> invalidResidences);
}
