package tech.brettsaunders.extended;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.Location;
import org.bukkit.block.Block;
import tech.brettsaunders.extended.BeltManagerContext.Side;

public class BeltManager{

  private int lenght;
  private ArrayList<Location> belts = new ArrayList<>();
  private BeltTree tree;

  public BeltManager (Block block) {
    lenght = 0;
    tree = new BeltTree(block.getLocation());
  }

  public int getLenght() {
    return lenght;
  }

  public void removeBelt() {
    lenght = lenght - 1;
  }

  public void addBelt(Block block, Side side, Block leadBlock, ArrayList<BeltManagerContext> currentBeltManagers) {
    lenght = lenght + 1;
    Extended.beltManagers.put(block.getLocation(), this);
    belts.add(block.getLocation());
    BeltNode node = new BeltNode(block.getLocation());

    switch (side) {
      case Front:
        BeltNode parent = getTree().getParent(leadBlock.getLocation());
        parent.setParentBehind(node);
        node.setChild(parent);
        getTree().replaceParent(leadBlock.getLocation(), block.getLocation(), node);
        break;
      case Back:
        tree.getRoot().setChild(node);
        node.setParentBehind(tree.getRoot());
        break;
      case Left:
        tree.getRoot().setChild(node);
        node.setParentLeft(tree.getRoot());
        break;
      case Right:
        tree.getRoot().setChild(node);
        node.setParentRight(tree.getRoot());
        break;
    }

    for (BeltManagerContext managerContext : currentBeltManagers) {
      BeltNode rootNode = managerContext.getBeltManager().getTree().getRoot();
      switch(managerContext.getSide()) {
        case Front:
          BeltNode parent = managerContext.getBeltManager().getTree().getParent(managerContext.getBlock().getLocation());
          parent.setParentBehind(node);
          node.setChild(parent);
          managerContext.getBeltManager().getTree().replaceParent(managerContext.getBlock().getLocation(), block.getLocation(), node);
          break;
        case Right:
          rootNode.setChild(node);
          node.setParentRight(rootNode);
          break;
        case Left:
          rootNode.setChild(node);
          node.setParentLeft(rootNode);
          break;
        case Back:
          rootNode.setChild(node);
          node.setParentBehind(rootNode);
          break;
      }

      for (Location belt : managerContext.getBeltManager().getBelts()) {
        Extended.beltManagers.getMap().replace(belt, this);
      }

      belts = (ArrayList<Location>) Stream.of(belts, managerContext.getBeltManager().getBelts()).flatMap(x -> x.stream()).collect(
          Collectors.toList());

      lenght = lenght + managerContext.getBeltManager().getLenght();
    }

  }

  public BeltTree getTree() {
    return tree;
  }

  public ArrayList<Location> getBelts() {
    return belts;
  }
}
