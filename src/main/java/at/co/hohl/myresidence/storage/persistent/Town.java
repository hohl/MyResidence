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

/**
 * Represents a town.
 *
 * @author Michael Hohl
 */
@Entity
@Table(name = "res_towns")
public class Town {
    @Id
    private int id;

    @NotEmpty
    @Length(max = 32)
    private String name;

    private int majorId = -1;

    private double money = 0;

    /** Creates a new town. */
    public Town() {
    }

    public void addMoney(double amount) {
        setMoney(getMoney() + amount);
    }

    public void subtractMoney(double amount) {
        setMoney(getMoney() - amount);
    }

    public static String toString(Town town) {
        if (town == null) {
            return "Wildness";
        } else {
            return town.toString();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMajorId() {
        return majorId;
    }

    public void setMajorId(int majorId) {
        this.majorId = majorId;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
