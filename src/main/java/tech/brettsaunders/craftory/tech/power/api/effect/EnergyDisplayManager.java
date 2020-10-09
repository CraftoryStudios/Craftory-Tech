package tech.brettsaunders.craftory.tech.power.api.effect;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.NonNull;
import org.bukkit.block.Block;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import tech.brettsaunders.craftory.Craftory;
import tech.brettsaunders.craftory.api.blocks.CustomBlock;
import tech.brettsaunders.craftory.tech.power.api.block.EnergyStorage;
import tech.brettsaunders.craftory.tech.power.api.block.PoweredBlock;

public class EnergyDisplayManager extends BukkitRunnable {

    private Map<Player, BossBar> bars = new HashMap<>();

    @Override
    public void run() {
        unDisplayEnergyBars();
        Craftory.instance.getServer().getOnlinePlayers().forEach(this::displayEnergyInfo);
    }

    private void unDisplayEnergyBars() {
        bars.forEach(((player, bossBar) -> bossBar.removePlayer(player)));
        bars.clear();
    }

    private void displayEnergyInfo(@NonNull Player player) {
        try {
            Block targetBlock = player.getTargetBlock(null, 8);

            CustomBlock customBlock =
                Craftory.instance.getCustomBlockManager().getCustomBlock(targetBlock.getLocation());
            if (customBlock instanceof PoweredBlock) {
                PoweredBlock poweredBlock = (PoweredBlock) customBlock;
                if (poweredBlock.getEnergyStorage().getMaxEnergyStored() > 0) {
                    EnergyStorage energyStorage = poweredBlock.getEnergyStorage();
                    energyStorage.updateEnergyBar();
                    BossBar bossBar = energyStorage.getEnergyBar();
                    if (Objects.nonNull(bossBar)) {
                        bossBar.addPlayer(player);
                        bars.put(player, bossBar);
                    }
                }
            }
        }catch (IllegalStateException e) {
            //TODO Figure out why exception thrown
        }
    }
}
