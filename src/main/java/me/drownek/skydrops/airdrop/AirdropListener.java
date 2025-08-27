package me.drownek.skydrops.airdrop;

import eu.okaeri.injector.annotation.Inject;
import me.drownek.platform.bukkit.scheduler.PlatformScheduler;
import me.drownek.platform.core.annotation.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
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
        if (airdropService.getAirDrop() == null) return;

        if (event.getBlock().getLocation().clone().add(0.5, 0, 0.5).equals(airdropService.getAirDrop().getTarget())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void OnInteractAtEntity(PlayerInteractAtEntityEvent e) {
        FallingPackageEntity airDrop = airdropService.getAirDrop();

        if (airDrop == null) return;

        if (e.getRightClicked().equals(airDrop.armorStand)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {

        if (event.getClickedBlock() == null) {
            return;
        }

        if (airdropService.getAirDrop() == null) {
            return;
        }

        Location location1 = event.getClickedBlock().getLocation().clone().add(0.5, 0, 0.5);
        Location target = airdropService.getAirDrop().getTarget();
        if (!location1.equals(target)) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        UUID playerUUID = event.getPlayer().getUniqueId();

        if (airdropService.getHp() != 0) {
            event.setCancelled(true);
        } else {
            scheduleChestRemoval();
        }

        if (!playersOnDelay.contains(playerUUID) && airdropService.getHp() > 0) {
            Location location = event.getClickedBlock().getLocation();
            Objects.requireNonNull(location.getWorld()).playSound(location, Sound.ENTITY_BAT_TAKEOFF, 3.0F, 0.5F);

            airdropService.setHp(airdropService.getHp() - 1);

            playersOnDelay.add(playerUUID);
            scheduler.runLaterSync(() -> playersOnDelay.remove(playerUUID), 20L);
            airdropService.updateHologram();
        }
    }

    private void scheduleChestRemoval() {
        scheduler.runLaterSync(() -> {
            Location target = airdropService.getAirDrop().getTarget();
            Chest chest = (Chest) Objects.requireNonNull(target.getWorld()).getBlockAt(target).getState();
            if (chest.getInventory().isEmpty()) {
                airdropService.removeAirdrop();
            } else {
                scheduleChestRemoval();
            }
        }, 2 * 20L);
    }
}
