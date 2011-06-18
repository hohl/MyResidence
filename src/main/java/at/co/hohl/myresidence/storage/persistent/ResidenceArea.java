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
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.Location;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * DAO used for storing information about an area.
 *
 * @author Michael Hohl
 */
@Entity
@Table(name = "res_areas")
public class ResidenceArea {
    @Id
    private int id;

    private int residenceId;

    @NotEmpty
    @Length(max = 32)
    private String world;

    @NotNull
    private int lowX;

    @NotNull
    private int lowY;

    @NotNull
    private int lowZ;

    @NotNull
    private int highX;

    @NotNull
    private int highY;

    @NotNull
    private int highZ;

    /** Creates a new ResidenceArea. */
    public ResidenceArea() {
    }

    /**
     * Creates a new Residence with passed selection.
     *
     * @param selection the selection to use for creation.
     */
    public ResidenceArea(Selection selection) {
        Location minimumPoint = selection.getMinimumPoint();
        setLowX(minimumPoint.getBlockX());
        setLowY(minimumPoint.getBlockY());
        setLowZ(minimumPoint.getBlockZ());

        Location maximumPoint = selection.getMaximumPoint();
        setHighX(maximumPoint.getBlockX());
        setHighY(maximumPoint.getBlockY());
        setHighZ(maximumPoint.getBlockZ());

        setWorld(selection.getWorld().getName());
    }

    public int getHighX() {
        return highX;
    }

    public void setHighX(int highX) {
        this.highX = highX;
    }

    public int getHighY() {
        return highY;
    }

    public void setHighY(int highY) {
        this.highY = highY;
    }

    public int getHighZ() {
        return highZ;
    }

    public void setHighZ(int highZ) {
        this.highZ = highZ;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLowX() {
        return lowX;
    }

    public void setLowX(int lowX) {
        this.lowX = lowX;
    }

    public int getLowY() {
        return lowY;
    }

    public void setLowY(int lowY) {
        this.lowY = lowY;
    }

    public int getLowZ() {
        return lowZ;
    }

    public void setLowZ(int lowZ) {
        this.lowZ = lowZ;
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

    @Override
    public String toString() {
        return String.format("W:%d L:%d H:%d",
                getHighX() - getLowX() + 1,
                getHighZ() - getLowZ() + 1,
                getHighY() - getLowY() + 1);
    }
}
