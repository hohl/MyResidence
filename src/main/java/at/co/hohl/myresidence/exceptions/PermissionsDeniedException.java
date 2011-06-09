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
 * Thrown when user isn't allowed to do something.
 *
 * @author Michael Hohl
 */
public class PermissionsDeniedException extends MyResidenceException {
    public PermissionsDeniedException() {
        super();
    }

    public PermissionsDeniedException(String s) {
        super(s);
    }

    public PermissionsDeniedException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public PermissionsDeniedException(Throwable throwable) {
        super(throwable);
    }
}
