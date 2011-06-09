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
import at.co.hohl.myresidence.storage.Session;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Commands for confirming or canceling tasks.
 *
 * @author Michael Hohl
 */
public class TaskCommands {
    @Command(
            aliases = {"confirm", "y"},
            desc = "Confirms a task",
            max = 0
    )
    public static void confirm(CommandContext args, MyResidence plugin, Player player, Session session) {
        if (session.getTaskActivator() == Session.Activator.CONFIRM_COMMAND) {
            session.getTask().run();
            session.setTaskActivator(null);
        } else {
            player.sendMessage(ChatColor.RED + "There is nothing to confirm!");
        }
    }

    @Command(
            aliases = {"cancel", "x", "n"},
            desc = "Cancels a task",
            max = 0
    )
    public static void cancel(CommandContext args, MyResidence plugin, Player player, Session session) {
        if (session.getTaskActivator() != null) {
            session.setTaskActivator(null);

            player.sendMessage(ChatColor.DARK_GREEN + "Task canceled!");
        } else {
            player.sendMessage(ChatColor.RED + "There is nothing to cancel!");
        }
    }
}
