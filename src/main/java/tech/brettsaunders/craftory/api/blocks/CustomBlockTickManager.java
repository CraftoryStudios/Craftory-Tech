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
import lombok.NonNull;
import lombok.Synchronized;
import org.bukkit.scheduler.BukkitRunnable;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.tech.power.api.block.BlockGUI;
import tech.brettsaunders.craftory.tech.power.api.block.PoweredBlock;
import tech.brettsaunders.craftory.tech.power.core.manager.PoweredBlockManager;

public class CustomBlockTickManager extends BukkitRunnable {

  //Custom Block in future
  private HashMap<Class<? extends BlockGUI>, HashMap<Method, Integer>> classCache = new HashMap<>();
  private HashSet<PoweredBlock> trackedBlocks = new HashSet<>();
  private long tick = 0;
  private CustomBlockManager customBlockManager;
  private PoweredBlockManager poweredBlockManager;

  public CustomBlockTickManager() {
    customBlockManager = Craftory.customBlockManager;
    poweredBlockManager = Craftory.getBlockPoweredManager();
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
  public void run() {
    tick++;
    for (PoweredBlock poweredBlock : trackedBlocks) {
      HashMap<Method, Integer> tickMap = classCache.get(poweredBlock.getClass());
      tickMap.forEach(((method, current) -> {
        if (tick % current == 0) {
          try {
            method.invoke(poweredBlock);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }));
    }
  }

  @Synchronized
  public void addTickingBlock(@NonNull PoweredBlock block) {
    if (classCache.containsKey(block.getClass())) {
      trackedBlocks.add(block);
    }
  }

  @Synchronized
  public void removeTickingBlock(@NonNull PoweredBlock block) {
    trackedBlocks.remove(block);
  }

  @Synchronized
  public void registerCustomBlockClass(@NonNull Class<? extends PoweredBlock> clazz) {
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
