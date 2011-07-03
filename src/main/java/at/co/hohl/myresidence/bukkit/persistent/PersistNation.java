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

package at.co.hohl.myresidence.bukkit.persistent;

import at.co.hohl.myresidence.*;
import at.co.hohl.myresidence.exceptions.MyResidenceException;
import at.co.hohl.myresidence.storage.persistent.*;
import com.avaje.ebean.EbeanServer;
import com.sk89q.util.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MyResidence Nation implementation for Bukkit.
 *
 * @author Michael Hohl
 */
public class PersistNation implements Nation {
    /** The MyResidencePlugin which holds the Nation. */
    protected final MyResidence plugin;

    /** ChunkManager used by PersistNation. */
    private final ChunkManager chunkManager = new PersistChunkManager(this);

    /**
     * Creates a new Nation for the passed plugin.
     *
     * @param plugin the plugin to create the nation.
     */
    public PersistNation(MyResidence plugin) {
        this.plugin = plugin;
    }

    /**
     * Sends the passed player information about the passed object.
     *
     * @param object object to retrieve information. Could be a Residence or a Town.
     */
    public void sendInformation(Player player, Object object) throws MyResidenceException {
        if (object instanceof Residence) {
            Residence residence = (Residence) object;
            ResidenceManager manager = getResidenceManager(residence);

            player.sendMessage(ChatColor.LIGHT_PURPLE + "= = = ABOUT RESIDENCE = = =");

            // Send name
            player.sendMessage(ChatColor.GRAY + "Name: " + ChatColor.WHITE + residence.getName());

            // Retrieve and send owner...
            String owner = "NOBODY";
            if (residence.getOwnerId() != -1) {
                owner = getInhabitant(residence.getOwnerId()).toString();
            }
            player.sendMessage(ChatColor.GRAY + "Owner: " + ChatColor.WHITE + owner);

            // Retrieve and send town...
            String town = "ANY (wildness)";
            if (residence.getTownId() != -1) {
                Town townData = getTown(residence.getTownId());
                town = townData.getName() +
                        " (Major: " + StringUtil.joinString(getTownManager(townData).getMajors(), ", ", 0) + ")";
            }
            player.sendMessage(ChatColor.GRAY + "Town: " + ChatColor.WHITE + town);

            // Retrieve and send area...
            player.sendMessage(ChatColor.GRAY + "Size: " + ChatColor.WHITE + manager.getArea().getArea());

            // Retrieve flags
            List<ResidenceFlag.Type> flags = manager.getFlags();
            if (flags.size() > 0) {
                player.sendMessage(
                        ChatColor.GRAY + "Flags: " + ChatColor.WHITE + StringUtil.joinString(flags, ", ", 0));
            }

            // Retrieve members
            List<Inhabitant> members = manager.getMembers();
            if (members.size() > 0) {
                player.sendMessage(
                        ChatColor.GRAY + "Members: " + ChatColor.WHITE + StringUtil.joinString(members, ", ", 0));
            }

            // Retrieve and send money values.
            player.sendMessage(ChatColor.GRAY + "Value: " + plugin.format(residence.getValue()));
            if (residence.isForSale()) {
                player.sendMessage(ChatColor.YELLOW + "RESIDENCE FOR SALE!");
                player.sendMessage(ChatColor.YELLOW + "Price: " + plugin.format(residence.getPrice()));
            }
        } else if (object instanceof Town) {
            Town town = (Town) object;
            TownManager manager = getTownManager(town);

            player.sendMessage(ChatColor.LIGHT_PURPLE + "= = = ABOUT TOWN = = =");

            // Send name
            player.sendMessage(ChatColor.GRAY + "Name: " + ChatColor.WHITE + town.getName());

            // Retrieve and send major
            player.sendMessage(ChatColor.GRAY + "Major: " +
                    ChatColor.WHITE + StringUtil.joinString(manager.getMajors(), " ,", 0));

            // Retrieve residences
            List<Residence> residences = manager.getResidences();
            player.sendMessage(ChatColor.GRAY + "Residences: " + ChatColor.WHITE + residences.size());

            // Retrieve value
            double value = 0;
            for (Residence residence : residences) {
                value += residence.getValue();
            }
            player.sendMessage(ChatColor.GRAY + "Value: " + ChatColor.WHITE + plugin.format(value));

            // Retrieve and send money values.
            player.sendMessage(ChatColor.GRAY + "Money: " + ChatColor.WHITE + plugin.format(town.getMoney()));

            // Retrieve flags
            List<TownFlag.Type> flags = manager.getFlags();
            if (flags.size() > 0) {
                player.sendMessage(
                        ChatColor.GRAY + "Flags: " + ChatColor.WHITE + StringUtil.joinString(flags, ", ", 0));
            }

            // Retrieve members
            List<String> rules = getRuleManager(town).getRules();
            if (rules.size() > 0) {
                player.sendMessage(ChatColor.GRAY + "Rules:");
                for (String line : rules) {
                    player.sendMessage(" " + line);
                }
            }
        } else {
            throw new MyResidenceException("Can't retrieve information about that object!");
        }
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
     * Find a residence by name owned by the passed residence.
     *
     * @param inhabitant the inhabitant which owns the residence to look for.
     * @param search     a part of the name to search.
     * @return the residence found.
     */
    public List<Residence> findResidence(Inhabitant inhabitant, String search) {
        return getDatabase().find(Residence.class).where()
                .eq("ownerId", inhabitant.getId())
                .like("name", "%" + search + "%")
                .findList();
    }

    /**
     * Find a residence by name.
     *
     * @param search a part of the name to search.
     * @return the residence found.
     */
    public List<Residence> findResidence(String search) {
        return getDatabase().find(Residence.class)
                .where().like("name", "%" + search + "%")
                .findList();
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
    public Inhabitant getInhabitant(String name) {
        Inhabitant player = getDatabase().find(Inhabitant.class).where().ilike("name", "%" + name + "%").findUnique();

        if (player == null) {
            player = new Inhabitant();
            player.setName(name);
            getDatabase().save(player);

            plugin.info("Created database entry for player %s.", name);
        }

        return player;
    }

    /**
     * Returns the player with the passed id.
     *
     * @param id the id of the player to look for.
     * @return the founded player or null.
     */
    public Inhabitant getInhabitant(int id) {
        return getDatabase().find(Inhabitant.class).where().idEq(id).findUnique();
    }

    /**
     * Sets a new major.
     *
     * @param town       the town where the new major should be set.
     * @param inhabitant the inhabitant to become the major.
     * @param co         there could be multiple co major, which has the same rights, but not mentioned as the major.
     */
    public void setMajor(Town town, Inhabitant inhabitant, boolean co) {
        Major major = new Major();
        major.setHidden(co);
        major.setInhabitantId(inhabitant.getId());
        major.setTownId(town.getId());

        if (!co) {
            Major oldMajor = getDatabase().find(Major.class).where()
                    .eq("townId", town.getId())
                    .eq("hidden", false)
                    .findUnique();

            if (oldMajor != null) {
                getDatabase().delete(oldMajor);
            }
        }

        getDatabase().save(major);
    }

    /**
     * Checks if the passed player is a major of the town.
     *
     * @param town       the town where to check if the player is major.
     * @param inhabitant the inhabitant to check if he is major.
     * @return true, if the inhabitant is a major.
     */
    public boolean isMajor(Town town, Inhabitant inhabitant) {
        return getDatabase().find(Major.class)
                .where()
                .eq("townId", town.getId())
                .eq("inhabitantId", inhabitant.getId())
                .findRowCount() > 0;
    }

    /** @return manager for the chunks. */
    public ChunkManager getChunkManager() {
        return chunkManager;
    }

    /**
     * Returns a manager for the rules of the town.
     *
     * @param town the town to manage.
     * @return the rule manager for the town.
     */
    public RuleManager getRuleManager(Town town) {
        return new PersistRuleManager(this, town);
    }

    /**
     * Returns a manager for the residence.
     *
     * @param residence the residence.
     * @return the manager for the residence.
     */
    public ResidenceManager getResidenceManager(Residence residence) {
        return new PersistResidenceManager(plugin, this, residence);
    }

    /**
     * Returns a manager for the town.
     *
     * @param town the manager for the town.
     * @return the town.
     */
    public TownManager getTownManager(Town town) {
        return new PersistTownManager(this, town);
    }

    /**
     * Returns a manager for the flags of the residence.
     *
     * @param residence the residence to manage.
     * @return the manager for the flags.
     */
    public FlagManager<ResidenceFlag.Type> getFlagManager(Residence residence) {
        return new PersistResidenceFlagManager(this, residence);
    }

    /**
     * Returns a manager for the flags of the town.
     *
     * @param town the town to manage.
     * @return the manager for the flags.
     */
    public FlagManager<TownFlag.Type> getFlagManager(Town town) {
        return new PersistTownFlagManager(this, town);
    }

    /**
     * Saves any changes to towns or residences.
     *
     * @param object the object of the town or residence to save.
     */
    public void save(Object object) {
        plugin.getDatabase().save(object);
    }

    /** @return the database which holds all information about towns and residences. */
    public EbeanServer getDatabase() {
        return plugin.getDatabase();
    }
}
