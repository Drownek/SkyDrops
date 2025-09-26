package me.drownek.skydrops.airdrop;

import eu.okaeri.injector.annotation.Inject;
import me.drownek.platform.bukkit.annotation.Scheduled;
import me.drownek.platform.bukkit.scheduler.PlatformScheduler;

import java.time.Instant;

@Scheduled(rate = 20, async = true)
public class AirDropDespawnTask implements Runnable {
    private @Inject AirdropService airdropService;
    private @Inject PlatformScheduler scheduler;

    @Override
    public void run() {
        airdropService.getAirDrops().forEach(airDrop -> {
            if (airDrop.getAutoDespawnTime() != null && Instant.now().isAfter(airDrop.getAutoDespawnTime())) {
                scheduler.runSync(() -> airdropService.removeAirdrop(airDrop));
            }
        });
    }
}
