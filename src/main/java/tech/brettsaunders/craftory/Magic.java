package tech.brettsaunders.craftory;

import dev.lone.itemsadder.api.ItemsAdder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import org.bukkit.Location;
import org.bukkit.Material;
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
    System.out.println("Wand stuff");
    ArrayList<ItemStack> items = getItemsInRadius(block.getLocation().add(0, 1, 0), spell_range);
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

  private ArrayList<ItemStack> fuseItems(ArrayList<ItemStack> items,
      HashMap<Material, Integer> inputs, HashMap<Material, Integer> products,
      HashMap<Material, Integer> counts) {
    if (counts == null) {
      counts = getItemCounts(items);
    }
    int min = Integer.MAX_VALUE;
    for (Entry<Material, Integer> entry : counts
        .entrySet()) { //Work out how many of the product can be made
      Material key = entry.getKey();
      Integer value = entry.getValue();
      int temp = value / inputs.get(key); //Divide by number of item required for recipe
      min = min < temp ? min : temp;
    }
    if (min == Integer.MAX_VALUE) {
      min = 0;
    }
    final int productAmounts = min;
    System.out.println("Fusion");
    System.out.println(counts);
    System.out.println(productAmounts);
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
    HashMap<Material, Integer> inputs = new HashMap<>();
    HashMap<Material, Integer> products = new HashMap<>();
    if (counts.containsKey(Material.GOLD_NUGGET) && counts.containsKey(Material.REDSTONE)) {
      inputs.put(Material.GOLD_NUGGET, 1);
      inputs.put(Material.REDSTONE, 1);
      products.put(Material.GLOWSTONE_DUST, 1);
    }

    if (inputs.size() > 0) { //Fuse and spawn the items
      ArrayList<ItemStack> toDrop = fuseItems(items, inputs, products, counts);
      for (ItemStack i : toDrop) {
        cauldron.getWorld().dropItemNaturally(loc, i);
      }
    }
  }
}
