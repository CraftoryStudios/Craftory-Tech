package tech.brettsaunders.craftory.tech.power.core.block.machine.turret;

import java.util.HashMap;
import java.util.Random;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
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
import tech.brettsaunders.craftory.utils.Pair;

public class ArrowTurret extends BaseMachine {
  private static final byte C_LEVEL = 0;
  private static final byte MAX_RECEIVE = 120;
  private ArmorStand base_turret;
  private ArmorStand gun_turret;
  private Pair<Double, Double> arcAngle;
  private TurretState turretState;

  private double directionRotate = 0;

  private double directionTilt = 0;
  private Location startPoint;
  private int targetingCount;

  private int counter = 0;

  private LivingEntity lastTargetedEntity;
  private Random random;

  private static final double TILT_LIMIT = 7;


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
    random = new Random();
  }

  public ArrowTurret() {
    super();
    arcAngle = new Pair<Double, Double>((double)0,(double)0);
    turretState = TurretState.Searching;
    random = new Random();
  }

  @Override
  public void beforeSaveUpdate() {
    super.beforeSaveUpdate();
    removeTurret();
  }

  @Override
  public void afterLoadUpdate() {
    super.afterLoadUpdate();
    createTurret();
  }

  private void removeTurret() {
    base_turret.remove();
    gun_turret.remove();
  }

  @Override
  public void blockBreak() {
    removeTurret();
  }

  private void createTurret() {
    base_turret = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(0.5,-0.70,0.5), EntityType.ARMOR_STAND);
    base_turret.setArms(false);
    base_turret.setBasePlate(false);
    base_turret.setVisible(false);
    base_turret.setOp(true);
    base_turret.setInvulnerable(true);
    base_turret.setGravity(false);

    EntityEquipment entityEquipment = base_turret.getEquipment();
    entityEquipment.setHelmet(CustomItemManager.getCustomItem(Items.BASE_TURRET));

    base_turret.setHeadPose(new EulerAngle(0,0,0));

    gun_turret = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(0.5,-0.70,0.5), EntityType.ARMOR_STAND);
    gun_turret.setArms(false);
    gun_turret.setBasePlate(false);
    gun_turret.setVisible(false);
    gun_turret.setOp(true);
    gun_turret.setInvulnerable(true);
    gun_turret.setGravity(false);

    entityEquipment = gun_turret.getEquipment();
    entityEquipment.setHelmet(CustomItemManager.getCustomItem(Items.GUN_TURRET));

    gun_turret.setHeadPose(new EulerAngle(0,0,0));

    startPoint = base_turret.getEyeLocation().clone().add(0, 0.7, 0);
  }

  @Override
  public void updateMachine() {
    if (arcAngle.getX() == 0 && arcAngle.getY()==0) return;
    if (turretState == TurretState.Searching) {
      searchingState();
    } else if (turretState == TurretState.Targeting) {
      targetingState();
    }

  }

  private void targetingState() {
    rotateTurret();

    Vector vector = eulerToVector(base_turret.getHeadPose());
    RayTraceResult rayTraceResult = location.getWorld().rayTraceEntities(startPoint, vector, 10,entity -> !(entity instanceof ArmorStand));
    if (rayTraceResult != null && rayTraceResult.getHitEntity() != null && rayTraceResult.getHitEntity() instanceof LivingEntity) {
      lastTargetedEntity = ((LivingEntity)rayTraceResult.getHitEntity());
      onShoot(vector, rayTraceResult.getHitPosition());
      targetingCount = 100;
      directionRotate = 0;
    } else {
      counter = 0;
      targetingCount--;
      double angle = (Math.atan2(location.getX() - lastTargetedEntity.getLocation().getX(), location.getZ() - lastTargetedEntity.getLocation().getZ()));
      angle = (-(angle / Math.PI) * 360.0d) / 2.0d + 180.0d;
      angle = (angle - Math.toDegrees(base_turret.getHeadPose().getY()));

      if (angle > 0.5) {
        directionRotate = 2;
      } else if (angle < -0.5){
        directionRotate = -2;
      } else {
        directionRotate = 0;
      }
    }

    if (targetingCount <= 0 || lastTargetedEntity.getHealth() <=0) {
      turretState = TurretState.Searching;
    }

    if (base_turret.getHeadPose().getY() > Math.toRadians(arcAngle.getY())) {
      directionRotate = 0;
    } else if (base_turret.getHeadPose().getY() < Math.toRadians(arcAngle.getX())) {
      directionRotate = 0;
    }
  }

  private void searchingState() {
    if (directionRotate == 0)
      directionRotate = 1;
    if (directionTilt == 0)
      directionTilt = 1;

    rotateTurret();

    if (base_turret.getHeadPose().getY() > Math.toRadians(arcAngle.getY())) {
      directionRotate = -1;
      counter = 10;
    } else if (base_turret.getHeadPose().getY() < Math.toRadians(arcAngle.getX())) {
      directionRotate = 1;
    }

    if (gun_turret.getHeadPose().getX() > Math.toRadians(TILT_LIMIT + 5)) {
      directionTilt = -0.3;
    } else if (gun_turret.getHeadPose().getX() < Math.toRadians(-TILT_LIMIT)) {
      directionTilt = 0.3;
    }

    //Ray
    Vector vector = eulerToVector(base_turret.getHeadPose());

    RayTraceResult rayTraceResult = location.getWorld().rayTraceEntities(startPoint, vector, 10,entity -> !(entity instanceof ArmorStand));
    if (rayTraceResult != null && rayTraceResult.getHitEntity() != null && rayTraceResult.getHitEntity() instanceof LivingEntity) {
      turretState = TurretState.Targeting;
      counter = 0;
      lastTargetedEntity = ((LivingEntity)rayTraceResult.getHitEntity());
      targetingCount = 100;
    }
  }

  private void onShoot(Vector turretVector, Vector hitPoint) {
    if (counter % 5 == 0) {
      lastTargetedEntity.damage(5);
      if (counter == 0) {
        Location particlePoint = startPoint.clone().add(turretVector.clone().multiply(1.2));
        Vector turretDirectionVector = turretVector.clone();
        turretDirectionVector.multiply(0.2);
        double addedLength = turretDirectionVector.length();
        double distance = particlePoint.toVector().distance(hitPoint);
        for (double length = 0; length < distance; particlePoint.add(turretDirectionVector)) {
          location.getWorld()
              .spawnParticle(Particle.REDSTONE, particlePoint, 1, new Particle.DustOptions(
                  Color.GRAY, (1 + random.nextFloat() * 0.5f)));
          length += addedLength;
        }
      }
      counter++;
    } else if (counter >= 10) {
      counter = 0;
    } else {
      counter++;
    }
  }

  private void rotateTurret() {
    EulerAngle turnAngle = base_turret.getHeadPose().add(0, Math.toRadians(directionRotate), 0);
    base_turret.setHeadPose(turnAngle);
    //Math.toRadians(directionTilt)
    EulerAngle tiltAngle = gun_turret.getHeadPose().add(0,Math.toRadians(directionRotate), 0);
    gun_turret.setHeadPose(tiltAngle);
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
