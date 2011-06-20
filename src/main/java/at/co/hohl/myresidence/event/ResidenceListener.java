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

/**
 * Abstract class for listeners for residences.
 *
 * @author Michael Hohl
 */
public abstract class ResidenceListener implements EventListener {
    /**
     * Checks if the handler can handle the event.
     *
     * @param o the object which is the event.
     * @return true, if the handler could handle the event.
     */
    public boolean canHandle(Object o) {
        return o instanceof ResidenceChangedEvent ||
                o instanceof ResidenceCreatedEvent ||
                o instanceof ResidenceRemovedEvent;
    }

    /**
     * Handles the event.
     *
     * @param o the object which is the event.
     */
    public void handle(Object o) {
        if (o instanceof ResidenceChangedEvent) {
            onResidenceChanged((ResidenceChangedEvent) o);
        } else if (o instanceof ResidenceCreatedEvent) {
            onResidenceCreated((ResidenceCreatedEvent) o);
        } else if (o instanceof ResidenceRemovedEvent) {
            onResidenceRemoved((ResidenceRemovedEvent) o);
        }
    }

    /**
     * Called when a new residence is created.
     *
     * @param event the event itself.
     */
    public void onResidenceCreated(ResidenceCreatedEvent event) {
    }

    /**
     * Called when a residence is changed.
     *
     * @param event the event itself.
     */
    public void onResidenceChanged(ResidenceChangedEvent event) {
    }

    /**
     * Called when a residence is removed.
     *
     * @param event the event itself.
     */
    public void onResidenceRemoved(ResidenceRemovedEvent event) {
    }
}
