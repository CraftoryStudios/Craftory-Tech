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

  private void wandUsed(Block block) {
    float spell_range = 1.5f;
    Bukkit.getLogger().info("Wand stuff");
    ArrayList<ItemStack> items = getItemsInRadius(block.getLocation().clone().add(0, 1, 0), spell_range);
    int amount;
    for (ItemStack i : items) {
      amount = i.getAmount();
      amount *= 2;
      amount = (amount > i.getMaxStackSize()) ? i.getMaxStackSize() : amount;
      i.setAmount(amount);
    }
  }

  private HashMap<Material, Integer> getItemCounts(ArrayList<ItemStack> items) {
    HashMap<Material, Integer> counts = new HashMap<>();
    for (ItemStack i : items) { //Count the amount of each item
      Material type = i.getType();
      if (counts.containsKey(i.getType())) {
        counts.put(type, counts.get(type) + i.getAmount());
      } else {
        counts.put(type, i.getAmount());
      }
    }
    return counts;
  }

  private ArrayList<ItemStack> fuseItems(ArrayList<ItemStack> items, HashMap<Material, Integer>[] recipe,
      HashMap<Material, Integer> counts) {
    HashMap<Material, Integer> inputs = recipe[0];
    HashMap<Material, Integer> products = recipe[1];
    if (counts == null) {
      counts = getItemCounts(items);
    }
    int min = Integer.MAX_VALUE;
    for (Entry<Material, Integer> entry : counts
        .entrySet()) { //Work out how many of the product can be made
      Material key = entry.getKey();
      if(!inputs.containsKey(key)) continue;
      Integer value = entry.getValue();
      int temp = value / inputs.get(key); //Divide by number of item required for recipe
      min = min < temp ? min : temp;
    }
    if (min == Integer.MAX_VALUE) {
      min = 0;
    }
    final int productAmounts = min;
    Bukkit.getLogger().info("Fusion");
    Bukkit.getLogger().info(counts.toString());
    Bukkit.getLogger().info(Integer.toString(productAmounts));
    //Ensure the right amount of each item is removed
    for (Entry<Material, Integer> e : counts.entrySet()) {
      Material key = e.getKey();
      counts.put(key, productAmounts * inputs.get(key));
    }
    for (ItemStack i : items) { //Remove items used
      for (Material m : inputs.keySet()) {
        int toRemove = counts.get(m);
        if (toRemove > 0 && i.getType().equals(m)) {
          if (i.getAmount() > toRemove) {
            i.setAmount(i.getAmount() - toRemove);
            toRemove = 0;
          } else {
            toRemove -= i.getAmount();
            i.setAmount(0);
          }
          counts.put(m, toRemove);
        }
      }
    }
    ArrayList<ItemStack> toDrop = new ArrayList<>();
    ItemStack item;
    for (Entry<Material, Integer> entry : products.entrySet()) {
      Material m = entry.getKey();
      Integer i = entry.getValue();
      item = new ItemStack(m);
      int max = item.getMaxStackSize();
      int tomake = productAmounts * i;
      while (tomake > 0) {
        if (tomake > max) {
          item = new ItemStack(m);
          item.setAmount(max);
          tomake -= max;
        } else {
          item = new ItemStack(m);
          item.setAmount(tomake);
          tomake = 0;
        }
        toDrop.add(item);
      }
    }
    return toDrop;
  }

  private void wandUsedCauldron(Block cauldron) {
    Location loc = cauldron.getLocation();
    ArrayList<ItemStack> items = getItemsInRadius(loc, 1.2f);
    HashMap<Material, Integer> counts = getItemCounts(items);

    //Set the recipe
    HashMap<Material, Integer>[] recipe = MagicFusionRecipes.getRecipe(counts);
    if(recipe==null) return;
    ArrayList<ItemStack> toDrop = fuseItems(items, recipe, counts);
    for (ItemStack i : toDrop) {
      cauldron.getWorld().dropItemNaturally(loc, i);
      Location particleLoc = loc.clone().add(0.5,0.75,0.5);
      cauldron.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, particleLoc, 10, 0, 0, 0, 0);
    }
  }
}
