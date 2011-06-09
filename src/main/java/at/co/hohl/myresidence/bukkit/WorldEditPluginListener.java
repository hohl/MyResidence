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

package at.co.hohl.myresidence.bukkit;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;

/**
 * Listens to bukkit if there is a WorldEdit api enabled.
 *
 * @author Michael Hohl
 */
public class WorldEditPluginListener extends ServerListener {
    /** Plugin which holds the instance. */
    private final MyResidenceAPI api;

    /**
     * Creates a new WorldEditPluginListener.
     *
     * @param plugin the api which holds the instance.
     */
    public WorldEditPluginListener(final MyResidenceAPI plugin) {
        this.api = plugin;
    }

    /**
     * Called when an api gets enabled.
     *
     * @param event the occurred event.
     */
    @Override
    public void onPluginEnable(PluginEnableEvent event) {
        if (api.getWorldEdit() == null && "WorldEdit".equals(event.getPlugin().getDescription().getName())) {
            api.setWorldEdit((WorldEditPlugin) event.getPlugin());

            api.info("WorldEdit plugin connected!");
        }
    }

    /**
     * Called when an api gets disabled.
     *
     * @param event the occurred event.
     */
    @Override
    public void onPluginDisable(PluginDisableEvent event) {
        if (api.getWorldEdit() != null && "WorldEdit".equals(event.getPlugin().getDescription().getName())) {
            api.setWorldEdit(null);

            api.info("WorldEdit plugin detached!");
        }
    }
}
