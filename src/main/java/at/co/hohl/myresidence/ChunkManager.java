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

import at.co.hohl.myresidence.exceptions.TownNotFoundException;
import at.co.hohl.myresidence.storage.persistent.Town;
import com.sk89q.worldedit.Vector2D;
import org.bukkit.World;

import java.util.Set;

/**
 * Manages the chunks connected to a town.
 *
 * @author Michael Hohl
 */
public interface ChunkManager {
    /**
     * Returns the owner of the chunk.
     *
     * @param chunk the chunk.
     * @return the owner of the chunk, or null if the chunk is connected to wildnes.
     */
    Town getChunkOwner(World world, Vector2D chunk);

    /**
     * Adds a single chunk to the town.
     *
     * @param town  town to add.
     * @param chunk the chunk to add.
     */
    void addChunk(Town town, World world, Vector2D chunk) throws TownNotFoundException;

    /**
     * Adds a list of chunks.
     *
     * @param town   town to add.
     * @param chunks list of chunks to add.
     */
    void addChunks(Town town, World world, Set<Vector2D> chunks) throws TownNotFoundException;

    /**
     * Checks if the chunks is owned by the town.
     *
     * @param town  town to check.
     * @param chunk the chunk to check.
     * @return true, if the town owns the chunk.
     */
    boolean hasChunk(Town town, World world, Vector2D chunk);

    /**
     * Checks if the chunks are owned by the town.
     *
     * @param town   town to check.
     * @param chunks the chunk.
     * @return if the town owns all the chunks.
     */
    boolean hasChunks(Town town, World world, Set<Vector2D> chunks);
}
