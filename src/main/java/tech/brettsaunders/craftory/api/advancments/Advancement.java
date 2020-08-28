package tech.brettsaunders.craftory.api.advancments;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Singular;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import tech.brettsaunders.craftory.api.advancments.triggers.LocationTrigger;
import tech.brettsaunders.craftory.api.advancments.triggers.AdvancementTrigger;
import tech.brettsaunders.craftory.utils.Logger;

@Builder
@Getter
public class Advancement {
  //Icon
  @Builder.Default private String itemID = "minecraft:stone";
  @Builder.Default private String itemNBT = "";

  //General
  @Builder.Default private String title ="";
  private Advancement parent;
  @Builder.Default private String description ="";

  //Display
  @Builder.Default private Frame frame = Frame.TASK;
  private String background;

  //Messages
  @Builder.Default private boolean toast = true;
  @Builder.Default private boolean announce = true;
  @Builder.Default private boolean hidden = false;

  //Criteria
  @Singular private Map<String, AdvancementTrigger> triggers;

  public NamespacedKey getNamespaceKey() {
    return new NamespacedKey("craftory",title.replaceAll("\\s", "").toLowerCase());
  }

  public String toJson() {
    JsonObject json = new JsonObject();

    //Parent
    if (parent != null) {
      json.addProperty("parent",parent.getNamespaceKey().toString());
    }

    //Icon
    JsonObject iconJson = new JsonObject();
    iconJson.addProperty("item",itemID);
    iconJson.addProperty("nbt",itemNBT);

    //Display
    JsonObject displayJson = new JsonObject();
    displayJson.addProperty("title",title);
    displayJson.addProperty("description", description);
    displayJson.addProperty("frame",frame.getValue());
    if (background != null && parent == null) {
      displayJson.addProperty("background",background);
    }
    displayJson.addProperty("show_toast",toast);
    displayJson.addProperty("announce_to_chat",announce);
    displayJson.addProperty("hidden",hidden);
    displayJson.add("icon",iconJson);
    json.add("display",displayJson);

    //Criteria
    JsonObject criteriaJson = new JsonObject();
    for (Map.Entry<String, AdvancementTrigger> entry : triggers.entrySet()) {
      criteriaJson.add(entry.getKey(), entry.getValue().toJson());
    }
    json.add("criteria", criteriaJson);

    //JSON pretty-print
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    return gson.toJson(json);
  }

  public Advancement register() {
    try {
      if (Bukkit.getAdvancement(getNamespaceKey()) == null) {
        Bukkit.getUnsafe().loadAdvancement(getNamespaceKey(), toJson());
      }
    } catch (Exception e) {
      Logger.error("Failed to register advancement: "+getNamespaceKey());
      Logger.info(toJson());
      e.printStackTrace();
    }
    return this;
  }

}
