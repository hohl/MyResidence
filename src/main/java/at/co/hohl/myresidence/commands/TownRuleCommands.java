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
import at.co.hohl.myresidence.exceptions.PermissionsDeniedException;
import at.co.hohl.myresidence.storage.Session;
import at.co.hohl.myresidence.storage.persistent.Town;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Commands for managing the town rules.
 *
 * @author Michael Hohl
 */
public class TownRuleCommands {
    @Command(
            aliases = {"add"},
            desc = "Adds a new rule to the selected town",
            min = 1
    )
    @CommandPermissions({"myresidence.town.major.rules"})
    public static void add(final CommandContext args,
                           final MyResidence plugin,
                           final Nation nation,
                           final Player player,
                           final Session session)
            throws MyResidenceException {
        Town selectedTown = session.getSelectedTown();

        if (!session.hasMajorRights(selectedTown)) {
            throw new PermissionsDeniedException("You are not the major of this town!");
        }

        nation.getRuleManager(selectedTown).addRule(args.getJoinedStrings(0));

        player.sendMessage(ChatColor.DARK_GREEN + "Added new line to the rules of " +
                ChatColor.GREEN + selectedTown.getName() +
                ChatColor.DARK_GREEN + ".");
    }

    @Command(
            aliases = {"remove"},
            desc = "Removes a line from the rules",
            min = 1
    )
    @CommandPermissions({"myresidence.town.major.rules"})
    public static void remove(final CommandContext args,
                              final MyResidence plugin,
                              final Nation nation,
                              final Player player,
                              final Session session)
            throws MyResidenceException {
        Town selectedTown = session.getSelectedTown();

        nation.getRuleManager(selectedTown).removeRule(args.getJoinedStrings(0));

        player.sendMessage(ChatColor.DARK_GREEN + "Removed a line from the rules of " +
                ChatColor.GREEN + selectedTown.getName() +
                ChatColor.DARK_GREEN + ".");
    }
}
