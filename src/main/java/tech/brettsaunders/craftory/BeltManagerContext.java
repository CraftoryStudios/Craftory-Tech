package tech.brettsaunders.craftory;


import org.bukkit.block.Block;

public class BeltManagerContext implements Comparable<BeltManagerContext> {

  private final BeltManager beltManager;
  private final Side side;
  private final boolean leadManager;
  private Block block;

  public BeltManagerContext(BeltManager beltManager, Side side) {
    this.beltManager = beltManager;
    this.side = side;
    this.leadManager = false;
  }

  public BeltManagerContext(BeltManager beltManager, Side side, Block block) {
    this.beltManager = beltManager;
    this.side = side;
    this.leadManager = false;
    this.block = block;
  }

  @Override
  public int compareTo(BeltManagerContext o) {
    if (beltManager == o.getBeltManager()) {
      return 0;
    }

    if (beltManager.getLength() < o.getBeltManager().getLength()) {
      return 1;
    } else {
      return -1;
    }
  }

  public BeltManager getBeltManager() {
    return beltManager;
  }

  public Side getSide() {
    return side;
  }

  public Block getBlock() {
    return block;
  }

  public enum Side {
    Front,
    Back,
    Left,
    Right,
    SidewaysLeft,
    SidewaysRight,
    None
  }
}
