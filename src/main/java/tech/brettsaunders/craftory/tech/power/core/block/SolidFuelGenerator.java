package tech.brettsaunders.craftory.tech.power.core.block;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashSet;
import org.bukkit.block.BlockFace;
import tech.brettsaunders.craftory.tech.power.api.block.EnergyConfig;
import tech.brettsaunders.craftory.tech.power.api.block.GeneratorBase;

public class SolidFuelGenerator extends GeneratorBase {

  protected static final EnergyConfig ENERGY_CONFIG = new EnergyConfig();

  public static boolean enable = true;
  public static int basePower = 40;

  public SolidFuelGenerator() {
    super();
    //Inv
  }

  @Override
  protected EnergyConfig getEnergyConfig() {
    return ENERGY_CONFIG;
  }

  @Override
  protected boolean canStart() {
    return false;
  }

  @Override
  protected void processStart() {

  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {

  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

  }

  @Override
  public boolean updateOutputCache(BlockFace inputFrom) {
    return false;
  }
}
