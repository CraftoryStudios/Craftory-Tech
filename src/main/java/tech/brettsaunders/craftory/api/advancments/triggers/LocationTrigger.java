package tech.brettsaunders.craftory.api.advancments.triggers;

import com.google.gson.JsonObject;
import java.util.Objects;
import org.bukkit.Location;
import tech.brettsaunders.craftory.api.advancments.triggers.AdvancementTrigger.TriggerType;

public class LocationTrigger extends AdvancementTrigger{
  private Location location;

  public LocationTrigger(Location location) {
    super(TriggerType.LOCATION);
    this.location = location;
  }

  public LocationTrigger() {
    super(TriggerType.LOCATION);
  }

  @Override
  protected JsonObject getConditions() {
    JsonObject root = new JsonObject();
    if (Objects.nonNull(location)) {
      //Location x
      JsonObject x = new JsonObject();
      x.addProperty("min",location.getX());
      x.addProperty("max",location.getX());
      //Location y
      JsonObject y = new JsonObject();
      y.addProperty("min",location.getY());
      y.addProperty("max",location.getY());
      //Location z
      JsonObject z = new JsonObject();
      z.addProperty("min",location.getZ());
      z.addProperty("max",location.getZ());

      //Location
      JsonObject loc = new JsonObject();
      loc.add("x",x);
      loc.add("y",y);
      loc.add("z",z);
      root.add("position",loc);
    }
    return root;
  }
}
