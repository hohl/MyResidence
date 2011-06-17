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
import java.util.List;

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
            min = 1
    )
    @CommandPermissions({"myresidence.town.found"})
    public static void create(final CommandContext args,
                              final MyResidence plugin,
                              final Nation nation,
                              final Player player,
                              final Session session) {
        Town town = new Town();
        town.setName(args.getJoinedStrings(0));
        town.setFoundedAt(new Date());
        nation.getDatabase().save(town);

        nation.setMajor(town, nation.getInhabitant(session.getPlayerId()), false);

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
            usage = "[town]"
    )
    public static void info(final CommandContext args,
                            final MyResidence plugin,
                            final Nation nation,
                            final Player player,
                            final Session session) throws MyResidenceException {
        Town selectedTown;

        if (args.argsLength() == 0) {
            selectedTown = session.getSelectedTown();
        } else {
            selectedTown = nation.getTown(args.getJoinedStrings(0));
        }

        nation.sendInformation(player, selectedTown);
    }

    @Command(
            aliases = {"rules", "�"},
            desc = "Shows the rules of the town",
            max = 0
    )
    public static void rules(final CommandContext args,
                             final MyResidence plugin,
                             final Nation nation,
                             final Player player,
                             final Session session) throws MyResidenceException {
        Town townAtCurrentLocation = nation.getTown(player.getLocation());

        List<String> rules = nation.getRules(townAtCurrentLocation);

        player.sendMessage(ChatColor.DARK_GREEN + "= = = " + townAtCurrentLocation.getName() + " = = =");
        for (String line : rules) {
            player.sendMessage(line);
        }
    }

    @Command(
            aliases = {"rule"},
            desc = "Manage rules of the selected town"
    )
    @NestedCommand({TownRuleCommands.class})
    @CommandPermissions({"myresidence.town.major.rules"})
    public static void rule(final CommandContext args,
                            final MyResidence plugin,
                            final Nation nation,
                            final Player player,
                            final Session session) {
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
    @CommandPermissions({"myresidence.town.major.account"})
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
    @CommandPermissions({"myresidence.town.major.expand"})
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
    @CommandPermissions({"myresidence.town.major.flag"})
    public static void flags(final CommandContext args,
                             final MyResidence plugin,
                             final Nation nation,
                             final Player player,
                             final Session session) {
    }
}
