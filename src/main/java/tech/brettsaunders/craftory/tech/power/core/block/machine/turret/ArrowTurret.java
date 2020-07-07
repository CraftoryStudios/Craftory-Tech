package tech.brettsaunders.craftory.tech.power.core.block.machine.turret;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EntityEquipment;
import tech.brettsaunders.craftory.CoreHolder.Blocks;
import tech.brettsaunders.craftory.CoreHolder.Items;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.tech.power.api.block.BaseMachine;

public class ArrowTurret extends BaseMachine {
  private static final byte C_LEVEL = 0;
  private static final byte MAX_RECEIVE = 120;
  private ArmorStand turretHead;

  public ArrowTurret(Location location) {
    super(location, Blocks.ARROW_TURRET, C_LEVEL, MAX_RECEIVE);
    createHead();
  }

  public ArrowTurret() {
    super();
    createHead();
  }

  private void createHead() {
    turretHead = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(0,1,0), EntityType.ARMOR_STAND);
    turretHead.setArms(false);
    turretHead.setBasePlate(false);
    turretHead.setVisible(false);
    turretHead.setOp(true);
    turretHead.setInvulnerable(true);

    EntityEquipment entityEquipment = turretHead.getEquipment();
    entityEquipment.setHelmet(CustomItemManager.getCustomItem(Items.ARROW_TURRET_HEAD));
    entityEquipment.setHelmetDropChance(0);
  }

  @Override
  protected void processComplete() {

  }

  @Override
  protected boolean validateContentes() {
    return false;
  }

  @Override
  protected void updateSlots() {

  }

  @Override
  public void updateMachine() {

  }
}
