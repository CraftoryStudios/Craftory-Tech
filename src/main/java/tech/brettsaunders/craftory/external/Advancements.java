/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.external;

import eu.endercentral.crazy_advancements.Advancement;
import eu.endercentral.crazy_advancements.AdvancementDisplay;
import eu.endercentral.crazy_advancements.AdvancementDisplay.AdvancementFrame;
import eu.endercentral.crazy_advancements.AdvancementVisibility;
import eu.endercentral.crazy_advancements.NameKey;
import eu.endercentral.crazy_advancements.manager.AdvancementManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import tech.brettsaunders.craftory.CoreHolder.Blocks;
import tech.brettsaunders.craftory.CoreHolder.Items;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.Utilities;
import tech.brettsaunders.craftory.api.items.CustomItemManager;

public class Advancements implements Listener {

  private AdvancementManager advancementManager;
  private Advancement craftory;

  public Advancements() {
    if (Utilities.advancementManager.isPresent()) {
      Craftory.plugin.getServer().getPluginManager().registerEvents(this, Craftory.plugin);
      advancementManager = Utilities.advancementManager.get();
      registerBase();
    }
  }

  private void registerBase() {

    //Craftory
    AdvancementDisplay advancementDisplayCraftory = new AdvancementDisplay(
        CustomItemManager.getCustomItem(Blocks.EMERALD_CELL), "Craftory",
        "The extended heart and story of the game",
        AdvancementFrame.TASK, true, true, AdvancementVisibility.ALWAYS);
    advancementDisplayCraftory.setBackgroundTexture("textures/block/light_blue_concrete.png");
    craftory = new Advancement(null, new NameKey("craftory", "craftory"),
        advancementDisplayCraftory);

    //A Life Long Attraction
    AdvancementDisplay aLifeLongAttractionDisplay = new AdvancementDisplay(
        CustomItemManager.getCustomItem(Items.MAGNETISED_IRON), "A Life Long Attraction",
        "A relationship that will last the ages", AdvancementFrame.TASK, true, true,
        AdvancementVisibility.VANILLA);
    advancementDisplayCraftory.setCoordinates(0, 1);
    Advancement aLifeLongAttraction = new Advancement(craftory,
        new NameKey("craftory", "aLifeLongAttraction"), aLifeLongAttractionDisplay);
    aLifeLongAttraction.setCriteria(1);

    //A New Foundation
    AdvancementDisplay aNewFoundationDisplay = new AdvancementDisplay(
        CustomItemManager.getCustomItem(Blocks.IRON_FOUNDRY), "A New Foundation",
        "Time to make steel", AdvancementFrame.TASK, true, true, AdvancementVisibility.VANILLA);
    aNewFoundationDisplay.setCoordinates(1, 0);
    Advancement aNewFoundation = new Advancement(craftory,
        new NameKey("craftory", "aNewFoundation"), aNewFoundationDisplay);
    aNewFoundation.setCriteria(1);

    //Acquiring Better Hardware
    AdvancementDisplay acquiringBetterHardwareDisplay = new AdvancementDisplay(
        CustomItemManager.getCustomItem(Items.STEEL_INGOT), "Acquiring Better Hardware",
        "Steel is the next step from Iron", AdvancementFrame.TASK, true, true,
        AdvancementVisibility.VANILLA);
    acquiringBetterHardwareDisplay.setCoordinates(2, 0);
    Advancement acquiringBetterHardware = new Advancement(aNewFoundation,
        new NameKey("craftory", "acquiringBetterHardware"), acquiringBetterHardwareDisplay);
    acquiringBetterHardware.setCriteria(1);

    //At The Core Of Things
    AdvancementDisplay atTheCoreOfThingsDisplay = new AdvancementDisplay(
        CustomItemManager.getCustomItem(Items.IRON_CORE), "At The Core Of Things",
        "The heart of all machines", AdvancementFrame.TASK, true, true,
        AdvancementVisibility.VANILLA);
    atTheCoreOfThingsDisplay.setCoordinates(3, 0);
    Advancement atTheCoreOfThings = new Advancement(acquiringBetterHardware,
        new NameKey("craftory", "atTheCoreOfThings"), atTheCoreOfThingsDisplay);
    atTheCoreOfThings.setCriteria(1);

    //Great Power, Brings Great Responsibility!
    AdvancementDisplay greatPowerBringsGreatResponsibilityDisplay = new AdvancementDisplay(
        CustomItemManager.getCustomItem(Blocks.SOLID_FUEL_GENERATOR),
        "Great Power, Brings Great Responsibility", "Life made easier with electricity",
        AdvancementFrame.TASK, true, true, AdvancementVisibility.VANILLA);
    greatPowerBringsGreatResponsibilityDisplay.setCoordinates(4, 0);
    Advancement greatPowerBringsGreatResponsibility = new Advancement(atTheCoreOfThings,
        new NameKey("craftory", "greatPowerBringsGreatResponsibility"),
        greatPowerBringsGreatResponsibilityDisplay);
    greatPowerBringsGreatResponsibility.setCriteria(1);

    //The Energy Piggy Bank
    AdvancementDisplay theEnergyPiggyBankDisplay = new AdvancementDisplay(
        CustomItemManager.getCustomItem(Blocks.IRON_CELL), "The Energy Piggy Bank",
        "A place to store all your Energy", AdvancementFrame.TASK, true, true,
        AdvancementVisibility.VANILLA);
    theEnergyPiggyBankDisplay.setCoordinates(4, -1);
    Advancement theEnergyPiggyBank = new Advancement(atTheCoreOfThings,
        new NameKey("craftory", "theEnergyPiggyBank"), theEnergyPiggyBankDisplay);
    theEnergyPiggyBank.setCriteria(1);

    //Things Are Heating Up
    AdvancementDisplay thingsAreHeatingUpDisplay = new AdvancementDisplay(
        CustomItemManager.getCustomItem(Blocks.IRON_ELECTRIC_FURNACE), "Things Are Heating Up",
        "Smelting made easy", AdvancementFrame.TASK, true, true, AdvancementVisibility.VANILLA);
    thingsAreHeatingUpDisplay.setCoordinates(4, 1);
    Advancement thingsAreHeatingUp = new Advancement(atTheCoreOfThings,
        new NameKey("craftory", "thingsAreHeatingUp"), thingsAreHeatingUpDisplay);
    thingsAreHeatingUp.setCriteria(1);

    advancementManager
        .addAdvancement(craftory, aLifeLongAttraction, aNewFoundation, acquiringBetterHardware,
            atTheCoreOfThings, greatPowerBringsGreatResponsibility, theEnergyPiggyBank,
            thingsAreHeatingUp);
  }

  @EventHandler
  public void addAdvancementsOnJoin(PlayerJoinEvent e) {
    Bukkit.getScheduler().scheduleSyncDelayedTask(Craftory.plugin, () -> {
      advancementManager.addPlayer(e.getPlayer());
      advancementManager.loadProgress(e.getPlayer(), "craftory");
      if (craftory.isGranted(e.getPlayer())) {
        advancementManager.grantAdvancement(e.getPlayer().getUniqueId(), craftory);
      }
    }, 5);
  }

  @EventHandler
  public void removeAdvancementsOnQuit(PlayerQuitEvent e) {
    Bukkit.getScheduler().scheduleSyncDelayedTask(Craftory.plugin, () -> {
      advancementManager.saveProgress(e.getPlayer(), "craftory");
      advancementManager.removePlayer(e.getPlayer());
    }, 5);
  }

  @EventHandler
  public void onDisable(PluginDisableEvent e) {
    Craftory.plugin.getServer().getOnlinePlayers().forEach(player -> {
      advancementManager.saveProgress(player, "craftory");
    });
  }
}
