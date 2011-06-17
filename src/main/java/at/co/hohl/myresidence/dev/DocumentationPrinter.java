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

package at.co.hohl.myresidence.dev;

import at.co.hohl.myresidence.commands.GeneralCommands;
import at.co.hohl.myresidence.commands.MapCommand;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.NestedCommand;
import com.sk89q.util.StringUtil;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Developer util for printing the documentation for the wiki.
 *
 * @author sk89q (modified: miho)
 */
public class DocumentationPrinter {
    public static void main(String[] args) throws IOException {
        List<Class<?>> commandClasses = getCommandClasses();

        System.out.printf("Writing permissions wiki table...\n");
        writePermissionsWikiTable(commandClasses);

        System.out.printf("Done!\n");
    }

    private static List<Class<?>> getCommandClasses() {
        List<Class<?>> classes = new ArrayList<Class<?>>();

        classes.add(GeneralCommands.class);
        classes.add(MapCommand.class);
        classes.add(HomeCommands.class);

        return classes;
    }

    private static void writePermissionsWikiTable(List<Class<?>> commandClasses)
            throws IOException {
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream("wiki_permissions.txt");
            PrintStream print = new PrintStream(stream);
            _writePermissionsWikiTable(print, commandClasses, "/");
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    private static void _writePermissionsWikiTable(PrintStream stream,
                                                   List<Class<?>> commandClasses, String prefix) {
        for (Class<?> cls : commandClasses) {
            List<Method> methodList = new ArrayList<Method>();
            for (Method method : cls.getMethods()) {
                if (method.isAnnotationPresent(Command.class)) {
                    methodList.add(method);
                }
            }
            Collections.sort(methodList, new Comparator<Method>() {
                public int compare(Method method, Method method1) {
                    Command cmd = method.getAnnotation(Command.class);
                    Command cmd1 = method1.getAnnotation(Command.class);
                    return cmd.aliases()[0].compareTo(cmd1.aliases()[0]);
                }
            });
            for (Method method : methodList) {
                Command cmd = method.getAnnotation(Command.class);

                if ("myres".equals(cmd.aliases()[0])) {
                    continue;
                }

                if (!method.isAnnotationPresent(NestedCommand.class)) {
                    stream.println("## Command: " + prefix + cmd.aliases()[0]);
                    if (cmd.aliases().length == 2) {
                        stream.println("**Alias:** " + prefix + cmd.aliases()[1]);
                    } else if (cmd.aliases().length > 2) {
                        stream.println(
                                "**Aliases:** " + prefix + StringUtil.joinString(cmd.aliases(), ", " + prefix, 1));

                    }
                    stream.println();
                    stream.println("**Description:** " + cmd.desc());
                    stream.println();

                    stream.print("**Usage:** " + prefix + cmd.aliases()[0]);
                    if (cmd.flags().length() > 0) {
                        stream.print(" [-" + cmd.flags() + "]");
                    }
                    stream.println(" " + cmd.usage());
                    stream.println();

                    if (method.isAnnotationPresent(CommandPermissions.class)) {
                        CommandPermissions perms = method.getAnnotation(CommandPermissions.class);

                        stream.println(
                                "**Required Permissions:** '" + StringUtil.joinString(perms.value(), "' or '", 0) +
                                        "'");
                    }
                } else {
                    NestedCommand nested = method.getAnnotation(NestedCommand.class);

                    Class<?>[] nestedClasses = nested.value();
                    _writePermissionsWikiTable(stream,
                            Arrays.asList(nestedClasses),
                            prefix + cmd.aliases()[0] + " ");
                }

                stream.println();
            }
        }
    }
}
