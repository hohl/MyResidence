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

import at.co.hohl.myresidence.storage.Session;
import at.co.hohl.myresidence.storage.persistent.Residence;
import org.bukkit.block.Sign;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

/**
 * Listener for listening if player clicked a sign.
 *
 * @author Michael Hohl
 */
public class SignClickListener extends PlayerListener {
    /** Plugin which holds the instance. */
    private final MyResidenceAPI plugin;

    /**
     * Creates a new SignClickListener.
     *
     * @param plugin the plugin which holds the instance.
     */
    public SignClickListener(MyResidenceAPI plugin) {
        this.plugin = plugin;
    }

    /**
     * Called when player interacts with the world.
     *
     * @param event event occurred itself.
     */
    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!(event.getClickedBlock().getState() instanceof Sign)) {
            return;
        }

        Session playerSession = plugin.getSession(event.getPlayer());
        Sign sign = (Sign) event.getClickedBlock().getState();
        playerSession.setSelectedSign(sign);

        if (Session.Activator.SELECT_SIGN.equals(playerSession.getTaskActivator())) {
            playerSession.getTask().run();
            playerSession.setTaskActivator(null);
        } else if (sign.getLine(0).equals("[" + plugin.getConfiguration().getString("sign.title", "Residence") + "]")) {
            Residence residence = plugin.getResidence(sign);
            residence.sendInformation(plugin, event.getPlayer());
        } else {
            playerSession.setSelectedSign(null);
        }

        // ToDo: Implement a auto remove selection (sign) after some time.
        // ToDo: Implement a auto remove session after some time.
    }
}
