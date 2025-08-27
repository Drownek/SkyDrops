package me.drownek.skydrops.settings.gatherers;

import me.drownek.datagatherer.DataGatherer;
import me.drownek.datagatherer.step.MsgStep;
import me.drownek.skydrops.lang.LangConfig;
import me.drownek.skydrops.settings.SettingValueGatherer;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class IntegerSettingGatherer implements SettingValueGatherer<Integer> {

    private final LangConfig langConfig;

    public IntegerSettingGatherer(LangConfig langConfig) {
        this.langConfig = langConfig;
    }

    @Override
    public CompletableFuture<Integer> gatherValue(Player player, Integer currentValue) {
        CompletableFuture<Integer> future = new CompletableFuture<>();

        DataGatherer.builder()
            .steps(
                new MsgStep(
                    langConfig.enterInteger.with("{current}", currentValue).format(),
                    value -> future.complete(Integer.parseInt(value)),
                    input -> {
                        try {
                            int value = Integer.parseInt(input);
                            return value >= 0;
                        } catch (NumberFormatException e) {
                            return false;
                        }
                    },
                    langConfig.integerSettingInvalidFormat.format()
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