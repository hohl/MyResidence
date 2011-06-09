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
import com.avaje.ebean.validation.NotNull;
import org.bukkit.block.Sign;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * DAO used for storing data values.
 *
 * @author Michael Hohl
 */
@Entity
@Table(name = "res_signs")
public class ResidenceSign {
    @Id
    private int id;

    private int residenceId;

    @NotEmpty
    @Length(max = 32)
    private String world;

    @NotNull
    private int x;

    @NotNull
    private int y;

    @NotNull
    private int z;

    /** Creates a new sign. */
    public ResidenceSign() {
    }

    /**
     * Creates a new sign, for the passed sign.
     *
     * @param sign the sign.
     */
    public ResidenceSign(Sign sign) {
        setWorld(sign.getWorld().getName());
        setX(sign.getX());
        setY(sign.getY());
        setZ(sign.getZ());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getResidenceId() {
        return residenceId;
    }

    public void setResidenceId(int residenceId) {
        this.residenceId = residenceId;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }
}
