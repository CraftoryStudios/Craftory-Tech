/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.testing;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tech.brettsaunders.craftory.CoreHolder.Blocks;

public class TestingCommand implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (sender instanceof Player) {
      Player player = (Player) sender;
      if (args.length == 1) {
        player.sendMessage("Blocks Created " + 3 * Integer.parseInt(args[0]));

        Testing.placeColumn(player.getLocation(), Blocks.DIAMOND_ELECTRIC_FURNACE,
            Blocks.SOLID_FUEL_GENERATOR, Blocks.EMERALD_CELL,
            Material.COBBLESTONE.name(), Material.COAL_BLOCK.name(), Integer.parseInt(args[0]));
      } else {
        player.sendMessage(
            "Blocks Created " + (3 * Integer.parseInt(args[0])) * Integer.parseInt(args[1]));

        Testing.placeGrid(player.getLocation(), Blocks.DIAMOND_ELECTRIC_FURNACE,
            Blocks.SOLID_FUEL_GENERATOR, Blocks.EMERALD_CELL,
            Material.COBBLESTONE.name(), Material.COAL_BLOCK.name(), Integer.parseInt(args[0]),
            Integer.parseInt(args[1]));
      }
    }
    return true;
  }
}
