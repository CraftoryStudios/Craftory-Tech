package tech.brettsaunders.craftory.magic.mobs.chestpet;

import dev.lone.itemsadder.api.ItemsAdder;
import java.util.List;
import java.util.UUID;

import java.util.UUID;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.exception.NPCLoadException;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Equipment.EquipmentSlot;
import net.citizensnpcs.api.util.DataKey;
import net.citizensnpcs.api.util.ItemStorage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Persists a {@link Player} to follow while spawned. Optionally allows protecting of the player as well.
 */
@TraitName("chestpet")
public class ChestPetTrait extends Trait {
  @Persist("active")
  private boolean enabled = false;
  @Persist
  private UUID followingUUID;
  private Player player;
  @Persist
  private boolean protect;

  private ItemStack[] contents;
  private Inventory inventory;

  private boolean moving = false;

  public ChestPetTrait() {
    super("chestpet");
    contents = new ItemStack[27];
  }

  /**
   * Returns whether the trait is actively following a {@link Player}.
   */
  public boolean isActive() {
    return enabled && npc.isSpawned() && player != null && npc.getEntity().getWorld().equals(player.getWorld());
  }

  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public void load(DataKey key) throws NPCLoadException {
    contents = parseContents(key);
  }

  @Override
  public void onDespawn() {
    saveContents();
  }

  private ItemStack[] parseContents(DataKey key) throws NPCLoadException {
    ItemStack[] contents = new ItemStack[72];
    for (DataKey slotKey : key.getIntegerSubKeys()) {
      contents[Integer.parseInt(slotKey.name())] = ItemStorage.loadItemStack(slotKey);
    }
    return contents;
  }

  @Override
  public void save(DataKey key) {
    int slot = 0;
    for (ItemStack item : contents) {
      // Clear previous items to avoid conflicts
      key.removeKey(String.valueOf(slot));
      if (item != null) {
        ItemStorage.saveItem(key.getRelative(String.valueOf(slot)), item);
      }
      slot++;
    }
  }

  private void saveContents() {
    if (inventory != null) {
      contents = inventory.getContents();
    }
  }

  @EventHandler
  private void onPlayerLeave(PlayerQuitEvent event) {
    Player playerLeaving = event.getPlayer();
    if (player != null && playerLeaving == player) {
      npc.despawn();
    }
  }

  @EventHandler
  private void onPlayerJoin(PlayerJoinEvent event) {
    Player playerJoining = event.getPlayer();
    if (player != null && playerJoining == player) {
      npc.spawn(playerJoining.getLocation());
    }
  }

  @Override
  public void onSpawn() {
    if (inventory == null) {
      inventory = Bukkit.createInventory(null, 27);
    }
    for (int i = 0; i < inventory.getSize(); i++) {
      inventory.setItem(i, contents[i]);
    }

    npc.getTrait(Equipment.class)
        .set(EquipmentSlot.HELMET, ItemsAdder.getCustomItem("craftory:chestpet_still"));
    Zombie chicken = (Zombie) npc.getEntity();
    chicken.setBaby(true);
    chicken.setCustomNameVisible(false);
    chicken.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
  }

  @EventHandler
  private void onEntityDamage(EntityDamageByEntityEvent event) {
    if (isActive() && event.getEntity().equals(player)) {
      Entity damager = event.getDamager();
      if (event.getEntity() instanceof Projectile) {
        Projectile projectile = (Projectile) event.getEntity();
        if (projectile.getShooter() instanceof Entity) {
          damager = (Entity) projectile.getShooter();
        }
      }
      npc.getNavigator().setTarget(damager, true);
    }
  }

  @Override
  public void run() {
    saveContents();
    if (player == null || !player.isValid()) {
      if (followingUUID == null)
        return;
      player = Bukkit.getPlayer(followingUUID);
      if (player == null) {
        return;
      }
    }
    if (!isActive()) {
      return;
    }
    if (!npc.getNavigator().isNavigating()) {
      npc.getNavigator().setTarget(player, false);
    }
    Zombie chicken = (Zombie) npc.getEntity();
    if (chicken.getVelocity().length() > 0) {
      if (!moving) {
        moving = true;
        npc.getTrait(Equipment.class)
            .set(EquipmentSlot.HELMET, ItemsAdder.getCustomItem("craftory:chestpet_walking"));
      }
    } else {
      if (moving) {
        moving = false;
        npc.getTrait(Equipment.class)
            .set(EquipmentSlot.HELMET, ItemsAdder.getCustomItem("craftory:chestpet_still"));
      }
    }
    List<Entity> itemsNearby = chicken.getNearbyEntities(5, 2, 5);
    if (itemsNearby.size() > 0) {
      Item nearest = null;
      double distance = 10;
      for (int i = 0; i < itemsNearby.size(); i++) {
        Entity item = itemsNearby.get(i);
        if (item.getType() != EntityType.DROPPED_ITEM) continue;
        double tempDistance = item.getLocation().distance(chicken.getLocation());
        if (tempDistance < 1.6) {
          nearest = null;
          pickUp((Item) item);
          item.remove();
        } else if (tempDistance < distance) {
          distance = tempDistance;
          nearest = (Item) item;
        }
      }
      if (nearest != null) {
        npc.getNavigator().setTarget((Entity) nearest, false);
      }
    }

  }

  @EventHandler
  public void onPlayerIntereactEntity(PlayerInteractEntityEvent e) {
    if(e.getRightClicked().equals(npc.getEntity())){
      e.getPlayer().openInventory(inventory);
    }
  }

  /**
   * Toggles and/or sets the {@link OfflinePlayer} to follow and whether to protect them (similar to wolves in
   * Minecraft, attack whoever attacks the player).
   *
   * Will toggle if the {@link OfflinePlayer} is the player currently being followed.
   *
   * @param player
   *            the player to follow
   * @param protect
   *            whether to protect the player
   * @return whether the trait is enabled
   */
  public boolean toggle(OfflinePlayer player, boolean protect) {
    this.protect = protect;
    if (player.getUniqueId().equals(this.followingUUID) || this.followingUUID == null) {
      this.enabled = !enabled;
    }
    this.followingUUID = player.getUniqueId();
    if (npc.getNavigator().isNavigating() && this.player != null && npc.getNavigator().getEntityTarget() != null
        && this.player == npc.getNavigator().getEntityTarget().getTarget()) {
      npc.getNavigator().cancelNavigation();
    }
    this.player = null;
    return this.enabled;
  }

  public void setInventory(Inventory inventory) {
    this.inventory = inventory;
  }

  private void pickUp(Item item) {
    if (inventory != null) {
      inventory.addItem(item.getItemStack());
    }
  }
}
