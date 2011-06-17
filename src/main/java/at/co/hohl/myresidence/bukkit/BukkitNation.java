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

import at.co.hohl.myresidence.Nation;
import at.co.hohl.myresidence.exceptions.MyResidenceException;
import at.co.hohl.myresidence.exceptions.ResidenceSignMissingException;
import at.co.hohl.myresidence.storage.persistent.*;
import com.avaje.ebean.EbeanServer;
import com.nijikokun.register.payment.Method;
import com.sk89q.util.StringUtil;
import com.sk89q.worldedit.Vector2D;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import javax.persistence.PersistenceException;
import java.util.*;

/**
 * MyResidence Nation implementation for Bukkit.
 *
 * @author Michael Hohl
 */
public class BukkitNation implements Nation {
    /** The MyResidencePlugin which holds the Nation. */
    private final MyResidencePlugin plugin;

    /**
     * Creates a new Nation for the passed plugin.
     *
     * @param plugin the plugin to create the nation.
     */
    public BukkitNation(MyResidencePlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Updates the sign linked to passed Residence.
     *
     * @param residence Residence to update.
     */
    public void updateResidenceSign(Residence residence) throws ResidenceSignMissingException {
        Method payment = plugin.getPaymentMethods().getMethod();

        ResidenceSign residenceSign = getDatabase().find(ResidenceSign.class)
                .where()
                .eq("residenceId", residence.getId())
                .findUnique();

        World world = plugin.getServer().getWorld(residenceSign.getWorld());
        Block signBlock = world.getBlockAt(residenceSign.getX(), residenceSign.getY(), residenceSign.getZ());

        if (!(signBlock.getState() instanceof Sign)) {
            throw new ResidenceSignMissingException(residence);
        }

        Sign sign = (Sign) signBlock.getState();
        sign.setLine(0, plugin.getConfiguration(world).getSignTitle());
        sign.setLine(1, StringUtil.trimLength(residence.getName(), 16));
        if (residence.isForSale()) {
            sign.setLine(2, ChatColor.YELLOW +
                    StringUtil.trimLength(plugin.getConfiguration(world).getSignSaleText(), 14));
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
     * Sends the passed player information about the passed object.
     *
     * @param object object to retrieve information. Could be a Residence or a Town.
     */
    public void sendInformation(Player player, Object object) throws MyResidenceException {
        if (object instanceof Residence) {
            Residence residence = (Residence) object;

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
                town = townData.toString() + " (Major: " + getMajor(townData) + ")";
            }
            player.sendMessage(ChatColor.GRAY + "Town: " + ChatColor.WHITE + town);

            // Retrieve and send area...
            player.sendMessage(ChatColor.GRAY + "Size: " + ChatColor.WHITE + getResidenceArea(residence));

            // Retrieve flags
            List<ResidenceFlag.Type> flags = getFlags(residence);
            if (flags.size() > 0) {
                player.sendMessage(
                        ChatColor.GRAY + "Flags: " + ChatColor.WHITE + StringUtil.joinString(flags, ", ", 0));
            }

            // Retrieve members
            List<Inhabitant> members = getMembers(residence);
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

            player.sendMessage(ChatColor.LIGHT_PURPLE + "= = = ABOUT TOWN = = =");

            // Send name
            player.sendMessage(ChatColor.GRAY + "Name: " + ChatColor.WHITE + town.getName());

            // Retrieve and send major
            player.sendMessage(ChatColor.GRAY + "Major: " + ChatColor.WHITE + getMajor(town).getName());

            // Retrieve residences
            List<Residence> residences = getResidences(town);
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
            List<TownFlag.Type> flags = getFlags(town);
            if (flags.size() > 0) {
                player.sendMessage(
                        ChatColor.GRAY + "Flags: " + ChatColor.WHITE + StringUtil.joinString(flags, ", ", 0));
            }

            // Retrieve members
            List<String> rules = getRules(town);
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
     * Returns all residences of that town.
     *
     * @param town the town to look up the residences.
     * @return list of founded residences.
     */
    public List<Residence> getResidences(Town town) {
        return getDatabase().find(Residence.class).where().eq("townId", town.getId()).findList();
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
     * Returns the nearest HomePoint for the Inhabitant.
     *
     * @param location the location of the player.
     * @return founded HomePoint.
     */
    public HomePoint getNearestHome(Inhabitant inhabitant, Location location) {
        List<HomePoint> home = getDatabase().find(HomePoint.class).where()
                .eq("inhabitantId", inhabitant.getId())
                .eq("world", location.getWorld().getName())
                .orderBy(String.format("abs((x-%d)+(y-%d)+(z-%d)) ASC"))
                .findList();

        if (home.size() > 0) {
            return home.get(0);
        } else {
            return null;
        }
    }

    /**
     * Returns the HomePoint of the residence.
     *
     * @param residence the residence to check.
     * @return founded or created HomePoint
     */
    public HomePoint getResidenceHome(Residence residence) {
        HomePoint home = getDatabase().find(HomePoint.class).where()
                .eq("residenceId", residence.getId())
                .findUnique();

        if (home == null) {
            home = new HomePoint();
            home.setResidenceId(residence.getId());
        }

        return home;
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
     * Returns the major of the passed Town.
     *
     * @param town the Town.
     * @return PlayerData of the major.
     */
    public Inhabitant getMajor(Town town) {
        Major major = getDatabase().find(Major.class)
                .where()
                .eq("townId", town.getId())
                .eq("hidden", false)
                .findUnique();

        if (major != null) {
            return getInhabitant(major.getInhabitantId());
        } else {
            return null;
        }
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

    /**
     * Returns all flags of a residence.
     *
     * @param residence the residence to check.
     * @return all flags set for the residence.
     */
    public List<ResidenceFlag.Type> getFlags(Residence residence) {
        List<ResidenceFlag> residenceFlags = getDatabase().find(ResidenceFlag.class)
                .where()
                .eq("residenceId", residence.getId()).findList();

        List<ResidenceFlag.Type> flagTypes = new LinkedList<ResidenceFlag.Type>();
        for (ResidenceFlag flag : residenceFlags) {
            flagTypes.add(flag.getFlag());
        }

        return flagTypes;
    }

    /**
     * Returns all flags of a town.
     *
     * @param town the town to check.
     * @return the flags set for the town.
     */
    public List<TownFlag.Type> getFlags(Town town) {
        List<TownFlag> residenceFlags = getDatabase().find(TownFlag.class)
                .where()
                .eq("townId", town.getId()).findList();

        List<TownFlag.Type> flagTypes = new LinkedList<TownFlag.Type>();
        for (TownFlag flag : residenceFlags) {
            flagTypes.add(flag.getFlag());
        }

        return flagTypes;
    }

    /**
     * Checks if the passed Residence has the flag set.
     *
     * @param residence residence to check.
     * @param flag      flag to check.
     * @return true, if the flag is set.
     */
    public boolean hasFlag(Residence residence, ResidenceFlag.Type flag) {
        return getDatabase().find(ResidenceFlag.class)
                .where()
                .eq("residenceId", residence.getId())
                .eq("flag", flag)
                .findRowCount() > 0;
    }

    /**
     * Checks if the passed Town has the flag set.
     *
     * @param town town to check.
     * @param flag flag to check.
     * @return true, if the flag is set.
     */
    public boolean hasFlag(Town town, TownFlag.Type flag) {
        return getDatabase().find(TownFlag.class)
                .where()
                .eq("townId", town.getId())
                .eq("flag", flag)
                .findRowCount() > 0;
    }

    /**
     * Sets the passed flag.
     *
     * @param town town to set the flag.
     * @param flag the flag to set.
     */
    public void setFlag(Town town, TownFlag.Type flag) {
        if (!hasFlag(town, flag)) {
            TownFlag townFlag = new TownFlag();
            townFlag.setTownId(town.getId());
            townFlag.setFlag(flag);
            getDatabase().save(townFlag);
        }
    }

    /**
     * Remove the passed flag.
     *
     * @param town town to remove the flag.
     * @param flag the flag to remove.
     */
    public void removeFlag(Town town, TownFlag.Type flag) {
        getDatabase().delete(
                getDatabase().find(TownFlag.class)
                        .where()
                        .eq("townId", town.getId())
                        .eq("flag", flag).findList());
    }

    /**
     * Sets the passed flag.
     *
     * @param residence residence to set the flag.
     * @param flag      the flag to set.
     */
    public void setFlag(Residence residence, ResidenceFlag.Type flag) {
        if (!hasFlag(residence, flag)) {
            ResidenceFlag residenceFlag = new ResidenceFlag();
            residenceFlag.setResidenceId(residence.getId());
            residenceFlag.setFlag(flag);
            getDatabase().save(residenceFlag);
        }
    }

    /**
     * Removes the passed flag.
     *
     * @param residence residence to remove the flag.
     * @param flag      the flag to remove.
     */
    public void removeFlag(Residence residence, ResidenceFlag.Type flag) {
        getDatabase().delete(
                getDatabase().find(ResidenceFlag.class)
                        .where()
                        .eq("residenceId", residence.getId())
                        .eq("flag", flag).findList());
    }

    /**
     * Adds a member.
     *
     * @param residence  the residence where the inhabitant should become member.
     * @param inhabitant the inhabitant, which should become member.
     */
    public void addMember(Residence residence, Inhabitant inhabitant) {
        ResidenceMember membership = new ResidenceMember();
        membership.setInhabitantId(inhabitant.getId());
        membership.setResidenceId(residence.getId());

        getDatabase().save(membership);
    }

    /**
     * Adds a single rule.
     *
     * @param town the town the rule should be for.
     * @param rule the rule to create.
     */
    public void addRule(Town town, String rule) {
        TownRule townRule = new TownRule();
        townRule.setTownId(town.getId());
        townRule.setMessage(rule);

        getDatabase().save(townRule);
    }

    /**
     * Removes a rule, which is like the passed string.
     *
     * @param town the town, where the rule should get removed.
     * @param rule the rule message.
     */
    public void removeRule(Town town, String rule) throws MyResidenceException {
        try {
            TownRule townRule = getDatabase().find(TownRule.class).where()
                    .ilike("message", "%" + rule + "%")
                    .eq("townId", town.getId())
                    .findUnique();

            getDatabase().delete(townRule);
        } catch (PersistenceException e) {
            throw new MyResidenceException("Rule not found!");
        }
    }

    /**
     * Gets all rules for the town.
     *
     * @param town the town to look up the rules.
     */
    public List<String> getRules(Town town) {
        List<TownRule> rules = getDatabase().find(TownRule.class)
                .where()
                .eq("townId", town.getId())
                .orderBy("message ASC")
                .findList();

        List<String> ruleLines = new LinkedList<String>();
        for (TownRule rule : rules) {
            ruleLines.add(rule.getMessage());
        }

        return ruleLines;
    }

    /**
     * Removes an inhabitant from the residence membership.
     *
     * @param residence  the residence.
     * @param inhabitant the inhabitant.
     */
    public void removeMember(Residence residence, Inhabitant inhabitant) throws MyResidenceException {
        List<ResidenceMember> foundedMembers = getDatabase().find(ResidenceMember.class)
                .where()
                .eq("residenceId", residence.getId())
                .eq("inhabitantId", inhabitant.getId())
                .findList();

        if (foundedMembers.size() > 0) {
            getDatabase().delete(foundedMembers);
        } else {
            throw new MyResidenceException("Inhabitant is not a member of that residence!");
        }
    }

    /**
     * Returns all members of the residence.
     *
     * @param residence the residence.
     */
    public List<Inhabitant> getMembers(Residence residence) {
        List<ResidenceMember> foundedMembers = getDatabase().find(ResidenceMember.class)
                .where()
                .eq("residenceId", residence.getId())
                .findList();

        List<Inhabitant> inhabitants = new LinkedList<Inhabitant>();
        for (ResidenceMember member : foundedMembers) {
            inhabitants.add(getInhabitant(member.getInhabitantId()));
        }

        Collections.sort(inhabitants);

        return inhabitants;
    }

    /**
     * Checks if the inhabitant is member or owner.
     *
     * @param residence  the residence.
     * @param inhabitant the inhabitant.
     */
    public boolean isMember(Residence residence, Inhabitant inhabitant) {
        return residence.getOwnerId() == inhabitant.getId() ||
                getDatabase().find(ResidenceMember.class).where()
                        .eq("residenceId", residence.getId())
                        .eq("inhabitantId", inhabitant.getId())
                        .findRowCount() > 0;
    }

    /**
     * Checks if the inhabitant is a member of the town.
     *
     * @param town       the town check.
     * @param inhabitant the inhabitant to check.
     * @return true, if the inhabitant is a member.
     */
    public boolean isMember(Town town, Inhabitant inhabitant) {
        boolean ownsResidences = getDatabase().find(Residence.class).where()
                .eq("inhabitantId", inhabitant.getId())
                .eq("townId", town.getId())
                .findRowCount() > 0;

        return ownsResidences || isMajor(town, inhabitant);
    }

    /**
     * Adds a single chunk.
     *
     * @param town  the town to add the chunk.
     * @param chunk the chunk to add.
     */
    public void addChunk(Town town, Chunk chunk) {
        TownChunk townChunk = getDatabase().find(TownChunk.class)
                .where()
                .eq("world", chunk.getWorld().getName())
                .eq("x", chunk.getX())
                .eq("z", chunk.getZ())
                .findUnique();

        if (townChunk == null) {
            townChunk = new TownChunk();
            townChunk.setWorld(chunk.getWorld().getName());
            townChunk.setX(chunk.getX());
            townChunk.setZ(chunk.getZ());
        }

        townChunk.setTownId(town.getId());

        getDatabase().save(townChunk);
    }

    /**
     * Checks if the passed chunk is free.
     *
     * @param chunk the chunk to check.
     * @return true, if the chunk is not used by any town.
     */
    public boolean isChunkFree(Chunk chunk) {
        return getDatabase().find(TownChunk.class)
                .where()
                .eq("world", chunk.getWorld().getName())
                .eq("x", chunk.getX())
                .eq("z", chunk.getZ())
                .findRowCount() == 0;
    }

    /**
     * Checks if the town has the chunk.
     *
     * @param town  the town to check.
     * @param chunk the chunk to check.
     * @return true, if the town has the chunk.
     */
    public boolean hasChunk(Town town, Chunk chunk) {
        return getDatabase().find(TownChunk.class)
                .where()
                .eq("townId", town.getId())
                .eq("world", chunk.getWorld().getName())
                .eq("x", chunk.getX())
                .eq("z", chunk.getZ())
                .findRowCount() > 0;
    }

    /**
     * Checks if the town has all chunks in the region.
     *
     * @param town   the town to check.
     * @param region the region to check.
     * @return true, if the town has all the chunks.
     */
    public boolean hasChunks(Town town, World world, Region region) {

        for (Vector2D chunk : region.getChunks()) {
            if (getDatabase().find(TownChunk.class)
                    .where()
                    .eq("townId", town.getId())
                    .eq("world", world.getName())
                    .eq("x", chunk.getBlockX())
                    .eq("z", chunk.getBlockZ())
                    .findRowCount() == 0) {
                return false;
            }
        }

        return true;
    }

    /**
     * Adds all chunks in the passed region to the town.
     *
     * @param town   the town to add the chunks.
     * @param region the region with the chunks.
     * @return number of chunks added.
     */
    public int addChunks(Town town, World world, Region region) {

        List<TownChunk> changedTownChunks = new LinkedList<TownChunk>();

        for (Vector2D chunk : region.getChunks()) {
            TownChunk townChunk = getDatabase().find(TownChunk.class)
                    .where()
                    .eq("world", world.getName())
                    .eq("x", chunk.getBlockX())
                    .eq("z", chunk.getBlockZ())
                    .findUnique();

            if (townChunk == null) {
                townChunk = new TownChunk();
                townChunk.setWorld(world.getName());
                townChunk.setX(chunk.getBlockX());
                townChunk.setZ(chunk.getBlockZ());
            }

            if (townChunk.getTownId() != town.getId()) {
                townChunk.setTownId(town.getId());
                changedTownChunks.add(townChunk);
            }
        }

        getDatabase().save(changedTownChunks);

        return changedTownChunks.size();
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

    /**
     * Returns the Inhabitant of the owner of the passed Residence.
     *
     * @param residence the Residence.
     * @return PlayerData of the owner.
     */
    public Inhabitant getOwner(Residence residence) {
        return getInhabitant(residence.getOwnerId());
    }
}
