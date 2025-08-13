package me.drownek.skydrops.settings;

import dev.triumphteam.gui.guis.PaginatedGui;
import eu.okaeri.injector.annotation.Inject;
import lombok.NonNull;
import me.drownek.platform.bukkit.scheduler.PlatformScheduler;
import me.drownek.platform.core.annotation.Service;
import me.drownek.util.ItemStackBuilder;
import me.drownek.util.gui.PaginatedGuiSettings;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

@Service
public class InGameSettingsGui {

    private @Inject InGameSettingsConfig config;
    private @Inject InGameSettingsEditorService editorService;
    private @Inject PlatformScheduler scheduler;

    public void open(@NonNull Player player) {
        PaginatedGuiSettings paginatedGuiSettings = PaginatedGuiSettings.builder()
            .title("Settings Editor")
            .rows(5)
            .build();

        PaginatedGui gui = paginatedGuiSettings.toPaginatedGuiBuilder().disableAllInteractions().create();

        for (InGameSettingsType settingType : InGameSettingsType.values()) {
            String currentValue = editorService.getCurrentValueDisplay(settingType);

            gui.addItem(ItemStackBuilder.of(settingType.getIcon())
                .name("§f" + settingType.getName())
                .lore(
                    "§7Current value: §f" + currentValue,
                    "§7Default: §f" + settingType.formatValue(settingType.getDefaultValue()),
                    "",
                    "§eLeft-click §7to edit"
                )
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

        addUtilityItems(gui, player);

        gui.open(player);
    }

    private void addUtilityItems(PaginatedGui gui, Player player) {
        gui.setItem(5, 1,
            ItemStackBuilder.of(Material.BARRIER)
                .name("§cReset All Settings")
                .lore(
                    "§7Click to reset all settings",
                    "§7to their default values.",
                    "",
                    "§c⚠ This cannot be undone!"
                )
                .asGuiItem(event -> {
                    config.resetAllToDefaults();
                    config.save();
                    player.sendMessage("§aAll settings have been reset to their defaults!");
                    open(player);
                })
        );
    }
}