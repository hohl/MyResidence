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

import at.co.hohl.myresidence.exceptions.MyResidenceException;
import at.co.hohl.myresidence.exceptions.ResidenceSignMissingException;
import at.co.hohl.myresidence.storage.persistent.*;
import com.avaje.ebean.EbeanServer;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Represents a collection of Towns and Residences.
 *
 * @author Michael Hohl
 */
public interface Nation {
    /**
     * Returns the residence at the passed location
     *
     * @param location the location to look for.
     * @return the founded residence or null.
     */
    Residence getResidence(Location location);

    /**
     * Returns the residence with the passed id.
     *
     * @param id the id of the residence to look for.
     * @return the founded residence or null.
     */
    Residence getResidence(int id);

    /**
     * Returns the residence with the passed name.
     *
     * @param name the name to look for.
     * @return the founded residence or null.
     */
    Residence getResidence(String name);

    /**
     * Returns the residence by the passed sign.
     *
     * @param sign the sign to look for.
     * @return the founded residence or null.
     */
    Residence getResidence(Sign sign);

    /**
     * Returns the area of the Residence.
     *
     * @param residence the Residence.
     * @return the area of the Residence.
     */
    ResidenceArea getResidenceArea(Residence residence);

    /**
     * Returns the area of the Residence
     *
     * @param id the id of the Residence.
     * @return the area of the Residence with the passed id.
     */
    ResidenceArea getResidenceArea(int id);

    /**
     * Returns the sign of the Residence
     *
     * @param residence the Residence.
     * @return the sign of the passed Residence.
     */
    ResidenceSign getResidenceSign(Residence residence);

    /**
     * Returns the sign of the Residence.
     *
     * @param id the id of the Residence.
     * @return the sign of the Residence with the passed id.
     */
    ResidenceSign getResidenceSign(int id);

    /**
     * Returns the town with the passed id.
     *
     * @param id the id of the town to look for.
     * @return the founded town or null.
     */
    Town getTown(int id);

    /**
     * Returns the town with the passed name.
     *
     * @param name the name to look for.
     * @return the founded town or null.
     */
    Town getTown(String name);

    /**
     * Returns the town at the passed location.
     *
     * @param location the location to look for.
     * @return the founded town or null.
     */
    Town getTown(Location location);

    /**
     * Returns the player with the passed id.
     *
     * @param id the id of the player to look for.
     * @return the founded player or null.
     */
    Inhabitant getInhabitant(int id);

    /**
     * Returns the player data for the passed name.
     *
     * @param name the name to look for.
     * @return the founded player or null.
     */
    Inhabitant getInhabitant(String name);

    /**
     * Returns the Inhabitant of the owner of the passed Residence.
     *
     * @param residence the Residence.
     * @return PlayerData of the owner.
     */
    Inhabitant getOwner(Residence residence);

    /**
     * Returns the major of the passed Town.
     *
     * @param town the Town.
     * @return PlayerData of the major.
     */
    Inhabitant getMajor(Town town);

    /**
     * Checks if the passed Town has the flag set.
     *
     * @param town town to check.
     * @param flag flag to check.
     * @return true, if the flag is set.
     */
    boolean hasFlag(Town town, TownFlag.Type flag);

    /**
     * Returns all flags of a town.
     *
     * @param town the town to check.
     * @return the flags set for the town.
     */
    List<TownFlag.Type> getFlags(Town town);

    /**
     * Sets the passed flag.
     *
     * @param town town to set the flag.
     * @param flag the flag to set.
     */
    void setFlag(Town town, TownFlag.Type flag);

    /**
     * Remove the passed flag.
     *
     * @param town town to remove the flag.
     * @param flag the flag to remove.
     */
    void removeFlag(Town town, TownFlag.Type flag);

    /**
     * Checks if the passed Residence has the flag set.
     *
     * @param residence residence to check.
     * @param flag      flag to check.
     * @return true, if the flag is set.
     */
    boolean hasFlag(Residence residence, ResidenceFlag.Type flag);

