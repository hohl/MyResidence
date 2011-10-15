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

package at.co.hohl.myresidence.bukkit.persistent;

import at.co.hohl.myresidence.Nation;
import at.co.hohl.myresidence.TownManager;
import at.co.hohl.myresidence.storage.persistent.Inhabitant;
import at.co.hohl.myresidence.storage.persistent.Residence;
import at.co.hohl.myresidence.storage.persistent.TownFlag;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

/**
 * Town Manager used for the wildness.
 *
 * @author Michael Hohl
 */
public class PersistWildnessManager implements TownManager {
  private final Nation nation;

  public PersistWildnessManager(Nation nation) {
    this.nation = nation;
  }

  /**
   * Adds a major for the town.
   *
   * @param inhabitant the inhabitant to add as major.
   */
  public void addMajor(Inhabitant inhabitant) {
    throw new RuntimeException("You can not add a major to the wildness!");
  }

  /**
   * Removes a major of the town.
   *
   * @param inhabitant the inhabitant to remove as major.
   */
  public void removeMajor(Inhabitant inhabitant) {
    throw new RuntimeException("You can not remove a major to the wildness!");
  }

  /**
   * Checks if the inhabitant is a major in the town.
   *
   * @param inhabitant the inhabitant to check.
   * @return true, if the inhabitant is major.
   */
  public boolean isMajor(Inhabitant inhabitant) {
    Player player = Bukkit.getServer().getPlayer(inhabitant.getName());
    return player != null && nation.getPermissionsResolver().hasPermission(player, "myresidence.wildness");
  }

  /**
   * @return the major of the town.
   */
  public List<Inhabitant> getMajors() {
    return new LinkedList<Inhabitant>();
  }

  /**
   * @return all public majors of the town.
   */
  public List<Inhabitant> getPublicMajors() {
    return new LinkedList<Inhabitant>();
  }

  /**
   * @return inhabitants of the town.
   */
  public List<Inhabitant> getInhabitants() {
    return new LinkedList<Inhabitant>();
  }

  /**
   * Checks if the passed inhabitant is an inhabitant of the town.
   *
   * @param inhabitant the inhabitant to check.
   * @return true, if the inhabitant is an inhabitant of the town.
   */
  public boolean isInhabitant(Inhabitant inhabitant) {
    return false;
  }

  /**
   * @return residences of the town.
   */
  public List<Residence> getResidences() {
    return new LinkedList<Residence>();
  }

  /**
   * Checks if residence or town has has the flag set.
   *
   * @param flag flag to check.
   * @return true, if the flag is set.
   */
  public boolean hasFlag(TownFlag.Type flag) {
    return false;
  }

  /**
   * Returns all flags of a town or residence.
   *
   * @return the flags set.
   */
  public List<TownFlag.Type> getFlags() {
    return new LinkedList<TownFlag.Type>();
  }

  /**
   * Sets the passed flag.
   *
   * @param flag the flag to set.
   */
  public void setFlag(TownFlag.Type flag) {
    throw new RuntimeException("You can not set a flag to the wildness!");
  }

  /**
   * Remove the passed flag.
   *
   * @param flag the flag to remove.
   */
  public void removeFlag(TownFlag.Type flag) {
    throw new RuntimeException("You can not remove a flag from the wildness!");
  }
}
