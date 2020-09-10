/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.api.menu;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;

import lombok.Getter;
import lombok.NonNull;

/**
 * A read-only collection of {@link Material}.
 * Internally represented by an Array of {@link Material}.
 *
 * @author TheBusyBiscuit
 *
 */
public class MaterialCollection implements Iterable<Material> {

  @Getter
  private final Material[] asArray;

  public MaterialCollection(@NonNull Collection<Material> materials) {
    this(materials.stream());
  }

  public MaterialCollection(Material... materials) {
    this(Arrays.stream(materials));
  }

  public MaterialCollection(Tag<Material> tag) {
    this(tag != null ? tag.getValues() : new HashSet<>());
  }

  public MaterialCollection(@NonNull Stream<Material> stream) {
    this.asArray = stream.distinct().filter(Objects::nonNull).toArray(Material[]::new);
  }

  public MaterialCollection merge(@NonNull MaterialCollection collection) {
    return new MaterialCollection(Stream.concat(stream(), collection.stream()));
  }

  public Stream<Material> stream() {
    return Arrays.stream(asArray);
  }

  public int size() {
    return asArray.length;
  }

  public boolean isEmpty() {
    return asArray.length == 0;
  }

  public Material get(int index) {
    return asArray[index];
  }

  public boolean contains(Material type) {
    if (type == null) return false;

    for (Material m : asArray) {
      if (m == type) return true;
    }

    return false;
  }

  public boolean containsAll(@NonNull Collection<Material> materials) {
    return materials.stream().allMatch(this::contains);
  }

  public boolean containsAll(@NonNull MaterialCollection materials) {
    return materials.stream().allMatch(this::contains);
  }

  public boolean containsAll(@NonNull Material... materials) {
    return Arrays.stream(materials).allMatch(this::contains);
  }

  public RecipeChoice getAsRecipeChoice() {
    return new MaterialChoice(asArray);
  }

  @Override
  public Iterator<Material> iterator() {
    return stream().iterator();
  }

}