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

package at.co.hohl.myresidence.bukkit.persistent;

import at.co.hohl.myresidence.ChunkManager;
import at.co.hohl.myresidence.Nation;
import at.co.hohl.myresidence.exceptions.TownNotFoundException;
import at.co.hohl.myresidence.storage.persistent.Town;
import at.co.hohl.myresidence.storage.persistent.TownChunk;
import com.sk89q.worldedit.Vector2D;
import org.bukkit.World;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Implementation of ChunkManager for bukkit peristence.
 *
 * @author Michael Hohl
 */
public class PersistChunkManager implements ChunkManager {
  /**
   * Nation which holds the residence.
   */
  protected final Nation nation;

  /**
   * Creates a new ChunkManager implementation.
   *
   * @param nation nation which holds the residence.
   */
  public PersistChunkManager(Nation nation) {
    this.nation = nation;
  }

  /**
   * Returns the owner of the chunk.
   *
   * @param chunk the chunk.
   * @return the owner of the chunk, or null if the chunk is connected to wildnes.
   */
  public Town getChunkOwner(World world, Vector2D chunk) {
    TownChunk townChunk = nation.getDatabase().find(TownChunk.class)
            .where()
            .eq("world", world.getName())
            .eq("x", chunk.getBlockX())
            .eq("z", chunk.getBlockZ())
            .findUnique();

    if (townChunk != null) {
      return nation.getDatabase().find(Town.class)
              .where()
              .idEq(townChunk.getTownId())
              .findUnique();
    } else {
      return null;
    }
  }

  /**
   * Adds a single chunk to the town.
   *
   * @param town  town to add.
   * @param chunk the chunk to add.
   */
  public void addChunk(Town town, World world, Vector2D chunk) throws TownNotFoundException {
    if (!nextToAnyChunks(town, world, chunk)) {
      throw new TownNotFoundException("Can only add chunks next to another town chunk!");
    }

    TownChunk townChunk = nation.getDatabase().find(TownChunk.class)
            .where()
            .eq("world", world.getName())
            .eq("x", chunk.getBlockX())
            .eq("z", chunk.getBlockZ())
            .findUnique();

    if (townChunk == null) {
      townChunk = new TownChunk();
      townChunk.setWorld(world.getName());
      townChunk.setX(chunk.getBlockX());
      townChunk.setZ(chunk.getBlockZ());
    }


    townChunk.setTownId(town.getId());

    nation.getDatabase().save(townChunk);
  }

  /**
   * Adds a list of chunks.
   *
   * @param town   town to add.
   * @param chunks list of chunks to add.
   */
  public void addChunks(Town town, World world, Set<Vector2D> chunks) throws TownNotFoundException {
    if (!nextToAnyChunks(town, world, chunks)) {
      throw new TownNotFoundException("Can only add chunks next to another town chunk!");
    }

    List<TownChunk> changedTownChunks = new LinkedList<TownChunk>();

    for (Vector2D chunk : chunks) {
      TownChunk townChunk = nation.getDatabase().find(TownChunk.class)
              .where()
              .eq("world", world.getName())
              .eq("x", chunk.getBlockX())
              .eq("z", chunk.getBlockZ())
              .findUnique();

      if (townChunk == null) {
        townChunk = new TownChunk();
        townChunk.setWorld(world.getName());
        townChunk.setX(chunk.getBlockX());
        townChunk.setZ(chunk.getBlockZ());
      }

      if (townChunk.getTownId() != town.getId()) {
        townChunk.setTownId(town.getId());
        changedTownChunks.add(townChunk);
      }
    }

    nation.getDatabase().save(changedTownChunks);
  }

  /**
   * Checks if the chunks is owned by the town.
   *
   * @param town  town to check.
   * @param chunk the chunk to check.
   * @return true, if the town owns the chunk.
   */
  public boolean hasChunk(Town town, World world, Vector2D chunk) {
    return nation.getDatabase().find(TownChunk.class)
            .where()
            .eq("townId", town.getId())
            .eq("world", world.getName())
            .eq("x", chunk.getBlockX())
            .eq("z", chunk.getBlockZ())
            .findRowCount() > 0;
  }

  /**
   * Checks if the chunks are owned by the town.
   *
   * @param town   town to check.
   * @param chunks the chunk.
   * @return true, if the town owns all the chunks.
   */
  public boolean hasChunks(Town town, World world, Set<Vector2D> chunks) {
    for (Vector2D chunk : chunks) {
      if (nation.getDatabase().find(TownChunk.class)
              .where()
              .eq("townId", town.getId())
              .eq("world", world.getName())
              .eq("x", chunk.getBlockX())
              .eq("z", chunk.getBlockZ())
              .findRowCount() == 0) {
        return false;
      }
    }

    return true;
  }

  /**
   * Checks if there is any of the town chunks next to any of the chunks in the passed list. If there aren't any
   * chunks owned by the town, this function always returns null.
   *
   * @param town  town to check
   * @param world world with the chunks.
   * @param chunk the chunk to check.
   * @return true if at least one of the chunks is next to one of the town chunks.
   */
  public boolean nextToAnyChunks(Town town, World world, Vector2D chunk) {
    Set<Vector2D> chunks = new LinkedHashSet<Vector2D>();
    chunks.add(chunk);
    return nextToAnyChunks(town, world, chunks);
  }

  /**
   * Checks if there is any of the town chunks next to any of the chunks in the passed list. If there aren't any
   * chunks owned by the town, this function always returns null.
   *
   * @param town   town to check
   * @param world  world with the chunks.
   * @param chunks the chunks to check.
   * @return true if at least one of the chunks is next to one of the town chunks.
   */
  public boolean nextToAnyChunks(Town town, World world, Set<Vector2D> chunks) {
    int countTownChunks = nation.getDatabase().find(TownChunk.class)
            .where()
            .eq("townId", town.getId())
            .findRowCount();

    if (countTownChunks == 0) {
      return true;
    }

    int countNearTownChunks = 0;

    for (Vector2D chunk : chunks) {
      countNearTownChunks += nation.getDatabase().find(TownChunk.class)
              .where()
              .eq("world", world.getName())
              .ge("x", chunk.getBlockX() - 1)
              .le("x", chunk.getBlockX() + 1)
              .ge("z", chunk.getBlockZ() - 1)
              .le("z", chunk.getBlockZ() + 1)
              .eq("townId", town.getId())
              .findRowCount();
    }

    return countNearTownChunks > 0;
  }
}
