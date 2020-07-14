package tech.brettsaunders.craftory.tech.power.core.block.generators;

import java.util.HashMap;

public class SolidFuelManager {

  private static final HashMap<String, Integer> fuelMap = new HashMap<>();

  static {
    fuelMap.put("COAL", 24000);
    fuelMap.put("COAL_BLOCK", 240000);
    fuelMap.put("LAVA_BUCKET", 300000);
    fuelMap.put("BLAZE_ROD", 36000);
    fuelMap.put("CHARCOAL", 24000);
    fuelMap.put("DRIED_KELP_BLOCK", 60000);

    fuelMap.put("ACACIA_PLANKS", 4500);
    fuelMap.put("BIRCH_PLANKS", 4500);
    fuelMap.put("DARK_OAK_PLANKS", 4500);
    fuelMap.put("JUNGLE_PLANKS", 4500);
    fuelMap.put("OAK_PLANKS", 4500);
    fuelMap.put("SPRUCE_PLANKS", 4500);

    fuelMap.put("ACACIA_SAPLING", 1500);
    fuelMap.put("BIRCH_SAPLING", 1500);
    fuelMap.put("DARK_OAK_SAPLING", 1500);
    fuelMap.put("JUNGLE_SAPLING", 1500);
    fuelMap.put("OAK_SAPLING", 1500);
    fuelMap.put("SPRUCE_SAPLING", 1500);

    fuelMap.put("ACACIA_DOOR", 3000);
    fuelMap.put("BIRCH_DOOR", 3000);
    fuelMap.put("DARK_OAK_DOOR", 3000);
    fuelMap.put("JUNGLE_DOOR", 3000);
    fuelMap.put("OAK_DOOR", 3000);
    fuelMap.put("SPRUCE_DOOR", 3000);

    fuelMap.put("ACACIA_SIGN", 3000);
    fuelMap.put("BIRCH_SIGN", 3000);
    fuelMap.put("DARK_OAK_SIGN", 3000);
    fuelMap.put("JUNGLE_SIGN", 3000);
    fuelMap.put("OAK_SIGN", 3000);
    fuelMap.put("SPRUCE_SIGN", 3000);

    fuelMap.put("ACACIA_BUTTON", 1500);
    fuelMap.put("BIRCH_BUTTON", 1500);
    fuelMap.put("DARK_OAK_BUTTON", 1500);
    fuelMap.put("JUNGLE_BUTTON", 1500);
    fuelMap.put("OAK_BUTTON", 1500);
    fuelMap.put("SPRUCE_BUTTON", 1500);

    fuelMap.put("ACACIA_SLAB", 2250);
    fuelMap.put("BIRCH_SLAB", 2250);
    fuelMap.put("DARK_OAK_SLAB", 2250);
    fuelMap.put("JUNGLE_SLAB", 2250);
    fuelMap.put("OAK_SLAB", 2250);
    fuelMap.put("SPRUCE_SLAB", 2250);

    fuelMap.put("ACACIA_TRAPDOOR", 4500);
    fuelMap.put("BIRCH_TRAPDOOR", 4500);
    fuelMap.put("DARK_OAK_TRAPDOOR", 4500);
    fuelMap.put("JUNGLE_TRAPDOOR", 4500);
    fuelMap.put("OAK_TRAPDOOR", 4500);
    fuelMap.put("SPRUCE_TRAPDOOR", 4500);

    fuelMap.put("ACACIA_STAIRS", 4500);
    fuelMap.put("BIRCH_STAIRS", 4500);
    fuelMap.put("DARK_OAK_STAIRS", 4500);
    fuelMap.put("JUNGLE_STAIRS", 4500);
    fuelMap.put("OAK_STAIRS", 4500);
    fuelMap.put("SPRUCE_STAIRS", 4500);

    fuelMap.put("ACACIA_PRESSURE_PLATE", 4500);
    fuelMap.put("BIRCH_PRESSURE_PLATE", 4500);
    fuelMap.put("DARK_OAK_PRESSURE_PLATE", 4500);
    fuelMap.put("JUNGLE_PRESSURE_PLATE", 4500);
    fuelMap.put("OAK_PRESSURE_PLATE", 4500);
    fuelMap.put("SPRUCE_PRESSURE_PLATE", 4500);

    fuelMap.put("ACACIA_FENCE", 4500);
    fuelMap.put("BIRCH_FENCE", 4500);
    fuelMap.put("DARK_OAK_FENCE", 4500);
    fuelMap.put("JUNGLE_FENCE", 4500);
    fuelMap.put("OAK_FENCE", 4500);
    fuelMap.put("SPRUCE_FENCE", 4500);

    fuelMap.put("ACACIA_FENCE_GATE", 4500);
    fuelMap.put("BIRCH_FENCE_GATE", 4500);
    fuelMap.put("DARK_OAK_FENCE_GATE", 4500);
    fuelMap.put("JUNGLE_FENCE_GATE", 4500);
    fuelMap.put("OAK_FENCE_GATE", 4500);
    fuelMap.put("SPRUCE_FENCE_GATE", 4500);

    fuelMap.put("ACACIA_LOG", 4500);
    fuelMap.put("BIRCH_LOG", 4500);
    fuelMap.put("DARK_OAK_LOG", 4500);
    fuelMap.put("JUNGLE_LOG", 4500);
    fuelMap.put("OAK_LOG", 4500);
    fuelMap.put("SPRUCE_LOG", 4500);

    fuelMap.put("STRIPPED_ACACIA_LOG", 4500);
    fuelMap.put("STRIPPED_BIRCH_LOG", 4500);
    fuelMap.put("STRIPPED_DARK_OAK_LOG", 4500);
    fuelMap.put("STRIPPED_JUNGLE_LOG", 4500);
    fuelMap.put("STRIPPED_OAK_LOG", 4500);
    fuelMap.put("STRIPPED_SPRUCE_LOG", 4500);

    fuelMap.put("ACACIA_BOAT", 18000);
    fuelMap.put("BIRCH_BOAT", 18000);
    fuelMap.put("DARK_OAK_BOAT", 18000);
    fuelMap.put("JUNGLE_BOAT", 18000);
    fuelMap.put("OAK_BOAT", 18000);
    fuelMap.put("SPRUCE_BOAT", 18000);

    fuelMap.put("SCAFFOLDING", 6000);
    fuelMap.put("CRAFTING_TABLE", 4500);
    fuelMap.put("CARTOGRAPHY_TABLE", 4500);
    fuelMap.put("FLETCHING_TABLE", 4500);
    fuelMap.put("SMITHING_TABLE", 4500);
    fuelMap.put("LOOM", 4500);
    fuelMap.put("BOOKSHELF", 4500);
    fuelMap.put("LECTERN", 4500);
    fuelMap.put("COMPOSTER", 4500);
    fuelMap.put("CHEST", 4500);
    fuelMap.put("TRAPPED_CHEST", 4500);
    fuelMap.put("BARREL", 4500);
    fuelMap.put("DAYLIGHT_SENSOR", 4500);
    fuelMap.put("JUKEBOX", 4500);
    fuelMap.put("NOTE_BLOCK", 4500);
    fuelMap.put("BROWN_MUSHROOM_BLOCK", 4500);
    fuelMap.put("MUSHROOM_STEM", 4500);
    fuelMap.put("RED_MUSHROOM_BLOCK", 4500);
    //BANNERS
    fuelMap.put("BOW", 4500);
    fuelMap.put("FISHING_ROD", 4500);
    fuelMap.put("LADDER", 4500);

    fuelMap.put("WOODEN_AXE", 3000);
    fuelMap.put("WOODEN_PICKAXE", 3000);
    fuelMap.put("WOODEN_HOE", 3000);
    fuelMap.put("WOODEN_SHOVEL", 3000);
    fuelMap.put("WOODEN_SWORD", 3000);
    fuelMap.put("BOWEL", 1500);
    fuelMap.put("STICK", 1500);

    fuelMap.put("BLACK_WOOL", 1500);
    fuelMap.put("BLUE_WOOL", 1500);
    fuelMap.put("BROWN_WOOL", 1500);
    fuelMap.put("CYAN_WOOL", 1500);
    fuelMap.put("GRAY_WOOL", 1500);
    fuelMap.put("GREEN_WOOL", 1500);
    fuelMap.put("LIGHT_BLUE_WOOL", 1500);
    fuelMap.put("LIGHT_GREY_WOOL", 1500);
    fuelMap.put("LIME_WOOL", 1500);
    fuelMap.put("MAGENTA_WOOL", 1500);
    fuelMap.put("ORANGE_WOOL", 1500);
    fuelMap.put("PINK_WOOL", 1500);
    fuelMap.put("PURPLE_WOOL", 1500);
    fuelMap.put("RED_WOOL", 1500);
    fuelMap.put("WHITE_WOOL", 1500);
    fuelMap.put("YELLOW_WOOL", 1500);

    fuelMap.put("BLACK_CARPET", 1005);
    fuelMap.put("BLUE_CARPET", 1005);
    fuelMap.put("BROWN_CARPET", 1005);
    fuelMap.put("CYAN_CARPET", 1005);
    fuelMap.put("GRAY_CARPET", 1005);
    fuelMap.put("GREEN_CARPET", 1005);
    fuelMap.put("LIGHT_BLUE_CARPET", 1005);
    fuelMap.put("LIGHT_GREY_CARPET", 1005);
    fuelMap.put("LIME_CARPET", 1005);
    fuelMap.put("MAGENTA_CARPET", 1005);
    fuelMap.put("ORANGE_CARPET", 1005);
    fuelMap.put("PINK_CARPET", 1005);
    fuelMap.put("PURPLE_CARPET", 1005);
    fuelMap.put("RED_CARPET", 1005);
    fuelMap.put("WHITE_CARPET", 1005);
    fuelMap.put("YELLOW_CARPET", 1005);

    fuelMap.put("BAMBOO", 750);
  }

  public static int getFuelEnergy(String fuel) {
    if (fuel.isEmpty()) {
      return 0;
    }
    return fuelMap.getOrDefault(fuel, 0);
  }

}
