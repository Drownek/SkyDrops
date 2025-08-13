package me.drownek.skydrops.settings.gatherers;

import me.drownek.datagatherer.DataGatherer;
import me.drownek.datagatherer.step.MsgStep;
import me.drownek.skydrops.settings.SettingValueGatherer;
import me.drownek.util.TimeUtil;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class DurationSettingGatherer implements SettingValueGatherer<Duration> {

    @Override
    public CompletableFuture<Duration> gatherValue(Player player, Duration currentValue) {
        CompletableFuture<Duration> future = new CompletableFuture<>();

        DataGatherer.builder()
            .steps(
                new MsgStep(
                    "§eEnter duration (e.g., 1m30s, 5m, 2h, 1d). Current: §f" + TimeUtil.formatDuration(currentValue),
                    value -> {
                        try {
                            Duration parsed = Duration.ofMillis(TimeUtil.timeFromString(value));
                            future.complete(parsed);
                        } catch (Exception e) {
                            future.complete(null);
                        }
                    },
                    input -> {
                        try {
                            Duration parsed = Duration.ofMillis(TimeUtil.timeFromString(input));
                            return parsed != null && !parsed.isNegative();
                        } catch (Exception e) {
                            return false;
                        }
                    },
                    "§cInvalid format! Use format like: 1m30s, 5m, 2h, 1d"
                )
            )
            .cancelAction(() -> future.complete(null))
            .withoutSuccessMessage()
            .build()
            .start(player);

        return future;
    }

    @Override
    public Class<Duration> getHandledType() {
        return Duration.class;
    }
}