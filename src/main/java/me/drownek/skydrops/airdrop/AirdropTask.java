package me.drownek.skydrops.airdrop;

import eu.okaeri.injector.annotation.Inject;
import lombok.Setter;
import me.drownek.platform.bukkit.annotation.Scheduled;
import me.drownek.skydrops.lang.LangConfig;
import me.drownek.skydrops.settings.InGameSettingsConfig;
import me.drownek.skydrops.settings.InGameSettingsType;
import me.drownek.util.LocationUtil;
import me.drownek.util.RandomUtil;
import me.drownek.util.TimeUtil;
import me.drownek.util.message.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Objects;

@Scheduled(rate = 20)
public class AirdropTask extends BukkitRunnable {
    private @Inject Plugin plugin;
    private @Inject InGameSettingsConfig inGameSettingsConfig;
    private @Inject AirdropService airdropService;
    private @Inject AirdropConfig airdropConfig;
    private @Inject LangConfig langConfig;

    private @Setter @Nullable Location nextLocation;
    private @Setter long lastDropTime = System.currentTimeMillis();

    public void run() {
        if (!inGameSettingsConfig.getValue(InGameSettingsType.AIRDROPS, Boolean.class)) {
            return;
        }

        int delay = (int) inGameSettingsConfig.getValue(InGameSettingsType.AIRDROP_DELAY, Duration.class).toMillis();
        long remainingTime = lastDropTime + delay - System.currentTimeMillis();

        if (remainingTime > 0) {
            if (remainingTime <= (int) inGameSettingsConfig.getValue(InGameSettingsType.AIRDROP_ANNOUNCEMENT_TIME, Duration.class).toMillis()) {
                String text = TextUtil.color(langConfig.airdropWillSpawn.replace("{TIME}", TimeUtil.formatTimeMillis(remainingTime)));
                BossBar bar = Bukkit.createBossBar(text, BarColor.RED, BarStyle.SOLID);
                Bukkit.getOnlinePlayers().forEach(bar::addPlayer);
                Bukkit.getServer().getScheduler().runTaskLater(plugin, bar::removeAll, 20L);
            }
            return;
        }

        lastDropTime = System.currentTimeMillis();

        Location location = nextLocation == null ? getRandomLocation().add(0, 20, 0) : nextLocation;
        nextLocation = null;
        airdropService.createAirdrop(location);
    }

    private Location getRandomLocation() {
        final World world = Bukkit.getWorld(airdropConfig.airdropsWorld);
        Objects.requireNonNull(world, "World '" + airdropConfig.airdropsWorld + "' not found");
        double size = world.getWorldBorder().getSize() - 1;
        final int x = RandomUtil.randomInteger(-(int)(size / 2.0), (int)(size / 2.0));
        final int z = RandomUtil.randomInteger(-(int)(size / 2.0), (int)(size / 2.0));
        final double y = world.getHighestBlockYAt(x, z) + 1.0f;
        return new Location(world, x + 0.5f, y, z + 0.5f);
    }
}
