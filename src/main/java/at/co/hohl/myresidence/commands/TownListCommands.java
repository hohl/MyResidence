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
import com.avaje.ebean.ExpressionList;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.worldedit.commands.InsufficientArgumentsException;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Commands for searching and listing towns.
 *
 * @author Michael Hohl
 */
public class TownListCommands {
    /** Maximum number of lines per page. */
    private static final int LINES_PER_PAGE = 7;

    @Command(
            aliases = {"alphabetic", "abc"},
            usage = "[page]",
            desc = "Lists the towns in alphabetical order",
            max = 1
    )
    @CommandPermissions({"myresidence.town.list.alphabetic"})
    public static void alphabetical(final CommandContext args,
                                    final MyResidence plugin,
                                    final Nation nation,
                                    final Player player,
                                    final Session session) throws InsufficientArgumentsException {

        ExpressionList expressionList = nation.getDatabase().find(Town.class).where();

        // Richest town at the top!
        expressionList.orderBy("name ASC");

        // Find and display exact page.
        int page = args.getInteger(0, 1);
        displayResults("Towns (Alphabetical Order)", expressionList, page, plugin, nation, player);

    }

    @Command(
            aliases = {"richest"},
            usage = "[page]",
            desc = "Lists the richest towns",
            max = 1
    )
    @CommandPermissions({"myresidence.town.list.richest"})
    public static void richest(final CommandContext args,
                               final MyResidence plugin,
                               final Nation nation,
                               final Player player,
                               final Session session) throws InsufficientArgumentsException {

        ExpressionList expressionList = nation.getDatabase().find(Town.class).where();

        // Richest town at the top!
        expressionList.orderBy("money DESC");

        // Find and display exact page.
        int page = args.getInteger(0, 1);
        displayResults("Towns (Richest)", expressionList, page, plugin, nation, player);

    }

    @Command(
            aliases = {"oldest", "age"},
            usage = "[page]",
            desc = "Lists the towns in order of their age",
            max = 1
    )
    @CommandPermissions({"myresidence.town.list.oldest"})
    public static void oldest(final CommandContext args,
                              final MyResidence plugin,
                              final Nation nation,
                              final Player player,
                              final Session session) throws InsufficientArgumentsException {

        ExpressionList expressionList = nation.getDatabase().find(Town.class).where();

        // Richest town at the top!
        expressionList.orderBy("foundedAt ASC");

        // Find and display exact page.
        int page = args.getInteger(0, 1);
        displayResults("Towns (Oldest)", expressionList, page, plugin, nation, player);

    }

    // Displays the search results.
    private static void displayResults(final String searchTitle,
                                       final ExpressionList expressionList,
                                       final int page,
                                       final MyResidence plugin,
                                       final Nation nation,
                                       final Player player)
            throws InsufficientArgumentsException {

        int rows = expressionList.findRowCount();
        int index = (page - 1) * 7 + 1;
        if (rows == 0) {
            throw new InsufficientArgumentsException("No search results found!");
        }
        if (index > rows || index < 1) {
            throw new InsufficientArgumentsException("Invalid page number!");
        }
        expressionList.setMaxRows(LINES_PER_PAGE);
        expressionList.setFirstRow((page - 1) * LINES_PER_PAGE);

        // Get towns.
        List<Town> towns = expressionList.findList();

        // Send results to player.
        player.sendMessage(String.format("%s= = = %s [Page %s/%s] = = =",
                ChatColor.LIGHT_PURPLE, searchTitle, page, rows / LINES_PER_PAGE + 1));

        for (Town town : towns) {
            player.sendMessage(
                    String.format(
                            ChatColor.GRAY + "%d. " + ChatColor.WHITE + "%s" + ChatColor.GRAY + "(%s) [Balance: %s]",
                            index,
                            town.getName(),
                            nation.getMajor(town).getName(),
                            plugin.format(town.getMoney())));
            ++index;
        }
    }
}
