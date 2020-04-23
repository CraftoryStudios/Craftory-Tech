package tech.brettsaunders.craftory;

import dev.lone.itemsadder.api.ItemsAdder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Magic implements Listener {

  @EventHandler
  public void onPlayerRightClick(PlayerInteractEvent e) {
    if (!(e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
      return;
    }
    if (!(e.hasItem())) {
      return;
    }
    if (!(ItemsAdder.matchCustomItemName(e.getItem(), "craftory:wand"))) {
      return;
    }
    Block clicked = e.getClickedBlock();
    if (clicked.getType().equals(Material.CAULDRON)) {
      wandUsedCauldron(clicked);
      return;
    } else if (clicked.getType().equals(Material.CHEST)){
      wandUsedChest(clicked);
      return;
    }
    wandUsed(clicked);
  }

  private ArrayList<ItemStack> getItemsInRadius(Location loc, float radius) {
    return new ArrayList<ItemStack>(
        Arrays.asList(Arrays.stream(loc.getChunk().getEntities()).filter(e -> e instanceof Item)
            .filter(e -> e.getLocation().distance(loc) <= radius)
            .map(e -> ((Item) e).getItemStack())
            .toArray(ItemStack[]::new)));
  }

  private void wandUsedChest(Block chest) {
    Location loc = chest.getLocation();
    ArrayList<ItemStack> items = getItemsInRadius(loc, 3f);
    HashMap<String, Integer> counts = getItemCounts(items);
    if(!counts.containsKey("craftory:life_gem") || !counts.containsKey(Material.STICK.toString()) || counts.get(Material.STICK.toString()) < 10) return;
    HashMap<String, Integer> remove = new HashMap<>();
    remove.put("craftory:life_gem", 1);
    remove.put(Material.STICK.toString(), 10);
    removeItems(items, remove);
    Location particleLoc = loc.clone();
    chest.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, particleLoc, 10, 0, 0, 0, 0);
    particleLoc = loc.clone().add(0.5, 1, 0.5);
    chest.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, particleLoc, 10, 0, 0, 0, 0);
    particleLoc = loc.clone().add(-0.5, 1, -0.5);
    chest.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, particleLoc, 10, 0, 0, 0, 0);
    particleLoc = loc.clone().add(-0.5, 1, 0.5);
    chest.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, particleLoc, 10, 0, 0, 0, 0);
    particleLoc = loc.clone().add(-0.5, 1, -0.5);
    chest.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, particleLoc, 10, 0, 0, 0, 0);
    //TODO spawn the chest thing in.
  }

  private void wandUsedCauldron(Block cauldron) {
    Location loc = cauldron.getLocation();
    ArrayList<ItemStack> items = getItemsInRadius(loc, 1.2f);
    HashMap<String, Integer> counts = getItemCounts(items);

    //Set the recipe
    HashMap<String, Integer>[] recipe = MagicFusionRecipes.getRecipe(counts);
    if (recipe == null) {
      return;
    }
    ArrayList<ItemStack> toDrop = fuseItems(items, recipe, counts);
    for (ItemStack i : toDrop) {
      cauldron.getWorld().dropItemNaturally(loc, i);
      Location particleLoc = loc.clone().add(0.5, 0.75, 0.5);
      cauldron.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, particleLoc, 10, 0, 0, 0, 0);
    }
  }
  private void wandUsed(Block block) {
    float spell_range = 1.5f;
    ArrayList<ItemStack> items = getItemsInRadius(block.getLocation().clone().add(0, 1, 0),
        spell_range);
    int amount;
    for (ItemStack i : items) {
      amount = i.getAmount();
      amount *= 2;
      amount = (amount > i.getMaxStackSize()) ? i.getMaxStackSize() : amount;
      i.setAmount(amount);
    }
  }

  private HashMap<String, Integer> getItemCounts(ArrayList<ItemStack> items) {
    HashMap<String, Integer> counts = new HashMap<>();
    for (ItemStack i : items) { //Count the amount of each item
      String type;
      if (ItemsAdder.isCustomItem(i)) {
        type = ItemsAdder.getCustomItemName(i);
      } else {
        type = i.getType().toString();
      }
      if (counts.containsKey(type)) {
        counts.put(type, counts.get(type) + i.getAmount());
      } else {
        counts.put(type, i.getAmount());
      }
    }
    return counts;
  }

  private void removeItems(ArrayList<ItemStack> items, HashMap<String, Integer> amounts){
    for (ItemStack i : items) {
      String s;
      if (ItemsAdder.isCustomItem(i)) {
        s = ItemsAdder.getCustomItemName(i);
      } else {
        s = i.getType().toString();
      }
      if (!amounts.containsKey(s)) continue;
      int toRemove = amounts.get(s);
      if (toRemove > 0 && ((ItemsAdder.isCustomItem(i) && ItemsAdder.matchCustomItemName(i, s))
          || !ItemsAdder.isCustomItem(i) && i.getType().toString().equals(s))) {
        if (i.getAmount() > toRemove) {
          i.setAmount(i.getAmount() - toRemove);
          toRemove = 0;
        } else {
          toRemove -= i.getAmount();
          i.setAmount(0);
        }
        amounts.put(s, toRemove);
      }
    }
  }
  private ArrayList<ItemStack> fuseItems(ArrayList<ItemStack> items,
      HashMap<String, Integer>[] recipe,
      HashMap<String, Integer> counts) {
    HashMap<String, Integer> inputs = recipe[0];
    HashMap<String, Integer> products = recipe[1];
    if (counts == null) {
      counts = getItemCounts(items);
    }
    Bukkit.getLogger().info("Fusion");
    Bukkit.getLogger().info(counts.toString());
    int min = Integer.MAX_VALUE;
    //Work out how many of the product can be made
    for (Entry<String, Integer> entry : counts.entrySet()) {
      String key = entry.getKey();
      if (!inputs.containsKey(key)) {
        continue;
      }
      Integer value = entry.getValue();
      int temp = value / inputs.get(key); //Divide by number of item required for recipe
      min = min < temp ? min : temp;
    }
    if (min == Integer.MAX_VALUE) {
      min = 0;
    }
    final int productAmounts = min;
    Bukkit.getLogger().info(Integer.toString(productAmounts));
    //Ensure the right amount of each item is removed
    for (Entry<String, Integer> e : counts.entrySet()) {
      String key = e.getKey();
      if (inputs.containsKey(key)) {
        counts.put(key, productAmounts * inputs.get(key));
      }
    }
    //Remove items used
    removeItems(items, counts);

    ArrayList<ItemStack> toDrop = new ArrayList<>();
    ItemStack item;
    for (Entry<String, Integer> entry : products.entrySet()) {
      String s = entry.getKey();
      Integer i = entry.getValue();
      if (ItemsAdder.isCustomItem(s)) {
        item = ItemsAdder.getCustomItem(s);
      } else {
        item = new ItemStack(Material.valueOf(s));
      }
      int max = item.getMaxStackSize();
      int toMake = productAmounts * i;
      while (toMake > 0) {
        if (toMake > max) {
          if (ItemsAdder.isCustomItem(s)) {
            item = ItemsAdder.getCustomItem(s);
          } else {
            item = new ItemStack(Material.valueOf(s));
          }
          item.setAmount(max);
          toMake -= max;
        } else {
          if (ItemsAdder.isCustomItem(s)) {
            item = ItemsAdder.getCustomItem(s);
          } else {
            item = new ItemStack(Material.valueOf(s));
          }
          item.setAmount(toMake);
          toMake = 0;
        }
        toDrop.add(item);
      }
    }
    return toDrop;
  }

}
