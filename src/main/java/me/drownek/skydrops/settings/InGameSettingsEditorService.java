package me.drownek.skydrops.settings;

import eu.okaeri.injector.annotation.Inject;
import me.drownek.platform.core.annotation.Component;
import me.drownek.skydrops.settings.gatherers.BooleanSettingGatherer;
import me.drownek.skydrops.settings.gatherers.DurationSettingGatherer;
import me.drownek.skydrops.settings.gatherers.IntegerSettingGatherer;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
public class InGameSettingsEditorService {

    private final Map<Class<?>, SettingValueGatherer<?>> gatherersByType;

    private @Inject InGameSettingsConfig config;

    public InGameSettingsEditorService() {
        Set<SettingValueGatherer<?>> gatherers = Set.of(
            new BooleanSettingGatherer(),
            new DurationSettingGatherer(),
            new IntegerSettingGatherer()
        );
        gatherersByType = gatherers.stream()
            .collect(Collectors.toMap(SettingValueGatherer::getHandledType, gatherer -> gatherer));
        System.out.println("gatherers = " + gatherers);
        System.out.println("gatherersByType = " + gatherersByType);
    }

    /**
     * Starts editing a setting for a player
     */
    public CompletableFuture<Void> startEditing(Player player, InGameSettingsType settingType) {
        SettingValueGatherer<?> gatherer = gatherersByType.get(settingType.getDefaultValue().getClass());

        if (gatherer == null) {
            player.sendMessage("§cNo editor available for this setting type!");
            return CompletableFuture.completedFuture(null);
        }

        // Close any open inventory
        player.closeInventory();

        return gatherValueSafely(player, settingType, gatherer)
            .thenAccept(newValue -> {
                if (newValue != null) {
                    updateSettingValue(settingType, newValue);
                    player.sendMessage("§aSetting '" + settingType.getName() + "' updated to: §f" +
                        settingType.formatValue(newValue));
                } else {
                    player.sendMessage("§cSetting update cancelled.");
                }
            })
            .exceptionally(throwable -> {
                System.err.println("Error in startEditing for setting " + settingType.name() + ": " + throwable.getMessage());
                throwable.printStackTrace();
                player.sendMessage("§cFailed to update setting: " + throwable.getMessage());
                return null;
            });
    }

    private CompletableFuture<Object> gatherValueSafely(Player player, InGameSettingsType settingType, SettingValueGatherer<?> gatherer) {
        try {
            Class<?> valueType = settingType.getDefaultValue().getClass();
            System.out.println("valueType = " + valueType);

            Object currentValue = config.getValue(settingType, valueType);
            System.out.println("currentValue = " + currentValue);

            @SuppressWarnings("unchecked")
            SettingValueGatherer<Object> rawGatherer = (SettingValueGatherer<Object>) gatherer;
            System.out.println("rawGatherer = " + rawGatherer.getClass());

            return rawGatherer.gatherValue(player, currentValue);
        } catch (Exception e) {
            CompletableFuture<Object> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(new RuntimeException("Failed to gather value for setting " + settingType.name(), e));
            return failedFuture;
        }
    }

    /**
     * Updates a setting value in the config with type safety
     */
    private void updateSettingValue(InGameSettingsType settingType, Object newValue) {
        try {
            config.setValue(settingType, newValue);
            config.save();
        } catch (Exception e) {
            throw new RuntimeException("Failed to update setting " + settingType.name(), e);
        }
    }

    /**
     * Gets all setting types that have available editors
     */
    public Set<InGameSettingsType> getEditableSettings() {
        return Arrays.stream(InGameSettingsType.values())
            .filter(type -> gatherersByType.containsKey(type.getClass()))
            .collect(Collectors.toSet());
    }

    public String getCurrentValueDisplay(InGameSettingsType settingType) {
        return settingType.formatValue(config.getValue(settingType, settingType.getDefaultValue().getClass()));
    }
}