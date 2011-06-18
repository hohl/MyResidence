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
import at.co.hohl.myresidence.exceptions.ResidenceSignMissingException;
import at.co.hohl.myresidence.storage.persistent.Residence;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;

/**
 * Listens to the block break event, if the player broke a block which is a residence sign.
 *
 * @author Michael Hohl
 */
public class SignBrokeListener extends BlockListener {
    private final MyResidence plugin;

    private final Nation nation;

    /**
     * Creates a new sing broke listener.
     *
     * @param plugin the plugin which holds the instance.
     * @param nation the nation.
     */
    public SignBrokeListener(MyResidence plugin, Nation nation) {
        this.nation = nation;
        this.plugin = plugin;
    }

    /**
     * Called when the player broken a block.
     *
     * @param event the event itself.
     */
    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled() || !(event.getBlock().getType().equals(Material.SIGN_POST)
                || event.getBlock().getType().equals(Material.WALL_SIGN))) {
            return;
        }

        final Sign clickedSign = (Sign) event.getBlock().getState();
        if (!plugin.getConfiguration(event.getBlock().getWorld()).getSignTitle().equals(clickedSign.getLine(0))) {
            return;
        }

        final Residence residence = nation.getResidence(clickedSign);
        if (residence == null) {
            return;
        }

        event.getPlayer().sendMessage(ChatColor.RED + "You can not destroy a residence sign!");
        event.setCancelled(true);

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    public void run() {
                        try {
                            nation.updateResidenceSign(residence);
                        } catch (ResidenceSignMissingException e) {
                            throw new RuntimeException("Residence sign missing after on sign break.", e);
                        }
                    }
                }, 20);
    }
}
