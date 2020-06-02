package tech.brettsaunders.craftory.tech.power.api.guiComponents;

import java.util.ArrayList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.tech.power.api.interfaces.IGUIComponent;

public class GOutputConfig implements IGUIComponent, Listener {

  private static final int NORTH_SLOT = 31;
  private static final int EAST_SLOT = 33;
  private static final int SOUTH_SLOT = 35;
  private static final int WEST_SLOT = 49;
  private static final int UP_SLOT = 15;
  private static final int DOWN_SLOT = 51;

  private Inventory inventory;
  private ArrayList<Integer> config;

  public GOutputConfig(Inventory inventory, ArrayList<Integer> config) {
    this.inventory = inventory;
    this.config = config;
    Craftory.getInstance().getServer().getPluginManager().registerEvents(this, Craftory.getInstance());
  }

  @Override
  public void update() {

  }
}
