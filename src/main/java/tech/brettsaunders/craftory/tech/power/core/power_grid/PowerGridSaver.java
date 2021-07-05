/*******************************************************************************
 * Copyright (c) 2021. Brett Saunders & Matthew Jones - All Rights Reserved
 ******************************************************************************/

package tech.brettsaunders.craftory.tech.power.core.power_grid;

import java.util.HashMap;
import java.util.HashSet;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import tech.brettsaunders.craftory.persistence.Persistent;

@AllArgsConstructor
@NoArgsConstructor
public class PowerGridSaver {

  @Persistent
  protected HashMap<PowerGrid, HashSet<Location>> data = new HashMap<>();

}
