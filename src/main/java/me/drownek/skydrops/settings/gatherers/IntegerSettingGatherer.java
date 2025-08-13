package me.drownek.skydrops.settings.gatherers;

import me.drownek.datagatherer.DataGatherer;
import me.drownek.datagatherer.step.MsgStep;
import me.drownek.skydrops.settings.SettingValueGatherer;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class IntegerSettingGatherer implements SettingValueGatherer<Integer> {

    @Override
    public CompletableFuture<Integer> gatherValue(Player player, Integer currentValue) {
        CompletableFuture<Integer> future = new CompletableFuture<>();

        DataGatherer.builder()
            .steps(
                new MsgStep(
                    "§eEnter integer value greater than 0. Current: §f" + currentValue,
                    value -> future.complete(Integer.parseInt(value)),
                    input -> {
                        try {
                            int value = Integer.parseInt(input);
                            return value >= 0;
                        } catch (NumberFormatException e) {
                            return false;
                        }
                    },
                    "§cInvalid input! Please enter a number greater than 0."
                )
            )
            .cancelAction(() -> future.complete(null))
            .withoutSuccessMessage()
            .build()
            .start(player);

        return future;
    }

    @Override
    public Class<Integer> getHandledType() {
        return Integer.class;
    }
}