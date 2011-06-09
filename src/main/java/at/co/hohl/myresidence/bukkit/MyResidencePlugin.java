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

import at.co.hohl.myresidence.commands.GeneralCommands;
import at.co.hohl.myresidence.exceptions.*;
import at.co.hohl.myresidence.storage.Configuration;
import at.co.hohl.myresidence.storage.Session;
import at.co.hohl.myresidence.storage.persistent.*;
import com.nijikokun.register.payment.Methods;
import com.sk89q.minecraft.util.commands.*;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.commands.InsufficientArgumentsException;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;

import javax.persistence.PersistenceException;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bukkit plugin which allows to manage residences and towns.
 *
 * @author Michael Home
 */
public class MyResidencePlugin extends MyResidenceAPI {
    /** Manager for all commands bound to this plugin. */
    private CommandsManager<Player> commands;

    /** Maps loaded configuration to worlds. */
    private Map<String, Configuration> configurationMap = new HashMap<String, Configuration>();

    /** Called on enabling this plugin. */
    public void onEnable() {
        logger = getServer().getLogger();

        methods = new Methods();

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
     * @param sender  the sender of the command.
     * @param command the command itself.
     * @param label   the label used for calling the command.
     * @param args    the arguments passed to the command.
     * @return true, if the plugin handles the command.
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
        } catch (NoResidenceSelectedException e) {
            player.sendMessage(ChatColor.RED + "You have to be inside a residence or select it by clicking the sign!");
        } catch (NoTownSelectedException e) {
            player.sendMessage(ChatColor.RED + "You need to select a town before!");
            player.sendMessage(ChatColor.RED + "Use /town select <name> to select a town.");
        } catch (ResidenceSignMissingException e) {
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Residence sign is missing!");
        } catch (NotOwnException e) {
            player.sendMessage(ChatColor.RED + "You are not the owner!");
        } catch (IncompleteRegionException e) {
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Make a region selection first.");
        } catch (InsufficientArgumentsException e) {
            player.sendMessage(ChatColor.RED + e.getMessage());
        } catch (MyResidenceException e) {
            player.sendMessage(ChatColor.RED + e.getMessage());
        } catch (Throwable exception) {
            player.sendMessage(ChatColor.RED + "Please report this error: [See console]");
            player.sendMessage(ChatColor.RED + exception.getClass().getName() + ": " + exception.getMessage());
            exception.printStackTrace();
        }

        return true;
    }

    /**
     * @param world the world to get configuration.
     * @return the main configuration for the plugin.
     */
    public Configuration getConfiguration(World world) {
        if (configurationMap.containsKey(world.getName())) {
            return configurationMap.get(world.getName());
        } else {
            org.bukkit.util.config.Configuration bukkitConfig =
                    new org.bukkit.util.config.Configuration(new File(getDataFolder(), world.getName() + ".yml"));
            Configuration configuration = new Configuration(bukkitConfig);
            configurationMap.put(world.getName(), configuration);
            return configuration;
        }
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

        // Listen for player clicking on signs.
        SignClickListener signClickListener = new SignClickListener(this);
        pluginManager.registerEvent(Event.Type.PLAYER_INTERACT, signClickListener, Event.Priority.Normal, this);
    }

    /** Setups the commands. */
    private void setupCommands() {
        commands = new CommandsManager<Player>() {
            @Override
            public boolean hasPermission(Player player, String permission) {
                return getPermissionsResolver().hasPermission(player.getName(), permission);
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
