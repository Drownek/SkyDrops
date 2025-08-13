package me.drownek.skydrops.settings;

import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

/**
 * Interface for gathering setting values from players
 * @param <T> The type of value this gatherer produces
 */
public interface SettingValueGatherer<T> {
    
    /**
     * Initiates the gathering process for a setting value
     * @param player The player to gather the value from
     * @param currentValue The current value of the setting
     * @return A CompletableFuture that will complete with the new value
     */
    CompletableFuture<T> gatherValue(Player player, T currentValue);
    
    /**
     * Gets the type this gatherer handles
     * @return The class type
     */
    Class<T> getHandledType();
}