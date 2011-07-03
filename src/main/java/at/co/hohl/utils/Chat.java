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

package at.co.hohl.utils;

import at.co.hohl.myresidence.MyResidence;
import com.sk89q.bukkit.migration.PermissionsResolver;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

/**
 * Helper class for the minecraft chat.
 *
 * @author Michael Hohl
 */
public final class Chat {
    /**
     * Sends a message to a player.
     *
     * @param player  the player to receive the message.
     * @param message the message to send.
     * @param args    the arguments to send.
     */
    public static void sendMessage(Player player, String message, Object... args) {
        player.sendMessage(replaceColorCodes(insertArgs(message, args)));
    }

    /**
     * Sends a message to all users.
     *
     * @param server     the server to broadcast the message.
     * @param permission the permission needed to receive the broadcast.
     * @param message    the message to send.
     * @param args       the arguments to insert into message.
     */
    public static void broadcastMessage(Server server, String permission, String message, Object... args) {
        MyResidence plugin = (MyResidence) server.getPluginManager().getPlugin("MyResidence");
        PermissionsResolver permissionsResolver = plugin.getPermissionsResolver();
        message = replaceColorCodes(insertArgs(message, args));

        for (Player player : server.getOnlinePlayers()) {
            if (permissionsResolver.hasPermission(player.getName(), permission)) {
                player.sendMessage(message);
            }
        }
    }

    /**
     * Inserts all the arguments.
     *
     * @param message the message to insert the args.
     * @param args    the arguments to insert.
     * @return
     */
    private static String insertArgs(String message, Object... args) {
        for (int index = 0; index < args.length; ++index) {
            message = message.replace("{" + index + "}", args[index].toString());
        }

        return message;
    }

    /**
     * Replaces all color codes in message.
     *
     * @param message the message to replace the color codes.
     * @return the message with replaced color codes.
     */
    private static String replaceColorCodes(String message) {
        for (int index = 0; index < 16; ++index) {
            message = message.replace(String.format("&%h", index), ChatColor.getByCode(index).toString());
        }

        return message;
    }

    // Hide constructor!
    private Chat() {
    }
}
