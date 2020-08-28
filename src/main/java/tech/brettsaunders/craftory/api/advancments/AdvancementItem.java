package tech.brettsaunders.craftory.api.advancments;

import com.google.gson.JsonObject;
import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class AdvancementItem {

  private String item;
  private int count;
  private String nbt;

  public JsonObject toJson() {
    JsonObject itemJson = new JsonObject();
    itemJson.addProperty("item",item);
    itemJson.addProperty("count",count);
    itemJson.addProperty("nbt",nbt);
    return itemJson;
  }

}
