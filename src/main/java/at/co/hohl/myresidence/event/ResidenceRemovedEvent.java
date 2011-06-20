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

import at.co.hohl.myresidence.storage.Session;
import at.co.hohl.myresidence.storage.persistent.Residence;

/**
 * Event for removing a residence.
 *
 * @author Michael Hohl
 */
public final class ResidenceRemovedEvent {
    private final Residence residence;

    private final Session removedBy;

    public ResidenceRemovedEvent(Session removedBy, Residence residence) {
        this.removedBy = removedBy;
        this.residence = residence;
    }

    public Session getRemovedBy() {
        return removedBy;
    }

    public Residence getResidence() {
        return residence;
    }
}
