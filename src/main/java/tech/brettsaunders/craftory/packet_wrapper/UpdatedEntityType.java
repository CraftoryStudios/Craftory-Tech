/*
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 */

package tech.brettsaunders.craftory.packet_wrapper;

import java.util.HashMap;
import tech.brettsaunders.craftory.utils.Log;

public enum UpdatedEntityType {
  AREA_EFFECT_CLOUD(0),
  ARMOR_STAND(1),
  ARROW(2),
  BAT(3),
  BEE(4),
  BLAZE(5),
  BOAT(6),
  CAT(7),
  CAVE_SPIDER(8),
  CHICKEN(9),
  COD(10),
  COW(11),
  CREEPER(12),
  DOLPHIN(13),
  DONKEY(14),
  DRAGON_FIREBALL(15),
  DROWNED(16),
  ELDER_GUARDIAN(17),
  END_CRTYSTAL(18),
  ENDER_DRAGON(19),
  ENDERMAN(20),
  ENDERMITE(21),
  EVOKER(22),
  EVOKER_FANGS(23),
  EXPERIENCE_ORB(24),
  EYE_OF_ENDER(25),
  FALLING_BLOCK(26),
  FIREWORK_ROCKET(27),
  FOX(28),
  GHAST(29),
  GIANT(30),
  GUARDIAN(31),
  HOGLIN(32),
  HORSE(33),
  HUSK(34),
  ILLUSIONER(35),
  IRON_GOLEM(36),
  ITEM(37),
  ITEM_FRAME(38),
  FIREBALL(39),
  LEASH_KNOT(40),
  LIGHTNING_BOLT(41),
  LLAMA(42),
  LLAMA_SPIT(43),
  MAGMA_CUBE(44),
  MINECART(45),
  CHEST_MINECART(46),
  COMMANDBLOCK_MINECART(47),
  FURNACE_MINECART(48),
  HOPEPR_MINECART(49),
  SPAWNER_MINECART(50),
  TNT_MINECART(51),
  MULE(52),
  MOOSHROOM(53),
  OCELOT(54),
  PAINTING(55),
  PANDA(56),
  PARROT(57),
  PHANTOM(58),
  PIG(59),
  PIGLIN(60),
  PIGLIN_BRUTE(61),
  PILLAGER(62),
  POLAR_BEAR(63),
  TNT(64),
  PUFFER_FISH(65),
  RABBIT(66),
  RAVAGER(67),
  SALMON(68),
  SHEEP(69),
  SHULKER(70),
  SHULKER_BULLET(71),
  SILVERFISH(72),
  SKELETON(73),
  SKELETON_HORSE(74),
  SLIME(75),
  SMALL_FIREBALL(76),
  SNOW_GOLEM(77),
  SNOWBALL(78),
  SPECTRAL_ARROW(79),
  SPIDER(80),
  SQUID(81),
  STRAY(82),
  STRIDER(83),
  EGG(84),
  ENDER_PEARL(85),
  EXPERIENCE_BOTTLE(86),
  POTION(87),
  TRIDENT(88),
  TRADER_LLAMA(89),
  TROPICAL_FISH(90),
  TURTLE(91),
  VEX(92),
  VILLAGER(93),
  VINDICATOR(94),
  WANDERING_TRADER(95),
  WITCH(96),
  WITHER(97),
  WITHER_SKELETON(98),
  WITHER_SKULL(99),
  WOLF(100),
  ZOGLIN(101),
  ZOMBIE(102),
  ZOMBIE_HORSE(103),
  ZOMBIE_VILLAGER(104),
  ZOMBIE_PIGMAN(105),
  PLAYER(106),
  FISHING_BOBBER(107);

  private static final HashMap<Integer,UpdatedEntityType> map = new HashMap<>();
  static {
    for(UpdatedEntityType type: UpdatedEntityType.values()) {
      map.put(type.id,type);
    }
  }
  private int id;

  UpdatedEntityType(int id) {
    this.id =  id;
  }

  public static UpdatedEntityType fromID(int id) {
    if(map.containsKey(id)) return map.get(id);
    Log.error("Failed to create Entity type - Invalid ID");
    return null;
  }

  public int getId() {return id;}
}
