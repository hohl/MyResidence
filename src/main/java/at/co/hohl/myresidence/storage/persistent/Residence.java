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

import at.co.hohl.myresidence.MyResidence;
import com.avaje.ebean.validation.Length;
import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * DAO used for storing information about residences.
 *
 * @author Michael Hohl
 */
@Entity
@Table(name = "res_residences")
public class Residence {
    @Id
    private int id;

    private int townId = -1;

    @NotEmpty
    @Length(max = 32)
    private String name;

    private int ownerId = -1;

    @NotNull
    private double value = 0.00f;

    @NotNull
    private double price = 0.00f;

    private boolean forSale = false;

    /** Creates a new Residence. */
    public Residence() {
    }

    public void sendInformation(MyResidence plugin, Player player) {
        player.sendMessage(ChatColor.LIGHT_PURPLE + "= = = ABOUT RESIDENCE = = =");

        // Send name
        player.sendMessage(ChatColor.GRAY + "Name: " + ChatColor.WHITE + getName());

        // Retrieve and send owner...
        String owner = "NOBODY";
        if (getOwnerId() != -1) {
            owner = plugin.getPlayer(getOwnerId()).toString();
        }
        player.sendMessage(ChatColor.GRAY + "Owner: " + ChatColor.WHITE + owner);

        // Retrieve and send town...
        String town = "ANY (wildness)";
        if (getTownId() != -1) {
            Town townData = plugin.getTown(getTownId());
            town = townData.toString() + " (Major: " + plugin.getMajor(townData) + ")";
        }
        player.sendMessage(ChatColor.GRAY + "Town: " + ChatColor.WHITE + town);

        // Retrieve and send area...
        player.sendMessage(ChatColor.GRAY + "Size: " + ChatColor.WHITE + plugin.getResidenceArea(this));

        // Retrieve and send money values.
        player.sendMessage(ChatColor.GRAY + "Value: " + plugin.format(getValue()));
        if (isForSale()) {
            player.sendMessage(ChatColor.YELLOW + "RESIDENCE FOR SALE!");
            player.sendMessage(ChatColor.YELLOW + "Price: " + plugin.format(getPrice()));
        }
    }

    public boolean isForSale() {
        return forSale;
    }

    public void setForSale(boolean forSale) {
        this.forSale = forSale;
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

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getTownId() {
        return townId;
    }

    public void setTownId(int townId) {
        this.townId = townId;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
