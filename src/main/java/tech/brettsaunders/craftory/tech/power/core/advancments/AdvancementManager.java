package tech.brettsaunders.craftory.tech.power.core.advancments;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.MinecraftKey;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import tech.brettsaunders.craftory.CoreHolder.Blocks;
import tech.brettsaunders.craftory.CoreHolder.Items;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.api.advancments.Advancement;
import tech.brettsaunders.craftory.api.advancments.AdvancementItem;
import tech.brettsaunders.craftory.api.advancments.triggers.InventoryChangedTrigger;
import tech.brettsaunders.craftory.api.advancments.triggers.LocationTrigger;
import tech.brettsaunders.craftory.api.items.CustomItemManager;
import tech.brettsaunders.craftory.packetWrapper.WrapperPlayServerAdvancements;
import tech.brettsaunders.craftory.packetWrapper.WrapperPlayServerAdvancements.SerializedAdvancement;
import tech.brettsaunders.craftory.utils.Logger;

public class AdvancementManager {


  public void register() {
    ProtocolManager manager = ProtocolLibrary.getProtocolManager();
    manager.addPacketListener(new PacketAdapter(Craftory.plugin, ListenerPriority.NORMAL,
        Server.ADVANCEMENTS) {
      @Override
      public void onPacketSending(PacketEvent event) {
        //Wrap Packet
        PacketContainer packetContainer = event.getPacket();
        WrapperPlayServerAdvancements playServerAdvancements =
            new WrapperPlayServerAdvancements(packetContainer);

        //Get advancements
        Optional<Map<MinecraftKey, SerializedAdvancement>> advancementOptional =
            playServerAdvancements.getAdvancements();

        advancementOptional.ifPresent(advancementMap -> {
          ArrayList<String> keys =
              (ArrayList<String>) advancementMap.keySet().stream().filter(Objects::nonNull).map(MinecraftKey::getFullKey).collect(Collectors.toList());
          Logger.info(keys.toString());
        });
      }
    });



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
