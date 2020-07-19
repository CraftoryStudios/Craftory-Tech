package tech.brettsaunders.craftory.tech.power.api.guiComponents;

import java.util.HashMap;
import java.util.Map.Entry;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.CoreHolder.Items;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IGUIComponent;
import tech.brettsaunders.craftory.utils.Pair;

public class GTurretConfig implements IGUIComponent, Listener {

  private final int NORTH_SLOT, EAST_SLOT, SOUTH_SLOT, WEST_SLOT, NORTH_WEST, NORTH_EAST, SOUTH_WEST, SOUTH_EAST;

  private static ItemStack E_ITEM;
  private static ItemStack E_E_ITEM;
  private static ItemStack N_ITEM;
  private static ItemStack N_E_ITEM;
  private static ItemStack NE_ITEM;
  private static ItemStack NE_E_ITEM;
  private static ItemStack NW_ITEM;
  private static ItemStack NW_E_ITEM;
  private static ItemStack S_ITEM;
  private static ItemStack S_E_ITEM;
  private static ItemStack SE_ITEM;
  private static ItemStack SE_E_ITEM;
  private static ItemStack SW_ITEM;
  private static ItemStack SW_E_ITEM;
  private static ItemStack W_ITEM;
  private static ItemStack W_E_ITEM;

  private static HashMap<BlockFace, Double> minAngle = new HashMap<>();
  private static HashMap<BlockFace, Double> maxAngle = new HashMap<>();

  static {
    minAngle.put(BlockFace.NORTH, -22.5);
    maxAngle.put(BlockFace.NORTH, 22.5);

    minAngle.put(BlockFace.NORTH_EAST, 22.5);
    maxAngle.put(BlockFace.NORTH_EAST, 67.5);

    minAngle.put(BlockFace.EAST, 67.5);
    maxAngle.put(BlockFace.EAST, 112.5);

    minAngle.put(BlockFace.SOUTH_EAST, 112.5);
    maxAngle.put(BlockFace.SOUTH_EAST, 157.5);

    minAngle.put(BlockFace.SOUTH, 157.5);
    maxAngle.put(BlockFace.SOUTH, 202.5);

    minAngle.put(BlockFace.SOUTH_WEST, 202.5);
    maxAngle.put(BlockFace.SOUTH_WEST, 247.5);

    minAngle.put(BlockFace.WEST, 247.5);
    maxAngle.put(BlockFace.WEST, 292.5);

    minAngle.put(BlockFace.NORTH_WEST, 292.5);
    maxAngle.put(BlockFace.NORTH_WEST, 337.5);
  }

  private final Inventory inventory;
  private final HashMap<BlockFace, Boolean> config;
  private final int UPGRADE_SLOT;
  private byte upgradeLevel = 1;
  private byte amountZonesActive = 0;
  private Pair<Double, Double> arcAngles;

  public GTurretConfig(Inventory inventory, HashMap<BlockFace, Boolean> config, int middleSlot, int upgradeSlot, Pair<Double, Double> arcAngles) {
    this.inventory = inventory;
    this.config = config;
    NORTH_SLOT = middleSlot - 9;
    NORTH_EAST = middleSlot - 8;
    EAST_SLOT = middleSlot + 1;
    SOUTH_EAST = middleSlot + 10;
    SOUTH_SLOT = middleSlot + 9;
    SOUTH_WEST = middleSlot + 8;
    WEST_SLOT = middleSlot - 1;
    NORTH_WEST = middleSlot - 10;
    UPGRADE_SLOT = upgradeSlot;
    this.arcAngles = arcAngles;

    E_ITEM = CustomItemManager.getCustomItem("turret_E");
    E_E_ITEM = CustomItemManager.getCustomItem("turret_E_E");
    N_ITEM = CustomItemManager.getCustomItem("turret_N");
    N_E_ITEM = CustomItemManager.getCustomItem("turret_N_E");
    NE_ITEM = CustomItemManager.getCustomItem("turret_NE");
    NE_E_ITEM = CustomItemManager.getCustomItem("turret_NE_E");
    NW_ITEM = CustomItemManager.getCustomItem("turret_NW");
    NW_E_ITEM = CustomItemManager.getCustomItem("turret_NW_E");
    S_ITEM = CustomItemManager.getCustomItem("turret_S");
    S_E_ITEM = CustomItemManager.getCustomItem("turret_S_E");
    SE_ITEM = CustomItemManager.getCustomItem("turret_SE");
    SE_E_ITEM = CustomItemManager.getCustomItem("turret_SE_E");
    SW_ITEM = CustomItemManager.getCustomItem("turret_SW");
    SW_E_ITEM = CustomItemManager.getCustomItem("turret_SW_E");
    W_ITEM = CustomItemManager.getCustomItem("turret_W");
    W_E_ITEM = CustomItemManager.getCustomItem("turret_W_E");

    updateTurretAngle();
    Craftory.plugin.getServer().getPluginManager()
        .registerEvents(this, Craftory.plugin);
  }

