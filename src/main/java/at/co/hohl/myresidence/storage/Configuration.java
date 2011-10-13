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

import java.util.LinkedList;
import java.util.List;

/**
 * Storage for all settings applied to a world.
 *
 * @author Michael Hohl
 */
public class Configuration {
  // The configuration used by for loading/saving.
  private final org.bukkit.util.config.Configuration configuration;

  // Costs for one chunk.
  private double chunkCost;

  // Title for residence signs.
  private String signTitle;

  // Text for sign for sale.
  private String signSaleText;

  // Check if block is free, before teleporting.
  private boolean safeTeleport;

  // Maximum numbers of blocks a residence is allowed to overlay from area.
  private int residenceOverlay;

  // Maximum number of creatures spawned in world.
  private int mobSpawnLimit;

  // Setting that to true will block mob spawning from spawners.
  private boolean denyBlockSpawners;

  // Blocks allowed to build/destroy in default areas.
  private List<Integer> allowedToBuildInWildness;
  private List<Integer> allowedToDestroyInWildness;
  private List<Integer> allowedToBuildInTown;
  private List<Integer> allowedToDestroyInTown;

  /**
   * Creates a new Configuration with the passed Bukkit Config.
   *
   * @param configuration the bukkit config to load/save.
   */
  public Configuration(org.bukkit.util.config.Configuration configuration) {
    this.configuration = configuration;
    load();
  }

  /**
   * Loads the configuration. (Auto done on construction, only for reload!)
   */
  public void load() {
    configuration.load();
    chunkCost = configuration.getDouble("cost.chunk", 1000);
    signTitle = configuration.getString("sign.title", "[Residence]");
    signSaleText = configuration.getString("sign.sale", "FOR SALE!");
    safeTeleport = configuration.getBoolean("safeTeleport", true);
    residenceOverlay = configuration.getInt("residenceOverlay", 1);
    mobSpawnLimit = configuration.getInt("mobSpawnLimit", 600);
    denyBlockSpawners = configuration.getBoolean("denyMobSpawners", true);
    allowedToBuildInTown = configuration.getIntList("town.place", new LinkedList<Integer>());
    allowedToDestroyInTown = configuration.getIntList("town.destroy", new LinkedList<Integer>());
    allowedToBuildInWildness = configuration.getIntList("wildness.place", new LinkedList<Integer>());
    allowedToDestroyInWildness = configuration.getIntList("wildness.destroy", new LinkedList<Integer>());
  }

  /**
   * Saves changes to file.
   */
  public void save() {
    configuration.setProperty("cost.chunk", chunkCost);
    configuration.setProperty("sign.title", signTitle);
    configuration.setProperty("sign.sale", signSaleText);
    configuration.setProperty("safeTeleport", safeTeleport);
    configuration.setProperty("residenceOverlay", residenceOverlay);
    configuration.setProperty("mobSpawnLimit", mobSpawnLimit);
    configuration.setProperty("denyMobSpawners", denyBlockSpawners);
    configuration.setProperty("town.place", allowedToBuildInTown);
    configuration.setProperty("town.destroy", allowedToDestroyInTown);
    configuration.setProperty("wildness.place", allowedToBuildInWildness);
    configuration.setProperty("wildness.destroy", allowedToDestroyInWildness);
    configuration.save();
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

  public boolean isSafeTeleport() {
    return safeTeleport;
  }

  public void setSafeTeleport(boolean safeTeleport) {
    this.safeTeleport = safeTeleport;
  }

  public boolean isDenyBlockSpawners() {
    return denyBlockSpawners;
  }

  public void setDenyBlockSpawners(boolean denyBlockSpawners) {
    this.denyBlockSpawners = denyBlockSpawners;
  }

  public int getResidenceOverlay() {
    return residenceOverlay;
  }

  public void setResidenceOverlay(int residenceOverlay) {
    this.residenceOverlay = residenceOverlay;
  }

  public int getMobSpawnLimit() {
    return mobSpawnLimit;
  }

  public void setMobSpawnLimit(int mobSpawnLimit) {
    this.mobSpawnLimit = mobSpawnLimit;
  }

  public List<Integer> getAllowedToBuildInWildness() {
    return allowedToBuildInWildness;
  }

  public void setAllowedToBuildInWildness(List<Integer> allowedToBuildInWildness) {
    this.allowedToBuildInWildness = allowedToBuildInWildness;
  }

  public List<Integer> getAllowedToDestroyInWildness() {
    return allowedToDestroyInWildness;
  }

  public void setAllowedToDestroyInWildness(List<Integer> allowedToDestroyInWildness) {
    this.allowedToDestroyInWildness = allowedToDestroyInWildness;
  }

  public List<Integer> getAllowedToBuildInTown() {
    return allowedToBuildInTown;
  }

  public void setAllowedToBuildInTown(List<Integer> allowedToBuildInTown) {
    this.allowedToBuildInTown = allowedToBuildInTown;
  }

  public List<Integer> getAllowedToDestroyInTown() {
    return allowedToDestroyInTown;
  }

  public void setAllowedToDestroyInTown(List<Integer> allowedToDestroyInTown) {
    this.allowedToDestroyInTown = allowedToDestroyInTown;
  }
}
