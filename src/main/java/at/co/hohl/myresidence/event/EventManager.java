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

package at.co.hohl.myresidence.event;

import at.co.hohl.myresidence.MyResidence;

import java.util.LinkedList;
import java.util.List;

/**
 * Handler for residence and town events.
 *
 * @author Michael Hohl
 */
public class EventManager {
    /** Plugin which holds the instance. */
    private final MyResidence plugin;

    /** Listeners for residence events. */
    private final List<EventListener> eventListeners = new LinkedList<EventListener>();

    /**
     * Creates a new event manager.
     *
     * @param plugin the plugin which holds the instance.
     */
    public EventManager(MyResidence plugin) {
        this.plugin = plugin;
    }

    /**
     * Calls a new event.
     *
     * @param o the event to call.
     */
    public void callEvent(final Object o) {
        Runnable eventRunnable = new Runnable() {
            public void run() {
                for (EventListener listener : eventListeners) {
                    if (listener.canHandle(o)) {
                        try {
                            listener.handle(o);
                        } catch (Throwable e) {
                            plugin.severe("Exception occurred: %s", e.getClass().getName());
                            e.printStackTrace();
                        }
                    }
                }
            }
        };

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, eventRunnable);
    }

    /**
     * Adds a new listener.
     *
     * @param listener listener to add.
     */
    public void addListener(EventListener listener) {
        eventListeners.add(listener);
    }

    /**
     * Removes a existing listener.
     *
     * @param eventListener listener to remove.
     */
    public void removeListener(EventListener eventListener) {
        eventListeners.remove(eventListener);
    }
}
