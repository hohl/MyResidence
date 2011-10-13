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

package at.co.hohl.myresidence.storage.persistent;

import com.avaje.ebean.validation.NotNull;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Represents a flag for a Residence. For example to set, that everybody can build on the residence.
 *
 * @author Michael Hohl
 */
@Entity
@Table(name = "res_residenceflags")
public class ResidenceFlag {
  /**
   * All type of town flags.
   */
  public enum Type {
    PUBLIC,
    LOCAL,
    SIGHT,
    HIDDEN,
    ALLOW_PVP,
    MOB_SPAWNING
  }

  @Id
  private int id;

  @NotNull
  private int residenceId;

  @NotNull
  private Type flag;

  /**
   * Creates a new ResidenceFlag
   */
  public ResidenceFlag() {
  }

  public Type getFlag() {
    return flag;
  }

  public void setFlag(Type flag) {
    this.flag = flag;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getResidenceId() {
    return residenceId;
  }

  public void setResidenceId(int residenceId) {
    this.residenceId = residenceId;
  }
}
