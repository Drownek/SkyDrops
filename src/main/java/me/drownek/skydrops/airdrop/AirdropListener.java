package me.drownek.skydrops.airdrop;

import eu.okaeri.injector.annotation.Inject;
import me.drownek.platform.bukkit.scheduler.PlatformScheduler;
import me.drownek.platform.core.annotation.Component;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class AirdropListener implements Listener {

    private static final long INTERACTION_DELAY_TICKS = 20L;
    private static final long CHEST_CHECK_INTERVAL_TICKS = 40L;

    private final List<UUID> playersOnCooldown = new ArrayList<>();

    private @Inject AirdropService airdropService;
    private @Inject PlatformScheduler scheduler;
    private @Inject AirdropConfig airdropConfig;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        
        if (isAirdropBlock(block)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractAtEntityEvent event) {
        Entity entity = event.getRightClicked();
        
        if (isAirdropEntity(entity)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!isValidInteraction(event)) {
            return;
        }

        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();

        if (clickedBlock == null) {
            return;
        }
        processAirdropInteraction(player, clickedBlock, event);
    }

    private boolean isValidInteraction(PlayerInteractEvent event) {
        return event.getClickedBlock() != null && event.getAction() == Action.RIGHT_CLICK_BLOCK;
    }

    private void processAirdropInteraction(Player player, Block clickedBlock, PlayerInteractEvent event) {
        UUID playerId = player.getUniqueId();
        Location blockLocation = clickedBlock.getLocation();
        
        airdropService.getAirDrops().forEach(airdrop -> {
            if (!isTargetBlock(airdrop, blockLocation)) {
                return;
            }

            if (airdrop.getHp() > 0) {
                event.setCancelled(true);
                handleAirdropDamage(airdrop, blockLocation, playerId);
            } else if (airdropConfig.despawnEmptyChests) {
                scheduleChestRemoval(airdrop);
            }
        });
    }

    private boolean isAirdropBlock(Block block) {
        return airdropService.getAirDrops().stream()
            .anyMatch(airdrop -> block.equals(airdrop.getTargetBlock()));
    }

    private boolean isAirdropEntity(Entity entity) {
        return airdropService.getAirDrops().stream()
            .anyMatch(airdrop -> entity.equals(airdrop.armorStand));
    }

    private boolean isTargetBlock(FallingPackageEntity airdrop, Location location) {
        return location.equals(airdrop.getTargetBlock().getLocation());
    }

    private void handleAirdropDamage(FallingPackageEntity airdrop,
                                     Location location, UUID playerId) {
        if (playersOnCooldown.contains(playerId)) {
            return;
        }

        World world = Objects.requireNonNull(location.getWorld());
        world.playSound(location, Sound.ENTITY_BAT_TAKEOFF, 3.0F, 0.5F);

        airdrop.setHp(airdrop.getHp() - 1);
        airdropService.updateHologram(airdrop);

        addPlayerToCooldown(playerId);
    }

    private void addPlayerToCooldown(UUID playerId) {
        playersOnCooldown.add(playerId);
        scheduler.runLaterSync(() -> playersOnCooldown.remove(playerId), INTERACTION_DELAY_TICKS);
    }

    private void scheduleChestRemoval(FallingPackageEntity airdrop) {
        scheduler.runLaterSync(() -> {
            if (isChestEmpty(airdrop)) {
                airdropService.removeAirdrop(airdrop);
            } else {
                scheduleChestRemoval(airdrop);
            }
        }, CHEST_CHECK_INTERVAL_TICKS);
    }

    private boolean isChestEmpty(FallingPackageEntity airdrop) {
        Location target = airdrop.getTarget();
        World world = Objects.requireNonNull(target.getWorld());
        Block block = world.getBlockAt(target);
        
        if (block.getState() instanceof Chest chest) {
            return chest.getInventory().isEmpty();
        }
        
        return true;
    }
}