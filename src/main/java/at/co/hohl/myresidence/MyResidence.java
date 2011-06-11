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

import at.co.hohl.myresidence.storage.Configuration;
import com.nijikokun.register.payment.Methods;
import com.sk89q.bukkit.migration.PermissionsResolver;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.plugin.PluginDescriptionFile;

/**
 * <p/>
 * Represents main interface for MyResidence. Get the interface by looking up the 'MyResidence' plugin on the Bukkit
 * server.
 * <p/>
 * If you are an plugin developer, which wants to link into MyResidence, also look at {@see
 * at.co.hohl.myresidence.Nation} for handling towns and residences.
 *
 * @author Michael Hohl
 */
public interface MyResidence {
    /** @return the collection of towns and residences. */
    Nation getNation();

    /**
     * @param world the world to get configuration.
     * @return the main configuration for the plugin.
     */
    Configuration getConfiguration(World world);

    /** @return the SessionManager used by this MyResidence implementation. */
    SessionManager getSessionManager();

    /** @return all available payment methods. */
    Methods getPaymentMethods();

    /** @return handler for the permissions. */
    PermissionsResolver getPermissionsResolver();

    /** @return world edit plugin. */
    WorldEditPlugin getWorldEdit();

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

    /** @return the description of the implementation of MyResidence. */
    PluginDescriptionFile getDescription();

    /** @return the server, which holds the implementation of MyResidence. */
    Server getServer();
}
