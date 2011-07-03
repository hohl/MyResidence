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

import at.co.hohl.myresidence.exceptions.ResidenceSignMissingException;
import at.co.hohl.myresidence.storage.persistent.Inhabitant;
import at.co.hohl.myresidence.storage.persistent.ResidenceFlag;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.List;

/**
 * Manager for a residence.
 *
 * @author Michael Hohl
 */
public interface ResidenceManager extends FlagManager<ResidenceFlag.Type> {
    /**
     * Adds a member to the residence.
     *
     * @param inhabitant the inhabitant to become membership.
     */
    void addMember(Inhabitant inhabitant);

    /**
     * Removes the residence as a member.
     *
     * @param inhabitant the inhabitant to remove as member.
     */
    void removeMember(Inhabitant inhabitant);

    /**
     * Checks if the inhabitant is member or owner of the residence.
     *
     * @param inhabitant the inhabitant to check.
     * @return true, if the inhabitant is a member.
     */
    boolean isMember(Inhabitant inhabitant);

    /**
     * Returns the members of the residence.
     *
     * @return the members of the residence.
     */
    List<Inhabitant> getMembers();

    /**
     * Sets the area of the residence.
     *
     * @param selection the area to set.
     */
    void setArea(Selection selection);

    /** @return the area of the residence. */
    Selection getArea();

    /**
     * Sets the sign of the residence.
     *
     * @param sign the sign of the residence.
     */
    void setSign(Block sign);

    /** @return the sign of the residence. */
    Block getSign() throws ResidenceSignMissingException;

    /**
     * Sets the home point for the location.
     *
     * @param home the home point for the location.
     */
    void setHome(Location home);

    /** @return the location of the home point. */
    Location getHome();

    /** @return the inhabitants who liked the residence. */
    List<Inhabitant> getLikes();

    /** @param inhabitant the inhabitant to like the residence. */
    void like(Inhabitant inhabitant);

    /** @param inhabitant the inhabitant to unlike the residence. */
    void unlike(Inhabitant inhabitant);
}
