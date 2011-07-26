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

package at.co.hohl.myresidence.commands;

import at.co.hohl.myresidence.MyResidence;
import at.co.hohl.myresidence.Nation;
import at.co.hohl.myresidence.storage.Session;
import at.co.hohl.myresidence.storage.persistent.Town;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.worldedit.Vector2D;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Command for viewing the town map.
 *
 * @author Michael Hohl
 */
public class MapCommand {
  private static final int MAP_ROWS = 20;
  private static final int MAP_LINES = 5;

  @Command(
          aliases = {"map"},
          desc = "Displays the town map",
          max = 0
  )
  public static void map(final CommandContext args,
                         final MyResidence plugin,
                         final Nation nation,
                         final Player player,
                         final Session session) {
    Chunk playerChunk = player.getLocation().getBlock().getChunk();
    World world = player.getWorld();
    Town currentTown = nation.getTown(player.getLocation());

    int chunkXstart = playerChunk.getX() - (MAP_ROWS / 2);
    int chunkXend = chunkXstart + MAP_ROWS;
    int chunkZstart = playerChunk.getZ() - (MAP_LINES / 2);
    int chunkZend = chunkZstart + MAP_LINES;

    player.sendMessage(ChatColor.GRAY + "= = = = TOWN MAP = = = =");
    for (int indexZ = chunkZstart; indexZ <= chunkZend; ++indexZ) {
      StringBuilder line = new StringBuilder();
      for (int indexX = chunkXstart; indexX <= chunkXend; ++indexX) {
        Vector2D chunk = new Vector2D(indexX, indexZ);

        if (chunk.getX() == playerChunk.getX() && chunk.getZ() == playerChunk.getZ()) {
          line.append(ChatColor.WHITE);
        } else {
          line.append(ChatColor.GRAY);
        }

        Town chunkOwner = nation.getChunkManager().getChunkOwner(world, chunk);
        if (chunkOwner == null) {
          line.append(" -");
        } else if (chunkOwner.equals(currentTown)) {
          line.append(" #");
        } else {
          line.append(ChatColor.DARK_GRAY + " #");
        }
      }
      player.sendMessage(line.toString());
    }
  }
}
