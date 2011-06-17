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
import at.co.hohl.myresidence.exceptions.PlayerNotFoundException;
import at.co.hohl.myresidence.storage.Session;
import at.co.hohl.myresidence.storage.persistent.Inhabitant;
import at.co.hohl.myresidence.storage.persistent.Residence;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.util.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Commands for managing the members of a Residence.
 *
 * @author Michael Hohl
 */
public class ResidenceMemberCommands {
    @Command(
            aliases = {"add"},
            desc = "Adds a member to the residence",
            usage = "<player>",
            min = 1,
            max = 1
    )
    @CommandPermissions({"myresidence.residence.member"})
    public static void add(final CommandContext args,
                           final MyResidence plugin,
                           final Nation nation,
                           final Player player,
                           final Session session)
            throws MyResidenceException {
        Residence selectedResidence = session.getSelectedResidence();
        Inhabitant inhabitantToAdd = nation.getInhabitant(args.getString(0));

        if (inhabitantToAdd == null) {
            throw new PlayerNotFoundException();
        }

        nation.addMember(selectedResidence, inhabitantToAdd);

        player.sendMessage(ChatColor.DARK_GREEN + "Member added!");
    }

    @Command(
            aliases = {"remove"},
            desc = "Removes a member of the residence",
            usage = "<player>",
            min = 1,
            max = 1
    )
    @CommandPermissions({"myresidence.residence.member"})
    public static void remove(final CommandContext args,
                              final MyResidence plugin,
                              final Nation nation,
                              final Player player,
                              final Session session)
            throws MyResidenceException {
        Residence selectedResidence = session.getSelectedResidence();
        Inhabitant inhabitantToAdd = nation.getInhabitant(args.getString(0));

        if (inhabitantToAdd == null) {
            throw new PlayerNotFoundException();
        }

        nation.removeMember(selectedResidence, inhabitantToAdd);

        player.sendMessage(ChatColor.DARK_GREEN + "Member removed!");
    }

    @Command(
            aliases = {"list"},
            desc = "Lists all members of the selected residence.",
            max = 0
    )
    public static void list(final CommandContext args,
                            final MyResidence plugin,
                            final Nation nation,
                            final Player player,
                            final Session session)
            throws MyResidenceException {
        Residence selectedResidence = session.getSelectedResidence();
        List<Inhabitant> members = nation.getMembers(selectedResidence);

        player.sendMessage(ChatColor.DARK_GREEN + "Members of the residence '" +
                ChatColor.GREEN + selectedResidence.getName() +
                ChatColor.DARK_GREEN + "': " +
                ChatColor.GREEN + StringUtil.joinString(members, ", ", 0));
    }
}
