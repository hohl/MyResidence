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

import at.co.hohl.myresidence.FlagManager;
import at.co.hohl.myresidence.Nation;
import at.co.hohl.myresidence.storage.persistent.Residence;
import at.co.hohl.myresidence.storage.persistent.ResidenceFlag;

import java.util.LinkedList;
import java.util.List;

/**
 * Implements a FlagManager for residences.
 *
 * @author Michael Hohl
 */
public class PersistResidenceFlagManager implements FlagManager<ResidenceFlag.Type> {
  /**
   * Nation which holds the residence.
   */
  protected final Nation nation;

  /**
   * The area to manage.
   */
  protected final Residence residence;

  /**
   * Creates a new FlagManager implementation.
   *
   * @param nation    nation which holds the residence.
   * @param residence the residence to manage.
   */
  public PersistResidenceFlagManager(Nation nation, Residence residence) {
    this.nation = nation;
    this.residence = residence;
  }

  /**
   * Checks if the passed residence has the flag set.
   *
   * @param flag flag to check.
   * @return true, if the flag is set.
   */
  public boolean hasFlag(ResidenceFlag.Type flag) {
    return nation.getDatabase().find(ResidenceFlag.class)
            .where()
            .eq("residenceId", residence.getId())
            .eq("flag", flag)
            .findRowCount() > 0;
  }

  /**
   * Returns all flags of a residence.
   *
   * @return all flags set for the residence.
   */
  public List<ResidenceFlag.Type> getFlags() {
    List<ResidenceFlag> residenceFlags = nation.getDatabase().find(ResidenceFlag.class)
            .where()
            .eq("residenceId", residence.getId()).findList();

    List<ResidenceFlag.Type> flagTypes = new LinkedList<ResidenceFlag.Type>();
    for (ResidenceFlag flag : residenceFlags) {
      flagTypes.add(flag.getFlag());
    }

    return flagTypes;
  }

  /**
   * Sets the passed flag.
   *
   * @param flag the flag to set.
   */
  public void setFlag(ResidenceFlag.Type flag) {
    if (!hasFlag(flag)) {
      ResidenceFlag residenceFlag = new ResidenceFlag();
      residenceFlag.setResidenceId(residence.getId());
      residenceFlag.setFlag(flag);
      nation.getDatabase().save(residenceFlag);
    }
  }

  /**
   * Remove the passed flag.
   *
   * @param flag the flag to remove.
   */
  public void removeFlag(ResidenceFlag.Type flag) {
    List<ResidenceFlag> residenceFlagsToDelete = nation.getDatabase().find(ResidenceFlag.class)
            .where()
            .eq("residenceId", residence.getId())
            .eq("flag", flag).findList();

    if (residenceFlagsToDelete.size() > 0) {
      nation.getDatabase().delete(residenceFlagsToDelete);
    }
  }
}
