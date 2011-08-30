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

package at.co.hohl.myresidence.bukkit.listener;

import at.co.hohl.mcutils.chat.Chat;
import at.co.hohl.myresidence.MyResidence;
import at.co.hohl.myresidence.Nation;
import at.co.hohl.myresidence.event.ResidenceLikedEvent;
import at.co.hohl.myresidence.event.ResidenceListener;
import at.co.hohl.myresidence.translations.Translate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * ResidenceListener used to inform the player on changes.
 *
 * @author Michael Hohl
 */
public class NotifyPlayerListener extends ResidenceListener {
  private final Nation nation;

  private final MyResidence plugin;

  /**
   * Creates a new listener, which notifies the player.
   *
   * @param nation the nation.
   * @param plugin the plugin.
   */
  public NotifyPlayerListener(Nation nation, MyResidence plugin) {
    this.nation = nation;
    this.plugin = plugin;
  }

  /**
   * Called when a residence received a like.
   *
   * @param event the event itself.
   */
  @Override
  public void onResidenceLiked(ResidenceLikedEvent event) {
    Player owner = Bukkit.getServer().getPlayer(nation.getInhabitant(event.getResidence().getOwnerId()).getName());

    if (owner != null) {
      Chat.sendMessage(owner, Translate.get("liked_your_residence"), event.getLikedBy().getName());
    }
  }
}
