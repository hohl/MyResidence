package at.co.hohl.myresidence.bukkit.listener;

import at.co.hohl.mcutils.chat.Chat;
import at.co.hohl.myresidence.MyResidence;
import at.co.hohl.myresidence.Nation;
import at.co.hohl.myresidence.event.ResidenceLikedEvent;
import at.co.hohl.myresidence.event.ResidenceListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * ResidenceListener used to inform the player on changes.
 *
 * @author Michael Hohl
 */
public class NotifyPlayerListener extends ResidenceListener {
  private final Nation nation;

  private final MyResidence plugin;

  /**
   * Creates a new listener, which notifies the player.
   *
   * @param nation the nation.
   * @param plugin the plugin.
   */
  public NotifyPlayerListener(Nation nation, MyResidence plugin) {
    this.nation = nation;
    this.plugin = plugin;
  }

  /**
   * Called when a residence received a like.
   *
   * @param event the event itself.
   */
  @Override
  public void onResidenceLiked(ResidenceLikedEvent event) {
    Player owner = Bukkit.getServer().getPlayer(nation.getInhabitant(event.getResidence().getOwnerId()).getName());

    if (owner != null) {
      Chat.sendMessage(owner, "&2{0} liked your residence!", event.getLikedBy().getName());
    }
  }
}
