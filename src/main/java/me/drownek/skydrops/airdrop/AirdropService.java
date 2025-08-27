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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Component
public class AirdropService {
    private FallingPackageEntity airDrop;
    private int hp;
    private @Inject PlatformScheduler scheduler;
    private @Inject InGameSettingsConfig inGameSettingsConfig;
    private @Inject SkyDropsPlugin plugin;
    private @Inject DropConfig dropConfig;
    private @Inject LangConfig langConfig;

    public void createAirdrop(final Location location) {
        Objects.requireNonNull(location.getWorld(), "World is null");
        removeAirdrop();

        scheduler.runSync(() -> {
            hp = inGameSettingsConfig.getValue(InGameSettingsType.AIRDROP_HP, Integer.class);
            Location loc = location.clone().add(0, 20, 0);
            airDrop = new FallingPackageEntity(plugin, loc, location, this, dropConfig);

            SendableMessage message = langConfig.airdropSpawnMessage
                .with("{x}", location.getX())
                .with("{z}", location.getZ());
            location.getWorld().getPlayers().forEach(message::sendTo);
        });
    }

    public void removeAirdrop() {
        if (airDrop == null) return;

        airDrop.remove();

        final World world = airDrop.getTarget().getWorld();

        if (world == null) return;

        if (airDrop.getTarget().getWorld().getBlockAt(airDrop.getTarget()).getType() == Material.CHEST) {
            world.playSound(airDrop.getTarget(), Sound.ENTITY_GENERIC_EXPLODE, 3.0F, 0.5F);
        }

        world.getBlockAt(airDrop.getTarget()).setType(Material.AIR);
        airDrop = null;
        updateHologram();
    }

    public void updateHologram() {
        DHAPI.removeHologram("airdrop");

        if (airDrop == null) {
            return;
        }

        List<String> lines = new ArrayList<>();

        final String color;
        if (hp > 100) color = "&a&l";
        else if (hp > 50) color = "&e&l";
        else color = "&c&l";

        lines.add(color + hp);
        lines.add(langConfig.clickAction);

        DHAPI.createHologram("airdrop", airDrop.getTarget().clone().add(0, 1.5f, 0), lines);
    }
}
