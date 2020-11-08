package tech.brettsaunders.craftory.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import tech.brettsaunders.craftory.Constants.INTERACTABLEBLOCK;
import tech.brettsaunders.craftory.tech.power.api.block.EnergyStorage;
import tech.brettsaunders.craftory.tech.power.api.fluids.FluidStorage;
import tech.brettsaunders.craftory.tech.power.core.power_grid.PowerGrid;

public class PersistenceTable {

  public HashMap<String,Class> referenceTable = new HashMap<>();

  public PersistenceTable() {
    //Power Grid
    referenceTable.put("tech.brettsaunders.craftory.tech.power.core.powerGrid.PowerGrid", PowerGrid.class);
    addToTable(PowerGrid.class);
    //Int
    referenceTable.put("int", int.class);
    referenceTable.put("int",Integer.class);
    //InteractableBlock
    referenceTable.put("tech.brettsaunders.craftory.CoreHolder$INTERACTABLEBLOCK", INTERACTABLEBLOCK.class);
    addToTable(INTERACTABLEBLOCK.class);
    addToTable(String.class);
    addToTable(Long.class);
    addToTable(HashMap.class);
    addToTable(Location.class);
    addToTable(HashSet.class);
    addToTable(EnergyStorage.class);
    addToTable(BlockFace.class);
    addToTable(ArrayList.class);
    addToTable(Boolean.class);
    addToTable(FluidStorage.class);
  }

  public void addToTable(Class clazz) {
    referenceTable.put(clazz.getSimpleName(), clazz);
    referenceTable.put(clazz.getName(), clazz);
  }

}