    /**
     * Returns all flags of a residence.
     *
     * @param residence the residence to check.
     * @return all flags set for the residence.
     */
    List<ResidenceFlag.Type> getFlags(Residence residence);

    /**
     * Sets the passed flag.
     *
     * @param residence residence to set the flag.
     * @param flag      the flag to set.
     */
    void setFlag(Residence residence, ResidenceFlag.Type flag);

    /**
     * Removes the passed flag.
     *
     * @param residence residence to remove the flag.
     * @param flag      the flag to remove.
     */
    void removeFlag(Residence residence, ResidenceFlag.Type flag);

    /**
     * Adds a single rule.
     *
     * @param town the town the rule should be for.
     * @param rule the rule to create.
     */
    void addRule(Town town, String rule);

    /**
     * Removes a rule, which is like the passed string.
     *
     * @param town the town, where the rule should get removed.
     * @param rule the rule message.
     */
    void removeRule(Town town, String rule) throws MyResidenceException;

    /**
     * Gets all rules for the town.
     *
     * @param town the town to look up the rules.
     */
    List<String> getRules(Town town);

    /**
     * Adds a member.
     *
     * @param residence  the residence where the inhabitant should become member.
     * @param inhabitant the inhabitant, which should become member.
     */
    void addMember(Residence residence, Inhabitant inhabitant);

    /**
     * Removes an inhabitant from the residence membership.
     *
     * @param residence  the residence.
     * @param inhabitant the inhabitant.
     */
    void removeMember(Residence residence, Inhabitant inhabitant) throws MyResidenceException;

    /**
     * Returns all members of the residence.
     *
     * @param residence the residence.
     */
    List<Inhabitant> getMembers(Residence residence);

    /**
     * Checks if the inhabitant is member or owner.
     *
     * @param residence  the residence.
     * @param inhabitant the inhabitant.
     */
    boolean isMember(Residence residence, Inhabitant inhabitant);

    /**
     * Checks if the passed chunk is free.
     *
     * @param chunk the chunk to check.
     * @return true, if the chunk is not used by any town.
     */
    boolean isChunkFree(Chunk chunk);

    /**
     * Checks if the town has the chunk.
     *
     * @param town  the town to check.
     * @param chunk the chunk to check.
     * @return true, if the town has the chunk.
     */
    boolean hasChunk(Town town, Chunk chunk);

    /**
     * Adds a single chunk.
     *
     * @param town  the town to add the chunk.
     * @param chunk the chunk to add.
     */
    void addChunk(Town town, Chunk chunk);

    /**
     * Adds all chunks in the passed region to the town.
     *
     * @param town   the town to add the chunks.
     * @param world  the world where the chunks are.
     * @param region the region with the chunks.
     * @return the number of chunks, which were added.
     */
    int addChunks(Town town, World world, Region region);

    /**
     * Checks if the town has all chunks in the region.
     *
     * @param town   the town to check.
     * @param world  the world where the chunks are.
     * @param region the region to check.
     * @return true, if the town has all the chunks.
     */
    boolean hasChunks(Town town, World world, Region region);

    /**
     * Updates the sign linked to passed Residence.
     *
     * @param residence Residence to update.
     * @throws ResidenceSignMissingException the sign of the Residence is missing!
     */
    void updateResidenceSign(Residence residence) throws ResidenceSignMissingException;

    /**
     * Sends the passed player information about the passed object.
     *
     * @param object object to retrieve information. Could be a Residence or a Town.
     * @throws MyResidenceException can not send information to the passed player.
     */
    public void sendInformation(Player player, Object object) throws MyResidenceException;

    /**
     * Saves any changes to towns or residences.
     *
     * @param object the object of the town or residence to save.
     */
    void save(Object object);

    /** @return the database which holds all information about towns and residences. */
    EbeanServer getDatabase();
}
