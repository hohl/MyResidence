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
import at.co.hohl.myresidence.exceptions.ResidenceSignMissingException;
import at.co.hohl.myresidence.storage.Session;
import at.co.hohl.myresidence.storage.persistent.*;
import com.nijikokun.register.payment.Method;
import com.nijikokun.register.payment.Methods;
import com.sk89q.bukkit.migration.PermissionsResolver;
import com.sk89q.util.StringUtil;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * API implementation of MyResidence.
 *
 * @author Michael Hohl
 */
public abstract class MyResidenceAPI extends JavaPlugin implements MyResidence {
    /** Logger used by this plugin. */
    protected Logger logger;

    /** Payment methods. */
    protected Methods methods;

    /** WorldEdit plugin. */
    private WorldEditPlugin worldEdit;

    /** Session Map used by this player. */
    private Map<Player, Session> sessionMap = new HashMap<Player, Session>();

    /**
     * Updates the sign linked to passed Residence.
     *
     * @param residence Residence to update.
     */
    public void updateResidenceSign(Residence residence) throws ResidenceSignMissingException {
        Method payment = getMethods().getMethod();

        ResidenceSign residenceSign = getDatabase().find(ResidenceSign.class)
                .where()
                .eq("residenceId", residence.getId())
                .findUnique();

        World world = getServer().getWorld(residenceSign.getWorld());
        Block signBlock = world.getBlockAt(residenceSign.getX(), residenceSign.getY(), residenceSign.getZ());

        if (!(signBlock.getState() instanceof Sign)) {
            throw new ResidenceSignMissingException(residence);
        }

        Sign sign = (Sign) signBlock.getState();
        sign.setLine(0, "[" + getConfiguration().getString("sign.title", "Residence") + "]");
        sign.setLine(1, StringUtil.trimLength(residence.getName(), 16));
        if (residence.isForSale()) {
            sign.setLine(2, ChatColor.YELLOW +
                    StringUtil.trimLength(getConfiguration().getString("sign.saletext", "FOR SALE!"), 14));
            if (payment == null) {
                sign.setLine(3,
                        ChatColor.YELLOW + StringUtil.trimLength(String.format("%.2f", residence.getPrice()), 14));
            } else {
                sign.setLine(3, ChatColor.YELLOW + StringUtil.trimLength(payment.format(residence.getPrice()), 14));
            }
        } else {
            sign.setLine(2, getOwner(residence).getName());
            sign.setLine(3, "");
        }
        sign.update();
    }

    /**
     * Returns the residence at the passed location
     *
     * @param location the location to look for.
     * @return the founded residence or null.
     */
    public Residence getResidence(Location location) {
        ResidenceArea residenceArea = getDatabase().find(ResidenceArea.class).where()
                .ieq("world", location.getWorld().getName())
                .le("lowX", location.getBlockX())
                .le("lowY", location.getBlockY())
                .le("lowZ", location.getBlockZ())
                .ge("highX", location.getBlockX())
                .ge("highY", location.getBlockY())
                .ge("highZ", location.getBlockZ())
                .findUnique();

        if (residenceArea != null) {
            return getResidence(residenceArea.getResidenceId());
        } else {
            return null;
        }
    }

    /**
     * Returns the residence with the passed id.
     *
     * @param id the id of the residence to look for.
     * @return the founded residence or null.
     */
    public Residence getResidence(int id) {
        return getDatabase().find(Residence.class).where().idEq(id).findUnique();
    }

    /**
     * Returns the residence with the passed name.
     *
     * @param name the name to look for.
     * @return the founded residence or null.
     */
    public Residence getResidence(String name) {
        return getDatabase().find(Residence.class).where().ieq("name", name).findUnique();
    }

    /**
     * Returns the residence by the passed sign.
     *
     * @param sign the sign to look for.
     * @return the founded residence or null.
     */
    public Residence getResidence(Sign sign) {
        Location blockLocation = sign.getBlock().getLocation();

        Map<String, Object> locationArgs = new HashMap<String, Object>();
        locationArgs.put("x", blockLocation.getBlockX());
        locationArgs.put("y", blockLocation.getBlockY());
        locationArgs.put("z", blockLocation.getBlockZ());
        locationArgs.put("world", blockLocation.getWorld().getName());

        ResidenceSign residenceSign = getDatabase().find(ResidenceSign.class).where().allEq(locationArgs).findUnique();

        if (residenceSign != null) {
            return getDatabase().find(Residence.class).where().idEq(residenceSign.getResidenceId()).findUnique();
        } else {
            return null;
        }
    }

    /**
     * Returns the area of the Residence
     *
     * @param id the id of the Residence.
     * @return the area of the Residence with the passed id.
     */
    public ResidenceArea getResidenceArea(int id) {
        return getDatabase().find(ResidenceArea.class).where().idEq(id).findUnique();
    }

    /**
     * Returns the area of the Residence.
     *
     * @param residence the Residence.
     * @return the area of the Residence.
     */
    public ResidenceArea getResidenceArea(Residence residence) {
        return getResidenceArea(residence.getId());
    }

