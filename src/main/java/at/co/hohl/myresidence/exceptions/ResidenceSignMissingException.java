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

import at.co.hohl.myresidence.storage.persistent.Residence;

/**
 * Exception thrown when sign linked to Residence is missing.
 *
 * @author Michael Hohl
 */
public class ResidenceSignMissingException extends MyResidenceException {
    /** Residence which miss the sign. */
    private final Residence residence;

    /**
     * Creates a new ResidenceSignMissingException for the passed Residence.
     *
     * @param residence the Residence which should own the sign.
     */
    public ResidenceSignMissingException(Residence residence) {
        super("Miss sign for Residence '" + residence.getName() + "'!");
        this.residence = residence;
    }

    /**
     * Creates a new ResidenceSignMissingException for the passed Residence.
     *
     * @param residence the residence to create.
     * @param message   the message to send.
     */
    public ResidenceSignMissingException(Residence residence, String message) {
        super(message);
        this.residence = residence;
    }
}
