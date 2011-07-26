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
import at.co.hohl.myresidence.ResidenceManager;
import at.co.hohl.myresidence.event.ResidenceChangedEvent;
import at.co.hohl.myresidence.event.ResidenceLikedEvent;
import at.co.hohl.myresidence.exceptions.MyResidenceException;
import at.co.hohl.myresidence.storage.Session;
import at.co.hohl.myresidence.storage.persistent.Inhabitant;
import at.co.hohl.myresidence.storage.persistent.Residence;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import org.bukkit.entity.Player;

/**
 * Commands to manage residence likes.
 *
 * @author Michael Hohl
 */
public class LikeCommands {
  @Command(
          aliases = {"like"},
          desc = "Likes the selected or passed residence",
          usage = "[residence]"
  )
  @CommandPermissions({"myresidence.like"})
  public static void like(final CommandContext args,
                          final MyResidence plugin,
                          final Nation nation,
                          final Player player,
                          final Session session)
          throws MyResidenceException {
    Residence residence;

    if (args.argsLength() == 0) {
      residence = session.getSelectedResidence();
    } else {
      residence = nation.getResidence(args.getJoinedStrings(0));
    }

    Inhabitant playerInhabitant = nation.getInhabitant(session.getPlayerId());
    ResidenceManager manager = nation.getResidenceManager(residence);

    manager.like(playerInhabitant);

    Chat.sendMessage(player, "&2You liked the residence.");

    plugin.getEventManager().callEvent(new ResidenceLikedEvent(session, residence));
  }

  @Command(
          aliases = {"unlike"},
          desc = "Unlikes the selected or passed residence",
          usage = "[residence]"
  )
  @CommandPermissions({"myresidence.like"})
  public static void unlike(final CommandContext args,
                            final MyResidence plugin,
                            final Nation nation,
                            final Player player,
                            final Session session)
          throws MyResidenceException {
    Residence residence;

    if (args.argsLength() == 0) {
      residence = session.getSelectedResidence();
    } else {
      residence = nation.getResidence(args.getJoinedStrings(0));
    }

    Inhabitant playerInhabitant = nation.getInhabitant(session.getPlayerId());
    ResidenceManager manager = nation.getResidenceManager(residence);

    manager.unlike(playerInhabitant);

    Chat.sendMessage(player, "&2You do not like the residence anymore.");

    plugin.getEventManager().callEvent(new ResidenceChangedEvent(session, residence));
  }
}
