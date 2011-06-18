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

package at.co.hohl.myresidence.bukkit;

import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;

/**
 * Dummy implementation of chunk, only used to store the address of a chunk.
 *
 * @author Michael Hohl
 */
public class DummyChunk implements Chunk {
    private World world;

    private int x;

    private int z;

    public DummyChunk(World world, int x, int z) {
        this.world = world;
        this.x = x;
        this.z = z;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public Block getBlock(int i, int i1, int i2) {
        return null;
    }

    public ChunkSnapshot getChunkSnapshot() {
        return null;
    }

    public ChunkSnapshot getChunkSnapshot(boolean b, boolean b1, boolean b2) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Entity[] getEntities() {
        return new Entity[0];
    }

    public BlockState[] getTileEntities() {
        return new BlockState[0];
    }
}
