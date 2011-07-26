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

import at.co.hohl.myresidence.Nation;
import at.co.hohl.myresidence.RuleManager;
import at.co.hohl.myresidence.exceptions.MyResidenceException;
import at.co.hohl.myresidence.storage.persistent.Town;
import at.co.hohl.myresidence.storage.persistent.TownRule;

import javax.persistence.PersistenceException;
import java.util.LinkedList;
import java.util.List;

/**
 * RuleManager implementation for the bukkit persistence.
 *
 * @author Michael Hohk
 */
public class PersistRuleManager implements RuleManager {
  /**
   * Nation of the town to manage.
   */
  protected final Nation nation;

  /**
   * Town to manage.
   */
  protected final Town town;

  /**
   * Creates a new PersistRuleManager.
   *
   * @param nation the nation of the town to manage.
   * @param town   the town to manage.
   */
  public PersistRuleManager(Nation nation, Town town) {
    this.nation = nation;
    this.town = town;
  }

  /**
   * Adds a single rule.
   *
   * @param rule the rule to create.
   */
  public void addRule(String rule) {
    TownRule townRule = new TownRule();
    townRule.setTownId(town.getId());
    townRule.setMessage(rule);

    nation.getDatabase().save(townRule);
  }

  /**
   * Removes a rule, which is like the passed string.
   *
   * @param rule (part of the) message of the rule to remove.
   */
  public void removeRule(String rule) throws MyResidenceException {
    try {
      TownRule townRule = nation.getDatabase().find(TownRule.class).where()
              .ilike("message", "%" + rule + "%")
              .eq("townId", town.getId())
              .findUnique();

      nation.getDatabase().delete(townRule);
    } catch (PersistenceException e) {
      throw new MyResidenceException("Rule not found!");
    }
  }

  /**
   * Gets all rules of the town.
   */
  public List<String> getRules() {
    List<TownRule> rules = nation.getDatabase().find(TownRule.class)
            .where()
            .eq("townId", town.getId())
            .orderBy("message ASC")
            .findList();

    List<String> ruleLines = new LinkedList<String>();
    for (TownRule rule : rules) {
      ruleLines.add(rule.getMessage());
    }

    return ruleLines;
  }
}
