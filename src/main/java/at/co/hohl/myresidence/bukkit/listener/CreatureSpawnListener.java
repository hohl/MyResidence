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

package at.co.hohl.myresidence.bukkit.listener;

import at.co.hohl.myresidence.MyResidence;
import at.co.hohl.myresidence.PermissionsResolver;
import org.bukkit.World;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityListener;

/**
 * Listener which checks if mobs are allowed to spawn.
 *
 * @author Michael Hohl
 */
public class CreatureSpawnListener extends EntityListener {
  private final MyResidence plugin;
  private final PermissionsResolver permissionsResolver;

  public CreatureSpawnListener(MyResidence plugin) {
    this.plugin = plugin;
    this.permissionsResolver = plugin.getNation().getPermissionsResolver();
  }

  /**
   * Called when a creature tries to spawn.
   *
   * @param event the event which occurred.
   */
  @Override
  public void onCreatureSpawn(CreatureSpawnEvent event) {
    if (event.isCancelled()) {
      return;
    }

    World creatureWorld = event.getLocation().getWorld();


    if ((!event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CUSTOM) &&
            creatureWorld.getLivingEntities().size() > plugin.getConfiguration(creatureWorld).getMobSpawnLimit())
            || !permissionsResolver.isAllowedToSpawnCreature(event)) {
      event.setCancelled(true);
    }
  }
}
