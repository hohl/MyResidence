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
import at.co.hohl.myresidence.storage.persistent.Town;
import at.co.hohl.myresidence.storage.persistent.TownFlag;

import java.util.LinkedList;
import java.util.List;

/**
 * Implements a FlagManager for TownFlags.
 *
 * @author Michael Hohl
 */
public class PersistTownFlagManager implements FlagManager<TownFlag.Type> {
    /** Nation which holds the town. */
    protected final Nation nation;

    /** The area to manage. */
    protected final Town town;

    /**
     * Creates a new FlagManager implementation.
     *
     * @param nation nation which holds the town.
     * @param town   the town to manage.
     */
    public PersistTownFlagManager(Nation nation, Town town) {
        this.nation = nation;
        this.town = town;
    }

    /**
     * Checks if residence or town has has the flag set.
     *
     * @param flag flag to check.
     * @return true, if the flag is set.
     */
    public boolean hasFlag(TownFlag.Type flag) {
        return nation.getDatabase().find(TownFlag.class)
                .where()
                .eq("townId", town.getId())
                .eq("flag", flag)
                .findRowCount() > 0;
    }

    /**
     * Returns all flags of a town or residence.
     *
     * @return the flags set.
     */
    public List<TownFlag.Type> getFlags() {
        List<TownFlag> residenceFlags = nation.getDatabase().find(TownFlag.class)
                .where()
                .eq("townId", town.getId()).findList();

        List<TownFlag.Type> flagTypes = new LinkedList<TownFlag.Type>();
        for (TownFlag flag : residenceFlags) {
            flagTypes.add(flag.getFlag());
        }

        return flagTypes;
    }

    /**
     * Sets the passed flag.
     *
     * @param flag the flag to set.
     */
    public void setFlag(TownFlag.Type flag) {
        if (!hasFlag(flag)) {
            TownFlag townFlag = new TownFlag();
            townFlag.setTownId(town.getId());
            townFlag.setFlag(flag);
            nation.getDatabase().save(townFlag);
        }
    }

    /**
     * Remove the passed flag.
     *
     * @param flag the flag to remove.
     */
    public void removeFlag(TownFlag.Type flag) {
        List<TownFlag> townFlagsToDelete = nation.getDatabase().find(TownFlag.class)
                .where()
                .eq("townId", town.getId())
                .eq("flag", flag).findList();

        if (townFlagsToDelete.size() > 0) {
            nation.getDatabase().delete(townFlagsToDelete);
        }
    }
}
