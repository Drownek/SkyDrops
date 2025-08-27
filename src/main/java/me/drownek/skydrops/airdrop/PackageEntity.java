package me.drownek.skydrops.airdrop;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;


public class PackageEntity {
    protected int counter;
    private final Plugin plugin;

    public PackageEntity(Plugin plugin) {
        this.counter = 0;
        this.plugin = plugin;
    }

    public void summon() {
    }

    public void remove() {
    }

    protected void tick() {
    }

    protected void retick() {
        Bukkit.getServer().getScheduler().runTaskLater(plugin, PackageEntity.this::tick, 1L);
    }
}