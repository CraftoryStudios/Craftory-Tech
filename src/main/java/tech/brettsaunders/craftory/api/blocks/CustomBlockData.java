package tech.brettsaunders.craftory.api.blocks;

public class CustomBlockData {
  public final boolean UP;
  public final boolean DOWN;
  public final boolean NORTH;
  public final boolean EAST;
  public final boolean SOUTH;
  public final boolean WEST;

  public CustomBlockData(boolean up, boolean down, boolean north, boolean east, boolean south,
      boolean west){
    this.UP = up;
    this.DOWN = down;
    this.NORTH = north;
    this.EAST = east;
    this.SOUTH = south;
    this.WEST = west;
  }
}
