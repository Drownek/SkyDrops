package me.drownek.skydrops.airdrop;

import eu.okaeri.injector.annotation.Inject;
import me.drownek.platform.bukkit.scheduler.PlatformScheduler;
import me.drownek.platform.core.annotation.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class AirdropListener implements Listener {

    private final List<UUID> playersOnDelay = new ArrayList<>();

    private @Inject AirdropService airdropService;
    private @Inject PlatformScheduler scheduler;

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (airdropService.getAirDrops().stream().anyMatch(airDrop -> block.equals(airDrop.getTargetBlock()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Entity rightClicked = event.getRightClicked();
        if (airdropService.getAirDrops().stream().anyMatch(airDrop -> rightClicked.equals(airDrop.armorStand))) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        UUID playerUUID = event.getPlayer().getUniqueId();
        Location clickedBlockLocation = event.getClickedBlock().getLocation();
        World world = Objects.requireNonNull(clickedBlockLocation.getWorld());

        airdropService.getAirDrops().forEach(airDrop -> {
            Block targetBlock = airDrop.getTargetBlock();
            if (!clickedBlockLocation.equals(targetBlock.getLocation())) {
                return;
            }
            if (airDrop.getHp() != 0) {
                event.setCancelled(true);
            } else {
                scheduleChestRemoval(airDrop);
            }
            if (!playersOnDelay.contains(playerUUID) && airDrop.getHp() > 0) {
                world.playSound(clickedBlockLocation, Sound.ENTITY_BAT_TAKEOFF, 3.0F, 0.5F);

                airDrop.setHp(airDrop.getHp() - 1);

                playersOnDelay.add(playerUUID);
                scheduler.runLaterSync(() -> playersOnDelay.remove(playerUUID), 20L);
                airdropService.updateHologram(airDrop);
            }
        });
    }

    private void scheduleChestRemoval(FallingPackageEntity fallingPackageEntity) {
        scheduler.runLaterSync(() -> {
            Location target = fallingPackageEntity.getTarget();
            World world = Objects.requireNonNull(target.getWorld());
            Chest chest = (Chest) world.getBlockAt(target).getState();
            if (chest.getInventory().isEmpty()) {
                airdropService.removeAirdrop(fallingPackageEntity);
            } else {
                scheduleChestRemoval(fallingPackageEntity);
            }
        }, 2 * 20L);
    }
}
