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

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Warp point per residence.
 *
 * @author Michael Hohl
 */
@Entity
@Table(name = "res_homes")
public class HomePoint {
    @Id
    private int id;

    private int residenceId;

    // ToDo: Inhabitant id entfernen! Programmiere anstatt des nähesten System ein, auf letzte eingabe passierentes System.
    // !!!: Muss sein, denn dieser Wert wird beim transferieren nicht beachtet!
    private int inhabitantId;

    @Length(max = 32)
    private String world;

    private double x;

    private int y;

    private double z;

    private float yaw;

    private float pitch;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Deprecated
    public int getInhabitantId() {
        return inhabitantId;
    }

    @Deprecated
    public void setInhabitantId(int inhabitantId) {
        this.inhabitantId = inhabitantId;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
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

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }
}
