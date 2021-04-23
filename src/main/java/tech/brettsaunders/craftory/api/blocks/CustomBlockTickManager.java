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

import static tech.brettsaunders.craftory.api.sentry.SentryLogging.sentryLog;

import com.google.common.collect.Lists;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import lombok.Synchronized;
import org.bukkit.scheduler.BukkitRunnable;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.Utilities;

public class CustomBlockTickManager extends BukkitRunnable {

  //Custom Block in future
  private final HashMap<Class<? extends CustomBlock>, HashMap<Method, Integer>> classCache = new HashMap<>();
  private ConcurrentHashMap <CustomBlock, Integer> backingTrackedBlock = new ConcurrentHashMap<>();
  private final Set<CustomBlock> trackedBlocks;
  private long tick = 0;

  private boolean autoSave = false;
  private int autoSaveInternal;

  public CustomBlockTickManager() {
    if (Utilities.config.isInt("general.autoSaveInterval") && Utilities.config.getInt("general.autoSaveInterval") != 0) {
      autoSaveInternal = 1200 * Utilities.config.getInt("general.autoSaveInterval");
      autoSave = true;
    }
    trackedBlocks = backingTrackedBlock.newKeySet();
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
            sentryLog(e);
            e.printStackTrace();
          }
        }
      }));
    }
    if (autoSave && tick % autoSaveInternal == 0) {
      //Auto Save
      Craftory.customBlockManager.autoSave();
    }
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
