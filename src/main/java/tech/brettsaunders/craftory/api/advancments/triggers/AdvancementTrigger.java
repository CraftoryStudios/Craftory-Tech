package tech.brettsaunders.craftory.api.advancments.triggers;

import com.google.gson.JsonObject;
import lombok.NonNull;
import org.apache.commons.lang.Validate;

public abstract class AdvancementTrigger {
  private final TriggerType triggerType;

  protected AdvancementTrigger(@NonNull TriggerType triggerType) {
    this.triggerType = triggerType;
  }

  public final JsonObject toJson() {
    JsonObject root = new JsonObject();
    root.addProperty("trigger",triggerType.toString());
    root.add("conditions",getConditions());
    return root;
  }

  protected abstract JsonObject getConditions();

  public enum TriggerType {
    INVENTORY_CHANGED,
    LOCATION,
    PLACED_BLOCK;

    @Override
    public String toString() {
      return "minecraft:" + name().toLowerCase();
    }
  }
  }
