/*
 * SonarQube JavaScript Plugin
 * Copyright (C) 2011-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.javascript.cfg;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import java.util.Map;
import java.util.Set;

class SimpleBlock extends MutableBlock {

  private MutableBlock successor;

  public SimpleBlock(MutableBlock successor) {
    Preconditions.checkArgument(successor != null, "Successor cannot be null");
    this.successor = successor;
  }

  @Override
  public Set<MutableBlock> successors() {
    return ImmutableSet.of(successor);
  }

  public MutableBlock firstNonEmptySuccessor() {
    MutableBlock block = this;
    while (block instanceof SimpleBlock && block.isEmpty()) {
      block = ((SimpleBlock) block).successor;
    }
    return block;
  }

  @Override
  public void replaceSuccessors(Map<MutableBlock, MutableBlock> replacements) {
    this.successor = replacement(this.successor, replacements);
  }

  // Should only be used in "for" loops which have no condition
  public void forceSuccessor(MutableBlock successor) {
    this.successor = successor;
  }

}
