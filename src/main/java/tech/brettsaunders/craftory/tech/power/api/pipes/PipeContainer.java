package tech.brettsaunders.craftory.tech.power.api.pipes;

import de.robotricker.transportpipes.api.DuctExtractEvent;
import de.robotricker.transportpipes.api.DuctInsertEvent;
import de.robotricker.transportpipes.api.TransportPipesContainer;
import de.robotricker.transportpipes.duct.pipe.filter.ItemFilter;
import de.robotricker.transportpipes.location.TPDirection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PipeContainer implements TransportPipesContainer {

  protected final Location location;
  protected final Map<BlockFace, Integer> inputFaces;
  protected final List<Integer> outputSlots;
  protected final Inventory cachedInv;

  public PipeContainer(Location location, Map<BlockFace, Integer> inputFaces, List<Integer> outputSlots, Inventory inventory) {
    this.location = location;
    this.inputFaces = inputFaces;
    this.outputSlots = outputSlots;
    this.cachedInv = inventory;
  }

  @Override
  public ItemStack extractItem(TPDirection extractDirection, int amount, ItemFilter itemFilter) {
    if (!isInLoadedChunk()) {
      return null;
    }
    ItemStack itemTaken = null;
    for (Integer i : outputSlots) {
      if (itemFilter.applyFilter(cachedInv.getItem(i)).getWeight() > 0) {
        int amountBefore = itemTaken != null ? itemTaken.getAmount() : 0;
        if (itemTaken == null) {
          itemTaken = Objects.requireNonNull(cachedInv.getItem(i)).clone();
          itemTaken.setAmount(Math.min(Math.min(amount, itemTaken.getAmount()), itemTaken.getMaxStackSize()));
        } else if (itemTaken.isSimilar(cachedInv.getItem(i))) {
          itemTaken.setAmount(Math.min(Math.min(amount, amountBefore + Objects.requireNonNull(cachedInv.getItem(i)).getAmount()), itemTaken.getMaxStackSize()));
        }
        ItemStack invItem = Objects.requireNonNull(cachedInv.getItem(i)).clone();
        invItem.setAmount(invItem.getAmount() - (itemTaken.getAmount() - amountBefore));
        DuctExtractEvent event = new DuctExtractEvent(cachedInv, invItem);
        Bukkit.getServer().getPluginManager().callEvent(event);
        cachedInv.setItem(i, invItem.getAmount() <= 0 ? null : invItem);

      }
    }
    return itemTaken;
  }

  @Override
  public ItemStack insertItem(TPDirection insertDirection, ItemStack insertion) {
    if (!isInLoadedChunk()) {
      return insertion;
    }
    if (insertion == null) {
      return null;
    }
    if (!inputFaces.containsKey(insertDirection.getBlockFace())) {
      return null;
    }

    Integer i = inputFaces.get(insertDirection.getBlockFace());
    if (spaceForItem(cachedInv.getItem(i), insertion) > 0) {
      DuctInsertEvent insertEvent = new DuctInsertEvent(cachedInv, insertion);
      Bukkit.getServer().getPluginManager().callEvent(insertEvent);
      ItemStack item = cachedInv.getItem(i);
      if (item == null) {
        cachedInv.setItem(i, insertion);
        return null;
      }
      int overflow = (item.getAmount() + insertion.getAmount()) - item.getMaxStackSize();
      item.setAmount(Math.min(item.getAmount() + insertion.getAmount(), item.getMaxStackSize()));
      if (overflow > 0) {
        insertion.setAmount(overflow);
        return insertion;
      }
    }
    return null;
  }

  @Override
  public int spaceForItem(TPDirection insertDirection, ItemStack insertion) {
    int space = 0;

    if (!inputFaces.containsKey(insertDirection.getBlockFace())) {
      return 0;
    }
    Integer i = inputFaces.get(insertDirection.getBlockFace());
    ItemStack item = cachedInv.getItem(i);
    if (item == null || item.getType() == Material.AIR) {
      space += insertion.getMaxStackSize();
    } else if (item.isSimilar(insertion) && item.getAmount() < item.getMaxStackSize()) {
      space += item.getMaxStackSize() - item.getAmount();
    }
    return space;
  }

  @Override
  public boolean isInLoadedChunk() {
    return location.getChunk().isLoaded();
  }

  protected int spaceForItem(ItemStack before, ItemStack put) {
    if (put == null) {
      return 0;
    }
    if (before == null) {
      return put.getMaxStackSize();
    }
    if (!before.isSimilar(put)) {
      return 0;
    }
    if (before.getAmount() >= before.getMaxStackSize()) {
      return 0;
    }
    return before.getMaxStackSize() - before.getAmount();
  }
}
