package me.drownek.skydrops.airdrop;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public abstract class PackageEntity {

    protected int counter;
    private final Plugin plugin;

    protected PackageEntity(Plugin plugin) {
        this.counter = 0;
        this.plugin = plugin;
    }

    public abstract void summon();

    protected abstract void tick();

    protected final void retick() {
        Bukkit.getServer().getScheduler().runTaskLater(plugin, this::tick, 1L);
    }
}