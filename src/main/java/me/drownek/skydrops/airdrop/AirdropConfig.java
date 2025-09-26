package me.drownek.skydrops.airdrop;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import me.drownek.platform.core.annotation.Configuration;

import java.time.Duration;

@Configuration(path = "airdrops.{ext}")
public class AirdropConfig extends OkaeriConfig {

    public String airdropsWorld = "world";
    public boolean randomizeItemSlots = true;

    @Comment("Prevents creating new chunks repeatedly when auto-spawning airdrops with the world border set to default size")
    public double maxBorder = 1000;

    @Comment("Whether to despawn chests that are already empty before auto-remove time")
    public boolean despawnEmptyChests = true;

    @Comment("How long to wait before removing chest with items in it")
    public Duration autoRemoveChestAfter = Duration.ofMinutes(1);
}