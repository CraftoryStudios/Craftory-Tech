package tech.brettsaunders.craftory.api;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;

import java.util.HashMap;
import java.util.Map;

public class Tags {
    private static Map<String, Tag<Material>> tags = new HashMap<>();

    public static Tag<Material> getTag(String tag) {
        return tags.get(tag);
    }

    public Tags() {
        Bukkit.getTags("blocks", Material.class).forEach(tag -> tags.put(tag.getKey().toString(), tag));
        Bukkit.getTags("items", Material.class).forEach(tag -> tags.put(tag.getKey().toString(), tag));
    }
}
