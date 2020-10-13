/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.api.block;

import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import tech.brettsaunders.craftory.Utilities;
import tech.brettsaunders.craftory.api.blocks.CustomBlock;
import tech.brettsaunders.craftory.api.blocks.CustomBlockTickManager.Ticking;
import tech.brettsaunders.craftory.api.font.NegativeSpaceFont;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IGUIComponent;

public abstract class BlockGUI extends CustomBlock implements Listener {

  /* Static Constants */
  private final ArrayList<IGUIComponent> components = new ArrayList<>();
  protected HashSet<Integer> interactableSlots = new HashSet<>();
  /* Per Object Variables */
  private Inventory inventoryInterface;

  /* Saving, Setup and Loading */
  protected BlockGUI(Location location, String blockName) {
    super(location, blockName);
  }

  protected BlockGUI() {
    super();
  }

  public Set<Integer> getInteractableSlots() {
    return interactableSlots;
  }

  @Override
  public void afterLoadUpdate() {
    super.afterLoadUpdate();
    setupGUI();
  }

  public abstract void setupGUI();

  /*GUI Methods */
  @Ticking(ticks = 4)
  public void updateInterface() {
    if (inventoryInterface == null || inventoryInterface.getViewers().isEmpty()) {
      return;
    }
    for (IGUIComponent component : components) {
      component.update();
    }
  }

  @Override
  public void blockBreak() {
    super.blockBreak();
    for(HumanEntity viewer: new ArrayList<>(inventoryInterface.getViewers())){
      viewer.closeInventory();
    }
  }

  public Inventory getInventory() {
    return inventoryInterface;
  }

  public void addGUIComponent(IGUIComponent component) {
    components.add(component);
  }

  public void openGUI(Player player) {
    if (Utilities.updateItemGraphics) {
      CustomItemManager.updateInventoryItemGraphics(inventoryInterface);
    }
    player.openInventory(inventoryInterface);
  }

  protected Inventory createInterfaceInventory(String title, String guiImage) {
    String titleSpaced = ChatColor.DARK_GRAY + title;
    String titleBuilder = ChatColor.WHITE + "" + NegativeSpaceFont.MINUS_16.label + guiImage
        + NegativeSpaceFont.MINUS_128.label + NegativeSpaceFont.MINUS_16.label
        + NegativeSpaceFont.MINUS_16.label + NegativeSpaceFont.MINUS_8.label + centerTitle(
        titleSpaced);
    inventoryInterface = Bukkit.createInventory(null, 54, titleBuilder);
    return inventoryInterface;
  }

  public String centerTitle(String title) {
    int length = 27 - ChatColor.stripColor(title).length();
    if (length < 0) {
      length = 0;
    }
    return Strings.repeat(" ", length) + title;
  }
}