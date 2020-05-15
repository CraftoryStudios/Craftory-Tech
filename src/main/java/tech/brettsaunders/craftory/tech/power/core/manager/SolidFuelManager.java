package tech.brettsaunders.craftory.tech.power.core.manager;

import com.google.common.collect.ImmutableSet;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.util.Set;
import org.bukkit.inventory.ItemStack;

public class SolidFuelManager {
  private static TObjectIntHashMap<ItemStack> fuelMap = new TObjectIntHashMap<>();
  private static TObjectIntHashMap<ItemStack> gemFuelMap = new TObjectIntHashMap<>();

  public static int DEFAULT_ENERGY = 30000;

  public static Set<ItemStack> getFuels() {

    return ImmutableSet.copyOf(fuelMap.keySet());
  }

  public static Set<ItemStack> getGemFuels() {

    return ImmutableSet.copyOf(gemFuelMap.keySet());
  }

  public static int getFuelEnergy(ItemStack stack) {

    if (stack == null) {
      return 0;
    }
    return fuelMap.get(new ItemStack(stack));
  }

  public static int getGemFuelEnergy(ItemStack stack) {

    if (stack == null) {
      return 0;
    }
    return gemFuelMap.get(new ItemStack(stack));
  }

  /* ADD FUELS */
  public static boolean addFuel(ItemStack stack, int energy) {

    if (stack == null || energy < 2000 || energy > 200000000) {
      return false;
    }
    if (fuelMap.containsKey(new ItemStack(stack))) {
      return false;
    }
    fuelMap.put(new ItemStack(stack), energy);
    return true;
  }

  public static boolean addGemFuel(ItemStack stack, int energy) {

    if (stack == null || energy < 2000 || energy > 200000000) {
      return false;
    }
    if (gemFuelMap.containsKey(new ItemStack(stack))) {
      return false;
    }
    gemFuelMap.put(new ItemStack(stack), energy);
    return true;
  }

  /* REMOVE FUELS */
  public static boolean removeFuel(ItemStack stack) {

    fuelMap.remove(new ItemStack(stack));
    return true;
  }

  public static boolean removeGemFuel(ItemStack stack) {

    gemFuelMap.remove(new ItemStack(stack));
    return true;
  }

}
