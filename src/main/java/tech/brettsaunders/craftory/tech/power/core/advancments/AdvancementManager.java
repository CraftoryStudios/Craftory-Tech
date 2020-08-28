package tech.brettsaunders.craftory.tech.power.core.advancments;

import tech.brettsaunders.craftory.CoreHolder.Blocks;
import tech.brettsaunders.craftory.CoreHolder.Items;
import tech.brettsaunders.craftory.api.advancments.Advancement;
import tech.brettsaunders.craftory.api.advancments.AdvancementItem;
import tech.brettsaunders.craftory.api.advancments.triggers.InventoryChangedTrigger;
import tech.brettsaunders.craftory.api.advancments.triggers.LocationTrigger;
import tech.brettsaunders.craftory.api.items.CustomItemManager;

public class AdvancementManager {

  public void register() {
    //Root
    Advancement craftory = Advancement
        .builder()
        .itemID("minecraft:stone")
        .itemNBT("{CustomModelData:1001}")
        .title("Craftory")
        .description("The extended heart and story of the game.")
        .background("minecraft:textures/gui/advancements/backgrounds/stone.png")
        .trigger("auto",new LocationTrigger())
        .toast(false)
        .announce(false)
        .build()
        .register();

    //Magnetised Iron <- Craftory
    InventoryChangedTrigger magnestiedIronTrigger = InventoryChangedTrigger.builder().itemStack(
        new AdvancementItem("minecraft:iron_ingot",1,"{CustomModelData:1001}")).build();

    Advancement magnestiedIron = Advancement
        .builder()
        .parent(craftory)
        .trigger("itemGot",magnestiedIronTrigger)
        .itemID("minecraft:iron_ingot")
        .itemNBT("{CustomModelData:1001}")
        .title("A Life Long Attraction")
        .description("A relationship that will last the ages.")
        .build()
        .register();

    //Iron Foundry <- Craftory
    InventoryChangedTrigger ironFoundryTrigger = InventoryChangedTrigger.builder().itemStack(
        new AdvancementItem("minecraft:stone",1,"{CustomModelData:10015}")).build();

    Advancement ironFoundry = Advancement
        .builder()
        .parent(craftory)
        .trigger("itemGot",ironFoundryTrigger)
        .itemID("minecraft:stone")
        .itemNBT("{CustomModelData:10015}")
        .title("A New Foundation")
        .description("Time to make steel")
        .build()
        .register();

    //Steel Ingot <- IronFoundry
    InventoryChangedTrigger steelTrigger = InventoryChangedTrigger.builder().itemStack(
        new AdvancementItem("minecraft:lime_dye",1,"{CustomModelData:1001}")).build();

    Advancement steelIngot = Advancement
        .builder()
        .trigger("itemGot",steelTrigger)
        .parent(ironFoundry)
        .itemID("minecraft:lime_dye")
        .itemNBT("{CustomModelData:1001}")
        .title("Acquiring Better Hardware")
        .description("Steel is the next step from Iron")
        .build()
        .register();
  }

}
