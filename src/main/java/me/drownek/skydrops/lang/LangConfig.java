package me.drownek.skydrops.lang;

import com.cryptomorin.xseries.XMaterial;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import me.drownek.platform.core.annotation.Configuration;
import me.drownek.util.DataItemStack;
import me.drownek.util.TimeUtil;
import me.drownek.util.gui.GuiItemInfo;
import me.drownek.util.gui.PaginatedGuiSettings;
import me.drownek.util.localization.LocalizationManager;
import me.drownek.util.localization.MessageKey;
import me.drownek.util.message.SendableMessage;
import me.drownek.util.message.TemplateString;
import me.drownek.util.message.TemplateStringList;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration(path = "lang.{ext}")
public class LangConfig extends OkaeriConfig {

    @Comment("───────────── General Messages ─────────────")
    public SendableMessage noEditorAvailable = SendableMessage.of("§cNo editor available for this setting type!");
    public SendableMessage settingUpdated = SendableMessage.of("§aSetting '{setting}' updated to: §f{to}");
    public SendableMessage settingUpdateCancelled = SendableMessage.of("§cSetting update cancelled.");
    public SendableMessage settingFailedUpdate = SendableMessage.of("§cFailed to update setting: {msg}");
    public SendableMessage settingsReset = SendableMessage.of("§aAll settings have been reset to their defaults!");
    public SendableMessage reloadConfigSuccess = SendableMessage.of("&aConfig has been reloaded successfully!");
    public SendableMessage reloadConfigError = SendableMessage.of("&cAn error occurred while reloading configuration, please check console logs!");

    @Comment("───────────── Drop Editor ─────────────")
    public SendableMessage wrongChance = SendableMessage.of("&cWrong chance!");
    public SendableMessage itemAlreadyAdded = SendableMessage.of("&cItem is already added to the drop!");
    public String editDropGuiTitle = "Click item in inventory to add";
    public List<String> editDropSignLines = List.of("", "/\\/\\/\\/\\/\\/\\/\\/\\", "Enter chance");

    public TemplateStringList dropGuiLoreAddition = TemplateStringList.of(
        "&8---------------",
        "&7Chance: &f{chance}%",
        "&eShift + Left Click to remove",
        "&eLeft Click to edit chance"
    );

    public TemplateString enterDuration = TemplateString.of("§eEnter duration (e.g., 1m30s, 5m, 2h, 1d). Current: §f{current}");
    public TemplateString durationSettingInvalidFormat = TemplateString.of("§cInvalid format! Use format like: 1m30s, 5m, 2h, 1d");
    public TemplateString enterInteger = TemplateString.of("§eEnter integer value greater than 0. Current: §f{current}");
    public TemplateString integerSettingInvalidFormat = TemplateString.of("§cInvalid input! Please enter a number greater than 0.");

    @Comment("───────────── Settings GUI ─────────────")
    public PaginatedGuiSettings settingsEditorGui = PaginatedGuiSettings.builder()
        .title("Settings Editor")
        .rows(5)
        .build();

    public DataItemStack settingsIcon = new DataItemStack(Material.BARRIER, "&f{setting}", List.of(
        "§7Current value: §f{current}",
        "§7Default: §f{default}",
        "",
        "§eLeft-click §7to edit"
    ));

    public GuiItemInfo resetAllSettings = new GuiItemInfo(36, XMaterial.BARRIER, "&cReset All Settings", List.of(
        "§7Click to reset all settings",
        "§7to their default values.",
        "",
        "§c⚠ This cannot be undone!"
    ));

    @Comment("───────────── Game ─────────────")
    public SendableMessage airdropSpawnMessage = SendableMessage.of(
        List.of(
            "     &8&l&m----[&b&l AIRDROP &8&l&m]----",
            "   &7A new &3airdrop&7 has just spawned on the map!",
            "     &7You can find it at coordinates&8: &7x&8: &f{x} &7z&8: &f{z}",
            "  &7Gather your &bteam &7and &fcapture &7the airdrop!"
        )
    );
    public String clickAction = "&a&lCLICK RIGHT";
    public String airdropWillSpawn = "&7Airdrop will spawn in &f{TIME}";

    @Comment("───────────── Localization ─────────────")
    public Map<MessageKey, String> utilsLocale = Arrays.stream(MessageKey.values())
        .collect(Collectors.toMap(key -> key, LocalizationManager::getMessage));

    public Map<me.drownek.datagatherer.localization.MessageKey, String> gathererLocale =
        Arrays.stream(me.drownek.datagatherer.localization.MessageKey.values())
            .collect(Collectors.toMap(key -> key, me.drownek.datagatherer.localization.LocalizationManager::getMessage));
}
