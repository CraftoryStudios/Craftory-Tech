package tech.brettsaunders.craftory;

import java.io.Serializable;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import tech.brettsaunders.craftory.BeltManagerContext.Side;

public class BeltManager implements Serializable {

  private int lenght;
  private ArrayList<Location> belts = new ArrayList<>();
  private BeltTree tree;

  public BeltManager (Block block) {
    lenght = 1;
    tree = new BeltTree(block.getLocation());
    Craftory.beltManagers.put(block.getLocation(), this);
    Bukkit.getLogger().info(this.toString());
    belts.add(block.getLocation());
  }

  public int getLenght() {
    return lenght;
  }

  public void removeBelt() {
    lenght = lenght - 1;
  }

  public void addBelt(Block block, Side side, Block leadBlock, ArrayList<BeltManagerContext> currentBeltManagers) {
    Bukkit.getLogger().info(this.toString());
    lenght = lenght + 1;
    Craftory.beltManagers.put(block.getLocation(), this);
    belts.add(block.getLocation());
    BeltNode node = new BeltNode(block.getLocation());
    tree.addToMapper(block.getLocation(), node);

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
        getTree().setRoot(node);
        break;
      case Left:
        tree.addParent(block.getLocation(), node);
        tree.getRoot().setChild(node);
        tree.setRoot(node);
        node.setParentLeft(tree.getRoot());
        break;
      case Right:
        tree.addParent(block.getLocation(), node);
        tree.getRoot().setChild(node);
        tree.setRoot(node);
        node.setParentRight(tree.getRoot());
        break;
      case SidewaysLeft:
        tree.addParent(block.getLocation(), node);
        tree.getNodeAt(leadBlock.getLocation()).setParentLeft(node);
        node.setChild(tree.getNodeAt(leadBlock.getLocation()));
        break;
      case SidewaysRight:
        tree.addParent(block.getLocation(), node);
        tree.getNodeAt(leadBlock.getLocation()).setParentRight(node);
        node.setChild(tree.getNodeAt(leadBlock.getLocation()));
        break;
    }
    Bukkit.getLogger().info("HERE");

    for (BeltManagerContext managerContext : currentBeltManagers) {
      if (managerContext.getBeltManager() == this) continue;
      BeltNode rootNode = managerContext.getBeltManager().getTree().getRoot();
      Bukkit.getLogger().info("Manager: " + managerContext.getBeltManager().toString() + " side: " + managerContext.getSide());
      switch(managerContext.getSide()) {
        case Front:
          BeltNode parent = managerContext.getBeltManager().getTree().getParent(managerContext.getBlock().getLocation());
          parent.setParentBehind(node);
          node.setChild(parent);
          getTree().setRoot(managerContext.getBeltManager().getTree().getRoot());
          managerContext.getBeltManager().getTree().replaceParent(managerContext.getBlock().getLocation(), block.getLocation(), node);
          break;
        case Right:
          rootNode.setChild(node);
          node.setParentRight(rootNode);
          getTree().getParents().putAll(managerContext.getBeltManager().getTree().getParents());
          break;
        case Left:
          rootNode.setChild(node);
          node.setParentLeft(rootNode);
          getTree().getParents().putAll(managerContext.getBeltManager().getTree().getParents());
          break;
        case Back:
          rootNode.setChild(node);
          node.setParentBehind(rootNode);
          getTree().replaceParent(block.getLocation(), managerContext.getBlock().getLocation(), rootNode);
          //getTree().getParents().putAll(managerContext.getBeltManager().getTree().getParents());
          break;
        case SidewaysLeft:
          rootNode.setParentLeft(node);
          node.setChild(rootNode);
          getTree().setRoot(managerContext.getBeltManager().getTree().getRoot());
          getTree().getParents().putAll(managerContext.getBeltManager().getTree().getParents());
          break;
        case SidewaysRight:
          rootNode.setParentRight(node);
          node.setChild(rootNode);
          getTree().setRoot(managerContext.getBeltManager().getTree().getRoot());
          getTree().getParents().putAll(managerContext.getBeltManager().getTree().getParents());
          break;
      }

      for (Location belt : managerContext.getBeltManager().getBelts()) {
        Craftory.beltManagers.replace(belt, this);
        belts.add(belt);
        Bukkit.getLogger().info(belt + "this one");
      }

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
