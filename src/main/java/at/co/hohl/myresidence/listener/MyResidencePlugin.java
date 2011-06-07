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

package at.co.hohl.myresidence.listener;

import at.co.hohl.myresidence.MyResidence;
import at.co.hohl.myresidence.commands.GeneralCommands;
import at.co.hohl.myresidence.storage.Session;
import at.co.hohl.myresidence.storage.persistent.*;
import at.co.hohl.permissions.Permission;
import at.co.hohl.permissions.PermissionHandler;
import com.nijikokun.register.payment.Methods;
import com.sk89q.minecraft.util.commands.*;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.commands.InsufficientArgumentsException;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Bukkit plugin which allows to manage residences and towns.
 *
 * @author Michael Home
 */
public class MyResidencePlugin extends JavaPlugin implements MyResidence {
    /** Logger used by this plugin. */
    private Logger logger;

    /** WorldEdit plugin. */
    private WorldEditPlugin worldEdit;

    /** Payment methods. */
    private Methods methods;

    /** PermissionHandler */
    private PermissionHandler permissionHandler;

    /** Manager for all commands bound to this plugin. */
    private CommandsManager<Player> commands;

    /** Session Map used by this player. */
    private Map<Player, Session> sessionMap = new HashMap<Player, Session>();

    /** Called on enabling this plugin. */
    public void onEnable() {
        logger = getServer().getLogger();

        methods = new Methods();
        permissionHandler = new PermissionHandler(this);

        setupDatabase();
        setupListeners();
        setupCommands();

        info("version %s enabled!", getDescription().getVersion());
    }

    /** Called on disabling this plugin. */
    public void onDisable() {
        info("version %s disabled!", getDescription().getVersion());
    }

    /**
     * Called when user uses a command bind to this application.
     *
     * @param sender
     * @param command
     * @param label
     * @param args
     * @return
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) && !commands.hasCommand(label)) {
            return false;
        }

        Player player = (Player) sender;
        Session session = getSession(player);

        try {
            long start = System.currentTimeMillis();

            String[] commandLine = new String[args.length + 1];
            commandLine[0] = label;
            System.arraycopy(args, 0, commandLine, 1, args.length);

            try {
                commands.execute(commandLine, player, this, player, session);
            } catch (CommandPermissionsException e) {
                player.sendMessage(ChatColor.RED + "You don't have permission to do this.");
            } catch (MissingNestedCommandException e) {
                player.sendMessage(ChatColor.RED + e.getUsage());
            } catch (CommandUsageException e) {
                player.sendMessage(ChatColor.RED + e.getMessage());
                player.sendMessage(ChatColor.RED + e.getUsage());
            } catch (WrappedCommandException e) {
                throw e.getCause();
            } catch (UnhandledCommandException e) {
                return false;
            } finally {
                if (session.isDebugger()) {
                    long time = System.currentTimeMillis() - start;
                    player.sendMessage(String.format("%s %f's elapsed.", ChatColor.LIGHT_PURPLE, time));
                }
            }
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Number expected; string given.");
        } catch (IncompleteRegionException e) {
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Make a region selection first.");
        } catch (InsufficientArgumentsException e) {
            player.sendMessage(ChatColor.RED + e.getMessage());
        } catch (Throwable exception) {
            player.sendMessage(ChatColor.RED + "Please report this error: [See console]");
            player.sendMessage(ChatColor.RED + exception.getClass().getName() + ": " + exception.getMessage());
            exception.printStackTrace();
        }

        return true;
    }

    /**
     * Returns the residence at the passed location
     *
     * @param location the location to look for.
     * @return the founded residence or null.
     */
    public Residence getResidence(Location location) {
        ResidenceArea residenceArea = getDatabase().find(ResidenceArea.class).where()
                .ge("lowX", location.getBlockX())
                .ge("lowY", location.getBlockY())
                .ge("lowZ", location.getBlockZ())
                .le("highX", location.getBlockX())
                .le("highY", location.getBlockY())
                .le("highZ", location.getBlockZ())
                .findUnique();

        if (residenceArea != null) {
            return getDatabase().find(Residence.class).where().eq("area", residenceArea).findUnique();
        } else {
            return null;
        }
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
            return getDatabase().find(Residence.class).where().eq("sign", residenceSign).findUnique();
        } else {
            return null;
        }
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
            return currentChunk.getTown();
        } else {
            return null;
        }
    }

    /**
     * Returns the session for the passed player.
     *
     * @param player the player to look for the session.
     * @return the found or create session.
     */
    public Session getSession(Player player) {
        if (!sessionMap.containsKey(player)) {
            sessionMap.put(player, new Session());
            info("session created for %s.", player.getName());
        }

        return sessionMap.get(player);
    }

    /** @return all available payment methods. */
    public Methods getMethods() {
        return methods;
    }

    /** @return handler for the permissions. */
    public PermissionHandler getPermissionHandler() {
        return permissionHandler;
    }

    /** @return world edit plugin. */
    public WorldEditPlugin getWorldEdit() {
        return worldEdit;
    }

    public void setWorldEdit(WorldEditPlugin worldEdit) {
        this.worldEdit = worldEdit;
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

    /** Setups the listeners for the plugin. */
    private void setupListeners() {
        PluginManager pluginManager = getServer().getPluginManager();

        // Listen for WorldEdit.
        WorldEditPluginListener worldEditPluginListener = new WorldEditPluginListener(this);
        pluginManager.registerEvent(Event.Type.PLUGIN_ENABLE, worldEditPluginListener, Event.Priority.Monitor, this);
        pluginManager.registerEvent(Event.Type.PLUGIN_DISABLE, worldEditPluginListener, Event.Priority.Monitor, this);

        // Listen for Economy Plugins.
        EconomyPluginListener economyPluginListener = new EconomyPluginListener(this);
        pluginManager.registerEvent(Event.Type.PLUGIN_ENABLE, economyPluginListener, Event.Priority.Monitor, this);
        pluginManager.registerEvent(Event.Type.PLUGIN_DISABLE, economyPluginListener, Event.Priority.Monitor, this);
    }

    /** Setups the commands. */
    private void setupCommands() {
        commands = new CommandsManager<Player>() {
            @Override
            public boolean hasPermission(Player player, String permission) {
                return permissionHandler.hasPermission(player, new Permission(permission, true));
            }
        };

        commands.register(GeneralCommands.class);
    }

    /** Creates needed databases. */
    private void setupDatabase() {
        try {
            getDatabase().find(Residence.class).findRowCount();
            getDatabase().find(Town.class).findRowCount();
            getDatabase().find(ResidenceArea.class).findRowCount();
            getDatabase().find(ResidenceSign.class).findRowCount();
            getDatabase().find(PlayerData.class).findRowCount();
            getDatabase().find(TownChunk.class).findRowCount();
        } catch (PersistenceException ex) {
            info("Installing database due to first time usage!");
            installDDL();
        }
    }

    /** @return all daos of this plugins. */
    @Override
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(Residence.class);
        list.add(Town.class);
        list.add(ResidenceArea.class);
        list.add(ResidenceSign.class);
        list.add(PlayerData.class);
        list.add(TownChunk.class);
        return list;
    }
}
