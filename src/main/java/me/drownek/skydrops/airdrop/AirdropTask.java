package me.drownek.skydrops.airdrop;

import eu.okaeri.injector.annotation.Inject;
import lombok.Setter;
import me.drownek.platform.bukkit.annotation.Scheduled;
import me.drownek.platform.bukkit.scheduler.PlatformScheduler;
import me.drownek.skydrops.lang.LangConfig;
import me.drownek.skydrops.settings.InGameSettingsConfig;
import me.drownek.skydrops.settings.InGameSettingsType;
import me.drownek.util.TimeUtil;
import me.drownek.util.message.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

@Scheduled(rate = 10, async = true)
public class AirdropTask extends BukkitRunnable {
    private @Inject InGameSettingsConfig inGameSettingsConfig;
    private @Inject AirdropService airdropService;
    private @Inject AirdropConfig airdropConfig;
    private @Inject LangConfig langConfig;
    private @Inject PlatformScheduler scheduler;

    @Setter
    private volatile @Nullable Location nextLocation;

    @Setter
    private long lastDropTime = System.currentTimeMillis();

    private final AtomicBoolean airdropExecuting = new AtomicBoolean(false);
    private long lastAnnouncementSecond = -1L;

    @Override
    public void run() {
        if (!isAirdropsEnabled()) {
            return;
        }

        long remaining = calculateRemainingTimeMillis();

        if (remaining > 0) {
            handleAnnouncementIfNeeded(remaining);
            return;
        }

        if (!airdropExecuting.compareAndSet(false, true)) {
            return;
        }

        scheduler.runSync(() -> {
            try {
                if (calculateRemainingTimeMillis() > 0) {
                    return;
                }
                performAirdrop();
            } finally {
                airdropExecuting.set(false);
            }
        });
    }

    private boolean isAirdropsEnabled() {
        Boolean enabled = inGameSettingsConfig.getValue(InGameSettingsType.AIRDROPS, Boolean.class);
        return enabled != null && enabled;
    }

    private long calculateRemainingTimeMillis() {
        Duration delay = inGameSettingsConfig.getValue(InGameSettingsType.AIRDROP_DELAY, Duration.class);
        long delayMs = delay == null ? 0L : delay.toMillis();
        return lastDropTime + delayMs - System.currentTimeMillis();
    }

    private void handleAnnouncementIfNeeded(long remainingMillis) {
        Duration announcementTime = inGameSettingsConfig.getValue(InGameSettingsType.AIRDROP_ANNOUNCEMENT_TIME, Duration.class);
        long announcementMs = announcementTime == null ? 0L : announcementTime.toMillis();

        if (remainingMillis <= announcementMs) {
            displayAnnouncementBossBar(remainingMillis);
        }
    }

    private void displayAnnouncementBossBar(long remainingMillis) {
        long seconds = Math.max(0L, remainingMillis / 1000L);
        if (seconds == lastAnnouncementSecond) {
            return;
        }
        lastAnnouncementSecond = seconds;

        String formattedTime = TimeUtil.formatTimeMillis(remainingMillis);
        String text = TextUtil.color(langConfig.airdropWillSpawn.replace("{TIME}", formattedTime));

        BossBar bossBar = Bukkit.createBossBar(text, BarColor.RED, BarStyle.SOLID);
        Bukkit.getOnlinePlayers().forEach(bossBar::addPlayer);
        scheduler.runLaterSync(bossBar::removeAll, 20L);
    }

    private void performAirdrop() {
        lastDropTime = System.currentTimeMillis();

        Location spawnLocation = determineSpawnLocation();
        airdropService.createAirdrop(spawnLocation);

        nextLocation = null;
    }

    private Location determineSpawnLocation() {
        if (nextLocation != null) {
            return nextLocation;
        }
        Location base = generateRandomLocation();
        return base.add(0.0, 20.0, 0.0);
    }

    private Location generateRandomLocation() {
        var world = getConfiguredWorld();
        var random = ThreadLocalRandom.current();

        double worldHalfBorder = world.getWorldBorder().getSize() / 2.0 - 1.0;

        int halfBorder = (int) Math.max(1.0, Math.min(worldHalfBorder, airdropConfig.maxBorder / 2));

        var x = random.nextInt(-halfBorder, halfBorder + 1);
        var z = random.nextInt(-halfBorder, halfBorder + 1);
        var y = world.getHighestBlockYAt(x, z);

        return new Location(world, x + 0.5, y, z + 0.5);
    }


    private World getConfiguredWorld() {
        World world = Bukkit.getWorld(airdropConfig.airdropsWorld);
        Objects.requireNonNull(world, "World '" + airdropConfig.airdropsWorld + "' not found");
        return world;
    }
}
