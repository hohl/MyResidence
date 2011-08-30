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

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Used to retrieve information about what a player is allowed to do and what not.
 *
 * @author Michael Hohl
 */
public interface PermissionsResolver {

  /**
   * Checks if the session is a session of an Administrator.
   *
   * @param player player to check the permissions for.
   * @return true, if the player owns the administrator permission.
   */
  boolean isAdmin(Player player);

  /**
   * Checks if the session is a session of an Administrator.
   *
   * @param player player to check the permissions for.
   * @return true, if the player owns the trusted permission.
   */
  boolean isTrustedPlayer(Player player);

  /**
   * Checks if the player is
   *
   * @param player   player to check the permissions for.
   * @param location the location to look up.
   * @return true, if the player is allowed to build here.
   */
  boolean isAllowedToPlaceBlockAt(Player player, Location location);

  /**
   * Checks if the player is
   *
   * @param player   player to check the permissions for.
   * @param location the location to look up.
   * @return true, if the player is allowed to build here.
   */
  boolean isAllowedToDestroyBlockAt(Player player, Location location);

}
