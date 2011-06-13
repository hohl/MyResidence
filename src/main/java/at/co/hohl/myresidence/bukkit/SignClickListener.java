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

package at.co.hohl.myresidence.bukkit;

import at.co.hohl.myresidence.MyResidence;
import at.co.hohl.myresidence.Nation;
import at.co.hohl.myresidence.exceptions.MyResidenceException;
import at.co.hohl.myresidence.storage.Session;
import at.co.hohl.myresidence.storage.persistent.Residence;
import org.bukkit.block.Sign;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listener for listening if player clicked a sign.
 *
 * @author Michael Hohl
 */
public class SignClickListener extends PlayerListener {
    /** Plugin which holds the instance. */
    private final MyResidence plugin;

    /** The nation which holds all the towns and residences. */
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
        if ((event.isCancelled()) || (event.getAction() != Action.LEFT_CLICK_BLOCK) ||
                !(event.getClickedBlock().getState() instanceof Sign)) {
            return;
        }

        Session playerSession = plugin.getSessionManager().get(event.getPlayer());
        Sign sign = (Sign) event.getClickedBlock().getState();
        playerSession.setSelectedSign(sign);

        if (Session.Activator.SELECT_SIGN.equals(playerSession.getTaskActivator())) {
            playerSession.getTask().run();
            playerSession.setTaskActivator(null);
        } else if (sign.getLine(0).equals(plugin.getConfiguration(sign.getWorld()).getSignTitle())) {
            Residence residence = plugin.getNation().getResidence(sign);
            try {
                nation.sendInformation(event.getPlayer(), residence);
            } catch (MyResidenceException e) {
                throw new RuntimeException(e);
            }
        } else {
            playerSession.setSelectedSign(null);
        }
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Session playerSession = plugin.getSessionManager().get(event.getPlayer());
        playerSession.setSelectedSign(null);
    }

    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getSessionManager().close(event.getPlayer());
        plugin.info("Removed session of %s.", event.getPlayer());
    }
}
