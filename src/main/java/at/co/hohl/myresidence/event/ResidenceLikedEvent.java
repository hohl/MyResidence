package at.co.hohl.myresidence.event;

import at.co.hohl.myresidence.storage.Session;
import at.co.hohl.myresidence.storage.persistent.Residence;

/**
 * Event created on residence receiving likes.
 *
 * @author Michae Hohl
 */
public class ResidenceLikedEvent {
  private final Residence residence;

  private final Session likedBy;

  public ResidenceLikedEvent(Session likedBy, Residence residence) {
    this.likedBy = likedBy;
    this.residence = residence;
  }

  public Session getLikedBy() {
    return likedBy;
  }

  public Residence getResidence() {
    return residence;
  }
}
