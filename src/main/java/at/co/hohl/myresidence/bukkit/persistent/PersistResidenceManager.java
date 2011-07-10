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

import at.co.hohl.myresidence.MyResidence;
import at.co.hohl.myresidence.Nation;
import at.co.hohl.myresidence.ResidenceManager;
import at.co.hohl.myresidence.exceptions.ResidenceSignMissingException;
import at.co.hohl.myresidence.storage.persistent.*;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Implementation of PersistResidenceManager for Bukkit persistence.
 *
 * @author Michael Hohl
 */
public class PersistResidenceManager extends PersistResidenceFlagManager implements ResidenceManager {
    /**
     * Plugin which holds the instance.
     */
    protected final MyResidence plugin;

    /**
     * Creates a new FlagManager implementation.
     *
     * @param nation    nation which holds the residence.
     * @param residence the residence to manage.
     */
    public PersistResidenceManager(MyResidence plugin, Nation nation, Residence residence) {
        super(nation, residence);
        this.plugin = plugin;
    }

    /**
     * Adds a member to the residence.
     *
     * @param inhabitant the inhabitant to become membership.
     */
    public void addMember(Inhabitant inhabitant) {
        if (!isMember(inhabitant)) {
            ResidenceMember membership = new ResidenceMember();
            membership.setInhabitantId(inhabitant.getId());
            membership.setResidenceId(residence.getId());

            nation.save(membership);
        }
    }

    /**
     * Removes the residence as a member.
     *
     * @param inhabitant the inhabitant to remove as member.
     */
    public void removeMember(Inhabitant inhabitant) {
        List<ResidenceMember> foundedMembers = nation.getDatabase().find(ResidenceMember.class)
                .where()
                .eq("residenceId", residence.getId())
                .eq("inhabitantId", inhabitant.getId())
                .findList();

        if (foundedMembers.size() > 0) {
            nation.getDatabase().delete(foundedMembers);
        }
    }

    /**
     * Checks if the inhabitant is member or owner of the residence.
     *
     * @param inhabitant the inhabitant to check.
     * @return true, if the inhabitant is a member.
     */
    public boolean isMember(Inhabitant inhabitant) {
        return residence.getOwnerId() == inhabitant.getId() ||
                nation.getDatabase().find(ResidenceMember.class).where()
                        .eq("residenceId", residence.getId())
                        .eq("inhabitantId", inhabitant.getId())
                        .findRowCount() > 0;
    }

    /**
     * Returns the members of the residence.
     *
     * @return the members of the residence.
     */
    public List<Inhabitant> getMembers() {
        List<ResidenceMember> foundedMembers = nation.getDatabase().find(ResidenceMember.class)
                .where()
                .eq("residenceId", residence.getId())
                .findList();

        List<Inhabitant> inhabitants = new LinkedList<Inhabitant>();
        for (ResidenceMember member : foundedMembers) {
            inhabitants.add(nation.getInhabitant(member.getInhabitantId()));
        }

        Collections.sort(inhabitants);

        return inhabitants;
    }

    /**
     * Sets the area of the residence.
     *
     * @param selection the area to set.
     */
    public void setArea(Selection selection) {
        ResidenceArea area = nation.getDatabase().find(ResidenceArea.class)
                .where().eq("residenceId", residence.getId()).findUnique();

        if (area == null) {
            area = new ResidenceArea();
            area.setResidenceId(residence.getId());
        }

        area.setWorld(selection.getWorld().getName());
        area.setHighX(selection.getMaximumPoint().getBlockX());
        area.setHighY(selection.getMaximumPoint().getBlockY());
        area.setHighZ(selection.getMaximumPoint().getBlockZ());
        area.setLowX(selection.getMinimumPoint().getBlockX());
        area.setLowY(selection.getMinimumPoint().getBlockY());
        area.setLowZ(selection.getMinimumPoint().getBlockZ());

        nation.save(area);
    }

    /**
     * @return the area of the residence.
     */
    public Selection getArea() {
        ResidenceArea area = nation.getDatabase().find(ResidenceArea.class)
                .where().eq("residenceId", residence.getId()).findUnique();

        World world = plugin.getServer().getWorld(area.getWorld());
        Location loc1 = new Location(world, area.getLowX(), area.getLowY(), area.getLowZ());
        Location loc2 = new Location(world, area.getHighX(), area.getHighY(), area.getHighZ());

        return new CuboidSelection(world, loc1, loc2);
    }

