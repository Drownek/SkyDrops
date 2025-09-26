package me.drownek.skydrops.airdrop;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.okaeri.injector.annotation.Inject;
import lombok.Getter;
import me.drownek.platform.bukkit.scheduler.PlatformScheduler;
import me.drownek.platform.core.annotation.Component;
import me.drownek.skydrops.SkyDropsPlugin;
import me.drownek.skydrops.drop.items.DropConfig;
import me.drownek.skydrops.lang.LangConfig;
import me.drownek.skydrops.settings.InGameSettingsConfig;
import me.drownek.skydrops.settings.InGameSettingsType;
import me.drownek.util.message.SendableMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Getter
public class AirdropService implements Closeable {

    private final List<FallingPackageEntity> airDrops = new CopyOnWriteArrayList<>();

    private @Inject PlatformScheduler scheduler;
    private @Inject InGameSettingsConfig inGameSettingsConfig;
    private @Inject SkyDropsPlugin plugin;
    private @Inject DropConfig dropConfig;
    private @Inject LangConfig langConfig;
    private @Inject AirdropConfig airdropConfig;

    public void createAirdrop(@NotNull Location startLocation) {
        int airdropHp = inGameSettingsConfig.getValue(InGameSettingsType.AIRDROP_HP, Integer.class);
        FallingPackageEntity airDrop = new FallingPackageEntity(
            plugin,
            startLocation,
            this,
            dropConfig,
            airdropHp,
            airdropConfig
        );
        airDrop.summon();

        airDrops.add(airDrop);
        broadcastSpawnMessage(startLocation);

        airDrop.setAutoDespawnTime(Instant.now().plus(airdropConfig.autoRemoveChestAfter));
    }

    public void removeAirdrop(@NotNull FallingPackageEntity airdrop) {
        airdrop.getArmorStand().remove();
        cleanupAirdropLocation(airdrop);
        airDrops.remove(airdrop);
        removeHologram(airdrop);
    }

    public void updateHologram(@NotNull FallingPackageEntity airdrop) {
        String hologramId = generateHologramId(airdrop);
        DHAPI.removeHologram(hologramId);

        List<String> hologramLines = createHologramLines(airdrop);
        Location hologramLocation = airdrop.getTarget().clone().add(0, 1.5, 0);

        DHAPI.createHologram(hologramId, hologramLocation, hologramLines);
    }

    private void broadcastSpawnMessage(Location location) {
        SendableMessage message = langConfig.airdropSpawnMessage
            .with("{x}", location.getBlockX())
            .with("{z}", location.getBlockZ());

        Objects.requireNonNull(location.getWorld())
            .getPlayers()
            .forEach(message::sendTo);
    }

    private void cleanupAirdropLocation(FallingPackageEntity airdrop) {
        World world = Objects.requireNonNull(airdrop.getStartLocation().getWorld());
        Block targetBlock = world.getBlockAt(airdrop.getTarget());

        targetBlock.setType(Material.AIR);
        world.playSound(airdrop.getTarget(), Sound.ENTITY_GENERIC_EXPLODE, 3.0F, 0.5F);
    }

    private void removeHologram(FallingPackageEntity airdrop) {
        String hologramId = generateHologramId(airdrop);
        DHAPI.removeHologram(hologramId);
    }

    private String generateHologramId(FallingPackageEntity airdrop) {
        return Integer.toHexString(airdrop.hashCode());
    }

    private List<String> createHologramLines(FallingPackageEntity airdrop) {
        List<String> lines = new ArrayList<>();

        String healthColor = determineHealthColor(airdrop.getHp());
        lines.add(healthColor + airdrop.getHp());
        lines.add(langConfig.clickAction);

        return lines;
    }

    private String determineHealthColor(int hp) {
        if (hp > 100) return "&a&l";
        if (hp > 50) return "&e&l";
        return "&c&l";
    }

    @Override
    public void close() {
        removeAirdrops();
    }

    public void removeAirdrops() {
        airDrops.forEach(this::removeAirdrop);
    }
}