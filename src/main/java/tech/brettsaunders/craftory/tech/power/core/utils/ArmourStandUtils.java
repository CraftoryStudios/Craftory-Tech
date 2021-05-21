package tech.brettsaunders.craftory.tech.power.core.utils;

import static com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry.getVectorSerializer;

import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.comphenix.protocol.wrappers.Vector3F;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import tech.brettsaunders.craftory.packet_wrapper.UpdatedEntityType;
import tech.brettsaunders.craftory.packet_wrapper.WrapperPlayServerEntityDestroy;
import tech.brettsaunders.craftory.packet_wrapper.WrapperPlayServerEntityEquipment;
import tech.brettsaunders.craftory.packet_wrapper.WrapperPlayServerEntityMetadata;
import tech.brettsaunders.craftory.packet_wrapper.WrapperPlayServerSpawnEntityLiving;
import tech.brettsaunders.craftory.tech.power.core.block.generators.RotaryGenerator;

public class ArmourStandUtils implements Listener {

  private static WrapperPlayServerSpawnEntityLiving spawnPacket = new WrapperPlayServerSpawnEntityLiving();
  private static WrapperPlayServerEntityMetadata metadataPacket = new WrapperPlayServerEntityMetadata();
  private static WrappedDataWatcher dataWatcher = new WrappedDataWatcher();
  private static WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
  private static WrapperPlayServerEntityEquipment equipmentPacket = new WrapperPlayServerEntityEquipment();

  private static Map<Integer, RotaryGenerator> entities = new ConcurrentHashMap<>();

  static {
    spawnPacket.setType(UpdatedEntityType.ARMOR_STAND);

    //Not Small, No Arms, No base plate, Is marker
    dataWatcher.setObject(new WrappedDataWatcherObject(14, Registry.get(Byte.class)), (byte) (0x08 | 0x10));
    //Invisible
    dataWatcher.setObject(new WrappedDataWatcherObject(0,Registry.get(Byte.class)), (byte) (0x20));
    //Is Silent
    dataWatcher.setObject(new WrappedDataWatcherObject(4, Registry.get(Boolean.class)), true);
    //No gravity
    dataWatcher.setObject(new WrappedDataWatcherObject(5, Registry.get(Boolean.class)), true);
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent e) {
    Collection<? extends Player> player = new HashSet<Player>(){{add(e.getPlayer());}};
    for (Entry<Integer, RotaryGenerator> entity : entities.entrySet()) {
      spawnArmourStand(entity.getValue().getWheelLocation(), entity.getValue().getEntityID(), entity.getValue().getUuid(),
          entity.getValue().getFacing(), player);
      ArmourStandUtils.setEntityHolding(entity.getKey(), entity.getValue().getArmourStandItem(), player);
    }
  }

  public static void spawnArmourStand(Location blockLocation, int entityId, UUID uuid, BlockFace facing, Collection<? extends Player> players) {
    Location spawnLoc = ArmourStandUtils.adjustLocation(blockLocation, facing);
    //Set Id's
    spawnPacket.setEntityID(entityId);
    spawnPacket.setUniqueId(uuid);

    //Set Positions
    spawnPacket.setX(spawnLoc.getX());
    spawnPacket.setY(spawnLoc.getY());
    spawnPacket.setZ(spawnLoc.getZ());
    spawnPacket.setYaw(spawnLoc.getYaw());

    metadataPacket.setEntityID(entityId);
    dataWatcher.setObject(15, getVectorSerializer(), new Vector3F(90, 0, 0));
    metadataPacket.setMetadata(dataWatcher.getWatchableObjects());

    //Spawn
    for (Player player : players) {
      spawnPacket.sendPacket(player);
      metadataPacket.sendPacket(player);
    }
  }

  public static void register(RotaryGenerator rotaryGenerator) {
    entities.put(rotaryGenerator.getEntityID(), rotaryGenerator);
  }

  public static void destroyArmourStand(int entityId, boolean remove) {
    destroy.setEntityIds(new int[]{entityId});
    for (Player player : Bukkit.getOnlinePlayers()) {
      destroy.sendPacket(player);
    }
    if (remove) entities.remove(entityId);
  }

  public static void setEntityHolding(int entityId, ItemStack itemStack, Collection<? extends Player> players) {
    equipmentPacket.setEntityID(entityId);
    equipmentPacket.setSlotStackPair(ItemSlot.HEAD, itemStack);
    for (Player player : players) {
      equipmentPacket.sendPacket(player);
    }
  }

  public static void rotateEntity(int entityId, int rotation) {
    WrapperPlayServerEntityMetadata rotateHeadPacket = new WrapperPlayServerEntityMetadata();
    rotateHeadPacket.setEntityID(entityId);
    dataWatcher.setObject(15, getVectorSerializer(), new Vector3F(90, 0, rotation));
    rotateHeadPacket.setMetadata(dataWatcher.getWatchableObjects());

    for (Player player : Bukkit.getOnlinePlayers()) {
      rotateHeadPacket.sendPacket(player);
    }
  }

  public static Location adjustLocation(Location location, BlockFace direction) {
    Location spawn = location.clone();
    switch (direction) {
      default:
      case NORTH:
        spawn.setYaw(180);
        return spawn.add(0.5, -0.95, 0.7);
      case EAST:
        spawn.setYaw(270);
        return spawn.add(0.3, -0.95, 0.5);
      case SOUTH:
        spawn.setYaw(0);
        return spawn.add(0.5, -0.95, 0.3);
      case WEST:
        spawn.setYaw(90);
        return spawn.add(0.7, -0.95, 0.5);
    }
  }
}
