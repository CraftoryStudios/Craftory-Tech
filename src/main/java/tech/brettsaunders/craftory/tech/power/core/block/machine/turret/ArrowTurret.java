package tech.brettsaunders.craftory.tech.power.core.block.machine.turret;

import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import tech.brettsaunders.craftory.CoreHolder.Blocks;
import tech.brettsaunders.craftory.CoreHolder.Items;
import tech.brettsaunders.craftory.api.font.Font;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.persistence.Persistent;
import tech.brettsaunders.craftory.tech.power.api.block.BaseMachine;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GBattery;
import tech.brettsaunders.craftory.tech.power.api.guiComponents.GTurretConfig;
import tech.brettsaunders.craftory.utils.Logger;
import tech.brettsaunders.craftory.utils.Pair;

public class ArrowTurret extends BaseMachine {
  private static final byte C_LEVEL = 0;
  private static final byte MAX_RECEIVE = 120;
  private ArmorStand turretHead;
  private Pair<Double, Double> arcAngle;
  private TurretState turretState;

  private double direction = 0;
  private Location startPoint;
  private int targetingCount;

  private LivingEntity lastTargetedEntity;


  @Persistent
  protected HashMap<BlockFace, Boolean> config;

  public ArrowTurret(Location location) {
    super(location, Blocks.TURRET_PLATFORM, C_LEVEL, MAX_RECEIVE);
    config = new HashMap<>();
    config.put(BlockFace.NORTH,false);
    config.put(BlockFace.NORTH_EAST,false);
    config.put(BlockFace.EAST, false);
    config.put(BlockFace.SOUTH, false);
    config.put(BlockFace.WEST, false);
    config.put(BlockFace.NORTH_WEST, false);
    config.put(BlockFace.SOUTH_EAST, false);
    config.put(BlockFace.SOUTH_WEST, false);
    arcAngle = new Pair<Double, Double>((double)0,(double)0);
    turretState = TurretState.Searching;
    startPoint = location.clone().add(0.5,0.5,0.5);
  }

  public ArrowTurret() {
    super();
    startPoint = location.clone().add(0.5,0.5,0.5);
    arcAngle = new Pair<Double, Double>((double)0,(double)0);
    turretState = TurretState.Searching;
  }

  @Override
  public void beforeSaveUpdate() {
    super.beforeSaveUpdate();
    turretHead.remove();
  }

  @Override
  public void afterLoadUpdate() {
    super.afterLoadUpdate();
    createHead();
  }

  @Override
  public void blockBreak() {
    turretHead.remove();
  }

  private void createHead() {
    turretHead = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(0.5,-0.70,0.5), EntityType.ARMOR_STAND);
    turretHead.setArms(false);
    turretHead.setBasePlate(false);
    turretHead.setVisible(false);
    turretHead.setOp(true);
    turretHead.setInvulnerable(true);
    turretHead.setGravity(false);

    EntityEquipment entityEquipment = turretHead.getEquipment();
    entityEquipment.setHelmet(CustomItemManager.getCustomItem(Items.TURRET_HEAD));

    turretHead.setHeadPose(new EulerAngle(0,0,0));
  }

  @Override
  public void updateMachine() {
    if (turretState == TurretState.Searching) {
      if (direction == 0) direction = 1;
      turretHead.setHeadPose(turretHead.getHeadPose().add(0,Math.toRadians(direction), 0));

      if (turretHead.getHeadPose().getY() > Math.toRadians(arcAngle.getY())) {
        direction = -1;
      } else if (turretHead.getHeadPose().getY() < Math.toRadians(arcAngle.getX())) {
        direction = 1;
      }

      //Ray
      Vector vector = eulerToVector(turretHead.getHeadPose());

      RayTraceResult rayTraceResult = location.getWorld().rayTraceEntities(startPoint, vector, 10,entity -> !(entity instanceof ArmorStand));
      if (rayTraceResult != null && rayTraceResult.getHitEntity() != null && rayTraceResult.getHitEntity() instanceof LivingEntity) {
        turretState = TurretState.Targeting; 
        lastTargetedEntity = ((LivingEntity)rayTraceResult.getHitEntity());
        targetingCount = 200;
      }
    } else if (turretState == TurretState.Targeting) {
      turretHead.setHeadPose(turretHead.getHeadPose().add(0,Math.toRadians(direction), 0));

      Vector vector = eulerToVector(turretHead.getHeadPose());
      RayTraceResult rayTraceResult = location.getWorld().rayTraceEntities(startPoint, vector, 10,entity -> !(entity instanceof ArmorStand));
      if (rayTraceResult != null && rayTraceResult.getHitEntity() != null && rayTraceResult.getHitEntity() instanceof LivingEntity) {
        lastTargetedEntity = ((LivingEntity)rayTraceResult.getHitEntity());
        lastTargetedEntity.damage(2);
        targetingCount = 200;
        direction = 0;
      } else {
        targetingCount--;
        //Vector toTarget = lastTargetedEntity.getLocation().toVector().subtract(location.toVector());
        double dx = location.getX() - lastTargetedEntity.getLocation().getX();
        double dz = location.getZ() - lastTargetedEntity.getLocation().getZ();
        double angle = dx*vector.getX() + dz*vector.getZ();
        //double angle = Math.toDegrees(vector.dot(lastTargetedEntity.getLocation().toVector()));

        direction = angle > 0 ? 2 : -2;
        Logger.info(angle+"");
      }

      if (targetingCount <= 0) turretState = TurretState.Searching;
      if (lastTargetedEntity.getHealth() <=0) turretState = TurretState.Searching;

      if (turretHead.getHeadPose().getY() > Math.toRadians(arcAngle.getY())) {
        direction = 0;
      } else if (turretHead.getHeadPose().getY() < Math.toRadians(arcAngle.getX())) {
        direction = 0;
      }
    }

  }

  @Override
  public void setupGUI() {
    Inventory inventory = createInterfaceInventory(displayName,
        Font.FURNACE_GUI.label + "");
    addGUIComponent(new GBattery(inventory, energyStorage));
    addGUIComponent(new GTurretConfig(inventory, config, 25, 25, arcAngle));
    this.inventoryInterface = inventory;
  }

  private Vector eulerToVector(EulerAngle angle) {
    return new Vector((Math.cos(angle.getX()) * Math.cos(angle.getY() + Math.toRadians(90))), (Math.sin(angle.getX()) * Math.cos(angle.getY() + Math.toRadians(90))), Math.sin(angle.getY() + Math.toRadians(90)));
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
}
