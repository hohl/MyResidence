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

import at.co.hohl.mcutils.chat.Chat;
import at.co.hohl.myresidence.MyResidence;
import at.co.hohl.myresidence.Nation;
import at.co.hohl.myresidence.TownManager;
import at.co.hohl.myresidence.exceptions.MyResidenceException;
import at.co.hohl.myresidence.exceptions.NoTownSelectedException;
import at.co.hohl.myresidence.exceptions.PermissionsDeniedException;
import at.co.hohl.myresidence.storage.Session;
import at.co.hohl.myresidence.storage.persistent.Inhabitant;
import at.co.hohl.myresidence.storage.persistent.Town;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import org.bukkit.entity.Player;

/**
 * Commands which allows to manage the majors of a town.
 *
 * @author Michael Hohl
 */
public class TownMajorCommands {
    @Command(
            aliases = {"add"},
            usage = "<playername>",
            desc = "Adds a major to the town",
            min = 1,
            max = 1
    )
    @CommandPermissions({"myresidence.major"})
    public static void add(final CommandContext args,
                           final MyResidence plugin,
                           final Nation nation,
                           final Player player,
                           final Session session) throws NoTownSelectedException, PermissionsDeniedException {
        Town selectedTown = session.getSelectedTown();

        if (!session.hasMajorRights(selectedTown)) {
            throw new PermissionsDeniedException("You are not the major of this town!");
        }

        TownManager manager = nation.getTownManager(selectedTown);
        Inhabitant newMajor = nation.getInhabitant(args.getString(0));
        manager.addMajor(newMajor);

        Chat.sendMessage(player, "&2{0}&a becomes major of &2{1}&a!", newMajor.getName(), selectedTown.getName());
    }

    @Command(
            aliases = {"remove"},
            usage = "<playername>",
            desc = "Removes a major to the town",
            min = 1,
            max = 1
    )
    @CommandPermissions({"myresidence.major"})
    public static void remove(final CommandContext args,
                              final MyResidence plugin,
                              final Nation nation,
                              final Player player,
                              final Session session) throws MyResidenceException, PermissionsDeniedException {
        Town selectedTown = session.getSelectedTown();

        if (!session.hasMajorRights(selectedTown)) {
            throw new PermissionsDeniedException("You are not the major of this town!");
        }

        TownManager manager = nation.getTownManager(selectedTown);
        Inhabitant majorToRemove = nation.getInhabitant(args.getString(0));

        if (!manager.isMajor(majorToRemove)) {
            throw new MyResidenceException("Major not found!");
        }

        manager.removeMajor(majorToRemove);
        Chat.sendMessage(player, "&2{0}&a is no longer major of &2{1}&a!", majorToRemove.getName(), selectedTown.getName());
    }
}
