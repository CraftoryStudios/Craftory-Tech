package tech.brettsaunders.craftory.tech.power.core.block.generators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import tech.brettsaunders.craftory.CoreHolder.Items;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.api.font.Font;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GBattery;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GIndicator;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GOutputConfig;

public class WindGenerator extends BaseRenewableGenerator{

  private static final byte C_LEVEL = 0;
  private static final int SLOT = 22;
  protected ArmorStand wheel;
  public WindGenerator() {
    super();
    init();
  }

  /* Saving, Setup and Loading */
  public WindGenerator(Location location) {
    super(location, Items.WINDMILL, C_LEVEL);
    init();
    inputSlots = new ArrayList<>();
    inputSlots.add(new ItemStack(Material.AIR));
  }

  private void init() {
    inputLocations = new ArrayList<>();
    inputLocations.add(0,SLOT);
    interactableSlots = new HashSet<>(Collections.singletonList(SLOT));
    inputFaces = new HashMap<BlockFace, Integer>() {
      {
        put(BlockFace.NORTH, SLOT);
        put(BlockFace.EAST, SLOT);
        put(BlockFace.SOUTH, SLOT);
        put(BlockFace.WEST, SLOT);
        put(BlockFace.UP, SLOT);
      }
    };
  }
  @Override
  protected void removeWheels() {
    wheel.remove();
  }

  @Override
  public void updateGenerator() {
    if(!wheelPlaced && wheelFree && inventoryInterface.getItem(SLOT)!=null){
     ItemStack itemStack = inventoryInterface.getItem(SLOT);
     if(CustomItemManager.isCustomItem(itemStack, false) && CustomItemManager.matchCustomItemName(itemStack,
         Items.WINDMILL)) {
       wheelPlaced =  placeWheel(wheelLocation);
     }
    }
    super.updateGenerator();
  }

  @Override
  protected void placeWheels() {
    if(wheelPlaced){
      wheelPlaced =  placeWheel(wheelLocation);
    }
  }

  @Override
  protected boolean canStart() {//e
    ItemStack itemStack = inventoryInterface.getItem(SLOT);
    if(itemStack!=null && CustomItemManager.isCustomItem(itemStack, false) && CustomItemManager.matchCustomItemName(itemStack,
        Items.WINDMILL)) {
      return super.canStart();
    } else {
      if(wheelPlaced){
        wheel.remove();
        wheelPlaced = false;
      }
    }
    return false;
  }

  @Override
  protected boolean placeWheel(Location loc) {
    wheel = (ArmorStand) loc.getWorld().spawnEntity(loc.clone().add(0.5,-0.95,0.7), EntityType.ARMOR_STAND);
    wheel.setArms(false);
    wheel.setBasePlate(false);
    wheel.setVisible(false);
    wheel.setOp(true);
    wheel.setInvulnerable(true);
    wheel.setGravity(false);

    EntityEquipment entityEquipment = wheel.getEquipment();
    entityEquipment.setHelmet(CustomItemManager.getCustomItem("windmill"));
    switch (facing) {
      case NORTH:
        wheel.setHeadPose(new EulerAngle(Math.toRadians(90), Math.toRadians(180), 0));
        break;
      case EAST:
        wheel.setHeadPose(new EulerAngle(0, 0, 0));
        break;
      case SOUTH:
        wheel.setHeadPose(new EulerAngle(0, 0, 0));
        break;
      case WEST:
        wheel.setHeadPose(new EulerAngle(0, 0, 0));
        break;
    }
    return true;
  }

  @Override
  protected void processTick() {
    super.processTick();
    if(facing.equals(BlockFace.NORTH) || facing.equals(BlockFace.SOUTH)){
      wheel.setHeadPose(wheel.getHeadPose().add(0,0,efficiencyMultiplier*0.1));
    } else {
      wheel.setHeadPose(wheel.getHeadPose().add(efficiencyMultiplier*0.1,0,0));
    }
  }

  @Override
  public void updateEfficiency() {
    ArrayList<Location> locations = new ArrayList<>();
    locations.addAll(wheelLocations);
    locations.add(wheelLocation);
    Vector v;
    if(facing.equals(BlockFace.NORTH) || facing.equals(BlockFace.SOUTH)) {
      v = new Vector(0,0,1);
    } else {
      v = new Vector(1,0,0);
    }
    int maxClearPositive = 12;
    boolean clear = true;
    for (int i = 1; i < 13; i++) {
      for(Location loc: locations) {
        if(!loc.add(v).getBlock().getType().equals(Material.AIR)){
          clear = false;
          break;
        }
      }
      if(!clear){
        maxClearPositive = i;
        break;
      }
    }
    locations = new ArrayList<>();
    locations.addAll(wheelLocations);
    locations.add(wheelLocation);
    if(facing.equals(BlockFace.NORTH) || facing.equals(BlockFace.SOUTH)) {
      v = new Vector(0,0,-1);
    } else {
      v = new Vector(-1,0,0);
    }
    int maxClearNegative = 12;
    clear = true;
    for (int i = 2; i < 13; i++) {
      for(Location loc: locations) {
        if(!loc.add(v).getBlock().getType().equals(Material.AIR)){
          clear = false;
          break;
        }
      }
      if(!clear){
        maxClearNegative = i;
        break;
      }
    }
    efficiencyMultiplier = Math.min(maxClearNegative,maxClearPositive)/12d;
  }

  @Override
  protected boolean wheelAreaFree(Location centerLoc) {
    if(Craftory.customBlockManager.isCustomBlock(centerLoc)){
      if(Craftory.customBlockManager.getCustomBlockName(centerLoc).equals(Items.WINDMILL)){
        return super.wheelAreaFree(centerLoc);
      }
    } else if(centerLoc.getBlock().getType().equals(Material.AIR)) return super.wheelAreaFree(centerLoc);
    return false;
  }

  @Override
  public void setupGUI() {
    Inventory inventory = createInterfaceInventory(displayName, Font.GENERATOR_GUI.label + "");
    addGUIComponent(new GBattery(inventory, energyStorage));
    addGUIComponent(new GOutputConfig(inventory, sidesConfig, 43, true));
    addGUIComponent(new GIndicator(inventory, runningContainer, 31));
    this.inventoryInterface = inventory;
  }
}