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
import java.util.Arrays;
import java.util.HashSet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import tech.brettsaunders.craftory.api.blocks.CustomBlock;
import tech.brettsaunders.craftory.api.blocks.CustomBlockTickManager.Ticking;
import tech.brettsaunders.craftory.api.font.NegativeSpaceFont;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IGUIComponent;

public abstract class BlockGUI extends CustomBlock implements Listener {

  /* Static Constants */
  private final ArrayList<IGUIComponent> components = new ArrayList<>();
  protected static HashSet<Integer> interactableSlots = new HashSet<>();

  private static final HashSet<InventoryAction> outputDisabledActions = new HashSet<>(Arrays
      .asList(InventoryAction.SWAP_WITH_CURSOR, InventoryAction.PLACE_ALL,
          InventoryAction.PLACE_ONE, InventoryAction.PLACE_SOME));
  /* Per Object Variables */
  private Inventory inventoryInterface;

  /* Saving, Setup and Loading */
  public BlockGUI(Location location, String blockName) { super(location, blockName); }

  public BlockGUI() {
    super();
  }

  public HashSet<Integer> getInteractableSlots() {
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
    if (inventoryInterface == null || inventoryInterface.getViewers().size() <= 0) {
      return;
    }
    for (IGUIComponent component : components) {
      component.update();
    }
  }

  public Inventory getInventory() {
    return inventoryInterface;
  }

  public void addGUIComponent(IGUIComponent component) {
    components.add(component);
  }

  public void openGUI(Player player) {
    player.openInventory(inventoryInterface);
  }

  protected Inventory createInterfaceInventory(String title, String guiImage) {
    String titleSpaced = ChatColor.DARK_GRAY + title;
    String titleBuilder = ChatColor.WHITE + "" + NegativeSpaceFont.MINUS_16.label + guiImage
        + NegativeSpaceFont.MINUS_128.label + NegativeSpaceFont.MINUS_16.label + NegativeSpaceFont.MINUS_16.label + NegativeSpaceFont.MINUS_8.label + centerTitle(titleSpaced);
    inventoryInterface = Bukkit.createInventory(null, 54, titleBuilder);
    return inventoryInterface;
  }

  public String centerTitle(String title) {
    int length = 27 - ChatColor.stripColor(title).length();
    if (length < 0) length = 0;
    return Strings.repeat(" ", length) + title;
  }
}