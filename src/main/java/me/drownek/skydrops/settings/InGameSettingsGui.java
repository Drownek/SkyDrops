package me.drownek.skydrops.settings;

import dev.triumphteam.gui.guis.PaginatedGui;
import eu.okaeri.injector.annotation.Inject;
import lombok.NonNull;
import me.drownek.platform.bukkit.scheduler.PlatformScheduler;
import me.drownek.platform.core.annotation.Component;
import me.drownek.platform.core.annotation.Service;
import me.drownek.skydrops.lang.LangConfig;
import me.drownek.util.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

@Component
public class InGameSettingsGui {

    private @Inject InGameSettingsConfig config;
    private @Inject InGameSettingsEditorService editorService;
    private @Inject PlatformScheduler scheduler;
    private @Inject LangConfig langConfig;

    public void open(@NonNull Player player) {
        PaginatedGui gui = langConfig.settingsEditorGui.toPaginatedGuiBuilder().disableAllInteractions().create();

        for (InGameSettingsType settingType : InGameSettingsType.values()) {
            String currentValue = editorService.getCurrentValueDisplay(settingType);

            gui.addItem(langConfig.settingsIcon
                .with("{setting}", settingType.getName())
                .with("{current}", currentValue)
                .with("{default}", settingType.formatValue(settingType.getDefaultValue()))
                .apply(stack -> stack.getItemStack().setType(settingType.getIcon()))
                .asGuiItem(event -> {
                    ClickType clickType = event.getClick();

                    if (clickType == ClickType.LEFT) {
                        player.closeInventory();
                        editorService.startEditing(player, settingType)
                            .whenComplete((result, throwable) -> {
                                if (throwable == null) {
                                    open(player);
                                }
                            });
                    }
                }));
        }

        langConfig.resetAllSettings.setGuiItem(gui, event -> {
            config.resetAllToDefaults();
            config.save();
            langConfig.settingsReset.sendTo(player);
            open(player);
        });

        gui.open(player);
    }
}