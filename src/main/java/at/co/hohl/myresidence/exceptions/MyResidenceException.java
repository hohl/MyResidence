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
 * Abstract base class for all Exceptions of the plugin.
 *
 * @author Michael Hohl
 */
public class MyResidenceException extends Exception {
    public MyResidenceException() {
        super();
    }

    public MyResidenceException(String s) {
        super(s);
    }

    public MyResidenceException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public MyResidenceException(Throwable throwable) {
        super(throwable);
    }
}
