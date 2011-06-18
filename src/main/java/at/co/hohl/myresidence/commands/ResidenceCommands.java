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

package at.co.hohl.myresidence.commands;

import at.co.hohl.myresidence.MyResidence;
import at.co.hohl.myresidence.Nation;
import at.co.hohl.myresidence.exceptions.*;
import at.co.hohl.myresidence.storage.Session;
import at.co.hohl.myresidence.storage.persistent.*;
import com.nijikokun.register.payment.Method;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.NestedCommand;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * Command for managing residences.
 *
 * @author Michael Hohl
 */
public class ResidenceCommands {
    @Command(
            aliases = {"create", "c"},
            usage = "<name>",
            desc = "Creates a new residence with passed name",
            min = 1,
            flags = "w"
    )
    @CommandPermissions({"myresidence.town.major.create", "myresidence.residence.wildness"})
    public static void create(final CommandContext args,
                              final MyResidence plugin,
                              final Nation nation,
                              final Player player,
                              final Session session)
            throws IncompleteRegionException, MyResidenceException {

        // Can not make a Residence without a selection.
        final Selection selection = plugin.getWorldEdit().getSelection(player);
        if (selection == null) {
            throw new IncompleteRegionException();
        }

        // Check if player has enough right.
        final boolean buildInWildness = args.hasFlag('w');
        if (buildInWildness && !session.hasPermission("myresidence.residence.wildness")) {
            throw new PermissionsDeniedException("You are not allowed to create a residence in wildness!");
        } else if (!session.hasPermission("myresidence.town.major.create")) {
            throw new PermissionsDeniedException("Your are not allowed to create residences inside towns!");
        }

        // If player wants to create a residence in town, he must be inside a town too!
        final Town town = nation.getTown(player.getLocation());
        if (!buildInWildness) {
            if (town == null) {
                throw new MyResidenceException("You can not create a residence outside the town!");
            } else if (!nation.hasChunks(town, selection.getWorld(), selection.getRegionSelector().getRegion())) {
                throw new MyResidenceException("Town does not own the chunks, where you want to create the residence!");
            }
        }

        // Create task, which gets executed after selecting a sign.
        session.setTask(new Runnable() {
            public void run() {
                try {
                    final Residence residence = new Residence();
                    residence.setName(args.getJoinedStrings(0));
                    residence.setOwnerId(session.getPlayerId());
                    if (!buildInWildness) {
                        residence.setTownId(town.getId());
                    }
                    nation.getDatabase().save(residence);

                    final ResidenceArea area = new ResidenceArea(selection);
                    area.setResidenceId(residence.getId());
                    nation.getDatabase().save(area);

                    ResidenceSign sign = new ResidenceSign(session.getSelectedSign());
                    sign.setResidenceId(residence.getId());
                    nation.getDatabase().save(sign);

                    nation.updateResidenceSign(residence);

                    player.sendMessage(ChatColor.DARK_GREEN + "Residence '" + residence.getName() + "' created!");
                } catch (MyResidenceException e) {
                    player.sendMessage(ChatColor.RED + e.getMessage());
                }
            }
        });
        session.setTaskActivator(Session.Activator.SELECT_SIGN);

        // Notify user about he has to select a sign.
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Please select a sign, to link it to the new Residence!");
    }

