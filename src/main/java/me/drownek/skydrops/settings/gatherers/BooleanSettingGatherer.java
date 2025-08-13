package me.drownek.skydrops.settings.gatherers;

import me.drownek.skydrops.settings.SettingValueGatherer;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class BooleanSettingGatherer implements SettingValueGatherer<Boolean> {

    @Override
    public CompletableFuture<Boolean> gatherValue(Player player, Boolean currentValue) {
        return CompletableFuture.completedFuture(!currentValue);
    }

    @Override
    public Class<Boolean> getHandledType() {
        return Boolean.class;
    }
}