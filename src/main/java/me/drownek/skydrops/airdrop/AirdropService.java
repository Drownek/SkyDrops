package me.drownek.skydrops.airdrop;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.okaeri.injector.annotation.Inject;
import lombok.Getter;
import lombok.Setter;
import me.drownek.platform.bukkit.scheduler.PlatformScheduler;
import me.drownek.platform.core.annotation.Component;
import me.drownek.skydrops.SkyDropsPlugin;
import me.drownek.skydrops.drop.items.DropConfig;
import me.drownek.skydrops.lang.LangConfig;
import me.drownek.skydrops.settings.InGameSettingsConfig;
import me.drownek.skydrops.settings.InGameSettingsType;
import me.drownek.util.message.SendableMessage;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Component
public class AirdropService {
    private List<FallingPackageEntity> airDrops = new ArrayList<>();
    private @Inject PlatformScheduler scheduler;
    private @Inject InGameSettingsConfig inGameSettingsConfig;
    private @Inject SkyDropsPlugin plugin;
    private @Inject DropConfig dropConfig;
    private @Inject LangConfig langConfig;
    private @Inject AirdropConfig airdropConfig;

    public void createAirdrop(@NotNull Location startLocation) {
        Objects.requireNonNull(startLocation.getWorld(), "World is null");

        var hp = inGameSettingsConfig.getValue(InGameSettingsType.AIRDROP_HP, Integer.class);
        airDrops.add(new FallingPackageEntity(plugin, startLocation, this, dropConfig, hp, airdropConfig));

        SendableMessage message = langConfig.airdropSpawnMessage
            .with("{x}", startLocation.getBlockX())
            .with("{z}", startLocation.getBlockZ());
        startLocation.getWorld().getPlayers().forEach(message::sendTo);
    }

    public void removeAirdrop(@NotNull FallingPackageEntity airDrop) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            airDrop.remove();

            World world = Objects.requireNonNull(airDrop.getStartLoc().getWorld());

            Block block = world.getBlockAt(airDrop.getTarget());
            block.setType(Material.AIR);
            world.playSound(airDrop.getTarget(), Sound.ENTITY_GENERIC_EXPLODE, 3.0F, 0.5F);
            airDrops.remove(airDrop);
            String hologramName = Integer.toHexString(airDrop.hashCode());
            DHAPI.removeHologram(hologramName);
        });
    }

    public void updateHologram(@NotNull FallingPackageEntity airDrop) {
        String hologramName = Integer.toHexString(airDrop.hashCode());
        DHAPI.removeHologram(hologramName);

        List<String> lines = new ArrayList<>();

        final String color;
        if (airDrop.getHp() > 100) color = "&a&l";
        else if (airDrop.getHp() > 50) color = "&e&l";
        else color = "&c&l";

        lines.add(color + airDrop.getHp());
        lines.add(langConfig.clickAction);

        DHAPI.createHologram(hologramName, airDrop.getTarget().clone().add(0, 1.5f, 0), lines);
    }
}