    @Command(
            aliases = {"remove", "r"},
            usage = "<name>",
            desc = "Removes a residence",
            max = 0
    )
    @CommandPermissions({"myresidence.town.major.remove"})
    public static void remove(final CommandContext args,
                              final MyResidence plugin,
                              final Nation nation,
                              final Player player,
                              final Session session)
            throws NoResidenceSelectedException, PermissionsDeniedException {
        // Get residence.
        final Residence residenceToRemove = session.getSelectedResidence();
        final ResidenceSign residenceSign = nation.getResidenceSign(residenceToRemove);
        final ResidenceArea residenceArea = nation.getResidenceArea(residenceToRemove);
        World signWorld = player.getServer().getWorld(residenceSign.getWorld());
        final Sign sign = (Sign) signWorld.getBlockAt(
                new Location(signWorld, residenceSign.getX(), residenceSign.getY(), residenceSign.getZ())).getState();

        if (!session.hasResidenceOwnerRights(residenceToRemove)) {
            throw new PermissionsDeniedException("Only the owner of the residence can remove it!");
        }

        // Create task to confirm.
        session.setTask(new Runnable() {
            public void run() {
                for (int index = 0; index < 4; ++index) {
                    sign.setLine(index, "");
                }
                nation.getDatabase().delete(residenceToRemove);
                nation.getDatabase().delete(residenceArea);
                nation.getDatabase().delete(residenceSign);
                player.sendMessage(ChatColor.DARK_GREEN + "Residence removed!");
            }
        });
        session.setTaskActivator(Session.Activator.CONFIRM_COMMAND);

        // Notify user about need confirmation.
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Do you really want to remove '" + ChatColor.DARK_PURPLE +
                residenceToRemove.getName() + ChatColor.LIGHT_PURPLE + "'?");
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Use /task confirm to confirm this task!");
    }

    @Command(
            aliases = {"buy", "b"},
            desc = "Buys the residence",
            max = 0
    )
    @CommandPermissions({"myresidence.residence.buy"})
    public static void buy(final CommandContext args,
                           final MyResidence plugin,
                           final Nation nation,
                           final Player player,
                           final Session session)
            throws MyResidenceException {
        Residence residence = session.getSelectedResidence();
        Method payment = plugin.getPaymentMethods().getMethod();

        if (!residence.isForSale()) {
            throw new MyResidenceException("The owner does not sell this residence!");
        }

        if (payment == null) {
            throw new EconomyMissingException();
        }

        Method.MethodAccount playerAccount = payment.getAccount(player.getName());
        double price = residence.getPrice();
        if (!playerAccount.hasEnough(price)) {
            throw new NotEnoughMoneyException();
        }
        Method.MethodAccount ownerAccount = payment.getAccount(nation.getOwner(residence).getName());
        playerAccount.subtract(price);
        ownerAccount.add(price);

        Player oldOwner = plugin.getServer().getPlayer(nation.getInhabitant(residence.getOwnerId()).getName());
        residence.setOwnerId(nation.getInhabitant(player.getName()).getId());
        residence.setForSale(false);
        nation.getDatabase().save(residence);

        nation.updateResidenceSign(residence);

        player.sendMessage(ChatColor.DARK_GREEN + "You have successfully bought the residence!");
        if (oldOwner != null && oldOwner.isOnline()) {
            oldOwner.sendMessage(ChatColor.DARK_GREEN + "Your residence " +
                    ChatColor.YELLOW + residence.getName() +
                    ChatColor.DARK_GREEN + " was sold for " +
                    ChatColor.YELLOW + plugin.format(price) +
                    ChatColor.DARK_GREEN + " to " +
                    ChatColor.YELLOW + player.getName() +
                    ChatColor.DARK_GREEN + ".");
        }
    }

    @Command(
            aliases = {"sell", "sale", "s"},
            usage = "[price]",
            desc = "Makes your residence available for sale",
            min = 1,
            max = 1
    )
    @CommandPermissions({"myresidence.residence.sell"})
    public static void sell(final CommandContext args,
                            final MyResidence plugin,
                            final Nation nation,
                            final Player player,
                            final Session session)
            throws MyResidenceException {
        Residence residence = session.getSelectedResidence();

        if (!session.hasResidenceOwnerRights(residence)) {
            throw new NotOwnException();
        }

        double price = args.getDouble(0, residence.getValue());
        residence.setForSale(true);
        residence.setPrice(price);
        if (residence.getValue() <= 0.0) {
            residence.setValue(price);
        }

        player.sendMessage(ChatColor.DARK_GREEN + "Your residence is available for sale now!");

        nation.getDatabase().save(residence);

        nation.updateResidenceSign(residence);
    }

    @Command(
            aliases = {"give", "transfer", "grant"},
            usage = "<player>",
            desc = "Transfers the residence to an owner",
            min = 1,
            max = 1
    )
    @CommandPermissions({"myresidence.residence.give"})
    public static void give(final CommandContext args,
                            final MyResidence plugin,
                            final Nation nation,
                            final Player player,
                            final Session session)
            throws MyResidenceException {
        Residence residence = session.getSelectedResidence();
        if (!session.hasResidenceOwnerRights(residence)) {
            throw new NotOwnException();
        }

        Inhabitant inhabitant = nation.getInhabitant(args.getString(0));
        if (inhabitant == null) {
            throw new PlayerNotFoundException();
        }

        residence.setOwnerId(inhabitant.getId());

        // Save and update sign!
        nation.save(residence);
        nation.updateResidenceSign(residence);

        player.sendMessage(ChatColor.GREEN + residence.getName() +
                ChatColor.DARK_GREEN + " transferred to " +
                ChatColor.GREEN + inhabitant.getName() +
                ChatColor.DARK_GREEN + ".");

        // Notify player, if online.
        Player playerWhichGotTheResidence = player.getServer().getPlayer(inhabitant.getName());
        if (playerWhichGotTheResidence != null && playerWhichGotTheResidence.isOnline()) {
            playerWhichGotTheResidence.sendMessage(ChatColor.GREEN + player.getDisplayName() +
                    ChatColor.DARK_GREEN + " transferred you the residence '" +
                    ChatColor.GREEN + residence.getName() +
                    ChatColor.DARK_GREEN + "'!");
        }
    }

    @Command(
            aliases = {"area"},
            desc = "Selects the residence area with WorldEdit",
            max = 0
    )
    public static void area(final CommandContext args,
                            final MyResidence plugin,
                            final Nation nation,
                            final Player player,
                            final Session session)
            throws MyResidenceException {
        ResidenceArea area = nation.getResidenceArea(session.getSelectedResidence());

        World world = player.getServer().getWorld(area.getWorld());
        Location edge1 = new Location(world, area.getLowX(), area.getLowY(), area.getLowZ());
        Location edge2 = new Location(world, area.getHighX(), area.getHighY(), area.getHighZ());
        Selection selection = new CuboidSelection(world, edge1, edge2);
        plugin.getWorldEdit().setSelection(player, selection);

        player.sendMessage(ChatColor.DARK_GREEN + "Residence area selected with WorldEdit!");
    }

    @Command(
            aliases = {"value"},
            usage = "<amount>",
            desc = "Sets the value of a residence",
            min = 1,
            max = 1
    )
    @CommandPermissions({"myresidence.residence.value"})
    public static void value(final CommandContext args,
                             final MyResidence plugin,
                             final Nation nation,
                             final Player player,
                             final Session session)
            throws MyResidenceException {

        Residence residence = session.getSelectedResidence();

        if (!(session.hasResidenceOwnerRights(residence) ||
                session.hasMajorRights(nation.getTown(residence.getTownId())))) {
            throw new NotOwnException();
        }

        double value = args.getDouble(0, residence.getValue());
        residence.setValue(value);

        player.sendMessage(ChatColor.DARK_GREEN + "Value has been set!");

        nation.getDatabase().save(residence);

        nation.updateResidenceSign(residence);

    }

    @Command(
            aliases = {"rename"},
            usage = "<name>",
            desc = "Change the name of a residence",
            min = 1
    )
    @CommandPermissions({"myresidence.residence.rename"})
    public static void rename(final CommandContext args,
                              final MyResidence plugin,
                              final Nation nation,
                              final Player player,
                              final Session session)
            throws MyResidenceException {

        Residence residence = session.getSelectedResidence();

        if (!(session.hasResidenceOwnerRights(residence) ||
                session.hasMajorRights(nation.getTown(residence.getTownId())))) {
            throw new NotOwnException();
        }

        residence.setName(args.getJoinedStrings(0));

        player.sendMessage(ChatColor.DARK_GREEN + "Residence renamed!");

        nation.getDatabase().save(residence);

        nation.updateResidenceSign(residence);

    }

    @Command(
            aliases = {"info", "i"},
            desc = "Shows information about residences",
            usage = "[residence]"
    )
    @CommandPermissions({"myresidence.residence.info"})
    public static void info(final CommandContext args,
                            final MyResidence plugin,
                            final Nation nation,
                            final Player player,
                            final Session session)
            throws MyResidenceException {
        Residence residence;

        if (args.argsLength() == 0) {
            residence = session.getSelectedResidence();
        } else {
            residence = nation.getResidence(args.getJoinedStrings(0));
        }

        nation.sendInformation(player, residence);
    }

    @Command(
            aliases = {"list", "l"},
            desc = "List & Search Residences"
    )
    @NestedCommand({ResidenceListCommands.class})
    @CommandPermissions({"myresidence.residence.list"})
    public static void list(final CommandContext args,
                            final MyResidence plugin,
                            final Nation nation,
                            final Player player,
                            final Session session) {
    }

    @Command(
            aliases = {"member", "members"},
            desc = "Manage Members of Residences"
    )
    @NestedCommand({ResidenceMemberCommands.class})
    @CommandPermissions({"myresidence.residence.member"})
    public static void members(final CommandContext args,
                               final MyResidence plugin,
                               final Nation nation,
                               final Player player,
                               final Session session) {
    }

    @Command(
            aliases = {"flag", "f"},
            desc = "Manage Flags of Residences"
    )
    @NestedCommand({ResidenceFlagCommands.class})
    @CommandPermissions({"myresidence.town.major.flag"})
    public static void flags(final CommandContext args,
                             final MyResidence plugin,
                             final Nation nation,
                             final Player player,
                             final Session session) {
    }
}
