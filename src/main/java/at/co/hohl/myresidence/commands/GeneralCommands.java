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
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.NestedCommand;
import org.bukkit.entity.Player;

/**
 * Base node for all commands.
 *
 * @author Michael Hohl
 */
public class GeneralCommands {
    @Command(
            aliases = {"res", "residence"},
            desc = "Commands to manage residences"
    )
    @NestedCommand({ResidenceCommands.class})
    public static void residence(final CommandContext args,
                                 final MyResidence plugin,
                                 final Nation nation,
                                 final Player player,
                                 final Session session) {
    }

    @Command(
            aliases = {"town"},
            desc = "Commands to manage towns"
    )
    @NestedCommand({TownCommands.class})
    public static void town(final CommandContext args,
                            final MyResidence plugin,
                            final Nation nation,
                            final Player player,
                            final Session session) {
    }

    @Command(
            aliases = {"task"},
            desc = "Confirms or cancels tasks"
    )
    @NestedCommand({TasksCommands.class})
    public static void tasks(final CommandContext args,
                             final MyResidence plugin,
                             final Nation nation,
                             final Player player,
                             final Session session) {
    }

    @Command(
            aliases = {"myres", "myresidence"},
            desc = "Manages the MyResidence plugin"
    )
    @NestedCommand({PluginCommands.class})
    public static void plugin(final CommandContext args,
                              final MyResidence plugin,
                              final Nation nation,
                              final Player player,
                              final Session session) {
    }
}
