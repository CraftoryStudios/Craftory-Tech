package tech.brettsaunders.craftory.api.blocks;

import lombok.Getter;

@Getter
public class StatsContainer {
  //Stats
  private int totalCustomBlocks = 0;
  private int totalPoweredBlocks = 0;
  private int totalCells = 0;
  private int totalGenerators = 0;
  private int totalMachines = 0;
  private int totalPowerConnectors = 0;

  public void increaseTotalCustomBlocks() {
    totalCustomBlocks++;
  }

  public void increaseTotalPoweredBlocks() {
    totalPoweredBlocks++;
  }

  public void increaseTotalCells() {
    totalCells++;
  }

  public void increaseTotalGenerators() {
    totalGenerators++;
  }

  public void increaseTotalMachines() {
    totalMachines++;
  }

  public void increaseTotalPowerConnectors() {
    totalPowerConnectors++;
  }

  public void decreaseTotalCustomBlocks() {
    totalCustomBlocks--;
  }

  public void decreaseTotalPoweredBlocks() {
    totalPoweredBlocks--;
  }

  public void decreaseTotalCells() {
    totalCells--;
  }

  public void decreaseTotalGenerators() {
    totalGenerators--;
  }

  public void decreaseTotalMachines() {
    totalMachines--;
  }

  public void decreaseTotalPowerConnectors() {
    totalPowerConnectors--;
  }

}