    /**
     * Returns the sign of the Residence.
     *
     * @param id the id of the Residence.
     * @return the sign of the Residence with the passed id.
     */
    public ResidenceSign getResidenceSign(int id) {
        return getDatabase().find(ResidenceSign.class).where().idEq(id).findUnique();
    }

    /**
     * Returns the sign of the Residence
     *
     * @param residence the Residence.
     * @return the sign of the passed Residence.
     */
    public ResidenceSign getResidenceSign(Residence residence) {
        return getResidenceSign(residence.getId());
    }

    /**
     * Returns the town with the passed id.
     *
     * @param id the id of the town to look for.
     * @return the founded town or null.
     */
    public Town getTown(int id) {
        return getDatabase().find(Town.class).where().idEq(id).findUnique();
    }

    /**
     * Returns the town with the passed name.
     *
     * @param name the name to look for.
     * @return the founded town or null.
     */
    public Town getTown(String name) {
        return getDatabase().find(Town.class).where().ieq("name", name).findUnique();
    }

    /**
     * Returns the town at the passed location.
     *
     * @param location the location to look for.
     * @return the founded town or null.
     */
    public Town getTown(Location location) {
        Map<String, Object> chunkArgs = new HashMap<String, Object>();
        chunkArgs.put("x", location.getBlock().getChunk().getX());
        chunkArgs.put("z", location.getBlock().getChunk().getZ());
        chunkArgs.put("world", location.getWorld().getName());

        TownChunk currentChunk = getDatabase().find(TownChunk.class).where().allEq(chunkArgs).findUnique();

        if (currentChunk != null) {
            return getDatabase().find(Town.class).where().idEq(currentChunk.getTownId()).findUnique();
        } else {
            return null;
        }
    }

    /**
     * Returns the player data for the passed name.
     *
     * @param name the name to look for.
     * @return the founded player or null.
     */
    public PlayerData getPlayer(String name) {
        PlayerData player = getDatabase().find(PlayerData.class).where().ieq("name", name).findUnique();

        if (player == null) {
            player = new PlayerData();
            player.setName(name);
            getDatabase().save(player);

            info("created database entry for player %s.", name);
        }

        return player;
    }

    /**
     * Returns the player with the passed id.
     *
     * @param id the id of the player to look for.
     * @return the founded player or null.
     */
    public PlayerData getPlayer(int id) {
        return getDatabase().find(PlayerData.class).where().idEq(id).findUnique();
    }

    /**
     * Returns the major of the passed Town.
     *
     * @param town the Town.
     * @return PlayerData of the major.
     */
    public PlayerData getMajor(Town town) {
        return getPlayer(town.getMajorId());
    }

    /**
     * Returns the PlayerData of the owner of the passed Residence.
     *
     * @param residence the Residence.
     * @return PlayerData of the owner.
     */
    public PlayerData getOwner(Residence residence) {
        return getPlayer(residence.getOwnerId());
    }

    /**
     * Returns the session for the passed player.
     *
     * @param player the player to look for the session.
     * @return the found or create session.
     */
    public Session getSession(Player player) {
        if (!sessionMap.containsKey(player)) {
            sessionMap.put(player, new Session(this, player));

            info("session created for %s.", player.getName());
        }

        return sessionMap.get(player);
    }

    /** @return all available payment methods. */
    public Methods getMethods() {
        return methods;
    }

    /** @return handler for the permissions. */
    public PermissionsResolver getPermissionsResolver() {
        return worldEdit.getPermissionsResolver();
    }

    /** @return world edit plugin. */
    public WorldEditPlugin getWorldEdit() {
        return worldEdit;
    }

    public void setWorldEdit(WorldEditPlugin worldEdit) {
        this.worldEdit = worldEdit;
    }

    /**
     * Formats the passed amount of money to a localized string.
     *
     * @param money the amount of money.
     * @return a string for the amount of money.
     */
    public String format(double money) {
        Method payment = getMethods().getMethod();

        if (payment == null) {
            return String.format("%.2f", money);
        } else {
            return payment.format(money);
        }
    }

    /**
     * Logs an message with the level info.
     *
     * @param message the message to log.
     */
    public void info(String message, Object... args) {
        String formattedMessage = String.format(message, args);
        logger.info(String.format("[%s] %s", getDescription().getName(), formattedMessage));
    }

    /**
     * Logs an message with the level warning.
     *
     * @param message the message to log.
     */
    public void warning(String message, Object... args) {
        String formattedMessage = String.format(message, args);
        logger.warning(String.format("[%s] %s", getDescription().getName(), formattedMessage));
    }

    /**
     * Logs an message with the level severe.
     *
     * @param message the message to log.
     */
    public void severe(String message, Object... args) {

        String formattedMessage = String.format(message, args);
        logger.severe(String.format("[%s] %s", getDescription().getName(), formattedMessage));
    }
}
