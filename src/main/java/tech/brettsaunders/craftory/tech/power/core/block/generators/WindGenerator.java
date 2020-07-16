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
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import tech.brettsaunders.craftory.api.items.CustomItemManager;

public class WindGenerator extends BaseRenewableGenerator{
  private static final byte C_LEVEL = 0;
  private static final int SLOT = 23; //TODO set
  protected ArmorStand wheel;
  public WindGenerator() {
    super();
    init();
  }

  /* Saving, Setup and Loading */
  public WindGenerator(Location location) {
    super(location, "TODO", C_LEVEL); //TODO set name
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
     //TODO place it
    }
  }
  @Override
  protected void placeWheels() {
    wheel = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(0.5,-0.70,0.5), EntityType.ARMOR_STAND);
    wheel.setArms(false);
    wheel.setBasePlate(false);
    wheel.setVisible(false);
    wheel.setOp(true);
    wheel.setInvulnerable(true);
    wheel.setGravity(false);

    EntityEquipment entityEquipment = wheel.getEquipment();
    entityEquipment.setHelmet(CustomItemManager.getCustomItem("windmill")); //TODO set
    wheel.setHeadPose(new EulerAngle(0,0,0));
    //TODO make it face the right direction
  }

  @Override
  protected boolean placeWheel(Location loc) {
    return false;
  }

  @Override
  public void updateEfficiency() {

  }
}
