/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.api.blocks;

import com.google.common.collect.Lists;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import lombok.Synchronized;
import org.bukkit.scheduler.BukkitRunnable;

public class CustomBlockTickManager extends BukkitRunnable {

  //Custom Block in future
  private final HashMap<Class<? extends CustomBlock>, HashMap<Method, Integer>> classCache = new HashMap<>();
  private final HashSet<CustomBlock> trackedBlocks;
  private long tick = 0;

  public CustomBlockTickManager() {
    trackedBlocks = new HashSet(ConcurrentHashMap.newKeySet());
  }

  public static Collection<Method> getMethodsRecursively(@NonNull Class<?> startClass,
      @NonNull Class<?> exclusiveParent) {
    Collection<Method> methods = Lists.newArrayList(startClass.getDeclaredMethods());
    Class<?> parentClass = startClass.getSuperclass();

    if (parentClass != null && !(parentClass.equals(exclusiveParent))) {
      methods.addAll(getMethodsRecursively(parentClass, exclusiveParent));
    }

    return methods;
  }

  @Override
  @Synchronized
  public void run() {
    tick++;
    for (CustomBlock customBlock : trackedBlocks) {
      HashMap<Method, Integer> tickMap = classCache.get(customBlock.getClass());
      tickMap.forEach(((method, current) -> {
        if (tick % current == 0) {
          try {
            method.invoke(customBlock);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }));
    }
  }

  @Synchronized
  public void addTickingBlock(@NonNull CustomBlock block) {
    if (classCache.containsKey(block.getClass())) {
      trackedBlocks.add(block);
    }
  }

  @Synchronized
  public void removeTickingBlock(@NonNull CustomBlock block) {
    trackedBlocks.remove(block);
  }

  @Synchronized
  public void registerCustomBlockClass(@NonNull Class<? extends CustomBlock> clazz) {
    if (!classCache.containsKey(clazz)) {
      Collection<Method> methods = getMethodsRecursively(clazz, Object.class);
      HashMap<Method, Integer> tickingMethods = new HashMap<>();
      methods.forEach(method -> {
        Ticking ticking = method.getAnnotation(Ticking.class);
        if (ticking != null && method.getParameterCount() == 0) {
          tickingMethods.put(method, ticking.ticks());
        }
      });
      if (tickingMethods.size() > 0) {
        classCache.put(clazz, tickingMethods);
      }
    }
  }

  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.RUNTIME)
  public @interface Ticking {

    int ticks();
  }
}
