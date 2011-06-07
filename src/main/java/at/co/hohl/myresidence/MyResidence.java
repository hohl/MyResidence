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

import at.co.hohl.myresidence.storage.Session;
import at.co.hohl.myresidence.storage.persistent.Residence;
import at.co.hohl.myresidence.storage.persistent.Town;
import at.co.hohl.permissions.PermissionHandler;
import com.avaje.ebean.EbeanServer;
import com.nijikokun.register.payment.Methods;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * Represents main interface for MyResidence.
 *
 * @author Michael Hohl
 */
public interface MyResidence {
    /**
     * Returns the residence at the passed location
     *
     * @param location the location to look for.
     * @return the founded residence or null.
     */
    Residence getResidence(Location location);

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
     * Returns the session for the passed player.
     *
     * @param player the player to look for the session.
     * @return the found or create session.
     */
    Session getSession(Player player);

    /** @return the database. */
    EbeanServer getDatabase();

    /** @return all available payment methods. */
    Methods getMethods();

    /** @return handler for the permissions. */
    PermissionHandler getPermissionHandler();

    /** @return world edit plugin. */
    WorldEditPlugin getWorldEdit();

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
