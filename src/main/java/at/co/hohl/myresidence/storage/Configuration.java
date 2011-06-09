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

package at.co.hohl.myresidence.storage;

/**
 * Storage for all settings applied to a world.
 *
 * @author Michael Hohl
 */
public class Configuration {
    /** The configuration used by for loading/saving. */
    private final org.bukkit.util.config.Configuration configuartion;

    /** Costs for one chunk. */
    private double chunkCost;

    /** Title for residence signs. */
    private String signTitle;

    /** Text for sign for sale. */
    private String signSaleText;

    /**
     * Creates a new Configuration with the passed Bukkit Config.
     *
     * @param configuration the bukkit config to load/save.
     */
    public Configuration(org.bukkit.util.config.Configuration configuration) {
        this.configuartion = configuration;
        load();
    }

    /** Loads the configuration. (Auto done on construction, only for reload!) */
    public void load() {
        configuartion.load();
        chunkCost = configuartion.getDouble("chunk.cost", 1000);
        signTitle = configuartion.getString("sign.title", "[Residence]");
        signSaleText = configuartion.getString("sign.sale", "FOR SALE!");
    }

    /** Saves changes to file. */
    public void save() {
        configuartion.setProperty("chunk.cost", chunkCost);
        configuartion.setProperty("sign.title", signTitle);
        configuartion.setProperty("sign.sale", signSaleText);
        configuartion.save();
    }

    public double getChunkCost() {
        return chunkCost;
    }

    public void setChunkCost(double chunkCost) {
        this.chunkCost = chunkCost;
    }

    public String getSignSaleText() {
        return signSaleText;
    }

    public void setSignSaleText(String signSaleText) {
        this.signSaleText = signSaleText;
    }

    public String getSignTitle() {
        return signTitle;
    }

    public void setSignTitle(String signTitle) {
        this.signTitle = signTitle;
    }
}
