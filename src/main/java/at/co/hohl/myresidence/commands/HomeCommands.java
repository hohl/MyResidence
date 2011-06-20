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
import at.co.hohl.myresidence.ResidenceManager;
import at.co.hohl.myresidence.exceptions.MyResidenceException;
import at.co.hohl.myresidence.exceptions.NotOwnException;
import at.co.hohl.myresidence.storage.Session;
import at.co.hohl.myresidence.storage.persistent.Residence;
import at.co.hohl.utils.Chat;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Commands for managing the home points of the residences.
 *
 * @author Michael Hohl
 */
public class HomeCommands {
    @Command(
            aliases = {"home"},
            desc = "Teleport you to your nearest home",
            max = 0
    )
    public static void home(final CommandContext args,
                            final MyResidence plugin,
                            final Nation nation,
                            final Player player,
                            final Session session) throws MyResidenceException {
        /*Inhabitant playerInhabitant = nation.getInhabitant(session.getPlayerId());
        HomePoint nearestHome = nation.getNearestHome(playerInhabitant, player.getLocation());

        if (nearestHome != null) {
            Location homeLocation = new Location(player.getWorld(),
                    nearestHome.getX(),
                    nearestHome.getY(),
                    nearestHome.getZ(),
                    nearestHome.getYaw(),
                    nearestHome.getPitch());

            player.teleport(homeLocation);

            player.sendMessage(ChatColor.DARK_GREEN + "Welcome Home!");
        } else {
            throw new MyResidenceException("You have no home in this world!");
        }*/
    }

    @Command(
            aliases = {"sethome"},
            desc = "Sets the home for your residence",
            max = 0
    )
    public static void sethome(final CommandContext args,
                               final MyResidence plugin,
                               final Nation nation,
                               final Player player,
                               final Session session) throws MyResidenceException {
        Location playerLocation = player.getLocation();
        Residence residence = nation.getResidence(player.getLocation());

        // Check if player is inside residence.
        if (residence == null) {
            throw new MyResidenceException("You are not inside a residence!");
        }

        // Check if player is the owner.
        if (session.hasResidenceOwnerRights(residence)) {
            throw new NotOwnException();
        }

        ResidenceManager manager = nation.getResidenceManager(residence);
        manager.setHome(player.getLocation());

        Chat.sendMessage(player, "&2Home set successfully for residence &a{0}&2.", residence);
    }
}
