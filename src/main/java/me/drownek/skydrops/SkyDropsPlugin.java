package me.drownek.skydrops;

import me.drownek.platform.bukkit.LightBukkitPlugin;
import me.drownek.platform.core.annotation.Scan;
import me.drownek.platform.core.plan.ExecutionPhase;
import me.drownek.platform.core.plan.Planned;
import me.drownek.skydrops.airdrop.AirdropService;
import me.drownek.skydrops.lang.LangConfig;

import java.util.Locale;

@Scan(deep = true, exclusions = "me.drownek.skydrops.libs")
public class SkyDropsPlugin extends LightBukkitPlugin {

    @Planned(ExecutionPhase.PRE_SETUP)
    void preSetup() {
        me.drownek.util.localization.LocalizationManager.setDefaultLocale(Locale.ENGLISH);
        me.drownek.datagatherer.localization.LocalizationManager.setDefaultLocale(Locale.ENGLISH);
    }

    @Planned(ExecutionPhase.POST_SETUP)
    void postSetup(LangConfig langConfig) {
        langConfig.utilsLocale.forEach(me.drownek.util.localization.LocalizationManager::setMessage);
        langConfig.gathererLocale.forEach(me.drownek.datagatherer.localization.LocalizationManager::setMessage);
    }
}
