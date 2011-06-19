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

package at.co.hohl.myresidence.exceptions;

/**
 * Called when a town is not found. For example, when using search function, or when claiming a region without any
 * chunks near the region.
 *
 * @author Michael Hohl
 */
public class TownNotFoundException extends MyResidenceException {
    public TownNotFoundException() {
        super();
    }

    public TownNotFoundException(String s) {
        super(s);
    }

    public TownNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public TownNotFoundException(Throwable throwable) {
        super(throwable);
    }
}
