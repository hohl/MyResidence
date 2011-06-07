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
import at.co.hohl.myresidence.storage.persistent.Residence;
import at.co.hohl.myresidence.storage.persistent.ResidenceArea;
import com.nijikokun.register.payment.Method;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Command for managing residences.
 *
 * @author Michael Hohl
 */
public class ResidenceCommands {
    @Command(
            aliases = {"create"},
            usage = "<name>",
            desc = "Creates a new residence with passed name",
            min = 1,
            flags = "w"
    )
    @CommandPermissions({"myresidence.residence.create"})
    public static void create(CommandContext args, MyResidence plugin, Player player, Session session)
            throws IncompleteRegionException {
        WorldEditPlugin worldEdit = plugin.getWorldEdit();
        Selection selection = worldEdit.getSelection(player);
        String residenceName = args.getJoinedStrings(0);

        // Can not make a Residence without a selection.
        if (selection == null) {
            throw new IncompleteRegionException();
        }

        ResidenceArea area = new ResidenceArea(selection);
        plugin.getDatabase().save(area);

        Residence residence = new Residence();
        residence.setName(residenceName);
        residence.setArea(area);
        plugin.getDatabase().save(residence);

        player.sendMessage(ChatColor.DARK_GREEN + "Residence " + residenceName + " created!");
    }

    @Command(
            aliases = {"buy"},
            desc = "Buys the residence",
            max = 0
    )
    @CommandPermissions({"myresidence.residence.buy"})
    public static void buy(CommandContext args, MyResidence plugin, Player player, Session session) {
    }

    @Command(
            aliases = {"sell"},
            usage = "<price>",
            desc = "Sells your residence",
            min = 1,
            max = 1,
            flags = "r"
    )
    @CommandPermissions({"myresidence.residence.sell"})
    public static void sell(CommandContext args, MyResidence plugin, Player player, Session session) {
    }

    @Command(
            aliases = {"info", "i"},
            desc = "Returns information about residence",
            max = 0
    )
    @CommandPermissions({"myresidence.residence.info"})
    public static void info(CommandContext args, MyResidence plugin, Player player, Session session) {
        Residence residence = plugin.getResidence(player.getLocation());
        Method payment = plugin.getMethods().getMethod();

        if (residence != null) {
            player.sendMessage(ChatColor.LIGHT_PURPLE + "= = = ABOUT RESIDENCE = = =");
            player.sendMessage(ChatColor.GRAY + "Owner: " + ChatColor.WHITE + residence.getOwner());
            player.sendMessage(ChatColor.GRAY + "Town: " + ChatColor.WHITE + residence.getTown());
            player.sendMessage(ChatColor.GRAY + "Value: " + ChatColor.WHITE + payment.format(residence.getValue()));
            player.sendMessage(ChatColor.GRAY + "Size:" + ChatColor.WHITE + residence.getArea());
            if (residence.isForSale()) {
                player.sendMessage(ChatColor.YELLOW + "RESIDENCE FOR SALE!");
                player.sendMessage(ChatColor.YELLOW + "Price: " + payment.format(residence.getValue()));
            }
        } else {
            player.sendMessage(ChatColor.RED + "You are not inside a Residence!");
        }
    }

    @Command(
            aliases = {"list"},
            usage = "[search]",
            desc = "List residences you own. (-s = only residences for sell)",
            max = 0,
            flags = "snp"
    )
    @CommandPermissions({"myresidence.residence.list"})
    public static void list(CommandContext args, MyResidence plugin, Player player, Session session) {
    }
}
