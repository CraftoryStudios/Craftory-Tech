package tech.brettsaunders.craftory.api.advancments.triggers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import tech.brettsaunders.craftory.api.advancments.AdvancementItem;

public class InventoryChangedTrigger extends AdvancementTrigger{
  private Set<AdvancementItem> itemStacks;

  public InventoryChangedTrigger(Set<AdvancementItem> itemStacks) {
    super(TriggerType.INVENTORY_CHANGED);
    this.itemStacks = itemStacks;
  }

  public static InventoryChangedTriggerBuilder builder() {
    return new InventoryChangedTriggerBuilder();
  }

  @Override
  protected JsonObject getConditions() {
    JsonObject root = new JsonObject();
    JsonArray itemsJson = new JsonArray();
    itemStacks.stream().map(AdvancementItem::toJson).forEach(itemsJson::add);
    root.add("items",itemsJson);
    return root;
  }


  public static class InventoryChangedTriggerBuilder {

    private ArrayList<AdvancementItem> itemStacks;

    InventoryChangedTriggerBuilder() {
    }

    public InventoryChangedTriggerBuilder itemStack(
        AdvancementItem itemStack) {
      if (this.itemStacks == null) {
        this.itemStacks = new ArrayList<AdvancementItem>();
      }
      this.itemStacks.add(itemStack);
      return this;
    }

    public InventoryChangedTriggerBuilder itemStacks(
        Collection<? extends AdvancementItem> itemStacks) {
      if (this.itemStacks == null) {
        this.itemStacks = new ArrayList<AdvancementItem>();
      }
      this.itemStacks.addAll(itemStacks);
      return this;
    }

    public InventoryChangedTriggerBuilder clearItemStacks() {
      if (this.itemStacks != null) {
        this.itemStacks.clear();
      }
      return this;
    }

    public InventoryChangedTrigger build() {
      Set<AdvancementItem> itemStacks;
      switch (this.itemStacks == null ? 0 : this.itemStacks.size()) {
        case 0:
          itemStacks = java.util.Collections.emptySet();
          break;
        case 1:
          itemStacks = java.util.Collections.singleton(this.itemStacks.get(0));
          break;
        default:
          itemStacks = new java.util.LinkedHashSet<AdvancementItem>(
              this.itemStacks.size() < 1073741824 ? 1 + this.itemStacks.size()
                  + (this.itemStacks.size() - 3) / 3 : Integer.MAX_VALUE);
          itemStacks.addAll(this.itemStacks);
          itemStacks = java.util.Collections.unmodifiableSet(itemStacks);
      }

      return new InventoryChangedTrigger(itemStacks);
    }

    public String toString() {
      return "InventoryChangedTrigger.InventoryChangedTriggerBuilder(itemStacks=" + this.itemStacks
          + ")";
    }
  }
}
