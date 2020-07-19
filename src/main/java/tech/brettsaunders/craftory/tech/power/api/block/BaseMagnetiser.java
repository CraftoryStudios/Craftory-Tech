package tech.brettsaunders.craftory.tech.power.api.block;

import java.util.HashMap;
import org.bukkit.Location;
import tech.brettsaunders.craftory.utils.RecipeUtils;

public class BaseMagnetiser extends BaseOneToOneMachine{


  /* Construction */
  public BaseMagnetiser(Location location, String blockName, byte level) {
    super(location, blockName, level, ENERGY_CONSUMPTION_LEVEL[level] * 5);

  }

  /* Saving, Setup and Loading */
  public BaseMagnetiser() {
    super();
  }

  @Override
  protected HashMap<String,String> getRecipes(){
    return RecipeUtils.getMagnetiserRecipes();
  }

}
