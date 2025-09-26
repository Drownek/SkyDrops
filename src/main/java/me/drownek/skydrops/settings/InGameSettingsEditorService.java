package me.drownek.skydrops.settings;

import eu.okaeri.injector.annotation.Inject;
import eu.okaeri.injector.annotation.PostConstruct;
import me.drownek.platform.core.annotation.Component;
import me.drownek.skydrops.lang.LangConfig;
import me.drownek.skydrops.settings.gatherers.BooleanSettingGatherer;
import me.drownek.skydrops.settings.gatherers.DurationSettingGatherer;
import me.drownek.skydrops.settings.gatherers.IntegerSettingGatherer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class InGameSettingsEditorService {

    private Map<Class<?>, SettingValueGatherer<?>> gatherersByType;

    private @Inject InGameSettingsConfig config;
    private @Inject Logger logger;
    private @Inject LangConfig langConfig;

    @PostConstruct
    public void init() {
        Set<SettingValueGatherer<?>> gatherers = Set.of(
            new BooleanSettingGatherer(),
            new DurationSettingGatherer(langConfig),
            new IntegerSettingGatherer(langConfig)
        );
        gatherersByType = gatherers.stream()
            .collect(Collectors.toMap(SettingValueGatherer::getHandledType, gatherer -> gatherer));
    }

    public CompletableFuture<Void> startEditing(Player player, InGameSettingsType settingType) {
        SettingValueGatherer<?> gatherer = gatherersByType.get(settingType.getDefaultValue().getClass());

        if (gatherer == null) {
            langConfig.noEditorAvailable.sendTo(player);
            return CompletableFuture.completedFuture(null);
        }

        player.closeInventory();

        return gatherValue(player, settingType, gatherer)
            .thenAccept(newValue -> {
                if (newValue != null) {
                    updateSettingValue(settingType, newValue);
                    langConfig.settingUpdated
                        .with("{setting}", settingType.getName())
                        .with("{to}", settingType.formatValue(newValue))
                        .sendTo(player);
                }
            })
            .exceptionally(throwable -> {
                logger.log(Level.SEVERE, "Error in startEditing for setting " + settingType.name(), throwable);
                langConfig.settingFailedUpdate.with("{msg}", throwable.getMessage()).sendTo(player);
                return null;
            });
    }

    private CompletableFuture<Object> gatherValue(Player player, InGameSettingsType settingType, SettingValueGatherer<?> gatherer) {
        try {
            Class<?> valueType = settingType.getDefaultValue().getClass();

            Object currentValue = config.getValue(settingType, valueType);

            @SuppressWarnings("unchecked")
            SettingValueGatherer<Object> rawGatherer = (SettingValueGatherer<Object>) gatherer;

            return rawGatherer.gatherValue(player, currentValue);
        } catch (Exception e) {
            CompletableFuture<Object> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(new RuntimeException("Failed to gather value for setting " + settingType.name(), e));
            return failedFuture;
        }
    }

    private void updateSettingValue(InGameSettingsType settingType, Object newValue) {
        try {
            config.setValue(settingType, newValue);
            config.save();
        } catch (Exception e) {
            throw new RuntimeException("Failed to update setting " + settingType.name(), e);
        }
    }

    public String getCurrentValueDisplay(InGameSettingsType settingType) {
        return settingType.formatValue(config.getValue(settingType, settingType.getDefaultValue().getClass()));
    }
}