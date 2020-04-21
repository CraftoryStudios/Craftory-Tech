package tech.brettsaunders.craftory;


import org.bukkit.block.Block;

public class BeltManagerContext implements Comparable<BeltManagerContext>{
  private BeltManager beltManager;
  private Side side;
  private boolean leadManager;
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


  public enum Side {
    Front,
    Back,
    Left,
    Right,
    SidewaysLeft,
    SidewaysRight,
    None
  }

  @Override
  public int compareTo(BeltManagerContext o) {
    if (beltManager == o.getBeltManager()) return 0;

    if (beltManager.getLenght() < o.getBeltManager().getLenght()) {
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
}
