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

package at.co.hohl.myresidence.storage.persistent;

import com.avaje.ebean.validation.Length;
import com.avaje.ebean.validation.NotEmpty;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Represents a player.
 *
 * @author Michael Hohl
 */
@Entity
@Table(name = "res_inhabitants", uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
public class Inhabitant implements Comparable<Inhabitant> {
    @Id
    private int id;

    @NotEmpty
    @Length(max = 16)
    private String name;

    /** Creates a new player data. */
    public Inhabitant() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int compareTo(Inhabitant inhabitant) {
        return getName().compareTo(inhabitant.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Inhabitant that = (Inhabitant) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return getName();
    }
}
