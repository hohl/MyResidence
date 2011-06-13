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
import at.co.hohl.myresidence.exceptions.MyResidenceException;
import at.co.hohl.myresidence.storage.Session;
import at.co.hohl.myresidence.storage.persistent.Town;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.NestedCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Date;

/**
 * Command for managing towns.
 *
 * @author Michael Hohl
 */
public class TownCommands {
    @Command(
            aliases = {"found", "create"},
            usage = "<name>",
            desc = "Creates a new town",
            min = 1,
            flags = "w"
    )
    @CommandPermissions({"myresidence.town.found"})
    public static void create(final CommandContext args,
                              final MyResidence plugin,
                              final Nation nation,
                              final Player player,
                              final Session session) {
        Town town = new Town();
        town.setName(args.getJoinedStrings(0));
        town.setMajorId(nation.getInhabitant(player.getName()).getId());
        town.setFoundedAt(new Date());
        nation.getDatabase().save(town);

        player.sendMessage(ChatColor.DARK_GREEN + "Town '" + args.getJoinedStrings(0) + "' created!");
    }

    @Command(
            aliases = {"select", "sel", "s"},
            usage = "<name>",
            desc = "Selects a town",
            min = 1
    )
    public static void select(final CommandContext args,
                              final MyResidence plugin,
                              final Nation nation,
                              final Player player,
                              final Session session) {
        Town townToSelect = nation.getTown(args.getJoinedStrings(0));
        session.setSelectedTown(townToSelect);

        player.sendMessage(ChatColor.DARK_GREEN + "Town '" + townToSelect.getName() + "' selected!");
    }

    @Command(
            aliases = {"info", "i"},
            desc = "Returns information about the selected town",
            max = 0
    )
    @CommandPermissions({"myresidence.town.info"})
    public static void info(final CommandContext args,
                            final MyResidence plugin,
                            final Nation nation,
                            final Player player,
                            final Session session) throws MyResidenceException {
        Town selectedTown = session.getSelectedTown();
        nation.sendInformation(player, selectedTown);
    }

    @Command(
            aliases = {"list"},
            desc = "Lists available towns"
    )
    @NestedCommand({TownListCommands.class})
    public static void list(final CommandContext args,
                            final MyResidence plugin,
                            final Nation nation,
                            final Player player,
                            final Session session) {
    }

    @Command(
            aliases = {"account"},
            desc = "Manage the bank account of the town"
    )
    @NestedCommand({TownAccountCommands.class})
    public static void account(final CommandContext args,
                               final MyResidence plugin,
                               final Nation nation,
                               final Player player,
                               final Session session) {
    }

    @Command(
            aliases = {"claim", "c"},
            desc = "Claims chunks and expands the town"
    )
    @NestedCommand({TownClaimCommands.class})
    public static void claim(final CommandContext args,
                             final MyResidence plugin,
                             final Nation nation,
                             final Player player,
                             final Session session) {
    }

    @Command(
            aliases = {"flag", "f"},
            desc = "Manage flags of the town"
    )
    @NestedCommand({TownFlagCommands.class})
    public static void flags(final CommandContext args,
                             final MyResidence plugin,
                             final Nation nation,
                             final Player player,
                             final Session session) {
    }
}
