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
import com.sk89q.minecraft.util.commands.CommandPermissions;
import org.bukkit.entity.Player;

/**
 * Command for managing towns.
 *
 * @author Michael Hohl
 */
public class TownCommands {
    @Command(
            aliases = {"create"},
            usage = "<name>",
            desc = "Creates a new town",
            min = 1,
            flags = "w"
    )
    @CommandPermissions({"myresidence.town.create"})
    public static void create(CommandContext args, MyResidence plugin, Player player, Session session) {
    }

    @Command(
            aliases = {"info", "i"},
            desc = "Returns information about residence",
            max = 0
    )
    @CommandPermissions({"myresidence.town.info"})
    public static void info(CommandContext args, MyResidence plugin, Player player, Session session) {
    }

    @Command(
            aliases = {"list"},
            desc = "List available towns",
            min = 0,
            max = 0,
            flags = "nm"
    )
    @CommandPermissions({"myresidence.town.list"})
    public static void list(CommandContext args, MyResidence plugin, Player player, Session session) {
    }

    @Command(
            aliases = {"pay"},
            usage = "<account> <amount>",
            desc = "Pays money to an economy account",
            min = 2,
            max = 2
    )
    @CommandPermissions({"myresidence.town.major"})
    public static void pay(CommandContext args, MyResidence plugin, Player player, Session session) {
    }

    @Command(
            aliases = {"grant"},
            usage = "<amount>",
            desc = "Grants money to town account",
            min = 1,
            max = 1
    )
    @CommandPermissions({"myresidence.town.major"})
    public static void grant(CommandContext args, MyResidence plugin, Player player, Session session) {
    }
}