    /**
     * Sets the sign of the residence.
     *
     * @param sign the sign of the residence.
     */
    public void setSign(Block sign) {
        ResidenceSign residenceSign = nation.getDatabase().find(ResidenceSign.class)
                .where().eq("residenceId", residence.getId()).findUnique();

        if (residenceSign == null) {
            residenceSign = new ResidenceSign();
            residenceSign.setResidenceId(residence.getId());
        }

        residenceSign.setWorld(sign.getWorld().getName());
        residenceSign.setX(sign.getX());
        residenceSign.setY(sign.getY());
        residenceSign.setZ(sign.getZ());

        nation.save(residenceSign);
    }

    /**
     * @return the sign of the residence.
     */
    public Block getSign() throws ResidenceSignMissingException {
        ResidenceSign residenceSign = nation.getDatabase().find(ResidenceSign.class)
                .where().eq("residenceId", residence.getId()).findUnique();

        if (residenceSign != null) {
            World world = Bukkit.getServer().getWorld(residenceSign.getWorld());
            Block block = world.getBlockAt(residenceSign.getX(), residenceSign.getY(), residenceSign.getZ());

            if (block != null && block.getState() instanceof Sign) {
                return block;
            } else {
                throw new RuntimeException("Invalid block at location of sign!");
            }
        }

        throw new ResidenceSignMissingException(residence);
    }

    /**
     * Sets the home point for the location.
     *
     * @param homeLocation the home point for the location.
     */
    public void setHome(Location homeLocation) {
        HomePoint residenceHome = nation.getDatabase().find(HomePoint.class).where()
                .eq("residenceId", residence.getId())
                .findUnique();

        if (residenceHome == null) {
            residenceHome = new HomePoint();
            residenceHome.setResidenceId(residence.getId());
        }

        residenceHome.setWorld(homeLocation.getWorld().getName());
        residenceHome.setX(homeLocation.getX());
        residenceHome.setY(homeLocation.getBlockY());
        residenceHome.setZ(homeLocation.getZ());
        residenceHome.setPitch(homeLocation.getPitch());
        residenceHome.setYaw(homeLocation.getYaw());

        nation.getDatabase().save(residenceHome);
    }

    /**
     * @return the location of the home point.
     */
    public Location getHome() {
        HomePoint residenceHome = nation.getDatabase().find(HomePoint.class).where()
                .eq("residenceId", residence.getId())
                .findUnique();

        if (residenceHome == null) {
            residenceHome = new HomePoint();
            residenceHome.setResidenceId(residence.getId());

            ResidenceArea area = nation.getDatabase().find(ResidenceArea.class).where()
                    .eq("residenceId", residence.getId()).findUnique();
            if (area != null) {
                residenceHome.setWorld(area.getWorld());
                residenceHome.setX(area.getLowX() + (area.getHighX() - area.getLowX()) / 2);
                residenceHome.setY(area.getHighY());
                residenceHome.setZ(area.getLowZ() + (area.getHighZ() - area.getLowZ()) / 2);
            }
        }

        Location homeLocation = new Location(
                Bukkit.getServer().getWorld(residenceHome.getWorld()),
                residenceHome.getX(),
                residenceHome.getY(),
                residenceHome.getZ(),
                residenceHome.getYaw(),
                residenceHome.getPitch());

        return homeLocation;
    }

    /**
     * @return the inhabitants who liked the residence.
     */
    public List<Inhabitant> getLikes() {
        List<Like> likes =
                nation.getDatabase().find(Like.class).where().eq("residenceId", residence.getId()).findList();

        List<Inhabitant> inhabitantsLikedThis = new LinkedList<Inhabitant>();
        for (Like like : likes) {
            inhabitantsLikedThis.add(nation.getInhabitant(like.getInhabitantId()));
        }

        return inhabitantsLikedThis;
    }

    /**
     * @param inhabitant the inhabitant to like the residence.
     */
    public void like(Inhabitant inhabitant) {
        unlike(inhabitant);

        Like like = new Like();
        like.setInhabitantId(inhabitant.getId());
        like.setResidenceId(residence.getId());

        nation.save(like);
    }

    /**
     * @param inhabitant the inhabitant to unlike the residence.
     */
    public void unlike(Inhabitant inhabitant) {
        List<Like> likes = nation.getDatabase().find(Like.class).where()
                .eq("residenceId", residence.getId())
                .eq("inhabitantId", inhabitant.getId())
                .findList();

        if (likes != null && likes.size() > 0) {
            nation.getDatabase().delete(likes);
        }
    }
}
