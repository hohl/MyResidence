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

package at.co.hohl.myresidence;

import at.co.hohl.myresidence.exceptions.MyResidenceException;
import at.co.hohl.myresidence.storage.persistent.*;
import com.avaje.ebean.EbeanServer;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Represents a collection of Towns and Residences.
 *
 * @author Michael Hohl
 */
public interface Nation {
  /**
   * Returns the residence at the passed location
   *
   * @param location the location to look for.
   * @return the founded residence or null.
   */
  Residence getResidence(Location location);

  /**
   * Returns the residence with the passed id.
   *
   * @param id the id of the residence to look for.
   * @return the founded residence or null.
   */
  Residence getResidence(int id);

  /**
   * Returns the residence with the passed name.
   *
   * @param name the name to look for.
   * @return the founded residence or null.
   */
  Residence getResidence(String name);

  /**
   * Returns the residence by the passed sign.
   *
   * @param sign the sign to look for.
   * @return the founded residence or null.
   */
  Residence getResidence(Sign sign);

  /**
   * Find a residence by name.
   *
   * @param search a part of the name to search.
   * @return the residence found.
   */
  List<Residence> findResidences(String search);

  /**
   * Find a residence by name owned by the passed residence.
   *
   * @param inhabitant the inhabitant which owns the residence to look for.
   * @param search     a part of the name to search.
   * @return the residence found.
   */
  List<Residence> findResidences(Inhabitant inhabitant, String search);

  /**
   * Finds all residences owned by the passed inhabitant.
   *
   * @param inhabitant the inhabitant to look for.
   * @return list of the found residences.
   */
  List<Residence> findResidences(Inhabitant inhabitant);

  /**
   * Returns the town with the passed id.
   *
   * @param id the id of the town to look for.
   * @return the founded town or null.
   */
  Town getTown(int id);

  /**
   * Returns the town with the passed name.
   *
   * @param name the name to look for.
   * @return the founded town or null.
   */
  Town getTown(String name);

  /**
   * Returns the town at the passed location.
   *
   * @param location the location to look for.
   * @return the founded town or null.
   */
  Town getTown(Location location);

  /**
   * Finds a town for the passed name.
   *
   * @param name the name of the town to find. Could alos be only a part of the name.
   * @return founded towns.
   */
  List<Town> findTown(String name);

  /**
   * Removes a town.
   *
   * @param town town to remove.
   */
  void remove(Town town) throws MyResidenceException;

  /**
   * Removes a residence.
   *
   * @param residence residence to remove.
   */
  void remove(Residence residence);

  /**
   * Returns the player with the passed id.
   *
   * @param id the id of the player to look for.
   * @return the founded player or null.
   */
  Inhabitant getInhabitant(int id);

  /**
   * Returns the player data for the passed name.
   *
   * @param name the name to look for.
   * @return the founded player or null.
   */
  Inhabitant getInhabitant(String name);

  /**
   * @return manager for the chunks.
   */
  ChunkManager getChunkManager();

  /**
   * Returns a manager for the residence.
   *
   * @param residence the residence.
   * @return the manager for the residence.
   */
  ResidenceManager getResidenceManager(Residence residence);

  /**
   * Returns a manager for the town.
   *
   * @param town the manager for the town.
   * @return the town.
   */
  TownManager getTownManager(Town town);

  /**
   * Returns a manager for the rules of the town.
   *
   * @param town the town to manage.
   * @return the rule manager for the town.
   */
  RuleManager getRuleManager(Town town);

  /**
   * Returns a manager for the flags of the residence.
   *
   * @param residence the residence to manage.
   * @return the manager for the flags.
   */
  FlagManager<ResidenceFlag.Type> getFlagManager(Residence residence);

  /**
   * Returns a manager for the flags of the town.
   *
   * @param town the town to manage.
   * @return the manager for the flags.
   */
  FlagManager<TownFlag.Type> getFlagManager(Town town);

  /**
   * Sends the passed player information about the passed object.
   *
   * @param object object to retrieve information. Could be a Residence or a Town.
   * @throws MyResidenceException can not send information to the passed player.
   */
  public void sendInformation(Player player, Object object) throws MyResidenceException;

  /**
   * Saves any changes to towns or residences.
   *
   * @param object the object of the town or residence to save.
   */
  void save(Object object);

  /**
   * @return the database which holds all information about towns and residences.
   */
  EbeanServer getDatabase();
}
