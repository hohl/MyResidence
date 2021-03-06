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

import at.co.hohl.myresidence.MyResidence;
import at.co.hohl.myresidence.Nation;
import at.co.hohl.myresidence.exceptions.MyResidenceException;
import at.co.hohl.myresidence.storage.Session;
import at.co.hohl.myresidence.storage.persistent.Residence;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;


/**
 * Listener for listening if player clicked a sign.
 *
 * @author Michael Hohl
 */
public class SignClickListener extends PlayerListener {
  /**
   * Plugin which holds the instance.
   */
  private final MyResidence plugin;

  /**
   * The nation which holds all the towns and residences.
   */
  private final Nation nation;

  /**
   * Creates a new SignClickListener.
   *
   * @param plugin the plugin which holds the instance.
   * @param nation the nation which holds all the towns and residences.
   */
  public SignClickListener(MyResidence plugin, Nation nation) {
    this.plugin = plugin;
    this.nation = nation;
  }

  /**
   * Called when player interacts with the world.
   *
   * @param event event occurred itself.
   */
  @Override
  public void onPlayerInteract(PlayerInteractEvent event) {
    if (event.isCancelled() || !(event.getClickedBlock().getType().equals(Material.SIGN_POST)
            || event.getClickedBlock().getType().equals(Material.WALL_SIGN))) {
      return;
    }

    Sign clickedSign = (Sign) event.getClickedBlock().getState();
    Session playerSession = plugin.getSessionManager().get(event.getPlayer());
    playerSession.setSelectedSignBlock(event.getClickedBlock());

    if (Session.Activator.SELECT_SIGN.equals(playerSession.getTaskActivator())) {
      playerSession.getTask().run();
      playerSession.setTaskActivator(null);
    } else if (clickedSign.getLine(0).equals(plugin.getConfiguration(clickedSign.getWorld()).getSignTitle())) {
      Residence residence = nation.getResidence(clickedSign);

      try {
        nation.sendInformation(event.getPlayer(), residence);
      } catch (MyResidenceException e) {
        Location location = event.getClickedBlock().getLocation();
        plugin.severe("Invalid residence sign at: [%s: %d, %d, %d]",
                location.getWorld().getName(), location.getBlockX(), location.getBlockY(),
                location.getBlockZ());
        e.printStackTrace();
      }
    } else {
      playerSession.setSelectedSignBlock(null);
    }
  }
}
