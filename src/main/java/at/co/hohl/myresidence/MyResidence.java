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
import at.co.hohl.myresidence.storage.Configuration;
import at.co.hohl.myresidence.storage.Session;
import at.co.hohl.myresidence.storage.persistent.*;
import com.avaje.ebean.EbeanServer;
import com.nijikokun.register.payment.Methods;
import com.sk89q.bukkit.migration.PermissionsResolver;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * Represents main interface for MyResidence.
 *
 * @author Michael Hohl
 */
public interface MyResidence {
    /**
     * Updates the sign linked to passed Residence.
     *
     * @param residence Residence to update.
     */
    void updateResidenceSign(Residence residence) throws ResidenceSignMissingException;

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
    PlayerData getPlayer(int id);

    /**
     * Returns the player data for the passed name.
     *
     * @param name the name to look for.
     * @return the founded player or null.
     */
    PlayerData getPlayer(String name);

    /**
     * Returns the PlayerData of the owner of the passed Residence.
     *
     * @param residence the Residence.
     * @return PlayerData of the owner.
     */
    PlayerData getOwner(Residence residence);

    /**
     * Returns the major of the passed Town.
     *
     * @param town the Town.
     * @return PlayerData of the major.
     */
    PlayerData getMajor(Town town);

    /**
     * Returns the session for the passed player.
     *
     * @param player the player to look for the session.
     * @return the found or create session.
     */
    Session getSession(Player player);

    /**
     * Removes the session of the passed player.
     *
     * @param player the player who's session should be removed.
     */
    void removeSession(Player player);

    /** @return the database. */
    EbeanServer getDatabase();

    /** @return the server instance, which holds the MyResidence plugin. */
    Server getServer();

    /** @return all available payment methods. */
    Methods getMethods();

    /** @return handler for the permissions. */
    PermissionsResolver getPermissionsResolver();

    /** @return world edit plugin. */
    WorldEditPlugin getWorldEdit();

    /**
     * @param world the world to get configuration.
     * @return the main configuration for the plugin.
     */
    Configuration getConfiguration(World world);

    /** @return the name of the implementation of MyResidence. */
    String getName();

    /** @return the version of the implementation of MyResidence. */
    String getVersion();

    /**
     * Formats the passed amount of money to a localized string.
     *
     * @param money the amount of money.
     * @return a string for the amount of money.
     */
    String format(double money);

    /**
     * Logs an message with the level info.
     *
     * @param message the message to log.
     */
    void info(String message, Object... args);

    /**
     * Logs an message with the level warning.
     *
     * @param message the message to log.
     */
    void warning(String message, Object... args);

    /**
     * Logs an message with the level severe.
     *
     * @param message the message to log.
     */
    void severe(String message, Object... args);
}
