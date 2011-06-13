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
import com.avaje.ebean.ExpressionList;
import com.nijikokun.register.payment.Method;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.NestedCommand;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.commands.InsufficientArgumentsException;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Command for managing residences.
 *
 * @author Michael Hohl
 */
public class ResidenceCommands {
    /** Maximum numbers of line per page. */
    private static final int LINES_PER_PAGE = 7;

    @Command(
            aliases = {"create", "c"},
            usage = "<name>",
            desc = "Creates a new residence with passed name. " +
                    "(For creating residences in wildness 'myresidence.residence.wildness' is needed)",
            min = 1,
            flags = "w"
    )
    @CommandPermissions({"myresidence.town.major.create"})
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

        // If player wants to create residences in wildness, he needs enough rights!
        final boolean buildInWildness = args.hasFlag('w');
        if (buildInWildness && !session.hasPermission("myresidence.residence.wildness")) {
            throw new PermissionsDeniedException("You are not allowed to create a residence in wildness!");
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
            throws NoResidenceSelectedException {
        // Get residence.
        final Residence residenceToRemove = session.getSelectedResidence();
        final ResidenceSign residenceSign = nation.getResidenceSign(residenceToRemove);
        final ResidenceArea residenceArea = nation.getResidenceArea(residenceToRemove);
        World signWorld = player.getServer().getWorld(residenceSign.getWorld());
        final Sign sign = (Sign) signWorld.getBlockAt(
                new Location(signWorld, residenceSign.getX(), residenceSign.getY(), residenceSign.getZ())).getState();

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
            throw new MyResidenceException("Residence is not for sell!");
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
            oldOwner.sendMessage(ChatColor.DARK_GREEN + "Your residence was sold for " +
                    ChatColor.YELLOW + plugin.format(price) +
                    ChatColor.DARK_GREEN + " to " + ChatColor.YELLOW + oldOwner.getName() +
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

        Inhabitant residenceOwner = nation.getOwner(residence);
        if (!(player.getName().equals(residenceOwner.getName()))) {
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
            aliases = {"info", "i"},
            desc = "Returns information about residence",
            max = 0
    )
    @CommandPermissions({"myresidence.residence.info"})
    public static void info(final CommandContext args,
                            final MyResidence plugin,
                            final Nation nation,
                            final Player player,
                            final Session session)
            throws MyResidenceException {
        Residence residence = session.getSelectedResidence();
        nation.sendInformation(player, residence);
    }

    @Command(
            aliases = {"list", "l"},
            usage = "[page]",
            desc = "List residences. " +
                    "(c: cheapest, n: nearest, o: residences you own, s: residences for sale, v: most expensive)",
            max = 1,
            flags = "cnosv"
    )
    @CommandPermissions({"myresidence.residence.list"})
    public static void list(final CommandContext args,
                            final MyResidence plugin,
                            final Nation nation,
                            final Player player,
                            final Session session)
            throws MyResidenceException, InsufficientArgumentsException {
        Location playerLocation = player.getLocation();

        ExpressionList expressionList = nation.getDatabase().find(Residence.class).where();

        // v: sort by value.
        if (args.hasFlag('v')) {
            expressionList.orderBy("value desc");
        } else {
            expressionList.orderBy("name asc");
        }

        // o: only residences you own.
        if (args.hasFlag('o')) {
            int ownerId = nation.getInhabitant(player.getName()).getId();
            expressionList.eq("ownerId", ownerId);
        }

        // n: only residences in your current town.
        if (args.hasFlag('n')) {
            try {
                int townId = nation.getTown(playerLocation).getId();
                expressionList.eq("townId", townId);
            } catch (NullPointerException e) {
                throw new MyResidenceException("You can only use -n flag inside towns!");
            }
        }

        // s: only residences for sale.
        if (args.hasFlag('s')) {
            expressionList.eq("forSale", true);

            // c: sort by price.
            if (args.hasFlag('c')) {
                expressionList.orderBy("price asc");
            }
        }

        // Find exact page.
        int page = args.getInteger(0, 1);
        int rows = expressionList.findRowCount();
        int index = (page - 1) * 7 + 1;
        if (rows == 0) {
            throw new MyResidenceException("No search results found!");
        }
        if (index > rows || index < 1) {
            throw new InsufficientArgumentsException("Invalid page number!");
        }
        expressionList.setMaxRows(LINES_PER_PAGE);
        expressionList.setFirstRow((page - 1) * LINES_PER_PAGE);

        List<Residence> residences = expressionList.findList();

        // Print results.
        player.sendMessage(ChatColor.LIGHT_PURPLE +
                String.format("= = = Residences [Page %s/%s] = = =", page, rows / LINES_PER_PAGE + 1));

        if (args.hasFlag('s')) {
            for (Residence residence : residences) {
                player.sendMessage(
                        String.format(
                                ChatColor.GRAY + "%d. %s->" + ChatColor.WHITE + " %s" + ChatColor.GRAY + " [Price: %s]",
                                index,
                                Town.toString(nation.getTown(residence.getTownId())),
                                residence.getName(),
                                plugin.format(residence.getPrice())));
                ++index;
            }
        } else if (args.hasFlag('o')) {
            for (Residence residence : residences) {
                player.sendMessage(String.format(
                        ChatColor.GRAY + "%d. %s->" + ChatColor.WHITE + " %s" + ChatColor.GRAY + " [Value: %s]",
                        index,
                        Town.toString(nation.getTown(residence.getTownId())),
                        residence.getName(),
                        plugin.format(residence.getValue())));
                ++index;
            }
        } else {
            for (Residence residence : residences) {
                player.sendMessage(String.format(
                        ChatColor.GRAY + "%d. %s->" + ChatColor.WHITE + " %s" + ChatColor.GRAY + " [Owner: %s]",
                        index,
                        Town.toString(nation.getTown(residence.getTownId())),
                        residence.getName(),
                        nation.getOwner(residence)));
                ++index;
            }
        }
    }

    @Command(
            aliases = {"flag", "f"},
            desc = "Manage flags of the residences"
    )
    @NestedCommand({ResidenceFlagCommands.class})
    public static void flags(final CommandContext args,
                             final MyResidence plugin,
                             final Nation nation,
                             final Player player,
                             final Session session) {
    }
}