  @EventHandler
  public void onInventoryInteract(final InventoryClickEvent event) {
    if (event.getInventory() != inventory) {
      return;
    }

    final ItemStack clickedItem = event.getCurrentItem();
    if (clickedItem == null || clickedItem.getType() == Material.AIR) {
      return;
    }

    int rawSlot = event.getRawSlot();
    if (rawSlot == NORTH_SLOT) {
      invertConfig(BlockFace.NORTH);
    } else if (rawSlot == EAST_SLOT) {
      invertConfig(BlockFace.EAST);
    } else if (rawSlot == SOUTH_SLOT) {
      invertConfig(BlockFace.SOUTH);
    } else if (rawSlot == WEST_SLOT) {
      invertConfig(BlockFace.WEST);
    } else if (rawSlot == NORTH_EAST) {
      invertConfig(BlockFace.NORTH_EAST);
    } else if (rawSlot == NORTH_WEST) {
      invertConfig(BlockFace.NORTH_WEST);
    } else if (rawSlot == SOUTH_EAST) {
      invertConfig(BlockFace.SOUTH_EAST);
    } else if (rawSlot == SOUTH_WEST) {
      invertConfig(BlockFace.SOUTH_WEST);
    }

    //Get new Level
    upgradeLevel = (byte) itemToLevel(inventory.getItem(UPGRADE_SLOT));

    //Too many zones, reduce
    if (amountZonesActive > upgradeLevel) {
      int difference = amountZonesActive - upgradeLevel;
      int i = 0;
      for (Entry<BlockFace, Boolean> entry : config.entrySet()) {
        BlockFace blockFace = entry.getKey();
        Boolean aBoolean = entry.getValue();
        if (aBoolean) {
          config.put(blockFace, false);
          i++;
        }

        if (i == difference) break;
      }
    }
    updateTurretAngle();
  }

  private void updateTurretAngle() {
    //Calculate the biggest and smallest angle to swing between
    double smallestAngle = 360;
    double biggestAngle = 0;
    for (Entry<BlockFace, Boolean> entry : config.entrySet()) {
      BlockFace blockFace = entry.getKey();
      Boolean aBoolean = entry.getValue();
      if (aBoolean) {
        double angle = minAngle.get(blockFace);
        if (angle < smallestAngle)
          smallestAngle = angle;
        angle = maxAngle.get(blockFace);
        if (angle > biggestAngle) biggestAngle = angle;
      }
    }

    //Set angle
    if (smallestAngle == 360) {
      arcAngles.setX((double) 0).setY((double) 0);
    } else {
      arcAngles.setX(smallestAngle).setY(biggestAngle);
    }
  }

  private void invertConfig(BlockFace blockFace) {
    config.put(blockFace, !config.get(blockFace));
    updateZones();
  }

  private int itemToLevel(ItemStack itemStack) {
    if (itemStack == null || itemStack.getType() == Material.AIR) {
      return 1;
    }
    switch (CustomItemManager.getCustomItemName(itemStack)) {
      case Items.UPGRADE_CARD_IRON:
        return 2;
      case Items.UPGRADE_CARD_GOLD:
        return 3;
      case Items.UPGRADE_CARD_DIAMOND:
        return 4;
      case Items.UPGRADE_CARD_EMERALD:
        return 5;
      default:
        return 1;
    }
  }

  private void updateZones() {
    amountZonesActive = 0;
    config.forEach((blockFace, aBoolean) -> {
      if (aBoolean) amountZonesActive++;
    });
  }

  @Override
  public void update() {
    inventory.setItem(NORTH_SLOT, config.get(BlockFace.NORTH) ? N_E_ITEM : N_ITEM);
    inventory.setItem(SOUTH_SLOT, config.get(BlockFace.SOUTH) ? S_E_ITEM : S_ITEM);
    inventory.setItem(EAST_SLOT, config.get(BlockFace.EAST) ? E_E_ITEM : E_ITEM);
    inventory.setItem(WEST_SLOT, config.get(BlockFace.WEST) ? W_E_ITEM : W_ITEM);
    inventory.setItem(NORTH_EAST, config.get(BlockFace.NORTH_EAST) ? NE_E_ITEM : NE_ITEM);
    inventory.setItem(NORTH_WEST, config.get(BlockFace.NORTH_WEST) ? NW_E_ITEM : NW_ITEM);
    inventory.setItem(SOUTH_EAST, config.get(BlockFace.SOUTH_EAST) ? SE_E_ITEM : SE_ITEM);
    inventory.setItem(SOUTH_WEST, config.get(BlockFace.SOUTH_WEST) ? SW_E_ITEM : SW_ITEM);
  }
}
